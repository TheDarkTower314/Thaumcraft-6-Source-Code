// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.Item;

public class ItemTCBase extends Item implements IThaumcraftItems
{
    protected final String BASE_NAME;
    protected String[] VARIANTS;
    protected int[] VARIANTS_META;
    
    public ItemTCBase(final String name, final String... variants) {
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(ConfigItems.TABTC);
        this.setNoRepair();
        this.setHasSubtypes(variants.length > 1);
        this.BASE_NAME = name;
        if (variants.length == 0) {
            this.VARIANTS = new String[] { name };
        }
        else {
            this.VARIANTS = variants;
        }
        this.VARIANTS_META = new int[this.VARIANTS.length];
        for (int m = 0; m < this.VARIANTS.length; ++m) {
            this.VARIANTS_META[m] = m;
        }
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        if (this.hasSubtypes && itemStack.getMetadata() < this.VARIANTS.length && this.VARIANTS[itemStack.getMetadata()] != this.BASE_NAME) {
            return String.format(super.getUnlocalizedName() + ".%s", this.VARIANTS[itemStack.getMetadata()]);
        }
        return super.getUnlocalizedName(itemStack);
    }
    
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            if (!this.getHasSubtypes()) {
                super.getSubItems(tab, items);
            }
            else {
                for (int meta = 0; meta < this.VARIANTS.length; ++meta) {
                    items.add(new ItemStack(this, 1, meta));
                }
            }
        }
    }
    
    public Item getItem() {
        return this;
    }
    
    public String[] getVariantNames() {
        return this.VARIANTS;
    }
    
    public int[] getVariantMeta() {
        return this.VARIANTS_META;
    }
    
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        if (variant.equals(this.BASE_NAME)) {
            return new ModelResourceLocation("thaumcraft:" + this.BASE_NAME);
        }
        return new ModelResourceLocation("thaumcraft:" + this.BASE_NAME, variant);
    }
}
