package thaumcraft.common.items.armor;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.renderers.models.gear.ModelKnightArmor;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemCultistPlateArmor extends ItemArmor implements IThaumcraftItems
{
    ModelBiped model1;
    ModelBiped model2;
    ModelBiped model;
    
    public ItemCultistPlateArmor(String name, ItemArmor.ArmorMaterial enumarmormaterial, int j, EntityEquipmentSlot k) {
        super(enumarmormaterial, j, k);
        model1 = null;
        model2 = null;
        model = null;
        setCreativeTab(ConfigItems.TABTC);
        setRegistryName(name);
        setUnlocalizedName(name);
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
    
    @SideOnly(Side.CLIENT)
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return (entity instanceof EntityInhabitedZombie) ? "thaumcraft:textures/entity/armor/zombie_plate_armor.png" : "thaumcraft:textures/entity/armor/cultist_plate_armor.png";
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(Items.IRON_INGOT)) || super.getIsRepairable(stack1, stack2);
    }
    
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (model1 == null) {
            model1 = new ModelKnightArmor(1.0f);
        }
        if (model2 == null) {
            model2 = new ModelKnightArmor(0.5f);
        }
        return model = CustomArmorHelper.getCustomArmorModel(entityLiving, itemStack, armorSlot, model, model1, model2);
    }
}
