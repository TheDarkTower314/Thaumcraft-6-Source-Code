// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
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
import thaumcraft.api.items.IWarpingGear;
import net.minecraft.item.ItemArmor;

public class ItemVoidArmor extends ItemArmor implements IWarpingGear, IThaumcraftItems
{
    public ItemVoidArmor(final String name, final ItemArmor.ArmorMaterial enumarmormaterial, final int j, final EntityEquipmentSlot k) {
        super(enumarmormaterial, j, k);
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
    
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public String getArmorTexture(final ItemStack stack, final Entity entity, final EntityEquipmentSlot slot, final String type) {
        if (stack.getItem() == ItemsTC.voidHelm || stack.getItem() == ItemsTC.voidChest || stack.getItem() == ItemsTC.voidBoots) {
            return "thaumcraft:textures/entity/armor/void_1.png";
        }
        if (stack.getItem() == ItemsTC.voidLegs) {
            return "thaumcraft:textures/entity/armor/void_2.png";
        }
        return "thaumcraft:textures/entity/armor/void_1.png";
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean getIsRepairable(final ItemStack stack1, final ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 1)) || super.getIsRepairable(stack1, stack2);
    }
    
    public void onUpdate(final ItemStack stack, final World world, final Entity entity, final int p_77663_4_, final boolean p_77663_5_) {
        super.onUpdate(stack, world, entity, p_77663_4_, p_77663_5_);
        if (!world.isRemote && stack.isItemDamaged() && entity.ticksExisted % 20 == 0 && entity instanceof EntityLivingBase) {
            stack.damageItem(-1, (EntityLivingBase)entity);
        }
    }
    
    public void onArmorTick(final World world, final EntityPlayer player, final ItemStack armor) {
        super.onArmorTick(world, player, armor);
        if (!world.isRemote && armor.getItemDamage() > 0 && player.ticksExisted % 20 == 0) {
            armor.damageItem(-1, player);
        }
    }
    
    public int getWarp(final ItemStack itemstack, final EntityPlayer player) {
        return 1;
    }
}
