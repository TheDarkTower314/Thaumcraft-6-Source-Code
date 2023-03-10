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
    
    public MeshPart(MeshPart p, int ti) {
        tintIndex = -1;
        indices = new ArrayList<int[]>();
        name = p.name;
        material = p.material;
        indices = p.indices;
        tintIndex = ti;
    }
    
    public void addTriangleFace(int[] a, int[] b, int[] c) {
        indices.add(a);
        indices.add(b);
        indices.add(c);
        indices.add(c);
    }
    
    public void addQuadFace(int[] a, int[] b, int[] c, int[] d) {
        indices.add(a);
        indices.add(b);
        indices.add(c);
        indices.add(d);
    }
}
