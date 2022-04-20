// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.common.capabilities.Capability;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import java.util.List;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ITickable;
import net.minecraft.inventory.ISidedInventory;

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
    
    public TileThaumcraftInventory(final int size) {
        this.stacks = NonNullList.withSize(1, ItemStack.EMPTY);
        this.syncedSlots = new int[0];
        this.syncedStacks = NonNullList.withSize(1, ItemStack.EMPTY);
        this.initial = true;
        this.handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
        this.handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
        this.handlerWest = new SidedInvWrapper(this, EnumFacing.WEST);
        this.handlerEast = new SidedInvWrapper(this, EnumFacing.EAST);
        this.handlerNorth = new SidedInvWrapper(this, EnumFacing.NORTH);
        this.handlerSouth = new SidedInvWrapper(this, EnumFacing.SOUTH);
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.syncedStacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.faceSlots = new int[size];
        for (int a = 0; a < size; ++a) {
            this.faceSlots[a] = a;
        }
    }
    
    public int getSizeInventory() {
        return this.stacks.size();
    }
    
    protected NonNullList<ItemStack> getItems() {
        return this.stacks;
    }
    
    public ItemStack getSyncedStackInSlot(final int index) {
        return this.syncedStacks.get(index);
    }
    
    public ItemStack getStackInSlot(final int index) {
        return this.getItems().get(index);
    }
    
    public ItemStack decrStackSize(final int index, final int count) {
        final ItemStack itemstack = ItemStackHelper.getAndSplit(this.getItems(), index, count);
        if (!itemstack.isEmpty() && this.isSyncedSlot(index)) {
            this.syncSlots(null);
        }
        this.markDirty();
        return itemstack;
    }
    
    public ItemStack removeStackFromSlot(final int index) {
        final ItemStack s = ItemStackHelper.getAndRemove(this.getItems(), index);
        if (this.isSyncedSlot(index)) {
            this.syncSlots(null);
        }
        this.markDirty();
        return s;
    }
    
    public void setInventorySlotContents(final int index, @Nullable final ItemStack stack) {
        this.getItems().set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
        if (this.isSyncedSlot(index)) {
            this.syncSlots(null);
        }
    }
    
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.thaumcraft";
    }
    
    public boolean hasCustomName() {
        return this.customName != null && this.customName.length() > 0;
    }
    
    public ITextComponent getDisplayName() {
        return null;
    }
    
    private boolean isSyncedSlot(final int slot) {
        for (final int s : this.syncedSlots) {
            if (s == slot) {
                return true;
            }
        }
        return false;
    }
    
    protected void syncSlots(final EntityPlayerMP player) {
        if (this.syncedSlots.length > 0) {
            final NBTTagCompound nbt = new NBTTagCompound();
            final NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < this.stacks.size(); ++i) {
                if (!this.stacks.get(i).isEmpty() && this.isSyncedSlot(i)) {
                    final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte)i);
                    this.stacks.get(i).writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                }
            }
            nbt.setTag("ItemsSynced", nbttaglist);
            this.sendMessageToClient(nbt, player);
        }
    }
    
    @Override
    public void syncTile(final boolean rerender) {
        super.syncTile(rerender);
        this.syncSlots(null);
    }
    
    @Override
    public void messageFromClient(final NBTTagCompound nbt, final EntityPlayerMP player) {
        super.messageFromClient(nbt, player);
        if (nbt.hasKey("requestSync")) {
            this.syncSlots(player);
        }
    }
    
    @Override
    public void messageFromServer(final NBTTagCompound nbt) {
        super.messageFromServer(nbt);
        if (nbt.hasKey("ItemsSynced")) {
            this.syncedStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
            final NBTTagList nbttaglist = nbt.getTagList("ItemsSynced", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                final byte b0 = nbttagcompound1.getByte("Slot");
                if (this.isSyncedSlot(b0)) {
                    this.syncedStacks.set(b0, new ItemStack(nbttagcompound1));
                }
            }
        }
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        if (nbtCompound.hasKey("CustomName")) {
            this.customName = nbtCompound.getString("CustomName");
        }
        ItemStackHelper.loadAllItems(nbtCompound, this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY));
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        if (this.hasCustomName()) {
            nbtCompound.setString("CustomName", this.customName);
        }
        ItemStackHelper.saveAllItems(nbtCompound, this.stacks);
        return nbtCompound;
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUsableByPlayer(final EntityPlayer par1EntityPlayer) {
        return this.world.getTileEntity(this.getPos()) == this && par1EntityPlayer.getDistanceSqToCenter(this.getPos()) <= 64.0;
    }
    
    public boolean isItemValidForSlot(final int par1, final ItemStack stack2) {
        return true;
    }
    
    public int[] getSlotsForFace(final EnumFacing par1) {
        return this.faceSlots;
    }
    
    public void openInventory(final EntityPlayer player) {
    }
    
    public void closeInventory(final EntityPlayer player) {
    }
    
    public int getField(final int id) {
        return 0;
    }
    
    public void setField(final int id, final int value) {
    }
    
    public int getFieldCount() {
        return 0;
    }
    
    public void clear() {
    }
    
    public boolean canInsertItem(final int par1, final ItemStack stack2, final EnumFacing par3) {
        return this.isItemValidForSlot(par1, stack2);
    }
    
    public boolean canExtractItem(final int par1, final ItemStack stack2, final EnumFacing par3) {
        return true;
    }
    
    public boolean isEmpty() {
        for (final ItemStack itemstack : this.stacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public void update() {
        if (this.initial) {
            this.initial = false;
            if (!this.world.isRemote) {
                this.syncSlots(null);
            }
            else {
                final NBTTagCompound nbt = new NBTTagCompound();
                nbt.setBoolean("requestSync", true);
                this.sendMessageToServer(nbt);
            }
        }
    }
    
    @Nullable
    public <T> T getCapability(final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (facing == null || capability != CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T)super.getCapability((Capability)capability, facing);
        }
        if (facing == EnumFacing.DOWN) {
            return (T)this.handlerBottom;
        }
        if (facing == EnumFacing.UP) {
            return (T)this.handlerTop;
        }
        if (facing == EnumFacing.WEST) {
            return (T)this.handlerWest;
        }
        if (facing == EnumFacing.EAST) {
            return (T)this.handlerEast;
        }
        if (facing == EnumFacing.NORTH) {
            return (T)this.handlerNorth;
        }
        return (T)this.handlerSouth;
    }
    
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
