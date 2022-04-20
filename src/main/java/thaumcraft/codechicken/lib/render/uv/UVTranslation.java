// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.render.uv;

import thaumcraft.codechicken.lib.vec.ITransformation;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import thaumcraft.codechicken.lib.math.MathHelper;

public class UVTranslation extends UVTransformation
{
    public double du;
    public double dv;
    
    public UVTranslation(final double u, final double v) {
        this.du = u;
        this.dv = v;
    }
    
    @Override
    public void apply(final UV uv) {
        uv.u += this.du;
        uv.v += this.dv;
    }
    
    @Override
    public UVTransformation at(final UV point) {
        return this;
    }
    
    @Override
    public UVTransformation inverse() {
        return new UVTranslation(-this.du, -this.dv);
    }
    
    @Override
    public UVTransformation merge(final UVTransformation next) {
        if (next instanceof UVTranslation) {
            final UVTranslation t = (UVTranslation)next;
            return new UVTranslation(this.du + t.du, this.dv + t.dv);
        }
        return null;
    }
    
    @Override
    public boolean isRedundant() {
        return MathHelper.between(-1.0E-5, this.du, 1.0E-5) && MathHelper.between(-1.0E-5, this.dv, 1.0E-5);
    }
    
    @Override
    public String toString() {
        final MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "UVTranslation(" + new BigDecimal(this.du, cont) + ", " + new BigDecimal(this.dv, cont) + ")";
    }
}
