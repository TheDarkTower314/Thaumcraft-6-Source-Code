// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.projectile;

import java.util.Iterator;
import java.util.List;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.entities.EntityFluxRift;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.entity.projectile.EntityThrowable;

public class EntityCausalityCollapser extends EntityThrowable
{
    public EntityCausalityCollapser(final World par1World) {
        super(par1World);
    }
    
    public EntityCausalityCollapser(final World par1World, final EntityLivingBase par2EntityLiving) {
        super(par1World, par2EntityLiving);
    }
    
    public EntityCausalityCollapser(final World par1World, final double par2, final double par4, final double par6) {
        super(par1World, par2, par4, par6);
    }
    
    public void shoot(final double x, final double y, final double z, final float velocity, final float inaccuracy) {
        super.shoot(x, y, z, 0.8f, inaccuracy);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.world.isRemote) {
            for (double i = 0.0; i < 3.0; ++i) {
                final double coeff = i / 3.0;
                FXDispatcher.INSTANCE.drawAlumentum((float)(this.prevPosX + (this.posX - this.prevPosX) * coeff), (float)(this.prevPosY + (this.posY - this.prevPosY) * coeff) + this.height / 2.0f, (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * coeff), 0.0125f * (this.rand.nextFloat() - 0.5f), 0.0125f * (this.rand.nextFloat() - 0.5f), 0.0125f * (this.rand.nextFloat() - 0.5f), 0.8f + this.rand.nextFloat() * 0.2f, 0.3f + this.rand.nextFloat() * 0.1f, this.rand.nextFloat() * 0.1f, 0.5f, 4.0f);
                FXDispatcher.INSTANCE.drawGenericParticles(this.posX + this.world.rand.nextGaussian() * 0.20000000298023224, this.posY + this.world.rand.nextGaussian() * 0.20000000298023224, this.posZ + this.world.rand.nextGaussian() * 0.20000000298023224, 0.0, 0.0, 0.0, 1.0f, 1.0f, 1.0f, 0.7f, false, 448, 8, 1, 8, 0, 0.3f, 0.0f, 1);
            }
        }
    }
    
    protected void onImpact(final RayTraceResult par1RayTraceResult) {
        if (!this.world.isRemote) {
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, 2.0f, true);
            final List<EntityFluxRift> list = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityFluxRift.class, 3.0);
            for (final EntityFluxRift fr : list) {
                fr.setCollapse(true);
            }
            this.setDead();
        }
    }
}
