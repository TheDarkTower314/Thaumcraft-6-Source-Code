package thaumcraft.common.lib.research.theorycraft;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;


public class CardMindOverMatter extends TheorycraftCard
{
    ItemStack stack;
    static ItemStack[] options;
    
    public CardMindOverMatter() {
        stack = ItemStack.EMPTY;
    }
    
    @Override
    public NBTTagCompound serialize() {
        NBTTagCompound nbt = super.serialize();
        nbt.setTag("stack", stack.serializeNBT());
        return nbt;
    }
    
    @Override
    public void deserialize(NBTTagCompound nbt) {
        super.deserialize(nbt);
        stack = new ItemStack(nbt.getCompoundTag("stack"));
    }
    
    @Override
    public boolean initialize(EntityPlayer player, ResearchTableData data) {
        Random r = new Random(getSeed());
        stack = CardMindOverMatter.options[r.nextInt(CardMindOverMatter.options.length)].copy();
        return stack != null;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "ARTIFICE";
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.mindmatter.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.mindmatter.text", getVal()).getFormattedText();
    }
    
    private int getVal() {
        int q = 10;
        try {
            q += (int)Math.sqrt(ThaumcraftCraftingManager.getObjectTags(stack).visSize());
        }
        catch (Exception ex) {}
        return q;
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { stack };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true };
    }
    
    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), getVal());
        return true;
    }
    
    static {
        CardMindOverMatter.options = new ItemStack[] { new ItemStack(ItemsTC.visResonator), new ItemStack(ItemsTC.thaumometer), new ItemStack(Blocks.ANVIL), new ItemStack(Blocks.ACTIVATOR_RAIL), new ItemStack(Blocks.DISPENSER), new ItemStack(Blocks.DROPPER), new ItemStack(Blocks.ENCHANTING_TABLE), new ItemStack(Blocks.ENDER_CHEST), new ItemStack(Blocks.JUKEBOX), new ItemStack(Blocks.DAYLIGHT_DETECTOR), new ItemStack(Blocks.PISTON), new ItemStack(Blocks.HOPPER), new ItemStack(Blocks.STICKY_PISTON), new ItemStack(Items.MAP), new ItemStack(Items.COMPASS), new ItemStack(Items.TNT_MINECART), new ItemStack(Items.COMPARATOR), new ItemStack(Items.CLOCK) };
    }
}
