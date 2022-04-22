// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.basic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTC;

public class BlockMetalTC extends BlockTC
{
    public BlockMetalTC(final String name) {
        super(Material.IRON, name);
        setHardness(4.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
    }
    
    public boolean isBeaconBase(final IBlockAccess world, final BlockPos pos, final BlockPos beacon) {
        return true;
    }
}
