// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.entities.monster.EntityPech;
import net.minecraft.inventory.InventoryBasic;

public class InventoryPech extends InventoryBasic
{
    private EntityPech theMerchant;
    private EntityPlayer thePlayer;
    
    public InventoryPech(IInventoryChangedListener listener, EntityPlayer par1EntityPlayer, EntityPech par2IMerchant) {
        super("container.pech", false, 5);
        addInventoryChangeListener(listener);
        thePlayer = par1EntityPlayer;
        theMerchant = par2IMerchant;
    }
    
    public boolean isUsableByPlayer(EntityPlayer player) {
        return theMerchant.isTamed();
    }
    
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0;
    }
}
