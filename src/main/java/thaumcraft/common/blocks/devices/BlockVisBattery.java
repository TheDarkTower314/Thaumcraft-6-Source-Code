package thaumcraft.common.blocks.devices;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.world.aura.AuraHandler;


public class BlockVisBattery extends Block
{
    public static PropertyInteger CHARGE;
    
    public BlockVisBattery() {
        super(Material.ROCK);
        setUnlocalizedName("vis_battery");
        setRegistryName("thaumcraft", "vis_battery");
        setHardness(0.5f);
        setSoundType(SoundType.STONE);
        setTickRandomly(true);
        setCreativeTab(ConfigItems.TABTC);
        setDefaultState(blockState.getBaseState().withProperty((IProperty)BlockVisBattery.CHARGE, (Comparable)0));
    }
    
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            int charge = getMetaFromState(state);
            if (worldIn.isBlockPowered(pos)) {
                if (charge > 0) {
                    AuraHandler.addVis(worldIn, pos, 1.0f);
                    worldIn.setBlockState(pos, state.withProperty((IProperty)BlockVisBattery.CHARGE, (Comparable)(charge - 1)));
                    worldIn.scheduleUpdate(pos, state.getBlock(), 5);
                }
            }
            else {
                float aura = AuraHelper.getVis(worldIn, pos);
                int base = AuraHelper.getAuraBase(worldIn, pos);
                if (charge < 10 && aura > base * 0.9 && aura > 1.0f) {
                    AuraHandler.drainVis(worldIn, pos, 1.0f, false);
                    worldIn.setBlockState(pos, state.withProperty((IProperty)BlockVisBattery.CHARGE, (Comparable)(charge + 1)));
                    worldIn.scheduleUpdate(pos, state.getBlock(), 100 + rand.nextInt(100));
                }
                else if (charge > 0 && aura < base * 0.75) {
                    AuraHandler.addVis(worldIn, pos, 1.0f);
                    worldIn.setBlockState(pos, state.withProperty((IProperty)BlockVisBattery.CHARGE, (Comparable)(charge - 1)));
                    worldIn.scheduleUpdate(pos, state.getBlock(), 20 + rand.nextInt(20));
                }
            }
        }
    }
    
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (worldIn.isBlockPowered(pos)) {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }
    
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }
    
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        return getMetaFromState(state);
    }
    
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getBlock().getMetaFromState(state);
    }
    
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
        int i = source.getCombinedLight(pos, state.getLightValue(source, pos));
        int j = 180;
        int k = i & 0xFF;
        int l = j & 0xFF;
        int i2 = i >> 16 & 0xFF;
        int j2 = j >> 16 & 0xFF;
        return ((k > l) ? k : l) | ((i2 > j2) ? i2 : j2) << 16;
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockVisBattery.CHARGE);
    }
    
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty((IProperty)BlockVisBattery.CHARGE, (Comparable)meta);
    }
    
    public int getMetaFromState(IBlockState state) {
        return (int)state.getValue((IProperty)BlockVisBattery.CHARGE);
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }
    
    static {
        CHARGE = PropertyInteger.create("charge", 0, 10);
    }
}
