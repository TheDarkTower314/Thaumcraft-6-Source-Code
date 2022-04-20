// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.essentia;

import thaumcraft.api.aspects.Aspect;

public class TileTubeRestrict extends TileTube
{
    @Override
    void calculateSuction(final Aspect filter, final boolean restrict, final boolean dir) {
        super.calculateSuction(filter, true, dir);
    }
}
