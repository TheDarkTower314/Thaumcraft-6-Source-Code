package thaumcraft.client.lib.obj;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public interface IModelCustom
{
    String getType();
    
    @SideOnly(Side.CLIENT)
    void renderAll();
    
    @SideOnly(Side.CLIENT)
    void renderOnly(String... p0);
    
    @SideOnly(Side.CLIENT)
    void renderPart(String p0);
    
    @SideOnly(Side.CLIENT)
    void renderAllExcept(String... p0);
    
    @SideOnly(Side.CLIENT)
    String[] getPartNames();
}
