// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import thaumcraft.api.golems.EnumGolemTrait;
import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.seals.ISealConfigToggles;

public class SealEmptyAdvanced extends SealEmpty implements ISealConfigToggles
{
    ResourceLocation icon;
    
    public SealEmptyAdvanced() {
        this.icon = new ResourceLocation("thaumcraft", "items/seals/seal_empty_advanced");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:empty_advanced";
    }
    
    @Override
    public int getFilterSize() {
        return 9;
    }
    
    @Override
    public ResourceLocation getSealIcon() {
        return this.icon;
    }
    
    @Override
    public NonNullList<ItemStack> getInv(final int c) {
        if (this.getToggles()[4].value && !this.isBlacklist()) {
            final ArrayList<ItemStack> w = new ArrayList<ItemStack>();
            for (final ItemStack s : super.getInv()) {
                if (s != null && !s.isEmpty()) {
                    w.add(s);
                }
            }
            if (w.size() > 0) {
                final int i = Math.abs(c % w.size());
                return NonNullList.withSize(1, w.get(i));
            }
        }
        return super.getInv();
    }
    
    @Override
    public NonNullList<ItemStack> getInv() {
        return super.getInv();
    }
    
    @Override
    public SealToggle[] getToggles() {
        return this.props;
    }
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 1, 3, 0, 4 };
    }
    
    @Override
    public void setToggle(final int indx, final boolean value) {
        this.props[indx].setValue(value);
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.SMART };
    }
}
