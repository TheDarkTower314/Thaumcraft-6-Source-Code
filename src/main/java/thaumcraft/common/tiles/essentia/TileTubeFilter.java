// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.essentia;

import thaumcraft.api.aspects.AspectList;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public class TileTubeFilter extends TileTube implements IAspectContainer
{
    public Aspect aspectFilter;
    
    public TileTubeFilter() {
        this.aspectFilter = null;
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        this.aspectFilter = Aspect.getAspect(nbttagcompound.getString("AspectFilter"));
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound = super.writeSyncNBT(nbttagcompound);
        if (this.aspectFilter != null) {
            nbttagcompound.setString("AspectFilter", this.aspectFilter.getTag());
        }
        return nbttagcompound;
    }
    
    @Override
    void calculateSuction(final Aspect filter, final boolean restrict, final boolean dir) {
        super.calculateSuction(this.aspectFilter, restrict, dir);
    }
    
    @Override
    public AspectList getAspects() {
        if (this.aspectFilter != null) {
            return new AspectList().add(this.aspectFilter, -1);
        }
        return null;
    }
    
    @Override
    public void setAspects(final AspectList aspects) {
    }
    
    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return false;
    }
    
    @Override
    public int addToContainer(final Aspect tag, final int amount) {
        return 0;
    }
    
    @Override
    public boolean takeFromContainer(final Aspect tag, final int amount) {
        return false;
    }
    
    @Override
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(final Aspect tag, final int amount) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }
    
    @Override
    public int containerContains(final Aspect tag) {
        return 0;
    }
}
