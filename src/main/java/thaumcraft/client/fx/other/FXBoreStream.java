// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.other;

import java.util.Iterator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import thaumcraft.client.fx.ParticleEngine;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import java.awt.Color;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.codechicken.lib.vec.Quat;
import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;
import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.client.particle.Particle;

public class FXBoreStream extends Particle
{
    private Entity target;
    private double startX;
    private double startY;
    private double startZ;
    private int count;
    public int length;
    private String key;
    private BlockPos startPos;
    CoreGLE gle;
    private static final ResourceLocation TEX0;
    int layer;
    double[][] points;
    float[][] colours;
    double[] radii;
    int growing;
    ArrayList<Quat> vecs;
    
    public FXBoreStream(final World w, final double par2, final double par4, final double par6, final Entity target, final int count, final int color, final float scale, final int extend, final double my) {
        super(w, par2, par4, par6, 0.0, 0.0, 0.0);
        this.count = 0;
        this.length = 5;
        this.key = "";
        this.startPos = null;
        this.gle = new CoreGLE();
        this.layer = 1;
        this.growing = -1;
        this.vecs = new ArrayList<Quat>();
        this.particleScale = (float)(scale * (1.0 + this.rand.nextGaussian() * 0.15000000596046448));
        this.length = Math.max(5, extend);
        this.count = count;
        this.target = target;
        this.particleMaxAge = this.length * 10;
        this.motionX = MathHelper.sin(count / 4.0f) * 0.15f;
        this.motionY = my + MathHelper.sin(count / 3.0f) * 0.15f;
        this.motionZ = MathHelper.sin(count / 2.0f) * 0.15f;
        final Color c = new Color(color);
        this.particleRed = c.getRed() / 255.0f;
        this.particleGreen = c.getGreen() / 255.0f;
        this.particleBlue = c.getBlue() / 255.0f;
        this.particleGravity = 0.2f;
        this.vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        this.vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        this.startX = this.posX;
        this.startY = this.posY;
        this.startZ = this.posZ;
        this.startPos = new BlockPos(this.startX, this.startY, this.startZ);
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        final double ePX = this.startX - FXBoreStream.interpPosX;
        final double ePY = this.startY - FXBoreStream.interpPosY;
        final double ePZ = this.startZ - FXBoreStream.interpPosZ;
        GL11.glTranslated(ePX, ePY, ePZ);
        if (this.points != null && this.points.length > 2) {
            Minecraft.getMinecraft().renderEngine.bindTexture(FXBoreStream.TEX0);
            this.gle.set_POLYCYL_TESS(8);
            this.gle.set__ROUND_TESS_PIECES(1);
            this.gle.gleSetJoinStyle(1042);
            this.gle.glePolyCone(this.points.length, this.points, this.colours, this.radii, 0.075f, (this.growing < 0) ? 0.0f : (0.075f * (this.particleAge - this.growing + f)));
        }
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
        double dx = this.target.posX - this.posX;
        double dy = this.target.posY + this.target.getEyeHeight() - this.posY;
        double dz = this.target.posZ - this.posZ;
        final double d11 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        final double clamp = d11 / 10.0;
        this.motionX = MathHelper.clamp((float)this.motionX, -clamp, clamp);
        this.motionY = MathHelper.clamp((float)this.motionY, -clamp, clamp);
        this.motionZ = MathHelper.clamp((float)this.motionZ, -clamp, clamp);
        dx /= d11;
        dy /= d11;
        dz /= d11;
        this.motionX += dx * (clamp / Math.min(1.0, d11));
        this.motionY += dy * (clamp / Math.min(1.0, d11));
        this.motionZ += dz * (clamp / Math.min(1.0, d11));
        float scale = this.particleScale * (0.75f + MathHelper.sin((this.count + this.particleAge) / 2.0f) * 0.25f);
        if (d11 < 1.0) {
            final float f = MathHelper.sin((float)(d11 * 1.5707963267948966));
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
            final float xx = MathHelper.sin((c + this.particleAge) / 6.0f) * 0.03f;
            final float yy = MathHelper.sin((c + this.particleAge) / 7.0f) * 0.03f;
            final float zz = MathHelper.sin((c + this.particleAge) / 8.0f) * 0.03f;
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
            final float v2 = 1.0f - MathHelper.sin((c + this.particleAge) / 2.0f) * 0.1f;
            this.colours[c][0] = this.particleRed * v2;
            this.colours[c][1] = this.particleGreen * v2;
            this.colours[c][2] = this.particleBlue * v2;
            this.colours[c][3] = 1.0f;
        }
    }
    
    public void setGravity(final float value) {
        this.particleGravity = value;
    }
    
    static {
        TEX0 = new ResourceLocation("thaumcraft", "textures/misc/essentia.png");
    }
}
