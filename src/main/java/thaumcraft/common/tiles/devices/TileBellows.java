package thaumcraft.common.tiles.devices;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileBellows extends TileThaumcraft implements ITickable
{
    public float inflation;
    boolean direction;
    boolean firstrun;
    public int delay;
    
    public TileBellows() {
        inflation = 1.0f;
        direction = false;
        firstrun = true;
        delay = 0;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX() - 0.3, getPos().getY() - 0.3, getPos().getZ() - 0.3, getPos().getX() + 1.3, getPos().getY() + 1.3, getPos().getZ() + 1.3);
    }
    
    public void update() {
        if (world.isRemote) {
            if (BlockStateUtils.isEnabled(getBlockMetadata())) {
                if (firstrun) {
                    inflation = 0.35f + world.rand.nextFloat() * 0.55f;
                }
                firstrun = false;
                if (inflation > 0.35f && !direction) {
                    inflation -= 0.075f;
                }
                if (inflation <= 0.35f && !direction) {
                    direction = true;
                }
                if (inflation < 1.0f && direction) {
                    inflation += 0.025f;
                }
                if (inflation >= 1.0f && direction) {
                    direction = false;
                    world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 0.01f, 0.5f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f, false);
                }
            }
        }
        else if (BlockStateUtils.isEnabled(getBlockMetadata())) {
            ++delay;
            if (delay >= 2) {
                delay = 0;
                TileEntity tile = world.getTileEntity(pos.offset(BlockStateUtils.getFacing(getBlockMetadata())));
                if (tile != null && tile instanceof TileEntityFurnace) {
                    TileEntityFurnace tf = (TileEntityFurnace)tile;
                    int ct = getCooktime(tf);
                    if (ct > 0 && ct < 199) {
                        setCooktime(tf, ct + 1);
                    }
                }
            }
        }
    }
    
    public void setCooktime(TileEntityFurnace ent, int hit) {
        ent.cookTime = hit;
    }
    
    public int getCooktime(TileEntityFurnace ent) {
        return ent.cookTime;
    }
    
    public static int getBellows(World world, BlockPos pos, EnumFacing[] directions) {
        int bellows = 0;
        for (EnumFacing dir : directions) {
            TileEntity tile = world.getTileEntity(pos.offset(dir));
            try {
                if (tile != null && tile instanceof TileBellows && BlockStateUtils.getFacing(tile.getBlockMetadata()) == dir.getOpposite() && BlockStateUtils.isEnabled(tile.getBlockMetadata())) {
                    ++bellows;
                }
            }
            catch (Exception ex) {}
        }
        return bellows;
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
}
