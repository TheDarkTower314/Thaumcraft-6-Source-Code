package thaumcraft.common.lib.potions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class PotionWarpWard extends Potion
{
    public static Potion instance;
    private int statusIconIndex;
    static ResourceLocation rl;
    
    public PotionWarpWard(boolean par2, int par3) {
        super(par2, par3);
        statusIconIndex = -1;
        setIconIndex(0, 0);
        setPotionName("potion.warpward");
        setIconIndex(3, 2);
        setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(PotionWarpWard.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(EntityLivingBase target, int par2) {
    }
    
    static {
        PotionWarpWard.instance = null;
        rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");
    }
}
