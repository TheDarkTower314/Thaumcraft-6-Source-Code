// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research;

import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.ThaumcraftApi;
import net.minecraft.init.Items;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.world.DimensionType;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.research.IScanThing;

public class ScanSky implements IScanThing
{
    @Override
    public boolean checkThing(final EntityPlayer player, final Object obj) {
        if (obj != null || player.rotationPitch > 0.0f || !player.world.canSeeSky(player.getPosition().up()) || player.world.provider.getDimensionType() != DimensionType.OVERWORLD || !ThaumcraftCapabilities.knowsResearchStrict(player, "CELESTIALSCANNING")) {
            return false;
        }
        final int yaw = (int)(player.rotationYaw + 90.0f) % 360;
        final int pitch = (int)Math.abs(player.rotationPitch);
        int ca = (int)((player.world.getCelestialAngle(0.0f) + 0.25) * 360.0) % 360;
        final boolean night = ca > 180;
        boolean inRangeYaw = false;
        boolean inRangePitch = false;
        if (night) {
            ca -= 180;
        }
        if (ca > 90) {
            inRangeYaw = (Math.abs(Math.abs(yaw) - 180) < 10);
            inRangePitch = (Math.abs(180 - ca - pitch) < 7);
        }
        else {
            inRangeYaw = (Math.abs(yaw) < 10);
            inRangePitch = (Math.abs(ca - pitch) < 7);
        }
        return (inRangeYaw && inRangePitch) || night;
    }
    
    @Override
    public void onSuccess(final EntityPlayer player, final Object object) {
        if (object != null || player.rotationPitch > 0.0f || !player.world.canSeeSky(player.getPosition().up()) || !ThaumcraftCapabilities.knowsResearchStrict(player, "CELESTIALSCANNING")) {
            return;
        }
        final int yaw = (int)(player.rotationYaw + 90.0f) % 360;
        final int pitch = (int)Math.abs(player.rotationPitch);
        int ca = (int)((player.world.getCelestialAngle(0.0f) + 0.25) * 360.0) % 360;
        final boolean night = ca > 180;
        boolean inRangeYaw = false;
        boolean inRangePitch = false;
        if (night) {
            ca -= 180;
        }
        if (ca > 90) {
            inRangeYaw = (Math.abs(Math.abs(yaw) - 180) < 10);
            inRangePitch = (Math.abs(180 - ca - pitch) < 7);
        }
        else {
            inRangeYaw = (Math.abs(yaw) < 10);
            inRangePitch = (Math.abs(ca - pitch) < 7);
        }
        final int worldDay = (int)(player.world.getTotalWorldTime() / 24000L);
        if (inRangeYaw && inRangePitch) {
            final String pk = "CEL_" + worldDay + "_";
            final String key = pk + (night ? ("Moon" + player.world.provider.getMoonPhase(player.world.getWorldTime())) : "Sun");
            if (ThaumcraftCapabilities.knowsResearch(player, key)) {
                player.sendStatusMessage(new TextComponentTranslation("tc.celestial.fail.1", ""), true);
                return;
            }
            if (InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(ItemsTC.scribingTools, 1, 32767), true) && InventoryUtils.consumePlayerItem(player, new ItemStack(Items.PAPER), false, true)) {
                final ItemStack stack = new ItemStack(ItemsTC.celestialNotes, 1, night ? (5 + player.world.provider.getMoonPhase(player.world.getWorldTime())) : 0);
                if (!player.inventory.addItemStackToInventory(stack)) {
                    player.dropItem(stack, false);
                }
                ThaumcraftApi.internalMethods.progressResearch(player, key);
            }
            else {
                player.sendStatusMessage(new TextComponentTranslation("tc.celestial.fail.2", ""), true);
            }
            this.cleanResearch(player, pk);
        }
        else {
            if (!night) {
                return;
            }
            final EnumFacing face = player.getAdjustedHorizontalFacing();
            final int num = face.getIndex() - 2;
            final String pk2 = "CEL_" + worldDay + "_";
            final String key2 = pk2 + "Star" + num;
            if (ThaumcraftCapabilities.knowsResearch(player, key2)) {
                player.sendStatusMessage(new TextComponentTranslation("tc.celestial.fail.1", ""), true);
                return;
            }
            if (InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(ItemsTC.scribingTools, 1, 32767), true) && InventoryUtils.consumePlayerItem(player, new ItemStack(Items.PAPER), false, true)) {
                final ItemStack stack2 = new ItemStack(ItemsTC.celestialNotes, 1, 1 + num);
                if (!player.inventory.addItemStackToInventory(stack2)) {
                    player.dropItem(stack2, false);
                }
                ThaumcraftApi.internalMethods.progressResearch(player, key2);
            }
            else {
                player.sendStatusMessage(new TextComponentTranslation("tc.celestial.fail.2", ""), true);
            }
            this.cleanResearch(player, pk2);
        }
    }
    
    private void cleanResearch(final EntityPlayer player, final String pk) {
        final ArrayList<String> list = new ArrayList<String>();
        for (final String key : ThaumcraftCapabilities.getKnowledge(player).getResearchList()) {
            if (key.startsWith("CEL_") && !key.startsWith(pk)) {
                list.add(key);
            }
        }
        for (final String key : list) {
            ThaumcraftCapabilities.getKnowledge(player).removeResearch(key);
        }
        ResearchManager.syncList.put(player.getName(), true);
    }
    
    @Override
    public String getResearchKey(final EntityPlayer player, final Object object) {
        return "";
    }
}
