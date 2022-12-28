package thaumcraft.common.blocks.world.ore;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.world.aura.AuraHandler;


public class BlockCrystal extends Block
{
    public static PropertyInteger SIZE;
    public static PropertyInteger GENERATION;
    public static IUnlistedProperty<Boolean> NORTH;
    public static IUnlistedProperty<Boolean> EAST;
    public static IUnlistedProperty<Boolean> SOUTH;
    public static IUnlistedProperty<Boolean> WEST;
    public static IUnlistedProperty<Boolean> UP;
    public static IUnlistedProperty<Boolean> DOWN;
    public Aspect aspect;
    
    public BlockCrystal(String name, Aspect aspect) {
        super(Material.GLASS);
        setUnlocalizedName(name);
        setRegistryName("thaumcraft", name);
        this.aspect = aspect;
        setHardness(0.25f);
        setSoundType(SoundsTC.CRYSTAL);
        setTickRandomly(true);
        setCreativeTab(ConfigItems.TABTC);
        setDefaultState(blockState.getBaseState().withProperty((IProperty)BlockCrystal.SIZE, (Comparable)0).withProperty((IProperty)BlockCrystal.GENERATION, (Comparable)1));
    }
    
    public SoundType getSoundType() {
        return SoundsTC.CRYSTAL;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemById(0);
    }
    
    protected boolean canSilkHarvest() {
        return false;
    }
    
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> ret = new ArrayList<ItemStack>();
        for (int count = getGrowth(state) + 1, i = 0; i < count; ++i) {
            ret.add(ThaumcraftApiHelper.makeCrystal(aspect));
        }
        return ret;
    }
    
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote && rand.nextInt(3 + getGeneration(state)) == 0) {
            int threshold = 10;
            int growth = getGrowth(state);
            int generation = getGeneration(state);
            if (aspect != Aspect.FLUX) {
                if (AuraHelper.getVis(worldIn, pos) <= threshold) {
                    if (growth > 0) {
                        worldIn.setBlockState(pos, state.withProperty((IProperty)BlockCrystal.SIZE, (Comparable)(growth - 1)));
                        AuraHelper.addVis(worldIn, pos, (float)threshold);
                    }
                    else if (BlockUtils.isBlockTouching(worldIn, pos, this)) {
                        worldIn.setBlockToAir(pos);
                        AuraHandler.addVis(worldIn, pos, (float)threshold);
                    }
                }
                else if (AuraHelper.getVis(worldIn, pos) > AuraHandler.getAuraBase(worldIn, pos) + threshold) {
                    if (growth < 3 && growth < 5 - generation + pos.toLong() % 3L) {
                        if (AuraHelper.drainVis(worldIn, pos, (float)threshold, false) > 0.0f) {
                            worldIn.setBlockState(pos, state.withProperty((IProperty)BlockCrystal.SIZE, (Comparable)(growth + 1)));
                        }
                    }
                    else if (generation < 4) {
                        BlockPos p2 = spreadCrystal(worldIn, pos);
                        if (p2 != null && AuraHelper.drainVis(worldIn, pos, (float)threshold, false) > 0.0f) {
                            if (rand.nextInt(6) == 0) {
                                --generation;
                            }
                            worldIn.setBlockState(p2, getDefaultState().withProperty((IProperty)BlockCrystal.GENERATION, (Comparable)(generation + 1)));
                        }
                    }
                }
            }
            else if (AuraHelper.getFlux(worldIn, pos) <= threshold) {
                if (growth > 0) {
                    worldIn.setBlockState(pos, state.withProperty((IProperty)BlockCrystal.SIZE, (Comparable)(growth - 1)));
                    AuraHelper.polluteAura(worldIn, pos, (float)threshold, false);
                }
                else if (BlockUtils.isBlockTouching(worldIn, pos, this)) {
                    worldIn.setBlockToAir(pos);
                    AuraHelper.polluteAura(worldIn, pos, (float)threshold, false);
                }
            }
            else if (AuraHelper.getFlux(worldIn, pos) > AuraHandler.getAuraBase(worldIn, pos) + threshold) {
                if (growth < 3 && growth < 5 - generation + pos.toLong() % 3L) {
                    if (AuraHelper.drainFlux(worldIn, pos, (float)threshold, false) > 0.0f) {
                        worldIn.setBlockState(pos, state.withProperty((IProperty)BlockCrystal.SIZE, (Comparable)(growth + 1)));
                    }
                }
                else if (generation < 4) {
                    BlockPos p2 = spreadCrystal(worldIn, pos);
                    if (p2 != null && AuraHelper.drainFlux(worldIn, pos, (float)threshold, false) > 0.0f) {
                        if (rand.nextInt(6) == 0) {
                            --generation;
                        }
                        worldIn.setBlockState(p2, getDefaultState().withProperty((IProperty)BlockCrystal.GENERATION, (Comparable)(generation + 1)));
                    }
                }
            }
        }
    }
    
    public static BlockPos spreadCrystal(World world, BlockPos pos) {
        int xx = pos.getX() + world.rand.nextInt(3) - 1;
        int yy = pos.getY() + world.rand.nextInt(3) - 1;
        int zz = pos.getZ() + world.rand.nextInt(3) - 1;
        BlockPos t = new BlockPos(xx, yy, zz);
        if (t.equals(pos)) {
            return null;
        }
        IBlockState bs = world.getBlockState(t);
        Material bm = bs.getMaterial();
        if (!bm.isLiquid() && (world.isAirBlock(t) || bs.getBlock().isReplaceable(world, t)) && world.rand.nextInt(16) == 0 && BlockUtils.isBlockTouching(world, t, Material.ROCK, true)) {
            return t;
        }
        return null;
    }
    
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!BlockUtils.isBlockTouching(worldIn, pos, Material.ROCK, true)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing o) {
        return false;
    }
    
    private boolean drawAt(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        IBlockState fbs = worldIn.getBlockState(pos);
        return fbs.getMaterial() == Material.ROCK && fbs.getBlock().isSideSolid(fbs, worldIn, pos, side.getOpposite());
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState bs, IBlockAccess iblockaccess, BlockPos pos) {
        IBlockState state = getExtendedState(bs, iblockaccess, pos);
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState es = (IExtendedBlockState)state;
            int c = 0;
            if (es.getValue(BlockCrystal.UP)) {
                ++c;
            }
            if (es.getValue(BlockCrystal.DOWN)) {
                ++c;
            }
            if (es.getValue(BlockCrystal.EAST)) {
                ++c;
            }
            if (es.getValue(BlockCrystal.WEST)) {
                ++c;
            }
            if (es.getValue(BlockCrystal.SOUTH)) {
                ++c;
            }
            if (es.getValue(BlockCrystal.NORTH)) {
                ++c;
            }
            if (c > 1) {
                return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            }
            if (es.getValue(BlockCrystal.UP)) {
                return new AxisAlignedBB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
            }
            if (es.getValue(BlockCrystal.DOWN)) {
                return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
            }
            if (es.getValue(BlockCrystal.EAST)) {
                return new AxisAlignedBB(0.5, 0.0, 0.0, 1.0, 1.0, 1.0);
            }
            if (es.getValue(BlockCrystal.WEST)) {
                return new AxisAlignedBB(0.0, 0.0, 0.0, 0.5, 1.0, 1.0);
            }
            if (es.getValue(BlockCrystal.SOUTH)) {
                return new AxisAlignedBB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0);
            }
            if (es.getValue(BlockCrystal.NORTH)) {
                return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5);
            }
        }
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }
    
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 1;
    }
    
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
        int i = source.getCombinedLight(pos, state.getLightValue(source, pos));
        int j = 180;
        int k = i & 0xFF;
        int l = j & 0xFF;
        int i2 = i >> 16 & 0xFF;
        int j2 = j >> 16 & 0xFF;
        return ((k > l) ? k : l) | ((i2 > j2) ? i2 : j2) << 16;
    }
    
    protected BlockStateContainer createBlockState() {
        IProperty[] listedProperties = {BlockCrystal.SIZE, BlockCrystal.GENERATION};
        IUnlistedProperty[] unlistedProperties = { BlockCrystal.UP, BlockCrystal.DOWN, BlockCrystal.NORTH, BlockCrystal.EAST, BlockCrystal.WEST, BlockCrystal.SOUTH };
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    }
    
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState retval = (IExtendedBlockState)state;
            return retval.withProperty(BlockCrystal.UP, drawAt(world, pos.up(), EnumFacing.UP)).withProperty(BlockCrystal.DOWN, drawAt(world, pos.down(), EnumFacing.DOWN)).withProperty(BlockCrystal.NORTH, drawAt(world, pos.north(), EnumFacing.NORTH)).withProperty(BlockCrystal.EAST, drawAt(world, pos.east(), EnumFacing.EAST)).withProperty(BlockCrystal.SOUTH, drawAt(world, pos.south(), EnumFacing.SOUTH)).withProperty(BlockCrystal.WEST, drawAt(world, pos.west(), EnumFacing.WEST));
        }
        return state;
    }
    
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state;
    }
    
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty((IProperty)BlockCrystal.SIZE, (Comparable)(meta & 0x3)).withProperty((IProperty)BlockCrystal.GENERATION, (Comparable)(1 + (meta >> 2 & 0x3)));
    }
    
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i |= (int)state.getValue((IProperty)BlockCrystal.SIZE);
        i |= (int)state.getValue((IProperty)BlockCrystal.GENERATION) - 1 << 2;
        return i;
    }
    
    public int getGrowth(IBlockState state) {
        return getMetaFromState(state) & 0x3;
    }
    
    public int getGeneration(IBlockState state) {
        return 1 + (getMetaFromState(state) >> 2);
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }
    
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
    }
    
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return BlockUtils.isBlockTouching(worldIn, pos, Material.ROCK, true) && super.canPlaceBlockAt(worldIn, pos);
    }
    
    static {
        SIZE = PropertyInteger.create("size", 0, 3);
        GENERATION = PropertyInteger.create("gen", 1, 4);
        NORTH = new Properties.PropertyAdapter(PropertyBool.create("north"));
        EAST = new Properties.PropertyAdapter(PropertyBool.create("east"));
        SOUTH = new Properties.PropertyAdapter(PropertyBool.create("south"));
        WEST = new Properties.PropertyAdapter(PropertyBool.create("west"));
        UP = new Properties.PropertyAdapter(PropertyBool.create("up"));
        DOWN = new Properties.PropertyAdapter(PropertyBool.create("down"));
    }
}
