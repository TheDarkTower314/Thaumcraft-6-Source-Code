// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.crafting;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import thaumcraft.Thaumcraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockArcaneWorkbench extends BlockTCDevice
{
    public BlockArcaneWorkbench() {
        super(Material.WOOD, TileArcaneWorkbench.class, "arcane_workbench");
        setSoundType(SoundType.WOOD);
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
        player.openGui(Thaumcraft.instance, 13, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    @Override
    public void breakBlock(final World world, final BlockPos pos, final IBlockState state) {
        final TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileArcaneWorkbench) {
            InventoryHelper.dropInventoryItems(world, pos, ((TileArcaneWorkbench)tileEntity).inventoryCraft);
        }
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }
}
