package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.items.tools.ItemElementalShovel;


public class PacketItemKeyToServer implements IMessage, IMessageHandler<PacketItemKeyToServer, IMessage>
{
    private byte key;
    private byte mod;
    
    public PacketItemKeyToServer() {
    }
    
    public PacketItemKeyToServer(int key) {
        this.key = (byte)key;
        mod = 0;
    }
    
    public PacketItemKeyToServer(int key, int mod) {
        this.key = (byte)key;
        this.mod = (byte)mod;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(key);
        buffer.writeByte(mod);
    }
    
    public void fromBytes(ByteBuf buffer) {
        key = buffer.readByte();
        mod = buffer.readByte();
    }
    
    public IMessage onMessage(PacketItemKeyToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                World world = ctx.getServerHandler().player.getServerWorld();
                if (world == null) {
                    return;
                }
                Entity player = ctx.getServerHandler().player;
                if (player != null && player instanceof EntityPlayer) {
                    boolean flag = false;
                    if (((EntityPlayer)player).getHeldItemMainhand() != null) {
                        if (message.key == 1 && ((EntityPlayer)player).getHeldItemMainhand().getItem() instanceof ICaster) {
                            CasterManager.toggleMisc(((EntityPlayer)player).getHeldItemMainhand(), world, (EntityPlayer)player, message.mod);
                            flag = true;
                        }
                        if (!flag && message.key == 1 && ((EntityPlayer)player).getHeldItemOffhand().getItem() instanceof ICaster) {
                            CasterManager.toggleMisc(((EntityPlayer)player).getHeldItemOffhand(), world, (EntityPlayer)player, message.mod);
                        }
                        if (message.key == 1 && ((EntityPlayer)player).getHeldItemMainhand().getItem() instanceof ItemElementalShovel) {
                            ItemElementalShovel itemElementalShovel = (ItemElementalShovel)((EntityPlayer)player).getHeldItemMainhand().getItem();
                            byte b = ItemElementalShovel.getOrientation(((EntityPlayer)player).getHeldItemMainhand());
                            ItemElementalShovel itemElementalShovel2 = (ItemElementalShovel)((EntityPlayer)player).getHeldItemMainhand().getItem();
                            ItemElementalShovel.setOrientation(((EntityPlayer)player).getHeldItemMainhand(), (byte)(b + 1));
                            flag = true;
                        }
                    }
                }
            }
        });
        return null;
    }
}
