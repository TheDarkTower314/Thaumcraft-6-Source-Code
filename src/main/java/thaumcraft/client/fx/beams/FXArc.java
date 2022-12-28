package thaumcraft.client.fx.beams;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.utils.Utils;


public class FXArc extends Particle
{
    public int particle;
    ArrayList<Vec3d> points;
    private Entity targetEntity;
    private double tX;
    private double tY;
    private double tZ;
    ResourceLocation beam;
    public int blendmode;
    public float length;
    
    public FXArc(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, double hg) {
        super(par1World, x, y, z, 0.0, 0.0, 0.0);
        particle = 16;
        points = new ArrayList<Vec3d>();
        targetEntity = null;
        tX = 0.0;
        tY = 0.0;
        tZ = 0.0;
        beam = new ResourceLocation("thaumcraft", "textures/misc/beamh.png");
        blendmode = 1;
        length = 1.0f;
        particleRed = red;
        particleGreen = green;
        particleBlue = blue;
        setSize(0.02f, 0.02f);
        motionX = 0.0;
        motionY = 0.0;
        motionZ = 0.0;
        tX = tx - x;
        tY = ty - y;
        tZ = tz - z;
        particleMaxAge = 3;
        double xx = 0.0;
        double yy = 0.0;
        double zz = 0.0;
        double gravity = 0.115;
        double noise = 0.25;
        Vec3d vs = new Vec3d(xx, yy, zz);
        Vec3d ve = new Vec3d(tX, tY, tZ);
        Vec3d vc = new Vec3d(xx, yy, zz);
        length = (float)ve.lengthVector();
        Vec3d vv = Utils.calculateVelocity(vs, ve, hg, gravity);
        double l = Utils.distanceSquared3d(new Vec3d(0.0, 0.0, 0.0), vv);
        points.add(vs);
        for (int c = 0; Utils.distanceSquared3d(ve, vc) > l && c < 50; ++c) {
            Vec3d vt = vc.addVector(vv.x, vv.y, vv.z);
            vc = new Vec3d(vt.x, vt.y, vt.z);
            vt = vt.addVector((rand.nextDouble() - rand.nextDouble()) * noise, (rand.nextDouble() - rand.nextDouble()) * noise, (rand.nextDouble() - rand.nextDouble()) * noise);
            points.add(vt);
            FXGeneric fb = new FXGeneric(par1World, x + vt.x, y + vt.y, z + vt.z, 0.0, 0.0, 0.0);
            int age = 30 + rand.nextInt(20);
            fb.setMaxAge(age);
            fb.setRBGColorF(MathHelper.clamp(red * 3.0f, 0.0f, 1.0f), MathHelper.clamp(green * 3.0f, 0.0f, 1.0f), MathHelper.clamp(blue * 3.0f, 0.0f, 1.0f), rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
            float[] alphas = new float[6 + rand.nextInt(age / 3)];
            for (int a = 1; a < alphas.length - 1; ++a) {
                alphas[a] = rand.nextFloat();
            }
            alphas[0] = 1.0f;
            fb.setAlphaF(alphas);
            boolean sp = rand.nextFloat() < 0.2;
            fb.setParticles(sp ? 320 : 512, 16, 1);
            fb.setLoop(true);
            fb.setGravity(sp ? 0.0f : 0.125f);
            fb.setScale(0.5f, 0.125f);
            fb.setLayer(0);
            fb.setSlowDown(0.995);
            fb.setRandomMovementScale(0.0025f, 0.001f, 0.0025f);
            ParticleEngine.addEffectWithDelay(par1World, fb, 2 + rand.nextInt(3));
            vv = vv.subtract(0.0, gravity / 1.9, 0.0);
        }
        points.add(ve);
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
    }
    
    public void setRGB(float r, float g, float b) {
        particleRed = r;
        particleGreen = g;
        particleBlue = b;
    }
    
    public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        double ePX = prevPosX + (posX - prevPosX) * f - FXArc.interpPosX;
        double ePY = prevPosY + (posY - prevPosY) * f - FXArc.interpPosY;
        double ePZ = prevPosZ + (posZ - prevPosZ) * f - FXArc.interpPosZ;
        GL11.glTranslated(ePX, ePY, ePZ);
        float size = 0.125f;
        Minecraft.getMinecraft().renderEngine.bindTexture(beam);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDisable(2884);
        int i = 220;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        float alpha = 1.0f - (particleAge + f) / particleMaxAge;
        wr.begin(5, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        float f6 = 0.0f;
        float f7 = 1.0f;
        for (int c = 0; c < points.size(); ++c) {
            Vec3d v = points.get(c);
            float f8 = c / length;
            double dx = v.x;
            double dy = v.y;
            double dz = v.z;
            wr.pos(dx, dy - size, dz).tex(f8, f7).lightmap(j, k).color(particleRed, particleGreen, particleBlue, alpha).endVertex();
            wr.pos(dx, dy + size, dz).tex(f8, f6).lightmap(j, k).color(particleRed, particleGreen, particleBlue, alpha).endVertex();
        }
        Tessellator.getInstance().draw();
        wr.begin(5, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        for (int c = 0; c < points.size(); ++c) {
            Vec3d v = points.get(c);
            float f8 = c / length;
            double dx = v.x;
            double dy = v.y;
            double dz = v.z;
            wr.pos(dx - size, dy, dz - size).tex(f8, f7).lightmap(j, k).color(particleRed, particleGreen, particleBlue, alpha).endVertex();
            wr.pos(dx + size, dy, dz + size).tex(f8, f6).lightmap(j, k).color(particleRed, particleGreen, particleBlue, alpha).endVertex();
        }
        Tessellator.getInstance().draw();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2884);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleManager.PARTICLE_TEXTURES);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
}
