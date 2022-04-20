// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.items.IRechargable;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.items.RechargeHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileRechargePedestal extends TileThaumcraftInventory implements IAspectContainer
{
    private static final int[] slots;
    int counter;
    
    public TileRechargePedestal() {
        super(1);
        this.counter = 0;
        this.syncedSlots = new int[] { 0 };
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1).grow(2.0, 2.0, 2.0);
    }
    
    @Override
    public void update() {
        super.update();
        if (!this.getWorld().isRemote && this.counter++ % 10 == 0 && this.getStackInSlot(0) != null && RechargeHelper.rechargeItem(this.getWorld(), this.getStackInSlot(0), this.pos, null, 5) > 0.0f) {
            this.syncTile(false);
            this.markDirty();
            final ArrayList<Aspect> al = Aspect.getPrimalAspects();
            this.world.addBlockEvent(this.pos, this.getBlockType(), 5, al.get(this.getWorld().rand.nextInt(al.size())).getColor());
        }
    }
    
    public void setInventorySlotContentsFromInfusion(final int par1, final ItemStack stack2) {
        this.setInventorySlotContents(par1, stack2);
        this.markDirty();
        if (!this.world.isRemote) {
            this.syncTile(false);
        }
    }
    
    @Override
    public boolean isItemValidForSlot(final int par1, final ItemStack stack) {
        return stack.getItem() instanceof IRechargable;
    }
    
    @Override
    public int[] getSlotsForFace(final EnumFacing side) {
        return TileRechargePedestal.slots;
    }
    
    @Override
    public boolean canInsertItem(final int par1, final ItemStack stack, final EnumFacing par3) {
        return stack.getItem() instanceof IRechargable;
    }
    
    @Override
    public boolean canExtractItem(final int par1, final ItemStack stack2, final EnumFacing par3) {
        return true;
    }
    
    @Override
    public AspectList getAspects() {
        final ItemStack s = (this.world == null || this.world.isRemote) ? this.getSyncedStackInSlot(0) : this.getStackInSlot(0);
        if (s != null && s.getItem() instanceof IRechargable) {
            final float c = (float)RechargeHelper.getCharge(s);
            return new AspectList().add(Aspect.ENERGY, Math.round(c));
        }
        return null;
    }
    
    @Override
    public void setAspects(final AspectList aspects) {
    }
    
    @Override
    public int addToContainer(final Aspect tag, final int amount) {
        return 0;
    }
    
    @Override
    public boolean takeFromContainer(final Aspect tag, final int amount) {
        return false;
    }
    
    @Override
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(final Aspect tag, final int amount) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }
    
    @Override
    public int containerContains(final Aspect tag) {
        return 0;
    }
    
    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return true;
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 5) {
            if (this.world.isRemote) {
                FXDispatcher.INSTANCE.visSparkle(this.pos.getX() + this.getWorld().rand.nextInt(3) - this.getWorld().rand.nextInt(3), this.pos.up().getY() + this.getWorld().rand.nextInt(3), this.pos.getZ() + this.getWorld().rand.nextInt(3) - this.getWorld().rand.nextInt(3), this.pos.getX(), this.pos.up().getY(), this.pos.getZ(), j);
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    static {
        slots = new int[] { 0 };
    }
}
