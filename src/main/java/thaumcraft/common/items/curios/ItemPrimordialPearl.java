// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.curios;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.items.ItemTCBase;

public class ItemPrimordialPearl extends ItemTCBase
{
    public ItemPrimordialPearl() {
        super("primordial_pearl");
        maxStackSize = 1;
        setMaxDamage(8);
        addPropertyOverride(new ResourceLocation("type"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(final ItemStack stack, @Nullable final World worldIn, @Nullable final EntityLivingBase entityIn) {
                if (stack.getItemDamage() < 3) {
                    return 0.0f;
                }
                if (stack.getItemDamage() < 6) {
                    return 1.0f;
                }
                return 2.0f;
            }
        });
        setNoRepair();
    }
    
    public boolean getIsRepairable(final ItemStack toRepair, final ItemStack repair) {
        return false;
    }
    
    public boolean isRepairable() {
        return false;
    }
    
    @Override
    public String getUnlocalizedName(final ItemStack stack) {
        if (stack.getItemDamage() < 3) {
            return super.getUnlocalizedName() + ".pearl";
        }
        if (stack.getItemDamage() < 6) {
            return super.getUnlocalizedName() + ".nodule";
        }
        return super.getUnlocalizedName() + ".mote";
    }
    
    public ItemStack getContainerItem(final ItemStack itemStack) {
        if (!hasContainerItem(itemStack)) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(itemStack.getItem(), itemStack.getCount(), itemStack.getItemDamage() + 1);
    }
    
    public boolean hasContainerItem(final ItemStack stack) {
        return stack.getItemDamage() < 7;
    }
    
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean isEnchantable(final ItemStack stack) {
        return false;
    }
    
    public boolean canApplyAtEnchantingTable(final ItemStack stack, final Enchantment enchantment) {
        return false;
    }
}
