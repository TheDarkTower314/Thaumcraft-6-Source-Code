package thaumcraft.common.blocks;
import com.google.common.base.Predicate;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;


public interface IBlockFacing
{
    public static PropertyDirection FACING = PropertyDirection.create("facing", new Predicate() {
        public boolean apply(EnumFacing facing) {
            return true;
        }
        
        public boolean apply(Object p_apply_1_) {
            return apply((EnumFacing)p_apply_1_);
        }
    });
}
