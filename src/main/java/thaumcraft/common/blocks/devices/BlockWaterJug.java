// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import thaumcraft.client.fx.FXDispatcher;
import java.util.Random;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionUtils;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.devices.TileWaterJug;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockWaterJug extends BlockTCDevice
{
    public BlockWaterJug() {
        super(Material.ROCK, TileWaterJug.class, "everfull_urn");
        setSoundType(SoundType.STONE);
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 1.0, 0.8125);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (!world.isRemote) {
            final TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileWaterJug) {
                final TileWaterJug tile = (TileWaterJug)te;
                if (FluidUtil.interactWithFluidHandler(player, hand, tile.tank)) {
                    player.inventoryContainer.detectAndSendChanges();
                    te.markDirty();
                    tile.syncTile(false);
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.33f, 1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3f);
                }
                else if (player.getHeldItem(hand).getItem() == Items.GLASS_BOTTLE && tile.tank.getFluidAmount() >= 333) {
                    final ItemStack itemstack = player.getHeldItem(hand);
                    final ItemStack itemstack2 = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
                    if (!player.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }
                    if (itemstack.isEmpty()) {
                        player.setHeldItem(hand, itemstack2);
                    }
                    else if (!player.inventory.addItemStackToInventory(itemstack2)) {
                        player.dropItem(itemstack2, false);
                    }
                    else if (player instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                    }
                    tile.drain(new FluidStack(FluidRegistry.WATER, 333), true);
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.33f, 1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3f);
                }
            }
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World world, final BlockPos pos, final Random rand) {
        final Block block = state.getBlock();
        if (block.hasTileEntity(state)) {
            final TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileWaterJug) {
                final TileWaterJug tile = (TileWaterJug)te;
                if (tile.tank.getFluidAmount() >= tile.tank.getCapacity()) {
                    FXDispatcher.INSTANCE.jarSplashFx(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                }
            }
        }
    }
}
