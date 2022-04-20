// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import java.util.List;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.util.NonNullList;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.api.ThaumcraftInvHelper;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.util.ITickable;
import thaumcraft.common.tiles.TileThaumcraft;

public class TilePatternCrafter extends TileThaumcraft implements ITickable
{
    public byte type;
    public int count;
    private final InventoryCrafting craftMatrix;
    float power;
    public float rot;
    public float rp;
    public int rotTicks;
    ItemStack outStack;
    
    public TilePatternCrafter() {
        this.type = 0;
        this.count = new Random(System.currentTimeMillis()).nextInt(20);
        this.craftMatrix = new InventoryCrafting(new Container() {
            public boolean canInteractWith(final EntityPlayer playerIn) {
                return false;
            }
        }, 3, 3);
        this.power = 0.0f;
        this.rotTicks = 0;
        this.outStack = null;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbt) {
        this.type = nbt.getByte("type");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbt) {
        nbt.setByte("type", this.type);
        return nbt;
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbt) {
        this.power = nbt.getFloat("power");
        super.readFromNBT(nbt);
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbt) {
        nbt.setFloat("power", this.power);
        return super.writeToNBT(nbt);
    }
    
    public void update() {
        if (this.world.isRemote) {
            if (this.rotTicks > 0) {
                --this.rotTicks;
                if (this.rotTicks % Math.floor(Math.max(1.0f, this.rp)) == 0.0) {
                    this.world.playSound(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, SoundsTC.clack, SoundCategory.BLOCKS, 0.2f, 1.7f, false);
                }
                ++this.rp;
            }
            else {
                this.rp *= 0.8f;
            }
            this.rot += this.rp;
        }
        if (!this.world.isRemote && this.count++ % 20 == 0 && BlockStateUtils.isEnabled(this.getBlockMetadata())) {
            if (this.power <= 0.0f) {
                this.power += AuraHelper.drainVis(this.getWorld(), this.getPos(), 5.0f, false);
            }
            int amt = 9;
            switch (this.type) {
                case 0: {
                    amt = 9;
                    break;
                }
                case 1: {
                    amt = 1;
                    break;
                }
                case 2:
                case 3: {
                    amt = 2;
                    break;
                }
                case 4: {
                    amt = 4;
                    break;
                }
                case 5:
                case 6: {
                    amt = 3;
                    break;
                }
                case 7:
                case 8: {
                    amt = 6;
                    break;
                }
                case 9: {
                    amt = 8;
                    break;
                }
            }
            final IItemHandler above = ThaumcraftInvHelper.getItemHandlerAt(this.getWorld(), this.getPos().up(), EnumFacing.DOWN);
            final IItemHandler below = ThaumcraftInvHelper.getItemHandlerAt(this.getWorld(), this.getPos().down(), EnumFacing.UP);
            if (above != null && below != null) {
                for (int a = 0; a < above.getSlots(); ++a) {
                    final ItemStack testStack = above.getStackInSlot(a).copy();
                    if (!testStack.isEmpty()) {
                        testStack.setCount(amt);
                        if (InventoryUtils.removeStackFrom(this.getWorld(), this.getPos().up(), EnumFacing.DOWN, testStack.copy(), ThaumcraftInvHelper.InvFilter.BASEORE, true).getCount() == amt && this.craft(testStack) && this.power >= 1.0f && ItemHandlerHelper.insertItem(below, this.outStack.copy(), true).isEmpty()) {
                            boolean b = true;
                            for (int i = 0; i < 9; ++i) {
                                if (this.craftMatrix.getStackInSlot(i) != null && !ItemHandlerHelper.insertItem(below, this.craftMatrix.getStackInSlot(i).copy(), true).isEmpty()) {
                                    b = false;
                                    break;
                                }
                            }
                            if (b) {
                                ItemHandlerHelper.insertItem(below, this.outStack.copy(), false);
                                for (int i = 0; i < 9; ++i) {
                                    if (this.craftMatrix.getStackInSlot(i) != null) {
                                        ItemHandlerHelper.insertItem(below, this.craftMatrix.getStackInSlot(i).copy(), false);
                                    }
                                }
                                InventoryUtils.removeStackFrom(this.getWorld(), this.getPos().up(), EnumFacing.DOWN, testStack, ThaumcraftInvHelper.InvFilter.BASEORE, false);
                                this.world.addBlockEvent(this.getPos(), this.getBlockType(), 1, 0);
                                --this.power;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean craft(final ItemStack inStack) {
        this.outStack = ItemStack.EMPTY;
        this.craftMatrix.clear();
        switch (this.type) {
            case 0: {
                for (int a = 0; a < 9; ++a) {
                    this.craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 1: {
                this.craftMatrix.setInventorySlotContents(0, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                break;
            }
            case 2: {
                for (int a = 0; a < 2; ++a) {
                    this.craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 3: {
                for (int a = 0; a < 2; ++a) {
                    this.craftMatrix.setInventorySlotContents(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 4: {
                for (int a = 0; a < 2; ++a) {
                    for (int b = 0; b < 2; ++b) {
                        this.craftMatrix.setInventorySlotContents(a + b * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                    }
                }
                break;
            }
            case 5: {
                for (int a = 0; a < 3; ++a) {
                    this.craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 6: {
                for (int a = 0; a < 3; ++a) {
                    this.craftMatrix.setInventorySlotContents(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 7: {
                for (int a = 0; a < 6; ++a) {
                    this.craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 8: {
                for (int a = 0; a < 2; ++a) {
                    for (int b = 0; b < 3; ++b) {
                        this.craftMatrix.setInventorySlotContents(a + b * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                    }
                }
                break;
            }
            case 9: {
                for (int a = 0; a < 9; ++a) {
                    if (a != 4) {
                        this.craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                    }
                }
                break;
            }
        }
        final IRecipe ir = CraftingManager.findMatchingRecipe(this.craftMatrix, this.world);
        if (ir == null) {
            return false;
        }
        this.outStack = ir.getCraftingResult(this.craftMatrix);
        final NonNullList<ItemStack> aitemstack = CraftingManager.getRemainingItems(this.craftMatrix, this.world);
        for (int i = 0; i < aitemstack.size(); ++i) {
            final ItemStack itemstack1 = this.craftMatrix.getStackInSlot(i);
            final ItemStack itemstack2 = aitemstack.get(i);
            if (!itemstack1.isEmpty()) {
                this.craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);
            }
            if (!itemstack1.isEmpty() && this.craftMatrix.getStackInSlot(i).isEmpty()) {
                this.craftMatrix.setInventorySlotContents(i, itemstack2);
            }
        }
        return !this.outStack.isEmpty();
    }
    
    public void cycle() {
        ++this.type;
        if (this.type > 9) {
            this.type = 0;
        }
        this.syncTile(false);
        this.markDirty();
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 1) {
            if (this.world.isRemote) {
                this.rotTicks = 10;
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    public RayTraceResult rayTrace(final World world, final Vec3d vec3d, final Vec3d vec3d1, final RayTraceResult fullblock) {
        return fullblock;
    }
    
    public void addTraceableCuboids(final List<IndexedCuboid6> cuboids) {
        final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
        cuboids.add(new IndexedCuboid6(0, this.getCuboidByFacing(facing)));
    }
    
    public Cuboid6 getCuboidByFacing(final EnumFacing facing) {
        switch (facing) {
            default: {
                return new Cuboid6(this.getPos().getX() + 0.75, this.getPos().getY() + 0.125, this.getPos().getZ() + 0.375, this.getPos().getX() + 0.875, this.getPos().getY() + 0.375, this.getPos().getZ() + 0.625);
            }
            case EAST: {
                return new Cuboid6(this.getPos().getX() + 0.125, this.getPos().getY() + 0.125, this.getPos().getZ() + 0.375, this.getPos().getX() + 0.25, this.getPos().getY() + 0.375, this.getPos().getZ() + 0.625);
            }
            case NORTH: {
                return new Cuboid6(this.getPos().getX() + 0.375, this.getPos().getY() + 0.125, this.getPos().getZ() + 0.75, this.getPos().getX() + 0.625, this.getPos().getY() + 0.375, this.getPos().getZ() + 0.875);
            }
            case SOUTH: {
                return new Cuboid6(this.getPos().getX() + 0.375, this.getPos().getY() + 0.125, this.getPos().getZ() + 0.125, this.getPos().getX() + 0.625, this.getPos().getY() + 0.375, this.getPos().getZ() + 0.25);
            }
        }
    }
}
