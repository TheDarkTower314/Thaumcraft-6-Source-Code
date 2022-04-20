// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.obj;

public class Vertex
{
    public float x;
    public float y;
    public float z;
    
    public Vertex(final float x, final float y) {
        this(x, y, 0.0f);
    }
    
    public Vertex(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
