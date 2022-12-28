package thaumcraft.common.items.baubles;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketPlayerFlagToServer;


public class ItemCloudRing extends ItemTCBase implements IBauble
{
    public static HashMap<String, Boolean> jumpList;
    
    public ItemCloudRing() {
        super("cloud_ring");
        maxStackSize = 1;
        canRepair = false;
        setMaxDamage(0);
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }
    
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player.getEntityWorld().isRemote) {
            boolean spacePressed = Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed();
            if (spacePressed && !ItemCloudRing.jumpList.containsKey(player.getName())) {
                ItemCloudRing.jumpList.put(player.getName(), true);
            }
            if (spacePressed && !player.onGround && !player.isInWater() && player.jumpTicks == 0 && ItemCloudRing.jumpList.containsKey(player.getName()) && ItemCloudRing.jumpList.get(player.getName())) {
                FXDispatcher.INSTANCE.drawBamf(player.posX, player.posY + 0.5, player.posZ, 1.0f, 1.0f, 1.0f, false, false, EnumFacing.UP);
                player.getEntityWorld().playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.1f, 1.0f + (float)player.getEntityWorld().rand.nextGaussian() * 0.05f, false);
                ItemCloudRing.jumpList.put(player.getName(), false);
                player.motionY = 0.75;
                if (player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    player.motionY += (player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
                }
                if (player.isSprinting()) {
                    float f = player.rotationYaw * 0.017453292f;
                    player.motionX -= MathHelper.sin(f) * 0.2f;
                    player.motionZ += MathHelper.cos(f) * 0.2f;
                }
                player.fallDistance = 0.0f;
                PacketHandler.INSTANCE.sendToServer(new PacketPlayerFlagToServer(player, 1));
                ForgeHooks.onLivingJump(player);
            }
            if (player.onGround && player.jumpTicks == 0) {
                ItemCloudRing.jumpList.remove(player.getName());
            }
        }
    }
    
    static {
        ItemCloudRing.jumpList = new HashMap<String, Boolean>();
    }
}
