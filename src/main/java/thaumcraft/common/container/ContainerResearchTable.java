// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import thaumcraft.api.research.theorycraft.TheorycraftCard;
import java.util.Iterator;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.common.container.slot.SlotLimitedByItemstack;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.slot.SlotLimitedByClass;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.entity.player.InventoryPlayer;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import net.minecraft.inventory.Container;

public class ContainerResearchTable extends Container
{
    public TileResearchTable tileEntity;
    String[] aspects;
    EntityPlayer player;
    static HashMap<Integer, Long> antiSpam;
    
    public ContainerResearchTable(final InventoryPlayer iinventory, final TileResearchTable iinventory1) {
        this.player = iinventory.player;
        this.tileEntity = iinventory1;
        this.aspects = Aspect.aspects.keySet().toArray(new String[0]);
        this.addSlotToContainer(new SlotLimitedByClass(IScribeTools.class, iinventory1, 0, 16, 15));
        this.addSlotToContainer(new SlotLimitedByItemstack(new ItemStack(Items.PAPER), iinventory1, 1, 224, 16));
        this.bindPlayerInventory(iinventory);
    }
    
    protected void bindPlayerInventory(final InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 77 + j * 18, 190 + i * 18));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlotToContainer(new Slot(inventoryPlayer, i + j * 3, 20 + i * 18, 190 + j * 18));
            }
        }
    }
    
    public boolean enchantItem(final EntityPlayer playerIn, final int button) {
        if (button == 1) {
            if (this.tileEntity.data.lastDraw != null) {
                this.tileEntity.data.savedCards.add(this.tileEntity.data.lastDraw.card.getSeed());
            }
            for (final ResearchTableData.CardChoice cc : this.tileEntity.data.cardChoices) {
                if (cc.selected) {
                    this.tileEntity.data.lastDraw = cc;
                    break;
                }
            }
            this.tileEntity.data.cardChoices.clear();
            this.tileEntity.syncTile(false);
            return true;
        }
        if (button == 4 || button == 5 || button == 6) {
            final long tn = System.currentTimeMillis();
            long to = 0L;
            if (ContainerResearchTable.antiSpam.containsKey(playerIn.getEntityId())) {
                to = ContainerResearchTable.antiSpam.get(playerIn.getEntityId());
            }
            if (tn - to < 333L) {
                return false;
            }
            ContainerResearchTable.antiSpam.put(playerIn.getEntityId(), tn);
            try {
                final TheorycraftCard card = this.tileEntity.data.cardChoices.get(button - 4).card;
                if (card.getRequiredItems() != null) {
                    for (final ItemStack stack : card.getRequiredItems()) {
                        if (stack != null && !stack.isEmpty() && !InventoryUtils.isPlayerCarryingAmount(this.player, stack, true)) {
                            return false;
                        }
                    }
                    if (card.getRequiredItemsConsumed() != null && card.getRequiredItemsConsumed().length == card.getRequiredItems().length) {
                        for (int a = 0; a < card.getRequiredItems().length; ++a) {
                            if (card.getRequiredItemsConsumed()[a] && card.getRequiredItems()[a] != null && !card.getRequiredItems()[a].isEmpty()) {
                                InventoryUtils.consumePlayerItem(this.player, card.getRequiredItems()[a], true, true);
                            }
                        }
                    }
                }
                if (card.activate(playerIn, this.tileEntity.data)) {
                    this.tileEntity.consumeInkFromTable();
                    this.tileEntity.data.cardChoices.get(button - 4).selected = true;
                    this.tileEntity.data.addInspiration(-card.getInspirationCost());
                    this.tileEntity.syncTile(false);
                    return true;
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        if (button == 7 && this.tileEntity.data.isComplete()) {
            this.tileEntity.finishTheory(playerIn);
            this.tileEntity.syncTile(false);
            return true;
        }
        if (button == 9 && !this.tileEntity.data.isComplete()) {
            this.tileEntity.data = null;
            this.tileEntity.syncTile(false);
            return true;
        }
        if (button == 2 || button == 3) {
            if (this.tileEntity.data != null && !this.tileEntity.data.isComplete() && this.tileEntity.consumepaperFromTable()) {
                this.tileEntity.data.drawCards(button, playerIn);
                this.tileEntity.syncTile(false);
            }
            return true;
        }
        return false;
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int slot) {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slotObject = this.inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            final ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot < 2) {
                if (!this.tileEntity.isItemValidForSlot(slot, stackInSlot) || !this.mergeItemStack(stackInSlot, 2, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.tileEntity.isItemValidForSlot(slot, stackInSlot) || !this.mergeItemStack(stackInSlot, 0, 2, false)) {
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
    
    public boolean canInteractWith(final EntityPlayer player) {
        return this.tileEntity.isUsableByPlayer(player);
    }
    
    static {
        ContainerResearchTable.antiSpam = new HashMap<Integer, Long>();
    }
}
