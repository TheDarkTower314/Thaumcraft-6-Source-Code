package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public interface IChampionModifierEffect
{
    float performEffect(EntityLivingBase p0, EntityLivingBase p1, DamageSource p2, float p3);
    
    @SideOnly(Side.CLIENT)
    void showFX(EntityLivingBase p0);
    
    void preRender(EntityLivingBase p0, RenderLivingBase p1);
}
