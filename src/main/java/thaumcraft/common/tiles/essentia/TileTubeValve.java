package thaumcraft.common.tiles.essentia;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.lib.SoundsTC;


public class TileTubeValve extends TileTube
{
    public boolean allowFlow;
    boolean wasPoweredLastTick;
    public float rotation;
    
    public TileTubeValve() {
        allowFlow = true;
        wasPoweredLastTick = false;
        rotation = 0.0f;
    }
    
    @Override
    public void update() {
        if (!world.isRemote && count % 5 == 0) {
            boolean gettingPower = gettingPower();
            if (wasPoweredLastTick && !gettingPower && !allowFlow) {
                allowFlow = true;
                world.playSound(null, pos, SoundsTC.squeek, SoundCategory.BLOCKS, 0.7f, 0.9f + world.rand.nextFloat() * 0.2f);
                syncTile(true);
                markDirty();
            }
            if (!wasPoweredLastTick && gettingPower && allowFlow) {
                allowFlow = false;
                world.playSound(null, pos, SoundsTC.squeek, SoundCategory.BLOCKS, 0.7f, 0.9f + world.rand.nextFloat() * 0.2f);
                syncTile(true);
                markDirty();
            }
            wasPoweredLastTick = gettingPower;
        }
        if (world.isRemote) {
            if (!allowFlow && rotation < 360.0f) {
                rotation += 20.0f;
            }
            else if (allowFlow && rotation > 0.0f) {
                rotation -= 20.0f;
            }
        }
        super.update();
    }
    
    @Override
    public boolean onCasterRightClick(World world, ItemStack wandstack, EntityPlayer player, BlockPos bp, EnumFacing side, EnumHand hand) {
        RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit == null) {
            return false;
        }
        if (hit.subHit >= 0 && hit.subHit < 6) {
            player.world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.tool, SoundCategory.BLOCKS, 0.5f, 0.9f + player.world.rand.nextFloat() * 0.2f, false);
            player.swingArm(hand);
            markDirty();
            syncTile(true);
            openSides[hit.subHit] = !openSides[hit.subHit];
            EnumFacing dir = EnumFacing.VALUES[hit.subHit];
            TileEntity tile = world.getTileEntity(pos.offset(dir));
            if (tile != null && tile instanceof TileTube) {
                ((TileTube)tile).openSides[dir.getOpposite().ordinal()] = openSides[hit.subHit];
                syncTile(true);
                tile.markDirty();
            }
            return true;
        }
        if (hit.subHit == 6) {
            player.world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.tool, SoundCategory.BLOCKS, 0.5f, 0.9f + player.world.rand.nextFloat() * 0.2f, false);
            player.swingArm(hand);
            int a = facing.ordinal();
            markDirty();
            while (++a < 20) {
                if (!canConnectSide(EnumFacing.VALUES[a % 6])) {
                    a %= 6;
                    facing = EnumFacing.VALUES[a];
                    syncTile(true);
                    markDirty();
                    break;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        allowFlow = nbttagcompound.getBoolean("flow");
        wasPoweredLastTick = nbttagcompound.getBoolean("hadpower");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound = super.writeSyncNBT(nbttagcompound);
        nbttagcompound.setBoolean("flow", allowFlow);
        nbttagcompound.setBoolean("hadpower", wasPoweredLastTick);
        return nbttagcompound;
    }
    
    @Override
    public boolean isConnectable(EnumFacing face) {
        return face != facing && super.isConnectable(face);
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
        if (allowFlow) {
            super.setSuction(aspect, amount);
        }
    }
    
    @Override
    public boolean gettingPower() {
        return world.isBlockIndirectlyGettingPowered(pos) > 0;
    }
}
