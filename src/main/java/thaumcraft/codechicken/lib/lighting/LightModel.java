// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.lighting;

import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.codechicken.lib.render.CCRenderState;

public class LightModel implements CCRenderState.IVertexOperation
{
    public static final int operationIndex;
    public static LightModel standardLightModel;
    private Vector3 ambient;
    private Light[] lights;
    private int lightCount;
    
    public LightModel() {
        ambient = new Vector3();
        lights = new Light[8];
    }
    
    public LightModel addLight(final Light light) {
        lights[lightCount++] = light;
        return this;
    }
    
    public LightModel setAmbient(final Vector3 vec) {
        ambient.set(vec);
        return this;
    }
    
    public int apply(final int colour, final Vector3 normal) {
        final Vector3 n_colour = ambient.copy();
        for (int l = 0; l < lightCount; ++l) {
            final Light light = lights[l];
            final double n_l = light.position.dotProduct(normal);
            final double f = (n_l > 0.0) ? 1.0 : 0.0;
            final Vector3 vector3 = n_colour;
            vector3.x += light.ambient.x + f * light.diffuse.x * n_l;
            final Vector3 vector4 = n_colour;
            vector4.y += light.ambient.y + f * light.diffuse.y * n_l;
            final Vector3 vector5 = n_colour;
            vector5.z += light.ambient.z + f * light.diffuse.z * n_l;
        }
        if (n_colour.x > 1.0) {
            n_colour.x = 1.0;
        }
        if (n_colour.y > 1.0) {
            n_colour.y = 1.0;
        }
        if (n_colour.z > 1.0) {
            n_colour.z = 1.0;
        }
        n_colour.multiply((colour >>> 24) / 255.0, (colour >> 16 & 0xFF) / 255.0, (colour >> 8 & 0xFF) / 255.0);
        return (int)(n_colour.x * 255.0) << 24 | (int)(n_colour.y * 255.0) << 16 | (int)(n_colour.z * 255.0) << 8 | (colour & 0xFF);
    }
    
    @Override
    public boolean load() {
        CCRenderState.pipeline.addDependency(CCRenderState.normalAttrib);
        CCRenderState.pipeline.addDependency(CCRenderState.colourAttrib);
        return true;
    }
    
    @Override
    public void operate() {
        CCRenderState.setColour(apply(CCRenderState.colour, CCRenderState.normal));
    }
    
    @Override
    public int operationID() {
        return LightModel.operationIndex;
    }
    
    public PlanarLightModel reducePlanar() {
        final int[] colours = new int[6];
        for (int i = 0; i < 6; ++i) {
            colours[i] = apply(-1, Rotation.axes[i]);
        }
        return new PlanarLightModel(colours);
    }
    
    static {
        operationIndex = CCRenderState.registerOperation();
        LightModel.standardLightModel = new LightModel().setAmbient(new Vector3(0.4, 0.4, 0.4)).addLight(new Light(new Vector3(0.2, 1.0, -0.7)).setDiffuse(new Vector3(0.6, 0.6, 0.6))).addLight(new Light(new Vector3(-0.2, 1.0, 0.7)).setDiffuse(new Vector3(0.6, 0.6, 0.6)));
    }
    
    public static class Light
    {
        public Vector3 ambient;
        public Vector3 diffuse;
        public Vector3 position;
        
        public Light(final Vector3 pos) {
            ambient = new Vector3();
            diffuse = new Vector3();
            position = pos.copy().normalize();
        }
        
        public Light setDiffuse(final Vector3 vec) {
            diffuse.set(vec);
            return this;
        }
        
        public Light setAmbient(final Vector3 vec) {
            ambient.set(vec);
            return this;
        }
    }
}
