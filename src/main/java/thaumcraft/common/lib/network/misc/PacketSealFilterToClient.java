package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.Thaumcraft;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.lib.utils.Utils;


public class PacketSealFilterToClient implements IMessage, IMessageHandler<PacketSealFilterToClient, IMessage>
{
    BlockPos pos;
    EnumFacing face;
    byte filtersize;
    NonNullList<ItemStack> filter;
    NonNullList<Integer> filterStackSize;
    
    public PacketSealFilterToClient() {
    }
    
    public PacketSealFilterToClient(ISealEntity se) {
        pos = se.getSealPos().pos;
        face = se.getSealPos().face;
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigFilter) {
            ISealConfigFilter cp = (ISealConfigFilter)se.getSeal();
            filtersize = (byte)cp.getFilterSize();
            filter = cp.getInv();
            filterStackSize = cp.getSizes();
        }
    }
    
    public void toBytes(ByteBuf dos) {
        dos.writeLong(pos.toLong());
        dos.writeByte(face.ordinal());
        dos.writeByte(filtersize);
        for (int a = 0; a < filtersize; ++a) {
            Utils.writeItemStackToBuffer(dos, filter.get(a));
            dos.writeShort(filterStackSize.get(a));
        }
    }
    
    public void fromBytes(ByteBuf dat) {
        pos = BlockPos.fromLong(dat.readLong());
        face = EnumFacing.VALUES[dat.readByte()];
        filtersize = dat.readByte();
        filter = NonNullList.withSize(filtersize, ItemStack.EMPTY);
        filterStackSize = NonNullList.withSize(filtersize, 0);
        for (int a = 0; a < filtersize; ++a) {
            filter.set(a, Utils.readItemStackFromBuffer(dat));
            filterStackSize.set(a, (int)dat.readShort());
        }
    }
    
    public IMessage onMessage(PacketSealFilterToClient message, MessageContext ctx) {
        try {
            ISealEntity seal = SealHandler.getSealEntity(Thaumcraft.proxy.getClientWorld().provider.getDimension(), new SealPos(message.pos, message.face));
            if (seal != null && seal.getSeal() instanceof ISealConfigFilter) {
                ISealConfigFilter cp = (ISealConfigFilter)seal.getSeal();
                for (int a = 0; a < message.filtersize; ++a) {
                    cp.setFilterSlot(a, message.filter.get(a));
                    cp.setFilterSlotSize(a, message.filterStackSize.get(a));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
