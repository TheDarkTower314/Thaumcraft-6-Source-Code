// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.nbt.NBTTagCompound;
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
        this.xp = 0;
        this.xpMax = 2000;
        this.eatDelay = 0;
        this.lastsigh = System.currentTimeMillis() + 1500L;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        this.xp = nbttagcompound.getInteger("XP");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("XP", this.xp);
        return nbttagcompound;
    }
    
    @Override
    public void update() {
        Entity entity = null;
        if (this.xp > this.xpMax) {
            this.xp = this.xpMax;
        }
        if (this.xp < this.xpMax) {
            entity = this.getClosestXPOrb();
            if (entity != null && this.eatDelay == 0) {
                final double var3 = (this.pos.getX() + 0.5 - entity.posX) / 25.0;
                final double var4 = (this.pos.getY() + 0.5 - entity.posY) / 25.0;
                final double var5 = (this.pos.getZ() + 0.5 - entity.posZ) / 25.0;
                final double var6 = Math.sqrt(var3 * var3 + var4 * var4 + var5 * var5);
                double var7 = 1.0 - var6;
                if (var7 > 0.0) {
                    var7 *= var7;
                    final Entity entity2 = entity;
                    entity2.motionX += var3 / var6 * var7 * 0.3;
                    final Entity entity3 = entity;
                    entity3.motionY += var4 / var6 * var7 * 0.5;
                    final Entity entity4 = entity;
                    entity4.motionZ += var5 / var6 * var7 * 0.3;
                }
            }
        }
        if (this.world.isRemote) {
            this.rotb = this.rota;
            if (entity == null) {
                entity = this.world.getClosestPlayer(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, 6.0, false);
                if (entity != null && this.lastsigh < System.currentTimeMillis()) {
                    this.world.playSound(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, SoundsTC.brain, SoundCategory.AMBIENT, 0.15f, 0.8f + this.world.rand.nextFloat() * 0.4f, false);
                    this.lastsigh = System.currentTimeMillis() + 5000L + this.world.rand.nextInt(25000);
                }
            }
            if (entity != null) {
                final double d = entity.posX - (this.pos.getX() + 0.5f);
                final double d2 = entity.posZ - (this.pos.getZ() + 0.5f);
                this.field_40066_q = (float)Math.atan2(d2, d);
                this.field_40059_f += 0.1f;
                if (this.field_40059_f < 0.5f || TileJarBrain.rand.nextInt(40) == 0) {
                    final float f3 = this.field_40061_d;
                    do {
                        this.field_40061_d += TileJarBrain.rand.nextInt(4) - TileJarBrain.rand.nextInt(4);
                    } while (f3 == this.field_40061_d);
                }
            }
            else {
                this.field_40066_q += 0.01f;
            }
            while (this.rota >= 3.141593f) {
                this.rota -= 6.283185f;
            }
            while (this.rota < -3.141593f) {
                this.rota += 6.283185f;
            }
            while (this.field_40066_q >= 3.141593f) {
                this.field_40066_q -= 6.283185f;
            }
            while (this.field_40066_q < -3.141593f) {
                this.field_40066_q += 6.283185f;
            }
            float f4;
            for (f4 = this.field_40066_q - this.rota; f4 >= 3.141593f; f4 -= 6.283185f) {}
            while (f4 < -3.141593f) {
                f4 += 6.283185f;
            }
            this.rota += f4 * 0.04f;
        }
        if (this.eatDelay > 0) {
            --this.eatDelay;
        }
        else if (this.xp < this.xpMax) {
            final List ents = this.world.getEntitiesWithinAABB((Class)EntityXPOrb.class, new AxisAlignedBB(this.pos.getX() - 0.1, this.pos.getY() - 0.1, this.pos.getZ() - 0.1, this.pos.getX() + 1.1, this.pos.getY() + 1.1, this.pos.getZ() + 1.1));
            if (ents.size() > 0) {
                for (final Object ent : ents) {
                    final EntityXPOrb eo = (EntityXPOrb)ent;
                    this.xp += eo.getXpValue();
                    eo.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.1f, (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2f + 1.0f);
                    eo.setDead();
                }
                this.syncTile(false);
                this.markDirty();
            }
        }
    }
    
    public Entity getClosestXPOrb() {
        double cdist = Double.MAX_VALUE;
        Entity orb = null;
        final List ents = this.world.getEntitiesWithinAABB((Class)EntityXPOrb.class, new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(8.0, 8.0, 8.0));
        if (ents.size() > 0) {
            for (final Object ent : ents) {
                final EntityXPOrb eo = (EntityXPOrb)ent;
                final double d = this.getDistanceSq(eo.posX, eo.posY, eo.posZ);
                if (d < cdist) {
                    orb = eo;
                    cdist = d;
                }
            }
        }
        return orb;
    }
}
