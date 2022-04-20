// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container.slot;

import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLiving;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;

public class SlotTurretBasic extends SlotMobEquipment
{
    public SlotTurretBasic(final EntityTurretCrossbow turret, final int par3, final int par4, final int par5) {
        super(turret, par3, par4, par5);
    }
    
    public boolean isItemValid(final ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ItemArrow;
    }
    
    @Override
    public void onSlotChanged() {
    }
}
