// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.Thaumcraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSealFilterToClient implements IMessage, IMessageHandler<PacketSealFilterToClient, IMessage>
{
    BlockPos pos;
    EnumFacing face;
    byte filtersize;
    NonNullList<ItemStack> filter;
    NonNullList<Integer> filterStackSize;
    
    public PacketSealFilterToClient() {
    }
    
    public PacketSealFilterToClient(final ISealEntity se) {
        this.pos = se.getSealPos().pos;
        this.face = se.getSealPos().face;
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigFilter) {
            final ISealConfigFilter cp = (ISealConfigFilter)se.getSeal();
            this.filtersize = (byte)cp.getFilterSize();
            this.filter = cp.getInv();
            this.filterStackSize = cp.getSizes();
        }
    }
    
    public void toBytes(final ByteBuf dos) {
        dos.writeLong(this.pos.toLong());
        dos.writeByte(this.face.ordinal());
        dos.writeByte(this.filtersize);
        for (int a = 0; a < this.filtersize; ++a) {
            Utils.writeItemStackToBuffer(dos, this.filter.get(a));
            dos.writeShort(this.filterStackSize.get(a));
        }
    }
    
    public void fromBytes(final ByteBuf dat) {
        this.pos = BlockPos.fromLong(dat.readLong());
        this.face = EnumFacing.VALUES[dat.readByte()];
        this.filtersize = dat.readByte();
        this.filter = NonNullList.withSize(this.filtersize, ItemStack.EMPTY);
        this.filterStackSize = NonNullList.withSize(this.filtersize, 0);
        for (int a = 0; a < this.filtersize; ++a) {
            this.filter.set(a, Utils.readItemStackFromBuffer(dat));
            this.filterStackSize.set(a, (int)dat.readShort());
        }
    }
    
    public IMessage onMessage(final PacketSealFilterToClient message, final MessageContext ctx) {
        try {
            final ISealEntity seal = SealHandler.getSealEntity(Thaumcraft.proxy.getClientWorld().provider.getDimension(), new SealPos(message.pos, message.face));
            if (seal != null && seal.getSeal() instanceof ISealConfigFilter) {
                final ISealConfigFilter cp = (ISealConfigFilter)seal.getSeal();
                for (int a = 0; a < message.filtersize; ++a) {
                    cp.setFilterSlot(a, message.filter.get(a));
                    cp.setFilterSlotSize(a, message.filterStackSize.get(a));
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
