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
import thaumcraft.client.fx.particles.FXGeneric;
import java.util.Random;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.potions.PotionWarpWard;
import net.minecraft.entity.player.EntityPlayer;
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

public class BlockFluidPure extends BlockFluidClassic
{
    public static final Material FLUID_PURE_MATERIAL;
    
    public BlockFluidPure() {
        super(ConfigBlocks.FluidPure.instance, BlockFluidPure.FLUID_PURE_MATERIAL);
        this.setRegistryName("purifying_fluid");
        this.setUnlocalizedName("purifying_fluid");
        this.setCreativeTab(ConfigItems.TABTC);
    }
    
    public void onEntityCollidedWithBlock(final World world, final BlockPos pos, final IBlockState state, final Entity entity) {
        entity.motionX *= 1.0f - this.getQuantaPercentage(world, pos) / 2.0f;
        entity.motionZ *= 1.0f - this.getQuantaPercentage(world, pos) / 2.0f;
        if (!world.isRemote && this.isSourceBlock(world, pos) && entity instanceof EntityPlayer && !((EntityPlayer)entity).isPotionActive(PotionWarpWard.instance)) {
            final int warp = ThaumcraftCapabilities.getWarp((EntityPlayer)entity).get(IPlayerWarp.EnumWarpType.PERMANENT);
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
    public void randomDisplayTick(final IBlockState state, final World world, final BlockPos pos, final Random rand) {
        final int meta = this.getMetaFromState(state);
        if (rand.nextInt(10) == 0) {
            final FXGeneric fb = new FXGeneric(world, pos.getX() + rand.nextFloat(), pos.getY() + 0.125f * (8 - meta), pos.getZ() + rand.nextFloat(), 0.0, 0.0, 0.0);
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
            final double var21 = pos.getX() + rand.nextFloat();
            final double var22 = pos.getY() + 0.5;
            final double var23 = pos.getZ() + rand.nextFloat();
            world.playSound(var21, var22, var23, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.1f + rand.nextFloat() * 0.1f, 0.9f + rand.nextFloat() * 0.15f, false);
        }
    }
    
    static {
        FLUID_PURE_MATERIAL = new MaterialLiquid(MapColor.SILVER);
    }
}