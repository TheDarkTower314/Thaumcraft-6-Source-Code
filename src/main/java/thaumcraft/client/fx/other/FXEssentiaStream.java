// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.other;

import java.util.Iterator;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import thaumcraft.client.fx.ParticleEngine;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.state.IBlockState;
import java.awt.Color;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.blocks.essentia.BlockEssentiaTransport;
import net.minecraft.world.World;
import thaumcraft.codechicken.lib.vec.Quat;
import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;
import com.sasmaster.glelwjgl.java.CoreGLE;
import java.util.HashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.particle.Particle;

public class FXEssentiaStream extends Particle
{
    private double targetX;
    private double targetY;
    private double targetZ;
    private double startX;
    private double startY;
    private double startZ;
    private int count;
    public int length;
    private String key;
    private BlockPos startPos;
    private BlockPos endPos;
    static HashMap<String, FXEssentiaStream> pt;
    CoreGLE gle;
    private static final ResourceLocation TEX0;
    int layer;
    double[][] points;
    float[][] colours;
    double[] radii;
    int growing;
    ArrayList<Quat> vecs;
    
    public FXEssentiaStream(final World w, final double par2, final double par4, final double par6, final double tx, final double ty, final double tz, final int count, final int color, final float scale, final int extend, final double my) {
        super(w, par2, par4, par6, 0.0, 0.0, 0.0);
        this.count = 0;
        this.length = 20;
        this.key = "";
        this.startPos = null;
        this.endPos = null;
        this.gle = new CoreGLE();
        this.layer = 1;
        this.growing = -1;
        this.vecs = new ArrayList<Quat>();
        this.particleScale = (float)(scale * (1.0 + this.rand.nextGaussian() * 0.15000000596046448));
        this.length = Math.max(20, extend);
        this.count = count;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        final BlockPos bp1 = new BlockPos(this.posX, this.posY, this.posZ);
        final BlockPos bp2 = new BlockPos(this.targetX, this.targetY, this.targetZ);
        final IBlockState bs = w.getBlockState(bp1);
        if (bs.getBlock() instanceof BlockEssentiaTransport) {
            final EnumFacing f = BlockStateUtils.getFacing(bs);
            this.posX += f.getFrontOffsetX() * 0.05f;
            this.posY += f.getFrontOffsetY() * 0.05f;
            this.posZ += f.getFrontOffsetZ() * 0.05f;
        }
        final double dx = tx - this.posX;
        final double dy = ty - this.posY;
        final double dz = tz - this.posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 21.0f);
        if (base < 1) {
            base = 1;
        }
        this.particleMaxAge = base;
        final String k = bp1.toLong() + "" + bp2.toLong() + "" + color;
        if (FXEssentiaStream.pt.containsKey(k)) {
            final FXEssentiaStream trail2 = FXEssentiaStream.pt.get(k);
            if (!trail2.isExpired && trail2.vecs.size() < trail2.length) {
                final FXEssentiaStream fxEssentiaStream = trail2;
                fxEssentiaStream.length += Math.max(extend, 5);
                final FXEssentiaStream fxEssentiaStream2 = trail2;
                fxEssentiaStream2.particleMaxAge += Math.max(extend, 5);
                this.particleMaxAge = 0;
            }
        }
        if (this.particleMaxAge > 0) {
            FXEssentiaStream.pt.put(k, this);
            this.key = k;
        }
        this.motionX = MathHelper.sin(count / 4.0f) * 0.015f;
        this.motionY = my + MathHelper.sin(count / 3.0f) * 0.015f;
        this.motionZ = MathHelper.sin(count / 2.0f) * 0.015f;
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
        this.endPos = bp2;
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        final double ePX = this.startX - FXEssentiaStream.interpPosX;
        final double ePY = this.startY - FXEssentiaStream.interpPosY;
        final double ePZ = this.startZ - FXEssentiaStream.interpPosZ;
        GL11.glTranslated(ePX, ePY, ePZ);
        if (this.points != null && this.points.length > 2) {
            Minecraft.getMinecraft().renderEngine.bindTexture(FXEssentiaStream.TEX0);
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
            if (FXEssentiaStream.pt.containsKey(this.key) && FXEssentiaStream.pt.get(this.key).isExpired) {
                FXEssentiaStream.pt.remove(this.key);
            }
            return;
        }
        this.motionY += 0.01 * this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985;
        this.motionY *= 0.985;
        this.motionZ *= 0.985;
        this.motionX = MathHelper.clamp((float)this.motionX, -0.05f, 0.05f);
        this.motionY = MathHelper.clamp((float)this.motionY, -0.05f, 0.05f);
        this.motionZ = MathHelper.clamp((float)this.motionZ, -0.05f, 0.05f);
        double dx = this.targetX - this.posX;
        double dy = this.targetY - this.posY;
        double dz = this.targetZ - this.posZ;
        final double d13 = 0.01;
        final double d14 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= d14;
        dy /= d14;
        dz /= d14;
        this.motionX += dx * (d13 / Math.min(1.0, d14));
        this.motionY += dy * (d13 / Math.min(1.0, d14));
        this.motionZ += dz * (d13 / Math.min(1.0, d14));
        float scale = this.particleScale * (0.75f + MathHelper.sin((this.count + this.particleAge) / 2.0f) * 0.25f);
        if (d14 < 1.0) {
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
            FXDispatcher.INSTANCE.essentiaDropFx(this.targetX + this.rand.nextGaussian() * 0.07500000298023224, this.targetY + this.rand.nextGaussian() * 0.07500000298023224, this.targetZ + this.rand.nextGaussian() * 0.07500000298023224, this.particleRed, this.particleGreen, this.particleBlue, 0.5f);
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
        if (this.vecs.size() > 2 && this.rand.nextBoolean()) {
            int q = this.rand.nextInt(3);
            if (this.rand.nextBoolean()) {
                q = this.vecs.size() - 2;
            }
            FXDispatcher.INSTANCE.essentiaDropFx(this.vecs.get(q).x + this.startX, this.vecs.get(q).y + this.startY, this.vecs.get(q).z + this.startZ, this.particleRed, this.particleGreen, this.particleBlue, 0.5f);
        }
    }
    
    public void setGravity(final float value) {
        this.particleGravity = value;
    }
    
    static {
        FXEssentiaStream.pt = new HashMap<String, FXEssentiaStream>();
        TEX0 = new ResourceLocation("thaumcraft", "textures/misc/essentia.png");
    }
}
