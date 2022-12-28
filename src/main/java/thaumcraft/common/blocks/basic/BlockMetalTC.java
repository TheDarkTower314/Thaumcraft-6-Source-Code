package thaumcraft.common.blocks.basic;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.blocks.BlockTC;


public class BlockMetalTC extends BlockTC
{
    public BlockMetalTC(String name) {
        super(Material.IRON, name);
        setHardness(4.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
    }
    
    public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beacon) {
        return true;
    }
}
