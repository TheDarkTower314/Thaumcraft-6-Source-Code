package thaumcraft.common.entities.projectile;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.client.fx.FXDispatcher;


public class EntityBottleTaint extends EntityThrowable
{
    public EntityBottleTaint(World p_i1788_1_) {
        super(p_i1788_1_);
    }
    
    public EntityBottleTaint(World p_i1790_1_, EntityLivingBase p_i1790_2) {
        super(p_i1790_1_, p_i1790_2);
    }
    
    public EntityBottleTaint(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for (int a = 0; a < 100; ++a) {
                FXDispatcher.INSTANCE.taintsplosionFX(this);
            }
            FXDispatcher.INSTANCE.bottleTaintBreak(posX, posY, posZ);
        }
    }
    
    protected void onImpact(RayTraceResult ray) {
        if (!world.isRemote) {
            List ents = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(posX, posY, posZ, posX, posY, posZ).grow(5.0, 5.0, 5.0));
            if (ents.size() > 0) {
                for (Object ent : ents) {
                    EntityLivingBase el = (EntityLivingBase)ent;
                    if (!(el instanceof ITaintedMob) && !el.isEntityUndead()) {
                        el.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 100, 0, false, true));
                    }
                }
            }
            for (int a = 0; a < 10; ++a) {
                int xx = (int)((rand.nextFloat() - rand.nextFloat()) * 4.0f);
                int zz = (int)((rand.nextFloat() - rand.nextFloat()) * 4.0f);
                BlockPos p = getPosition().add(xx, 0, zz);
                if (world.rand.nextBoolean()) {
                    if (world.isBlockNormalCube(p.down(), false) && world.getBlockState(p).getBlock().isReplaceable(world, p)) {
                        world.setBlockState(p, BlocksTC.fluxGoo.getDefaultState());
                    }
                    else {
                        p = p.down();
                        if (world.isBlockNormalCube(p.down(), false) && world.getBlockState(p).getBlock().isReplaceable(world, p)) {
                            world.setBlockState(p, BlocksTC.fluxGoo.getDefaultState());
                        }
                    }
                }
            }
            world.setEntityState(this, (byte)3);
            setDead();
        }
    }
}
