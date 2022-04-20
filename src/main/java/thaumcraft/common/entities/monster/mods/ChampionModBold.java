// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.mods;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

public class ChampionModBold implements IChampionModifierEffect
{
    @Override
    public float performEffect(final EntityLivingBase boss, final EntityLivingBase target, final DamageSource source, final float ammount) {
        return 0.0f;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void showFX(final EntityLivingBase boss) {
        if (boss.world.rand.nextBoolean()) {
            return;
        }
        final float w = boss.world.rand.nextFloat() * boss.width;
        final float d = boss.world.rand.nextFloat() * boss.width;
        final float h = boss.world.rand.nextFloat() * boss.height / 3.0f;
        FXDispatcher.INSTANCE.spark(boss.getEntityBoundingBox().minX + w, boss.getEntityBoundingBox().minY + h, boss.getEntityBoundingBox().minZ + d, 0.2f, 0.3f - boss.world.rand.nextFloat() * 0.1f, 0.0f, 0.8f + boss.world.rand.nextFloat() * 0.2f, 1.0f);
    }
    
    @Override
    public void preRender(final EntityLivingBase boss, final RenderLivingBase renderLivingBase) {
    }
}
