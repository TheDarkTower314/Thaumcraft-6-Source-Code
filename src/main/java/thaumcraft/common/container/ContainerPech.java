// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;
import java.util.ArrayList;
import thaumcraft.common.lib.SoundsTC;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import thaumcraft.common.container.slot.SlotOutput;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.entities.monster.EntityPech;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Container;

public class ContainerPech extends Container implements IInventoryChangedListener
{
    private EntityPech pech;
    private InventoryPech inventory;
    private EntityPlayer player;
    private final World theWorld;
    
    public ContainerPech(final InventoryPlayer par1InventoryPlayer, final World par3World, final EntityPech par2IMerchant) {
        this.pech = par2IMerchant;
        this.theWorld = par3World;
        this.player = par1InventoryPlayer.player;
        this.inventory = new InventoryPech(this, par1InventoryPlayer.player, par2IMerchant);
        this.pech.trading = true;
        this.addSlotToContainer(new Slot(this.inventory, 0, 36, 29));
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlotToContainer(new SlotOutput(this.inventory, 1 + j + i * 2, 106 + 18 * j, 20 + 18 * i));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    public InventoryPech getMerchantInventory() {
        return this.inventory;
    }
    
    public void onInventoryChanged(final IInventory invBasic) {
    }
    
    public boolean enchantItem(final EntityPlayer par1EntityPlayer, final int par2) {
        if (par2 == 0) {
            this.generateContents();
            return true;
        }
        return super.enchantItem(par1EntityPlayer, par2);
    }
    
    private boolean hasStuffInPack() {
        for (final ItemStack stack : this.pech.loot) {
            if (stack != null && !stack.isEmpty() && stack.getCount() > 0) {
                return true;
            }
        }
        return false;
    }
    
    private void generateContents() {
        if (!this.theWorld.isRemote && !this.inventory.getStackInSlot(0).isEmpty() && this.inventory.getStackInSlot(1).isEmpty() && this.inventory.getStackInSlot(2).isEmpty() && this.inventory.getStackInSlot(3).isEmpty() && this.inventory.getStackInSlot(4).isEmpty() && this.pech.isValued(this.inventory.getStackInSlot(0))) {
            int value = this.pech.getValue(this.inventory.getStackInSlot(0));
            if (this.theWorld.rand.nextInt(100) <= value / 2) {
                this.pech.setTamed(false);
                this.pech.playSound(SoundsTC.pech_trade, 0.4f, 1.0f);
            }
            if (this.theWorld.rand.nextInt(5) == 0) {
                value += this.theWorld.rand.nextInt(3);
            }
            else if (this.theWorld.rand.nextBoolean()) {
                value -= this.theWorld.rand.nextInt(3);
            }
            final EntityPech pech = this.pech;
            final ArrayList<List> pos = EntityPech.tradeInventory.get(this.pech.getPechType());
            while (value > 0) {
                final int am = Math.min(5, Math.max((value + 1) / 2, this.theWorld.rand.nextInt(value) + 1));
                value -= am;
                if (am == 1 && this.theWorld.rand.nextBoolean() && this.hasStuffInPack()) {
                    final ArrayList<Integer> loot = new ArrayList<Integer>();
                    for (int a = 0; a < this.pech.loot.size(); ++a) {
                        if (this.pech.loot.get(a) != null && !this.pech.loot.get(a).isEmpty() && this.pech.loot.get(a).getCount() > 0) {
                            loot.add(a);
                        }
                    }
                    final int r = loot.get(this.theWorld.rand.nextInt(loot.size()));
                    final ItemStack is = this.pech.loot.get(r).copy();
                    is.setCount(1);
                    this.addStack(is);
                    this.pech.loot.get(r).shrink(1);
                    if (this.pech.loot.get(r).getCount() > 0) {
                        continue;
                    }
                    this.pech.loot.set(r, ItemStack.EMPTY);
                }
                else {
                    if (am >= 4 && this.theWorld.rand.nextBoolean()) {
                        continue;
                    }
                    List it = null;
                    do {
                        it = pos.get(this.theWorld.rand.nextInt(pos.size()));
                    } while ((int)it.get(0) != am);
                    final ItemStack is2 = ((ItemStack)it.get(1)).copy();
                    is2.onCrafting(this.theWorld, this.player, 0);
                    this.addStack(is2);
                }
            }
            this.inventory.decrStackSize(0, 1);
        }
    }
    
    private void addStack(final ItemStack s) {
        for (int a = 1; a < 5; ++a) {
            if (this.inventory.getStackInSlot(a).isEmpty()) {
                this.inventory.setInventorySlotContents(a, s);
                break;
            }
            if (this.inventory.getStackInSlot(a).isItemEqual(s) && this.inventory.getStackInSlot(a).getCount() + s.getCount() < this.inventory.getStackInSlot(a).getMaxStackSize()) {
                this.inventory.getStackInSlot(a).grow(s.getCount());
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(final int par1, final int par2) {
    }
    
    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.pech.isTamed();
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        final Slot slot = this.inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            final ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (par2 == 0) {
                if (!this.mergeItemStack(itemstack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (par2 >= 1 && par2 < 5) {
                if (!this.mergeItemStack(itemstack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (par2 != 0 && par2 >= 5 && par2 < 41 && !this.mergeItemStack(itemstack2, 0, 1, true)) {
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
    
    public void onContainerClosed(final EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        this.pech.trading = false;
        if (!this.theWorld.isRemote) {
            for (int a = 0; a < 5; ++a) {
                final ItemStack itemstack = this.inventory.removeStackFromSlot(a);
                if (itemstack != null) {
                    final EntityItem ei = par1EntityPlayer.dropItem(itemstack, false);
                    if (ei != null) {
                        ei.setThrower("PechDrop");
                    }
                }
            }
        }
    }
}
