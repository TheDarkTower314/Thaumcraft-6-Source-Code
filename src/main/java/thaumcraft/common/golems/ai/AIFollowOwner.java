// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import net.minecraft.entity.ai.EntityAIBase;

public class AIFollowOwner extends EntityAIBase
{
    private EntityOwnedConstruct thePet;
    private EntityLivingBase theOwner;
    World theWorld;
    private double followSpeed;
    private PathNavigate petPathfinder;
    private int timeToRecalcPath;
    float maxDist;
    float minDist;
    private float oldWaterCost;
    
    public AIFollowOwner(final EntityOwnedConstruct p_i1625_1_, final double p_i1625_2_, final float p_i1625_4_, final float p_i1625_5_) {
        this.thePet = p_i1625_1_;
        this.theWorld = p_i1625_1_.world;
        this.followSpeed = p_i1625_2_;
        this.petPathfinder = p_i1625_1_.getNavigator();
        this.minDist = p_i1625_4_;
        this.maxDist = p_i1625_5_;
        this.setMutexBits(3);
        if (!(p_i1625_1_.getNavigator() instanceof PathNavigateGround) && !(p_i1625_1_.getNavigator() instanceof PathNavigateGolemAir)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }
    
    public boolean shouldExecute() {
        final EntityLivingBase entitylivingbase = this.thePet.getOwnerEntity();
        if (entitylivingbase == null) {
            return false;
        }
        if (this.thePet.getDistanceSq(entitylivingbase) < this.minDist * this.minDist) {
            return false;
        }
        this.theOwner = entitylivingbase;
        return true;
    }
    
    public boolean shouldContinueExecuting() {
        return !this.petPathfinder.noPath() && this.thePet.getDistanceSq(this.theOwner) > this.maxDist * this.maxDist;
    }
    
    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.thePet.getPathPriority(PathNodeType.WATER);
        this.thePet.setPathPriority(PathNodeType.WATER, 0.0f);
    }
    
    public void resetTask() {
        this.theOwner = null;
        this.petPathfinder.clearPath();
        this.thePet.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }
    
    private boolean func_181065_a(final BlockPos p_181065_1_) {
        final IBlockState iblockstate = this.theWorld.getBlockState(p_181065_1_);
        final Block block = iblockstate.getBlock();
        return block == Blocks.AIR || !iblockstate.isFullCube();
    }
    
    public void updateTask() {
        this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0f, (float)this.thePet.getVerticalFaceSpeed());
        final int timeToRecalcPath = this.timeToRecalcPath - 1;
        this.timeToRecalcPath = timeToRecalcPath;
        if (timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.followSpeed) && !this.thePet.getLeashed() && this.thePet.getDistanceSq(this.theOwner) >= 144.0) {
                final int i = MathHelper.floor(this.theOwner.posX) - 2;
                final int j = MathHelper.floor(this.theOwner.posZ) - 2;
                final int k = MathHelper.floor(this.theOwner.getEntityBoundingBox().minY);
                for (int l = 0; l <= 4; ++l) {
                    for (int i2 = 0; i2 <= 4; ++i2) {
                        if ((l < 1 || i2 < 1 || l > 3 || i2 > 3) && this.theWorld.getBlockState(new BlockPos(i + l, k - 1, j + i2)).isFullCube() && this.func_181065_a(new BlockPos(i + l, k, j + i2)) && this.func_181065_a(new BlockPos(i + l, k + 1, j + i2))) {
                            this.thePet.setLocationAndAngles(i + l + 0.5f, k, j + i2 + 0.5f, this.thePet.rotationYaw, this.thePet.rotationPitch);
                            this.petPathfinder.clearPath();
                            return;
                        }
                    }
                }
            }
        }
    }
}
