// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

public class Line3
{
    public static final double tol = 1.0E-4;
    public Vector3 pt1;
    public Vector3 pt2;
    
    public Line3(final Vector3 pt1, final Vector3 pt2) {
        this.pt1 = pt1;
        this.pt2 = pt2;
    }
    
    public Line3() {
        this(new Vector3(), new Vector3());
    }
    
    public static boolean intersection2D(final Line3 line1, final Line3 line2, final Vector3 store) {
        final double xD1 = line1.pt2.x - line1.pt1.x;
        final double zD1 = line1.pt2.z - line1.pt1.z;
        final double xD2 = line2.pt2.x - line2.pt1.x;
        final double zD2 = line2.pt2.z - line2.pt1.z;
        final double xD3 = line1.pt1.x - line2.pt1.x;
        final double zD3 = line1.pt1.z - line2.pt1.z;
        final double div = zD2 * xD1 - xD2 * zD1;
        if (div == 0.0) {
            return false;
        }
        final double ua = (xD2 * zD3 - zD2 * xD3) / div;
        store.set(line1.pt1.x + ua * xD1, 0.0, line1.pt1.z + ua * zD1);
        return store.x >= Math.min(line1.pt1.x, line1.pt2.x) - 1.0E-4 && store.x >= Math.min(line2.pt1.x, line2.pt2.x) - 1.0E-4 && store.z >= Math.min(line1.pt1.z, line1.pt2.z) - 1.0E-4 && store.z >= Math.min(line2.pt1.z, line2.pt2.z) - 1.0E-4 && store.x <= Math.max(line1.pt1.x, line1.pt2.x) + 1.0E-4 && store.x <= Math.max(line2.pt1.x, line2.pt2.x) + 1.0E-4 && store.z <= Math.max(line1.pt1.z, line1.pt2.z) + 1.0E-4 && store.z <= Math.max(line2.pt1.z, line2.pt2.z) + 1.0E-4;
    }
}
