package thaumcraft.common.tiles.crafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileThaumatoriumTop extends TileThaumcraft implements IAspectContainer, IEssentiaTransport, ISidedInventory, ITickable
{
    public TileThaumatorium thaumatorium;
    
    public TileThaumatoriumTop() {
        thaumatorium = null;
    }
    
    public void update() {
        if (thaumatorium == null) {
            TileEntity tile = world.getTileEntity(pos.down());
            if (tile != null && tile instanceof TileThaumatorium) {
                thaumatorium = (TileThaumatorium)tile;
            }
        }
    }
    
    @Override
    public int addToContainer(Aspect tt, int am) {
        if (thaumatorium == null) {
            return am;
        }
        return thaumatorium.addToContainer(tt, am);
    }
    
    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        return thaumatorium != null && thaumatorium.takeFromContainer(tt, am);
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tt, int am) {
        return thaumatorium != null && thaumatorium.doesContainerContainAmount(tt, am);
    }
    
    @Override
    public int containerContains(Aspect tt) {
        if (thaumatorium == null) {
            return 0;
        }
        return thaumatorium.containerContains(tt);
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    @Override
    public boolean isConnectable(EnumFacing face) {
        return thaumatorium != null && thaumatorium.isConnectable(face);
    }
    
    @Override
    public boolean canInputFrom(EnumFacing face) {
        return thaumatorium != null && thaumatorium.canInputFrom(face);
    }
    
    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
        if (thaumatorium == null) {
            return;
        }
        thaumatorium.setSuction(aspect, amount);
    }
    
    @Override
    public Aspect getSuctionType(EnumFacing loc) {
        if (thaumatorium == null) {
            return null;
        }
        return thaumatorium.getSuctionType(loc);
    }
    
    @Override
    public int getSuctionAmount(EnumFacing loc) {
        if (thaumatorium == null) {
            return 0;
        }
        return thaumatorium.getSuctionAmount(loc);
    }
    
    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (thaumatorium == null) {
            return 0;
        }
        return thaumatorium.takeEssentia(aspect, amount, face);
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
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
    public void setAspects(AspectList aspects) {
        if (thaumatorium == null) {
            return;
        }
        thaumatorium.setAspects(aspects);
    }
    
    public int getSizeInventory() {
        return 1;
    }
    
    public ItemStack getStackInSlot(int par1) {
        if (thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return thaumatorium.getStackInSlot(par1);
    }
    
    public ItemStack decrStackSize(int par1, int par2) {
        if (thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return thaumatorium.decrStackSize(par1, par2);
    }
    
    public ItemStack removeStackFromSlot(int par1) {
        if (thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return thaumatorium.removeStackFromSlot(par1);
    }
    
    public void setInventorySlotContents(int par1, ItemStack stack2) {
        if (thaumatorium == null) {
            return;
        }
        thaumatorium.setInventorySlotContents(par1, stack2);
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUsableByPlayer(EntityPlayer par1EntityPlayer) {
        return world.getTileEntity(pos) == this && par1EntityPlayer.getDistanceSqToCenter(pos) <= 64.0;
    }
    
    public boolean isItemValidForSlot(int par1, ItemStack stack2) {
        return true;
    }
    
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] { 0 };
    }
    
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return true;
    }
    
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
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
