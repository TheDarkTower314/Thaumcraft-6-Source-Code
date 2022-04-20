// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.particles;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.particle.Particle;

@SideOnly(Side.CLIENT)
public class FXBoreParticles extends Particle
{
    private IBlockState blockInstance;
    private ItemStack itemInstance;
    private int side;
    private Entity target;
    private double targetX;
    private double targetY;
    private double targetZ;
    
    public FXBoreParticles(final World par1World, final double par2, final double par4, final double par6, final double tx, final double ty, final double tz, final IBlockState par14Block, final int par15) {
        super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        this.blockInstance = par14Block;
        try {
            this.setParticleTexture(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Item.getItemFromBlock(par14Block.getBlock()), par15));
        }
        catch (final Exception e) {
            this.setParticleTexture(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Item.getItemFromBlock(Blocks.STONE), 0));
            this.particleMaxAge = 0;
        }
        this.particleGravity = par14Block.getBlock().blockParticleGravity;
        final float particleRed = 0.6f;
        this.particleBlue = particleRed;
        this.particleGreen = particleRed;
        this.particleRed = particleRed;
        this.particleScale = this.rand.nextFloat() * 0.3f + 0.4f;
        this.side = par15;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        final double dx = tx - this.posX;
        final double dy = ty - this.posY;
        final double dz = tz - this.posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 10.0f);
        if (base < 1) {
            base = 1;
        }
        this.particleMaxAge = base / 2 + this.rand.nextInt(base);
        final float f3 = 0.01f;
        this.motionX = (float)this.world.rand.nextGaussian() * f3;
        this.motionY = (float)this.world.rand.nextGaussian() * f3;
        this.motionZ = (float)this.world.rand.nextGaussian() * f3;
        this.particleGravity = 0.01f;
    }
    
    public FXBoreParticles(final World par1World, final double par2, final double par4, final double par6, final double tx, final double ty, final double tz, final double sx, final double sy, final double sz, final ItemStack item) {
        super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        this.itemInstance = item;
        this.setParticleTexture(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item.getItem(), item.getItemDamage()));
        this.particleGravity = Blocks.SNOW_LAYER.blockParticleGravity;
        final float particleRed = 0.6f;
        this.particleBlue = particleRed;
        this.particleGreen = particleRed;
        this.particleRed = particleRed;
        this.particleScale = this.rand.nextFloat() * 0.3f + 0.4f;
        this.side = 0;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        final double dx = tx - this.posX;
        final double dy = ty - this.posY;
        final double dz = tz - this.posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 10.0f);
        if (base < 1) {
            base = 1;
        }
        this.particleMaxAge = base / 2 + this.rand.nextInt(base);
        final float f3 = 0.01f;
        this.motionX = sx + (float)this.world.rand.nextGaussian() * f3;
        this.motionY = sy + (float)this.world.rand.nextGaussian() * f3;
        this.motionZ = sz + (float)this.world.rand.nextGaussian() * f3;
        this.particleGravity = 0.01f;
        final Entity renderentity = FMLClientHandler.instance().getClient().getRenderViewEntity();
        int visibleDistance = 64;
        if (!FMLClientHandler.instance().getClient().gameSettings.fancyGraphics) {
            visibleDistance = 32;
        }
        if (renderentity.getDistance(this.posX, this.posY, this.posZ) > visibleDistance) {
            this.particleMaxAge = 0;
        }
    }
    
    public void setTarget(final Entity target) {
        this.target = target;
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.target != null) {
            this.targetX = this.target.posX;
            this.targetY = this.target.posY + this.target.getEyeHeight();
            this.targetZ = this.target.posZ;
        }
        if (this.particleAge++ >= this.particleMaxAge || (MathHelper.floor(this.posX) == MathHelper.floor(this.targetX) && MathHelper.floor(this.posY) == MathHelper.floor(this.targetY) && MathHelper.floor(this.posZ) == MathHelper.floor(this.targetZ))) {
            this.setExpired();
            return;
        }
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985;
        this.motionY *= 0.95;
        this.motionZ *= 0.985;
        double dx = this.targetX - this.posX;
        double dy = this.targetY - this.posY;
        double dz = this.targetZ - this.posZ;
        final double d11 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        final double clamp = Math.min(0.25, d11 / 15.0);
        if (d11 < 2.0) {
            this.particleScale *= 0.9f;
        }
        dx /= d11;
        dy /= d11;
        dz /= d11;
        this.motionX += dx * clamp;
        this.motionY += dy * clamp;
        this.motionZ += dz * clamp;
        this.motionX = MathHelper.clamp((float)this.motionX, -clamp, clamp);
        this.motionY = MathHelper.clamp((float)this.motionY, -clamp, clamp);
        this.motionZ = MathHelper.clamp((float)this.motionZ, -clamp, clamp);
        this.motionX += this.rand.nextGaussian() * 0.005;
        this.motionY += this.rand.nextGaussian() * 0.005;
        this.motionZ += this.rand.nextGaussian() * 0.005;
    }
    
    public int getFXLayer() {
        return 1;
    }
    
    public FXBoreParticles getObjectColor(final BlockPos pos) {
        if (this.blockInstance == null || this.world.getBlockState(pos) != this.blockInstance) {
            try {
                final int var4 = Minecraft.getMinecraft().getItemColors().colorMultiplier(this.itemInstance, 0);
                this.particleRed *= (var4 >> 16 & 0xFF) / 255.0f;
                this.particleGreen *= (var4 >> 8 & 0xFF) / 255.0f;
                this.particleBlue *= (var4 & 0xFF) / 255.0f;
            }
            catch (final Exception ex) {}
            return this;
        }
        if (this.blockInstance == Blocks.GRASS && this.side != 1) {
            return this;
        }
        try {
            final int var4 = Minecraft.getMinecraft().getBlockColors().colorMultiplier(this.blockInstance, this.world, pos, 0);
            this.particleRed *= (var4 >> 16 & 0xFF) / 255.0f;
            this.particleGreen *= (var4 >> 8 & 0xFF) / 255.0f;
            this.particleBlue *= (var4 & 0xFF) / 255.0f;
        }
        catch (final Exception ex2) {}
        return this;
    }
    
    public void renderParticle(final BufferBuilder p_180434_1_, final Entity p_180434_2_, final float p_180434_3_, final float p_180434_4_, final float p_180434_5_, final float p_180434_6_, final float p_180434_7_, final float p_180434_8_) {
        float f6 = (this.particleTextureIndexX + this.particleTextureJitterX / 4.0f) / 16.0f;
        float f7 = f6 + 0.015609375f;
        float f8 = (this.particleTextureIndexY + this.particleTextureJitterY / 4.0f) / 16.0f;
        float f9 = f8 + 0.015609375f;
        final float f10 = 0.1f * this.particleScale;
        if (this.particleTexture != null) {
            f6 = this.particleTexture.getInterpolatedU(this.particleTextureJitterX / 4.0f * 16.0f);
            f7 = this.particleTexture.getInterpolatedU((this.particleTextureJitterX + 1.0f) / 4.0f * 16.0f);
            f8 = this.particleTexture.getInterpolatedV(this.particleTextureJitterY / 4.0f * 16.0f);
            f9 = this.particleTexture.getInterpolatedV((this.particleTextureJitterY + 1.0f) / 4.0f * 16.0f);
        }
        final int i = this.getBrightnessForRender(p_180434_3_);
        final int j = i >> 16 & 0xFFFF;
        final int k = i & 0xFFFF;
        final float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * p_180434_3_ - FXBoreParticles.interpPosX);
        final float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * p_180434_3_ - FXBoreParticles.interpPosY);
        final float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * p_180434_3_ - FXBoreParticles.interpPosZ);
        p_180434_1_.pos(f11 - p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 - p_180434_6_ * f10 - p_180434_8_ * f10).tex(f6, f9).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0f).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 - p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 - p_180434_6_ * f10 + p_180434_8_ * f10).tex(f6, f8).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0f).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 + p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 + p_180434_6_ * f10 + p_180434_8_ * f10).tex(f7, f8).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0f).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 + p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 + p_180434_6_ * f10 - p_180434_8_ * f10).tex(f7, f9).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0f).lightmap(j, k).endVertex();
    }
}
