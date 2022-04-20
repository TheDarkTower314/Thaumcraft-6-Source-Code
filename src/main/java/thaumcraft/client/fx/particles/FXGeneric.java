// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import thaumcraft.common.lib.utils.Utils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.client.particle.Particle;

public class FXGeneric extends Particle
{
    boolean doneFrames;
    boolean flipped;
    double windX;
    double windZ;
    int layer;
    float dr;
    float dg;
    float db;
    boolean loop;
    float rotationSpeed;
    int startParticle;
    int numParticles;
    int particleInc;
    float[] scaleKeys;
    float[] scaleFrames;
    float[] alphaKeys;
    float[] alphaFrames;
    double slowDown;
    float randomX;
    float randomY;
    float randomZ;
    int[] finalFrames;
    boolean angled;
    float angleYaw;
    float anglePitch;
    int gridSize;
    
    public FXGeneric(final World world, final double x, final double y, final double z, final double xx, final double yy, final double zz) {
        super(world, x, y, z, xx, yy, zz);
        this.doneFrames = false;
        this.flipped = false;
        this.layer = 0;
        this.dr = 0.0f;
        this.dg = 0.0f;
        this.db = 0.0f;
        this.loop = false;
        this.rotationSpeed = 0.0f;
        this.startParticle = 0;
        this.numParticles = 1;
        this.particleInc = 1;
        this.scaleKeys = new float[] { 1.0f };
        this.scaleFrames = new float[] { 0.0f };
        this.alphaKeys = new float[] { 1.0f };
        this.alphaFrames = new float[] { 0.0f };
        this.slowDown = 0.9800000190734863;
        this.finalFrames = null;
        this.angled = false;
        this.gridSize = 64;
        this.setSize(0.1f, 0.1f);
        this.setPosition(x, y, z);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.particleTextureJitterX = 0.0f;
        this.particleTextureJitterY = 0.0f;
        this.motionX = xx;
        this.motionY = yy;
        this.motionZ = zz;
    }
    
    public FXGeneric(final World world, final double x, final double y, final double z) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.doneFrames = false;
        this.flipped = false;
        this.layer = 0;
        this.dr = 0.0f;
        this.dg = 0.0f;
        this.db = 0.0f;
        this.loop = false;
        this.rotationSpeed = 0.0f;
        this.startParticle = 0;
        this.numParticles = 1;
        this.particleInc = 1;
        this.scaleKeys = new float[] { 1.0f };
        this.scaleFrames = new float[] { 0.0f };
        this.alphaKeys = new float[] { 1.0f };
        this.alphaFrames = new float[] { 0.0f };
        this.slowDown = 0.9800000190734863;
        this.finalFrames = null;
        this.angled = false;
        this.gridSize = 64;
        this.setSize(0.1f, 0.1f);
        this.setPosition(x, y, z);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.particleTextureJitterX = 0.0f;
        this.particleTextureJitterY = 0.0f;
    }
    
    void calculateFrames() {
        this.doneFrames = true;
        if (this.alphaKeys == null) {
            this.setAlphaF(1.0f);
        }
        this.alphaFrames = new float[this.particleMaxAge + 1];
        float inc = (this.alphaKeys.length - 1) / (float)this.particleMaxAge;
        float is = 0.0f;
        for (int a = 0; a <= this.particleMaxAge; ++a) {
            final int isF = MathHelper.floor(is);
            float diff = (isF < this.alphaKeys.length - 1) ? (diff = this.alphaKeys[isF + 1] - this.alphaKeys[isF]) : 0.0f;
            final float pa = is - isF;
            this.alphaFrames[a] = this.alphaKeys[isF] + diff * pa;
            is += inc;
        }
        if (this.scaleKeys == null) {
            this.setScale(1.0f);
        }
        this.scaleFrames = new float[this.particleMaxAge + 1];
        inc = (this.scaleKeys.length - 1) / (float)this.particleMaxAge;
        is = 0.0f;
        for (int a = 0; a <= this.particleMaxAge; ++a) {
            final int isF = MathHelper.floor(is);
            float diff = (isF < this.scaleKeys.length - 1) ? (diff = this.scaleKeys[isF + 1] - this.scaleKeys[isF]) : 0.0f;
            final float pa = is - isF;
            this.scaleFrames[a] = this.scaleKeys[isF] + diff * pa;
            is += inc;
        }
    }
    
    public void onUpdate() {
        if (!this.doneFrames) {
            this.calculateFrames();
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
        this.prevParticleAngle = this.particleAngle;
        this.particleAngle += 3.1415927f * this.rotationSpeed * 2.0f;
        this.motionY -= 0.04 * this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= this.slowDown;
        this.motionY *= this.slowDown;
        this.motionZ *= this.slowDown;
        this.motionX += this.world.rand.nextGaussian() * this.randomX;
        this.motionY += this.world.rand.nextGaussian() * this.randomY;
        this.motionZ += this.world.rand.nextGaussian() * this.randomZ;
        this.motionX += this.windX;
        this.motionZ += this.windZ;
        if (this.onGround && this.slowDown != 1.0) {
            this.motionX *= 0.699999988079071;
            this.motionZ *= 0.699999988079071;
        }
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        if (this.loop) {
            this.setParticleTextureIndex(this.startParticle + this.particleAge / this.particleInc % this.numParticles);
        }
        else {
            final float fs = this.particleAge / (float)this.particleMaxAge;
            this.setParticleTextureIndex((int)(this.startParticle + Math.min(this.numParticles * fs, (float)(this.numParticles - 1))));
        }
        if (this.finalFrames != null && this.finalFrames.length > 0 && this.particleAge > this.particleMaxAge - this.finalFrames.length) {
            int frame = this.particleMaxAge - this.particleAge;
            if (frame < 0) {
                frame = 0;
            }
            this.setParticleTextureIndex(this.finalFrames[frame]);
        }
        this.particleAlpha = ((this.alphaFrames.length <= 0) ? 0.0f : this.alphaFrames[Math.min(this.particleAge, this.alphaFrames.length - 1)]);
        this.particleScale = ((this.scaleFrames.length <= 0) ? 0.0f : this.scaleFrames[Math.min(this.particleAge, this.scaleFrames.length - 1)]);
        this.draw(wr, entity, f, f1, f2, f3, f4, f5);
    }
    
    public boolean isFlipped() {
        return this.flipped;
    }
    
    public void setFlipped(final boolean flip) {
        this.flipped = flip;
    }
    
    public void draw(final BufferBuilder wr, final Entity entityIn, final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ) {
        float tx1 = this.particleTextureIndexX / (float)this.gridSize;
        float tx2 = tx1 + 1.0f / this.gridSize;
        float ty1 = this.particleTextureIndexY / (float)this.gridSize;
        float ty2 = ty1 + 1.0f / this.gridSize;
        final float ts = 0.1f * this.particleScale;
        if (this.particleTexture != null) {
            tx1 = this.particleTexture.getMinU();
            tx2 = this.particleTexture.getMaxU();
            ty1 = this.particleTexture.getMinV();
            ty2 = this.particleTexture.getMaxV();
        }
        if (this.flipped) {
            final float t = tx1;
            tx1 = tx2;
            tx2 = t;
        }
        final float fs = MathHelper.clamp((this.particleAge + partialTicks) / this.particleMaxAge, 0.0f, 1.0f);
        final float pr = this.particleRed + (this.dr - this.particleRed) * fs;
        final float pg = this.particleGreen + (this.dg - this.particleGreen) * fs;
        final float pb = this.particleBlue + (this.db - this.particleBlue) * fs;
        final int i = this.getBrightnessForRender(partialTicks);
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        final float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - FXGeneric.interpPosX);
        final float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - FXGeneric.interpPosY);
        final float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - FXGeneric.interpPosZ);
        if (this.angled) {
            Tessellator.getInstance().draw();
            GL11.glPushMatrix();
            GL11.glTranslated(f5, f6, f7);
            GL11.glRotatef(-this.angleYaw + 90.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(this.anglePitch + 90.0f, 1.0f, 0.0f, 0.0f);
            if (this.particleAngle != 0.0f) {
                final float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
                GL11.glRotated(f8 * 57.29577951308232, 0.0, 0.0, 1.0);
            }
            wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            wr.pos(-ts, -ts, 0.0).tex(tx2, ty2).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
            wr.pos(-ts, ts, 0.0).tex(tx2, ty1).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
            wr.pos(ts, ts, 0.0).tex(tx1, ty1).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
            wr.pos(ts, -ts, 0.0).tex(tx1, ty2).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
            Tessellator.getInstance().draw();
            GL11.glPopMatrix();
            wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }
        else {
            final Vec3d[] avec3d = { new Vec3d(-rotationX * ts - rotationXY * ts, -rotationZ * ts, -rotationYZ * ts - rotationXZ * ts), new Vec3d(-rotationX * ts + rotationXY * ts, rotationZ * ts, -rotationYZ * ts + rotationXZ * ts), new Vec3d(rotationX * ts + rotationXY * ts, rotationZ * ts, rotationYZ * ts + rotationXZ * ts), new Vec3d(rotationX * ts - rotationXY * ts, -rotationZ * ts, rotationYZ * ts - rotationXZ * ts) };
            if (this.particleAngle != 0.0f) {
                final float f9 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
                final float f10 = MathHelper.cos(f9 * 0.5f);
                final float f11 = MathHelper.sin(f9 * 0.5f) * (float)FXGeneric.cameraViewDir.x;
                final float f12 = MathHelper.sin(f9 * 0.5f) * (float)FXGeneric.cameraViewDir.y;
                final float f13 = MathHelper.sin(f9 * 0.5f) * (float)FXGeneric.cameraViewDir.z;
                final Vec3d vec3d = new Vec3d(f11, f12, f13);
                for (int l = 0; l < 4; ++l) {
                    avec3d[l] = vec3d.scale(2.0 * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale(f10 * f10 - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale(2.0f * f10));
                }
            }
            wr.pos(f5 + avec3d[0].x, f6 + avec3d[0].y, f7 + avec3d[0].z).tex(tx2, ty2).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
            wr.pos(f5 + avec3d[1].x, f6 + avec3d[1].y, f7 + avec3d[1].z).tex(tx2, ty1).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
            wr.pos(f5 + avec3d[2].x, f6 + avec3d[2].y, f7 + avec3d[2].z).tex(tx1, ty1).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
            wr.pos(f5 + avec3d[3].x, f6 + avec3d[3].y, f7 + avec3d[3].z).tex(tx1, ty2).color(pr, pg, pb, this.particleAlpha).lightmap(j, k).endVertex();
        }
    }
    
    public void setWind(final double d) {
        final int m = this.world.getMoonPhase();
        final Vec3d vsource = new Vec3d(0.0, 0.0, 0.0);
        Vec3d vtar = new Vec3d(0.1, 0.0, 0.0);
        vtar = Utils.rotateAroundY(vtar, m * (40 + this.world.rand.nextInt(10)) / 180.0f * 3.1415927f);
        final Vec3d vres = vsource.addVector(vtar.x, vtar.y, vtar.z);
        this.windX = vres.x * d;
        this.windZ = vres.z * d;
    }
    
    public void setLayer(final int layer) {
        this.layer = layer;
    }
    
    public void setRBGColorF(final float particleRedIn, final float particleGreenIn, final float particleBlueIn) {
        super.setRBGColorF(particleRedIn, particleGreenIn, particleBlueIn);
        this.dr = particleRedIn;
        this.dg = particleGreenIn;
        this.db = particleBlueIn;
    }
    
    public void setRBGColorF(final float particleRedIn, final float particleGreenIn, final float particleBlueIn, final float r2, final float g2, final float b2) {
        super.setRBGColorF(particleRedIn, particleGreenIn, particleBlueIn);
        this.dr = r2;
        this.dg = g2;
        this.db = b2;
    }
    
    public int getFXLayer() {
        return this.layer;
    }
    
    public void setLoop(final boolean loop) {
        this.loop = loop;
    }
    
    public void setRotationSpeed(final float rot) {
        this.rotationSpeed = (float)(rot * 0.017453292519943);
    }
    
    public void setRotationSpeed(final float start, final float rot) {
        this.particleAngle = (float)(start * 3.141592653589793 * 2.0);
        this.rotationSpeed = (float)(rot * 0.017453292519943);
    }
    
    public void setMaxAge(final int max) {
        this.particleMaxAge = max;
    }
    
    public void setParticles(final int startParticle, final int numParticles, final int particleInc) {
        this.numParticles = numParticles;
        this.particleInc = particleInc;
        this.setParticleTextureIndex(this.startParticle = startParticle);
    }
    
    public void setParticle(final int startParticle) {
        this.numParticles = 1;
        this.particleInc = 1;
        this.setParticleTextureIndex(this.startParticle = startParticle);
    }
    
    public void setScale(final float... scale) {
        this.particleScale = scale[0];
        this.scaleKeys = scale;
    }
    
    public void setAlphaF(final float... a1) {
        super.setAlphaF(a1[0]);
        this.alphaKeys = a1;
    }
    
    public void setAlphaF(final float a1) {
        super.setAlphaF(a1);
        (this.alphaKeys = new float[1])[0] = a1;
    }
    
    public void setSlowDown(final double slowDown) {
        this.slowDown = slowDown;
    }
    
    public void setRandomMovementScale(final float x, final float y, final float z) {
        this.randomX = x;
        this.randomY = y;
        this.randomZ = z;
    }
    
    public void setFinalFrames(final int... frames) {
        this.finalFrames = frames;
    }
    
    public void setAngles(final float yaw, final float pitch) {
        this.angleYaw = yaw;
        this.anglePitch = pitch;
        this.angled = true;
    }
    
    public void setGravity(final float g) {
        this.particleGravity = g;
    }
    
    public void setParticleTextureIndex(int index) {
        if (index < 0) {
            index = 0;
        }
        this.particleTextureIndexX = index % this.gridSize;
        this.particleTextureIndexY = index / this.gridSize;
    }
    
    public void setGridSize(final int gridSize) {
        this.gridSize = gridSize;
    }
    
    public void setNoClip(final boolean clip) {
        this.canCollide = clip;
    }
}
