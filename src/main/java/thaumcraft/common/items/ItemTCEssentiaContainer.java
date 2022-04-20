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
    
    @Override
    public Item getItem() {
        return this;
    }
    
    @Override
    public String[] getVariantNames() {
        return this.VARIANTS;
    }
    
    @Override
    public int[] getVariantMeta() {
        return this.VARIANTS_META;
    }
    
    @Override
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    @Override
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        if (variant.equals(this.BASE_NAME)) {
            return new ModelResourceLocation("thaumcraft:" + this.BASE_NAME);
        }
        return new ModelResourceLocation("thaumcraft:" + this.BASE_NAME, variant);
    }
}
