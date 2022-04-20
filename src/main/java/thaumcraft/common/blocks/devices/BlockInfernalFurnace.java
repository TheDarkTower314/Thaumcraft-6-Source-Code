// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import net.minecraft.util.DamageSource;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import java.util.Random;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.init.Blocks;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.devices.TileInfernalFurnace;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockInfernalFurnace extends BlockTCDevice implements IBlockFacingHorizontal
{
    public static boolean ignore;
    
    public BlockInfernalFurnace() {
        super(Material.ROCK, TileInfernalFurnace.class, "infernal_furnace");
        this.setSoundType(SoundType.STONE);
        this.setLightLevel(0.9f);
        final IBlockState bs = this.blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)EnumFacing.NORTH);
        this.setDefaultState(bs);
        this.setCreativeTab(null);
    }
    
    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing axis) {
        return false;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
    }
    
    @Override
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
    
    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState bs = this.getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)placer.getHorizontalFacing().getOpposite());
        return bs;
    }
    
    public static void destroyFurnace(final World worldIn, final BlockPos pos, final IBlockState state, final BlockPos startpos) {
        if (BlockInfernalFurnace.ignore || worldIn.isRemote) {
            return;
        }
        BlockInfernalFurnace.ignore = true;
        for (int a = -1; a <= 1; ++a) {
            for (int b = -1; b <= 1; ++b) {
                for (int c = -1; c <= 1; ++c) {
                    if (pos.add(a, b, c) != startpos) {
                        final IBlockState bs = worldIn.getBlockState(pos.add(a, b, c));
                        if (bs.getBlock() == BlocksTC.placeholderNetherbrick) {
                            worldIn.setBlockState(pos.add(a, b, c), Blocks.NETHER_BRICK.getDefaultState());
                        }
                        if (bs.getBlock() == BlocksTC.placeholderObsidian) {
                            worldIn.setBlockState(pos.add(a, b, c), Blocks.OBSIDIAN.getDefaultState());
                        }
                    }
                }
            }
        }
        if (worldIn.isAirBlock(pos.offset(BlockStateUtils.getFacing(state).getOpposite()))) {
            worldIn.setBlockState(pos.offset(BlockStateUtils.getFacing(state).getOpposite()), Blocks.IRON_BARS.getDefaultState());
        }
        worldIn.setBlockState(pos, Blocks.LAVA.getDefaultState());
        BlockInfernalFurnace.ignore = false;
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemById(0);
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        destroyFurnace(worldIn, pos, state, pos);
        super.breakBlock(worldIn, pos, state);
    }
    
    public void onEntityCollidedWithBlock(final World world, final BlockPos pos, final IBlockState state, final Entity entity) {
        if (entity.posX < pos.getX() + 0.3f) {
            entity.motionX += 9.999999747378752E-5;
        }
        if (entity.posX > pos.getX() + 0.7f) {
            entity.motionX -= 9.999999747378752E-5;
        }
        if (entity.posZ < pos.getZ() + 0.3f) {
            entity.motionZ += 9.999999747378752E-5;
        }
        if (entity.posZ > pos.getZ() + 0.7f) {
            entity.motionZ -= 9.999999747378752E-5;
        }
        if (!world.isRemote && entity.ticksExisted % 10 == 0) {
            if (entity instanceof EntityItem) {
                entity.motionY = 0.02500000037252903;
                if (entity.onGround) {
                    final TileInfernalFurnace taf = (TileInfernalFurnace)world.getTileEntity(pos);
                    ((EntityItem)entity).setItem(taf.addItemsToInventory(((EntityItem)entity).getItem()));
                }
            }
            else if (entity instanceof EntityLivingBase && !entity.isImmuneToFire()) {
                entity.attackEntityFrom(DamageSource.LAVA, 3.0f);
                entity.setFire(10);
            }
        }
        super.onEntityCollidedWithBlock(world, pos, state, entity);
    }
    
    static {
        BlockInfernalFurnace.ignore = false;
    }
}
