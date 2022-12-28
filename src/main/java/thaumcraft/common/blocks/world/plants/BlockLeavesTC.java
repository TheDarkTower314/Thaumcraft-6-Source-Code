package thaumcraft.common.blocks.world.plants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.world.aura.AuraHandler;


public class BlockLeavesTC extends BlockLeaves
{
    public BlockLeavesTC(String name) {
        setDefaultState(blockState.getBaseState().withProperty((IProperty)BlockLeavesTC.CHECK_DECAY, (Comparable)true).withProperty((IProperty)BlockLeavesTC.DECAYABLE, (Comparable)true));
        setCreativeTab(ConfigItems.TABTC);
        setUnlocalizedName(name);
        setRegistryName("thaumcraft", name);
    }
    
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 60;
    }
    
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 30;
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return Blocks.LEAVES.getBlockLayer();
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return Blocks.LEAVES.isOpaqueCube(state);
    }
    
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (placer != null && placer instanceof EntityPlayer) {
            worldIn.setBlockState(pos, state.withProperty((IProperty)BlockLeavesTC.DECAYABLE, (Comparable)false));
        }
    }
    
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        setGraphicsLevel(!isOpaqueCube(blockState));
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
    
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return (state.getBlock() == BlocksTC.leafSilverwood) ? MapColor.LIGHT_BLUE : super.getMapColor(state, worldIn, pos);
    }
    
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this));
    }
    
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(this);
    }
    
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote && state.getBlock() == BlocksTC.leafSilverwood && (boolean)state.getValue((IProperty)BlockLeavesTC.DECAYABLE) && AuraHandler.getVis(worldIn, pos) < AuraHandler.getAuraBase(worldIn, pos)) {
            AuraHandler.addVis(worldIn, pos, 0.01f);
        }
        super.updateTick(worldIn, pos, state, rand);
    }
    
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty((IProperty)BlockLeavesTC.DECAYABLE, (Comparable)((meta & 0x4) == 0x0)).withProperty((IProperty)BlockLeavesTC.CHECK_DECAY, (Comparable)((meta & 0x8) > 0));
    }
    
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        if (!(boolean)state.getValue(BlockLeavesTC.DECAYABLE)) {
            i |= 0x4;
        }
        if (state.getValue(BlockLeavesTC.CHECK_DECAY)) {
            i |= 0x8;
        }
        return i;
    }
    
    protected int getSaplingDropChance(IBlockState state) {
        return 75;
    }
    
    protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
        if (state.getBlock() == BlocksTC.leafSilverwood && worldIn.rand.nextInt((int)(chance * 0.75)) == 0) {
            spawnAsEntity(worldIn, pos, new ItemStack(ItemsTC.nuggets, 1, 5));
        }
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return (state.getBlock() == BlocksTC.leafSilverwood) ? Item.getItemFromBlock(BlocksTC.saplingSilverwood) : Item.getItemFromBlock(BlocksTC.saplingGreatwood);
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockLeavesTC.CHECK_DECAY, BlockLeavesTC.DECAYABLE);
    }
    
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        IBlockState state = world.getBlockState(pos);
        return new ArrayList<ItemStack>(Arrays.asList(new ItemStack(this)));
    }
    
    public BlockPlanks.EnumType getWoodType(int meta) {
        return null;
    }
}
