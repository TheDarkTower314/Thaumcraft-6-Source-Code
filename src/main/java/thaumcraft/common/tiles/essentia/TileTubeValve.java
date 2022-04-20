// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.essentia;

import thaumcraft.api.aspects.Aspect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;

public class TileTubeValve extends TileTube
{
    public boolean allowFlow;
    boolean wasPoweredLastTick;
    public float rotation;
    
    public TileTubeValve() {
        this.allowFlow = true;
        this.wasPoweredLastTick = false;
        this.rotation = 0.0f;
    }
    
    @Override
    public void update() {
        if (!this.world.isRemote && this.count % 5 == 0) {
            final boolean gettingPower = this.gettingPower();
            if (this.wasPoweredLastTick && !gettingPower && !this.allowFlow) {
                this.allowFlow = true;
                this.world.playSound(null, this.pos, SoundsTC.squeek, SoundCategory.BLOCKS, 0.7f, 0.9f + this.world.rand.nextFloat() * 0.2f);
                this.syncTile(true);
                this.markDirty();
            }
            if (!this.wasPoweredLastTick && gettingPower && this.allowFlow) {
                this.allowFlow = false;
                this.world.playSound(null, this.pos, SoundsTC.squeek, SoundCategory.BLOCKS, 0.7f, 0.9f + this.world.rand.nextFloat() * 0.2f);
                this.syncTile(true);
                this.markDirty();
            }
            this.wasPoweredLastTick = gettingPower;
        }
        if (this.world.isRemote) {
            if (!this.allowFlow && this.rotation < 360.0f) {
                this.rotation += 20.0f;
            }
            else if (this.allowFlow && this.rotation > 0.0f) {
                this.rotation -= 20.0f;
            }
        }
        super.update();
    }
    
    @Override
    public boolean onCasterRightClick(final World world, final ItemStack wandstack, final EntityPlayer player, final BlockPos bp, final EnumFacing side, final EnumHand hand) {
        final RayTraceResult hit = RayTracer.retraceBlock(world, player, this.pos);
        if (hit == null) {
            return false;
        }
        if (hit.subHit >= 0 && hit.subHit < 6) {
            player.world.playSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), SoundsTC.tool, SoundCategory.BLOCKS, 0.5f, 0.9f + player.world.rand.nextFloat() * 0.2f, false);
            player.swingArm(hand);
            this.markDirty();
            this.syncTile(true);
            this.openSides[hit.subHit] = !this.openSides[hit.subHit];
            final EnumFacing dir = EnumFacing.VALUES[hit.subHit];
            final TileEntity tile = world.getTileEntity(this.pos.offset(dir));
            if (tile != null && tile instanceof TileTube) {
                ((TileTube)tile).openSides[dir.getOpposite().ordinal()] = this.openSides[hit.subHit];
                this.syncTile(true);
                tile.markDirty();
            }
            return true;
        }
        if (hit.subHit == 6) {
            player.world.playSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), SoundsTC.tool, SoundCategory.BLOCKS, 0.5f, 0.9f + player.world.rand.nextFloat() * 0.2f, false);
            player.swingArm(hand);
            int a = this.facing.ordinal();
            this.markDirty();
            while (++a < 20) {
                if (!this.canConnectSide(EnumFacing.VALUES[a % 6])) {
                    a %= 6;
                    this.facing = EnumFacing.VALUES[a];
                    this.syncTile(true);
                    this.markDirty();
                    break;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        this.allowFlow = nbttagcompound.getBoolean("flow");
        this.wasPoweredLastTick = nbttagcompound.getBoolean("hadpower");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound = super.writeSyncNBT(nbttagcompound);
        nbttagcompound.setBoolean("flow", this.allowFlow);
        nbttagcompound.setBoolean("hadpower", this.wasPoweredLastTick);
        return nbttagcompound;
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return face != this.facing && super.isConnectable(face);
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
        if (this.allowFlow) {
            super.setSuction(aspect, amount);
        }
    }
    
    @Override
    public boolean gettingPower() {
        return this.world.isBlockIndirectlyGettingPowered(this.pos) > 0;
    }
}
