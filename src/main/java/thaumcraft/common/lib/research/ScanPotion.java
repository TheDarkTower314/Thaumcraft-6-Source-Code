// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.research;

import net.minecraft.item.ItemStack;
import java.util.Iterator;
import net.minecraft.potion.PotionUtils;
import thaumcraft.api.research.ScanningManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import thaumcraft.api.research.IScanThing;

public class ScanPotion implements IScanThing
{
    Potion potion;
    
    public ScanPotion(final Potion potion) {
        this.potion = potion;
    }
    
    @Override
    public boolean checkThing(final EntityPlayer player, final Object obj) {
        return this.getPotionEffect(player, obj) != null;
    }
    
    private PotionEffect getPotionEffect(final EntityPlayer player, final Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof EntityLivingBase) {
            final EntityLivingBase e = (EntityLivingBase)obj;
            for (final PotionEffect potioneffect : e.getActivePotionEffects()) {
                if (potioneffect.getPotion() == this.potion) {
                    return potioneffect;
                }
            }
        }
        else {
            final ItemStack is = ScanningManager.getItemFromParms(player, obj);
            if (is != null && !is.isEmpty()) {
                for (final PotionEffect potioneffect : PotionUtils.getEffectsFromStack(is)) {
                    if (potioneffect.getPotion() == this.potion) {
                        return potioneffect;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public String getResearchKey(final EntityPlayer player, final Object obj) {
        return "!" + this.potion.getName();
    }
}
