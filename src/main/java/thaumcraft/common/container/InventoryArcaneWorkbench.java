package thaumcraft.common.container;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.crafting.IArcaneWorkbench;


public class InventoryArcaneWorkbench extends InventoryCrafting implements IArcaneWorkbench
{
    TileEntity workbench;
    
    public InventoryArcaneWorkbench(TileEntity tileEntity, Container container) {
        super(container, 5, 3);
        workbench = tileEntity;
    }
    
    public String getName() {
        return "container.arcaneworkbench";
    }
    
    public void markDirty() {
        super.markDirty();
        workbench.markDirty();
    }
}
