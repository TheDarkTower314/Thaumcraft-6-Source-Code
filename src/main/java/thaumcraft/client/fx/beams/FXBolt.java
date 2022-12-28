package thaumcraft.client.fx.beams;
import com.sasmaster.glelwjgl.java.CoreGLE;
import java.util.ArrayList;
import java.util.Random;
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
    
    public FXBolt(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, float width) {
        super(par1World, x, y, z, 0.0, 0.0, 0.0);
        this.width = 0.0f;
        points = new ArrayList<Vec3d>();
        pointsWidth = new ArrayList<Float>();
        dr = 0.0f;
        seed = 0L;
        targetEntity = null;
        tX = 0.0;
        tY = 0.0;
        tZ = 0.0;
        beam = new ResourceLocation("thaumcraft", "textures/misc/essentia.png");
        length = 1.0f;
        gle = new CoreGLE();
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
        this.width = width;
        particleMaxAge = 3;
        Vec3d vs = new Vec3d(0.0, 0.0, 0.0);
        Vec3d ve = new Vec3d(tX, tY, tZ);
        length = (float)(ve.lengthVector() * 3.141592653589793);
        int steps = (int) length;
        points.add(vs);
        pointsWidth.add(width);
        dr = (float)(rand.nextInt(50) * 3.141592653589793);
        float ampl = 0.1f;
        for (int a = 1; a < steps - 1; ++a) {
            float dist = a * (length / steps) + dr;
            double dx = tX / steps * a + MathHelper.sin(dist / 4.0f) * ampl;
            double dy = tY / steps * a + MathHelper.sin(dist / 3.0f) * ampl;
            double dz = tZ / steps * a + MathHelper.sin(dist / 2.0f) * ampl;
            dx += (rand.nextFloat() - rand.nextFloat()) * 0.1f;
            dy += (rand.nextFloat() - rand.nextFloat()) * 0.1f;
            dz += (rand.nextFloat() - rand.nextFloat()) * 0.1f;
            Vec3d vp = new Vec3d(dx, dy, dz);
            points.add(vp);
            pointsWidth.add(width);
        }
        pointsWidth.add(width);
        points.add(ve);
        seed = rand.nextInt(1000);
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
    }
    
    private void calcSteps(float f) {
        Random rr = new Random(seed);
        points.clear();
        pointsWidth.clear();
        Vec3d vs = new Vec3d(0.0, 0.0, 0.0);
        Vec3d ve = new Vec3d(tX, tY, tZ);
        int steps = (int) length;
        points.add(vs);
        pointsWidth.add(width);
        float ampl = (particleAge + f) / 10.0f;
        for (int a = 1; a < steps - 1; ++a) {
            float dist = a * (length / steps) + dr;
            double dx = tX / steps * a + MathHelper.sin(dist / 4.0f) * ampl;
            double dy = tY / steps * a + MathHelper.sin(dist / 3.0f) * ampl;
            double dz = tZ / steps * a + MathHelper.sin(dist / 2.0f) * ampl;
            dx += (rr.nextFloat() - rr.nextFloat()) * 0.1f;
            dy += (rr.nextFloat() - rr.nextFloat()) * 0.1f;
            dz += (rr.nextFloat() - rr.nextFloat()) * 0.1f;
            Vec3d vp = new Vec3d(dx, dy, dz);
            points.add(vp);
            pointsWidth.add(((rr.nextInt(4) == 0) ? (1.0f - particleAge * 0.25f) : 1.0f) * width);
        }
        pointsWidth.add(width);
        points.add(ve);
    }
    
    public void setRGB(float r, float g, float b) {
        particleRed = r;
        particleGreen = g;
        particleBlue = b;
    }
    
    public void renderParticle(BufferBuilder wr, Entity entity, float f, float cosyaw, float cospitch, float sinyaw, float cossinpitch, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        double ePX = prevPosX + (posX - prevPosX) * f - FXBolt.interpPosX;
        double ePY = prevPosY + (posY - prevPosY) * f - FXBolt.interpPosY;
        double ePZ = prevPosZ + (posZ - prevPosZ) * f - FXBolt.interpPosZ;
        GL11.glTranslated(ePX, ePY, ePZ);
        Minecraft.getMinecraft().renderEngine.bindTexture(beam);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDisable(2884);
        calcSteps(f);
        float alpha = MathHelper.clamp(1.0f - particleAge / (float) particleMaxAge, 0.1f, 1.0f);
        if (points != null && points.size() > 2) {
            double[][] pp = new double[points.size()][3];
            float[][] colours = new float[points.size()][4];
            double[] radii = new double[points.size()];
            for (int a = 0; a < points.size(); ++a) {
                pp[a][0] = points.get(a).x;
                pp[a][1] = points.get(a).y;
                pp[a][2] = points.get(a).z;
                colours[a][0] = particleRed;
                colours[a][1] = particleGreen;
                colours[a][2] = particleBlue;
                colours[a][3] = alpha;
                radii[a] = pointsWidth.get(a) / 10.0f;
            }
            gle.set_POLYCYL_TESS(5);
            gle.gleSetJoinStyle(1042);
            gle.glePolyCone(pp.length, pp, colours, radii, 1.0f, 0.0f);
            for (int a = 0; a < points.size(); ++a) {
                radii[a] /= 3.0;
            }
            gle.glePolyCone(pp.length, pp, colours, radii, 1.0f, 0.0f);
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
