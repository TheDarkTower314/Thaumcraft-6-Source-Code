// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import thaumcraft.common.entities.projectile.EntityGrapple;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.items.ItemTCBase;

public class ItemGrappleGun extends ItemTCBase implements IRechargable
{
    public ItemGrappleGun() {
        super("grapple_gun");
        this.setMaxStackSize(1);
        this.addPropertyOverride(new ResourceLocation("type"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(final ItemStack stack, @Nullable final World worldIn, @Nullable final EntityLivingBase entityIn) {
                return (stack.hasTagCompound() && stack.getTagCompound().getByte("loaded") == 1) ? 1.0f : 0.0f;
            }
        });
    }
    
    @Override
    public int getMaxCharge(final ItemStack stack, final EntityLivingBase player) {
        return 100;
    }
    
    @Override
    public EnumChargeDisplay showInHud(final ItemStack stack, final EntityLivingBase player) {
        return EnumChargeDisplay.NORMAL;
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public void onUpdate(final ItemStack stack, final World worldIn, final Entity entityIn, final int itemSlot, final boolean isSelected) {
        if (!EntityGrapple.grapples.containsKey(entityIn.getEntityId()) && stack.hasTagCompound() && stack.getTagCompound().getByte("loaded") == 1) {
            stack.setTagInfo("loaded", new NBTTagByte((byte)0));
        }
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        player.playSound(SoundsTC.ice, 3.0f, 0.8f + world.rand.nextFloat() * 0.1f);
        if (!world.isRemote && RechargeHelper.getCharge(player.getHeldItem(hand)) > 0) {
            final EntityGrapple grapple = new EntityGrapple(world, player, hand);
            grapple.shoot(player, player.rotationPitch, player.rotationYaw, -5.0f, 1.5f, 0.0f);
            final double px = -MathHelper.cos((player.rotationYaw - 0.5f) / 180.0f * 3.141593f) * 0.2f * ((grapple.hand == EnumHand.MAIN_HAND) ? 1 : -1);
            final double pz = -MathHelper.sin((player.rotationYaw - 0.5f) / 180.0f * 3.141593f) * 0.3f * ((grapple.hand == EnumHand.MAIN_HAND) ? 1 : -1);
            final Vec3d vl = player.getLookVec();
            final EntityGrapple entityGrapple = grapple;
            entityGrapple.posX += px + vl.x;
            final EntityGrapple entityGrapple2 = grapple;
            entityGrapple2.posZ += pz + vl.y;
            if (world.spawnEntity(grapple)) {
                RechargeHelper.consumeCharge(player.getHeldItem(hand), player, 1);
                player.getHeldItem(hand).setTagInfo("loaded", new NBTTagByte((byte)1));
            }
        }
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
    
    public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
        if (oldStack.getItem() != null && oldStack.getItem() == this && newStack.getItem() != null && newStack.getItem() == this && !ItemStack.areItemStackTagsEqual(oldStack, newStack)) {
            final boolean b1 = !oldStack.hasTagCompound() || oldStack.getTagCompound().getByte("loaded") == 0;
            final boolean b2 = !newStack.hasTagCompound() || newStack.getTagCompound().getByte("loaded") == 0;
            return b1 != b2;
        }
        return newStack.getItem() != oldStack.getItem();
    }
}
