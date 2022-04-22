// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardReactions extends TheorycraftCard
{
    Aspect aspect1;
    Aspect aspect2;
    
    @Override
    public NBTTagCompound serialize() {
        final NBTTagCompound nbt = super.serialize();
        nbt.setString("aspect1", this.aspect1.getTag());
        nbt.setString("aspect2", this.aspect2.getTag());
        return nbt;
    }
    
    @Override
    public void deserialize(final NBTTagCompound nbt) {
        super.deserialize(nbt);
        this.aspect1 = Aspect.getAspect(nbt.getString("aspect1"));
        this.aspect2 = Aspect.getAspect(nbt.getString("aspect2"));
    }
    
    @Override
    public boolean initialize(final EntityPlayer player, final ResearchTableData data) {
        final Random r = new Random(this.getSeed());
        final int num = MathHelper.getInt(r, 0, Aspect.getCompoundAspects().size() - 1);
        this.aspect1 = Aspect.getCompoundAspects().get(num);
        int num2;
        for (num2 = num; num2 == num; num2 = MathHelper.getInt(r, 0, Aspect.getCompoundAspects().size() - 1)) {}
        this.aspect2 = Aspect.getCompoundAspects().get(num2);
        return true;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "ALCHEMY";
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.reactions.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.reactions.text", TextFormatting.BOLD + this.aspect1.getName() + TextFormatting.RESET, TextFormatting.BOLD + this.aspect2.getName() + TextFormatting.RESET).getFormattedText();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { ThaumcraftApiHelper.makeCrystal(this.aspect1), ThaumcraftApiHelper.makeCrystal(this.aspect2) };
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        data.addTotal(this.getResearchCategory(), 25);
        if (player.getRNG().nextFloat() < 0.33) {
            data.addInspiration(1);
        }
        return true;
    }
}
