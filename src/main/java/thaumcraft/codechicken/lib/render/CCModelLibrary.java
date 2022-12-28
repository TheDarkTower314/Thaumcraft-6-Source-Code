package thaumcraft.codechicken.lib.render;
import thaumcraft.codechicken.lib.vec.Matrix4;
import thaumcraft.codechicken.lib.vec.Quat;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Scale;
import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.Vector3;


public class CCModelLibrary
{
    public static CCModel icosahedron4;
    public static CCModel icosahedron7;
    private static int i;
    
    private static void generateIcosahedron() {
        Vector3[] verts = { new Vector3(-1.0, 1.618033988749894, 0.0), new Vector3(1.0, 1.618033988749894, 0.0), new Vector3(1.0, -1.618033988749894, 0.0), new Vector3(-1.0, -1.618033988749894, 0.0), new Vector3(0.0, -1.0, 1.618033988749894), new Vector3(0.0, 1.0, 1.618033988749894), new Vector3(0.0, 1.0, -1.618033988749894), new Vector3(0.0, -1.0, -1.618033988749894), new Vector3(1.618033988749894, 0.0, -1.0), new Vector3(1.618033988749894, 0.0, 1.0), new Vector3(-1.618033988749894, 0.0, 1.0), new Vector3(-1.618033988749894, 0.0, -1.0) };
        Quat quat = Quat.aroundAxis(0.0, 0.0, 1.0, Math.atan(0.6180339887498951));
        for (Vector3 vec : verts) {
            quat.rotate(vec);
        }
        CCModelLibrary.icosahedron4 = CCModel.newModel(4, 60);
        CCModelLibrary.icosahedron7 = CCModel.newModel(7, 80);
        CCModelLibrary.i = 0;
        addIcosahedronTriangle(verts[1], 0.5, 0.0, verts[0], 0.0, 0.25, verts[5], 1.0, 0.25);
        addIcosahedronTriangle(verts[1], 0.5, 0.0, verts[5], 0.0, 0.25, verts[9], 1.0, 0.25);
        addIcosahedronTriangle(verts[1], 0.5, 0.0, verts[9], 0.0, 0.25, verts[8], 1.0, 0.25);
        addIcosahedronTriangle(verts[1], 0.5, 0.0, verts[8], 0.0, 0.25, verts[6], 1.0, 0.25);
        addIcosahedronTriangle(verts[1], 0.5, 0.0, verts[6], 0.0, 0.25, verts[0], 1.0, 0.25);
        addIcosahedronTriangle(verts[0], 0.5, 0.25, verts[11], 0.0, 0.75, verts[10], 1.0, 0.75);
        addIcosahedronTriangle(verts[5], 0.5, 0.25, verts[10], 0.0, 0.75, verts[4], 1.0, 0.75);
        addIcosahedronTriangle(verts[9], 0.5, 0.25, verts[4], 0.0, 0.75, verts[2], 1.0, 0.75);
        addIcosahedronTriangle(verts[8], 0.5, 0.25, verts[2], 0.0, 0.75, verts[7], 1.0, 0.75);
        addIcosahedronTriangle(verts[6], 0.5, 0.25, verts[7], 0.0, 0.75, verts[11], 1.0, 0.75);
        addIcosahedronTriangle(verts[2], 0.5, 0.75, verts[8], 0.0, 0.25, verts[9], 1.0, 0.25);
        addIcosahedronTriangle(verts[7], 0.5, 0.75, verts[6], 0.0, 0.25, verts[8], 1.0, 0.25);
        addIcosahedronTriangle(verts[11], 0.5, 0.75, verts[0], 0.0, 0.25, verts[6], 1.0, 0.25);
        addIcosahedronTriangle(verts[10], 0.5, 0.75, verts[5], 0.0, 0.25, verts[0], 1.0, 0.25);
        addIcosahedronTriangle(verts[4], 0.5, 0.75, verts[9], 0.0, 0.25, verts[5], 1.0, 0.25);
        addIcosahedronTriangle(verts[3], 0.5, 1.0, verts[2], 0.0, 0.75, verts[4], 1.0, 0.75);
        addIcosahedronTriangle(verts[3], 0.5, 1.0, verts[7], 0.0, 0.75, verts[2], 1.0, 0.75);
        addIcosahedronTriangle(verts[3], 0.5, 1.0, verts[11], 0.0, 0.75, verts[7], 1.0, 0.75);
        addIcosahedronTriangle(verts[3], 0.5, 1.0, verts[10], 0.0, 0.75, verts[11], 1.0, 0.75);
        addIcosahedronTriangle(verts[3], 0.5, 1.0, verts[4], 0.0, 0.75, verts[10], 1.0, 0.75);
        CCModelLibrary.icosahedron4.computeNormals().smoothNormals();
        CCModelLibrary.icosahedron7.computeNormals().smoothNormals();
    }
    
    private static void addIcosahedronTriangle(Vector3 vec1, double u1, double v1, Vector3 vec2, double u2, double v2, Vector3 vec3, double u3, double v3) {
        CCModelLibrary.icosahedron4.verts[CCModelLibrary.i * 3] = (CCModelLibrary.icosahedron7.verts[CCModelLibrary.i * 4] = new Vertex5(vec1, u1, v1));
        CCModelLibrary.icosahedron4.verts[CCModelLibrary.i * 3 + 1] = (CCModelLibrary.icosahedron7.verts[CCModelLibrary.i * 4 + 1] = new Vertex5(vec2, u2, v2));
        Vertex5[] verts = CCModelLibrary.icosahedron4.verts;
        int n = CCModelLibrary.i * 3 + 2;
        Vertex5[] verts2 = CCModelLibrary.icosahedron7.verts;
        int n2 = CCModelLibrary.i * 4 + 2;
        Vertex5[] verts3 = CCModelLibrary.icosahedron7.verts;
        int n3 = CCModelLibrary.i * 4 + 3;
        Vertex5 vertex5 = new Vertex5(vec3, u3, v3);
        verts3[n3] = vertex5;
        verts[n] = (verts2[n2] = vertex5);
        ++CCModelLibrary.i;
    }
    
    public static Matrix4 getRenderMatrix(Vector3 position, Rotation rotation, double scale) {
        return new Matrix4().translate(position).apply(new Scale(scale)).apply(rotation);
    }
    
    static {
        generateIcosahedron();
    }
}
