// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.world.biomes;

import thaumcraft.Thaumcraft;
import java.util.Random;
import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import thaumcraft.api.aspects.Aspect;
import java.util.Collection;
import java.util.List;
import net.minecraftforge.common.BiomeDictionary;
import java.util.HashMap;
import net.minecraft.world.biome.Biome;

public class BiomeHandler
{
    public static Biome EERIE;
    public static Biome MAGICAL_FOREST;
    public static Biome ELDRITCH;
    public static HashMap<BiomeDictionary.Type, List> biomeInfo; //list is: float, Aspect, boolean, float
    public static Collection<Aspect> c;
    public static ArrayList<Aspect> basicAspects;
    public static ArrayList<Aspect> complexAspects;
    public static HashMap<Integer, Integer> dimensionBlacklist;
    public static HashMap<Integer, Integer> biomeBlacklist;
    
    public static void registerBiomeInfo(final BiomeDictionary.Type type, final float auraLevel, final Aspect tag, final boolean greatwood, final float greatwoodchance) {
        BiomeHandler.biomeInfo.put(type, Arrays.asList(auraLevel, tag, greatwood, greatwoodchance));
    }
    
    public static float getBiomeAuraModifier(final Biome biome) {
        try {
            final Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
            float average = 0.0f;
            int count = 0;
            for (final BiomeDictionary.Type type : types) {
                average += (float)BiomeHandler.biomeInfo.get(type).get(0);
                ++count;
            }
            return average / count;
        }
        catch (final Exception ex) {
            return 0.5f;
        }
    }
    
    public static Aspect getRandomBiomeTag(final int biomeId, final Random random) {
        try {
            final Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(Biome.getBiome(biomeId));
            final BiomeDictionary.Type type = types.toArray(new BiomeDictionary.Type[0])[random.nextInt(types.size())];
            return (Aspect) BiomeHandler.biomeInfo.get(type).get(1);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public static float getBiomeSupportsGreatwood(final int biomeId) {
        try {
            final Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(Biome.getBiome(biomeId));
            for (final BiomeDictionary.Type type : types) {
                if ((boolean)BiomeHandler.biomeInfo.get(type).get(2)) {
                    return (float) BiomeHandler.biomeInfo.get(type).get(3);
                }
            }
        }
        catch (final Exception ex) {}
        return 0.0f;
    }
    
    public static int getFirstFreeBiomeSlot(final int old) {
        for (int a = 0; a < Biome.REGISTRY.getKeys().size() * 2; ++a) {
            if (Biome.getBiome(a) == null) {
                Thaumcraft.log.warn("Biome slot " + old + " already occupied. Using first free biome slot at " + a);
                return a;
            }
        }
        return -1;
    }
    
    public static void addDimBlacklist(final int dim, final int level) {
        BiomeHandler.dimensionBlacklist.put(dim, level);
    }
    
    public static int getDimBlacklist(final int dim) {
        if (!BiomeHandler.dimensionBlacklist.containsKey(dim)) {
            return -1;
        }
        return BiomeHandler.dimensionBlacklist.get(dim);
    }
    
    public static void addBiomeBlacklist(final int biome, final int level) {
        BiomeHandler.biomeBlacklist.put(biome, level);
    }
    
    public static int getBiomeBlacklist(final int biome) {
        if (!BiomeHandler.biomeBlacklist.containsKey(biome)) {
            return -1;
        }
        return BiomeHandler.biomeBlacklist.get(biome);
    }
    
    public static void registerBiomes() {
        BiomeDictionary.addTypes(BiomeHandler.EERIE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
        BiomeDictionary.addTypes(BiomeHandler.ELDRITCH, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.END);
        BiomeDictionary.addTypes(BiomeHandler.MAGICAL_FOREST, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.FOREST);
        registerBiomeInfo(BiomeDictionary.Type.WATER, 0.33f, Aspect.WATER, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.OCEAN, 0.33f, Aspect.WATER, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.RIVER, 0.4f, Aspect.WATER, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.WET, 0.4f, Aspect.WATER, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.LUSH, 0.5f, Aspect.WATER, true, 0.5f);
        registerBiomeInfo(BiomeDictionary.Type.HOT, 0.33f, Aspect.FIRE, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.DRY, 0.25f, Aspect.FIRE, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.NETHER, 0.125f, Aspect.FIRE, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.MESA, 0.33f, Aspect.FIRE, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.SPOOKY, 0.5f, Aspect.FIRE, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.DENSE, 0.4f, Aspect.ORDER, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.SNOWY, 0.25f, Aspect.ORDER, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.COLD, 0.25f, Aspect.ORDER, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.MUSHROOM, 0.75f, Aspect.ORDER, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.MAGICAL, 0.75f, Aspect.ORDER, true, 1.0f);
        registerBiomeInfo(BiomeDictionary.Type.CONIFEROUS, 0.33f, Aspect.EARTH, true, 0.2f);
        registerBiomeInfo(BiomeDictionary.Type.FOREST, 0.5f, Aspect.EARTH, true, 1.0f);
        registerBiomeInfo(BiomeDictionary.Type.SANDY, 0.25f, Aspect.EARTH, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.BEACH, 0.3f, Aspect.EARTH, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.JUNGLE, 0.6f, Aspect.EARTH, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.SAVANNA, 0.25f, Aspect.AIR, true, 0.2f);
        registerBiomeInfo(BiomeDictionary.Type.MOUNTAIN, 0.3f, Aspect.AIR, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.HILLS, 0.33f, Aspect.AIR, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.PLAINS, 0.3f, Aspect.AIR, true, 0.2f);
        registerBiomeInfo(BiomeDictionary.Type.END, 0.125f, Aspect.AIR, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.DRY, 0.125f, Aspect.ENTROPY, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.SPARSE, 0.2f, Aspect.ENTROPY, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.SWAMP, 0.5f, Aspect.ENTROPY, true, 0.2f);
        registerBiomeInfo(BiomeDictionary.Type.WASTELAND, 0.125f, Aspect.ENTROPY, false, 0.0f);
        registerBiomeInfo(BiomeDictionary.Type.DEAD, 0.1f, Aspect.ENTROPY, false, 0.0f);
    }
    
    static {
        BiomeHandler.biomeInfo = new HashMap<BiomeDictionary.Type, List>();
        BiomeHandler.c = Aspect.aspects.values();
        BiomeHandler.basicAspects = new ArrayList<Aspect>();
        BiomeHandler.complexAspects = new ArrayList<Aspect>();
        BiomeHandler.dimensionBlacklist = new HashMap<Integer, Integer>();
        BiomeHandler.biomeBlacklist = new HashMap<Integer, Integer>();
    }
}
