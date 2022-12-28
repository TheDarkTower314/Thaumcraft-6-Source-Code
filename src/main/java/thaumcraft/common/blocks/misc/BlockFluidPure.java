package thaumcraft.common.blocks.misc;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.potions.PotionWarpWard;


public class BlockFluidPure extends BlockFluidClassic
{
    public static Material FLUID_PURE_MATERIAL;
    
    public BlockFluidPure() {
        super(ConfigBlocks.FluidPure.instance, BlockFluidPure.FLUID_PURE_MATERIAL);
        setRegistryName("purifying_fluid");
        setUnlocalizedName("purifying_fluid");
        setCreativeTab(ConfigItems.TABTC);
    }
    
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        entity.motionX *= 1.0f - getQuantaPercentage(world, pos) / 2.0f;
        entity.motionZ *= 1.0f - getQuantaPercentage(world, pos) / 2.0f;
        if (!world.isRemote && isSourceBlock(world, pos) && entity instanceof EntityPlayer && !((EntityPlayer)entity).isPotionActive(PotionWarpWard.instance)) {
            int warp = ThaumcraftCapabilities.getWarp((EntityPlayer)entity).get(IPlayerWarp.EnumWarpType.PERMANENT);
            int div = 1;
            if (warp > 0) {
                div = (int)Math.sqrt(warp);
                if (div < 1) {
                    div = 1;
                }
            }
            ((EntityPlayer)entity).addPotionEffect(new PotionEffect(PotionWarpWard.instance, Math.min(32000, 200000 / div), 0, true, true));
            world.setBlockToAir(pos);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        int meta = getMetaFromState(state);
        if (rand.nextInt(10) == 0) {
            FXGeneric fb = new FXGeneric(world, pos.getX() + rand.nextFloat(), pos.getY() + 0.125f * (8 - meta), pos.getZ() + rand.nextFloat(), 0.0, 0.0, 0.0);
            fb.setMaxAge(10 + world.rand.nextInt(10));
            fb.setScale(world.rand.nextFloat() * 0.3f + 0.3f);
            fb.setRBGColorF(1.0f, 1.0f, 1.0f);
            fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
            fb.setGravity(-0.01f);
            fb.setAlphaF(0.25f);
            fb.setParticle(64);
            fb.setFinalFrames(65, 66);
            ParticleEngine.addEffect(world, fb);
        }
        if (rand.nextInt(50) == 0) {
            double var21 = pos.getX() + rand.nextFloat();
            double var22 = pos.getY() + 0.5;
            double var23 = pos.getZ() + rand.nextFloat();
            world.playSound(var21, var22, var23, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.1f + rand.nextFloat() * 0.1f, 0.9f + rand.nextFloat() * 0.15f, false);
        }
    }
    
    static {
        FLUID_PURE_MATERIAL = new MaterialLiquid(MapColor.SILVER);
    }
}
