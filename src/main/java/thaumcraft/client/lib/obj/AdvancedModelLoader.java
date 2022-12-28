package thaumcraft.client.lib.obj;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class AdvancedModelLoader
{
    private static Map<String, IModelCustomLoader> instances;
    
    public static void registerModelHandler(IModelCustomLoader modelHandler) {
        for (String suffix : modelHandler.getSuffixes()) {
            AdvancedModelLoader.instances.put(suffix, modelHandler);
        }
    }
    
    public static IModelCustom loadModel(ResourceLocation resource) throws IllegalArgumentException, WavefrontObject.ModelFormatException {
        String name = resource.getResourcePath();
        int i = name.lastIndexOf(46);
        if (i == -1) {
            FMLLog.severe("The resource name %s is not valid", resource);
            throw new IllegalArgumentException("The resource name is not valid");
        }
        String suffix = name.substring(i + 1);
        IModelCustomLoader loader = AdvancedModelLoader.instances.get(suffix);
        if (loader == null) {
            FMLLog.severe("The resource name %s is not supported", resource);
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
