package thaumcraft.common.items.consumables;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemTripleMeatTreat extends ItemFood implements IThaumcraftItems
{
    public ItemTripleMeatTreat() {
        super(6, 0.8f, true);
        setAlwaysEdible();
        setRegistryName("triple_meat_treat");
        setUnlocalizedName("triple_meat_treat");
        setCreativeTab(ConfigItems.TABTC);
        setPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0), 0.66f);
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
    
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
}
