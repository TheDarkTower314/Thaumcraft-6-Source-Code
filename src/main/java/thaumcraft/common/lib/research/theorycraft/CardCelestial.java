// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardCelestial extends TheorycraftCard
{
    int md1;
    int md2;
    String cat;
    
    public CardCelestial() {
        cat = "BASICS";
    }
    
    @Override
    public NBTTagCompound serialize() {
        final NBTTagCompound nbt = super.serialize();
        nbt.setInteger("md1", md1);
        nbt.setInteger("md2", md2);
        nbt.setString("cat", cat);
        return nbt;
    }
    
    @Override
    public void deserialize(final NBTTagCompound nbt) {
        super.deserialize(nbt);
        md1 = nbt.getInteger("md1");
        md2 = nbt.getInteger("md2");
        cat = nbt.getString("cat");
    }
    
    @Override
    public String getResearchCategory() {
        return cat;
    }
    
    @Override
    public boolean initialize(final EntityPlayer player, final ResearchTableData data) {
        if (data.categoryTotals.isEmpty() || !ThaumcraftCapabilities.knowsResearch(player, "CELESTIALSCANNING")) {
            return false;
        }
        final Random r = new Random(getSeed());
        md1 = MathHelper.getInt(r, 0, 12);
        md2 = md1;
        while (md1 == md2) {
            md2 = MathHelper.getInt(r, 0, 12);
        }
        int hVal = 0;
        String hKey = "";
        for (final String category : data.categoryTotals.keySet()) {
            final int q = data.getTotal(category);
            if (q > hVal) {
                hVal = q;
                hKey = category;
            }
        }
        cat = hKey;
        return cat != null;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.celestial.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.celestial.text", TextFormatting.BOLD + new TextComponentTranslation("tc.research_category." + cat).getFormattedText() + TextFormatting.RESET).getUnformattedText();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { new ItemStack(ItemsTC.celestialNotes, 1, md1), new ItemStack(ItemsTC.celestialNotes, 1, md2) };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true, true };
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        data.addTotal(getResearchCategory(), MathHelper.getInt(player.getRNG(), 25, 50));
        final boolean sun = md1 == 0 || md2 == 0;
        final boolean moon = md1 > 4 || md2 > 4;
        final boolean stars = (md1 > 0 && md1 < 5) || (md2 > 0 && md2 < 5);
        if (stars) {
            final int amt = MathHelper.getInt(player.getRNG(), 0, 5);
            data.addTotal("ELDRITCH", amt * 2);
            ThaumcraftApi.internalMethods.addWarpToPlayer(player, amt, IPlayerWarp.EnumWarpType.TEMPORARY);
        }
        if (sun) {
            ++data.penaltyStart;
        }
        if (moon) {
            ++data.bonusDraws;
        }
        return true;
    }
}
