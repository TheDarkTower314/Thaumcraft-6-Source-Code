// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityMoveHelper;

public class FlightMoveHelper extends EntityMoveHelper
{
    private static final String __OBFID = "CL_00002209";
    
    public FlightMoveHelper(final EntityLiving entity) {
        super(entity);
    }
    
    public void onUpdateMoveHelper() {
        if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.entity.getNavigator().noPath()) {
            this.action = EntityMoveHelper.Action.WAIT;
            final double d0 = this.posX - this.entity.posX;
            double d2 = this.posY - this.entity.posY;
            final double d3 = this.posZ - this.entity.posZ;
            double d4 = d0 * d0 + d2 * d2 + d3 * d3;
            d4 = MathHelper.sqrt(d4);
            d2 /= d4;
            final float f = (float)(Math.atan2(d3, d0) * 180.0 / 3.141592653589793) - 90.0f;
            this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f, 30.0f);
            this.entity.renderYawOffset = this.entity.rotationYaw;
            final float f2 = (float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
            this.entity.setAIMoveSpeed(this.entity.getAIMoveSpeed() + (f2 - this.entity.getAIMoveSpeed()) * 0.125f);
            double d5 = Math.sin((this.entity.ticksExisted + this.entity.getEntityId()) * 0.5) * 0.05;
            final double d6 = Math.cos(this.entity.rotationYaw * 3.1415927f / 180.0f);
            final double d7 = Math.sin(this.entity.rotationYaw * 3.1415927f / 180.0f);
            final EntityLiving entity = this.entity;
            entity.motionX += d5 * d6;
            final EntityLiving entity2 = this.entity;
            entity2.motionZ += d5 * d7;
            d5 = Math.sin((this.entity.ticksExisted + this.entity.getEntityId()) * 0.75) * 0.05;
            final EntityLiving entity3 = this.entity;
            entity3.motionY += d5 * (d7 + d6) * 0.25;
            final EntityLiving entity4 = this.entity;
            entity4.motionY += this.entity.getAIMoveSpeed() * d2 * 0.1;
            final EntityLookHelper entitylookhelper = this.entity.getLookHelper();
            final double d8 = this.entity.posX + d0 / d4 * 2.0;
            final double d9 = this.entity.getEyeHeight() + this.entity.posY + d2 / d4 * 1.0;
            final double d10 = this.entity.posZ + d3 / d4 * 2.0;
            double d11 = entitylookhelper.getLookPosX();
            double d12 = entitylookhelper.getLookPosY();
            double d13 = entitylookhelper.getLookPosZ();
            if (!entitylookhelper.getIsLooking()) {
                d11 = d8;
                d12 = d9;
                d13 = d10;
            }
            this.entity.getLookHelper().setLookPosition(d11 + (d8 - d11) * 0.125, d12 + (d9 - d12) * 0.125, d13 + (d10 - d13) * 0.125, 10.0f, 40.0f);
        }
        else {
            this.entity.setAIMoveSpeed(0.0f);
        }
    }
}
