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
        y = z;
    }
    
    public PosXY(final PosXY c) {
        x = c.x;
        y = c.y;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PosXY)) {
            return false;
        }
        final PosXY chunkcoordinates = (PosXY)o;
        return x == chunkcoordinates.x && y == chunkcoordinates.y;
    }
    
    @Override
    public int hashCode() {
        return x + y << 8;
    }
    
    public int compareTo(final PosXY c) {
        return (y == c.y) ? (x - c.x) : (y - c.y);
    }
    
    public void set(final int x, final int z) {
        this.x = x;
        y = z;
    }
    
    public float getDistanceSquared(final int x, final int z) {
        final float f = (float)(this.x - x);
        final float f2 = (float)(y - z);
        return f * f + f2 * f2;
    }
    
    public float getDistanceSquaredToChunkCoordinates(final PosXY c) {
        return getDistanceSquared(c.x, c.y);
    }
    
    @Override
    public String toString() {
        return "Pos{x=" + x + ", y=" + y + '}';
    }
    
    @Override
    public int compareTo(final Object o) {
        return compareTo((PosXY)o);
    }
}
