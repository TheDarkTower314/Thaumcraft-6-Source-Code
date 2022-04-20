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
    private final EntityPech theMerchant;
    private final EntityPlayer thePlayer;
    
    public InventoryPech(final IInventoryChangedListener listener, final EntityPlayer par1EntityPlayer, final EntityPech par2IMerchant) {
        super("container.pech", false, 5);
        this.addInventoryChangeListener(listener);
        this.thePlayer = par1EntityPlayer;
        this.theMerchant = par2IMerchant;
    }
    
    public boolean isUsableByPlayer(final EntityPlayer player) {
        return this.theMerchant.isTamed();
    }
    
    public boolean isItemValidForSlot(final int index, final ItemStack stack) {
        return index == 0;
    }
}
