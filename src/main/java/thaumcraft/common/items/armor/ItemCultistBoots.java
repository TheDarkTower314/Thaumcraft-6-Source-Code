// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.init.Items;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.inventory.EntityEquipmentSlot;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import net.minecraft.item.ItemArmor;

public class ItemCultistBoots extends ItemArmor implements IWarpingGear, IVisDiscountGear, IThaumcraftItems
{
    public ItemCultistBoots() {
        super(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.FEET);
        setCreativeTab(ConfigItems.TABTC);
        setRegistryName("crimson_boots");
        setUnlocalizedName("crimson_boots");
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
    
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(final ItemStack stack, final Entity entity, final EntityEquipmentSlot slot, final String type) {
        return "thaumcraft:textures/entity/armor/cultistboots.png";
    }
    
    public boolean getIsRepairable(final ItemStack stack1, final ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(Items.IRON_INGOT)) || super.getIsRepairable(stack1, stack2);
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public int getWarp(final ItemStack itemstack, final EntityPlayer player) {
        return 1;
    }
    
    public int getVisDiscount(final ItemStack stack, final EntityPlayer player) {
        return 1;
    }
}
