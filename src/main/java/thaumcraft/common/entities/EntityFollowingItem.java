package thaumcraft.common.entities;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.client.fx.FXDispatcher;


public class EntityFollowingItem extends EntitySpecialItem implements IEntityAdditionalSpawnData
{
    double targetX;
    double targetY;
    double targetZ;
    int type;
    public Entity target;
    int age;
    public double gravity;
    
    public EntityFollowingItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack) {
        super(par1World);
        targetX = 0.0;
        targetY = 0.0;
        targetZ = 0.0;
        type = 3;
        target = null;
        age = 20;
        gravity = 0.03999999910593033;
        setSize(0.25f, 0.25f);
        setPosition(par2, par4, par6);
        setItem(par8ItemStack);
        rotationYaw = (float)(Math.random() * 360.0);
    }
    
    public EntityFollowingItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack, Entity target, int t) {
        this(par1World, par2, par4, par6, par8ItemStack);
        this.target = target;
        targetX = target.posX;
        targetY = target.getEntityBoundingBox().minY + target.height / 2.0f;
        targetZ = target.posZ;
        type = t;
        noClip = true;
    }
    
    public EntityFollowingItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack, double tx, double ty, double tz) {
        this(par1World, par2, par4, par6, par8ItemStack);
        targetX = tx;
        targetY = ty;
        targetZ = tz;
    }
    
    public EntityFollowingItem(World par1World) {
        super(par1World);
        targetX = 0.0;
        targetY = 0.0;
        targetZ = 0.0;
        type = 3;
        target = null;
        age = 20;
        gravity = 0.03999999910593033;
        setSize(0.25f, 0.25f);
    }
    
    @Override
    public void onUpdate() {
        if (target != null) {
            targetX = target.posX;
            targetY = target.getEntityBoundingBox().minY + target.height / 2.0f;
            targetZ = target.posZ;
        }
        if (targetX != 0.0 || targetY != 0.0 || targetZ != 0.0) {
            float xd = (float)(targetX - posX);
            float yd = (float)(targetY - posY);
            float zd = (float)(targetZ - posZ);
            if (age > 1) {
                --age;
            }
            double distance = MathHelper.sqrt(xd * xd + yd * yd + zd * zd);
            if (distance > 0.5) {
                distance *= age;
                motionX = xd / distance;
                motionY = yd / distance;
                motionZ = zd / distance;
            }
            else {
                motionX *= 0.10000000149011612;
                motionY *= 0.10000000149011612;
                motionZ *= 0.10000000149011612;
                targetX = 0.0;
                targetY = 0.0;
                targetZ = 0.0;
                target = null;
                noClip = false;
            }
            if (world.isRemote) {
                float h = (float)((getEntityBoundingBox().maxY - getEntityBoundingBox().minY) / 2.0) + MathHelper.sin(getAge() / 10.0f + hoverStart) * 0.1f + 0.1f;
                if (type != 10) {
                    FXDispatcher.INSTANCE.drawNitorCore((float) prevPosX + (rand.nextFloat() - rand.nextFloat()) * 0.125f, (float) prevPosY + h + (rand.nextFloat() - rand.nextFloat()) * 0.125f, (float) prevPosZ + (rand.nextFloat() - rand.nextFloat()) * 0.125f, rand.nextGaussian() * 0.009999999776482582, rand.nextGaussian() * 0.009999999776482582, rand.nextGaussian() * 0.009999999776482582);
                }
                else {
                    FXDispatcher.INSTANCE.crucibleBubble((float) prevPosX + (rand.nextFloat() - rand.nextFloat()) * 0.125f, (float) prevPosY + h + (rand.nextFloat() - rand.nextFloat()) * 0.125f, (float) prevPosZ + (rand.nextFloat() - rand.nextFloat()) * 0.125f, 0.33f, 0.33f, 1.0f);
                }
            }
        }
        else {
            motionY -= gravity;
        }
        super.onUpdate();
    }
    
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("type", (short) type);
    }
    
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        type = par1NBTTagCompound.getShort("type");
    }
    
    public void writeSpawnData(ByteBuf data) {
        if (target != null) {
            data.writeInt((target == null) ? -1 : target.getEntityId());
            data.writeDouble(targetX);
            data.writeDouble(targetY);
            data.writeDouble(targetZ);
            data.writeByte(type);
        }
    }
    
    public void readSpawnData(ByteBuf data) {
        try {
            int ent = data.readInt();
            if (ent > -1) {
                target = world.getEntityByID(ent);
            }
            targetX = data.readDouble();
            targetY = data.readDouble();
            targetZ = data.readDouble();
            type = data.readByte();
        }
        catch (Exception ex) {}
    }
}
