// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.world.aura;

import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.world.World;
import thaumcraft.common.lib.events.ServerEvents;
import net.minecraftforge.common.DimensionManager;
import thaumcraft.Thaumcraft;
import java.util.Random;

public class AuraThread implements Runnable
{
    public int dim;
    private final long INTERVAL = 1000L;
    private boolean stop;
    Random rand;
    private float phaseVis;
    private float phaseFlux;
    private float phaseMax;
    private long lastWorldTime;
    private float[] phaseTable;
    private float[] maxTable;
    
    public AuraThread(final int dim2) {
        this.stop = false;
        this.rand = new Random(System.currentTimeMillis());
        this.phaseVis = 0.0f;
        this.phaseFlux = 0.0f;
        this.phaseMax = 0.0f;
        this.lastWorldTime = 0L;
        this.phaseTable = new float[] { 0.25f, 0.15f, 0.1f, 0.05f, 0.0f, 0.05f, 0.1f, 0.15f };
        this.maxTable = new float[] { 0.15f, 0.05f, 0.0f, -0.05f, -0.15f, -0.05f, 0.0f, 0.05f };
        this.dim = dim2;
    }
    
    @Override
    public void run() {
        Thaumcraft.log.info("Starting aura thread for dim " + this.dim);
        while (!this.stop) {
            if (AuraHandler.auras.isEmpty()) {
                Thaumcraft.log.warn("No auras found!");
                break;
            }
            final long startTime = System.currentTimeMillis();
            final AuraWorld auraWorld = AuraHandler.getAuraWorld(this.dim);
            if (auraWorld != null) {
                final World world = DimensionManager.getWorld(this.dim);
                if (this.lastWorldTime != world.getWorldTime()) {
                    this.lastWorldTime = world.getWorldTime();
                    if (world != null) {
                        this.phaseVis = this.phaseTable[world.provider.getMoonPhase(world.getWorldInfo().getWorldTime())];
                        this.phaseMax = 1.0f + this.maxTable[world.provider.getMoonPhase(world.getWorldInfo().getWorldTime())];
                        this.phaseFlux = 0.25f - this.phaseVis;
                    }
                    for (final AuraChunk auraChunk : auraWorld.auraChunks.values()) {
                        this.processAuraChunk(auraWorld, auraChunk);
                    }
                }
            }
            else {
                this.stop();
            }
            final long executionTime = System.currentTimeMillis() - startTime;
            try {
                if (executionTime > 1000L) {
                    Thaumcraft.log.warn("AURAS TAKING " + (executionTime - 1000L) + " ms LONGER THAN NORMAL IN DIM " + this.dim);
                }
                Thread.sleep(Math.max(1L, 1000L - executionTime));
            }
            catch (final InterruptedException ex) {}
        }
        Thaumcraft.log.info("Stopping aura thread for dim " + this.dim);
        try {
            ServerEvents.auraThreads.remove(this.dim);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void processAuraChunk(final AuraWorld auraWorld, final AuraChunk auraChunk) {
        final List<Integer> directions = Arrays.asList(0, 1, 2, 3);
        Collections.shuffle(directions, this.rand);
        final int x = auraChunk.loc.x;
        final int y = auraChunk.loc.z;
        final float base = auraChunk.getBase() * this.phaseMax;
        boolean dirty = false;
        float currentVis = auraChunk.getVis();
        float currentFlux = auraChunk.getFlux();
        AuraChunk neighbourVisChunk = null;
        AuraChunk neighbourFluxChunk = null;
        float lowestVis = Float.MAX_VALUE;
        float lowestFlux = Float.MAX_VALUE;
        for (final Integer a : directions) {
            final EnumFacing dir = EnumFacing.getHorizontal(a);
            final AuraChunk n = auraWorld.getAuraChunkAt(x + dir.getFrontOffsetX(), y + dir.getFrontOffsetZ());
            if (n != null) {
                if ((neighbourVisChunk == null || lowestVis > n.getVis()) && n.getVis() + n.getFlux() < n.getBase() * this.phaseMax) {
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
            final float inc = Math.min(currentVis - lowestVis, 1.0f);
            currentVis -= inc;
            neighbourVisChunk.setVis(lowestVis + inc);
            dirty = true;
            this.markChunkAsDirty(neighbourVisChunk, auraWorld.dim);
        }
        if (neighbourFluxChunk != null && currentFlux > Math.max(5.0f, auraChunk.getBase() / 10.0f) && lowestFlux < currentFlux / 1.75) {
            final float inc = Math.min(currentFlux - lowestFlux, 1.0f);
            currentFlux -= inc;
            neighbourFluxChunk.setFlux(lowestFlux + inc);
            dirty = true;
            this.markChunkAsDirty(neighbourFluxChunk, auraWorld.dim);
        }
        if (currentVis + currentFlux < base) {
            final float inc = Math.min(base - (currentVis + currentFlux), this.phaseVis);
            currentVis += inc;
            dirty = true;
        }
        else if (currentVis > base * 1.25 && this.rand.nextFloat() < 0.1) {
            currentFlux += this.phaseFlux;
            currentVis -= this.phaseFlux;
            dirty = true;
        }
        else if (currentVis <= base * 0.1 && currentVis >= currentFlux && this.rand.nextFloat() < 0.1) {
            currentFlux += this.phaseFlux;
            dirty = true;
        }
        if (dirty) {
            auraChunk.setVis(currentVis);
            auraChunk.setFlux(currentFlux);
            this.markChunkAsDirty(auraChunk, auraWorld.dim);
        }
        if (currentFlux > base * 0.75 && this.rand.nextFloat() < currentFlux / 500.0f / 10.0f) {
            AuraHandler.riftTrigger.put(auraWorld.dim, new BlockPos(x * 16, 0, y * 16));
        }
    }
    
    private void markChunkAsDirty(final AuraChunk chunk, final int dim) {
        if (chunk.isModified()) {
            return;
        }
        final ChunkPos pos = new ChunkPos(chunk.loc.x, chunk.loc.z);
        if (!AuraHandler.dirtyChunks.containsKey(dim)) {
            AuraHandler.dirtyChunks.put(dim, new CopyOnWriteArrayList<ChunkPos>());
        }
        final CopyOnWriteArrayList<ChunkPos> dc = AuraHandler.dirtyChunks.get(dim);
        if (!dc.contains(pos)) {
            dc.add(pos);
        }
    }
    
    public void stop() {
        this.stop = true;
    }
}
