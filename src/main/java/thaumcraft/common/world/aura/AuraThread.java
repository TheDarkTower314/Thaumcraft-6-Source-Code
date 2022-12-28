package thaumcraft.common.world.aura;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.events.ServerEvents;


public class AuraThread implements Runnable
{
    public int dim;
    private long INTERVAL = 1000L;
    private boolean stop;
    Random rand;
    private float phaseVis;
    private float phaseFlux;
    private float phaseMax;
    private long lastWorldTime;
    private float[] phaseTable;
    private float[] maxTable;
    
    public AuraThread(int dim2) {
        stop = false;
        rand = new Random(System.currentTimeMillis());
        phaseVis = 0.0f;
        phaseFlux = 0.0f;
        phaseMax = 0.0f;
        lastWorldTime = 0L;
        phaseTable = new float[] { 0.25f, 0.15f, 0.1f, 0.05f, 0.0f, 0.05f, 0.1f, 0.15f };
        maxTable = new float[] { 0.15f, 0.05f, 0.0f, -0.05f, -0.15f, -0.05f, 0.0f, 0.05f };
        dim = dim2;
    }
    
    @Override
    public void run() {
        Thaumcraft.log.info("Starting aura thread for dim " + dim);
        while (!stop) {
            if (AuraHandler.auras.isEmpty()) {
                Thaumcraft.log.warn("No auras found!");
                break;
            }
            long startTime = System.currentTimeMillis();
            AuraWorld auraWorld = AuraHandler.getAuraWorld(dim);
            if (auraWorld != null) {
                World world = DimensionManager.getWorld(dim);
                if (lastWorldTime != world.getWorldTime()) {
                    lastWorldTime = world.getWorldTime();
                    if (world != null) {
                        phaseVis = phaseTable[world.provider.getMoonPhase(world.getWorldInfo().getWorldTime())];
                        phaseMax = 1.0f + maxTable[world.provider.getMoonPhase(world.getWorldInfo().getWorldTime())];
                        phaseFlux = 0.25f - phaseVis;
                    }
                    for (AuraChunk auraChunk : auraWorld.auraChunks.values()) {
                        processAuraChunk(auraWorld, auraChunk);
                    }
                }
            }
            else {
                stop();
            }
            long executionTime = System.currentTimeMillis() - startTime;
            try {
                if (executionTime > 1000L) {
                    Thaumcraft.log.warn("AURAS TAKING " + (executionTime - 1000L) + " ms LONGER THAN NORMAL IN DIM " + dim);
                }
                Thread.sleep(Math.max(1L, 1000L - executionTime));
            }
            catch (InterruptedException ex) {}
        }
        Thaumcraft.log.info("Stopping aura thread for dim " + dim);
        try {
            ServerEvents.auraThreads.remove(dim);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void processAuraChunk(AuraWorld auraWorld, AuraChunk auraChunk) {
        List<Integer> directions = Arrays.asList(0, 1, 2, 3);
        Collections.shuffle(directions, rand);
        int x = auraChunk.loc.x;
        int y = auraChunk.loc.z;
        float base = auraChunk.getBase() * phaseMax;
        boolean dirty = false;
        float currentVis = auraChunk.getVis();
        float currentFlux = auraChunk.getFlux();
        AuraChunk neighbourVisChunk = null;
        AuraChunk neighbourFluxChunk = null;
        float lowestVis = Float.MAX_VALUE;
        float lowestFlux = Float.MAX_VALUE;
        for (Integer a : directions) {
            EnumFacing dir = EnumFacing.getHorizontal(a);
            AuraChunk n = auraWorld.getAuraChunkAt(x + dir.getFrontOffsetX(), y + dir.getFrontOffsetZ());
            if (n != null) {
                if ((neighbourVisChunk == null || lowestVis > n.getVis()) && n.getVis() + n.getFlux() < n.getBase() * phaseMax) {
                    neighbourVisChunk = n;
                    lowestVis = n.getVis();
                }
                if (neighbourFluxChunk != null && lowestFlux <= n.getFlux()) {
                    continue;
                }
                neighbourFluxChunk = n;
                lowestFlux = n.getFlux();
            }
        }
        if (neighbourVisChunk != null && lowestVis < currentVis && lowestVis / currentVis < 0.75) {
            float inc = Math.min(currentVis - lowestVis, 1.0f);
            currentVis -= inc;
            neighbourVisChunk.setVis(lowestVis + inc);
            dirty = true;
            markChunkAsDirty(neighbourVisChunk, auraWorld.dim);
        }
        if (neighbourFluxChunk != null && currentFlux > Math.max(5.0f, auraChunk.getBase() / 10.0f) && lowestFlux < currentFlux / 1.75) {
            float inc = Math.min(currentFlux - lowestFlux, 1.0f);
            currentFlux -= inc;
            neighbourFluxChunk.setFlux(lowestFlux + inc);
            dirty = true;
            markChunkAsDirty(neighbourFluxChunk, auraWorld.dim);
        }
        if (currentVis + currentFlux < base) {
            float inc = Math.min(base - (currentVis + currentFlux), phaseVis);
            currentVis += inc;
            dirty = true;
        }
        else if (currentVis > base * 1.25 && rand.nextFloat() < 0.1) {
            currentFlux += phaseFlux;
            currentVis -= phaseFlux;
            dirty = true;
        }
        else if (currentVis <= base * 0.1 && currentVis >= currentFlux && rand.nextFloat() < 0.1) {
            currentFlux += phaseFlux;
            dirty = true;
        }
        if (dirty) {
            auraChunk.setVis(currentVis);
            auraChunk.setFlux(currentFlux);
            markChunkAsDirty(auraChunk, auraWorld.dim);
        }
        if (currentFlux > base * 0.75 && rand.nextFloat() < currentFlux / 500.0f / 10.0f) {
            AuraHandler.riftTrigger.put(auraWorld.dim, new BlockPos(x * 16, 0, y * 16));
        }
    }
    
    private void markChunkAsDirty(AuraChunk chunk, int dim) {
        if (chunk.isModified()) {
            return;
        }
        ChunkPos pos = new ChunkPos(chunk.loc.x, chunk.loc.z);
        if (!AuraHandler.dirtyChunks.containsKey(dim)) {
            AuraHandler.dirtyChunks.put(dim, new CopyOnWriteArrayList<ChunkPos>());
        }
        CopyOnWriteArrayList<ChunkPos> dc = AuraHandler.dirtyChunks.get(dim);
        if (!dc.contains(pos)) {
            dc.add(pos);
        }
    }
    
    public void stop() {
        stop = true;
    }
}
