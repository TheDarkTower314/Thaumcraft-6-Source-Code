package thaumcraft.common.golems.seals;
import java.util.Iterator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketSealToClient;


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
        priority = 0;
        color = 0;
        locked = false;
        redstone = false;
        owner = "";
        stopped = false;
        area = new BlockPos(1, 1, 1);
    }
    
    public SealEntity(World world, SealPos sealPos, ISeal seal) {
        priority = 0;
        color = 0;
        locked = false;
        redstone = false;
        owner = "";
        stopped = false;
        area = new BlockPos(1, 1, 1);
        this.sealPos = sealPos;
        this.seal = seal;
        if (seal instanceof ISealConfigArea) {
            int x = (sealPos.face.getFrontOffsetX() == 0) ? 3 : 1;
            int y = (sealPos.face.getFrontOffsetY() == 0) ? 3 : 1;
            int z = (sealPos.face.getFrontOffsetZ() == 0) ? 3 : 1;
            area = new BlockPos(x, y, z);
        }
    }
    
    @Override
    public void tickSealEntity(World world) {
        if (seal != null) {
            if (isStoppedByRedstone(world)) {
                if (!stopped) {
                    for (Task t : TaskHandler.getTasks(world.provider.getDimension()).values()) {
                        if (t.getSealPos() != null && t.getSealPos().equals(sealPos)) {
                            t.setSuspended(true);
                        }
                    }
                }
                stopped = true;
                return;
            }
            stopped = false;
            seal.tickSeal(world, this);
        }
    }
    
    @Override
    public boolean isStoppedByRedstone(World world) {
        return isRedstoneSensitive() && (world.isBlockPowered(getSealPos().pos) || world.isBlockPowered(getSealPos().pos.offset(getSealPos().face)));
    }
    
    @Override
    public ISeal getSeal() {
        return seal;
    }
    
    @Override
    public SealPos getSealPos() {
        return sealPos;
    }
    
    @Override
    public byte getPriority() {
        return priority;
    }
    
    @Override
    public void setPriority(byte priority) {
        this.priority = priority;
    }
    
    @Override
    public byte getColor() {
        return color;
    }
    
    @Override
    public void setColor(byte color) {
        this.color = color;
    }
    
    @Override
    public String getOwner() {
        return owner;
    }
    
    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    @Override
    public boolean isLocked() {
        return locked;
    }
    
    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    @Override
    public boolean isRedstoneSensitive() {
        return redstone;
    }
    
    @Override
    public void setRedstoneSensitive(boolean redstone) {
        this.redstone = redstone;
    }
    
    @Override
    public void readNBT(NBTTagCompound nbt) {
        BlockPos p = BlockPos.fromLong(nbt.getLong("pos"));
        EnumFacing face = EnumFacing.VALUES[nbt.getByte("face")];
        sealPos = new SealPos(p, face);
        setPriority(nbt.getByte("priority"));
        setColor(nbt.getByte("color"));
        setLocked(nbt.getBoolean("locked"));
        setRedstoneSensitive(nbt.getBoolean("redstone"));
        setOwner(nbt.getString("owner"));
        try {
            seal = SealHandler.getSeal(nbt.getString("type")).getClass().newInstance();
        }
        catch (Exception ex) {}
        if (seal != null) {
            seal.readCustomNBT(nbt);
            if (seal instanceof ISealConfigArea) {
                area = BlockPos.fromLong(nbt.getLong("area"));
            }
            if (seal instanceof ISealConfigToggles) {
                for (ISealConfigToggles.SealToggle prop : ((ISealConfigToggles) seal).getToggles()) {
                    if (nbt.hasKey(prop.getKey())) {
                        prop.setValue(nbt.getBoolean(prop.getKey()));
                    }
                }
            }
        }
    }
    
    @Override
    public NBTTagCompound writeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("pos", sealPos.pos.toLong());
        nbt.setByte("face", (byte) sealPos.face.ordinal());
        nbt.setString("type", seal.getKey());
        nbt.setByte("priority", getPriority());
        nbt.setByte("color", getColor());
        nbt.setBoolean("locked", isLocked());
        nbt.setBoolean("redstone", isRedstoneSensitive());
        nbt.setString("owner", getOwner());
        if (seal != null) {
            seal.writeCustomNBT(nbt);
            if (seal instanceof ISealConfigArea) {
                nbt.setLong("area", area.toLong());
            }
            if (seal instanceof ISealConfigToggles) {
                for (ISealConfigToggles.SealToggle prop : ((ISealConfigToggles) seal).getToggles()) {
                    nbt.setBoolean(prop.getKey(), prop.getValue());
                }
            }
        }
        return nbt;
    }
    
    @Override
    public void syncToClient(World world) {
        if (!world.isRemote) {
            PacketHandler.INSTANCE.sendToDimension(new PacketSealToClient(this), world.provider.getDimension());
        }
    }
    
    @Override
    public BlockPos getArea() {
        return area;
    }
    
    @Override
    public void setArea(BlockPos v) {
        area = v;
    }
}
