// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.world.aura.AuraHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.Arrays;
import net.minecraft.util.ITickable;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileDioptra extends TileThaumcraft implements ITickable
{
    public int counter;
    public byte[] grid_amt;
    private byte[] grid_amt_p;
    
    public TileDioptra() {
        this.counter = 0;
        this.grid_amt = new byte[169];
        this.grid_amt_p = new byte[169];
        Arrays.fill(this.grid_amt, (byte)0);
        Arrays.fill(this.grid_amt_p, (byte)0);
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos().getX() - 0.3, this.getPos().getY() - 0.3, this.getPos().getZ() - 0.3, this.getPos().getX() + 1.3, this.getPos().getY() + 2.3, this.getPos().getZ() + 1.3);
    }
    
    public void update() {
        ++this.counter;
        if (!this.world.isRemote) {
            if (this.counter % 20 == 0) {
                Arrays.fill(this.grid_amt, (byte)0);
                for (int xx = 0; xx < 13; ++xx) {
                    for (int zz = 0; zz < 13; ++zz) {
                        final AuraChunk ac = AuraHandler.getAuraChunk(this.world.provider.getDimension(), (this.pos.getX() >> 4) + xx - 6, (this.pos.getZ() >> 4) + zz - 6);
                        if (ac != null) {
                            if (BlockStateUtils.isEnabled(this.getBlockMetadata())) {
                                this.grid_amt[xx + zz * 13] = (byte)Math.min(64.0f, ac.getVis() / 500.0f * 64.0f);
                            }
                            else {
                                this.grid_amt[xx + zz * 13] = (byte)Math.min(64.0f, ac.getFlux() / 500.0f * 64.0f);
                            }
                        }
                    }
                }
                this.markDirty();
                this.syncTile(false);
            }
        }
        else {
            this.counter = 0;
        }
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbt) {
        if (nbt.hasKey("grid_a")) {
            this.grid_amt = nbt.getByteArray("grid_a");
        }
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbt) {
        nbt.setByteArray("grid_a", this.grid_amt);
        return nbt;
    }
}
