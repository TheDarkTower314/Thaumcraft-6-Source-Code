// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import java.util.Iterator;
import java.util.ArrayList;
import thaumcraft.common.config.ModConfig;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.tasks.Task;
import net.minecraft.entity.Entity;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.golems.EntityThaumcraftGolem;

public class AIGotoBlock extends AIGoto
{
    public AIGotoBlock(final EntityThaumcraftGolem g) {
        super(g, (byte)0);
    }
    
    @Override
    public void updateTask() {
        super.updateTask();
        if (this.golem.getLookHelper() != null) {
            this.golem.getLookHelper().setLookPosition(this.golem.getTask().getPos().getX() + 0.5, this.golem.getTask().getPos().getY() + 0.5, this.golem.getTask().getPos().getZ() + 0.5, 10.0f, (float)this.golem.getVerticalFaceSpeed());
        }
    }
    
    @Override
    protected void moveTo() {
        if (this.targetBlock != null) {
            this.golem.getNavigator().tryMoveToXYZ(this.targetBlock.getX() + 0.5, this.targetBlock.getY() + 0.5, this.targetBlock.getZ() + 0.5, this.golem.getGolemMoveSpeed());
        }
        else {
            this.golem.getNavigator().tryMoveToXYZ(this.golem.getTask().getPos().getX() + 0.5, this.golem.getTask().getPos().getY() + 0.5, this.golem.getTask().getPos().getZ() + 0.5, this.golem.getGolemMoveSpeed());
        }
    }
    
    @Override
    protected boolean findDestination() {
        final ArrayList<Task> list = TaskHandler.getBlockTasksSorted(this.golem.world.provider.getDimension(), this.golem.getUniqueID(), this.golem);
        for (final Task ticket : list) {
            if (this.areGolemTagsValidForTask(ticket) && ticket.canGolemPerformTask(this.golem) && this.golem.isWithinHomeDistanceFromPosition(ticket.getPos()) && this.isValidDestination(this.golem.world, ticket.getPos()) && this.canEasilyReach(ticket.getPos())) {
                this.targetBlock = this.getAdjacentSpace(ticket.getPos());
                this.golem.setTask(ticket);
                this.golem.getTask().setReserved(true);
                if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                    this.golem.world.setEntityState(this.golem, (byte)5);
                }
                return true;
            }
        }
        return false;
    }
    
    private BlockPos getAdjacentSpace(final BlockPos pos) {
        double d = Double.MAX_VALUE;
        BlockPos closest = null;
        for (final EnumFacing face : EnumFacing.HORIZONTALS) {
            final IBlockState block = this.golem.world.getBlockState(pos.offset(face));
            if (!block.getMaterial().blocksMovement()) {
                final double dist = pos.offset(face).distanceSqToCenter(this.golem.posX, this.golem.posY, this.golem.posZ);
                if (dist < d) {
                    closest = pos.offset(face);
                    d = dist;
                }
            }
        }
        return closest;
    }
    
    private boolean canEasilyReach(final BlockPos pos) {
        if (this.golem.getDistanceSqToCenter(pos) < this.minDist) {
            return true;
        }
        final Path pathentity = this.golem.getNavigator().getPathToXYZ(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        if (pathentity == null) {
            return false;
        }
        final PathPoint pathpoint = pathentity.getFinalPathPoint();
        if (pathpoint == null) {
            return false;
        }
        final int i = pathpoint.x - MathHelper.floor((float)pos.getX());
        final int j = pathpoint.z - MathHelper.floor((float)pos.getZ());
        int k = pathpoint.y - MathHelper.floor((float)pos.getY());
        if (i == 0 && j == 0 && k == 2) {
            --k;
        }
        return i * i + j * j + k * k < 2.25;
    }
}
