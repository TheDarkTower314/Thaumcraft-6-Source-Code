package thaumcraft.common.container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.container.slot.SlotTurretBasic;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;


public class ContainerTurretAdvanced extends Container
{
    private EntityTurretCrossbowAdvanced turret;
    private EntityPlayer player;
    private World theWorld;
    
    public ContainerTurretAdvanced(InventoryPlayer par1InventoryPlayer, World par3World, EntityTurretCrossbowAdvanced ent) {
        turret = ent;
        theWorld = par3World;
        player = par1InventoryPlayer.player;
        addSlotToContainer(new SlotTurretBasic(turret, 0, 42, 29));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    public boolean enchantItem(EntityPlayer par1EntityPlayer, int par2) {
        if (par2 == 1) {
            turret.setTargetAnimal(!turret.getTargetAnimal());
            return true;
        }
        if (par2 == 2) {
            turret.setTargetMob(!turret.getTargetMob());
            return true;
        }
        if (par2 == 3) {
            turret.setTargetPlayer(!turret.getTargetPlayer());
            return true;
        }
        if (par2 == 4) {
            turret.setTargetFriendly(!turret.getTargetFriendly());
            return true;
        }
        return super.enchantItem(par1EntityPlayer, par2);
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
    }
    
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return true;
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
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
