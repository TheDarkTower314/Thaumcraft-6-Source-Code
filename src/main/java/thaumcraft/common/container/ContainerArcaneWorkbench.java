package thaumcraft.common.container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.container.slot.SlotCraftingArcaneWorkbench;
import thaumcraft.common.container.slot.SlotCrystal;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;


public class ContainerArcaneWorkbench extends Container
{
    private TileArcaneWorkbench tileEntity;
    private InventoryPlayer ip;
    public InventoryCraftResult craftResult;
    public static int[] xx;
    public static int[] yy;
    private int lastVis;
    private long lastCheck;
    
    public ContainerArcaneWorkbench(InventoryPlayer par1InventoryPlayer, TileArcaneWorkbench e) {
        craftResult = new InventoryCraftResult();
        lastVis = -1;
        lastCheck = 0L;
        tileEntity = e;
        tileEntity.inventoryCraft.eventHandler = this;
        ip = par1InventoryPlayer;
        e.getAura();
        addSlotToContainer(new SlotCraftingArcaneWorkbench(tileEntity, par1InventoryPlayer.player, tileEntity.inventoryCraft, craftResult, 15, 160, 64));
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 3; ++var7) {
                addSlotToContainer(new Slot(tileEntity.inventoryCraft, var7 + var6 * 3, 40 + var7 * 24, 40 + var6 * 24));
            }
        }
        for (ShardType st : ShardType.values()) {
            if (st.getMetadata() < 6) {
                addSlotToContainer(new SlotCrystal(st.getAspect(), tileEntity.inventoryCraft, st.getMetadata() + 9, ContainerArcaneWorkbench.xx[st.getMetadata()], ContainerArcaneWorkbench.yy[st.getMetadata()]));
            }
        }
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 9; ++var7) {
                addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 16 + var7 * 18, 151 + var6 * 18));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            addSlotToContainer(new Slot(par1InventoryPlayer, var6, 16 + var6 * 18, 209));
        }
        onCraftMatrixChanged(tileEntity.inventoryCraft);
    }
    
    public void addListener(IContainerListener par1ICrafting) {
        super.addListener(par1ICrafting);
        tileEntity.getAura();
        par1ICrafting.sendWindowProperty(this, 0, tileEntity.auraVisServer);
    }
    
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        long t = System.currentTimeMillis();
        if (t > lastCheck) {
            lastCheck = t + 500L;
            tileEntity.getAura();
        }
        if (lastVis != tileEntity.auraVisServer) {
            onCraftMatrixChanged(tileEntity.inventoryCraft);
        }
        for (int i = 0; i < listeners.size(); ++i) {
            IContainerListener icrafting = listeners.get(i);
            if (lastVis != tileEntity.auraVisServer) {
                icrafting.sendWindowProperty(this, 0, tileEntity.auraVisServer);
            }
        }
        lastVis = tileEntity.auraVisServer;
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            tileEntity.auraVisClient = par2;
        }
    }
    
    public void onCraftMatrixChanged(IInventory par1IInventory) {
        IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(tileEntity.inventoryCraft, ip.player);
        boolean hasVis = true;
        boolean hasCrystals = true;
        if (recipe != null) {
            int vis = 0;
            AspectList crystals = null;
            vis = recipe.getVis();
            vis *= (int)(1.0f - CasterManager.getTotalVisDiscount(ip.player));
            crystals = recipe.getCrystals();
            tileEntity.getAura();
            hasVis = (tileEntity.getWorld().isRemote ? (tileEntity.auraVisClient >= vis) : (tileEntity.auraVisServer >= vis));
            if (crystals != null && crystals.size() > 0) {
                for (Aspect aspect : crystals.getAspects()) {
                    if (ThaumcraftInvHelper.countTotalItemsIn(ThaumcraftInvHelper.wrapInventory(tileEntity.inventoryCraft, EnumFacing.UP), ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)), ThaumcraftInvHelper.InvFilter.STRICT) < crystals.getAmount(aspect)) {
                        hasCrystals = false;
                        break;
                    }
                }
            }
        }
        if (hasVis && hasCrystals) {
            slotChangedCraftingGrid(tileEntity.getWorld(), ip.player, tileEntity.inventoryCraft, craftResult);
        }
        super.detectAndSendChanges();
    }
    
    protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftMat, InventoryCraftResult craftRes) {
        if (!world.isRemote) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)player;
            ItemStack itemstack = ItemStack.EMPTY;
            IArcaneRecipe arecipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(craftMat, entityplayermp);
            if (arecipe != null && (arecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(arecipe)) && ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(arecipe.getResearch())) {
                craftRes.setRecipeUsed(arecipe);
                itemstack = arecipe.getCraftingResult(craftMat);
            }
            else {
                InventoryCrafting craftInv = new InventoryCrafting(new ContainerDummy(), 3, 3);
                for (int a = 0; a < 9; ++a) {
                    craftInv.setInventorySlotContents(a, craftMat.getStackInSlot(a));
                }
                IRecipe irecipe = CraftingManager.findMatchingRecipe(craftInv, world);
                if (irecipe != null && (irecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(irecipe))) {
                    craftRes.setRecipeUsed(irecipe);
                    itemstack = irecipe.getCraftingResult(craftMat);
                }
            }
            craftRes.setInventorySlotContents(0, itemstack);
            entityplayermp.connection.sendPacket(new SPacketSetSlot(windowId, 0, itemstack));
        }
    }
    
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!tileEntity.getWorld().isRemote) {
            tileEntity.inventoryCraft.eventHandler = new ContainerDummy();
        }
    }
    
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return tileEntity.getWorld().getTileEntity(tileEntity.getPos()) == tileEntity && par1EntityPlayer.getDistanceSqToCenter(tileEntity.getPos()) <= 64.0;
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1) {
        ItemStack var2 = ItemStack.EMPTY;
        Slot var3 = inventorySlots.get(par1);
        if (var3 != null && var3.getHasStack()) {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();
            if (par1 == 0) {
                if (!mergeItemStack(var4, 16, 52, true)) {
                    return ItemStack.EMPTY;
                }
                var3.onSlotChange(var4, var2);
            }
            else if (par1 >= 16 && par1 < 52) {
                for (ShardType st : ShardType.values()) {
                    if (st.getMetadata() < 6) {
                        if (SlotCrystal.isValidCrystal(var4, st.getAspect())) {
                            if (!mergeItemStack(var4, 10 + st.getMetadata(), 11 + st.getMetadata(), false)) {
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
                        if (!mergeItemStack(var4, 43, 52, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                    else if (par1 >= 43 && par1 < 52 && !mergeItemStack(var4, 16, 43, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (!mergeItemStack(var4, 16, 52, false)) {
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
            var3.onTake(ip.player, var4);
        }
        return var2;
    }
    
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        return slot.inventory != craftResult && super.canMergeSlot(stack, slot);
    }
    
    static {
        ContainerArcaneWorkbench.xx = new int[] { 64, 17, 112, 17, 112, 64 };
        ContainerArcaneWorkbench.yy = new int[] { 13, 35, 35, 93, 93, 115 };
    }
}
