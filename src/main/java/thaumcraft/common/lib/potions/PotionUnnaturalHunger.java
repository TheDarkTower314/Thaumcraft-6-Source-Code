// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.potions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.Potion;

public class PotionUnnaturalHunger extends Potion
{
    public static Potion instance;
    private int statusIconIndex;
    static final ResourceLocation rl;
    
    public PotionUnnaturalHunger(final boolean par2, final int par3) {
        super(par2, par3);
        this.statusIconIndex = -1;
        this.setIconIndex(0, 0);
        this.setPotionName("potion.unhunger");
        this.setIconIndex(7, 1);
        this.setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(PotionUnnaturalHunger.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(final EntityLivingBase target, final int par2) {
        if (!target.world.isRemote && target instanceof EntityPlayer) {
            ((EntityPlayer)target).addExhaustion(0.025f * (par2 + 1));
        }
    }
    
    public boolean isReady(final int par1, final int par2) {
        return true;
    }
    
    static {
        PotionUnnaturalHunger.instance = null;
        rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");
    }
}
