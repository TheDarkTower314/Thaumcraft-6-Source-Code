package thaumcraft.common.lib.research.theorycraft;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardReactions extends TheorycraftCard
{
    Aspect aspect1;
    Aspect aspect2;
    
    @Override
    public NBTTagCompound serialize() {
        NBTTagCompound nbt = super.serialize();
        nbt.setString("aspect1", aspect1.getTag());
        nbt.setString("aspect2", aspect2.getTag());
        return nbt;
    }
    
    @Override
    public void deserialize(NBTTagCompound nbt) {
        super.deserialize(nbt);
        aspect1 = Aspect.getAspect(nbt.getString("aspect1"));
        aspect2 = Aspect.getAspect(nbt.getString("aspect2"));
    }
    
    @Override
    public boolean initialize(EntityPlayer player, ResearchTableData data) {
        Random r = new Random(getSeed());
        int num = MathHelper.getInt(r, 0, Aspect.getCompoundAspects().size() - 1);
        aspect1 = Aspect.getCompoundAspects().get(num);
        int num2;
        for (num2 = num; num2 == num; num2 = MathHelper.getInt(r, 0, Aspect.getCompoundAspects().size() - 1)) {}
        aspect2 = Aspect.getCompoundAspects().get(num2);
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
        return new TextComponentTranslation("card.reactions.text", TextFormatting.BOLD + aspect1.getName() + TextFormatting.RESET, TextFormatting.BOLD + aspect2.getName() + TextFormatting.RESET).getFormattedText();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { ThaumcraftApiHelper.makeCrystal(aspect1), ThaumcraftApiHelper.makeCrystal(aspect2) };
    }
    
    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 25);
        if (player.getRNG().nextFloat() < 0.33) {
            data.addInspiration(1);
        }
        return true;
    }
}
