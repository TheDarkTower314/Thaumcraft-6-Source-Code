package thaumcraft.common.tiles.devices;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.Utils;
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
        linked = false;
        count = 0;
        inc = 40;
        outputStacks = new ArrayList<ItemStack>();
    }
    
    public void restoreLink() {
        if (isDestinationValid()) {
            World targetWorld = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(linkDim);
            if (targetWorld == null) {
                return;
            }
            TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
            if (te != null && te instanceof TileMirror) {
                TileMirror tm = (TileMirror)te;
                tm.linked = true;
                tm.linkX = getPos().getX();
                tm.linkY = getPos().getY();
                tm.linkZ = getPos().getZ();
                tm.linkDim = world.provider.getDimension();
                tm.syncTile(false);
                linked = true;
                markDirty();
                tm.markDirty();
                syncTile(false);
            }
        }
    }
    
    public void invalidateLink() {
        World targetWorld = DimensionManager.getWorld(linkDim);
        if (targetWorld == null) {
            return;
        }
        if (!Utils.isChunkLoaded(targetWorld, linkX, linkZ)) {
            return;
        }
        TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (te != null && te instanceof TileMirror) {
            TileMirror tm = (TileMirror)te;
            tm.linked = false;
            markDirty();
            tm.markDirty();
            tm.syncTile(false);
        }
    }
    
    public boolean isLinkValid() {
        if (!linked) {
            return false;
        }
        World targetWorld = DimensionManager.getWorld(linkDim);
        if (targetWorld == null) {
            return false;
        }
        TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (te == null || !(te instanceof TileMirror)) {
            linked = false;
            markDirty();
            syncTile(false);
            return false;
        }
        TileMirror tm = (TileMirror)te;
        if (!tm.linked) {
            linked = false;
            markDirty();
            syncTile(false);
            return false;
        }
        if (tm.linkX != getPos().getX() || tm.linkY != getPos().getY() || tm.linkZ != getPos().getZ() || tm.linkDim != world.provider.getDimension()) {
            linked = false;
            markDirty();
            syncTile(false);
            return false;
        }
        return true;
    }
    
    public boolean isLinkValidSimple() {
        if (!linked) {
            return false;
        }
        World targetWorld = DimensionManager.getWorld(linkDim);
        if (targetWorld == null) {
            return false;
        }
        TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (te == null || !(te instanceof TileMirror)) {
            return false;
        }
        TileMirror tm = (TileMirror)te;
        return tm.linked && tm.linkX == getPos().getX() && tm.linkY == getPos().getY() && tm.linkZ == getPos().getZ() && tm.linkDim == world.provider.getDimension();
    }
    
    public boolean isDestinationValid() {
        World targetWorld = DimensionManager.getWorld(linkDim);
        if (targetWorld == null) {
            return false;
        }
        TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (te == null || !(te instanceof TileMirror)) {
            linked = false;
            markDirty();
            syncTile(false);
            return false;
        }
        TileMirror tm = (TileMirror)te;
        return !tm.isLinkValid();
    }
    
    public boolean transport(EntityItem ie) {
        ItemStack items = ie.getItem();
        if (!linked || !isLinkValid()) {
            return false;
        }
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(linkDim);
        TileEntity target = world.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (target != null && target instanceof TileMirror) {
            ((TileMirror)target).addStack(items);
            addInstability(null, items.getCount());
            ie.setDead();
            markDirty();
            target.markDirty();
            world.addBlockEvent(getPos(), blockType, 1, 0);
            return true;
        }
        return false;
    }
    
    public boolean transportDirect(ItemStack items) {
        if (items == null || items.isEmpty() || items.getCount() <= 0) {
            return false;
        }
        addStack(items.copy());
        markDirty();
        return true;
    }
    
    public void eject() {
        if (outputStacks.size() > 0 && count > 20) {
            int i = world.rand.nextInt(outputStacks.size());
            if (outputStacks.get(i) != null && !outputStacks.get(i).isEmpty()) {
                ItemStack outItem = outputStacks.get(i).copy();
                outItem.setCount(1);
                if (spawnItem(outItem)) {
                    outputStacks.get(i).shrink(1);
                    addInstability(null, 1);
                    world.addBlockEvent(getPos(), blockType, 1, 0);
                    if (outputStacks.get(i).getCount() <= 0) {
                        outputStacks.remove(i);
                    }
                }
            }
            else {
                outputStacks.remove(i);
            }
            markDirty();
        }
    }
    
    public boolean spawnItem(ItemStack stack) {
        try {
            EnumFacing face = BlockStateUtils.getFacing(getBlockMetadata());
            EntityItem ie2 = new EntityItem(world, getPos().getX() + 0.5, getPos().getY() + 0.25, getPos().getZ() + 0.5, stack);
            ie2.motionX = face.getFrontOffsetX() * 0.15f;
            ie2.motionY = face.getFrontOffsetY() * 0.15f;
            ie2.motionZ = face.getFrontOffsetZ() * 0.15f;
            ie2.timeUntilPortal = 20;
            world.spawnEntity(ie2);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    protected void addInstability(World targetWorld, int amt) {
        instability += amt;
        markDirty();
        if (targetWorld != null) {
            TileEntity te = targetWorld.getTileEntity(new BlockPos(linkX, linkY, linkZ));
            if (te != null && te instanceof TileMirror) {
                TileMirror tileMirror = (TileMirror)te;
                tileMirror.instability += amt;
                if (((TileMirror)te).instability < 0) {
                    ((TileMirror)te).instability = 0;
                }
                te.markDirty();
            }
        }
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        linked = nbttagcompound.getBoolean("linked");
        linkX = nbttagcompound.getInteger("linkX");
        linkY = nbttagcompound.getInteger("linkY");
        linkZ = nbttagcompound.getInteger("linkZ");
        linkDim = nbttagcompound.getInteger("linkDim");
        instability = nbttagcompound.getInteger("instability");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        super.writeSyncNBT(nbttagcompound);
        nbttagcompound.setBoolean("linked", linked);
        nbttagcompound.setInteger("linkX", linkX);
        nbttagcompound.setInteger("linkY", linkY);
        nbttagcompound.setInteger("linkZ", linkZ);
        nbttagcompound.setInteger("linkDim", linkDim);
        nbttagcompound.setInteger("instability", instability);
        return nbttagcompound;
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 1) {
            if (world.isRemote) {
                EnumFacing face = BlockStateUtils.getFacing(getBlockMetadata());
                double xx = getPos().getX() + 0.33 + world.rand.nextFloat() * 0.33f - face.getFrontOffsetX() / 2.0;
                double yy = getPos().getY() + 0.33 + world.rand.nextFloat() * 0.33f - face.getFrontOffsetY() / 2.0;
                double zz = getPos().getZ() + 0.33 + world.rand.nextFloat() * 0.33f - face.getFrontOffsetZ() / 2.0;
                FXDispatcher.INSTANCE.drawWispyMotes(xx, yy, zz, face.getFrontOffsetX() / 50.0 + world.rand.nextGaussian() * 0.01, face.getFrontOffsetY() / 50.0 + world.rand.nextGaussian() * 0.01, face.getFrontOffsetZ() / 50.0 + world.rand.nextGaussian() * 0.01, MathHelper.getInt(world.rand, 10, 30), world.rand.nextFloat() / 3.0f, 0.0f, world.rand.nextFloat() / 2.0f, (float)(world.rand.nextGaussian() * 0.01));
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    public void update() {
        if (!world.isRemote) {
            eject();
            checkInstability();
            if (count++ % inc == 0) {
                if (!isLinkValidSimple()) {
                    if (inc < 600) {
                        inc += 20;
                    }
                    restoreLink();
                }
                else {
                    inc = 40;
                }
            }
        }
    }
    
    public void checkInstability() {
        if (instability > 128) {
            AuraHelper.polluteAura(world, pos, 1.0f, true);
            instability -= 128;
            markDirty();
        }
        if (instability > 0 && count % 100 == 0) {
            --instability;
        }
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        NBTTagList nbttaglist = nbtCompound.getTagList("Items", 10);
        outputStacks = new ArrayList<ItemStack>();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");
            outputStacks.add(new ItemStack(nbttagcompound1));
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < outputStacks.size(); ++i) {
            if (outputStacks.get(i) != null && outputStacks.get(i).getCount() > 0) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                outputStacks.get(i).writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        nbtCompound.setTag("Items", nbttaglist);
        return nbtCompound;
    }
    
    public int getSizeInventory() {
        return 1;
    }
    
    public ItemStack getStackInSlot(int par1) {
        return ItemStack.EMPTY;
    }
    
    public ItemStack decrStackSize(int par1, int par2) {
        return ItemStack.EMPTY;
    }
    
    public ItemStack removeStackFromSlot(int par1) {
        return ItemStack.EMPTY;
    }
    
    public void addStack(ItemStack stack) {
        outputStacks.add(stack);
        markDirty();
    }
    
    public void setInventorySlotContents(int par1, ItemStack stack2) {
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(linkDim);
        TileEntity target = world.getTileEntity(new BlockPos(linkX, linkY, linkZ));
        if (target != null && target instanceof TileMirror) {
            ((TileMirror)target).addStack(stack2.copy());
            addInstability(null, stack2.getCount());
            world.addBlockEvent(getPos(), blockType, 1, 0);
        }
        else {
            spawnItem(stack2.copy());
        }
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUsableByPlayer(EntityPlayer var1) {
        return false;
    }
    
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(linkDim);
        TileEntity target = world.getTileEntity(new BlockPos(linkX, linkY, linkZ));
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
    
    public void openInventory(EntityPlayer player) {
    }
    
    public void closeInventory(EntityPlayer player) {
    }
    
    public int getField(int id) {
        return 0;
    }
    
    public void setField(int id, int value) {
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
