// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.projectile;

import net.minecraft.util.math.BlockPos;
import java.util.Iterator;
import java.util.List;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.world.IBlockAccess;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.entities.ITaintedMob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.entity.projectile.EntityThrowable;

public class EntityBottleTaint extends EntityThrowable
{
    public EntityBottleTaint(final World p_i1788_1_) {
        super(p_i1788_1_);
    }
    
    public EntityBottleTaint(final World p_i1790_1_, final EntityLivingBase p_i1790_2) {
        super(p_i1790_1_, p_i1790_2);
    }
    
    public EntityBottleTaint(final World worldIn, final double x, final double y, final double z) {
        super(worldIn, x, y, z);
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte id) {
        if (id == 3) {
            for (int a = 0; a < 100; ++a) {
                FXDispatcher.INSTANCE.taintsplosionFX(this);
            }
            FXDispatcher.INSTANCE.bottleTaintBreak(this.posX, this.posY, this.posZ);
        }
    }
    
    protected void onImpact(final RayTraceResult ray) {
        if (!this.world.isRemote) {
            final List ents = this.world.getEntitiesWithinAABB((Class)EntityLivingBase.class, new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ).grow(5.0, 5.0, 5.0));
            if (ents.size() > 0) {
                for (final Object ent : ents) {
                    final EntityLivingBase el = (EntityLivingBase)ent;
                    if (!(el instanceof ITaintedMob) && !el.isEntityUndead()) {
                        el.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 100, 0, false, true));
                    }
                }
            }
            for (int a = 0; a < 10; ++a) {
                final int xx = (int)((this.rand.nextFloat() - this.rand.nextFloat()) * 4.0f);
                final int zz = (int)((this.rand.nextFloat() - this.rand.nextFloat()) * 4.0f);
                BlockPos p = this.getPosition().add(xx, 0, zz);
                if (this.world.rand.nextBoolean()) {
                    if (this.world.isBlockNormalCube(p.down(), false) && this.world.getBlockState(p).getBlock().isReplaceable(this.world, p)) {
                        this.world.setBlockState(p, BlocksTC.fluxGoo.getDefaultState());
                    }
                    else {
                        p = p.down();
                        if (this.world.isBlockNormalCube(p.down(), false) && this.world.getBlockState(p).getBlock().isReplaceable(this.world, p)) {
                            this.world.setBlockState(p, BlocksTC.fluxGoo.getDefaultState());
                        }
                    }
                }
            }
            this.world.setEntityState(this, (byte)3);
            this.setDead();
        }
    }
}
