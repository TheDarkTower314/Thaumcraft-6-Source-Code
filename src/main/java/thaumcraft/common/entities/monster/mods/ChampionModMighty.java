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

public class ChampionModMighty implements IChampionModifierEffect
{
    @Override
    public float performEffect(final EntityLivingBase boss, final EntityLivingBase target, final DamageSource source, final float ammount) {
        return 0.0f;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void showFX(final EntityLivingBase boss) {
        if (boss.world.rand.nextFloat() > 0.3f) {
            return;
        }
        final float w = boss.world.rand.nextFloat() * boss.width;
        final float d = boss.world.rand.nextFloat() * boss.width;
        final float h = boss.world.rand.nextFloat() * boss.height;
        final int p = 704 + boss.world.rand.nextInt(4) * 3;
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getEntityBoundingBox().minX + w, boss.getEntityBoundingBox().minY + h, boss.getEntityBoundingBox().minZ + d, 0.0, 0.0, 0.0, 0.8f + boss.world.rand.nextFloat() * 0.2f, 0.8f + boss.world.rand.nextFloat() * 0.2f, 0.8f + boss.world.rand.nextFloat() * 0.2f, 0.7f, false, p, 3, 1, 4 + boss.world.rand.nextInt(3), 0, 1.0f + boss.world.rand.nextFloat() * 0.3f, 0.0f, 0);
    }
    
    @Override
    public void preRender(final EntityLivingBase boss, final RenderLivingBase renderLivingBase) {
    }
}
