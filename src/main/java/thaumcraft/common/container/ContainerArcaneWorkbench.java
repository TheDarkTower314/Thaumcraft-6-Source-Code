// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.item.crafting.CraftingManager;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.inventory.IContainerListener;
import thaumcraft.common.container.slot.SlotCrystal;
import thaumcraft.common.blocks.world.ore.ShardType;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import thaumcraft.common.container.slot.SlotCraftingArcaneWorkbench;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.entity.player.InventoryPlayer;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import net.minecraft.inventory.Container;

public class ContainerArcaneWorkbench extends Container
{
    private TileArcaneWorkbench tileEntity;
    private InventoryPlayer ip;
    public InventoryCraftResult craftResult;
    public static int[] xx;
    public static int[] yy;
    private int lastVis;
    private long lastCheck;
    
    public ContainerArcaneWorkbench(final InventoryPlayer par1InventoryPlayer, final TileArcaneWorkbench e) {
        this.craftResult = new InventoryCraftResult();
        this.lastVis = -1;
        this.lastCheck = 0L;
        this.tileEntity = e;
        this.tileEntity.inventoryCraft.eventHandler = this;
        this.ip = par1InventoryPlayer;
        e.getAura();
        this.addSlotToContainer(new SlotCraftingArcaneWorkbench(this.tileEntity, par1InventoryPlayer.player, this.tileEntity.inventoryCraft, this.craftResult, 15, 160, 64));
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 3; ++var7) {
                this.addSlotToContainer(new Slot(this.tileEntity.inventoryCraft, var7 + var6 * 3, 40 + var7 * 24, 40 + var6 * 24));
            }
        }
        for (final ShardType st : ShardType.values()) {
            if (st.getMetadata() < 6) {
                this.addSlotToContainer(new SlotCrystal(st.getAspect(), this.tileEntity.inventoryCraft, st.getMetadata() + 9, ContainerArcaneWorkbench.xx[st.getMetadata()], ContainerArcaneWorkbench.yy[st.getMetadata()]));
            }
        }
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 16 + var7 * 18, 151 + var6 * 18));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, var6, 16 + var6 * 18, 209));
        }
        this.onCraftMatrixChanged(this.tileEntity.inventoryCraft);
    }
    
    public void addListener(final IContainerListener par1ICrafting) {
        super.addListener(par1ICrafting);
        this.tileEntity.getAura();
        par1ICrafting.sendWindowProperty(this, 0, this.tileEntity.auraVisServer);
    }
    
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        final long t = System.currentTimeMillis();
        if (t > this.lastCheck) {
            this.lastCheck = t + 500L;
            this.tileEntity.getAura();
        }
        if (this.lastVis != this.tileEntity.auraVisServer) {
            this.onCraftMatrixChanged(this.tileEntity.inventoryCraft);
        }
        for (int i = 0; i < this.listeners.size(); ++i) {
            final IContainerListener icrafting = this.listeners.get(i);
            if (this.lastVis != this.tileEntity.auraVisServer) {
                icrafting.sendWindowProperty(this, 0, this.tileEntity.auraVisServer);
            }
        }
        this.lastVis = this.tileEntity.auraVisServer;
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(final int par1, final int par2) {
        if (par1 == 0) {
            this.tileEntity.auraVisClient = par2;
        }
    }
    
    public void onCraftMatrixChanged(final IInventory par1IInventory) {
        final IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(this.tileEntity.inventoryCraft, this.ip.player);
        boolean hasVis = true;
        boolean hasCrystals = true;
        if (recipe != null) {
            int vis = 0;
            AspectList crystals = null;
            vis = recipe.getVis();
            vis *= (int)(1.0f - CasterManager.getTotalVisDiscount(this.ip.player));
            crystals = recipe.getCrystals();
            this.tileEntity.getAura();
            hasVis = (this.tileEntity.getWorld().isRemote ? (this.tileEntity.auraVisClient >= vis) : (this.tileEntity.auraVisServer >= vis));
            if (crystals != null && crystals.size() > 0) {
                for (final Aspect aspect : crystals.getAspects()) {
                    if (ThaumcraftInvHelper.countTotalItemsIn(ThaumcraftInvHelper.wrapInventory(this.tileEntity.inventoryCraft, EnumFacing.UP), ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)), ThaumcraftInvHelper.InvFilter.STRICT) < crystals.getAmount(aspect)) {
                        hasCrystals = false;
                        break;
                    }
                }
            }
        }
        if (hasVis && hasCrystals) {
            this.slotChangedCraftingGrid(this.tileEntity.getWorld(), this.ip.player, this.tileEntity.inventoryCraft, this.craftResult);
        }
        super.detectAndSendChanges();
    }
    
    protected void slotChangedCraftingGrid(final World world, final EntityPlayer player, final InventoryCrafting craftMat, final InventoryCraftResult craftRes) {
        if (!world.isRemote) {
            final EntityPlayerMP entityplayermp = (EntityPlayerMP)player;
            ItemStack itemstack = ItemStack.EMPTY;
            final IArcaneRecipe arecipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(craftMat, entityplayermp);
            if (arecipe != null && (arecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(arecipe)) && ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(arecipe.getResearch())) {
                craftRes.setRecipeUsed(arecipe);
                itemstack = arecipe.getCraftingResult(craftMat);
            }
            else {
                final InventoryCrafting craftInv = new InventoryCrafting(new ContainerDummy(), 3, 3);
                for (int a = 0; a < 9; ++a) {
                    craftInv.setInventorySlotContents(a, craftMat.getStackInSlot(a));
                }
                final IRecipe irecipe = CraftingManager.findMatchingRecipe(craftInv, world);
                if (irecipe != null && (irecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(irecipe))) {
                    craftRes.setRecipeUsed(irecipe);
                    itemstack = irecipe.getCraftingResult(craftMat);
                }
            }
            craftRes.setInventorySlotContents(0, itemstack);
            entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, itemstack));
        }
    }
    
    public void onContainerClosed(final EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!this.tileEntity.getWorld().isRemote) {
            this.tileEntity.inventoryCraft.eventHandler = new ContainerDummy();
        }
    }
    
    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.tileEntity.getWorld().getTileEntity(this.tileEntity.getPos()) == this.tileEntity && par1EntityPlayer.getDistanceSqToCenter(this.tileEntity.getPos()) <= 64.0;
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = ItemStack.EMPTY;
        final Slot var3 = this.inventorySlots.get(par1);
        if (var3 != null && var3.getHasStack()) {
            final ItemStack var4 = var3.getStack();
            var2 = var4.copy();
            if (par1 == 0) {
                if (!this.mergeItemStack(var4, 16, 52, true)) {
                    return ItemStack.EMPTY;
                }
                var3.onSlotChange(var4, var2);
            }
            else if (par1 >= 16 && par1 < 52) {
                for (final ShardType st : ShardType.values()) {
                    if (st.getMetadata() < 6) {
                        if (SlotCrystal.isValidCrystal(var4, st.getAspect())) {
                            if (!this.mergeItemStack(var4, 10 + st.getMetadata(), 11 + st.getMetadata(), false)) {
                                return ItemStack.EMPTY;
                            }
                            if (var4.getCount() == 0) {
                                break;
                            }
                        }
                    }
                }
                if (var4.getCount() != 0) {
                    if (par1 >= 16 && par1 < 43) {
                        if (!this.mergeItemStack(var4, 43, 52, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                    else if (par1 >= 43 && par1 < 52 && !this.mergeItemStack(var4, 16, 43, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (!this.mergeItemStack(var4, 16, 52, false)) {
                return ItemStack.EMPTY;
            }
            if (var4.getCount() == 0) {
                var3.putStack(ItemStack.EMPTY);
            }
            else {
                var3.onSlotChanged();
            }
            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY;
            }
            var3.onTake(this.ip.player, var4);
        }
        return var2;
    }
    
    public boolean canMergeSlot(final ItemStack stack, final Slot slot) {
        return slot.inventory != this.craftResult && super.canMergeSlot(stack, slot);
    }
    
    static {
        ContainerArcaneWorkbench.xx = new int[] { 64, 17, 112, 17, 112, 64 };
        ContainerArcaneWorkbench.yy = new int[] { 13, 35, 35, 93, 93, 115 };
    }
}
