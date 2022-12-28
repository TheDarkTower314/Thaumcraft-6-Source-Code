package thaumcraft.client.lib.events;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.items.IArchitect;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketNote;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import thaumcraft.common.tiles.devices.TileRedstoneRelay;


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
    static ResourceLocation CFRAME;
    static ResourceLocation MIDDLE;
    static EnumFacing[][] rotfaces;
    static int[][] rotmat;
    public static HashMap<Integer, ShaderGroup> shaderGroups;
    public static boolean fogFiddled;
    public static float fogTarget;
    public static int fogDuration;
    public static float prevVignetteBrightness;
    public static float targetBrightness;
    protected static ResourceLocation vignetteTexPath;
    
    public RenderEventHandler() {
        random = new Random();
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
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
            catch (Exception ex) {}
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void clientWorldTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.SERVER) {
            return;
        }
        Minecraft mc = FMLClientHandler.instance().getClient();
        World world = mc.world;
        if (event.phase == TickEvent.Phase.START) {
            ++RenderEventHandler.tickCount;
            for (String fxk : EssentiaHandler.sourceFX.keySet().toArray(new String[0])) {
                EssentiaHandler.EssentiaSourceFX fx = EssentiaHandler.sourceFX.get(fxk);
                if (world != null) {
                    int mod = 0;
                    TileEntity tile = world.getTileEntity(fx.start);
                    if (tile != null && tile instanceof TileInfusionMatrix) {
                        mod = -1;
                    }
                    FXDispatcher.INSTANCE.essentiaTrailFx(fx.end, fx.start.up(mod), RenderEventHandler.tickCount, fx.color, 0.1f, fx.ext);
                    EssentiaHandler.sourceFX.remove(fxk);
                }
            }
        }
        else {
            LinkedBlockingQueue<HudHandler.KnowledgeGainTracker> temp = new LinkedBlockingQueue<HudHandler.KnowledgeGainTracker>();
            if (RenderEventHandler.hudHandler.knowledgeGainTrackers.isEmpty()) {
                if (RenderEventHandler.hudHandler.kgFade > 0.0f) {
                    HudHandler hudHandler = RenderEventHandler.hudHandler;
                    --hudHandler.kgFade;
                }
            }
            else {
                HudHandler hudHandler2 = RenderEventHandler.hudHandler;
                hudHandler2.kgFade += 10.0f;
                if (RenderEventHandler.hudHandler.kgFade > 40.0f) {
                    RenderEventHandler.hudHandler.kgFade = 40.0f;
                }
                while (!RenderEventHandler.hudHandler.knowledgeGainTrackers.isEmpty()) {
                    HudHandler.KnowledgeGainTracker current = RenderEventHandler.hudHandler.knowledgeGainTrackers.poll();
                    if (current != null && current.progress > 0) {
                        HudHandler.KnowledgeGainTracker knowledgeGainTracker = current;
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
                Calendar calendar = mc.world.getCurrentDate();
                if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
                    ModConfig.isHalloween = true;
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            UtilsFX.sysPartialTicks = event.renderTickTime;
        }
        else {
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
                long time = System.currentTimeMillis();
                if (player != null) {
                    RenderEventHandler.hudHandler.renderHuds(mc, event.renderTickTime, player, time);
                }
                if ((player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof IArchitect) || (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof IArchitect)) {
                    ItemStack stack = (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof IArchitect) ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
                    if (!((IArchitect)stack.getItem()).useBlockHighlight(stack)) {
                        RayTraceResult target2 = ((IArchitect)stack.getItem()).getArchitectMOP(stack, player.world, player);
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
    public static void tooltipEvent(ItemTooltipEvent event) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        GuiScreen gui = mc.currentScreen;
        if (gui instanceof GuiContainer && GuiScreen.isShiftKeyDown() != ModConfig.CONFIG_GRAPHICS.showTags && !Mouse.isGrabbed() && event.getItemStack() != null) {
            AspectList tags = ThaumcraftCraftingManager.getObjectTags(event.getItemStack());
            int index = 0;
            if (tags != null && tags.size() > 0) {
                for (Aspect tag : tags.getAspects()) {
                    if (tag != null) {
                        ++index;
                    }
                }
            }
            int width = index * 18;
            if (width > 0) {
                double sw = mc.fontRenderer.getStringWidth(" ");
                int t = MathHelper.ceil(width / sw);
                for (int l = MathHelper.ceil(18.0 / mc.fontRenderer.FONT_HEIGHT), a = 0; a < l; ++a) {
                    event.getToolTip().add("                                                                                                                                            ".substring(0, Math.min(120, t)));
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void tooltipEvent(RenderTooltipEvent.PostBackground event) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        GuiScreen gui = mc.currentScreen;
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
    public static void renderOverlay(RenderGameOverlayEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        long time = System.nanoTime() / 1000000L;
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            RenderEventHandler.wandHandler.handleFociRadial(mc, time, event);
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.PORTAL) {
            renderVignette(RenderEventHandler.targetBrightness, event.getResolution().getScaledWidth_double(), event.getResolution().getScaledHeight_double());
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderShaders(RenderGameOverlayEvent.Pre event) {
        if (!ModConfig.CONFIG_GRAPHICS.disableShaders && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            Minecraft mc = Minecraft.getMinecraft();
            if (OpenGlHelper.shadersSupported && RenderEventHandler.shaderGroups.size() > 0) {
                updateShaderFrameBuffers(mc);
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                for (ShaderGroup sg : RenderEventHandler.shaderGroups.values()) {
                    GL11.glPushMatrix();
                    try {
                        sg.render(event.getPartialTicks());
                    }
                    catch (Exception ex) {}
                    GL11.glPopMatrix();
                }
                mc.getFramebuffer().bindFramebuffer(true);
            }
        }
    }
    
    private static void updateShaderFrameBuffers(Minecraft mc) {
        if (RenderEventHandler.resetShaders || mc.displayWidth != RenderEventHandler.oldDisplayWidth || RenderEventHandler.oldDisplayHeight != mc.displayHeight) {
            for (ShaderGroup sg : RenderEventHandler.shaderGroups.values()) {
                sg.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            }
            RenderEventHandler.oldDisplayWidth = mc.displayWidth;
            RenderEventHandler.oldDisplayHeight = mc.displayHeight;
            RenderEventHandler.resetShaders = false;
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void blockHighlight(DrawBlockHighlightEvent event) {
        int ticks = event.getPlayer().ticksExisted;
        RayTraceResult target = event.getTarget();
        if (RenderEventHandler.blockTags.size() > 0) {
            int x = (int) RenderEventHandler.blockTags.get(0);
            int y = (int) RenderEventHandler.blockTags.get(1);
            int z = (int) RenderEventHandler.blockTags.get(2);
            AspectList ot = (AspectList) RenderEventHandler.blockTags.get(3);
            EnumFacing dir = EnumFacing.VALUES[(int) RenderEventHandler.blockTags.get(4)];
            if (x == target.getBlockPos().getX() && y == target.getBlockPos().getY() && z == target.getBlockPos().getZ()) {
                if (RenderEventHandler.tagscale < 0.5f) {
                    RenderEventHandler.tagscale += 0.031f - RenderEventHandler.tagscale / 10.0f;
                }
                drawTagsOnContainer(target.getBlockPos().getX() + dir.getFrontOffsetX() / 2.0f, target.getBlockPos().getY() + dir.getFrontOffsetY() / 2.0f, target.getBlockPos().getZ() + dir.getFrontOffsetZ() / 2.0f, ot, 220, dir, event.getPartialTicks());
            }
        }
        if (target != null && target.getBlockPos() != null) {
            TileEntity te = event.getPlayer().world.getTileEntity(target.getBlockPos());
            if (te != null && te instanceof TileRedstoneRelay) {
                RayTraceResult hit = RayTracer.retraceBlock(event.getPlayer().world, event.getPlayer(), target.getBlockPos());
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
                    Vec3d v = ((IGogglesDisplayExtended)te).getIGogglesTextOffset();
                    String[] iGogglesText;
                    String[] sa = iGogglesText = ((IGogglesDisplayExtended)te).getIGogglesText();
                    for (String s : iGogglesText) {
                        drawTextInAir(target.getBlockPos().getX() + v.x, target.getBlockPos().getY() + v.y - (to - sa.length / 2.0f) / 5.5f, target.getBlockPos().getZ() + v.z, event.getPartialTicks(), s);
                        ++to;
                    }
                    GL11.glEnable(2929);
                }
                else {
                    Block b = event.getPlayer().world.getBlockState(target.getBlockPos()).getBlock();
                    if (b instanceof IGogglesDisplayExtended) {
                        GL11.glDisable(2929);
                        Vec3d v2 = ((IGogglesDisplayExtended)b).getIGogglesTextOffset();
                        String[] iGogglesText2;
                        String[] sa2 = iGogglesText2 = ((IGogglesDisplayExtended)b).getIGogglesText();
                        for (String s2 : iGogglesText2) {
                            drawTextInAir(target.getBlockPos().getX() + v2.x, target.getBlockPos().getY() + v2.y + (to - sa2.length / 2.0f) / 5.5f, target.getBlockPos().getZ() + v2.z, event.getPartialTicks(), s2);
                            ++to;
                        }
                        GL11.glEnable(2929);
                    }
                }
                boolean spaceAbove = event.getPlayer().world.isAirBlock(target.getBlockPos().up());
                if (te != null) {
                    int note = -1;
                    if (te instanceof TileEntityNote) {
                        note = ((TileEntityNote)te).note;
                    }
                    else if (te instanceof TileArcaneEar) {
                        note = ((TileArcaneEar)te).note;
                    }
                    else if (te instanceof IAspectContainer && ((IAspectContainer)te).getAspects() != null && ((IAspectContainer)te).getAspects().size() > 0) {
                        float shift = 0.0f;
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
            ItemStack stack = (event.getPlayer().getHeldItemMainhand() != null && event.getPlayer().getHeldItemMainhand().getItem() instanceof IArchitect) ? event.getPlayer().getHeldItemMainhand() : event.getPlayer().getHeldItemOffhand();
            if (((IArchitect)stack.getItem()).useBlockHighlight(stack)) {
                RayTraceResult target2 = ((IArchitect)stack.getItem()).getArchitectMOP(stack, event.getPlayer().world, event.getPlayer());
                if (target2 != null && RenderEventHandler.wandHandler.handleArchitectOverlay(stack, event.getPlayer(), event.getPartialTicks(), event.getPlayer().ticksExisted, target2)) {
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderLast(RenderWorldLastEvent event) {
        if (RenderEventHandler.tagscale > 0.0f) {
            RenderEventHandler.tagscale -= 0.005f;
        }
        float partialTicks = event.getPartialTicks();
        Minecraft mc = Minecraft.getMinecraft();
        if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)mc.getRenderViewEntity();
            if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ISealDisplayer) {
                drawSeals(partialTicks, player);
            }
            else if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof ISealDisplayer) {
                drawSeals(partialTicks, player);
            }
            if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof IArchitect) {
                RayTraceResult target = ((IArchitect)player.getHeldItemMainhand().getItem()).getArchitectMOP(player.getHeldItemMainhand(), player.world, player);
                RenderEventHandler.wandHandler.handleArchitectOverlay(player.getHeldItemMainhand(), player, partialTicks, player.ticksExisted, target);
            }
            else if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof IArchitect) {
                RayTraceResult target = ((IArchitect)player.getHeldItemOffhand().getItem()).getArchitectMOP(player.getHeldItemOffhand(), player.world, player);
                RenderEventHandler.wandHandler.handleArchitectOverlay(player.getHeldItemOffhand(), player, partialTicks, player.ticksExisted, target);
            }
            if (RenderEventHandler.thaumTarget != null) {
                AspectList ot = AspectHelper.getEntityAspects(RenderEventHandler.thaumTarget);
                if (ot != null && !ot.aspects.isEmpty()) {
                    if (RenderEventHandler.tagscale < 0.5f) {
                        RenderEventHandler.tagscale += 0.031f - RenderEventHandler.tagscale / 10.0f;
                    }
                    double iPX = RenderEventHandler.thaumTarget.prevPosX + (RenderEventHandler.thaumTarget.posX - RenderEventHandler.thaumTarget.prevPosX) * partialTicks;
                    double iPY = RenderEventHandler.thaumTarget.prevPosY + (RenderEventHandler.thaumTarget.posY - RenderEventHandler.thaumTarget.prevPosY) * partialTicks;
                    double iPZ = RenderEventHandler.thaumTarget.prevPosZ + (RenderEventHandler.thaumTarget.posZ - RenderEventHandler.thaumTarget.prevPosZ) * partialTicks;
                    drawTagsOnContainer(iPX, iPY + RenderEventHandler.thaumTarget.height, iPZ, ot, 220, null, event.getPartialTicks());
                }
            }
        }
    }
    
    private static void drawSeals(float partialTicks, EntityPlayer player) {
        ConcurrentHashMap<SealPos, SealEntity> seals = SealHandler.sealEntities.get(player.world.provider.getDimension());
        if (seals != null && seals.size() > 0) {
            GL11.glPushMatrix();
            if (player.isSneaking()) {
                GL11.glDisable(2929);
            }
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2884);
            double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
            double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
            double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
            GL11.glTranslated(-iPX, -iPY, -iPZ);
            for (ISealEntity seal : seals.values()) {
                double dis = player.getDistanceSqToCenter(seal.getSealPos().pos);
                if (dis <= 256.0) {
                    float alpha = 1.0f - (float)(dis / 256.0);
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
    
    private static void drawSealArea(EntityPlayer player, ISealEntity seal, float alpha, float partialTicks) {
        GL11.glPushMatrix();
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;
        if (seal.getColor() > 0) {
            Color c = new Color(EnumDyeColor.byMetadata(seal.getColor() - 1).getColorValue());
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
            AxisAlignedBB area = new AxisAlignedBB(seal.getSealPos().pos.getX(), seal.getSealPos().pos.getY(), seal.getSealPos().pos.getZ(), seal.getSealPos().pos.getX() + 1, seal.getSealPos().pos.getY() + 1, seal.getSealPos().pos.getZ() + 1).offset(seal.getSealPos().face.getFrontOffsetX(), seal.getSealPos().face.getFrontOffsetY(), seal.getSealPos().face.getFrontOffsetZ()).expand((seal.getSealPos().face.getFrontOffsetX() != 0) ? ((double)((seal.getArea().getX() - 1) * seal.getSealPos().face.getFrontOffsetX())) : 0.0, (seal.getSealPos().face.getFrontOffsetY() != 0) ? ((double)((seal.getArea().getY() - 1) * seal.getSealPos().face.getFrontOffsetY())) : 0.0, (seal.getSealPos().face.getFrontOffsetZ() != 0) ? ((double)((seal.getArea().getZ() - 1) * seal.getSealPos().face.getFrontOffsetZ())) : 0.0).grow((seal.getSealPos().face.getFrontOffsetX() == 0) ? ((double)(seal.getArea().getX() - 1)) : 0.0, (seal.getSealPos().face.getFrontOffsetY() == 0) ? ((double)(seal.getArea().getY() - 1)) : 0.0, (seal.getSealPos().face.getFrontOffsetZ() == 0) ? ((double)(seal.getArea().getZ() - 1)) : 0.0);
            double[][] locs = { { area.minX, area.minY, area.minZ }, { area.minX, area.maxY - 1.0, area.minZ }, { area.maxX - 1.0, area.minY, area.minZ }, { area.maxX - 1.0, area.maxY - 1.0, area.minZ }, { area.maxX - 1.0, area.minY, area.maxZ - 1.0 }, { area.maxX - 1.0, area.maxY - 1.0, area.maxZ - 1.0 }, { area.minX, area.minY, area.maxZ - 1.0 }, { area.minX, area.maxY - 1.0, area.maxZ - 1.0 } };
            int q = 0;
            for (double[] loc : locs) {
                GL11.glPushMatrix();
                GL11.glTranslated(loc[0] + 0.5, loc[1] + 0.5, loc[2] + 0.5);
                int w = 0;
                for (EnumFacing face : RenderEventHandler.rotfaces[q]) {
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
    
    static void renderSeal(int x, int y, int z, float alpha, EnumFacing face, ResourceLocation resourceLocation, boolean ia) {
        GL11.glPushMatrix();
        GL11.glColor4f(ia ? 0.5f : 1.0f, ia ? 0.5f : 1.0f, ia ? 0.5f : 1.0f, alpha);
        translateSeal((float)x, (float)y, (float)z, face.ordinal(), -0.05f);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        UtilsFX.renderItemIn2D(resourceLocation.toString(), Minecraft.getMinecraft().getRenderViewEntity().isSneaking() ? 0.0f : 0.1f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    private static void translateSeal(float x, float y, float z, int orientation, float off) {
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
    public static void fogDensityEvent(EntityViewRenderEvent.RenderFogEvent event) {
        if (RenderEventHandler.fogFiddled && RenderEventHandler.fogTarget > 0.0f) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, RenderEventHandler.fogTarget);
        }
    }
    
    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().world.isRemote && event.getEntity() instanceof EntityCreature && !event.getEntity().isDead) {
            EntityCreature mob = (EntityCreature)event.getEntity();
            if (mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD) != null) {
                Integer t = (int)mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
                if (t != null && t >= 0 && t < ChampionModifier.mods.length) {
                    ChampionModifier.mods[t].effect.showFX(mob);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void renderLivingPre(RenderLivingEvent.Pre event) {
        if (event.getEntity().world.isRemote && event.getEntity() instanceof EntityCreature && !event.getEntity().isDead) {
            EntityCreature mob = (EntityCreature)event.getEntity();
            if (mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD) != null) {
                Integer t = (int)mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
                if (t != null && t >= 0 && t < ChampionModifier.mods.length) {
                    ChampionModifier.mods[t].effect.preRender(mob, event.getRenderer());
                }
            }
        }
    }
    
    public static void drawTagsOnContainer(double x, double y, double z, AspectList tags, int bright, EnumFacing dir, float partialTicks) {
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
            EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
            double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
            double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
            double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
            int rowsize = 5;
            int current = 0;
            float shifty = 0.0f;
            int left = tags.size();
            for (Aspect tag : tags.getAspects()) {
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
                Color color = new Color(tag.getColor());
                GL11.glPushMatrix();
                GL11.glDisable(2929);
                GL11.glTranslated(-iPX + x + 0.5 + RenderEventHandler.tagscale * 2.0f * fox, -iPY + y - shifty + 0.5 + RenderEventHandler.tagscale * 2.0f * foy, -iPZ + z + 0.5 + RenderEventHandler.tagscale * 2.0f * foz);
                float xd = (float)(iPX - (x + 0.5));
                float zd = (float)(iPZ - (z + 0.5));
                float rotYaw = (float)(Math.atan2(xd, zd) * 180.0 / 3.141592653589793);
                GL11.glRotatef(rotYaw + 180.0f, 0.0f, 1.0f, 0.0f);
                GL11.glTranslated(shift, 0.0, 0.0);
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glScalef(RenderEventHandler.tagscale, RenderEventHandler.tagscale, RenderEventHandler.tagscale);
                UtilsFX.renderQuadCentered(tag.getImage(), 1.0f, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, bright, 771, 0.75f);
                if (tags.getAmount(tag) >= 0) {
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                    String am = "" + tags.getAmount(tag);
                    GL11.glScalef(0.04f, 0.04f, 0.04f);
                    GL11.glTranslated(0.0, 6.0, -0.1);
                    int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(am);
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
    
    public static void drawTextInAir(double x, double y, double z, float partialTicks, String text) {
        if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
            double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
            double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
            double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
            GL11.glPushMatrix();
            GL11.glTranslated(-iPX + x + 0.5, -iPY + y + 0.5, -iPZ + z + 0.5);
            float xd = (float)(iPX - (x + 0.5));
            float zd = (float)(iPZ - (z + 0.5));
            float rotYaw = (float)(Math.atan2(xd, zd) * 180.0 / 3.141592653589793);
            GL11.glRotatef(rotYaw + 180.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glScalef(0.0125f, 0.0125f, 0.0125f);
            int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            Minecraft.getMinecraft().fontRenderer.drawString(text, (float)(1 - sw / 2), 1.0f, 16777215, true);
            GL11.glPopMatrix();
        }
    }
    
    protected static void renderVignette(float brightness, double sw, double sh) {
        int k = (int)sw;
        int l = (int)sh;
        brightness = 1.0f - brightness;
        RenderEventHandler.prevVignetteBrightness += (float)((brightness - RenderEventHandler.prevVignetteBrightness) * 0.01);
        if (RenderEventHandler.prevVignetteBrightness > 0.0f) {
            float b = RenderEventHandler.prevVignetteBrightness * (1.0f + MathHelper.sin(Minecraft.getMinecraft().player.ticksExisted / 2.0f) * 0.1f);
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
            Tessellator tessellator = Tessellator.getInstance();
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
    public static void textureStitchEventPre(TextureStitchEvent.Pre event) {
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
        
        public ChargeEntry(long time, ItemStack item, float charge) {
            this.charge = 0.0f;
            diff = 0;
            this.time = time;
            this.item = item;
            this.charge = charge;
        }
    }
}
