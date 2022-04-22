// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.crafting;

import thaumcraft.Thaumcraft;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTC;

public class BlockArcaneWorkbenchCharger extends BlockTC
{
    public BlockArcaneWorkbenchCharger() {
        super(Material.WOOD, "arcane_workbench_charger");
        setSoundType(SoundType.WOOD);
        setHardness(1.25f);
        setResistance(10.0f);
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && (worldIn.getBlockState(pos.down()).getBlock() == BlocksTC.arcaneWorkbench || worldIn.getBlockState(pos.down()).getBlock() == BlocksTC.wandWorkbench);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        final TileEntity te = worldIn.getTileEntity(pos.down());
        if (te != null && te instanceof TileArcaneWorkbench) {
            ((TileArcaneWorkbench)te).syncTile(true);
        }
        if (te != null && te instanceof TileFocalManipulator) {
            ((TileFocalManipulator)te).syncTile(true);
        }
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }
    
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos pos2) {
        if (worldIn.getBlockState(pos.down()).getBlock() != BlocksTC.arcaneWorkbench && worldIn.getBlockState(pos.down()).getBlock() != BlocksTC.wandWorkbench) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (world.isRemote) {
            return true;
        }
        if (world.getBlockState(pos.down()).getBlock() == BlocksTC.arcaneWorkbench) {
            player.openGui(Thaumcraft.instance, 13, world, pos.getX(), pos.down().getY(), pos.getZ());
        }
        if (world.getBlockState(pos.down()).getBlock() == BlocksTC.wandWorkbench) {
            player.openGui(Thaumcraft.instance, 7, world, pos.getX(), pos.down().getY(), pos.getZ());
        }
        return true;
    }
}
