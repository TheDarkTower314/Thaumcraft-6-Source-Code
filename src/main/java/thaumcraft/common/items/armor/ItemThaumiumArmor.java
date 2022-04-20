// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.armor;

import net.minecraft.item.EnumRarity;
import thaumcraft.api.items.ItemsTC;
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
import net.minecraft.item.ItemArmor;

public class ItemThaumiumArmor extends ItemArmor implements IThaumcraftItems
{
    public ItemThaumiumArmor(final String name, final ItemArmor.ArmorMaterial enumarmormaterial, final int j, final EntityEquipmentSlot k) {
        super(enumarmormaterial, j, k);
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
    
    public String getArmorTexture(final ItemStack stack, final Entity entity, final EntityEquipmentSlot slot, final String type) {
        if (stack.getItem() == ItemsTC.thaumiumHelm || stack.getItem() == ItemsTC.thaumiumChest || stack.getItem() == ItemsTC.thaumiumBoots) {
            return "thaumcraft:textures/entity/armor/thaumium_1.png";
        }
        if (stack.getItem() == ItemsTC.thaumiumLegs) {
            return "thaumcraft:textures/entity/armor/thaumium_2.png";
        }
        return "thaumcraft:textures/entity/armor/thaumium_1.png";
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean getIsRepairable(final ItemStack stack1, final ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 0)) || super.getIsRepairable(stack1, stack2);
    }
}
