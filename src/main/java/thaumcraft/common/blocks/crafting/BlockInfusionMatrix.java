// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.crafting;

import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockInfusionMatrix extends BlockTCDevice
{
    public BlockInfusionMatrix() {
        super(Material.ROCK, TileInfusionMatrix.class, "infusion_matrix");
        setSoundType(SoundType.STONE);
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
    
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
}
