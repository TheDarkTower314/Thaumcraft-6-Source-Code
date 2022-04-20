// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities;

import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.item.EntityItem;

public class EntitySpecialItem extends EntityItem
{
    public EntitySpecialItem(final World par1World, final double par2, final double par4, final double par6, final ItemStack par8ItemStack) {
        super(par1World);
        this.setSize(0.25f, 0.25f);
        this.setPosition(par2, par4, par6);
        this.setItem(par8ItemStack);
        this.rotationYaw = (float)(Math.random() * 360.0);
        this.motionX = (float)(Math.random() * 0.20000000298023224 - 0.10000000149011612);
        this.motionY = 0.20000000298023224;
        this.motionZ = (float)(Math.random() * 0.20000000298023224 - 0.10000000149011612);
    }
    
    public EntitySpecialItem(final World par1World) {
        super(par1World);
        this.setSize(0.25f, 0.25f);
    }
    
    public void onUpdate() {
        if (this.ticksExisted > 1) {
            if (this.motionY > 0.0) {
                this.motionY *= 0.8999999761581421;
            }
            this.motionY += 0.03999999910593033;
            super.onUpdate();
        }
    }
    
    public boolean attackEntityFrom(final DamageSource source, final float damage) {
        return !source.isExplosion() && super.attackEntityFrom(source, damage);
    }
}
