package thaumcraft.common.blocks.misc;
import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.tiles.misc.TileHole;


public class BlockHole extends BlockContainer
{
    public BlockHole() {
        super(Material.ROCK);
        setUnlocalizedName("hole");
        setRegistryName("thaumcraft", "hole");
        setBlockUnbreakable();
        setResistance(6000000.0f);
        setSoundType(SoundType.CLOTH);
        setLightLevel(0.7f);
        setTickRandomly(true);
        setCreativeTab(null);
    }
    
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return ItemStack.EMPTY;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
    }
    
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing o) {
        return true;
    }
    
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockHole.FULL_BLOCK_AABB;
    }
    
    public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
    
    public boolean isFullCube(IBlockState blockState) {
        return false;
    }
    
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileHole();
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemById(0);
    }
}
