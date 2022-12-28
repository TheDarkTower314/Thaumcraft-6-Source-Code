package thaumcraft.common.golems.seals;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;


public abstract class SealFiltered implements ISeal, ISealGui, ISealConfigFilter
{
    NonNullList<ItemStack> filter;
    NonNullList<Integer> filterSize;
    boolean blacklist;
    
    public SealFiltered() {
        filter = NonNullList.withSize(getFilterSize(), ItemStack.EMPTY);
        filterSize = NonNullList.withSize(getFilterSize(), 0);
        blacklist = true;
    }
    
    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        ItemStackHelper.loadAllItems(nbt, filter = NonNullList.withSize(getFilterSize(), ItemStack.EMPTY));
        for (ItemStack s : filter) {
            if (s.getCount() > 1) {
                s.setCount(1);
            }
        }
        blacklist = nbt.getBoolean("bl");
        filterSize = NonNullList.withSize(getFilterSize(), 0);
        NBTTagList nbttaglist = nbt.getTagList("Sizes", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 0xFF;
            if (j >= 0 && j < filterSize.size()) {
                filterSize.set(j, nbttagcompound.getInteger("Size"));
            }
        }
    }
    
    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        ItemStackHelper.saveAllItems(nbt, filter);
        nbt.setBoolean("bl", blacklist);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < filterSize.size(); ++i) {
            int size = filterSize.get(i);
            if (size != 0) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                nbttagcompound.setInteger("Size", size);
                nbttaglist.appendTag(nbttagcompound);
            }
        }
        nbt.setTag("Sizes", nbttaglist);
    }
    
    @Override
    public Object returnContainer(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
        return new SealBaseContainer(player.inventory, world, seal);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Object returnGui(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
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
        return filter;
    }
    
    @Override
    public NonNullList<Integer> getSizes() {
        return filterSize;
    }
    
    @Override
    public ItemStack getFilterSlot(int i) {
        return filter.get(i);
    }
    
    @Override
    public int getFilterSlotSize(int i) {
        return filterSize.get(i);
    }
    
    @Override
    public void setFilterSlot(int i, ItemStack stack) {
        filter.set(i, stack.copy());
    }
    
    @Override
    public void setFilterSlotSize(int i, int size) {
        filterSize.set(i, size);
    }
    
    @Override
    public boolean isBlacklist() {
        return blacklist;
    }
    
    @Override
    public void setBlacklist(boolean black) {
        blacklist = black;
    }
    
    @Override
    public boolean hasStacksizeLimiters() {
        return false;
    }
}
