// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.projectile;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.entity.projectile.EntityArrow;

public class EntityGolemDart extends EntityArrow
{
    public EntityGolemDart(World par1World) {
        super(par1World);
        setSize(0.2f, 0.2f);
    }
    
    public EntityGolemDart(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        setSize(0.2f, 0.2f);
    }
    
    public EntityGolemDart(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        setSize(0.2f, 0.2f);
    }
    
    protected ItemStack getArrowStack() {
        return new ItemStack(Items.ARROW);
    }
}
