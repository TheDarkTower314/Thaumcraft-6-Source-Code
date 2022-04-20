// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.potions;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.Potion;

public class PotionSunScorned extends Potion
{
    public static Potion instance;
    private int statusIconIndex;
    static final ResourceLocation rl;
    
    public PotionSunScorned(final boolean par2, final int par3) {
        super(par2, par3);
        this.statusIconIndex = -1;
        this.setIconIndex(0, 0);
        this.setPotionName("potion.sunscorned");
        this.setIconIndex(6, 2);
        this.setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(PotionSunScorned.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(final EntityLivingBase target, final int par2) {
        if (!target.world.isRemote) {
            final float f = target.getBrightness();
            if (f > 0.5f && target.world.rand.nextFloat() * 30.0f < (f - 0.4f) * 2.0f && target.world.canBlockSeeSky(new BlockPos(MathHelper.floor(target.posX), MathHelper.floor(target.posY), MathHelper.floor(target.posZ)))) {
                target.setFire(4);
            }
            else if (f < 0.25f && target.world.rand.nextFloat() > f * 2.0f) {
                target.heal(1.0f);
            }
        }
    }
    
    public boolean isReady(final int par1, final int par2) {
        return par1 % 40 == 0;
    }
    
    static {
        PotionSunScorned.instance = null;
        rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");
    }
}
