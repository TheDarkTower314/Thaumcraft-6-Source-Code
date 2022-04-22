// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.render;

import thaumcraft.codechicken.lib.render.uv.UVTransformation;
import thaumcraft.codechicken.lib.vec.Transformation;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import thaumcraft.codechicken.lib.render.uv.UV;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.codechicken.lib.util.Copyable;

public class Vertex5 implements Copyable<Vertex5>
{
    public Vector3 vec;
    public UV uv;
    
    public Vertex5() {
        this(new Vector3(), new UV());
    }
    
    public Vertex5(final Vector3 vert, final UV uv) {
        vec = vert;
        this.uv = uv;
    }
    
    public Vertex5(final Vector3 vert, final double u, final double v) {
        this(vert, new UV(u, v));
    }
    
    public Vertex5(final double x, final double y, final double z, final double u, final double v) {
        this(new Vector3(x, y, z), new UV(u, v));
    }
    
    public Vertex5 set(final double x, final double y, final double z, final double u, final double v) {
        vec.set(x, y, z);
        uv.set(u, v);
        return this;
    }
    
    public Vertex5 set(final double x, final double y, final double z, final double u, final double v, final int tex) {
        vec.set(x, y, z);
        uv.set(u, v, tex);
        return this;
    }
    
    public Vertex5 set(final Vertex5 vert) {
        vec.set(vert.vec);
        uv.set(vert.uv);
        return this;
    }
    
    public Vertex5(final Vertex5 vertex5) {
        this(vertex5.vec.copy(), vertex5.uv.copy());
    }
    
    @Override
    public Vertex5 copy() {
        return new Vertex5(this);
    }
    
    @Override
    public String toString() {
        final MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Vertex: (" + new BigDecimal(vec.x, cont) + ", " + new BigDecimal(vec.y, cont) + ", " + new BigDecimal(vec.z, cont) + ") (" + new BigDecimal(uv.u, cont) + ", " + new BigDecimal(uv.v, cont) + ")";
    }
    
    public Vertex5 apply(final Transformation t) {
        vec.apply(t);
        return this;
    }
    
    public Vertex5 apply(final UVTransformation t) {
        uv.apply(t);
        return this;
    }
}
