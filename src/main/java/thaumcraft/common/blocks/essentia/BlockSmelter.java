// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.essentia;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.EnumParticleTypes;
import java.util.Random;
import thaumcraft.api.aura.AuraHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.world.IBlockAccess;
import thaumcraft.Thaumcraft;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.essentia.TileSmelter;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockSmelter extends BlockTCDevice implements IBlockEnabled, IBlockFacingHorizontal
{
    public BlockSmelter(final String name) {
        super(Material.IRON, TileSmelter.class, name);
        this.setSoundType(SoundType.METAL);
        final IBlockState bs = this.blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)EnumFacing.NORTH);
        bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false);
        this.setDefaultState(bs);
    }
    
    @Override
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
    }
    
    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState bs = this.getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)placer.getHorizontalFacing().getOpposite());
        bs = bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false);
        return bs;
    }
    
    @Override
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos pos2) {
        final TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileSmelter) {
            ((TileSmelter)te).checkNeighbours();
        }
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (!world.isRemote && !player.isSneaking()) {
            player.openGui(Thaumcraft.instance, 9, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    
    public int getLightValue(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return BlockStateUtils.isEnabled(world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos))) ? 13 : super.getLightValue(state, world, pos);
    }
    
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return true;
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public int getComparatorInputOverride(final IBlockState state, final World world, final BlockPos pos) {
        final TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof IInventory) {
            return Container.calcRedstoneFromInventory((IInventory)te);
        }
        return 0;
    }
    
    public static void setFurnaceState(final World world, final BlockPos pos, final boolean state) {
        if (state == BlockStateUtils.isEnabled(world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)))) {
            return;
        }
        final TileEntity tileentity = world.getTileEntity(pos);
        BlockSmelter.keepInventory = true;
        world.setBlockState(pos, world.getBlockState(pos).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)state), 3);
        world.setBlockState(pos, world.getBlockState(pos).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)state), 3);
        if (tileentity != null) {
            tileentity.validate();
            world.setTileEntity(pos, tileentity);
        }
        BlockSmelter.keepInventory = false;
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileSmelter && !worldIn.isRemote && ((TileSmelter)tileentity).vis > 0) {
            final int ess = ((TileSmelter)tileentity).vis;
            AuraHelper.polluteAura(worldIn, pos, (float)ess, true);
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World w, final BlockPos pos, final Random r) {
        if (BlockStateUtils.isEnabled(state)) {
            final float f = pos.getX() + 0.5f;
            final float f2 = pos.getY() + 0.2f + r.nextFloat() * 5.0f / 16.0f;
            final float f3 = pos.getZ() + 0.5f;
            final float f4 = 0.52f;
            final float f5 = r.nextFloat() * 0.5f - 0.25f;
            if (BlockStateUtils.getFacing(state) == EnumFacing.WEST) {
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f - f4, f2, f3 + f5, 0.0, 0.0, 0.0);
                w.spawnParticle(EnumParticleTypes.FLAME, f - f4, f2, f3 + f5, 0.0, 0.0, 0.0);
            }
            if (BlockStateUtils.getFacing(state) == EnumFacing.EAST) {
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f4, f2, f3 + f5, 0.0, 0.0, 0.0);
                w.spawnParticle(EnumParticleTypes.FLAME, f + f4, f2, f3 + f5, 0.0, 0.0, 0.0);
            }
            if (BlockStateUtils.getFacing(state) == EnumFacing.NORTH) {
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f5, f2, f3 - f4, 0.0, 0.0, 0.0);
                w.spawnParticle(EnumParticleTypes.FLAME, f + f5, f2, f3 - f4, 0.0, 0.0, 0.0);
            }
            if (BlockStateUtils.getFacing(state) == EnumFacing.SOUTH) {
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f5, f2, f3 + f4, 0.0, 0.0, 0.0);
                w.spawnParticle(EnumParticleTypes.FLAME, f + f5, f2, f3 + f4, 0.0, 0.0, 0.0);
            }
        }
    }
}
