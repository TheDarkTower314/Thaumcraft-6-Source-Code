// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aura.AuraHelper;
import net.minecraft.entity.item.EntityItem;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.CrucibleRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.InventoryFake;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import net.minecraft.entity.Entity;
import thaumcraft.common.entities.EntitySpecialItem;
import net.minecraft.item.ItemStack;
import java.awt.Color;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTank;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraft.util.ITickable;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileCrucible extends TileThaumcraft implements ITickable, IFluidHandler, IAspectContainer
{
    public short heat;
    public AspectList aspects;
    public final int maxTags = 500;
    int bellows;
    private int delay;
    private long counter;
    int prevcolor;
    int prevx;
    int prevy;
    public FluidTank tank;
    
    public TileCrucible() {
        aspects = new AspectList();
        bellows = -1;
        delay = 0;
        counter = -100L;
        prevcolor = 0;
        prevx = 0;
        prevy = 0;
        tank = new FluidTank(FluidRegistry.WATER, 0, 1000);
        heat = 0;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        heat = nbttagcompound.getShort("Heat");
        tank.readFromNBT(nbttagcompound);
        if (nbttagcompound.hasKey("Empty")) {
            tank.setFluid(null);
        }
        aspects.readFromNBT(nbttagcompound);
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Heat", heat);
        tank.writeToNBT(nbttagcompound);
        aspects.writeToNBT(nbttagcompound);
        return nbttagcompound;
    }
    
    public void update() {
        ++counter;
        final int prevheat = heat;
        if (!world.isRemote) {
            if (tank.getFluidAmount() > 0) {
                final IBlockState block = world.getBlockState(getPos().down());
                if (block.getMaterial() == Material.LAVA || block.getMaterial() == Material.FIRE || BlocksTC.nitor.containsValue(block.getBlock()) || block.getBlock() == Blocks.MAGMA) {
                    if (heat < 200) {
                        ++heat;
                        if (prevheat < 151 && heat >= 151) {
                            markDirty();
                            syncTile(false);
                        }
                    }
                }
                else if (heat > 0) {
                    --heat;
                    if (heat == 149) {
                        markDirty();
                        syncTile(false);
                    }
                }
            }
            else if (heat > 0) {
                --heat;
            }
            if (aspects.visSize() > 500) {
                spillRandom();
            }
            if (counter >= 100L) {
                spillRandom();
                counter = 0L;
            }
        }
        else if (tank.getFluidAmount() > 0) {
            drawEffects();
        }
        if (world.isRemote && prevheat < 151 && heat >= 151) {
            ++heat;
        }
    }
    
    private void drawEffects() {
        if (heat > 150) {
            FXDispatcher.INSTANCE.crucibleFroth(pos.getX() + 0.2f + world.rand.nextFloat() * 0.6f, pos.getY() + getFluidHeight(), pos.getZ() + 0.2f + world.rand.nextFloat() * 0.6f);
            if (aspects.visSize() > 500) {
                for (int a = 0; a < 2; ++a) {
                    FXDispatcher.INSTANCE.crucibleFrothDown((float) pos.getX(), (float)(pos.getY() + 1), pos.getZ() + world.rand.nextFloat());
                    FXDispatcher.INSTANCE.crucibleFrothDown((float)(pos.getX() + 1), (float)(pos.getY() + 1), pos.getZ() + world.rand.nextFloat());
                    FXDispatcher.INSTANCE.crucibleFrothDown(pos.getX() + world.rand.nextFloat(), (float)(pos.getY() + 1), (float) pos.getZ());
                    FXDispatcher.INSTANCE.crucibleFrothDown(pos.getX() + world.rand.nextFloat(), (float)(pos.getY() + 1), (float)(pos.getZ() + 1));
                }
            }
        }
        if (world.rand.nextInt(6) == 0 && aspects.size() > 0) {
            final int color = aspects.getAspects()[world.rand.nextInt(aspects.size())].getColor() - 16777216;
            final int x = 5 + world.rand.nextInt(22);
            final int y = 5 + world.rand.nextInt(22);
            delay = world.rand.nextInt(10);
            prevcolor = color;
            prevx = x;
            prevy = y;
            final Color c = new Color(color);
            final float r = c.getRed() / 255.0f;
            final float g = c.getGreen() / 255.0f;
            final float b = c.getBlue() / 255.0f;
            FXDispatcher.INSTANCE.crucibleBubble(pos.getX() + x / 32.0f + 0.015625f, pos.getY() + 0.05f + getFluidHeight(), pos.getZ() + y / 32.0f + 0.015625f, r, g, b);
        }
    }
    
    public void ejectItem(final ItemStack items) {
        boolean first = true;
        do {
            final ItemStack spitout = items.copy();
            if (spitout.getCount() > spitout.getMaxStackSize()) {
                spitout.setCount(spitout.getMaxStackSize());
            }
            items.shrink(spitout.getCount());
            final EntitySpecialItem entityitem = new EntitySpecialItem(world, pos.getX() + 0.5f, pos.getY() + 0.71f, pos.getZ() + 0.5f, spitout);
            entityitem.motionY = 0.07500000298023224;
            entityitem.motionX = (first ? 0.0 : ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.01f));
            entityitem.motionZ = (first ? 0.0 : ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.01f));
            world.spawnEntity(entityitem);
            first = false;
        } while (items.getCount() > 0);
    }
    
    public ItemStack attemptSmelt(final ItemStack item, final String username) {
        boolean bubble = false;
        boolean craftDone = false;
        int stacksize = item.getCount();
        final EntityPlayer player = world.getPlayerEntityByName(username);
        for (int a = 0; a < stacksize; ++a) {
            final CrucibleRecipe rc = ThaumcraftCraftingManager.findMatchingCrucibleRecipe(player, aspects, item);
            if (rc != null && tank.getFluidAmount() > 0) {
                final ItemStack out = rc.getRecipeOutput().copy();
                if (player != null) {
                    FMLCommonHandler.instance().firePlayerCraftingEvent(player, out, new InventoryFake(item));
                }
                aspects = rc.removeMatching(aspects);
                tank.drain(50, true);
                ejectItem(out);
                craftDone = true;
                --stacksize;
                counter = -250L;
            }
            else {
                final AspectList ot = ThaumcraftCraftingManager.getObjectTags(item);
                if (ot != null) {
                    if (ot.size() != 0) {
                        for (final Aspect tag : ot.getAspects()) {
                            aspects.add(tag, ot.getAmount(tag));
                        }
                        bubble = true;
                        --stacksize;
                        counter = -150L;
                    }
                }
            }
        }
        if (bubble) {
            world.playSound(null, pos, SoundsTC.bubble, SoundCategory.BLOCKS, 0.2f, 1.0f + world.rand.nextFloat() * 0.4f);
            syncTile(false);
            world.addBlockEvent(pos, BlocksTC.crucible, 2, 1);
        }
        if (craftDone) {
            syncTile(false);
            world.addBlockEvent(pos, BlocksTC.crucible, 99, 0);
        }
        markDirty();
        if (stacksize <= 0) {
            return null;
        }
        item.setCount(stacksize);
        return item;
    }
    
    public void attemptSmelt(final EntityItem entity) {
        final ItemStack item = entity.getItem();
        final NBTTagCompound itemData = entity.getEntityData();
        final String username = itemData.getString("thrower");
        final ItemStack res = attemptSmelt(item, username);
        if (res == null || res.getCount() <= 0) {
            entity.setDead();
        }
        else {
            item.setCount(res.getCount());
            entity.setItem(item);
        }
    }
    
    public float getFluidHeight() {
        final float base = 0.3f + 0.5f * (tank.getFluidAmount() / (float) tank.getCapacity());
        float out = base + aspects.visSize() / 500.0f * (1.0f - base);
        if (out > 1.0f) {
            out = 1.001f;
        }
        if (out == 1.0f) {
            out = 0.9999f;
        }
        return out;
    }
    
    public void spillRandom() {
        if (aspects.size() > 0) {
            final Aspect tag = aspects.getAspects()[world.rand.nextInt(aspects.getAspects().length)];
            aspects.remove(tag, 1);
            AuraHelper.polluteAura(world, getPos(), (tag == Aspect.FLUX) ? 1.0f : 0.25f, true);
        }
        markDirty();
        syncTile(false);
    }
    
    public void spillRemnants() {
        final int vs = aspects.visSize();
        if (tank.getFluidAmount() > 0 || vs > 0) {
            tank.setFluid(null);
            AuraHelper.polluteAura(world, getPos(), vs * 0.25f, true);
            final int f = aspects.getAmount(Aspect.FLUX);
            if (f > 0) {
                AuraHelper.polluteAura(world, getPos(), f * 0.75f, false);
            }
            aspects = new AspectList();
            world.addBlockEvent(pos, BlocksTC.crucible, 2, 5);
            markDirty();
            syncTile(false);
        }
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 99) {
            if (world.isRemote) {
                FXDispatcher.INSTANCE.drawBamf(pos.getX() + 0.5, pos.getY() + 1.25f, pos.getZ() + 0.5, true, true, EnumFacing.UP);
                world.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundsTC.spill, SoundCategory.BLOCKS, 0.2f, 1.0f, false);
            }
            return true;
        }
        if (i == 1) {
            if (world.isRemote) {
                FXDispatcher.INSTANCE.drawBamf(pos.up(), true, true, EnumFacing.UP);
            }
            return true;
        }
        if (i == 2) {
            world.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundsTC.spill, SoundCategory.BLOCKS, 0.2f, 1.0f, false);
            if (world.isRemote) {
                for (int q = 0; q < 10; ++q) {
                    FXDispatcher.INSTANCE.crucibleBoil(pos, this, j);
                }
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }
    
    public AspectList getAspects() {
        return aspects;
    }
    
    public void setAspects(final AspectList aspects) {
    }
    
    public int addToContainer(final Aspect tag, final int amount) {
        return 0;
    }
    
    public boolean takeFromContainer(final Aspect tag, final int amount) {
        return false;
    }
    
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    public boolean doesContainerContainAmount(final Aspect tag, final int amount) {
        return false;
    }
    
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }
    
    public int containerContains(final Aspect tag) {
        return 0;
    }
    
    public boolean doesContainerAccept(final Aspect tag) {
        return true;
    }
    
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
    
    @Nullable
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) tank;
        }
        return (T)super.getCapability((Capability)capability, facing);
    }
    
    public IFluidTankProperties[] getTankProperties() {
        return tank.getTankProperties();
    }
    
    public int fill(final FluidStack resource, final boolean doFill) {
        markDirty();
        syncTile(false);
        return tank.fill(resource, doFill);
    }
    
    public FluidStack drain(final FluidStack resource, final boolean doDrain) {
        final FluidStack fs = tank.drain(resource, doDrain);
        markDirty();
        syncTile(false);
        return fs;
    }
    
    public FluidStack drain(final int maxDrain, final boolean doDrain) {
        final FluidStack fs = tank.drain(maxDrain, doDrain);
        markDirty();
        syncTile(false);
        return fs;
    }
}
