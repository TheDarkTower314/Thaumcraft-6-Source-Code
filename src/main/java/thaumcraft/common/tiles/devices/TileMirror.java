// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import thaumcraft.api.aura.AuraHelper;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.entity.item.EntityItem;
import thaumcraft.common.lib.utils.Utils;
import net.minecraftforge.common.DimensionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import net.minecraft.util.ITickable;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileMirror extends TileThaumcraft implements IInventory, ITickable
{
    public boolean linked;
    public int linkX;
    public int linkY;
    public int linkZ;
    public int linkDim;
    public int instability;
    int count;
    int inc;
    private ArrayList<ItemStack> outputStacks;
    
    public TileMirror() {
        this.linked = false;
        this.count = 0;
        this.inc = 40;
        this.outputStacks = new ArrayList<ItemStack>();
    }
    
    public void restoreLink() {
        if (this.isDestinationValid()) {
            final World targetWorld = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.linkDim);
            if (targetWorld == null) {
                return;
            }
            final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
            if (te != null && te instanceof TileMirror) {
                final TileMirror tm = (TileMirror)te;
                tm.linked = true;
                tm.linkX = this.getPos().getX();
                tm.linkY = this.getPos().getY();
                tm.linkZ = this.getPos().getZ();
                tm.linkDim = this.world.provider.getDimension();
                tm.syncTile(false);
                this.linked = true;
                this.markDirty();
                tm.markDirty();
                this.syncTile(false);
            }
        }
    }
    
    public void invalidateLink() {
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return;
        }
        if (!Utils.isChunkLoaded(targetWorld, this.linkX, this.linkZ)) {
            return;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (te != null && te instanceof TileMirror) {
            final TileMirror tm = (TileMirror)te;
            tm.linked = false;
            this.markDirty();
            tm.markDirty();
            tm.syncTile(false);
        }
    }
    
    public boolean isLinkValid() {
        if (!this.linked) {
            return false;
        }
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return false;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (te == null || !(te instanceof TileMirror)) {
            this.linked = false;
            this.markDirty();
            this.syncTile(false);
            return false;
        }
        final TileMirror tm = (TileMirror)te;
        if (!tm.linked) {
            this.linked = false;
            this.markDirty();
            this.syncTile(false);
            return false;
        }
        if (tm.linkX != this.getPos().getX() || tm.linkY != this.getPos().getY() || tm.linkZ != this.getPos().getZ() || tm.linkDim != this.world.provider.getDimension()) {
            this.linked = false;
            this.markDirty();
            this.syncTile(false);
            return false;
        }
        return true;
    }
    
    public boolean isLinkValidSimple() {
        if (!this.linked) {
            return false;
        }
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return false;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (te == null || !(te instanceof TileMirror)) {
            return false;
        }
        final TileMirror tm = (TileMirror)te;
        return tm.linked && tm.linkX == this.getPos().getX() && tm.linkY == this.getPos().getY() && tm.linkZ == this.getPos().getZ() && tm.linkDim == this.world.provider.getDimension();
    }
    
    public boolean isDestinationValid() {
        final World targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return false;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (te == null || !(te instanceof TileMirror)) {
            this.linked = false;
            this.markDirty();
            this.syncTile(false);
            return false;
        }
        final TileMirror tm = (TileMirror)te;
        return !tm.isLinkValid();
    }
    
    public boolean transport(final EntityItem ie) {
        final ItemStack items = ie.getItem();
        if (!this.linked || !this.isLinkValid()) {
            return false;
        }
        final World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.linkDim);
        final TileEntity target = world.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (target != null && target instanceof TileMirror) {
            ((TileMirror)target).addStack(items);
            this.addInstability(null, items.getCount());
            ie.setDead();
            this.markDirty();
            target.markDirty();
            world.addBlockEvent(this.getPos(), this.blockType, 1, 0);
            return true;
        }
        return false;
    }
    
    public boolean transportDirect(final ItemStack items) {
        if (items == null || items.isEmpty() || items.getCount() <= 0) {
            return false;
        }
        this.addStack(items.copy());
        this.markDirty();
        return true;
    }
    
    public void eject() {
        if (this.outputStacks.size() > 0 && this.count > 20) {
            final int i = this.world.rand.nextInt(this.outputStacks.size());
            if (this.outputStacks.get(i) != null && !this.outputStacks.get(i).isEmpty()) {
                final ItemStack outItem = this.outputStacks.get(i).copy();
                outItem.setCount(1);
                if (this.spawnItem(outItem)) {
                    this.outputStacks.get(i).shrink(1);
                    this.addInstability(null, 1);
                    this.world.addBlockEvent(this.getPos(), this.blockType, 1, 0);
                    if (this.outputStacks.get(i).getCount() <= 0) {
                        this.outputStacks.remove(i);
                    }
                }
            }
            else {
                this.outputStacks.remove(i);
            }
            this.markDirty();
        }
    }
    
    public boolean spawnItem(final ItemStack stack) {
        try {
            final EnumFacing face = BlockStateUtils.getFacing(this.getBlockMetadata());
            final EntityItem ie2 = new EntityItem(this.world, this.getPos().getX() + 0.5, this.getPos().getY() + 0.25, this.getPos().getZ() + 0.5, stack);
            ie2.motionX = face.getFrontOffsetX() * 0.15f;
            ie2.motionY = face.getFrontOffsetY() * 0.15f;
            ie2.motionZ = face.getFrontOffsetZ() * 0.15f;
            ie2.timeUntilPortal = 20;
            this.world.spawnEntity(ie2);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    protected void addInstability(final World targetWorld, final int amt) {
        this.instability += amt;
        this.markDirty();
        if (targetWorld != null) {
            final TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
            if (te != null && te instanceof TileMirror) {
                final TileMirror tileMirror = (TileMirror)te;
                tileMirror.instability += amt;
                if (((TileMirror)te).instability < 0) {
                    ((TileMirror)te).instability = 0;
                }
                te.markDirty();
            }
        }
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        this.linked = nbttagcompound.getBoolean("linked");
        this.linkX = nbttagcompound.getInteger("linkX");
        this.linkY = nbttagcompound.getInteger("linkY");
        this.linkZ = nbttagcompound.getInteger("linkZ");
        this.linkDim = nbttagcompound.getInteger("linkDim");
        this.instability = nbttagcompound.getInteger("instability");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        super.writeSyncNBT(nbttagcompound);
        nbttagcompound.setBoolean("linked", this.linked);
        nbttagcompound.setInteger("linkX", this.linkX);
        nbttagcompound.setInteger("linkY", this.linkY);
        nbttagcompound.setInteger("linkZ", this.linkZ);
        nbttagcompound.setInteger("linkDim", this.linkDim);
        nbttagcompound.setInteger("instability", this.instability);
        return nbttagcompound;
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 1) {
            if (this.world.isRemote) {
                final EnumFacing face = BlockStateUtils.getFacing(this.getBlockMetadata());
                final double xx = this.getPos().getX() + 0.33 + this.world.rand.nextFloat() * 0.33f - face.getFrontOffsetX() / 2.0;
                final double yy = this.getPos().getY() + 0.33 + this.world.rand.nextFloat() * 0.33f - face.getFrontOffsetY() / 2.0;
                final double zz = this.getPos().getZ() + 0.33 + this.world.rand.nextFloat() * 0.33f - face.getFrontOffsetZ() / 2.0;
                FXDispatcher.INSTANCE.drawWispyMotes(xx, yy, zz, face.getFrontOffsetX() / 50.0 + this.world.rand.nextGaussian() * 0.01, face.getFrontOffsetY() / 50.0 + this.world.rand.nextGaussian() * 0.01, face.getFrontOffsetZ() / 50.0 + this.world.rand.nextGaussian() * 0.01, MathHelper.getInt(this.world.rand, 10, 30), this.world.rand.nextFloat() / 3.0f, 0.0f, this.world.rand.nextFloat() / 2.0f, (float)(this.world.rand.nextGaussian() * 0.01));
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    public void update() {
        if (!this.world.isRemote) {
            this.eject();
            this.checkInstability();
            if (this.count++ % this.inc == 0) {
                if (!this.isLinkValidSimple()) {
                    if (this.inc < 600) {
                        this.inc += 20;
                    }
                    this.restoreLink();
                }
                else {
                    this.inc = 40;
                }
            }
        }
    }
    
    public void checkInstability() {
        if (this.instability > 128) {
            AuraHelper.polluteAura(this.world, this.pos, 1.0f, true);
            this.instability -= 128;
            this.markDirty();
        }
        if (this.instability > 0 && this.count % 100 == 0) {
            --this.instability;
        }
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        final NBTTagList nbttaglist = nbtCompound.getTagList("Items", 10);
        this.outputStacks = new ArrayList<ItemStack>();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            final byte b0 = nbttagcompound1.getByte("Slot");
            this.outputStacks.add(new ItemStack(nbttagcompound1));
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        final NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.outputStacks.size(); ++i) {
            if (this.outputStacks.get(i) != null && this.outputStacks.get(i).getCount() > 0) {
                final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.outputStacks.get(i).writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        nbtCompound.setTag("Items", nbttaglist);
        return nbtCompound;
    }
    
    public int getSizeInventory() {
        return 1;
    }
    
    public ItemStack getStackInSlot(final int par1) {
        return ItemStack.EMPTY;
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        return ItemStack.EMPTY;
    }
    
    public ItemStack removeStackFromSlot(final int par1) {
        return ItemStack.EMPTY;
    }
    
    public void addStack(final ItemStack stack) {
        this.outputStacks.add(stack);
        this.markDirty();
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack stack2) {
        final World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.linkDim);
        final TileEntity target = world.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (target != null && target instanceof TileMirror) {
            ((TileMirror)target).addStack(stack2.copy());
            this.addInstability(null, stack2.getCount());
            world.addBlockEvent(this.getPos(), this.blockType, 1, 0);
        }
        else {
            this.spawnItem(stack2.copy());
        }
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUsableByPlayer(final EntityPlayer var1) {
        return false;
    }
    
    public boolean isItemValidForSlot(final int var1, final ItemStack var2) {
        final World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.linkDim);
        final TileEntity target = world.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        return target != null && target instanceof TileMirror;
    }
    
    public String getName() {
        return null;
    }
    
    public boolean hasCustomName() {
        return false;
    }
    
    public ITextComponent getDisplayName() {
        return null;
    }
    
    public void openInventory(final EntityPlayer player) {
    }
    
    public void closeInventory(final EntityPlayer player) {
    }
    
    public int getField(final int id) {
        return 0;
    }
    
    public void setField(final int id, final int value) {
    }
    
    public int getFieldCount() {
        return 0;
    }
    
    public void clear() {
    }
    
    public boolean isEmpty() {
        return false;
    }
}
