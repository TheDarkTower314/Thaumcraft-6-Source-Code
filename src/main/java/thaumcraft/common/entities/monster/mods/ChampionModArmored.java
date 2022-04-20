// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.mods;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

public class ChampionModArmored implements IChampionModifierEffect
{
    @Override
    public float performEffect(final EntityLivingBase mob, final EntityLivingBase target, final DamageSource source, float amount) {
        if (!source.isUnblockable()) {
            final float f1 = amount * 19.0f;
            amount = f1 / 25.0f;
        }
        return amount;
    }
    
    @Override
    public void showFX(final EntityLivingBase boss) {
        if (boss.world.rand.nextInt(4) != 0) {
            return;
        }
        final float w = boss.world.rand.nextFloat() * boss.width;
        final float d = boss.world.rand.nextFloat() * boss.width;
        final float h = boss.world.rand.nextFloat() * boss.height;
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getEntityBoundingBox().minX + w, boss.getEntityBoundingBox().minY + h, boss.getEntityBoundingBox().minZ + d, 0.0, 0.0, 0.0, 0.9f, 0.9f, 0.9f + boss.world.rand.nextFloat() * 0.1f, 0.7f, false, 448, 9, 1, 5 + boss.world.rand.nextInt(4), 0, 0.6f + boss.world.rand.nextFloat() * 0.2f, 0.0f, 0);
    }
    
    @Override
    public void preRender(final EntityLivingBase boss, final RenderLivingBase renderLivingBase) {
    }
}
