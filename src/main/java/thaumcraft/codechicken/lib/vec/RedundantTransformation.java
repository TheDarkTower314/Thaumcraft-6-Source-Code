// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RedundantTransformation extends Transformation
{
    @Override
    public void apply(final Vector3 vec) {
    }
    
    @Override
    public void apply(final Matrix4 mat) {
    }
    
    @Override
    public void applyN(final Vector3 normal) {
    }
    
    @Override
    public Transformation at(final Vector3 point) {
        return this;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void glApply() {
    }
    
    @Override
    public Transformation inverse() {
        return this;
    }
    
    @Override
    public Transformation merge(final Transformation next) {
        return next;
    }
    
    @Override
    public boolean isRedundant() {
        return true;
    }
    
    @Override
    public String toString() {
        return "Nothing()";
    }
}