package thaumcraft.common.container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.items.tools.ItemHandMirror;


public class ContainerHandMirror extends Container implements IInventoryChangedListener
{
    private World worldObj;
    private int posX;
    private int posY;
    private int posZ;
    public IInventory input;
    ItemStack mirror;
    EntityPlayer player;
    
    public ContainerHandMirror(InventoryPlayer iinventory, World par2World, int par3, int par4, int par5) {
        input = new InventoryHandMirror(this);
        mirror = null;
        player = null;
        worldObj = par2World;
        posX = par3;
        posY = par4;
        posZ = par5;
        player = iinventory.player;
        mirror = iinventory.getCurrentItem();
        addSlotToContainer(new Slot(input, 0, 80, 24));
        bindPlayerInventory(iinventory);
        onCraftMatrixChanged(input);
    }
    
    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    public void onCraftMatrixChanged(IInventory par1IInventory) {
        if (!input.getStackInSlot(0).isEmpty() && ItemStack.areItemStacksEqual(input.getStackInSlot(0), mirror)) {
            player.openContainer = player.inventoryContainer;
        }
        else if (!worldObj.isRemote && !input.getStackInSlot(0).isEmpty() && player != null) {
            ItemStack is = input.getStackInSlot(0).copy();
            input.setInventorySlotContents(0, ItemStack.EMPTY);
            input.markDirty();
            if (ItemHandMirror.transport(mirror, is, player, worldObj)) {
                for (int var4 = 0; var4 < listeners.size(); ++var4) {
                    listeners.get(var4).sendSlotContents(this, 0, ItemStack.EMPTY);
                }
            }
            else {
                input.setInventorySlotContents(0, is);
                input.markDirty();
            }
        }
    }
    
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        try {
            ItemStack s = getSlot(slotId).getStack();
            if (s.getItem() instanceof ItemHandMirror) {
                return ItemStack.EMPTY;
            }
        }
        catch (Exception ex) {}
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack() && !(slotObject.getStack().getItem() instanceof ItemHandMirror)) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot == 0) {
                if (!mergeItemStack(stackInSlot, 1, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!mergeItemStack(stackInSlot, 0, 1, false)) {
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
    
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!worldObj.isRemote) {
            ItemStack var3 = input.removeStackFromSlot(0);
            par1EntityPlayer.dropItem(var3, false);
        }
    }
    
    protected boolean mergeItemStack(ItemStack stackin, int par2, int par3, boolean par4, int limit) {
        boolean var5 = false;
        int var6 = par2;
        if (par4) {
            var6 = par3 - 1;
        }
        if (stackin.isStackable()) {
            while (stackin.getCount() > 0 && ((!par4 && var6 < par3) || (par4 && var6 >= par2))) {
                Slot var7 = inventorySlots.get(var6);
                ItemStack stack8 = var7.getStack();
                if (stack8 != null && !stack8.isEmpty() && stack8.getItem() == stackin.getItem() && (!stackin.getHasSubtypes() || stackin.getItemDamage() == stack8.getItemDamage()) && ItemStack.areItemStackTagsEqual(stackin, stack8)) {
                    int var8 = stack8.getCount() + stackin.getCount();
                    if (var8 <= Math.min(stackin.getMaxStackSize(), limit)) {
                        stackin.setCount(0);
                        stack8.setCount(var8);
                        var7.onSlotChanged();
                        var5 = true;
                    }
                    else if (stack8.getCount() < Math.min(stackin.getMaxStackSize(), limit)) {
                        stackin.shrink(Math.min(stackin.getMaxStackSize(), limit) - stack8.getCount());
                        stack8.setCount(Math.min(stackin.getMaxStackSize(), limit));
                        var7.onSlotChanged();
                        var5 = true;
                    }
                }
                if (par4) {
                    --var6;
                }
                else {
                    ++var6;
                }
            }
        }
        if (stackin.getCount() > 0) {
            if (par4) {
                var6 = par3 - 1;
            }
            else {
                var6 = par2;
            }
            while ((!par4 && var6 < par3) || (par4 && var6 >= par2)) {
                Slot var7 = inventorySlots.get(var6);
                ItemStack stack8 = var7.getStack();
                if (stack8 == null || stack8.isEmpty()) {
                    ItemStack res = stackin.copy();
                    res.setCount(Math.min(res.getCount(), limit));
                    var7.putStack(res);
                    var7.onSlotChanged();
                    stackin.shrink(res.getCount());
                    var5 = true;
                    break;
                }
                if (par4) {
                    --var6;
                }
                else {
                    ++var6;
                }
            }
        }
        return var5;
    }
    
    public void onInventoryChanged(IInventory invBasic) {
        detectAndSendChanges();
    }
}
