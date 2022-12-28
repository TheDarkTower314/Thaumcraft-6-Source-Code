package thaumcraft.client.fx.other;
import com.sasmaster.glelwjgl.java.CoreGLE;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.client.lib.ender.ShaderHelper;
import thaumcraft.codechicken.lib.vec.Quat;


public class FXVoidStream extends Particle
{
    private double targetX;
    private double targetY;
    private double targetZ;
    private double startX;
    private double startY;
    private double startZ;
    private int seed;
    public int length;
    private ShaderCallback shaderCallback;
    private static ResourceLocation starsTexture;
    CoreGLE gle;
    int layer;
    double[][] points;
    float[][] colours;
    double[] radii;
    int growing;
    ArrayList<Quat> vecs;
    
    public FXVoidStream(World w, double par2, double par4, double par6, double tx, double ty, double tz, int seed, float scale) {
        super(w, par2, par4, par6, 0.0, 0.0, 0.0);
        this.seed = 0;
        length = 20;
        gle = new CoreGLE();
        layer = 1;
        growing = -1;
        vecs = new ArrayList<Quat>();
        shaderCallback = new ShaderCallback() {
            @Override
            public void call(int shader) {
                Minecraft mc = Minecraft.getMinecraft();
                int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
                ARBShaderObjects.glUniform1fARB(x, (float)(mc.player.rotationYaw * 2.0f * 3.141592653589793 / 360.0));
                int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
                ARBShaderObjects.glUniform1fARB(z, -(float)(mc.player.rotationPitch * 2.0f * 3.141592653589793 / 360.0));
            }
        };
        particleScale = (float)(scale * (1.0 + rand.nextGaussian() * 0.15000000596046448));
        length = 40;
        this.seed = seed;
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        double dx = tx - posX;
        double dy = ty - posY;
        double dz = tz - posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 21.0f);
        if (base < 1) {
            base = 1;
        }
        particleMaxAge = base * 2;
        motionX = MathHelper.sin(seed / 4.0f) * 0.025f;
        motionY = MathHelper.sin(seed / 3.0f) * 0.025f;
        motionZ = MathHelper.sin(seed / 2.0f) * 0.025f;
        particleGravity = 0.2f;
        vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        startX = posX;
        startY = posY;
        startZ = posZ;
    }
    
    public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        double ePX = startX - FXVoidStream.interpPosX;
        double ePY = startY - FXVoidStream.interpPosY;
        double ePZ = startZ - FXVoidStream.interpPosZ;
        GL11.glTranslated(ePX, ePY, ePZ);
        for (int q = 0; q <= 1; ++q) {
            if (q < 1) {
                GlStateManager.depthMask(false);
            }
            GL11.glBlendFunc(770, (q < 1) ? 1 : 771);
            if (points != null && points.length > 2) {
                Minecraft.getMinecraft().renderEngine.bindTexture(FXVoidStream.starsTexture);
                ShaderHelper.useShader(ShaderHelper.endShader, shaderCallback);
                double[] r2 = new double[radii.length];
                int ri = 0;
                float m = (1.5f - q) / 1.0f;
                for (double d : radii) {
                    r2[ri] = radii[ri] * m;
                    ++ri;
                }
                gle.set_POLYCYL_TESS(3);
                gle.set__ROUND_TESS_PIECES(1);
                gle.gleSetJoinStyle(1042);
                gle.glePolyCone(points.length, points, colours, r2, 0.075f, (growing < 0) ? 0.0f : (0.075f * (particleAge - growing + f)));
                ShaderHelper.releaseShader();
            }
            if (q < 1) {
                GlStateManager.depthMask(true);
            }
        }
        GlStateManager.depthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleEngine.particleTexture);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
    
    public void setFXLayer(int l) {
        layer = l;
    }
    
    public int getFXLayer() {
        return layer;
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge || length < 1) {
            setExpired();
            return;
        }
        motionY += 0.01 * particleGravity;
        move(motionX, motionY, motionZ);
        motionX *= 0.985;
        motionY *= 0.985;
        motionZ *= 0.985;
        motionX = MathHelper.clamp((float) motionX, -0.04f, 0.04f);
        motionY = MathHelper.clamp((float) motionY, -0.04f, 0.04f);
        motionZ = MathHelper.clamp((float) motionZ, -0.04f, 0.04f);
        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        double d13 = 0.01;
        double d14 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= d14;
        dy /= d14;
        dz /= d14;
        motionX += dx * (d13 / Math.min(1.0, d14)) + rand.nextGaussian() * 0.014999999664723873;
        motionY += dy * (d13 / Math.min(1.0, d14)) + rand.nextGaussian() * 0.014999999664723873;
        motionZ += dz * (d13 / Math.min(1.0, d14)) + rand.nextGaussian() * 0.014999999664723873;
        float scale = particleScale * (0.75f + MathHelper.sin((seed + particleAge) / 2.0f) * 0.25f);
        if (d14 < 0.5) {
            float f = MathHelper.sin((float)(d14 * 1.5707963267948966));
            scale *= f;
            particleScale *= f;
        }
        if (particleScale > 0.001) {
            vecs.add(new Quat(scale, posX - startX, posY - startY, posZ - startZ));
        }
        else {
            if (growing < 0) {
                growing = particleAge;
            }
            --length;
        }
        if (vecs.size() > length) {
            vecs.remove(0);
        }
        points = new double[vecs.size()][3];
        colours = new float[vecs.size()][4];
        radii = new double[vecs.size()];
        int c = vecs.size();
        for (Quat v : vecs) {
            --c;
            float variance = 1.0f + MathHelper.sin((c + particleAge) / 3.0f) * 0.2f;
            float xx = MathHelper.sin((c + particleAge) / 6.0f) * 0.01f;
            float yy = MathHelper.sin((c + particleAge) / 7.0f) * 0.01f;
            float zz = MathHelper.sin((c + particleAge) / 8.0f) * 0.01f;
            points[c][0] = v.x + xx;
            points[c][1] = v.y + yy;
            points[c][2] = v.z + zz;
            radii[c] = v.s * variance;
            if (c > vecs.size() - 10) {
                double[] radii = this.radii;
                int n = c;
                radii[n] *= MathHelper.cos((float)((c - (vecs.size() - 12)) / 10.0f * 1.5707963267948966));
            }
            if (c == 0) {
                radii[c] = 0.0;
            }
            else if (c == 1) {
                radii[c] = 0.0;
            }
            else if (c == 2) {
                radii[c] = (particleScale * 0.5 + radii[c]) / 2.0;
            }
            else if (c == 3) {
                radii[c] = (particleScale + radii[c]) / 2.0;
            }
            else if (c == 4) {
                radii[c] = (particleScale + radii[c] * 2.0) / 3.0;
            }
            colours[c][0] = 1.0f;
            colours[c][1] = 1.0f;
            colours[c][2] = 1.0f;
            colours[c][3] = 1.0f;
        }
        if (vecs.size() > 2 && rand.nextBoolean()) {
            int q = rand.nextInt(3);
            if (rand.nextBoolean()) {
                q = vecs.size() - 2;
            }
        }
    }
    
    public void setGravity(float value) {
        particleGravity = value;
    }
    
    static {
        starsTexture = new ResourceLocation("textures/entity/end_portal.png");
    }
}
