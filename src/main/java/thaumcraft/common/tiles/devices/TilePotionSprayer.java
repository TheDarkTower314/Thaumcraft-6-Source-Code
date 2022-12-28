package thaumcraft.common.tiles.devices;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.container.slot.SlotPotion;
import thaumcraft.common.lib.utils.BlockStateUtils;
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
        recipe = new AspectList();
        recipeProgress = new AspectList();
        charges = 0;
        color = 0;
        counter = 0;
        activated = false;
        venting = 0;
        currentSuction = null;
    }
    
    @Override
    public void update() {
        super.update();
        ++counter;
        EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
        if (!world.isRemote) {
            if (counter % 5 == 0) {
                currentSuction = null;
                if (getStackInSlot(0).isEmpty() || charges >= 8) {
                    return;
                }
                boolean done = true;
                for (Aspect aspect : recipe.getAspectsSortedByName()) {
                    if (recipeProgress.getAmount(aspect) < recipe.getAmount(aspect)) {
                        currentSuction = aspect;
                        done = false;
                        break;
                    }
                }
                if (done) {
                    recipeProgress = new AspectList();
                    ++charges;
                    syncTile(false);
                    markDirty();
                }
                else if (currentSuction != null) {
                    fill();
                }
            }
            if (!BlockStateUtils.isEnabled(getBlockMetadata())) {
                if (!activated && charges > 0) {
                    --charges;
                    List<PotionEffect> effects = PotionUtils.getEffectsFromStack(getStackInSlot(0));
                    if (effects != null && !effects.isEmpty()) {
                        int area = 1;
                        BlockPos p = pos.offset(facing, 2);
                        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(p.getX() - area, p.getY() - area, p.getZ() - area, p.getX() + 1 + area, p.getY() + 1 + area, p.getZ() + 1 + area));
                        boolean lifted = false;
                        if (targets.size() > 0) {
                            for (EntityLivingBase e : targets) {
                                if (!e.isDead) {
                                    if (!e.canBeHitWithPotion()) {
                                        continue;
                                    }
                                    for (PotionEffect potioneffect1 : effects) {
                                        Potion potion = potioneffect1.getPotion();
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
                    world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.25f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f);
                    world.addBlockEvent(getPos(), getBlockType(), 0, 0);
                    syncTile(false);
                    markDirty();
                }
                activated = true;
            }
            else if (activated) {
                activated = false;
            }
        }
        else if (venting > 0) {
            --venting;
            for (int a = 0; a < venting / 2; ++a) {
                float fx = 0.1f - world.rand.nextFloat() * 0.2f;
                float fz = 0.1f - world.rand.nextFloat() * 0.2f;
                float fy = 0.1f - world.rand.nextFloat() * 0.2f;
                float fx2 = (float)(world.rand.nextGaussian() * 0.06);
                float fz2 = (float)(world.rand.nextGaussian() * 0.06);
                float fy2 = (float)(world.rand.nextGaussian() * 0.06);
                FXDispatcher.INSTANCE.drawVentParticles2(pos.getX() + 0.5f + fx + facing.getFrontOffsetX() / 2.0f, pos.getY() + 0.5f + fy + facing.getFrontOffsetY() / 2.0f, pos.getZ() + 0.5f + fz + facing.getFrontOffsetZ() / 2.0f, fx2 + facing.getFrontOffsetX() * 0.25, fy2 + facing.getFrontOffsetY() * 0.25, fz2 + facing.getFrontOffsetZ() * 0.25, color, 4.0f);
            }
        }
    }
    
    private void drawFX(EnumFacing facing, double c) {
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i >= 0) {
            if (world.isRemote) {
                venting = 15;
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbt) {
        (recipe = new AspectList()).readFromNBT(nbt, "recipe");
        (recipeProgress = new AspectList()).readFromNBT(nbt, "progress");
        charges = nbt.getInteger("charges");
        color = nbt.getInteger("color");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
        recipe.writeToNBT(nbt, "recipe");
        recipeProgress.writeToNBT(nbt, "progress");
        nbt.setInteger("charges", charges);
        nbt.setInteger("color", color);
        return nbt;
    }
    
    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack) {
        return stack != null && !stack.isEmpty() && SlotPotion.isValidPotion(stack);
    }
    
    @Override
    public void setInventorySlotContents(int par1, ItemStack stack) {
        super.setInventorySlotContents(par1, stack);
        recalcAspects();
    }
    
    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        ItemStack stack = super.decrStackSize(par1, par2);
        recalcAspects();
        return stack;
    }
    
    private void recalcAspects() {
        if (world.isRemote) {
            return;
        }
        ItemStack stack = getStackInSlot(0);
        color = 3355443;
        if (!world.isRemote) {
            if (stack == null || stack.isEmpty()) {
                recipe = new AspectList();
            }
            else {
                recipe = ConfigAspects.getPotionAspects(stack);
                color = getPotionColor(stack);
            }
            charges = 0;
            recipe = AspectHelper.cullTags(recipe, 10);
            recipeProgress = new AspectList();
            syncTile(false);
            markDirty();
        }
    }
    
    public int getPotionColor(ItemStack itemstack) {
        PotionType potion = PotionUtils.getPotionFromItem(itemstack);
        if (potion != null) {
            return PotionUtils.getPotionColor(potion);
        }
        return 3355443;
    }
    
    void fill() {
        EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
        TileEntity te = null;
        IEssentiaTransport ic = null;
        for (int y = 0; y <= 1; ++y) {
            for (EnumFacing dir : EnumFacing.VALUES) {
                if (dir != facing) {
                    te = ThaumcraftApiHelper.getConnectableTile(world, pos.up(y), dir);
                    if (te != null) {
                        ic = (IEssentiaTransport)te;
                        if (ic.getEssentiaAmount(dir.getOpposite()) > 0 && ic.getSuctionAmount(dir.getOpposite()) < getSuctionAmount(null) && getSuctionAmount(null) >= ic.getMinimumSuction()) {
                            int ess = ic.takeEssentia(currentSuction, 1, dir.getOpposite());
                            if (ess > 0) {
                                addToContainer(currentSuction, ess);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public int addToContainer(Aspect tt, int am) {
        int ce = recipe.getAmount(tt) - recipeProgress.getAmount(tt);
        if (ce <= 0) {
            return am;
        }
        int add = Math.min(ce, am);
        recipeProgress.add(tt, add);
        syncTile(false);
        markDirty();
        return am - add;
    }
    
    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        return false;
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tt, int am) {
        return recipeProgress.getAmount(tt) >= am;
    }
    
    @Override
    public int containerContains(Aspect tt) {
        return recipeProgress.getAmount(tt);
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    @Override
    public boolean isConnectable(EnumFacing face) {
        return face != BlockStateUtils.getFacing(getBlockMetadata());
    }
    
    @Override
    public boolean canInputFrom(EnumFacing face) {
        return face != BlockStateUtils.getFacing(getBlockMetadata());
    }
    
    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
        currentSuction = aspect;
    }
    
    @Override
    public Aspect getSuctionType(EnumFacing loc) {
        return currentSuction;
    }
    
    @Override
    public int getSuctionAmount(EnumFacing loc) {
        return (currentSuction != null) ? 128 : 0;
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
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return (canOutputTo(face) && takeFromContainer(aspect, amount)) ? amount : 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return canInputFrom(face) ? (amount - addToContainer(aspect, amount)) : 0;
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public AspectList getAspects() {
        return recipeProgress;
    }
    
    @Override
    public void setAspects(AspectList aspects) {
        recipeProgress = aspects;
    }
}
