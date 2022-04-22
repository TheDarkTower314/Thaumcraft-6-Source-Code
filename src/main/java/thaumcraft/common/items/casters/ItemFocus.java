// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters;

import net.minecraft.item.EnumRarity;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.FocusModSplit;
import net.minecraft.util.text.TextFormatting;
import java.util.Iterator;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.IFocusElement;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusPackage;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import java.awt.Color;
import thaumcraft.api.casters.FocusEngine;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemFocus extends ItemTCBase
{
    private int maxComplexity;
    
    public ItemFocus(final String name, final int complexity) {
        super(name);
        this.maxStackSize = 1;
        this.setMaxDamage(0);
        this.maxComplexity = complexity;
    }
    
    public int getFocusColor(final ItemStack focusstack) {
        if (focusstack == null || focusstack.isEmpty() || focusstack.getTagCompound() == null) {
            return 16777215;
        }
        int color = 16777215;
        if (!focusstack.getTagCompound().hasKey("color")) {
            final FocusPackage core = getPackage(focusstack);
            if (core != null) {
                final FocusEffect[] fe = core.getFocusEffects();
                int r = 0;
                int g = 0;
                int b = 0;
                for (final FocusEffect ef : fe) {
                    final Color c = new Color(FocusEngine.getElementColor(ef.getKey()));
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                }
                if (fe.length > 0) {
                    r /= fe.length;
                    g /= fe.length;
                    b /= fe.length;
                }
                final Color c2 = new Color(r, g, b);
                color = c2.getRGB();
                focusstack.setTagInfo("color", new NBTTagInt(color));
            }
        }
        else {
            color = focusstack.getTagCompound().getInteger("color");
        }
        return color;
    }
    
    public String getSortingHelper(final ItemStack focusstack) {
        if (focusstack == null || focusstack.isEmpty() || !focusstack.hasTagCompound()) {
            return null;
        }
        int sh = focusstack.getTagCompound().getInteger("srt");
        if (sh == 0) {
            sh = getPackage(focusstack).getSortingHelper();
            focusstack.setTagInfo("srt", new NBTTagInt(sh));
        }
        return focusstack.getDisplayName() + sh;
    }
    
    public static void setPackage(final ItemStack focusstack, final FocusPackage core) {
        final NBTTagCompound tag = core.serialize();
        focusstack.setTagInfo("package", tag);
    }
    
    public static FocusPackage getPackage(final ItemStack focusstack) {
        if (focusstack == null || focusstack.isEmpty()) {
            return null;
        }
        final NBTTagCompound tag = focusstack.getSubCompound("package");
        if (tag != null) {
            final FocusPackage p = new FocusPackage();
            p.deserialize(tag);
            return p;
        }
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        this.addFocusInformation(stack, worldIn, tooltip, flagIn);
    }
    
    @SideOnly(Side.CLIENT)
    public void addFocusInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        final FocusPackage p = getPackage(stack);
        if (p != null) {
            final float al = this.getVisCost(stack);
            final String amount = ItemStack.DECIMALFORMAT.format(al);
            tooltip.add(amount + " " + I18n.translateToLocal("item.Focus.cost1"));
            for (final IFocusElement fe : p.nodes) {
                if (fe instanceof FocusNode && !(fe instanceof FocusMediumRoot)) {
                    this.buildInfo(tooltip, (FocusNode)fe, 0);
                }
            }
        }
    }
    
    private void buildInfo(final List list, final FocusNode node, final int depth) {
        if (node instanceof FocusNode && !(node instanceof FocusMediumRoot)) {
            String t0 = "";
            for (int a = 0; a < depth; ++a) {
                t0 += "  ";
            }
            t0 = t0 + TextFormatting.DARK_PURPLE + I18n.translateToLocal(node.getUnlocalizedName());
            if (!node.getSettingList().isEmpty()) {
                t0 = t0 + TextFormatting.DARK_AQUA + " [";
                boolean q = false;
                for (final String st : node.getSettingList()) {
                    final NodeSetting ns = node.getSetting(st);
                    t0 = t0 + (q ? ", " : "") + ns.getLocalizedName() + " " + ns.getValueText();
                    q = true;
                }
                t0 += "]";
            }
            list.add(t0);
            if (node instanceof FocusModSplit) {
                final FocusModSplit split = (FocusModSplit)node;
                for (final FocusPackage p : split.getSplitPackages()) {
                    for (final IFocusElement fe : p.nodes) {
                        if (fe instanceof FocusNode && !(fe instanceof FocusMediumRoot)) {
                            this.buildInfo(list, (FocusNode)fe, depth + 1);
                        }
                    }
                }
            }
        }
    }
    
    public EnumRarity getRarity(final ItemStack focusstack) {
        return EnumRarity.RARE;
    }
    
    public float getVisCost(final ItemStack focusstack) {
        final FocusPackage p = getPackage(focusstack);
        return (p == null) ? 0.0f : (p.getComplexity() / 5.0f);
    }
    
    public int getActivationTime(final ItemStack focusstack) {
        final FocusPackage p = getPackage(focusstack);
        return (p == null) ? 0 : Math.max(5, p.getComplexity() / 5 * (p.getComplexity() / 4));
    }
    
    public int getMaxComplexity() {
        return this.maxComplexity;
    }
}
