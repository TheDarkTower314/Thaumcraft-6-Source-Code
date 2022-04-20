// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.obj;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModelCustom
{
    String getType();
    
    @SideOnly(Side.CLIENT)
    void renderAll();
    
    @SideOnly(Side.CLIENT)
    void renderOnly(final String... p0);
    
    @SideOnly(Side.CLIENT)
    void renderPart(final String p0);
    
    @SideOnly(Side.CLIENT)
    void renderAllExcept(final String... p0);
    
    @SideOnly(Side.CLIENT)
    String[] getPartNames();
}
