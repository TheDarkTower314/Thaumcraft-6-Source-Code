// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.SoundType;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;

public class BlockTC extends Block
{
    public BlockTC(final Material material, final String name) {
        super(material);
        this.setUnlocalizedName(name);
        this.setRegistryName("thaumcraft", name);
        this.setCreativeTab(ConfigItems.TABTC);
        this.setResistance(2.0f);
        this.setHardness(1.5f);
    }
    
    public BlockTC(final Material mat, final String name, final SoundType st) {
        this(mat, name);
        this.setSoundType(st);
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final CreativeTabs tab, final NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }
    
    public int damageDropped(final IBlockState state) {
        return 0;
    }
}
