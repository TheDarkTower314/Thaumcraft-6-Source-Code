// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.obj;

import net.minecraft.util.ResourceLocation;

public interface IModelCustomLoader
{
    String getType();
    
    String[] getSuffixes();
    
    IModelCustom loadInstance(ResourceLocation p0) throws WavefrontObject.ModelFormatException;
}
