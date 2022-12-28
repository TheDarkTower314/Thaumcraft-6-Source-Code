package thaumcraft.common.tiles.essentia;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;


public class TileJarFillableVoid extends TileJarFillable
{
    int count;
    
    public TileJarFillableVoid() {
        count = 0;
    }
    
    @Override
    public int addToContainer(Aspect tt, int am) {
        boolean up = amount < 250;
        if (am == 0) {
            return am;
        }
        if (tt == aspect || amount == 0) {
            aspect = tt;
            amount += am;
            am = 0;
            if (amount > 250) {
                if (world.rand.nextInt(250) == 0) {
                    AuraHelper.polluteAura(getWorld(), getPos(), 1.0f, true);
                }
                amount = 250;
            }
        }
        if (up) {
            syncTile(false);
            markDirty();
        }
        return am;
    }
    
    @Override
    public int getMinimumSuction() {
        return (aspectFilter != null) ? 48 : 32;
    }
    
    @Override
    public int getSuctionAmount(EnumFacing loc) {
        if (aspectFilter != null && amount < 250) {
            return 48;
        }
        return 32;
    }
    
    @Override
    public void update() {
        if (!world.isRemote && ++count % 5 == 0) {
            fillJar();
        }
    }
}
