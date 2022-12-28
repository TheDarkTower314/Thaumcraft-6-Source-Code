package thaumcraft.common.tiles.devices;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TileInfernalFurnace extends TileThaumcraftInventory
{
    public int furnaceCookTime;
    public int furnaceMaxCookTime;
    public int speedyTime;
    public int facingX;
    public int facingZ;
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX() - 1.3, getPos().getY() - 1.3, getPos().getZ() - 1.3, getPos().getX() + 2.3, getPos().getY() + 2.3, getPos().getZ() + 2.3);
    }
    
    public TileInfernalFurnace() {
        super(32);
        facingX = -5;
        facingZ = -5;
        furnaceCookTime = 0;
        furnaceMaxCookTime = 0;
        speedyTime = 0;
    }
    
    @Override
    public int[] getSlotsForFace(EnumFacing par1) {
        return (par1 == EnumFacing.UP) ? super.getSlotsForFace(par1) : new int[0];
    }
    
    @Override
    public boolean canExtractItem(int par1, ItemStack stack2, EnumFacing par3) {
        return false;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        furnaceCookTime = nbttagcompound.getShort("CookTime");
        speedyTime = nbttagcompound.getShort("SpeedyTime");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("CookTime", (short) furnaceCookTime);
        nbttagcompound.setShort("SpeedyTime", (short) speedyTime);
        return nbttagcompound;
    }
    
    @Override
    public void update() {
        super.update();
        if (facingX == -5) {
            setFacing();
        }
        if (!world.isRemote) {
            boolean cookedflag = false;
            if (furnaceCookTime > 0) {
                --furnaceCookTime;
                cookedflag = true;
            }
            if (furnaceMaxCookTime <= 0) {
                furnaceMaxCookTime = calcCookTime();
            }
            if (furnaceCookTime > furnaceMaxCookTime) {
                furnaceCookTime = furnaceMaxCookTime;
            }
            if (furnaceCookTime <= 0 && cookedflag) {
                for (int a = 0; a < getSizeInventory(); ++a) {
                    if (getStackInSlot(a) != null && !getStackInSlot(a).isEmpty()) {
                        ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(getStackInSlot(a));
                        if (itemstack != null && !itemstack.isEmpty()) {
                            if (speedyTime > 0) {
                                --speedyTime;
                            }
                            ejectItem(itemstack.copy(), getStackInSlot(a));
                            world.addBlockEvent(getPos(), BlocksTC.infernalFurnace, 3, 0);
                            if (getWorld().rand.nextInt(20) == 0) {
                                AuraHelper.polluteAura(getWorld(), getPos().offset(getFacing().getOpposite()), 1.0f, true);
                            }
                            decrStackSize(a, 1);
                            break;
                        }
                        setInventorySlotContents(a, ItemStack.EMPTY);
                    }
                }
            }
            if (speedyTime <= 0) {
                speedyTime = (int)AuraHelper.drainVis(getWorld(), getPos(), 20.0f, false);
            }
            if (furnaceCookTime == 0 && !cookedflag) {
                for (int a = 0; a < getSizeInventory(); ++a) {
                    if (canSmelt(getStackInSlot(a))) {
                        furnaceMaxCookTime = calcCookTime();
                        furnaceCookTime = furnaceMaxCookTime;
                        break;
                    }
                }
            }
        }
    }
    
    private int getBellows() {
        int bellows = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            if (dir != EnumFacing.UP) {
                BlockPos p2 = pos.offset(dir, 2);
                TileEntity tile = world.getTileEntity(p2);
                if (tile != null && tile instanceof TileBellows && BlockStateUtils.getFacing(world.getBlockState(p2)) == dir.getOpposite() && world.isBlockIndirectlyGettingPowered(p2) == 0) {
                    ++bellows;
                }
            }
        }
        return Math.min(4, bellows);
    }
    
    private int calcCookTime() {
        int b = getBellows();
        if (b > 0) {
            b *= 20 - (b - 1);
        }
        return Math.max(10, ((speedyTime > 0) ? 80 : 140) - b);
    }
    
    public ItemStack addItemsToInventory(ItemStack items) {
        if (canSmelt(items)) {
            items = ThaumcraftInvHelper.insertStackAt(getWorld(), getPos(), EnumFacing.UP, items, false);
        }
        else {
            destroyItem();
            items = ItemStack.EMPTY;
        }
        return items;
    }
    
    private void destroyItem() {
        world.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.3f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f, false);
        double var21 = pos.getX() + world.rand.nextFloat();
        double var22 = pos.getY() + 1;
        double var23 = pos.getZ() + world.rand.nextFloat();
        world.spawnParticle(EnumParticleTypes.LAVA, var21, var22, var23, 0.0, 0.0, 0.0);
    }
    
    public void ejectItem(ItemStack items, ItemStack furnaceItemStack) {
        if (items == null || items.isEmpty()) {
            return;
        }
        ArrayList<ItemStack> ejecti = new ArrayList<ItemStack>();
        ejecti.add(items.copy());
        int bellows = getBellows() + 1;
        float lx = 0.5f;
        lx += facingX * 1.2f;
        float lz = 0.5f;
        lz += facingZ * 1.2f;
        float mx = 0.0f;
        float mz = 0.0f;
        for (int a = 0; a < bellows; ++a) {
            ItemStack[] boni = getSmeltingBonus(furnaceItemStack);
            if (boni != null) {
                for (ItemStack bonus : boni) {
                    if (!bonus.isEmpty() && bonus.getCount() > 0) {
                        ejecti.add(bonus);
                    }
                }
            }
        }
        for (ItemStack outItem : ejecti) {
            if (outItem.isEmpty()) {
                continue;
            }
            EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata()).getOpposite();
            InventoryUtils.ejectStackAt(getWorld(), getPos(), facing, outItem);
        }
        int cnt = items.getCount();
        float xpf = FurnaceRecipes.instance().getSmeltingExperience(items);
        if (xpf == 0.0f) {
            cnt = 0;
        }
        else if (xpf < 1.0f) {
            int var4 = MathHelper.floor(cnt * xpf);
            if (var4 < MathHelper.ceil(cnt * xpf) && (float)Math.random() < cnt * xpf - var4) {
                ++var4;
            }
            cnt = var4;
        }
        while (cnt > 0) {
            int var4 = EntityXPOrb.getXPSplit(cnt);
            cnt -= var4;
            EntityXPOrb xp = new EntityXPOrb(world, pos.getX() + lx, pos.getY() + 0.4f, pos.getZ() + lz, var4);
            mx = ((facingX == 0) ? ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.025f) : (facingX * 0.13f));
            mz = ((facingZ == 0) ? ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.025f) : (facingZ * 0.13f));
            xp.motionX = mx;
            xp.motionZ = mz;
            xp.motionY = 0.0;
            world.spawnEntity(xp);
        }
    }
    
    private ItemStack[] getSmeltingBonus(ItemStack in) {
        ArrayList<ItemStack> out = new ArrayList<ItemStack>();
        for (ThaumcraftApi.SmeltBonus bonus : CommonInternals.smeltingBonus) {
            if (bonus.in instanceof ItemStack) {
                if (in.getItem() != ((ItemStack)bonus.in).getItem() || (in.getItemDamage() != ((ItemStack)bonus.in).getItemDamage() && ((ItemStack)bonus.in).getItemDamage() != 32767) || world.rand.nextFloat() > bonus.chance) {
                    continue;
                }
                ItemStack is = bonus.out.copy();
                if (is.getCount() < 1) {
                    is.setCount(1);
                }
                out.add(is);
            }
            else {
                int[] oreIDs = OreDictionary.getOreIDs(in);
                int length = oreIDs.length;
                int i = 0;
                while (i < length) {
                    int id = oreIDs[i];
                    String od = OreDictionary.getOreName(id);
                    if (bonus.in.equals(od)) {
                        if (world.rand.nextFloat() <= bonus.chance) {
                            ItemStack is2 = bonus.out.copy();
                            if (is2.getCount() < 1) {
                                is2.setCount(1);
                            }
                            out.add(is2);
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
        }
        return out.toArray(new ItemStack[0]);
    }
    
    private boolean canSmelt(ItemStack stack) {
        return !FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty();
    }
    
    private void setFacing() {
        facingX = 0;
        facingZ = 0;
        EnumFacing face = getFacing().getOpposite();
        facingX = face.getFrontOffsetX();
        facingZ = face.getFrontOffsetZ();
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 3) {
            if (world.isRemote) {
                for (int a = 0; a < 5; ++a) {
                    FXDispatcher.INSTANCE.furnaceLavaFx(pos.getX(), pos.getY(), pos.getZ(), facingX, facingZ);
                    world.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.1f + world.rand.nextFloat() * 0.1f, 0.9f + world.rand.nextFloat() * 0.15f, false);
                }
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
}
