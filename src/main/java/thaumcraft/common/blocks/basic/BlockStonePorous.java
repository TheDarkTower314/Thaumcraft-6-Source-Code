package thaumcraft.common.blocks.basic;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.config.ModConfig;


public class BlockStonePorous extends BlockTC
{
    static Random r;
    static ArrayList<WeightedRandomLoot> pdrops;
    
    public BlockStonePorous() {
        super(Material.ROCK, "stone_porous");
        setHardness(1.0f);
        setResistance(5.0f);
        setSoundType(SoundType.STONE);
    }
    
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> ret = new ArrayList<ItemStack>();
        int rr = BlockStonePorous.r.nextInt(15) + fortune;
        if (rr > 13) {
            if (BlockStonePorous.pdrops == null || BlockStonePorous.pdrops.size() <= 0) {
                createDrops();
            }
            ItemStack s = ((WeightedRandomLoot)WeightedRandom.getRandomItem(BlockStonePorous.r, (List)BlockStonePorous.pdrops)).item.copy();
            ret.add(s);
        }
        else {
            ret.add(new ItemStack(Blocks.GRAVEL));
        }
        return ret;
    }
    
    private void createDrops() {
        BlockStonePorous.pdrops = new ArrayList<WeightedRandomLoot>();
        for (Aspect aspect : Aspect.getCompoundAspects()) {
            ItemStack is = new ItemStack(ItemsTC.crystalEssence);
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
