package thaumcraft.common.items.casters;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.ItemTCBase;


public class ItemFocusPouch extends ItemTCBase implements IBauble
{
    public ItemFocusPouch() {
        super("focus_pouch");
        setMaxStackSize(1);
        setHasSubtypes(false);
        setMaxDamage(0);
    }
    
    public boolean getShareTag() {
        return true;
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean hasEffect(ItemStack stack1) {
        return false;
    }
    
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (!worldIn.isRemote) {
            playerIn.openGui(Thaumcraft.instance, 5, worldIn, MathHelper.floor(playerIn.posX), MathHelper.floor(playerIn.posY), MathHelper.floor(playerIn.posZ));
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }
    
    public NonNullList<ItemStack> getInventory(ItemStack item) {
        NonNullList<ItemStack> stackList = NonNullList.withSize(18, ItemStack.EMPTY);
        if (item.hasTagCompound()) {
            ItemStackHelper.loadAllItems(item.getTagCompound(), stackList);
        }
        return stackList;
    }
    
    public void setInventory(ItemStack item, NonNullList<ItemStack> stackList) {
        if (item.getTagCompound() == null) {
            item.setTagCompound(new NBTTagCompound());
        }
        ItemStackHelper.saveAllItems(item.getTagCompound(), stackList);
    }
    
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BELT;
    }
    
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
    }
    
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
    }
    
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
    }
    
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
    
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
}
