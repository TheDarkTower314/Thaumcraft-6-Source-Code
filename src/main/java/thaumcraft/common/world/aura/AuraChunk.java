// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.world.aura;

import net.minecraft.world.chunk.Chunk;
import java.lang.ref.WeakReference;
import net.minecraft.util.math.ChunkPos;

public class AuraChunk
{
    ChunkPos loc;
    short base;
    float vis;
    float flux;
    WeakReference<Chunk> chunkRef;
    
    public AuraChunk(final ChunkPos loc) {
        this.loc = loc;
    }
    
    public AuraChunk(final Chunk chunk, final short base, final float vis, final float flux) {
        if (chunk != null) {
            this.loc = chunk.getPos();
            this.chunkRef = new WeakReference<Chunk>(chunk);
        }
        this.base = base;
        this.vis = vis;
        this.flux = flux;
    }
    
    public boolean isModified() {
        return this.chunkRef != null && this.chunkRef.get() != null && this.chunkRef.get().needsSaving(false);
    }
    
    public short getBase() {
        return this.base;
    }
    
    public void setBase(final short base) {
        this.base = base;
    }
    
    public float getVis() {
        return this.vis;
    }
    
    public void setVis(final float vis) {
        this.vis = Math.min(32766.0f, Math.max(0.0f, vis));
    }
    
    public float getFlux() {
        return this.flux;
    }
    
    public void setFlux(final float flux) {
        this.flux = Math.min(32766.0f, Math.max(0.0f, flux));
    }
    
    public ChunkPos getLoc() {
        return this.loc;
    }
    
    public void setLoc(final ChunkPos loc) {
        this.loc = loc;
    }
}
