package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.Thaumcraft;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.lib.utils.Utils;


public class PacketSealToClient implements IMessage, IMessageHandler<PacketSealToClient, IMessage>
{
    BlockPos pos;
    EnumFacing face;
    String type;
    long area;
    boolean[] props;
    boolean blacklist;
    byte filtersize;
    NonNullList<ItemStack> filter;
    NonNullList<Integer> filterStackSize;
    byte priority;
    byte color;
    boolean locked;
    boolean redstone;
    String owner;
    
    public PacketSealToClient() {
        props = null;
    }
    
    public PacketSealToClient(ISealEntity se) {
        props = null;
        pos = se.getSealPos().pos;
        face = se.getSealPos().face;
        type = ((se.getSeal() == null) ? "REMOVE" : se.getSeal().getKey());
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigArea) {
            area = se.getArea().toLong();
        }
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigToggles) {
            ISealConfigToggles cp = (ISealConfigToggles)se.getSeal();
            props = new boolean[cp.getToggles().length];
            for (int a = 0; a < cp.getToggles().length; ++a) {
                props[a] = cp.getToggles()[a].getValue();
            }
        }
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigFilter) {
            ISealConfigFilter cp2 = (ISealConfigFilter)se.getSeal();
            blacklist = cp2.isBlacklist();
            filtersize = (byte)cp2.getFilterSize();
            filter = cp2.getInv();
            filterStackSize = cp2.getSizes();
        }
        priority = se.getPriority();
        color = se.getColor();
        locked = se.isLocked();
        redstone = se.isRedstoneSensitive();
        owner = se.getOwner();
    }
    
    public void toBytes(ByteBuf dos) {
        dos.writeLong(pos.toLong());
        dos.writeByte(face.ordinal());
        dos.writeByte(priority);
        dos.writeByte(color);
        dos.writeBoolean(locked);
        dos.writeBoolean(redstone);
        ByteBufUtils.writeUTF8String(dos, owner);
        ByteBufUtils.writeUTF8String(dos, type);
        dos.writeBoolean(blacklist);
        dos.writeByte(filtersize);
        for (int a = 0; a < filtersize; ++a) {
            Utils.writeItemStackToBuffer(dos, filter.get(a));
            dos.writeShort(filterStackSize.get(a));
        }
        if (area != 0L) {
            dos.writeLong(area);
        }
        if (props != null) {
            for (boolean b : props) {
                dos.writeBoolean(b);
            }
        }
    }
    
    public void fromBytes(ByteBuf dat) {
        pos = BlockPos.fromLong(dat.readLong());
        face = EnumFacing.VALUES[dat.readByte()];
        priority = dat.readByte();
        color = dat.readByte();
        locked = dat.readBoolean();
        redstone = dat.readBoolean();
        owner = ByteBufUtils.readUTF8String(dat);
        type = ByteBufUtils.readUTF8String(dat);
        blacklist = dat.readBoolean();
        filtersize = dat.readByte();
        filter = NonNullList.withSize(filtersize, ItemStack.EMPTY);
        filterStackSize = NonNullList.withSize(filtersize, 0);
        for (int a = 0; a < filtersize; ++a) {
            filter.set(a, Utils.readItemStackFromBuffer(dat));
            filterStackSize.set(a, (int)dat.readShort());
        }
        if (!type.equals("REMOVE") && SealHandler.getSeal(type) != null) {
            if (SealHandler.getSeal(type) instanceof ISealConfigArea) {
                try {
                    area = dat.readLong();
                }
                catch (Exception ex) {}
            }
            if (SealHandler.getSeal(type) instanceof ISealConfigToggles) {
                try {
                    ISealConfigToggles cp = (ISealConfigToggles)SealHandler.getSeal(type);
                    props = new boolean[cp.getToggles().length];
                    for (int a2 = 0; a2 < cp.getToggles().length; ++a2) {
                        props[a2] = dat.readBoolean();
                    }
                }
                catch (Exception ex2) {}
            }
        }
    }
    
    public IMessage onMessage(PacketSealToClient message, MessageContext ctx) {
        if (message.type.equals("REMOVE")) {
            SealHandler.removeSealEntity(Thaumcraft.proxy.getClientWorld(), new SealPos(message.pos, message.face), true);
        }
        else {
            try {
                SealEntity seal = new SealEntity(Thaumcraft.proxy.getClientWorld(), new SealPos(message.pos, message.face), SealHandler.getSeal(message.type).getClass().newInstance());
                if (message.area != 0L) {
                    seal.setArea(BlockPos.fromLong(message.area));
                }
                if (message.props != null && seal.getSeal() instanceof ISealConfigToggles) {
                    ISealConfigToggles cp = (ISealConfigToggles)seal.getSeal();
                    for (int a = 0; a < message.props.length; ++a) {
                        cp.setToggle(a, message.props[a]);
                    }
                }
                if (seal.getSeal() instanceof ISealConfigFilter) {
                    ISealConfigFilter cp2 = (ISealConfigFilter)seal.getSeal();
                    cp2.setBlacklist(message.blacklist);
                    for (int a = 0; a < message.filtersize; ++a) {
                        cp2.setFilterSlot(a, message.filter.get(a));
                        cp2.setFilterSlotSize(a, message.filterStackSize.get(a));
                    }
                }
                seal.setPriority(message.priority);
                seal.setColor(message.color);
                seal.setLocked(message.locked);
                seal.setRedstoneSensitive(message.redstone);
                seal.setOwner(message.owner);
                SealHandler.addSealEntity(Thaumcraft.proxy.getClientWorld(), seal);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
