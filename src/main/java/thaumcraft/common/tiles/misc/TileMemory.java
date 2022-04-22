// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.misc;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;

public class TileMemory extends TileEntity
{
    public IBlockState oldblock;
    public NBTTagCompound tileEntityCompound;
    
    public TileMemory() {
        oldblock = Blocks.AIR.getDefaultState();
    }
    
    public TileMemory(final IBlockState bi) {
        oldblock = Blocks.AIR.getDefaultState();
        oldblock = bi;
    }
    
    public void readFromNBT(final NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        final Block b = Block.getBlockById(nbttagcompound.getInteger("oldblock"));
        final int meta = nbttagcompound.getInteger("oldmeta");
        oldblock = b.getStateFromMeta(meta);
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("oldblock", Block.getIdFromBlock(oldblock.getBlock()));
        nbttagcompound.setInteger("oldmeta", oldblock.getBlock().getMetaFromState(oldblock));
        return nbttagcompound;
    }
}
