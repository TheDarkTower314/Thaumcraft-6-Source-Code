// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import thaumcraft.api.crafting.IThaumcraftRecipe;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import thaumcraft.common.blocks.IBlockFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.ThaumcraftApiHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.InventoryFake;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.util.EnumFacing;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagList;
import java.util.Iterator;
import thaumcraft.api.ThaumcraftApi;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.inventory.Container;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.aspects.Aspect;
import java.util.ArrayList;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.util.ITickable;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileThaumatorium extends TileThaumcraftInventory implements IAspectContainer, IEssentiaTransport, ITickable
{
    public AspectList essentia;
    public ArrayList<Integer> recipeHash;
    public ArrayList<AspectList> recipeEssentia;
    public ArrayList<String> recipePlayer;
    public int currentCraft;
    public int maxRecipes;
    public Aspect currentSuction;
    int venting;
    int counter;
    boolean heated;
    CrucibleRecipe currentRecipe;
    public Container eventHandler;
    public ArrayList<CrucibleRecipe> recipes;
    
    public TileThaumatorium() {
        super(1);
        essentia = new AspectList();
        recipeHash = new ArrayList<Integer>();
        recipeEssentia = new ArrayList<AspectList>();
        recipePlayer = new ArrayList<String>();
        currentCraft = -1;
        maxRecipes = 1;
        currentSuction = null;
        venting = 0;
        counter = 0;
        heated = false;
        currentRecipe = null;
        recipes = new ArrayList<CrucibleRecipe>();
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX() - 0.1, getPos().getY() - 0.1, getPos().getZ() - 0.1, getPos().getX() + 1.1, getPos().getY() + 2.1, getPos().getZ() + 1.1);
    }
    
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        essentia.readFromNBT(nbttagcompound);
        maxRecipes = nbttagcompound.getByte("maxrec");
        recipeEssentia = new ArrayList<AspectList>();
        recipeHash = new ArrayList<Integer>();
        recipePlayer = new ArrayList<String>();
        final int[] hashes = nbttagcompound.getIntArray("recipes");
        if (hashes != null) {
            for (final int hash : hashes) {
                final CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(hash);
                if (recipe != null) {
                    recipeEssentia.add(recipe.getAspects().copy());
                    recipePlayer.add("");
                    recipeHash.add(hash);
                }
            }
        }
    }
    
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("maxrec", (byte) maxRecipes);
        essentia.writeToNBT(nbttagcompound);
        final int[] hashes = new int[recipeHash.size()];
        int a = 0;
        for (final Integer i : recipeHash) {
            hashes[a] = i;
            ++a;
        }
        nbttagcompound.setIntArray("recipes", hashes);
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        final NBTTagList nbttaglist2 = nbtCompound.getTagList("OutputPlayer", 8);
        for (int a = 0; a < nbttaglist2.tagCount(); ++a) {
            if (recipePlayer.size() > a) {
                recipePlayer.set(a, nbttaglist2.getStringTagAt(a));
            }
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        final NBTTagList nbttaglist2 = new NBTTagList();
        if (recipePlayer.size() > 0) {
            for (int a = 0; a < recipePlayer.size(); ++a) {
                if (recipePlayer.get(a) != null) {
                    final NBTTagString nbttagcompound1 = new NBTTagString(recipePlayer.get(a));
                    nbttaglist2.appendTag(nbttagcompound1);
                }
            }
        }
        nbtCompound.setTag("OutputPlayer", nbttaglist2);
        return nbtCompound;
    }
    
    boolean checkHeat() {
        final Material mat = world.getBlockState(pos.down(2)).getMaterial();
        final Block bi = world.getBlockState(pos.down(2)).getBlock();
        return mat == Material.LAVA || mat == Material.FIRE || BlocksTC.nitor.containsValue(bi) || bi == Blocks.MAGMA;
    }
    
    public ItemStack getCurrentOutputRecipe() {
        ItemStack out = ItemStack.EMPTY;
        if (currentCraft >= 0 && recipeHash != null && recipeHash.size() > 0) {
            final CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(recipeHash.get(currentCraft));
            if (recipe != null) {
                out = recipe.getRecipeOutput().copy();
            }
        }
        return out;
    }
    
    @Override
    public void update() {
        if (!world.isRemote) {
            if (counter == 0 || counter % 40 == 0) {
                heated = checkHeat();
                getUpgrades();
            }
            ++counter;
            if (heated && !gettingPower() && counter % 5 == 0 && recipeHash != null && recipeHash.size() > 0) {
                if (getStackInSlot(0).isEmpty()) {
                    currentSuction = null;
                    return;
                }
                if (currentCraft < 0 || currentCraft >= recipeHash.size() || currentRecipe == null || !currentRecipe.catalystMatches(getStackInSlot(0))) {
                    for (int a = 0; a < recipeHash.size(); ++a) {
                        final CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(recipeHash.get(a));
                        if (recipe.catalystMatches(getStackInSlot(0))) {
                            currentCraft = a;
                            currentRecipe = recipe;
                            break;
                        }
                    }
                }
                if (currentCraft < 0 || currentCraft >= recipeHash.size()) {
                    return;
                }
                boolean done = true;
                currentSuction = null;
                for (final Aspect aspect : recipeEssentia.get(currentCraft).getAspectsSortedByName()) {
                    if (essentia.getAmount(aspect) < recipeEssentia.get(currentCraft).getAmount(aspect)) {
                        currentSuction = aspect;
                        done = false;
                        break;
                    }
                }
                if (done) {
                    completeRecipe();
                }
                else if (currentSuction != null) {
                    fill();
                }
            }
        }
        else if (venting > 0) {
            --venting;
            final float fx = 0.1f - world.rand.nextFloat() * 0.2f;
            final float fz = 0.1f - world.rand.nextFloat() * 0.2f;
            final float fy = 0.1f - world.rand.nextFloat() * 0.2f;
            final float fx2 = 0.1f - world.rand.nextFloat() * 0.2f;
            final float fz2 = 0.1f - world.rand.nextFloat() * 0.2f;
            final float fy2 = 0.1f - world.rand.nextFloat() * 0.2f;
            final int color = 16777215;
            final EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
            FXDispatcher.INSTANCE.drawVentParticles(pos.getX() + 0.5f + fx + facing.getFrontOffsetX() / 2.0f, pos.getY() + 0.5f + fy, pos.getZ() + 0.5f + fz + facing.getFrontOffsetZ() / 2.0f, facing.getFrontOffsetX() / 4.0f + fx2, fy2, facing.getFrontOffsetZ() / 4.0f + fz2, color);
        }
    }
    
    private void completeRecipe() {
        if (currentRecipe != null && currentCraft < recipeHash.size() && currentRecipe.matches(essentia, getStackInSlot(0)) && decrStackSize(0, 1) != null) {
            essentia = new AspectList();
            final ItemStack dropped = getCurrentOutputRecipe();
            final EntityPlayer p = world.getPlayerEntityByName(recipePlayer.get(currentCraft));
            if (p != null) {
                FMLCommonHandler.instance().firePlayerCraftingEvent(p, dropped, new InventoryFake(getStackInSlot(0)));
            }
            final EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
            InventoryUtils.ejectStackAt(getWorld(), getPos(), facing, dropped);
            world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.25f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f);
            currentCraft = -1;
            syncTile(false);
            markDirty();
        }
    }
    
    void fill() {
        final EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
        TileEntity te = null;
        IEssentiaTransport ic = null;
        for (int y = 0; y <= 1; ++y) {
            for (final EnumFacing dir : EnumFacing.VALUES) {
                if (dir != facing && dir != EnumFacing.DOWN) {
                    if (y != 0 || dir != EnumFacing.UP) {
                        te = ThaumcraftApiHelper.getConnectableTile(world, pos.up(y), dir);
                        if (te != null) {
                            ic = (IEssentiaTransport)te;
                            if (ic.getEssentiaAmount(dir.getOpposite()) > 0 && ic.getSuctionAmount(dir.getOpposite()) < getSuctionAmount(null) && getSuctionAmount(null) >= ic.getMinimumSuction()) {
                                final int ess = ic.takeEssentia(currentSuction, 1, dir.getOpposite());
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
    }
    
    @Override
    public int addToContainer(final Aspect tt, final int am) {
        final int ce = currentRecipe.getAspects().getAmount(tt) - essentia.getAmount(tt);
        if (currentRecipe == null || ce <= 0) {
            return am;
        }
        final int add = Math.min(ce, am);
        essentia.add(tt, add);
        syncTile(false);
        markDirty();
        return am - add;
    }
    
    @Override
    public boolean takeFromContainer(final Aspect tt, final int am) {
        if (essentia.getAmount(tt) >= am) {
            essentia.remove(tt, am);
            syncTile(false);
            markDirty();
            return true;
        }
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
        return essentia.getAmount(tt) >= am;
    }
    
    @Override
    public int containerContains(final Aspect tt) {
        return essentia.getAmount(tt);
    }
    
    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return true;
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i >= 0) {
            if (world.isRemote) {
                venting = 7;
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return face != BlockStateUtils.getFacing(getBlockMetadata());
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return face != BlockStateUtils.getFacing(getBlockMetadata());
    }
    
    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return false;
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
        currentSuction = aspect;
    }
    
    @Override
    public Aspect getSuctionType(final EnumFacing loc) {
        return currentSuction;
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing loc) {
        return (currentSuction != null) ? 128 : 0;
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
        return (canOutputTo(face) && takeFromContainer(aspect, amount)) ? amount : 0;
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return canInputFrom(face) ? (amount - addToContainer(aspect, amount)) : 0;
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public AspectList getAspects() {
        return essentia;
    }
    
    @Override
    public void setAspects(final AspectList aspects) {
        essentia = aspects;
    }
    
    public void markDirty() {
        super.markDirty();
        if (eventHandler != null) {
            eventHandler.onCraftMatrixChanged(this);
        }
    }
    
    @Override
    public int[] getSlotsForFace(final EnumFacing side) {
        return new int[] { 0 };
    }
    
    public boolean gettingPower() {
        return world.isBlockIndirectlyGettingPowered(pos) > 0 || world.isBlockIndirectlyGettingPowered(pos.down()) > 0 || world.isBlockIndirectlyGettingPowered(pos.up()) > 0;
    }
    
    public void getUpgrades() {
        final EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
        int mr = 1;
        for (int yy = 0; yy <= 1; ++yy) {
            for (final EnumFacing dir : EnumFacing.VALUES) {
                if (dir != EnumFacing.DOWN) {
                    if (dir != facing) {
                        final int xx = pos.getX() + dir.getFrontOffsetX();
                        final int zz = pos.getZ() + dir.getFrontOffsetZ();
                        final BlockPos bp = new BlockPos(xx, pos.getY() + yy + dir.getFrontOffsetY(), zz);
                        final IBlockState bs = world.getBlockState(bp);
                        if (bs == BlocksTC.brainBox.getDefaultState().withProperty((IProperty)IBlockFacing.FACING, (Comparable)dir.getOpposite())) {
                            mr += 2;
                        }
                    }
                }
            }
        }
        if (mr != maxRecipes) {
            maxRecipes = mr;
            while (recipeHash.size() > maxRecipes) {
                recipeHash.remove(recipeHash.size() - 1);
            }
            syncTile(false);
            markDirty();
        }
    }
    
    @Override
    public String getName() {
        return "container.alchemyfurnace";
    }
    
    @Override
    public boolean hasCustomName() {
        return false;
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return null;
    }
    
    public void updateRecipes(final EntityPlayer player) {
        recipes.clear();
        final ArrayList<CrucibleRecipe> recipesTemp = new ArrayList<CrucibleRecipe>();
        if (getStackInSlot(0) != null && !getStackInSlot(0).isEmpty() && recipeHash != null) {
            for (final Object r : ThaumcraftApi.getCraftingRecipes().values()) {
                if (r instanceof CrucibleRecipe) {
                    final CrucibleRecipe creps = (CrucibleRecipe)r;
                    if (ThaumcraftCapabilities.knowsResearchStrict(player, creps.getResearch()) && creps.catalystMatches(getStackInSlot(0))) {
                        recipesTemp.add(creps);
                    }
                    else {
                        if (recipeHash == null || recipeHash.size() <= 0) {
                            continue;
                        }
                        for (final Integer hash : recipeHash) {
                            if (creps.hash == hash) {
                                recipesTemp.add(creps);
                                break;
                            }
                        }
                    }
                }
            }
        }
        recipes = recipesTemp.stream().sorted(new RecipeOutputComparator()).collect(Collectors.toCollection(ArrayList::new));
    }
    
    public ArrayList<Integer> generateRecipeHashlist() {
        final ArrayList<Integer> hashList = new ArrayList<Integer>();
    Label_0016:
        for (final int hash : recipeHash) {
            for (final CrucibleRecipe cr : recipes) {
                if (cr.hash == hash) {
                    continue Label_0016;
                }
            }
            hashList.add(hash);
        }
        for (final CrucibleRecipe cr2 : recipes) {
            hashList.add(cr2.hash);
        }
        return hashList;
    }
    
    private class RecipeOutputComparator implements Comparator<CrucibleRecipe>
    {
        public RecipeOutputComparator() {
        }
        
        @Override
        public int compare(final CrucibleRecipe a, final CrucibleRecipe b) {
            if (a.equals(b)) {
                return 0;
            }
            return a.getRecipeOutput().getDisplayName().compareTo(b.getRecipeOutput().getDisplayName());
        }
    }
}
