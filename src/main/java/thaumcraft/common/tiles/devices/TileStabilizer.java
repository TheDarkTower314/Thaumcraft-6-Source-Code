// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import net.minecraft.nbt.NBTTagCompound;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.api.aura.AuraHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ITickable;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileStabilizer extends TileThaumcraft implements ITickable
{
    private int ticks;
    private int delay;
    int lastEnergy;
    protected int energy;
    protected final int capacity = 15;
    
    public TileStabilizer() {
        this.ticks = 0;
        this.delay = 0;
        this.lastEnergy = 0;
        this.energy = 0;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1.5, this.getPos().getZ() + 1);
    }
    
    public void update() {
        if (!this.world.isRemote) {
            ++this.ticks;
            final int energy = this.energy;
            this.getClass();
            if (energy < 15 && this.ticks % 20 == 0) {
                ++this.energy;
                AuraHelper.polluteAura(this.getWorld(), this.getPos(), 0.25f, true);
                this.markDirty();
                this.syncTile(false);
                this.world.notifyNeighborsOfStateChange(this.getPos(), this.world.getBlockState(this.pos).getBlock(), false);
            }
            if (this.energy > 0 && this.delay <= 0 && this.ticks % 5 == 0) {
                final int q = this.energy;
                this.tryAddStability();
                if (q != this.energy) {
                    this.markDirty();
                    this.syncTile(false);
                }
            }
            if (this.delay > 0) {
                --this.delay;
            }
        }
        if (this.world.isRemote && this.energy != this.lastEnergy) {
            this.world.markBlockRangeForRenderUpdate(this.getPos(), this.getPos());
            this.lastEnergy = this.energy;
        }
    }
    
    private void tryAddStability() {
        final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
        final List<EntityFluxRift> targets = this.world.getEntitiesWithinAABB((Class)EntityFluxRift.class, new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(8.0));
        if (targets.size() > 0) {
            for (final EntityFluxRift e : targets) {
                if (e.isDead) {
                    continue;
                }
                if (e.getStability() == EntityFluxRift.EnumStability.VERY_STABLE || !this.mitigate(1)) {
                    continue;
                }
                e.addStability();
                this.delay += 5;
                if (this.energy <= 0) {
                    return;
                }
            }
        }
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbt) {
        this.energy = Math.min(nbt.getInteger("energy"), 15);
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbt) {
        nbt.setInteger("energy", this.energy);
        return nbt;
    }
    
    public int getEnergy() {
        return this.energy;
    }
    
    public boolean mitigate(final int e) {
        if (this.energy >= e) {
            this.energy -= e;
            this.world.notifyNeighborsOfStateChange(this.getPos(), this.world.getBlockState(this.pos).getBlock(), false);
            return true;
        }
        return false;
    }
}
