package thaumcraft.common.blocks.misc;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.tiles.misc.TileBarrierStone;


public class BlockBarrier extends Block
{
    public static Material barrierMat;
    
    public BlockBarrier() {
        super(BlockBarrier.barrierMat);
        setCreativeTab(null);
        setLightOpacity(0);
        setUnlocalizedName("barrier");
        setRegistryName("thaumcraft", "barrier");
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    }
    
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return ItemStack.EMPTY;
    }
    
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing o) {
        return false;
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
    
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List list, Entity collidingEntity, boolean isActualState) {
        if (collidingEntity != null && collidingEntity instanceof EntityLivingBase && !(collidingEntity instanceof EntityPlayer) && collidingEntity.getRecursivePassengersByType(EntityPlayer.class).isEmpty()) {
            int a = 1;
            if (world.getBlockState(pos.down(a)).getBlock() != BlocksTC.pavingStoneBarrier) {
                ++a;
            }
            if (world.isBlockIndirectlyGettingPowered(pos.down(a)) == 0) {
                list.add(BlockBarrier.FULL_BLOCK_AABB.offset(pos));
            }
        }
    }
    
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos pos2) {
        if (world.getBlockState(pos.down(1)) != BlocksTC.pavingStoneBarrier.getDefaultState() && world.getBlockState(pos.down(1)) != getDefaultState()) {
            world.setBlockToAir(pos);
        }
    }
    
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        for (int a = 1; a < 3; ++a) {
            TileEntity te = worldIn.getTileEntity(pos.down(a));
            if (te != null && te instanceof TileBarrierStone) {
                return te.getWorld().isBlockIndirectlyGettingPowered(pos.down(a)) > 0;
            }
        }
        return true;
    }
    
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemById(0);
    }
    
    public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
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
