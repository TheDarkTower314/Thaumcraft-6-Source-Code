// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.misc.PacketSealToClient;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import net.minecraft.util.EnumFacing;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Iterator;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.api.golems.seals.ISealConfigArea;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.seals.ISealEntity;

public class SealEntity implements ISealEntity
{
    SealPos sealPos;
    ISeal seal;
    byte priority;
    byte color;
    boolean locked;
    boolean redstone;
    String owner;
    boolean stopped;
    private BlockPos area;
    
    public SealEntity() {
        this.priority = 0;
        this.color = 0;
        this.locked = false;
        this.redstone = false;
        this.owner = "";
        this.stopped = false;
        this.area = new BlockPos(1, 1, 1);
    }
    
    public SealEntity(final World world, final SealPos sealPos, final ISeal seal) {
        this.priority = 0;
        this.color = 0;
        this.locked = false;
        this.redstone = false;
        this.owner = "";
        this.stopped = false;
        this.area = new BlockPos(1, 1, 1);
        this.sealPos = sealPos;
        this.seal = seal;
        if (seal instanceof ISealConfigArea) {
            final int x = (sealPos.face.getFrontOffsetX() == 0) ? 3 : 1;
            final int y = (sealPos.face.getFrontOffsetY() == 0) ? 3 : 1;
            final int z = (sealPos.face.getFrontOffsetZ() == 0) ? 3 : 1;
            this.area = new BlockPos(x, y, z);
        }
    }
    
    @Override
    public void tickSealEntity(final World world) {
        if (this.seal != null) {
            if (this.isStoppedByRedstone(world)) {
                if (!this.stopped) {
                    for (final Task t : TaskHandler.getTasks(world.provider.getDimension()).values()) {
                        if (t.getSealPos() != null && t.getSealPos().equals(this.sealPos)) {
                            t.setSuspended(true);
                        }
                    }
                }
                this.stopped = true;
                return;
            }
            this.stopped = false;
            this.seal.tickSeal(world, this);
        }
    }
    
    @Override
    public boolean isStoppedByRedstone(final World world) {
        return this.isRedstoneSensitive() && (world.isBlockPowered(this.getSealPos().pos) || world.isBlockPowered(this.getSealPos().pos.offset(this.getSealPos().face)));
    }
    
    @Override
    public ISeal getSeal() {
        return this.seal;
    }
    
    @Override
    public SealPos getSealPos() {
        return this.sealPos;
    }
    
    @Override
    public byte getPriority() {
        return this.priority;
    }
    
    @Override
    public void setPriority(final byte priority) {
        this.priority = priority;
    }
    
    @Override
    public byte getColor() {
        return this.color;
    }
    
    @Override
    public void setColor(final byte color) {
        this.color = color;
    }
    
    @Override
    public String getOwner() {
        return this.owner;
    }
    
    @Override
    public void setOwner(final String owner) {
        this.owner = owner;
    }
    
    @Override
    public boolean isLocked() {
        return this.locked;
    }
    
    @Override
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }
    
    @Override
    public boolean isRedstoneSensitive() {
        return this.redstone;
    }
    
    @Override
    public void setRedstoneSensitive(final boolean redstone) {
        this.redstone = redstone;
    }
    
    @Override
    public void readNBT(final NBTTagCompound nbt) {
        final BlockPos p = BlockPos.fromLong(nbt.getLong("pos"));
        final EnumFacing face = EnumFacing.VALUES[nbt.getByte("face")];
        this.sealPos = new SealPos(p, face);
        this.setPriority(nbt.getByte("priority"));
        this.setColor(nbt.getByte("color"));
        this.setLocked(nbt.getBoolean("locked"));
        this.setRedstoneSensitive(nbt.getBoolean("redstone"));
        this.setOwner(nbt.getString("owner"));
        try {
            this.seal = SealHandler.getSeal(nbt.getString("type")).getClass().newInstance();
        }
        catch (final Exception ex) {}
        if (this.seal != null) {
            this.seal.readCustomNBT(nbt);
            if (this.seal instanceof ISealConfigArea) {
                this.area = BlockPos.fromLong(nbt.getLong("area"));
            }
            if (this.seal instanceof ISealConfigToggles) {
                for (final ISealConfigToggles.SealToggle prop : ((ISealConfigToggles)this.seal).getToggles()) {
                    if (nbt.hasKey(prop.getKey())) {
                        prop.setValue(nbt.getBoolean(prop.getKey()));
                    }
                }
            }
        }
    }
    
    @Override
    public NBTTagCompound writeNBT() {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("pos", this.sealPos.pos.toLong());
        nbt.setByte("face", (byte)this.sealPos.face.ordinal());
        nbt.setString("type", this.seal.getKey());
        nbt.setByte("priority", this.getPriority());
        nbt.setByte("color", this.getColor());
        nbt.setBoolean("locked", this.isLocked());
        nbt.setBoolean("redstone", this.isRedstoneSensitive());
        nbt.setString("owner", this.getOwner());
        if (this.seal != null) {
            this.seal.writeCustomNBT(nbt);
            if (this.seal instanceof ISealConfigArea) {
                nbt.setLong("area", this.area.toLong());
            }
            if (this.seal instanceof ISealConfigToggles) {
                for (final ISealConfigToggles.SealToggle prop : ((ISealConfigToggles)this.seal).getToggles()) {
                    nbt.setBoolean(prop.getKey(), prop.getValue());
                }
            }
        }
        return nbt;
    }
    
    @Override
    public void syncToClient(final World world) {
        if (!world.isRemote) {
            PacketHandler.INSTANCE.sendToDimension(new PacketSealToClient(this), world.provider.getDimension());
        }
    }
    
    @Override
    public BlockPos getArea() {
        return this.area;
    }
    
    @Override
    public void setArea(final BlockPos v) {
        this.area = v;
    }
}
