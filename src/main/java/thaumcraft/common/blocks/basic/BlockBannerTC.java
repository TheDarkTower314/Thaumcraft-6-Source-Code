// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.basic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.consumables.ItemPhial;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.misc.TileBanner;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.block.ITileEntityProvider;
import thaumcraft.common.blocks.BlockTC;

public class BlockBannerTC extends BlockTC implements ITileEntityProvider
{
    public final EnumDyeColor dye;
    
    public BlockBannerTC(final String name, final EnumDyeColor dye) {
        super(Material.WOOD, name);
        setHardness(1.0f);
        setSoundType(SoundType.WOOD);
        this.dye = dye;
    }
    
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public MapColor getMapColor(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return (dye == null) ? MapColor.RED : MapColor.getBlockColor(dye);
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        final TileEntity tile = source.getTileEntity(pos);
        if (tile != null && tile instanceof TileBanner) {
            if (!((TileBanner)tile).getWall()) {
                return new AxisAlignedBB(0.33000001311302185, 0.0, 0.33000001311302185, 0.6600000262260437, 2.0, 0.6600000262260437);
            }
            switch (((TileBanner)tile).getBannerFacing()) {
                case 0: {
                    return new AxisAlignedBB(0.0, -1.0, 0.0, 1.0, 1.0, 0.25);
                }
                case 8: {
                    return new AxisAlignedBB(0.0, -1.0, 0.75, 1.0, 1.0, 1.0);
                }
                case 12: {
                    return new AxisAlignedBB(0.0, -1.0, 0.0, 0.25, 1.0, 1.0);
                }
                case 4: {
                    return new AxisAlignedBB(0.75, -1.0, 0.0, 1.0, 1.0, 1.0);
                }
            }
        }
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }
    
    public AxisAlignedBB getCollisionBoundingBox(final IBlockState blockState, final IBlockAccess worldIn, final BlockPos pos) {
        return null;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean onBlockActivated(final World w, final BlockPos pos, final IBlockState state, final EntityPlayer p, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (!w.isRemote && (p.isSneaking() || p.getHeldItem(hand).getItem() instanceof ItemPhial)) {
            final TileBanner te = (TileBanner)w.getTileEntity(pos);
            if (te != null && dye != null) {
                if (p.isSneaking()) {
                    te.setAspect(null);
                }
                else if (((IEssentiaContainerItem)p.getHeldItem(hand).getItem()).getAspects(p.getHeldItem(hand)) != null) {
                    te.setAspect(((IEssentiaContainerItem)p.getHeldItem(hand).getItem()).getAspects(p.getHeldItem(hand)).getAspects()[0]);
                    p.getHeldItem(hand).shrink(1);
                }
                w.markAndNotifyBlock(pos, w.getChunkFromBlockCoords(pos), state, state, 3);
                te.markDirty();
                te.syncTile(false);
                w.playSound(null, pos, SoundEvents.BLOCK_CLOTH_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
        return true;
    }
    
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileBanner();
    }
    
    public boolean hasTileEntity(final IBlockState state) {
        return true;
    }
    
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
        final TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileBanner) {
            final ItemStack drop = new ItemStack(this);
            if (dye != null || ((TileBanner)te).getAspect() != null) {
                drop.setTagCompound(new NBTTagCompound());
                if (((TileBanner)te).getAspect() != null) {
                    drop.getTagCompound().setString("aspect", ((TileBanner)te).getAspect().getTag());
                }
            }
            return drop;
        }
        return super.getPickBlock(state, target, world, pos, player);
    }
    
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        final TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileBanner) {
            final ItemStack drop = new ItemStack(this);
            if (dye != null || ((TileBanner)te).getAspect() != null) {
                drop.setTagCompound(new NBTTagCompound());
                if (((TileBanner)te).getAspect() != null) {
                    drop.getTagCompound().setString("aspect", ((TileBanner)te).getAspect().getTag());
                }
            }
            spawnAsEntity(worldIn, pos, drop);
        }
        else {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }
    
    public void harvestBlock(final World worldIn, final EntityPlayer player, final BlockPos pos, final IBlockState state, final TileEntity te, final ItemStack stack) {
        if (te instanceof TileBanner) {
            final ItemStack drop = new ItemStack(this);
            if (dye != null || ((TileBanner)te).getAspect() != null) {
                drop.setTagCompound(new NBTTagCompound());
                if (((TileBanner)te).getAspect() != null) {
                    drop.getTagCompound().setString("aspect", ((TileBanner)te).getAspect().getTag());
                }
            }
            spawnAsEntity(worldIn, pos, drop);
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }
}
