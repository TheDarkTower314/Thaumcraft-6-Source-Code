package thaumcraft.common.blocks.world.taint;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.lib.SoundsTC;


public class BlockTaintLog extends BlockTC implements ITaintBlock
{
    public static PropertyEnum AXIS;
    
    public BlockTaintLog() {
        super(ThaumcraftMaterials.MATERIAL_TAINT, "taint_log");
        setHarvestLevel("axe", 0);
        setHardness(3.0f);
        setResistance(100.0f);
        setSoundType(SoundsTC.GORE);
        setDefaultState(blockState.getBaseState().withProperty(BlockTaintLog.AXIS, (Comparable)EnumFacing.Axis.Y));
        setTickRandomly(true);
    }
    
    public SoundType getSoundType() {
        return SoundsTC.GORE;
    }
    
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 4;
    }
    
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 4;
    }
    
    @Override
    public void die(World world, BlockPos pos, IBlockState blockState) {
        world.setBlockState(pos, BlocksTC.fluxGoo.getDefaultState());
    }
    
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote) {
            if (!TaintHelper.isNearTaintSeed(world, pos)) {
                die(world, pos, state);
            }
            else {
                TaintHelper.spreadFibres(world, pos);
            }
        }
    }
    
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int metadata, EntityLivingBase entity) {
        return super.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, metadata, entity).withProperty(BlockTaintLog.AXIS, (Comparable)side.getAxis());
    }
    
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, damageDropped(state));
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    public IBlockState getStateFromMeta(int meta) {
        int axis = meta % 3;
        return getDefaultState().withProperty(BlockTaintLog.AXIS, (Comparable)EnumFacing.Axis.values()[axis]);
    }
    
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing.Axis)state.getValue(BlockTaintLog.AXIS)).ordinal();
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockTaintLog.AXIS);
    }
    
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }
    
    public boolean isWood(IBlockAccess world, BlockPos pos) {
        return true;
    }
    
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        byte b0 = 4;
        int i = b0 + 1;
        if (worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
            for (BlockPos blockpos1 : BlockPos.getAllInBox(pos.add(-b0, -b0, -b0), pos.add(b0, b0, b0))) {
                IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);
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
