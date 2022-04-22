// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.Thaumcraft;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketTileToClient implements IMessage, IMessageHandler<PacketTileToClient, IMessage>
{
    private long pos;
    private NBTTagCompound nbt;
    
    public PacketTileToClient() {
    }
    
    public PacketTileToClient(BlockPos pos, NBTTagCompound nbt) {
        this.pos = pos.toLong();
        this.nbt = nbt;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(pos);
        Utils.writeNBTTagCompoundToBuffer(buffer, nbt);
    }
    
    public void fromBytes(ByteBuf buffer) {
        pos = buffer.readLong();
        nbt = Utils.readNBTTagCompoundFromBuffer(buffer);
    }
    
    public IMessage onMessage(PacketTileToClient message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                World world = Thaumcraft.proxy.getClientWorld();
                BlockPos bp = BlockPos.fromLong(message.pos);
                if (world != null && bp != null) {
                    TileEntity te = world.getTileEntity(bp);
                    if (te != null && te instanceof TileThaumcraft) {
                        ((TileThaumcraft)te).messageFromServer((message.nbt == null) ? new NBTTagCompound() : message.nbt);
                    }
                }
            }
        });
        return null;
    }
}
