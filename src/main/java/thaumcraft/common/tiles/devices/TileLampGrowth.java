package thaumcraft.common.tiles.devices;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockMist;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.CropUtils;
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
        reserve = false;
        charges = -1;
        maxCharges = 20;
        lx = 0;
        ly = 0;
        lz = 0;
        lid = Blocks.AIR;
        lmd = 0;
        checklist = new ArrayList<BlockPos>();
        drawDelay = 0;
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (world != null && world.isRemote) {
            world.checkLightFor(EnumSkyBlock.BLOCK, getPos());
        }
    }
    
    public void update() {
        if (!world.isRemote) {
            if (charges <= 0) {
                if (reserve) {
                    charges = maxCharges;
                    reserve = false;
                    markDirty();
                    syncTile(true);
                }
                else if (drawEssentia()) {
                    charges = maxCharges;
                    markDirty();
                    syncTile(true);
                }
                if (charges <= 0) {
                    if (BlockStateUtils.isEnabled(getBlockMetadata())) {
                        world.setBlockState(pos, world.getBlockState(getPos()).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false), 3);
                    }
                }
                else if (!gettingPower() && !BlockStateUtils.isEnabled(getBlockMetadata())) {
                    world.setBlockState(pos, world.getBlockState(getPos()).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)true), 3);
                }
            }
            if (!reserve && drawEssentia()) {
                reserve = true;
            }
            if (charges == 0) {
                charges = -1;
                syncTile(true);
            }
            if (!gettingPower() && charges > 0) {
                updatePlant();
            }
        }
    }
    
    boolean isPlant(BlockPos bp) {
        IBlockState b = world.getBlockState(bp);
        boolean flag = b.getBlock() instanceof IGrowable;
        Material mat = b.getMaterial();
        return (flag || mat == Material.CACTUS || mat == Material.PLANTS) && mat != Material.GRASS;
    }
    
    private void updatePlant() {
        IBlockState bs = world.getBlockState(new BlockPos(lx, ly, lz));
        if (lid != bs.getBlock() || lmd != bs.getBlock().getMetaFromState(bs)) {
            EntityPlayer p = world.getClosestPlayer(lx, ly, lz, 32.0, false);
            if (p != null) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockMist(new BlockPos(lx, ly, lz), 4259648), new NetworkRegistry.TargetPoint(world.provider.getDimension(), lx, ly, lz, 32.0));
            }
            lid = bs.getBlock();
            lmd = bs.getBlock().getMetaFromState(bs);
        }
        int distance = 6;
        if (checklist.size() == 0) {
            for (int a = -distance; a <= distance; ++a) {
                for (int b = -distance; b <= distance; ++b) {
                    checklist.add(getPos().add(a, distance, b));
                }
            }
            Collections.shuffle(checklist, world.rand);
        }
        int x = checklist.get(0).getX();
        int y = checklist.get(0).getY();
        int z = checklist.get(0).getZ();
        checklist.remove(0);
        while (y >= pos.getY() - distance) {
            BlockPos bp = new BlockPos(x, y, z);
            if (!world.isAirBlock(bp) && isPlant(bp) && getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < distance * distance && !CropUtils.isGrownCrop(world, bp) && CropUtils.doesLampGrow(world, bp)) {
                --charges;
                lx = x;
                ly = y;
                lz = z;
                IBlockState bs2 = world.getBlockState(bp);
                lid = bs2.getBlock();
                lmd = bs2.getBlock().getMetaFromState(bs2);
                world.scheduleUpdate(bp, lid, 1);
                return;
            }
            --y;
        }
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        reserve = nbttagcompound.getBoolean("reserve");
        charges = nbttagcompound.getInteger("charges");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("reserve", reserve);
        nbttagcompound.setInteger("charges", charges);
        return nbttagcompound;
    }
    
    boolean drawEssentia() {
        if (++drawDelay % 5 != 0) {
            return false;
        }
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, getPos(), BlockStateUtils.getFacing(getBlockMetadata()));
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(BlockStateUtils.getFacing(getBlockMetadata()).getOpposite())) {
                return false;
            }
            if (ic.getSuctionAmount(BlockStateUtils.getFacing(getBlockMetadata()).getOpposite()) < getSuctionAmount(BlockStateUtils.getFacing(getBlockMetadata())) && ic.takeEssentia(Aspect.PLANT, 1, BlockStateUtils.getFacing(getBlockMetadata()).getOpposite()) == 1) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isConnectable(EnumFacing face) {
        return face == BlockStateUtils.getFacing(getBlockMetadata());
    }
    
    @Override
    public boolean canInputFrom(EnumFacing face) {
        return face == BlockStateUtils.getFacing(getBlockMetadata());
    }
    
    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return Aspect.PLANT;
    }
    
    @Override
    public int getSuctionAmount(EnumFacing face) {
        return (face == BlockStateUtils.getFacing(getBlockMetadata()) && (!reserve || charges <= 0)) ? 128 : 0;
    }
    
    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing loc) {
        return 0;
    }
}
