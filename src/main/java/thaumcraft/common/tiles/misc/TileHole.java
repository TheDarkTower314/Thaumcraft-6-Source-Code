package thaumcraft.common.tiles.misc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.casters.foci.FocusEffectRift;


public class TileHole extends TileMemory implements ITickable
{
    public short countdown;
    public short countdownmax;
    public byte count;
    public EnumFacing direction;
    
    public TileHole() {
        countdown = 0;
        countdownmax = 120;
        count = 0;
        direction = null;
    }
    
    public TileHole(IBlockState bi, short max, byte count, EnumFacing direction) {
        super(bi);
        countdown = 0;
        countdownmax = 120;
        this.count = 0;
        this.direction = null;
        this.count = count;
        countdownmax = max;
        this.direction = direction;
    }
    
    public TileHole(byte count) {
        countdown = 0;
        countdownmax = 120;
        this.count = 0;
        direction = null;
        this.count = count;
    }
    
    public void update() {
        if (world.isRemote) {
            for (int a = 0; a < 2; ++a) {
                surroundwithsparkles();
            }
        }
        else {
            if (countdown == 0 && count > 1 && direction != null) {
                switch (direction.getAxis()) {
                    case Y: {
                        for (int a = 0; a < 9; ++a) {
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(world, getPos().add(-1 + a / 3, 0, -1 + a % 3), null, (byte)1, countdownmax);
                            }
                        }
                        break;
                    }
                    case Z: {
                        for (int a = 0; a < 9; ++a) {
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(world, getPos().add(-1 + a / 3, -1 + a % 3, 0), null, (byte)1, countdownmax);
                            }
                        }
                        break;
                    }
                    case X: {
                        for (int a = 0; a < 9; ++a) {
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(world, getPos().add(0, -1 + a / 3, -1 + a % 3), null, (byte)1, countdownmax);
                            }
                        }
                        break;
                    }
                }
                if (!FocusEffectRift.createHole(world, getPos().offset(direction.getOpposite()), direction, (byte)(count - 1), countdownmax)) {
                    count = 0;
                }
            }
            ++countdown;
            if (countdown % 20 == 0) {
                markDirty();
            }
            if (countdown >= countdownmax) {
                world.setBlockState(getPos(), oldblock, 3);
            }
        }
    }
    
    private void surroundwithsparkles() {
        for (EnumFacing d1 : EnumFacing.values()) {
            IBlockState b1 = world.getBlockState(getPos().offset(d1));
            if (b1.getBlock() != BlocksTC.hole && !b1.isOpaqueCube()) {
                for (EnumFacing d2 : EnumFacing.values()) {
                    if (d1.getAxis() != d2.getAxis() && (world.getBlockState(getPos().offset(d2)).isOpaqueCube() || world.getBlockState(getPos().offset(d1).offset(d2)).isOpaqueCube())) {
                        float sx = 0.5f * d1.getFrontOffsetX();
                        float sy = 0.5f * d1.getFrontOffsetY();
                        float sz = 0.5f * d1.getFrontOffsetZ();
                        if (sx == 0.0f) {
                            sx = 0.5f * d2.getFrontOffsetX();
                        }
                        if (sy == 0.0f) {
                            sy = 0.5f * d2.getFrontOffsetY();
                        }
                        if (sz == 0.0f) {
                            sz = 0.5f * d2.getFrontOffsetZ();
                        }
                        if (sx == 0.0f) {
                            sx = world.rand.nextFloat();
                        }
                        else {
                            sx += 0.5f;
                        }
                        if (sy == 0.0f) {
                            sy = world.rand.nextFloat();
                        }
                        else {
                            sy += 0.5f;
                        }
                        if (sz == 0.0f) {
                            sz = world.rand.nextFloat();
                        }
                        else {
                            sz += 0.5f;
                        }
                        FXDispatcher.INSTANCE.sparkle(getPos().getX() + sx, getPos().getY() + sy, getPos().getZ() + sz, 0.25f, 0.25f, 1.0f);
                    }
                }
            }
        }
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        countdown = nbttagcompound.getShort("countdown");
        countdownmax = nbttagcompound.getShort("countdownmax");
        count = nbttagcompound.getByte("count");
        byte db = nbttagcompound.getByte("direction");
        direction = ((db >= 0) ? EnumFacing.values()[db] : null);
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("countdown", countdown);
        nbttagcompound.setShort("countdownmax", countdownmax);
        nbttagcompound.setByte("count", count);
        nbttagcompound.setByte("direction", (direction == null) ? -1 : ((byte) direction.ordinal()));
        return nbttagcompound;
    }
}
