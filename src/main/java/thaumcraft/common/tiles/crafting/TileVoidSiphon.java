// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.EnumFacing;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.EntityUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileVoidSiphon extends TileThaumcraftInventory
{
    private static final int[] slots;
    int counter;
    public int progress;
    public final int PROGREQ = 2000;
    
    public TileVoidSiphon() {
        super(1);
        this.counter = 0;
        this.progress = 0;
    }
    
    @Override
    public void update() {
        super.update();
        ++this.counter;
        if (!this.getWorld().isRemote || !BlockStateUtils.isEnabled(this.getBlockMetadata())) {
            if (!this.getWorld().isRemote && BlockStateUtils.isEnabled(this.getBlockMetadata()) && this.counter % 20 == 0 && this.progress < 2000 && (this.getStackInSlot(0).isEmpty() || (this.getStackInSlot(0).getItem() == ItemsTC.voidSeed && this.getStackInSlot(0).getCount() < this.getStackInSlot(0).getMaxStackSize()))) {
                final List<EntityFluxRift> frl = this.getValidRifts();
                boolean b = false;
                for (final EntityFluxRift fr : frl) {
                    final double d = Math.sqrt(fr.getRiftSize());
                    this.progress += (int)d;
                    fr.setRiftStability((float)(fr.getRiftStability() - d / 15.0));
                    if (this.world.rand.nextInt(33) == 0) {
                        fr.setRiftSize(fr.getRiftSize() - 1);
                    }
                    b = (d >= 1.0);
                }
                if (b && this.counter % 40 == 0) {
                    this.world.addBlockEvent(this.pos, this.getBlockType(), 5, this.counter);
                }
                b = false;
                while (this.progress >= 2000 && (this.getStackInSlot(0).isEmpty() || (this.getStackInSlot(0).getItem() == ItemsTC.voidSeed && this.getStackInSlot(0).getCount() < this.getStackInSlot(0).getMaxStackSize()))) {
                    this.progress -= 2000;
                    if (this.getStackInSlot(0).isEmpty()) {
                        this.setInventorySlotContents(0, new ItemStack(ItemsTC.voidSeed));
                    }
                    else {
                        this.getStackInSlot(0).setCount(this.getStackInSlot(0).getCount() + 1);
                    }
                    b = true;
                }
                if (b) {
                    this.syncTile(false);
                    this.markDirty();
                }
            }
        }
    }
    
    private List<EntityFluxRift> getValidRifts() {
        final ArrayList<EntityFluxRift> ret = new ArrayList<EntityFluxRift>();
        final List<EntityFluxRift> frl = EntityUtils.getEntitiesInRange(this.getWorld(), this.getPos(), null, EntityFluxRift.class, 8.0);
        for (final EntityFluxRift fr : frl) {
            if (!fr.isDead) {
                if (fr.getRiftSize() < 2) {
                    continue;
                }
                final double xx = this.getPos().getX() + 0.5;
                final double yy = this.getPos().getY() + 1;
                final double zz = this.getPos().getZ() + 0.5;
                Vec3d v1 = new Vec3d(xx, yy, zz);
                final Vec3d v2 = new Vec3d(fr.posX, fr.posY, fr.posZ);
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
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.progress = nbt.getShort("progress");
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("progress", (short)this.progress);
        return nbt;
    }
    
    @Override
    public boolean isItemValidForSlot(final int par1, final ItemStack stack) {
        return stack.getItem() == ItemsTC.voidSeed;
    }
    
    @Override
    public int[] getSlotsForFace(final EnumFacing side) {
        return TileVoidSiphon.slots;
    }
    
    @Override
    public boolean canInsertItem(final int par1, final ItemStack stack, final EnumFacing par3) {
        return false;
    }
    
    @Override
    public boolean canExtractItem(final int par1, final ItemStack stack2, final EnumFacing par3) {
        return true;
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 5) {
            if (this.world.isRemote) {
                final List<EntityFluxRift> frl = this.getValidRifts();
                for (final EntityFluxRift fr : frl) {
                    FXDispatcher.INSTANCE.voidStreak(fr.posX, fr.posY, fr.posZ, this.getPos().getX() + 0.5, this.getPos().getY() + 0.5625f, this.getPos().getZ() + 0.5, j, 0.04f);
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
