package thaumcraft.common.tiles.devices;
import java.util.Arrays;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;


public class TileDioptra extends TileThaumcraft implements ITickable
{
    public int counter;
    public byte[] grid_amt;
    private byte[] grid_amt_p;
    
    public TileDioptra() {
        counter = 0;
        grid_amt = new byte[169];
        grid_amt_p = new byte[169];
        Arrays.fill(grid_amt, (byte)0);
        Arrays.fill(grid_amt_p, (byte)0);
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX() - 0.3, getPos().getY() - 0.3, getPos().getZ() - 0.3, getPos().getX() + 1.3, getPos().getY() + 2.3, getPos().getZ() + 1.3);
    }
    
    public void update() {
        ++counter;
        if (!world.isRemote) {
            if (counter % 20 == 0) {
                Arrays.fill(grid_amt, (byte)0);
                for (int xx = 0; xx < 13; ++xx) {
                    for (int zz = 0; zz < 13; ++zz) {
                        AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), (pos.getX() >> 4) + xx - 6, (pos.getZ() >> 4) + zz - 6);
                        if (ac != null) {
                            if (BlockStateUtils.isEnabled(getBlockMetadata())) {
                                grid_amt[xx + zz * 13] = (byte)Math.min(64.0f, ac.getVis() / 500.0f * 64.0f);
                            }
                            else {
                                grid_amt[xx + zz * 13] = (byte)Math.min(64.0f, ac.getFlux() / 500.0f * 64.0f);
                            }
                        }
                    }
                }
                markDirty();
                syncTile(false);
            }
        }
        else {
            counter = 0;
        }
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("grid_a")) {
            grid_amt = nbt.getByteArray("grid_a");
        }
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
        nbt.setByteArray("grid_a", grid_amt);
        return nbt;
    }
}
