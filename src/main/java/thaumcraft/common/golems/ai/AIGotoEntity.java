// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.MathHelper;
import java.util.Iterator;
import java.util.ArrayList;
import thaumcraft.common.config.ModConfig;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.tasks.Task;
import net.minecraft.entity.Entity;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.golems.EntityThaumcraftGolem;

public class AIGotoEntity extends AIGoto
{
    public AIGotoEntity(final EntityThaumcraftGolem g) {
        super(g, (byte)1);
    }
    
    @Override
    public void updateTask() {
        super.updateTask();
        if (this.golem.getLookHelper() != null && this.golem.getTask() != null && this.golem.getTask().getEntity() != null) {
            this.golem.getLookHelper().setLookPositionWithEntity(this.golem.getTask().getEntity(), 10.0f, (float)this.golem.getVerticalFaceSpeed());
        }
    }
    
    @Override
    protected void moveTo() {
        if (this.golem.getNavigator() != null && this.golem.getTask() != null && this.golem.getTask().getEntity() != null) {
            this.golem.getNavigator().tryMoveToEntityLiving(this.golem.getTask().getEntity(), this.golem.getGolemMoveSpeed());
        }
    }
    
    @Override
    protected boolean findDestination() {
        final ArrayList<Task> list = TaskHandler.getEntityTasksSorted(this.golem.world.provider.getDimension(), this.golem.getUniqueID(), this.golem);
        for (final Task ticket : list) {
            if (this.areGolemTagsValidForTask(ticket) && ticket.canGolemPerformTask(this.golem) && this.golem.isWithinHomeDistanceFromPosition(ticket.getEntity().getPosition()) && this.isValidDestination(this.golem.world, ticket.getEntity().getPosition()) && this.canEasilyReach(ticket.getEntity())) {
                this.golem.setTask(ticket);
                this.golem.getTask().setReserved(true);
                this.minDist = 3.5 + this.golem.getTask().getEntity().width / 2.0f * (this.golem.getTask().getEntity().width / 2.0f);
                if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                    this.golem.world.setEntityState(this.golem, (byte)5);
                }
                return true;
            }
        }
        return false;
    }
    
    private boolean canEasilyReach(final Entity e) {
        if (this.golem.getDistanceSq(e) < this.minDist) {
            return true;
        }
        final Path pathentity = this.golem.getNavigator().getPathToEntityLiving(e);
        if (pathentity == null) {
            return false;
        }
        final PathPoint pathpoint = pathentity.getFinalPathPoint();
        if (pathpoint == null) {
            return false;
        }
        final int i = pathpoint.x - MathHelper.floor(e.posX);
        final int j = pathpoint.z - MathHelper.floor(e.posZ);
        return i * i + j * j < this.minDist;
    }
}
