// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

public class SwapYZ extends VariableTransformation
{
    public SwapYZ() {
        super(new Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0));
    }
    
    @Override
    public void apply(final Vector3 vec) {
        final double vz = vec.z;
        vec.z = vec.y;
        vec.y = vz;
    }
    
    @Override
    public Transformation inverse() {
        return this;
    }
}
