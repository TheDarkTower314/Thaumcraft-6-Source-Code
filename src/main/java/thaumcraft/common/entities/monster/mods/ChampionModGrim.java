package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.FXDispatcher;


public class ChampionModGrim implements IChampionModifierEffect
{
    @Override
    public float performEffect(EntityLivingBase boss, EntityLivingBase target, DamageSource source, float amount) {
        if (boss.world.rand.nextFloat() < 0.4f) {
            target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
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
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getEntityBoundingBox().minX + w, boss.getEntityBoundingBox().minY + h, boss.getEntityBoundingBox().minZ + d, 0.0, -0.02, 0.0, boss.world.rand.nextFloat() * 0.2f, boss.world.rand.nextFloat() * 0.2f, boss.world.rand.nextFloat() * 0.2f, 0.8f, false, 640, 10, 1, 8 + boss.world.rand.nextInt(4), 0, 0.6f + boss.world.rand.nextFloat() * 0.4f, 0.0f, 0);
    }
    
    @Override
    public void preRender(EntityLivingBase boss, RenderLivingBase renderLivingBase) {
        GL11.glColor4f(0.6f, 0.6f, 0.6f, 1.0f);
    }
}
