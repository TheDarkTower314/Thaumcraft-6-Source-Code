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
    float performEffect(EntityLivingBase p0, EntityLivingBase p1, DamageSource p2, float p3);
    
    @SideOnly(Side.CLIENT)
    void showFX(EntityLivingBase p0);
    
    void preRender(EntityLivingBase p0, RenderLivingBase p1);
}
