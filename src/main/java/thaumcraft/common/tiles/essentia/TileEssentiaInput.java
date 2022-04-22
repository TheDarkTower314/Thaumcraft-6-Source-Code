// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.essentia;

import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileEssentiaInput extends TileThaumcraft implements IEssentiaTransport, ITickable
{
    int count;
    
    public TileEssentiaInput() {
        count = 0;
    }
    
    @Override
    public boolean isConnectable(final EnumFacing face) {
        return face == getFacing().getOpposite();
    }
    
    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return face == getFacing().getOpposite();
    }
    
    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return false;
    }
    
    @Override
    public void setSuction(final Aspect aspect, final int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public Aspect getSuctionType(final EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing loc) {
        return 128;
    }
    
    @Override
    public Aspect getEssentiaType(final EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getEssentiaAmount(final EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return 0;
    }
    
    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return amount;
    }
    
    public void update() {
        if (!world.isRemote && ++count % 5 == 0) {
            fillJar();
        }
    }
    
    void fillJar() {
        final TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, getPos(), getFacing().getOpposite());
        if (te != null) {
            final IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(getFacing())) {
                return;
            }
            if (ic.getEssentiaAmount(getFacing()) > 0 && ic.getSuctionAmount(getFacing()) < getSuctionAmount(getFacing().getOpposite()) && getSuctionAmount(getFacing().getOpposite()) >= ic.getMinimumSuction()) {
                final Aspect ta = ic.getEssentiaType(getFacing());
                if (EssentiaHandler.addEssentia(this, ta, getFacing(), 16, false, 5)) {
                    ic.takeEssentia(ta, 1, getFacing());
                }
            }
        }
    }
}
