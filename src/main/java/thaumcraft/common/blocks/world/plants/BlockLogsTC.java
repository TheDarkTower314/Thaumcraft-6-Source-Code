// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world.plants;

import java.util.Iterator;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import com.google.common.collect.UnmodifiableIterator;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import thaumcraft.common.blocks.BlockTC;

public class BlockLogsTC extends BlockTC
{
    public static final PropertyEnum AXIS;
    
    public BlockLogsTC(final String name) {
        super(Material.WOOD, name);
        setHarvestLevel("axe", 0);
        setHardness(2.0f);
        setResistance(5.0f);
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(BlockLogsTC.AXIS, (Comparable)EnumFacing.Axis.Y));
    }
    
    public IBlockState getStateForPlacement(final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final int metadata, final EntityLivingBase entity) {
        return super.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, metadata, entity).withProperty(BlockLogsTC.AXIS, (Comparable)side.getAxis());
    }
    
    public int getLightValue(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return (state.getBlock() == BlocksTC.logSilverwood) ? 5 : super.getLightValue(state, world, pos);
    }
    
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing axis) {
        final IBlockState state = world.getBlockState(pos);
        for (final IProperty<?> prop : state.getProperties().keySet()) {
            if (prop.getName().equals("axis")) {
                world.setBlockState(pos, state.cycleProperty((IProperty)prop));
                return true;
            }
        }
        return false;
    }
    
    protected ItemStack getSilkTouchDrop(final IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this));
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        final int axis = meta % 3;
        return getDefaultState().withProperty(BlockLogsTC.AXIS, (Comparable)EnumFacing.Axis.values()[axis]);
    }
    
    public int getMetaFromState(final IBlockState state) {
        return ((EnumFacing.Axis)state.getValue(BlockLogsTC.AXIS)).ordinal();
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockLogsTC.AXIS);
    }
    
    public boolean canSustainLeaves(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return true;
    }
    
    public boolean isWood(final IBlockAccess world, final BlockPos pos) {
        return true;
    }
    
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final byte b0 = 4;
        final int i = b0 + 1;
        if (worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
            for (final BlockPos blockpos1 : BlockPos.getAllInBox(pos.add(-b0, -b0, -b0), pos.add(b0, b0, b0))) {
                final IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);
                if (iblockstate1.getBlock().isLeaves(iblockstate1, worldIn, blockpos1)) {
                    iblockstate1.getBlock().beginLeavesDecay(iblockstate1, worldIn, blockpos1);
                }
            }
        }
    }
    
    public int getFlammability(final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
        return 5;
    }
    
    public int getFireSpreadSpeed(final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
        return 5;
    }
    
    static {
        AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
    }
}
