// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.Item;
import thaumcraft.common.items.IThaumcraftItems;
import net.minecraft.item.ItemSword;

public class ItemThaumiumSword extends ItemSword implements IThaumcraftItems
{
    public ItemThaumiumSword(final Item.ToolMaterial enumtoolmaterial) {
        super(enumtoolmaterial);
        setCreativeTab(ConfigItems.TABTC);
        setRegistryName("thaumium_sword");
        setUnlocalizedName("thaumium_sword");
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public Item getItem() {
        return this;
    }
    
    public String[] getVariantNames() {
        return new String[] { "normal" };
    }
    
    public int[] getVariantMeta() {
        return new int[] { 0 };
    }
    
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public boolean getIsRepairable(final ItemStack stack1, final ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 0)) || super.getIsRepairable(stack1, stack2);
    }
}
