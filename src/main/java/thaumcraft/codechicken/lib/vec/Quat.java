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
        s = 1.0;
        x = 0.0;
        y = 0.0;
        z = 0.0;
    }
    
    public Quat(Quat quat) {
        x = quat.x;
        y = quat.y;
        z = quat.z;
        s = quat.s;
    }
    
    public Quat(double d, double d1, double d2, double d3) {
        x = d1;
        y = d2;
        z = d3;
        s = d;
    }
    
    public Quat set(Quat quat) {
        x = quat.x;
        y = quat.y;
        z = quat.z;
        s = quat.s;
        return this;
    }
    
    public Quat set(double d, double d1, double d2, double d3) {
        x = d1;
        y = d2;
        z = d3;
        s = d;
        return this;
    }
    
    public static Quat aroundAxis(double ax, double ay, double az, double angle) {
        return new Quat().setAroundAxis(ax, ay, az, angle);
    }
    
    public static Quat aroundAxis(Vector3 axis, double angle) {
        return aroundAxis(axis.x, axis.y, axis.z, angle);
    }
    
    public Quat setAroundAxis(double ax, double ay, double az, double angle) {
        angle *= 0.5;
        double d4 = MathHelper.sin(angle);
        return set(MathHelper.cos(angle), ax * d4, ay * d4, az * d4);
    }
    
    public Quat setAroundAxis(Vector3 axis, double angle) {
        return setAroundAxis(axis.x, axis.y, axis.z, angle);
    }
    
    public Quat multiply(Quat quat) {
        double d = s * quat.s - x * quat.x - y * quat.y - z * quat.z;
        double d2 = s * quat.x + x * quat.s - y * quat.z + z * quat.y;
        double d3 = s * quat.y + x * quat.z + y * quat.s - z * quat.x;
        double d4 = s * quat.z - x * quat.y + y * quat.x + z * quat.s;
        s = d;
        x = d2;
        y = d3;
        z = d4;
        return this;
    }
    
    public Quat rightMultiply(Quat quat) {
        double d = s * quat.s - x * quat.x - y * quat.y - z * quat.z;
        double d2 = s * quat.x + x * quat.s + y * quat.z - z * quat.y;
        double d3 = s * quat.y - x * quat.z + y * quat.s + z * quat.x;
        double d4 = s * quat.z + x * quat.y - y * quat.x + z * quat.s;
        s = d;
        x = d2;
        y = d3;
        z = d4;
        return this;
    }
    
    public double mag() {
        return Math.sqrt(x * x + y * y + z * z + s * s);
    }
    
    public Quat normalize() {
        double d = mag();
        if (d != 0.0) {
            d = 1.0 / d;
            x *= d;
            y *= d;
            z *= d;
            s *= d;
        }
        return this;
    }
    
    @Override
    public Quat copy() {
        return new Quat(this);
    }
    
    public void rotate(Vector3 vec) {
        double d = -x * vec.x - y * vec.y - z * vec.z;
        double d2 = s * vec.x + y * vec.z - z * vec.y;
        double d3 = s * vec.y - x * vec.z + z * vec.x;
        double d4 = s * vec.z + x * vec.y - y * vec.x;
        vec.x = d2 * s - d * x - d3 * z + d4 * y;
        vec.y = d3 * s - d * y + d2 * z - d4 * x;
        vec.z = d4 * s - d * z - d2 * y + d3 * x;
    }
    
    @Override
    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Quat(" + new BigDecimal(s, cont) + ", " + new BigDecimal(x, cont) + ", " + new BigDecimal(y, cont) + ", " + new BigDecimal(z, cont) + ")";
    }
    
    public Rotation rotation() {
        return new Rotation(this);
    }
}
