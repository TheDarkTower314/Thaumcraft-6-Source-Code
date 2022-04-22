// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.item.Item;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.block.Block;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import java.util.Random;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardInfuse extends TheorycraftCard
{
    Aspect aspect;
    ItemStack stack;
    static ItemStack[] options;
    
    public CardInfuse() {
        this.stack = ItemStack.EMPTY;
    }
    
    @Override
    public NBTTagCompound serialize() {
        final NBTTagCompound nbt = super.serialize();
        nbt.setString("aspect", this.aspect.getTag());
        nbt.setTag("stack", this.stack.serializeNBT());
        return nbt;
    }
    
    @Override
    public void deserialize(final NBTTagCompound nbt) {
        super.deserialize(nbt);
        this.aspect = Aspect.getAspect(nbt.getString("aspect"));
        this.stack = new ItemStack(nbt.getCompoundTag("stack"));
    }
    
    @Override
    public boolean initialize(final EntityPlayer player, final ResearchTableData data) {
        final Random r = new Random(this.getSeed());
        final int num = r.nextInt(Aspect.getCompoundAspects().size());
        this.aspect = Aspect.getCompoundAspects().get(num);
        this.stack = CardInfuse.options[r.nextInt(CardInfuse.options.length)].copy();
        return this.aspect != null && this.stack != null;
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
        return new TextComponentTranslation("card.infuse.text", TextFormatting.BOLD + this.aspect.getName() + TextFormatting.RESET, this.stack.getDisplayName(), this.getVal()).getFormattedText();
    }
    
    private int getVal() {
        int q = 10;
        try {
            q += (int)(Math.sqrt(ThaumcraftCraftingManager.getObjectTags(this.stack).visSize()) * 1.5);
        }
        catch (final Exception ex) {}
        return q;
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { this.stack, ItemPhial.makeFilledPhial(this.aspect) };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true, true };
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        data.addTotal(this.getResearchCategory(), this.getVal());
        return true;
    }
    
    static {
        CardInfuse.options = new ItemStack[] { new ItemStack(ItemsTC.alumentum), new ItemStack(BlocksTC.nitor.get(EnumDyeColor.YELLOW)), new ItemStack(ItemsTC.amber), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.ingots, 1, 0), new ItemStack(ItemsTC.ingots, 1, 2), new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.DIAMOND), new ItemStack(Items.EMERALD), new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.LEATHER), new ItemStack(Blocks.WOOL), new ItemStack(Items.BRICK), new ItemStack(Items.ARROW), new ItemStack(Items.EGG), new ItemStack(Items.FEATHER), new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(Items.REDSTONE), new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.GUNPOWDER), new ItemStack(Items.BOW), new ItemStack(Items.GOLDEN_SWORD), new ItemStack(Items.IRON_SWORD), new ItemStack(Items.IRON_PICKAXE), new ItemStack(Items.GOLDEN_PICKAXE), new ItemStack(Items.QUARTZ), new ItemStack(Items.APPLE) };
    }
}
