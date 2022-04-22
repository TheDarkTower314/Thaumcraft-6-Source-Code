// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import net.minecraft.util.EnumFacing;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import java.util.ArrayList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.block.properties.IProperty;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ITickable;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileLampFertility extends TileThaumcraft implements IEssentiaTransport, ITickable
{
    public int charges;
    int count;
    int drawDelay;
    
    public TileLampFertility() {
        charges = 0;
        count = 0;
        drawDelay = 0;
    }
    
    @Override
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (world != null && world.isRemote) {
            world.checkLightFor(EnumSkyBlock.BLOCK, getPos());
        }
    }
    
    public void update() {
        if (!world.isRemote) {
            if (charges < 10) {
                if (drawEssentia()) {
                    ++charges;
                    markDirty();
                    syncTile(true);
                }
                if (charges <= 1) {
                    if (BlockStateUtils.isEnabled(getBlockMetadata())) {
                        world.setBlockState(pos, world.getBlockState(getPos()).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false), 3);
                    }
                }
                else if (!gettingPower() && !BlockStateUtils.isEnabled(getBlockMetadata())) {
                    world.setBlockState(pos, world.getBlockState(getPos()).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)true), 3);
                }
            }
            if (!gettingPower() && charges > 1 && count++ % 300 == 0) {
                updateAnimals();
            }
        }
    }
    
    private void updateAnimals() {
        final int distance = 7;
        final List<EntityAnimal> var5 = world.getEntitiesWithinAABB(EntityAnimal.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(distance, distance, distance));
    Label_0314:
        for (final EntityLivingBase var8 : var5) {
            final EntityAnimal var7 = (EntityAnimal)var8;
            if (var7.getGrowingAge() == 0) {
                if (var7.isInLove()) {
                    continue;
                }
                final ArrayList<EntityAnimal> sa = new ArrayList<EntityAnimal>();
                for (final EntityLivingBase var9 : var5) {
                    if (var9.getClass().equals(var8.getClass())) {
                        sa.add((EntityAnimal)var9);
                    }
                }
                if (sa != null && sa.size() > 9) {
                    continue;
                }
                final Iterator<EntityAnimal> var10 = sa.iterator();
                EntityAnimal partner = null;
                while (var10.hasNext()) {
                    final EntityAnimal var11 = var10.next();
                    if (var11.getGrowingAge() == 0) {
                        if (var11.isInLove()) {
                            continue;
                        }
                        if (partner != null) {
                            charges -= 5;
                            var11.setInLove(null);
                            partner.setInLove(null);
                            break Label_0314;
                        }
                        partner = var11;
                    }
                }
            }
        }
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        charges = nbttagcompound.getInteger("charges");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("charges", charges);
        return nbttagcompound;
    }
    
    boolean drawEssentia() {
        if (++drawDelay % 5 != 0) {
            return false;
        }
        final TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, getPos(), BlockStateUtils.getFacing(getBlockMetadata()));
        if (te != null) {
            final IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(BlockStateUtils.getFacing(getBlockMetadata()).getOpposite())) {
                return false;
            }
            if (ic.getSuctionAmount(BlockStateUtils.getFacing(getBlockMetadata()).getOpposite()) < getSuctionAmount(BlockStateUtils.getFacing(getBlockMetadata())) && ic.takeEssentia(Aspect.DESIRE, 1, BlockStateUtils.getFacing(getBlockMetadata()).getOpposite()) == 1) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return face == BlockStateUtils.getFacing(getBlockMetadata());
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return face == BlockStateUtils.getFacing(getBlockMetadata());
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
        return Aspect.DESIRE;
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing face) {
        return (face == BlockStateUtils.getFacing(getBlockMetadata())) ? (128 - charges * 10) : 0;
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
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing facing) {
        return 0;
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing facing) {
        return 0;
    }
}
