package thaumcraft;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.potions.PotionBlurredVision;
import thaumcraft.common.lib.potions.PotionDeathGaze;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;
import thaumcraft.common.lib.potions.PotionSunScorned;
import thaumcraft.common.lib.potions.PotionThaumarhia;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;
import thaumcraft.common.lib.potions.PotionWarpWard;
import thaumcraft.common.world.biomes.BiomeGenEerie;
import thaumcraft.common.world.biomes.BiomeGenEldritch;
import thaumcraft.common.world.biomes.BiomeGenMagicalForest;
import thaumcraft.common.world.biomes.BiomeHandler;
import thaumcraft.proxies.ProxyBlock;


@Mod.EventBusSubscriber
public class Registrar
{
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ConfigBlocks.initBlocks(event.getRegistry());
        ConfigBlocks.initTileEntities();
        ConfigBlocks.initMisc();
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerBlocksClient(RegistryEvent.Register<Block> event) {
        ProxyBlock.setupBlocksClient(event.getRegistry());
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        ConfigItems.preInitSeals();
        ConfigItems.initItems(event.getRegistry());
        ConfigItems.initMisc();
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerItemsClient(RegistryEvent.Register<Item> event) {
        ConfigItems.initModelsAndVariants();
    }
    
    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        ConfigEntities.initEntities(event.getRegistry());
    }
    
    @SubscribeEvent
    public static void registerVanillaRecipes(RegistryEvent.Register<IRecipe> event) {
        ModConfig.modCompatibility();
        ConfigRecipes.initializeNormalRecipes(event.getRegistry());
        ConfigRecipes.initializeArcaneRecipes(event.getRegistry());
        ConfigRecipes.initializeInfusionRecipes();
        ConfigRecipes.initializeAlchemyRecipes();
        ConfigRecipes.initializeCompoundRecipes();
    }
    
    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        PotionFluxTaint.instance = new PotionFluxTaint(true, 6697847).setRegistryName("fluxTaint");
        PotionVisExhaust.instance = new PotionVisExhaust(true, 6702199).setRegistryName("visExhaust");
        PotionInfectiousVisExhaust.instance = new PotionInfectiousVisExhaust(true, 6706551).setRegistryName("infectiousVisExhaust");
        PotionUnnaturalHunger.instance = new PotionUnnaturalHunger(true, 4482611).setRegistryName("unnaturalHunger");
        PotionWarpWard.instance = new PotionWarpWard(false, 14742263).setRegistryName("warpWard");
        PotionDeathGaze.instance = new PotionDeathGaze(true, 6702131).setRegistryName("deathGaze");
        PotionBlurredVision.instance = new PotionBlurredVision(true, 8421504).setRegistryName("blurredVision");
        PotionSunScorned.instance = new PotionSunScorned(true, 16308330).setRegistryName("sunScorned");
        PotionThaumarhia.instance = new PotionThaumarhia(true, 6702199).setRegistryName("thaumarhia");
        event.getRegistry().register(PotionFluxTaint.instance);
        event.getRegistry().register(PotionVisExhaust.instance);
        event.getRegistry().register(PotionInfectiousVisExhaust.instance);
        event.getRegistry().register(PotionUnnaturalHunger.instance);
        event.getRegistry().register(PotionWarpWard.instance);
        event.getRegistry().register(PotionDeathGaze.instance);
        event.getRegistry().register(PotionBlurredVision.instance);
        event.getRegistry().register(PotionSunScorned.instance);
        event.getRegistry().register(PotionThaumarhia.instance);
    }
    
    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        BiomeHandler.MAGICAL_FOREST = new BiomeGenMagicalForest(new Biome.BiomeProperties("Magical Forest").setBaseHeight(0.2f).setHeightVariation(0.3f).setTemperature(0.8f).setRainfall(0.4f));
        event.getRegistry().register(BiomeHandler.MAGICAL_FOREST);
        BiomeHandler.EERIE = new BiomeGenEerie(new Biome.BiomeProperties("Eerie").setBaseHeight(0.125f).setHeightVariation(0.4f).setTemperature(0.8f).setRainDisabled());
        event.getRegistry().register(BiomeHandler.EERIE);
        BiomeHandler.ELDRITCH = new BiomeGenEldritch(new Biome.BiomeProperties("Outer Lands").setBaseHeight(0.125f).setHeightVariation(0.15f).setTemperature(0.8f).setRainfall(0.2f));
        event.getRegistry().register(BiomeHandler.ELDRITCH);
        BiomeHandler.registerBiomes();
        if (ModConfig.CONFIG_WORLD.generateMagicForest) {
            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(BiomeHandler.MAGICAL_FOREST, ModConfig.CONFIG_WORLD.biomeMagicalForestWeight));
            BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(BiomeHandler.MAGICAL_FOREST, ModConfig.CONFIG_WORLD.biomeMagicalForestWeight));
        }
    }
    
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        SoundsTC.registerSounds(event);
        SoundsTC.registerSoundTypes();
    }
}
