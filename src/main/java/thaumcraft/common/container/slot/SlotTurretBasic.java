package thaumcraft.common.container.slot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;


public class SlotTurretBasic extends SlotMobEquipment
{
    public SlotTurretBasic(EntityTurretCrossbow turret, int par3, int par4, int par5) {
        super(turret, par3, par4, par5);
    }
    
    public boolean isItemValid(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ItemArrow;
    }
    
    @Override
    public void onSlotChanged() {
    }
}
