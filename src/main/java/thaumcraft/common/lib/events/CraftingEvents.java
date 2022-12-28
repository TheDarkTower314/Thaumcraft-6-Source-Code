package thaumcraft.common.lib.events;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.lib.research.ResearchManager;


@Mod.EventBusSubscriber
public class CraftingEvents implements IFuelHandler
{
    public int getBurnTime(ItemStack fuel) {
        if (fuel.isItemEqual(new ItemStack(ItemsTC.alumentum))) {
            return 4800;
        }
        if (fuel.isItemEqual(new ItemStack(BlocksTC.logGreatwood))) {
            return 500;
        }
        if (fuel.isItemEqual(new ItemStack(BlocksTC.logSilverwood))) {
            return 400;
        }
        return 0;
    }
    
    @SubscribeEvent
    public static void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        int warp = ThaumcraftApi.getWarp(event.crafting);
        if (!ModConfig.CONFIG_MISC.wussMode && warp > 0 && !event.player.world.isRemote) {
            ThaumcraftApi.internalMethods.addWarpToPlayer(event.player, warp, IPlayerWarp.EnumWarpType.NORMAL);
        }
        if (event.crafting.getItem() == ItemsTC.label && event.crafting.hasTagCompound()) {
            for (int var2 = 0; var2 < 9; ++var2) {
                ItemStack var3 = event.craftMatrix.getStackInSlot(var2);
                if (var3 != null && var3.getItem() instanceof ItemPhial) {
                    var3.grow(1);
                    event.craftMatrix.setInventorySlotContents(var2, var3);
                }
            }
        }
        if (event.player != null && !event.player.world.isRemote) {
            int stackHash = ResearchManager.createItemStackHash(event.crafting.copy());
            if (ResearchManager.craftingReferences.contains(stackHash)) {
                ResearchManager.completeResearch(event.player, "[#]" + stackHash);
            }
            else {
                stackHash = ResearchManager.createItemStackHash(new ItemStack(event.crafting.getItem(), event.crafting.getCount(), event.crafting.getItemDamage()));
                if (ResearchManager.craftingReferences.contains(stackHash)) {
                    ResearchManager.completeResearch(event.player, "[#]" + stackHash);
                }
            }
            try {
                int[] oreIDs;
                int[] ids = oreIDs = OreDictionary.getOreIDs(event.crafting.copy());
                for (int id : oreIDs) {
                    String ore = OreDictionary.getOreName(id);
                    if (ore != null) {
                        int cd = ("oredict:" + ore).hashCode();
                        if (ore != null && ResearchManager.craftingReferences.contains(cd)) {
                            ResearchManager.completeResearch(event.player, "[#]" + cd);
                        }
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    @SubscribeEvent
    public static void onAnvil(AnvilUpdateEvent event) {
        if (event.getLeft().getItem() == ItemsTC.primordialPearl || event.getRight().getItem() == ItemsTC.primordialPearl) {
            event.setCanceled(true);
        }
    }
}
