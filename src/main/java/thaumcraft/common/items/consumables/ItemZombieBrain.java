// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.consumables;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.ThaumcraftApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import thaumcraft.common.items.IThaumcraftItems;
import net.minecraft.item.ItemFood;

public class ItemZombieBrain extends ItemFood implements IThaumcraftItems
{
    public ItemZombieBrain() {
        super(4, 0.2f, true);
        setPotionEffect(new PotionEffect(MobEffects.HUNGER, 30, 0), 0.8f);
        setCreativeTab(ConfigItems.TABTC);
        setRegistryName("brain");
        setUnlocalizedName("brain");
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (world.rand.nextFloat() < 0.1f) {
                ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
            }
            else {
                ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1 + world.rand.nextInt(3), IPlayerWarp.EnumWarpType.TEMPORARY);
            }
        }
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
    
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
}
