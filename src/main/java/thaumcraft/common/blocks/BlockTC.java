package thaumcraft.common.blocks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigItems;


public class BlockTC extends Block
{
    public BlockTC(Material material, String name) {
        super(material);
        setUnlocalizedName(name);
        setRegistryName("thaumcraft", name);
        setCreativeTab(ConfigItems.TABTC);
        setResistance(2.0f);
        setHardness(1.5f);
    }
    
    public BlockTC(Material mat, String name, SoundType st) {
        this(mat, name);
        setSoundType(st);
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }
    
    public int damageDropped(IBlockState state) {
        return 0;
    }
}
