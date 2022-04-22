// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import thaumcraft.common.lib.utils.Utils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.BlockStateUtils;
import java.util.Random;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.properties.IProperty;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.devices.TileVisGenerator;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockVisGenerator extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    public BlockVisGenerator() {
        super(Material.WOOD, TileVisGenerator.class, "vis_generator");
        setSoundType(SoundType.WOOD);
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        for (final EnumFacing face : EnumFacing.VALUES) {
            final TileEntity tileentity = worldIn.getTileEntity(pos.offset(face));
            if (tileentity != null && tileentity.hasCapability(CapabilityEnergy.ENERGY, face.getOpposite())) {
                final IEnergyStorage capability = tileentity.getCapability(CapabilityEnergy.ENERGY, face.getOpposite());
                if (capability.canReceive()) {
                    IBlockState bs = getDefaultState();
                    bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)face);
                    bs = bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)true);
                    return bs;
                }
            }
        }
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World world, final BlockPos pos, final Random rand) {
        final Block block = state.getBlock();
        if (block.hasTileEntity(state)) {
            final TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity != null) {
                final EnumFacing face = BlockStateUtils.getFacing(state);
                if (tileentity.hasCapability(CapabilityEnergy.ENERGY, face)) {
                    final IEnergyStorage capability = tileentity.getCapability(CapabilityEnergy.ENERGY, face);
                    if (capability.getEnergyStored() > 0) {
                        final double x = (face.getFrontOffsetX() == 0) ? (rand.nextGaussian() * 0.1) : (face.getFrontOffsetX() * 0.1);
                        final double y = (face.getFrontOffsetY() == 0) ? (rand.nextGaussian() * 0.1) : (face.getFrontOffsetY() * 0.1);
                        final double z = (face.getFrontOffsetZ() == 0) ? (rand.nextGaussian() * 0.1) : (face.getFrontOffsetZ() * 0.1);
                        FXDispatcher.INSTANCE.spark(pos.getX() + 0.5 + x, pos.getY() + 0.5 + y, pos.getZ() + 0.5 + z, 0.66f + rand.nextFloat(), 0.65f + rand.nextFloat() * 0.1f, 1.0f, 1.0f, 0.8f);
                    }
                }
            }
        }
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return Utils.rotateBlockAABB(new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.875, 0.75), BlockStateUtils.getFacing(getMetaFromState(state)));
    }
}
