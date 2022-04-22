// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.armor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockCauldron;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.client.renderers.models.gear.ModelRobe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.EnumRarity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.model.ModelBiped;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.api.items.IWarpingGear;
import net.minecraftforge.common.ISpecialArmor;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IVisDiscountGear;
import net.minecraft.item.ItemArmor;

public class ItemVoidRobeArmor extends ItemArmor implements IVisDiscountGear, IGoggles, IRevealer, ISpecialArmor, IWarpingGear, IThaumcraftItems
{
    ModelBiped model1;
    ModelBiped model2;
    ModelBiped model;
    
    public ItemVoidRobeArmor(final String name, final ItemArmor.ArmorMaterial enumarmormaterial, final int j, final EntityEquipmentSlot k) {
        super(enumarmormaterial, j, k);
        model1 = null;
        model2 = null;
        model = null;
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
        return (type == null) ? "thaumcraft:textures/entity/armor/void_robe_armor_overlay.png" : "thaumcraft:textures/entity/armor/void_robe_armor.png";
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.EPIC;
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
    
    public boolean showNodes(final ItemStack itemstack, final EntityLivingBase player) {
        final EntityEquipmentSlot type = ((ItemArmor)itemstack.getItem()).armorType;
        return type == EntityEquipmentSlot.HEAD;
    }
    
    public boolean showIngamePopups(final ItemStack itemstack, final EntityLivingBase player) {
        final EntityEquipmentSlot type = ((ItemArmor)itemstack.getItem()).armorType;
        return type == EntityEquipmentSlot.HEAD;
    }
    
    public int getVisDiscount(final ItemStack stack, final EntityPlayer player) {
        return 5;
    }
    
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(final EntityLivingBase entityLiving, final ItemStack itemStack, final EntityEquipmentSlot armorSlot, final ModelBiped _default) {
        if (model1 == null) {
            model1 = new ModelRobe(1.0f);
        }
        if (model2 == null) {
            model2 = new ModelRobe(0.5f);
        }
        return model = CustomArmorHelper.getCustomArmorModel(entityLiving, itemStack, armorSlot, model, model1, model2);
    }
    
    public boolean hasColor(final ItemStack stack1) {
        return true;
    }
    
    public int getColor(final ItemStack stack1) {
        final NBTTagCompound nbttagcompound = stack1.getTagCompound();
        if (nbttagcompound == null) {
            return 6961280;
        }
        final NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("display");
        return (nbttagcompound2 == null) ? 6961280 : (nbttagcompound2.hasKey("color") ? nbttagcompound2.getInteger("color") : 6961280);
    }
    
    public void removeColor(final ItemStack stack1) {
        final NBTTagCompound nbttagcompound = stack1.getTagCompound();
        if (nbttagcompound != null) {
            final NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("display");
            if (nbttagcompound2.hasKey("color")) {
                nbttagcompound2.removeTag("color");
            }
        }
    }
    
    public void setColor(final ItemStack stack1, final int par2) {
        NBTTagCompound nbttagcompound = stack1.getTagCompound();
        if (nbttagcompound == null) {
            nbttagcompound = new NBTTagCompound();
            stack1.setTagCompound(nbttagcompound);
        }
        final NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("display");
        if (!nbttagcompound.hasKey("display")) {
            nbttagcompound.setTag("display", nbttagcompound2);
        }
        nbttagcompound2.setInteger("color", par2);
    }
    
    public ISpecialArmor.ArmorProperties getProperties(final EntityLivingBase player, final ItemStack armor, final DamageSource source, final double damage, final int slot) {
        int priority = 0;
        double ratio = damageReduceAmount / 25.0;
        if (source.isMagicDamage()) {
            priority = 1;
            ratio = damageReduceAmount / 35.0;
        }
        else if (source.isUnblockable()) {
            priority = 0;
            ratio = 0.0;
        }
        return new ISpecialArmor.ArmorProperties(priority, ratio, armor.getMaxDamage() + 1 - armor.getItemDamage());
    }
    
    public int getArmorDisplay(final EntityPlayer player, final ItemStack armor, final int slot) {
        return damageReduceAmount;
    }
    
    public void damageArmor(final EntityLivingBase entity, final ItemStack stack, final DamageSource source, final int damage, final int slot) {
        if (source != DamageSource.FALL) {
            stack.damageItem(damage, entity);
        }
    }
    
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
        final IBlockState bs = world.getBlockState(pos);
        if (bs.getBlock() == Blocks.CAULDRON) {
            final IBlockState blockState = bs;
            final BlockCauldron cauldron = Blocks.CAULDRON;
            final int i = (int)blockState.getValue((IProperty)BlockCauldron.LEVEL);
            if (!world.isRemote && i > 0) {
                removeColor(player.getHeldItem(hand));
                Blocks.CAULDRON.setWaterLevel(world, pos, bs, i - 1);
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
    
    public int getWarp(final ItemStack itemstack, final EntityPlayer player) {
        return 3;
    }
}
