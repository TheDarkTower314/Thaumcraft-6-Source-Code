package thaumcraft.common.blocks.world.plants;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigItems;


public class BlockPlantCinderpearl extends BlockBush
{
    public BlockPlantCinderpearl() {
        super(Material.PLANTS);
        setUnlocalizedName("cinderpearl");
        setRegistryName("thaumcraft", "cinderpearl");
        setCreativeTab(ConfigItems.TABTC);
        setSoundType(SoundType.PLANT);
        setLightLevel(0.5f);
    }
    
    protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() == Blocks.SAND || state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.STAINED_HARDENED_CLAY || state.getBlock() == Blocks.HARDENED_CLAY;
    }
    
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Desert;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextBoolean()) {
            float xr = pos.getX() + 0.5f + (rand.nextFloat() - rand.nextFloat()) * 0.1f;
            float yr = pos.getY() + 0.6f + (rand.nextFloat() - rand.nextFloat()) * 0.1f;
            float zr = pos.getZ() + 0.5f + (rand.nextFloat() - rand.nextFloat()) * 0.1f;
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xr, yr, zr, 0.0, 0.0, 0.0);
            world.spawnParticle(EnumParticleTypes.FLAME, xr, yr, zr, 0.0, 0.0, 0.0);
        }
    }
    
    public Block.EnumOffsetType getOffsetType() {
        return Block.EnumOffsetType.XZ;
    }
}
