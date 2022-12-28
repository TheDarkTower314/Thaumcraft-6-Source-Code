package thaumcraft.common.entities;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;


public class EntitySpecialItem extends EntityItem
{
    public EntitySpecialItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack) {
        super(par1World);
        setSize(0.25f, 0.25f);
        setPosition(par2, par4, par6);
        setItem(par8ItemStack);
        rotationYaw = (float)(Math.random() * 360.0);
        motionX = (float)(Math.random() * 0.20000000298023224 - 0.10000000149011612);
        motionY = 0.20000000298023224;
        motionZ = (float)(Math.random() * 0.20000000298023224 - 0.10000000149011612);
    }
    
    public EntitySpecialItem(World par1World) {
        super(par1World);
        setSize(0.25f, 0.25f);
    }
    
    public void onUpdate() {
        if (ticksExisted > 1) {
            if (motionY > 0.0) {
                motionY *= 0.8999999761581421;
            }
            motionY += 0.03999999910593033;
            super.onUpdate();
        }
    }
    
    public boolean attackEntityFrom(DamageSource source, float damage) {
        return !source.isExplosion() && super.attackEntityFrom(source, damage);
    }
}
