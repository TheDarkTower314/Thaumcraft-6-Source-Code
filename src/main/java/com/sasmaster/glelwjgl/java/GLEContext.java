// 
// Decompiled by Procyon v0.6.0
// 

package com.sasmaster.glelwjgl.java;

public class GLEContext
{
    public static final String VERSION;
    private int joinStyle;
    protected int ncp;
    protected double[][] contour;
    protected double[][] contourNormal;
    protected double[] up;
    protected int npoints;
    protected double[][] pointArray;
    protected float[][] colourArray;
    protected double[][][] xformArray;
    
    public GLEContext() {
        this.joinStyle = 1;
        this.ncp = 0;
        this.contour = null;
        this.contourNormal = null;
        this.up = null;
        this.npoints = 0;
        this.pointArray = null;
        this.colourArray = null;
        this.xformArray = null;
    }
    
    protected final int getJoinStyle() {
        return this.joinStyle;
    }
    
    protected final void setJoinStyle(final int style) {
        this.joinStyle = style;
    }
    
    static {
        VERSION = new String("$Id: GLEContext.java,v 1.1 1998/05/03 16:18:47 descarte Exp descarte $");
    }
}
