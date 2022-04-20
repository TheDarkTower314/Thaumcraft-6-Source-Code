// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.mods;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

public interface IChampionModifierEffect
{
    float performEffect(final EntityLivingBase p0, final EntityLivingBase p1, final DamageSource p2, final float p3);
    
    @SideOnly(Side.CLIENT)
    void showFX(final EntityLivingBase p0);
    
    void preRender(final EntityLivingBase p0, final RenderLivingBase p1);
}
