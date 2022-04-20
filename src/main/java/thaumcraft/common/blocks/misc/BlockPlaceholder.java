// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.misc;

import thaumcraft.common.blocks.crafting.BlockGolemBuilder;
import thaumcraft.common.blocks.devices.BlockInfernalFurnace;
import thaumcraft.Thaumcraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import java.util.Random;
import thaumcraft.common.blocks.BlockTC;

public class BlockPlaceholder extends BlockTC
{
    private final Random rand;
    
    public BlockPlaceholder(final String name) {
        super(Material.ROCK, name);
        this.rand = new Random();
        this.setHardness(2.5f);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(null);
    }
    
    public EnumPushReaction getMobilityFlag(final IBlockState state) {
        return EnumPushReaction.BLOCK;
    }
    
    protected boolean canSilkHarvest() {
        return false;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public int getLightValue(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        if (state.getBlock() == BlocksTC.placeholderCauldron) {
            return 13;
        }
        return super.getLightValue(state, world, pos);
    }
    
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        if (state.getBlock() == BlocksTC.placeholderNetherbrick) {
            return Item.getItemFromBlock(Blocks.NETHER_BRICK);
        }
        if (state.getBlock() == BlocksTC.placeholderObsidian) {
            return Item.getItemFromBlock(Blocks.OBSIDIAN);
        }
        if (state.getBlock() == BlocksTC.placeholderBars) {
            return Item.getItemFromBlock(Blocks.IRON_BARS);
        }
        if (state.getBlock() == BlocksTC.placeholderAnvil) {
            return Item.getItemFromBlock(Blocks.ANVIL);
        }
        if (state.getBlock() == BlocksTC.placeholderCauldron) {
            return Item.getItemFromBlock(Blocks.CAULDRON);
        }
        if (state.getBlock() == BlocksTC.placeholderTable) {
            return Item.getItemFromBlock(BlocksTC.tableStone);
        }
        return Item.getItemById(0);
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (world.isRemote) {
            return true;
        }
        if (state.getBlock() != BlocksTC.placeholderNetherbrick && state.getBlock() != BlocksTC.placeholderObsidian) {
            for (int a = -1; a <= 1; ++a) {
                for (int b = -1; b <= 1; ++b) {
                    for (int c = -1; c <= 1; ++c) {
                        final IBlockState s = world.getBlockState(pos.add(a, b, c));
                        if (s.getBlock() == BlocksTC.golemBuilder) {
                            player.openGui(Thaumcraft.instance, 19, world, pos.add(a, b, c).getX(), pos.add(a, b, c).getY(), pos.add(a, b, c).getZ());
                            return true;
                        }
                    }
                }
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
    
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        Label_0265: {
            if ((state.getBlock() == BlocksTC.placeholderNetherbrick || state.getBlock() == BlocksTC.placeholderObsidian) && !BlockInfernalFurnace.ignore && !worldIn.isRemote) {
                for (int a = -1; a <= 1; ++a) {
                    for (int b = -1; b <= 1; ++b) {
                        for (int c = -1; c <= 1; ++c) {
                            final IBlockState s = worldIn.getBlockState(pos.add(a, b, c));
                            if (s.getBlock() == BlocksTC.infernalFurnace) {
                                BlockInfernalFurnace.destroyFurnace(worldIn, pos.add(a, b, c), s, pos);
                                break Label_0265;
                            }
                        }
                    }
                }
            }
            else if (state.getBlock() != BlocksTC.placeholderNetherbrick && state.getBlock() != BlocksTC.placeholderObsidian && !BlockGolemBuilder.ignore && !worldIn.isRemote) {
                for (int a = -1; a <= 1; ++a) {
                    for (int b = -1; b <= 1; ++b) {
                        for (int c = -1; c <= 1; ++c) {
                            final IBlockState s = worldIn.getBlockState(pos.add(a, b, c));
                            if (s.getBlock() == BlocksTC.golemBuilder) {
                                BlockGolemBuilder.destroy(worldIn, pos.add(a, b, c), s, pos);
                                break Label_0265;
                            }
                        }
                    }
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
