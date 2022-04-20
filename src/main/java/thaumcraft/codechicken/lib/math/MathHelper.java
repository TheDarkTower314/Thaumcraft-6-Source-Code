// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.math;

public class MathHelper
{
    public static final double phi = 1.618033988749894;
    public static final double pi = 3.141592653589793;
    public static final double todeg = 57.29577951308232;
    public static final double torad = 0.017453292519943;
    public static final double sqrt2 = 1.414213562373095;
    public static double[] SIN_TABLE;
    
    public static double sin(final double d) {
        return MathHelper.SIN_TABLE[(int)((float)d * 10430.378f) & 0xFFFF];
    }
    
    public static double cos(final double d) {
        return MathHelper.SIN_TABLE[(int)((float)d * 10430.378f + 16384.0f) & 0xFFFF];
    }
    
    public static float approachLinear(final float a, final float b, final float max) {
        return (a > b) ? ((a - b < max) ? b : (a - max)) : ((b - a < max) ? b : (a + max));
    }
    
    public static double approachLinear(final double a, final double b, final double max) {
        return (a > b) ? ((a - b < max) ? b : (a - max)) : ((b - a < max) ? b : (a + max));
    }
    
    public static float interpolate(final float a, final float b, final float d) {
        return a + (b - a) * d;
    }
    
    public static double interpolate(final double a, final double b, final double d) {
        return a + (b - a) * d;
    }
    
    public static double approachExp(final double a, final double b, final double ratio) {
        return a + (b - a) * ratio;
    }
    
    public static double approachExp(final double a, final double b, final double ratio, final double cap) {
        double d = (b - a) * ratio;
        if (Math.abs(d) > cap) {
            d = Math.signum(d) * cap;
        }
        return a + d;
    }
    
    public static double retreatExp(final double a, final double b, final double c, final double ratio, final double kick) {
        final double d = (Math.abs(c - a) + kick) * ratio;
        if (d > Math.abs(b - a)) {
            return b;
        }
        return a + Math.signum(b - a) * d;
    }
    
    public static double clip(double value, final double min, final double max) {
        if (value > max) {
            value = max;
        }
        if (value < min) {
            value = min;
        }
        return value;
    }
    
    public static boolean between(final double a, final double x, final double b) {
        return a <= x && x <= b;
    }
    
    public static int approachExpI(final int a, final int b, final double ratio) {
        final int r = (int)Math.round(approachExp(a, b, ratio));
        return (r == a) ? b : r;
    }
    
    public static int retreatExpI(final int a, final int b, final int c, final double ratio, final int kick) {
        final int r = (int)Math.round(retreatExp(a, b, c, ratio, kick));
        return (r == a) ? b : r;
    }
    
    public static int roundAway(final double d) {
        return (int)((d < 0.0) ? Math.floor(d) : Math.ceil(d));
    }
    
    public static int compare(final int a, final int b) {
        return (a == b) ? 0 : ((a < b) ? -1 : 1);
    }
    
    public static int compare(final double a, final double b) {
        return (a == b) ? 0 : ((a < b) ? -1 : 1);
    }
    
    static {
        MathHelper.SIN_TABLE = new double[65536];
        for (int i = 0; i < 65536; ++i) {
            MathHelper.SIN_TABLE[i] = Math.sin(i / 65536.0 * 2.0 * 3.141592653589793);
        }
        MathHelper.SIN_TABLE[0] = 0.0;
        MathHelper.SIN_TABLE[16384] = 1.0;
        MathHelper.SIN_TABLE[32768] = 0.0;
        MathHelper.SIN_TABLE[49152] = 1.0;
    }
}
