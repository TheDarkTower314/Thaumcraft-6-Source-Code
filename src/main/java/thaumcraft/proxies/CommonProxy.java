package thaumcraft.proxies;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.BehaviorDispenseAlumetum;
import thaumcraft.common.lib.InternalMethodHandler;
import thaumcraft.common.lib.capabilities.PlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerWarp;
import thaumcraft.common.lib.events.CraftingEvents;
import thaumcraft.common.lib.events.WorldEvents;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.CropUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.ThaumcraftWorldGenerator;
import thaumcraft.common.world.biomes.BiomeHandler;


public class CommonProxy implements IGuiHandler, IProxy
{
    ProxyGUI proxyGUI;
    
    public CommonProxy() {
        proxyGUI = new ProxyGUI();
    }
    
    public void preInit(FMLPreInitializationEvent event) {
        event.getModMetadata().version = "6.1.BETA26";
        Thaumcraft.instance.modDir = event.getModConfigurationDirectory();
        ThaumcraftApi.internalMethods = new InternalMethodHandler();
        PlayerKnowledge.preInit();
        PlayerWarp.preInit();
        PacketHandler.preInit();
        MinecraftForge.TERRAIN_GEN_BUS.register(WorldEvents.INSTANCE);
        GameRegistry.registerFuelHandler(new CraftingEvents());
        GameRegistry.registerWorldGenerator(ThaumcraftWorldGenerator.INSTANCE, 0);
        MinecraftForge.EVENT_BUS.register(Thaumcraft.instance);
    }
    
    public void init(FMLInitializationEvent event) {
        ConfigItems.init();
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemsTC.alumentum, new BehaviorDispenseAlumetum());
        NetworkRegistry.INSTANCE.registerGuiHandler(Thaumcraft.instance, this);
        ConfigResearch.init();
        ConfigManager.sync("thaumcraft", Config.Type.INSTANCE);
        ConfigRecipes.initializeSmelting();
    }
    
    public void postInit(FMLPostInitializationEvent event) {
        ConfigEntities.postInitEntitySpawns();
        ConfigAspects.postInit();
        ConfigRecipes.postAspects();
        ModConfig.postInitLoot();
        ModConfig.postInitMisc();
        ConfigRecipes.compileGroups();
        ConfigResearch.postInit();
    }
    
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return proxyGUI.getClientGuiElement(ID, player, world, x, y, z);
    }
    
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return proxyGUI.getServerGuiElement(ID, player, world, x, y, z);
    }
    
    public boolean isShiftKeyDown() {
        return false;
    }
    
    public World getClientWorld() {
        return null;
    }
    
    public void registerModel(ItemBlock itemBlock) {
    }
    
    public void checkInterModComs(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.key.equals("portableHoleBlacklist") && message.isStringMessage()) {
                BlockUtils.portableHoleBlackList.add(message.getStringValue());
            }
            if (message.key.equals("harvestStandardCrop") && message.isItemStackMessage()) {
                ItemStack crop = message.getItemStackValue();
                CropUtils.addStandardCrop(crop, crop.getItemDamage());
            }
            if (message.key.equals("harvestClickableCrop") && message.isItemStackMessage()) {
                ItemStack crop = message.getItemStackValue();
                CropUtils.addClickableCrop(crop, crop.getItemDamage());
            }
            if (message.key.equals("harvestStackedCrop") && message.isItemStackMessage()) {
                ItemStack crop = message.getItemStackValue();
                CropUtils.addStackedCrop(crop, crop.getItemDamage());
            }
            if (message.key.equals("nativeCluster") && message.isStringMessage()) {
                String[] t = message.getStringValue().split(",");
                if (t != null && t.length == 5) {
                    try {
                        ItemStack ore = new ItemStack(Item.getItemById(Integer.parseInt(t[0])), 1, Integer.parseInt(t[1]));
                        ItemStack cluster = new ItemStack(Item.getItemById(Integer.parseInt(t[2])), 1, Integer.parseInt(t[3]));
                        Utils.addSpecialMiningResult(ore, cluster, Float.parseFloat(t[4]));
                    }
                    catch (Exception ex) {}
                }
            }
            if (message.key.equals("lampBlacklist") && message.isItemStackMessage()) {
                ItemStack crop = message.getItemStackValue();
                CropUtils.blacklistLamp(crop, crop.getItemDamage());
            }
            if (message.key.equals("dimensionBlacklist") && message.isStringMessage()) {
                String[] t = message.getStringValue().split(":");
                if (t != null && t.length == 2) {
                    try {
                        BiomeHandler.addDimBlacklist(Integer.parseInt(t[0]), Integer.parseInt(t[1]));
                    }
                    catch (Exception ex2) {}
                }
            }
            if (message.key.equals("biomeBlacklist") && message.isStringMessage()) {
                String[] t = message.getStringValue().split(":");
                if (t != null && t.length == 2 && Biome.getBiome(Integer.parseInt(t[0])) != null) {
                    try {
                        BiomeHandler.addBiomeBlacklist(Integer.parseInt(t[0]), Integer.parseInt(t[1]));
                    }
                    catch (Exception ex3) {}
                }
            }
            if (message.key.equals("championWhiteList") && message.isStringMessage()) {
                try {
                    String[] t = message.getStringValue().split(":");
                    Class oclass = EntityList.getClassFromName(t[0]);
                    if (oclass == null) {
                        continue;
                    }
                    ConfigEntities.championModWhitelist.put(oclass, Integer.parseInt(t[1]));
                }
                catch (Exception e) {
                    Thaumcraft.log.error("Failed to Whitelist [" + message.getStringValue() + "] with [ championWhiteList ] message.");
                }
            }
        }
    }
    
    public World getWorld(int dim) {
        return null;
    }
    
    public boolean getSingleplayer() {
        return false;
    }
}
