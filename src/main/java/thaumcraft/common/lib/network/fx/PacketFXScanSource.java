// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.fx;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.item.ItemStack;
import thaumcraft.common.lib.utils.BlockUtils;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.Vec3i;
import java.util.Iterator;
import net.minecraft.client.particle.Particle;
import thaumcraft.client.fx.ParticleEngine;
import java.awt.Color;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.utils.Utils;
import java.util.ArrayList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFXScanSource implements IMessage, IMessageHandler<PacketFXScanSource, IMessage>
{
    private long loc;
    private int size;
    final int C_QUARTZ = 15064789;
    final int C_IRON = 14200723;
    final int C_LAPIS = 1328572;
    final int C_GOLD = 16576075;
    final int C_DIAMOND = 6155509;
    final int C_EMERALD = 1564002;
    final int C_REDSTONE = 16711680;
    final int C_COAL = 1052688;
    final int C_SILVER = 14342653;
    final int C_TIN = 15724539;
    final int C_COPPER = 16620629;
    final int C_AMBER = 16626469;
    final int C_CINNABAR = 10159368;
    
    public PacketFXScanSource() {
    }
    
    public PacketFXScanSource(final BlockPos pos, final int size) {
        this.loc = pos.toLong();
        this.size = size;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeLong(this.loc);
        buffer.writeByte(this.size);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.loc = buffer.readLong();
        this.size = buffer.readByte();
    }
    
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final PacketFXScanSource message, final MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                PacketFXScanSource.this.startScan(Minecraft.getMinecraft().player.world, BlockPos.fromLong(message.loc), message.size);
            }
        });
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    public void startScan(final World world, final BlockPos pos, final int r) {
        final int range = 4 + r * 4;
        final ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        for (int xx = -range; xx <= range; ++xx) {
            for (int yy = -range; yy <= range; ++yy) {
                for (int zz = -range; zz <= range; ++zz) {
                    final BlockPos p = pos.add(xx, yy, zz);
                    if (Utils.isOreBlock(world, p)) {
                        positions.add(p);
                    }
                }
            }
        }
        while (!positions.isEmpty()) {
            final BlockPos start = positions.get(0);
            final ArrayList<BlockPos> coll = new ArrayList<BlockPos>();
            coll.add(start);
            positions.remove(0);
            this.calcGroup(world, start, coll, positions);
            if (!coll.isEmpty()) {
                final int c = this.getOreColor(world, start);
                double x = 0.0;
                double y = 0.0;
                double z = 0.0;
                for (final BlockPos p2 : coll) {
                    x += p2.getX() + 0.5;
                    y += p2.getY() + 0.5;
                    z += p2.getZ() + 0.5;
                }
                x /= coll.size();
                y /= coll.size();
                z /= coll.size();
                final double dis = Math.sqrt(pos.distanceSqToCenter(x, y, z));
                final FXGeneric fb = new FXGeneric(world, x, y, z, 0.0, 0.0, 0.0);
                fb.setMaxAge(44);
                final Color cc = new Color(c);
                fb.setRBGColorF(cc.getRed() / 255.0f, cc.getGreen() / 255.0f, cc.getBlue() / 255.0f);
                final float q = (cc.getRed() / 255.0f + cc.getGreen() / 255.0f + cc.getBlue() / 255.0f) / 3.0f;
                fb.setAlphaF(0.0f, 1.0f, 0.8f, 0.0f);
                fb.setParticles(240, 15, 1);
                fb.setGridSize(16);
                fb.setLoop(true);
                fb.setScale(9.0f);
                fb.setLayer((q < 0.25f) ? 3 : 2);
                fb.setRotationSpeed(0.0f);
                ParticleEngine.addEffectWithDelay(world, fb, (int)(dis * 3.0));
            }
        }
    }
    
    private void calcGroup(final World world, final BlockPos start, final ArrayList<BlockPos> coll, final ArrayList<BlockPos> positions) {
        final IBlockState bs = world.getBlockState(start);
    Label_0132:
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    final BlockPos t = new BlockPos(start).add(x, y, z);
                    final IBlockState ts = world.getBlockState(t);
                    if (ts.equals(bs) && positions.contains(t)) {
                        positions.remove(t);
                        coll.add(t);
                        if (positions.isEmpty()) {
                            break Label_0132;
                        }
                        this.calcGroup(world, t, coll, positions);
                    }
                }
            }
        }
    }
    
    private int getOreColor(final World world, final BlockPos pos) {
        final IBlockState bi = world.getBlockState(pos);
        if (bi.getBlock() != Blocks.AIR && bi.getBlock() != Blocks.BEDROCK) {
            ItemStack is = BlockUtils.getSilkTouchDrop(bi);
            if (is == null || is.isEmpty()) {
                final int md = bi.getBlock().getMetaFromState(bi);
                is = new ItemStack(bi.getBlock(), 1, md);
            }
            if (is == null || is.isEmpty() || is.getItem() == null) {
                return 12632256;
            }
            final int[] od = OreDictionary.getOreIDs(is);
            if (od != null && od.length > 0) {
                for (final int id : od) {
                    if (OreDictionary.getOreName(id) != null) {
                        if (OreDictionary.getOreName(id).toUpperCase().contains("IRON")) {
                            return 14200723;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("COAL")) {
                            return 1052688;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("REDSTONE")) {
                            return 16711680;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("GOLD")) {
                            return 16576075;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("LAPIS")) {
                            return 1328572;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("DIAMOND")) {
                            return 6155509;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("EMERALD")) {
                            return 1564002;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("QUARTZ")) {
                            return 15064789;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("SILVER")) {
                            return 14342653;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("TIN")) {
                            return 15724539;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("COPPER")) {
                            return 16620629;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("AMBER")) {
                            return 16626469;
                        }
                        if (OreDictionary.getOreName(id).toUpperCase().contains("CINNABAR")) {
                            return 10159368;
                        }
                    }
                }
            }
        }
        return 12632256;
    }
}
