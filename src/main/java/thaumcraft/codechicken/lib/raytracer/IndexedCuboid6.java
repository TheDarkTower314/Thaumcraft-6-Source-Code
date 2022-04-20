// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.raytracer;

import thaumcraft.codechicken.lib.vec.Cuboid6;

public class IndexedCuboid6 extends Cuboid6
{
    public Object data;
    
    public IndexedCuboid6(final Object data, final Cuboid6 cuboid) {
        super(cuboid);
        this.data = data;
    }
}
