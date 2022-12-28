package thaumcraft.common.blocks.devices;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.devices.TileRechargePedestal;


public class BlockRechargePedestal extends BlockTCDevice
{
    public BlockRechargePedestal() {
        super(Material.ROCK, TileRechargePedestal.class, "recharge_pedestal");
        setSoundType(SoundType.STONE);
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileRechargePedestal) {
            TileRechargePedestal ped = (TileRechargePedestal)tile;
            if (ped.getStackInSlot(0).isEmpty() && player.inventory.getCurrentItem().getItem() instanceof IRechargable) {
                ItemStack i = player.getHeldItem(hand).copy();
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
