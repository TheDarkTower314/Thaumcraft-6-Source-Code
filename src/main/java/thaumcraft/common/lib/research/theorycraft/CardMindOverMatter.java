// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import net.minecraft.util.text.TextComponentTranslation;
import java.util.Random;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardMindOverMatter extends TheorycraftCard
{
    ItemStack stack;
    static ItemStack[] options;
    
    public CardMindOverMatter() {
        this.stack = ItemStack.EMPTY;
    }
    
    @Override
    public NBTTagCompound serialize() {
        final NBTTagCompound nbt = super.serialize();
        nbt.setTag("stack", this.stack.serializeNBT());
        return nbt;
    }
    
    @Override
    public void deserialize(final NBTTagCompound nbt) {
        super.deserialize(nbt);
        this.stack = new ItemStack(nbt.getCompoundTag("stack"));
    }
    
    @Override
    public boolean initialize(final EntityPlayer player, final ResearchTableData data) {
        final Random r = new Random(this.getSeed());
        this.stack = CardMindOverMatter.options[r.nextInt(CardMindOverMatter.options.length)].copy();
        return this.stack != null;
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
        return new TextComponentTranslation("card.mindmatter.text", this.getVal()).getFormattedText();
    }
    
    private int getVal() {
        int q = 10;
        try {
            q += (int)Math.sqrt(ThaumcraftCraftingManager.getObjectTags(this.stack).visSize());
        }
        catch (final Exception ex) {}
        return q;
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { this.stack };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true };
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        data.addTotal(this.getResearchCategory(), this.getVal());
        return true;
    }
    
    static {
        CardMindOverMatter.options = new ItemStack[] { new ItemStack(ItemsTC.visResonator), new ItemStack(ItemsTC.thaumometer), new ItemStack(Blocks.ANVIL), new ItemStack(Blocks.ACTIVATOR_RAIL), new ItemStack(Blocks.DISPENSER), new ItemStack(Blocks.DROPPER), new ItemStack(Blocks.ENCHANTING_TABLE), new ItemStack(Blocks.ENDER_CHEST), new ItemStack(Blocks.JUKEBOX), new ItemStack(Blocks.DAYLIGHT_DETECTOR), new ItemStack(Blocks.PISTON), new ItemStack(Blocks.HOPPER), new ItemStack(Blocks.STICKY_PISTON), new ItemStack(Items.MAP), new ItemStack(Items.COMPASS), new ItemStack(Items.TNT_MINECART), new ItemStack(Items.COMPARATOR), new ItemStack(Items.CLOCK) };
    }
}
