// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.consumables;

import thaumcraft.common.lib.SoundsTC;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.lib.potions.PotionWarpWard;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemSanitySoap extends ItemTCBase
{
    public ItemSanitySoap() {
        super("sanity_soap", new String[0]);
        this.setHasSubtypes(false);
    }
    
    public int getMaxItemUseDuration(final ItemStack p_77626_1_) {
        return 100;
    }
    
    public EnumAction getItemUseAction(final ItemStack p_77661_1_) {
        return EnumAction.BLOCK;
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World p_77659_2_, final EntityPlayer player, final EnumHand hand) {
        player.setActiveHand(hand);
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
    
    public void onUsingTick(final ItemStack stack, final EntityLivingBase player, final int count) {
        final int ticks = this.getMaxItemUseDuration(stack) - count;
        if (ticks > 95) {
            player.stopActiveHand();
        }
        if (player.world.isRemote) {
            if (player.world.rand.nextFloat() < 0.2f) {
                player.world.playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.PLAYERS, 0.1f, 1.5f + player.world.rand.nextFloat() * 0.2f, false);
            }
            for (int a = 0; a < 10; ++a) {
                FXDispatcher.INSTANCE.crucibleBubble((float)player.posX - 0.5f + player.world.rand.nextFloat(), (float)player.getEntityBoundingBox().minY + player.world.rand.nextFloat() * player.height, (float)player.posZ - 0.5f + player.world.rand.nextFloat(), 1.0f, 0.8f, 0.9f);
            }
        }
    }
    
    public void onPlayerStoppedUsing(final ItemStack stack, final World world, final EntityLivingBase player, final int timeLeft) {
        final int qq = this.getMaxItemUseDuration(stack) - timeLeft;
        if (qq > 95 && player instanceof EntityPlayer) {
            stack.shrink(1);
            if (!world.isRemote) {
                final IPlayerWarp warp = ThaumcraftCapabilities.getWarp((EntityPlayer)player);
                int amt = 1;
                if (player.isPotionActive(PotionWarpWard.instance)) {
                    ++amt;
                }
                final int i = MathHelper.floor(player.posX);
                final int j = MathHelper.floor(player.posY);
                final int k = MathHelper.floor(player.posZ);
                if (world.getBlockState(new BlockPos(i, j, k)).getBlock() == BlocksTC.purifyingFluid) {
                    ++amt;
                }
                if (warp.get(IPlayerWarp.EnumWarpType.NORMAL) > 0) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)player, -amt, IPlayerWarp.EnumWarpType.NORMAL);
                }
                if (warp.get(IPlayerWarp.EnumWarpType.TEMPORARY) > 0) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)player, -warp.get(IPlayerWarp.EnumWarpType.TEMPORARY), IPlayerWarp.EnumWarpType.TEMPORARY);
                }
            }
            else {
                player.world.playSound(player.posX, player.posY, player.posZ, SoundsTC.craftstart, SoundCategory.PLAYERS, 0.25f, 1.0f, false);
                for (int a = 0; a < 40; ++a) {
                    FXDispatcher.INSTANCE.crucibleBubble((float)player.posX - 0.5f + player.world.rand.nextFloat() * 1.5f, (float)player.getEntityBoundingBox().minY + player.world.rand.nextFloat() * player.height, (float)player.posZ - 0.5f + player.world.rand.nextFloat() * 1.5f, 1.0f, 0.7f, 0.9f);
                }
            }
        }
    }
}
