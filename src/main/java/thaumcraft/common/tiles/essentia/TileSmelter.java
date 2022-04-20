// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.essentia;

import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.init.Items;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.tiles.devices.TileBellows;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import net.minecraft.item.Item;
import net.minecraft.block.state.IBlockState;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.essentia.BlockSmelter;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileSmelter extends TileThaumcraftInventory
{
    private static final int[] slots_bottom;
    private static final int[] slots_top;
    private static final int[] slots_sides;
    public AspectList aspects;
    public int vis;
    private int maxVis;
    public int smeltTime;
    boolean speedBoost;
    public int furnaceBurnTime;
    public int currentItemBurnTime;
    public int furnaceCookTime;
    int count;
    int bellows;
    
    public TileSmelter() {
        super(2);
        this.aspects = new AspectList();
        this.maxVis = 256;
        this.smeltTime = 100;
        this.speedBoost = false;
        this.count = 0;
        this.bellows = -1;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        this.furnaceBurnTime = nbttagcompound.getShort("BurnTime");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("BurnTime", (short)this.furnaceBurnTime);
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        this.speedBoost = nbtCompound.getBoolean("speedBoost");
        this.furnaceCookTime = nbtCompound.getShort("CookTime");
        this.currentItemBurnTime = TileEntityFurnace.getItemBurnTime(this.getStackInSlot(1));
        this.aspects.readFromNBT(nbtCompound);
        this.vis = this.aspects.visSize();
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtCompound) {
        nbtCompound = super.writeToNBT(nbtCompound);
        nbtCompound.setBoolean("speedBoost", this.speedBoost);
        nbtCompound.setShort("CookTime", (short)this.furnaceCookTime);
        this.aspects.writeToNBT(nbtCompound);
        return nbtCompound;
    }
    
    @Override
    public void update() {
        super.update();
        final boolean flag = this.furnaceBurnTime > 0;
        boolean flag2 = false;
        ++this.count;
        if (this.furnaceBurnTime > 0) {
            --this.furnaceBurnTime;
        }
        if (this.world != null && !this.world.isRemote) {
            if (this.bellows < 0) {
                this.checkNeighbours();
            }
            int speed = this.getSpeed();
            if (this.speedBoost) {
                speed *= (int)0.8;
            }
            if (this.count % speed == 0 && this.aspects.size() > 0) {
                for (final Aspect aspect : this.aspects.getAspects()) {
                    if (this.aspects.getAmount(aspect) > 0 && TileAlembic.processAlembics(this.getWorld(), this.getPos(), aspect)) {
                        this.takeFromContainer(aspect, 1);
                        break;
                    }
                }
                for (final EnumFacing face : EnumFacing.HORIZONTALS) {
                    if (BlockStateUtils.getFacing(this.getBlockMetadata()) != face) {
                        final IBlockState aux = this.world.getBlockState(this.getPos().offset(face));
                        if (aux.getBlock() == BlocksTC.smelterAux && BlockStateUtils.getFacing(aux) == face.getOpposite()) {
                            for (final Aspect aspect2 : this.aspects.getAspects()) {
                                if (this.aspects.getAmount(aspect2) > 0 && TileAlembic.processAlembics(this.getWorld(), this.getPos().offset(face), aspect2)) {
                                    this.takeFromContainer(aspect2, 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (this.furnaceBurnTime == 0) {
                if (this.canSmelt()) {
                    final int itemBurnTime = TileEntityFurnace.getItemBurnTime(this.getStackInSlot(1));
                    this.furnaceBurnTime = itemBurnTime;
                    this.currentItemBurnTime = itemBurnTime;
                    if (this.furnaceBurnTime > 0) {
                        BlockSmelter.setFurnaceState(this.world, this.getPos(), true);
                        flag2 = true;
                        this.speedBoost = false;
                        final ItemStack itemstack = this.getStackInSlot(1);
                        if (!itemstack.isEmpty()) {
                            if (itemstack.isItemEqual(new ItemStack(ItemsTC.alumentum))) {
                                this.speedBoost = true;
                            }
                            final Item item = itemstack.getItem();
                            itemstack.shrink(1);
                            if (itemstack.isEmpty()) {
                                final ItemStack item2 = item.getContainerItem(itemstack);
                                this.setInventorySlotContents(1, item2);
                            }
                        }
                    }
                    else {
                        BlockSmelter.setFurnaceState(this.world, this.getPos(), false);
                    }
                }
                else {
                    BlockSmelter.setFurnaceState(this.world, this.getPos(), false);
                }
            }
            if (BlockStateUtils.isEnabled(this.getBlockMetadata()) && this.canSmelt()) {
                ++this.furnaceCookTime;
                if (this.furnaceCookTime >= this.smeltTime) {
                    this.furnaceCookTime = 0;
                    this.smeltItem();
                    flag2 = true;
                }
            }
            else {
                this.furnaceCookTime = 0;
            }
            if (flag != this.furnaceBurnTime > 0) {
                flag2 = true;
            }
        }
        if (flag2) {
            this.markDirty();
        }
    }
    
    private boolean canSmelt() {
        if (this.getStackInSlot(0).isEmpty()) {
            return false;
        }
        final AspectList al = ThaumcraftCraftingManager.getObjectTags(this.getStackInSlot(0));
        if (al == null || al.size() == 0) {
            return false;
        }
        final int vs = al.visSize();
        if (vs > this.maxVis - this.vis) {
            return false;
        }
        this.smeltTime = (int)(vs * 2 * (1.0f - 0.125f * this.bellows));
        return true;
    }
    
    public void checkNeighbours() {
        EnumFacing[] faces = EnumFacing.HORIZONTALS;
        try {
            if (BlockStateUtils.getFacing(this.getBlockMetadata()) == EnumFacing.NORTH) {
                faces = new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST };
            }
            if (BlockStateUtils.getFacing(this.getBlockMetadata()) == EnumFacing.SOUTH) {
                faces = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST };
            }
            if (BlockStateUtils.getFacing(this.getBlockMetadata()) == EnumFacing.EAST) {
                faces = new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.WEST };
            }
            if (BlockStateUtils.getFacing(this.getBlockMetadata()) == EnumFacing.WEST) {
                faces = new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH };
            }
        }
        catch (final Exception ex) {}
        this.bellows = TileBellows.getBellows(this.world, this.pos, faces);
    }
    
    private int getType() {
        return (this.getBlockType() == BlocksTC.smelterBasic) ? 0 : ((this.getBlockType() == BlocksTC.smelterThaumium) ? 1 : 2);
    }
    
    private float getEfficiency() {
        float efficiency = 0.8f;
        if (this.getType() == 1) {
            efficiency = 0.9f;
        }
        if (this.getType() == 2) {
            efficiency = 0.95f;
        }
        return efficiency;
    }
    
    private int getSpeed() {
        final int speed = 20 - ((this.getType() == 1) ? 10 : 5);
        return speed;
    }
    
    public void smeltItem() {
        if (this.canSmelt()) {
            int flux = 0;
            final AspectList al = ThaumcraftCraftingManager.getObjectTags(this.getStackInSlot(0));
            for (final Aspect a : al.getAspects()) {
                if (this.getEfficiency() < 1.0f) {
                    for (int qq = al.getAmount(a), q = 0; q < qq; ++q) {
                        if (this.world.rand.nextFloat() > ((a == Aspect.FLUX) ? (this.getEfficiency() * 0.66f) : this.getEfficiency())) {
                            al.reduce(a, 1);
                            ++flux;
                        }
                    }
                }
                this.aspects.add(a, al.getAmount(a));
            }
            if (flux > 0) {
                int pp = 0;
                int c = 0;
            Label_0155:
                while (c < flux) {
                    while (true) {
                        for (final EnumFacing face : EnumFacing.HORIZONTALS) {
                            if (BlockStateUtils.getFacing(this.getBlockMetadata()) != face) {
                                final IBlockState vent = this.world.getBlockState(this.getPos().offset(face));
                                if (vent.getBlock() == BlocksTC.smelterVent && BlockStateUtils.getFacing(vent) == face.getOpposite() && this.world.rand.nextFloat() < 0.333) {
                                    this.world.addBlockEvent(this.getPos(), this.getBlockType(), 1, face.getOpposite().ordinal());
                                    ++c;
                                    continue Label_0155;
                                }
                            }
                        }
                        ++pp;
                        continue;
                    }
                }
                AuraHelper.polluteAura(this.getWorld(), this.getPos(), (float)pp, true);
            }
            this.vis = this.aspects.visSize();
            this.getStackInSlot(0).shrink(1);
            if (this.getStackInSlot(0).getCount() <= 0) {
                this.setInventorySlotContents(0, ItemStack.EMPTY);
            }
        }
    }
    
    public static boolean isItemFuel(final ItemStack par0ItemStack) {
        return TileEntityFurnace.getItemBurnTime(par0ItemStack) > 0;
    }
    
    @Override
    public boolean isItemValidForSlot(final int par1, final ItemStack stack2) {
        if (par1 == 0) {
            final AspectList al = ThaumcraftCraftingManager.getObjectTags(stack2);
            if (al != null && al.size() > 0) {
                return true;
            }
        }
        return par1 == 1 && isItemFuel(stack2);
    }
    
    @Override
    public int[] getSlotsForFace(final EnumFacing par1) {
        return (par1 == EnumFacing.DOWN) ? TileSmelter.slots_bottom : ((par1 == EnumFacing.UP) ? TileSmelter.slots_top : TileSmelter.slots_sides);
    }
    
    @Override
    public boolean canInsertItem(final int par1, final ItemStack stack2, final EnumFacing par3) {
        return par3 != EnumFacing.UP && this.isItemValidForSlot(par1, stack2);
    }
    
    @Override
    public boolean canExtractItem(final int par1, final ItemStack stack2, final EnumFacing par3) {
        return par3 != EnumFacing.UP || par1 != 1 || stack2.getItem() == Items.BUCKET;
    }
    
    public boolean takeFromContainer(final Aspect tag, final int amount) {
        if (this.aspects != null && this.aspects.getAmount(tag) >= amount) {
            this.aspects.remove(tag, amount);
            this.vis = this.aspects.visSize();
            this.markDirty();
            return true;
        }
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public int getCookProgressScaled(final int par1) {
        if (this.smeltTime <= 0) {
            this.smeltTime = 1;
        }
        return this.furnaceCookTime * par1 / this.smeltTime;
    }
    
    @SideOnly(Side.CLIENT)
    public int getVisScaled(final int par1) {
        return this.vis * par1 / this.maxVis;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(final int par1) {
        if (this.currentItemBurnTime == 0) {
            this.currentItemBurnTime = 200;
        }
        return this.furnaceBurnTime * par1 / this.currentItemBurnTime;
    }
    
    @Override
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (this.world != null) {
            this.world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
        }
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 1) {
            if (this.world.isRemote) {
                final EnumFacing d = EnumFacing.VALUES[j];
                this.world.playSound(this.getPos().getX() + 0.5 + d.getOpposite().getFrontOffsetX(), this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5 + d.getOpposite().getFrontOffsetZ(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.25f, 2.6f + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8f, true);
                for (int a = 0; a < 4; ++a) {
                    final float fx = 0.1f - this.world.rand.nextFloat() * 0.2f;
                    final float fz = 0.1f - this.world.rand.nextFloat() * 0.2f;
                    final float fy = 0.1f - this.world.rand.nextFloat() * 0.2f;
                    final float fx2 = 0.1f - this.world.rand.nextFloat() * 0.2f;
                    final float fz2 = 0.1f - this.world.rand.nextFloat() * 0.2f;
                    final float fy2 = 0.1f - this.world.rand.nextFloat() * 0.2f;
                    final int color = 11184810;
                    FXDispatcher.INSTANCE.drawVentParticles(this.getPos().getX() + 0.5f + fx + d.getOpposite().getFrontOffsetX(), this.getPos().getY() + 0.5f + fy, this.getPos().getZ() + 0.5f + fz + d.getOpposite().getFrontOffsetZ(), d.getOpposite().getFrontOffsetX() / 4.0f + fx2, d.getOpposite().getFrontOffsetY() / 4.0f + fy2, d.getOpposite().getFrontOffsetZ() / 4.0f + fz2, color);
                }
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    static {
        slots_bottom = new int[] { 1 };
        slots_top = new int[0];
        slots_sides = new int[] { 0 };
    }
}
