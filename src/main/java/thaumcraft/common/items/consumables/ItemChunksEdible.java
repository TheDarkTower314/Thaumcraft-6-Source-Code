// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.consumables;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import net.minecraft.item.ItemFood;

public class ItemChunksEdible extends ItemFood implements IThaumcraftItems
{
    public final int itemUseDuration;
    private String[] variants;
    
    public ItemChunksEdible() {
        super(1, 0.3f, false);
        this.variants = new String[] { "beef", "chicken", "pork", "fish", "rabbit", "mutton" };
        this.itemUseDuration = 10;
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setRegistryName("chunk");
        this.setUnlocalizedName("chunk");
        this.setCreativeTab(ConfigItems.TABTC);
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public int getMaxItemUseDuration(final ItemStack stack1) {
        return this.itemUseDuration;
    }
    
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            for (int a = 0; a < this.variants.length; ++a) {
                items.add(new ItemStack(this, 1, a));
            }
        }
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        if (this.hasSubtypes && itemStack.getMetadata() < this.variants.length && this.variants[itemStack.getMetadata()] != "chunk") {
            return String.format(super.getUnlocalizedName() + ".%s", this.variants[itemStack.getMetadata()]);
        }
        return super.getUnlocalizedName(itemStack);
    }
    
    public String[] getVariantNames() {
        return this.variants;
    }
    
    public int[] getVariantMeta() {
        return new int[] { 0, 1, 2, 3, 4, 5 };
    }
    
    public Item getItem() {
        return this;
    }
    
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        if (variant.equals("chunk")) {
            return new ModelResourceLocation("thaumcraft:chunk");
        }
        return new ModelResourceLocation("thaumcraft:chunk", variant);
    }
}
