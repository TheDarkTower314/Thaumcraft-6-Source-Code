package thaumcraft.common.golems.ai;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.tasks.TaskHandler;


public class AIGotoBlock extends AIGoto
{
    public AIGotoBlock(EntityThaumcraftGolem g) {
        super(g, (byte)0);
    }
    
    @Override
    public void updateTask() {
        super.updateTask();
        if (golem.getLookHelper() != null) {
            golem.getLookHelper().setLookPosition(golem.getTask().getPos().getX() + 0.5, golem.getTask().getPos().getY() + 0.5, golem.getTask().getPos().getZ() + 0.5, 10.0f, (float) golem.getVerticalFaceSpeed());
        }
    }
    
    @Override
    protected void moveTo() {
        if (targetBlock != null) {
            golem.getNavigator().tryMoveToXYZ(targetBlock.getX() + 0.5, targetBlock.getY() + 0.5, targetBlock.getZ() + 0.5, golem.getGolemMoveSpeed());
        }
        else {
            golem.getNavigator().tryMoveToXYZ(golem.getTask().getPos().getX() + 0.5, golem.getTask().getPos().getY() + 0.5, golem.getTask().getPos().getZ() + 0.5, golem.getGolemMoveSpeed());
        }
    }
    
    @Override
    protected boolean findDestination() {
        ArrayList<Task> list = TaskHandler.getBlockTasksSorted(golem.world.provider.getDimension(), golem.getUniqueID(), golem);
        for (Task ticket : list) {
            if (areGolemTagsValidForTask(ticket) && ticket.canGolemPerformTask(golem) && golem.isWithinHomeDistanceFromPosition(ticket.getPos()) && isValidDestination(golem.world, ticket.getPos()) && canEasilyReach(ticket.getPos())) {
                targetBlock = getAdjacentSpace(ticket.getPos());
                golem.setTask(ticket);
                golem.getTask().setReserved(true);
                if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                    golem.world.setEntityState(golem, (byte)5);
                }
                return true;
            }
        }
        return false;
    }
    
    private BlockPos getAdjacentSpace(BlockPos pos) {
        double d = Double.MAX_VALUE;
        BlockPos closest = null;
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            IBlockState block = golem.world.getBlockState(pos.offset(face));
            if (!block.getMaterial().blocksMovement()) {
                double dist = pos.offset(face).distanceSqToCenter(golem.posX, golem.posY, golem.posZ);
                if (dist < d) {
                    closest = pos.offset(face);
                    d = dist;
                }
            }
        }
        return closest;
    }
    
    private boolean canEasilyReach(BlockPos pos) {
        if (golem.getDistanceSqToCenter(pos) < minDist) {
            return true;
        }
        Path pathentity = golem.getNavigator().getPathToXYZ(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        if (pathentity == null) {
            return false;
        }
        PathPoint pathpoint = pathentity.getFinalPathPoint();
        if (pathpoint == null) {
            return false;
        }
        int i = pathpoint.x - MathHelper.floor((float)pos.getX());
        int j = pathpoint.z - MathHelper.floor((float)pos.getZ());
        int k = pathpoint.y - MathHelper.floor((float)pos.getY());
        if (i == 0 && j == 0 && k == 2) {
            --k;
        }
        return i * i + j * j + k * k < 2.25;
    }
}
