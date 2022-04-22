// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import thaumcraft.common.container.slot.SlotArcaneBorePickaxe;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.entities.construct.EntityArcaneBore;
import net.minecraft.inventory.Container;

public class ContainerArcaneBore extends Container
{
    private EntityArcaneBore turret;
    private EntityPlayer player;
    private final World theWorld;
    
    public ContainerArcaneBore(final InventoryPlayer par1InventoryPlayer, final World par3World, final EntityArcaneBore ent) {
        turret = ent;
        theWorld = par3World;
        player = par1InventoryPlayer.player;
        addSlotToContainer(new SlotArcaneBorePickaxe(turret, 0, 80, 29));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    public boolean enchantItem(final EntityPlayer par1EntityPlayer, final int par2) {
        return par2 == 0 || super.enchantItem(par1EntityPlayer, par2);
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(final int par1, final int par2) {
    }
    
    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return true;
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int slot) {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slotObject = inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            final ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot == 0) {
                if (!mergeItemStack(stackInSlot, 1, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!mergeItemStack(stackInSlot, 0, 1, false)) {
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
}
