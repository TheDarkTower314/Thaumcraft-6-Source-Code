// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.basic;

import net.minecraft.util.EnumParticleTypes;
import java.util.Random;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.blocks.BlockTC;

public class BlockCandle extends BlockTC implements IInfusionStabiliserExt
{
    public final EnumDyeColor dye;
    
    public BlockCandle(final String name, final EnumDyeColor dye) {
        super(Material.CIRCUITS, name);
        this.setHardness(0.1f);
        this.setSoundType(SoundType.CLOTH);
        this.setLightLevel(0.9375f);
        this.dye = dye;
    }
    
    public MapColor getMapColor(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return MapColor.getBlockColor(this.dye);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean canPlaceBlockAt(final World par1World, final BlockPos pos) {
        return par1World.isSideSolid(pos, EnumFacing.UP);
    }
    
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos pos2) {
        if (!this.canPlaceBlockAt(worldIn, pos.down())) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    public boolean canPlaceBlockOnSide(final World par1World, final BlockPos pos, final EnumFacing par5) {
        return this.canPlaceBlockAt(par1World, pos.down());
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 0.5, 0.625);
    }
    
    public boolean isSideSolid(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing side) {
        return false;
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
    
    public void randomDisplayTick(final IBlockState state, final World par1World, final BlockPos pos, final Random par5Random) {
        final double var7 = pos.getX() + 0.5f;
        final double var8 = pos.getY() + 0.7f;
        final double var9 = pos.getZ() + 0.5f;
        par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var7, var8, var9, 0.0, 0.0, 0.0, new int[0]);
        par1World.spawnParticle(EnumParticleTypes.FLAME, var7, var8, var9, 0.0, 0.0, 0.0, new int[0]);
    }
    
    public boolean canStabaliseInfusion(final World world, final BlockPos pos) {
        return true;
    }
    
    @Override
    public float getStabilizationAmount(final World world, final BlockPos pos) {
        return 0.1f;
    }
    
    @Override
    public boolean hasSymmetryPenalty(final World world, final BlockPos pos1, final BlockPos pos2) {
        return false;
    }
    
    @Override
    public float getSymmetryPenalty(final World world, final BlockPos pos) {
        return 0.0f;
    }
}
