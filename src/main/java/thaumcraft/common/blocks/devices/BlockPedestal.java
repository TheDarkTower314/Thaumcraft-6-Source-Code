// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import thaumcraft.api.blocks.BlocksTC;
import java.util.Iterator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.crafting.TilePedestal;
import net.minecraft.block.material.Material;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.blocks.BlockTCTile;

public class BlockPedestal extends BlockTCTile implements IInfusionStabiliserExt
{
    public static BlockPedestal instance;
    
    public BlockPedestal(final String name) {
        super(Material.ROCK, TilePedestal.class, name);
        setSoundType(SoundType.STONE);
        BlockPedestal.instance = this;
        final IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)BlockInlay.CHARGE, (Comparable)0);
        setDefaultState(bs);
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (world.isRemote) {
            return true;
        }
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TilePedestal) {
            final TilePedestal ped = (TilePedestal)tile;
            if (ped.getStackInSlot(0).isEmpty() && !player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getCount() > 0) {
                final ItemStack i = player.getHeldItem(hand).copy();
                i.setCount(1);
                ped.setInventorySlotContents(0, i);
                player.getHeldItem(hand).shrink(1);
                if (player.getHeldItem(hand).getCount() == 0) {
                    player.setHeldItem(hand, ItemStack.EMPTY);
                }
                player.inventory.markDirty();
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 1.6f);
                return true;
            }
            if (!ped.getStackInSlot(0).isEmpty()) {
                InventoryUtils.dropItemsAtEntity(world, pos, player);
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 1.5f);
                return true;
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        return getDefaultState().withProperty((IProperty)BlockInlay.CHARGE, (Comparable)meta);
    }
    
    public int getMetaFromState(final IBlockState state) {
        return (int)state.getValue((IProperty)BlockInlay.CHARGE);
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockInlay.CHARGE);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState stateIn, final World worldIn, final BlockPos pos, final Random rand) {
        final int charge = (int)stateIn.getValue((IProperty)BlockInlay.CHARGE);
        if (charge > 0) {
            FXDispatcher.INSTANCE.blockRunes2(pos.getX(), pos.getY() - 0.375, pos.getZ(), 1.0f, 0.0f, 0.0f, 10, 0.0f);
        }
    }
    
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (!worldIn.isRemote) {
            BlockInlay.updateSurroundingInlay(worldIn, pos, state);
            for (final EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                BlockInlay.notifyInlayNeighborsOfStateChange(worldIn, pos.offset(enumfacing1));
            }
        }
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (!worldIn.isRemote) {
            for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
            BlockInlay.updateSurroundingInlay(worldIn, pos, state);
            for (final EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
                BlockInlay.notifyInlayNeighborsOfStateChange(worldIn, pos.offset(enumfacing2));
            }
        }
    }
    
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos fromPos) {
        if (!worldIn.isRemote) {
            BlockInlay.updateSurroundingInlay(worldIn, pos, state);
        }
    }
    
    public boolean canStabaliseInfusion(final World world, final BlockPos pos) {
        return true;
    }
    
    @Override
    public float getStabilizationAmount(final World world, final BlockPos pos) {
        final Block b = world.getBlockState(pos).getBlock();
        return (b == BlocksTC.pedestalEldritch) ? 0.1f : 0.0f;
    }
    
    @Override
    public boolean hasSymmetryPenalty(final World world, final BlockPos pos1, final BlockPos pos2) {
        final TileEntity te1 = world.getTileEntity(pos1);
        final TileEntity te2 = world.getTileEntity(pos2);
        if (world.isRemote) {
            if (te1 != null && te2 != null && te1 instanceof TilePedestal && te2 instanceof TilePedestal) {
                return ((TilePedestal)te1).getSyncedStackInSlot(0).isEmpty() != ((TilePedestal)te2).getSyncedStackInSlot(0).isEmpty();
            }
        }
        else if (te1 != null && te2 != null && te1 instanceof TilePedestal && te2 instanceof TilePedestal) {
            return ((TilePedestal)te1).getStackInSlot(0).isEmpty() != ((TilePedestal)te2).getStackInSlot(0).isEmpty();
        }
        return false;
    }
    
    @Override
    public float getSymmetryPenalty(final World world, final BlockPos pos) {
        return 0.1f;
    }
}
