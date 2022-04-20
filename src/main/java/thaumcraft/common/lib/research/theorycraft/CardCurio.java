// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.util.math.MathHelper;
import thaumcraft.api.research.ResearchCategories;
import java.util.Iterator;
import thaumcraft.common.items.curios.ItemCurio;
import java.util.ArrayList;
import java.util.Random;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardCurio extends TheorycraftCard
{
    ItemStack curio;
    
    public CardCurio() {
        this.curio = ItemStack.EMPTY;
    }
    
    @Override
    public NBTTagCompound serialize() {
        final NBTTagCompound nbt = super.serialize();
        nbt.setTag("stack", this.curio.serializeNBT());
        return nbt;
    }
    
    @Override
    public void deserialize(final NBTTagCompound nbt) {
        super.deserialize(nbt);
        this.curio = new ItemStack(nbt.getCompoundTag("stack"));
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.curio.name", new Object[0]).getFormattedText();
    }
    
    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.curio.text", new Object[0]).getFormattedText();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { this.curio };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true };
    }
    
    @Override
    public boolean initialize(final EntityPlayer player, final ResearchTableData data) {
        final Random r = new Random(this.getSeed());
        final ArrayList<ItemStack> curios = new ArrayList<ItemStack>();
        for (final ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemCurio) {
                final ItemStack c = stack.copy();
                c.setCount(1);
                curios.add(c);
            }
        }
        if (!curios.isEmpty()) {
            this.curio = curios.get(r.nextInt(curios.size()));
        }
        return !this.curio.isEmpty();
    }
    
    @Override
    public boolean activate(final EntityPlayer player, final ResearchTableData data) {
        data.addTotal("BASICS", 5);
        final String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
        data.addTotal(s[player.getRNG().nextInt(s.length)], 5);
        final String s2;
        final String type = s2 = ((ItemCurio)this.getRequiredItems()[0].getItem()).getVariantNames()[this.getRequiredItems()[0].getItemDamage()];
        switch (s2) {
            case "arcane": {
                data.addTotal("AUROMANCY", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "preserved": {
                data.addTotal("ALCHEMY", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "ancient": {
                data.addTotal("GOLEMANCY", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "eldritch": {
                data.addTotal("ELDRITCH", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "knowledge": {
                data.addTotal("INFUSION", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "twisted": {
                data.addTotal("ARTIFICE", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "rites": {
                data.addTotal("ELDRITCH", MathHelper.getInt(player.getRNG(), 15, 20));
                data.addTotal("AUROMANCY", MathHelper.getInt(player.getRNG(), 10, 15));
                break;
            }
            default: {
                data.addTotal("BASICS", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
        }
        if (player.getRNG().nextBoolean()) {
            ++data.bonusDraws;
        }
        if (player.getRNG().nextBoolean()) {
            ++data.bonusDraws;
        }
        return true;
    }
}
