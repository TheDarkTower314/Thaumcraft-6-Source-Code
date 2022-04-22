// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib;

import thaumcraft.common.entities.projectile.EntityAlumentum;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.dispenser.IPosition;
import net.minecraft.world.World;
import net.minecraft.dispenser.BehaviorProjectileDispense;

public class BehaviorDispenseAlumetum extends BehaviorProjectileDispense
{
    protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
        return new EntityAlumentum(worldIn, position.getX(), position.getY(), position.getZ());
    }
}
