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
        tintIndex = -1;
        indices = new ArrayList<int[]>();
    }
    
    public MeshPart(final MeshPart p, final int ti) {
        tintIndex = -1;
        indices = new ArrayList<int[]>();
        name = p.name;
        material = p.material;
        indices = p.indices;
        tintIndex = ti;
    }
    
    public void addTriangleFace(final int[] a, final int[] b, final int[] c) {
        indices.add(a);
        indices.add(b);
        indices.add(c);
        indices.add(c);
    }
    
    public void addQuadFace(final int[] a, final int[] b, final int[] c, final int[] d) {
        indices.add(a);
        indices.add(b);
        indices.add(c);
        indices.add(d);
    }
}
