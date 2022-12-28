package thaumcraft.common.blocks.world.plants;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;


public class BlockLogsTC extends BlockTC
{
    public static PropertyEnum AXIS;
    
    public BlockLogsTC(String name) {
        super(Material.WOOD, name);
        setHarvestLevel("axe", 0);
        setHardness(2.0f);
        setResistance(5.0f);
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(BlockLogsTC.AXIS, (Comparable)EnumFacing.Axis.Y));
    }
    
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int metadata, EntityLivingBase entity) {
        return super.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, metadata, entity).withProperty(BlockLogsTC.AXIS, (Comparable)side.getAxis());
    }
    
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return (state.getBlock() == BlocksTC.logSilverwood) ? 5 : super.getLightValue(state, world, pos);
    }
    
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        IBlockState state = world.getBlockState(pos);
        for (IProperty<?> prop : state.getProperties().keySet()) {
            if (prop.getName().equals("axis")) {
                world.setBlockState(pos, state.cycleProperty((IProperty)prop));
                return true;
            }
        }
        return false;
    }
    
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this));
    }
    
    public IBlockState getStateFromMeta(int meta) {
        int axis = meta % 3;
        return getDefaultState().withProperty(BlockLogsTC.AXIS, (Comparable)EnumFacing.Axis.values()[axis]);
    }
    
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing.Axis)state.getValue(BlockLogsTC.AXIS)).ordinal();
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockLogsTC.AXIS);
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
    
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 5;
    }
    
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 5;
    }
    
    static {
        AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
    }
}
