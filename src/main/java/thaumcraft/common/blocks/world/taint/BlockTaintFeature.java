// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world.taint;

import net.minecraft.util.math.AxisAlignedBB;
import java.util.ArrayList;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import java.util.Random;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.common.blocks.BlockTC;

public class BlockTaintFeature extends BlockTC implements ITaintBlock
{
    public BlockTaintFeature() {
        super(ThaumcraftMaterials.MATERIAL_TAINT, "taint_feature");
        this.setHardness(0.1f);
        this.setLightLevel(0.625f);
        final IBlockState bs = this.blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        this.setDefaultState(bs);
        this.setTickRandomly(true);
    }
    
    protected boolean canSilkHarvest() {
        return false;
    }
    
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (!worldIn.isRemote) {
            if (worldIn.rand.nextFloat() < 0.333f) {
                final Entity e = new EntityTaintCrawler(worldIn);
                e.setLocationAndAngles(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, (float)worldIn.rand.nextInt(360), 0.0f);
                worldIn.spawnEntity(e);
            }
            else {
                AuraHelper.polluteAura(worldIn, pos, 1.0f, true);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    public void die(final World world, final BlockPos pos, final IBlockState blockState) {
        world.setBlockState(pos, BlocksTC.fluxGoo.getDefaultState());
    }
    
    public void updateTick(final World world, final BlockPos pos, final IBlockState state, final Random random) {
        if (!world.isRemote) {
            if (!TaintHelper.isNearTaintSeed(world, pos) && random.nextInt(10) == 0) {
                this.die(world, pos, state);
                return;
            }
            TaintHelper.spreadFibres(world, pos);
            if (world.getBlockState(pos.down()).getBlock() == BlocksTC.taintLog && world.getBlockState(pos.down()).getValue(BlockTaintLog.AXIS) == EnumFacing.Axis.Y && world.rand.nextInt(100) == 0) {
                world.setBlockState(pos, BlocksTC.taintGeyser.getDefaultState());
            }
        }
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemById(0);
    }
    
    public boolean canSilkHarvest(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player) {
        return true;
    }
    
    public int getPackedLightmapCoords(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return 200;
    }
    
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos pos2) {
        if (!worldIn.isRemote && !worldIn.getBlockState(pos.offset(BlockStateUtils.getFacing(state).getOpposite())).isSideSolid(worldIn, pos.offset(BlockStateUtils.getFacing(state).getOpposite()), BlockStateUtils.getFacing(state))) {
            worldIn.setBlockToAir(pos);
        }
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState bs = this.getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)facing);
        return bs;
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        IBlockState bs = this.getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)BlockStateUtils.getFacing(meta));
        return bs;
    }
    
    public int getMetaFromState(final IBlockState state) {
        final byte b0 = 0;
        final int i = b0 | ((EnumFacing)state.getValue((IProperty)IBlockFacing.FACING)).getIndex();
        return i;
    }
    
    protected BlockStateContainer createBlockState() {
        final ArrayList<IProperty> ip = new ArrayList<IProperty>();
        ip.add(IBlockFacing.FACING);
        return new BlockStateContainer(this, ip.toArray(new IProperty[ip.size()]));
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        final EnumFacing facing = BlockStateUtils.getFacing(this.getMetaFromState(state));
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
