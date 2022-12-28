package thaumcraft.common.tiles.crafting;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.devices.BlockInlay;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TilePedestal extends TileThaumcraftInventory
{
    public TilePedestal() {
        super(1);
        syncedSlots = new int[] { 0 };
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 2, getPos().getZ() + 1);
    }
    
    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack2) {
        return stack2.isEmpty() || getStackInSlot(par1).isEmpty();
    }
    
    public void setInventorySlotContentsFromInfusion(int par1, ItemStack stack2) {
        setInventorySlotContents(par1, stack2);
        markDirty();
        if (!world.isRemote) {
            syncTile(false);
        }
    }
    
    public BlockPos findInstabilityMitigator() {
        if (getBlockMetadata() > 0) {
            BlockPos pp = seekSourceRecursive(pos, getBlockMetadata());
            if (pp != null) {
                return pp;
            }
        }
        return null;
    }
    
    private BlockPos seekSourceRecursive(BlockPos pos, int lastCharge) {
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos pp = pos.offset(face);
            int ss = BlockInlay.getSourceStrengthAt(world, pp);
            if (ss >= 5) {
                return pp;
            }
            IBlockState bs = world.getBlockState(pp);
            if (bs.getProperties().containsKey(BlockInlay.CHARGE)) {
                int charge = (int)bs.getValue((IProperty)BlockInlay.CHARGE);
                if (charge > lastCharge) {
                    BlockPos ob = seekSourceRecursive(pp, charge);
                    if (ob != null) {
                        return ob;
                    }
                }
            }
        }
        return null;
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 11) {
            if (world.isRemote) {
                FXDispatcher.INSTANCE.drawBamf(pos.up(), 0.75f, 0.0f, 0.5f, true, true, null);
            }
            return true;
        }
        if (i == 12) {
            if (world.isRemote) {
                FXDispatcher.INSTANCE.drawBamf(pos.up(), true, true, null);
            }
            return true;
        }
        if (i == 5) {
            if (world.isRemote) {
                FXDispatcher.INSTANCE.drawPedestalShield(pos);
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
}
