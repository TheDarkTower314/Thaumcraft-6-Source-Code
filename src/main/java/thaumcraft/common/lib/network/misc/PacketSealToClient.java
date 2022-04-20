// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.Thaumcraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.lib.utils.Utils;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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
        this.props = null;
    }
    
    public PacketSealToClient(final ISealEntity se) {
        this.props = null;
        this.pos = se.getSealPos().pos;
        this.face = se.getSealPos().face;
        this.type = ((se.getSeal() == null) ? "REMOVE" : se.getSeal().getKey());
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigArea) {
            this.area = se.getArea().toLong();
        }
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigToggles) {
            final ISealConfigToggles cp = (ISealConfigToggles)se.getSeal();
            this.props = new boolean[cp.getToggles().length];
            for (int a = 0; a < cp.getToggles().length; ++a) {
                this.props[a] = cp.getToggles()[a].getValue();
            }
        }
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigFilter) {
            final ISealConfigFilter cp2 = (ISealConfigFilter)se.getSeal();
            this.blacklist = cp2.isBlacklist();
            this.filtersize = (byte)cp2.getFilterSize();
            this.filter = cp2.getInv();
            this.filterStackSize = cp2.getSizes();
        }
        this.priority = se.getPriority();
        this.color = se.getColor();
        this.locked = se.isLocked();
        this.redstone = se.isRedstoneSensitive();
        this.owner = se.getOwner();
    }
    
    public void toBytes(final ByteBuf dos) {
        dos.writeLong(this.pos.toLong());
        dos.writeByte(this.face.ordinal());
        dos.writeByte(this.priority);
        dos.writeByte(this.color);
        dos.writeBoolean(this.locked);
        dos.writeBoolean(this.redstone);
        ByteBufUtils.writeUTF8String(dos, this.owner);
        ByteBufUtils.writeUTF8String(dos, this.type);
        dos.writeBoolean(this.blacklist);
        dos.writeByte(this.filtersize);
        for (int a = 0; a < this.filtersize; ++a) {
            Utils.writeItemStackToBuffer(dos, this.filter.get(a));
            dos.writeShort(this.filterStackSize.get(a));
        }
        if (this.area != 0L) {
            dos.writeLong(this.area);
        }
        if (this.props != null) {
            for (final boolean b : this.props) {
                dos.writeBoolean(b);
            }
        }
    }
    
    public void fromBytes(final ByteBuf dat) {
        this.pos = BlockPos.fromLong(dat.readLong());
        this.face = EnumFacing.VALUES[dat.readByte()];
        this.priority = dat.readByte();
        this.color = dat.readByte();
        this.locked = dat.readBoolean();
        this.redstone = dat.readBoolean();
        this.owner = ByteBufUtils.readUTF8String(dat);
        this.type = ByteBufUtils.readUTF8String(dat);
        this.blacklist = dat.readBoolean();
        this.filtersize = dat.readByte();
        this.filter = NonNullList.withSize(this.filtersize, ItemStack.EMPTY);
        this.filterStackSize = NonNullList.withSize(this.filtersize, 0);
        for (int a = 0; a < this.filtersize; ++a) {
            this.filter.set(a, Utils.readItemStackFromBuffer(dat));
            this.filterStackSize.set(a, (int)dat.readShort());
        }
        if (!this.type.equals("REMOVE") && SealHandler.getSeal(this.type) != null) {
            if (SealHandler.getSeal(this.type) instanceof ISealConfigArea) {
                try {
                    this.area = dat.readLong();
                }
                catch (final Exception ex) {}
            }
            if (SealHandler.getSeal(this.type) instanceof ISealConfigToggles) {
                try {
                    final ISealConfigToggles cp = (ISealConfigToggles)SealHandler.getSeal(this.type);
                    this.props = new boolean[cp.getToggles().length];
                    for (int a2 = 0; a2 < cp.getToggles().length; ++a2) {
                        this.props[a2] = dat.readBoolean();
                    }
                }
                catch (final Exception ex2) {}
            }
        }
    }
    
    public IMessage onMessage(final PacketSealToClient message, final MessageContext ctx) {
        if (message.type.equals("REMOVE")) {
            SealHandler.removeSealEntity(Thaumcraft.proxy.getClientWorld(), new SealPos(message.pos, message.face), true);
        }
        else {
            try {
                final SealEntity seal = new SealEntity(Thaumcraft.proxy.getClientWorld(), new SealPos(message.pos, message.face), SealHandler.getSeal(message.type).getClass().newInstance());
                if (message.area != 0L) {
                    seal.setArea(BlockPos.fromLong(message.area));
                }
                if (message.props != null && seal.getSeal() instanceof ISealConfigToggles) {
                    final ISealConfigToggles cp = (ISealConfigToggles)seal.getSeal();
                    for (int a = 0; a < message.props.length; ++a) {
                        cp.setToggle(a, message.props[a]);
                    }
                }
                if (seal.getSeal() instanceof ISealConfigFilter) {
                    final ISealConfigFilter cp2 = (ISealConfigFilter)seal.getSeal();
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
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
