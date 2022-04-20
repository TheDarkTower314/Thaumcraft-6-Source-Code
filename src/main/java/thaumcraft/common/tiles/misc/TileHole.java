// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.misc;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.items.casters.foci.FocusEffectRift;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileHole extends TileMemory implements ITickable
{
    public short countdown;
    public short countdownmax;
    public byte count;
    public EnumFacing direction;
    
    public TileHole() {
        this.countdown = 0;
        this.countdownmax = 120;
        this.count = 0;
        this.direction = null;
    }
    
    public TileHole(final IBlockState bi, final short max, final byte count, final EnumFacing direction) {
        super(bi);
        this.countdown = 0;
        this.countdownmax = 120;
        this.count = 0;
        this.direction = null;
        this.count = count;
        this.countdownmax = max;
        this.direction = direction;
    }
    
    public TileHole(final byte count) {
        this.countdown = 0;
        this.countdownmax = 120;
        this.count = 0;
        this.direction = null;
        this.count = count;
    }
    
    public void update() {
        if (this.world.isRemote) {
            for (int a = 0; a < 2; ++a) {
                this.surroundwithsparkles();
            }
        }
        else {
            if (this.countdown == 0 && this.count > 1 && this.direction != null) {
                switch (this.direction.getAxis()) {
                    case Y: {
                        for (int a = 0; a < 9; ++a) {
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(this.world, this.getPos().add(-1 + a / 3, 0, -1 + a % 3), null, (byte)1, this.countdownmax);
                            }
                        }
                        break;
                    }
                    case Z: {
                        for (int a = 0; a < 9; ++a) {
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(this.world, this.getPos().add(-1 + a / 3, -1 + a % 3, 0), null, (byte)1, this.countdownmax);
                            }
                        }
                        break;
                    }
                    case X: {
                        for (int a = 0; a < 9; ++a) {
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(this.world, this.getPos().add(0, -1 + a / 3, -1 + a % 3), null, (byte)1, this.countdownmax);
                            }
                        }
                        break;
                    }
                }
                if (!FocusEffectRift.createHole(this.world, this.getPos().offset(this.direction.getOpposite()), this.direction, (byte)(this.count - 1), this.countdownmax)) {
                    this.count = 0;
                }
            }
            ++this.countdown;
            if (this.countdown % 20 == 0) {
                this.markDirty();
            }
            if (this.countdown >= this.countdownmax) {
                this.world.setBlockState(this.getPos(), this.oldblock, 3);
            }
        }
    }
    
    private void surroundwithsparkles() {
        for (final EnumFacing d1 : EnumFacing.values()) {
            final IBlockState b1 = this.world.getBlockState(this.getPos().offset(d1));
            if (b1.getBlock() != BlocksTC.hole && !b1.isOpaqueCube()) {
                for (final EnumFacing d2 : EnumFacing.values()) {
                    if (d1.getAxis() != d2.getAxis() && (this.world.getBlockState(this.getPos().offset(d2)).isOpaqueCube() || this.world.getBlockState(this.getPos().offset(d1).offset(d2)).isOpaqueCube())) {
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
                            sx = this.world.rand.nextFloat();
                        }
                        else {
                            sx += 0.5f;
                        }
                        if (sy == 0.0f) {
                            sy = this.world.rand.nextFloat();
                        }
                        else {
                            sy += 0.5f;
                        }
                        if (sz == 0.0f) {
                            sz = this.world.rand.nextFloat();
                        }
                        else {
                            sz += 0.5f;
                        }
                        FXDispatcher.INSTANCE.sparkle(this.getPos().getX() + sx, this.getPos().getY() + sy, this.getPos().getZ() + sz, 0.25f, 0.25f, 1.0f);
                    }
                }
            }
        }
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.countdown = nbttagcompound.getShort("countdown");
        this.countdownmax = nbttagcompound.getShort("countdownmax");
        this.count = nbttagcompound.getByte("count");
        final byte db = nbttagcompound.getByte("direction");
        this.direction = ((db >= 0) ? EnumFacing.values()[db] : null);
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("countdown", this.countdown);
        nbttagcompound.setShort("countdownmax", this.countdownmax);
        nbttagcompound.setByte("count", this.count);
        nbttagcompound.setByte("direction", (this.direction == null) ? -1 : ((byte)this.direction.ordinal()));
        return nbttagcompound;
    }
}
