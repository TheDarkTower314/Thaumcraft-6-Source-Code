package thaumcraft.common.blocks.devices;
import com.google.common.base.Predicate;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.devices.TileHungryChest;


public class BlockHungryChest extends BlockContainer
{
    public static PropertyDirection FACING;
    private Random rand;
    
    public BlockHungryChest() {
        super(Material.WOOD);
        rand = new Random();
        setUnlocalizedName("hungry_chest");
        setRegistryName("thaumcraft", "hungry_chest");
        setDefaultState(blockState.getBaseState().withProperty((IProperty)BlockHungryChest.FACING, (Comparable)EnumFacing.NORTH));
        setHardness(2.5f);
        setSoundType(SoundType.WOOD);
        setCreativeTab(ConfigItems.TABTC);
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
    }
    
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
    }
    
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty((IProperty)BlockHungryChest.FACING, (Comparable)placer.getHorizontalFacing());
    }
    
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.floor(placer.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3).getOpposite();
        state = state.withProperty((IProperty)BlockHungryChest.FACING, (Comparable)enumfacing);
        worldIn.setBlockState(pos, state, 3);
    }
    
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity instanceof IInventory) {
            player.displayGUIChest((IInventory)tileentity);
        }
        return true;
    }
    
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        Object var10 = world.getTileEntity(pos);
        if (var10 == null) {
            return;
        }
        if (world.isRemote) {
            return;
        }
        if (entity instanceof EntityItem && !entity.isDead) {
            ItemStack leftovers = ThaumcraftInvHelper.insertStackAt(world, pos, EnumFacing.UP, ((EntityItem)entity).getItem(), false);
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
    
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }
    
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        Object var10 = worldIn.getTileEntity(pos);
        if (var10 instanceof TileHungryChest) {
            return Container.calcRedstoneFromInventory((IInventory)var10);
        }
        return 0;
    }
    
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        IBlockState state = world.getBlockState(pos);
        for (IProperty<?> prop : state.getProperties().keySet()) {
            if (prop.getName().equals("facing")) {
                world.setBlockState(pos, state.cycleProperty((IProperty)prop));
                return true;
            }
        }
        return false;
    }
    
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return getDefaultState().withProperty((IProperty)BlockHungryChest.FACING, (Comparable)enumfacing);
    }
    
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing)state.getValue((IProperty)BlockHungryChest.FACING)).getIndex();
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockHungryChest.FACING);
    }
    
    public TileEntity createNewTileEntity(World par1World, int m) {
        return new TileHungryChest();
    }
    
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty((IProperty)BlockHungryChest.FACING, (Comparable)rot.rotate((EnumFacing)state.getValue((IProperty)BlockHungryChest.FACING)));
    }
    
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue((IProperty)BlockHungryChest.FACING)));
    }
    
    static {
        FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    }
}
