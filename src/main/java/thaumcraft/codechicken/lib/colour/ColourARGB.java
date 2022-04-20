// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.colour;

public class ColourARGB extends Colour
{
    public ColourARGB(final int colour) {
        super(colour >> 16 & 0xFF, colour >> 8 & 0xFF, colour & 0xFF, colour >> 24 & 0xFF);
    }
    
    public ColourARGB(final int a, final int r, final int g, final int b) {
        super(r, g, b, a);
    }
    
    public ColourARGB(final ColourARGB colour) {
        super(colour);
    }
    
    @Override
    public ColourARGB copy() {
        return new ColourARGB(this);
    }
    
    @Override
    public int pack() {
        return pack(this);
    }
    
    public static int pack(final Colour colour) {
        return (colour.a & 0xFF) << 24 | (colour.r & 0xFF) << 16 | (colour.g & 0xFF) << 8 | (colour.b & 0xFF);
    }
}
