// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.misc;

import net.minecraft.block.material.MapColor;
import net.minecraft.item.Item;
import java.util.Random;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.misc.TileBarrierStone;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;

public class BlockBarrier extends Block
{
    public static final Material barrierMat;
    
    public BlockBarrier() {
        super(BlockBarrier.barrierMat);
        this.setCreativeTab(null);
        this.setLightOpacity(0);
        this.setUnlocalizedName("barrier");
        this.setRegistryName("thaumcraft", "barrier");
    }
    
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public void getSubBlocks(final CreativeTabs tab, final NonNullList<ItemStack> list) {
    }
    
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
        return ItemStack.EMPTY;
    }
    
    public boolean isSideSolid(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing o) {
        return false;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
    
    public void addCollisionBoxToList(final IBlockState state, final World world, final BlockPos pos, final AxisAlignedBB mask, final List list, final Entity collidingEntity, final boolean isActualState) {
        if (collidingEntity != null && collidingEntity instanceof EntityLivingBase && !(collidingEntity instanceof EntityPlayer) && collidingEntity.getRecursivePassengersByType((Class)EntityPlayer.class).isEmpty()) {
            int a = 1;
            if (world.getBlockState(pos.down(a)).getBlock() != BlocksTC.pavingStoneBarrier) {
                ++a;
            }
            if (world.isBlockIndirectlyGettingPowered(pos.down(a)) == 0) {
                list.add(BlockBarrier.FULL_BLOCK_AABB.offset(pos));
            }
        }
    }
    
    public void neighborChanged(final IBlockState state, final World world, final BlockPos pos, final Block neighborBlock, final BlockPos pos2) {
        if (world.getBlockState(pos.down(1)) != BlocksTC.pavingStoneBarrier.getDefaultState() && world.getBlockState(pos.down(1)) != this.getDefaultState()) {
            world.setBlockToAir(pos);
        }
    }
    
    public boolean isPassable(final IBlockAccess worldIn, final BlockPos pos) {
        for (int a = 1; a < 3; ++a) {
            final TileEntity te = worldIn.getTileEntity(pos.down(a));
            if (te != null && te instanceof TileBarrierStone) {
                return te.getWorld().isBlockIndirectlyGettingPowered(pos.down(a)) > 0;
            }
        }
        return true;
    }
    
    public boolean isReplaceable(final IBlockAccess worldIn, final BlockPos pos) {
        return true;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemById(0);
    }
    
    public boolean isAir(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return false;
    }
    
    static {
        barrierMat = new MaterialBarrier();
    }
    
    private static class MaterialBarrier extends Material
    {
        public MaterialBarrier() {
            super(MapColor.AIR);
        }
        
        public boolean blocksMovement() {
            return true;
        }
        
        public boolean isSolid() {
            return false;
        }
        
        public boolean blocksLight() {
            return false;
        }
    }
}
