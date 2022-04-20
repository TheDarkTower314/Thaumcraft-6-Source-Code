// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.events;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.misc.PacketSealToClient;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraftforge.event.world.ChunkWatchEvent;
import java.util.ArrayList;
import thaumcraft.Thaumcraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.golems.seals.SealEntity;
import java.util.Iterator;
import thaumcraft.common.world.aura.AuraChunk;
import net.minecraft.util.math.ChunkPos;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import net.minecraft.nbt.NBTTagList;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.common.config.ModConfig;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ChunkEvents
{
    @SubscribeEvent
    public static void chunkSave(final ChunkDataEvent.Save event) {
        final int dim = event.getWorld().provider.getDimension();
        final ChunkPos loc = event.getChunk().getPos();
        final NBTTagCompound nbt = new NBTTagCompound();
        event.getData().setTag("Thaumcraft", nbt);
        nbt.setBoolean(ModConfig.CONFIG_WORLD.regenKey, true);
        final AuraChunk ac = AuraHandler.getAuraChunk(dim, loc.x, loc.z);
        if (ac != null) {
            nbt.setShort("base", ac.getBase());
            nbt.setFloat("flux", ac.getFlux());
            nbt.setFloat("vis", ac.getVis());
            if (!event.getChunk().isLoaded()) {
                AuraHandler.removeAuraChunk(dim, loc.x, loc.z);
            }
        }
        final NBTTagList tagList = new NBTTagList();
        for (final ISealEntity seal : SealHandler.getSealsInChunk(event.getWorld(), loc)) {
            final NBTTagCompound sealnbt = seal.writeNBT();
            tagList.appendTag(sealnbt);
            if (!event.getChunk().isLoaded()) {
                SealHandler.removeSealEntity(event.getWorld(), seal.getSealPos(), true);
            }
        }
        nbt.setTag("seals", tagList);
    }
    
    @SubscribeEvent
    public static void chunkLoad(final ChunkDataEvent.Load event) {
        final int dim = event.getWorld().provider.getDimension();
        final ChunkPos loc = event.getChunk().getPos();
        if (event.getData().getCompoundTag("Thaumcraft").hasKey("base")) {
            final NBTTagCompound nbt = event.getData().getCompoundTag("Thaumcraft");
            final short base = nbt.getShort("base");
            final float flux = nbt.getFloat("flux");
            final float vis = nbt.getFloat("vis");
            AuraHandler.addAuraChunk(dim, event.getChunk(), base, vis, flux);
        }
        else {
            AuraHandler.generateAura(event.getChunk(), event.getWorld().rand);
        }
        if (event.getData().getCompoundTag("Thaumcraft").hasKey("seals")) {
            final NBTTagCompound nbt = event.getData().getCompoundTag("Thaumcraft");
            final NBTTagList tagList = nbt.getTagList("seals", 10);
            for (int a = 0; a < tagList.tagCount(); ++a) {
                final NBTTagCompound tasknbt = tagList.getCompoundTagAt(a);
                final SealEntity seal = new SealEntity();
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
    public static void chunkWatch(final ChunkWatchEvent.Watch event) {
        for (final ISealEntity seal : SealHandler.getSealsInChunk(event.getPlayer().world, event.getChunk())) {
            PacketHandler.INSTANCE.sendTo(new PacketSealToClient(seal), event.getPlayer());
        }
    }
}
