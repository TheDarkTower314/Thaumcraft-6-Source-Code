// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.crafting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import thaumcraft.api.casters.ICaster;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.EntitySpecialItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.crafting.TileCrucible;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.blocks.BlockTCTile;

public class BlockCrucible extends BlockTCTile
{
    private int delay;
    protected static final AxisAlignedBB AABB_LEGS;
    protected static final AxisAlignedBB AABB_WALL_NORTH;
    protected static final AxisAlignedBB AABB_WALL_SOUTH;
    protected static final AxisAlignedBB AABB_WALL_EAST;
    protected static final AxisAlignedBB AABB_WALL_WEST;
    
    public BlockCrucible() {
        super(Material.IRON, TileCrucible.class, "crucible");
        delay = 0;
        setSoundType(SoundType.METAL);
    }
    
    public void onEntityCollidedWithBlock(final World world, final BlockPos pos, final IBlockState state, final Entity entity) {
        if (!world.isRemote) {
            final TileCrucible tile = (TileCrucible)world.getTileEntity(pos);
            if (tile != null && entity instanceof EntityItem && !(entity instanceof EntitySpecialItem) && tile.heat > 150 && tile.tank.getFluidAmount() > 0) {
                tile.attemptSmelt((EntityItem)entity);
            }
            else {
                ++delay;
                if (delay < 10) {
                    return;
                }
                delay = 0;
                if (entity instanceof EntityLivingBase && tile != null && tile.heat > 150 && tile.tank.getFluidAmount() > 0) {
                    entity.attackEntityFrom(DamageSource.IN_FIRE, 1.0f);
                    world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4f, 2.0f + world.rand.nextFloat() * 0.4f);
                }
            }
        }
        super.onEntityCollidedWithBlock(world, pos, state, entity);
    }
    
    public void addCollisionBoxToList(final IBlockState state, final World worldIn, final BlockPos pos, final AxisAlignedBB AABB, final List<AxisAlignedBB> list, final Entity p_185477_6_, final boolean isActualState) {
        addCollisionBoxToList(pos, AABB, list, BlockCrucible.AABB_LEGS);
        addCollisionBoxToList(pos, AABB, list, BlockCrucible.AABB_WALL_WEST);
        addCollisionBoxToList(pos, AABB, list, BlockCrucible.AABB_WALL_NORTH);
        addCollisionBoxToList(pos, AABB, list, BlockCrucible.AABB_WALL_EAST);
        addCollisionBoxToList(pos, AABB, list, BlockCrucible.AABB_WALL_SOUTH);
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return BlockCrucible.FULL_BLOCK_AABB;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileCrucible) {
            ((TileCrucible)te).spillRemnants();
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (!world.isRemote) {
            final FluidStack fs = FluidUtil.getFluidContained(player.getHeldItem(hand));
            if (fs != null) {
                final FluidStack fluidStack = fs;
                final Fluid water = FluidRegistry.WATER;
                final Fluid water2 = FluidRegistry.WATER;
                if (fluidStack.containsFluid(new FluidStack(water, 1000))) {
                    final TileEntity te = world.getTileEntity(pos);
                    if (te != null && te instanceof TileCrucible) {
                        final TileCrucible tile = (TileCrucible)te;
                        if (tile.tank.getFluidAmount() < tile.tank.getCapacity()) {
                            if (FluidUtil.interactWithFluidHandler(player, hand, tile.tank)) {
                                player.inventoryContainer.detectAndSendChanges();
                                te.markDirty();
                                world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 3);
                                world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.33f, 1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3f);
                            }
                            return true;
                        }
                    }
                    return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
                }
            }
            if (!player.isSneaking() && !(player.getHeldItem(hand).getItem() instanceof ICaster) && side == EnumFacing.UP) {
                final TileEntity te = world.getTileEntity(pos);
                if (te != null && te instanceof TileCrucible) {
                    final TileCrucible tile = (TileCrucible)te;
                    final ItemStack ti = player.getHeldItem(hand).copy();
                    ti.setCount(1);
                    if (tile.heat > 150 && tile.tank.getFluidAmount() > 0 && tile.attemptSmelt(ti, player.getName()) == null) {
                        player.inventory.decrStackSize(player.inventory.currentItem, 1);
                        return true;
                    }
                }
            }
            else if (player.getHeldItem(hand).isEmpty() && player.isSneaking()) {
                final TileEntity te = world.getTileEntity(pos);
                if (te != null && te instanceof TileCrucible) {
                    final TileCrucible tile = (TileCrucible)te;
                    tile.spillRemnants();
                    return true;
                }
            }
            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
        }
        return true;
    }
    
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return true;
    }
    
    public int getComparatorInputOverride(final IBlockState state, final World world, final BlockPos pos) {
        final TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileCrucible) {
            final float n = (float)((TileCrucible)te).aspects.visSize();
            te.getClass();
            final float r = n / 500.0f;
            return MathHelper.floor(r * 14.0f) + ((((TileCrucible)te).aspects.visSize() > 0) ? 1 : 0);
        }
        return 0;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World w, final BlockPos pos, final Random r) {
        if (r.nextInt(10) == 0) {
            final TileEntity te = w.getTileEntity(pos);
            if (te != null && te instanceof TileCrucible && ((TileCrucible)te).tank.getFluidAmount() > 0 && ((TileCrucible)te).heat > 150) {
                w.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.1f + r.nextFloat() * 0.1f, 1.2f + r.nextFloat() * 0.2f, false);
            }
        }
    }
    
    static {
        AABB_LEGS = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0);
        AABB_WALL_NORTH = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.125);
        AABB_WALL_SOUTH = new AxisAlignedBB(0.0, 0.0, 0.875, 1.0, 1.0, 1.0);
        AABB_WALL_EAST = new AxisAlignedBB(0.875, 0.0, 0.0, 1.0, 1.0, 1.0);
        AABB_WALL_WEST = new AxisAlignedBB(0.0, 0.0, 0.0, 0.125, 1.0, 1.0);
    }
}
