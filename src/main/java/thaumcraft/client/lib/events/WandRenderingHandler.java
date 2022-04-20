// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.events;

import net.minecraft.util.EnumFacing;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.World;
import thaumcraft.api.items.IArchitect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.lib.UtilsFX;
import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Mouse;
import thaumcraft.api.casters.ICaster;
import net.minecraft.util.NonNullList;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.ItemFocusPouch;
import net.minecraft.entity.player.EntityPlayer;
import baubles.api.BaublesApi;
import java.util.Iterator;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;
import thaumcraft.common.lib.network.PacketHandler;
import org.lwjgl.opengl.Display;
import thaumcraft.common.lib.events.KeyHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import java.util.HashMap;
import java.util.TreeMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WandRenderingHandler
{
    static float radialHudScale;
    TreeMap<String, Integer> foci;
    HashMap<String, ItemStack> fociItem;
    HashMap<String, Boolean> fociHover;
    HashMap<String, Float> fociScale;
    long lastTime;
    boolean lastState;
    final ResourceLocation R1;
    final ResourceLocation R2;
    int lastArcHash;
    ArrayList<BlockPos> architectBlocks;
    HashMap<BlockPos, boolean[]> bmCache;
    final ResourceLocation CFRAME;
    final ResourceLocation SFRAME;
    int[][] mos;
    int[][] rotmat;
    ResourceLocation tex;
    
    public WandRenderingHandler() {
        this.foci = new TreeMap<String, Integer>();
        this.fociItem = new HashMap<String, ItemStack>();
        this.fociHover = new HashMap<String, Boolean>();
        this.fociScale = new HashMap<String, Float>();
        this.lastTime = 0L;
        this.lastState = false;
        this.R1 = new ResourceLocation("thaumcraft", "textures/misc/radial.png");
        this.R2 = new ResourceLocation("thaumcraft", "textures/misc/radial2.png");
        this.lastArcHash = 0;
        this.architectBlocks = new ArrayList<BlockPos>();
        this.bmCache = new HashMap<BlockPos, boolean[]>();
        this.CFRAME = new ResourceLocation("thaumcraft", "textures/misc/frame_corner.png");
        this.SFRAME = new ResourceLocation("thaumcraft", "textures/misc/frame_side.png");
        this.mos = new int[][] { { 4, 5, 6, 7 }, { 0, 1, 2, 3 }, { 0, 1, 4, 5 }, { 2, 3, 6, 7 }, { 0, 2, 4, 6 }, { 1, 3, 5, 7 } };
        this.rotmat = new int[][] { { 0, 90, 270, 180 }, { 270, 180, 0, 90 }, { 180, 90, 270, 0 }, { 0, 270, 90, 180 }, { 270, 180, 0, 90 }, { 180, 270, 90, 0 } };
        this.tex = new ResourceLocation("thaumcraft", "textures/misc/architect_arrows.png");
    }
    
    @SideOnly(Side.CLIENT)
    public void handleFociRadial(final Minecraft mc, final long time, final RenderGameOverlayEvent event) {
        if (KeyHandler.radialActive || WandRenderingHandler.radialHudScale > 0.0f) {
            if (KeyHandler.radialActive) {
                if (mc.currentScreen != null) {
                    KeyHandler.radialActive = false;
                    KeyHandler.radialLock = true;
                    mc.setIngameFocus();
                    mc.setIngameNotInFocus();
                    return;
                }
                if (WandRenderingHandler.radialHudScale == 0.0f) {
                    this.getFociInfo(mc);
                    if (this.foci.size() > 0 && mc.inGameHasFocus) {
                        mc.inGameHasFocus = false;
                        mc.mouseHelper.ungrabMouseCursor();
                    }
                }
            }
            else if (mc.currentScreen == null && this.lastState) {
                if (Display.isActive() && !mc.inGameHasFocus) {
                    mc.inGameHasFocus = true;
                    mc.mouseHelper.grabMouseCursor();
                }
                this.lastState = false;
            }
            this.renderFocusRadialHUD(event.getResolution().getScaledWidth_double(), event.getResolution().getScaledHeight_double(), time, event.getPartialTicks());
            if (time > this.lastTime) {
                for (final String key : this.fociHover.keySet()) {
                    if (this.fociHover.get(key)) {
                        if (!KeyHandler.radialActive && !KeyHandler.radialLock) {
                            PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer(key));
                            KeyHandler.radialLock = true;
                        }
                        if (this.fociScale.get(key) >= 1.3f) {
                            continue;
                        }
                        this.fociScale.put(key, Math.min(1.3f, this.fociScale.get(key) + this.getRadialChange(time, this.lastTime, 150L)));
                    }
                    else {
                        if (this.fociScale.get(key) <= 1.0f) {
                            continue;
                        }
                        this.fociScale.put(key, Math.max(1.0f, this.fociScale.get(key) - this.getRadialChange(time, this.lastTime, 250L)));
                    }
                }
                if (!KeyHandler.radialActive) {
                    WandRenderingHandler.radialHudScale -= this.getRadialChange(time, this.lastTime, 150L);
                }
                else if (KeyHandler.radialActive && WandRenderingHandler.radialHudScale < 1.0f) {
                    WandRenderingHandler.radialHudScale += this.getRadialChange(time, this.lastTime, 150L);
                }
                if (WandRenderingHandler.radialHudScale > 1.0f) {
                    WandRenderingHandler.radialHudScale = 1.0f;
                }
                if (WandRenderingHandler.radialHudScale < 0.0f) {
                    WandRenderingHandler.radialHudScale = 0.0f;
                    KeyHandler.radialLock = false;
                }
                this.lastState = KeyHandler.radialActive;
            }
        }
        this.lastTime = time;
    }
    
    @SideOnly(Side.CLIENT)
    private float getRadialChange(final long time, final long lasttime, final long total) {
        return (time - lasttime) / (float)total;
    }
    
    @SideOnly(Side.CLIENT)
    private void getFociInfo(final Minecraft mc) {
        this.foci.clear();
        this.fociItem.clear();
        this.fociHover.clear();
        this.fociScale.clear();
        int pouchcount = 0;
        ItemStack item = null;
        final IInventory baubles = BaublesApi.getBaubles(mc.player);
        for (int a = 0; a < baubles.getSizeInventory(); ++a) {
            if (baubles.getStackInSlot(a) != null && !baubles.getStackInSlot(a).isEmpty() && baubles.getStackInSlot(a).getItem() instanceof ItemFocusPouch) {
                ++pouchcount;
                item = baubles.getStackInSlot(a);
                final NonNullList<ItemStack> inv = ((ItemFocusPouch)item.getItem()).getInventory(item);
                for (int q = 0; q < inv.size(); ++q) {
                    item = inv.get(q);
                    if (item.getItem() instanceof ItemFocus) {
                        final String sh = ((ItemFocus)item.getItem()).getSortingHelper(item);
                        if (sh != null) {
                            this.foci.put(sh, q + pouchcount * 1000);
                            this.fociItem.put(sh, item.copy());
                            this.fociScale.put(sh, 1.0f);
                            this.fociHover.put(sh, false);
                        }
                    }
                }
            }
        }
        for (int a = 0; a < 36; ++a) {
            item = mc.player.inventory.mainInventory.get(a);
            if (item.getItem() instanceof ItemFocus) {
                final String sh2 = ((ItemFocus)item.getItem()).getSortingHelper(item);
                if (sh2 == null) {
                    continue;
                }
                this.foci.put(sh2, a);
                this.fociItem.put(sh2, item.copy());
                this.fociScale.put(sh2, 1.0f);
                this.fociHover.put(sh2, false);
            }
            if (item.getItem() instanceof ItemFocusPouch) {
                ++pouchcount;
                final NonNullList<ItemStack> inv = ((ItemFocusPouch)item.getItem()).getInventory(item);
                for (int q = 0; q < inv.size(); ++q) {
                    item = inv.get(q);
                    if (item.getItem() instanceof ItemFocus) {
                        final String sh = ((ItemFocus)item.getItem()).getSortingHelper(item);
                        if (sh != null) {
                            this.foci.put(sh, q + pouchcount * 1000);
                            this.fociItem.put(sh, item.copy());
                            this.fociScale.put(sh, 1.0f);
                            this.fociHover.put(sh, false);
                        }
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    private void renderFocusRadialHUD(final double sw, final double sh, final long time, final float partialTicks) {
        final Minecraft mc = Minecraft.getMinecraft();
        ItemStack s = mc.player.getHeldItemMainhand();
        if (!(s.getItem() instanceof ICaster)) {
            s = mc.player.getHeldItemOffhand();
        }
        if (!(s.getItem() instanceof ICaster)) {
            return;
        }
        final ICaster wand = (ICaster)s.getItem();
        final ItemFocus focus = (ItemFocus)wand.getFocus(s);
        final int i = (int)(Mouse.getEventX() * sw / mc.displayWidth);
        final int j = (int)(sh - Mouse.getEventY() * sh / mc.displayHeight - 1.0);
        final int k = Mouse.getEventButton();
        if (this.fociItem.size() == 0) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, sw, sh, 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -2000.0f);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        GL11.glTranslated((int)(sw / 2.0), (int)(sh / 2.0), 0.0);
        ItemStack tt = null;
        final float width = 16.0f + this.fociItem.size() * 2.5f;
        mc.renderEngine.bindTexture(this.R1);
        GL11.glPushMatrix();
        GL11.glRotatef(partialTicks + mc.player.ticksExisted % 720 / 2.0f, 0.0f, 0.0f, 1.0f);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        UtilsFX.renderQuadCentered(1, 1, 0, width * 2.75f * WandRenderingHandler.radialHudScale, 0.5f, 0.5f, 0.5f, 200, 771, 0.5f);
        GL11.glDisable(3042);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glPopMatrix();
        mc.renderEngine.bindTexture(this.R2);
        GL11.glPushMatrix();
        GL11.glRotatef(-(partialTicks + mc.player.ticksExisted % 720 / 2.0f), 0.0f, 0.0f, 1.0f);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        UtilsFX.renderQuadCentered(1, 1, 0, width * 2.55f * WandRenderingHandler.radialHudScale, 0.5f, 0.5f, 0.5f, 200, 771, 0.5f);
        GL11.glDisable(3042);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glPopMatrix();
        if (focus != null) {
            final ItemStack item = wand.getFocusStack(s).copy();
            UtilsFX.renderItemInGUI(-8, -8, 100, item);
            final int mx = (int)(i - sw / 2.0);
            final int my = (int)(j - sh / 2.0);
            if (mx >= -10 && mx <= 10 && my >= -10 && my <= 10) {
                tt = item;
            }
        }
        GL11.glScaled(WandRenderingHandler.radialHudScale, WandRenderingHandler.radialHudScale, WandRenderingHandler.radialHudScale);
        float currentRot = -90.0f * WandRenderingHandler.radialHudScale;
        final float pieSlice = 360.0f / this.fociItem.size();
        String key = this.foci.firstKey();
        for (int a = 0; a < this.fociItem.size(); ++a) {
            final double xx = MathHelper.cos(currentRot / 180.0f * 3.1415927f) * width;
            final double yy = MathHelper.sin(currentRot / 180.0f * 3.1415927f) * width;
            currentRot += pieSlice;
            GL11.glPushMatrix();
            GL11.glTranslated((int)xx, (int)yy, 100.0);
            GL11.glScalef(this.fociScale.get(key), this.fociScale.get(key), this.fociScale.get(key));
            GL11.glEnable(32826);
            final ItemStack item2 = this.fociItem.get(key).copy();
            UtilsFX.renderItemInGUI(-8, -8, 100, item2);
            GL11.glDisable(32826);
            GL11.glPopMatrix();
            if (!KeyHandler.radialLock && KeyHandler.radialActive) {
                final int mx2 = (int)(i - sw / 2.0 - xx);
                final int my2 = (int)(j - sh / 2.0 - yy);
                if (mx2 >= -10 && mx2 <= 10 && my2 >= -10 && my2 <= 10) {
                    this.fociHover.put(key, true);
                    tt = this.fociItem.get(key);
                    if (k == 0) {
                        KeyHandler.radialActive = false;
                        KeyHandler.radialLock = true;
                        PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer(key));
                        break;
                    }
                }
                else {
                    this.fociHover.put(key, false);
                }
            }
            key = this.foci.higherKey(key);
        }
        GL11.glPopMatrix();
        if (tt != null) {
            UtilsFX.drawCustomTooltip(mc.currentScreen, mc.fontRenderer, tt.getTooltip(mc.player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL), -4, 20, 11);
        }
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    @SideOnly(Side.CLIENT)
    public boolean handleArchitectOverlay(final ItemStack stack, final EntityPlayer player, final float partialTicks, final int playerticks, final RayTraceResult target) {
        if (target == null) {
            return false;
        }
        final Minecraft mc = Minecraft.getMinecraft();
        final IArchitect af = (IArchitect)stack.getItem();
        final String h = target.getBlockPos().getX() + "" + target.getBlockPos().getY() + "" + target.getBlockPos().getZ() + "" + target.sideHit + "" + playerticks / 5;
        final int hc = h.hashCode();
        if (hc != this.lastArcHash) {
            this.lastArcHash = hc;
            this.bmCache.clear();
            this.architectBlocks = af.getArchitectBlocks(stack, mc.world, target.getBlockPos(), target.sideHit, player);
        }
        if (this.architectBlocks == null || this.architectBlocks.size() == 0) {
            return false;
        }
        this.drawArchitectAxis(target.getBlockPos(), partialTicks, af.showAxis(stack, mc.world, player, target.sideHit, IArchitect.EnumAxis.X), af.showAxis(stack, mc.world, player, target.sideHit, IArchitect.EnumAxis.Y), af.showAxis(stack, mc.world, player, target.sideHit, IArchitect.EnumAxis.Z));
        for (final BlockPos cc : this.architectBlocks) {
            this.drawOverlayBlock(cc, playerticks, mc, partialTicks);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        return true;
    }
    
    private boolean isConnectedBlock(final World world, final BlockPos pos) {
        return this.architectBlocks.contains(pos);
    }
    
    @SideOnly(Side.CLIENT)
    private boolean[] getConnectedSides(final World world, final BlockPos pos) {
        if (this.bmCache.containsKey(pos)) {
            return this.bmCache.get(pos);
        }
        final boolean[] bitMatrix = { !this.isConnectedBlock(world, pos.add(-1, 0, 0)) && !this.isConnectedBlock(world, pos.add(0, 0, -1)) && !this.isConnectedBlock(world, pos.add(0, 1, 0)), !this.isConnectedBlock(world, pos.add(1, 0, 0)) && !this.isConnectedBlock(world, pos.add(0, 0, -1)) && !this.isConnectedBlock(world, pos.add(0, 1, 0)), !this.isConnectedBlock(world, pos.add(-1, 0, 0)) && !this.isConnectedBlock(world, pos.add(0, 0, 1)) && !this.isConnectedBlock(world, pos.add(0, 1, 0)), !this.isConnectedBlock(world, pos.add(1, 0, 0)) && !this.isConnectedBlock(world, pos.add(0, 0, 1)) && !this.isConnectedBlock(world, pos.add(0, 1, 0)), !this.isConnectedBlock(world, pos.add(-1, 0, 0)) && !this.isConnectedBlock(world, pos.add(0, 0, -1)) && !this.isConnectedBlock(world, pos.add(0, -1, 0)), !this.isConnectedBlock(world, pos.add(1, 0, 0)) && !this.isConnectedBlock(world, pos.add(0, 0, -1)) && !this.isConnectedBlock(world, pos.add(0, -1, 0)), !this.isConnectedBlock(world, pos.add(-1, 0, 0)) && !this.isConnectedBlock(world, pos.add(0, 0, 1)) && !this.isConnectedBlock(world, pos.add(0, -1, 0)), !this.isConnectedBlock(world, pos.add(1, 0, 0)) && !this.isConnectedBlock(world, pos.add(0, 0, 1)) && !this.isConnectedBlock(world, pos.add(0, -1, 0)) };
        this.bmCache.put(pos, bitMatrix);
        return bitMatrix;
    }
    
    @SideOnly(Side.CLIENT)
    public void drawOverlayBlock(final BlockPos pos, final int ticks, final Minecraft mc, final float partialTicks) {
        final boolean[] bitMatrix = this.getConnectedSides(mc.world, pos);
        GL11.glPushMatrix();
        GlStateManager.blendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glDisable(2884);
        final EntityPlayer player = (EntityPlayer)mc.getRenderViewEntity();
        final double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        final double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        final double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
        GL11.glTranslated(-iPX + pos.getX() + 0.5, -iPY + pos.getY() + 0.5, -iPZ + pos.getZ() + 0.5);
        for (final EnumFacing face : EnumFacing.values()) {
            if (!this.isConnectedBlock(mc.world, pos.offset(face))) {
                GL11.glPushMatrix();
                GL11.glRotatef(90.0f, (float)(-face.getFrontOffsetY()), (float)face.getFrontOffsetX(), (float)(-face.getFrontOffsetZ()));
                if (face.getFrontOffsetZ() < 0) {
                    GL11.glTranslated(0.0, 0.0, -0.5);
                }
                else {
                    GL11.glTranslated(0.0, 0.0, 0.5);
                }
                GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
                GL11.glPushMatrix();
                UtilsFX.renderQuadCentered(this.SFRAME, 1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 1, 0.1f);
                GL11.glPopMatrix();
                for (int a = 0; a < 4; ++a) {
                    if (bitMatrix[this.mos[face.ordinal()][a]]) {
                        GL11.glPushMatrix();
                        GL11.glRotatef((float)this.rotmat[face.ordinal()][a], 0.0f, 0.0f, 1.0f);
                        UtilsFX.renderQuadCentered(this.CFRAME, 1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 1, 0.66f);
                        GL11.glPopMatrix();
                    }
                }
                GL11.glPopMatrix();
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2884);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
        GL11.glPopMatrix();
    }
    
    @SideOnly(Side.CLIENT)
    public void drawArchitectAxis(final BlockPos pos, final float partialTicks, final boolean dx, final boolean dy, final boolean dz) {
        if (!dx && !dy && !dz) {
            return;
        }
        final EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
        final double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        final double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        final double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
        final float r = MathHelper.sin(player.ticksExisted / 4.0f + pos.getX()) * 0.2f + 0.3f;
        final float g = MathHelper.sin(player.ticksExisted / 3.0f + pos.getY()) * 0.2f + 0.3f;
        final float b = MathHelper.sin(player.ticksExisted / 2.0f + pos.getZ()) * 0.2f + 0.8f;
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glDisable(2884);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glTranslated(-iPX + pos.getX() + 0.5, -iPY + pos.getY() + 0.5, -iPZ + pos.getZ() + 0.5);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.33f);
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        if (dz) {
            GL11.glPushMatrix();
            UtilsFX.renderQuadCentered(this.tex, 1.0f, r, g, b, 200, 1, 1.0f);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            UtilsFX.renderQuadCentered(this.tex, 1.0f, r, g, b, 200, 1, 1.0f);
            GL11.glPopMatrix();
        }
        if (dx) {
            GL11.glPushMatrix();
            GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            UtilsFX.renderQuadCentered(this.tex, 1.0f, r, g, b, 200, 1, 1.0f);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            UtilsFX.renderQuadCentered(this.tex, 1.0f, r, g, b, 200, 1, 1.0f);
            GL11.glPopMatrix();
        }
        if (dy) {
            GL11.glPushMatrix();
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            UtilsFX.renderQuadCentered(this.tex, 1.0f, r, g, b, 200, 1, 1.0f);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            UtilsFX.renderQuadCentered(this.tex, 1.0f, r, g, b, 200, 1, 1.0f);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
    
    static {
        WandRenderingHandler.radialHudScale = 0.0f;
    }
}
