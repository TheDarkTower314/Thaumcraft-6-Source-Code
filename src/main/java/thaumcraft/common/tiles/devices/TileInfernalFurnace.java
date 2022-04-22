// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import thaumcraft.client.fx.FXDispatcher;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.CommonInternals;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.lib.utils.InventoryUtils;
import java.util.ArrayList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import thaumcraft.api.ThaumcraftInvHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
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
        return new AxisAlignedBB(this.getPos().getX() - 1.3, this.getPos().getY() - 1.3, this.getPos().getZ() - 1.3, this.getPos().getX() + 2.3, this.getPos().getY() + 2.3, this.getPos().getZ() + 2.3);
    }
    
    public TileInfernalFurnace() {
        super(32);
        this.facingX = -5;
        this.facingZ = -5;
        this.furnaceCookTime = 0;
        this.furnaceMaxCookTime = 0;
        this.speedyTime = 0;
    }
    
    @Override
    public int[] getSlotsForFace(final EnumFacing par1) {
        return (par1 == EnumFacing.UP) ? super.getSlotsForFace(par1) : new int[0];
    }
    
    @Override
    public boolean canExtractItem(final int par1, final ItemStack stack2, final EnumFacing par3) {
        return false;
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.furnaceCookTime = nbttagcompound.getShort("CookTime");
        this.speedyTime = nbttagcompound.getShort("SpeedyTime");
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("CookTime", (short)this.furnaceCookTime);
        nbttagcompound.setShort("SpeedyTime", (short)this.speedyTime);
        return nbttagcompound;
    }
    
    @Override
    public void update() {
        super.update();
        if (this.facingX == -5) {
            this.setFacing();
        }
        if (!this.world.isRemote) {
            boolean cookedflag = false;
            if (this.furnaceCookTime > 0) {
                --this.furnaceCookTime;
                cookedflag = true;
            }
            if (this.furnaceMaxCookTime <= 0) {
                this.furnaceMaxCookTime = this.calcCookTime();
            }
            if (this.furnaceCookTime > this.furnaceMaxCookTime) {
                this.furnaceCookTime = this.furnaceMaxCookTime;
            }
            if (this.furnaceCookTime <= 0 && cookedflag) {
                for (int a = 0; a < this.getSizeInventory(); ++a) {
                    if (this.getStackInSlot(a) != null && !this.getStackInSlot(a).isEmpty()) {
                        final ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(this.getStackInSlot(a));
                        if (itemstack != null && !itemstack.isEmpty()) {
                            if (this.speedyTime > 0) {
                                --this.speedyTime;
                            }
                            this.ejectItem(itemstack.copy(), this.getStackInSlot(a));
                            this.world.addBlockEvent(this.getPos(), BlocksTC.infernalFurnace, 3, 0);
                            if (this.getWorld().rand.nextInt(20) == 0) {
                                AuraHelper.polluteAura(this.getWorld(), this.getPos().offset(this.getFacing().getOpposite()), 1.0f, true);
                            }
                            this.decrStackSize(a, 1);
                            break;
                        }
                        this.setInventorySlotContents(a, ItemStack.EMPTY);
                    }
                }
            }
            if (this.speedyTime <= 0) {
                this.speedyTime = (int)AuraHelper.drainVis(this.getWorld(), this.getPos(), 20.0f, false);
            }
            if (this.furnaceCookTime == 0 && !cookedflag) {
                for (int a = 0; a < this.getSizeInventory(); ++a) {
                    if (this.canSmelt(this.getStackInSlot(a))) {
                        this.furnaceMaxCookTime = this.calcCookTime();
                        this.furnaceCookTime = this.furnaceMaxCookTime;
                        break;
                    }
                }
            }
        }
    }
    
    private int getBellows() {
        int bellows = 0;
        for (final EnumFacing dir : EnumFacing.VALUES) {
            if (dir != EnumFacing.UP) {
                final BlockPos p2 = this.pos.offset(dir, 2);
                final TileEntity tile = this.world.getTileEntity(p2);
                if (tile != null && tile instanceof TileBellows && BlockStateUtils.getFacing(this.world.getBlockState(p2)) == dir.getOpposite() && this.world.isBlockIndirectlyGettingPowered(p2) == 0) {
                    ++bellows;
                }
            }
        }
        return Math.min(4, bellows);
    }
    
    private int calcCookTime() {
        int b = this.getBellows();
        if (b > 0) {
            b *= 20 - (b - 1);
        }
        return Math.max(10, ((this.speedyTime > 0) ? 80 : 140) - b);
    }
    
    public ItemStack addItemsToInventory(ItemStack items) {
        if (this.canSmelt(items)) {
            items = ThaumcraftInvHelper.insertStackAt(this.getWorld(), this.getPos(), EnumFacing.UP, items, false);
        }
        else {
            this.destroyItem();
            items = ItemStack.EMPTY;
        }
        return items;
    }
    
    private void destroyItem() {
        this.world.playSound(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.3f, 2.6f + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8f, false);
        final double var21 = this.pos.getX() + this.world.rand.nextFloat();
        final double var22 = this.pos.getY() + 1;
        final double var23 = this.pos.getZ() + this.world.rand.nextFloat();
        this.world.spawnParticle(EnumParticleTypes.LAVA, var21, var22, var23, 0.0, 0.0, 0.0);
    }
    
    public void ejectItem(final ItemStack items, final ItemStack furnaceItemStack) {
        if (items == null || items.isEmpty()) {
            return;
        }
        final ArrayList<ItemStack> ejecti = new ArrayList<ItemStack>();
        ejecti.add(items.copy());
        final int bellows = this.getBellows() + 1;
        float lx = 0.5f;
        lx += this.facingX * 1.2f;
        float lz = 0.5f;
        lz += this.facingZ * 1.2f;
        float mx = 0.0f;
        float mz = 0.0f;
        for (int a = 0; a < bellows; ++a) {
            final ItemStack[] boni = this.getSmeltingBonus(furnaceItemStack);
            if (boni != null) {
                for (final ItemStack bonus : boni) {
                    if (!bonus.isEmpty() && bonus.getCount() > 0) {
                        ejecti.add(bonus);
                    }
                }
            }
        }
        for (final ItemStack outItem : ejecti) {
            if (outItem.isEmpty()) {
                continue;
            }
            final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata()).getOpposite();
            InventoryUtils.ejectStackAt(this.getWorld(), this.getPos(), facing, outItem);
        }
        int cnt = items.getCount();
        final float xpf = FurnaceRecipes.instance().getSmeltingExperience(items);
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
            final int var4 = EntityXPOrb.getXPSplit(cnt);
            cnt -= var4;
            final EntityXPOrb xp = new EntityXPOrb(this.world, this.pos.getX() + lx, this.pos.getY() + 0.4f, this.pos.getZ() + lz, var4);
            mx = ((this.facingX == 0) ? ((this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.025f) : (this.facingX * 0.13f));
            mz = ((this.facingZ == 0) ? ((this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.025f) : (this.facingZ * 0.13f));
            xp.motionX = mx;
            xp.motionZ = mz;
            xp.motionY = 0.0;
            this.world.spawnEntity(xp);
        }
    }
    
    private ItemStack[] getSmeltingBonus(final ItemStack in) {
        final ArrayList<ItemStack> out = new ArrayList<ItemStack>();
        for (final ThaumcraftApi.SmeltBonus bonus : CommonInternals.smeltingBonus) {
            if (bonus.in instanceof ItemStack) {
                if (in.getItem() != ((ItemStack)bonus.in).getItem() || (in.getItemDamage() != ((ItemStack)bonus.in).getItemDamage() && ((ItemStack)bonus.in).getItemDamage() != 32767) || this.world.rand.nextFloat() > bonus.chance) {
                    continue;
                }
                final ItemStack is = bonus.out.copy();
                if (is.getCount() < 1) {
                    is.setCount(1);
                }
                out.add(is);
            }
            else {
                final int[] oreIDs = OreDictionary.getOreIDs(in);
                final int length = oreIDs.length;
                int i = 0;
                while (i < length) {
                    final int id = oreIDs[i];
                    final String od = OreDictionary.getOreName(id);
                    if (bonus.in.equals(od)) {
                        if (this.world.rand.nextFloat() <= bonus.chance) {
                            final ItemStack is2 = bonus.out.copy();
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
    
    private boolean canSmelt(final ItemStack stack) {
        return !FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty();
    }
    
    private void setFacing() {
        this.facingX = 0;
        this.facingZ = 0;
        final EnumFacing face = this.getFacing().getOpposite();
        this.facingX = face.getFrontOffsetX();
        this.facingZ = face.getFrontOffsetZ();
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 3) {
            if (this.world.isRemote) {
                for (int a = 0; a < 5; ++a) {
                    FXDispatcher.INSTANCE.furnaceLavaFx(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.facingX, this.facingZ);
                    this.world.playSound(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.1f + this.world.rand.nextFloat() * 0.1f, 0.9f + this.world.rand.nextFloat() * 0.15f, false);
                }
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
}
