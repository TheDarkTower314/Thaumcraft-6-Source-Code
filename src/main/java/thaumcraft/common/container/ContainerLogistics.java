// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.misc.PacketItemToClientContainer;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.items.IItemHandler;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.common.golems.seals.SealProvide;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import net.minecraft.inventory.Slot;
import thaumcraft.common.container.slot.SlotGhostFull;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import java.util.TreeMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Container;

public class ContainerLogistics extends Container implements IInventoryChangedListener
{
    private World worldObj;
    EntityPlayer player;
    public IInventory input;
    TreeMap<String, ItemStack> items;
    int lastTotal;
    public int start;
    public int end;
    public String searchText;
    int lastStart;
    int lastEnd;
    public boolean updated;
    
    public ContainerLogistics(final InventoryPlayer iinventory, final World par2World) {
        this.player = null;
        this.input = new InventoryLogistics(this);
        this.items = new TreeMap<String, ItemStack>();
        this.lastTotal = 0;
        this.start = 0;
        this.end = 0;
        this.searchText = "";
        this.lastStart = 0;
        this.lastEnd = 0;
        this.updated = false;
        this.worldObj = par2World;
        this.player = iinventory.player;
        for (int a = 0; a < this.input.getSizeInventory(); ++a) {
            this.addSlotToContainer(new SlotGhostFull(this.input, a, 19 + a % 9 * 19, 19 + a / 9 * 19));
        }
        this.refreshItemList(true);
    }
    
    public void refreshItemList(final boolean full) {
        int newTotal = this.lastTotal;
        final TreeMap<String, ItemStack> ti = new TreeMap<String, ItemStack>();
        if (full) {
            newTotal = 0;
            final CopyOnWriteArrayList<SealEntity> seals = SealHandler.getSealsInRange(this.worldObj, this.player.getPosition(), 32);
            for (final SealEntity seal : seals) {
                if (seal.getSeal() instanceof SealProvide && seal.getOwner().equals(this.player.getUniqueID().toString())) {
                    final IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(this.worldObj, seal.getSealPos().pos, seal.getSealPos().face);
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        final ItemStack stack = handler.getStackInSlot(slot).copy();
                        if (((SealProvide)seal.getSeal()).matchesFilters(stack)) {
                            if (this.searchText.isEmpty() || stack.getDisplayName().toLowerCase().contains(this.searchText.toLowerCase())) {
                                final String key = stack.getDisplayName() + stack.getItemDamage() + stack.getTagCompound();
                                if (ti.containsKey(key)) {
                                    stack.grow(ti.get(key).getCount());
                                }
                                ti.put(key, stack);
                                newTotal += stack.getCount();
                            }
                        }
                    }
                }
            }
        }
        if (this.lastTotal != newTotal || this.start != this.lastStart) {
            this.lastTotal = newTotal;
            if (full) {
                this.items = ti;
            }
            this.input.clear();
            int j = 0;
            int q = 0;
            for (final String key2 : this.items.keySet()) {
                if (++j <= this.start * 9) {
                    continue;
                }
                this.input.setInventorySlotContents(q, this.items.get(key2));
                if (++q >= this.input.getSizeInventory()) {
                    break;
                }
            }
            this.end = this.items.size() / 9 - 8;
        }
    }
    
    public void addListener(final IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.input);
        listener.sendWindowProperty(this, 0, this.start);
    }
    
    public void detectAndSendChanges() {
        this.sendLargeSlotsToClient();
        super.detectAndSendChanges();
        for (int i = 0; i < this.listeners.size(); ++i) {
            final IContainerListener icrafting = this.listeners.get(i);
            if (this.lastStart != this.start) {
                icrafting.sendWindowProperty(this, 0, this.start);
            }
            if (this.lastEnd != this.end) {
                icrafting.sendWindowProperty(this, 1, this.end);
            }
        }
        this.lastStart = this.start;
        this.lastEnd = this.end;
    }
    
    private void sendLargeSlotsToClient() {
        for (int i = 0; i < this.inventorySlots.size(); ++i) {
            if (this.getSlot(i) instanceof SlotGhostFull) {
                final ItemStack itemstack = this.inventorySlots.get(i).getStack();
                final ItemStack itemstack2 = this.inventoryItemStacks.get(i);
                if (itemstack.getCount() > itemstack.getMaxStackSize()) {
                    for (int j = 0; j < this.listeners.size(); ++j) {
                        if (this.listeners.get(j) instanceof EntityPlayerMP) {
                            final EntityPlayerMP p = (EntityPlayerMP) this.listeners.get(j);
                            PacketHandler.INSTANCE.sendTo(new PacketItemToClientContainer(this.windowId, i, itemstack), p);
                        }
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(final int par1, final int par2) {
        if (par1 == 0) {
            this.start = par2;
            this.updated = true;
        }
        if (par1 == 1) {
            this.end = par2;
            this.updated = true;
        }
    }
    
    public boolean enchantItem(final EntityPlayer par1EntityPlayer, final int par2) {
        if (par2 == 22) {
            this.refreshItemList(true);
            return true;
        }
        if (par2 == 0) {
            if (this.start < this.items.size() / 9 - 8) {
                ++this.start;
                this.refreshItemList(false);
            }
            return true;
        }
        if (par2 == 1) {
            if (this.start > 0) {
                --this.start;
                this.refreshItemList(false);
            }
            return true;
        }
        if (par2 >= 100) {
            final int s = par2 - 100;
            if (s >= 0 && s <= this.items.size() / 9 - 8) {
                this.start = s;
                this.refreshItemList(false);
            }
            return true;
        }
        return super.enchantItem(par1EntityPlayer, par2);
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int slot) {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slotObject = this.inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            final ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot < this.input.getSizeInventory()) {
                if (!this.input.isItemValidForSlot(slot, stackInSlot) || !this.mergeItemStack(stackInSlot, this.input.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.input.isItemValidForSlot(slot, stackInSlot) || !this.mergeItemStack(stackInSlot, 0, this.input.getSizeInventory(), false)) {
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
    
    public boolean canInteractWith(final EntityPlayer var1) {
        return true;
    }
    
    public void onInventoryChanged(final IInventory invBasic) {
        this.detectAndSendChanges();
    }
}
