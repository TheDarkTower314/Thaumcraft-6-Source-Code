package thaumcraft.common.items.tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.lib.events.RenderEventHandler;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketAuraToClient;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;


public class ItemThaumometer extends ItemTCBase
{
    public ItemThaumometer() {
        super("thaumometer");
        setMaxStackSize(1);
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer p, EnumHand hand) {
        if (world.isRemote) {
            drawFX(world, p);
            p.world.playSound(p.posX, p.posY, p.posZ, SoundsTC.scan, SoundCategory.PLAYERS, 0.5f, 1.0f, false);
        }
        else {
            doScan(world, p);
        }
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, p.getHeldItem(hand));
    }
    
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        boolean held = isSelected || itemSlot == 0;
        if (held && !world.isRemote && entity.ticksExisted % 20 == 0 && entity instanceof EntityPlayerMP) {
            updateAura(stack, world, (EntityPlayerMP)entity);
        }
        if (held && world.isRemote && entity.ticksExisted % 5 == 0 && entity instanceof EntityPlayer) {
            Entity target = EntityUtils.getPointedEntity(world, entity, 1.0, 16.0, 5.0f, true);
            if (target != null && ScanningManager.isThingStillScannable((EntityPlayer)entity, target)) {
                FXDispatcher.INSTANCE.scanHighlight(target);
            }
            RenderEventHandler.thaumTarget = target;
            RayTraceResult mop = getRayTraceResultFromPlayerWild(world, (EntityPlayer)entity, true);
            if (mop != null && mop.getBlockPos() != null && ScanningManager.isThingStillScannable((EntityPlayer)entity, mop.getBlockPos())) {
                FXDispatcher.INSTANCE.scanHighlight(mop.getBlockPos());
            }
        }
    }
    
    protected RayTraceResult getRayTraceResultFromPlayerWild(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
        float f = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) + worldIn.rand.nextInt(25) - worldIn.rand.nextInt(25);
        float f2 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) + worldIn.rand.nextInt(25) - worldIn.rand.nextInt(25);
        double d0 = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX);
        double d2 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) + playerIn.getEyeHeight();
        double d3 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ);
        Vec3d vec3 = new Vec3d(d0, d2, d3);
        float f3 = MathHelper.cos(-f2 * 0.017453292f - 3.1415927f);
        float f4 = MathHelper.sin(-f2 * 0.017453292f - 3.1415927f);
        float f5 = -MathHelper.cos(-f * 0.017453292f);
        float f6 = MathHelper.sin(-f * 0.017453292f);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d4 = 16.0;
        Vec3d vec4 = vec3.addVector(f7 * d4, f6 * d4, f8 * d4);
        return worldIn.rayTraceBlocks(vec3, vec4, useLiquids, !useLiquids, false);
    }
    
    private void updateAura(ItemStack stack, World world, EntityPlayerMP player) {
        AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), player.getPosition().getX() >> 4, player.getPosition().getZ() >> 4);
        if (ac != null) {
            if ((ac.getFlux() > ac.getVis() || ac.getFlux() > ac.getBase() / 3) && !ThaumcraftCapabilities.knowsResearch(player, "FLUX")) {
                ResearchManager.startResearchWithPopup(player, "FLUX");
                player.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("research.FLUX.warn")), true);
            }
            PacketHandler.INSTANCE.sendTo(new PacketAuraToClient(ac), player);
        }
    }
    
    private void drawFX(World worldIn, EntityPlayer playerIn) {
        Entity target = EntityUtils.getPointedEntity(worldIn, playerIn, 1.0, 9.0, 0.0f, true);
        if (target != null) {
            for (int a = 0; a < 10; ++a) {
                FXDispatcher.INSTANCE.blockRunes(target.posX - 0.5, target.posY + target.getEyeHeight() / 2.0f, target.posZ - 0.5, 0.3f + worldIn.rand.nextFloat() * 0.7f, 0.0f, 0.3f + worldIn.rand.nextFloat() * 0.7f, (int)(target.height * 15.0f), 0.03f);
            }
        }
        else {
            RayTraceResult mop = rayTrace(worldIn, playerIn, true);
            if (mop != null && mop.getBlockPos() != null) {
                for (int a2 = 0; a2 < 10; ++a2) {
                    FXDispatcher.INSTANCE.blockRunes(mop.getBlockPos().getX(), mop.getBlockPos().getY() + 0.25, mop.getBlockPos().getZ(), 0.3f + worldIn.rand.nextFloat() * 0.7f, 0.0f, 0.3f + worldIn.rand.nextFloat() * 0.7f, 15, 0.03f);
                }
            }
        }
    }
    
    public void doScan(World worldIn, EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            Entity target = EntityUtils.getPointedEntity(worldIn, playerIn, 1.0, 9.0, 0.0f, true);
            if (target != null) {
                ScanningManager.scanTheThing(playerIn, target);
            }
            else {
                RayTraceResult mop = rayTrace(worldIn, playerIn, true);
                if (mop != null && mop.getBlockPos() != null) {
                    ScanningManager.scanTheThing(playerIn, mop.getBlockPos());
                }
                else {
                    ScanningManager.scanTheThing(playerIn, null);
                }
            }
        }
    }
}
