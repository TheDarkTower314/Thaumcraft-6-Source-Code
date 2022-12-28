package thaumcraft.common.tiles;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;


public class TileThaumcraftInventory extends TileThaumcraft implements ISidedInventory, ITickable
{
    private NonNullList<ItemStack> stacks;
    protected int[] syncedSlots;
    private NonNullList<ItemStack> syncedStacks;
    protected String customName;
    private int[] faceSlots;
    boolean initial;
    IItemHandler handlerTop;
    IItemHandler handlerBottom;
    IItemHandler handlerWest;
    IItemHandler handlerEast;
    IItemHandler handlerNorth;
    IItemHandler handlerSouth;
    
    public TileThaumcraftInventory(int size) {
        stacks = NonNullList.withSize(1, ItemStack.EMPTY);
        syncedSlots = new int[0];
        syncedStacks = NonNullList.withSize(1, ItemStack.EMPTY);
        initial = true;
        handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
        handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
        handlerWest = new SidedInvWrapper(this, EnumFacing.WEST);
        handlerEast = new SidedInvWrapper(this, EnumFacing.EAST);
        handlerNorth = new SidedInvWrapper(this, EnumFacing.NORTH);
        handlerSouth = new SidedInvWrapper(this, EnumFacing.SOUTH);
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        syncedStacks = NonNullList.withSize(size, ItemStack.EMPTY);
        faceSlots = new int[size];
        for (int a = 0; a < size; ++a) {
            faceSlots[a] = a;
        }
    }
    
    public int getSizeInventory() {
        return stacks.size();
    }
    
    protected NonNullList<ItemStack> getItems() {
        return stacks;
    }
    
    public ItemStack getSyncedStackInSlot(int index) {
        return syncedStacks.get(index);
    }
    
    public ItemStack getStackInSlot(int index) {
        return getItems().get(index);
    }
    
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(getItems(), index, count);
        if (!itemstack.isEmpty() && isSyncedSlot(index)) {
            syncSlots(null);
        }
        markDirty();
        return itemstack;
    }
    
    public ItemStack removeStackFromSlot(int index) {
        ItemStack s = ItemStackHelper.getAndRemove(getItems(), index);
        if (isSyncedSlot(index)) {
            syncSlots(null);
        }
        markDirty();
        return s;
    }
    
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        getItems().set(index, stack);
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
        if (isSyncedSlot(index)) {
            syncSlots(null);
        }
    }
    
    public String getName() {
        return hasCustomName() ? customName : "container.thaumcraft";
    }
    
    public boolean hasCustomName() {
        return customName != null && customName.length() > 0;
    }
    
    public ITextComponent getDisplayName() {
        return null;
    }
    
    private boolean isSyncedSlot(int slot) {
        for (int s : syncedSlots) {
            if (s == slot) {
                return true;
            }
        }
        return false;
    }
    
    protected void syncSlots(EntityPlayerMP player) {
        if (syncedSlots.length > 0) {
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < stacks.size(); ++i) {
                if (!stacks.get(i).isEmpty() && isSyncedSlot(i)) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte)i);
                    stacks.get(i).writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                }
            }
            nbt.setTag("ItemsSynced", nbttaglist);
            sendMessageToClient(nbt, player);
        }
    }
    
    @Override
    public void syncTile(boolean rerender) {
        super.syncTile(rerender);
        syncSlots(null);
    }
    
    @Override
    public void messageFromClient(NBTTagCompound nbt, EntityPlayerMP player) {
        super.messageFromClient(nbt, player);
        if (nbt.hasKey("requestSync")) {
            syncSlots(player);
        }
    }
    
    @Override
    public void messageFromServer(NBTTagCompound nbt) {
        super.messageFromServer(nbt);
        if (nbt.hasKey("ItemsSynced")) {
            syncedStacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
            NBTTagList nbttaglist = nbt.getTagList("ItemsSynced", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                byte b0 = nbttagcompound1.getByte("Slot");
                if (isSyncedSlot(b0)) {
                    syncedStacks.set(b0, new ItemStack(nbttagcompound1));
                }
            }
        }
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        if (nbtCompound.hasKey("CustomName")) {
            customName = nbtCompound.getString("CustomName");
        }
        ItemStackHelper.loadAllItems(nbtCompound, stacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY));
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        if (hasCustomName()) {
            nbtCompound.setString("CustomName", customName);
        }
        ItemStackHelper.saveAllItems(nbtCompound, stacks);
        return nbtCompound;
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUsableByPlayer(EntityPlayer par1EntityPlayer) {
        return world.getTileEntity(getPos()) == this && par1EntityPlayer.getDistanceSqToCenter(getPos()) <= 64.0;
    }
    
    public boolean isItemValidForSlot(int par1, ItemStack stack2) {
        return true;
    }
    
    public int[] getSlotsForFace(EnumFacing par1) {
        return faceSlots;
    }
    
    public void openInventory(EntityPlayer player) {
    }
    
    public void closeInventory(EntityPlayer player) {
    }
    
    public int getField(int id) {
        return 0;
    }
    
    public void setField(int id, int value) {
    }
    
    public int getFieldCount() {
        return 0;
    }
    
    public void clear() {
    }
    
    public boolean canInsertItem(int par1, ItemStack stack2, EnumFacing par3) {
        return isItemValidForSlot(par1, stack2);
    }
    
    public boolean canExtractItem(int par1, ItemStack stack2, EnumFacing par3) {
        return true;
    }
    
    public boolean isEmpty() {
        for (ItemStack itemstack : stacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public void update() {
        if (initial) {
            initial = false;
            if (!world.isRemote) {
                syncSlots(null);
            }
            else {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setBoolean("requestSync", true);
                sendMessageToServer(nbt);
            }
        }
    }
    
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (facing == null || capability != CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T)super.getCapability((Capability)capability, facing);
        }
        if (facing == EnumFacing.DOWN) {
            return (T) handlerBottom;
        }
        if (facing == EnumFacing.UP) {
            return (T) handlerTop;
        }
        if (facing == EnumFacing.WEST) {
            return (T) handlerWest;
        }
        if (facing == EnumFacing.EAST) {
            return (T) handlerEast;
        }
        if (facing == EnumFacing.NORTH) {
            return (T) handlerNorth;
        }
        return (T) handlerSouth;
    }
    
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
