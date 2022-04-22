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
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.inventory.EntityEquipmentSlot;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.api.items.IVisDiscountGear;
import net.minecraft.item.ItemArmor;

public class ItemRobeArmor extends ItemArmor implements IVisDiscountGear, IThaumcraftItems
{
    public ItemRobeArmor(final String name, final ItemArmor.ArmorMaterial enumarmormaterial, final int j, final EntityEquipmentSlot k) {
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
    
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
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
    
    public String getArmorTexture(final ItemStack stack, final Entity entity, final EntityEquipmentSlot slot, final String type) {
        if (stack.getItem() == ItemsTC.clothChest || stack.getItem() == ItemsTC.clothBoots) {
            return (type == null) ? "thaumcraft:textures/entity/armor/robes_1.png" : "thaumcraft:textures/entity/armor/robes_1_overlay.png";
        }
        if (stack.getItem() == ItemsTC.clothLegs) {
            return (type == null) ? "thaumcraft:textures/entity/armor/robes_2.png" : "thaumcraft:textures/entity/armor/robes_2_overlay.png";
        }
        return (type == null) ? "thaumcraft:textures/entity/armor/robes_1.png" : "thaumcraft:textures/entity/armor/robes_1_overlay.png";
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean getIsRepairable(final ItemStack stack1, final ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.fabric)) || super.getIsRepairable(stack1, stack2);
    }
    
    public int getVisDiscount(final ItemStack stack, final EntityPlayer player) {
        return (armorType == EntityEquipmentSlot.FEET) ? 2 : 3;
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
}
