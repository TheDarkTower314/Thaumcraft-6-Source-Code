package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import thaumcraft.client.fx.FXDispatcher;


public class ChampionModArmored implements IChampionModifierEffect
{
    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        if (!source.isUnblockable()) {
            float f1 = amount * 19.0f;
            amount = f1 / 25.0f;
        }
        return amount;
    }
    
    @Override
    public void showFX(EntityLivingBase boss) {
        if (boss.world.rand.nextInt(4) != 0) {
            return;
        }
        float w = boss.world.rand.nextFloat() * boss.width;
        float d = boss.world.rand.nextFloat() * boss.width;
        float h = boss.world.rand.nextFloat() * boss.height;
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getEntityBoundingBox().minX + w, boss.getEntityBoundingBox().minY + h, boss.getEntityBoundingBox().minZ + d, 0.0, 0.0, 0.0, 0.9f, 0.9f, 0.9f + boss.world.rand.nextFloat() * 0.1f, 0.7f, false, 448, 9, 1, 5 + boss.world.rand.nextInt(4), 0, 0.6f + boss.world.rand.nextFloat() * 0.2f, 0.0f, 0);
    }
    
    @Override
    public void preRender(EntityLivingBase boss, RenderLivingBase renderLivingBase) {
    }
}
