// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.events;

import java.util.ArrayList;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderLivingEvent;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.golems.seals.ISealConfigArea;
import java.awt.Color;
import net.minecraft.item.EnumDyeColor;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.api.golems.seals.SealPos;
import java.util.concurrent.ConcurrentHashMap;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.golems.ISealDisplayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraft.block.Block;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.misc.PacketNote;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import net.minecraft.tileentity.TileEntityNote;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.tiles.devices.TileRedstoneRelay;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import org.lwjgl.input.Mouse;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.api.items.IArchitect;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.client.lib.UtilsFX;
import java.util.Calendar;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.common.config.ModConfig;
import java.util.concurrent.LinkedBlockingQueue;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.lib.events.EssentiaHandler;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.shader.ShaderGroup;
import java.util.HashMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import java.util.Random;
import java.util.List;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({ Side.CLIENT })
public class RenderEventHandler
{
    public static RenderEventHandler INSTANCE;
    @SideOnly(Side.CLIENT)
    public static HudHandler hudHandler;
    @SideOnly(Side.CLIENT)
    public static WandRenderingHandler wandHandler;
    @SideOnly(Side.CLIENT)
    static ShaderHandler shaderhandler;
    public static List blockTags;
    public static float tagscale;
    public static int tickCount;
    static boolean checkedDate;
    private Random random;
    public static boolean resetShaders;
    private static int oldDisplayWidth;
    private static int oldDisplayHeight;
    public static Entity thaumTarget;
    static final ResourceLocation CFRAME;
    static final ResourceLocation MIDDLE;
    static EnumFacing[][] rotfaces;
    static int[][] rotmat;
    public static HashMap<Integer, ShaderGroup> shaderGroups;
    public static boolean fogFiddled;
    public static float fogTarget;
    public static int fogDuration;
    public static float prevVignetteBrightness;
    public static float targetBrightness;
    protected static final ResourceLocation vignetteTexPath;
    
    public RenderEventHandler() {
        this.random = new Random();
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void playerTick(final TickEvent.PlayerTickEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.side == Side.SERVER || event.player.getEntityId() != mc.player.getEntityId()) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            try {
                RenderEventHandler.shaderhandler.checkShaders(event, mc);
                if (ShaderHandler.warpVignette > 0) {
                    --ShaderHandler.warpVignette;
                    RenderEventHandler.targetBrightness = 0.0f;
                }
                else {
                    RenderEventHandler.targetBrightness = 1.0f;
                }
                if (RenderEventHandler.fogFiddled) {
                    if (RenderEventHandler.fogDuration < 100) {
                        RenderEventHandler.fogTarget = 0.1f * (RenderEventHandler.fogDuration / 100.0f);
                    }
                    else if (RenderEventHandler.fogTarget < 0.1f) {
                        RenderEventHandler.fogTarget += 0.001f;
                    }
                    --RenderEventHandler.fogDuration;
                    if (RenderEventHandler.fogDuration < 0) {
                        RenderEventHandler.fogFiddled = false;
                    }
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void clientWorldTick(final TickEvent.ClientTickEvent event) {
        if (event.side == Side.SERVER) {
            return;
        }
        final Minecraft mc = FMLClientHandler.instance().getClient();
        final World world = mc.world;
        if (event.phase == TickEvent.Phase.START) {
            ++RenderEventHandler.tickCount;
            for (final String fxk : EssentiaHandler.sourceFX.keySet().toArray(new String[0])) {
                final EssentiaHandler.EssentiaSourceFX fx = EssentiaHandler.sourceFX.get(fxk);
                if (world != null) {
                    int mod = 0;
                    final TileEntity tile = world.getTileEntity(fx.start);
                    if (tile != null && tile instanceof TileInfusionMatrix) {
                        mod = -1;
                    }
                    FXDispatcher.INSTANCE.essentiaTrailFx(fx.end, fx.start.up(mod), RenderEventHandler.tickCount, fx.color, 0.1f, fx.ext);
                    EssentiaHandler.sourceFX.remove(fxk);
                }
            }
        }
        else {
            final LinkedBlockingQueue<HudHandler.KnowledgeGainTracker> temp = new LinkedBlockingQueue<HudHandler.KnowledgeGainTracker>();
            if (RenderEventHandler.hudHandler.knowledgeGainTrackers.isEmpty()) {
                if (RenderEventHandler.hudHandler.kgFade > 0.0f) {
                    final HudHandler hudHandler = RenderEventHandler.hudHandler;
                    --hudHandler.kgFade;
                }
            }
            else {
                final HudHandler hudHandler2 = RenderEventHandler.hudHandler;
                hudHandler2.kgFade += 10.0f;
                if (RenderEventHandler.hudHandler.kgFade > 40.0f) {
                    RenderEventHandler.hudHandler.kgFade = 40.0f;
                }
                while (!RenderEventHandler.hudHandler.knowledgeGainTrackers.isEmpty()) {
                    final HudHandler.KnowledgeGainTracker current = RenderEventHandler.hudHandler.knowledgeGainTrackers.poll();
                    if (current != null && current.progress > 0) {
                        final HudHandler.KnowledgeGainTracker knowledgeGainTracker = current;
                        --knowledgeGainTracker.progress;
                        temp.offer(current);
                    }
                }
                while (!temp.isEmpty()) {
                    RenderEventHandler.hudHandler.knowledgeGainTrackers.offer(temp.poll());
                }
            }
            if (mc.world != null && !RenderEventHandler.checkedDate) {
                RenderEventHandler.checkedDate = true;
                final Calendar calendar = mc.world.getCurrentDate();
                if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
                    ModConfig.isHalloween = true;
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderTick(final TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            UtilsFX.sysPartialTicks = event.renderTickTime;
        }
        else {
            final Minecraft mc = FMLClientHandler.instance().getClient();
            if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
                final long time = System.currentTimeMillis();
                if (player != null) {
                    RenderEventHandler.hudHandler.renderHuds(mc, event.renderTickTime, player, time);
                }
                if ((player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof IArchitect) || (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof IArchitect)) {
                    final ItemStack stack = (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof IArchitect) ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
                    if (!((IArchitect)stack.getItem()).useBlockHighlight(stack)) {
                        final RayTraceResult target2 = ((IArchitect)stack.getItem()).getArchitectMOP(stack, player.world, player);
                        if (target2 != null) {
                            RenderEventHandler.wandHandler.handleArchitectOverlay(stack, player, event.renderTickTime, player.ticksExisted, target2);
                        }
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void tooltipEvent(final ItemTooltipEvent event) {
        final Minecraft mc = FMLClientHandler.instance().getClient();
        final GuiScreen gui = mc.currentScreen;
        if (gui instanceof GuiContainer && GuiScreen.isShiftKeyDown() != ModConfig.CONFIG_GRAPHICS.showTags && !Mouse.isGrabbed() && event.getItemStack() != null) {
            final AspectList tags = ThaumcraftCraftingManager.getObjectTags(event.getItemStack());
            int index = 0;
            if (tags != null && tags.size() > 0) {
                for (final Aspect tag : tags.getAspects()) {
                    if (tag != null) {
                        ++index;
                    }
                }
            }
            final int width = index * 18;
            if (width > 0) {
                final double sw = mc.fontRenderer.getStringWidth(" ");
                final int t = MathHelper.ceil(width / sw);
                for (int l = MathHelper.ceil(18.0 / mc.fontRenderer.FONT_HEIGHT), a = 0; a < l; ++a) {
                    event.getToolTip().add("                                                                                                                                            ".substring(0, Math.min(120, t)));
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void tooltipEvent(final RenderTooltipEvent.PostBackground event) {
        final Minecraft mc = FMLClientHandler.instance().getClient();
        final GuiScreen gui = mc.currentScreen;
        if (gui instanceof GuiContainer && GuiScreen.isShiftKeyDown() != ModConfig.CONFIG_GRAPHICS.showTags && !Mouse.isGrabbed()) {
            int bot = event.getHeight();
            if (!event.getLines().isEmpty()) {
                for (int a = event.getLines().size() - 1; a >= 0; --a) {
                    if (event.getLines().get(a) != null && !event.getLines().get(a).contains("    ")) {
                        bot -= 10;
                    }
                    else if (a > 0 && event.getLines().get(a - 1) != null && event.getLines().get(a - 1).contains("    ")) {
                        RenderEventHandler.hudHandler.renderAspectsInGui((GuiContainer)gui, mc.player, event.getStack(), bot, event.getX(), event.getY());
                        break;
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderOverlay(final RenderGameOverlayEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        final long time = System.nanoTime() / 1000000L;
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            RenderEventHandler.wandHandler.handleFociRadial(mc, time, event);
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.PORTAL) {
            renderVignette(RenderEventHandler.targetBrightness, event.getResolution().getScaledWidth_double(), event.getResolution().getScaledHeight_double());
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderShaders(final RenderGameOverlayEvent.Pre event) {
        if (!ModConfig.CONFIG_GRAPHICS.disableShaders && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (OpenGlHelper.shadersSupported && RenderEventHandler.shaderGroups.size() > 0) {
                updateShaderFrameBuffers(mc);
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                for (final ShaderGroup sg : RenderEventHandler.shaderGroups.values()) {
                    GL11.glPushMatrix();
                    try {
                        sg.render(event.getPartialTicks());
                    }
                    catch (final Exception ex) {}
                    GL11.glPopMatrix();
                }
                mc.getFramebuffer().bindFramebuffer(true);
            }
        }
    }
    
    private static void updateShaderFrameBuffers(final Minecraft mc) {
        if (RenderEventHandler.resetShaders || mc.displayWidth != RenderEventHandler.oldDisplayWidth || RenderEventHandler.oldDisplayHeight != mc.displayHeight) {
            for (final ShaderGroup sg : RenderEventHandler.shaderGroups.values()) {
                sg.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            }
            RenderEventHandler.oldDisplayWidth = mc.displayWidth;
            RenderEventHandler.oldDisplayHeight = mc.displayHeight;
            RenderEventHandler.resetShaders = false;
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void blockHighlight(final DrawBlockHighlightEvent event) {
        final int ticks = event.getPlayer().ticksExisted;
        final RayTraceResult target = event.getTarget();
        if (RenderEventHandler.blockTags.size() > 0) {
            final int x = (int) RenderEventHandler.blockTags.get(0);
            final int y = (int) RenderEventHandler.blockTags.get(1);
            final int z = (int) RenderEventHandler.blockTags.get(2);
            final AspectList ot = (AspectList) RenderEventHandler.blockTags.get(3);
            final EnumFacing dir = EnumFacing.VALUES[(int) RenderEventHandler.blockTags.get(4)];
            if (x == target.getBlockPos().getX() && y == target.getBlockPos().getY() && z == target.getBlockPos().getZ()) {
                if (RenderEventHandler.tagscale < 0.5f) {
                    RenderEventHandler.tagscale += 0.031f - RenderEventHandler.tagscale / 10.0f;
                }
                drawTagsOnContainer(target.getBlockPos().getX() + dir.getFrontOffsetX() / 2.0f, target.getBlockPos().getY() + dir.getFrontOffsetY() / 2.0f, target.getBlockPos().getZ() + dir.getFrontOffsetZ() / 2.0f, ot, 220, dir, event.getPartialTicks());
            }
        }
        if (target != null && target.getBlockPos() != null) {
            final TileEntity te = event.getPlayer().world.getTileEntity(target.getBlockPos());
            if (te != null && te instanceof TileRedstoneRelay) {
                final RayTraceResult hit = RayTracer.retraceBlock(event.getPlayer().world, event.getPlayer(), target.getBlockPos());
                if (hit != null) {
                    if (hit.subHit == 0) {
                        drawTextInAir(target.getBlockPos().getX(), target.getBlockPos().getY() + 0.3, target.getBlockPos().getZ(), event.getPartialTicks(), "Out: " + ((TileRedstoneRelay)te).getOut());
                    }
                    else if (hit.subHit == 1) {
                        drawTextInAir(target.getBlockPos().getX(), target.getBlockPos().getY() + 0.3, target.getBlockPos().getZ(), event.getPartialTicks(), "In: " + ((TileRedstoneRelay)te).getIn());
                    }
                }
            }
            if (EntityUtils.hasGoggles(event.getPlayer())) {
                float to = 0.0f;
                if (te instanceof IGogglesDisplayExtended) {
                    GL11.glDisable(2929);
                    final Vec3d v = ((IGogglesDisplayExtended)te).getIGogglesTextOffset();
                    final String[] iGogglesText;
                    final String[] sa = iGogglesText = ((IGogglesDisplayExtended)te).getIGogglesText();
                    for (final String s : iGogglesText) {
                        drawTextInAir(target.getBlockPos().getX() + v.x, target.getBlockPos().getY() + v.y - (to - sa.length / 2.0f) / 5.5f, target.getBlockPos().getZ() + v.z, event.getPartialTicks(), s);
                        ++to;
                    }
                    GL11.glEnable(2929);
                }
                else {
                    final Block b = event.getPlayer().world.getBlockState(target.getBlockPos()).getBlock();
                    if (b instanceof IGogglesDisplayExtended) {
                        GL11.glDisable(2929);
                        final Vec3d v2 = ((IGogglesDisplayExtended)b).getIGogglesTextOffset();
                        final String[] iGogglesText2;
                        final String[] sa2 = iGogglesText2 = ((IGogglesDisplayExtended)b).getIGogglesText();
                        for (final String s2 : iGogglesText2) {
                            drawTextInAir(target.getBlockPos().getX() + v2.x, target.getBlockPos().getY() + v2.y + (to - sa2.length / 2.0f) / 5.5f, target.getBlockPos().getZ() + v2.z, event.getPartialTicks(), s2);
                            ++to;
                        }
                        GL11.glEnable(2929);
                    }
                }
                final boolean spaceAbove = event.getPlayer().world.isAirBlock(target.getBlockPos().up());
                if (te != null) {
                    int note = -1;
                    if (te instanceof TileEntityNote) {
                        note = ((TileEntityNote)te).note;
                    }
                    else if (te instanceof TileArcaneEar) {
                        note = ((TileArcaneEar)te).note;
                    }
                    else if (te instanceof IAspectContainer && ((IAspectContainer)te).getAspects() != null && ((IAspectContainer)te).getAspects().size() > 0) {
                        final float shift = 0.0f;
                        if (RenderEventHandler.tagscale < 0.3f) {
                            RenderEventHandler.tagscale += 0.031f - RenderEventHandler.tagscale / 10.0f;
                        }
                        drawTagsOnContainer(target.getBlockPos().getX(), target.getBlockPos().getY() + (spaceAbove ? 0.4f : 0.0f) + shift, target.getBlockPos().getZ(), ((IAspectContainer)te).getAspects(), 220, spaceAbove ? EnumFacing.UP : event.getTarget().sideHit, event.getPartialTicks());
                    }
                    if (note >= 0) {
                        if (ticks % 5 == 0) {
                            PacketHandler.INSTANCE.sendToServer(new PacketNote(target.getBlockPos().getX(), target.getBlockPos().getY(), target.getBlockPos().getZ(), event.getPlayer().world.provider.getDimension()));
                        }
                        drawTextInAir(target.getBlockPos().getX(), target.getBlockPos().getY() + 1, target.getBlockPos().getZ(), event.getPartialTicks(), "Note: " + note);
                    }
                }
            }
        }
        if (target.typeOfHit == RayTraceResult.Type.BLOCK && ((event.getPlayer().getHeldItemMainhand() != null && event.getPlayer().getHeldItemMainhand().getItem() instanceof IArchitect) || (event.getPlayer().getHeldItemOffhand() != null && event.getPlayer().getHeldItemOffhand().getItem() instanceof IArchitect))) {
            final ItemStack stack = (event.getPlayer().getHeldItemMainhand() != null && event.getPlayer().getHeldItemMainhand().getItem() instanceof IArchitect) ? event.getPlayer().getHeldItemMainhand() : event.getPlayer().getHeldItemOffhand();
            if (((IArchitect)stack.getItem()).useBlockHighlight(stack)) {
                final RayTraceResult target2 = ((IArchitect)stack.getItem()).getArchitectMOP(stack, event.getPlayer().world, event.getPlayer());
                if (target2 != null && RenderEventHandler.wandHandler.handleArchitectOverlay(stack, event.getPlayer(), event.getPartialTicks(), event.getPlayer().ticksExisted, target2)) {
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderLast(final RenderWorldLastEvent event) {
        if (RenderEventHandler.tagscale > 0.0f) {
            RenderEventHandler.tagscale -= 0.005f;
        }
        final float partialTicks = event.getPartialTicks();
        final Minecraft mc = Minecraft.getMinecraft();
        if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)mc.getRenderViewEntity();
            if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ISealDisplayer) {
                drawSeals(partialTicks, player);
            }
            else if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof ISealDisplayer) {
                drawSeals(partialTicks, player);
            }
            if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof IArchitect) {
                final RayTraceResult target = ((IArchitect)player.getHeldItemMainhand().getItem()).getArchitectMOP(player.getHeldItemMainhand(), player.world, player);
                RenderEventHandler.wandHandler.handleArchitectOverlay(player.getHeldItemMainhand(), player, partialTicks, player.ticksExisted, target);
            }
            else if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof IArchitect) {
                final RayTraceResult target = ((IArchitect)player.getHeldItemOffhand().getItem()).getArchitectMOP(player.getHeldItemOffhand(), player.world, player);
                RenderEventHandler.wandHandler.handleArchitectOverlay(player.getHeldItemOffhand(), player, partialTicks, player.ticksExisted, target);
            }
            if (RenderEventHandler.thaumTarget != null) {
                final AspectList ot = AspectHelper.getEntityAspects(RenderEventHandler.thaumTarget);
                if (ot != null && !ot.aspects.isEmpty()) {
                    if (RenderEventHandler.tagscale < 0.5f) {
                        RenderEventHandler.tagscale += 0.031f - RenderEventHandler.tagscale / 10.0f;
                    }
                    final double iPX = RenderEventHandler.thaumTarget.prevPosX + (RenderEventHandler.thaumTarget.posX - RenderEventHandler.thaumTarget.prevPosX) * partialTicks;
                    final double iPY = RenderEventHandler.thaumTarget.prevPosY + (RenderEventHandler.thaumTarget.posY - RenderEventHandler.thaumTarget.prevPosY) * partialTicks;
                    final double iPZ = RenderEventHandler.thaumTarget.prevPosZ + (RenderEventHandler.thaumTarget.posZ - RenderEventHandler.thaumTarget.prevPosZ) * partialTicks;
                    drawTagsOnContainer(iPX, iPY + RenderEventHandler.thaumTarget.height, iPZ, ot, 220, null, event.getPartialTicks());
                }
            }
        }
    }
    
    private static void drawSeals(final float partialTicks, final EntityPlayer player) {
        final ConcurrentHashMap<SealPos, SealEntity> seals = SealHandler.sealEntities.get(player.world.provider.getDimension());
        if (seals != null && seals.size() > 0) {
            GL11.glPushMatrix();
            if (player.isSneaking()) {
                GL11.glDisable(2929);
            }
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2884);
            final double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
            final double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
            final double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
            GL11.glTranslated(-iPX, -iPY, -iPZ);
            for (final ISealEntity seal : seals.values()) {
                final double dis = player.getDistanceSqToCenter(seal.getSealPos().pos);
                if (dis <= 256.0) {
                    final float alpha = 1.0f - (float)(dis / 256.0);
                    boolean ia = false;
                    if (seal.isStoppedByRedstone(player.world)) {
                        ia = true;
                        if (player.world.rand.nextFloat() < partialTicks / 12.0f) {
                            FXDispatcher.INSTANCE.spark(seal.getSealPos().pos.getX() + 0.5f + seal.getSealPos().face.getFrontOffsetX() * 0.66f, seal.getSealPos().pos.getY() + 0.5f + seal.getSealPos().face.getFrontOffsetY() * 0.66f, seal.getSealPos().pos.getZ() + 0.5f + seal.getSealPos().face.getFrontOffsetZ() * 0.66f, 2.0f, 0.8f - player.world.rand.nextFloat() * 0.2f, 0.0f, 0.0f, 1.0f);
                            ia = false;
                        }
                    }
                    renderSeal(seal.getSealPos().pos.getX(), seal.getSealPos().pos.getY(), seal.getSealPos().pos.getZ(), alpha, seal.getSealPos().face, seal.getSeal().getSealIcon(), ia);
                    drawSealArea(player, seal, alpha, partialTicks);
                }
            }
            GL11.glDisable(3042);
            GL11.glEnable(2884);
            if (player.isSneaking()) {
                GL11.glEnable(2929);
            }
            GL11.glPopMatrix();
        }
    }
    
    private static void drawSealArea(final EntityPlayer player, final ISealEntity seal, final float alpha, final float partialTicks) {
        GL11.glPushMatrix();
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;
        if (seal.getColor() > 0) {
            final Color c = new Color(EnumDyeColor.byMetadata(seal.getColor() - 1).getColorValue());
            r = c.getRed() / 255.0f;
            g = c.getGreen() / 255.0f;
            b = c.getBlue() / 255.0f;
        }
        else {
            r = 0.7f + MathHelper.sin((player.ticksExisted + partialTicks + seal.getSealPos().pos.getX()) / 4.0f) * 0.1f;
            g = 0.7f + MathHelper.sin((player.ticksExisted + partialTicks + seal.getSealPos().pos.getY()) / 5.0f) * 0.1f;
            b = 0.7f + MathHelper.sin((player.ticksExisted + partialTicks + seal.getSealPos().pos.getZ()) / 6.0f) * 0.1f;
        }
        GL11.glPushMatrix();
        GL11.glTranslated(seal.getSealPos().pos.getX() + 0.5, seal.getSealPos().pos.getY() + 0.5, seal.getSealPos().pos.getZ() + 0.5);
        GL11.glRotatef(90.0f, (float)(-seal.getSealPos().face.getFrontOffsetY()), (float)seal.getSealPos().face.getFrontOffsetX(), (float)(-seal.getSealPos().face.getFrontOffsetZ()));
        if (seal.getSealPos().face.getFrontOffsetZ() < 0) {
            GL11.glTranslated(0.0, 0.0, -0.5099999904632568);
        }
        else {
            GL11.glTranslated(0.0, 0.0, 0.5099999904632568);
        }
        GL11.glRotatef(player.ticksExisted % 360 + partialTicks, 0.0f, 0.0f, 1.0f);
        UtilsFX.renderQuadCentered(RenderEventHandler.MIDDLE, 0.9f, r, g, b, 200, 771, alpha * 0.8f);
        GL11.glPopMatrix();
        if (seal.getSeal() instanceof ISealConfigArea) {
            GL11.glDepthMask(false);
            final AxisAlignedBB area = new AxisAlignedBB(seal.getSealPos().pos.getX(), seal.getSealPos().pos.getY(), seal.getSealPos().pos.getZ(), seal.getSealPos().pos.getX() + 1, seal.getSealPos().pos.getY() + 1, seal.getSealPos().pos.getZ() + 1).offset(seal.getSealPos().face.getFrontOffsetX(), seal.getSealPos().face.getFrontOffsetY(), seal.getSealPos().face.getFrontOffsetZ()).expand((seal.getSealPos().face.getFrontOffsetX() != 0) ? ((double)((seal.getArea().getX() - 1) * seal.getSealPos().face.getFrontOffsetX())) : 0.0, (seal.getSealPos().face.getFrontOffsetY() != 0) ? ((double)((seal.getArea().getY() - 1) * seal.getSealPos().face.getFrontOffsetY())) : 0.0, (seal.getSealPos().face.getFrontOffsetZ() != 0) ? ((double)((seal.getArea().getZ() - 1) * seal.getSealPos().face.getFrontOffsetZ())) : 0.0).grow((seal.getSealPos().face.getFrontOffsetX() == 0) ? ((double)(seal.getArea().getX() - 1)) : 0.0, (seal.getSealPos().face.getFrontOffsetY() == 0) ? ((double)(seal.getArea().getY() - 1)) : 0.0, (seal.getSealPos().face.getFrontOffsetZ() == 0) ? ((double)(seal.getArea().getZ() - 1)) : 0.0);
            final double[][] locs = { { area.minX, area.minY, area.minZ }, { area.minX, area.maxY - 1.0, area.minZ }, { area.maxX - 1.0, area.minY, area.minZ }, { area.maxX - 1.0, area.maxY - 1.0, area.minZ }, { area.maxX - 1.0, area.minY, area.maxZ - 1.0 }, { area.maxX - 1.0, area.maxY - 1.0, area.maxZ - 1.0 }, { area.minX, area.minY, area.maxZ - 1.0 }, { area.minX, area.maxY - 1.0, area.maxZ - 1.0 } };
            int q = 0;
            for (final double[] loc : locs) {
                GL11.glPushMatrix();
                GL11.glTranslated(loc[0] + 0.5, loc[1] + 0.5, loc[2] + 0.5);
                int w = 0;
                for (final EnumFacing face : RenderEventHandler.rotfaces[q]) {
                    GL11.glPushMatrix();
                    GL11.glRotatef(90.0f, (float)(-face.getFrontOffsetY()), (float)face.getFrontOffsetX(), (float)(-face.getFrontOffsetZ()));
                    if (face.getFrontOffsetZ() < 0) {
                        GL11.glTranslated(0.0, 0.0, -0.49000000953674316);
                    }
                    else {
                        GL11.glTranslated(0.0, 0.0, 0.49000000953674316);
                    }
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
                    GL11.glRotatef((float)RenderEventHandler.rotmat[q][w], 0.0f, 0.0f, 1.0f);
                    UtilsFX.renderQuadCentered(RenderEventHandler.CFRAME, 1.0f, r, g, b, 200, 771, alpha * 0.7f);
                    GL11.glPopMatrix();
                    ++w;
                }
                GL11.glPopMatrix();
                ++q;
            }
            GL11.glDepthMask(true);
        }
        GL11.glPopMatrix();
    }
    
    static void renderSeal(final int x, final int y, final int z, final float alpha, final EnumFacing face, final ResourceLocation resourceLocation, final boolean ia) {
        GL11.glPushMatrix();
        GL11.glColor4f(ia ? 0.5f : 1.0f, ia ? 0.5f : 1.0f, ia ? 0.5f : 1.0f, alpha);
        translateSeal((float)x, (float)y, (float)z, face.ordinal(), -0.05f);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        UtilsFX.renderItemIn2D(resourceLocation.toString(), Minecraft.getMinecraft().getRenderViewEntity().isSneaking() ? 0.0f : 0.1f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    private static void translateSeal(final float x, final float y, final float z, final int orientation, final float off) {
        if (orientation == 1) {
            GL11.glTranslatef(x + 0.25f, y + 1.0f, z + 0.75f);
            GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        }
        else if (orientation == 0) {
            GL11.glTranslatef(x + 0.25f, y, z + 0.25f);
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        }
        else if (orientation == 3) {
            GL11.glTranslatef(x + 0.25f, y + 0.25f, z + 1.0f);
        }
        else if (orientation == 2) {
            GL11.glTranslatef(x + 0.75f, y + 0.25f, z);
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        }
        else if (orientation == 5) {
            GL11.glTranslatef(x + 1.0f, y + 0.25f, z + 0.75f);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        }
        else if (orientation == 4) {
            GL11.glTranslatef(x, y + 0.25f, z + 0.25f);
            GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        }
        GL11.glTranslatef(0.0f, 0.0f, -off);
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void fogDensityEvent(final EntityViewRenderEvent.RenderFogEvent event) {
        if (RenderEventHandler.fogFiddled && RenderEventHandler.fogTarget > 0.0f) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, RenderEventHandler.fogTarget);
        }
    }
    
    @SubscribeEvent
    public static void livingTick(final LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().world.isRemote && event.getEntity() instanceof EntityCreature && !event.getEntity().isDead) {
            final EntityCreature mob = (EntityCreature)event.getEntity();
            if (mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD) != null) {
                final Integer t = (int)mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
                if (t != null && t >= 0 && t < ChampionModifier.mods.length) {
                    ChampionModifier.mods[t].effect.showFX(mob);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void renderLivingPre(final RenderLivingEvent.Pre event) {
        if (event.getEntity().world.isRemote && event.getEntity() instanceof EntityCreature && !event.getEntity().isDead) {
            final EntityCreature mob = (EntityCreature)event.getEntity();
            if (mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD) != null) {
                final Integer t = (int)mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
                if (t != null && t >= 0 && t < ChampionModifier.mods.length) {
                    ChampionModifier.mods[t].effect.preRender(mob, event.getRenderer());
                }
            }
        }
    }
    
    public static void drawTagsOnContainer(double x, final double y, double z, final AspectList tags, final int bright, final EnumFacing dir, final float partialTicks) {
        if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer && tags != null && tags.size() > 0) {
            int fox = 0;
            int foy = 0;
            int foz = 0;
            if (dir != null) {
                fox = dir.getFrontOffsetX();
                foy = dir.getFrontOffsetY();
                foz = dir.getFrontOffsetZ();
            }
            else {
                x -= 0.5;
                z -= 0.5;
            }
            final EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
            final double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
            final double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
            final double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
            final int rowsize = 5;
            int current = 0;
            float shifty = 0.0f;
            int left = tags.size();
            for (final Aspect tag : tags.getAspects()) {
                int div = Math.min(left, rowsize);
                if (current >= rowsize) {
                    current = 0;
                    shifty -= RenderEventHandler.tagscale * 1.05f;
                    left -= rowsize;
                    if (left < rowsize) {
                        div = left % rowsize;
                    }
                }
                float shift = (current - div / 2.0f + 0.5f) * RenderEventHandler.tagscale * 4.0f;
                shift *= RenderEventHandler.tagscale;
                final Color color = new Color(tag.getColor());
                GL11.glPushMatrix();
                GL11.glDisable(2929);
                GL11.glTranslated(-iPX + x + 0.5 + RenderEventHandler.tagscale * 2.0f * fox, -iPY + y - shifty + 0.5 + RenderEventHandler.tagscale * 2.0f * foy, -iPZ + z + 0.5 + RenderEventHandler.tagscale * 2.0f * foz);
                final float xd = (float)(iPX - (x + 0.5));
                final float zd = (float)(iPZ - (z + 0.5));
                final float rotYaw = (float)(Math.atan2(xd, zd) * 180.0 / 3.141592653589793);
                GL11.glRotatef(rotYaw + 180.0f, 0.0f, 1.0f, 0.0f);
                GL11.glTranslated(shift, 0.0, 0.0);
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glScalef(RenderEventHandler.tagscale, RenderEventHandler.tagscale, RenderEventHandler.tagscale);
                UtilsFX.renderQuadCentered(tag.getImage(), 1.0f, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, bright, 771, 0.75f);
                if (tags.getAmount(tag) >= 0) {
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                    final String am = "" + tags.getAmount(tag);
                    GL11.glScalef(0.04f, 0.04f, 0.04f);
                    GL11.glTranslated(0.0, 6.0, -0.1);
                    final int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(am);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    Minecraft.getMinecraft().fontRenderer.drawString(am, 14 - sw, 1, 1118481);
                    GL11.glTranslated(0.0, 0.0, -0.1);
                    Minecraft.getMinecraft().fontRenderer.drawString(am, 13 - sw, 0, 16777215);
                }
                GL11.glEnable(2929);
                GL11.glPopMatrix();
                ++current;
            }
        }
    }
    
    public static void drawTextInAir(final double x, final double y, final double z, final float partialTicks, final String text) {
        if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
            final double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
            final double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
            final double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
            GL11.glPushMatrix();
            GL11.glTranslated(-iPX + x + 0.5, -iPY + y + 0.5, -iPZ + z + 0.5);
            final float xd = (float)(iPX - (x + 0.5));
            final float zd = (float)(iPZ - (z + 0.5));
            final float rotYaw = (float)(Math.atan2(xd, zd) * 180.0 / 3.141592653589793);
            GL11.glRotatef(rotYaw + 180.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glScalef(0.0125f, 0.0125f, 0.0125f);
            final int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            Minecraft.getMinecraft().fontRenderer.drawString(text, (float)(1 - sw / 2), 1.0f, 16777215, true);
            GL11.glPopMatrix();
        }
    }
    
    protected static void renderVignette(float brightness, final double sw, final double sh) {
        final int k = (int)sw;
        final int l = (int)sh;
        brightness = 1.0f - brightness;
        RenderEventHandler.prevVignetteBrightness += (float)((brightness - RenderEventHandler.prevVignetteBrightness) * 0.01);
        if (RenderEventHandler.prevVignetteBrightness > 0.0f) {
            final float b = RenderEventHandler.prevVignetteBrightness * (1.0f + MathHelper.sin(Minecraft.getMinecraft().player.ticksExisted / 2.0f) * 0.1f);
            GL11.glPushMatrix();
            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, sw, sh, 0.0, 1000.0, 3000.0);
            Minecraft.getMinecraft().getTextureManager().bindTexture(RenderEventHandler.vignetteTexPath);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, 0.0f, -2000.0f);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            OpenGlHelper.glBlendFunc(0, 769, 1, 0);
            GL11.glColor4f(b, b, b, 1.0f);
            final Tessellator tessellator = Tessellator.getInstance();
            tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
            tessellator.getBuffer().pos(0.0, l, -90.0).tex(0.0, 1.0).endVertex();
            tessellator.getBuffer().pos(k, l, -90.0).tex(1.0, 1.0).endVertex();
            tessellator.getBuffer().pos(k, 0.0, -90.0).tex(1.0, 0.0).endVertex();
            tessellator.getBuffer().pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
            tessellator.draw();
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glPopMatrix();
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void textureStitchEventPre(final TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "research/quill"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/crystal"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/taint_growth_1"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/taint_growth_2"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/taint_growth_3"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/taint_growth_4"));
    }
    
    static {
        RenderEventHandler.INSTANCE = new RenderEventHandler();
        RenderEventHandler.hudHandler = new HudHandler();
        RenderEventHandler.wandHandler = new WandRenderingHandler();
        RenderEventHandler.shaderhandler = new ShaderHandler();
        RenderEventHandler.blockTags = new ArrayList();
        RenderEventHandler.tagscale = 0.0f;
        RenderEventHandler.tickCount = 0;
        RenderEventHandler.checkedDate = false;
        RenderEventHandler.resetShaders = false;
        RenderEventHandler.oldDisplayWidth = 0;
        RenderEventHandler.oldDisplayHeight = 0;
        RenderEventHandler.thaumTarget = null;
        CFRAME = new ResourceLocation("thaumcraft", "textures/misc/frame_corner.png");
        MIDDLE = new ResourceLocation("thaumcraft", "textures/misc/seal_area.png");
        RenderEventHandler.rotfaces = new EnumFacing[][] { { EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.WEST }, { EnumFacing.UP, EnumFacing.NORTH, EnumFacing.WEST }, { EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST }, { EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST }, { EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.EAST }, { EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.EAST }, { EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.WEST }, { EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.WEST } };
        RenderEventHandler.rotmat = new int[][] { { 0, 270, 0 }, { 270, 180, 270 }, { 90, 0, 90 }, { 180, 90, 180 }, { 180, 180, 0 }, { 90, 270, 270 }, { 270, 90, 90 }, { 0, 0, 180 } };
        RenderEventHandler.shaderGroups = new HashMap<Integer, ShaderGroup>();
        RenderEventHandler.fogFiddled = false;
        RenderEventHandler.fogTarget = 0.0f;
        RenderEventHandler.fogDuration = 0;
        RenderEventHandler.prevVignetteBrightness = 0.0f;
        RenderEventHandler.targetBrightness = 1.0f;
        vignetteTexPath = new ResourceLocation("thaumcraft", "textures/misc/vignette.png");
    }
    
    public static class ChargeEntry
    {
        public long time;
        public long tickTime;
        public ItemStack item;
        float charge;
        byte diff;
        
        public ChargeEntry(final long time, final ItemStack item, final float charge) {
            this.charge = 0.0f;
            this.diff = 0;
            this.time = time;
            this.item = item;
            this.charge = charge;
        }
    }
}
