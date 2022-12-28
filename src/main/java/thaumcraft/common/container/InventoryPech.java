package thaumcraft.common.container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import thaumcraft.common.entities.monster.EntityPech;


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
