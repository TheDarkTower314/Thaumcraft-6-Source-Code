// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.container.slot;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.util.NonNullList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.common.items.casters.CasterManager;
import net.minecraft.inventory.Container;
import thaumcraft.api.crafting.ContainerDummy;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.ForgeHooks;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;

public class SlotCraftingArcaneWorkbench extends Slot
{
    private final InventoryCrafting craftMatrix;
    private EntityPlayer player;
    private int amountCrafted;
    private TileArcaneWorkbench tile;
    
    public SlotCraftingArcaneWorkbench(final TileArcaneWorkbench te, final EntityPlayer par1EntityPlayer, final InventoryCrafting inventory, final IInventory par3IInventory, final int par4, final int par5, final int par6) {
        super(par3IInventory, par4, par5, par6);
        player = par1EntityPlayer;
        craftMatrix = inventory;
        tile = te;
    }
    
    public boolean isItemValid(final ItemStack stack) {
        return false;
    }
    
    public ItemStack decrStackSize(final int amount) {
        if (getHasStack()) {
            amountCrafted += Math.min(amount, getStack().getCount());
        }
        return super.decrStackSize(amount);
    }
    
    protected void onCrafting(final ItemStack stack, final int amount) {
        amountCrafted += amount;
        onCrafting(stack);
    }
    
    protected void onSwapCraft(final int p_190900_1_) {
        amountCrafted += p_190900_1_;
    }
    
    protected void onCrafting(final ItemStack stack) {
        if (amountCrafted > 0) {
            stack.onCrafting(player.world, player, amountCrafted);
            FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, craftMatrix);
        }
        amountCrafted = 0;
        final InventoryCraftResult inventorycraftresult = (InventoryCraftResult) inventory;
        final IRecipe irecipe = inventorycraftresult.getRecipeUsed();
        if (irecipe != null && !irecipe.isDynamic()) {
            player.unlockRecipes((List)Lists.newArrayList((Object[])new IRecipe[] { irecipe }));
            inventorycraftresult.setRecipeUsed(null);
        }
    }
    
    public ItemStack onTake(final EntityPlayer thePlayer, final ItemStack stack) {
        onCrafting(stack);
        final IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(craftMatrix, thePlayer);
        InventoryCrafting ic = craftMatrix;
        ForgeHooks.setCraftingPlayer(thePlayer);
        NonNullList<ItemStack> nonnulllist;
        if (recipe != null) {
            nonnulllist = CraftingManager.getRemainingItems(craftMatrix, thePlayer.world);
        }
        else {
            ic = new InventoryCrafting(new ContainerDummy(), 3, 3);
            for (int a = 0; a < 9; ++a) {
                ic.setInventorySlotContents(a, craftMatrix.getStackInSlot(a));
            }
            ic.eventHandler = craftMatrix.eventHandler;
            nonnulllist = CraftingManager.getRemainingItems(ic, thePlayer.world);
        }
        ForgeHooks.setCraftingPlayer(null);
        int vis = 0;
        AspectList crystals = null;
        if (recipe != null) {
            vis = recipe.getVis();
            vis *= (int)(1.0f - CasterManager.getTotalVisDiscount(thePlayer));
            crystals = recipe.getCrystals();
            if (vis > 0) {
                tile.getAura();
                tile.spendAura(vis);
            }
        }
        for (int i = 0; i < Math.min(9, nonnulllist.size()); ++i) {
            ItemStack itemstack = ic.getStackInSlot(i);
            final ItemStack itemstack2 = nonnulllist.get(i);
            if (!itemstack.isEmpty()) {
                craftMatrix.decrStackSize(i, 1);
                itemstack = ic.getStackInSlot(i);
            }
            if (!itemstack2.isEmpty()) {
                if (itemstack.isEmpty()) {
                    craftMatrix.setInventorySlotContents(i, itemstack2);
                }
                else if (ItemStack.areItemsEqual(itemstack, itemstack2) && ItemStack.areItemStackTagsEqual(itemstack, itemstack2)) {
                    itemstack2.grow(itemstack.getCount());
                    craftMatrix.setInventorySlotContents(i, itemstack2);
                }
                else if (!player.inventory.addItemStackToInventory(itemstack2)) {
                    player.dropItem(itemstack2, false);
                }
            }
        }
        if (crystals != null) {
            for (final Aspect aspect : crystals.getAspects()) {
                final ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));
                for (int j = 0; j < 6; ++j) {
                    final ItemStack itemstack3 = craftMatrix.getStackInSlot(9 + j);
                    if (itemstack3.getItem() == ItemsTC.crystalEssence && ItemStack.areItemStackTagsEqual(cs, itemstack3)) {
                        craftMatrix.decrStackSize(9 + j, cs.getCount());
                    }
                }
            }
        }
        return stack;
    }
}
