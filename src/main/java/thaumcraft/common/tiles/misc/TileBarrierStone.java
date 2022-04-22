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
        this.count = 0;
    }
    
    public boolean gettingPower() {
        return this.world.isBlockIndirectlyGettingPowered(this.pos) > 0;
    }
    
    public void update() {
        if (!this.world.isRemote) {
            if (this.count == 0) {
                this.count = this.world.rand.nextInt(100);
            }
            if (this.count % 5 == 0 && !this.gettingPower()) {
                final List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 3, this.pos.getZ() + 1).grow(0.1, 0.1, 0.1));
                if (targets.size() > 0) {
                    for (final EntityLivingBase e : targets) {
                        if (!e.onGround && !(e instanceof EntityPlayer)) {
                            e.addVelocity(-MathHelper.sin((e.rotationYaw + 180.0f) * 3.1415927f / 180.0f) * 0.2f, -0.1, MathHelper.cos((e.rotationYaw + 180.0f) * 3.1415927f / 180.0f) * 0.2f);
                        }
                    }
                }
            }
            if (++this.count % 100 == 0) {
                if (this.world.getBlockState(this.pos.up(1)) != BlocksTC.barrier.getDefaultState() && this.world.isAirBlock(this.pos.up(1))) {
                    this.world.setBlockState(this.pos.up(1), BlocksTC.barrier.getDefaultState(), 3);
                }
                if (this.world.getBlockState(this.pos.up(2)) != BlocksTC.barrier.getDefaultState() && this.world.isAirBlock(this.pos.up(2))) {
                    this.world.setBlockState(this.pos.up(2), BlocksTC.barrier.getDefaultState(), 3);
                }
            }
        }
    }
}
