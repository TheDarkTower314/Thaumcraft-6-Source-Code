// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

public abstract class ITransformation<Vector, Transformation extends ITransformation>
{
    public abstract void apply(final Vector p0);
    
    public abstract Transformation at(final Vector p0);
    
    public abstract Transformation with(final Transformation p0);
    
    public Transformation merge(final Transformation next) {
        return null;
    }
    
    public boolean isRedundant() {
        return false;
    }
    
    public abstract Transformation inverse();
    
    public Transformation $plus$plus(final Transformation t) {
        return with(t);
    }
}
