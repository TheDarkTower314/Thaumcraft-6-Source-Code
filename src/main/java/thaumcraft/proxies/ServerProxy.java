// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.proxies;

import net.minecraftforge.common.DimensionManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy
{
    @Override
    public void preInit(final FMLPreInitializationEvent event) {
        super.preInit(event);
    }
    
    @Override
    public World getWorld(final int dim) {
        return DimensionManager.getWorld(dim);
    }
}