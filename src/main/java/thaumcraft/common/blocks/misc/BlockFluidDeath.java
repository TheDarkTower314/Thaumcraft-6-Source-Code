// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.misc;

import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.material.MapColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.client.particle.Particle;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXSlimyBubble;
import java.util.Random;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigItems;
import net.minecraftforge.fluids.Fluid;
import thaumcraft.common.config.ConfigBlocks;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;

public class BlockFluidDeath extends BlockFluidClassic
{
    public static final Material FLUID_DEATH_MATERIAL;
    
    public BlockFluidDeath() {
        super(ConfigBlocks.FluidDeath.instance, BlockFluidDeath.FLUID_DEATH_MATERIAL);
        this.setRegistryName("liquid_death");
        this.setUnlocalizedName("liquid_death");
        this.setCreativeTab(ConfigItems.TABTC);
        this.setQuantaPerBlock(4);
    }
    
    public void onEntityCollidedWithBlock(final World world, final BlockPos pos, final IBlockState state, final Entity entity) {
        entity.motionX *= 1.0f - this.getQuantaPercentage(world, pos) / 2.0f;
        entity.motionZ *= 1.0f - this.getQuantaPercentage(world, pos) / 2.0f;
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            entity.attackEntityFrom(DamageSourceThaumcraft.dissolve, (float)(4 - this.getMetaFromState(state) + 1));
        }
    }
    
    public int getQuanta() {
        return this.quantaPerBlock;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World world, final BlockPos pos, final Random rand) {
        if (rand.nextInt(20) == 0) {
            final int meta = this.getMetaFromState(state);
            final float h = rand.nextFloat() * 0.075f;
            final FXSlimyBubble ef = new FXSlimyBubble(world, pos.getX() + rand.nextFloat(), pos.getY() + 0.1f + 0.225f * (4 - meta), pos.getZ() + rand.nextFloat(), 0.075f + h);
            ef.setAlphaF(0.8f);
            ef.setRBGColorF(0.3f - rand.nextFloat() * 0.1f, 0.0f, 0.4f + rand.nextFloat() * 0.1f);
            ParticleEngine.addEffect(world, ef);
        }
        if (rand.nextInt(50) == 0) {
            final double var21 = pos.getX() + rand.nextFloat();
            final double var22 = pos.getY() + (double)(this.getMaxRenderHeightMeta() / 4.0f);
            final double var23 = pos.getZ() + rand.nextFloat();
            world.playSound(var21, var22, var23, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.1f + rand.nextFloat() * 0.1f, 0.9f + rand.nextFloat() * 0.15f, false);
        }
    }
    
    static {
        FLUID_DEATH_MATERIAL = new MaterialLiquid(MapColor.PURPLE);
    }
}
