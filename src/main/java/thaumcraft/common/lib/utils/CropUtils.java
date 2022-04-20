// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;

public class CropUtils
{
    public static ArrayList<String> clickableCrops;
    public static ArrayList<String> standardCrops;
    public static ArrayList<String> stackedCrops;
    public static ArrayList<String> lampBlacklist;
    
    public static void addStandardCrop(final ItemStack stack, final int grownMeta) {
        final Block block = Block.getBlockFromItem(stack.getItem());
        if (block == null) {
            return;
        }
        addStandardCrop(block, grownMeta);
    }
    
    public static void addStandardCrop(final Block block, final int grownMeta) {
        if (grownMeta == 32767) {
            for (int a = 0; a < 16; ++a) {
                CropUtils.standardCrops.add(block.getUnlocalizedName() + a);
            }
        }
        else {
            CropUtils.standardCrops.add(block.getUnlocalizedName() + grownMeta);
        }
        if (block instanceof BlockCrops && grownMeta != 7) {
            CropUtils.standardCrops.add(block.getUnlocalizedName() + "7");
        }
    }
    
    public static void addClickableCrop(final ItemStack stack, final int grownMeta) {
        if (Block.getBlockFromItem(stack.getItem()) == null) {
            return;
        }
        if (grownMeta == 32767) {
            for (int a = 0; a < 16; ++a) {
                CropUtils.clickableCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + a);
            }
        }
        else {
            CropUtils.clickableCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + grownMeta);
        }
        if (Block.getBlockFromItem(stack.getItem()) instanceof BlockCrops && grownMeta != 7) {
            CropUtils.clickableCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + "7");
        }
    }
    
    public static void addStackedCrop(final ItemStack stack, final int grownMeta) {
        if (Block.getBlockFromItem(stack.getItem()) == null) {
            return;
        }
        addStackedCrop(Block.getBlockFromItem(stack.getItem()), grownMeta);
    }
    
    public static void addStackedCrop(final Block block, final int grownMeta) {
        if (grownMeta == 32767) {
            for (int a = 0; a < 16; ++a) {
                CropUtils.stackedCrops.add(block.getUnlocalizedName() + a);
            }
        }
        else {
            CropUtils.stackedCrops.add(block.getUnlocalizedName() + grownMeta);
        }
        if (block instanceof BlockCrops && grownMeta != 7) {
            CropUtils.stackedCrops.add(block.getUnlocalizedName() + "7");
        }
    }
    
    public static boolean isGrownCrop(final World world, final BlockPos pos) {
        if (world.isAirBlock(pos)) {
            return false;
        }
        boolean found = false;
        final IBlockState bs = world.getBlockState(pos);
        final Block bi = bs.getBlock();
        final int md = bi.getMetaFromState(bs);
        if (CropUtils.standardCrops.contains(bi.getUnlocalizedName() + md) || CropUtils.clickableCrops.contains(bi.getUnlocalizedName() + md) || CropUtils.stackedCrops.contains(bi.getUnlocalizedName() + md)) {
            found = true;
        }
        final Block biB = world.getBlockState(pos.down()).getBlock();
        return (bi instanceof IGrowable && !((IGrowable)bi).canGrow(world, pos, world.getBlockState(pos), world.isRemote) && !(bi instanceof BlockStem)) || (bi instanceof BlockCrops && md == 7 && !found) || CropUtils.standardCrops.contains(bi.getUnlocalizedName() + md) || CropUtils.clickableCrops.contains(bi.getUnlocalizedName() + md) || (CropUtils.stackedCrops.contains(bi.getUnlocalizedName() + md) && biB == bi);
    }
    
    public static void blacklistLamp(final ItemStack stack, final int meta) {
        if (Block.getBlockFromItem(stack.getItem()) == null) {
            return;
        }
        if (meta == 32767) {
            for (int a = 0; a < 16; ++a) {
                CropUtils.lampBlacklist.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + a);
            }
        }
        else {
            CropUtils.lampBlacklist.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + meta);
        }
    }
    
    public static boolean doesLampGrow(final World world, final BlockPos pos) {
        final Block bi = world.getBlockState(pos).getBlock();
        final int md = bi.getMetaFromState(world.getBlockState(pos));
        return !CropUtils.lampBlacklist.contains(bi.getUnlocalizedName() + md);
    }
    
    static {
        CropUtils.clickableCrops = new ArrayList<String>();
        CropUtils.standardCrops = new ArrayList<String>();
        CropUtils.stackedCrops = new ArrayList<String>();
        CropUtils.lampBlacklist = new ArrayList<String>();
    }
}
