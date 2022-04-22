// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.common.items.consumables.ItemPhial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import java.util.Random;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardChannel extends TheorycraftCard
{
    Aspect aspect;
    
    @Override
    public NBTTagCompound serialize() {
        final NBTTagCompound nbt = super.serialize();
        nbt.setString("aspect", aspect.getTag());
        return nbt;
    }
    
    @Override
    public void deserialize(final NBTTagCompound nbt) {
        super.deserialize(nbt);
        aspect = Aspect.getAspect(nbt.getString("aspect"));
    }
    
    @Override
    public boolean initialize(final EntityPlayer player, final ResearchTableData data) {
        final Random r = new Random(getSeed());
        final int num = r.nextInt(Aspect.getCompoundAspects().size());
        aspect = Aspect.getCompoundAspects().get(num);
        return true;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "INFUSION";
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.channel.name", TextFormatting.DARK_BLUE + aspect.getName() + TextFormatting.RESET + "" + TextFormatting.BOLD).getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.channel.text", TextFormatting.BOLD + aspect.getName() + TextFormatting.RESET).getFormattedText();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { ItemPhial.makeFilledPhial(aspect) };
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        data.addTotal(getResearchCategory(), 25);
        return true;
    }
}
