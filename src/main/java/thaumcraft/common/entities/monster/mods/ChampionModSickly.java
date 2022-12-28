package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;


public class ChampionModSickly implements IChampionModifierEffect
{
    @Override
    public float performEffect(EntityLivingBase boss, EntityLivingBase target, DamageSource source, float amount) {
        if (boss.world.rand.nextFloat() < 0.4f) {
            target.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 500));
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
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getEntityBoundingBox().minX + w, boss.getEntityBoundingBox().minY + h, boss.getEntityBoundingBox().minZ + d, 0.0, -0.02, 0.0, 0.2f, 0.6f + boss.world.rand.nextFloat() * 0.1f, 0.2f + boss.world.rand.nextFloat() * 0.1f, 0.5f, false, 1, 4, 2, 5 + boss.world.rand.nextInt(4), 0, 0.9f + boss.world.rand.nextFloat() * 0.3f, 0.0f, 0);
    }
    
    @Override
    public void preRender(EntityLivingBase boss, RenderLivingBase renderLivingBase) {
    }
}
