package thaumcraft.common.blocks.misc;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.misc.TileNitor;


public class BlockNitor extends BlockTC implements ITileEntityProvider
{
    public EnumDyeColor dye;
    
    public BlockNitor(String name, EnumDyeColor dye) {
        super(Material.CIRCUITS, name);
        setHardness(0.1f);
        setSoundType(SoundType.CLOTH);
        setLightLevel(1.0f);
        this.dye = dye;
    }
    
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileNitor();
    }
    
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.getBlockColor(dye);
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.33000001311302185, 0.33000001311302185, 0.33000001311302185, 0.6600000262260437, 0.6600000262260437, 0.6600000262260437);
    }
    
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
