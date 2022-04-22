// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.basic;

import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTC;

public class BlockStoneTC extends BlockTC
{
    private boolean spawn;
    
    public BlockStoneTC(final String name, final boolean spawn) {
        super(Material.ROCK, name);
        this.spawn = spawn;
        setHardness(2.0f);
        setResistance(10.0f);
        setSoundType(SoundType.STONE);
    }
    
    public boolean isBeaconBase(final IBlockAccess world, final BlockPos pos, final BlockPos beacon) {
        return true;
    }
    
    public boolean canEntityDestroy(final IBlockState state, final IBlockAccess world, final BlockPos pos, final Entity entity) {
        return blockHardness >= 0.0f;
    }
}
