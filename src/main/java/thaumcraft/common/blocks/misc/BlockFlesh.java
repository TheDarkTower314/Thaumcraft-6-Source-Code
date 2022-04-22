// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.misc;

import thaumcraft.common.lib.SoundsTC;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTC;

public class BlockFlesh extends BlockTC
{
    public BlockFlesh() {
        super(Material.SPONGE, "flesh_block");
        setResistance(2.0f);
        setHardness(0.25f);
    }
    
    public SoundType getSoundType() {
        return SoundsTC.GORE;
    }
}
