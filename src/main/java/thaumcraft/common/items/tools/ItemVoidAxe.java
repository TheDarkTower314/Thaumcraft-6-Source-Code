// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.Item;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.api.items.IWarpingGear;
import net.minecraft.item.ItemAxe;

public class ItemVoidAxe extends ItemAxe implements IWarpingGear, IThaumcraftItems
{
    public ItemVoidAxe(final Item.ToolMaterial enumtoolmaterial) {
        super(enumtoolmaterial, 8.0f, -3.0f);
        setCreativeTab(ConfigItems.TABTC);
        setRegistryName("void_axe");
        setUnlocalizedName("void_axe");
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
    
    public void onUpdate(final ItemStack stack, final World world, final Entity entity, final int p_77663_4_, final boolean p_77663_5_) {
        super.onUpdate(stack, world, entity, p_77663_4_, p_77663_5_);
        if (stack.isItemDamaged() && entity != null && entity.ticksExisted % 20 == 0 && entity instanceof EntityLivingBase) {
            stack.damageItem(-1, (EntityLivingBase)entity);
        }
    }
    
    public boolean onLeftClickEntity(final ItemStack stack, final EntityPlayer player, final Entity entity) {
        if (!player.world.isRemote && entity instanceof EntityLivingBase) {
            if (!(entity instanceof EntityPlayer) || FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()) {
                ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 80));
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }
    
    public int getWarp(final ItemStack itemstack, final EntityPlayer player) {
        return 1;
    }
}
