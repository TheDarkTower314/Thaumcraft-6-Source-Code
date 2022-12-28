package thaumcraft.common.container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


public class ContainerThaumatorium extends Container
{
    private TileThaumatorium thaumatorium;
    private EntityPlayer player;
    
    public ContainerThaumatorium(InventoryPlayer par1InventoryPlayer, TileThaumatorium tileEntity) {
        player = null;
        player = par1InventoryPlayer.player;
        thaumatorium = tileEntity;
        ((ContainerThaumatorium)(thaumatorium.eventHandler = this)).addSlotToContainer(new Slot(tileEntity, 0, 55, 24));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 135 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 193));
        }
        thaumatorium.updateRecipes(player);
    }
    
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        thaumatorium.updateRecipes(player);
    }
    
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!thaumatorium.getWorld().isRemote) {
            thaumatorium.eventHandler = null;
        }
    }
    
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return thaumatorium.isUsableByPlayer(par1EntityPlayer);
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (par2 != 0) {
                if (!mergeItemStack(itemstack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (par2 >= 1 && par2 < 28) {
                if (!mergeItemStack(itemstack2, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (par2 >= 28 && par2 < 37 && !mergeItemStack(itemstack2, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
                if (!mergeItemStack(itemstack2, 1, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack2.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }
            if (itemstack2.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(par1EntityPlayer, itemstack2);
        }
        return itemstack;
    }
}
