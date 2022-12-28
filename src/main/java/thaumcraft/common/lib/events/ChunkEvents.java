package thaumcraft.common.lib.events;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.Thaumcraft;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketSealToClient;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;


@Mod.EventBusSubscriber
public class ChunkEvents
{
    @SubscribeEvent
    public static void chunkSave(ChunkDataEvent.Save event) {
        int dim = event.getWorld().provider.getDimension();
        ChunkPos loc = event.getChunk().getPos();
        NBTTagCompound nbt = new NBTTagCompound();
        event.getData().setTag("Thaumcraft", nbt);
        nbt.setBoolean(ModConfig.CONFIG_WORLD.regenKey, true);
        AuraChunk ac = AuraHandler.getAuraChunk(dim, loc.x, loc.z);
        if (ac != null) {
            nbt.setShort("base", ac.getBase());
            nbt.setFloat("flux", ac.getFlux());
            nbt.setFloat("vis", ac.getVis());
            if (!event.getChunk().isLoaded()) {
                AuraHandler.removeAuraChunk(dim, loc.x, loc.z);
            }
        }
        NBTTagList tagList = new NBTTagList();
        for (ISealEntity seal : SealHandler.getSealsInChunk(event.getWorld(), loc)) {
            NBTTagCompound sealnbt = seal.writeNBT();
            tagList.appendTag(sealnbt);
            if (!event.getChunk().isLoaded()) {
                SealHandler.removeSealEntity(event.getWorld(), seal.getSealPos(), true);
            }
        }
        nbt.setTag("seals", tagList);
    }
    
    @SubscribeEvent
    public static void chunkLoad(ChunkDataEvent.Load event) {
        int dim = event.getWorld().provider.getDimension();
        ChunkPos loc = event.getChunk().getPos();
        if (event.getData().getCompoundTag("Thaumcraft").hasKey("base")) {
            NBTTagCompound nbt = event.getData().getCompoundTag("Thaumcraft");
            short base = nbt.getShort("base");
            float flux = nbt.getFloat("flux");
            float vis = nbt.getFloat("vis");
            AuraHandler.addAuraChunk(dim, event.getChunk(), base, vis, flux);
        }
        else {
            AuraHandler.generateAura(event.getChunk(), event.getWorld().rand);
        }
        if (event.getData().getCompoundTag("Thaumcraft").hasKey("seals")) {
            NBTTagCompound nbt = event.getData().getCompoundTag("Thaumcraft");
            NBTTagList tagList = nbt.getTagList("seals", 10);
            for (int a = 0; a < tagList.tagCount(); ++a) {
                NBTTagCompound tasknbt = tagList.getCompoundTagAt(a);
                SealEntity seal = new SealEntity();
                seal.readNBT(tasknbt);
                SealHandler.addSealEntity(event.getWorld(), seal);
            }
        }
        if (!event.getData().getCompoundTag("Thaumcraft").hasKey(ModConfig.CONFIG_WORLD.regenKey) && (ModConfig.CONFIG_WORLD.regenAmber || ModConfig.CONFIG_WORLD.regenAura || ModConfig.CONFIG_WORLD.regenCinnabar || ModConfig.CONFIG_WORLD.regenCrystals || ModConfig.CONFIG_WORLD.regenStructure || ModConfig.CONFIG_WORLD.regenTrees)) {
            Thaumcraft.log.warn("World gen was never run for chunk at " + event.getChunk().getPos() + ". Adding to queue for regeneration.");
            ArrayList<ChunkPos> chunks = ServerEvents.chunksToGenerate.get(dim);
            if (chunks == null) {
                ServerEvents.chunksToGenerate.put(dim, new ArrayList<ChunkPos>());
                chunks = ServerEvents.chunksToGenerate.get(dim);
            }
            if (chunks != null) {
                chunks.add(new ChunkPos(loc.x, loc.z));
                ServerEvents.chunksToGenerate.put(dim, chunks);
            }
        }
    }
    
    @SubscribeEvent
    public static void chunkWatch(ChunkWatchEvent.Watch event) {
        for (ISealEntity seal : SealHandler.getSealsInChunk(event.getPlayer().world, event.getChunk())) {
            PacketHandler.INSTANCE.sendTo(new PacketSealToClient(seal), event.getPlayer());
        }
    }
}
