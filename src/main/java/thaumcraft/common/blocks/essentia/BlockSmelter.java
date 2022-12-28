package thaumcraft.common.blocks.essentia;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.essentia.TileSmelter;


public class BlockSmelter extends BlockTCDevice implements IBlockEnabled, IBlockFacingHorizontal
{
    public BlockSmelter(String name) {
        super(Material.IRON, TileSmelter.class, name);
        setSoundType(SoundType.METAL);
        IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)EnumFacing.NORTH);
        bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false);
        setDefaultState(bs);
    }
    
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)placer.getHorizontalFacing().getOpposite());
        bs = bs.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false);
        return bs;
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileSmelter) {
            ((TileSmelter)te).checkNeighbours();
        }
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && !player.isSneaking()) {
            player.openGui(Thaumcraft.instance, 9, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BlockStateUtils.isEnabled(world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos))) ? 13 : super.getLightValue(state, world, pos);
    }
    
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof IInventory) {
            return Container.calcRedstoneFromInventory((IInventory)te);
        }
        return 0;
    }
    
    public static void setFurnaceState(World world, BlockPos pos, boolean state) {
        if (state == BlockStateUtils.isEnabled(world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)))) {
            return;
        }
        TileEntity tileentity = world.getTileEntity(pos);
        BlockSmelter.keepInventory = true;
        world.setBlockState(pos, world.getBlockState(pos).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)state), 3);
        world.setBlockState(pos, world.getBlockState(pos).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)state), 3);
        if (tileentity != null) {
            tileentity.validate();
            world.setTileEntity(pos, tileentity);
        }
        BlockSmelter.keepInventory = false;
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileSmelter && !worldIn.isRemote && ((TileSmelter)tileentity).vis > 0) {
            int ess = ((TileSmelter)tileentity).vis;
            AuraHelper.polluteAura(worldIn, pos, (float)ess, true);
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World w, BlockPos pos, Random r) {
        if (BlockStateUtils.isEnabled(state)) {
            float f = pos.getX() + 0.5f;
            float f2 = pos.getY() + 0.2f + r.nextFloat() * 5.0f / 16.0f;
            float f3 = pos.getZ() + 0.5f;
            float f4 = 0.52f;
            float f5 = r.nextFloat() * 0.5f - 0.25f;
            if (BlockStateUtils.getFacing(state) == EnumFacing.WEST) {
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f - f4, f2, f3 + f5, 0.0, 0.0, 0.0);
                w.spawnParticle(EnumParticleTypes.FLAME, f - f4, f2, f3 + f5, 0.0, 0.0, 0.0);
            }
            if (BlockStateUtils.getFacing(state) == EnumFacing.EAST) {
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f4, f2, f3 + f5, 0.0, 0.0, 0.0);
                w.spawnParticle(EnumParticleTypes.FLAME, f + f4, f2, f3 + f5, 0.0, 0.0, 0.0);
            }
            if (BlockStateUtils.getFacing(state) == EnumFacing.NORTH) {
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f5, f2, f3 - f4, 0.0, 0.0, 0.0);
                w.spawnParticle(EnumParticleTypes.FLAME, f + f5, f2, f3 - f4, 0.0, 0.0, 0.0);
            }
            if (BlockStateUtils.getFacing(state) == EnumFacing.SOUTH) {
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f5, f2, f3 + f4, 0.0, 0.0, 0.0);
                w.spawnParticle(EnumParticleTypes.FLAME, f + f5, f2, f3 + f4, 0.0, 0.0, 0.0);
            }
        }
    }
}
