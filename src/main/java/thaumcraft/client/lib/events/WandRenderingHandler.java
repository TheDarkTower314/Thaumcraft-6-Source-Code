package thaumcraft.client.lib.events;
import baubles.api.BaublesApi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.items.IArchitect;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.ItemFocusPouch;
import thaumcraft.common.lib.events.KeyHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;


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
    ResourceLocation R1;
    ResourceLocation R2;
    int lastArcHash;
    ArrayList<BlockPos> architectBlocks;
    HashMap<BlockPos, boolean[]> bmCache;
    ResourceLocation CFRAME;
    ResourceLocation SFRAME;
    int[][] mos;
    int[][] rotmat;
    ResourceLocation tex;
    
    public WandRenderingHandler() {
        foci = new TreeMap<String, Integer>();
        fociItem = new HashMap<String, ItemStack>();
        fociHover = new HashMap<String, Boolean>();
        fociScale = new HashMap<String, Float>();
        lastTime = 0L;
        lastState = false;
        R1 = new ResourceLocation("thaumcraft", "textures/misc/radial.png");
        R2 = new ResourceLocation("thaumcraft", "textures/misc/radial2.png");
        lastArcHash = 0;
        architectBlocks = new ArrayList<BlockPos>();
        bmCache = new HashMap<BlockPos, boolean[]>();
        CFRAME = new ResourceLocation("thaumcraft", "textures/misc/frame_corner.png");
        SFRAME = new ResourceLocation("thaumcraft", "textures/misc/frame_side.png");
        mos = new int[][] { { 4, 5, 6, 7 }, { 0, 1, 2, 3 }, { 0, 1, 4, 5 }, { 2, 3, 6, 7 }, { 0, 2, 4, 6 }, { 1, 3, 5, 7 } };
        rotmat = new int[][] { { 0, 90, 270, 180 }, { 270, 180, 0, 90 }, { 180, 90, 270, 0 }, { 0, 270, 90, 180 }, { 270, 180, 0, 90 }, { 180, 270, 90, 0 } };
        tex = new ResourceLocation("thaumcraft", "textures/misc/architect_arrows.png");
    }
    
    @SideOnly(Side.CLIENT)
    public void handleFociRadial(Minecraft mc, long time, RenderGameOverlayEvent event) {
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
                    getFociInfo(mc);
                    if (foci.size() > 0 && mc.inGameHasFocus) {
                        mc.inGameHasFocus = false;
                        mc.mouseHelper.ungrabMouseCursor();
                    }
                }
            }
            else if (mc.currentScreen == null && lastState) {
                if (Display.isActive() && !mc.inGameHasFocus) {
                    mc.inGameHasFocus = true;
                    mc.mouseHelper.grabMouseCursor();
                }
                lastState = false;
            }
            renderFocusRadialHUD(event.getResolution().getScaledWidth_double(), event.getResolution().getScaledHeight_double(), time, event.getPartialTicks());
            if (time > lastTime) {
                for (String key : fociHover.keySet()) {
                    if (fociHover.get(key)) {
                        if (!KeyHandler.radialActive && !KeyHandler.radialLock) {
                            PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer(key));
                            KeyHandler.radialLock = true;
                        }
                        if (fociScale.get(key) >= 1.3f) {
                            continue;
                        }
                        fociScale.put(key, Math.min(1.3f, fociScale.get(key) + getRadialChange(time, lastTime, 150L)));
                    }
                    else {
                        if (fociScale.get(key) <= 1.0f) {
                            continue;
                        }
                        fociScale.put(key, Math.max(1.0f, fociScale.get(key) - getRadialChange(time, lastTime, 250L)));
                    }
                }
                if (!KeyHandler.radialActive) {
                    WandRenderingHandler.radialHudScale -= getRadialChange(time, lastTime, 150L);
                }
                else if (KeyHandler.radialActive && WandRenderingHandler.radialHudScale < 1.0f) {
                    WandRenderingHandler.radialHudScale += getRadialChange(time, lastTime, 150L);
                }
                if (WandRenderingHandler.radialHudScale > 1.0f) {
                    WandRenderingHandler.radialHudScale = 1.0f;
                }
                if (WandRenderingHandler.radialHudScale < 0.0f) {
                    WandRenderingHandler.radialHudScale = 0.0f;
                    KeyHandler.radialLock = false;
                }
                lastState = KeyHandler.radialActive;
            }
        }
        lastTime = time;
    }
    
    @SideOnly(Side.CLIENT)
    private float getRadialChange(long time, long lasttime, long total) {
        return (time - lasttime) / (float)total;
    }
    
    @SideOnly(Side.CLIENT)
    private void getFociInfo(Minecraft mc) {
        foci.clear();
        fociItem.clear();
        fociHover.clear();
        fociScale.clear();
        int pouchcount = 0;
        ItemStack item = null;
        IInventory baubles = BaublesApi.getBaubles(mc.player);
        for (int a = 0; a < baubles.getSizeInventory(); ++a) {
            if (baubles.getStackInSlot(a) != null && !baubles.getStackInSlot(a).isEmpty() && baubles.getStackInSlot(a).getItem() instanceof ItemFocusPouch) {
                ++pouchcount;
                item = baubles.getStackInSlot(a);
                NonNullList<ItemStack> inv = ((ItemFocusPouch)item.getItem()).getInventory(item);
                for (int q = 0; q < inv.size(); ++q) {
                    item = inv.get(q);
                    if (item.getItem() instanceof ItemFocus) {
                        String sh = ((ItemFocus)item.getItem()).getSortingHelper(item);
                        if (sh != null) {
                            foci.put(sh, q + pouchcount * 1000);
                            fociItem.put(sh, item.copy());
                            fociScale.put(sh, 1.0f);
                            fociHover.put(sh, false);
                        }
                    }
                }
            }
        }
        for (int a = 0; a < 36; ++a) {
            item = mc.player.inventory.mainInventory.get(a);
            if (item.getItem() instanceof ItemFocus) {
                String sh2 = ((ItemFocus)item.getItem()).getSortingHelper(item);
                if (sh2 == null) {
                    continue;
                }
                foci.put(sh2, a);
                fociItem.put(sh2, item.copy());
                fociScale.put(sh2, 1.0f);
                fociHover.put(sh2, false);
            }
            if (item.getItem() instanceof ItemFocusPouch) {
                ++pouchcount;
                NonNullList<ItemStack> inv = ((ItemFocusPouch)item.getItem()).getInventory(item);
                for (int q = 0; q < inv.size(); ++q) {
                    item = inv.get(q);
                    if (item.getItem() instanceof ItemFocus) {
                        String sh = ((ItemFocus)item.getItem()).getSortingHelper(item);
                        if (sh != null) {
                            foci.put(sh, q + pouchcount * 1000);
                            fociItem.put(sh, item.copy());
                            fociScale.put(sh, 1.0f);
                            fociHover.put(sh, false);
                        }
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    private void renderFocusRadialHUD(double sw, double sh, long time, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack s = mc.player.getHeldItemMainhand();
        if (!(s.getItem() instanceof ICaster)) {
            s = mc.player.getHeldItemOffhand();
        }
        if (!(s.getItem() instanceof ICaster)) {
            return;
        }
        ICaster wand = (ICaster)s.getItem();
        ItemFocus focus = (ItemFocus)wand.getFocus(s);
        int i = (int)(Mouse.getEventX() * sw / mc.displayWidth);
        int j = (int)(sh - Mouse.getEventY() * sh / mc.displayHeight - 1.0);
        int k = Mouse.getEventButton();
        if (fociItem.size() == 0) {
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
        float width = 16.0f + fociItem.size() * 2.5f;
        mc.renderEngine.bindTexture(R1);
        GL11.glPushMatrix();
        GL11.glRotatef(partialTicks + mc.player.ticksExisted % 720 / 2.0f, 0.0f, 0.0f, 1.0f);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        UtilsFX.renderQuadCentered(1, 1, 0, width * 2.75f * WandRenderingHandler.radialHudScale, 0.5f, 0.5f, 0.5f, 200, 771, 0.5f);
        GL11.glDisable(3042);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glPopMatrix();
        mc.renderEngine.bindTexture(R2);
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
            ItemStack item = wand.getFocusStack(s).copy();
            UtilsFX.renderItemInGUI(-8, -8, 100, item);
            int mx = (int)(i - sw / 2.0);
            int my = (int)(j - sh / 2.0);
            if (mx >= -10 && mx <= 10 && my >= -10 && my <= 10) {
                tt = item;
            }
        }
        GL11.glScaled(WandRenderingHandler.radialHudScale, WandRenderingHandler.radialHudScale, WandRenderingHandler.radialHudScale);
        float currentRot = -90.0f * WandRenderingHandler.radialHudScale;
        float pieSlice = 360.0f / fociItem.size();
        String key = foci.firstKey();
        for (int a = 0; a < fociItem.size(); ++a) {
            double xx = MathHelper.cos(currentRot / 180.0f * 3.1415927f) * width;
            double yy = MathHelper.sin(currentRot / 180.0f * 3.1415927f) * width;
            currentRot += pieSlice;
            GL11.glPushMatrix();
            GL11.glTranslated((int)xx, (int)yy, 100.0);
            GL11.glScalef(fociScale.get(key), fociScale.get(key), fociScale.get(key));
            GL11.glEnable(32826);
            ItemStack item2 = fociItem.get(key).copy();
            UtilsFX.renderItemInGUI(-8, -8, 100, item2);
            GL11.glDisable(32826);
            GL11.glPopMatrix();
            if (!KeyHandler.radialLock && KeyHandler.radialActive) {
                int mx2 = (int)(i - sw / 2.0 - xx);
                int my2 = (int)(j - sh / 2.0 - yy);
                if (mx2 >= -10 && mx2 <= 10 && my2 >= -10 && my2 <= 10) {
                    fociHover.put(key, true);
                    tt = fociItem.get(key);
                    if (k == 0) {
                        KeyHandler.radialActive = false;
                        KeyHandler.radialLock = true;
                        PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer(key));
                        break;
                    }
                }
                else {
                    fociHover.put(key, false);
                }
            }
            key = foci.higherKey(key);
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
    public boolean handleArchitectOverlay(ItemStack stack, EntityPlayer player, float partialTicks, int playerticks, RayTraceResult target) {
        if (target == null) {
            return false;
        }
        Minecraft mc = Minecraft.getMinecraft();
        IArchitect af = (IArchitect)stack.getItem();
        String h = target.getBlockPos().getX() + "" + target.getBlockPos().getY() + "" + target.getBlockPos().getZ() + "" + target.sideHit + "" + playerticks / 5;
        int hc = h.hashCode();
        if (hc != lastArcHash) {
            lastArcHash = hc;
            bmCache.clear();
            architectBlocks = af.getArchitectBlocks(stack, mc.world, target.getBlockPos(), target.sideHit, player);
        }
        if (architectBlocks == null || architectBlocks.size() == 0) {
            return false;
        }
        drawArchitectAxis(target.getBlockPos(), partialTicks, af.showAxis(stack, mc.world, player, target.sideHit, IArchitect.EnumAxis.X), af.showAxis(stack, mc.world, player, target.sideHit, IArchitect.EnumAxis.Y), af.showAxis(stack, mc.world, player, target.sideHit, IArchitect.EnumAxis.Z));
        for (BlockPos cc : architectBlocks) {
            drawOverlayBlock(cc, playerticks, mc, partialTicks);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        return true;
    }
    
    private boolean isConnectedBlock(World world, BlockPos pos) {
        return architectBlocks.contains(pos);
    }
    
    @SideOnly(Side.CLIENT)
    private boolean[] getConnectedSides(World world, BlockPos pos) {
        if (bmCache.containsKey(pos)) {
            return bmCache.get(pos);
        }
        boolean[] bitMatrix = { !isConnectedBlock(world, pos.add(-1, 0, 0)) && !isConnectedBlock(world, pos.add(0, 0, -1)) && !isConnectedBlock(world, pos.add(0, 1, 0)), !isConnectedBlock(world, pos.add(1, 0, 0)) && !isConnectedBlock(world, pos.add(0, 0, -1)) && !isConnectedBlock(world, pos.add(0, 1, 0)), !isConnectedBlock(world, pos.add(-1, 0, 0)) && !isConnectedBlock(world, pos.add(0, 0, 1)) && !isConnectedBlock(world, pos.add(0, 1, 0)), !isConnectedBlock(world, pos.add(1, 0, 0)) && !isConnectedBlock(world, pos.add(0, 0, 1)) && !isConnectedBlock(world, pos.add(0, 1, 0)), !isConnectedBlock(world, pos.add(-1, 0, 0)) && !isConnectedBlock(world, pos.add(0, 0, -1)) && !isConnectedBlock(world, pos.add(0, -1, 0)), !isConnectedBlock(world, pos.add(1, 0, 0)) && !isConnectedBlock(world, pos.add(0, 0, -1)) && !isConnectedBlock(world, pos.add(0, -1, 0)), !isConnectedBlock(world, pos.add(-1, 0, 0)) && !isConnectedBlock(world, pos.add(0, 0, 1)) && !isConnectedBlock(world, pos.add(0, -1, 0)), !isConnectedBlock(world, pos.add(1, 0, 0)) && !isConnectedBlock(world, pos.add(0, 0, 1)) && !isConnectedBlock(world, pos.add(0, -1, 0)) };
        bmCache.put(pos, bitMatrix);
        return bitMatrix;
    }
    
    @SideOnly(Side.CLIENT)
    public void drawOverlayBlock(BlockPos pos, int ticks, Minecraft mc, float partialTicks) {
        boolean[] bitMatrix = getConnectedSides(mc.world, pos);
        GL11.glPushMatrix();
        GlStateManager.blendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glDisable(2884);
        EntityPlayer player = (EntityPlayer)mc.getRenderViewEntity();
        double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
        GL11.glTranslated(-iPX + pos.getX() + 0.5, -iPY + pos.getY() + 0.5, -iPZ + pos.getZ() + 0.5);
        for (EnumFacing face : EnumFacing.values()) {
            if (!isConnectedBlock(mc.world, pos.offset(face))) {
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
                UtilsFX.renderQuadCentered(SFRAME, 1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 1, 0.1f);
                GL11.glPopMatrix();
                for (int a = 0; a < 4; ++a) {
                    if (bitMatrix[mos[face.ordinal()][a]]) {
                        GL11.glPushMatrix();
                        GL11.glRotatef((float) rotmat[face.ordinal()][a], 0.0f, 0.0f, 1.0f);
                        UtilsFX.renderQuadCentered(CFRAME, 1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 1, 0.66f);
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
    public void drawArchitectAxis(BlockPos pos, float partialTicks, boolean dx, boolean dy, boolean dz) {
        if (!dx && !dy && !dz) {
            return;
        }
        EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
        double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
        float r = MathHelper.sin(player.ticksExisted / 4.0f + pos.getX()) * 0.2f + 0.3f;
        float g = MathHelper.sin(player.ticksExisted / 3.0f + pos.getY()) * 0.2f + 0.3f;
        float b = MathHelper.sin(player.ticksExisted / 2.0f + pos.getZ()) * 0.2f + 0.8f;
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
            UtilsFX.renderQuadCentered(tex, 1.0f, r, g, b, 200, 1, 1.0f);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            UtilsFX.renderQuadCentered(tex, 1.0f, r, g, b, 200, 1, 1.0f);
            GL11.glPopMatrix();
        }
        if (dx) {
            GL11.glPushMatrix();
            GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            UtilsFX.renderQuadCentered(tex, 1.0f, r, g, b, 200, 1, 1.0f);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            UtilsFX.renderQuadCentered(tex, 1.0f, r, g, b, 200, 1, 1.0f);
            GL11.glPopMatrix();
        }
        if (dy) {
            GL11.glPushMatrix();
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            UtilsFX.renderQuadCentered(tex, 1.0f, r, g, b, 200, 1, 1.0f);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            UtilsFX.renderQuadCentered(tex, 1.0f, r, g, b, 200, 1, 1.0f);
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
