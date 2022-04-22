// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.EnumRarity;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.Item;
import thaumcraft.common.items.IThaumcraftItems;
import net.minecraft.item.ItemPickaxe;

public class ItemElementalPickaxe extends ItemPickaxe implements IThaumcraftItems
{
    public ItemElementalPickaxe(final Item.ToolMaterial enumtoolmaterial) {
        super(enumtoolmaterial);
        setCreativeTab(ConfigItems.TABTC);
        setRegistryName("elemental_pick");
        setUnlocalizedName("elemental_pick");
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
    
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public Set<String> getToolClasses(final ItemStack stack) {
        return ImmutableSet.of("pickaxe");
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public boolean getIsRepairable(final ItemStack stack1, final ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 0)) || super.getIsRepairable(stack1, stack2);
    }
    
    public boolean onLeftClickEntity(final ItemStack stack, final EntityPlayer player, final Entity entity) {
        if (!player.world.isRemote) {
            if (!(entity instanceof EntityPlayer) || FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()) {
                entity.setFire(2);
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }
    
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            final ItemStack w1 = new ItemStack(this);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.REFINING, 1);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.SOUNDING, 2);
            items.add(w1);
        }
        else {
            super.getSubItems(tab, items);
        }
    }
}
