// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.Item;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.api.items.IWarpingGear;
import net.minecraft.item.ItemSword;

public class ItemVoidSword extends ItemSword implements IWarpingGear, IThaumcraftItems
{
    public ItemVoidSword(final Item.ToolMaterial enumtoolmaterial) {
        super(enumtoolmaterial);
        setCreativeTab(ConfigItems.TABTC);
        setRegistryName("void_sword");
        setUnlocalizedName("void_sword");
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
    
    public void onUpdate(final ItemStack stack, final World world, final Entity entity, final int p_77663_4_, final boolean p_77663_5_) {
        super.onUpdate(stack, world, entity, p_77663_4_, p_77663_5_);
        if (stack.isItemDamaged() && entity != null && entity.ticksExisted % 20 == 0 && entity instanceof EntityLivingBase) {
            stack.damageItem(-1, (EntityLivingBase)entity);
        }
    }
    
    public boolean hitEntity(final ItemStack is, final EntityLivingBase target, final EntityLivingBase hitter) {
        if (!target.world.isRemote) {
            if (!(target instanceof EntityPlayer) || !(hitter instanceof EntityPlayer) || FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()) {
                try {
                    target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60));
                }
                catch (final Exception ex) {}
            }
        }
        return super.hitEntity(is, target, hitter);
    }
    
    public int getWarp(final ItemStack itemstack, final EntityPlayer player) {
        return 1;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("enchantment.special.sapless"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
