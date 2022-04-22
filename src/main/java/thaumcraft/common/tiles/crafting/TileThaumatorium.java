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
        this.essentia = new AspectList();
        this.recipeHash = new ArrayList<Integer>();
        this.recipeEssentia = new ArrayList<AspectList>();
        this.recipePlayer = new ArrayList<String>();
        this.currentCraft = -1;
        this.maxRecipes = 1;
        this.currentSuction = null;
        this.venting = 0;
        this.counter = 0;
        this.heated = false;
        this.currentRecipe = null;
        this.recipes = new ArrayList<CrucibleRecipe>();
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos().getX() - 0.1, this.getPos().getY() - 0.1, this.getPos().getZ() - 0.1, this.getPos().getX() + 1.1, this.getPos().getY() + 2.1, this.getPos().getZ() + 1.1);
    }
    
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        this.essentia.readFromNBT(nbttagcompound);
        this.maxRecipes = nbttagcompound.getByte("maxrec");
        this.recipeEssentia = new ArrayList<AspectList>();
        this.recipeHash = new ArrayList<Integer>();
        this.recipePlayer = new ArrayList<String>();
        final int[] hashes = nbttagcompound.getIntArray("recipes");
        if (hashes != null) {
            for (final int hash : hashes) {
                final CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(hash);
                if (recipe != null) {
                    this.recipeEssentia.add(recipe.getAspects().copy());
                    this.recipePlayer.add("");
                    this.recipeHash.add(hash);
                }
            }
        }
    }
    
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("maxrec", (byte)this.maxRecipes);
        this.essentia.writeToNBT(nbttagcompound);
        final int[] hashes = new int[this.recipeHash.size()];
        int a = 0;
        for (final Integer i : this.recipeHash) {
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
            if (this.recipePlayer.size() > a) {
                this.recipePlayer.set(a, nbttaglist2.getStringTagAt(a));
            }
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        final NBTTagList nbttaglist2 = new NBTTagList();
        if (this.recipePlayer.size() > 0) {
            for (int a = 0; a < this.recipePlayer.size(); ++a) {
                if (this.recipePlayer.get(a) != null) {
                    final NBTTagString nbttagcompound1 = new NBTTagString(this.recipePlayer.get(a));
                    nbttaglist2.appendTag(nbttagcompound1);
                }
            }
        }
        nbtCompound.setTag("OutputPlayer", nbttaglist2);
        return nbtCompound;
    }
    
    boolean checkHeat() {
        final Material mat = this.world.getBlockState(this.pos.down(2)).getMaterial();
        final Block bi = this.world.getBlockState(this.pos.down(2)).getBlock();
        return mat == Material.LAVA || mat == Material.FIRE || BlocksTC.nitor.containsValue(bi) || bi == Blocks.MAGMA;
    }
    
    public ItemStack getCurrentOutputRecipe() {
        ItemStack out = ItemStack.EMPTY;
        if (this.currentCraft >= 0 && this.recipeHash != null && this.recipeHash.size() > 0) {
            final CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(this.recipeHash.get(this.currentCraft));
            if (recipe != null) {
                out = recipe.getRecipeOutput().copy();
            }
        }
        return out;
    }
    
    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (this.counter == 0 || this.counter % 40 == 0) {
                this.heated = this.checkHeat();
                this.getUpgrades();
            }
            ++this.counter;
            if (this.heated && !this.gettingPower() && this.counter % 5 == 0 && this.recipeHash != null && this.recipeHash.size() > 0) {
                if (this.getStackInSlot(0).isEmpty()) {
                    this.currentSuction = null;
                    return;
                }
                if (this.currentCraft < 0 || this.currentCraft >= this.recipeHash.size() || this.currentRecipe == null || !this.currentRecipe.catalystMatches(this.getStackInSlot(0))) {
                    for (int a = 0; a < this.recipeHash.size(); ++a) {
                        final CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(this.recipeHash.get(a));
                        if (recipe.catalystMatches(this.getStackInSlot(0))) {
                            this.currentCraft = a;
                            this.currentRecipe = recipe;
                            break;
                        }
                    }
                }
                if (this.currentCraft < 0 || this.currentCraft >= this.recipeHash.size()) {
                    return;
                }
                boolean done = true;
                this.currentSuction = null;
                for (final Aspect aspect : this.recipeEssentia.get(this.currentCraft).getAspectsSortedByName()) {
                    if (this.essentia.getAmount(aspect) < this.recipeEssentia.get(this.currentCraft).getAmount(aspect)) {
                        this.currentSuction = aspect;
                        done = false;
                        break;
                    }
                }
                if (done) {
                    this.completeRecipe();
                }
                else if (this.currentSuction != null) {
                    this.fill();
                }
            }
        }
        else if (this.venting > 0) {
            --this.venting;
            final float fx = 0.1f - this.world.rand.nextFloat() * 0.2f;
            final float fz = 0.1f - this.world.rand.nextFloat() * 0.2f;
            final float fy = 0.1f - this.world.rand.nextFloat() * 0.2f;
            final float fx2 = 0.1f - this.world.rand.nextFloat() * 0.2f;
            final float fz2 = 0.1f - this.world.rand.nextFloat() * 0.2f;
            final float fy2 = 0.1f - this.world.rand.nextFloat() * 0.2f;
            final int color = 16777215;
            final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
            FXDispatcher.INSTANCE.drawVentParticles(this.pos.getX() + 0.5f + fx + facing.getFrontOffsetX() / 2.0f, this.pos.getY() + 0.5f + fy, this.pos.getZ() + 0.5f + fz + facing.getFrontOffsetZ() / 2.0f, facing.getFrontOffsetX() / 4.0f + fx2, fy2, facing.getFrontOffsetZ() / 4.0f + fz2, color);
        }
    }
    
    private void completeRecipe() {
        if (this.currentRecipe != null && this.currentCraft < this.recipeHash.size() && this.currentRecipe.matches(this.essentia, this.getStackInSlot(0)) && this.decrStackSize(0, 1) != null) {
            this.essentia = new AspectList();
            final ItemStack dropped = this.getCurrentOutputRecipe();
            final EntityPlayer p = this.world.getPlayerEntityByName(this.recipePlayer.get(this.currentCraft));
            if (p != null) {
                FMLCommonHandler.instance().firePlayerCraftingEvent(p, dropped, new InventoryFake(this.getStackInSlot(0)));
            }
            final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
            InventoryUtils.ejectStackAt(this.getWorld(), this.getPos(), facing, dropped);
            this.world.playSound(null, this.pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.25f, 2.6f + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8f);
            this.currentCraft = -1;
            this.syncTile(false);
            this.markDirty();
        }
    }
    
    void fill() {
        final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
        TileEntity te = null;
        IEssentiaTransport ic = null;
        for (int y = 0; y <= 1; ++y) {
            for (final EnumFacing dir : EnumFacing.VALUES) {
                if (dir != facing && dir != EnumFacing.DOWN) {
                    if (y != 0 || dir != EnumFacing.UP) {
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
    }
    
    @Override
    public int addToContainer(final Aspect tt, final int am) {
        final int ce = this.currentRecipe.getAspects().getAmount(tt) - this.essentia.getAmount(tt);
        if (this.currentRecipe == null || ce <= 0) {
            return am;
        }
        final int add = Math.min(ce, am);
        this.essentia.add(tt, add);
        this.syncTile(false);
        this.markDirty();
        return am - add;
    }
    
    @Override
    public boolean takeFromContainer(final Aspect tt, final int am) {
        if (this.essentia.getAmount(tt) >= am) {
            this.essentia.remove(tt, am);
            this.syncTile(false);
            this.markDirty();
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
        return this.essentia.getAmount(tt) >= am;
    }
    
    @Override
    public int containerContains(final Aspect tt) {
        return this.essentia.getAmount(tt);
    }
    
    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return true;
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i >= 0) {
            if (this.world.isRemote) {
                this.venting = 7;
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
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
        return this.essentia;
    }
    
    @Override
    public void setAspects(final AspectList aspects) {
        this.essentia = aspects;
    }
    
    public void markDirty() {
        super.markDirty();
        if (this.eventHandler != null) {
            this.eventHandler.onCraftMatrixChanged(this);
        }
    }
    
    @Override
    public int[] getSlotsForFace(final EnumFacing side) {
        return new int[] { 0 };
    }
    
    public boolean gettingPower() {
        return this.world.isBlockIndirectlyGettingPowered(this.pos) > 0 || this.world.isBlockIndirectlyGettingPowered(this.pos.down()) > 0 || this.world.isBlockIndirectlyGettingPowered(this.pos.up()) > 0;
    }
    
    public void getUpgrades() {
        final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
        int mr = 1;
        for (int yy = 0; yy <= 1; ++yy) {
            for (final EnumFacing dir : EnumFacing.VALUES) {
                if (dir != EnumFacing.DOWN) {
                    if (dir != facing) {
                        final int xx = this.pos.getX() + dir.getFrontOffsetX();
                        final int zz = this.pos.getZ() + dir.getFrontOffsetZ();
                        final BlockPos bp = new BlockPos(xx, this.pos.getY() + yy + dir.getFrontOffsetY(), zz);
                        final IBlockState bs = this.world.getBlockState(bp);
                        if (bs == BlocksTC.brainBox.getDefaultState().withProperty((IProperty)IBlockFacing.FACING, (Comparable)dir.getOpposite())) {
                            mr += 2;
                        }
                    }
                }
            }
        }
        if (mr != this.maxRecipes) {
            this.maxRecipes = mr;
            while (this.recipeHash.size() > this.maxRecipes) {
                this.recipeHash.remove(this.recipeHash.size() - 1);
            }
            this.syncTile(false);
            this.markDirty();
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
        this.recipes.clear();
        final ArrayList<CrucibleRecipe> recipesTemp = new ArrayList<CrucibleRecipe>();
        if (this.getStackInSlot(0) != null && !this.getStackInSlot(0).isEmpty() && this.recipeHash != null) {
            for (final Object r : ThaumcraftApi.getCraftingRecipes().values()) {
                if (r instanceof CrucibleRecipe) {
                    final CrucibleRecipe creps = (CrucibleRecipe)r;
                    if (ThaumcraftCapabilities.knowsResearchStrict(player, creps.getResearch()) && creps.catalystMatches(this.getStackInSlot(0))) {
                        recipesTemp.add(creps);
                    }
                    else {
                        if (this.recipeHash == null || this.recipeHash.size() <= 0) {
                            continue;
                        }
                        for (final Integer hash : this.recipeHash) {
                            if (creps.hash == hash) {
                                recipesTemp.add(creps);
                                break;
                            }
                        }
                    }
                }
            }
        }
        this.recipes = recipesTemp.stream().sorted(new RecipeOutputComparator()).collect(Collectors.toCollection(ArrayList::new));
    }
    
    public ArrayList<Integer> generateRecipeHashlist() {
        final ArrayList<Integer> hashList = new ArrayList<Integer>();
    Label_0016:
        for (final int hash : this.recipeHash) {
            for (final CrucibleRecipe cr : this.recipes) {
                if (cr.hash == hash) {
                    continue Label_0016;
                }
            }
            hashList.add(hash);
        }
        for (final CrucibleRecipe cr2 : this.recipes) {
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
