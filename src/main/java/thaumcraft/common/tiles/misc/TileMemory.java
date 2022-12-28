package thaumcraft.common.tiles.misc;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;


public class TileMemory extends TileEntity
{
    public IBlockState oldblock;
    public NBTTagCompound tileEntityCompound;
    
    public TileMemory() {
        oldblock = Blocks.AIR.getDefaultState();
    }
    
    public TileMemory(IBlockState bi) {
        oldblock = Blocks.AIR.getDefaultState();
        oldblock = bi;
    }
    
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        Block b = Block.getBlockById(nbttagcompound.getInteger("oldblock"));
        int meta = nbttagcompound.getInteger("oldmeta");
        oldblock = b.getStateFromMeta(meta);
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("oldblock", Block.getIdFromBlock(oldblock.getBlock()));
        nbttagcompound.setInteger("oldmeta", oldblock.getBlock().getMetaFromState(oldblock));
        return nbttagcompound;
    }
}
