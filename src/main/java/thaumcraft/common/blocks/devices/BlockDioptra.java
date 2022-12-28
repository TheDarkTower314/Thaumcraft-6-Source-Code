package thaumcraft.common.blocks.devices;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.tiles.devices.TileDioptra;


public class BlockDioptra extends BlockTCDevice implements IBlockEnabled
{
    public BlockDioptra() {
        super(Material.ROCK, TileDioptra.class, "dioptra");
        setSoundType(SoundType.STONE);
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }
    
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileDioptra) {
            float r = ((TileDioptra)tile).grid_amt[84] / 64.0f;
            return MathHelper.floor(r * 14.0f) + ((r > 0.0f) ? 1 : 0);
        }
        return super.getComparatorInputOverride(state, world, pos);
    }
    
    @Override
    protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        boolean b = (boolean)state.getValue((IProperty)IBlockEnabled.ENABLED);
        world.setBlockState(pos, state.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)!b), 3);
        return true;
    }
}
