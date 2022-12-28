package thaumcraft.common.items.consumables;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.projectile.EntityBottleTaint;
import thaumcraft.common.items.ItemTCBase;


public class ItemBottleTaint extends ItemTCBase
{
    public ItemBottleTaint() {
        super("bottle_taint");
        maxStackSize = 8;
        setMaxDamage(0);
        setCreativeTab(ConfigItems.TABTC);
        setHasSubtypes(false);
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }
        player.playSound(SoundEvents.ENTITY_EGG_THROW, 0.5f, 0.4f / (ItemBottleTaint.itemRand.nextFloat() * 0.4f + 0.8f));
        if (!world.isRemote) {
            EntityBottleTaint entityBottle = new EntityBottleTaint(world, player);
            entityBottle.shoot(player, player.rotationPitch, player.rotationYaw, -5.0f, 0.66f, 1.0f);
            world.spawnEntity(entityBottle);
        }
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
