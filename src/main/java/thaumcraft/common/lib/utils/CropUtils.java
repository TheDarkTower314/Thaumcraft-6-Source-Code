package thaumcraft.common.lib.utils;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class CropUtils
{
    public static ArrayList<String> clickableCrops;
    public static ArrayList<String> standardCrops;
    public static ArrayList<String> stackedCrops;
    public static ArrayList<String> lampBlacklist;
    
    public static void addStandardCrop(ItemStack stack, int grownMeta) {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == null) {
            return;
        }
        addStandardCrop(block, grownMeta);
    }
    
    public static void addStandardCrop(Block block, int grownMeta) {
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
    
    public static void addClickableCrop(ItemStack stack, int grownMeta) {
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
    
    public static void addStackedCrop(ItemStack stack, int grownMeta) {
        if (Block.getBlockFromItem(stack.getItem()) == null) {
            return;
        }
        addStackedCrop(Block.getBlockFromItem(stack.getItem()), grownMeta);
    }
    
    public static void addStackedCrop(Block block, int grownMeta) {
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
    
    public static boolean isGrownCrop(World world, BlockPos pos) {
        if (world.isAirBlock(pos)) {
            return false;
        }
        boolean found = false;
        IBlockState bs = world.getBlockState(pos);
        Block bi = bs.getBlock();
        int md = bi.getMetaFromState(bs);
        if (CropUtils.standardCrops.contains(bi.getUnlocalizedName() + md) || CropUtils.clickableCrops.contains(bi.getUnlocalizedName() + md) || CropUtils.stackedCrops.contains(bi.getUnlocalizedName() + md)) {
            found = true;
        }
        Block biB = world.getBlockState(pos.down()).getBlock();
        return (bi instanceof IGrowable && !((IGrowable)bi).canGrow(world, pos, world.getBlockState(pos), world.isRemote) && !(bi instanceof BlockStem)) || (bi instanceof BlockCrops && md == 7 && !found) || CropUtils.standardCrops.contains(bi.getUnlocalizedName() + md) || CropUtils.clickableCrops.contains(bi.getUnlocalizedName() + md) || (CropUtils.stackedCrops.contains(bi.getUnlocalizedName() + md) && biB == bi);
    }
    
    public static void blacklistLamp(ItemStack stack, int meta) {
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
    
    public static boolean doesLampGrow(World world, BlockPos pos) {
        Block bi = world.getBlockState(pos).getBlock();
        int md = bi.getMetaFromState(world.getBlockState(pos));
        return !CropUtils.lampBlacklist.contains(bi.getUnlocalizedName() + md);
    }
    
    static {
        CropUtils.clickableCrops = new ArrayList<String>();
        CropUtils.standardCrops = new ArrayList<String>();
        CropUtils.stackedCrops = new ArrayList<String>();
        CropUtils.lampBlacklist = new ArrayList<String>();
    }
}
