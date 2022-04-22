// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters;

import net.minecraft.entity.EntityLivingBase;
import baubles.api.BaubleType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import thaumcraft.Thaumcraft;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import baubles.api.IBauble;
import thaumcraft.common.items.ItemTCBase;

public class ItemFocusPouch extends ItemTCBase implements IBauble
{
    public ItemFocusPouch() {
        super("focus_pouch");
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
    }
    
    public boolean getShareTag() {
        return true;
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean hasEffect(final ItemStack stack1) {
        return false;
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World worldIn, final EntityPlayer playerIn, final EnumHand hand) {
        if (!worldIn.isRemote) {
            playerIn.openGui(Thaumcraft.instance, 5, worldIn, MathHelper.floor(playerIn.posX), MathHelper.floor(playerIn.posY), MathHelper.floor(playerIn.posZ));
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }
    
    public NonNullList<ItemStack> getInventory(final ItemStack item) {
        final NonNullList<ItemStack> stackList = NonNullList.withSize(18, ItemStack.EMPTY);
        if (item.hasTagCompound()) {
            ItemStackHelper.loadAllItems(item.getTagCompound(), stackList);
        }
        return stackList;
    }
    
    public void setInventory(final ItemStack item, final NonNullList<ItemStack> stackList) {
        if (item.getTagCompound() == null) {
            item.setTagCompound(new NBTTagCompound());
        }
        ItemStackHelper.saveAllItems(item.getTagCompound(), stackList);
    }
    
    public BaubleType getBaubleType(final ItemStack itemstack) {
        return BaubleType.BELT;
    }
    
    public void onWornTick(final ItemStack itemstack, final EntityLivingBase player) {
    }
    
    public void onEquipped(final ItemStack itemstack, final EntityLivingBase player) {
    }
    
    public void onUnequipped(final ItemStack itemstack, final EntityLivingBase player) {
    }
    
    public boolean canEquip(final ItemStack itemstack, final EntityLivingBase player) {
        return true;
    }
    
    public boolean canUnequip(final ItemStack itemstack, final EntityLivingBase player) {
        return true;
    }
}
