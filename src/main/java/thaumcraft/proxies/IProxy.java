package thaumcraft.proxies;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


public interface IProxy
{
    void preInit(FMLPreInitializationEvent p0);
    
    void init(FMLInitializationEvent p0);
    
    void postInit(FMLPostInitializationEvent p0);
    
    World getClientWorld();
    
    boolean getSingleplayer();
    
    World getWorld(int p0);
    
    boolean isShiftKeyDown();
    
    void registerModel(ItemBlock p0);
    
    void checkInterModComs(FMLInterModComms.IMCEvent p0);
}
