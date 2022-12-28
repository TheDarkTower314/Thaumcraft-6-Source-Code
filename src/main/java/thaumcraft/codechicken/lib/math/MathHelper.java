package thaumcraft.codechicken.lib.math;


public class MathHelper
{
    public static double phi = 1.618033988749894;
    public static double pi = 3.141592653589793;
    public static double todeg = 57.29577951308232;
    public static double torad = 0.017453292519943;
    public static double sqrt2 = 1.414213562373095;
    public static double[] SIN_TABLE;
    
    public static double sin(double d) {
        return MathHelper.SIN_TABLE[(int)((float)d * 10430.378f) & 0xFFFF];
    }
    
    public static double cos(double d) {
        return MathHelper.SIN_TABLE[(int)((float)d * 10430.378f + 16384.0f) & 0xFFFF];
    }
    
    public static float approachLinear(float a, float b, float max) {
        return (a > b) ? ((a - b < max) ? b : (a - max)) : ((b - a < max) ? b : (a + max));
    }
    
    public static double approachLinear(double a, double b, double max) {
        return (a > b) ? ((a - b < max) ? b : (a - max)) : ((b - a < max) ? b : (a + max));
    }
    
    public static float interpolate(float a, float b, float d) {
        return a + (b - a) * d;
    }
    
    public static double interpolate(double a, double b, double d) {
        return a + (b - a) * d;
    }
    
    public static double approachExp(double a, double b, double ratio) {
        return a + (b - a) * ratio;
    }
    
    public static double approachExp(double a, double b, double ratio, double cap) {
        double d = (b - a) * ratio;
        if (Math.abs(d) > cap) {
            d = Math.signum(d) * cap;
        }
        return a + d;
    }
    
    public static double retreatExp(double a, double b, double c, double ratio, double kick) {
        double d = (Math.abs(c - a) + kick) * ratio;
        if (d > Math.abs(b - a)) {
            return b;
        }
        return a + Math.signum(b - a) * d;
    }
    
    public static double clip(double value, double min, double max) {
        if (value > max) {
            value = max;
        }
        if (value < min) {
            value = min;
        }
        return value;
    }
    
    public static boolean between(double a, double x, double b) {
        return a <= x && x <= b;
    }
    
    public static int approachExpI(int a, int b, double ratio) {
        int r = (int)Math.round(approachExp(a, b, ratio));
        return (r == a) ? b : r;
    }
    
    public static int retreatExpI(int a, int b, int c, double ratio, int kick) {
        int r = (int)Math.round(retreatExp(a, b, c, ratio, kick));
        return (r == a) ? b : r;
    }
    
    public static int roundAway(double d) {
        return (int)((d < 0.0) ? Math.floor(d) : Math.ceil(d));
    }
    
    public static int compare(int a, int b) {
        return (a == b) ? 0 : ((a < b) ? -1 : 1);
    }
    
    public static int compare(double a, double b) {
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
