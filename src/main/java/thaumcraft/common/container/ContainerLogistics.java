package thaumcraft.common.container;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.common.container.slot.SlotGhostFull;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.seals.SealProvide;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketItemToClientContainer;


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
    
    public ContainerLogistics(InventoryPlayer iinventory, World par2World) {
        player = null;
        input = new InventoryLogistics(this);
        items = new TreeMap<String, ItemStack>();
        lastTotal = 0;
        start = 0;
        end = 0;
        searchText = "";
        lastStart = 0;
        lastEnd = 0;
        updated = false;
        worldObj = par2World;
        player = iinventory.player;
        for (int a = 0; a < input.getSizeInventory(); ++a) {
            addSlotToContainer(new SlotGhostFull(input, a, 19 + a % 9 * 19, 19 + a / 9 * 19));
        }
        refreshItemList(true);
    }
    
    public void refreshItemList(boolean full) {
        int newTotal = lastTotal;
        TreeMap<String, ItemStack> ti = new TreeMap<String, ItemStack>();
        if (full) {
            newTotal = 0;
            CopyOnWriteArrayList<SealEntity> seals = SealHandler.getSealsInRange(worldObj, player.getPosition(), 32);
            for (SealEntity seal : seals) {
                if (seal.getSeal() instanceof SealProvide && seal.getOwner().equals(player.getUniqueID().toString())) {
                    IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(worldObj, seal.getSealPos().pos, seal.getSealPos().face);
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        ItemStack stack = handler.getStackInSlot(slot).copy();
                        if (((SealProvide)seal.getSeal()).matchesFilters(stack)) {
                            if (searchText.isEmpty() || stack.getDisplayName().toLowerCase().contains(searchText.toLowerCase())) {
                                String key = stack.getDisplayName() + stack.getItemDamage() + stack.getTagCompound();
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
        if (lastTotal != newTotal || start != lastStart) {
            lastTotal = newTotal;
            if (full) {
                items = ti;
            }
            input.clear();
            int j = 0;
            int q = 0;
            for (String key2 : items.keySet()) {
                if (++j <= start * 9) {
                    continue;
                }
                input.setInventorySlotContents(q, items.get(key2));
                if (++q >= input.getSizeInventory()) {
                    break;
                }
            }
            end = items.size() / 9 - 8;
        }
    }
    
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, input);
        listener.sendWindowProperty(this, 0, start);
    }
    
    public void detectAndSendChanges() {
        sendLargeSlotsToClient();
        super.detectAndSendChanges();
        for (int i = 0; i < listeners.size(); ++i) {
            IContainerListener icrafting = listeners.get(i);
            if (lastStart != start) {
                icrafting.sendWindowProperty(this, 0, start);
            }
            if (lastEnd != end) {
                icrafting.sendWindowProperty(this, 1, end);
            }
        }
        lastStart = start;
        lastEnd = end;
    }
    
    private void sendLargeSlotsToClient() {
        for (int i = 0; i < inventorySlots.size(); ++i) {
            if (getSlot(i) instanceof SlotGhostFull) {
                ItemStack itemstack = inventorySlots.get(i).getStack();
                ItemStack itemstack2 = inventoryItemStacks.get(i);
                if (itemstack.getCount() > itemstack.getMaxStackSize()) {
                    for (int j = 0; j < listeners.size(); ++j) {
                        if (listeners.get(j) instanceof EntityPlayerMP) {
                            EntityPlayerMP p = (EntityPlayerMP) listeners.get(j);
                            PacketHandler.INSTANCE.sendTo(new PacketItemToClientContainer(windowId, i, itemstack), p);
                        }
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            start = par2;
            updated = true;
        }
        if (par1 == 1) {
            end = par2;
            updated = true;
        }
    }
    
    public boolean enchantItem(EntityPlayer par1EntityPlayer, int par2) {
        if (par2 == 22) {
            refreshItemList(true);
            return true;
        }
        if (par2 == 0) {
            if (start < items.size() / 9 - 8) {
                ++start;
                refreshItemList(false);
            }
            return true;
        }
        if (par2 == 1) {
            if (start > 0) {
                --start;
                refreshItemList(false);
            }
            return true;
        }
        if (par2 >= 100) {
            int s = par2 - 100;
            if (s >= 0 && s <= items.size() / 9 - 8) {
                start = s;
                refreshItemList(false);
            }
            return true;
        }
        return super.enchantItem(par1EntityPlayer, par2);
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot < input.getSizeInventory()) {
                if (!input.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, input.getSizeInventory(), inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!input.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 0, input.getSizeInventory(), false)) {
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
    
    public void onInventoryChanged(IInventory invBasic) {
        detectAndSendChanges();
    }
}
