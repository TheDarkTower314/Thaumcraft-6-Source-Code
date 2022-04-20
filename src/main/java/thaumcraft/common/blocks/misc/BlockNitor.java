// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.misc;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.tiles.misc.TileNitor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.block.ITileEntityProvider;
import thaumcraft.common.blocks.BlockTC;

public class BlockNitor extends BlockTC implements ITileEntityProvider
{
    public final EnumDyeColor dye;
    
    public BlockNitor(final String name, final EnumDyeColor dye) {
        super(Material.CIRCUITS, name);
        this.setHardness(0.1f);
        this.setSoundType(SoundType.CLOTH);
        this.setLightLevel(1.0f);
        this.dye = dye;
    }
    
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileNitor();
    }
    
    public boolean hasTileEntity(final IBlockState state) {
        return true;
    }
    
    public MapColor getMapColor(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return MapColor.getBlockColor(this.dye);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.33000001311302185, 0.33000001311302185, 0.33000001311302185, 0.6600000262260437, 0.6600000262260437, 0.6600000262260437);
    }
    
    public AxisAlignedBB getCollisionBoundingBox(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return null;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
}
