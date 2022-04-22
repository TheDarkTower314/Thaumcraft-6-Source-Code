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
        setRegistryName(name);
        setUnlocalizedName(name);
        setCreativeTab(ConfigItems.TABTC);
        setNoRepair();
        setHasSubtypes(variants.length > 1);
        BASE_NAME = name;
        if (variants.length == 0) {
            VARIANTS = new String[] { name };
        }
        else {
            VARIANTS = variants;
        }
        VARIANTS_META = new int[VARIANTS.length];
        for (int m = 0; m < VARIANTS.length; ++m) {
            VARIANTS_META[m] = m;
        }
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        if (hasSubtypes && itemStack.getMetadata() < VARIANTS.length && VARIANTS[itemStack.getMetadata()] != BASE_NAME) {
            return String.format(super.getUnlocalizedName() + ".%s", VARIANTS[itemStack.getMetadata()]);
        }
        return super.getUnlocalizedName(itemStack);
    }
    
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            if (!getHasSubtypes()) {
                super.getSubItems(tab, items);
            }
            else {
                for (int meta = 0; meta < VARIANTS.length; ++meta) {
                    items.add(new ItemStack(this, 1, meta));
                }
            }
        }
    }
    
    public Item getItem() {
        return this;
    }
    
    public String[] getVariantNames() {
        return VARIANTS;
    }
    
    public int[] getVariantMeta() {
        return VARIANTS_META;
    }
    
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        if (variant.equals(BASE_NAME)) {
            return new ModelResourceLocation("thaumcraft:" + BASE_NAME);
        }
        return new ModelResourceLocation("thaumcraft:" + BASE_NAME, variant);
    }
}
