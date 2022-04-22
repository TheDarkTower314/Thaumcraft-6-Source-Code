// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.render.uv;

import thaumcraft.codechicken.lib.vec.ITransformation;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import thaumcraft.codechicken.lib.util.Copyable;

public class UV implements Copyable<UV>
{
    public double u;
    public double v;
    public int tex;
    
    public UV() {
    }
    
    public UV(final double u, final double v) {
        this(u, v, 0);
    }
    
    public UV(final double u, final double v, final int tex) {
        this.u = u;
        this.v = v;
        this.tex = tex;
    }
    
    public UV(final UV uv) {
        this(uv.u, uv.v, uv.tex);
    }
    
    public UV set(final double u, final double v, final int tex) {
        this.u = u;
        this.v = v;
        this.tex = tex;
        return this;
    }
    
    public UV set(final double u, final double v) {
        return set(u, v, tex);
    }
    
    public UV set(final UV uv) {
        return set(uv.u, uv.v, uv.tex);
    }
    
    @Override
    public UV copy() {
        return new UV(this);
    }
    
    public UV add(final UV uv) {
        u += uv.u;
        v += uv.v;
        return this;
    }
    
    public UV multiply(final double d) {
        u *= d;
        v *= d;
        return this;
    }
    
    @Override
    public String toString() {
        final MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "UV(" + new BigDecimal(u, cont) + ", " + new BigDecimal(v, cont) + ")";
    }
    
    public UV apply(final UVTransformation t) {
        t.apply(this);
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof UV)) {
            return false;
        }
        final UV uv = (UV)o;
        return u == uv.u && v == uv.v;
    }
}
