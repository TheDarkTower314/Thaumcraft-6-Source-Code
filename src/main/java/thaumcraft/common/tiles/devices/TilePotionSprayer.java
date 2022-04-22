// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.potion.PotionType;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.container.slot.SlotPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import net.minecraft.util.EnumFacing;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionUtils;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TilePotionSprayer extends TileThaumcraftInventory implements IAspectContainer, IEssentiaTransport
{
    public AspectList recipe;
    public AspectList recipeProgress;
    public int charges;
    public int color;
    int counter;
    boolean activated;
    int venting;
    Aspect currentSuction;
    
    public TilePotionSprayer() {
        super(1);
        this.recipe = new AspectList();
        this.recipeProgress = new AspectList();
        this.charges = 0;
        this.color = 0;
        this.counter = 0;
        this.activated = false;
        this.venting = 0;
        this.currentSuction = null;
    }
    
    @Override
    public void update() {
        super.update();
        ++this.counter;
        final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
        if (!this.world.isRemote) {
            if (this.counter % 5 == 0) {
                this.currentSuction = null;
                if (this.getStackInSlot(0).isEmpty() || this.charges >= 8) {
                    return;
                }
                boolean done = true;
                for (final Aspect aspect : this.recipe.getAspectsSortedByName()) {
                    if (this.recipeProgress.getAmount(aspect) < this.recipe.getAmount(aspect)) {
                        this.currentSuction = aspect;
                        done = false;
                        break;
                    }
                }
                if (done) {
                    this.recipeProgress = new AspectList();
                    ++this.charges;
                    this.syncTile(false);
                    this.markDirty();
                }
                else if (this.currentSuction != null) {
                    this.fill();
                }
            }
            if (!BlockStateUtils.isEnabled(this.getBlockMetadata())) {
                if (!this.activated && this.charges > 0) {
                    --this.charges;
                    final List<PotionEffect> effects = PotionUtils.getEffectsFromStack(this.getStackInSlot(0));
                    if (effects != null && !effects.isEmpty()) {
                        final int area = 1;
                        final BlockPos p = this.pos.offset(facing, 2);
                        final List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(p.getX() - area, p.getY() - area, p.getZ() - area, p.getX() + 1 + area, p.getY() + 1 + area, p.getZ() + 1 + area));
                        final boolean lifted = false;
                        if (targets.size() > 0) {
                            for (final EntityLivingBase e : targets) {
                                if (!e.isDead) {
                                    if (!e.canBeHitWithPotion()) {
                                        continue;
                                    }
                                    for (final PotionEffect potioneffect1 : effects) {
                                        final Potion potion = potioneffect1.getPotion();
                                        if (potion.isInstant()) {
                                            potion.affectEntity(null, null, e, potioneffect1.getAmplifier(), 1.0);
                                        }
                                        else {
                                            e.addPotionEffect(new PotionEffect(potion, potioneffect1.getDuration(), potioneffect1.getAmplifier()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.world.playSound(null, this.pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.25f, 2.6f + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8f);
                    this.world.addBlockEvent(this.getPos(), this.getBlockType(), 0, 0);
                    this.syncTile(false);
                    this.markDirty();
                }
                this.activated = true;
            }
            else if (this.activated) {
                this.activated = false;
            }
        }
        else if (this.venting > 0) {
            --this.venting;
            for (int a = 0; a < this.venting / 2; ++a) {
                final float fx = 0.1f - this.world.rand.nextFloat() * 0.2f;
                final float fz = 0.1f - this.world.rand.nextFloat() * 0.2f;
                final float fy = 0.1f - this.world.rand.nextFloat() * 0.2f;
                final float fx2 = (float)(this.world.rand.nextGaussian() * 0.06);
                final float fz2 = (float)(this.world.rand.nextGaussian() * 0.06);
                final float fy2 = (float)(this.world.rand.nextGaussian() * 0.06);
                FXDispatcher.INSTANCE.drawVentParticles2(this.pos.getX() + 0.5f + fx + facing.getFrontOffsetX() / 2.0f, this.pos.getY() + 0.5f + fy + facing.getFrontOffsetY() / 2.0f, this.pos.getZ() + 0.5f + fz + facing.getFrontOffsetZ() / 2.0f, fx2 + facing.getFrontOffsetX() * 0.25, fy2 + facing.getFrontOffsetY() * 0.25, fz2 + facing.getFrontOffsetZ() * 0.25, this.color, 4.0f);
            }
        }
    }
    
    private void drawFX(final EnumFacing facing, final double c) {
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i >= 0) {
            if (this.world.isRemote) {
                this.venting = 15;
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbt) {
        (this.recipe = new AspectList()).readFromNBT(nbt, "recipe");
        (this.recipeProgress = new AspectList()).readFromNBT(nbt, "progress");
        this.charges = nbt.getInteger("charges");
        this.color = nbt.getInteger("color");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbt) {
        this.recipe.writeToNBT(nbt, "recipe");
        this.recipeProgress.writeToNBT(nbt, "progress");
        nbt.setInteger("charges", this.charges);
        nbt.setInteger("color", this.color);
        return nbt;
    }
    
    @Override
    public boolean isItemValidForSlot(final int par1, final ItemStack stack) {
        return stack != null && !stack.isEmpty() && SlotPotion.isValidPotion(stack);
    }
    
    @Override
    public void setInventorySlotContents(final int par1, final ItemStack stack) {
        super.setInventorySlotContents(par1, stack);
        this.recalcAspects();
    }
    
    @Override
    public ItemStack decrStackSize(final int par1, final int par2) {
        final ItemStack stack = super.decrStackSize(par1, par2);
        this.recalcAspects();
        return stack;
    }
    
    private void recalcAspects() {
        if (this.world.isRemote) {
            return;
        }
        final ItemStack stack = this.getStackInSlot(0);
        this.color = 3355443;
        if (!this.world.isRemote) {
            if (stack == null || stack.isEmpty()) {
                this.recipe = new AspectList();
            }
            else {
                this.recipe = ConfigAspects.getPotionAspects(stack);
                this.color = this.getPotionColor(stack);
            }
            this.charges = 0;
            this.recipe = AspectHelper.cullTags(this.recipe, 10);
            this.recipeProgress = new AspectList();
            this.syncTile(false);
            this.markDirty();
        }
    }
    
    public int getPotionColor(final ItemStack itemstack) {
        final PotionType potion = PotionUtils.getPotionFromItem(itemstack);
        if (potion != null) {
            return PotionUtils.getPotionColor(potion);
        }
        return 3355443;
    }
    
    void fill() {
        final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
        TileEntity te = null;
        IEssentiaTransport ic = null;
        for (int y = 0; y <= 1; ++y) {
            for (final EnumFacing dir : EnumFacing.VALUES) {
                if (dir != facing) {
                    te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos.up(y), dir);
                    if (te != null) {
                        ic = (IEssentiaTransport)te;
                        if (ic.getEssentiaAmount(dir.getOpposite()) > 0 && ic.getSuctionAmount(dir.getOpposite()) < this.getSuctionAmount(null) && this.getSuctionAmount(null) >= ic.getMinimumSuction()) {
                            final int ess = ic.takeEssentia(this.currentSuction, 1, dir.getOpposite());
                            if (ess > 0) {
                                this.addToContainer(this.currentSuction, ess);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public int addToContainer(final Aspect tt, final int am) {
        final int ce = this.recipe.getAmount(tt) - this.recipeProgress.getAmount(tt);
        if (ce <= 0) {
            return am;
        }
        final int add = Math.min(ce, am);
        this.recipeProgress.add(tt, add);
        this.syncTile(false);
        this.markDirty();
        return am - add;
    }
    
    @Override
    public boolean takeFromContainer(final Aspect tt, final int am) {
        return false;
    }
    
    @Override
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(final Aspect tt, final int am) {
        return this.recipeProgress.getAmount(tt) >= am;
    }
    
    @Override
    public int containerContains(final Aspect tt) {
        return this.recipeProgress.getAmount(tt);
    }
    
    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return true;
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return face != BlockStateUtils.getFacing(this.getBlockMetadata());
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return face != BlockStateUtils.getFacing(this.getBlockMetadata());
    }
    
    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return false;
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
        this.currentSuction = aspect;
    }
    
    @Override
    public Aspect getSuctionType(final EnumFacing loc) {
        return this.currentSuction;
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing loc) {
        return (this.currentSuction != null) ? 128 : 0;
    }
    
    @Override
    public Aspect getEssentiaType(final EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getEssentiaAmount(final EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return (this.canOutputTo(face) && this.takeFromContainer(aspect, amount)) ? amount : 0;
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return this.canInputFrom(face) ? (amount - this.addToContainer(aspect, amount)) : 0;
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public AspectList getAspects() {
        return this.recipeProgress;
    }
    
    @Override
    public void setAspects(final AspectList aspects) {
        this.recipeProgress = aspects;
    }
}
