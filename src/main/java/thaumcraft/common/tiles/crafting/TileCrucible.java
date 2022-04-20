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
        this.aspects = new AspectList();
        this.bellows = -1;
        this.delay = 0;
        this.counter = -100L;
        this.prevcolor = 0;
        this.prevx = 0;
        this.prevy = 0;
        this.tank = new FluidTank(FluidRegistry.WATER, 0, 1000);
        this.heat = 0;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        this.heat = nbttagcompound.getShort("Heat");
        this.tank.readFromNBT(nbttagcompound);
        if (nbttagcompound.hasKey("Empty")) {
            this.tank.setFluid(null);
        }
        this.aspects.readFromNBT(nbttagcompound);
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Heat", this.heat);
        this.tank.writeToNBT(nbttagcompound);
        this.aspects.writeToNBT(nbttagcompound);
        return nbttagcompound;
    }
    
    public void update() {
        ++this.counter;
        final int prevheat = this.heat;
        if (!this.world.isRemote) {
            if (this.tank.getFluidAmount() > 0) {
                final IBlockState block = this.world.getBlockState(this.getPos().down());
                if (block.getMaterial() == Material.LAVA || block.getMaterial() == Material.FIRE || BlocksTC.nitor.containsValue(block.getBlock()) || block.getBlock() == Blocks.MAGMA) {
                    if (this.heat < 200) {
                        ++this.heat;
                        if (prevheat < 151 && this.heat >= 151) {
                            this.markDirty();
                            this.syncTile(false);
                        }
                    }
                }
                else if (this.heat > 0) {
                    --this.heat;
                    if (this.heat == 149) {
                        this.markDirty();
                        this.syncTile(false);
                    }
                }
            }
            else if (this.heat > 0) {
                --this.heat;
            }
            if (this.aspects.visSize() > 500) {
                this.spillRandom();
            }
            if (this.counter >= 100L) {
                this.spillRandom();
                this.counter = 0L;
            }
        }
        else if (this.tank.getFluidAmount() > 0) {
            this.drawEffects();
        }
        if (this.world.isRemote && prevheat < 151 && this.heat >= 151) {
            ++this.heat;
        }
    }
    
    private void drawEffects() {
        if (this.heat > 150) {
            FXDispatcher.INSTANCE.crucibleFroth(this.pos.getX() + 0.2f + this.world.rand.nextFloat() * 0.6f, this.pos.getY() + this.getFluidHeight(), this.pos.getZ() + 0.2f + this.world.rand.nextFloat() * 0.6f);
            if (this.aspects.visSize() > 500) {
                for (int a = 0; a < 2; ++a) {
                    FXDispatcher.INSTANCE.crucibleFrothDown((float)this.pos.getX(), (float)(this.pos.getY() + 1), this.pos.getZ() + this.world.rand.nextFloat());
                    FXDispatcher.INSTANCE.crucibleFrothDown((float)(this.pos.getX() + 1), (float)(this.pos.getY() + 1), this.pos.getZ() + this.world.rand.nextFloat());
                    FXDispatcher.INSTANCE.crucibleFrothDown(this.pos.getX() + this.world.rand.nextFloat(), (float)(this.pos.getY() + 1), (float)this.pos.getZ());
                    FXDispatcher.INSTANCE.crucibleFrothDown(this.pos.getX() + this.world.rand.nextFloat(), (float)(this.pos.getY() + 1), (float)(this.pos.getZ() + 1));
                }
            }
        }
        if (this.world.rand.nextInt(6) == 0 && this.aspects.size() > 0) {
            final int color = this.aspects.getAspects()[this.world.rand.nextInt(this.aspects.size())].getColor() - 16777216;
            final int x = 5 + this.world.rand.nextInt(22);
            final int y = 5 + this.world.rand.nextInt(22);
            this.delay = this.world.rand.nextInt(10);
            this.prevcolor = color;
            this.prevx = x;
            this.prevy = y;
            final Color c = new Color(color);
            final float r = c.getRed() / 255.0f;
            final float g = c.getGreen() / 255.0f;
            final float b = c.getBlue() / 255.0f;
            FXDispatcher.INSTANCE.crucibleBubble(this.pos.getX() + x / 32.0f + 0.015625f, this.pos.getY() + 0.05f + this.getFluidHeight(), this.pos.getZ() + y / 32.0f + 0.015625f, r, g, b);
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
            final EntitySpecialItem entityitem = new EntitySpecialItem(this.world, this.pos.getX() + 0.5f, this.pos.getY() + 0.71f, this.pos.getZ() + 0.5f, spitout);
            entityitem.motionY = 0.07500000298023224;
            entityitem.motionX = (first ? 0.0 : ((this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.01f));
            entityitem.motionZ = (first ? 0.0 : ((this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.01f));
            this.world.spawnEntity(entityitem);
            first = false;
        } while (items.getCount() > 0);
    }
    
    public ItemStack attemptSmelt(final ItemStack item, final String username) {
        boolean bubble = false;
        boolean craftDone = false;
        int stacksize = item.getCount();
        final EntityPlayer player = this.world.getPlayerEntityByName(username);
        for (int a = 0; a < stacksize; ++a) {
            final CrucibleRecipe rc = ThaumcraftCraftingManager.findMatchingCrucibleRecipe(player, this.aspects, item);
            if (rc != null && this.tank.getFluidAmount() > 0) {
                final ItemStack out = rc.getRecipeOutput().copy();
                if (player != null) {
                    FMLCommonHandler.instance().firePlayerCraftingEvent(player, out, new InventoryFake(new ItemStack[] { item }));
                }
                this.aspects = rc.removeMatching(this.aspects);
                this.tank.drain(50, true);
                this.ejectItem(out);
                craftDone = true;
                --stacksize;
                this.counter = -250L;
            }
            else {
                final AspectList ot = ThaumcraftCraftingManager.getObjectTags(item);
                if (ot != null) {
                    if (ot.size() != 0) {
                        for (final Aspect tag : ot.getAspects()) {
                            this.aspects.add(tag, ot.getAmount(tag));
                        }
                        bubble = true;
                        --stacksize;
                        this.counter = -150L;
                    }
                }
            }
        }
        if (bubble) {
            this.world.playSound(null, this.pos, SoundsTC.bubble, SoundCategory.BLOCKS, 0.2f, 1.0f + this.world.rand.nextFloat() * 0.4f);
            this.syncTile(false);
            this.world.addBlockEvent(this.pos, BlocksTC.crucible, 2, 1);
        }
        if (craftDone) {
            this.syncTile(false);
            this.world.addBlockEvent(this.pos, BlocksTC.crucible, 99, 0);
        }
        this.markDirty();
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
        final ItemStack res = this.attemptSmelt(item, username);
        if (res == null || res.getCount() <= 0) {
            entity.setDead();
        }
        else {
            item.setCount(res.getCount());
            entity.setItem(item);
        }
    }
    
    public float getFluidHeight() {
        final float base = 0.3f + 0.5f * (this.tank.getFluidAmount() / (float)this.tank.getCapacity());
        float out = base + this.aspects.visSize() / 500.0f * (1.0f - base);
        if (out > 1.0f) {
            out = 1.001f;
        }
        if (out == 1.0f) {
            out = 0.9999f;
        }
        return out;
    }
    
    public void spillRandom() {
        if (this.aspects.size() > 0) {
            final Aspect tag = this.aspects.getAspects()[this.world.rand.nextInt(this.aspects.getAspects().length)];
            this.aspects.remove(tag, 1);
            AuraHelper.polluteAura(this.world, this.getPos(), (tag == Aspect.FLUX) ? 1.0f : 0.25f, true);
        }
        this.markDirty();
        this.syncTile(false);
    }
    
    public void spillRemnants() {
        final int vs = this.aspects.visSize();
        if (this.tank.getFluidAmount() > 0 || vs > 0) {
            this.tank.setFluid(null);
            AuraHelper.polluteAura(this.world, this.getPos(), vs * 0.25f, true);
            final int f = this.aspects.getAmount(Aspect.FLUX);
            if (f > 0) {
                AuraHelper.polluteAura(this.world, this.getPos(), f * 0.75f, false);
            }
            this.aspects = new AspectList();
            this.world.addBlockEvent(this.pos, BlocksTC.crucible, 2, 5);
            this.markDirty();
            this.syncTile(false);
        }
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 99) {
            if (this.world.isRemote) {
                FXDispatcher.INSTANCE.drawBamf(this.pos.getX() + 0.5, this.pos.getY() + 1.25f, this.pos.getZ() + 0.5, true, true, EnumFacing.UP);
                this.world.playSound(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f, SoundsTC.spill, SoundCategory.BLOCKS, 0.2f, 1.0f, false);
            }
            return true;
        }
        if (i == 1) {
            if (this.world.isRemote) {
                FXDispatcher.INSTANCE.drawBamf(this.pos.up(), true, true, EnumFacing.UP);
            }
            return true;
        }
        if (i == 2) {
            this.world.playSound(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f, SoundsTC.spill, SoundCategory.BLOCKS, 0.2f, 1.0f, false);
            if (this.world.isRemote) {
                for (int q = 0; q < 10; ++q) {
                    FXDispatcher.INSTANCE.crucibleBoil(this.pos, this, j);
                }
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1);
    }
    
    public AspectList getAspects() {
        return this.aspects;
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
            return (T)this.tank;
        }
        return (T)super.getCapability((Capability)capability, facing);
    }
    
    public IFluidTankProperties[] getTankProperties() {
        return this.tank.getTankProperties();
    }
    
    public int fill(final FluidStack resource, final boolean doFill) {
        this.markDirty();
        this.syncTile(false);
        return this.tank.fill(resource, doFill);
    }
    
    public FluidStack drain(final FluidStack resource, final boolean doDrain) {
        final FluidStack fs = this.tank.drain(resource, doDrain);
        this.markDirty();
        this.syncTile(false);
        return fs;
    }
    
    public FluidStack drain(final int maxDrain, final boolean doDrain) {
        final FluidStack fs = this.tank.drain(maxDrain, doDrain);
        this.markDirty();
        this.syncTile(false);
        return fs;
    }
}
