package thaumcraft.common.blocks.world.taint;
import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.lib.SoundsTC;


public class BlockFluxGoo extends BlockFluidFinite
{
    public BlockFluxGoo() {
        super(ConfigBlocks.FluidFluxGoo.instance, ThaumcraftMaterials.MATERIAL_TAINT);
        setRegistryName("flux_goo");
        setUnlocalizedName("flux_goo");
        setCreativeTab(ConfigItems.TABTC);
        setSoundType(SoundsTC.GORE);
        setDefaultState(blockState.getBaseState().withProperty((IProperty)BlockFluxGoo.LEVEL, (Comparable)7));
    }
    
    public SoundType getSoundType() {
        return SoundsTC.GORE;
    }
    
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        int md = (int)state.getValue((IProperty)BlockFluxGoo.LEVEL);
        if (entity instanceof EntityThaumicSlime) {
            EntityThaumicSlime slime = (EntityThaumicSlime)entity;
            if (slime.getSlimeSize() < md && world.rand.nextBoolean()) {
                slime.setSlimeSize(slime.getSlimeSize() + 1, true);
                if (md > 1) {
                    world.setBlockState(pos, state.withProperty((IProperty)BlockFluxGoo.LEVEL, (Comparable)(md - 1)), 2);
                }
                else {
                    world.setBlockToAir(pos);
                }
            }
        }
        else {
            entity.motionX *= 1.0f - getQuantaPercentage(world, pos);
            entity.motionZ *= 1.0f - getQuantaPercentage(world, pos);
            if (entity instanceof EntityLivingBase) {
                PotionEffect pe = new PotionEffect(PotionVisExhaust.instance, 600, md / 3, true, true);
                pe.getCurativeItems().clear();
                ((EntityLivingBase)entity).addPotionEffect(pe);
            }
        }
    }
    
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        int meta = (int)state.getValue((IProperty)BlockFluxGoo.LEVEL);
        if (meta >= 2 && meta < 6 && world.isAirBlock(pos.up()) && rand.nextInt(50) == 0) {
            world.setBlockToAir(pos);
            EntityThaumicSlime slime = new EntityThaumicSlime(world);
            slime.setLocationAndAngles(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, 0.0f, 0.0f);
            slime.setSlimeSize(1, true);
            world.spawnEntity(slime);
            slime.playSound(SoundsTC.gore, 1.0f, 1.0f);
            return;
        }
        if (meta >= 6 && world.isAirBlock(pos.up()) && rand.nextInt(50) == 0) {
            world.setBlockToAir(pos);
            EntityThaumicSlime slime = new EntityThaumicSlime(world);
            slime.setLocationAndAngles(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, 0.0f, 0.0f);
            slime.setSlimeSize(2, true);
            world.spawnEntity(slime);
            slime.playSound(SoundsTC.gore, 1.0f, 1.0f);
            return;
        }
        if (rand.nextInt(4) == 0) {
            if (meta == 0) {
                if (rand.nextBoolean()) {
                    AuraHelper.polluteAura(world, pos, 1.0f, true);
                    world.setBlockToAir(pos);
                }
                else {
                    world.setBlockState(pos, BlocksTC.taintFibre.getDefaultState());
                }
            }
            else {
                world.setBlockState(pos, state.withProperty((IProperty)BlockFluxGoo.LEVEL, (Comparable)(meta - 1)), 2);
                AuraHelper.polluteAura(world, pos, 1.0f, true);
            }
            return;
        }
        super.updateTick(world, pos, state, rand);
    }
    
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return (int)world.getBlockState(pos).getValue((IProperty)BlockFluxGoo.LEVEL) < 4;
    }
    
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        int meta = getMetaFromState(state);
        if (rand.nextInt(44) <= meta) {
            FXGeneric fb = new FXGeneric(world, pos.getX() + rand.nextFloat(), pos.getY() + 0.125f * meta, pos.getZ() + rand.nextFloat(), 0.0, 0.0, 0.0);
            fb.setMaxAge(2 + world.rand.nextInt(3));
            fb.setScale(world.rand.nextFloat() * 0.3f + 0.2f);
            fb.setRBGColorF(1.0f, 0.0f, 0.5f);
            fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
            fb.setGravity(-0.01f);
            fb.setAlphaF(0.25f);
            fb.setParticleTextureIndex(64);
            fb.setFinalFrames(65, 66);
            ParticleEngine.addEffect(world, fb);
        }
    }
    
    static {
        BlockFluxGoo.defaultDisplacements.put(BlocksTC.taintFibre, true);
    }
}
