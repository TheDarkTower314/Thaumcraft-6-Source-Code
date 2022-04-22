// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GlStateManager;

public class Scale extends Transformation
{
    public Vector3 factor;
    
    public Scale(final Vector3 factor) {
        this.factor = factor;
    }
    
    public Scale(final double factor) {
        this(new Vector3(factor, factor, factor));
    }
    
    public Scale(final double x, final double y, final double z) {
        this(new Vector3(x, y, z));
    }
    
    @Override
    public void apply(final Vector3 vec) {
        vec.multiply(factor);
    }
    
    @Override
    public void applyN(final Vector3 normal) {
    }
    
    @Override
    public void apply(final Matrix4 mat) {
        mat.scale(factor);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void glApply() {
        GlStateManager.scale(factor.x, factor.y, factor.z);
    }
    
    @Override
    public Transformation inverse() {
        return new Scale(1.0 / factor.x, 1.0 / factor.y, 1.0 / factor.z);
    }
    
    @Override
    public Transformation merge(final Transformation next) {
        if (next instanceof Scale) {
            return new Scale(factor.copy().multiply(((Scale)next).factor));
        }
        return null;
    }
    
    @Override
    public boolean isRedundant() {
        return factor.equalsT(Vector3.one);
    }
    
    @Override
    public String toString() {
        final MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Scale(" + new BigDecimal(factor.x, cont) + ", " + new BigDecimal(factor.y, cont) + ", " + new BigDecimal(factor.z, cont) + ")";
    }
}
