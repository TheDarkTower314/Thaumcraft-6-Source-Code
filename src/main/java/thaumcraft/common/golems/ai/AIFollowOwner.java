package thaumcraft.common.golems.ai;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;


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
    
    public AIFollowOwner(EntityOwnedConstruct p_i1625_1_, double p_i1625_2_, float p_i1625_4_, float p_i1625_5_) {
        thePet = p_i1625_1_;
        theWorld = p_i1625_1_.world;
        followSpeed = p_i1625_2_;
        petPathfinder = p_i1625_1_.getNavigator();
        minDist = p_i1625_4_;
        maxDist = p_i1625_5_;
        setMutexBits(3);
        if (!(p_i1625_1_.getNavigator() instanceof PathNavigateGround) && !(p_i1625_1_.getNavigator() instanceof PathNavigateGolemAir)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }
    
    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = thePet.getOwnerEntity();
        if (entitylivingbase == null) {
            return false;
        }
        if (thePet.getDistanceSq(entitylivingbase) < minDist * minDist) {
            return false;
        }
        theOwner = entitylivingbase;
        return true;
    }
    
    public boolean shouldContinueExecuting() {
        return !petPathfinder.noPath() && thePet.getDistanceSq(theOwner) > maxDist * maxDist;
    }
    
    public void startExecuting() {
        timeToRecalcPath = 0;
        oldWaterCost = thePet.getPathPriority(PathNodeType.WATER);
        thePet.setPathPriority(PathNodeType.WATER, 0.0f);
    }
    
    public void resetTask() {
        theOwner = null;
        petPathfinder.clearPath();
        thePet.setPathPriority(PathNodeType.WATER, oldWaterCost);
    }
    
    private boolean func_181065_a(BlockPos p_181065_1_) {
        IBlockState iblockstate = theWorld.getBlockState(p_181065_1_);
        Block block = iblockstate.getBlock();
        return block == Blocks.AIR || !iblockstate.isFullCube();
    }
    
    public void updateTask() {
        thePet.getLookHelper().setLookPositionWithEntity(theOwner, 10.0f, (float) thePet.getVerticalFaceSpeed());
        int timeToRecalcPath = this.timeToRecalcPath - 1;
        this.timeToRecalcPath = timeToRecalcPath;
        if (timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!petPathfinder.tryMoveToEntityLiving(theOwner, followSpeed) && !thePet.getLeashed() && thePet.getDistanceSq(theOwner) >= 144.0) {
                int i = MathHelper.floor(theOwner.posX) - 2;
                int j = MathHelper.floor(theOwner.posZ) - 2;
                int k = MathHelper.floor(theOwner.getEntityBoundingBox().minY);
                for (int l = 0; l <= 4; ++l) {
                    for (int i2 = 0; i2 <= 4; ++i2) {
                        if ((l < 1 || i2 < 1 || l > 3 || i2 > 3) && theWorld.getBlockState(new BlockPos(i + l, k - 1, j + i2)).isFullCube() && func_181065_a(new BlockPos(i + l, k, j + i2)) && func_181065_a(new BlockPos(i + l, k + 1, j + i2))) {
                            thePet.setLocationAndAngles(i + l + 0.5f, k, j + i2 + 0.5f, thePet.rotationYaw, thePet.rotationPitch);
                            petPathfinder.clearPath();
                            return;
                        }
                    }
                }
            }
        }
    }
}
