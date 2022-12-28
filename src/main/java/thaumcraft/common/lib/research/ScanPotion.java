package thaumcraft.common.lib.research;
import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;


public class ScanPotion implements IScanThing
{
    Potion potion;
    
    public ScanPotion(Potion potion) {
        this.potion = potion;
    }
    
    @Override
    public boolean checkThing(EntityPlayer player, Object obj) {
        return getPotionEffect(player, obj) != null;
    }
    
    private PotionEffect getPotionEffect(EntityPlayer player, Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof EntityLivingBase) {
            EntityLivingBase e = (EntityLivingBase)obj;
            for (PotionEffect potioneffect : e.getActivePotionEffects()) {
                if (potioneffect.getPotion() == potion) {
                    return potioneffect;
                }
            }
        }
        else {
            ItemStack is = ScanningManager.getItemFromParms(player, obj);
            if (is != null && !is.isEmpty()) {
                for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(is)) {
                    if (potioneffect.getPotion() == potion) {
                        return potioneffect;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public String getResearchKey(EntityPlayer player, Object obj) {
        return "!" + potion.getName();
    }
}
