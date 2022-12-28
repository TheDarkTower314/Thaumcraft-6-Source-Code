package thaumcraft.common.blocks.devices;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;


public class BlockMirror extends BlockTCDevice implements IBlockFacing
{
    public BlockMirror(Class cls, String name) {
        super(Material.IRON, cls, name);
        setSoundType(SoundsTC.JAR);
        setHardness(0.1f);
        setHarvestLevel(null, 0);
        IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        setDefaultState(bs);
    }
    
    public SoundType getSoundType() {
        return SoundsTC.JAR;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)facing);
        return bs;
    }
    
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        EnumFacing d = BlockStateUtils.getFacing(state);
        if (!worldIn.getBlockState(pos.offset(d.getOpposite())).isSideSolid(worldIn, pos.offset(d.getOpposite()), d)) {
            dropBlockAsItem(worldIn, pos, getDefaultState(), 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = BlockStateUtils.getFacing(state);
        switch (facing.ordinal()) {
            default: {
                return new AxisAlignedBB(0.0, 0.875, 0.0, 1.0, 1.0, 1.0);
            }
            case 1: {
                return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
            }
            case 2: {
                return new AxisAlignedBB(0.0, 0.0, 0.875, 1.0, 1.0, 1.0);
            }
            case 3: {
                return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.125);
            }
            case 4: {
                return new AxisAlignedBB(0.875, 0.0, 0.0, 1.0, 1.0, 1.0);
            }
            case 5: {
                return new AxisAlignedBB(0.0, 0.0, 0.0, 0.125, 1.0, 1.0);
            }
        }
    }
    
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return worldIn.getBlockState(pos.offset(side.getOpposite())).isSideSolid(worldIn, pos.offset(side.getOpposite()), side);
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return !world.isRemote || true;
    }
    
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileMirror || te instanceof TileMirrorEssentia) {
            dropMirror(worldIn, pos, state, te);
        }
        else {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }
    
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (te instanceof TileMirror || te instanceof TileMirrorEssentia) {
            dropMirror(worldIn, pos, state, te);
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }
    
    public void dropMirror(World world, BlockPos pos, IBlockState state, TileEntity te) {
        if (tileClass == TileMirror.class) {
            TileMirror tm = (TileMirror)te;
            ItemStack drop = new ItemStack(this, 1, 0);
            if (tm != null && tm instanceof TileMirror) {
                if (tm.linked) {
                    drop.setItemDamage(1);
                    drop.setTagInfo("linkX", new NBTTagInt(tm.linkX));
                    drop.setTagInfo("linkY", new NBTTagInt(tm.linkY));
                    drop.setTagInfo("linkZ", new NBTTagInt(tm.linkZ));
                    drop.setTagInfo("linkDim", new NBTTagInt(tm.linkDim));
                    tm.invalidateLink();
                }
                spawnAsEntity(world, pos, drop);
            }
        }
        else {
            TileMirrorEssentia tm2 = (TileMirrorEssentia)te;
            ItemStack drop = new ItemStack(this, 1, 0);
            if (tm2 != null && tm2 instanceof TileMirrorEssentia) {
                if (tm2.linked) {
                    drop.setItemDamage(1);
                    drop.setTagInfo("linkX", new NBTTagInt(tm2.linkX));
                    drop.setTagInfo("linkY", new NBTTagInt(tm2.linkY));
                    drop.setTagInfo("linkZ", new NBTTagInt(tm2.linkZ));
                    drop.setTagInfo("linkDim", new NBTTagInt(tm2.linkDim));
                    tm2.invalidateLink();
                }
                spawnAsEntity(world, pos, drop);
            }
        }
    }
    
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote && tileClass == TileMirror.class && entity instanceof EntityItem && !entity.isDead && entity.timeUntilPortal == 0) {
            TileMirror taf = (TileMirror)world.getTileEntity(pos);
            if (taf != null) {
                taf.transport((EntityItem)entity);
            }
        }
        super.onEntityCollidedWithBlock(world, pos, state, entity);
    }
}
