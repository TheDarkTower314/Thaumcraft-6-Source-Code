// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.armor;

import thaumcraft.common.lib.events.PlayerEvents;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.api.items.RechargeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.items.IThaumcraftItems;
import net.minecraft.item.ItemArmor;

public class ItemBootsTraveller extends ItemArmor implements IThaumcraftItems, IRechargable
{
    public ItemBootsTraveller() {
        super(ThaumcraftMaterials.ARMORMAT_SPECIAL, 4, EntityEquipmentSlot.FEET);
        setMaxDamage(350);
        setRegistryName("traveller_boots");
        setUnlocalizedName("traveller_boots");
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
        setCreativeTab(ConfigItems.TABTC);
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
        return "thaumcraft:textures/entity/armor/bootstraveler.png";
    }
    
    public boolean getIsRepairable(final ItemStack stack1, final ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(Items.LEATHER)) || super.getIsRepairable(stack1, stack2);
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public void onArmorTick(final World world, final EntityPlayer player, final ItemStack itemStack) {
        final boolean hasCharge = RechargeHelper.getCharge(itemStack) > 0;
        if (!world.isRemote && player.ticksExisted % 20 == 0) {
            int e = 0;
            if (itemStack.hasTagCompound()) {
                e = itemStack.getTagCompound().getInteger("energy");
            }
            if (e > 0) {
                --e;
            }
            else if (e <= 0 && RechargeHelper.consumeCharge(itemStack, player, 1)) {
                e = 60;
            }
            itemStack.setTagInfo("energy", new NBTTagInt(e));
        }
        if (hasCharge && !player.capabilities.isFlying && player.moveForward > 0.0f) {
            if (player.world.isRemote && !player.isSneaking()) {
                if (!PlayerEvents.prevStep.containsKey(player.getEntityId())) {
                    PlayerEvents.prevStep.put(player.getEntityId(), player.stepHeight);
                }
                player.stepHeight = 1.0f;
            }
            if (player.onGround) {
                float bonus = 0.05f;
                if (player.isInWater()) {
                    bonus /= 4.0f;
                }
                player.moveRelative(0.0f, 0.0f, bonus, 1.0f);
            }
            else {
                if (player.isInWater()) {
                    player.moveRelative(0.0f, 0.0f, 0.025f, 1.0f);
                }
                player.jumpMovementFactor = 0.05f;
            }
        }
    }
    
    public int getMaxCharge(final ItemStack stack, final EntityLivingBase player) {
        return 240;
    }
    
    public EnumChargeDisplay showInHud(final ItemStack stack, final EntityLivingBase player) {
        return EnumChargeDisplay.PERIODIC;
    }
}
