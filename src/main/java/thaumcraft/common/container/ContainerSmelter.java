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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.container.slot.SlotLimitedHasAspects;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.essentia.TileSmelter;


public class ContainerSmelter extends Container
{
    private TileSmelter furnace;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;
    private int lastVis;
    private int lastSmelt;
    private int lastFlux;
    
    public ContainerSmelter(InventoryPlayer par1InventoryPlayer, TileSmelter tileEntity) {
        furnace = tileEntity;
        addSlotToContainer(new SlotLimitedHasAspects(tileEntity, 0, 80, 8));
        addSlotToContainer(new Slot(tileEntity, 1, 80, 48));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, furnace);
        listener.sendWindowProperty(this, 0, furnace.furnaceCookTime);
        listener.sendWindowProperty(this, 1, furnace.furnaceBurnTime);
        listener.sendWindowProperty(this, 2, furnace.currentItemBurnTime);
        listener.sendWindowProperty(this, 3, furnace.vis);
        listener.sendWindowProperty(this, 4, furnace.smeltTime);
    }
    
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < listeners.size(); ++i) {
            IContainerListener icrafting = listeners.get(i);
            if (lastCookTime != furnace.furnaceCookTime) {
                icrafting.sendWindowProperty(this, 0, furnace.furnaceCookTime);
            }
            if (lastBurnTime != furnace.furnaceBurnTime) {
                icrafting.sendWindowProperty(this, 1, furnace.furnaceBurnTime);
            }
            if (lastItemBurnTime != furnace.currentItemBurnTime) {
                icrafting.sendWindowProperty(this, 2, furnace.currentItemBurnTime);
            }
            if (lastVis != furnace.vis) {
                icrafting.sendWindowProperty(this, 3, furnace.vis);
            }
            if (lastSmelt != furnace.smeltTime) {
                icrafting.sendWindowProperty(this, 4, furnace.smeltTime);
            }
        }
        lastCookTime = furnace.furnaceCookTime;
        lastBurnTime = furnace.furnaceBurnTime;
        lastItemBurnTime = furnace.currentItemBurnTime;
        lastVis = furnace.vis;
        lastSmelt = furnace.smeltTime;
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            furnace.furnaceCookTime = par2;
        }
        if (par1 == 1) {
            furnace.furnaceBurnTime = par2;
        }
        if (par1 == 2) {
            furnace.currentItemBurnTime = par2;
        }
        if (par1 == 3) {
            furnace.vis = par2;
        }
        if (par1 == 4) {
            furnace.smeltTime = par2;
        }
    }
    
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return furnace.isUsableByPlayer(par1EntityPlayer);
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (par2 != 1 && par2 != 0) {
                AspectList al = ThaumcraftCraftingManager.getObjectTags(itemstack2);
                if (TileSmelter.isItemFuel(itemstack2)) {
                    if (!mergeItemStack(itemstack2, 1, 2, false) && !mergeItemStack(itemstack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (al != null && al.size() > 0) {
                    if (!mergeItemStack(itemstack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (par2 >= 2 && par2 < 29) {
                    if (!mergeItemStack(itemstack2, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (par2 >= 29 && par2 < 38 && !mergeItemStack(itemstack2, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!mergeItemStack(itemstack2, 2, 38, false)) {
                return ItemStack.EMPTY;
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
