package thaumcraft.common.tiles.crafting;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TilePatternCrafter extends TileThaumcraft implements ITickable
{
    public byte type;
    public int count;
    private InventoryCrafting craftMatrix;
    float power;
    public float rot;
    public float rp;
    public int rotTicks;
    ItemStack outStack;
    
    public TilePatternCrafter() {
        type = 0;
        count = new Random(System.currentTimeMillis()).nextInt(20);
        craftMatrix = new InventoryCrafting(new Container() {
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        }, 3, 3);
        power = 0.0f;
        rotTicks = 0;
        outStack = null;
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbt) {
        type = nbt.getByte("type");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
        nbt.setByte("type", type);
        return nbt;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        power = nbt.getFloat("power");
        super.readFromNBT(nbt);
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setFloat("power", power);
        return super.writeToNBT(nbt);
    }
    
    public void update() {
        if (world.isRemote) {
            if (rotTicks > 0) {
                --rotTicks;
                if (rotTicks % Math.floor(Math.max(1.0f, rp)) == 0.0) {
                    world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.clack, SoundCategory.BLOCKS, 0.2f, 1.7f, false);
                }
                ++rp;
            }
            else {
                rp *= 0.8f;
            }
            rot += rp;
        }
        if (!world.isRemote && count++ % 20 == 0 && BlockStateUtils.isEnabled(getBlockMetadata())) {
            if (power <= 0.0f) {
                power += AuraHelper.drainVis(getWorld(), getPos(), 5.0f, false);
            }
            int amt = 9;
            switch (type) {
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
            IItemHandler above = ThaumcraftInvHelper.getItemHandlerAt(getWorld(), getPos().up(), EnumFacing.DOWN);
            IItemHandler below = ThaumcraftInvHelper.getItemHandlerAt(getWorld(), getPos().down(), EnumFacing.UP);
            if (above != null && below != null) {
                for (int a = 0; a < above.getSlots(); ++a) {
                    ItemStack testStack = above.getStackInSlot(a).copy();
                    if (!testStack.isEmpty()) {
                        testStack.setCount(amt);
                        if (InventoryUtils.removeStackFrom(getWorld(), getPos().up(), EnumFacing.DOWN, testStack.copy(), ThaumcraftInvHelper.InvFilter.BASEORE, true).getCount() == amt && craft(testStack) && power >= 1.0f && ItemHandlerHelper.insertItem(below, outStack.copy(), true).isEmpty()) {
                            boolean b = true;
                            for (int i = 0; i < 9; ++i) {
                                if (craftMatrix.getStackInSlot(i) != null && !ItemHandlerHelper.insertItem(below, craftMatrix.getStackInSlot(i).copy(), true).isEmpty()) {
                                    b = false;
                                    break;
                                }
                            }
                            if (b) {
                                ItemHandlerHelper.insertItem(below, outStack.copy(), false);
                                for (int i = 0; i < 9; ++i) {
                                    if (craftMatrix.getStackInSlot(i) != null) {
                                        ItemHandlerHelper.insertItem(below, craftMatrix.getStackInSlot(i).copy(), false);
                                    }
                                }
                                InventoryUtils.removeStackFrom(getWorld(), getPos().up(), EnumFacing.DOWN, testStack, ThaumcraftInvHelper.InvFilter.BASEORE, false);
                                world.addBlockEvent(getPos(), getBlockType(), 1, 0);
                                --power;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean craft(ItemStack inStack) {
        outStack = ItemStack.EMPTY;
        craftMatrix.clear();
        switch (type) {
            case 0: {
                for (int a = 0; a < 9; ++a) {
                    craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 1: {
                craftMatrix.setInventorySlotContents(0, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                break;
            }
            case 2: {
                for (int a = 0; a < 2; ++a) {
                    craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 3: {
                for (int a = 0; a < 2; ++a) {
                    craftMatrix.setInventorySlotContents(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 4: {
                for (int a = 0; a < 2; ++a) {
                    for (int b = 0; b < 2; ++b) {
                        craftMatrix.setInventorySlotContents(a + b * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                    }
                }
                break;
            }
            case 5: {
                for (int a = 0; a < 3; ++a) {
                    craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 6: {
                for (int a = 0; a < 3; ++a) {
                    craftMatrix.setInventorySlotContents(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 7: {
                for (int a = 0; a < 6; ++a) {
                    craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 8: {
                for (int a = 0; a < 2; ++a) {
                    for (int b = 0; b < 3; ++b) {
                        craftMatrix.setInventorySlotContents(a + b * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                    }
                }
                break;
            }
            case 9: {
                for (int a = 0; a < 9; ++a) {
                    if (a != 4) {
                        craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                    }
                }
                break;
            }
        }
        IRecipe ir = CraftingManager.findMatchingRecipe(craftMatrix, world);
        if (ir == null) {
            return false;
        }
        outStack = ir.getCraftingResult(craftMatrix);
        NonNullList<ItemStack> aitemstack = CraftingManager.getRemainingItems(craftMatrix, world);
        for (int i = 0; i < aitemstack.size(); ++i) {
            ItemStack itemstack1 = craftMatrix.getStackInSlot(i);
            ItemStack itemstack2 = aitemstack.get(i);
            if (!itemstack1.isEmpty()) {
                craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);
            }
            if (!itemstack1.isEmpty() && craftMatrix.getStackInSlot(i).isEmpty()) {
                craftMatrix.setInventorySlotContents(i, itemstack2);
            }
        }
        return !outStack.isEmpty();
    }
    
    public void cycle() {
        ++type;
        if (type > 9) {
            type = 0;
        }
        syncTile(false);
        markDirty();
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 1) {
            if (world.isRemote) {
                rotTicks = 10;
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    public RayTraceResult rayTrace(World world, Vec3d vec3d, Vec3d vec3d1, RayTraceResult fullblock) {
        return fullblock;
    }
    
    public void addTraceableCuboids(List<IndexedCuboid6> cuboids) {
        EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
        cuboids.add(new IndexedCuboid6(0, getCuboidByFacing(facing)));
    }
    
    public Cuboid6 getCuboidByFacing(EnumFacing facing) {
        switch (facing) {
            default: {
                return new Cuboid6(getPos().getX() + 0.75, getPos().getY() + 0.125, getPos().getZ() + 0.375, getPos().getX() + 0.875, getPos().getY() + 0.375, getPos().getZ() + 0.625);
            }
            case EAST: {
                return new Cuboid6(getPos().getX() + 0.125, getPos().getY() + 0.125, getPos().getZ() + 0.375, getPos().getX() + 0.25, getPos().getY() + 0.375, getPos().getZ() + 0.625);
            }
            case NORTH: {
                return new Cuboid6(getPos().getX() + 0.375, getPos().getY() + 0.125, getPos().getZ() + 0.75, getPos().getX() + 0.625, getPos().getY() + 0.375, getPos().getZ() + 0.875);
            }
            case SOUTH: {
                return new Cuboid6(getPos().getX() + 0.375, getPos().getY() + 0.125, getPos().getZ() + 0.125, getPos().getX() + 0.625, getPos().getY() + 0.375, getPos().getZ() + 0.25);
            }
        }
    }
}
