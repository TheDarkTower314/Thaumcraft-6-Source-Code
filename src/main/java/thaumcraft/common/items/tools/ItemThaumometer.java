// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import thaumcraft.common.world.aura.AuraChunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.misc.PacketAuraToClient;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.world.aura.AuraHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.client.lib.events.RenderEventHandler;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemThaumometer extends ItemTCBase
{
    public ItemThaumometer() {
        super("thaumometer");
        this.setMaxStackSize(1);
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer p, final EnumHand hand) {
        if (world.isRemote) {
            this.drawFX(world, p);
            p.world.playSound(p.posX, p.posY, p.posZ, SoundsTC.scan, SoundCategory.PLAYERS, 0.5f, 1.0f, false);
        }
        else {
            this.doScan(world, p);
        }
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, p.getHeldItem(hand));
    }
    
    public void onUpdate(final ItemStack stack, final World world, final Entity entity, final int itemSlot, final boolean isSelected) {
        final boolean held = isSelected || itemSlot == 0;
        if (held && !world.isRemote && entity.ticksExisted % 20 == 0 && entity instanceof EntityPlayerMP) {
            this.updateAura(stack, world, (EntityPlayerMP)entity);
        }
        if (held && world.isRemote && entity.ticksExisted % 5 == 0 && entity instanceof EntityPlayer) {
            final Entity target = EntityUtils.getPointedEntity(world, entity, 1.0, 16.0, 5.0f, true);
            if (target != null && ScanningManager.isThingStillScannable((EntityPlayer)entity, target)) {
                FXDispatcher.INSTANCE.scanHighlight(target);
            }
            RenderEventHandler.thaumTarget = target;
            final RayTraceResult mop = this.getRayTraceResultFromPlayerWild(world, (EntityPlayer)entity, true);
            if (mop != null && mop.getBlockPos() != null && ScanningManager.isThingStillScannable((EntityPlayer)entity, mop.getBlockPos())) {
                FXDispatcher.INSTANCE.scanHighlight(mop.getBlockPos());
            }
        }
    }
    
    protected RayTraceResult getRayTraceResultFromPlayerWild(final World worldIn, final EntityPlayer playerIn, final boolean useLiquids) {
        final float f = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) + worldIn.rand.nextInt(25) - worldIn.rand.nextInt(25);
        final float f2 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) + worldIn.rand.nextInt(25) - worldIn.rand.nextInt(25);
        final double d0 = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX);
        final double d2 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) + playerIn.getEyeHeight();
        final double d3 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ);
        final Vec3d vec3 = new Vec3d(d0, d2, d3);
        final float f3 = MathHelper.cos(-f2 * 0.017453292f - 3.1415927f);
        final float f4 = MathHelper.sin(-f2 * 0.017453292f - 3.1415927f);
        final float f5 = -MathHelper.cos(-f * 0.017453292f);
        final float f6 = MathHelper.sin(-f * 0.017453292f);
        final float f7 = f4 * f5;
        final float f8 = f3 * f5;
        final double d4 = 16.0;
        final Vec3d vec4 = vec3.addVector(f7 * d4, f6 * d4, f8 * d4);
        return worldIn.rayTraceBlocks(vec3, vec4, useLiquids, !useLiquids, false);
    }
    
    private void updateAura(final ItemStack stack, final World world, final EntityPlayerMP player) {
        final AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), player.getPosition().getX() >> 4, player.getPosition().getZ() >> 4);
        if (ac != null) {
            if ((ac.getFlux() > ac.getVis() || ac.getFlux() > ac.getBase() / 3) && !ThaumcraftCapabilities.knowsResearch(player, "FLUX")) {
                ResearchManager.startResearchWithPopup(player, "FLUX");
                player.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("research.FLUX.warn")), true);
            }
            PacketHandler.INSTANCE.sendTo(new PacketAuraToClient(ac), player);
        }
    }
    
    private void drawFX(final World worldIn, final EntityPlayer playerIn) {
        final Entity target = EntityUtils.getPointedEntity(worldIn, playerIn, 1.0, 9.0, 0.0f, true);
        if (target != null) {
            for (int a = 0; a < 10; ++a) {
                FXDispatcher.INSTANCE.blockRunes(target.posX - 0.5, target.posY + target.getEyeHeight() / 2.0f, target.posZ - 0.5, 0.3f + worldIn.rand.nextFloat() * 0.7f, 0.0f, 0.3f + worldIn.rand.nextFloat() * 0.7f, (int)(target.height * 15.0f), 0.03f);
            }
        }
        else {
            final RayTraceResult mop = this.rayTrace(worldIn, playerIn, true);
            if (mop != null && mop.getBlockPos() != null) {
                for (int a2 = 0; a2 < 10; ++a2) {
                    FXDispatcher.INSTANCE.blockRunes(mop.getBlockPos().getX(), mop.getBlockPos().getY() + 0.25, mop.getBlockPos().getZ(), 0.3f + worldIn.rand.nextFloat() * 0.7f, 0.0f, 0.3f + worldIn.rand.nextFloat() * 0.7f, 15, 0.03f);
                }
            }
        }
    }
    
    public void doScan(final World worldIn, final EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            final Entity target = EntityUtils.getPointedEntity(worldIn, playerIn, 1.0, 9.0, 0.0f, true);
            if (target != null) {
                ScanningManager.scanTheThing(playerIn, target);
            }
            else {
                final RayTraceResult mop = this.rayTrace(worldIn, playerIn, true);
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
