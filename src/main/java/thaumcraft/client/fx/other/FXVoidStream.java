// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.other;

import java.util.Iterator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.ender.ShaderHelper;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.ARBShaderObjects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import thaumcraft.codechicken.lib.vec.Quat;
import java.util.ArrayList;
import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.lib.ender.ShaderCallback;
import net.minecraft.client.particle.Particle;

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
    private final ShaderCallback shaderCallback;
    private static final ResourceLocation starsTexture;
    CoreGLE gle;
    int layer;
    double[][] points;
    float[][] colours;
    double[] radii;
    int growing;
    ArrayList<Quat> vecs;
    
    public FXVoidStream(final World w, final double par2, final double par4, final double par6, final double tx, final double ty, final double tz, final int seed, final float scale) {
        super(w, par2, par4, par6, 0.0, 0.0, 0.0);
        this.seed = 0;
        this.length = 20;
        this.gle = new CoreGLE();
        this.layer = 1;
        this.growing = -1;
        this.vecs = new ArrayList<Quat>();
        this.shaderCallback = new ShaderCallback() {
            @Override
            public void call(final int shader) {
                final Minecraft mc = Minecraft.getMinecraft();
                final int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
                ARBShaderObjects.glUniform1fARB(x, (float)(mc.player.rotationYaw * 2.0f * 3.141592653589793 / 360.0));
                final int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
                ARBShaderObjects.glUniform1fARB(z, -(float)(mc.player.rotationPitch * 2.0f * 3.141592653589793 / 360.0));
            }
        };
        this.particleScale = (float)(scale * (1.0 + this.rand.nextGaussian() * 0.15000000596046448));
        this.length = 40;
        this.seed = seed;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        final double dx = tx - this.posX;
        final double dy = ty - this.posY;
        final double dz = tz - this.posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 21.0f);
        if (base < 1) {
            base = 1;
        }
        this.particleMaxAge = base * 2;
        this.motionX = MathHelper.sin(seed / 4.0f) * 0.025f;
        this.motionY = MathHelper.sin(seed / 3.0f) * 0.025f;
        this.motionZ = MathHelper.sin(seed / 2.0f) * 0.025f;
        this.particleGravity = 0.2f;
        this.vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        this.vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        this.startX = this.posX;
        this.startY = this.posY;
        this.startZ = this.posZ;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        final double ePX = this.startX - FXVoidStream.interpPosX;
        final double ePY = this.startY - FXVoidStream.interpPosY;
        final double ePZ = this.startZ - FXVoidStream.interpPosZ;
        GL11.glTranslated(ePX, ePY, ePZ);
        for (int q = 0; q <= 1; ++q) {
            if (q < 1) {
                GlStateManager.depthMask(false);
            }
            GL11.glBlendFunc(770, (q < 1) ? 1 : 771);
            if (this.points != null && this.points.length > 2) {
                Minecraft.getMinecraft().renderEngine.bindTexture(FXVoidStream.starsTexture);
                ShaderHelper.useShader(ShaderHelper.endShader, this.shaderCallback);
                final double[] r2 = new double[this.radii.length];
                int ri = 0;
                final float m = (1.5f - q) / 1.0f;
                for (final double d : this.radii) {
                    r2[ri] = this.radii[ri] * m;
                    ++ri;
                }
                this.gle.set_POLYCYL_TESS(3);
                this.gle.set__ROUND_TESS_PIECES(1);
                this.gle.gleSetJoinStyle(1042);
                this.gle.glePolyCone(this.points.length, this.points, this.colours, r2, 0.075f, (this.growing < 0) ? 0.0f : (0.075f * (this.particleAge - this.growing + f)));
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
    
    public void setFXLayer(final int l) {
        this.layer = l;
    }
    
    public int getFXLayer() {
        return this.layer;
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge || this.length < 1) {
            this.setExpired();
            return;
        }
        this.motionY += 0.01 * this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985;
        this.motionY *= 0.985;
        this.motionZ *= 0.985;
        this.motionX = MathHelper.clamp((float)this.motionX, -0.04f, 0.04f);
        this.motionY = MathHelper.clamp((float)this.motionY, -0.04f, 0.04f);
        this.motionZ = MathHelper.clamp((float)this.motionZ, -0.04f, 0.04f);
        double dx = this.targetX - this.posX;
        double dy = this.targetY - this.posY;
        double dz = this.targetZ - this.posZ;
        final double d13 = 0.01;
        final double d14 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= d14;
        dy /= d14;
        dz /= d14;
        this.motionX += dx * (d13 / Math.min(1.0, d14)) + this.rand.nextGaussian() * 0.014999999664723873;
        this.motionY += dy * (d13 / Math.min(1.0, d14)) + this.rand.nextGaussian() * 0.014999999664723873;
        this.motionZ += dz * (d13 / Math.min(1.0, d14)) + this.rand.nextGaussian() * 0.014999999664723873;
        float scale = this.particleScale * (0.75f + MathHelper.sin((this.seed + this.particleAge) / 2.0f) * 0.25f);
        if (d14 < 0.5) {
            final float f = MathHelper.sin((float)(d14 * 1.5707963267948966));
            scale *= f;
            this.particleScale *= f;
        }
        if (this.particleScale > 0.001) {
            this.vecs.add(new Quat(scale, this.posX - this.startX, this.posY - this.startY, this.posZ - this.startZ));
        }
        else {
            if (this.growing < 0) {
                this.growing = this.particleAge;
            }
            --this.length;
        }
        if (this.vecs.size() > this.length) {
            this.vecs.remove(0);
        }
        this.points = new double[this.vecs.size()][3];
        this.colours = new float[this.vecs.size()][4];
        this.radii = new double[this.vecs.size()];
        int c = this.vecs.size();
        for (final Quat v : this.vecs) {
            --c;
            final float variance = 1.0f + MathHelper.sin((c + this.particleAge) / 3.0f) * 0.2f;
            final float xx = MathHelper.sin((c + this.particleAge) / 6.0f) * 0.01f;
            final float yy = MathHelper.sin((c + this.particleAge) / 7.0f) * 0.01f;
            final float zz = MathHelper.sin((c + this.particleAge) / 8.0f) * 0.01f;
            this.points[c][0] = v.x + xx;
            this.points[c][1] = v.y + yy;
            this.points[c][2] = v.z + zz;
            this.radii[c] = v.s * variance;
            if (c > this.vecs.size() - 10) {
                final double[] radii = this.radii;
                final int n = c;
                radii[n] *= MathHelper.cos((float)((c - (this.vecs.size() - 12)) / 10.0f * 1.5707963267948966));
            }
            if (c == 0) {
                this.radii[c] = 0.0;
            }
            else if (c == 1) {
                this.radii[c] = 0.0;
            }
            else if (c == 2) {
                this.radii[c] = (this.particleScale * 0.5 + this.radii[c]) / 2.0;
            }
            else if (c == 3) {
                this.radii[c] = (this.particleScale + this.radii[c]) / 2.0;
            }
            else if (c == 4) {
                this.radii[c] = (this.particleScale + this.radii[c] * 2.0) / 3.0;
            }
            this.colours[c][0] = 1.0f;
            this.colours[c][1] = 1.0f;
            this.colours[c][2] = 1.0f;
            this.colours[c][3] = 1.0f;
        }
        if (this.vecs.size() > 2 && this.rand.nextBoolean()) {
            int q = this.rand.nextInt(3);
            if (this.rand.nextBoolean()) {
                q = this.vecs.size() - 2;
            }
        }
    }
    
    public void setGravity(final float value) {
        this.particleGravity = value;
    }
    
    static {
        starsTexture = new ResourceLocation("textures/entity/end_portal.png");
    }
}
