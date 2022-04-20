// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.obj;

import com.google.common.collect.Maps;
import java.util.Collection;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraft.util.ResourceLocation;
import java.util.Map;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AdvancedModelLoader
{
    private static Map<String, IModelCustomLoader> instances;
    
    public static void registerModelHandler(final IModelCustomLoader modelHandler) {
        for (final String suffix : modelHandler.getSuffixes()) {
            AdvancedModelLoader.instances.put(suffix, modelHandler);
        }
    }
    
    public static IModelCustom loadModel(final ResourceLocation resource) throws IllegalArgumentException, WavefrontObject.ModelFormatException {
        final String name = resource.getResourcePath();
        final int i = name.lastIndexOf(46);
        if (i == -1) {
            FMLLog.severe("The resource name %s is not valid", new Object[] { resource });
            throw new IllegalArgumentException("The resource name is not valid");
        }
        final String suffix = name.substring(i + 1);
        final IModelCustomLoader loader = AdvancedModelLoader.instances.get(suffix);
        if (loader == null) {
            FMLLog.severe("The resource name %s is not supported", new Object[] { resource });
            throw new IllegalArgumentException("The resource name is not supported");
        }
        return loader.loadInstance(resource);
    }
    
    public static Collection<String> getSupportedSuffixes() {
        return AdvancedModelLoader.instances.keySet();
    }
    
    static {
        AdvancedModelLoader.instances = Maps.newHashMap();
        registerModelHandler(new ObjModelLoader());
    }
}
