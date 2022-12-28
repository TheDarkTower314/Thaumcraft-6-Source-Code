package thaumcraft.common.golems.ai;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.tasks.TaskHandler;


public abstract class AIGoto extends EntityAIBase
{
    protected EntityThaumcraftGolem golem;
    protected int taskCounter;
    protected byte type;
    protected int cooldown;
    protected double minDist;
    private BlockPos prevRamble;
    protected BlockPos targetBlock;
    int pause;
    
    public AIGoto(EntityThaumcraftGolem g, byte type) {
        taskCounter = -1;
        this.type = 0;
        minDist = 4.0;
        pause = 0;
        golem = g;
        this.type = type;
        setMutexBits(5);
    }
    
    public boolean shouldExecute() {
        if (cooldown > 0) {
            --cooldown;
            return false;
        }
        cooldown = 5;
        if (golem.getTask() != null && !golem.getTask().isSuspended()) {
            return false;
        }
        targetBlock = null;
        boolean start = findDestination();
        if (start && golem.getTask() != null && golem.getTask().getSealPos() != null) {
            ISealEntity se = GolemHelper.getSealEntity(golem.world.provider.getDimension(), golem.getTask().getSealPos());
            if (se != null) {
                se.getSeal().onTaskStarted(golem.world, golem, golem.getTask());
            }
        }
        return start;
    }
    
    public void startExecuting() {
        moveTo();
        taskCounter = 0;
    }
    
    protected abstract void moveTo();
    
    public boolean shouldContinueExecuting() {
        return taskCounter >= 0 && taskCounter <= 1000 && golem.getTask() != null && !golem.getTask().isSuspended() && isValidDestination(golem.world, golem.getTask().getPos());
    }
    
    public void updateTask() {
        if (golem.getTask() == null) {
            return;
        }
        if (pause-- <= 0) {
            double dist = (golem.getTask().getType() == 0) ? golem.getDistanceSqToCenter((targetBlock == null) ? golem.getTask().getPos() : targetBlock) : golem.getDistanceSq(golem.getTask().getEntity());
            if (dist > minDist) {
                golem.getTask().setCompletion(false);
                ++taskCounter;
                if (taskCounter % 5 == 0) {
                    if (prevRamble != null && prevRamble.equals(golem.getPosition())) {
                        Vec3d vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(golem, 6, 4, new Vec3d(golem.getTask().getPos().getX(), golem.getTask().getPos().getY(), golem.getTask().getPos().getZ()));
                        if (vec3 != null) {
                            golem.getNavigator().tryMoveToXYZ(vec3.x + 0.5, vec3.y + 0.5, vec3.z + 0.5, golem.getGolemMoveSpeed());
                        }
                    }
                    else {
                        moveTo();
                    }
                    prevRamble = golem.getPosition();
                }
            }
            else {
                TaskHandler.completeTask(golem.getTask(), golem);
                if (golem.getTask() != null && golem.getTask().isCompleted()) {
                    if (taskCounter >= 0) {
                        taskCounter = 0;
                    }
                    pause = 0;
                }
                else {
                    pause = 10;
                    ++taskCounter;
                }
                --taskCounter;
            }
        }
    }
    
    public void resetTask() {
        if (golem.getTask() != null) {
            if (!golem.getTask().isCompleted() && golem.getTask().isReserved() && ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                golem.world.setEntityState(golem, (byte)6);
            }
            if (golem.getTask().isCompleted() && !golem.getTask().isSuspended()) {
                golem.getTask().setSuspended(true);
            }
            golem.getTask().setReserved(false);
        }
    }
    
    protected abstract boolean findDestination();
    
    protected boolean isValidDestination(World world, BlockPos pos) {
        return true;
    }
    
    protected boolean areGolemTagsValidForTask(Task ticket) {
        ISealEntity se = SealHandler.getSealEntity(golem.world.provider.getDimension(), ticket.getSealPos());
        if (se == null || se.getSeal() == null) {
            return true;
        }
        if (se.isLocked() && !golem.getOwnerId().equals(se.getOwner())) {
            return false;
        }
        if (se.getSeal().getRequiredTags() != null && !golem.getProperties().getTraits().containsAll(Arrays.asList(se.getSeal().getRequiredTags()))) {
            return false;
        }
        if (se.getSeal().getForbiddenTags() != null) {
            for (EnumGolemTrait tag : se.getSeal().getForbiddenTags()) {
                if (golem.getProperties().getTraits().contains(tag)) {
                    return false;
                }
            }
        }
        return true;
    }
}
