package thaumcraft.common.blocks.world.plants;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigItems;


public class BlockPlantShimmerleaf extends BlockBush
{
    public BlockPlantShimmerleaf() {
        super(Material.PLANTS);
        setUnlocalizedName("shimmerleaf");
        setRegistryName("thaumcraft", "shimmerleaf");
        setCreativeTab(ConfigItems.TABTC);
        setSoundType(SoundType.PLANT);
        setLightLevel(0.4f);
    }
    
    protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT;
    }
    
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Plains;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextInt(3) == 0) {
            float xr = (float)(pos.getX() + 0.5f + rand.nextGaussian() * 0.1);
            float yr = (float)(pos.getY() + 0.4f + rand.nextGaussian() * 0.1);
            float zr = (float)(pos.getZ() + 0.5f + rand.nextGaussian() * 0.1);
            FXDispatcher.INSTANCE.drawWispyMotes(xr, yr, zr, rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01, 10, 0.3f + world.rand.nextFloat() * 0.3f, 0.7f + world.rand.nextFloat() * 0.3f, 0.7f + world.rand.nextFloat() * 0.3f, 0.0f);
        }
    }
    
    public Block.EnumOffsetType getOffsetType() {
        return Block.EnumOffsetType.XZ;
    }
}
