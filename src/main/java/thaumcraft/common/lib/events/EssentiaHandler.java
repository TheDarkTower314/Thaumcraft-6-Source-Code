package thaumcraft.common.lib.events;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.internal.WorldCoordinates;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXEssentiaSource;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;


public class EssentiaHandler
{
    static int DELAY = 10000;
    private static HashMap<WorldCoordinates, ArrayList<WorldCoordinates>> sources;
    private static HashMap<WorldCoordinates, Long> sourcesDelay;
    private static TileEntity lat;
    private static TileEntity las;
    private static Aspect lasp;
    private static int lext;
    public static ConcurrentHashMap<String, EssentiaSourceFX> sourceFX;
    
    public static boolean drainEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, int ext) {
        return drainEssentia(tile, aspect, direction, range, false, ext);
    }
    
    public static boolean drainEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror, int ext) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.getPos(), tile.getWorld().provider.getDimension());
        if (!EssentiaHandler.sources.containsKey(tileLoc)) {
            getSources(tile.getWorld(), tileLoc, direction, range);
            return EssentiaHandler.sources.containsKey(tileLoc) && drainEssentia(tile, aspect, direction, range, ignoreMirror, ext);
        }
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
            }
            if (ignoreMirror && sourceTile instanceof TileMirrorEssentia) {
                continue;
            }
            if (as.takeFromContainer(aspect, 1)) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXEssentiaSource(tile.getPos(), (byte)(tile.getPos().getX() - source.pos.getX()), (byte)(tile.getPos().getY() - source.pos.getY()), (byte)(tile.getPos().getZ() - source.pos.getZ()), aspect.getColor(), ext), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 32.0));
                return true;
            }
        }
        EssentiaHandler.sources.remove(tileLoc);
        EssentiaHandler.sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
        return false;
    }
    
    public static boolean drainEssentiaWithConfirmation(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror, int ext) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.getPos(), tile.getWorld().provider.getDimension());
        if (!EssentiaHandler.sources.containsKey(tileLoc)) {
            getSources(tile.getWorld(), tileLoc, direction, range);
            return EssentiaHandler.sources.containsKey(tileLoc) && drainEssentiaWithConfirmation(tile, aspect, direction, range, ignoreMirror, ext);
        }
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
            }
            if (ignoreMirror && sourceTile instanceof TileMirrorEssentia) {
                continue;
            }
            if (as.doesContainerContainAmount(aspect, 1)) {
                EssentiaHandler.las = sourceTile;
                EssentiaHandler.lasp = aspect;
                EssentiaHandler.lat = tile;
                EssentiaHandler.lext = ext;
                return true;
            }
        }
        EssentiaHandler.sources.remove(tileLoc);
        EssentiaHandler.sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
        return false;
    }
    
    public static void confirmDrain() {
        if (EssentiaHandler.las != null && EssentiaHandler.lasp != null && EssentiaHandler.lat != null) {
            IAspectSource as = (IAspectSource)EssentiaHandler.las;
            if (as.takeFromContainer(EssentiaHandler.lasp, 1)) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXEssentiaSource(EssentiaHandler.lat.getPos(), (byte)(EssentiaHandler.lat.getPos().getX() - EssentiaHandler.las.getPos().getX()), (byte)(EssentiaHandler.lat.getPos().getY() - EssentiaHandler.las.getPos().getY()), (byte)(EssentiaHandler.lat.getPos().getZ() - EssentiaHandler.las.getPos().getZ()), EssentiaHandler.lasp.getColor(), EssentiaHandler.lext), new NetworkRegistry.TargetPoint(EssentiaHandler.lat.getWorld().provider.getDimension(), EssentiaHandler.lat.getPos().getX(), EssentiaHandler.lat.getPos().getY(), EssentiaHandler.lat.getPos().getZ(), 32.0));
            }
        }
        EssentiaHandler.las = null;
        EssentiaHandler.lasp = null;
        EssentiaHandler.lat = null;
    }
    
    public static boolean addEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror, int ext) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.getPos(), tile.getWorld().provider.getDimension());
        if (!EssentiaHandler.sources.containsKey(tileLoc)) {
            getSources(tile.getWorld(), tileLoc, direction, range);
            return EssentiaHandler.sources.containsKey(tileLoc) && addEssentia(tile, aspect, direction, range, ignoreMirror, ext);
        }
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        ArrayList<WorldCoordinates> empties = new ArrayList<WorldCoordinates>();
        for (WorldCoordinates source : es) {
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
            }
            if (ignoreMirror && sourceTile instanceof TileMirrorEssentia) {
                continue;
            }
            if (as.doesContainerAccept(aspect) && (as.getAspects() == null || as.getAspects().visSize() == 0)) {
                empties.add(source);
            }
            else {
                if (as.doesContainerAccept(aspect) && as.addToContainer(aspect, 1) <= 0) {
                    PacketHandler.INSTANCE.sendToAllAround(new PacketFXEssentiaSource(source.pos, (byte)(source.pos.getX() - tile.getPos().getX()), (byte)(source.pos.getY() - tile.getPos().getY()), (byte)(source.pos.getZ() - tile.getPos().getZ()), aspect.getColor(), ext), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 32.0));
                    return true;
                }
                continue;
            }
        }
        for (WorldCoordinates source : empties) {
            if (source != null) {
                if (source.pos == null) {
                    continue;
                }
                TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
                if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                    break;
                }
                IAspectSource as = (IAspectSource)sourceTile;
                if (aspect != null && as.doesContainerAccept(aspect) && as.addToContainer(aspect, 1) <= 0) {
                    PacketHandler.INSTANCE.sendToAllAround(new PacketFXEssentiaSource(source.pos, (byte)(source.pos.getX() - tile.getPos().getX()), (byte)(source.pos.getY() - tile.getPos().getY()), (byte)(source.pos.getZ() - tile.getPos().getZ()), aspect.getColor(), ext), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 32.0));
                    return true;
                }
                continue;
            }
        }
        EssentiaHandler.sources.remove(tileLoc);
        EssentiaHandler.sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
        return false;
    }
    
    public static boolean findEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.getPos(), tile.getWorld().provider.getDimension());
        if (!EssentiaHandler.sources.containsKey(tileLoc)) {
            getSources(tile.getWorld(), tileLoc, direction, range);
            return EssentiaHandler.sources.containsKey(tileLoc) && findEssentia(tile, aspect, direction, range, ignoreMirror);
        }
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
            }
            if (ignoreMirror && sourceTile instanceof TileMirrorEssentia) {
                continue;
            }
            if (as.doesContainerContainAmount(aspect, 1)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean canAcceptEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.getPos(), tile.getWorld().provider.getDimension());
        if (!EssentiaHandler.sources.containsKey(tileLoc)) {
            getSources(tile.getWorld(), tileLoc, direction, range);
            return EssentiaHandler.sources.containsKey(tileLoc) && findEssentia(tile, aspect, direction, range, ignoreMirror);
        }
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            if (ignoreMirror && sourceTile instanceof TileMirrorEssentia) {
                continue;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (!as.isBlocked() && as.doesContainerAccept(aspect)) {
                return true;
            }
        }
        return false;
    }
    
    private static void getSources(World world, WorldCoordinates tileLoc, EnumFacing direction, int range) {
        if (EssentiaHandler.sourcesDelay.containsKey(tileLoc)) {
            long d = EssentiaHandler.sourcesDelay.get(tileLoc);
            if (d > System.currentTimeMillis()) {
                return;
            }
            EssentiaHandler.sourcesDelay.remove(tileLoc);
        }
        TileEntity sourceTile = world.getTileEntity(tileLoc.pos);
        ArrayList<WorldCoordinates> sourceList = new ArrayList<WorldCoordinates>();
        int start = 0;
        if (direction == null) {
            start = -range;
            direction = EnumFacing.UP;
        }
        int xx = 0;
        int yy = 0;
        int zz = 0;
        for (int aa = -range; aa <= range; ++aa) {
            for (int bb = -range; bb <= range; ++bb) {
                for (int cc = start; cc < range; ++cc) {
                    if (aa != 0 || bb != 0 || cc != 0) {
                        xx = tileLoc.pos.getX();
                        yy = tileLoc.pos.getY();
                        zz = tileLoc.pos.getZ();
                        if (direction.getFrontOffsetY() != 0) {
                            xx += aa;
                            yy += cc * direction.getFrontOffsetY();
                            zz += bb;
                        }
                        else if (direction.getFrontOffsetX() == 0) {
                            xx += aa;
                            yy += bb;
                            zz += cc * direction.getFrontOffsetZ();
                        }
                        else {
                            xx += cc * direction.getFrontOffsetX();
                            yy += aa;
                            zz += bb;
                        }
                        TileEntity te = world.getTileEntity(new BlockPos(xx, yy, zz));
                        if (te != null && te instanceof IAspectSource) {
                            if (!(sourceTile instanceof TileMirrorEssentia) || !(te instanceof TileMirrorEssentia) || sourceTile.getPos().getX() != ((TileMirrorEssentia)te).linkX || sourceTile.getPos().getY() != ((TileMirrorEssentia)te).linkY || sourceTile.getPos().getZ() != ((TileMirrorEssentia)te).linkZ || sourceTile.getWorld().provider.getDimension() != ((TileMirrorEssentia)te).linkDim) {
                                sourceList.add(new WorldCoordinates(new BlockPos(xx, yy, zz), world.provider.getDimension()));
                            }
                        }
                    }
                }
            }
        }
        if (sourceList.size() > 0) {
            ArrayList<WorldCoordinates> sourceList2 = new ArrayList<WorldCoordinates>();
        Label_0467:
            for (WorldCoordinates wc : sourceList) {
                double dist = wc.getDistanceSquaredToWorldCoordinates(tileLoc);
                if (!sourceList2.isEmpty()) {
                    for (int a = 0; a < sourceList2.size(); ++a) {
                        double d2 = sourceList2.get(a).getDistanceSquaredToWorldCoordinates(tileLoc);
                        if (dist < d2) {
                            sourceList2.add(a, wc);
                            continue Label_0467;
                        }
                    }
                }
                sourceList2.add(wc);
            }
            EssentiaHandler.sources.put(tileLoc, sourceList2);
        }
        else {
            EssentiaHandler.sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
        }
    }
    
    public static void refreshSources(TileEntity tile) {
        EssentiaHandler.sources.remove(new WorldCoordinates(tile.getPos(), tile.getWorld().provider.getDimension()));
    }
    
    static {
        EssentiaHandler.sources = new HashMap<WorldCoordinates, ArrayList<WorldCoordinates>>();
        EssentiaHandler.sourcesDelay = new HashMap<WorldCoordinates, Long>();
        EssentiaHandler.lat = null;
        EssentiaHandler.las = null;
        EssentiaHandler.lasp = null;
        EssentiaHandler.lext = 0;
        EssentiaHandler.sourceFX = new ConcurrentHashMap<String, EssentiaSourceFX>();
    }
    
    public static class EssentiaSourceFX
    {
        public BlockPos start;
        public BlockPos end;
        public int color;
        public int ext;
        
        public EssentiaSourceFX(BlockPos start, BlockPos end, int color, int ext) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.ext = ext;
        }
    }
}
