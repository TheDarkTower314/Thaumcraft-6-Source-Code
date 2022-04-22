// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.consumables;

import net.minecraft.util.EnumActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.projectile.EntityCausalityCollapser;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.common.items.ItemTCBase;

public class ItemCausalityCollapser extends ItemTCBase
{
    public ItemCausalityCollapser() {
        super("causality_collapser");
        setMaxStackSize(16);
        setMaxDamage(0);
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }
        player.playSound(SoundEvents.ENTITY_EGG_THROW, 0.3f, 0.4f / (ItemCausalityCollapser.itemRand.nextFloat() * 0.4f + 0.8f));
        if (!world.isRemote) {
            EntityCausalityCollapser proj = new EntityCausalityCollapser(world, player);
            proj.shoot(player, player.rotationPitch, player.rotationYaw, -5.0f, 0.8f, 2.0f);
            world.spawnEntity(proj);
        }
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
