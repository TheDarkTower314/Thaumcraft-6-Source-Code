package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


public class PacketSelectThaumotoriumRecipeToServer implements IMessage, IMessageHandler<PacketSelectThaumotoriumRecipeToServer, IMessage>
{
    private long pos;
    private int hash;
    
    public PacketSelectThaumotoriumRecipeToServer() {
    }
    
    public PacketSelectThaumotoriumRecipeToServer(EntityPlayer player, BlockPos pos, int recipeHash) {
        this.pos = pos.toLong();
        hash = recipeHash;
    }
    
    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(pos);
        buffer.writeInt(hash);
    }
    
    public void fromBytes(ByteBuf buffer) {
        pos = buffer.readLong();
        hash = buffer.readInt();
    }
    
    public IMessage onMessage(PacketSelectThaumotoriumRecipeToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                World world = ctx.getServerHandler().player.getServerWorld();
                Entity player = ctx.getServerHandler().player;
                BlockPos bp = BlockPos.fromLong(message.pos);
                if (world != null && player != null && player instanceof EntityPlayer && bp != null) {
                    TileEntity te = world.getTileEntity(bp);
                    if (te != null && te instanceof TileThaumatorium) {
                        TileThaumatorium thaumatorium = (TileThaumatorium)te;
                        int i = 0;
                        boolean flag = false;
                        for (int hash : thaumatorium.recipeHash) {
                            if (message.hash == hash) {
                                thaumatorium.recipeEssentia.remove(i);
                                thaumatorium.recipePlayer.remove(i);
                                thaumatorium.recipeHash.remove(i);
                                thaumatorium.currentCraft = -1;
                                flag = true;
                                break;
                            }
                            ++i;
                        }
                        if (!flag && thaumatorium.recipeHash.size() < thaumatorium.maxRecipes) {
                            for (CrucibleRecipe cr : thaumatorium.recipes) {
                                if (cr.hash == message.hash) {
                                    thaumatorium.recipeEssentia.add(cr.getAspects().copy());
                                    thaumatorium.recipePlayer.add(player.getName());
                                    thaumatorium.recipeHash.add(cr.hash);
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if (flag) {
                            thaumatorium.markDirty();
                            thaumatorium.syncTile(false);
                        }
                    }
                }
            }
        });
        return null;
    }
}
