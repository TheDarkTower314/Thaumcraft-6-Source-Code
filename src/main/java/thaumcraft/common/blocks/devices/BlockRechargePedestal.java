// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.IRechargable;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.devices.TileRechargePedestal;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockRechargePedestal extends BlockTCDevice
{
    public BlockRechargePedestal() {
        super(Material.ROCK, TileRechargePedestal.class, "recharge_pedestal");
        this.setSoundType(SoundType.STONE);
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (world.isRemote) {
            return true;
        }
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileRechargePedestal) {
            final TileRechargePedestal ped = (TileRechargePedestal)tile;
            if (ped.getStackInSlot(0).isEmpty() && player.inventory.getCurrentItem().getItem() instanceof IRechargable) {
                final ItemStack i = player.getHeldItem(hand).copy();
                i.setCount(1);
                ped.setInventorySlotContents(0, i);
                player.getHeldItem(hand).shrink(1);
                if (player.getHeldItem(hand).getCount() == 0) {
                    player.setHeldItem(hand, ItemStack.EMPTY);
                }
                player.inventory.markDirty();
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 1.6f);
                return true;
            }
            if (!ped.getStackInSlot(0).isEmpty()) {
                InventoryUtils.dropItemsAtEntity(world, pos, player);
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 1.5f);
                return true;
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
}
