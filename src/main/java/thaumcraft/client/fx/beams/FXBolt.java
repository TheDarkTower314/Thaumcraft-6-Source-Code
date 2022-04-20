// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.beams;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import net.minecraft.client.particle.Particle;

public class FXBolt extends Particle
{
    float width;
    ArrayList<Vec3d> points;
    ArrayList<Float> pointsWidth;
    float dr;
    long seed;
    private Entity targetEntity;
    private double tX;
    private double tY;
    private double tZ;
    ResourceLocation beam;
    public float length;
    CoreGLE gle;
    
    public FXBolt(final World par1World, final double x, final double y, final double z, final double tx, final double ty, final double tz, final float red, final float green, final float blue, final float width) {
        super(par1World, x, y, z, 0.0, 0.0, 0.0);
        this.width = 0.0f;
        this.points = new ArrayList<Vec3d>();
        this.pointsWidth = new ArrayList<Float>();
        this.dr = 0.0f;
        this.seed = 0L;
        this.targetEntity = null;
        this.tX = 0.0;
        this.tY = 0.0;
        this.tZ = 0.0;
        this.beam = new ResourceLocation("thaumcraft", "textures/misc/essentia.png");
        this.length = 1.0f;
        this.gle = new CoreGLE();
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        this.setSize(0.02f, 0.02f);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.tX = tx - x;
        this.tY = ty - y;
        this.tZ = tz - z;
        this.width = width;
        this.particleMaxAge = 3;
        final Vec3d vs = new Vec3d(0.0, 0.0, 0.0);
        final Vec3d ve = new Vec3d(this.tX, this.tY, this.tZ);
        this.length = (float)(ve.lengthVector() * 3.141592653589793);
        final int steps = (int)this.length;
        this.points.add(vs);
        this.pointsWidth.add(width);
        this.dr = (float)(this.rand.nextInt(50) * 3.141592653589793);
        final float ampl = 0.1f;
        for (int a = 1; a < steps - 1; ++a) {
            final float dist = a * (this.length / steps) + this.dr;
            double dx = this.tX / steps * a + MathHelper.sin(dist / 4.0f) * ampl;
            double dy = this.tY / steps * a + MathHelper.sin(dist / 3.0f) * ampl;
            double dz = this.tZ / steps * a + MathHelper.sin(dist / 2.0f) * ampl;
            dx += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1f;
            dy += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1f;
            dz += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1f;
            final Vec3d vp = new Vec3d(dx, dy, dz);
            this.points.add(vp);
            this.pointsWidth.add(width);
        }
        this.pointsWidth.add(width);
        this.points.add(ve);
        this.seed = this.rand.nextInt(1000);
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
    }
    
    private void calcSteps(final float f) {
        final Random rr = new Random(this.seed);
        this.points.clear();
        this.pointsWidth.clear();
        final Vec3d vs = new Vec3d(0.0, 0.0, 0.0);
        final Vec3d ve = new Vec3d(this.tX, this.tY, this.tZ);
        final int steps = (int)this.length;
        this.points.add(vs);
        this.pointsWidth.add(this.width);
        final float ampl = (this.particleAge + f) / 10.0f;
        for (int a = 1; a < steps - 1; ++a) {
            final float dist = a * (this.length / steps) + this.dr;
            double dx = this.tX / steps * a + MathHelper.sin(dist / 4.0f) * ampl;
            double dy = this.tY / steps * a + MathHelper.sin(dist / 3.0f) * ampl;
            double dz = this.tZ / steps * a + MathHelper.sin(dist / 2.0f) * ampl;
            dx += (rr.nextFloat() - rr.nextFloat()) * 0.1f;
            dy += (rr.nextFloat() - rr.nextFloat()) * 0.1f;
            dz += (rr.nextFloat() - rr.nextFloat()) * 0.1f;
            final Vec3d vp = new Vec3d(dx, dy, dz);
            this.points.add(vp);
            this.pointsWidth.add(((rr.nextInt(4) == 0) ? (1.0f - this.particleAge * 0.25f) : 1.0f) * this.width);
        }
        this.pointsWidth.add(this.width);
        this.points.add(ve);
    }
    
    public void setRGB(final float r, final float g, final float b) {
        this.particleRed = r;
        this.particleGreen = g;
        this.particleBlue = b;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float cosyaw, final float cospitch, final float sinyaw, final float cossinpitch, final float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        final double ePX = this.prevPosX + (this.posX - this.prevPosX) * f - FXBolt.interpPosX;
        final double ePY = this.prevPosY + (this.posY - this.prevPosY) * f - FXBolt.interpPosY;
        final double ePZ = this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXBolt.interpPosZ;
        GL11.glTranslated(ePX, ePY, ePZ);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.beam);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDisable(2884);
        this.calcSteps(f);
        final float alpha = MathHelper.clamp(1.0f - this.particleAge / (float)this.particleMaxAge, 0.1f, 1.0f);
        if (this.points != null && this.points.size() > 2) {
            final double[][] pp = new double[this.points.size()][3];
            final float[][] colours = new float[this.points.size()][4];
            final double[] radii = new double[this.points.size()];
            for (int a = 0; a < this.points.size(); ++a) {
                pp[a][0] = this.points.get(a).x;
                pp[a][1] = this.points.get(a).y;
                pp[a][2] = this.points.get(a).z;
                colours[a][0] = this.particleRed;
                colours[a][1] = this.particleGreen;
                colours[a][2] = this.particleBlue;
                colours[a][3] = alpha;
                radii[a] = this.pointsWidth.get(a) / 10.0f;
            }
            this.gle.set_POLYCYL_TESS(5);
            this.gle.gleSetJoinStyle(1042);
            this.gle.glePolyCone(pp.length, pp, colours, radii, 1.0f, 0.0f);
            for (int a = 0; a < this.points.size(); ++a) {
                radii[a] /= 3.0;
            }
            this.gle.glePolyCone(pp.length, pp, colours, radii, 1.0f, 0.0f);
        }
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
