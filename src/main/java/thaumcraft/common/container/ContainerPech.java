package thaumcraft.common.container;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.container.slot.SlotOutput;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.lib.SoundsTC;


public class ContainerPech extends Container implements IInventoryChangedListener
{
    private EntityPech pech;
    private InventoryPech inventory;
    private EntityPlayer player;
    private World theWorld;
    
    public ContainerPech(InventoryPlayer par1InventoryPlayer, World par3World, EntityPech par2IMerchant) {
        pech = par2IMerchant;
        theWorld = par3World;
        player = par1InventoryPlayer.player;
        inventory = new InventoryPech(this, par1InventoryPlayer.player, par2IMerchant);
        pech.trading = true;
        addSlotToContainer(new Slot(inventory, 0, 36, 29));
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                addSlotToContainer(new SlotOutput(inventory, 1 + j + i * 2, 106 + 18 * j, 20 + 18 * i));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    public InventoryPech getMerchantInventory() {
        return inventory;
    }
    
    public void onInventoryChanged(IInventory invBasic) {
    }
    
    public boolean enchantItem(EntityPlayer par1EntityPlayer, int par2) {
        if (par2 == 0) {
            generateContents();
            return true;
        }
        return super.enchantItem(par1EntityPlayer, par2);
    }
    
    private boolean hasStuffInPack() {
        for (ItemStack stack : pech.loot) {
            if (stack != null && !stack.isEmpty() && stack.getCount() > 0) {
                return true;
            }
        }
        return false;
    }
    
    private void generateContents() {
        if (!theWorld.isRemote && !inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(1).isEmpty() && inventory.getStackInSlot(2).isEmpty() && inventory.getStackInSlot(3).isEmpty() && inventory.getStackInSlot(4).isEmpty() && pech.isValued(inventory.getStackInSlot(0))) {
            int value = pech.getValue(inventory.getStackInSlot(0));
            if (theWorld.rand.nextInt(100) <= value / 2) {
                pech.setTamed(false);
                pech.playSound(SoundsTC.pech_trade, 0.4f, 1.0f);
            }
            if (theWorld.rand.nextInt(5) == 0) {
                value += theWorld.rand.nextInt(3);
            }
            else if (theWorld.rand.nextBoolean()) {
                value -= theWorld.rand.nextInt(3);
            }
            EntityPech pech = this.pech;
            ArrayList<List> pos = EntityPech.tradeInventory.get(this.pech.getPechType());
            while (value > 0) {
                int am = Math.min(5, Math.max((value + 1) / 2, theWorld.rand.nextInt(value) + 1));
                value -= am;
                if (am == 1 && theWorld.rand.nextBoolean() && hasStuffInPack()) {
                    ArrayList<Integer> loot = new ArrayList<Integer>();
                    for (int a = 0; a < this.pech.loot.size(); ++a) {
                        if (this.pech.loot.get(a) != null && !this.pech.loot.get(a).isEmpty() && this.pech.loot.get(a).getCount() > 0) {
                            loot.add(a);
                        }
                    }
                    int r = loot.get(theWorld.rand.nextInt(loot.size()));
                    ItemStack is = this.pech.loot.get(r).copy();
                    is.setCount(1);
                    addStack(is);
                    this.pech.loot.get(r).shrink(1);
                    if (this.pech.loot.get(r).getCount() > 0) {
                        continue;
                    }
                    this.pech.loot.set(r, ItemStack.EMPTY);
                }
                else {
                    if (am >= 4 && theWorld.rand.nextBoolean()) {
                        continue;
                    }
                    List it = null;
                    do {
                        it = pos.get(theWorld.rand.nextInt(pos.size()));
                    } while ((int)it.get(0) != am);
                    ItemStack is2 = ((ItemStack)it.get(1)).copy();
                    is2.onCrafting(theWorld, player, 0);
                    addStack(is2);
                }
            }
            inventory.decrStackSize(0, 1);
        }
    }
    
    private void addStack(ItemStack s) {
        for (int a = 1; a < 5; ++a) {
            if (inventory.getStackInSlot(a).isEmpty()) {
                inventory.setInventorySlotContents(a, s);
                break;
            }
            if (inventory.getStackInSlot(a).isItemEqual(s) && inventory.getStackInSlot(a).getCount() + s.getCount() < inventory.getStackInSlot(a).getMaxStackSize()) {
                inventory.getStackInSlot(a).grow(s.getCount());
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
    }
    
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return pech.isTamed();
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (par2 == 0) {
                if (!mergeItemStack(itemstack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (par2 >= 1 && par2 < 5) {
                if (!mergeItemStack(itemstack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (par2 != 0 && par2 >= 5 && par2 < 41 && !mergeItemStack(itemstack2, 0, 1, true)) {
                return ItemStack.EMPTY;
            }
            if (itemstack2.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }
            if (itemstack2.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(par1EntityPlayer, itemstack2);
        }
        return itemstack;
    }
    
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        pech.trading = false;
        if (!theWorld.isRemote) {
            for (int a = 0; a < 5; ++a) {
                ItemStack itemstack = inventory.removeStackFromSlot(a);
                if (itemstack != null) {
                    EntityItem ei = par1EntityPlayer.dropItem(itemstack, false);
                    if (ei != null) {
                        ei.setThrower("PechDrop");
                    }
                }
            }
        }
    }
}
