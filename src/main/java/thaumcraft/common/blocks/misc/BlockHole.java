// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.misc;

import net.minecraft.item.Item;
import java.util.Random;
import thaumcraft.common.tiles.misc.TileHole;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.NonNullList;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockContainer;

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
    
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
        return ItemStack.EMPTY;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final CreativeTabs par2CreativeTabs, final NonNullList<ItemStack> par3List) {
    }
    
    public boolean isSideSolid(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing o) {
        return true;
    }
    
    public AxisAlignedBB getCollisionBoundingBox(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return null;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return BlockHole.FULL_BLOCK_AABB;
    }
    
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState blockState, final World worldIn, final BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
    
    public boolean isFullCube(final IBlockState blockState) {
        return false;
    }
    
    public boolean isOpaqueCube(final IBlockState blockState) {
        return false;
    }
    
    public TileEntity createNewTileEntity(final World var1, final int var2) {
        return new TileHole();
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemById(0);
    }
}
