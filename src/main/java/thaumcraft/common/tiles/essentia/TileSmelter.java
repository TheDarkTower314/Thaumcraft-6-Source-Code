package thaumcraft.common.tiles.essentia;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.essentia.BlockSmelter;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.tiles.devices.TileBellows;


public class TileSmelter extends TileThaumcraftInventory
{
    private static int[] slots_bottom;
    private static int[] slots_top;
    private static int[] slots_sides;
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
        aspects = new AspectList();
        maxVis = 256;
        smeltTime = 100;
        speedBoost = false;
        count = 0;
        bellows = -1;
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        furnaceBurnTime = nbttagcompound.getShort("BurnTime");
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("BurnTime", (short) furnaceBurnTime);
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        speedBoost = nbtCompound.getBoolean("speedBoost");
        furnaceCookTime = nbtCompound.getShort("CookTime");
        currentItemBurnTime = TileEntityFurnace.getItemBurnTime(getStackInSlot(1));
        aspects.readFromNBT(nbtCompound);
        vis = aspects.visSize();
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtCompound) {
        nbtCompound = super.writeToNBT(nbtCompound);
        nbtCompound.setBoolean("speedBoost", speedBoost);
        nbtCompound.setShort("CookTime", (short) furnaceCookTime);
        aspects.writeToNBT(nbtCompound);
        return nbtCompound;
    }
    
    @Override
    public void update() {
        super.update();
        boolean flag = furnaceBurnTime > 0;
        boolean flag2 = false;
        ++count;
        if (furnaceBurnTime > 0) {
            --furnaceBurnTime;
        }
        if (world != null && !world.isRemote) {
            if (bellows < 0) {
                checkNeighbours();
            }
            int speed = getSpeed();
            if (speedBoost) {
                speed *= (int)0.8;
            }
            if (count % speed == 0 && aspects.size() > 0) {
                for (Aspect aspect : aspects.getAspects()) {
                    if (aspects.getAmount(aspect) > 0 && TileAlembic.processAlembics(getWorld(), getPos(), aspect)) {
                        takeFromContainer(aspect, 1);
                        break;
                    }
                }
                for (EnumFacing face : EnumFacing.HORIZONTALS) {
                    if (BlockStateUtils.getFacing(getBlockMetadata()) != face) {
                        IBlockState aux = world.getBlockState(getPos().offset(face));
                        if (aux.getBlock() == BlocksTC.smelterAux && BlockStateUtils.getFacing(aux) == face.getOpposite()) {
                            for (Aspect aspect2 : aspects.getAspects()) {
                                if (aspects.getAmount(aspect2) > 0 && TileAlembic.processAlembics(getWorld(), getPos().offset(face), aspect2)) {
                                    takeFromContainer(aspect2, 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (furnaceBurnTime == 0) {
                if (canSmelt()) {
                    int itemBurnTime = TileEntityFurnace.getItemBurnTime(getStackInSlot(1));
                    furnaceBurnTime = itemBurnTime;
                    currentItemBurnTime = itemBurnTime;
                    if (furnaceBurnTime > 0) {
                        BlockSmelter.setFurnaceState(world, getPos(), true);
                        flag2 = true;
                        speedBoost = false;
                        ItemStack itemstack = getStackInSlot(1);
                        if (!itemstack.isEmpty()) {
                            if (itemstack.isItemEqual(new ItemStack(ItemsTC.alumentum))) {
                                speedBoost = true;
                            }
                            Item item = itemstack.getItem();
                            itemstack.shrink(1);
                            if (itemstack.isEmpty()) {
                                ItemStack item2 = item.getContainerItem(itemstack);
                                setInventorySlotContents(1, item2);
                            }
                        }
                    }
                    else {
                        BlockSmelter.setFurnaceState(world, getPos(), false);
                    }
                }
                else {
                    BlockSmelter.setFurnaceState(world, getPos(), false);
                }
            }
            if (BlockStateUtils.isEnabled(getBlockMetadata()) && canSmelt()) {
                ++furnaceCookTime;
                if (furnaceCookTime >= smeltTime) {
                    furnaceCookTime = 0;
                    smeltItem();
                    flag2 = true;
                }
            }
            else {
                furnaceCookTime = 0;
            }
            if (flag != furnaceBurnTime > 0) {
                flag2 = true;
            }
        }
        if (flag2) {
            markDirty();
        }
    }
    
    private boolean canSmelt() {
        if (getStackInSlot(0).isEmpty()) {
            return false;
        }
        AspectList al = ThaumcraftCraftingManager.getObjectTags(getStackInSlot(0));
        if (al == null || al.size() == 0) {
            return false;
        }
        int vs = al.visSize();
        if (vs > maxVis - vis) {
            return false;
        }
        smeltTime = (int)(vs * 2 * (1.0f - 0.125f * bellows));
        return true;
    }
    
    public void checkNeighbours() {
        EnumFacing[] faces = EnumFacing.HORIZONTALS;
        try {
            if (BlockStateUtils.getFacing(getBlockMetadata()) == EnumFacing.NORTH) {
                faces = new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST };
            }
            if (BlockStateUtils.getFacing(getBlockMetadata()) == EnumFacing.SOUTH) {
                faces = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST };
            }
            if (BlockStateUtils.getFacing(getBlockMetadata()) == EnumFacing.EAST) {
                faces = new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.WEST };
            }
            if (BlockStateUtils.getFacing(getBlockMetadata()) == EnumFacing.WEST) {
                faces = new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH };
            }
        }
        catch (Exception ex) {}
        bellows = TileBellows.getBellows(world, pos, faces);
    }
    
    private int getType() {
        return (getBlockType() == BlocksTC.smelterBasic) ? 0 : ((getBlockType() == BlocksTC.smelterThaumium) ? 1 : 2);
    }
    
    private float getEfficiency() {
        float efficiency = 0.8f;
        if (getType() == 1) {
            efficiency = 0.9f;
        }
        if (getType() == 2) {
            efficiency = 0.95f;
        }
        return efficiency;
    }
    
    private int getSpeed() {
        int speed = 20 - ((getType() == 1) ? 10 : 5);
        return speed;
    }
    
    public void smeltItem() {
        if (canSmelt()) {
            int flux = 0;
            AspectList al = ThaumcraftCraftingManager.getObjectTags(getStackInSlot(0));
            for (Aspect a : al.getAspects()) {
                if (getEfficiency() < 1.0f) {
                    for (int qq = al.getAmount(a), q = 0; q < qq; ++q) {
                        if (world.rand.nextFloat() > ((a == Aspect.FLUX) ? (getEfficiency() * 0.66f) : getEfficiency())) {
                            al.reduce(a, 1);
                            ++flux;
                        }
                    }
                }
                aspects.add(a, al.getAmount(a));
            }
            if (flux > 0) {
                int pp = 0;
                int c = 0;
            Label_0155:
                while (c < flux) {
                    while (true) {
                        for (EnumFacing face : EnumFacing.HORIZONTALS) {
                            if (BlockStateUtils.getFacing(getBlockMetadata()) != face) {
                                IBlockState vent = world.getBlockState(getPos().offset(face));
                                if (vent.getBlock() == BlocksTC.smelterVent && BlockStateUtils.getFacing(vent) == face.getOpposite() && world.rand.nextFloat() < 0.333) {
                                    world.addBlockEvent(getPos(), getBlockType(), 1, face.getOpposite().ordinal());
                                    ++c;
                                    continue Label_0155;
                                }
                            }
                        }
                        ++pp;
                        continue;
                    }
                }
                AuraHelper.polluteAura(getWorld(), getPos(), (float)pp, true);
            }
            vis = aspects.visSize();
            getStackInSlot(0).shrink(1);
            if (getStackInSlot(0).getCount() <= 0) {
                setInventorySlotContents(0, ItemStack.EMPTY);
            }
        }
    }
    
    public static boolean isItemFuel(ItemStack par0ItemStack) {
        return TileEntityFurnace.getItemBurnTime(par0ItemStack) > 0;
    }
    
    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack2) {
        if (par1 == 0) {
            AspectList al = ThaumcraftCraftingManager.getObjectTags(stack2);
            if (al != null && al.size() > 0) {
                return true;
            }
        }
        return par1 == 1 && isItemFuel(stack2);
    }
    
    @Override
    public int[] getSlotsForFace(EnumFacing par1) {
        return (par1 == EnumFacing.DOWN) ? TileSmelter.slots_bottom : ((par1 == EnumFacing.UP) ? TileSmelter.slots_top : TileSmelter.slots_sides);
    }
    
    @Override
    public boolean canInsertItem(int par1, ItemStack stack2, EnumFacing par3) {
        return par3 != EnumFacing.UP && isItemValidForSlot(par1, stack2);
    }
    
    @Override
    public boolean canExtractItem(int par1, ItemStack stack2, EnumFacing par3) {
        return par3 != EnumFacing.UP || par1 != 1 || stack2.getItem() == Items.BUCKET;
    }
    
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (aspects != null && aspects.getAmount(tag) >= amount) {
            aspects.remove(tag, amount);
            vis = aspects.visSize();
            markDirty();
            return true;
        }
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int par1) {
        if (smeltTime <= 0) {
            smeltTime = 1;
        }
        return furnaceCookTime * par1 / smeltTime;
    }
    
    @SideOnly(Side.CLIENT)
    public int getVisScaled(int par1) {
        return vis * par1 / maxVis;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int par1) {
        if (currentItemBurnTime == 0) {
            currentItemBurnTime = 200;
        }
        return furnaceBurnTime * par1 / currentItemBurnTime;
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (world != null) {
            world.checkLightFor(EnumSkyBlock.BLOCK, pos);
        }
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 1) {
            if (world.isRemote) {
                EnumFacing d = EnumFacing.VALUES[j];
                world.playSound(getPos().getX() + 0.5 + d.getOpposite().getFrontOffsetX(), getPos().getY() + 0.5, getPos().getZ() + 0.5 + d.getOpposite().getFrontOffsetZ(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.25f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f, true);
                for (int a = 0; a < 4; ++a) {
                    float fx = 0.1f - world.rand.nextFloat() * 0.2f;
                    float fz = 0.1f - world.rand.nextFloat() * 0.2f;
                    float fy = 0.1f - world.rand.nextFloat() * 0.2f;
                    float fx2 = 0.1f - world.rand.nextFloat() * 0.2f;
                    float fz2 = 0.1f - world.rand.nextFloat() * 0.2f;
                    float fy2 = 0.1f - world.rand.nextFloat() * 0.2f;
                    int color = 11184810;
                    FXDispatcher.INSTANCE.drawVentParticles(getPos().getX() + 0.5f + fx + d.getOpposite().getFrontOffsetX(), getPos().getY() + 0.5f + fy, getPos().getZ() + 0.5f + fz + d.getOpposite().getFrontOffsetZ(), d.getOpposite().getFrontOffsetX() / 4.0f + fx2, d.getOpposite().getFrontOffsetY() / 4.0f + fy2, d.getOpposite().getFrontOffsetZ() / 4.0f + fz2, color);
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
