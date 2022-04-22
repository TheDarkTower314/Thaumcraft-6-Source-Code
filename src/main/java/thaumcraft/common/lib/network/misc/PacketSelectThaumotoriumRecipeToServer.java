// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.misc;

import net.minecraft.util.IThreadListener;
import java.util.Iterator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSelectThaumotoriumRecipeToServer implements IMessage, IMessageHandler<PacketSelectThaumotoriumRecipeToServer, IMessage>
{
    private long pos;
    private int hash;
    
    public PacketSelectThaumotoriumRecipeToServer() {
    }
    
    public PacketSelectThaumotoriumRecipeToServer(final EntityPlayer player, final BlockPos pos, final int recipeHash) {
        this.pos = pos.toLong();
        hash = recipeHash;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeLong(pos);
        buffer.writeInt(hash);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        pos = buffer.readLong();
        hash = buffer.readInt();
    }
    
    public IMessage onMessage(final PacketSelectThaumotoriumRecipeToServer message, final MessageContext ctx) {
        final IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                final World world = ctx.getServerHandler().player.getServerWorld();
                final Entity player = ctx.getServerHandler().player;
                final BlockPos bp = BlockPos.fromLong(message.pos);
                if (world != null && player != null && player instanceof EntityPlayer && bp != null) {
                    final TileEntity te = world.getTileEntity(bp);
                    if (te != null && te instanceof TileThaumatorium) {
                        final TileThaumatorium thaumatorium = (TileThaumatorium)te;
                        int i = 0;
                        boolean flag = false;
                        for (final int hash : thaumatorium.recipeHash) {
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
                            for (final CrucibleRecipe cr : thaumatorium.recipes) {
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
