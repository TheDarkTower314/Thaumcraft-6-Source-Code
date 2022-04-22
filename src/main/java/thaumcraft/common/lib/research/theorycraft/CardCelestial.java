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
        this.cat = "BASICS";
    }
    
    @Override
    public NBTTagCompound serialize() {
        final NBTTagCompound nbt = super.serialize();
        nbt.setInteger("md1", this.md1);
        nbt.setInteger("md2", this.md2);
        nbt.setString("cat", this.cat);
        return nbt;
    }
    
    @Override
    public void deserialize(final NBTTagCompound nbt) {
        super.deserialize(nbt);
        this.md1 = nbt.getInteger("md1");
        this.md2 = nbt.getInteger("md2");
        this.cat = nbt.getString("cat");
    }
    
    @Override
    public String getResearchCategory() {
        return this.cat;
    }
    
    @Override
    public boolean initialize(final EntityPlayer player, final ResearchTableData data) {
        if (data.categoryTotals.isEmpty() || !ThaumcraftCapabilities.knowsResearch(player, "CELESTIALSCANNING")) {
            return false;
        }
        final Random r = new Random(this.getSeed());
        this.md1 = MathHelper.getInt(r, 0, 12);
        this.md2 = this.md1;
        while (this.md1 == this.md2) {
            this.md2 = MathHelper.getInt(r, 0, 12);
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
        this.cat = hKey;
        return this.cat != null;
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
        return new TextComponentTranslation("card.celestial.text", TextFormatting.BOLD + new TextComponentTranslation("tc.research_category." + this.cat).getFormattedText() + TextFormatting.RESET).getUnformattedText();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { new ItemStack(ItemsTC.celestialNotes, 1, this.md1), new ItemStack(ItemsTC.celestialNotes, 1, this.md2) };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true, true };
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        data.addTotal(this.getResearchCategory(), MathHelper.getInt(player.getRNG(), 25, 50));
        final boolean sun = this.md1 == 0 || this.md2 == 0;
        final boolean moon = this.md1 > 4 || this.md2 > 4;
        final boolean stars = (this.md1 > 0 && this.md1 < 5) || (this.md2 > 0 && this.md2 < 5);
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
