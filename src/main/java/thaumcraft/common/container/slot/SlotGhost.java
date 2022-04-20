// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotGhost extends Slot
{
    int limit;
    
    public SlotGhost(final IInventory par1iInventory, final int par2, final int par3, final int par4, final int par5) {
        super(par1iInventory, par2, par3, par4);
        this.limit = Integer.MAX_VALUE;
        this.limit = par5;
    }
    
    public SlotGhost(final IInventory par1iInventory, final int par2, final int par3, final int par4) {
        super(par1iInventory, par2, par3, par4);
        this.limit = Integer.MAX_VALUE;
    }
    
    public int getSlotStackLimit() {
        return this.limit;
    }
    
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
        return false;
    }
}
