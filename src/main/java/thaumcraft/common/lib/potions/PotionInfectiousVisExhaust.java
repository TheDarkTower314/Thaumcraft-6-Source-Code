package thaumcraft.common.lib.potions;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.potions.PotionVisExhaust;


public class PotionInfectiousVisExhaust extends Potion
{
    public static Potion instance;
    private int statusIconIndex;
    static ResourceLocation rl;
    
    public PotionInfectiousVisExhaust(boolean par2, int par3) {
        super(par2, par3);
        statusIconIndex = -1;
        setIconIndex(0, 0);
        setPotionName("potion.infvisexhaust");
        setIconIndex(6, 1);
        setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(PotionInfectiousVisExhaust.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(EntityLivingBase target, int par2) {
        List<EntityLivingBase> targets = target.world.getEntitiesWithinAABB(EntityLivingBase.class, target.getEntityBoundingBox().grow(4.0, 4.0, 4.0));
        if (targets.size() > 0) {
            for (EntityLivingBase e : targets) {
                if (!e.isPotionActive(PotionInfectiousVisExhaust.instance)) {
                    if (par2 > 0) {
                        e.addPotionEffect(new PotionEffect(PotionInfectiousVisExhaust.instance, 6000, par2 - 1, false, true));
                    }
                    else {
                        e.addPotionEffect(new PotionEffect(PotionVisExhaust.instance, 6000, 0, false, true));
                    }
                }
            }
        }
    }
    
    public boolean isReady(int par1, int par2) {
        return par1 % 40 == 0;
    }
    
    static {
        PotionInfectiousVisExhaust.instance = null;
        rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");
    }
}
