// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world;

import thaumcraft.common.lib.utils.Utils;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import java.util.Random;
import thaumcraft.common.blocks.BlockTC;

public class BlockLoot extends BlockTC
{
    LootType type;
    Random rand;
    
    public BlockLoot(final Material mat, final String name, final LootType type) {
        super(mat, name);
        rand = new Random();
        setHardness(0.15f);
        setResistance(0.0f);
        this.type = type;
    }
    
    public SoundType getSoundType() {
        return (blockMaterial == Material.WOOD) ? SoundType.WOOD : SoundsTC.URN;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    protected boolean canSilkHarvest() {
        return true;
    }
    
    public boolean canHarvestBlock(final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return true;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        if (getMaterial(state) == Material.ROCK) {
            return new AxisAlignedBB(0.125, 0.0625, 0.125, 0.875, 0.8125, 0.875);
        }
        return new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
    }
    
    public List<ItemStack> getDrops(final IBlockAccess world, final BlockPos pos, final IBlockState state, final int fortune) {
        final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        for (int q = 1 + type.ordinal() + rand.nextInt(3), a = 0; a < q; ++a) {
            final ItemStack is = Utils.generateLoot(type.ordinal(), rand);
            if (is != null && !is.isEmpty()) {
                ret.add(is.copy());
            }
        }
        return ret;
    }
    
    public enum LootType
    {
        COMMON, 
        UNCOMMON, 
        RARE;
    }
}
