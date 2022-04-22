// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.misc;

import java.util.Iterator;
import java.util.List;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ITickable;
import net.minecraft.tileentity.TileEntity;

public class TileBarrierStone extends TileEntity implements ITickable
{
    int count;
    
    public TileBarrierStone() {
        count = 0;
    }
    
    public boolean gettingPower() {
        return world.isBlockIndirectlyGettingPowered(pos) > 0;
    }
    
    public void update() {
        if (!world.isRemote) {
            if (count == 0) {
                count = world.rand.nextInt(100);
            }
            if (count % 5 == 0 && !gettingPower()) {
                final List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 3, pos.getZ() + 1).grow(0.1, 0.1, 0.1));
                if (targets.size() > 0) {
                    for (final EntityLivingBase e : targets) {
                        if (!e.onGround && !(e instanceof EntityPlayer)) {
                            e.addVelocity(-MathHelper.sin((e.rotationYaw + 180.0f) * 3.1415927f / 180.0f) * 0.2f, -0.1, MathHelper.cos((e.rotationYaw + 180.0f) * 3.1415927f / 180.0f) * 0.2f);
                        }
                    }
                }
            }
            if (++count % 100 == 0) {
                if (world.getBlockState(pos.up(1)) != BlocksTC.barrier.getDefaultState() && world.isAirBlock(pos.up(1))) {
                    world.setBlockState(pos.up(1), BlocksTC.barrier.getDefaultState(), 3);
                }
                if (world.getBlockState(pos.up(2)) != BlocksTC.barrier.getDefaultState() && world.isAirBlock(pos.up(2))) {
                    world.setBlockState(pos.up(2), BlocksTC.barrier.getDefaultState(), 3);
                }
            }
        }
    }
}
