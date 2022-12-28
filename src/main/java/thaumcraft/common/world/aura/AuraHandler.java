package thaumcraft.common.world.aura;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.utils.PosXY;
import thaumcraft.common.world.biomes.BiomeHandler;


public class AuraHandler
{
    public static int AURA_CEILING = 500;
    static ConcurrentHashMap<Integer, AuraWorld> auras;
    public static ConcurrentHashMap<Integer, CopyOnWriteArrayList<ChunkPos>> dirtyChunks;
    public static ConcurrentHashMap<Integer, BlockPos> riftTrigger;
    
    public static AuraWorld getAuraWorld(int dim) {
        return AuraHandler.auras.get(dim);
    }
    
    public static AuraChunk getAuraChunk(int dim, int x, int y) {
        if (AuraHandler.auras.containsKey(dim)) {
            return AuraHandler.auras.get(dim).getAuraChunkAt(x, y);
        }
        addAuraWorld(dim);
        if (AuraHandler.auras.containsKey(dim)) {
            return AuraHandler.auras.get(dim).getAuraChunkAt(x, y);
        }
        return null;
    }
    
    public static void addAuraWorld(int dim) {
        if (!AuraHandler.auras.containsKey(dim)) {
            AuraHandler.auras.put(dim, new AuraWorld(dim));
            Thaumcraft.log.info("Creating aura cache for world " + dim);
        }
    }
    
    public static void removeAuraWorld(int dim) {
        AuraHandler.auras.remove(dim);
        Thaumcraft.log.info("Removing aura cache for world " + dim);
    }
    
    public static void addAuraChunk(int dim, Chunk chunk, short base, float vis, float flux) {
        AuraWorld aw = AuraHandler.auras.get(dim);
        if (aw == null) {
            aw = new AuraWorld(dim);
        }
        aw.getAuraChunks().put(new PosXY(chunk.x, chunk.z), new AuraChunk(chunk, base, vis, flux));
        AuraHandler.auras.put(dim, aw);
    }
    
    public static void removeAuraChunk(int dim, int x, int y) {
        AuraWorld aw = AuraHandler.auras.get(dim);
        if (aw != null) {
            aw.getAuraChunks().remove(new PosXY(x, y));
        }
    }
    
    public static float getTotalAura(World world, BlockPos pos) {
        AuraChunk ac = getAuraChunk(world.provider.getDimension(), pos.getX() >> 4, pos.getZ() >> 4);
        return (ac != null) ? (ac.getVis() + ac.getFlux()) : 0.0f;
    }
    
    public static float getFluxSaturation(World world, BlockPos pos) {
        AuraChunk ac = getAuraChunk(world.provider.getDimension(), pos.getX() >> 4, pos.getZ() >> 4);
        return (ac != null) ? (ac.getFlux() / ac.getBase()) : 0.0f;
    }
    
    public static float getVis(World world, BlockPos pos) {
        AuraChunk ac = getAuraChunk(world.provider.getDimension(), pos.getX() >> 4, pos.getZ() >> 4);
        return (ac != null) ? ac.getVis() : 0.0f;
    }
    
    public static float getFlux(World world, BlockPos pos) {
        AuraChunk ac = getAuraChunk(world.provider.getDimension(), pos.getX() >> 4, pos.getZ() >> 4);
        return (ac != null) ? ac.getFlux() : 0.0f;
    }
    
    public static int getAuraBase(World world, BlockPos pos) {
        AuraChunk ac = getAuraChunk(world.provider.getDimension(), pos.getX() >> 4, pos.getZ() >> 4);
        return (ac != null) ? ac.getBase() : 0;
    }
    
    public static boolean shouldPreserveAura(World world, EntityPlayer player, BlockPos pos) {
        return (player == null || ThaumcraftCapabilities.getKnowledge(player).isResearchComplete("AURAPRESERVE")) && getVis(world, pos) / getAuraBase(world, pos) < 0.1;
    }
    
    public static void addVis(World world, BlockPos pos, float amount) {
        if (amount < 0.0f) {
            return;
        }
        try {
            AuraChunk ac = getAuraChunk(world.provider.getDimension(), pos.getX() >> 4, pos.getZ() >> 4);
            modifyVisInChunk(ac, amount, true);
        }
        catch (Exception ex) {}
    }
    
    public static void addFlux(World world, BlockPos pos, float amount) {
        if (amount < 0.0f) {
            return;
        }
        try {
            AuraChunk ac = getAuraChunk(world.provider.getDimension(), pos.getX() >> 4, pos.getZ() >> 4);
            modifyFluxInChunk(ac, amount, true);
        }
        catch (Exception ex) {}
    }
    
    public static float drainVis(World world, BlockPos pos, float amount, boolean simulate) {
        boolean didit = false;
        try {
            AuraChunk ac = getAuraChunk(world.provider.getDimension(), pos.getX() >> 4, pos.getZ() >> 4);
            if (amount > ac.getVis()) {
                amount = ac.getVis();
            }
            didit = modifyVisInChunk(ac, -amount, !simulate);
        }
        catch (Exception ex) {}
        return didit ? amount : 0.0f;
    }
    
    public static float drainFlux(World world, BlockPos pos, float amount, boolean simulate) {
        boolean didit = false;
        try {
            AuraChunk ac = getAuraChunk(world.provider.getDimension(), pos.getX() >> 4, pos.getZ() >> 4);
            if (amount > ac.getFlux()) {
                amount = ac.getFlux();
            }
            didit = modifyFluxInChunk(ac, -amount, !simulate);
        }
        catch (Exception ex) {}
        return didit ? amount : 0.0f;
    }
    
    public static boolean modifyVisInChunk(AuraChunk ac, float amount, boolean doit) {
        if (ac != null) {
            if (doit) {
                ac.setVis(Math.max(0.0f, ac.getVis() + amount));
            }
            return true;
        }
        return false;
    }
    
    private static boolean modifyFluxInChunk(AuraChunk ac, float amount, boolean doit) {
        if (ac != null) {
            if (doit) {
                ac.setFlux(Math.max(0.0f, ac.getFlux() + amount));
            }
            return true;
        }
        return false;
    }
    
    public static void generateAura(Chunk chunk, Random rand) {
        Biome bgb = chunk.getWorld().getBiome(new BlockPos(chunk.x * 16 + 8, 50, chunk.z * 16 + 8));
        if (BiomeHandler.getBiomeBlacklist(Biome.getIdForBiome(bgb)) != -1) {
            return;
        }
        float life = BiomeHandler.getBiomeAuraModifier(bgb);
        for (int a = 0; a < 4; ++a) {
            EnumFacing dir = EnumFacing.getHorizontal(a);
            Biome bgb2 = chunk.getWorld().getBiome(new BlockPos((chunk.x + dir.getFrontOffsetX()) * 16 + 8, 50, (chunk.z + dir.getFrontOffsetZ()) * 16 + 8));
            life += BiomeHandler.getBiomeAuraModifier(bgb2);
        }
        life /= 5.0f;
        float noise = (float)(1.0 + rand.nextGaussian() * 0.10000000149011612);
        short base = (short)(life * 500.0f * noise);
        base = (short)MathHelper.clamp(base, 0, 500);
        addAuraChunk(chunk.getWorld().provider.getDimension(), chunk, base, base, 0.0f);
    }
    
    static {
        AuraHandler.auras = new ConcurrentHashMap<Integer, AuraWorld>();
        AuraHandler.dirtyChunks = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<ChunkPos>>();
        AuraHandler.riftTrigger = new ConcurrentHashMap<Integer, BlockPos>();
    }
}
