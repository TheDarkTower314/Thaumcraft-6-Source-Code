package thaumcraft.common.tiles.essentia;
import java.util.Random;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileJar extends TileThaumcraft implements ITickable
{
    protected static Random rand;
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1);
    }
    
    public void update() {
    }
    
    static {
        TileJar.rand = new Random();
    }
}
