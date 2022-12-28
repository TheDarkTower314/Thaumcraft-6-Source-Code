package com.sasmaster.glelwjgl.java;


public class intersect
{
    public static String VERSION;
    private static double DEGENERATE_TOLERANCE = 2.0E-6;
    
    public static int FIND_NON_DEGENERATE_POINT(int index, int npoints, double len, double[] diff, double[][] point_array) {
        double[] summa = null;
        int i = index;
        double tlen = len;
        double[] tdiff = null;
        double slen;
        do {
            tdiff = matrix.VEC_DIFF(point_array[i + 1], point_array[i]);
            diff[0] = tdiff[0];
            diff[1] = tdiff[1];
            diff[2] = tdiff[2];
            tlen = (len = matrix.VEC_LENGTH(diff));
            summa = matrix.VEC_SUM(point_array[i + 1], point_array[i]);
            slen = matrix.VEC_LENGTH(summa);
            slen *= 2.0E-6;
            ++i;
        } while (tlen <= slen && i < npoints - 1);
        return i;
    }
    
    public static double[] INNERSECT(double[] p, double[] n, double[] v1, double[] v2) {
        double deno = 0.0;
        double numer = 0.0;
        double t = 0.0;
        double omt = 0.0;
        boolean valid = false;
        double[] sect = new double[3];
        deno = (v1[0] - v2[0]) * n[0];
        deno += (v1[1] - v2[1]) * n[1];
        deno += (v1[2] - v2[2]) * n[2];
        if (deno == 0.0) {
            valid = false;
            n = matrix.VEC_COPY(v1);
        }
        else {
            valid = true;
            numer = (p[0] - v2[0]) * n[0];
            numer += (p[1] - v2[1]) * n[1];
            numer += (p[2] - v2[2]) * n[2];
            t = numer / deno;
            omt = 1.0 - t;
            if (1.0 < t * 2.0E-6 || -1.0 > t * 2.0E-6) {
                valid = false;
            }
            sect[0] = t * v1[0] + omt * v2[0];
            sect[1] = t * v1[1] + omt * v2[1];
            sect[2] = t * v1[2] + omt * v2[2];
        }
        return sect;
    }
    
    public static double[] bisecting_plane(double[] v1, double[] v2, double[] v3) {
        boolean valid = false;
        double[] v4 = null;
        double[] v5 = null;
        double[] n = new double[3];
        v4 = matrix.VEC_DIFF(v2, v1);
        v5 = matrix.VEC_DIFF(v3, v2);
        double len21 = matrix.VEC_LENGTH(v4);
        double len22 = matrix.VEC_LENGTH(v5);
        if (len21 <= 2.0E-6 * len22) {
            if (len22 == 0.0) {
                n = matrix.VEC_ZERO();
                valid = false;
            }
            else {
                len22 = 1.0 / len22;
                n = matrix.VEC_SCALE(len22, v5);
                valid = true;
            }
        }
        else {
            valid = true;
            if (len22 <= 2.0E-6 * len21) {
                len21 = 1.0 / len21;
                n = matrix.VEC_SCALE(len21, v4);
            }
            else {
                len21 = 1.0 / len21;
                v4 = matrix.VEC_SCALE(len21, v4);
                len22 = 1.0 / len22;
                v5 = matrix.VEC_SCALE(len22, v5);
                double dot = matrix.VEC_DOT_PRODUCT(v5, v4);
                if (dot >= 0.999998 || dot <= -0.999998) {
                    n = matrix.VEC_COPY(v4);
                }
                else {
                    n[0] = dot * (v5[0] + v4[0]) - v5[0] - v4[0];
                    n[1] = dot * (v5[1] + v4[1]) - v5[1] - v4[1];
                    n[2] = dot * (v5[2] + v4[2]) - v5[2] - v4[2];
                    n = matrix.VEC_NORMALIZE(n);
                }
            }
        }
        return n;
    }
    
    public static boolean CUTTING_PLANE(double[] n, double[] v1, double[] v2, double[] v3) {
        double[] v4 = new double[3];
        double[] v5 = new double[3];
        double len21 = 0.0;
        double len22 = 0.0;
        double lendiff = 0.0;
        boolean valid = false;
        double[] vtmp = matrix.VEC_COPY(n);
        v4 = matrix.VEC_DIFF(v2, v1);
        v5 = matrix.VEC_DIFF(v3, v2);
        len21 = matrix.VEC_LENGTH(v4);
        len22 = matrix.VEC_LENGTH(v5);
        if (len21 <= 2.0E-6 * len22) {
            if (len22 == 0.0) {
                vtmp = matrix.VEC_ZERO();
                valid = false;
            }
            else {
                len22 = 1.0 / len22;
                vtmp = matrix.VEC_SCALE(len22, v5);
                valid = true;
            }
        }
        else {
            valid = true;
            if (len22 <= 2.0E-6 * len21) {
                len21 = 1.0 / len21;
                vtmp = matrix.VEC_SCALE(len21, v4);
            }
            else {
                len21 = 1.0 / len21;
                v4 = matrix.VEC_SCALE(len21, v4);
                len22 = 1.0 / len22;
                v5 = matrix.VEC_SCALE(len22, v5);
                vtmp = matrix.VEC_DIFF(v4, v5);
                lendiff = matrix.VEC_LENGTH(vtmp);
                if (lendiff < 2.0E-6) {
                    vtmp = matrix.VEC_ZERO();
                    valid = false;
                }
                else {
                    lendiff = 1.0 / lendiff;
                    vtmp = matrix.VEC_SCALE(lendiff, vtmp);
                }
            }
        }
        n[0] = vtmp[0];
        n[1] = vtmp[1];
        n[2] = vtmp[2];
        return valid;
    }
    
    public static boolean COLINEAR(double[] v1, double[] v2, double[] v3) {
        double[] v4 = new double[3];
        double[] v5 = new double[3];
        boolean rv = false;
        double len21 = 0.0;
        double len22 = 0.0;
        double dot = 0.0;
        v4 = matrix.VEC_DIFF(v2, v1);
        v5 = matrix.VEC_DIFF(v3, v2);
        len21 = matrix.VEC_DOT_PRODUCT(v4, v4);
        len22 = matrix.VEC_DOT_PRODUCT(v5, v5);
        rv = (len22 <= 2.0E-6 * len21);
        rv = (rv || len21 <= 2.0E-6 * len22);
        dot = matrix.VEC_DOT_PRODUCT(v4, v5);
        rv = (rv || len21 * len22 - dot * dot <= len21 * len22 * 2.0E-6 * 2.0E-6);
        return rv;
    }
    
    static {
        VERSION = new String("$Id: intersect.java,v 1.1 1998/05/05 23:31:21 descarte Exp descarte $");
    }
}
