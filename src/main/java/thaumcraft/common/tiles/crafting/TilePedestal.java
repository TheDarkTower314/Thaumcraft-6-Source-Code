// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.blocks.devices.BlockInlay;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TilePedestal extends TileThaumcraftInventory
{
    public TilePedestal() {
        super(1);
        this.syncedSlots = new int[] { 0 };
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 2, this.getPos().getZ() + 1);
    }
    
    @Override
    public boolean isItemValidForSlot(final int par1, final ItemStack stack2) {
        return stack2.isEmpty() || this.getStackInSlot(par1).isEmpty();
    }
    
    public void setInventorySlotContentsFromInfusion(final int par1, final ItemStack stack2) {
        this.setInventorySlotContents(par1, stack2);
        this.markDirty();
        if (!this.world.isRemote) {
            this.syncTile(false);
        }
    }
    
    public BlockPos findInstabilityMitigator() {
        if (this.getBlockMetadata() > 0) {
            final BlockPos pp = this.seekSourceRecursive(this.pos, this.getBlockMetadata());
            if (pp != null) {
                return pp;
            }
        }
        return null;
    }
    
    private BlockPos seekSourceRecursive(final BlockPos pos, final int lastCharge) {
        for (final EnumFacing face : EnumFacing.HORIZONTALS) {
            final BlockPos pp = pos.offset(face);
            final int ss = BlockInlay.getSourceStrengthAt(this.world, pp);
            if (ss >= 5) {
                return pp;
            }
            final IBlockState bs = this.world.getBlockState(pp);
            if (bs.getProperties().containsKey(BlockInlay.CHARGE)) {
                final int charge = (int)bs.getValue((IProperty)BlockInlay.CHARGE);
                if (charge > lastCharge) {
                    final BlockPos ob = this.seekSourceRecursive(pp, charge);
                    if (ob != null) {
                        return ob;
                    }
                }
            }
        }
        return null;
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 11) {
            if (this.world.isRemote) {
                FXDispatcher.INSTANCE.drawBamf(this.pos.up(), 0.75f, 0.0f, 0.5f, true, true, null);
            }
            return true;
        }
        if (i == 12) {
            if (this.world.isRemote) {
                FXDispatcher.INSTANCE.drawBamf(this.pos.up(), true, true, null);
            }
            return true;
        }
        if (i == 5) {
            if (this.world.isRemote) {
                FXDispatcher.INSTANCE.drawPedestalShield(this.pos);
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
}
