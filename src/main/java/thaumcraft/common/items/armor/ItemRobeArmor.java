package thaumcraft.common.items.armor;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemRobeArmor extends ItemArmor implements IVisDiscountGear, IThaumcraftItems
{
    public ItemRobeArmor(String name, ItemArmor.ArmorMaterial enumarmormaterial, int j, EntityEquipmentSlot k) {
        super(enumarmormaterial, j, k);
        setRegistryName(name);
        setUnlocalizedName(name);
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
    
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
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
    
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (stack.getItem() == ItemsTC.clothChest || stack.getItem() == ItemsTC.clothBoots) {
            return (type == null) ? "thaumcraft:textures/entity/armor/robes_1.png" : "thaumcraft:textures/entity/armor/robes_1_overlay.png";
        }
        if (stack.getItem() == ItemsTC.clothLegs) {
            return (type == null) ? "thaumcraft:textures/entity/armor/robes_2.png" : "thaumcraft:textures/entity/armor/robes_2_overlay.png";
        }
        return (type == null) ? "thaumcraft:textures/entity/armor/robes_1.png" : "thaumcraft:textures/entity/armor/robes_1_overlay.png";
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.fabric)) || super.getIsRepairable(stack1, stack2);
    }
    
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        return (armorType == EntityEquipmentSlot.FEET) ? 2 : 3;
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
}
