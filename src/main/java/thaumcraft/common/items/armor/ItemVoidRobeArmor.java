package thaumcraft.common.items.armor;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.renderers.models.gear.ModelRobe;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemVoidRobeArmor extends ItemArmor implements IVisDiscountGear, IGoggles, IRevealer, ISpecialArmor, IWarpingGear, IThaumcraftItems
{
    ModelBiped model1;
    ModelBiped model2;
    ModelBiped model;
    
    public ItemVoidRobeArmor(String name, ItemArmor.ArmorMaterial enumarmormaterial, int j, EntityEquipmentSlot k) {
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
    
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return (type == null) ? "thaumcraft:textures/entity/armor/void_robe_armor_overlay.png" : "thaumcraft:textures/entity/armor/void_robe_armor.png";
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.EPIC;
    }
    
    public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 1)) || super.getIsRepairable(stack1, stack2);
    }
    
    public void onUpdate(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        super.onUpdate(stack, world, entity, p_77663_4_, p_77663_5_);
        if (!world.isRemote && stack.isItemDamaged() && entity.ticksExisted % 20 == 0 && entity instanceof EntityLivingBase) {
            stack.damageItem(-1, (EntityLivingBase)entity);
        }
    }
    
    public void onArmorTick(World world, EntityPlayer player, ItemStack armor) {
        super.onArmorTick(world, player, armor);
        if (!world.isRemote && armor.getItemDamage() > 0 && player.ticksExisted % 20 == 0) {
            armor.damageItem(-1, player);
        }
    }
    
    public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
        EntityEquipmentSlot type = ((ItemArmor)itemstack.getItem()).armorType;
        return type == EntityEquipmentSlot.HEAD;
    }
    
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        EntityEquipmentSlot type = ((ItemArmor)itemstack.getItem()).armorType;
        return type == EntityEquipmentSlot.HEAD;
    }
    
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        return 5;
    }
    
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (model1 == null) {
            model1 = new ModelRobe(1.0f);
        }
        if (model2 == null) {
            model2 = new ModelRobe(0.5f);
        }
        return model = CustomArmorHelper.getCustomArmorModel(entityLiving, itemStack, armorSlot, model, model1, model2);
    }
    
    public boolean hasColor(ItemStack stack1) {
        return true;
    }
    
    public int getColor(ItemStack stack1) {
        NBTTagCompound nbttagcompound = stack1.getTagCompound();
        if (nbttagcompound == null) {
            return 6961280;
        }
        NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("display");
        return (nbttagcompound2 == null) ? 6961280 : (nbttagcompound2.hasKey("color") ? nbttagcompound2.getInteger("color") : 6961280);
    }
    
    public void removeColor(ItemStack stack1) {
        NBTTagCompound nbttagcompound = stack1.getTagCompound();
        if (nbttagcompound != null) {
            NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("display");
            if (nbttagcompound2.hasKey("color")) {
                nbttagcompound2.removeTag("color");
            }
        }
    }
    
    public void setColor(ItemStack stack1, int par2) {
        NBTTagCompound nbttagcompound = stack1.getTagCompound();
        if (nbttagcompound == null) {
            nbttagcompound = new NBTTagCompound();
            stack1.setTagCompound(nbttagcompound);
        }
        NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("display");
        if (!nbttagcompound.hasKey("display")) {
            nbttagcompound.setTag("display", nbttagcompound2);
        }
        nbttagcompound2.setInteger("color", par2);
    }
    
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
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
    
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return damageReduceAmount;
    }
    
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        if (source != DamageSource.FALL) {
            stack.damageItem(damage, entity);
        }
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState bs = world.getBlockState(pos);
        if (bs.getBlock() == Blocks.CAULDRON) {
            IBlockState blockState = bs;
            BlockCauldron cauldron = Blocks.CAULDRON;
            int i = (int)blockState.getValue((IProperty)BlockCauldron.LEVEL);
            if (!world.isRemote && i > 0) {
                removeColor(player.getHeldItem(hand));
                Blocks.CAULDRON.setWaterLevel(world, pos, bs, i - 1);
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
    
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 3;
    }
}
