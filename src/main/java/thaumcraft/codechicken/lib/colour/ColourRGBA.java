// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.colour;

public class ColourRGBA extends Colour
{
    public ColourRGBA(final int colour) {
        super(colour >> 24 & 0xFF, colour >> 16 & 0xFF, colour >> 8 & 0xFF, colour & 0xFF);
    }
    
    public ColourRGBA(final double r, final double g, final double b, final double a) {
        super((int)(255.0 * r), (int)(255.0 * g), (int)(255.0 * b), (int)(255.0 * a));
    }
    
    public ColourRGBA(final int r, final int g, final int b, final int a) {
        super(r, g, b, a);
    }
    
    public ColourRGBA(final ColourRGBA colour) {
        super(colour);
    }
    
    @Override
    public int pack() {
        return pack(this);
    }
    
    @Override
    public Colour copy() {
        return new ColourRGBA(this);
    }
    
    public static int pack(final Colour colour) {
        return (colour.r & 0xFF) << 24 | (colour.g & 0xFF) << 16 | (colour.b & 0xFF) << 8 | (colour.a & 0xFF);
    }
    
    public static int multiply(final int c1, final int c2) {
        if (c1 == -1) {
            return c2;
        }
        if (c2 == -1) {
            return c1;
        }
        final int r = ((c1 >>> 24) * (c2 >>> 24) & 0xFF00) << 16;
        final int g = ((c1 >> 16 & 0xFF) * (c2 >> 16 & 0xFF) & 0xFF00) << 8;
        final int b = (c1 >> 8 & 0xFF) * (c2 >> 8 & 0xFF) & 0xFF00;
        final int a = (c1 & 0xFF) * (c2 & 0xFF) >> 8;
        return r | g | b | a;
    }
    
    public static int multiplyC(final int c, final float f) {
        final int r = (int)((c >>> 24) * f);
        final int g = (int)((c >> 16 & 0xFF) * f);
        final int b = (int)((c >> 8 & 0xFF) * f);
        return r << 24 | g << 16 | b << 8 | (c & 0xFF);
    }
}
