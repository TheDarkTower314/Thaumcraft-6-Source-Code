package thaumcraft.common.tiles.essentia;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileEssentiaOutput extends TileThaumcraft implements IEssentiaTransport, ITickable
{
    int count;
    
    public TileEssentiaOutput() {
        count = 0;
    }
    
    @Override
    public boolean isConnectable(EnumFacing face) {
        return face == getFacing().getOpposite();
    }
    
    @Override
    public boolean canInputFrom(EnumFacing face) {
        return false;
    }
    
    @Override
    public boolean canOutputTo(EnumFacing face) {
        return face == getFacing().getOpposite();
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public Aspect getSuctionType(EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getSuctionAmount(EnumFacing loc) {
        return 0;
    }
    
    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return null;
    }
    
    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return amount;
    }
    
    public void update() {
        if (!world.isRemote && ++count % 5 == 0) {
            fillBuffer();
        }
    }
    
    void fillBuffer() {
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, getPos(), getFacing().getOpposite());
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canInputFrom(getFacing())) {
                return;
            }
            if (ic.getSuctionAmount(getFacing()) > 0 && ic.getSuctionType(getFacing()) != null) {
                Aspect ta = ic.getSuctionType(getFacing());
                if (EssentiaHandler.drainEssentiaWithConfirmation(this, ta, getFacing(), 16, false, 5) && ic.addEssentia(ta, 1, getFacing()) > 0) {
                    EssentiaHandler.confirmDrain();
                }
            }
        }
    }
}
