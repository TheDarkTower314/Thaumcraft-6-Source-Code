package thaumcraft.common.tiles.crafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.container.ContainerGolemBuilder;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.golems.ItemGolemPlacer;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TileGolemBuilder extends TileThaumcraftInventory implements IEssentiaTransport
{
    public long golem;
    public int cost;
    public int maxCost;
    public boolean[] hasStuff;
    boolean bufferedEssentia;
    int ticks;
    public int press;
    IGolemProperties props;
    ItemStack[] components;
    
    public TileGolemBuilder() {
        super(1);
        golem = -1L;
        cost = 0;
        maxCost = 0;
        hasStuff = null;
        bufferedEssentia = false;
        ticks = 0;
        press = 0;
        props = null;
        components = null;
    }
    
    @Override
    public void messageFromClient(NBTTagCompound nbt, EntityPlayerMP player) {
        super.messageFromClient(nbt, player);
        if (nbt.hasKey("check")) {
            hasStuff = checkCraft(nbt.getLong("golem"));
            byte[] ba = new byte[hasStuff.length];
            for (int a = 0; a < ba.length; ++a) {
                ba[a] = (byte)(hasStuff[a] ? 1 : 0);
            }
            NBTTagCompound nbt2 = new NBTTagCompound();
            nbt2.setByteArray("stuff", ba);
            sendMessageToClient(nbt2, player);
        }
        else if (nbt.hasKey("golem")) {
            startCraft(nbt.getLong("golem"), player);
        }
    }
    
    @Override
    public void messageFromServer(NBTTagCompound nbt) {
        super.messageFromServer(nbt);
        if (nbt.hasKey("stuff")) {
            hasStuff = null;
            byte[] ba = nbt.getByteArray("stuff");
            if (ba != null && ba.length > 0) {
                hasStuff = new boolean[ba.length];
                for (int a = 0; a < ba.length; ++a) {
                    hasStuff[a] = (ba[a] == 1);
                }
            }
            ContainerGolemBuilder.redo = true;
        }
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        golem = nbttagcompound.getLong("golem");
        cost = nbttagcompound.getInteger("cost");
        maxCost = nbttagcompound.getInteger("mcost");
        if (golem >= 0L) {
            try {
                props = GolemProperties.fromLong(golem);
                components = props.generateComponents();
            }
            catch (Exception e) {
                props = null;
                components = null;
                cost = 0;
                golem = -1L;
            }
        }
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setLong("golem", golem);
        nbttagcompound.setInteger("cost", cost);
        nbttagcompound.setInteger("mcost", maxCost);
        return super.writeSyncNBT(nbttagcompound);
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2);
    }
    
    @Override
    public void update() {
        super.update();
        boolean complete = false;
        if (!world.isRemote) {
            ++ticks;
            if (ticks % 5 == 0 && !complete && cost > 0 && golem >= 0L) {
                if (bufferedEssentia || drawEssentia()) {
                    bufferedEssentia = false;
                    --cost;
                    markDirty();
                }
                if (cost <= 0) {
                    ItemStack placer = new ItemStack(ItemsTC.golemPlacer);
                    placer.setTagInfo("props", new NBTTagLong(golem));
                    if (getStackInSlot(0).isEmpty() || (getStackInSlot(0).getCount() < getStackInSlot(0).getMaxStackSize() && getStackInSlot(0).isItemEqual(placer) && ItemStack.areItemStackTagsEqual(getStackInSlot(0), placer))) {
                        if (getStackInSlot(0) == null || getStackInSlot(0).isEmpty()) {
                            setInventorySlotContents(0, placer.copy());
                        }
                        else {
                            getStackInSlot(0).grow(1);
                        }
                        complete = true;
                        world.playSound(null, pos, SoundsTC.wand, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    }
                }
            }
        }
        else {
            if (press < 90 && cost > 0 && golem > 0L) {
                press += 6;
                if (press >= 60) {
                    world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.66f, 1.0f + world.rand.nextFloat() * 0.1f, false);
                    for (int a = 0; a < 16; ++a) {
                        FXDispatcher.INSTANCE.drawVentParticles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, world.rand.nextGaussian() * 0.1, 0.0, world.rand.nextGaussian() * 0.1, 11184810);
                    }
                }
            }
            if (press >= 90 && world.rand.nextInt(8) == 0) {
                FXDispatcher.INSTANCE.drawVentParticles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, world.rand.nextGaussian() * 0.1, 0.0, world.rand.nextGaussian() * 0.1, 11184810);
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.1f, 1.0f + world.rand.nextFloat() * 0.1f, false);
            }
            if (press > 0 && (cost <= 0 || golem == -1L)) {
                if (press >= 90) {
                    for (int a = 0; a < 10; ++a) {
                        FXDispatcher.INSTANCE.drawVentParticles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, world.rand.nextGaussian() * 0.1, 0.0, world.rand.nextGaussian() * 0.1, 11184810);
                    }
                }
                press -= 3;
            }
        }
        if (complete) {
            cost = 0;
            golem = -1L;
            syncTile(false);
            markDirty();
        }
    }
    
    public boolean[] checkCraft(long id) {
        IGolemProperties props = GolemProperties.fromLong(id);
        ItemStack[] cc = props.generateComponents();
        boolean[] ret = new boolean[cc.length];
        int a = 0;
        for (ItemStack stack : props.generateComponents()) {
            ret[a] = InventoryUtils.checkAdjacentChests(world, pos, stack);
            ++a;
        }
        return ret;
    }
    
    public boolean startCraft(long id, EntityPlayer p) {
        ItemStack placer = new ItemStack(ItemsTC.golemPlacer);
        placer.setTagInfo("props", new NBTTagLong(id));
        if (getStackInSlot(0) != null && !getStackInSlot(0).isEmpty() && (getStackInSlot(0).getCount() >= getStackInSlot(0).getMaxStackSize() || !getStackInSlot(0).isItemEqual(placer) || !ItemStack.areItemStackTagsEqual(getStackInSlot(0), placer))) {
            cost = 0;
            props = null;
            components = null;
            golem = -1L;
            return false;
        }
        golem = id;
        props = GolemProperties.fromLong(golem);
        components = props.generateComponents();
        if (!InventoryUtils.consumeItemsFromAdjacentInventoryOrPlayer(getWorld(), getPos(), p, true, components)) {
            cost = 0;
            props = null;
            components = null;
            golem = -1L;
            return false;
        }
        cost = props.getTraits().size() * 2;
        for (ItemStack stack : components) {
            cost += stack.getCount();
        }
        InventoryUtils.consumeItemsFromAdjacentInventoryOrPlayer(getWorld(), getPos(), p, false, components);
        maxCost = cost;
        markDirty();
        syncTile(false);
        world.playSound(null, pos, SoundsTC.wand, SoundCategory.BLOCKS, 0.25f, 1.0f);
        return true;
    }
    
    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack2) {
        return stack2 != null && !stack2.isEmpty() && stack2.getItem() instanceof ItemGolemPlacer;
    }
    
    boolean drawEssentia() {
        for (EnumFacing face : EnumFacing.VALUES) {
            TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, getPos(), face);
            if (te != null) {
                IEssentiaTransport ic = (IEssentiaTransport)te;
                if (!ic.canOutputTo(face.getOpposite())) {
                    return false;
                }
                if (ic.getSuctionAmount(face.getOpposite()) < getSuctionAmount(face) && ic.takeEssentia(Aspect.MECHANISM, 1, face.getOpposite()) == 1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean isConnectable(EnumFacing face) {
        return face.getHorizontalIndex() >= 0 || face == EnumFacing.DOWN;
    }
    
    @Override
    public boolean canInputFrom(EnumFacing face) {
        return isConnectable(face);
    }
    
    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return Aspect.MECHANISM;
    }
    
    @Override
    public int getSuctionAmount(EnumFacing face) {
        return (cost > 0 && golem >= 0L) ? 128 : 0;
    }
    
    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing facing) {
        return 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing facing) {
        if (!bufferedEssentia && cost > 0 && golem >= 0L && aspect == Aspect.MECHANISM) {
            bufferedEssentia = true;
            return 1;
        }
        return 0;
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
}
