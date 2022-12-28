package thaumcraft.client.lib.obj;
import net.minecraft.util.ResourceLocation;


public class ObjModelLoader implements IModelCustomLoader
{
    private static String[] types;
    
    @Override
    public String getType() {
        return "OBJ model";
    }
    
    @Override
    public String[] getSuffixes() {
        return ObjModelLoader.types;
    }
    
    @Override
    public IModelCustom loadInstance(ResourceLocation resource) throws WavefrontObject.ModelFormatException {
        return new WavefrontObject(resource);
    }
    
    static {
        types = new String[] { "obj" };
    }
}
