package thaumcraft.common.container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.common.container.slot.SlotLimitedByClass;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.ItemFocusPouch;


public class ContainerFocusPouch extends Container implements IInventoryChangedListener
{
    private World worldObj;
    private int posX;
    private int posY;
    private int posZ;
    private int blockSlot;
    public IInventory input;
    ItemStack pouch;
    EntityPlayer player;
    
    public ContainerFocusPouch(InventoryPlayer iinventory, World par2World, int par3, int par4, int par5) {
        input = new InventoryFocusPouch(this);
        pouch = null;
        player = null;
        worldObj = par2World;
        posX = par3;
        posY = par4;
        posZ = par5;
        player = iinventory.player;
        pouch = iinventory.getCurrentItem();
        blockSlot = iinventory.currentItem + 45;
        for (int a = 0; a < 18; ++a) {
            addSlotToContainer(new SlotLimitedByClass(ItemFocus.class, input, a, 37 + a % 6 * 18, 51 + a / 6 * 18));
        }
        bindPlayerInventory(iinventory);
        if (!par2World.isRemote) {
            try {
                NonNullList<ItemStack> list = ((ItemFocusPouch) pouch.getItem()).getInventory(pouch);
                for (int a2 = 0; a2 < list.size(); ++a2) {
                    input.setInventorySlotContents(a2, list.get(a2));
                }
            }
            catch (Exception ex) {}
        }
        onCraftMatrixChanged(input);
    }
    
    public void onInventoryChanged(IInventory invBasic) {
        detectAndSendChanges();
    }
    
    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 151 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 209));
        }
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slot) {
        if (slot == blockSlot) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot < 18) {
                if (!input.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 18, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!input.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 0, 18, false)) {
                return ItemStack.EMPTY;
            }
            if (stackInSlot.getCount() == 0) {
                slotObject.putStack(ItemStack.EMPTY);
            }
            else {
                slotObject.onSlotChanged();
            }
        }
        return stack;
    }
    
    public boolean canInteractWith(EntityPlayer var1) {
        return true;
    }
    
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId == blockSlot) {
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
    
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!worldObj.isRemote) {
            NonNullList<ItemStack> list = NonNullList.withSize(18, ItemStack.EMPTY);
            for (int a = 0; a < list.size(); ++a) {
                list.set(a, input.getStackInSlot(a));
            }
            if (pouch.getItem() instanceof ItemFocusPouch) {
                ((ItemFocusPouch) pouch.getItem()).setInventory(pouch, list);
            }
            if (player == null) {
                return;
            }
            if (player.getHeldItem(player.getActiveHand()).isItemEqual(pouch)) {
                player.setHeldItem(player.getActiveHand(), pouch);
            }
            player.inventory.markDirty();
        }
    }
}
