package thaumcraft.common.lib.potions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class PotionBlurredVision extends Potion
{
    public static Potion instance;
    private int statusIconIndex;
    static ResourceLocation rl;
    
    public PotionBlurredVision(boolean par2, int par3) {
        super(par2, par3);
        statusIconIndex = -1;
        setIconIndex(0, 0);
        setPotionName("potion.blurred");
        setIconIndex(5, 2);
        setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(PotionBlurredVision.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(EntityLivingBase target, int par2) {
    }
    
    static {
        PotionBlurredVision.instance = null;
        rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");
    }
}
