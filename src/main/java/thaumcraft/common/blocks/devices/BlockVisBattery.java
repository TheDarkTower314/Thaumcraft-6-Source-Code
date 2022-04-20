// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.devices;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.world.IBlockAccess;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.world.aura.AuraHandler;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.Block;

public class BlockVisBattery extends Block
{
    public static final PropertyInteger CHARGE;
    
    public BlockVisBattery() {
        super(Material.ROCK);
        this.setUnlocalizedName("vis_battery");
        this.setRegistryName("thaumcraft", "vis_battery");
        this.setHardness(0.5f);
        this.setSoundType(SoundType.STONE);
        this.setTickRandomly(true);
        this.setCreativeTab(ConfigItems.TABTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)BlockVisBattery.CHARGE, (Comparable)0));
    }
    
    public int damageDropped(final IBlockState state) {
        return 0;
    }
    
    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (!worldIn.isRemote) {
            final int charge = this.getMetaFromState(state);
            if (worldIn.isBlockPowered(pos)) {
                if (charge > 0) {
                    AuraHandler.addVis(worldIn, pos, 1.0f);
                    worldIn.setBlockState(pos, state.withProperty((IProperty)BlockVisBattery.CHARGE, (Comparable)(charge - 1)));
                    worldIn.scheduleUpdate(pos, state.getBlock(), 5);
                }
            }
            else {
                final float aura = AuraHelper.getVis(worldIn, pos);
                final int base = AuraHelper.getAuraBase(worldIn, pos);
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
    
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos fromPos) {
        if (worldIn.isBlockPowered(pos)) {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }
    
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return true;
    }
    
    public int getComparatorInputOverride(final IBlockState state, final World world, final BlockPos pos) {
        return this.getMetaFromState(state);
    }
    
    public int getLightValue(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return state.getBlock().getMetaFromState(state);
    }
    
    public int getPackedLightmapCoords(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        final int i = source.getCombinedLight(pos, state.getLightValue(source, pos));
        final int j = 180;
        final int k = i & 0xFF;
        final int l = j & 0xFF;
        final int i2 = i >> 16 & 0xFF;
        final int j2 = j >> 16 & 0xFF;
        return ((k > l) ? k : l) | ((i2 > j2) ? i2 : j2) << 16;
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {BlockVisBattery.CHARGE});
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty((IProperty)BlockVisBattery.CHARGE, (Comparable)meta);
    }
    
    public int getMetaFromState(final IBlockState state) {
        return (int)state.getValue((IProperty)BlockVisBattery.CHARGE);
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final CreativeTabs tab, final NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }
    
    static {
        CHARGE = PropertyInteger.create("charge", 0, 10);
    }
}
