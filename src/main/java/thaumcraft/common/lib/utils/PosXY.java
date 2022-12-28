package thaumcraft.common.lib.utils;


public class PosXY implements Comparable
{
    public int x;
    public int y;
    
    public PosXY() {
    }
    
    public PosXY(int x, int z) {
        this.x = x;
        y = z;
    }
    
    public PosXY(PosXY c) {
        x = c.x;
        y = c.y;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PosXY)) {
            return false;
        }
        PosXY chunkcoordinates = (PosXY)o;
        return x == chunkcoordinates.x && y == chunkcoordinates.y;
    }
    
    @Override
    public int hashCode() {
        return x + y << 8;
    }
    
    public int compareTo(PosXY c) {
        return (y == c.y) ? (x - c.x) : (y - c.y);
    }
    
    public void set(int x, int z) {
        this.x = x;
        y = z;
    }
    
    public float getDistanceSquared(int x, int z) {
        float f = (float)(this.x - x);
        float f2 = (float)(y - z);
        return f * f + f2 * f2;
    }
    
    public float getDistanceSquaredToChunkCoordinates(PosXY c) {
        return getDistanceSquared(c.x, c.y);
    }
    
    @Override
    public String toString() {
        return "Pos{x=" + x + ", y=" + y + '}';
    }
    
    @Override
    public int compareTo(Object o) {
        return compareTo((PosXY)o);
    }
}
