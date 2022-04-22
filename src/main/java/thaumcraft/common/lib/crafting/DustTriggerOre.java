// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.crafting;

import thaumcraft.common.lib.events.ServerEvents;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.InventoryFake;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.block.state.IBlockState;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import thaumcraft.api.crafting.IDustTrigger;

public class DustTriggerOre implements IDustTrigger
{
    String target;
    ItemStack result;
    String research;
    
    public DustTriggerOre(final String research, final String target, final ItemStack result) {
        this.target = target;
        this.result = result;
        this.research = research;
    }
    
    @Override
    public Placement getValidFace(final World world, final EntityPlayer player, final BlockPos pos, final EnumFacing face) {
        final IBlockState bs = world.getBlockState(pos);
        boolean b = false;
        try {
            final int[] oreIDs;
            final int[] ods = oreIDs = OreDictionary.getOreIDs(new ItemStack(bs.getBlock(), 1, bs.getBlock().damageDropped(bs)));
            for (final int q : oreIDs) {
                if (q == OreDictionary.getOreID(target)) {
                    b = true;
                    break;
                }
            }
        }
        catch (final Exception ex) {}
        return (b && (research == null || ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research))) ? new Placement(0, 0, 0, null) : null;
    }
    
    @Override
    public void execute(final World world, final EntityPlayer player, final BlockPos pos, final Placement placement, final EnumFacing side) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, result, new InventoryFake(1));
        final IBlockState state = world.getBlockState(pos);
        ServerEvents.addRunnableServer(world, new Runnable() {
            @Override
            public void run() {
                ServerEvents.addSwapper(world, pos, state, result, false, 0, player, true, true, -9999, false, false, 0, ServerEvents.DEFAULT_PREDICATE, 0.0f);
            }
        }, 50);
    }
}
