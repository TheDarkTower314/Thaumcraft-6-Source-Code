// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.crafting;

import thaumcraft.Thaumcraft;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockPistonBase;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import java.util.Random;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockGolemBuilder extends BlockTCDevice implements IBlockFacingHorizontal
{
    public static boolean ignore;
    
    public BlockGolemBuilder() {
        super(Material.ROCK, TileGolemBuilder.class, "golem_builder");
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(null);
    }
    
    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing axis) {
        return false;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemFromBlock(Blocks.PISTON);
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        destroy(worldIn, pos, state, pos);
        super.breakBlock(worldIn, pos, state);
    }
    
    public static void destroy(final World worldIn, final BlockPos pos, final IBlockState state, final BlockPos startpos) {
        if (BlockGolemBuilder.ignore || worldIn.isRemote) {
            return;
        }
        BlockGolemBuilder.ignore = true;
        for (int a = -1; a <= 1; ++a) {
            for (int b = 0; b <= 1; ++b) {
                for (int c = -1; c <= 1; ++c) {
                    if (pos.add(a, b, c) != startpos) {
                        final IBlockState bs = worldIn.getBlockState(pos.add(a, b, c));
                        if (bs.getBlock() == BlocksTC.placeholderBars) {
                            worldIn.setBlockState(pos.add(a, b, c), Blocks.IRON_BARS.getDefaultState());
                        }
                        if (bs.getBlock() == BlocksTC.placeholderAnvil) {
                            worldIn.setBlockState(pos.add(a, b, c), Blocks.ANVIL.getDefaultState());
                        }
                        if (bs.getBlock() == BlocksTC.placeholderCauldron) {
                            worldIn.setBlockState(pos.add(a, b, c), Blocks.CAULDRON.getDefaultState());
                        }
                        if (bs.getBlock() == BlocksTC.placeholderTable) {
                            worldIn.setBlockState(pos.add(a, b, c), BlocksTC.tableStone.getDefaultState());
                        }
                    }
                }
            }
        }
        if (pos != startpos) {
            worldIn.setBlockState(pos, Blocks.PISTON.getDefaultState().withProperty((IProperty)BlockPistonBase.FACING, (Comparable)EnumFacing.UP));
        }
        BlockGolemBuilder.ignore = false;
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (world.isRemote) {
            return true;
        }
        player.openGui(Thaumcraft.instance, 19, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    static {
        BlockGolemBuilder.ignore = false;
    }
}
