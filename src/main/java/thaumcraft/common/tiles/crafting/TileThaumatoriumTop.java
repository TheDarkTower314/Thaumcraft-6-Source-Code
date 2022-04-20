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
        this.thaumatorium = null;
    }
    
    public void update() {
        if (this.thaumatorium == null) {
            final TileEntity tile = this.world.getTileEntity(this.pos.down());
            if (tile != null && tile instanceof TileThaumatorium) {
                this.thaumatorium = (TileThaumatorium)tile;
            }
        }
    }
    
    @Override
    public int addToContainer(final Aspect tt, final int am) {
        if (this.thaumatorium == null) {
            return am;
        }
        return this.thaumatorium.addToContainer(tt, am);
    }
    
    @Override
    public boolean takeFromContainer(final Aspect tt, final int am) {
        return this.thaumatorium != null && this.thaumatorium.takeFromContainer(tt, am);
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
        return this.thaumatorium != null && this.thaumatorium.doesContainerContainAmount(tt, am);
    }
    
    @Override
    public int containerContains(final Aspect tt) {
        if (this.thaumatorium == null) {
            return 0;
        }
        return this.thaumatorium.containerContains(tt);
    }
    
    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return true;
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return this.thaumatorium != null && this.thaumatorium.isConnectable(face);
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return this.thaumatorium != null && this.thaumatorium.canInputFrom(face);
    }
    
    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return false;
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
        if (this.thaumatorium == null) {
            return;
        }
        this.thaumatorium.setSuction(aspect, amount);
    }
    
    @Override
    public Aspect getSuctionType(final EnumFacing loc) {
        if (this.thaumatorium == null) {
            return null;
        }
        return this.thaumatorium.getSuctionType(loc);
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing loc) {
        if (this.thaumatorium == null) {
            return 0;
        }
        return this.thaumatorium.getSuctionAmount(loc);
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
        if (this.thaumatorium == null) {
            return 0;
        }
        return this.thaumatorium.takeEssentia(aspect, amount, face);
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        if (this.thaumatorium == null) {
            return 0;
        }
        return this.thaumatorium.addEssentia(aspect, amount, face);
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public AspectList getAspects() {
        if (this.thaumatorium == null) {
            return null;
        }
        return this.thaumatorium.essentia;
    }
    
    @Override
    public void setAspects(final AspectList aspects) {
        if (this.thaumatorium == null) {
            return;
        }
        this.thaumatorium.setAspects(aspects);
    }
    
    public int getSizeInventory() {
        return 1;
    }
    
    public ItemStack getStackInSlot(final int par1) {
        if (this.thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return this.thaumatorium.getStackInSlot(par1);
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        if (this.thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return this.thaumatorium.decrStackSize(par1, par2);
    }
    
    public ItemStack removeStackFromSlot(final int par1) {
        if (this.thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return this.thaumatorium.removeStackFromSlot(par1);
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack stack2) {
        if (this.thaumatorium == null) {
            return;
        }
        this.thaumatorium.setInventorySlotContents(par1, stack2);
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUsableByPlayer(final EntityPlayer par1EntityPlayer) {
        return this.world.getTileEntity(this.pos) == this && par1EntityPlayer.getDistanceSqToCenter(this.pos) <= 64.0;
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
        this.thaumatorium.clear();
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
        return this.thaumatorium.isEmpty();
    }
}
