package thaumcraft.client.fx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;


@Mod.EventBusSubscriber({ Side.CLIENT })
public class ParticleEngine
{
    public static ResourceLocation particleTexture;
    protected World world;
    private static HashMap<Integer, ArrayList<Particle>>[] particles;
    private static ArrayList<ParticleDelay> particlesDelayed;
    private Random rand;
    
    public ParticleEngine() {
        rand = new Random();
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (Minecraft.getMinecraft().world == null) {
            return;
        }
        if (event.phase == TickEvent.Phase.END) {
            float frame = event.renderTickTime;
            Entity entity = Minecraft.getMinecraft().player;
            TextureManager renderer = Minecraft.getMinecraft().renderEngine;
            int dim = Minecraft.getMinecraft().world.provider.getDimension();
            GL11.glPushMatrix();
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, 0.0f, -2000.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableBlend();
            GL11.glEnable(3042);
            GL11.glAlphaFunc(516, 0.003921569f);
            renderer.bindTexture(ParticleEngine.particleTexture);
            GlStateManager.depthMask(false);
            for (int layer = 5; layer >= 4; --layer) {
                if (ParticleEngine.particles[layer].containsKey(dim)) {
                    ArrayList<Particle> parts = ParticleEngine.particles[layer].get(dim);
                    if (parts.size() != 0) {
                        switch (layer) {
                            case 4: {
                                GlStateManager.blendFunc(770, 1);
                                break;
                            }
                            case 5: {
                                GlStateManager.blendFunc(770, 771);
                                break;
                            }
                        }
                        Tessellator tessellator = Tessellator.getInstance();
                        BufferBuilder buffer = tessellator.getBuffer();
                        for (int j = 0; j < parts.size(); ++j) {
                            Particle Particle = parts.get(j);
                            if (Particle != null) {
                                try {
                                    Particle.renderParticle(buffer, entity, frame, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
                                }
                                catch (Throwable throwable) {
                                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
                                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
                                    crashreportcategory.addDetail("Particle", new ICrashReportDetail<String>() {
                                        public String call() {
                                            return Particle.toString();
                                        }
                                    });
                                    crashreportcategory.addDetail("Particle Type", new ICrashReportDetail<String>() {
                                        public String call() {
                                            return "ENTITY_PARTICLE_TEXTURE";
                                        }
                                    });
                                    throw new ReportedException(crashreport);
                                }
                            }
                        }
                    }
                }
            }
            GlStateManager.depthMask(true);
            GlStateManager.blendFunc(770, 771);
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1f);
            GL11.glPopMatrix();
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        float frame = event.getPartialTicks();
        Entity entity = Minecraft.getMinecraft().player;
        TextureManager renderer = Minecraft.getMinecraft().renderEngine;
        int dim = Minecraft.getMinecraft().world.provider.getDimension();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        GL11.glEnable(3042);
        GL11.glAlphaFunc(516, 0.003921569f);
        renderer.bindTexture(ParticleEngine.particleTexture);
        GlStateManager.depthMask(false);
        for (int layer = 3; layer >= 0; --layer) {
            if (ParticleEngine.particles[layer].containsKey(dim)) {
                ArrayList<Particle> parts = ParticleEngine.particles[layer].get(dim);
                if (parts.size() != 0) {
                    switch (layer) {
                        case 0: {
                            GlStateManager.blendFunc(770, 1);
                            break;
                        }
                        case 1: {
                            GlStateManager.blendFunc(770, 771);
                            break;
                        }
                        case 2: {
                            GlStateManager.blendFunc(770, 1);
                            GlStateManager.disableDepth();
                            break;
                        }
                        case 3: {
                            GlStateManager.blendFunc(770, 771);
                            GlStateManager.disableDepth();
                            break;
                        }
                    }
                    float f1 = ActiveRenderInfo.getRotationX();
                    float f2 = ActiveRenderInfo.getRotationZ();
                    float f3 = ActiveRenderInfo.getRotationYZ();
                    float f4 = ActiveRenderInfo.getRotationXY();
                    float f5 = ActiveRenderInfo.getRotationXZ();
                    Particle.interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * frame;
                    Particle.interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * frame;
                    Particle.interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * frame;
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder buffer = tessellator.getBuffer();
                    buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                    for (int j = 0; j < parts.size(); ++j) {
                        Particle Particle = parts.get(j);
                        if (Particle != null) {
                            try {
                                Particle.renderParticle(buffer, entity, frame, f1, f5, f2, f3, f4);
                            }
                            catch (Throwable throwable) {
                                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
                                CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
                                crashreportcategory.addDetail("Particle", new ICrashReportDetail<String>() {
                                    public String call() {
                                        return Particle.toString();
                                    }
                                });
                                crashreportcategory.addDetail("Particle Type", new ICrashReportDetail<String>() {
                                    public String call() {
                                        return "ENTITY_PARTICLE_TEXTURE";
                                    }
                                });
                                throw new ReportedException(crashreport);
                            }
                        }
                    }
                    tessellator.draw();
                    switch (layer) {
                        case 2:
                        case 3: {
                            GlStateManager.enableDepth();
                            break;
                        }
                    }
                }
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
        GL11.glPopMatrix();
    }
    
    public static void addEffect(World world, Particle fx) {
        addEffect(world.provider.getDimension(), fx);
    }
    
    private static int getParticleLimit() {
        return (FMLClientHandler.instance().getClient().gameSettings.particleSetting == 2) ? 500 : ((FMLClientHandler.instance().getClient().gameSettings.particleSetting == 1) ? 1000 : 2000);
    }
    
    public static void addEffect(int dim, Particle fx) {
        if (Minecraft.getMinecraft().world == null) {
            return;
        }
        int ps = FMLClientHandler.instance().getClient().gameSettings.particleSetting;
        Minecraft.getMinecraft();
        if (Minecraft.getDebugFPS() < 30) {
            ++ps;
        }
        if (Minecraft.getMinecraft().world.rand.nextInt(3) < ps) {
            return;
        }
        if (!ParticleEngine.particles[fx.getFXLayer()].containsKey(dim)) {
            ParticleEngine.particles[fx.getFXLayer()].put(dim, new ArrayList<Particle>());
        }
        ArrayList<Particle> parts = ParticleEngine.particles[fx.getFXLayer()].get(dim);
        if (parts.size() >= getParticleLimit()) {
            parts.remove(0);
        }
        parts.add(fx);
        ParticleEngine.particles[fx.getFXLayer()].put(dim, parts);
    }
    
    public static void addEffectWithDelay(World world, Particle fx, int delay) {
        ParticleEngine.particlesDelayed.add(new ParticleDelay(fx, world.provider.getDimension(), delay));
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void updateParticles(TickEvent.ClientTickEvent event) {
        if (event.side == Side.SERVER) {
            return;
        }
        Minecraft mc = FMLClientHandler.instance().getClient();
        World world = mc.world;
        if (mc.world == null) {
            return;
        }
        int dim = world.provider.getDimension();
        if (event.phase == TickEvent.Phase.START) {
            try {
                Iterator<ParticleDelay> i = ParticleEngine.particlesDelayed.iterator();
                while (i.hasNext()) {
                    ParticleDelay pd = i.next();
                    --pd.delay;
                    if (pd.delay <= 0) {
                        if (pd.dim == dim) {
                            addEffect(pd.dim, pd.particle);
                        }
                        i.remove();
                    }
                }
            }
            catch (Exception ex) {}
            for (int layer = 0; layer < 6; ++layer) {
                if (ParticleEngine.particles[layer].containsKey(dim)) {
                    ArrayList<Particle> parts = ParticleEngine.particles[layer].get(dim);
                    for (int j = 0; j < parts.size(); ++j) {
                        Particle Particle = parts.get(j);
                        try {
                            if (Particle != null) {
                                Particle.onUpdate();
                            }
                        }
                        catch (Exception e) {
                            try {
                                CrashReport crashreport = CrashReport.makeCrashReport(e, "Ticking Particle");
                                CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
                                crashreportcategory.addCrashSection("Particle", new Callable() {
                                    @Override
                                    public String call() {
                                        return Particle.toString();
                                    }
                                });
                                crashreportcategory.addCrashSection("Particle Type", new Callable() {
                                    @Override
                                    public String call() {
                                        return "ENTITY_PARTICLE_TEXTURE";
                                    }
                                });
                                Particle.setExpired();
                            }
                            catch (Exception ex2) {}
                        }
                        if (Particle == null || !Particle.isAlive()) {
                            parts.remove(j--);
                            ParticleEngine.particles[layer].put(dim, parts);
                        }
                    }
                }
            }
        }
    }
    
    static {
        particleTexture = new ResourceLocation("thaumcraft", "textures/misc/particles.png");
        ParticleEngine.particles = new HashMap[] { new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap() };
        ParticleEngine.particlesDelayed = new ArrayList<ParticleDelay>();
    }
    
    private static class ParticleDelay
    {
        Particle particle;
        int dim;
        int level;
        int delay;
        
        public ParticleDelay(Particle particle, int dim, int delay) {
            this.dim = dim;
            this.particle = particle;
            this.delay = delay;
        }
    }
}
