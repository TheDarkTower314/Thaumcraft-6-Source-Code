package thaumcraft.common.items.consumables;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemChunksEdible extends ItemFood implements IThaumcraftItems
{
    public int itemUseDuration;
    private String[] variants;
    
    public ItemChunksEdible() {
        super(1, 0.3f, false);
        variants = new String[] { "beef", "chicken", "pork", "fish", "rabbit", "mutton" };
        itemUseDuration = 10;
        setMaxStackSize(64);
        setHasSubtypes(true);
        setMaxDamage(0);
        setRegistryName("chunk");
        setUnlocalizedName("chunk");
        setCreativeTab(ConfigItems.TABTC);
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public int getMaxItemUseDuration(ItemStack stack1) {
        return itemUseDuration;
    }
    
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            for (int a = 0; a < variants.length; ++a) {
                items.add(new ItemStack(this, 1, a));
            }
        }
    }
    
    public String getUnlocalizedName(ItemStack itemStack) {
        if (hasSubtypes && itemStack.getMetadata() < variants.length && variants[itemStack.getMetadata()] != "chunk") {
            return String.format(super.getUnlocalizedName() + ".%s", variants[itemStack.getMetadata()]);
        }
        return super.getUnlocalizedName(itemStack);
    }
    
    public String[] getVariantNames() {
        return variants;
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
    
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        if (variant.equals("chunk")) {
            return new ModelResourceLocation("thaumcraft:chunk");
        }
        return new ModelResourceLocation("thaumcraft:chunk", variant);
    }
}
