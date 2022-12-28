package thaumcraft.common.lib.research;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.IScanThing;
import thaumcraft.common.lib.utils.InventoryUtils;


public class ScanSky implements IScanThing
{
    @Override
    public boolean checkThing(EntityPlayer player, Object obj) {
        if (obj != null || player.rotationPitch > 0.0f || !player.world.canSeeSky(player.getPosition().up()) || player.world.provider.getDimensionType() != DimensionType.OVERWORLD || !ThaumcraftCapabilities.knowsResearchStrict(player, "CELESTIALSCANNING")) {
            return false;
        }
        int yaw = (int)(player.rotationYaw + 90.0f) % 360;
        int pitch = (int)Math.abs(player.rotationPitch);
        int ca = (int)((player.world.getCelestialAngle(0.0f) + 0.25) * 360.0) % 360;
        boolean night = ca > 180;
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
    public void onSuccess(EntityPlayer player, Object object) {
        if (object != null || player.rotationPitch > 0.0f || !player.world.canSeeSky(player.getPosition().up()) || !ThaumcraftCapabilities.knowsResearchStrict(player, "CELESTIALSCANNING")) {
            return;
        }
        int yaw = (int)(player.rotationYaw + 90.0f) % 360;
        int pitch = (int)Math.abs(player.rotationPitch);
        int ca = (int)((player.world.getCelestialAngle(0.0f) + 0.25) * 360.0) % 360;
        boolean night = ca > 180;
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
        int worldDay = (int)(player.world.getTotalWorldTime() / 24000L);
        if (inRangeYaw && inRangePitch) {
            String pk = "CEL_" + worldDay + "_";
            String key = pk + (night ? ("Moon" + player.world.provider.getMoonPhase(player.world.getWorldTime())) : "Sun");
            if (ThaumcraftCapabilities.knowsResearch(player, key)) {
                player.sendStatusMessage(new TextComponentTranslation("tc.celestial.fail.1", ""), true);
                return;
            }
            if (InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(ItemsTC.scribingTools, 1, 32767), true) && InventoryUtils.consumePlayerItem(player, new ItemStack(Items.PAPER), false, true)) {
                ItemStack stack = new ItemStack(ItemsTC.celestialNotes, 1, night ? (5 + player.world.provider.getMoonPhase(player.world.getWorldTime())) : 0);
                if (!player.inventory.addItemStackToInventory(stack)) {
                    player.dropItem(stack, false);
                }
                ThaumcraftApi.internalMethods.progressResearch(player, key);
            }
            else {
                player.sendStatusMessage(new TextComponentTranslation("tc.celestial.fail.2", ""), true);
            }
            cleanResearch(player, pk);
        }
        else {
            if (!night) {
                return;
            }
            EnumFacing face = player.getAdjustedHorizontalFacing();
            int num = face.getIndex() - 2;
            String pk2 = "CEL_" + worldDay + "_";
            String key2 = pk2 + "Star" + num;
            if (ThaumcraftCapabilities.knowsResearch(player, key2)) {
                player.sendStatusMessage(new TextComponentTranslation("tc.celestial.fail.1", ""), true);
                return;
            }
            if (InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(ItemsTC.scribingTools, 1, 32767), true) && InventoryUtils.consumePlayerItem(player, new ItemStack(Items.PAPER), false, true)) {
                ItemStack stack2 = new ItemStack(ItemsTC.celestialNotes, 1, 1 + num);
                if (!player.inventory.addItemStackToInventory(stack2)) {
                    player.dropItem(stack2, false);
                }
                ThaumcraftApi.internalMethods.progressResearch(player, key2);
            }
            else {
                player.sendStatusMessage(new TextComponentTranslation("tc.celestial.fail.2", ""), true);
            }
            cleanResearch(player, pk2);
        }
    }
    
    private void cleanResearch(EntityPlayer player, String pk) {
        ArrayList<String> list = new ArrayList<String>();
        for (String key : ThaumcraftCapabilities.getKnowledge(player).getResearchList()) {
            if (key.startsWith("CEL_") && !key.startsWith(pk)) {
                list.add(key);
            }
        }
        for (String key : list) {
            ThaumcraftCapabilities.getKnowledge(player).removeResearch(key);
        }
        ResearchManager.syncList.put(player.getName(), true);
    }
    
    @Override
    public String getResearchKey(EntityPlayer player, Object object) {
        return "";
    }
}
