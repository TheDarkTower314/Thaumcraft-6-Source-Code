// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.monster.mods;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.entity.Entity;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

public class ChampionModInfested implements IChampionModifierEffect
{
    @Override
    public float performEffect(EntityLivingBase boss, EntityLivingBase target, DamageSource source, float amount) {
        if (boss.world.rand.nextFloat() < 0.4f && !boss.world.isRemote) {
            EntityTaintCrawler spiderling = new EntityTaintCrawler(boss.world);
            spiderling.setLocationAndAngles(boss.posX, boss.posY + boss.height / 2.0f, boss.posZ, boss.world.rand.nextFloat() * 360.0f, 0.0f);
            boss.world.spawnEntity(spiderling);
            boss.playSound(SoundsTC.gore, 0.5f, 1.0f);
        }
        return amount;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void showFX(EntityLivingBase boss) {
        if (boss.world.rand.nextBoolean()) {
            FXDispatcher.INSTANCE.slimeJumpFX(boss, 0);
        }
    }
    
    @Override
    public void preRender(EntityLivingBase boss, RenderLivingBase renderLivingBase) {
    }
}
