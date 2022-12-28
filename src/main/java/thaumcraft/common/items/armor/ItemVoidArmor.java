package thaumcraft.common.items.armor;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemVoidArmor extends ItemArmor implements IWarpingGear, IThaumcraftItems
{
    public ItemVoidArmor(String name, ItemArmor.ArmorMaterial enumarmormaterial, int j, EntityEquipmentSlot k) {
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
    
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (stack.getItem() == ItemsTC.voidHelm || stack.getItem() == ItemsTC.voidChest || stack.getItem() == ItemsTC.voidBoots) {
            return "thaumcraft:textures/entity/armor/void_1.png";
        }
        if (stack.getItem() == ItemsTC.voidLegs) {
            return "thaumcraft:textures/entity/armor/void_2.png";
        }
        return "thaumcraft:textures/entity/armor/void_1.png";
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
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
    
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 1;
    }
}
