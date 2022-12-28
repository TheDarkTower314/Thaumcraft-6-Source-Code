package thaumcraft.common.blocks.basic;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.tiles.crafting.TileResearchTable;


public class BlockTable extends BlockTC
{
    public BlockTable(Material mat, String name, SoundType st) {
        super(mat, name, st);
    }
    
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
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
        if (this == BlocksTC.tableWood && player.getHeldItem(hand).getItem() instanceof IScribeTools) {
            IBlockState bs = BlocksTC.researchTable.getDefaultState();
            bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)player.getHorizontalFacing());
            world.setBlockState(pos, bs);
            TileResearchTable tile = (TileResearchTable)world.getTileEntity(pos);
            tile.setInventorySlotContents(0, player.getHeldItem(hand).copy());
            player.setHeldItem(hand, ItemStack.EMPTY);
            player.inventory.markDirty();
            tile.markDirty();
            world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), bs, bs, 3);
            FMLCommonHandler.instance().firePlayerCraftingEvent(player, new ItemStack(BlocksTC.researchTable), new InventoryFake(1));
        }
        return true;
    }
}
