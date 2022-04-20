// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import java.util.Iterator;
import java.util.List;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.item.EnumAction;
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
import net.minecraft.item.ItemAxe;

public class ItemElementalAxe extends ItemAxe implements IThaumcraftItems
{
    public ItemElementalAxe(final Item.ToolMaterial enumtoolmaterial) {
        super(enumtoolmaterial, 8.0f, -3.0f);
        this.setCreativeTab(ConfigItems.TABTC);
        this.setRegistryName("elemental_axe");
        this.setUnlocalizedName("elemental_axe");
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
        return ImmutableSet.of("axe");
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public boolean getIsRepairable(final ItemStack stack1, final ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 0)) || super.getIsRepairable(stack1, stack2);
    }
    
    public EnumAction getItemUseAction(final ItemStack itemstack) {
        return EnumAction.BOW;
    }
    
    public int getMaxItemUseDuration(final ItemStack p_77626_1_) {
        return 72000;
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World worldIn, final EntityPlayer playerIn, final EnumHand hand) {
        playerIn.setActiveHand(hand);
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
    }
    
    public void onUsingTick(final ItemStack stack, final EntityLivingBase player, final int count) {
        final List<EntityItem> stuff = EntityUtils.getEntitiesInRange(player.world, player.posX, player.posY, player.posZ, player, EntityItem.class, 10.0);
        if (stuff != null && stuff.size() > 0) {
            for (final EntityItem e : stuff) {
                if (!e.isDead) {
                    double d6 = e.posX - player.posX;
                    double d7 = e.posY - player.posY + player.height / 2.0f;
                    double d8 = e.posZ - player.posZ;
                    final double d9 = MathHelper.sqrt(d6 * d6 + d7 * d7 + d8 * d8);
                    d6 /= d9;
                    d7 /= d9;
                    d8 /= d9;
                    final double d10 = 0.3;
                    final EntityItem entityItem = e;
                    entityItem.motionX -= d6 * d10;
                    final EntityItem entityItem2 = e;
                    entityItem2.motionY -= d7 * d10 - 0.1;
                    final EntityItem entityItem3 = e;
                    entityItem3.motionZ -= d8 * d10;
                    if (e.motionX > 0.25) {
                        e.motionX = 0.25;
                    }
                    if (e.motionX < -0.25) {
                        e.motionX = -0.25;
                    }
                    if (e.motionY > 0.25) {
                        e.motionY = 0.25;
                    }
                    if (e.motionY < -0.25) {
                        e.motionY = -0.25;
                    }
                    if (e.motionZ > 0.25) {
                        e.motionZ = 0.25;
                    }
                    if (e.motionZ < -0.25) {
                        e.motionZ = -0.25;
                    }
                    if (!player.world.isRemote) {
                        continue;
                    }
                    FXDispatcher.INSTANCE.crucibleBubble((float)e.posX + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2f, (float)e.posY + e.height + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2f, (float)e.posZ + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2f, 0.33f, 0.33f, 1.0f);
                }
            }
        }
    }
    
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            final ItemStack w1 = new ItemStack(this);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.BURROWING, 1);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.COLLECTOR, 1);
            items.add(w1);
        }
        else {
            super.getSubItems(tab, items);
        }
    }
}
