// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.obj;

import net.minecraft.util.ResourceLocation;

public class ObjModelLoader implements IModelCustomLoader
{
    private static final String[] types;
    
    @Override
    public String getType() {
        return "OBJ model";
    }
    
    @Override
    public String[] getSuffixes() {
        return ObjModelLoader.types;
    }
    
    @Override
    public IModelCustom loadInstance(final ResourceLocation resource) throws WavefrontObject.ModelFormatException {
        return new WavefrontObject(resource);
    }
    
    static {
        types = new String[] { "obj" };
    }
}
