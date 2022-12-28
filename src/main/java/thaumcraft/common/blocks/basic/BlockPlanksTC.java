package thaumcraft.common.blocks.basic;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.blocks.BlockTC;


public class BlockPlanksTC extends BlockTC
{
    public BlockPlanksTC(String name) {
        super(Material.WOOD, name);
        setHarvestLevel("axe", 0);
        setHardness(2.0f);
        setSoundType(SoundType.WOOD);
    }
    
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 20;
    }
    
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 5;
    }
}
