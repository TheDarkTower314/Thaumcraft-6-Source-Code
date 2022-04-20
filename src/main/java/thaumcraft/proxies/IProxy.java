// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.proxies;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy
{
    void preInit(final FMLPreInitializationEvent p0);
    
    void init(final FMLInitializationEvent p0);
    
    void postInit(final FMLPostInitializationEvent p0);
    
    World getClientWorld();
    
    boolean getSingleplayer();
    
    World getWorld(final int p0);
    
    boolean isShiftKeyDown();
    
    void registerModel(final ItemBlock p0);
    
    void checkInterModComs(final FMLInterModComms.IMCEvent p0);
}
