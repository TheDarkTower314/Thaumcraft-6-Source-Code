package thaumcraft.common.container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.container.slot.SlotOutput;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;


public class ContainerVoidSiphon extends Container
{
    private TileVoidSiphon siphon;
    private int lastProgress;
    
    public ContainerVoidSiphon(InventoryPlayer par1InventoryPlayer, TileVoidSiphon tileEntity) {
        siphon = tileEntity;
        addSlotToContainer(new SlotOutput(tileEntity, 0, 80, 32));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    public void addListener(IContainerListener par1ICrafting) {
        super.addListener(par1ICrafting);
        par1ICrafting.sendWindowProperty(this, 0, siphon.progress);
    }
    
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < listeners.size(); ++i) {
            IContainerListener icrafting = listeners.get(i);
            if (lastProgress != siphon.progress) {
                icrafting.sendWindowProperty(this, 0, siphon.progress);
            }
        }
        lastProgress = siphon.progress;
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            siphon.progress = par2;
        }
    }
    
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return siphon.isUsableByPlayer(par1EntityPlayer);
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot == 0) {
                if (!siphon.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 1, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!siphon.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 0, 1, false)) {
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
}
