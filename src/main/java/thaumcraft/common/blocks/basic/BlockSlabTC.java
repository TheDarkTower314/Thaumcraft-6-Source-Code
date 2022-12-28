package thaumcraft.common.blocks.basic;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigItems;


public class BlockSlabTC extends BlockSlab
{
    public static PropertyEnum<Variant> VARIANT;
    boolean wood;
    Block drop;
    
    protected BlockSlabTC(String name, Block b, boolean wood) {
        super(wood ? Material.WOOD : Material.ROCK);
        this.wood = false;
        drop = null;
        drop = b;
        this.wood = wood;
        setUnlocalizedName(name);
        setRegistryName("thaumcraft", name);
        IBlockState iblockstate = blockState.getBaseState();
        if (!isDouble()) {
            iblockstate = iblockstate.withProperty((IProperty)BlockSlabTC.HALF, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM);
            setCreativeTab(ConfigItems.TABTC);
        }
        setSoundType(wood ? SoundType.WOOD : SoundType.STONE);
        setDefaultState(iblockstate.withProperty((IProperty)BlockSlabTC.VARIANT, (Comparable)Variant.DEFAULT));
        useNeighborBrightness = !isDouble();
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return (drop == null) ? Item.getItemFromBlock(state.getBlock()) : Item.getItemFromBlock(drop);
    }
    
    @SideOnly(Side.CLIENT)
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(state.getBlock());
    }
    
    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = getDefaultState().withProperty((IProperty)BlockSlabTC.VARIANT, (Comparable)Variant.DEFAULT);
        if (!isDouble()) {
            iblockstate = iblockstate.withProperty((IProperty)BlockSlabTC.HALF, (Comparable)(((meta & 0x8) == 0x0) ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP));
        }
        return iblockstate;
    }
    
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        if (!isDouble() && state.getValue((IProperty)BlockSlabTC.HALF) == BlockSlab.EnumBlockHalf.TOP) {
            i |= 0x8;
        }
        return i;
    }
    
    protected BlockStateContainer createBlockState() {
        return isDouble() ? new BlockStateContainer(this, BlockSlabTC.VARIANT) : new BlockStateContainer(this, BlockSlabTC.HALF, BlockSlabTC.VARIANT);
    }
    
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    public boolean isDouble() {
        return false;
    }
    
    public String getUnlocalizedName(int meta) {
        return getUnlocalizedName();
    }
    
    public IProperty<?> getVariantProperty() {
        return BlockSlabTC.VARIANT;
    }
    
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return Variant.DEFAULT;
    }
    
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return wood ? 20 : super.getFlammability(world, pos, face);
    }
    
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return wood ? 5 : super.getFireSpreadSpeed(world, pos, face);
    }
    
    static {
        VARIANT = PropertyEnum.create("variant", Variant.class);
    }
    
    public static class Double extends BlockSlabTC
    {
        public Double(String name, Block b, boolean wood) {
            super(name, b, wood);
        }
        
        @Override
        public boolean isDouble() {
            return true;
        }
    }
    
    public static class Half extends BlockSlabTC
    {
        public Half(String name, Block b, boolean wood) {
            super(name, b, wood);
        }
        
        @Override
        public boolean isDouble() {
            return false;
        }
    }
    
    public enum Variant implements IStringSerializable
    {
        DEFAULT;
        
        public String getName() {
            return "default";
        }
    }
}
