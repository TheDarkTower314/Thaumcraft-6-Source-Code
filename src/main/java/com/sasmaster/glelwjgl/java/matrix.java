// 
// Decompiled by Procyon v0.6.0
// 

package com.sasmaster.glelwjgl.java;

import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;
import java.nio.DoubleBuffer;

public class matrix
{
    public static final String VERSION;
    
    private static final double[][] ROTX_CS(final double cosine, final double sine) {
        final double[][] m = new double[4][4];
        m[0][0] = 1.0;
        m[0][1] = 0.0;
        m[0][2] = 0.0;
        m[0][3] = 0.0;
        m[1][0] = 0.0;
        m[1][1] = cosine;
        m[1][2] = sine;
        m[1][3] = 0.0;
        m[2][0] = 0.0;
        m[2][1] = -sine;
        m[2][2] = cosine;
        m[2][3] = 0.0;
        m[3][0] = 0.0;
        m[3][1] = 0.0;
        m[3][2] = 0.0;
        m[3][3] = 1.0;
        return m;
    }
    
    private static final double[][] ROTY_CS(final double cosine, final double sine) {
        final double[][] m = new double[4][4];
        m[0][0] = cosine;
        m[0][1] = 0.0;
        m[0][2] = -sine;
        m[0][3] = 0.0;
        m[1][0] = 0.0;
        m[1][1] = 1.0;
        m[1][2] = 0.0;
        m[1][3] = 0.0;
        m[2][0] = sine;
        m[2][1] = 0.0;
        m[2][2] = cosine;
        m[2][3] = 0.0;
        m[3][0] = 0.0;
        m[3][1] = 0.0;
        m[3][2] = 0.0;
        m[3][3] = 1.0;
        return m;
    }
    
    private static final double[][] ROTZ_CS(final double cosine, final double sine) {
        final double[][] m = new double[4][4];
        m[0][0] = cosine;
        m[0][1] = sine;
        m[0][2] = 0.0;
        m[0][3] = 0.0;
        m[1][0] = -sine;
        m[1][1] = cosine;
        m[1][2] = 0.0;
        m[1][3] = 0.0;
        m[2][0] = 0.0;
        m[2][1] = 0.0;
        m[2][2] = 1.0;
        m[2][3] = 0.0;
        m[3][0] = 0.0;
        m[3][1] = 0.0;
        m[3][2] = 0.0;
        m[3][3] = 1.0;
        return m;
    }
    
    private static DoubleBuffer getBufferedMatrix(final double[][] m) {
        final DoubleBuffer mbuffer = BufferUtils.createDoubleBuffer(16);
        mbuffer.put(new double[] { m[0][0], m[0][1], m[0][2], m[0][3], m[1][0], m[1][1], m[1][2], m[1][3], m[2][0], m[2][1], m[2][2], m[2][3], m[3][0], m[3][1], m[3][2], m[3][3] });
        mbuffer.flip();
        return mbuffer;
    }
    
    public static final double[][] urotx_cs_d(final double cosine, final double sine) {
        return ROTX_CS(cosine, sine);
    }
    
    public static final void rotx_cs_d(final double cosine, final double sine) {
        GL11.glMultMatrix(getBufferedMatrix(urotx_cs_d(cosine, sine)));
    }
    
    public static final double[][] uroty_cs_d(final double cosine, final double sine) {
        return ROTX_CS(cosine, sine);
    }
    
    public static final void roty_cs_d(final double cosine, final double sine) {
        GL11.glMultMatrix(getBufferedMatrix(uroty_cs_d(cosine, sine)));
    }
    
    public static final double[][] urotz_cs_d(final double cosine, final double sine) {
        return ROTX_CS(cosine, sine);
    }
    
    public static final void rotz_cs_d(final double cosine, final double sine) {
        GL11.glMultMatrix(getBufferedMatrix(urotz_cs_d(cosine, sine)));
    }
    
    public static final double[][] urot_cs_d(final double cosine, final double sine, final char axis) {
        switch (axis) {
            case 'X':
            case 'x': {
                return urotx_cs_d(cosine, sine);
            }
            case 'Y':
            case 'y': {
                return uroty_cs_d(cosine, sine);
            }
            case 'Z':
            case 'z': {
                return urotz_cs_d(cosine, sine);
            }
            default: {
                return null;
            }
        }
    }
    
    public static final void rot_cs_d(final double cosine, final double sine, final char axis) {
        GL11.glMultMatrix(getBufferedMatrix(urot_cs_d(cosine, sine, axis)));
    }
    
    public static final double[][] urot_prince_d(final double theta, final char axis) {
        return urot_cs_d(Math.cos(theta), Math.sin(theta), axis);
    }
    
    public static final void rot_prince_d(final double theta, final char axis) {
        GL11.glMultMatrix(getBufferedMatrix(urot_prince_d(theta, axis)));
    }
    
    public static final void rot_axis_d(final double omega, final double[] axis) {
        GL11.glMultMatrix(getBufferedMatrix(urot_axis_d(omega, axis)));
    }
    
    public static final void rot_about_axis_d(final double angle, final double[] axis) {
        GL11.glMultMatrix(getBufferedMatrix(urot_about_axis_d(angle, axis)));
    }
    
    public static final void rot_omega_d(final double[] axis) {
        GL11.glMultMatrix(getBufferedMatrix(urot_omega_d(axis)));
    }
    
    public static final double[][] urot_axis_d(final double omega, final double[] axis) {
        final double[][] m = new double[4][4];
        if (axis.length != 3) {
            throw new GLEException("Length of axis parameter != 3. This is not a valid vector!");
        }
        double tmp = omega / 2.0;
        final double s = Math.sin(tmp);
        final double c = Math.cos(tmp);
        double ssq = s * s;
        final double csq = c * c;
        final double[] array = m[0];
        final int n = 0;
        final double[] array2 = m[1];
        final int n2 = 1;
        final double[] array3 = m[2];
        final int n3 = 2;
        final double n4 = csq - ssq;
        array3[n3] = n4;
        array[n] = (array2[n2] = n4);
        ssq *= 2.0;
        final double[] array4 = m[0];
        final int n5 = 0;
        array4[n5] += ssq * axis[0] * axis[0];
        final double[] array5 = m[1];
        final int n6 = 1;
        array5[n6] += ssq * axis[1] * axis[1];
        final double[] array6 = m[2];
        final int n7 = 2;
        array6[n7] += ssq * axis[2] * axis[2];
        m[0][1] = (m[1][0] = axis[0] * axis[1] * ssq);
        m[1][2] = (m[2][1] = axis[1] * axis[2] * ssq);
        m[2][0] = (m[0][2] = axis[2] * axis[0] * ssq);
        final double cts = 2.0 * c * s;
        tmp = cts * axis[2];
        final double[] array7 = m[0];
        final int n8 = 1;
        array7[n8] += tmp;
        final double[] array8 = m[1];
        final int n9 = 0;
        array8[n9] -= tmp;
        tmp = cts * axis[0];
        final double[] array9 = m[1];
        final int n10 = 2;
        array9[n10] += tmp;
        final double[] array10 = m[2];
        final int n11 = 1;
        array10[n11] -= tmp;
        tmp = cts * axis[1];
        final double[] array11 = m[2];
        final int n12 = 0;
        array11[n12] += tmp;
        final double[] array12 = m[0];
        final int n13 = 2;
        array12[n13] -= tmp;
        final double[] array13 = m[0];
        final int n14 = 3;
        final double[] array14 = m[1];
        final int n15 = 3;
        final double[] array15 = m[2];
        final int n16 = 3;
        final double[] array16 = m[3];
        final int n17 = 2;
        final double[] array17 = m[3];
        final int n18 = 1;
        final double[] array18 = m[3];
        final int n19 = 0;
        final double n20 = 0.0;
        array17[n18] = (array18[n19] = n20);
        array15[n16] = (array16[n17] = n20);
        array13[n14] = (array14[n15] = n20);
        m[3][3] = 1.0;
        return m;
    }
    
    public static final double[][] urot_about_axis_d(final double angle, final double[] axis) {
        double[][] m = null;
        final double[] ax = new double[3];
        double ang = angle;
        if (axis.length != 3) {
            throw new GLEException("Length of axis parameter != 3. This is not a valid vector!");
        }
        ang *= 0.017453292519943295;
        double len = axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2];
        if (len != 1.0) {
            len = 1.0 / Math.sqrt(len);
            ax[0] = axis[0] * len;
            ax[1] = axis[1] * len;
            ax[2] = axis[2] * len;
            m = urot_axis_d(ang, ax);
        }
        else {
            m = urot_axis_d(ang, axis);
        }
        return m;
    }
    
    public static final double[][] urot_omega_d(final double[] axis) {
        final double[][] m = null;
        final double[] ax = new double[3];
        double len = axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2];
        len = 1.0 / Math.sqrt(len);
        ax[0] = axis[0] * len;
        ax[1] = axis[1] * len;
        ax[2] = axis[2] * len;
        return urot_axis_d(len, ax);
    }
    
    public static final double[] VEC_ZERO() {
        final double[] array;
        final double[] vtmp = array = new double[3];
        final int n = 0;
        final double[] array2 = vtmp;
        final int n2 = 1;
        final double[] array3 = vtmp;
        final int n3 = 2;
        final double n4 = 0.0;
        array3[n3] = n4;
        array[n] = (array2[n2] = n4);
        return vtmp;
    }
    
    public static final double[] VEC_NORMALIZE(final double[] v) {
        final double[] vtmp = new double[3];
        double vlen = VEC_LENGTH(v);
        if (vlen != 0.0) {
            vlen = 1.0 / vlen;
            vtmp[0] = v[0] * vlen;
            vtmp[1] = v[1] * vlen;
            vtmp[2] = v[2] * vlen;
        }
        return vtmp;
    }
    
    public static final double[] VEC_REFLECT(final double[] v, final double[] n) {
        final double[] vtmp = new double[3];
        final double dot = VEC_DOT_PRODUCT(v, n);
        vtmp[0] = v[0] - 2.0 * dot * n[0];
        vtmp[1] = v[1] - 2.0 * dot * n[1];
        vtmp[2] = v[2] - 2.0 * dot * n[2];
        return vtmp;
    }
    
    public static final double[] VEC_COPY_2(final double[] v) {
        final double[] vtmp = { v[0], v[1], 0.0 };
        return vtmp;
    }
    
    public static final double[] VEC_COPY(final double[] v) {
        final double[] vtmp = { v[0], v[1], v[2] };
        return vtmp;
    }
    
    public static final double VEC_LENGTH_2(final double[] v) {
        final double length = v[0] * v[0] + v[1] * v[1];
        return length;
    }
    
    public static final double VEC_LENGTH(final double[] v) {
        final double length = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        return length;
    }
    
    public static final double[] VEC_SCALE(final double scale, final double[] v) {
        final double[] vtmp = { scale * v[0], scale * v[1], scale * v[2] };
        return vtmp;
    }
    
    public static final double[] VEC_CROSS_PRODUCT(final double[] v1, final double[] v2) {
        final double[] vtmp = { v1[1] * v2[2] - v1[2] * v2[1], v1[2] * v2[0] - v1[0] * v2[2], v1[0] * v2[1] - v1[1] * v2[0] };
        return vtmp;
    }
    
    public static final double VEC_DOT_PRODUCT(final double[] v1, final double[] v2) {
        double dot = 0.0;
        if (v1.length != 3 || v2.length != 3) {
            throw new GLEException("Length of v1 or v2 != 3. Invalid vectors!");
        }
        dot = v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
        return dot;
    }
    
    public static final double[] VEC_PERP(final double[] v, final double[] n) {
        final double[] vtmp = new double[3];
        final double dot = VEC_DOT_PRODUCT(v, n);
        if (v.length != 3 || n.length != 3) {
            throw new GLEException("Length of v or n !=3. Invalid vectors!");
        }
        vtmp[0] = v[0] - dot * n[0];
        vtmp[1] = v[1] - dot * n[1];
        vtmp[2] = v[2] - dot * n[2];
        return vtmp;
    }
    
    public static final double[] VEC_DIFF(final double[] v2, final double[] v1) {
        final double[] vtmp = new double[3];
        if (v1.length != 3 || v2.length != 3) {
            throw new GLEException("Length of v1 or v2 != 3. Invalid vectors!");
        }
        vtmp[0] = v2[0] - v1[0];
        vtmp[1] = v2[1] - v1[1];
        vtmp[2] = v2[2] - v1[2];
        return vtmp;
    }
    
    public static final double[] VEC_SUM(final double[] v1, final double[] v2) {
        final double[] vtmp = new double[3];
        if (v1.length != 3 || v2.length != 3) {
            throw new GLEException("Length of v1 or v2 != 3. Invalid vectors!");
        }
        vtmp[0] = v2[0] + v1[0];
        vtmp[1] = v2[1] + v1[1];
        vtmp[2] = v2[2] + v1[2];
        return vtmp;
    }
    
    public static final double[][] IDENTIFY_MATRIX_3X3() {
        final double[][] m = new double[3][3];
        m[0][0] = 1.0;
        m[0][1] = 0.0;
        m[0][2] = 0.0;
        m[1][0] = 0.0;
        m[1][1] = 1.0;
        m[1][2] = 0.0;
        m[2][0] = 0.0;
        m[2][1] = 0.0;
        m[2][2] = 1.0;
        return m;
    }
    
    public static final double[][] IDENTIFY_MATRIX_4X4() {
        final double[][] m = new double[4][4];
        m[0][0] = 1.0;
        m[0][1] = 0.0;
        m[0][2] = 0.0;
        m[0][3] = 0.0;
        m[1][0] = 0.0;
        m[1][1] = 1.0;
        m[1][2] = 0.0;
        m[1][3] = 0.0;
        m[2][0] = 0.0;
        m[2][1] = 0.0;
        m[2][2] = 1.0;
        m[2][3] = 0.0;
        m[3][0] = 0.0;
        m[3][1] = 0.0;
        m[3][2] = 0.0;
        m[3][3] = 1.0;
        return m;
    }
    
    public static final double[][] COPY_MATRIX_2X2(final double[][] a) {
        final double[][] b = new double[2][2];
        b[0][0] = a[0][0];
        b[0][1] = a[0][1];
        b[1][0] = a[1][0];
        b[1][1] = a[1][1];
        return b;
    }
    
    public static final double[][] COPY_MATRIX_2X3(final double[][] a) {
        final double[][] b = new double[2][3];
        b[0][0] = a[0][0];
        b[0][1] = a[0][1];
        b[0][2] = a[0][2];
        b[1][0] = a[1][0];
        b[1][1] = a[1][1];
        b[1][2] = a[1][2];
        return b;
    }
    
    public static final double[][] COPY_MATRIX_4X4(final double[][] a) {
        final double[][] b = new double[4][4];
        b[0][0] = a[0][0];
        b[0][1] = a[0][1];
        b[0][2] = a[0][2];
        b[0][3] = a[0][3];
        b[1][0] = a[1][0];
        b[1][1] = a[1][1];
        b[1][2] = a[1][2];
        b[1][3] = a[1][3];
        b[2][0] = a[2][0];
        b[2][1] = a[2][1];
        b[2][2] = a[2][2];
        b[2][3] = a[2][3];
        b[3][0] = a[3][0];
        b[3][1] = a[3][1];
        b[3][2] = a[3][2];
        b[3][3] = a[3][3];
        return b;
    }
    
    public static final double[][] MATRIX_PRODUCT_2X2(final double[][] a, final double[][] b) {
        final double[][] c = new double[2][2];
        c[0][0] = a[0][0] * b[0][0] + a[0][1] * b[1][0];
        c[0][1] = a[0][0] * b[0][1] + a[0][1] * b[1][1];
        c[1][0] = a[1][0] * b[0][0] + a[1][1] * b[1][0];
        c[1][1] = a[1][0] * b[0][1] + a[1][1] * b[1][1];
        return c;
    }
    
    public static final double[][] MATRIX_PRODUCT_4X4(final double[][] a, final double[][] b) {
        final double[][] c = new double[4][4];
        c[0][0] = a[0][0] * b[0][0] + a[0][1] * b[1][0] + a[0][2] * b[2][0] + a[0][3] * b[3][0];
        c[0][1] = a[0][0] * b[0][1] + a[0][1] * b[1][1] + a[0][2] * b[2][1] + a[0][3] * b[3][1];
        c[0][2] = a[0][0] * b[0][2] + a[0][1] * b[1][2] + a[0][2] * b[2][2] + a[0][3] * b[3][2];
        c[0][3] = a[0][0] * b[0][3] + a[0][1] * b[1][3] + a[0][2] * b[2][3] + a[0][3] * b[3][3];
        c[1][0] = a[1][0] * b[0][0] + a[1][1] * b[1][0] + a[1][2] * b[2][0] + a[1][3] * b[3][0];
        c[1][1] = a[1][0] * b[0][1] + a[1][1] * b[1][1] + a[1][2] * b[2][1] + a[1][3] * b[3][1];
        c[1][2] = a[1][0] * b[0][2] + a[1][1] * b[1][2] + a[1][2] * b[2][2] + a[1][3] * b[3][2];
        c[1][3] = a[1][0] * b[0][3] + a[1][1] * b[1][3] + a[1][2] * b[2][3] + a[1][3] * b[3][3];
        c[2][0] = a[2][0] * b[0][0] + a[2][1] * b[1][0] + a[2][2] * b[2][0] + a[2][3] * b[3][0];
        c[2][1] = a[2][0] * b[0][1] + a[2][1] * b[1][1] + a[2][2] * b[2][1] + a[2][3] * b[3][1];
        c[2][2] = a[2][0] * b[0][2] + a[2][1] * b[1][2] + a[2][2] * b[2][2] + a[2][3] * b[3][2];
        c[2][3] = a[2][0] * b[0][3] + a[2][1] * b[1][3] + a[2][2] * b[2][3] + a[2][3] * b[3][3];
        c[3][0] = a[3][0] * b[0][0] + a[3][1] * b[1][0] + a[3][2] * b[2][0] + a[3][3] * b[3][0];
        c[3][1] = a[3][0] * b[0][1] + a[3][1] * b[1][1] + a[3][2] * b[2][1] + a[3][3] * b[3][1];
        c[3][2] = a[3][0] * b[0][2] + a[3][1] * b[1][2] + a[3][2] * b[2][2] + a[3][3] * b[3][2];
        c[3][3] = a[3][0] * b[0][3] + a[3][1] * b[1][3] + a[3][2] * b[2][3] + a[3][3] * b[3][3];
        return c;
    }
    
    public static final double[] MAT_DOT_VEC_2X3(final double[][] m, final double[] v) {
        final double[] vtmp = { m[0][0] * v[0] + m[0][1] * v[1] + m[0][2], m[1][0] * v[0] + m[1][1] * v[1] + m[1][2], 0.0 };
        return vtmp;
    }
    
    public static final double[] MAT_DOT_VEC_3X3(final double[][] m, final double[] v) {
        final double[] vtmp = { m[0][0] * v[0] + m[0][1] * v[1] + m[0][2] * v[2], m[1][0] * v[0] + m[1][1] * v[1] + m[1][2] * v[2], m[2][0] * v[0] + m[2][1] * v[1] + m[2][2] * v[2] };
        return vtmp;
    }
    
    public static final double[] NORM_XFORM_2X2(final double[][] m, final double[] v) {
        double len = 0.0;
        double[] p = new double[3];
        if (m[0][1] != 0.0 || m[1][0] != 0.0 || m[0][0] != m[1][1]) {
            p[0] = m[1][1] * v[0] - m[1][0] * v[1];
            p[1] = -m[0][1] * v[0] + m[0][0] * v[1];
            len = p[0] * p[0] + p[1] * p[1];
            len = 1.0 / Math.sqrt(len);
            final double[] array = p;
            final int n = 0;
            array[n] *= len;
            final double[] array2 = p;
            final int n2 = 1;
            array2[n2] *= len;
        }
        else {
            p = VEC_COPY_2(v);
        }
        return p;
    }
    
    public static final double[][] uview_direction_d(final double[] v21, final double[] up) {
        double[][] amat = null;
        double[][] bmat = null;
        double[][] cmat = null;
        double[] v_hat_21 = new double[3];
        double[] v_xy = new double[3];
        double[] up_proj = new double[3];
        final double[] tmp = new double[3];
        double[][] m = null;
        v_hat_21 = VEC_COPY(v21);
        double len = VEC_LENGTH(v_hat_21);
        if (len != 0.0) {
            len = 1.0 / len;
            v_hat_21 = VEC_SCALE(len, v_hat_21);
            final double sine = Math.sqrt(1.0 - v_hat_21[2] * v_hat_21[2]);
            amat = ROTY_CS(-v_hat_21[2], -sine);
        }
        else {
            amat = IDENTIFY_MATRIX_4X4();
        }
        v_xy[0] = v21[0];
        v_xy[1] = v21[1];
        v_xy[2] = 0.0;
        len = VEC_LENGTH(v_xy);
        if (len != 0.0) {
            len = 1.0 / len;
            v_xy = VEC_SCALE(len, v_xy);
            bmat = ROTZ_CS(v_xy[0], v_xy[1]);
            cmat = MATRIX_PRODUCT_4X4(amat, bmat);
        }
        else {
            cmat = COPY_MATRIX_4X4(amat);
        }
        up_proj = VEC_PERP(up, v_hat_21);
        len = VEC_LENGTH(up_proj);
        if (len != 0.0) {
            len = 1.0 / len;
            up_proj = VEC_SCALE(len, up_proj);
            tmp[0] = cmat[1][0];
            tmp[1] = cmat[1][1];
            tmp[2] = cmat[1][2];
            final double cosine = VEC_DOT_PRODUCT(tmp, up_proj);
            tmp[0] = cmat[0][0];
            tmp[1] = cmat[0][1];
            tmp[2] = cmat[0][2];
            final double sine = VEC_DOT_PRODUCT(tmp, up_proj);
            amat = ROTZ_CS(cosine, -sine);
            m = MATRIX_PRODUCT_4X4(amat, cmat);
        }
        else {
            m = COPY_MATRIX_4X4(cmat);
        }
        return m;
    }
    
    public static final double[][] uviewpoint_d(final double[] v1, final double[] v2, final double[] up) {
        double[] v_hat_21 = null;
        double[][] trans_mat = null;
        double[][] rot_mat = null;
        double[][] m = null;
        v_hat_21 = VEC_DIFF(v2, v1);
        rot_mat = uview_direction_d(v_hat_21, up);
        trans_mat = IDENTIFY_MATRIX_4X4();
        trans_mat[3][0] = v1[0];
        trans_mat[3][1] = v1[1];
        trans_mat[3][2] = v1[2];
        m = MATRIX_PRODUCT_4X4(rot_mat, trans_mat);
        return m;
    }
    
    static {
        VERSION = new String("$Id: matrix.java,v 1.2 1998/05/05 23:31:09 descarte Exp descarte $");
    }
}
