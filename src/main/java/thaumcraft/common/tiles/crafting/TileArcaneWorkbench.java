package thaumcraft.common.tiles.crafting;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.common.container.InventoryArcaneWorkbench;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;


public class TileArcaneWorkbench extends TileThaumcraft
{
    public InventoryArcaneWorkbench inventoryCraft;
    public int auraVisServer;
    public int auraVisClient;
    
    public TileArcaneWorkbench() {
        auraVisServer = 0;
        auraVisClient = 0;
        inventoryCraft = new InventoryArcaneWorkbench(this, new ContainerDummy());
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        NonNullList<ItemStack> stacks = NonNullList.withSize(inventoryCraft.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbtCompound, stacks);
        for (int a = 0; a < stacks.size(); ++a) {
            inventoryCraft.setInventorySlotContents(a, stacks.get(a));
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        NonNullList<ItemStack> stacks = NonNullList.withSize(inventoryCraft.getSizeInventory(), ItemStack.EMPTY);
        for (int a = 0; a < stacks.size(); ++a) {
            stacks.set(a, inventoryCraft.getStackInSlot(a));
        }
        ItemStackHelper.saveAllItems(nbtCompound, stacks);
        return nbtCompound;
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbtCompound) {
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbtCompound) {
        return nbtCompound;
    }
    
    public void getAura() {
        if (!getWorld().isRemote) {
            int t = 0;
            if (world.getBlockState(getPos().up()).getBlock() == BlocksTC.arcaneWorkbenchCharger) {
                int sx = pos.getX() >> 4;
                int sz = pos.getZ() >> 4;
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int zz = -1; zz <= 1; ++zz) {
                        AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), sx + xx, sz + zz);
                        if (ac != null) {
                            t += (int)ac.getVis();
                        }
                    }
                }
            }
            else {
                t = (int)AuraHandler.getVis(getWorld(), getPos());
            }
            auraVisServer = t;
        }
    }
    
    public void spendAura(int vis) {
        if (!getWorld().isRemote) {
            if (world.getBlockState(getPos().up()).getBlock() == BlocksTC.arcaneWorkbenchCharger) {
                int q = vis;
                int z = Math.max(1, vis / 9);
                int attempts = 0;
            Label_0144:
                while (q > 0) {
                    ++attempts;
                    for (int xx = -1; xx <= 1; ++xx) {
                        for (int zz = -1; zz <= 1; ++zz) {
                            if (z > q) {
                                z = q;
                            }
                            q -= (int)AuraHandler.drainVis(getWorld(), getPos().add(xx * 16, 0, zz * 16), (float)z, false);
                            if (q <= 0) {
                                break Label_0144;
                            }
                            if (attempts > 1000) {
                                break Label_0144;
                            }
                        }
                    }
                }
            }
            else {
                AuraHandler.drainVis(getWorld(), getPos(), (float)vis, false);
            }
        }
    }
}
