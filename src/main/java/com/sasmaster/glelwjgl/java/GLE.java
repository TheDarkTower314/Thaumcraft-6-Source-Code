package com.sasmaster.glelwjgl.java;


public interface GLE
{
    public static String VERSION = new String("$Id: GLE.java,v 1.3 1998/05/02 12:06:39 descarte Exp descarte $");
    public static int SUMMARY = 0;
    public static int VERBOSE = 1;
    public static int TUBE_JN_RAW = 1;
    public static int TUBE_JN_ANGLE = 2;
    public static int TUBE_JN_CUT = 3;
    public static int TUBE_JN_ROUND = 4;
    public static int TUBE_JN_MASK = 15;
    public static int TUBE_JN_CAP = 16;
    public static int TUBE_NORM_FACET = 256;
    public static int TUBE_NORM_EDGE = 512;
    public static int TUBE_NORM_PATH_EDGE = 1024;
    public static int TUBE_NORM_MASK = 3840;
    public static int TUBE_CONTOUR_CLOSED = 4096;
    public static int GLE_TEXTURE_ENABLE = 65536;
    public static int GLE_TEXTURE_STYLE_MASK = 255;
    public static int GLE_TEXTURE_VERTEX_FLAT = 1;
    public static int GLE_TEXTURE_NORMAL_FLAT = 2;
    public static int GLE_TEXTURE_VERTEX_CYL = 3;
    public static int GLE_TEXTURE_NORMAL_CYL = 4;
    public static int GLE_TEXTURE_VERTEX_SPH = 5;
    public static int GLE_TEXTURE_NORMAL_SPH = 6;
    public static int GLE_TEXTURE_VERTEX_MODEL_FLAT = 7;
    public static int GLE_TEXTURE_NORMAL_MODEL_FLAT = 8;
    public static int GLE_TEXTURE_VERTEX_MODEL_CYL = 9;
    public static int GLE_TEXTURE_NORMAL_MODEL_CYL = 10;
    public static int GLE_TEXTURE_VERTEX_MODEL_SPH = 11;
    public static int GLE_TEXTURE_NORMAL_MODEL_SPH = 12;
    
    int gleGetJoinStyle();
    
    void gleSetJoinStyle(int p0);
    
    void gleTextureMode(int p0);
    
    void glePolyCylinder(int p0, double[][] p1, float[][] p2, double p3, float p4, float p5) throws GLEException;
    
    void glePolyCone(int p0, double[][] p1, float[][] p2, double[] p3, float p4, float p5) throws GLEException;
    
    void gleExtrusion(int p0, double[][] p1, double[][] p2, double[] p3, int p4, double[][] p5, float[][] p6) throws GLEException;
    
    void gleTwistExtrusion(int p0, double[][] p1, double[][] p2, double[] p3, int p4, double[][] p5, float[][] p6, double[] p7) throws GLEException;
    
    void gleSuperExtrusion(int p0, double[][] p1, double[][] p2, double[] p3, int p4, double[][] p5, float[][] p6, double[][][] p7) throws GLEException;
    
    void gleSpiral(int p0, double[][] p1, double[][] p2, double[] p3, double p4, double p5, double p6, double p7, double[][] p8, double[][] p9, double p10, double p11) throws GLEException;
    
    void gleLathe(int p0, double[][] p1, double[][] p2, double[] p3, double p4, double p5, double p6, double p7, double[][] p8, double[][] p9, double p10, double p11) throws GLEException;
    
    void gleHelicoid(double p0, double p1, double p2, double p3, double p4, double[][] p5, double[][] p6, double p7, double p8) throws GLEException;
    
    void gleToroid(double p0, double p1, double p2, double p3, double p4, double[][] p5, double[][] p6, double p7, double p8) throws GLEException;
    
    void gleScrew(int p0, double[][] p1, double[][] p2, double[] p3, double p4, double p5, double p6) throws GLEException;
}
