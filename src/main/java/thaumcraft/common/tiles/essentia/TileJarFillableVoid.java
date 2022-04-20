// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.essentia;

import net.minecraft.util.EnumFacing;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.aspects.Aspect;

public class TileJarFillableVoid extends TileJarFillable
{
    int count;
    
    public TileJarFillableVoid() {
        this.count = 0;
    }
    
    @Override
    public int addToContainer(final Aspect tt, int am) {
        final boolean up = this.amount < 250;
        if (am == 0) {
            return am;
        }
        if (tt == this.aspect || this.amount == 0) {
            this.aspect = tt;
            this.amount += am;
            am = 0;
            if (this.amount > 250) {
                if (this.world.rand.nextInt(250) == 0) {
                    AuraHelper.polluteAura(this.getWorld(), this.getPos(), 1.0f, true);
                }
                this.amount = 250;
            }
        }
        if (up) {
            this.syncTile(false);
            this.markDirty();
        }
        return am;
    }
    
    @Override
    public int getMinimumSuction() {
        return (this.aspectFilter != null) ? 48 : 32;
    }
    
    @Override
    public int getSuctionAmount(final EnumFacing loc) {
        if (this.aspectFilter != null && this.amount < 250) {
            return 48;
        }
        return 32;
    }
    
    @Override
    public void update() {
        if (!this.world.isRemote && ++this.count % 5 == 0) {
            this.fillJar();
        }
    }
}
