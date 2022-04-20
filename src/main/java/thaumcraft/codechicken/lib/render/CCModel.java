// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.render;

import io.netty.util.AttributeKey;
import thaumcraft.codechicken.lib.vec.ITransformation;
import java.util.LinkedList;
import java.util.Collection;
import thaumcraft.codechicken.lib.vec.TransformationList;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.render.uv.UV;
import java.io.PrintWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import java.io.IOException;
import java.util.List;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import thaumcraft.codechicken.lib.vec.RedundantTransformation;
import java.util.Map;
import java.io.InputStream;
import thaumcraft.codechicken.lib.render.uv.UVTransformation;
import thaumcraft.codechicken.lib.vec.Transformation;
import java.util.Iterator;
import thaumcraft.codechicken.lib.lighting.LC;
import java.util.Arrays;
import thaumcraft.codechicken.lib.lighting.LightModel;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import thaumcraft.codechicken.lib.util.Copyable;

public class CCModel implements CCRenderState.IVertexSource, Copyable<CCModel>
{
    public final int vertexMode;
    public final int vp;
    public Vertex5[] verts;
    public ArrayList<Object> attributes;
    private static final Pattern vertPattern;
    private static final Pattern uvwPattern;
    private static final Pattern normalPattern;
    private static final Pattern polyPattern;
    public static final Matcher vertMatcher;
    public static final Matcher uvwMatcher;
    public static final Matcher normalMatcher;
    public static final Matcher polyMatcher;
    
    protected CCModel(final int vertexMode) {
        this.attributes = new ArrayList<Object>();
        if (vertexMode != 7 && vertexMode != 4) {
            throw new IllegalArgumentException("Models must be GL_QUADS or GL_TRIANGLES");
        }
        this.vp = (((this.vertexMode = vertexMode) == 7) ? 4 : 3);
    }
    
    public Vector3[] normals() {
        return this.getAttributes(CCRenderState.normalAttrib);
    }
    
    @Override
    public Vertex5[] getVertices() {
        return this.verts;
    }
    
    @Override
    public <T> T getAttributes(final CCRenderState.VertexAttribute<T> attr) {
        if (attr.attributeIndex < this.attributes.size()) {
            return (T)this.attributes.get(attr.attributeIndex);
        }
        return null;
    }
    
    @Override
    public boolean hasAttribute(final CCRenderState.VertexAttribute<?> attrib) {
        return attrib.attributeIndex < this.attributes.size() && this.attributes.get(attrib.attributeIndex) != null;
    }
    
    @Override
    public void prepareVertex() {
    }
    
    public <T> T getOrAllocate(final CCRenderState.VertexAttribute<T> attrib) {
        T array = (T)this.getAttributes((CCRenderState.VertexAttribute<Object>)attrib);
        if (array == null) {
            while (this.attributes.size() <= attrib.attributeIndex) {
                this.attributes.add(null);
            }
            this.attributes.set(attrib.attributeIndex, array = attrib.newArray(this.verts.length));
        }
        return array;
    }
    
    public CCModel generateBox(int i, double x1, double y1, double z1, final double w, final double h, final double d, final double tx, final double ty, final double tw, final double th, final double f) {
        double x2 = x1 + w;
        double y2 = y1 + h;
        double z2 = z1 + d;
        x1 /= f;
        x2 /= f;
        y1 /= f;
        y2 /= f;
        z1 /= f;
        z2 /= f;
        double u1 = (tx + d + w) / tw;
        double v1 = (ty + d) / th;
        double u2 = (tx + d * 2.0 + w) / tw;
        double v2 = ty / th;
        this.verts[i++] = new Vertex5(x1, y1, z2, u1, v2);
        this.verts[i++] = new Vertex5(x1, y1, z1, u1, v1);
        this.verts[i++] = new Vertex5(x2, y1, z1, u2, v1);
        this.verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
        u1 = (tx + d) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d + w) / tw;
        v2 = ty / th;
        this.verts[i++] = new Vertex5(x2, y2, z2, u2, v2);
        this.verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
        this.verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
        this.verts[i++] = new Vertex5(x1, y2, z2, u1, v2);
        u1 = (tx + d + w) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d) / tw;
        v2 = (ty + d + h) / th;
        this.verts[i++] = new Vertex5(x1, y2, z1, u2, v1);
        this.verts[i++] = new Vertex5(x2, y2, z1, u1, v1);
        this.verts[i++] = new Vertex5(x2, y1, z1, u1, v2);
        this.verts[i++] = new Vertex5(x1, y1, z1, u2, v2);
        u1 = (tx + d * 2.0 + w * 2.0) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d * 2.0 + w) / tw;
        v2 = (ty + d + h) / th;
        this.verts[i++] = new Vertex5(x1, y2, z2, u1, v1);
        this.verts[i++] = new Vertex5(x1, y1, z2, u1, v2);
        this.verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
        this.verts[i++] = new Vertex5(x2, y2, z2, u2, v1);
        u1 = (tx + d) / tw;
        v1 = (ty + d) / th;
        u2 = tx / tw;
        v2 = (ty + d + h) / th;
        this.verts[i++] = new Vertex5(x1, y2, z2, u2, v1);
        this.verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
        this.verts[i++] = new Vertex5(x1, y1, z1, u1, v2);
        this.verts[i++] = new Vertex5(x1, y1, z2, u2, v2);
        u1 = (tx + d * 2.0 + w) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d + w) / tw;
        v2 = (ty + d + h) / th;
        this.verts[i++] = new Vertex5(x2, y1, z2, u1, v2);
        this.verts[i++] = new Vertex5(x2, y1, z1, u2, v2);
        this.verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
        this.verts[i++] = new Vertex5(x2, y2, z2, u1, v1);
        return this;
    }
    
    public CCModel generateBlock(final int i, final Cuboid6 bounds) {
        return this.generateBlock(i, bounds, 0);
    }
    
    public CCModel generateBlock(final int i, final Cuboid6 bounds, final int mask) {
        return this.generateBlock(i, bounds.min.x, bounds.min.y, bounds.min.z, bounds.max.x, bounds.max.y, bounds.max.z, mask);
    }
    
    public CCModel generateBlock(final int i, final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        return this.generateBlock(i, x1, y1, z1, x2, y2, z2, 0);
    }
    
    public CCModel generateBlock(int i, final double x1, final double y1, final double z1, final double x2, final double y2, final double z2, final int mask) {
        if ((mask & 0x1) == 0x0) {
            final double u1 = x1;
            final double v1 = z1;
            final double u2 = x2;
            final double v2 = z2;
            this.verts[i++] = new Vertex5(x1, y1, z2, u1, v2);
            this.verts[i++] = new Vertex5(x1, y1, z1, u1, v1);
            this.verts[i++] = new Vertex5(x2, y1, z1, u2, v1);
            this.verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
        }
        if ((mask & 0x2) == 0x0) {
            final double u1 = x1 + 2.0;
            final double v1 = z1;
            final double u2 = x2 + 2.0;
            final double v2 = z2;
            this.verts[i++] = new Vertex5(x2, y2, z2, u2, v2);
            this.verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
            this.verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
            this.verts[i++] = new Vertex5(x1, y2, z2, u1, v2);
        }
        if ((mask & 0x4) == 0x0) {
            final double u1 = 1.0 - x1 + 4.0;
            final double v1 = 1.0 - y2;
            final double u2 = 1.0 - x2 + 4.0;
            final double v2 = 1.0 - y1;
            this.verts[i++] = new Vertex5(x1, y1, z1, u1, v2);
            this.verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
            this.verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
            this.verts[i++] = new Vertex5(x2, y1, z1, u2, v2);
        }
        if ((mask & 0x8) == 0x0) {
            final double u1 = x1 + 6.0;
            final double v1 = 1.0 - y2;
            final double u2 = x2 + 6.0;
            final double v2 = 1.0 - y1;
            this.verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
            this.verts[i++] = new Vertex5(x2, y2, z2, u2, v1);
            this.verts[i++] = new Vertex5(x1, y2, z2, u1, v1);
            this.verts[i++] = new Vertex5(x1, y1, z2, u1, v2);
        }
        if ((mask & 0x10) == 0x0) {
            final double u1 = z1 + 8.0;
            final double v1 = 1.0 - y2;
            final double u2 = z2 + 8.0;
            final double v2 = 1.0 - y1;
            this.verts[i++] = new Vertex5(x1, y1, z2, u2, v2);
            this.verts[i++] = new Vertex5(x1, y2, z2, u2, v1);
            this.verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
            this.verts[i++] = new Vertex5(x1, y1, z1, u1, v2);
        }
        if ((mask & 0x20) == 0x0) {
            final double u1 = 1.0 - z1 + 10.0;
            final double v1 = 1.0 - y2;
            final double u2 = 1.0 - z2 + 10.0;
            final double v2 = 1.0 - y1;
            this.verts[i++] = new Vertex5(x2, y1, z1, u1, v2);
            this.verts[i++] = new Vertex5(x2, y2, z1, u1, v1);
            this.verts[i++] = new Vertex5(x2, y2, z2, u2, v1);
            this.verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
        }
        return this;
    }
    
    public CCModel computeNormals() {
        return this.computeNormals(0, this.verts.length);
    }
    
    public CCModel computeNormals(final int start, final int length) {
        if (length % this.vp != 0 || start % this.vp != 0) {
            throw new IllegalArgumentException("Cannot generate normals across polygons");
        }
        final Vector3[] normals = this.getOrAllocate(CCRenderState.normalAttrib);
        for (int k = 0; k < length; k += this.vp) {
            final int i = k + start;
            final Vector3 diff1 = this.verts[i + 1].vec.copy().subtract(this.verts[i].vec);
            final Vector3 diff2 = this.verts[i + this.vp - 1].vec.copy().subtract(this.verts[i].vec);
            normals[i] = diff1.crossProduct(diff2).normalize();
            for (int d = 1; d < this.vp; ++d) {
                normals[i + d] = normals[i].copy();
            }
        }
        return this;
    }
    
    public CCModel computeLighting(final LightModel light) {
        final Vector3[] normals = this.normals();
        int[] colours = this.getAttributes(CCRenderState.colourAttrib);
        if (colours == null) {
            this.setColour(-1);
            colours = this.getAttributes(CCRenderState.colourAttrib);
        }
        for (int k = 0; k < this.verts.length; ++k) {
            colours[k] = light.apply(colours[k], normals[k]);
        }
        return this;
    }
    
    public CCModel setColour(final int c) {
        final int[] colours = this.getOrAllocate(CCRenderState.colourAttrib);
        Arrays.fill(colours, c);
        return this;
    }
    
    public CCModel computeLightCoords() {
        final LC[] lcs = this.getOrAllocate(CCRenderState.lightCoordAttrib);
        final Vector3[] normals = this.normals();
        for (int i = 0; i < this.verts.length; ++i) {
            lcs[i] = new LC().compute(this.verts[i].vec, normals[i]);
        }
        return this;
    }
    
    public CCModel smoothNormals() {
        final ArrayList<PositionNormalEntry> map = new ArrayList<PositionNormalEntry>();
        final Vector3[] normals = this.normals();
        int k = 0;
    Label_0015:
        while (k < this.verts.length) {
            final Vector3 vec = this.verts[k].vec;
            while (true) {
                for (final PositionNormalEntry e : map) {
                    if (e.positionEqual(vec)) {
                        e.addNormal(normals[k]);
                        ++k;
                        continue Label_0015;
                    }
                }
                map.add(new PositionNormalEntry(vec).addNormal(normals[k]));
                continue;
            }
        }
        for (final PositionNormalEntry e2 : map) {
            if (e2.normals.size() <= 1) {
                continue;
            }
            final Vector3 new_n = new Vector3();
            for (final Vector3 n : e2.normals) {
                new_n.add(n);
            }
            new_n.normalize();
            for (final Vector3 n : e2.normals) {
                n.set(new_n);
            }
        }
        return this;
    }
    
    public CCModel apply(final Transformation t) {
        for (int k = 0; k < this.verts.length; ++k) {
            this.verts[k].apply(t);
        }
        final Vector3[] normals = this.normals();
        if (normals != null) {
            for (int i = 0; i < normals.length; ++i) {
                t.applyN(normals[i]);
            }
        }
        return this;
    }
    
    public CCModel apply(final UVTransformation uvt) {
        for (int k = 0; k < this.verts.length; ++k) {
            this.verts[k].apply(uvt);
        }
        return this;
    }
    
    public CCModel expand(final int extraVerts) {
        final int newLen = this.verts.length + extraVerts;
        this.verts = Arrays.copyOf(this.verts, newLen);
        for (int i = 0; i < this.attributes.size(); ++i) {
            if (this.attributes.get(i) != null) {
                this.attributes.set(i, CCRenderState.copyOf((CCRenderState.VertexAttribute)CCRenderState.getAttribute(i), this.attributes.get(i), newLen));
            }
        }
        return this;
    }
    
    public static CCModel quadModel(final int numVerts) {
        return newModel(7, numVerts);
    }
    
    public static CCModel triModel(final int numVerts) {
        return newModel(4, numVerts);
    }
    
    public static CCModel newModel(final int vertexMode, final int numVerts) {
        final CCModel model = newModel(vertexMode);
        model.verts = new Vertex5[numVerts];
        return model;
    }
    
    public static CCModel newModel(final int vertexMode) {
        return new CCModel(vertexMode);
    }
    
    public static double[] parseDoubles(final String s, final String token) {
        final String[] as = s.split(token);
        final double[] values = new double[as.length];
        for (int i = 0; i < as.length; ++i) {
            values[i] = Double.parseDouble(as[i]);
        }
        return values;
    }
    
    public static void illegalAssert(final boolean b, final String err) {
        if (!b) {
            throw new IllegalArgumentException(err);
        }
    }
    
    public static void assertMatch(final Matcher m, final String s) {
        m.reset(s);
        illegalAssert(m.matches(), "Malformed line: " + s);
    }
    
    public static Map<String, CCModel> parseObjModels(final InputStream input, final int vertexMode, Transformation coordSystem) throws IOException {
        if (coordSystem == null) {
            coordSystem = new RedundantTransformation();
        }
        final int vp = (vertexMode == 7) ? 4 : 3;
        final HashMap<String, CCModel> modelMap = new HashMap<String, CCModel>();
        final ArrayList<Vector3> verts = new ArrayList<Vector3>();
        final ArrayList<Vector3> uvs = new ArrayList<Vector3>();
        final ArrayList<Vector3> normals = new ArrayList<Vector3>();
        final ArrayList<int[]> polys = new ArrayList<int[]>();
        String modelName = "unnamed";
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("\\s+", " ").trim();
            if (!line.startsWith("#")) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("v ")) {
                    assertMatch(CCModel.vertMatcher, line);
                    final double[] values = parseDoubles(line.substring(2), " ");
                    illegalAssert(values.length >= 3, "Vertices must have x, y and z components");
                    final Vector3 vert = new Vector3(values[0], values[1], values[2]);
                    coordSystem.apply(vert);
                    verts.add(vert);
                }
                else if (line.startsWith("vt ")) {
                    assertMatch(CCModel.uvwMatcher, line);
                    final double[] values = parseDoubles(line.substring(3), " ");
                    illegalAssert(values.length >= 2, "Tex Coords must have u, and v components");
                    uvs.add(new Vector3(values[0], 1.0 - values[1], 0.0));
                }
                else if (line.startsWith("vn ")) {
                    assertMatch(CCModel.normalMatcher, line);
                    final double[] values = parseDoubles(line.substring(3), " ");
                    illegalAssert(values.length >= 3, "Normals must have x, y and z components");
                    final Vector3 norm = new Vector3(values[0], values[1], values[2]).normalize();
                    coordSystem.applyN(norm);
                    normals.add(norm);
                }
                else {
                    if (line.startsWith("f ")) {
                        assertMatch(CCModel.polyMatcher, line);
                        final String[] av = line.substring(2).split(" ");
                        illegalAssert(av.length >= 3, "Polygons must have at least 3 vertices");
                        final int[][] polyVerts = new int[av.length][3];
                        for (int i = 0; i < av.length; ++i) {
                            final String[] as = av[i].split("/");
                            for (int p = 0; p < as.length; ++p) {
                                if (as[p].length() > 0) {
                                    polyVerts[i][p] = Integer.parseInt(as[p]);
                                }
                            }
                        }
                        if (vp == 3) {
                            triangulate(polys, polyVerts);
                        }
                        else {
                            quadulate(polys, polyVerts);
                        }
                    }
                    if (!line.startsWith("g ")) {
                        continue;
                    }
                    if (!polys.isEmpty()) {
                        modelMap.put(modelName, createModel(verts, uvs, normals, vertexMode, polys));
                        polys.clear();
                    }
                    modelName = line.substring(2);
                }
            }
        }
        if (!polys.isEmpty()) {
            modelMap.put(modelName, createModel(verts, uvs, normals, vertexMode, polys));
        }
        return modelMap;
    }
    
    public static void triangulate(final List<int[]> polys, final int[][] polyVerts) {
        for (int i = 2; i < polyVerts.length; ++i) {
            polys.add(polyVerts[0]);
            polys.add(polyVerts[i]);
            polys.add(polyVerts[i - 1]);
        }
    }
    
    public static void quadulate(final List<int[]> polys, final int[][] polyVerts) {
        if (polyVerts.length == 4) {
            polys.add(polyVerts[0]);
            polys.add(polyVerts[3]);
            polys.add(polyVerts[2]);
            polys.add(polyVerts[1]);
        }
        else {
            for (int i = 2; i < polyVerts.length; ++i) {
                polys.add(polyVerts[0]);
                polys.add(polyVerts[i]);
                polys.add(polyVerts[i - 1]);
                polys.add(polyVerts[i - 1]);
            }
        }
    }
    
    public static Map<String, CCModel> parseObjModels(final ResourceLocation res) {
        return parseObjModels(res, 4, null);
    }
    
    public static Map<String, CCModel> parseObjModels(final ResourceLocation res, final Transformation coordSystem) {
        try {
            return parseObjModels(Minecraft.getMinecraft().getResourceManager().getResource(res).getInputStream(), 4, coordSystem);
        }
        catch (final IOException e) {
            throw new RuntimeException("failed to load model: " + res, e);
        }
    }
    
    public static Map<String, CCModel> parseObjModels(final ResourceLocation res, final int vertexMode, final Transformation coordSystem) {
        try {
            return parseObjModels(Minecraft.getMinecraft().getResourceManager().getResource(res).getInputStream(), vertexMode, coordSystem);
        }
        catch (final Exception e) {
            throw new RuntimeException("failed to load model: " + res, e);
        }
    }
    
    public static CCModel createModel(final List<Vector3> verts, final List<Vector3> uvs, final List<Vector3> normals, final int vertexMode, final List<int[]> polys) {
        final int vp = (vertexMode == 7) ? 4 : 3;
        if (polys.size() < vp || polys.size() % vp != 0) {
            throw new IllegalArgumentException("Invalid number of vertices for model: " + polys.size());
        }
        final boolean hasNormals = polys.get(0)[2] > 0;
        final CCModel model = newModel(vertexMode, polys.size());
        if (hasNormals) {
            model.getOrAllocate(CCRenderState.normalAttrib);
        }
        for (int i = 0; i < polys.size(); ++i) {
            final int[] ai = polys.get(i);
            final Vector3 vert = verts.get(ai[0] - 1).copy();
            final Vector3 uv = (ai[1] <= 0) ? new Vector3() : uvs.get(ai[1] - 1).copy();
            if (ai[2] > 0 != hasNormals) {
                throw new IllegalArgumentException("Normals are an all or nothing deal here.");
            }
            model.verts[i] = new Vertex5(vert, uv.x, uv.y);
            if (hasNormals) {
                model.normals()[i] = normals.get(ai[2] - 1).copy();
            }
        }
        return model;
    }
    
    private static <T> int addIndex(final List<T> list, final T elem) {
        int i = list.indexOf(elem) + 1;
        if (i == 0) {
            list.add(elem);
            i = list.size();
        }
        return i;
    }
    
    private static String clean(final double d) {
        return (d == (int)d) ? Integer.toString((int)d) : Double.toString(d);
    }
    
    public static void exportObj(final Map<String, CCModel> models, final PrintWriter p) {
        final List<Vector3> verts = new ArrayList<Vector3>();
        final List<UV> uvs = new ArrayList<UV>();
        final List<Vector3> normals = new ArrayList<Vector3>();
        final List<int[]> polys = new ArrayList<int[]>();
        for (final Map.Entry<String, CCModel> e : models.entrySet()) {
            p.println("g " + e.getKey());
            final CCModel m = e.getValue();
            final int vStart = verts.size();
            final int uStart = uvs.size();
            final int nStart = normals.size();
            final boolean hasNormals = m.normals() != null;
            polys.clear();
            for (int i = 0; i < m.verts.length; ++i) {
                final int[] ia = new int[hasNormals ? 3 : 2];
                ia[0] = addIndex(verts, m.verts[i].vec);
                ia[1] = addIndex(uvs, m.verts[i].uv);
                if (hasNormals) {
                    ia[2] = addIndex(normals, m.normals()[i]);
                }
                polys.add(ia);
            }
            if (vStart < verts.size()) {
                p.println();
                for (int i = vStart; i < verts.size(); ++i) {
                    final Vector3 v = verts.get(i);
                    p.format("v %s %s %s\n", clean(v.x), clean(v.y), clean(v.z));
                }
            }
            if (uStart < uvs.size()) {
                p.println();
                for (int i = uStart; i < uvs.size(); ++i) {
                    final UV uv = uvs.get(i);
                    p.format("vt %s %s\n", clean(uv.u), clean(uv.v));
                }
            }
            if (nStart < normals.size()) {
                p.println();
                for (int i = nStart; i < normals.size(); ++i) {
                    final Vector3 n = normals.get(i);
                    p.format("vn %s %s %s\n", clean(n.x), clean(n.y), clean(n.z));
                }
            }
            p.println();
            for (int i = 0; i < polys.size(); ++i) {
                if (i % m.vp == 0) {
                    p.format("f", new Object[0]);
                }
                final int[] ia = polys.get(i);
                if (hasNormals) {
                    p.format(" %d/%d/%d", ia[0], ia[1], ia[2]);
                }
                else {
                    p.format(" %d/%d", ia[0], ia[1]);
                }
                if (i % m.vp == m.vp - 1) {
                    p.println();
                }
            }
        }
    }
    
    public CCModel shrinkUVs(final double d) {
        for (int k = 0; k < this.verts.length; k += this.vp) {
            final UV uv = new UV();
            for (int i = 0; i < this.vp; ++i) {
                uv.add(this.verts[k + i].uv);
            }
            uv.multiply(1.0 / this.vp);
            for (int i = 0; i < this.vp; ++i) {
                final Vertex5 vert = this.verts[k + i];
                final UV uv2 = vert.uv;
                uv2.u += ((vert.uv.u < uv.u) ? d : (-d));
                final UV uv3 = vert.uv;
                uv3.v += ((vert.uv.v < uv.v) ? d : (-d));
            }
        }
        return this;
    }
    
    public CCModel sidedCopy(final int side1, final int side2, final Vector3 point) {
        final CCModel model = newModel(this.vertexMode, this.verts.length);
        copy(this, 0, model, 0, model.verts.length);
        return model.apply(new TransformationList(new Transformation[] { (Rotation.sideRotations[side1]).inverse(), Rotation.sideRotations[side2] }).at(point));
    }
    
    public static void copy(final CCModel src, final int srcpos, final CCModel dst, final int destpos, final int length) {
        for (int k = 0; k < length; ++k) {
            dst.verts[destpos + k] = src.verts[srcpos + k].copy();
        }
        for (int i = 0; i < src.attributes.size(); ++i) {
            if (src.attributes.get(i) != null) {
                CCRenderState.arrayCopy(src.attributes.get(i), srcpos, dst.getOrAllocate(CCRenderState.getAttribute(i)), destpos, length);
            }
        }
    }
    
    public static void generateSidedModels(final CCModel[] models, final int side, final Vector3 point) {
        for (int s = 0; s < 6; ++s) {
            if (s != side) {
                models[s] = models[side].sidedCopy(side, s, point);
            }
        }
    }
    
    public static void generateSidedModelsH(final CCModel[] models, final int side, final Vector3 point) {
        for (int s = 2; s < 6; ++s) {
            if (s != side) {
                models[s] = models[side].sidedCopy(side, s, point);
            }
        }
    }
    
    public CCModel backfacedCopy() {
        return generateBackface(this, 0, this.copy(), 0, this.verts.length);
    }
    
    public static CCModel generateBackface(final CCModel src, final int srcpos, final CCModel dst, final int destpos, final int length) {
        final int vp = src.vp;
        if (srcpos % vp != 0 || destpos % vp != 0 || length % vp != 0) {
            throw new IllegalArgumentException("Vertices do not align with polygons");
        }
        final int[][] o = { { 0, 0 }, { 1, vp - 1 }, { 2, vp - 2 }, { 3, vp - 3 } };
        for (int i = 0; i < length; ++i) {
            final int b = i / vp * vp;
            final int d = i % vp;
            final int di = destpos + b + o[d][1];
            final int si = srcpos + b + o[d][0];
            dst.verts[di] = src.verts[si].copy();
            for (int a = 0; a < src.attributes.size(); ++a) {
                if (src.attributes.get(a) != null) {
                    CCRenderState.arrayCopy(src.attributes.get(a), si, dst.getOrAllocate(CCRenderState.getAttribute(a)), di, 1);
                }
            }
            if (dst.normals() != null && dst.normals()[di] != null) {
                dst.normals()[di].negate();
            }
        }
        return dst;
    }
    
    public CCModel generateSidedParts(final int side, final Vector3 point) {
        if (this.verts.length % (6 * this.vp) != 0) {
            throw new IllegalArgumentException("Invalid number of vertices for sided part generation");
        }
        final int length = this.verts.length / 6;
        for (int s = 0; s < 6; ++s) {
            if (s != side) {
                this.generateSidedPart(side, s, point, length * side, length * s, length);
            }
        }
        return this;
    }
    
    public CCModel generateSidedPartsH(final int side, final Vector3 point) {
        if (this.verts.length % (4 * this.vp) != 0) {
            throw new IllegalArgumentException("Invalid number of vertices for sided part generation");
        }
        final int length = this.verts.length / 4;
        for (int s = 2; s < 6; ++s) {
            if (s != side) {
                this.generateSidedPart(side, s, point, length * (side - 2), length * (s - 2), length);
            }
        }
        return this;
    }
    
    public CCModel generateSidedPart(final int side1, final int side2, final Vector3 point, final int srcpos, final int destpos, final int length) {
        return this.apply(new TransformationList(new Transformation[] { (Rotation.sideRotations[side1]).inverse(), Rotation.sideRotations[side2] }).at(point), srcpos, destpos, length);
    }
    
    public CCModel apply(final Transformation t, final int srcpos, final int destpos, final int length) {
        for (int k = 0; k < length; ++k) {
            this.verts[destpos + k] = this.verts[srcpos + k].copy();
            this.verts[destpos + k].vec.apply(t);
        }
        final Vector3[] normals = this.normals();
        if (normals != null) {
            for (int i = 0; i < length; ++i) {
                t.applyN(normals[destpos + i] = normals[srcpos + i].copy());
            }
        }
        return this;
    }
    
    public static CCModel combine(final Collection<CCModel> models) {
        if (models.isEmpty()) {
            return null;
        }
        int numVerts = 0;
        int vertexMode = -1;
        for (final CCModel model : models) {
            if (vertexMode == -1) {
                vertexMode = model.vertexMode;
            }
            if (vertexMode != model.vertexMode) {
                throw new IllegalArgumentException("Cannot combine models with different vertex modes");
            }
            numVerts += model.verts.length;
        }
        final CCModel c_model = newModel(vertexMode, numVerts);
        int i = 0;
        for (final CCModel model2 : models) {
            copy(model2, 0, c_model, i, model2.verts.length);
            i += model2.verts.length;
        }
        return c_model;
    }
    
    public CCModel twoFacedCopy() {
        final CCModel model = newModel(this.vertexMode, this.verts.length * 2);
        copy(this, 0, model, 0, this.verts.length);
        return generateBackface(model, 0, model, this.verts.length, this.verts.length);
    }
    
    @Override
    public CCModel copy() {
        final CCModel model = newModel(this.vertexMode, this.verts.length);
        copy(this, 0, model, 0, this.verts.length);
        return model;
    }
    
    public Vector3 collapse() {
        final Vector3 v = new Vector3();
        for (final Vertex5 vert : this.verts) {
            v.add(vert.vec);
        }
        v.multiply(1.0 / this.verts.length);
        return v;
    }
    
    public CCModel zOffset(final Cuboid6 offsets) {
        for (int k = 0; k < this.verts.length; ++k) {
            final Vertex5 vert = this.verts[k];
            final Vector3 normal = this.normals()[k];
            switch (findSide(normal)) {
                case 0: {
                    final Vector3 vec = vert.vec;
                    vec.y += offsets.min.y;
                    break;
                }
                case 1: {
                    final Vector3 vec2 = vert.vec;
                    vec2.y += offsets.max.y;
                    break;
                }
                case 2: {
                    final Vector3 vec3 = vert.vec;
                    vec3.z += offsets.min.z;
                    break;
                }
                case 3: {
                    final Vector3 vec4 = vert.vec;
                    vec4.z += offsets.max.z;
                    break;
                }
                case 4: {
                    final Vector3 vec5 = vert.vec;
                    vec5.x += offsets.min.x;
                    break;
                }
                case 5: {
                    final Vector3 vec6 = vert.vec;
                    vec6.x += offsets.max.x;
                    break;
                }
            }
        }
        return this;
    }
    
    public static int findSide(final Vector3 normal) {
        if (normal.y <= -0.99) {
            return 0;
        }
        if (normal.y >= 0.99) {
            return 1;
        }
        if (normal.z <= -0.99) {
            return 2;
        }
        if (normal.z >= 0.99) {
            return 3;
        }
        if (normal.x <= -0.99) {
            return 4;
        }
        if (normal.x >= 0.99) {
            return 5;
        }
        return -1;
    }
    
    public Cuboid6 bounds() {
        final Vector3 vec1 = this.verts[0].vec;
        final Cuboid6 c = new Cuboid6(vec1.copy(), vec1.copy());
        for (int i = 1; i < this.verts.length; ++i) {
            c.enclose(this.verts[i].vec);
        }
        return c;
    }
    
    static {
        vertPattern = Pattern.compile("v(?: ([\\d\\.+-]+))+");
        uvwPattern = Pattern.compile("vt(?: ([\\d\\.+-]+))+");
        normalPattern = Pattern.compile("vn(?: ([\\d\\.+-]+))+");
        polyPattern = Pattern.compile("f(?: ((?:\\d*)(?:/\\d*)?(?:/\\d*)?))+");
        vertMatcher = CCModel.vertPattern.matcher("");
        uvwMatcher = CCModel.uvwPattern.matcher("");
        normalMatcher = CCModel.normalPattern.matcher("");
        polyMatcher = CCModel.polyPattern.matcher("");
    }
    
    private static class PositionNormalEntry
    {
        public Vector3 pos;
        public LinkedList<Vector3> normals;
        
        public PositionNormalEntry(final Vector3 position) {
            this.normals = new LinkedList<Vector3>();
            this.pos = position;
        }
        
        public boolean positionEqual(final Vector3 v) {
            return this.pos.x == v.x && this.pos.y == v.y && this.pos.z == v.z;
        }
        
        public PositionNormalEntry addNormal(final Vector3 normal) {
            this.normals.add(normal);
            return this;
        }
    }
}
