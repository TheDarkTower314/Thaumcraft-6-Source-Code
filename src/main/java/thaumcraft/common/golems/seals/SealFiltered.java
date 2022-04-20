// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import java.util.Iterator;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.seals.ISeal;

public abstract class SealFiltered implements ISeal, ISealGui, ISealConfigFilter
{
    NonNullList<ItemStack> filter;
    NonNullList<Integer> filterSize;
    boolean blacklist;
    
    public SealFiltered() {
        this.filter = NonNullList.withSize(this.getFilterSize(), ItemStack.EMPTY);
        this.filterSize = NonNullList.withSize(this.getFilterSize(), 0);
        this.blacklist = true;
    }
    
    @Override
    public void readCustomNBT(final NBTTagCompound nbt) {
        ItemStackHelper.loadAllItems(nbt, this.filter = NonNullList.withSize(this.getFilterSize(), ItemStack.EMPTY));
        for (final ItemStack s : this.filter) {
            if (s.getCount() > 1) {
                s.setCount(1);
            }
        }
        this.blacklist = nbt.getBoolean("bl");
        this.filterSize = NonNullList.withSize(this.getFilterSize(), 0);
        final NBTTagList nbttaglist = nbt.getTagList("Sizes", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            final int j = nbttagcompound.getByte("Slot") & 0xFF;
            if (j >= 0 && j < this.filterSize.size()) {
                this.filterSize.set(j, nbttagcompound.getInteger("Size"));
            }
        }
    }
    
    @Override
    public void writeCustomNBT(final NBTTagCompound nbt) {
        ItemStackHelper.saveAllItems(nbt, this.filter);
        nbt.setBoolean("bl", this.blacklist);
        final NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.filterSize.size(); ++i) {
            final int size = this.filterSize.get(i);
            if (size != 0) {
                final NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                nbttagcompound.setInteger("Size", size);
                nbttaglist.appendTag(nbttagcompound);
            }
        }
        nbt.setTag("Sizes", nbttaglist);
    }
    
    @Override
    public Object returnContainer(final World world, final EntityPlayer player, final BlockPos pos, final EnumFacing side, final ISealEntity seal) {
        return new SealBaseContainer(player.inventory, world, seal);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Object returnGui(final World world, final EntityPlayer player, final BlockPos pos, final EnumFacing side, final ISealEntity seal) {
        return new SealBaseGUI(player.inventory, world, seal);
    }
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 0 };
    }
    
    @Override
    public int getFilterSize() {
        return 1;
    }
    
    @Override
    public NonNullList<ItemStack> getInv() {
        return this.filter;
    }
    
    @Override
    public NonNullList<Integer> getSizes() {
        return this.filterSize;
    }
    
    @Override
    public ItemStack getFilterSlot(final int i) {
        return this.filter.get(i);
    }
    
    @Override
    public int getFilterSlotSize(final int i) {
        return this.filterSize.get(i);
    }
    
    @Override
    public void setFilterSlot(final int i, final ItemStack stack) {
        this.filter.set(i, stack.copy());
    }
    
    @Override
    public void setFilterSlotSize(final int i, final int size) {
        this.filterSize.set(i, size);
    }
    
    @Override
    public boolean isBlacklist() {
        return this.blacklist;
    }
    
    @Override
    public void setBlacklist(final boolean black) {
        this.blacklist = black;
    }
    
    @Override
    public boolean hasStacksizeLimiters() {
        return false;
    }
}
