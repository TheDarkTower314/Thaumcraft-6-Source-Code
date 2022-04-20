// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.mods;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

public class ChampionModDummy implements IChampionModifierEffect
{
    @Override
    public float performEffect(final EntityLivingBase boss, final EntityLivingBase target, final DamageSource source, final float amount) {
        return amount;
    }
    
    @Override
    public void showFX(final EntityLivingBase boss) {
    }
    
    @Override
    public void preRender(final EntityLivingBase boss, final RenderLivingBase renderLivingBase) {
    }
}
