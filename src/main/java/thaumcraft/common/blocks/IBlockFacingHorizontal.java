// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks;

import net.minecraft.util.EnumFacing;
import com.google.common.base.Predicate;
import net.minecraft.block.properties.PropertyDirection;

public interface IBlockFacingHorizontal
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate() {
        public boolean apply(final EnumFacing facing) {
            return facing != EnumFacing.UP && facing != EnumFacing.DOWN;
        }
        
        public boolean apply(final Object p_apply_1_) {
            return apply((EnumFacing)p_apply_1_);
        }
    });
}
