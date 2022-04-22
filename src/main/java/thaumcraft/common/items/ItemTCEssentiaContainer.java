// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.items.ItemGenericEssentiaContainer;

public class ItemTCEssentiaContainer extends ItemGenericEssentiaContainer implements IEssentiaContainerItem, IThaumcraftItems
{
    private final String BASE_NAME;
    protected String[] VARIANTS;
    protected int[] VARIANTS_META;
    
    public ItemTCEssentiaContainer(final String name, final int base, final String... variants) {
        super(base);
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
    
    @Override
    public Item getItem() {
        return this;
    }
    
    @Override
    public String[] getVariantNames() {
        return VARIANTS;
    }
    
    @Override
    public int[] getVariantMeta() {
        return VARIANTS_META;
    }
    
    @Override
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    @Override
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        if (variant.equals(BASE_NAME)) {
            return new ModelResourceLocation("thaumcraft:" + BASE_NAME);
        }
        return new ModelResourceLocation("thaumcraft:" + BASE_NAME, variant);
    }
}
