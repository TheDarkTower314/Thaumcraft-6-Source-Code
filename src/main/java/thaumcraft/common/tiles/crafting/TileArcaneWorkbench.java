// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.common.container.InventoryArcaneWorkbench;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileArcaneWorkbench extends TileThaumcraft
{
    public InventoryArcaneWorkbench inventoryCraft;
    public int auraVisServer;
    public int auraVisClient;
    
    public TileArcaneWorkbench() {
        this.auraVisServer = 0;
        this.auraVisClient = 0;
        this.inventoryCraft = new InventoryArcaneWorkbench(this, new ContainerDummy());
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        final NonNullList<ItemStack> stacks = NonNullList.withSize(this.inventoryCraft.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbtCompound, stacks);
        for (int a = 0; a < stacks.size(); ++a) {
            this.inventoryCraft.setInventorySlotContents(a, stacks.get(a));
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        final NonNullList<ItemStack> stacks = NonNullList.withSize(this.inventoryCraft.getSizeInventory(), ItemStack.EMPTY);
        for (int a = 0; a < stacks.size(); ++a) {
            stacks.set(a, this.inventoryCraft.getStackInSlot(a));
        }
        ItemStackHelper.saveAllItems(nbtCompound, stacks);
        return nbtCompound;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbtCompound) {
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbtCompound) {
        return nbtCompound;
    }
    
    public void getAura() {
        if (!this.getWorld().isRemote) {
            int t = 0;
            if (this.world.getBlockState(this.getPos().up()).getBlock() == BlocksTC.arcaneWorkbenchCharger) {
                final int sx = this.pos.getX() >> 4;
                final int sz = this.pos.getZ() >> 4;
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int zz = -1; zz <= 1; ++zz) {
                        final AuraChunk ac = AuraHandler.getAuraChunk(this.world.provider.getDimension(), sx + xx, sz + zz);
                        if (ac != null) {
                            t += (int)ac.getVis();
                        }
                    }
                }
            }
            else {
                t = (int)AuraHandler.getVis(this.getWorld(), this.getPos());
            }
            this.auraVisServer = t;
        }
    }
    
    public void spendAura(final int vis) {
        if (!this.getWorld().isRemote) {
            if (this.world.getBlockState(this.getPos().up()).getBlock() == BlocksTC.arcaneWorkbenchCharger) {
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
                            q -= (int)AuraHandler.drainVis(this.getWorld(), this.getPos().add(xx * 16, 0, zz * 16), (float)z, false);
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
                AuraHandler.drainVis(this.getWorld(), this.getPos(), (float)vis, false);
            }
        }
    }
}
