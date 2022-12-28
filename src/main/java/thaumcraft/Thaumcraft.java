package thaumcraft;
import java.io.File;
import net.minecraft.command.ICommand;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.common.lib.CommandThaumcraft;
import thaumcraft.proxies.IProxy;


@Mod(modid = "thaumcraft", name = "Thaumcraft", version = "6.1.BETA26", dependencies = "required-after:forge@[14.23.5.2768,);required-after:baubles@[1.5.2,)", acceptedMinecraftVersions = "[1.12.2]")
public class Thaumcraft
{
    public static String MODID = "thaumcraft";
    public static String MODNAME = "Thaumcraft";
    public static String VERSION = "6.1.BETA26";
    @SidedProxy(clientSide = "thaumcraft.proxies.ClientProxy", serverSide = "thaumcraft.proxies.ServerProxy")
    public static IProxy proxy;
    @Mod.Instance("thaumcraft")
    public static Thaumcraft instance;
    public File modDir;
    public static Logger log;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Thaumcraft.proxy.preInit(event);
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Thaumcraft.proxy.init(event);
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Thaumcraft.proxy.postInit(event);
    }
    
    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandThaumcraft());
    }
    
    @Mod.EventHandler
    public void interModComs(FMLInterModComms.IMCEvent event) {
        Thaumcraft.proxy.checkInterModComs(event);
    }
    
    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals("thaumcraft")) {
            ConfigManager.sync("thaumcraft", Config.Type.INSTANCE);
        }
    }
    
    static {
        log = LogManager.getLogger("thaumcraft".toUpperCase());
        FluidRegistry.enableUniversalBucket();
    }
}
