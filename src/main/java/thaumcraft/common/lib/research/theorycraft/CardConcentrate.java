// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import java.util.Random;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardConcentrate extends TheorycraftCard
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
        return "ALCHEMY";
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.concentrate.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.concentrate.text", TextFormatting.BOLD + aspect.getName() + TextFormatting.RESET).getFormattedText();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { ThaumcraftApiHelper.makeCrystal(aspect) };
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        data.addTotal(getResearchCategory(), 15);
        ++data.bonusDraws;
        if (player.getRNG().nextFloat() < 0.33) {
            data.addInspiration(1);
        }
        return true;
    }
}
