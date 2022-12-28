package thaumcraft.common.blocks.crafting;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;


public class BlockArcaneWorkbenchCharger extends BlockTC
{
    public BlockArcaneWorkbenchCharger() {
        super(Material.WOOD, "arcane_workbench_charger");
        setSoundType(SoundType.WOOD);
        setHardness(1.25f);
        setResistance(10.0f);
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && (worldIn.getBlockState(pos.down()).getBlock() == BlocksTC.arcaneWorkbench || worldIn.getBlockState(pos.down()).getBlock() == BlocksTC.wandWorkbench);
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        TileEntity te = worldIn.getTileEntity(pos.down());
        if (te != null && te instanceof TileArcaneWorkbench) {
            ((TileArcaneWorkbench)te).syncTile(true);
        }
        if (te != null && te instanceof TileFocalManipulator) {
            ((TileFocalManipulator)te).syncTile(true);
        }
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }
    
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        if (worldIn.getBlockState(pos.down()).getBlock() != BlocksTC.arcaneWorkbench && worldIn.getBlockState(pos.down()).getBlock() != BlocksTC.wandWorkbench) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        if (world.getBlockState(pos.down()).getBlock() == BlocksTC.arcaneWorkbench) {
            player.openGui(Thaumcraft.instance, 13, world, pos.getX(), pos.down().getY(), pos.getZ());
        }
        if (world.getBlockState(pos.down()).getBlock() == BlocksTC.wandWorkbench) {
            player.openGui(Thaumcraft.instance, 7, world, pos.getX(), pos.down().getY(), pos.getZ());
        }
        return true;
    }
}
