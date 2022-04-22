package thaumcraft.proxies;

import net.minecraftforge.common.DimensionManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }
    
    @Override
    public World getWorld(int dim) {
        return DimensionManager.getWorld(dim);
    }
}
