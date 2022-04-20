// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.basic;

import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.InventoryFake;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.item.ItemStack;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import net.minecraft.block.properties.IProperty;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTC;

public class BlockTable extends BlockTC
{
    public BlockTable(final Material mat, final String name, final SoundType st) {
        super(mat, name, st);
    }
    
    public boolean isSideSolid(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing side) {
        return side == EnumFacing.UP;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean canHarvestBlock(final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return true;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (world.isRemote) {
            return true;
        }
        if (this == BlocksTC.tableWood && player.getHeldItem(hand).getItem() instanceof IScribeTools) {
            IBlockState bs = BlocksTC.researchTable.getDefaultState();
            bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)player.getHorizontalFacing());
            world.setBlockState(pos, bs);
            final TileResearchTable tile = (TileResearchTable)world.getTileEntity(pos);
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
