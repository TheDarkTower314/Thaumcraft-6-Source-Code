// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.devices.TileDioptra;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockDioptra extends BlockTCDevice implements IBlockEnabled
{
    public BlockDioptra() {
        super(Material.ROCK, TileDioptra.class, "dioptra");
        setSoundType(SoundType.STONE);
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return true;
    }
    
    public int getComparatorInputOverride(final IBlockState state, final World world, final BlockPos pos) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileDioptra) {
            final float r = ((TileDioptra)tile).grid_amt[84] / 64.0f;
            return MathHelper.floor(r * 14.0f) + ((r > 0.0f) ? 1 : 0);
        }
        return super.getComparatorInputOverride(state, world, pos);
    }
    
    @Override
    protected void updateState(final World worldIn, final BlockPos pos, final IBlockState state) {
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        final boolean b = (boolean)state.getValue((IProperty)IBlockEnabled.ENABLED);
        world.setBlockState(pos, state.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)!b), 3);
        return true;
    }
}
