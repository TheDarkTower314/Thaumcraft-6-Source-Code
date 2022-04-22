// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.essentia;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.aura.AuraHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.essentia.TileAlembic;
import net.minecraft.block.material.Material;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.common.blocks.BlockTCTile;

public class BlockAlembic extends BlockTCTile implements ILabelable
{
    public BlockAlembic() {
        super(Material.WOOD, TileAlembic.class, "alembic");
        setSoundType(SoundType.WOOD);
    }
    
    @Override
    public TileEntity createNewTileEntity(final World world, final int metadata) {
        if (metadata == 0) {
            return new TileAlembic();
        }
        return null;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return getStateFromMeta(meta);
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        final TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileAlembic && player.isSneaking() && ((TileAlembic)te).aspectFilter != null && side.ordinal() == ((TileAlembic)te).facing) {
            ((TileAlembic)te).aspectFilter = null;
            ((TileAlembic)te).facing = EnumFacing.DOWN.ordinal();
            te.markDirty();
            world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 3);
            if (world.isRemote) {
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.page, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            }
            else {
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5f + side.getFrontOffsetX() / 3.0f, pos.getY() + 0.5f, pos.getZ() + 0.5f + side.getFrontOffsetZ() / 3.0f, new ItemStack(ItemsTC.label)));
            }
            return true;
        }
        if (te != null && te instanceof TileAlembic && player.isSneaking() && player.getHeldItemMainhand().isEmpty() && (((TileAlembic)te).aspectFilter == null || side.ordinal() != ((TileAlembic)te).facing)) {
            ((TileAlembic)te).aspect = null;
            if (world.isRemote) {
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.jar, SoundCategory.BLOCKS, 0.4f, 1.0f, false);
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.5f, 1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3f, false);
            }
            else {
                AuraHelper.polluteAura(world, pos, (float)((TileAlembic)te).amount, true);
            }
            ((TileAlembic)te).amount = 0;
            te.markDirty();
            world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 3);
        }
        return true;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 1.0, 0.875);
    }
    
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return true;
    }
    
    public int getComparatorInputOverride(final IBlockState state, final World world, final BlockPos pos) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileAlembic) {
            final float r = ((TileAlembic)tile).amount / (float)((TileAlembic)tile).maxAmount;
            return MathHelper.floor(r * 14.0f) + ((((TileAlembic)tile).amount > 0) ? 1 : 0);
        }
        return super.getComparatorInputOverride(state, world, pos);
    }
    
    @Override
    public boolean applyLabel(final EntityPlayer player, final BlockPos pos, final EnumFacing side, final ItemStack labelstack) {
        final TileEntity te = player.world.getTileEntity(pos);
        if (te == null || !(te instanceof TileAlembic) || side.ordinal() <= 1 || ((TileAlembic)te).aspectFilter != null) {
            return false;
        }
        Aspect la = null;
        if (((IEssentiaContainerItem)labelstack.getItem()).getAspects(labelstack) != null) {
            la = ((IEssentiaContainerItem)labelstack.getItem()).getAspects(labelstack).getAspects()[0];
        }
        if (((TileAlembic)te).amount == 0 && la == null) {
            return false;
        }
        Aspect aspect = null;
        if (((TileAlembic)te).amount == 0 && la != null) {
            aspect = la;
        }
        if (((TileAlembic)te).amount > 0) {
            aspect = ((TileAlembic)te).aspect;
        }
        if (aspect == null) {
            return false;
        }
        final IBlockState state = player.world.getBlockState(pos);
        onBlockPlacedBy(player.world, pos, state, player, null);
        ((TileAlembic)te).aspectFilter = aspect;
        ((TileAlembic)te).facing = side.ordinal();
        te.markDirty();
        player.world.markAndNotifyBlock(pos, player.world.getChunkFromBlockCoords(pos), state, state, 3);
        player.world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.page, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return true;
    }
}
