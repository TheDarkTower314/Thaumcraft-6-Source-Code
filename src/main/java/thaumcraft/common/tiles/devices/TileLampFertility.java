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
        this.charges = 0;
        this.count = 0;
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
            if (this.charges < 10) {
                if (this.drawEssentia()) {
                    ++this.charges;
                    this.markDirty();
                    this.syncTile(true);
                }
                if (this.charges <= 1) {
                    if (BlockStateUtils.isEnabled(this.getBlockMetadata())) {
                        this.world.setBlockState(this.pos, this.world.getBlockState(this.getPos()).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false), 3);
                    }
                }
                else if (!this.gettingPower() && !BlockStateUtils.isEnabled(this.getBlockMetadata())) {
                    this.world.setBlockState(this.pos, this.world.getBlockState(this.getPos()).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)true), 3);
                }
            }
            if (!this.gettingPower() && this.charges > 1 && this.count++ % 300 == 0) {
                this.updateAnimals();
            }
        }
    }
    
    private void updateAnimals() {
        final int distance = 7;
        final List<EntityAnimal> var5 = this.world.getEntitiesWithinAABB(EntityAnimal.class, new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(distance, distance, distance));
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
                            this.charges -= 5;
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
        this.charges = nbttagcompound.getInteger("charges");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
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
            if (ic.getSuctionAmount(BlockStateUtils.getFacing(this.getBlockMetadata()).getOpposite()) < this.getSuctionAmount(BlockStateUtils.getFacing(this.getBlockMetadata())) && ic.takeEssentia(Aspect.DESIRE, 1, BlockStateUtils.getFacing(this.getBlockMetadata()).getOpposite()) == 1) {
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
        return Aspect.DESIRE;
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing face) {
        return (face == BlockStateUtils.getFacing(this.getBlockMetadata())) ? (128 - this.charges * 10) : 0;
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
