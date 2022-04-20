// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;

public class SlotGhostFluid extends SlotGhost
{
    public SlotGhostFluid(final IInventory par1iInventory, final int par2, final int par3, final int par4) {
        super(par1iInventory, par2, par3, par4);
    }
    
    @Override
    public int getSlotStackLimit() {
        return 1;
    }
    
    public boolean isItemValid(final ItemStack stack1) {
        return FluidUtil.getFluidHandler(stack1) != null;
    }
    
    @Override
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
        return false;
    }
}
