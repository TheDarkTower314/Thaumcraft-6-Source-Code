// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import thaumcraft.codechicken.lib.math.MathHelper;
import thaumcraft.codechicken.lib.util.Copyable;

public class Quat implements Copyable<Quat>
{
    public double x;
    public double y;
    public double z;
    public double s;
    
    public Quat() {
        this.s = 1.0;
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }
    
    public Quat(final Quat quat) {
        this.x = quat.x;
        this.y = quat.y;
        this.z = quat.z;
        this.s = quat.s;
    }
    
    public Quat(final double d, final double d1, final double d2, final double d3) {
        this.x = d1;
        this.y = d2;
        this.z = d3;
        this.s = d;
    }
    
    public Quat set(final Quat quat) {
        this.x = quat.x;
        this.y = quat.y;
        this.z = quat.z;
        this.s = quat.s;
        return this;
    }
    
    public Quat set(final double d, final double d1, final double d2, final double d3) {
        this.x = d1;
        this.y = d2;
        this.z = d3;
        this.s = d;
        return this;
    }
    
    public static Quat aroundAxis(final double ax, final double ay, final double az, final double angle) {
        return new Quat().setAroundAxis(ax, ay, az, angle);
    }
    
    public static Quat aroundAxis(final Vector3 axis, final double angle) {
        return aroundAxis(axis.x, axis.y, axis.z, angle);
    }
    
    public Quat setAroundAxis(final double ax, final double ay, final double az, double angle) {
        angle *= 0.5;
        final double d4 = MathHelper.sin(angle);
        return this.set(MathHelper.cos(angle), ax * d4, ay * d4, az * d4);
    }
    
    public Quat setAroundAxis(final Vector3 axis, final double angle) {
        return this.setAroundAxis(axis.x, axis.y, axis.z, angle);
    }
    
    public Quat multiply(final Quat quat) {
        final double d = this.s * quat.s - this.x * quat.x - this.y * quat.y - this.z * quat.z;
        final double d2 = this.s * quat.x + this.x * quat.s - this.y * quat.z + this.z * quat.y;
        final double d3 = this.s * quat.y + this.x * quat.z + this.y * quat.s - this.z * quat.x;
        final double d4 = this.s * quat.z - this.x * quat.y + this.y * quat.x + this.z * quat.s;
        this.s = d;
        this.x = d2;
        this.y = d3;
        this.z = d4;
        return this;
    }
    
    public Quat rightMultiply(final Quat quat) {
        final double d = this.s * quat.s - this.x * quat.x - this.y * quat.y - this.z * quat.z;
        final double d2 = this.s * quat.x + this.x * quat.s + this.y * quat.z - this.z * quat.y;
        final double d3 = this.s * quat.y - this.x * quat.z + this.y * quat.s + this.z * quat.x;
        final double d4 = this.s * quat.z + this.x * quat.y - this.y * quat.x + this.z * quat.s;
        this.s = d;
        this.x = d2;
        this.y = d3;
        this.z = d4;
        return this;
    }
    
    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.s * this.s);
    }
    
    public Quat normalize() {
        double d = this.mag();
        if (d != 0.0) {
            d = 1.0 / d;
            this.x *= d;
            this.y *= d;
            this.z *= d;
            this.s *= d;
        }
        return this;
    }
    
    @Override
    public Quat copy() {
        return new Quat(this);
    }
    
    public void rotate(final Vector3 vec) {
        final double d = -this.x * vec.x - this.y * vec.y - this.z * vec.z;
        final double d2 = this.s * vec.x + this.y * vec.z - this.z * vec.y;
        final double d3 = this.s * vec.y - this.x * vec.z + this.z * vec.x;
        final double d4 = this.s * vec.z + this.x * vec.y - this.y * vec.x;
        vec.x = d2 * this.s - d * this.x - d3 * this.z + d4 * this.y;
        vec.y = d3 * this.s - d * this.y + d2 * this.z - d4 * this.x;
        vec.z = d4 * this.s - d * this.z - d2 * this.y + d3 * this.x;
    }
    
    @Override
    public String toString() {
        final MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Quat(" + new BigDecimal(this.s, cont) + ", " + new BigDecimal(this.x, cont) + ", " + new BigDecimal(this.y, cont) + ", " + new BigDecimal(this.z, cont) + ")";
    }
    
    public Rotation rotation() {
        return new Rotation(this);
    }
}
