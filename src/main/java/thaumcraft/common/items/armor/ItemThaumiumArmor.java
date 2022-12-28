package thaumcraft.common.items.armor;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemThaumiumArmor extends ItemArmor implements IThaumcraftItems
{
    public ItemThaumiumArmor(String name, ItemArmor.ArmorMaterial enumarmormaterial, int j, EntityEquipmentSlot k) {
        super(enumarmormaterial, j, k);
        setRegistryName(name);
        setUnlocalizedName(name);
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
        setCreativeTab(ConfigItems.TABTC);
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
    
    @SideOnly(Side.CLIENT)
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (stack.getItem() == ItemsTC.thaumiumHelm || stack.getItem() == ItemsTC.thaumiumChest || stack.getItem() == ItemsTC.thaumiumBoots) {
            return "thaumcraft:textures/entity/armor/thaumium_1.png";
        }
        if (stack.getItem() == ItemsTC.thaumiumLegs) {
            return "thaumcraft:textures/entity/armor/thaumium_2.png";
        }
        return "thaumcraft:textures/entity/armor/thaumium_1.png";
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 0)) || super.getIsRepairable(stack1, stack2);
    }
}
