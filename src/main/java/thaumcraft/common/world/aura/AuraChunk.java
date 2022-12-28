package thaumcraft.common.world.aura;
import java.lang.ref.WeakReference;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;


public class AuraChunk
{
    ChunkPos loc;
    short base;
    float vis;
    float flux;
    WeakReference<Chunk> chunkRef;
    
    public AuraChunk(ChunkPos loc) {
        this.loc = loc;
    }
    
    public AuraChunk(Chunk chunk, short base, float vis, float flux) {
        if (chunk != null) {
            loc = chunk.getPos();
            chunkRef = new WeakReference<Chunk>(chunk);
        }
        this.base = base;
        this.vis = vis;
        this.flux = flux;
    }
    
    public boolean isModified() {
        return chunkRef != null && chunkRef.get() != null && chunkRef.get().needsSaving(false);
    }
    
    public short getBase() {
        return base;
    }
    
    public void setBase(short base) {
        this.base = base;
    }
    
    public float getVis() {
        return vis;
    }
    
    public void setVis(float vis) {
        this.vis = Math.min(32766.0f, Math.max(0.0f, vis));
    }
    
    public float getFlux() {
        return flux;
    }
    
    public void setFlux(float flux) {
        this.flux = Math.min(32766.0f, Math.max(0.0f, flux));
    }
    
    public ChunkPos getLoc() {
        return loc;
    }
    
    public void setLoc(ChunkPos loc) {
        this.loc = loc;
    }
}
