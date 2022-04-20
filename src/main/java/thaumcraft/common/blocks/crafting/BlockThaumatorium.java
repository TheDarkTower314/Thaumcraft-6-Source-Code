// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.crafting;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.block.Block;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.item.Item;
import java.util.Random;
import thaumcraft.Thaumcraft;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.tiles.crafting.TileThaumatoriumTop;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockThaumatorium extends BlockTCDevice implements IBlockFacingHorizontal
{
    boolean top;
    
    public BlockThaumatorium(final boolean top) {
        super(Material.IRON, null, top ? "thaumatorium_top" : "thaumatorium");
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(null);
        this.top = top;
    }
    
    @Override
    public TileEntity createNewTileEntity(final World world, final int metadata) {
        if (!this.top) {
            return new TileThaumatorium();
        }
        if (this.top) {
            return new TileThaumatoriumTop();
        }
        return null;
    }
    
    @Override
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return this.top ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (!world.isRemote && !player.isSneaking()) {
            if (!this.top) {
                player.openGui(Thaumcraft.instance, 3, world, pos.getX(), pos.getY(), pos.getZ());
            }
            else {
                player.openGui(Thaumcraft.instance, 3, world, pos.down().getX(), pos.down().getY(), pos.down().getZ());
            }
        }
        return true;
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemFromBlock(BlocksTC.metalAlchemical);
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (this.top && worldIn.getBlockState(pos.down()).getBlock() == BlocksTC.thaumatorium) {
            worldIn.setBlockState(pos.down(), BlocksTC.metalAlchemical.getDefaultState());
        }
        if (!this.top && worldIn.getBlockState(pos.up()).getBlock() == BlocksTC.thaumatoriumTop) {
            worldIn.setBlockState(pos.up(), BlocksTC.metalAlchemical.getDefaultState());
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    @Override
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos pos2) {
        if (!this.top && worldIn.getBlockState(pos.down()).getBlock() != BlocksTC.crucible) {
            worldIn.setBlockState(pos, BlocksTC.metalAlchemical.getDefaultState());
            if (worldIn.getBlockState(pos.up()).getBlock() == BlocksTC.thaumatoriumTop) {
                worldIn.setBlockState(pos.up(), BlocksTC.metalAlchemical.getDefaultState());
            }
        }
    }
    
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return !this.top;
    }
    
    public int getComparatorInputOverride(final IBlockState state, final World world, final BlockPos pos) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileThaumatorium) {
            return Container.calcRedstoneFromInventory((IInventory)tile);
        }
        return super.getComparatorInputOverride(state, world, pos);
    }
}
