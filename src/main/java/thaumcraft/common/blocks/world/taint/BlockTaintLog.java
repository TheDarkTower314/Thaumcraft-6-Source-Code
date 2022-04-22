// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world.taint;

import java.util.Iterator;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import java.util.Random;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.api.ThaumcraftMaterials;
import net.minecraft.block.properties.PropertyEnum;
import thaumcraft.common.blocks.BlockTC;

public class BlockTaintLog extends BlockTC implements ITaintBlock
{
    public static final PropertyEnum AXIS;
    
    public BlockTaintLog() {
        super(ThaumcraftMaterials.MATERIAL_TAINT, "taint_log");
        this.setHarvestLevel("axe", 0);
        this.setHardness(3.0f);
        this.setResistance(100.0f);
        this.setSoundType(SoundsTC.GORE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockTaintLog.AXIS, (Comparable)EnumFacing.Axis.Y));
        this.setTickRandomly(true);
    }
    
    public SoundType getSoundType() {
        return SoundsTC.GORE;
    }
    
    public int getFlammability(final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
        return 4;
    }
    
    public int getFireSpreadSpeed(final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
        return 4;
    }
    
    @Override
    public void die(final World world, final BlockPos pos, final IBlockState blockState) {
        world.setBlockState(pos, BlocksTC.fluxGoo.getDefaultState());
    }
    
    public void updateTick(final World world, final BlockPos pos, final IBlockState state, final Random random) {
        if (!world.isRemote) {
            if (!TaintHelper.isNearTaintSeed(world, pos)) {
                this.die(world, pos, state);
            }
            else {
                TaintHelper.spreadFibres(world, pos);
            }
        }
    }
    
    public IBlockState getStateForPlacement(final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final int metadata, final EntityLivingBase entity) {
        return super.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, metadata, entity).withProperty(BlockTaintLog.AXIS, (Comparable)side.getAxis());
    }
    
    protected ItemStack getSilkTouchDrop(final IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(state));
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        final int axis = meta % 3;
        return this.getDefaultState().withProperty(BlockTaintLog.AXIS, (Comparable)EnumFacing.Axis.values()[axis]);
    }
    
    public int getMetaFromState(final IBlockState state) {
        return ((EnumFacing.Axis)state.getValue(BlockTaintLog.AXIS)).ordinal();
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockTaintLog.AXIS);
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
    
    static {
        AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
    }
}
