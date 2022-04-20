// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import thaumcraft.common.world.aura.AuraHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.common.lib.network.misc.PacketSealToClient;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.util.math.ChunkPos;
import java.util.Iterator;
import net.minecraft.util.math.Vec3i;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.api.golems.seals.SealPos;
import java.util.concurrent.ConcurrentHashMap;
import thaumcraft.api.golems.seals.ISeal;
import java.util.LinkedHashMap;

public class SealHandler
{
    public static LinkedHashMap<String, ISeal> types;
    private static int lastID;
    public static ConcurrentHashMap<Integer, ConcurrentHashMap<SealPos, SealEntity>> sealEntities;
    static int count;
    
    public static void registerSeal(final ISeal seal) {
        if (SealHandler.types.containsKey(seal.getKey())) {
            Thaumcraft.log.error("Attempting to register Seal [" + seal.getKey() + "] twice. Ignoring.");
        }
        else {
            SealHandler.types.put(seal.getKey(), seal);
        }
    }
    
    public static String[] getRegisteredSeals() {
        return SealHandler.types.keySet().toArray(new String[0]);
    }
    
    public static ISeal getSeal(final String key) {
        return SealHandler.types.get(key);
    }
    
    public static CopyOnWriteArrayList<SealEntity> getSealsInRange(final World world, final BlockPos source, final int range) {
        final CopyOnWriteArrayList<SealEntity> out = new CopyOnWriteArrayList<SealEntity>();
        final ConcurrentHashMap<SealPos, SealEntity> list = SealHandler.sealEntities.get(world.provider.getDimension());
        if (list != null && list.size() > 0) {
            for (final SealEntity se : list.values()) {
                if (se.getSeal() != null) {
                    if (se.getSealPos() == null) {
                        continue;
                    }
                    if (se.sealPos.pos.distanceSq(source) > range * range) {
                        continue;
                    }
                    out.add(se);
                }
            }
        }
        return out;
    }
    
    public static CopyOnWriteArrayList<SealEntity> getSealsInChunk(final World world, final ChunkPos chunk) {
        final CopyOnWriteArrayList<SealEntity> out = new CopyOnWriteArrayList<SealEntity>();
        final ConcurrentHashMap<SealPos, SealEntity> list = SealHandler.sealEntities.get(world.provider.getDimension());
        if (list != null && list.size() > 0) {
            for (final SealEntity se : list.values()) {
                if (se.getSeal() != null) {
                    if (se.getSealPos() == null) {
                        continue;
                    }
                    final ChunkPos cc = new ChunkPos(se.sealPos.pos);
                    if (!cc.equals(chunk)) {
                        continue;
                    }
                    out.add(se);
                }
            }
        }
        return out;
    }
    
    public static void removeSealEntity(final World world, final SealPos pos, final boolean quiet) {
        if (!SealHandler.sealEntities.containsKey(world.provider.getDimension())) {
            SealHandler.sealEntities.put(world.provider.getDimension(), new ConcurrentHashMap<SealPos, SealEntity>());
        }
        final ConcurrentHashMap<SealPos, SealEntity> se = SealHandler.sealEntities.get(world.provider.getDimension());
        if (se != null) {
            final SealEntity seal = se.remove(pos);
            try {
                if (!world.isRemote && seal != null && seal.seal != null) {
                    seal.seal.onRemoval(world, pos.pos, pos.face);
                }
                if (!quiet && seal != null && !world.isRemote) {
                    final String[] rs = getRegisteredSeals();
                    int indx = 1;
                    for (final String s : rs) {
                        if (s.equals(seal.getSeal().getKey())) {
                            world.spawnEntity(new EntityItem(world, pos.pos.getX() + 0.5 + pos.face.getFrontOffsetX() / 1.7f, pos.pos.getY() + 0.5 + pos.face.getFrontOffsetY() / 1.7f, pos.pos.getZ() + 0.5 + pos.face.getFrontOffsetZ() / 1.7f, new ItemStack(ItemsTC.seals, 1, indx)));
                            break;
                        }
                        ++indx;
                    }
                }
            }
            catch (final Exception e) {
                Thaumcraft.log.warn("Removing invalid seal at " + pos.pos);
            }
            final ConcurrentHashMap<Integer, Task> ts = TaskHandler.getTasks(world.provider.getDimension());
            for (final Task task : ts.values()) {
                if (task.getSealPos() != null && task.getSealPos().equals(pos)) {
                    task.setSuspended(true);
                }
            }
            if (!world.isRemote) {
                PacketHandler.INSTANCE.sendToDimension(new PacketSealToClient(new SealEntity(world, pos, null)), world.provider.getDimension());
            }
            if (!quiet) {
                markChunkAsDirty(world.provider.getDimension(), pos.pos);
            }
        }
    }
    
    public static ISealEntity getSealEntity(final int dim, final SealPos pos) {
        if (!SealHandler.sealEntities.containsKey(dim)) {
            SealHandler.sealEntities.put(dim, new ConcurrentHashMap<SealPos, SealEntity>());
        }
        if (pos == null) {
            return null;
        }
        final ConcurrentHashMap<SealPos, SealEntity> se = SealHandler.sealEntities.get(dim);
        if (se != null) {
            return se.get(pos);
        }
        return null;
    }
    
    public static boolean addSealEntity(final World world, final BlockPos pos, final EnumFacing face, final ISeal seal, final EntityPlayer player) {
        if (!SealHandler.sealEntities.containsKey(world.provider.getDimension())) {
            SealHandler.sealEntities.put(world.provider.getDimension(), new ConcurrentHashMap<SealPos, SealEntity>());
        }
        final ConcurrentHashMap<SealPos, SealEntity> se = SealHandler.sealEntities.get(world.provider.getDimension());
        final SealPos sp = new SealPos(pos, face);
        if (se.containsKey(sp)) {
            return false;
        }
        final SealEntity sealent = new SealEntity(world, sp, seal);
        sealent.setOwner(player.getUniqueID().toString());
        se.put(sp, sealent);
        if (!world.isRemote) {
            sealent.syncToClient(world);
            markChunkAsDirty(world.provider.getDimension(), pos);
        }
        return true;
    }
    
    public static boolean addSealEntity(final World world, final SealEntity seal) {
        if (world == null || SealHandler.sealEntities == null) {
            return false;
        }
        if (!SealHandler.sealEntities.containsKey(world.provider.getDimension())) {
            SealHandler.sealEntities.put(world.provider.getDimension(), new ConcurrentHashMap<SealPos, SealEntity>());
        }
        final ConcurrentHashMap<SealPos, SealEntity> se = SealHandler.sealEntities.get(world.provider.getDimension());
        if (se.containsKey(seal.getSealPos())) {
            return false;
        }
        se.put(seal.getSealPos(), seal);
        if (!world.isRemote) {
            seal.syncToClient(world);
            markChunkAsDirty(world.provider.getDimension(), seal.getSealPos().pos);
        }
        return true;
    }
    
    public static void tickSealEntities(final World world) {
        if (!SealHandler.sealEntities.containsKey(world.provider.getDimension())) {
            SealHandler.sealEntities.put(world.provider.getDimension(), new ConcurrentHashMap<SealPos, SealEntity>());
        }
        final ConcurrentHashMap<SealPos, SealEntity> se = SealHandler.sealEntities.get(world.provider.getDimension());
        ++SealHandler.count;
        for (final SealEntity sealEntity : se.values()) {
            if (world.isBlockLoaded(sealEntity.sealPos.pos)) {
                try {
                    boolean tick = true;
                    if (SealHandler.count % 20 == 0 && !sealEntity.seal.canPlaceAt(world, sealEntity.sealPos.pos, sealEntity.sealPos.face)) {
                        removeSealEntity(world, sealEntity.sealPos, false);
                        tick = false;
                    }
                    if (!tick) {
                        continue;
                    }
                    sealEntity.tickSealEntity(world);
                }
                catch (final Exception e) {
                    removeSealEntity(world, sealEntity.sealPos, false);
                }
            }
        }
    }
    
    public static void markChunkAsDirty(final int dim, final BlockPos bp) {
        final ChunkPos pos = new ChunkPos(bp);
        if (!AuraHandler.dirtyChunks.containsKey(dim)) {
            AuraHandler.dirtyChunks.put(dim, new CopyOnWriteArrayList<ChunkPos>());
        }
        final CopyOnWriteArrayList<ChunkPos> dc = AuraHandler.dirtyChunks.get(dim);
        if (!dc.contains(pos)) {
            dc.add(pos);
        }
    }
    
    static {
        SealHandler.types = new LinkedHashMap<String, ISeal>();
        SealHandler.lastID = 0;
        SealHandler.sealEntities = new ConcurrentHashMap<Integer, ConcurrentHashMap<SealPos, SealEntity>>();
        SealHandler.count = 0;
    }
}
