// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import net.minecraft.world.World;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.tiles.devices.TileStabilizer;
import net.minecraft.block.material.Material;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.blocks.BlockTCDevice;

public class BlockStabilizer extends BlockTCDevice implements IInfusionStabiliserExt
{
    public BlockStabilizer() {
        super(Material.ROCK, TileStabilizer.class, "stabilizer");
        setSoundType(SoundType.STONE);
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    @SideOnly(Side.CLIENT)
    public static int colorMultiplier(final int meta) {
        final float f = meta / 15.0f;
        float f2 = f * 0.5f + 0.5f;
        if (meta == 0) {
            f2 = 0.3f;
        }
        final int i = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        final int j = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        final int k = MathHelper.clamp((int)(f2 * 255.0f), 0, 255);
        return 0xFF000000 | i << 16 | j << 8 | k;
    }
    
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public int getLightValue(final IBlockState state) {
        return 4;
    }
    
    @Override
    public boolean canStabaliseInfusion(final World world, final BlockPos pos) {
        return true;
    }
    
    @Override
    public float getStabilizationAmount(final World world, final BlockPos pos) {
        return 0.25f;
    }
}
