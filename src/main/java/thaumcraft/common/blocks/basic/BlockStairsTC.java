// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.basic;

import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockStairs;

public class BlockStairsTC extends BlockStairs
{
    public BlockStairsTC(final String name, final IBlockState modelState) {
        super(modelState);
        this.setUnlocalizedName(name);
        this.setRegistryName("thaumcraft", name);
        this.setCreativeTab(ConfigItems.TABTC);
        this.setLightOpacity(0);
    }
    
    public int getFlammability(final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
        if (this.getMaterial(this.getDefaultState()) == Material.WOOD) {
            return 20;
        }
        return super.getFlammability(world, pos, face);
    }
    
    public int getFireSpreadSpeed(final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
        if (this.getMaterial(this.getDefaultState()) == Material.WOOD) {
            return 5;
        }
        return super.getFireSpreadSpeed(world, pos, face);
    }
}
