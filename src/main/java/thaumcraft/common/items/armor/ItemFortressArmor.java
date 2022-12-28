package thaumcraft.common.items.armor;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.renderers.models.gear.ModelFortressArmor;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemFortressArmor extends ItemArmor implements ISpecialArmor, IGoggles, IRevealer, IThaumcraftItems
{
    ModelBiped model1;
    ModelBiped model2;
    ModelBiped model;
    
    public ItemFortressArmor(String name, ItemArmor.ArmorMaterial material, int renderIndex, EntityEquipmentSlot armorType) {
        super(material, renderIndex, armorType);
        model1 = null;
        model2 = null;
        model = null;
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
    
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (model1 == null) {
            model1 = new ModelFortressArmor(1.0f);
        }
        if (model2 == null) {
            model2 = new ModelFortressArmor(0.5f);
        }
        return model = CustomArmorHelper.getCustomArmorModel(entityLiving, itemStack, armorSlot, model, model1, model2);
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "thaumcraft:textures/entity/armor/fortress_armor.png";
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("goggles")) {
            tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("item.goggles.name"));
        }
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("mask")) {
            tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("item.fortress_helm.mask." + stack.getTagCompound().getInteger("mask")));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 0)) || super.getIsRepairable(stack1, stack2);
    }
    
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        int priority = 0;
        double ratio = damageReduceAmount / 25.0;
        if (source.isMagicDamage()) {
            priority = 1;
            ratio = damageReduceAmount / 35.0;
        }
        else if (source.isFireDamage() || source.isExplosion()) {
            priority = 1;
            ratio = damageReduceAmount / 20.0;
        }
        else if (source.isUnblockable()) {
            priority = 0;
            ratio = 0.0;
        }
        ISpecialArmor.ArmorProperties ap = new ISpecialArmor.ArmorProperties(priority, ratio, armor.getMaxDamage() + 1 - armor.getItemDamage());
        if (player instanceof EntityPlayer) {
            int q = 0;
            for (int a = 1; a < 4; ++a) {
                ItemStack piece = ((EntityPlayer)player).inventory.armorInventory.get(a);
                if (piece != null && !piece.isEmpty() && piece.getItem() instanceof ItemFortressArmor) {
                    if (piece.hasTagCompound() && piece.getTagCompound().hasKey("mask")) {
                        ISpecialArmor.ArmorProperties armorProperties = ap;
                        ++armorProperties.Armor;
                    }
                    if (++q <= 1) {
                        ISpecialArmor.ArmorProperties armorProperties2 = ap;
                        ++armorProperties2.Armor;
                        ISpecialArmor.ArmorProperties armorProperties3 = ap;
                        ++armorProperties3.Toughness;
                    }
                }
            }
        }
        return ap;
    }
    
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        int q = 0;
        int ar = 0;
        for (int a = 1; a < 4; ++a) {
            ItemStack piece = player.inventory.armorInventory.get(a);
            if (piece != null && !piece.isEmpty() && piece.getItem() instanceof ItemFortressArmor) {
                if (piece.hasTagCompound() && piece.getTagCompound().hasKey("mask")) {
                    ++ar;
                }
                if (++q <= 1) {
                    ++ar;
                }
            }
        }
        return ar;
    }
    
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        if (source != DamageSource.FALL) {
            stack.damageItem(damage, entity);
        }
    }
    
    public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
        return itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("goggles");
    }
    
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        return itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("goggles");
    }
}
