// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.obj;

import java.util.ArrayList;
import java.util.List;

public class MeshPart
{
    public String name;
    public Material material;
    public List<int[]> indices;
    public int tintIndex;
    
    public MeshPart() {
        this.tintIndex = -1;
        this.indices = new ArrayList<int[]>();
    }
    
    public MeshPart(final MeshPart p, final int ti) {
        this.tintIndex = -1;
        this.indices = new ArrayList<int[]>();
        this.name = p.name;
        this.material = p.material;
        this.indices = p.indices;
        this.tintIndex = ti;
    }
    
    public void addTriangleFace(final int[] a, final int[] b, final int[] c) {
        this.indices.add(a);
        this.indices.add(b);
        this.indices.add(c);
        this.indices.add(c);
    }
    
    public void addQuadFace(final int[] a, final int[] b, final int[] c, final int[] d) {
        this.indices.add(a);
        this.indices.add(b);
        this.indices.add(c);
        this.indices.add(d);
    }
}
