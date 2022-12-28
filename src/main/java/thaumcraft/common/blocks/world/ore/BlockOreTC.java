package thaumcraft.common.blocks.world.ore;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.BlockTC;


public class BlockOreTC extends BlockTC
{
    public BlockOreTC(String name) {
        super(Material.ROCK, name);
        setResistance(5.0f);
        setSoundType(SoundType.STONE);
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return (state.getBlock() == BlocksTC.oreQuartz) ? Items.QUARTZ : ((state.getBlock() == BlocksTC.oreAmber) ? ItemsTC.amber : Item.getItemFromBlock(state.getBlock()));
    }
    
    public int quantityDropped(Random random) {
        return (this == BlocksTC.oreAmber) ? (1 + random.nextInt(2)) : 1;
    }
    
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
        if (this == BlocksTC.oreAmber && drops != null) {
            Random rand = (world instanceof World) ? ((World)world).rand : BlockOreTC.RANDOM;
            for (int a = 0; a < drops.size(); ++a) {
                ItemStack is = drops.get(a);
                if (is != null && !is.isEmpty() && is.getItem() == ItemsTC.amber && rand.nextFloat() < 0.066) {
                    drops.set(a, new ItemStack(ItemsTC.curio, 1, 1));
                }
            }
        }
        return drops;
    }
    
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = (world instanceof World) ? ((World)world).rand : new Random();
        if (getItemDropped(state, rand, fortune) != Item.getItemFromBlock(this)) {
            int j = 0;
            if (this == BlocksTC.oreAmber || this == BlocksTC.oreQuartz) {
                j = MathHelper.getInt(rand, 1, 4);
            }
            return j;
        }
        return 0;
    }
    
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0 && Item.getItemFromBlock(this) != getItemDropped(getBlockState().getValidStates().iterator().next(), random, fortune)) {
            int j = random.nextInt(fortune + 2) - 1;
            if (j < 0) {
                j = 0;
            }
            return quantityDropped(random) * (j + 1);
        }
        return quantityDropped(random);
    }
}
