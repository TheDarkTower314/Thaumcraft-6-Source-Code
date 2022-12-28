package thaumcraft.common.lib.research.theorycraft;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;


public class CardInfuse extends TheorycraftCard
{
    Aspect aspect;
    ItemStack stack;
    static ItemStack[] options;
    
    public CardInfuse() {
        stack = ItemStack.EMPTY;
    }
    
    @Override
    public NBTTagCompound serialize() {
        NBTTagCompound nbt = super.serialize();
        nbt.setString("aspect", aspect.getTag());
        nbt.setTag("stack", stack.serializeNBT());
        return nbt;
    }
    
    @Override
    public void deserialize(NBTTagCompound nbt) {
        super.deserialize(nbt);
        aspect = Aspect.getAspect(nbt.getString("aspect"));
        stack = new ItemStack(nbt.getCompoundTag("stack"));
    }
    
    @Override
    public boolean initialize(EntityPlayer player, ResearchTableData data) {
        Random r = new Random(getSeed());
        int num = r.nextInt(Aspect.getCompoundAspects().size());
        aspect = Aspect.getCompoundAspects().get(num);
        stack = CardInfuse.options[r.nextInt(CardInfuse.options.length)].copy();
        return aspect != null && stack != null;
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
        return new TextComponentTranslation("card.infuse.name").getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.infuse.text", TextFormatting.BOLD + aspect.getName() + TextFormatting.RESET, stack.getDisplayName(), getVal()).getFormattedText();
    }
    
    private int getVal() {
        int q = 10;
        try {
            q += (int)(Math.sqrt(ThaumcraftCraftingManager.getObjectTags(stack).visSize()) * 1.5);
        }
        catch (Exception ex) {}
        return q;
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { stack, ItemPhial.makeFilledPhial(aspect) };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true, true };
    }
    
    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), getVal());
        return true;
    }
    
    static {
        CardInfuse.options = new ItemStack[] { new ItemStack(ItemsTC.alumentum), new ItemStack(BlocksTC.nitor.get(EnumDyeColor.YELLOW)), new ItemStack(ItemsTC.amber), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.ingots, 1, 0), new ItemStack(ItemsTC.ingots, 1, 2), new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.DIAMOND), new ItemStack(Items.EMERALD), new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.LEATHER), new ItemStack(Blocks.WOOL), new ItemStack(Items.BRICK), new ItemStack(Items.ARROW), new ItemStack(Items.EGG), new ItemStack(Items.FEATHER), new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(Items.REDSTONE), new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.GUNPOWDER), new ItemStack(Items.BOW), new ItemStack(Items.GOLDEN_SWORD), new ItemStack(Items.IRON_SWORD), new ItemStack(Items.IRON_PICKAXE), new ItemStack(Items.GOLDEN_PICKAXE), new ItemStack(Items.QUARTZ), new ItemStack(Items.APPLE) };
    }
}
