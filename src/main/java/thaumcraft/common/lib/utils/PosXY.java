// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.utils;

public class PosXY implements Comparable
{
    public int x;
    public int y;
    
    public PosXY() {
    }
    
    public PosXY(final int x, final int z) {
        this.x = x;
        this.y = z;
    }
    
    public PosXY(final PosXY c) {
        this.x = c.x;
        this.y = c.y;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PosXY)) {
            return false;
        }
        final PosXY chunkcoordinates = (PosXY)o;
        return this.x == chunkcoordinates.x && this.y == chunkcoordinates.y;
    }
    
    @Override
    public int hashCode() {
        return this.x + this.y << 8;
    }
    
    public int compareTo(final PosXY c) {
        return (this.y == c.y) ? (this.x - c.x) : (this.y - c.y);
    }
    
    public void set(final int x, final int z) {
        this.x = x;
        this.y = z;
    }
    
    public float getDistanceSquared(final int x, final int z) {
        final float f = (float)(this.x - x);
        final float f2 = (float)(this.y - z);
        return f * f + f2 * f2;
    }
    
    public float getDistanceSquaredToChunkCoordinates(final PosXY c) {
        return this.getDistanceSquared(c.x, c.y);
    }
    
    @Override
    public String toString() {
        return "Pos{x=" + this.x + ", y=" + this.y + '}';
    }
    
    @Override
    public int compareTo(final Object o) {
        return this.compareTo((PosXY)o);
    }
}
