package com.sasmaster.glelwjgl.java;


public class GLEContext
{
    public static String VERSION;
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
        joinStyle = 1;
        ncp = 0;
        contour = null;
        contourNormal = null;
        up = null;
        npoints = 0;
        pointArray = null;
        colourArray = null;
        xformArray = null;
    }
    
    protected int getJoinStyle() {
        return joinStyle;
    }
    
    protected void setJoinStyle(int style) {
        joinStyle = style;
    }
    
    static {
        VERSION = new String("$Id: GLEContext.java,v 1.1 1998/05/03 16:18:47 descarte Exp descarte $");
    }
}
