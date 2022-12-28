package thaumcraft.codechicken.lib.render;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import thaumcraft.codechicken.lib.colour.ColourRGBA;
import thaumcraft.codechicken.lib.lighting.LC;
import thaumcraft.codechicken.lib.lighting.LightMatrix;
import thaumcraft.codechicken.lib.util.Copyable;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.Vector3;


public class CCRenderState
{
    private static int nextOperationIndex;
    private static ArrayList<VertexAttribute<?>> vertexAttributes;
    public static VertexAttribute<Vector3[]> normalAttrib;
    public static VertexAttribute<int[]> colourAttrib;
    public static VertexAttribute<int[]> lightingAttrib;
    public static VertexAttribute<int[]> sideAttrib;
    public static VertexAttribute<LC[]> lightCoordAttrib;
    public static IVertexSource model;
    public static int firstVertexIndex;
    public static int lastVertexIndex;
    public static int vertexIndex;
    public static CCRenderPipeline pipeline;
    public static int baseColour;
    public static int alphaOverride;
    public static boolean useNormals;
    public static boolean computeLighting;
    public static boolean useColour;
    public static LightMatrix lightMatrix;
    public static Vertex5 vert;
    public static boolean hasNormal;
    public static Vector3 normal;
    public static boolean hasColour;
    public static int colour;
    public static boolean hasBrightness;
    public static int brightness;
    public static int side;
    public static LC lc;
    
    public static int registerOperation() {
        return CCRenderState.nextOperationIndex++;
    }
    
    public static int operationCount() {
        return CCRenderState.nextOperationIndex;
    }
    
    private static int registerVertexAttribute(VertexAttribute<?> attr) {
        CCRenderState.vertexAttributes.add(attr);
        return CCRenderState.vertexAttributes.size() - 1;
    }
    
    public static VertexAttribute<?> getAttribute(int index) {
        return CCRenderState.vertexAttributes.get(index);
    }
    
    public static void arrayCopy(Object src, int srcPos, Object dst, int destPos, int length) {
        System.arraycopy(src, srcPos, dst, destPos, length);
        if (dst instanceof Copyable[]) {
            Object[] oa = (Object[])dst;
            Copyable<Object>[] c = (Copyable<Object>[])dst;
            for (int i = destPos; i < destPos + length; ++i) {
                if (c[i] != null) {
                    oa[i] = c[i].copy();
                }
            }
        }
    }
    
    public static <T> T copyOf(VertexAttribute<T> attr, T src, int length) {
        T dst = attr.newArray(length);
        arrayCopy(src, 0, dst, 0, ((Object[]) src).length);
        return dst;
    }
    
    public static void reset() {
        CCRenderState.model = null;
        CCRenderState.pipeline.reset();
        CCRenderState.useNormals = (CCRenderState.hasNormal = (CCRenderState.hasBrightness = (CCRenderState.hasColour = false)));
        CCRenderState.useColour = (CCRenderState.computeLighting = true);
        CCRenderState.baseColour = (CCRenderState.alphaOverride = -1);
    }
    
    public static void setPipeline(IVertexOperation... ops) {
        CCRenderState.pipeline.setPipeline(ops);
    }
    
    public static void setPipeline(IVertexSource model, int start, int end, IVertexOperation... ops) {
        CCRenderState.pipeline.reset();
        setModel(model, start, end);
        CCRenderState.pipeline.setPipeline(ops);
    }
    
    public static void bindModel(IVertexSource model) {
        if (CCRenderState.model != model) {
            CCRenderState.model = model;
            CCRenderState.pipeline.rebuild();
        }
    }
    
    public static void setModel(IVertexSource source) {
        setModel(source, 0, source.getVertices().length);
    }
    
    public static void setModel(IVertexSource source, int start, int end) {
        bindModel(source);
        setVertexRange(start, end);
    }
    
    public static void setVertexRange(int start, int end) {
        CCRenderState.firstVertexIndex = start;
        CCRenderState.lastVertexIndex = end;
    }
    
    public static void setNormal(double x, double y, double z) {
        CCRenderState.hasNormal = true;
        CCRenderState.normal.set(x, y, z);
    }
    
    public static void setNormal(Vector3 n) {
        CCRenderState.hasNormal = true;
        CCRenderState.normal.set(n);
    }
    
    public static void setColour(int c) {
        CCRenderState.hasColour = true;
        CCRenderState.colour = c;
    }
    
    public static void setBrightness(int b) {
        CCRenderState.hasBrightness = true;
        CCRenderState.brightness = b;
    }
    
    public static void pullLightmap() {
        setBrightness((int)OpenGlHelper.lastBrightnessY << 16 | (int)OpenGlHelper.lastBrightnessX);
    }
    
    public static void pushLightmap() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(CCRenderState.brightness & 0xFFFF), (float)(CCRenderState.brightness >>> 16));
    }
    
    public static void setDynamic() {
        CCRenderState.useNormals = true;
        CCRenderState.computeLighting = false;
    }
    
    public static void changeTexture(String texture) {
        changeTexture(new ResourceLocation(texture));
    }
    
    public static void changeTexture(ResourceLocation texture) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    }
    
    public static void draw() {
        Tessellator.getInstance().draw();
    }
    
    static {
        CCRenderState.vertexAttributes = new ArrayList<VertexAttribute<?>>();
        CCRenderState.normalAttrib = new VertexAttribute<Vector3[]>() {
            private Vector3[] normalRef;
            
            @Override
            public Vector3[] newArray(int length) {
                return new Vector3[length];
            }
            
            @Override
            public boolean load() {
                normalRef = CCRenderState.model.getAttributes(this);
                if (CCRenderState.model.hasAttribute(this)) {
                    return normalRef != null;
                }
                if (CCRenderState.model.hasAttribute(CCRenderState.sideAttrib)) {
                    CCRenderState.pipeline.addDependency(CCRenderState.sideAttrib);
                    return true;
                }
                throw new IllegalStateException("Normals requested but neither normal or side attrutes are provided by the model");
            }
            
            @Override
            public void operate() {
                if (normalRef != null) {
                    CCRenderState.setNormal(normalRef[CCRenderState.vertexIndex]);
                }
                else {
                    CCRenderState.setNormal(Rotation.axes[CCRenderState.side]);
                }
            }
        };
        CCRenderState.colourAttrib = new VertexAttribute<int[]>() {
            private int[] colourRef;
            
            @Override
            public int[] newArray(int length) {
                return new int[length];
            }
            
            @Override
            public boolean load() {
                colourRef = CCRenderState.model.getAttributes(this);
                return colourRef != null || !CCRenderState.model.hasAttribute(this);
            }
            
            @Override
            public void operate() {
                if (colourRef != null) {
                    CCRenderState.setColour(ColourRGBA.multiply(CCRenderState.baseColour, colourRef[CCRenderState.vertexIndex]));
                }
                else {
                    CCRenderState.setColour(CCRenderState.baseColour);
                }
            }
        };
        CCRenderState.lightingAttrib = new VertexAttribute<int[]>() {
            private int[] colourRef;
            
            @Override
            public int[] newArray(int length) {
                return new int[length];
            }
            
            @Override
            public boolean load() {
                if (!CCRenderState.computeLighting || !CCRenderState.useColour || !CCRenderState.model.hasAttribute(this)) {
                    return false;
                }
                colourRef = CCRenderState.model.getAttributes(this);
                if (colourRef != null) {
                    CCRenderState.pipeline.addDependency(CCRenderState.colourAttrib);
                    return true;
                }
                return false;
            }
            
            @Override
            public void operate() {
                CCRenderState.setColour(ColourRGBA.multiply(CCRenderState.colour, colourRef[CCRenderState.vertexIndex]));
            }
        };
        CCRenderState.sideAttrib = new VertexAttribute<int[]>() {
            private int[] sideRef;
            
            @Override
            public int[] newArray(int length) {
                return new int[length];
            }
            
            @Override
            public boolean load() {
                sideRef = CCRenderState.model.getAttributes(this);
                if (CCRenderState.model.hasAttribute(this)) {
                    return sideRef != null;
                }
                CCRenderState.pipeline.addDependency(CCRenderState.normalAttrib);
                return true;
            }
            
            @Override
            public void operate() {
                if (sideRef != null) {
                    CCRenderState.side = sideRef[CCRenderState.vertexIndex];
                }
                else {
                    CCRenderState.side = CCModel.findSide(CCRenderState.normal);
                }
            }
        };
        CCRenderState.lightCoordAttrib = new VertexAttribute<LC[]>() {
            private LC[] lcRef;
            private Vector3 vec = new Vector3();
            private Vector3 pos = new Vector3();
            
            @Override
            public LC[] newArray(int length) {
                return new LC[length];
            }
            
            @Override
            public boolean load() {
                lcRef = CCRenderState.model.getAttributes(this);
                if (CCRenderState.model.hasAttribute(this)) {
                    return lcRef != null;
                }
                pos.set(CCRenderState.lightMatrix.pos.x, CCRenderState.lightMatrix.pos.y, CCRenderState.lightMatrix.pos.z);
                CCRenderState.pipeline.addDependency(CCRenderState.sideAttrib);
                CCRenderState.pipeline.addRequirement(Transformation.operationIndex);
                return true;
            }
            
            @Override
            public void operate() {
                if (lcRef != null) {
                    CCRenderState.lc.set(lcRef[CCRenderState.vertexIndex]);
                }
                else {
                    CCRenderState.lc.compute(vec.set(CCRenderState.vert.vec).sub(pos), CCRenderState.side);
                }
            }
        };
        CCRenderState.pipeline = new CCRenderPipeline();
        CCRenderState.lightMatrix = new LightMatrix();
        CCRenderState.vert = new Vertex5();
        CCRenderState.normal = new Vector3();
        CCRenderState.lc = new LC();
    }
    
    public abstract static class VertexAttribute<T> implements IVertexOperation
    {
        public int attributeIndex;
        private int operationIndex;
        public boolean active;
        
        public VertexAttribute() {
            attributeIndex = registerVertexAttribute(this);
            operationIndex = CCRenderState.registerOperation();
            active = false;
        }
        
        public abstract T newArray(int p0);
        
        @Override
        public int operationID() {
            return operationIndex;
        }
    }
    
    public interface IVertexSource
    {
        Vertex5[] getVertices();
        
         <T> T getAttributes(VertexAttribute<T> p0);
        
        boolean hasAttribute(VertexAttribute<?> p0);
        
        void prepareVertex();
    }
    
    public interface IVertexOperation
    {
        boolean load();
        
        void operate();
        
        int operationID();
    }
}
