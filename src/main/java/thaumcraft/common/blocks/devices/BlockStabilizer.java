package thaumcraft.common.blocks.devices;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.devices.TileStabilizer;


public class BlockStabilizer extends BlockTCDevice implements IInfusionStabiliserExt
{
    public BlockStabilizer() {
        super(Material.ROCK, TileStabilizer.class, "stabilizer");
        setSoundType(SoundType.STONE);
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    @SideOnly(Side.CLIENT)
    public static int colorMultiplier(int meta) {
        float f = meta / 15.0f;
        float f2 = f * 0.5f + 0.5f;
        if (meta == 0) {
            f2 = 0.3f;
        }
        int i = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        int j = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        int k = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        return 0xFF000000 | i << 16 | j << 8 | k;
    }
    
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public int getLightValue(IBlockState state) {
        return 4;
    }
    
    @Override
    public boolean canStabaliseInfusion(World world, BlockPos pos) {
        return true;
    }
    
    @Override
    public float getStabilizationAmount(World world, BlockPos pos) {
        return 0.25f;
    }
}
