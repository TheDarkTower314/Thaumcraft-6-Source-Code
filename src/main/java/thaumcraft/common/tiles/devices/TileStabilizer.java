package thaumcraft.common.tiles.devices;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileStabilizer extends TileThaumcraft implements ITickable
{
    private int ticks;
    private int delay;
    int lastEnergy;
    protected int energy;
    protected int capacity = 15;
    
    public TileStabilizer() {
        ticks = 0;
        delay = 0;
        lastEnergy = 0;
        energy = 0;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1.5, getPos().getZ() + 1);
    }
    
    public void update() {
        if (!world.isRemote) {
            ++ticks;
            int energy = this.energy;
            getClass();
            if (energy < 15 && ticks % 20 == 0) {
                ++this.energy;
                AuraHelper.polluteAura(getWorld(), getPos(), 0.25f, true);
                markDirty();
                syncTile(false);
                world.notifyNeighborsOfStateChange(getPos(), world.getBlockState(pos).getBlock(), false);
            }
            if (this.energy > 0 && delay <= 0 && ticks % 5 == 0) {
                int q = this.energy;
                tryAddStability();
                if (q != this.energy) {
                    markDirty();
                    syncTile(false);
                }
            }
            if (delay > 0) {
                --delay;
            }
        }
        if (world.isRemote && energy != lastEnergy) {
            world.markBlockRangeForRenderUpdate(getPos(), getPos());
            lastEnergy = energy;
        }
    }
    
    private void tryAddStability() {
        EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
        List<EntityFluxRift> targets = world.getEntitiesWithinAABB(EntityFluxRift.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(8.0));
        if (targets.size() > 0) {
            for (EntityFluxRift e : targets) {
                if (e.isDead) {
                    continue;
                }
                if (e.getStability() == EntityFluxRift.EnumStability.VERY_STABLE || !mitigate(1)) {
                    continue;
                }
                e.addStability();
                delay += 5;
                if (energy <= 0) {
                    return;
                }
            }
        }
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbt) {
        energy = Math.min(nbt.getInteger("energy"), 15);
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
        nbt.setInteger("energy", energy);
        return nbt;
    }
    
    public int getEnergy() {
        return energy;
    }
    
    public boolean mitigate(int e) {
        if (energy >= e) {
            energy -= e;
            world.notifyNeighborsOfStateChange(getPos(), world.getBlockState(pos).getBlock(), false);
            return true;
        }
        return false;
    }
}
