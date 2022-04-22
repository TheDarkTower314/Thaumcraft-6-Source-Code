// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.basic;

import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTC;

public class BlockTranslucent extends BlockTC
{
    public BlockTranslucent(final String name) {
        super(Material.GLASS, name);
        setHardness(0.5f);
        setSoundType(SoundType.STONE);
    }
    
    public boolean isBeaconBase(final IBlockAccess world, final BlockPos pos, final BlockPos beacon) {
        return true;
    }
    
    public boolean canHarvestBlock(final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return true;
    }
    
    public EnumPushReaction getMobilityFlag(final IBlockState state) {
        return EnumPushReaction.NORMAL;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(final IBlockState blockState, final IBlockAccess blockAccess, final BlockPos pos, final EnumFacing side) {
        final IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
        final Block block = iblockstate.getBlock();
        return block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    public boolean isOpaqueCube(final IBlockState iblockstate) {
        return false;
    }
}
