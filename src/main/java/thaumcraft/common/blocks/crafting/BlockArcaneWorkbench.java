package thaumcraft.common.blocks.crafting;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;


public class BlockArcaneWorkbench extends BlockTCDevice
{
    public BlockArcaneWorkbench() {
        super(Material.WOOD, TileArcaneWorkbench.class, "arcane_workbench");
        setSoundType(SoundType.WOOD);
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        player.openGui(Thaumcraft.instance, 13, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileArcaneWorkbench) {
            InventoryHelper.dropInventoryItems(world, pos, ((TileArcaneWorkbench)tileEntity).inventoryCraft);
        }
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }
}
