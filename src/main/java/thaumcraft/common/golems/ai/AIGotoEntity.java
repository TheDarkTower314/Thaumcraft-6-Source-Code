package thaumcraft.common.golems.ai;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.tasks.TaskHandler;


public class AIGotoEntity extends AIGoto
{
    public AIGotoEntity(EntityThaumcraftGolem g) {
        super(g, (byte)1);
    }
    
    @Override
    public void updateTask() {
        super.updateTask();
        if (golem.getLookHelper() != null && golem.getTask() != null && golem.getTask().getEntity() != null) {
            golem.getLookHelper().setLookPositionWithEntity(golem.getTask().getEntity(), 10.0f, (float) golem.getVerticalFaceSpeed());
        }
    }
    
    @Override
    protected void moveTo() {
        if (golem.getNavigator() != null && golem.getTask() != null && golem.getTask().getEntity() != null) {
            golem.getNavigator().tryMoveToEntityLiving(golem.getTask().getEntity(), golem.getGolemMoveSpeed());
        }
    }
    
    @Override
    protected boolean findDestination() {
        ArrayList<Task> list = TaskHandler.getEntityTasksSorted(golem.world.provider.getDimension(), golem.getUniqueID(), golem);
        for (Task ticket : list) {
            if (areGolemTagsValidForTask(ticket) && ticket.canGolemPerformTask(golem) && golem.isWithinHomeDistanceFromPosition(ticket.getEntity().getPosition()) && isValidDestination(golem.world, ticket.getEntity().getPosition()) && canEasilyReach(ticket.getEntity())) {
                golem.setTask(ticket);
                golem.getTask().setReserved(true);
                minDist = 3.5 + golem.getTask().getEntity().width / 2.0f * (golem.getTask().getEntity().width / 2.0f);
                if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                    golem.world.setEntityState(golem, (byte)5);
                }
                return true;
            }
        }
        return false;
    }
    
    private boolean canEasilyReach(Entity e) {
        if (golem.getDistanceSq(e) < minDist) {
            return true;
        }
        Path pathentity = golem.getNavigator().getPathToEntityLiving(e);
        if (pathentity == null) {
            return false;
        }
        PathPoint pathpoint = pathentity.getFinalPathPoint();
        if (pathpoint == null) {
            return false;
        }
        int i = pathpoint.x - MathHelper.floor(e.posX);
        int j = pathpoint.z - MathHelper.floor(e.posZ);
        return i * i + j * j < minDist;
    }
}
