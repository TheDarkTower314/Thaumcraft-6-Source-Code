// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.inventory.ISidedInventory;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileThaumatoriumTop extends TileThaumcraft implements IAspectContainer, IEssentiaTransport, ISidedInventory, ITickable
{
    public TileThaumatorium thaumatorium;
    
    public TileThaumatoriumTop() {
        thaumatorium = null;
    }
    
    public void update() {
        if (thaumatorium == null) {
            final TileEntity tile = world.getTileEntity(pos.down());
            if (tile != null && tile instanceof TileThaumatorium) {
                thaumatorium = (TileThaumatorium)tile;
            }
        }
    }
    
    @Override
    public int addToContainer(final Aspect tt, final int am) {
        if (thaumatorium == null) {
            return am;
        }
        return thaumatorium.addToContainer(tt, am);
    }
    
    @Override
    public boolean takeFromContainer(final Aspect tt, final int am) {
        return thaumatorium != null && thaumatorium.takeFromContainer(tt, am);
    }
    
    @Override
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(final Aspect tt, final int am) {
        return thaumatorium != null && thaumatorium.doesContainerContainAmount(tt, am);
    }
    
    @Override
    public int containerContains(final Aspect tt) {
        if (thaumatorium == null) {
            return 0;
        }
        return thaumatorium.containerContains(tt);
    }
    
    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return true;
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return thaumatorium != null && thaumatorium.isConnectable(face);
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return thaumatorium != null && thaumatorium.canInputFrom(face);
    }
    
    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return false;
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
        if (thaumatorium == null) {
            return;
        }
        thaumatorium.setSuction(aspect, amount);
    }
    
    @Override
    public Aspect getSuctionType(final EnumFacing loc) {
        if (thaumatorium == null) {
            return null;
        }
        return thaumatorium.getSuctionType(loc);
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing loc) {
        if (thaumatorium == null) {
            return 0;
        }
        return thaumatorium.getSuctionAmount(loc);
    }
    
    @Override
    public Aspect getEssentiaType(final EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getEssentiaAmount(final EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        if (thaumatorium == null) {
            return 0;
        }
        return thaumatorium.takeEssentia(aspect, amount, face);
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        if (thaumatorium == null) {
            return 0;
        }
        return thaumatorium.addEssentia(aspect, amount, face);
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public AspectList getAspects() {
        if (thaumatorium == null) {
            return null;
        }
        return thaumatorium.essentia;
    }
    
    @Override
    public void setAspects(final AspectList aspects) {
        if (thaumatorium == null) {
            return;
        }
        thaumatorium.setAspects(aspects);
    }
    
    public int getSizeInventory() {
        return 1;
    }
    
    public ItemStack getStackInSlot(final int par1) {
        if (thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return thaumatorium.getStackInSlot(par1);
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        if (thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return thaumatorium.decrStackSize(par1, par2);
    }
    
    public ItemStack removeStackFromSlot(final int par1) {
        if (thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return thaumatorium.removeStackFromSlot(par1);
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack stack2) {
        if (thaumatorium == null) {
            return;
        }
        thaumatorium.setInventorySlotContents(par1, stack2);
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUsableByPlayer(final EntityPlayer par1EntityPlayer) {
        return world.getTileEntity(pos) == this && par1EntityPlayer.getDistanceSqToCenter(pos) <= 64.0;
    }
    
    public boolean isItemValidForSlot(final int par1, final ItemStack stack2) {
        return true;
    }
    
    public int[] getSlotsForFace(final EnumFacing side) {
        return new int[] { 0 };
    }
    
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction) {
        return true;
    }
    
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing direction) {
        return true;
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
        thaumatorium.clear();
    }
    
    public String getName() {
        return null;
    }
    
    public boolean hasCustomName() {
        return false;
    }
    
    public ITextComponent getDisplayName() {
        return null;
    }
    
    public boolean isEmpty() {
        return thaumatorium.isEmpty();
    }
}
