package thaumcraft.common.container.slot;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;


public class SlotCraftingArcaneWorkbench extends Slot
{
    private InventoryCrafting craftMatrix;
    private EntityPlayer player;
    private int amountCrafted;
    private TileArcaneWorkbench tile;
    
    public SlotCraftingArcaneWorkbench(TileArcaneWorkbench te, EntityPlayer par1EntityPlayer, InventoryCrafting inventory, IInventory par3IInventory, int par4, int par5, int par6) {
        super(par3IInventory, par4, par5, par6);
        player = par1EntityPlayer;
        craftMatrix = inventory;
        tile = te;
    }
    
    public boolean isItemValid(ItemStack stack) {
        return false;
    }
    
    public ItemStack decrStackSize(int amount) {
        if (getHasStack()) {
            amountCrafted += Math.min(amount, getStack().getCount());
        }
        return super.decrStackSize(amount);
    }
    
    protected void onCrafting(ItemStack stack, int amount) {
        amountCrafted += amount;
        onCrafting(stack);
    }
    
    protected void onSwapCraft(int p_190900_1_) {
        amountCrafted += p_190900_1_;
    }
    
    protected void onCrafting(ItemStack stack) {
        if (amountCrafted > 0) {
            stack.onCrafting(player.world, player, amountCrafted);
            FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, craftMatrix);
        }
        amountCrafted = 0;
        InventoryCraftResult inventorycraftresult = (InventoryCraftResult) inventory;
        IRecipe irecipe = inventorycraftresult.getRecipeUsed();
        if (irecipe != null && !irecipe.isDynamic()) {
            player.unlockRecipes((List)Lists.newArrayList((Object[])new IRecipe[] { irecipe }));
            inventorycraftresult.setRecipeUsed(null);
        }
    }
    
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
        onCrafting(stack);
        IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(craftMatrix, thePlayer);
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
            ItemStack itemstack2 = nonnulllist.get(i);
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
            for (Aspect aspect : crystals.getAspects()) {
                ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));
                for (int j = 0; j < 6; ++j) {
                    ItemStack itemstack3 = craftMatrix.getStackInSlot(9 + j);
                    if (itemstack3.getItem() == ItemsTC.crystalEssence && ItemStack.areItemStackTagsEqual(cs, itemstack3)) {
                        craftMatrix.decrStackSize(9 + j, cs.getCount());
                    }
                }
            }
        }
        return stack;
    }
}
