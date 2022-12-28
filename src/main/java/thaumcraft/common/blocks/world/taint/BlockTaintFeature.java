package thaumcraft.common.blocks.world.taint;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class BlockTaintFeature extends BlockTC implements ITaintBlock
{
    public BlockTaintFeature() {
        super(ThaumcraftMaterials.MATERIAL_TAINT, "taint_feature");
        setHardness(0.1f);
        setLightLevel(0.625f);
        IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        setDefaultState(bs);
        setTickRandomly(true);
    }
    
    protected boolean canSilkHarvest() {
        return false;
    }
    
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            if (worldIn.rand.nextFloat() < 0.333f) {
                Entity e = new EntityTaintCrawler(worldIn);
                e.setLocationAndAngles(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, (float)worldIn.rand.nextInt(360), 0.0f);
                worldIn.spawnEntity(e);
            }
            else {
                AuraHelper.polluteAura(worldIn, pos, 1.0f, true);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    public void die(World world, BlockPos pos, IBlockState blockState) {
        world.setBlockState(pos, BlocksTC.fluxGoo.getDefaultState());
    }
    
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote) {
            if (!TaintHelper.isNearTaintSeed(world, pos) && random.nextInt(10) == 0) {
                die(world, pos, state);
                return;
            }
            TaintHelper.spreadFibres(world, pos);
            if (world.getBlockState(pos.down()).getBlock() == BlocksTC.taintLog && world.getBlockState(pos.down()).getValue(BlockTaintLog.AXIS) == EnumFacing.Axis.Y && world.rand.nextInt(100) == 0) {
                world.setBlockState(pos, BlocksTC.taintGeyser.getDefaultState());
            }
        }
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemById(0);
    }
    
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }
    
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
        return 200;
    }
    
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        if (!worldIn.isRemote && !worldIn.getBlockState(pos.offset(BlockStateUtils.getFacing(state).getOpposite())).isSideSolid(worldIn, pos.offset(BlockStateUtils.getFacing(state).getOpposite()), BlockStateUtils.getFacing(state))) {
            worldIn.setBlockToAir(pos);
        }
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)facing);
        return bs;
    }
    
    public IBlockState getStateFromMeta(int meta) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)BlockStateUtils.getFacing(meta));
        return bs;
    }
    
    public int getMetaFromState(IBlockState state) {
        byte b0 = 0;
        int i = b0 | ((EnumFacing)state.getValue((IProperty)IBlockFacing.FACING)).getIndex();
        return i;
    }
    
    protected BlockStateContainer createBlockState() {
        ArrayList<IProperty> ip = new ArrayList<IProperty>();
        ip.add(IBlockFacing.FACING);
        return new BlockStateContainer(this, ip.toArray(new IProperty[ip.size()]));
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = BlockStateUtils.getFacing(getMetaFromState(state));
        switch (facing.ordinal()) {
            case 0: {
                return new AxisAlignedBB(0.125, 0.625, 0.125, 0.875, 1.0, 0.875);
            }
            case 1: {
                return new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 0.375, 0.875);
            }
            case 2: {
                return new AxisAlignedBB(0.125, 0.125, 0.625, 0.875, 0.875, 1.0);
            }
            case 3: {
                return new AxisAlignedBB(0.125, 0.125, 0.0, 0.875, 0.875, 0.375);
            }
            case 4: {
                return new AxisAlignedBB(0.625, 0.125, 0.125, 1.0, 0.875, 0.875);
            }
            case 5: {
                return new AxisAlignedBB(0.0, 0.125, 0.125, 0.375, 0.875, 0.875);
            }
            default: {
                return super.getBoundingBox(state, source, pos);
            }
        }
    }
}
