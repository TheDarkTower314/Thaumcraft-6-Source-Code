// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.baubles;

import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.items.RechargeHelper;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import baubles.api.BaubleType;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.items.IRechargable;
import baubles.api.IBauble;
import thaumcraft.common.items.ItemTCBase;

public class ItemVerdantCharm extends ItemTCBase implements IBauble, IRechargable
{
    public ItemVerdantCharm() {
        super("verdant_charm", new String[0]);
        this.maxStackSize = 1;
        this.canRepair = false;
        this.setMaxDamage(0);
        this.addPropertyOverride(new ResourceLocation("type"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(final ItemStack stack, @Nullable final World worldIn, @Nullable final EntityLivingBase entityIn) {
                if (stack.getItem() instanceof ItemVerdantCharm && stack.hasTagCompound()) {
                    return stack.getTagCompound().getByte("type");
                }
                return 0.0f;
            }
        });
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public BaubleType getBaubleType(final ItemStack itemstack) {
        return BaubleType.CHARM;
    }
    
    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            items.add(new ItemStack(this));
            final ItemStack vhbl = new ItemStack(this);
            vhbl.setTagInfo("type", new NBTTagByte((byte)1));
            items.add(vhbl);
            final ItemStack vhbl2 = new ItemStack(this);
            vhbl2.setTagInfo("type", new NBTTagByte((byte)2));
            items.add(vhbl2);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().getByte("type") == 1) {
            tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("item.verdant_charm.life.text"));
        }
        if (stack.hasTagCompound() && stack.getTagCompound().getByte("type") == 2) {
            tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("item.verdant_charm.sustain.text"));
        }
    }
    
    public void onWornTick(final ItemStack itemstack, final EntityLivingBase player) {
        if (!player.world.isRemote && player.ticksExisted % 20 == 0 && player instanceof EntityPlayer) {
            if (player.getActivePotionEffect(MobEffects.WITHER) != null && RechargeHelper.consumeCharge(itemstack, player, 20)) {
                player.removePotionEffect(MobEffects.WITHER);
                return;
            }
            if (player.getActivePotionEffect(MobEffects.POISON) != null && RechargeHelper.consumeCharge(itemstack, player, 10)) {
                player.removePotionEffect(MobEffects.POISON);
                return;
            }
            if (player.getActivePotionEffect(PotionFluxTaint.instance) != null && RechargeHelper.consumeCharge(itemstack, player, 5)) {
                player.removePotionEffect(PotionFluxTaint.instance);
                return;
            }
            if (itemstack.hasTagCompound() && itemstack.getTagCompound().getByte("type") == 1 && player.getHealth() < player.getMaxHealth() && RechargeHelper.consumeCharge(itemstack, player, 5)) {
                player.heal(1.0f);
                return;
            }
            if (itemstack.hasTagCompound() && itemstack.getTagCompound().getByte("type") == 2) {
                if (player.getAir() < 100 && RechargeHelper.consumeCharge(itemstack, player, 1)) {
                    player.setAir(300);
                    return;
                }
                if (player instanceof EntityPlayer && ((EntityPlayer)player).canEat(false) && RechargeHelper.consumeCharge(itemstack, player, 1)) {
                    ((EntityPlayer)player).getFoodStats().addStats(1, 0.3f);
                }
            }
        }
    }
    
    public int getMaxCharge(final ItemStack stack, final EntityLivingBase player) {
        return 200;
    }
    
    public EnumChargeDisplay showInHud(final ItemStack stack, final EntityLivingBase player) {
        return EnumChargeDisplay.NORMAL;
    }
    
    public boolean willAutoSync(final ItemStack itemstack, final EntityLivingBase player) {
        return true;
    }
}
