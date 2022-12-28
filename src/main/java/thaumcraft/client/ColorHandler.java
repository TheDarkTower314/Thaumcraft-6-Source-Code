package thaumcraft.client;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.devices.BlockInlay;
import thaumcraft.common.blocks.devices.BlockStabilizer;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.blocks.essentia.BlockTube;
import thaumcraft.common.blocks.world.ore.BlockCrystal;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.tiles.devices.TileStabilizer;
import thaumcraft.common.tiles.essentia.TileTubeFilter;


@SideOnly(Side.CLIENT)
public class ColorHandler
{
    public static void registerColourHandlers() {
        BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        registerBlockColourHandlers(blockColors);
        registerItemColourHandlers(blockColors, itemColors);
    }
    
    private static void registerBlockColourHandlers(BlockColors blockColors) {
        IBlockColor basicColourHandler = (state, blockAccess, pos, tintIndex) -> state.getBlock().getMapColor(state, blockAccess, pos).colorValue;
        Block[] basicBlocks = new Block[BlocksTC.candles.size() + BlocksTC.banners.size() + BlocksTC.nitor.size()];
        int i = 0;
        for (Block b : BlocksTC.candles.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        for (Block b : BlocksTC.banners.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        for (Block b : BlocksTC.nitor.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        blockColors.registerBlockColorHandler(basicColourHandler, basicBlocks);
        IBlockColor grassColourHandler = (state, blockAccess, pos, tintIndex) -> (blockAccess != null && pos != null) ? BiomeColorHelper.getGrassColorAtPos(blockAccess, pos) : ColorizerGrass.getGrassColor(0.5, 1.0);
        blockColors.registerBlockColorHandler(grassColourHandler, BlocksTC.grassAmbient);
        IBlockColor leafColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() == BlocksTC.leafSilverwood) {
                return 16777215;
            }
            if (blockAccess != null && pos != null) {
                return BiomeColorHelper.getFoliageColorAtPos(blockAccess, pos);
            }
            return ColorizerFoliage.getFoliageColorBasic();
        };
        blockColors.registerBlockColorHandler(leafColourHandler, BlocksTC.leafGreatwood, BlocksTC.leafSilverwood);
        IBlockColor crystalColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockCrystal) {
                return ((BlockCrystal)state.getBlock()).aspect.getColor();
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(crystalColourHandler, BlocksTC.crystalAir, BlocksTC.crystalEarth, BlocksTC.crystalFire, BlocksTC.crystalWater, BlocksTC.crystalEntropy, BlocksTC.crystalOrder, BlocksTC.crystalTaint);
        IBlockColor tubeFilterColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockTube && tintIndex == 1) {
                TileEntity te = blockAccess.getTileEntity(pos);
                if (te != null && te instanceof TileTubeFilter && ((TileTubeFilter)te).aspectFilter != null) {
                    return ((TileTubeFilter)te).aspectFilter.getColor();
                }
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(tubeFilterColourHandler, BlocksTC.tubeFilter);
        IBlockColor inlayColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockInlay && tintIndex == 0) {
                BlockInlay blockInlay = (BlockInlay)state.getBlock();
                return BlockInlay.colorMultiplier(state.getBlock().getMetaFromState(state));
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(inlayColourHandler, BlocksTC.inlay);
        IBlockColor stabilizerColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockStabilizer && tintIndex == 0) {
                int charge = 0;
                TileEntity te = blockAccess.getTileEntity(pos);
                if (te != null && te instanceof TileStabilizer) {
                    charge = ((TileStabilizer)te).getEnergy();
                }
                BlockStabilizer blockStabilizer = (BlockStabilizer)state.getBlock();
                return BlockStabilizer.colorMultiplier(charge);
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(stabilizerColourHandler, BlocksTC.stabilizer);
    }
    
    private static void registerItemColourHandlers(BlockColors blockColors, ItemColors itemColors) {
        IItemColor itemBlockColourHandler = (stack, tintIndex) -> {
            IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return blockColors.colorMultiplier(state, null, null, tintIndex);
        };
        Block[] basicBlocks = new Block[BlocksTC.candles.size() + BlocksTC.nitor.size() + 3];
        int i = 0;
        for (Block b : BlocksTC.candles.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        for (Block b : BlocksTC.nitor.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        basicBlocks[i] = BlocksTC.leafGreatwood;
        ++i;
        basicBlocks[i] = BlocksTC.leafSilverwood;
        ++i;
        basicBlocks[i] = BlocksTC.grassAmbient;
        ++i;
        itemColors.registerItemColorHandler(itemBlockColourHandler, basicBlocks);
        IItemColor itemEssentiaColourHandler = (stack, tintIndex) -> {
            ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.getItem();
            try {
                if (item != null && item.getAspects(stack) != null) {
                    return item.getAspects(stack).getAspects()[0].getColor();
                }
            }
            catch (Exception ex) {}
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemEssentiaColourHandler, ItemsTC.crystalEssence);
        IItemColor itemJarColourHandler = (stack, tintIndex) -> {
            BlockJarItem item = (BlockJarItem)stack.getItem();
            try {
                if (item.getAspects(stack) != null && tintIndex == 1) {
                    return item.getAspects(stack).getAspects()[0].getColor();
                }
            }
            catch (Exception ex) {}
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemJarColourHandler, BlocksTC.jarNormal);
        itemColors.registerItemColorHandler(itemJarColourHandler, BlocksTC.jarVoid);
        IItemColor itemCrystalPlanterColourHandler = (stack, tintIndex) -> {
            Item item = stack.getItem();
            if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof BlockCrystal) {
                return ((BlockCrystal)((ItemBlock)item).getBlock()).aspect.getColor();
            }
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalAir);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalEarth);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalFire);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalWater);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalEntropy);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalOrder);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalTaint);
        IItemColor itemEssentiaAltColourHandler = (stack, tintIndex) -> {
            ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.getItem();
            if (stack.getItemDamage() == 1 && item.getAspects(stack) != null && tintIndex == 1) {
                return item.getAspects(stack).getAspects()[0].getColor();
            }
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemEssentiaAltColourHandler, ItemsTC.phial, ItemsTC.label);
        IItemColor itemArmorColourHandler = (stack, tintIndex) -> {
            ItemArmor item = (ItemArmor)stack.getItem();
            return (tintIndex > 0) ? -1 : item.getColor(stack);
        };
        itemColors.registerItemColorHandler(itemArmorColourHandler, ItemsTC.voidRobeChest, ItemsTC.voidRobeHelm, ItemsTC.voidRobeLegs, ItemsTC.clothChest, ItemsTC.clothLegs, ItemsTC.clothBoots);
        IItemColor itemCasterColourHandler = (stack, tintIndex) -> {
            ItemCaster item = (ItemCaster)stack.getItem();
            ItemFocus focus = item.getFocus(stack);
            return (tintIndex > 0 && focus != null) ? focus.getFocusColor(item.getFocusStack(stack)) : -1;
        };
        itemColors.registerItemColorHandler(itemCasterColourHandler, ItemsTC.casterBasic);
        IItemColor itemFocusColourHandler = (stack, tintIndex) -> {
            ItemFocus item = (ItemFocus)stack.getItem();
            int color = item.getFocusColor(stack);
            return color;
        };
        itemColors.registerItemColorHandler(itemFocusColourHandler, ItemsTC.focus1);
        itemColors.registerItemColorHandler(itemFocusColourHandler, ItemsTC.focus2);
        itemColors.registerItemColorHandler(itemFocusColourHandler, ItemsTC.focus3);
        IItemColor itemGolemColourHandler = (stack, tintIndex) -> {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("props")) {
                IGolemProperties props = GolemProperties.fromLong(stack.getTagCompound().getLong("props"));
                return props.getMaterial().itemColor;
            }
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemGolemColourHandler, ItemsTC.golemPlacer);
        IItemColor itemBannerColourHandler = (stack, tintIndex) -> {
            if (tintIndex == 1) {
                IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
                return blockColors.colorMultiplier(state, null, null, tintIndex);
            }
            if (tintIndex != 2) {
                return 16777215;
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("aspect") && stack.getTagCompound().getString("aspect") != null) {
                return Aspect.getAspect(stack.getTagCompound().getString("aspect")).getColor();
            }
            IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return blockColors.colorMultiplier(state, null, null, tintIndex);
        };
        Block[] bannerBlocks = new Block[BlocksTC.banners.size()];
        i = 0;
        for (Block b2 : BlocksTC.banners.values()) {
            bannerBlocks[i] = b2;
            ++i;
        }
        itemColors.registerItemColorHandler(itemBannerColourHandler, bannerBlocks);
    }
}
