// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityFollowingItem extends EntitySpecialItem implements IEntityAdditionalSpawnData
{
    double targetX;
    double targetY;
    double targetZ;
    int type;
    public Entity target;
    int age;
    public double gravity;
    
    public EntityFollowingItem(final World par1World, final double par2, final double par4, final double par6, final ItemStack par8ItemStack) {
        super(par1World);
        this.targetX = 0.0;
        this.targetY = 0.0;
        this.targetZ = 0.0;
        this.type = 3;
        this.target = null;
        this.age = 20;
        this.gravity = 0.03999999910593033;
        this.setSize(0.25f, 0.25f);
        this.setPosition(par2, par4, par6);
        this.setItem(par8ItemStack);
        this.rotationYaw = (float)(Math.random() * 360.0);
    }
    
    public EntityFollowingItem(final World par1World, final double par2, final double par4, final double par6, final ItemStack par8ItemStack, final Entity target, final int t) {
        this(par1World, par2, par4, par6, par8ItemStack);
        this.target = target;
        this.targetX = target.posX;
        this.targetY = target.getEntityBoundingBox().minY + target.height / 2.0f;
        this.targetZ = target.posZ;
        this.type = t;
        this.noClip = true;
    }
    
    public EntityFollowingItem(final World par1World, final double par2, final double par4, final double par6, final ItemStack par8ItemStack, final double tx, final double ty, final double tz) {
        this(par1World, par2, par4, par6, par8ItemStack);
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
    }
    
    public EntityFollowingItem(final World par1World) {
        super(par1World);
        this.targetX = 0.0;
        this.targetY = 0.0;
        this.targetZ = 0.0;
        this.type = 3;
        this.target = null;
        this.age = 20;
        this.gravity = 0.03999999910593033;
        this.setSize(0.25f, 0.25f);
    }
    
    @Override
    public void onUpdate() {
        if (this.target != null) {
            this.targetX = this.target.posX;
            this.targetY = this.target.getEntityBoundingBox().minY + this.target.height / 2.0f;
            this.targetZ = this.target.posZ;
        }
        if (this.targetX != 0.0 || this.targetY != 0.0 || this.targetZ != 0.0) {
            final float xd = (float)(this.targetX - this.posX);
            final float yd = (float)(this.targetY - this.posY);
            final float zd = (float)(this.targetZ - this.posZ);
            if (this.age > 1) {
                --this.age;
            }
            double distance = MathHelper.sqrt(xd * xd + yd * yd + zd * zd);
            if (distance > 0.5) {
                distance *= this.age;
                this.motionX = xd / distance;
                this.motionY = yd / distance;
                this.motionZ = zd / distance;
            }
            else {
                this.motionX *= 0.10000000149011612;
                this.motionY *= 0.10000000149011612;
                this.motionZ *= 0.10000000149011612;
                this.targetX = 0.0;
                this.targetY = 0.0;
                this.targetZ = 0.0;
                this.target = null;
                this.noClip = false;
            }
            if (this.world.isRemote) {
                final float h = (float)((this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) / 2.0) + MathHelper.sin(this.getAge() / 10.0f + this.hoverStart) * 0.1f + 0.1f;
                if (this.type != 10) {
                    FXDispatcher.INSTANCE.drawNitorCore((float)this.prevPosX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125f, (float)this.prevPosY + h + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125f, (float)this.prevPosZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125f, this.rand.nextGaussian() * 0.009999999776482582, this.rand.nextGaussian() * 0.009999999776482582, this.rand.nextGaussian() * 0.009999999776482582);
                }
                else {
                    FXDispatcher.INSTANCE.crucibleBubble((float)this.prevPosX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125f, (float)this.prevPosY + h + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125f, (float)this.prevPosZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125f, 0.33f, 0.33f, 1.0f);
                }
            }
        }
        else {
            this.motionY -= this.gravity;
        }
        super.onUpdate();
    }
    
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("type", (short)this.type);
    }
    
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.type = par1NBTTagCompound.getShort("type");
    }
    
    public void writeSpawnData(final ByteBuf data) {
        if (this.target != null) {
            data.writeInt((this.target == null) ? -1 : this.target.getEntityId());
            data.writeDouble(this.targetX);
            data.writeDouble(this.targetY);
            data.writeDouble(this.targetZ);
            data.writeByte(this.type);
        }
    }
    
    public void readSpawnData(final ByteBuf data) {
        try {
            final int ent = data.readInt();
            if (ent > -1) {
                this.target = this.world.getEntityByID(ent);
            }
            this.targetX = data.readDouble();
            this.targetY = data.readDouble();
            this.targetZ = data.readDouble();
            this.type = data.readByte();
        }
        catch (final Exception ex) {}
    }
}
