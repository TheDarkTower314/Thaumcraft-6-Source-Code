// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.item.EnumRarity;
import net.minecraft.entity.Entity;
import thaumcraft.client.renderers.models.gear.ModelFortressArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.model.ModelBiped;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IGoggles;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraft.item.ItemArmor;

public class ItemFortressArmor extends ItemArmor implements ISpecialArmor, IGoggles, IRevealer, IThaumcraftItems
{
    ModelBiped model1;
    ModelBiped model2;
    ModelBiped model;
    
    public ItemFortressArmor(final String name, final ItemArmor.ArmorMaterial material, final int renderIndex, final EntityEquipmentSlot armorType) {
        super(material, renderIndex, armorType);
        this.model1 = null;
        this.model2 = null;
        this.model = null;
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
        this.setCreativeTab(ConfigItems.TABTC);
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
    
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(final EntityLivingBase entityLiving, final ItemStack itemStack, final EntityEquipmentSlot armorSlot, final ModelBiped _default) {
        if (this.model1 == null) {
            this.model1 = new ModelFortressArmor(1.0f);
        }
        if (this.model2 == null) {
            this.model2 = new ModelFortressArmor(0.5f);
        }
        return this.model = CustomArmorHelper.getCustomArmorModel(entityLiving, itemStack, armorSlot, this.model, this.model1, this.model2);
    }
    
    public String getArmorTexture(final ItemStack stack, final Entity entity, final EntityEquipmentSlot slot, final String type) {
        return "thaumcraft:textures/entity/armor/fortress_armor.png";
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("goggles")) {
            tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("item.goggles.name"));
        }
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("mask")) {
            tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("item.fortress_helm.mask." + stack.getTagCompound().getInteger("mask")));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    public boolean getIsRepairable(final ItemStack stack1, final ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 0)) || super.getIsRepairable(stack1, stack2);
    }
    
    public ISpecialArmor.ArmorProperties getProperties(final EntityLivingBase player, final ItemStack armor, final DamageSource source, final double damage, final int slot) {
        int priority = 0;
        double ratio = this.damageReduceAmount / 25.0;
        if (source.isMagicDamage()) {
            priority = 1;
            ratio = this.damageReduceAmount / 35.0;
        }
        else if (source.isFireDamage() || source.isExplosion()) {
            priority = 1;
            ratio = this.damageReduceAmount / 20.0;
        }
        else if (source.isUnblockable()) {
            priority = 0;
            ratio = 0.0;
        }
        final ISpecialArmor.ArmorProperties ap = new ISpecialArmor.ArmorProperties(priority, ratio, armor.getMaxDamage() + 1 - armor.getItemDamage());
        if (player instanceof EntityPlayer) {
            int q = 0;
            for (int a = 1; a < 4; ++a) {
                final ItemStack piece = ((EntityPlayer)player).inventory.armorInventory.get(a);
                if (piece != null && !piece.isEmpty() && piece.getItem() instanceof ItemFortressArmor) {
                    if (piece.hasTagCompound() && piece.getTagCompound().hasKey("mask")) {
                        final ISpecialArmor.ArmorProperties armorProperties = ap;
                        ++armorProperties.Armor;
                    }
                    if (++q <= 1) {
                        final ISpecialArmor.ArmorProperties armorProperties2 = ap;
                        ++armorProperties2.Armor;
                        final ISpecialArmor.ArmorProperties armorProperties3 = ap;
                        ++armorProperties3.Toughness;
                    }
                }
            }
        }
        return ap;
    }
    
    public int getArmorDisplay(final EntityPlayer player, final ItemStack armor, final int slot) {
        int q = 0;
        int ar = 0;
        for (int a = 1; a < 4; ++a) {
            final ItemStack piece = player.inventory.armorInventory.get(a);
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
    
    public void damageArmor(final EntityLivingBase entity, final ItemStack stack, final DamageSource source, final int damage, final int slot) {
        if (source != DamageSource.FALL) {
            stack.damageItem(damage, entity);
        }
    }
    
    public boolean showNodes(final ItemStack itemstack, final EntityLivingBase player) {
        return itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("goggles");
    }
    
    public boolean showIngamePopups(final ItemStack itemstack, final EntityLivingBase player) {
        return itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("goggles");
    }
}
