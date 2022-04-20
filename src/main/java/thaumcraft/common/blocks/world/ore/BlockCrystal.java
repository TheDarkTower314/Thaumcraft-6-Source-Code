// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world.ore;

import net.minecraftforge.common.property.Properties;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.api.aura.AuraHelper;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import java.util.List;
import net.minecraft.item.Item;
import java.util.Random;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.block.material.Material;
import thaumcraft.api.aspects.Aspect;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.Block;

public class BlockCrystal extends Block
{
    public static final PropertyInteger SIZE;
    public static final PropertyInteger GENERATION;
    public static final IUnlistedProperty<Boolean> NORTH;
    public static final IUnlistedProperty<Boolean> EAST;
    public static final IUnlistedProperty<Boolean> SOUTH;
    public static final IUnlistedProperty<Boolean> WEST;
    public static final IUnlistedProperty<Boolean> UP;
    public static final IUnlistedProperty<Boolean> DOWN;
    public Aspect aspect;
    
    public BlockCrystal(final String name, final Aspect aspect) {
        super(Material.GLASS);
        this.setUnlocalizedName(name);
        this.setRegistryName("thaumcraft", name);
        this.aspect = aspect;
        this.setHardness(0.25f);
        this.setSoundType(SoundsTC.CRYSTAL);
        this.setTickRandomly(true);
        this.setCreativeTab(ConfigItems.TABTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)BlockCrystal.SIZE, (Comparable)0).withProperty((IProperty)BlockCrystal.GENERATION, (Comparable)1));
    }
    
    public SoundType getSoundType() {
        return SoundsTC.CRYSTAL;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemById(0);
    }
    
    protected boolean canSilkHarvest() {
        return false;
    }
    
    public List<ItemStack> getDrops(final IBlockAccess world, final BlockPos pos, final IBlockState state, final int fortune) {
        final List<ItemStack> ret = new ArrayList<ItemStack>();
        for (int count = this.getGrowth(state) + 1, i = 0; i < count; ++i) {
            ret.add(ThaumcraftApiHelper.makeCrystal(this.aspect));
        }
        return ret;
    }
    
    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (!worldIn.isRemote && rand.nextInt(3 + this.getGeneration(state)) == 0) {
            final int threshold = 10;
            final int growth = this.getGrowth(state);
            int generation = this.getGeneration(state);
            if (this.aspect != Aspect.FLUX) {
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
                        final BlockPos p2 = spreadCrystal(worldIn, pos);
                        if (p2 != null && AuraHelper.drainVis(worldIn, pos, (float)threshold, false) > 0.0f) {
                            if (rand.nextInt(6) == 0) {
                                --generation;
                            }
                            worldIn.setBlockState(p2, this.getDefaultState().withProperty((IProperty)BlockCrystal.GENERATION, (Comparable)(generation + 1)));
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
                    final BlockPos p2 = spreadCrystal(worldIn, pos);
                    if (p2 != null && AuraHelper.drainFlux(worldIn, pos, (float)threshold, false) > 0.0f) {
                        if (rand.nextInt(6) == 0) {
                            --generation;
                        }
                        worldIn.setBlockState(p2, this.getDefaultState().withProperty((IProperty)BlockCrystal.GENERATION, (Comparable)(generation + 1)));
                    }
                }
            }
        }
    }
    
    public static BlockPos spreadCrystal(final World world, final BlockPos pos) {
        final int xx = pos.getX() + world.rand.nextInt(3) - 1;
        final int yy = pos.getY() + world.rand.nextInt(3) - 1;
        final int zz = pos.getZ() + world.rand.nextInt(3) - 1;
        final BlockPos t = new BlockPos(xx, yy, zz);
        if (t.equals(pos)) {
            return null;
        }
        final IBlockState bs = world.getBlockState(t);
        final Material bm = bs.getMaterial();
        if (!bm.isLiquid() && (world.isAirBlock(t) || bs.getBlock().isReplaceable(world, t)) && world.rand.nextInt(16) == 0 && BlockUtils.isBlockTouching(world, t, Material.ROCK, true)) {
            return t;
        }
        return null;
    }
    
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos fromPos) {
        if (!BlockUtils.isBlockTouching(worldIn, pos, Material.ROCK, true)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    public boolean isSideSolid(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing o) {
        return false;
    }
    
    private boolean drawAt(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        final IBlockState fbs = worldIn.getBlockState(pos);
        return fbs.getMaterial() == Material.ROCK && fbs.getBlock().isSideSolid(fbs, worldIn, pos, side.getOpposite());
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState bs, final IBlockAccess iblockaccess, final BlockPos pos) {
        final IBlockState state = this.getExtendedState(bs, iblockaccess, pos);
        if (state instanceof IExtendedBlockState) {
            final IExtendedBlockState es = (IExtendedBlockState)state;
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
    
    public AxisAlignedBB getCollisionBoundingBox(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return null;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public int getLightValue(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return 1;
    }
    
    public int getPackedLightmapCoords(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        final int i = source.getCombinedLight(pos, state.getLightValue(source, pos));
        final int j = 180;
        final int k = i & 0xFF;
        final int l = j & 0xFF;
        final int i2 = i >> 16 & 0xFF;
        final int j2 = j >> 16 & 0xFF;
        return ((k > l) ? k : l) | ((i2 > j2) ? i2 : j2) << 16;
    }
    
    protected BlockStateContainer createBlockState() {
        final IProperty[] listedProperties = {BlockCrystal.SIZE, BlockCrystal.GENERATION};
        final IUnlistedProperty[] unlistedProperties = { BlockCrystal.UP, BlockCrystal.DOWN, BlockCrystal.NORTH, BlockCrystal.EAST, BlockCrystal.WEST, BlockCrystal.SOUTH };
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    }
    
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            final IExtendedBlockState retval = (IExtendedBlockState)state;
            return retval.withProperty(BlockCrystal.UP, this.drawAt(world, pos.up(), EnumFacing.UP)).withProperty(BlockCrystal.DOWN, this.drawAt(world, pos.down(), EnumFacing.DOWN)).withProperty(BlockCrystal.NORTH, this.drawAt(world, pos.north(), EnumFacing.NORTH)).withProperty(BlockCrystal.EAST, this.drawAt(world, pos.east(), EnumFacing.EAST)).withProperty(BlockCrystal.SOUTH, this.drawAt(world, pos.south(), EnumFacing.SOUTH)).withProperty(BlockCrystal.WEST, this.drawAt(world, pos.west(), EnumFacing.WEST));
        }
        return state;
    }
    
    public IBlockState getActualState(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return state;
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty((IProperty)BlockCrystal.SIZE, (Comparable)(meta & 0x3)).withProperty((IProperty)BlockCrystal.GENERATION, (Comparable)(1 + (meta >> 2 & 0x3)));
    }
    
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= (int)state.getValue((IProperty)BlockCrystal.SIZE);
        i |= (int)state.getValue((IProperty)BlockCrystal.GENERATION) - 1 << 2;
        return i;
    }
    
    public int getGrowth(final IBlockState state) {
        return this.getMetaFromState(state) & 0x3;
    }
    
    public int getGeneration(final IBlockState state) {
        return 1 + (this.getMetaFromState(state) >> 2);
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final CreativeTabs tab, final NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }
    
    public boolean canSilkHarvest(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player) {
        return false;
    }
    
    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
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
