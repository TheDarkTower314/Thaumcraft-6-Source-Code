// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import net.minecraft.util.EnumFacing;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.lib.utils.CropUtils;
import java.util.List;
import java.util.Collections;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXBlockMist;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.util.ITickable;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileLampGrowth extends TileThaumcraft implements IEssentiaTransport, ITickable
{
    private boolean reserve;
    public int charges;
    public int maxCharges;
    int lx;
    int ly;
    int lz;
    Block lid;
    int lmd;
    ArrayList<BlockPos> checklist;
    int drawDelay;
    
    public TileLampGrowth() {
        this.reserve = false;
        this.charges = -1;
        this.maxCharges = 20;
        this.lx = 0;
        this.ly = 0;
        this.lz = 0;
        this.lid = Blocks.AIR;
        this.lmd = 0;
        this.checklist = new ArrayList<BlockPos>();
        this.drawDelay = 0;
    }
    
    @Override
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (this.world != null && this.world.isRemote) {
            this.world.checkLightFor(EnumSkyBlock.BLOCK, this.getPos());
        }
    }
    
    public void update() {
        if (!this.world.isRemote) {
            if (this.charges <= 0) {
                if (this.reserve) {
                    this.charges = this.maxCharges;
                    this.reserve = false;
                    this.markDirty();
                    this.syncTile(true);
                }
                else if (this.drawEssentia()) {
                    this.charges = this.maxCharges;
                    this.markDirty();
                    this.syncTile(true);
                }
                if (this.charges <= 0) {
                    if (BlockStateUtils.isEnabled(this.getBlockMetadata())) {
                        this.world.setBlockState(this.pos, this.world.getBlockState(this.getPos()).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false), 3);
                    }
                }
                else if (!this.gettingPower() && !BlockStateUtils.isEnabled(this.getBlockMetadata())) {
                    this.world.setBlockState(this.pos, this.world.getBlockState(this.getPos()).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)true), 3);
                }
            }
            if (!this.reserve && this.drawEssentia()) {
                this.reserve = true;
            }
            if (this.charges == 0) {
                this.charges = -1;
                this.syncTile(true);
            }
            if (!this.gettingPower() && this.charges > 0) {
                this.updatePlant();
            }
        }
    }
    
    boolean isPlant(final BlockPos bp) {
        final IBlockState b = this.world.getBlockState(bp);
        final boolean flag = b.getBlock() instanceof IGrowable;
        final Material mat = b.getMaterial();
        return (flag || mat == Material.CACTUS || mat == Material.PLANTS) && mat != Material.GRASS;
    }
    
    private void updatePlant() {
        final IBlockState bs = this.world.getBlockState(new BlockPos(this.lx, this.ly, this.lz));
        if (this.lid != bs.getBlock() || this.lmd != bs.getBlock().getMetaFromState(bs)) {
            final EntityPlayer p = this.world.getClosestPlayer(this.lx, this.ly, this.lz, 32.0, false);
            if (p != null) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockMist(new BlockPos(this.lx, this.ly, this.lz), 4259648), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.lx, this.ly, this.lz, 32.0));
            }
            this.lid = bs.getBlock();
            this.lmd = bs.getBlock().getMetaFromState(bs);
        }
        final int distance = 6;
        if (this.checklist.size() == 0) {
            for (int a = -distance; a <= distance; ++a) {
                for (int b = -distance; b <= distance; ++b) {
                    this.checklist.add(this.getPos().add(a, distance, b));
                }
            }
            Collections.shuffle(this.checklist, this.world.rand);
        }
        final int x = this.checklist.get(0).getX();
        int y = this.checklist.get(0).getY();
        final int z = this.checklist.get(0).getZ();
        this.checklist.remove(0);
        while (y >= this.pos.getY() - distance) {
            final BlockPos bp = new BlockPos(x, y, z);
            if (!this.world.isAirBlock(bp) && this.isPlant(bp) && this.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < distance * distance && !CropUtils.isGrownCrop(this.world, bp) && CropUtils.doesLampGrow(this.world, bp)) {
                --this.charges;
                this.lx = x;
                this.ly = y;
                this.lz = z;
                final IBlockState bs2 = this.world.getBlockState(bp);
                this.lid = bs2.getBlock();
                this.lmd = bs2.getBlock().getMetaFromState(bs2);
                this.world.scheduleUpdate(bp, this.lid, 1);
                return;
            }
            --y;
        }
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        this.reserve = nbttagcompound.getBoolean("reserve");
        this.charges = nbttagcompound.getInteger("charges");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("reserve", this.reserve);
        nbttagcompound.setInteger("charges", this.charges);
        return nbttagcompound;
    }
    
    boolean drawEssentia() {
        if (++this.drawDelay % 5 != 0) {
            return false;
        }
        final TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.getPos(), BlockStateUtils.getFacing(this.getBlockMetadata()));
        if (te != null) {
            final IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(BlockStateUtils.getFacing(this.getBlockMetadata()).getOpposite())) {
                return false;
            }
            if (ic.getSuctionAmount(BlockStateUtils.getFacing(this.getBlockMetadata()).getOpposite()) < this.getSuctionAmount(BlockStateUtils.getFacing(this.getBlockMetadata())) && ic.takeEssentia(Aspect.PLANT, 1, BlockStateUtils.getFacing(this.getBlockMetadata()).getOpposite()) == 1) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return face == BlockStateUtils.getFacing(this.getBlockMetadata());
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return face == BlockStateUtils.getFacing(this.getBlockMetadata());
    }
    
    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return false;
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public Aspect getSuctionType(final EnumFacing face) {
        return Aspect.PLANT;
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing face) {
        return (face == BlockStateUtils.getFacing(this.getBlockMetadata()) && (!this.reserve || this.charges <= 0)) ? 128 : 0;
    }
    
    @Override
    public Aspect getEssentiaType(final EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getEssentiaAmount(final EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing loc) {
        return 0;
    }
}
