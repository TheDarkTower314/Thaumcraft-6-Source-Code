// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import com.google.common.base.Predicate;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.block.state.BlockStateContainer;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.inventory.Container;
import net.minecraft.init.SoundEvents;
import thaumcraft.api.ThaumcraftInvHelper;
import net.minecraft.entity.item.EntityItem;
import thaumcraft.common.tiles.devices.TileHungryChest;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.material.Material;
import java.util.Random;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.BlockContainer;

public class BlockHungryChest extends BlockContainer
{
    public static final PropertyDirection FACING;
    private final Random rand;
    
    public BlockHungryChest() {
        super(Material.WOOD);
        this.rand = new Random();
        this.setUnlocalizedName("hungry_chest");
        this.setRegistryName("thaumcraft", "hungry_chest");
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)BlockHungryChest.FACING, (Comparable)EnumFacing.NORTH));
        this.setHardness(2.5f);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(ConfigItems.TABTC);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean canHarvestBlock(final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return true;
    }
    
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
    }
    
    public AxisAlignedBB getCollisionBoundingBox(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
    }
    
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return this.getDefaultState().withProperty((IProperty)BlockHungryChest.FACING, (Comparable)placer.getHorizontalFacing());
    }
    
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        final EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.floor(placer.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3).getOpposite();
        state = state.withProperty((IProperty)BlockHungryChest.FACING, (Comparable)enumfacing);
        worldIn.setBlockState(pos, state, 3);
    }
    
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (world.isRemote) {
            return true;
        }
        final TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity instanceof IInventory) {
            player.displayGUIChest((IInventory)tileentity);
        }
        return true;
    }
    
    public void onEntityCollidedWithBlock(final World world, final BlockPos pos, final IBlockState state, final Entity entity) {
        final Object var10 = world.getTileEntity(pos);
        if (var10 == null) {
            return;
        }
        if (world.isRemote) {
            return;
        }
        if (entity instanceof EntityItem && !entity.isDead) {
            final ItemStack leftovers = ThaumcraftInvHelper.insertStackAt(world, pos, EnumFacing.UP, ((EntityItem)entity).getItem(), false);
            if (leftovers == null || leftovers.isEmpty() || leftovers.getCount() != ((EntityItem)entity).getItem().getCount()) {
                entity.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.25f, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f + 1.0f);
            }
            if (leftovers != null && !leftovers.isEmpty()) {
                ((EntityItem)entity).setItem(leftovers);
            }
            else {
                entity.setDead();
            }
            ((TileHungryChest)var10).markDirty();
        }
    }
    
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return true;
    }
    
    public int getComparatorInputOverride(final IBlockState state, final World worldIn, final BlockPos pos) {
        final Object var10 = worldIn.getTileEntity(pos);
        if (var10 instanceof TileHungryChest) {
            return Container.calcRedstoneFromInventory((IInventory)var10);
        }
        return 0;
    }
    
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing axis) {
        final IBlockState state = world.getBlockState(pos);
        for (final IProperty<?> prop : state.getProperties().keySet()) {
            if (prop.getName().equals("facing")) {
                world.setBlockState(pos, state.cycleProperty((IProperty)prop));
                return true;
            }
        }
        return false;
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty((IProperty)BlockHungryChest.FACING, (Comparable)enumfacing);
    }
    
    public int getMetaFromState(final IBlockState state) {
        return ((EnumFacing)state.getValue((IProperty)BlockHungryChest.FACING)).getIndex();
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockHungryChest.FACING);
    }
    
    public TileEntity createNewTileEntity(final World par1World, final int m) {
        return new TileHungryChest();
    }
    
    public IBlockState withRotation(final IBlockState state, final Rotation rot) {
        return state.withProperty((IProperty)BlockHungryChest.FACING, (Comparable)rot.rotate((EnumFacing)state.getValue((IProperty)BlockHungryChest.FACING)));
    }
    
    public IBlockState withMirror(final IBlockState state, final Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue((IProperty)BlockHungryChest.FACING)));
    }
    
    static {
        FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    }
}
