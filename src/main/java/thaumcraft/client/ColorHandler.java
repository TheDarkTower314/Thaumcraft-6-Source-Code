// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client;

import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.biome.BiomeColorHelper;
import thaumcraft.common.tiles.essentia.TileTubeFilter;
import thaumcraft.common.blocks.essentia.BlockTube;
import thaumcraft.common.blocks.devices.BlockInlay;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileStabilizer;
import thaumcraft.common.blocks.devices.BlockStabilizer;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.blocks.world.ore.BlockCrystal;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import net.minecraft.item.ItemArmor;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.common.golems.GolemProperties;
import net.minecraft.block.state.IBlockState;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.color.IItemColor;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.Item;
import java.util.Iterator;
import net.minecraft.client.renderer.color.IBlockColor;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorHandler
{
    public static void registerColourHandlers() {
        final BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
        final ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        registerBlockColourHandlers(blockColors);
        registerItemColourHandlers(blockColors, itemColors);
    }
    
    private static void registerBlockColourHandlers(final BlockColors blockColors) {
        final IBlockColor basicColourHandler = (state, blockAccess, pos, tintIndex) -> state.getBlock().getMapColor(state, blockAccess, pos).colorValue;
        final Block[] basicBlocks = new Block[BlocksTC.candles.size() + BlocksTC.banners.size() + BlocksTC.nitor.size()];
        int i = 0;
        for (final Block b : BlocksTC.candles.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        for (final Block b : BlocksTC.banners.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        for (final Block b : BlocksTC.nitor.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        blockColors.registerBlockColorHandler(basicColourHandler, basicBlocks);
        final IBlockColor grassColourHandler = (state, blockAccess, pos, tintIndex) -> (blockAccess != null && pos != null) ? BiomeColorHelper.getGrassColorAtPos(blockAccess, pos) : ColorizerGrass.getGrassColor(0.5, 1.0);
        blockColors.registerBlockColorHandler(grassColourHandler, new Block[] { BlocksTC.grassAmbient });
        final IBlockColor leafColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() == BlocksTC.leafSilverwood) {
                return 16777215;
            }
            if (blockAccess != null && pos != null) {
                return BiomeColorHelper.getFoliageColorAtPos(blockAccess, pos);
            }
            return ColorizerFoliage.getFoliageColorBasic();
        };
        blockColors.registerBlockColorHandler(leafColourHandler, new Block[] { BlocksTC.leafGreatwood, BlocksTC.leafSilverwood });
        final IBlockColor crystalColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockCrystal) {
                return ((BlockCrystal)state.getBlock()).aspect.getColor();
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(crystalColourHandler, new Block[] { BlocksTC.crystalAir, BlocksTC.crystalEarth, BlocksTC.crystalFire, BlocksTC.crystalWater, BlocksTC.crystalEntropy, BlocksTC.crystalOrder, BlocksTC.crystalTaint });
        final IBlockColor tubeFilterColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockTube && tintIndex == 1) {
                final TileEntity te = blockAccess.getTileEntity(pos);
                if (te != null && te instanceof TileTubeFilter && ((TileTubeFilter)te).aspectFilter != null) {
                    return ((TileTubeFilter)te).aspectFilter.getColor();
                }
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(tubeFilterColourHandler, new Block[] { BlocksTC.tubeFilter });
        final IBlockColor inlayColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockInlay && tintIndex == 0) {
                final BlockInlay blockInlay = (BlockInlay)state.getBlock();
                return BlockInlay.colorMultiplier(state.getBlock().getMetaFromState(state));
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(inlayColourHandler, new Block[] { BlocksTC.inlay });
        final IBlockColor stabilizerColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockStabilizer && tintIndex == 0) {
                int charge = 0;
                final TileEntity te = blockAccess.getTileEntity(pos);
                if (te != null && te instanceof TileStabilizer) {
                    charge = ((TileStabilizer)te).getEnergy();
                }
                final BlockStabilizer blockStabilizer = (BlockStabilizer)state.getBlock();
                return BlockStabilizer.colorMultiplier(charge);
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(stabilizerColourHandler, new Block[] { BlocksTC.stabilizer });
    }
    
    private static void registerItemColourHandlers(final BlockColors blockColors, final ItemColors itemColors) {
        final IItemColor itemBlockColourHandler = (stack, tintIndex) -> {
            final IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return blockColors.colorMultiplier(state, null, null, tintIndex);
        };
        final Block[] basicBlocks = new Block[BlocksTC.candles.size() + BlocksTC.nitor.size() + 3];
        int i = 0;
        for (final Block b : BlocksTC.candles.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        for (final Block b : BlocksTC.nitor.values()) {
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
        final IItemColor itemEssentiaColourHandler = (stack, tintIndex) -> {
            final ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.getItem();
            try {
                if (item != null && item.getAspects(stack) != null) {
                    return item.getAspects(stack).getAspects()[0].getColor();
                }
            }
            catch (final Exception ex) {}
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemEssentiaColourHandler, new Item[] { ItemsTC.crystalEssence });
        final IItemColor itemJarColourHandler = (stack, tintIndex) -> {
            final BlockJarItem item = (BlockJarItem)stack.getItem();
            try {
                if (item.getAspects(stack) != null && tintIndex == 1) {
                    return item.getAspects(stack).getAspects()[0].getColor();
                }
            }
            catch (final Exception ex) {}
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemJarColourHandler, new Block[] { BlocksTC.jarNormal });
        itemColors.registerItemColorHandler(itemJarColourHandler, new Block[] { BlocksTC.jarVoid });
        final IItemColor itemCrystalPlanterColourHandler = (stack, tintIndex) -> {
            final Item item = stack.getItem();
            if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof BlockCrystal) {
                return ((BlockCrystal)((ItemBlock)item).getBlock()).aspect.getColor();
            }
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalAir });
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalEarth });
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalFire });
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalWater });
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalEntropy });
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalOrder });
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, new Block[] { BlocksTC.crystalTaint });
        final IItemColor itemEssentiaAltColourHandler = (stack, tintIndex) -> {
            final ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.getItem();
            if (stack.getItemDamage() == 1 && item.getAspects(stack) != null && tintIndex == 1) {
                return item.getAspects(stack).getAspects()[0].getColor();
            }
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemEssentiaAltColourHandler, new Item[] { ItemsTC.phial, ItemsTC.label });
        final IItemColor itemArmorColourHandler = (stack, tintIndex) -> {
            final ItemArmor item = (ItemArmor)stack.getItem();
            return (tintIndex > 0) ? -1 : item.getColor(stack);
        };
        itemColors.registerItemColorHandler(itemArmorColourHandler, new Item[] { ItemsTC.voidRobeChest, ItemsTC.voidRobeHelm, ItemsTC.voidRobeLegs, ItemsTC.clothChest, ItemsTC.clothLegs, ItemsTC.clothBoots });
        final IItemColor itemCasterColourHandler = (stack, tintIndex) -> {
            final ItemCaster item = (ItemCaster)stack.getItem();
            final ItemFocus focus = item.getFocus(stack);
            return (tintIndex > 0 && focus != null) ? focus.getFocusColor(item.getFocusStack(stack)) : -1;
        };
        itemColors.registerItemColorHandler(itemCasterColourHandler, new Item[] { ItemsTC.casterBasic });
        final IItemColor itemFocusColourHandler = (stack, tintIndex) -> {
            final ItemFocus item = (ItemFocus)stack.getItem();
            final int color = item.getFocusColor(stack);
            return color;
        };
        itemColors.registerItemColorHandler(itemFocusColourHandler, new Item[] { ItemsTC.focus1 });
        itemColors.registerItemColorHandler(itemFocusColourHandler, new Item[] { ItemsTC.focus2 });
        itemColors.registerItemColorHandler(itemFocusColourHandler, new Item[] { ItemsTC.focus3 });
        final IItemColor itemGolemColourHandler = (stack, tintIndex) -> {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("props")) {
                final IGolemProperties props = GolemProperties.fromLong(stack.getTagCompound().getLong("props"));
                return props.getMaterial().itemColor;
            }
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemGolemColourHandler, new Item[] { ItemsTC.golemPlacer });
        final IItemColor itemBannerColourHandler = (stack, tintIndex) -> {
            if (tintIndex == 1) {
                final IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
                return blockColors.colorMultiplier(state, null, null, tintIndex);
            }
            if (tintIndex != 2) {
                return 16777215;
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("aspect") && stack.getTagCompound().getString("aspect") != null) {
                return Aspect.getAspect(stack.getTagCompound().getString("aspect")).getColor();
            }
            final IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return blockColors.colorMultiplier(state, null, null, tintIndex);
        };
        final Block[] bannerBlocks = new Block[BlocksTC.banners.size()];
        i = 0;
        for (final Block b2 : BlocksTC.banners.values()) {
            bannerBlocks[i] = b2;
            ++i;
        }
        itemColors.registerItemColorHandler(itemBannerColourHandler, bannerBlocks);
    }
}
