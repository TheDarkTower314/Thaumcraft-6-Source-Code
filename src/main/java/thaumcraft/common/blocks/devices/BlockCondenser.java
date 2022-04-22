package thaumcraft.common.blocks.devices;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.devices.TileCondenser;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockCondenser extends BlockTCDevice implements IBlockEnabled
{
    public BlockCondenser() {
        super(Material.IRON, TileCondenser.class, "condenser");
        setSoundType(SoundType.METAL);
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
}
