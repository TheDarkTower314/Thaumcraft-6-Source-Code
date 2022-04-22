// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.crafting;

import thaumcraft.Thaumcraft;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.BlockTCDevice;

@Mod.EventBusSubscriber({ Side.CLIENT })
public class BlockVoidSiphon extends BlockTCDevice implements IBlockEnabled
{
    protected static final AxisAlignedBB AABB_MAIN;
    protected static final AxisAlignedBB AABB_BASE;
    protected static final AxisAlignedBB AABB_TOP;
    protected static final AxisAlignedBB AABB_ORB;
    
    public BlockVoidSiphon() {
        super(Material.IRON, TileVoidSiphon.class, "void_siphon");
        setSoundType(SoundType.METAL);
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean isSideSolid(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing side) {
        return false;
    }
    
    public void addCollisionBoxToList(final IBlockState state, final World worldIn, final BlockPos pos, final AxisAlignedBB AABB, final List<AxisAlignedBB> list, final Entity p_185477_6_, final boolean isActualState) {
        addCollisionBoxToList(pos, AABB, list, BlockVoidSiphon.AABB_BASE);
        addCollisionBoxToList(pos, AABB, list, BlockVoidSiphon.AABB_TOP);
        addCollisionBoxToList(pos, AABB, list, BlockVoidSiphon.AABB_ORB);
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return BlockVoidSiphon.AABB_MAIN;
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
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
