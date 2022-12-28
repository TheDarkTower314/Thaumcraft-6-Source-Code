package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;


public class ChampionModWarded implements IChampionModifierEffect
{
    @Override
    public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
        if (mob.hurtResistantTime <= 0 && mob.ticksExisted % 25 == 0) {
            int bh = (int)mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() / 2;
            if (mob.getAbsorptionAmount() < bh) {
                mob.setAbsorptionAmount(mob.getAbsorptionAmount() + 1.0f);
            }
        }
        return amount;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void showFX(EntityLivingBase boss) {
        if (boss.world.rand.nextBoolean()) {
            return;
        }
        float w = boss.world.rand.nextFloat() * boss.width;
        float d = boss.world.rand.nextFloat() * boss.width;
        float h = boss.world.rand.nextFloat() * boss.height;
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getEntityBoundingBox().minX + w, boss.getEntityBoundingBox().minY + h, boss.getEntityBoundingBox().minZ + d, 0.0, 0.0, 0.0, 0.5f + boss.world.rand.nextFloat() * 0.1f, 0.5f + boss.world.rand.nextFloat() * 0.1f, 0.5f + boss.world.rand.nextFloat() * 0.1f, 0.6f, true, 69, 4, 1, 4 + boss.world.rand.nextInt(4), 0, 0.8f + boss.world.rand.nextFloat() * 0.3f, 0.0f, 0);
    }
    
    @Override
    public void preRender(EntityLivingBase boss, RenderLivingBase renderLivingBase) {
    }
}
