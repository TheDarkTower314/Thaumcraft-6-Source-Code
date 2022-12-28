package thaumcraft.common.world.aura;
import java.util.concurrent.ConcurrentHashMap;
import thaumcraft.common.lib.utils.PosXY;


public class AuraWorld
{
    int dim;
    ConcurrentHashMap<PosXY, AuraChunk> auraChunks;
    
    public AuraWorld(int dim) {
        auraChunks = new ConcurrentHashMap<PosXY, AuraChunk>();
        this.dim = dim;
    }
    
    public ConcurrentHashMap<PosXY, AuraChunk> getAuraChunks() {
        return auraChunks;
    }
    
    public void setAuraChunks(ConcurrentHashMap<PosXY, AuraChunk> auraChunks) {
        this.auraChunks = auraChunks;
    }
    
    public AuraChunk getAuraChunkAt(int x, int y) {
        return getAuraChunkAt(new PosXY(x, y));
    }
    
    public AuraChunk getAuraChunkAt(PosXY loc) {
        AuraChunk ac = auraChunks.get(loc);
        return ac;
    }
}
