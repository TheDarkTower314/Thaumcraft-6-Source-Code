// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.basic;

import java.util.Iterator;
import net.minecraft.init.Items;
import thaumcraft.common.config.ModConfig;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.item.ItemStack;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.api.internal.WeightedRandomLoot;
import java.util.ArrayList;
import java.util.Random;
import thaumcraft.common.blocks.BlockTC;

public class BlockStonePorous extends BlockTC
{
    static Random r;
    static ArrayList<WeightedRandomLoot> pdrops;
    
    public BlockStonePorous() {
        super(Material.ROCK, "stone_porous");
        this.setHardness(1.0f);
        this.setResistance(5.0f);
        this.setSoundType(SoundType.STONE);
    }
    
    public List<ItemStack> getDrops(final IBlockAccess world, final BlockPos pos, final IBlockState state, final int fortune) {
        final List<ItemStack> ret = new ArrayList<ItemStack>();
        final int rr = BlockStonePorous.r.nextInt(15) + fortune;
        if (rr > 13) {
            if (BlockStonePorous.pdrops == null || BlockStonePorous.pdrops.size() <= 0) {
                this.createDrops();
            }
            final ItemStack s = ((WeightedRandomLoot)WeightedRandom.getRandomItem(BlockStonePorous.r, (List)BlockStonePorous.pdrops)).item.copy();
            ret.add(s);
        }
        else {
            ret.add(new ItemStack(Blocks.GRAVEL));
        }
        return ret;
    }
    
    private void createDrops() {
        BlockStonePorous.pdrops = new ArrayList<WeightedRandomLoot>();
        for (final Aspect aspect : Aspect.getCompoundAspects()) {
            final ItemStack is = new ItemStack(ItemsTC.crystalEssence);
            ((ItemGenericEssentiaContainer)ItemsTC.crystalEssence).setAspects(is, new AspectList().add(aspect, (aspect == Aspect.FLUX) ? 100 : (aspect.isPrimal() ? 20 : 1)));
            BlockStonePorous.pdrops.add(new WeightedRandomLoot(is, 1));
        }
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.amber), 20));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 0), 20));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 1), 10));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 6), 10));
        if (ModConfig.foundCopperIngot) {
            BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 2), 10));
        }
        if (ModConfig.foundTinIngot) {
            BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 3), 10));
        }
        if (ModConfig.foundSilverIngot) {
            BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 4), 8));
        }
        if (ModConfig.foundLeadIngot) {
            BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 5), 10));
        }
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.DIAMOND), 2));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.EMERALD), 4));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.REDSTONE), 8));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.PRISMARINE_CRYSTALS), 3));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.PRISMARINE_SHARD), 3));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.CLAY_BALL), 30));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.QUARTZ), 15));
    }
    
    static {
        BlockStonePorous.r = new Random(System.currentTimeMillis());
        BlockStonePorous.pdrops = null;
    }
}
