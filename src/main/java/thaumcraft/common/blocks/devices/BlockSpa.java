package thaumcraft.common.blocks.devices;

import net.minecraftforge.fluids.FluidStack;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.Thaumcraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.devices.TileSpa;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockSpa extends BlockTCDevice
{
    public BlockSpa() {
        super(Material.ROCK, TileSpa.class, "spa");
        setSoundType(SoundType.STONE);
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileSpa && !player.isSneaking()) {
            FluidStack fs = FluidUtil.getFluidContained(player.getHeldItem(hand));
            if (fs != null) {
                TileSpa tile = (TileSpa)tileEntity;
                if (tile.tank.getFluidAmount() < tile.tank.getCapacity() && (tile.tank.getFluid() == null || tile.tank.getFluid().isFluidEqual(fs)) && FluidUtil.interactWithFluidHandler(player, hand, tile.tank)) {
                    player.inventoryContainer.detectAndSendChanges();
                    tile.markDirty();
                    world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 3);
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.33f, 1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3f);
                }
            }
            else {
                player.openGui(Thaumcraft.instance, 6, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }
}
