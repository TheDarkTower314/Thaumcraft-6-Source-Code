package thaumcraft.common.tiles.devices;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.essentia.TileJar;


public class TileJarBrain extends TileJar
{
    public float field_40063_b;
    public float field_40061_d;
    public float field_40059_f;
    public float field_40066_q;
    public float rota;
    public float rotb;
    public int xp;
    public int xpMax;
    public int eatDelay;
    long lastsigh;
    
    public TileJarBrain() {
        xp = 0;
        xpMax = 2000;
        eatDelay = 0;
        lastsigh = System.currentTimeMillis() + 1500L;
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        xp = nbttagcompound.getInteger("XP");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("XP", xp);
        return nbttagcompound;
    }
    
    @Override
    public void update() {
        Entity entity = null;
        if (xp > xpMax) {
            xp = xpMax;
        }
        if (xp < xpMax) {
            entity = getClosestXPOrb();
            if (entity != null && eatDelay == 0) {
                double var3 = (pos.getX() + 0.5 - entity.posX) / 25.0;
                double var4 = (pos.getY() + 0.5 - entity.posY) / 25.0;
                double var5 = (pos.getZ() + 0.5 - entity.posZ) / 25.0;
                double var6 = Math.sqrt(var3 * var3 + var4 * var4 + var5 * var5);
                double var7 = 1.0 - var6;
                if (var7 > 0.0) {
                    var7 *= var7;
                    Entity entity2 = entity;
                    entity2.motionX += var3 / var6 * var7 * 0.3;
                    Entity entity3 = entity;
                    entity3.motionY += var4 / var6 * var7 * 0.5;
                    Entity entity4 = entity;
                    entity4.motionZ += var5 / var6 * var7 * 0.3;
                }
            }
        }
        if (world.isRemote) {
            rotb = rota;
            if (entity == null) {
                entity = world.getClosestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6.0, false);
                if (entity != null && lastsigh < System.currentTimeMillis()) {
                    world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.brain, SoundCategory.AMBIENT, 0.15f, 0.8f + world.rand.nextFloat() * 0.4f, false);
                    lastsigh = System.currentTimeMillis() + 5000L + world.rand.nextInt(25000);
                }
            }
            if (entity != null) {
                double d = entity.posX - (pos.getX() + 0.5f);
                double d2 = entity.posZ - (pos.getZ() + 0.5f);
                field_40066_q = (float)Math.atan2(d2, d);
                field_40059_f += 0.1f;
                if (field_40059_f < 0.5f || TileJarBrain.rand.nextInt(40) == 0) {
                    float f3 = field_40061_d;
                    do {
                        field_40061_d += TileJarBrain.rand.nextInt(4) - TileJarBrain.rand.nextInt(4);
                    } while (f3 == field_40061_d);
                }
            }
            else {
                field_40066_q += 0.01f;
            }
            while (rota >= 3.141593f) {
                rota -= 6.283185f;
            }
            while (rota < -3.141593f) {
                rota += 6.283185f;
            }
            while (field_40066_q >= 3.141593f) {
                field_40066_q -= 6.283185f;
            }
            while (field_40066_q < -3.141593f) {
                field_40066_q += 6.283185f;
            }
            float f4;
            for (f4 = field_40066_q - rota; f4 >= 3.141593f; f4 -= 6.283185f) {}
            while (f4 < -3.141593f) {
                f4 += 6.283185f;
            }
            rota += f4 * 0.04f;
        }
        if (eatDelay > 0) {
            --eatDelay;
        }
        else if (xp < xpMax) {
            List ents = world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(pos.getX() - 0.1, pos.getY() - 0.1, pos.getZ() - 0.1, pos.getX() + 1.1, pos.getY() + 1.1, pos.getZ() + 1.1));
            if (ents.size() > 0) {
                for (Object ent : ents) {
                    EntityXPOrb eo = (EntityXPOrb)ent;
                    xp += eo.getXpValue();
                    eo.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.1f, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f + 1.0f);
                    eo.setDead();
                }
                syncTile(false);
                markDirty();
            }
        }
    }
    
    public Entity getClosestXPOrb() {
        double cdist = Double.MAX_VALUE;
        Entity orb = null;
        List ents = world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(8.0, 8.0, 8.0));
        if (ents.size() > 0) {
            for (Object ent : ents) {
                EntityXPOrb eo = (EntityXPOrb)ent;
                double d = getDistanceSq(eo.posX, eo.posY, eo.posZ);
                if (d < cdist) {
                    orb = eo;
                    cdist = d;
                }
            }
        }
        return orb;
    }
}
