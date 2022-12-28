package thaumcraft.common.lib;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.entities.projectile.EntityAlumentum;


public class BehaviorDispenseAlumetum extends BehaviorProjectileDispense
{
    protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
        return new EntityAlumentum(worldIn, position.getX(), position.getY(), position.getZ());
    }
}
