package thaumcraft.common.blocks.crafting;
import java.util.List;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;


@Mod.EventBusSubscriber({ Side.CLIENT })
public class BlockVoidSiphon extends BlockTCDevice implements IBlockEnabled
{
    protected static AxisAlignedBB AABB_MAIN;
    protected static AxisAlignedBB AABB_BASE;
    protected static AxisAlignedBB AABB_TOP;
    protected static AxisAlignedBB AABB_ORB;
    
    public BlockVoidSiphon() {
        super(Material.IRON, TileVoidSiphon.class, "void_siphon");
        setSoundType(SoundType.METAL);
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
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }
    
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB AABB, List<AxisAlignedBB> list, Entity p_185477_6_, boolean isActualState) {
        addCollisionBoxToList(pos, AABB, list, BlockVoidSiphon.AABB_BASE);
        addCollisionBoxToList(pos, AABB, list, BlockVoidSiphon.AABB_TOP);
        addCollisionBoxToList(pos, AABB, list, BlockVoidSiphon.AABB_ORB);
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockVoidSiphon.AABB_MAIN;
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        player.openGui(Thaumcraft.instance, 22, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    static {
        AABB_MAIN = new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 1.0, 0.8125);
        AABB_BASE = new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.125, 0.8125);
        AABB_TOP = new AxisAlignedBB(0.25, 0.125, 0.25, 0.75, 0.6875, 0.75);
        AABB_ORB = new AxisAlignedBB(0.3125, 0.75, 0.3125, 0.625, 1.0, 0.625);
    }
}
