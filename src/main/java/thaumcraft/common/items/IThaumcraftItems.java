// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;

public interface IThaumcraftItems
{
    Item getItem();
    
    String[] getVariantNames();
    
    int[] getVariantMeta();
    
    ItemMeshDefinition getCustomMesh();
    
    ModelResourceLocation getCustomModelResourceLocation(final String p0);
}
