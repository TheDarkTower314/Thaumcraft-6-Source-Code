// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;
import thaumcraft.common.tiles.devices.TileMirror;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockMirror extends BlockTCDevice implements IBlockFacing
{
    public BlockMirror(final Class cls, final String name) {
        super(Material.IRON, cls, name);
        setSoundType(SoundsTC.JAR);
        setHardness(0.1f);
        setHarvestLevel(null, 0);
        final IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        setDefaultState(bs);
    }
    
    public SoundType getSoundType() {
        return SoundsTC.JAR;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    public boolean canHarvestBlock(final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return true;
    }
    
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
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
    
    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)facing);
        return bs;
    }
    
    @Override
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
    }
    
    @Override
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos pos2) {
        final EnumFacing d = BlockStateUtils.getFacing(state);
        if (!worldIn.getBlockState(pos.offset(d.getOpposite())).isSideSolid(worldIn, pos.offset(d.getOpposite()), d)) {
            dropBlockAsItem(worldIn, pos, getDefaultState(), 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        final EnumFacing facing = BlockStateUtils.getFacing(state);
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
    
    public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
        return worldIn.getBlockState(pos.offset(side.getOpposite())).isSideSolid(worldIn, pos.offset(side.getOpposite()), side);
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        return !world.isRemote || true;
    }
    
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        final TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileMirror || te instanceof TileMirrorEssentia) {
            dropMirror(worldIn, pos, state, te);
        }
        else {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }
    
    public void harvestBlock(final World worldIn, final EntityPlayer player, final BlockPos pos, final IBlockState state, final TileEntity te, final ItemStack stack) {
        if (te instanceof TileMirror || te instanceof TileMirrorEssentia) {
            dropMirror(worldIn, pos, state, te);
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }
    
    public void dropMirror(final World world, final BlockPos pos, final IBlockState state, final TileEntity te) {
        if (tileClass == TileMirror.class) {
            final TileMirror tm = (TileMirror)te;
            final ItemStack drop = new ItemStack(this, 1, 0);
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
            final TileMirrorEssentia tm2 = (TileMirrorEssentia)te;
            final ItemStack drop = new ItemStack(this, 1, 0);
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
    
    public void onEntityCollidedWithBlock(final World world, final BlockPos pos, final IBlockState state, final Entity entity) {
        if (!world.isRemote && tileClass == TileMirror.class && entity instanceof EntityItem && !entity.isDead && entity.timeUntilPortal == 0) {
            final TileMirror taf = (TileMirror)world.getTileEntity(pos);
            if (taf != null) {
                taf.transport((EntityItem)entity);
            }
        }
        super.onEntityCollidedWithBlock(world, pos, state, entity);
    }
}
