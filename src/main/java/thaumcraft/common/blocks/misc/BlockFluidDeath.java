package thaumcraft.common.blocks.misc;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXSlimyBubble;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;


public class BlockFluidDeath extends BlockFluidClassic
{
    public static Material FLUID_DEATH_MATERIAL;
    
    public BlockFluidDeath() {
        super(ConfigBlocks.FluidDeath.instance, BlockFluidDeath.FLUID_DEATH_MATERIAL);
        setRegistryName("liquid_death");
        setUnlocalizedName("liquid_death");
        setCreativeTab(ConfigItems.TABTC);
        setQuantaPerBlock(4);
    }
    
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        entity.motionX *= 1.0f - getQuantaPercentage(world, pos) / 2.0f;
        entity.motionZ *= 1.0f - getQuantaPercentage(world, pos) / 2.0f;
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            entity.attackEntityFrom(DamageSourceThaumcraft.dissolve, (float)(4 - getMetaFromState(state) + 1));
        }
    }
    
    public int getQuanta() {
        return quantaPerBlock;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextInt(20) == 0) {
            int meta = getMetaFromState(state);
            float h = rand.nextFloat() * 0.075f;
            FXSlimyBubble ef = new FXSlimyBubble(world, pos.getX() + rand.nextFloat(), pos.getY() + 0.1f + 0.225f * (4 - meta), pos.getZ() + rand.nextFloat(), 0.075f + h);
            ef.setAlphaF(0.8f);
            ef.setRBGColorF(0.3f - rand.nextFloat() * 0.1f, 0.0f, 0.4f + rand.nextFloat() * 0.1f);
            ParticleEngine.addEffect(world, ef);
        }
        if (rand.nextInt(50) == 0) {
            double var21 = pos.getX() + rand.nextFloat();
            double var22 = pos.getY() + (double)(getMaxRenderHeightMeta() / 4.0f);
            double var23 = pos.getZ() + rand.nextFloat();
            world.playSound(var21, var22, var23, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.1f + rand.nextFloat() * 0.1f, 0.9f + rand.nextFloat() * 0.15f, false);
        }
    }
    
    static {
        FLUID_DEATH_MATERIAL = new MaterialLiquid(MapColor.PURPLE);
    }
}
