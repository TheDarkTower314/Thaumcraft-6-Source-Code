package thaumcraft.common.tiles.crafting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TileVoidSiphon extends TileThaumcraftInventory
{
    private static int[] slots;
    int counter;
    public int progress;
    public int PROGREQ = 2000;
    
    public TileVoidSiphon() {
        super(1);
        counter = 0;
        progress = 0;
    }
    
    @Override
    public void update() {
        super.update();
        ++counter;
        if (!getWorld().isRemote || !BlockStateUtils.isEnabled(getBlockMetadata())) {
            if (!getWorld().isRemote && BlockStateUtils.isEnabled(getBlockMetadata()) && counter % 20 == 0 && progress < 2000 && (getStackInSlot(0).isEmpty() || (getStackInSlot(0).getItem() == ItemsTC.voidSeed && getStackInSlot(0).getCount() < getStackInSlot(0).getMaxStackSize()))) {
                List<EntityFluxRift> frl = getValidRifts();
                boolean b = false;
                for (EntityFluxRift fr : frl) {
                    double d = Math.sqrt(fr.getRiftSize());
                    progress += (int)d;
                    fr.setRiftStability((float)(fr.getRiftStability() - d / 15.0));
                    if (world.rand.nextInt(33) == 0) {
                        fr.setRiftSize(fr.getRiftSize() - 1);
                    }
                    b = (d >= 1.0);
                }
                if (b && counter % 40 == 0) {
                    world.addBlockEvent(pos, getBlockType(), 5, counter);
                }
                b = false;
                while (progress >= 2000 && (getStackInSlot(0).isEmpty() || (getStackInSlot(0).getItem() == ItemsTC.voidSeed && getStackInSlot(0).getCount() < getStackInSlot(0).getMaxStackSize()))) {
                    progress -= 2000;
                    if (getStackInSlot(0).isEmpty()) {
                        setInventorySlotContents(0, new ItemStack(ItemsTC.voidSeed));
                    }
                    else {
                        getStackInSlot(0).setCount(getStackInSlot(0).getCount() + 1);
                    }
                    b = true;
                }
                if (b) {
                    syncTile(false);
                    markDirty();
                }
            }
        }
    }
    
    private List<EntityFluxRift> getValidRifts() {
        ArrayList<EntityFluxRift> ret = new ArrayList<EntityFluxRift>();
        List<EntityFluxRift> frl = EntityUtils.getEntitiesInRange(getWorld(), getPos(), null, EntityFluxRift.class, 8.0);
        for (EntityFluxRift fr : frl) {
            if (!fr.isDead) {
                if (fr.getRiftSize() < 2) {
                    continue;
                }
                double xx = getPos().getX() + 0.5;
                double yy = getPos().getY() + 1;
                double zz = getPos().getZ() + 0.5;
                Vec3d v1 = new Vec3d(xx, yy, zz);
                Vec3d v2 = new Vec3d(fr.posX, fr.posY, fr.posZ);
                v1 = v1.add(v2.subtract(v1).normalize());
                if (!EntityUtils.canEntityBeSeen(fr, v1.x, v1.y, v1.z)) {
                    continue;
                }
                ret.add(fr);
            }
        }
        return ret;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        progress = nbt.getShort("progress");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("progress", (short) progress);
        return nbt;
    }
    
    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack) {
        return stack.getItem() == ItemsTC.voidSeed;
    }
    
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return TileVoidSiphon.slots;
    }
    
    @Override
    public boolean canInsertItem(int par1, ItemStack stack, EnumFacing par3) {
        return false;
    }
    
    @Override
    public boolean canExtractItem(int par1, ItemStack stack2, EnumFacing par3) {
        return true;
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 5) {
            if (world.isRemote) {
                List<EntityFluxRift> frl = getValidRifts();
                for (EntityFluxRift fr : frl) {
                    FXDispatcher.INSTANCE.voidStreak(fr.posX, fr.posY, fr.posZ, getPos().getX() + 0.5, getPos().getY() + 0.5625f, getPos().getZ() + 0.5, j, 0.04f);
                }
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    static {
        slots = new int[] { 0 };
    }
}
