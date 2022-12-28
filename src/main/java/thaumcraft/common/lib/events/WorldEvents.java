package thaumcraft.common.lib.events;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import thaumcraft.common.world.aura.AuraHandler;


@Mod.EventBusSubscriber
public class WorldEvents
{
    public static WorldEvents INSTANCE;
    
    @SubscribeEvent
    public static void worldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            AuraHandler.addAuraWorld(event.getWorld().provider.getDimension());
        }
    }
    
    @SubscribeEvent
    public static void worldSave(WorldEvent.Save event) {
        if (!event.getWorld().isRemote) {}
    }
    
    @SubscribeEvent
    public static void worldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) {
            return;
        }
        SealHandler.sealEntities.remove(event.getWorld().provider.getDimension());
        AuraHandler.removeAuraWorld(event.getWorld().provider.getDimension());
    }
    
    @SubscribeEvent
    public static void placeBlockEvent(BlockEvent.PlaceEvent event) {
        if (isNearActiveBoss(event.getWorld(), event.getPlayer(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ())) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void placeBlockEvent(BlockEvent.MultiPlaceEvent event) {
        if (isNearActiveBoss(event.getWorld(), event.getPlayer(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ())) {
            event.setCanceled(true);
        }
    }
    
    private static boolean isNearActiveBoss(World world, EntityPlayer player, int x, int y, int z) {
        return false;
    }
    
    @SubscribeEvent
    public static void noteEvent(NoteBlockEvent.Play event) {
        if (event.getWorld().isRemote) {
            return;
        }
        if (!TileArcaneEar.noteBlockEvents.containsKey(event.getWorld().provider.getDimension())) {
            TileArcaneEar.noteBlockEvents.put(event.getWorld().provider.getDimension(), new ArrayList<Integer[]>());
        }
        ArrayList<Integer[]> list = TileArcaneEar.noteBlockEvents.get(event.getWorld().provider.getDimension());
        list.add(new Integer[] { event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getInstrument().ordinal(), event.getVanillaNoteId() });
        TileArcaneEar.noteBlockEvents.put(event.getWorld().provider.getDimension(), list);
    }
    
    static {
        WorldEvents.INSTANCE = new WorldEvents();
    }
}
