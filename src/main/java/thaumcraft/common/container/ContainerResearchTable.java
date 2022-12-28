package thaumcraft.common.container;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.container.slot.SlotLimitedByClass;
import thaumcraft.common.container.slot.SlotLimitedByItemstack;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.TileResearchTable;


public class ContainerResearchTable extends Container
{
    public TileResearchTable tileEntity;
    String[] aspects;
    EntityPlayer player;
    static HashMap<Integer, Long> antiSpam;
    
    public ContainerResearchTable(InventoryPlayer iinventory, TileResearchTable iinventory1) {
        player = iinventory.player;
        tileEntity = iinventory1;
        aspects = Aspect.aspects.keySet().toArray(new String[0]);
        addSlotToContainer(new SlotLimitedByClass(IScribeTools.class, iinventory1, 0, 16, 15));
        addSlotToContainer(new SlotLimitedByItemstack(new ItemStack(Items.PAPER), iinventory1, 1, 224, 16));
        bindPlayerInventory(iinventory);
    }
    
    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 77 + j * 18, 190 + i * 18));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                addSlotToContainer(new Slot(inventoryPlayer, i + j * 3, 20 + i * 18, 190 + j * 18));
            }
        }
    }
    
    public boolean enchantItem(EntityPlayer playerIn, int button) {
        if (button == 1) {
            if (tileEntity.data.lastDraw != null) {
                tileEntity.data.savedCards.add(tileEntity.data.lastDraw.card.getSeed());
            }
            for (ResearchTableData.CardChoice cc : tileEntity.data.cardChoices) {
                if (cc.selected) {
                    tileEntity.data.lastDraw = cc;
                    break;
                }
            }
            tileEntity.data.cardChoices.clear();
            tileEntity.syncTile(false);
            return true;
        }
        if (button == 4 || button == 5 || button == 6) {
            long tn = System.currentTimeMillis();
            long to = 0L;
            if (ContainerResearchTable.antiSpam.containsKey(playerIn.getEntityId())) {
                to = ContainerResearchTable.antiSpam.get(playerIn.getEntityId());
            }
            if (tn - to < 333L) {
                return false;
            }
            ContainerResearchTable.antiSpam.put(playerIn.getEntityId(), tn);
            try {
                TheorycraftCard card = tileEntity.data.cardChoices.get(button - 4).card;
                if (card.getRequiredItems() != null) {
                    for (ItemStack stack : card.getRequiredItems()) {
                        if (stack != null && !stack.isEmpty() && !InventoryUtils.isPlayerCarryingAmount(player, stack, true)) {
                            return false;
                        }
                    }
                    if (card.getRequiredItemsConsumed() != null && card.getRequiredItemsConsumed().length == card.getRequiredItems().length) {
                        for (int a = 0; a < card.getRequiredItems().length; ++a) {
                            if (card.getRequiredItemsConsumed()[a] && card.getRequiredItems()[a] != null && !card.getRequiredItems()[a].isEmpty()) {
                                InventoryUtils.consumePlayerItem(player, card.getRequiredItems()[a], true, true);
                            }
                        }
                    }
                }
                if (card.activate(playerIn, tileEntity.data)) {
                    tileEntity.consumeInkFromTable();
                    tileEntity.data.cardChoices.get(button - 4).selected = true;
                    tileEntity.data.addInspiration(-card.getInspirationCost());
                    tileEntity.syncTile(false);
                    return true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (button == 7 && tileEntity.data.isComplete()) {
            tileEntity.finishTheory(playerIn);
            tileEntity.syncTile(false);
            return true;
        }
        if (button == 9 && !tileEntity.data.isComplete()) {
            tileEntity.data = null;
            tileEntity.syncTile(false);
            return true;
        }
        if (button == 2 || button == 3) {
            if (tileEntity.data != null && !tileEntity.data.isComplete() && tileEntity.consumepaperFromTable()) {
                tileEntity.data.drawCards(button, playerIn);
                tileEntity.syncTile(false);
            }
            return true;
        }
        return false;
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot < 2) {
                if (!tileEntity.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 2, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!tileEntity.isItemValidForSlot(slot, stackInSlot) || !mergeItemStack(stackInSlot, 0, 2, false)) {
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
    
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUsableByPlayer(player);
    }
    
    static {
        ContainerResearchTable.antiSpam = new HashMap<Integer, Long>();
    }
}
