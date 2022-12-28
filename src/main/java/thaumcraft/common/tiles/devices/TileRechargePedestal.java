package thaumcraft.common.tiles.devices;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TileRechargePedestal extends TileThaumcraftInventory implements IAspectContainer
{
    private static int[] slots;
    int counter;
    
    public TileRechargePedestal() {
        super(1);
        counter = 0;
        syncedSlots = new int[] { 0 };
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(2.0, 2.0, 2.0);
    }
    
    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote && counter++ % 10 == 0 && getStackInSlot(0) != null && RechargeHelper.rechargeItem(getWorld(), getStackInSlot(0), pos, null, 5) > 0.0f) {
            syncTile(false);
            markDirty();
            ArrayList<Aspect> al = Aspect.getPrimalAspects();
            world.addBlockEvent(pos, getBlockType(), 5, al.get(getWorld().rand.nextInt(al.size())).getColor());
        }
    }
    
    public void setInventorySlotContentsFromInfusion(int par1, ItemStack stack2) {
        setInventorySlotContents(par1, stack2);
        markDirty();
        if (!world.isRemote) {
            syncTile(false);
        }
    }
    
    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack) {
        return stack.getItem() instanceof IRechargable;
    }
    
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return TileRechargePedestal.slots;
    }
    
    @Override
    public boolean canInsertItem(int par1, ItemStack stack, EnumFacing par3) {
        return stack.getItem() instanceof IRechargable;
    }
    
    @Override
    public boolean canExtractItem(int par1, ItemStack stack2, EnumFacing par3) {
        return true;
    }
    
    @Override
    public AspectList getAspects() {
        ItemStack s = (world == null || world.isRemote) ? getSyncedStackInSlot(0) : getStackInSlot(0);
        if (s != null && s.getItem() instanceof IRechargable) {
            float c = (float)RechargeHelper.getCharge(s);
            return new AspectList().add(Aspect.ENERGY, Math.round(c));
        }
        return null;
    }
    
    @Override
    public void setAspects(AspectList aspects) {
    }
    
    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }
    
    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }
    
    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 5) {
            if (world.isRemote) {
                FXDispatcher.INSTANCE.visSparkle(pos.getX() + getWorld().rand.nextInt(3) - getWorld().rand.nextInt(3), pos.up().getY() + getWorld().rand.nextInt(3), pos.getZ() + getWorld().rand.nextInt(3) - getWorld().rand.nextInt(3), pos.getX(), pos.up().getY(), pos.getZ(), j);
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    static {
        slots = new int[] { 0 };
    }
}
