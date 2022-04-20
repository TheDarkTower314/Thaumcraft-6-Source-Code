// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft;

import net.minecraftforge.fluids.FluidRegistry;
import org.apache.logging.log4j.LogManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraft.command.ICommand;
import thaumcraft.common.lib.CommandThaumcraft;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import java.io.File;
import net.minecraftforge.fml.common.SidedProxy;
import thaumcraft.proxies.IProxy;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "thaumcraft", name = "Thaumcraft", version = "6.1.BETA26", dependencies = "required-after:forge@[14.23.5.2768,);required-after:baubles@[1.5.2,)", acceptedMinecraftVersions = "[1.12.2]")
public class Thaumcraft
{
    public static final String MODID = "thaumcraft";
    public static final String MODNAME = "Thaumcraft";
    public static final String VERSION = "6.1.BETA26";
    @SidedProxy(clientSide = "thaumcraft.proxies.ClientProxy", serverSide = "thaumcraft.proxies.ServerProxy")
    public static IProxy proxy;
    @Mod.Instance("thaumcraft")
    public static Thaumcraft instance;
    public File modDir;
    public static final Logger log;
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        Thaumcraft.proxy.preInit(event);
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        Thaumcraft.proxy.init(event);
    }
    
    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent event) {
        Thaumcraft.proxy.postInit(event);
    }
    
    @Mod.EventHandler
    public void serverLoad(final FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandThaumcraft());
    }
    
    @Mod.EventHandler
    public void interModComs(final FMLInterModComms.IMCEvent event) {
        Thaumcraft.proxy.checkInterModComs(event);
    }
    
    @SubscribeEvent
    public void onConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals("thaumcraft")) {
            ConfigManager.sync("thaumcraft", Config.Type.INSTANCE);
        }
    }
    
    static {
        log = LogManager.getLogger("thaumcraft".toUpperCase());
        FluidRegistry.enableUniversalBucket();
    }
}
