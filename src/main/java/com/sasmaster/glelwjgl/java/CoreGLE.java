package com.sasmaster.glelwjgl.java;
import java.nio.DoubleBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;
import org.lwjgl.util.glu.GLUtessellatorCallback;


public class CoreGLE implements GLE
{
    public static String VERSION;
    private static String GLE_VERSION;
    private GLEContext context_;
    private int _POLYCYL_TESS;
    private int __ROUND_TESS_PIECES;
    private static GLU glu_;
    private tessellCallBack tessCallback;
    private float SLICE;
    private float SLICE_PROGRESS;
    
    public void set_POLYCYL_TESS(int _POLYCYL_TESS) {
        this._POLYCYL_TESS = _POLYCYL_TESS;
    }
    
    public void set__ROUND_TESS_PIECES(int __ROUND_TESS_PIECES) {
        this.__ROUND_TESS_PIECES = __ROUND_TESS_PIECES;
    }
    
    public CoreGLE() {
        context_ = new GLEContext();
        _POLYCYL_TESS = 20;
        __ROUND_TESS_PIECES = 5;
        tessCallback = new tessellCallBack(CoreGLE.glu_);
        SLICE = 1.0f;
        SLICE_PROGRESS = 0.0f;
    }
    
    @Override
    public int gleGetJoinStyle() {
        return context_.getJoinStyle();
    }
    
    @Override
    public void gleSetJoinStyle(int style) {
        context_.setJoinStyle(style);
    }
    
    @Override
    public void gleTextureMode(int mode) {
    }
    
    private void gen_polycone(int npoints, double[][] pointArray, float[][] colourArray, double radius, double[][][] xformArray, float texSlice, float start) {
        SLICE = texSlice;
        SLICE_PROGRESS = start;
        double[][] circle = new double[_POLYCYL_TESS][2];
        double[][] norm = new double[_POLYCYL_TESS][2];
        double[] v21 = new double[3];
        double len = 0.0;
        double[] up = new double[3];
        if (xformArray != null) {
            radius = 1.0;
        }
        double s = Math.sin(6.283185307179586 / _POLYCYL_TESS);
        double c = Math.cos(6.283185307179586 / _POLYCYL_TESS);
        norm[0][0] = 1.0;
        norm[0][1] = 0.0;
        circle[0][0] = radius;
        circle[0][1] = 0.0;
        for (int i = 1; i < _POLYCYL_TESS; ++i) {
            norm[i][0] = norm[i - 1][0] * c - norm[i - 1][1] * s;
            norm[i][1] = norm[i - 1][0] * s + norm[i - 1][1] * c;
            circle[i][0] = radius * norm[i][0];
            circle[i][1] = radius * norm[i][1];
        }
        int i = 0;
        i = intersect.FIND_NON_DEGENERATE_POINT(i, npoints, len, v21, pointArray);
        len = matrix.VEC_LENGTH(v21);
        if (i == npoints) {
            return;
        }
        if (v21[0] == 0.0 && v21[2] == 0.0) {
            double[] array = up;
            int n = 0;
            double[] array2 = up;
            int n2 = 1;
            double[] array3 = up;
            int n3 = 2;
            double n4 = 1.0;
            array3[n3] = n4;
            array[n] = (array2[n2] = n4);
        }
        else {
            up[0] = (up[2] = 0.0);
            up[1] = 1.0;
        }
        int savedStyle = gleGetJoinStyle();
        gleSetJoinStyle(savedStyle | 0x1000);
        if (!GL11.glIsEnabled(2896)) {
            gleSuperExtrusion(_POLYCYL_TESS, circle, null, up, npoints, pointArray, colourArray, xformArray);
        }
        else {
            gleSuperExtrusion(_POLYCYL_TESS, circle, norm, up, npoints, pointArray, colourArray, xformArray);
        }
        gleSetJoinStyle(savedStyle);
    }
    
    @Override
    public void glePolyCylinder(int npoints, double[][] pointArray, float[][] colourArray, double radius, float texSlice, float start) throws GLEException {
        gen_polycone(npoints, pointArray, colourArray, radius, null, texSlice, start);
    }
    
    @Override
    public void glePolyCone(int npoints, double[][] pointArray, float[][] colourArray, double[] radiusArray, float texSlice, float start) throws GLEException {
        double[][][] xforms = new double[npoints][2][3];
        for (int i = 0; i < npoints; ++i) {
            xforms[i][0][0] = radiusArray[i];
            xforms[i][0][1] = 0.0;
            xforms[i][0][2] = 0.0;
            xforms[i][1][0] = 0.0;
            xforms[i][1][1] = radiusArray[i];
            xforms[i][1][2] = 0.0;
        }
        gen_polycone(npoints, pointArray, colourArray, 1.0, xforms, texSlice, start);
    }
    
    @Override
    public void gleExtrusion(int ncp, double[][] contour, double[][] contourNormal, double[] up, int npoints, double[][] pointArray, float[][] colourArray) throws GLEException {
        gleSuperExtrusion(ncp, contour, contourNormal, up, npoints, pointArray, colourArray, null);
    }
    
    @Override
    public void gleTwistExtrusion(int ncp, double[][] contour, double[][] contourNormal, double[] up, int npoints, double[][] pointArray, float[][] colourArray, double[] twistArray) throws GLEException {
        double[][][] xforms = new double[npoints][2][3];
        double angle = 0.0;
        double si = 0.0;
        double co = 0.0;
        for (int j = 0; j < npoints; ++j) {
            angle = 0.017453292519943295 * twistArray[j];
            si = Math.sin(angle);
            co = Math.cos(angle);
            xforms[j][0][0] = co;
            xforms[j][0][1] = -si;
            xforms[j][0][2] = 0.0;
            xforms[j][1][0] = si;
            xforms[j][1][1] = co;
            xforms[j][1][2] = 0.0;
        }
        gleSuperExtrusion(ncp, contour, contourNormal, up, npoints, pointArray, colourArray, xforms);
    }
    
    @Override
    public void gleSuperExtrusion(int ncp, double[][] contour, double[][] contourNormal, double[] up, int npoints, double[][] pointArray, float[][] colourArray, double[][][] xformArray) throws GLEException {
        context_.ncp = ncp;
        context_.contour = contour;
        context_.contourNormal = contourNormal;
        context_.up = up;
        context_.npoints = npoints;
        context_.pointArray = pointArray;
        context_.colourArray = colourArray;
        context_.xformArray = xformArray;
        switch (gleGetJoinStyle() & 0xF) {
            case 1: {
                extrusion_raw_join(ncp, contour, contourNormal, up, npoints, pointArray, colourArray, xformArray);
                break;
            }
            case 2: {
                extrusion_angle_join(ncp, contour, contourNormal, up, npoints, pointArray, colourArray, xformArray);
                break;
            }
            case 3:
            case 4: {
                extrusion_round_or_cut_join(ncp, contour, contourNormal, up, npoints, pointArray, colourArray, xformArray);
                break;
            }
            default: {
                throw new GLEException("Join style is complete rubbish!");
            }
        }
    }
    
    @Override
    public void gleSpiral(int ncp, double[][] contour, double[][] contourNormal, double[] up, double startRadius, double drdTheta, double startZ, double dzdTheta, double[][] startTransform, double[][] dTransformdTheta, double startTheta, double sweepTheta) throws GLEException {
        int npoints = (int)(_POLYCYL_TESS / 360.0 * Math.abs(sweepTheta) + 4.0);
        double[][] points = null;
        double[][][] xforms = null;
        double delta = 0.0;
        double deltaAngle = 0.0;
        double cdelta = 0.0;
        double sdelta = 0.0;
        double sprev = 0.0;
        double cprev = 0.0;
        double scurr = 0.0;
        double ccurr = 0.0;
        double[][] mA = new double[2][2];
        double[][] mB = new double[2][2];
        double[][] run = new double[2][2];
        double[] deltaTrans = new double[2];
        double[] trans = new double[2];
        points = new double[npoints][3];
        if (startTransform == null) {
            xforms = null;
        }
        else {
            xforms = new double[npoints][2][3];
        }
        deltaAngle = 0.017453292519943295 * sweepTheta / (npoints - 3);
        startTheta *= 0.017453292519943295;
        startTheta -= deltaAngle;
        cprev = Math.cos(startTheta);
        sprev = Math.sin(startTheta);
        cdelta = Math.cos(deltaAngle);
        sdelta = Math.sin(deltaAngle);
        delta = deltaAngle / 6.283185307179586;
        dzdTheta *= delta;
        drdTheta *= delta;
        startZ -= dzdTheta;
        startRadius -= drdTheta;
        for (int i = 0; i < npoints; ++i) {
            points[i][0] = startRadius * cprev;
            points[i][1] = startRadius * sprev;
            points[i][2] = startZ;
            startZ += dzdTheta;
            startRadius += drdTheta;
            ccurr = cprev * cdelta - sprev * sdelta;
            scurr = cprev * sdelta + sprev * cdelta;
            cprev = ccurr;
            sprev = scurr;
        }
        if (startTransform != null) {
            if (dTransformdTheta == null) {
                for (int i = 0; i < npoints; ++i) {
                    xforms[i][0][0] = startTransform[0][0];
                    xforms[i][0][1] = startTransform[0][1];
                    xforms[i][0][2] = startTransform[0][2];
                    xforms[i][1][0] = startTransform[1][0];
                    xforms[i][1][1] = startTransform[1][1];
                    xforms[i][1][2] = startTransform[1][2];
                }
            }
            else {
                deltaTrans[0] = delta * dTransformdTheta[0][2];
                deltaTrans[1] = delta * dTransformdTheta[1][2];
                trans[0] = startTransform[0][2];
                trans[1] = startTransform[1][2];
                delta /= 32.0;
                mA[0][0] = 1.0 + delta * dTransformdTheta[0][0];
                mA[0][1] = delta * dTransformdTheta[0][1];
                mA[1][0] = delta * dTransformdTheta[1][0];
                mA[1][1] = 1.0 + delta * dTransformdTheta[1][1];
                mB = matrix.MATRIX_PRODUCT_2X2(mA, mA);
                mA = matrix.MATRIX_PRODUCT_2X2(mB, mB);
                mB = matrix.MATRIX_PRODUCT_2X2(mA, mA);
                mA = matrix.MATRIX_PRODUCT_2X2(mB, mB);
                mB = matrix.MATRIX_PRODUCT_2X2(mA, mA);
                run = matrix.COPY_MATRIX_2X2(startTransform);
                xforms[0][0][0] = startTransform[0][0];
                xforms[0][0][1] = startTransform[0][1];
                xforms[0][0][2] = startTransform[0][2];
                xforms[0][1][0] = startTransform[1][0];
                xforms[0][1][1] = startTransform[1][1];
                xforms[0][1][2] = startTransform[1][2];
                for (int j = 0; j < npoints; ++j) {
                    xforms[j][0][0] = run[0][0];
                    xforms[j][0][1] = run[0][1];
                    xforms[j][1][0] = run[1][0];
                    xforms[j][1][1] = run[1][1];
                    mA = matrix.MATRIX_PRODUCT_2X2(mB, run);
                    run = matrix.COPY_MATRIX_2X2(mA);
                    xforms[j][0][2] = trans[0];
                    xforms[j][1][2] = trans[1];
                    double[] array = trans;
                    int n = 0;
                    array[n] += deltaTrans[0];
                    double[] array2 = trans;
                    int n2 = 1;
                    array2[n2] += deltaTrans[1];
                }
            }
        }
        int style;
        int saveStyle = style = gleGetJoinStyle();
        style &= 0xFFFFFFF0;
        style |= 0x2;
        gleSetJoinStyle(style);
        gleSuperExtrusion(ncp, contour, contourNormal, up, npoints, points, null, xforms);
        gleSetJoinStyle(saveStyle);
    }
    
    @Override
    public void gleLathe(int ncp, double[][] contour, double[][] contourNormal, double[] up, double startRadius, double drdTheta, double startZ, double dzdTheta, double[][] startTransform, double[][] dTransformdTheta, double startTheta, double sweepTheta) throws GLEException {
        double[] localup = new double[3];
        double len = 0.0;
        double[] trans = new double[2];
        double[][] start = new double[2][3];
        double[][] delt = new double[2][3];
        if (up != null) {
            if (up[1] != 0.0) {
                localup[0] = up[0];
                localup[1] = 0.0;
                localup[2] = up[2];
                len = matrix.VEC_LENGTH(localup);
                if (len != 0.0) {
                    len = 1.0 / len;
                    double[] array = localup;
                    int n = 0;
                    array[n] *= len;
                    double[] array2 = localup;
                    int n2 = 2;
                    array2[n2] *= len;
                    localup = matrix.VEC_SCALE(len, localup);
                }
                else {
                    localup[0] = 0.0;
                    localup[2] = 1.0;
                }
            }
            else {
                localup = matrix.VEC_COPY(up);
            }
        }
        else {
            localup[0] = 0.0;
            localup[2] = 1.0;
        }
        trans[0] = localup[2] * drdTheta - localup[0] * dzdTheta;
        trans[1] = localup[0] * drdTheta + localup[2] * dzdTheta;
        if (startTransform != null) {
            if (dTransformdTheta != null) {
                delt = matrix.COPY_MATRIX_2X3(dTransformdTheta);
                double[] array3 = delt[0];
                int n3 = 2;
                array3[n3] += trans[0];
                double[] array4 = delt[1];
                int n4 = 2;
                array4[n4] += trans[1];
            }
            else {
                delt[0][0] = 0.0;
                delt[0][1] = 0.0;
                delt[0][2] = trans[0];
                delt[1][0] = 0.0;
                delt[1][1] = 0.0;
                delt[1][2] = trans[1];
            }
            gleSpiral(ncp, contour, contourNormal, up, startRadius, 0.0, startZ, 0.0, startTransform, delt, startTheta, sweepTheta);
        }
        else {
            start[0][0] = 1.0;
            start[0][1] = 0.0;
            start[0][2] = 0.0;
            start[1][0] = 0.0;
            start[1][1] = 1.0;
            start[1][2] = 0.0;
            delt[0][0] = 0.0;
            delt[0][1] = 0.0;
            delt[0][2] = trans[0];
            delt[1][0] = 0.0;
            delt[1][1] = 0.0;
            delt[1][2] = trans[1];
            gleSpiral(ncp, contour, contourNormal, up, startRadius, 0.0, startZ, 0.0, start, delt, startTheta, sweepTheta);
        }
    }
    
    @Override
    public void gleHelicoid(double rToroid, double startRadius, double drdTheta, double startZ, double dzdTheta, double[][] startTransform, double[][] dTransformdTheta, double startTheta, double sweepTheta) throws GLEException {
        super_helix(rToroid, startRadius, drdTheta, startZ, dzdTheta, startTransform, dTransformdTheta, startTheta, sweepTheta, "Spiral");
    }
    
    @Override
    public void gleToroid(double rToroid, double startRadius, double drdTheta, double startZ, double dzdTheta, double[][] startTransform, double[][] dTransformdTheta, double startTheta, double sweepTheta) throws GLEException {
        super_helix(rToroid, startRadius, drdTheta, startZ, dzdTheta, startTransform, dTransformdTheta, startTheta, sweepTheta, "Lathe");
    }
    
    @Override
    public void gleScrew(int ncp, double[][] contour, double[][] contourNormal, double[] up, double startz, double endz, double twist) throws GLEException {
        int numsegs = (int)Math.abs(twist / 18.0) + 4;
        double[][] path = new double[numsegs][3];
        double[] twarr = new double[numsegs];
        double delta = 0.0;
        double currz = 0.0;
        double currang = 0.0;
        double delang = 0.0;
        delta = (endz - startz) / (numsegs - 3);
        currz = startz - delta;
        delang = twist / (numsegs - 3);
        currang = -delang;
        for (int i = 0; i < numsegs; ++i) {
            path[i][0] = 0.0;
            path[i][1] = 0.0;
            path[i][2] = currz;
            currz += delta;
            twarr[i] = currang;
            currang += delang;
        }
        gleTwistExtrusion(ncp, contour, contourNormal, up, numsegs, path, null, twarr);
    }
    
    private void super_helix(double rToroid, double startRadius, double drdTheta, double startZ, double dzdTheta, double[][] startTransform, double[][] dTransformdTheta, double startTheta, double sweepTheta, String callback) {
        double[][] circle = new double[_POLYCYL_TESS][2];
        double[][] norm = new double[_POLYCYL_TESS][2];
        double c = 0.0;
        double s = 0.0;
        double[] up = new double[3];
        s = Math.sin(6.283185307179586 / _POLYCYL_TESS);
        c = Math.cos(6.283185307179586 / _POLYCYL_TESS);
        norm[0][0] = 1.0;
        norm[0][1] = 0.0;
        circle[0][0] = rToroid;
        circle[0][1] = 0.0;
        for (int i = 1; i < _POLYCYL_TESS; ++i) {
            norm[i][0] = norm[i - 1][0] * c - norm[i - 1][1] * s;
            norm[i][1] = norm[i - 1][0] * s + norm[i - 1][1] * c;
            circle[i][0] = rToroid * norm[i][0];
            circle[i][1] = rToroid * norm[i][1];
        }
        up[1] = (up[2] = 0.0);
        up[0] = 1.0;
        int style;
        int saveStyle = style = gleGetJoinStyle();
        style |= 0x1000;
        style |= 0x400;
        gleSetJoinStyle(style);
        if (!GL11.glIsEnabled(2896)) {
            if (callback.equals("Spiral")) {
                gleSpiral(_POLYCYL_TESS, circle, null, up, startRadius, drdTheta, startZ, dzdTheta, startTransform, dTransformdTheta, startTheta, sweepTheta);
            }
            else {
                if (!callback.equals("Lathe")) {
                    throw new GLEException("Specified callback " + callback + " is not registered. Use either ``Spiral'' or ``Lathe''");
                }
                gleLathe(_POLYCYL_TESS, circle, null, up, startRadius, drdTheta, startZ, dzdTheta, startTransform, dTransformdTheta, startTheta, sweepTheta);
            }
        }
        else if (callback.equals("Spiral")) {
            gleSpiral(_POLYCYL_TESS, circle, norm, up, startRadius, drdTheta, startZ, dzdTheta, startTransform, dTransformdTheta, startTheta, sweepTheta);
        }
        else {
            if (!callback.equals("Lathe")) {
                throw new GLEException("Specified callback " + callback + " is not registered. Use either ``Spiral'' or ``Lathe''");
            }
            gleLathe(_POLYCYL_TESS, circle, norm, up, startRadius, drdTheta, startZ, dzdTheta, startTransform, dTransformdTheta, startTheta, sweepTheta);
        }
        gleSetJoinStyle(saveStyle);
    }
    
    private double[] up_sanity_check(double[] up, int npoints, double[][] pointArray) {
        double len = 0.0;
        double[] diff = null;
        double[] vtmp = null;
        diff = matrix.VEC_DIFF(pointArray[1], pointArray[0]);
        len = matrix.VEC_LENGTH(diff);
        if (len == 0.0) {
            for (int i = 1; i < npoints - 2; ++i) {
                diff = matrix.VEC_DIFF(pointArray[i + 1], pointArray[i]);
                len = matrix.VEC_LENGTH(diff);
                if (len != 0.0) {
                    break;
                }
            }
        }
        len = 1.0 / len;
        diff = matrix.VEC_SCALE(len, diff);
        vtmp = matrix.VEC_PERP(up, diff);
        len = matrix.VEC_LENGTH(vtmp);
        if (len == 0.0) {
            System.err.println("Extrusion: Warning: ");
            System.err.println("contour up vector parallel to tubing direction");
            vtmp = matrix.VEC_COPY(diff);
        }
        return vtmp;
    }
    
    private void extrusion_raw_join(int ncp, double[][] contour, double[][] contourNormal, double[] up, int npoints, double[][] pointArray, float[][] colourArray, double[][][] xformArray) {
        int i = 0;
        int j = 0;
        int inext = 0;
        double[][] m = null;
        double len = 0.0;
        double[] diff = new double[3];
        double[] bi_0 = new double[3];
        double[] yup = new double[3];
        double[] nrmv = new double[3];
        boolean no_norm = contourNormal == null;
        boolean no_cols = colourArray == null;
        boolean no_xform = xformArray == null;
        double[][] mem_anchor = null;
        double[][] front_loop = null;
        double[][] back_loop = null;
        double[][] front_norm = null;
        double[][] back_norm = null;
        double[][] tmp = null;
        nrmv[0] = (nrmv[1] = 0.0);
        if (!no_xform) {
            front_loop = new double[ncp][3];
            back_loop = new double[ncp][3];
            front_norm = new double[ncp][3];
            back_norm = new double[ncp][3];
        }
        if (up == null) {
            yup[0] = 0.0;
            yup[1] = 1.0;
            yup[2] = 0.0;
        }
        else {
            yup = matrix.VEC_COPY(up);
        }
        up = matrix.VEC_COPY(yup);
        yup = up_sanity_check(up, npoints, pointArray);
        i = (inext = 1);
        inext = intersect.FIND_NON_DEGENERATE_POINT(inext, npoints, len, diff, pointArray);
        len = matrix.VEC_LENGTH(diff);
        if (!no_xform) {
            for (j = 0; j < ncp; ++j) {
                (front_loop[j] = matrix.MAT_DOT_VEC_2X3(xformArray[inext - 1], contour[j]))[2] = 0.0;
            }
            if (!no_norm) {
                for (j = 0; j < ncp; ++j) {
                    (front_norm[j] = matrix.NORM_XFORM_2X2(xformArray[inext - 1], contourNormal[j]))[2] = 0.0;
                    back_norm[j][2] = 0.0;
                }
            }
        }
        while (inext < npoints - 1) {
            bi_0 = intersect.bisecting_plane(pointArray[i - 1], pointArray[i], pointArray[inext]);
            yup = matrix.VEC_REFLECT(yup, bi_0);
            m = matrix.uviewpoint_d(pointArray[i], pointArray[inext], yup);
            DoubleBuffer mbuffer = BufferUtils.createDoubleBuffer(16);
            mbuffer.put(new double[] { m[0][0], m[0][1], m[0][2], m[0][3], m[1][0], m[1][1], m[1][2], m[1][3], m[2][0], m[2][1], m[2][2], m[2][3], m[3][0], m[3][1], m[3][2], m[3][3] });
            mbuffer.flip();
            GL11.glPushMatrix();
            GL11.glMultMatrix(mbuffer);
            if (no_xform) {
                if (no_cols) {
                    if (no_norm) {
                        draw_raw_segment_plain(ncp, contour, inext, len);
                    }
                    else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                        draw_raw_segment_facet_n(ncp, contour, contourNormal, inext, len);
                    }
                    else {
                        draw_raw_segment_edge_n(ncp, contour, contourNormal, inext, len);
                    }
                }
                else if (no_norm) {
                    draw_raw_segment_color(ncp, contour, colourArray, inext, len);
                }
                else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                    draw_raw_segment_c_and_facet_n(ncp, contour, colourArray, contourNormal, inext, len);
                }
                else {
                    draw_raw_segment_c_and_edge_n(ncp, contour, colourArray, contourNormal, inext, len);
                }
            }
            else {
                for (j = 0; j < ncp; ++j) {
                    (back_loop[j] = matrix.MAT_DOT_VEC_2X3(xformArray[inext], contour[j]))[2] = -len;
                    front_loop[j][2] = 0.0;
                }
                if (!no_norm) {
                    for (j = 0; j < ncp; ++j) {
                        back_norm[j] = matrix.NORM_XFORM_2X2(xformArray[inext], contourNormal[j]);
                    }
                }
                if (no_cols) {
                    if (no_norm) {
                        draw_segment_plain(ncp, front_loop, back_loop, inext, len);
                    }
                    else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                        draw_binorm_segment_facet_n(ncp, front_loop, back_loop, front_norm, back_norm, inext, len);
                    }
                    else {
                        draw_binorm_segment_edge_n(ncp, front_loop, back_loop, front_norm, back_norm, inext, len);
                    }
                    if ((gleGetJoinStyle() & 0x10) == 0x10) {
                        nrmv[2] = 1.0;
                        GL11.glNormal3d(nrmv[0], nrmv[1], nrmv[2]);
                        draw_front_contour_cap(ncp, front_loop);
                        nrmv[2] = -1.0;
                        GL11.glNormal3d(nrmv[0], nrmv[1], nrmv[2]);
                        draw_back_contour_cap(ncp, back_loop);
                    }
                }
                else {
                    if (no_norm) {
                        draw_segment_color(ncp, front_loop, back_loop, colourArray[inext - 1], colourArray[inext], inext, len);
                    }
                    else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                        draw_binorm_segment_c_and_facet_n(ncp, front_loop, back_loop, front_norm, back_norm, colourArray[inext - 1], colourArray[inext], inext, len);
                    }
                    else {
                        draw_binorm_segment_c_and_edge_n(ncp, front_loop, back_loop, front_norm, back_norm, colourArray[inext - 1], colourArray[inext], inext, len);
                    }
                    if ((gleGetJoinStyle() & 0x10) == 0x10) {
                        GL11.glColor4f(colourArray[inext - 1][0], colourArray[inext - 1][1], colourArray[inext - 1][2], colourArray[inext - 1][3]);
                        nrmv[2] = 1.0;
                        GL11.glNormal3d(nrmv[0], nrmv[1], nrmv[2]);
                        draw_front_contour_cap(ncp, front_loop);
                        GL11.glColor4f(colourArray[inext][0], colourArray[inext][1], colourArray[inext][2], colourArray[inext][3]);
                        nrmv[2] = -1.0;
                        GL11.glNormal3d(nrmv[0], nrmv[1], nrmv[2]);
                        draw_back_contour_cap(ncp, back_loop);
                    }
                }
            }
            GL11.glPopMatrix();
            tmp = front_loop;
            front_loop = back_loop;
            back_loop = tmp;
            tmp = front_norm;
            front_norm = back_norm;
            back_norm = tmp;
            i = inext;
            inext = intersect.FIND_NON_DEGENERATE_POINT(inext, npoints, len, diff, pointArray);
            len = matrix.VEC_LENGTH(diff);
        }
    }
    
    private void draw_raw_segment_plain(int ncp, double[][] contour, int inext, double len) {
        double[] point = new double[3];
        System.err.println("draw_raw_segment_plain()");
        GL11.glBegin(5);
        for (int j = 0; j < ncp; ++j) {
            point[0] = contour[j][0];
            point[1] = contour[j][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            point[0] = contour[0][0];
            point[1] = contour[0][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        GL11.glEnd();
        if ((gleGetJoinStyle() & 0x10) == 0x10) {
            draw_raw_style_end_cap(ncp, contour, 0.0, true);
            draw_raw_style_end_cap(ncp, contour, -len, false);
        }
    }
    
    private void draw_raw_segment_color(int ncp, double[][] contour, float[][] color_array, int inext, double len) {
        double[] point = new double[3];
        System.out.println("draw_raw_segment_color");
        GL11.glBegin(5);
        double tc = 0.0;
        for (int j = 0; j < ncp; ++j) {
            tc = j / (double)ncp;
            point[0] = contour[j][0];
            point[1] = contour[j][1];
            point[2] = 0.0;
            GL11.glTexCoord2d(tc, SLICE_PROGRESS);
            GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[2] = -len;
            GL11.glTexCoord2d(tc, SLICE_PROGRESS + SLICE);
            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            point[0] = contour[0][0];
            point[1] = contour[0][1];
            point[2] = 0.0;
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS);
            GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[2] = -len;
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS + SLICE);
            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        GL11.glEnd();
        if ((gleGetJoinStyle() & 0x10) == 0x10) {
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS);
            GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
            draw_raw_style_end_cap(ncp, contour, 0.0, true);
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS + SLICE);
            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
            draw_raw_style_end_cap(ncp, contour, -len, false);
        }
        SLICE_PROGRESS += SLICE;
    }
    
    private void draw_raw_segment_edge_n(int ncp, double[][] contour, double[][] cont_normal, int inext, double len) {
        double[] point = new double[3];
        double[] norm = new double[3];
        System.err.println("draw_raw_segment_edge_n");
        norm[2] = 0.0;
        GL11.glBegin(5);
        for (int j = 0; j < ncp; ++j) {
            norm[0] = cont_normal[j][0];
            norm[1] = cont_normal[j][1];
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[0] = contour[j][0];
            point[1] = contour[j][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            norm[0] = cont_normal[0][0];
            norm[1] = cont_normal[0][1];
            norm[2] = 0.0;
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[0] = contour[0][0];
            point[1] = contour[0][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        GL11.glEnd();
        if ((gleGetJoinStyle() & 0x10) == 0x10) {
            norm[0] = (norm[1] = 0.0);
            norm[2] = 1.0;
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            draw_raw_style_end_cap(ncp, contour, 0.0, true);
            norm[2] = -1.0;
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            draw_raw_style_end_cap(ncp, contour, -len, false);
        }
    }
    
    private void draw_raw_segment_c_and_edge_n(int ncp, double[][] contour, float[][] color_array, double[][] cont_normal, int inext, double len) {
        double[] point = new double[3];
        double[] norm = new double[3];
        System.out.println("draw_raw_segment_c_and_edge_n");
        norm[2] = 0.0;
        GL11.glBegin(5);
        for (int j = 0; j < ncp; ++j) {
            GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
            norm[0] = cont_normal[j][0];
            norm[1] = cont_normal[j][1];
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[0] = contour[j][0];
            point[1] = contour[j][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
            norm[0] = cont_normal[0][0];
            norm[1] = cont_normal[0][1];
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[0] = contour[0][0];
            point[1] = contour[0][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
            norm[0] = cont_normal[0][0];
            norm[1] = cont_normal[0][1];
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        GL11.glEnd();
        if ((gleGetJoinStyle() & 0x10) == 0x10) {
            GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
            norm[0] = (norm[1] = 0.0);
            norm[2] = 1.0;
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            draw_raw_style_end_cap(ncp, contour, 0.0, true);
            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
            norm[2] = -1.0;
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            draw_raw_style_end_cap(ncp, contour, -len, false);
        }
    }
    
    private void draw_raw_segment_facet_n(int ncp, double[][] contour, double[][] cont_normal, int inext, double len) {
        double[] point = new double[3];
        double[] norm = new double[3];
        System.out.println("draw_raw_segment_facet_n");
        norm[2] = 0.0;
        GL11.glBegin(5);
        for (int j = 0; j < ncp - 1; ++j) {
            norm[0] = cont_normal[j][0];
            norm[1] = cont_normal[j][1];
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[0] = contour[j][0];
            point[1] = contour[j][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[0] = contour[j + 1][0];
            point[1] = contour[j + 1][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            norm[0] = cont_normal[ncp - 1][0];
            norm[1] = cont_normal[ncp - 1][1];
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[0] = contour[ncp - 1][0];
            point[1] = contour[ncp - 1][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[0] = contour[0][0];
            point[1] = contour[0][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        GL11.glEnd();
        if ((gleGetJoinStyle() & 0x10) == 0x10) {
            norm[0] = (norm[1] = 0.0);
            norm[2] = 1.0;
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            draw_raw_style_end_cap(ncp, contour, 0.0, true);
            norm[2] = -1.0;
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            draw_raw_style_end_cap(ncp, contour, -len, false);
        }
    }
    
    private void draw_raw_segment_c_and_facet_n(int ncp, double[][] contour, float[][] color_array, double[][] cont_normal, int inext, double len) {
        System.out.println("draw_raw_segment_c_and_facet_n");
        double[] point = new double[3];
        double[] norm = { 0.0, 0.0, 0.0 };
        GL11.glBegin(5);
        for (int j = 0; j < ncp - 1; ++j) {
            GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
            norm[0] = cont_normal[j][0];
            norm[1] = cont_normal[j][1];
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[0] = contour[j][0];
            point[1] = contour[j][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
            GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[0] = contour[j + 1][0];
            point[1] = contour[j + 1][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            point[0] = contour[ncp - 1][0];
            point[1] = contour[ncp - 1][1];
            point[2] = 0.0;
            GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
            norm[0] = cont_normal[ncp - 1][0];
            norm[1] = cont_normal[ncp - 1][1];
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            GL11.glVertex3d(point[0], point[1], point[2]);
            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
            GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
            norm[0] = cont_normal[0][0];
            norm[1] = cont_normal[0][1];
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[0] = contour[0][0];
            point[1] = contour[0][1];
            point[2] = 0.0;
            GL11.glVertex3d(point[0], point[1], point[2]);
            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            point[2] = -len;
            GL11.glVertex3d(point[0], point[1], point[2]);
        }
        GL11.glEnd();
        if ((gleGetJoinStyle() & 0x10) == 0x10) {
            GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
            norm[0] = (norm[1] = 0.0);
            norm[2] = 1.0;
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            draw_raw_style_end_cap(ncp, contour, 0.0, true);
            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
            norm[2] = -1.0;
            GL11.glNormal3d(norm[0], norm[1], norm[2]);
            draw_raw_style_end_cap(ncp, contour, -len, false);
        }
    }
    
    private void draw_raw_style_end_cap(int ncp, double[][] contour, double zval, boolean frontwards) {
        System.out.println("draw_raw_style_end_cap");
        GLUtessellator tobj = GLU.gluNewTess();
        tobj.gluTessProperty(100140, 100130.0);
        tobj.gluTessCallback(100101, tessCallback);
        tobj.gluTessCallback(100100, tessCallback);
        tobj.gluTessCallback(100102, tessCallback);
        tobj.gluTessCallback(100103, tessCallback);
        tobj.gluTessBeginPolygon(null);
        tobj.gluTessBeginContour();
        if (frontwards) {
            for (int j = 0; j < ncp; ++j) {
                double[] vertex = { contour[j][0], contour[j][1], zval };
                tobj.gluTessVertex(vertex, 0, vertex);
            }
        }
        else {
            for (int j = ncp - 1; j > -1; --j) {
                double[] vertex = { contour[j][0], contour[j][1], zval };
                tobj.gluTessVertex(vertex, 0, vertex);
            }
        }
        tobj.gluTessEndContour();
        tobj.gluTessEndPolygon();
        tobj.gluDeleteTess();
    }
    
    private void draw_front_contour_cap(int ncp, double[][] contour) {
        GLUtessellator tobj = GLU.gluNewTess();
        tobj.gluTessProperty(100140, 100130.0);
        tobj.gluTessCallback(100101, tessCallback);
        tobj.gluTessCallback(100100, tessCallback);
        tobj.gluTessCallback(100102, tessCallback);
        tobj.gluTessCallback(100103, tessCallback);
        tobj.gluTessBeginPolygon(null);
        tobj.gluTessBeginContour();
        for (int j = 0; j < ncp; ++j) {
            tobj.gluTessVertex(contour[j], 0, contour[j]);
        }
        tobj.gluTessEndContour();
        tobj.gluTessEndPolygon();
        tobj.gluDeleteTess();
    }
    
    private void draw_back_contour_cap(int ncp, double[][] contour) {
        GLUtessellator tobj = GLU.gluNewTess();
        tobj.gluTessProperty(100140, 100132.0);
        tobj.gluTessCallback(100101, tessCallback);
        tobj.gluTessCallback(100100, tessCallback);
        tobj.gluTessCallback(100102, tessCallback);
        tobj.gluTessCallback(100103, tessCallback);
        tobj.gluTessBeginPolygon(null);
        tobj.gluTessBeginContour();
        for (int j = ncp - 1; j > -1; --j) {
            tobj.gluTessVertex(contour[j], 0, contour[j]);
        }
        tobj.gluTessEndContour();
        tobj.gluTessEndPolygon();
        tobj.gluDeleteTess();
    }
    
    private void extrusion_angle_join(int ncp, double[][] contour, double[][] cont_normal, double[] up, int npoints, double[][] point_array, float[][] color_array, double[][][] xform_array) {
        int i = 0;
        int j = 0;
        int inext = 0;
        int inextnext = 0;
        double[][] m = new double[4][4];
        double len = 0.0;
        double len_seg = 0.0;
        double[] diff = new double[3];
        double[] bi_0 = new double[3];
        double[] bi_2 = new double[3];
        double[] bisector_0 = new double[3];
        double[] bisector_2 = new double[3];
        double[] end_point_0 = new double[3];
        double[] end_point_2 = new double[3];
        double[] origin = new double[3];
        double[] neg_z = new double[3];
        double[] yup = new double[3];
        if (up == null) {
            yup[0] = 0.0;
            yup[1] = 1.0;
            yup[2] = 0.0;
        }
        else {
            yup = matrix.VEC_COPY(up);
        }
        yup = up_sanity_check(yup, npoints, point_array);
        origin[0] = 0.0;
        origin[2] = (origin[1] = 0.0);
        neg_z[1] = (neg_z[0] = 0.0);
        neg_z[2] = 1.0;
        i = (inext = 1);
        inext = intersect.FIND_NON_DEGENERATE_POINT(inext, npoints, len, diff, point_array);
        len = (len_seg = matrix.VEC_LENGTH(diff));
        bi_0 = intersect.bisecting_plane(point_array[0], point_array[1], point_array[inext]);
        yup = matrix.VEC_REFLECT(yup, bi_0);
        double[][] front_loop = new double[ncp][3];
        double[][] back_loop = new double[ncp][3];
        double[][] front_norm = new double[ncp][3];
        double[][] back_norm = new double[ncp][3];
        double[][] norm_loop = front_norm;
        if (cont_normal != null) {
            if (xform_array == null) {
                for (j = 0; j < ncp; ++j) {
                    norm_loop[j][0] = cont_normal[j][0];
                    norm_loop[j][1] = cont_normal[j][1];
                    norm_loop[j][2] = 0.0;
                }
            }
            else {
                for (j = 0; j < ncp; ++j) {
                    (front_norm[j] = matrix.NORM_XFORM_2X2(xform_array[inext - 1], cont_normal[j]))[2] = 0.0;
                    back_norm[j][2] = 0.0;
                }
            }
        }
        boolean first_time = true;
        while (inext < npoints - 1) {
            inextnext = inext;
            inextnext = intersect.FIND_NON_DEGENERATE_POINT(inextnext, npoints, len, diff, point_array);
            len = matrix.VEC_LENGTH(diff);
            bi_2 = intersect.bisecting_plane(point_array[i], point_array[inext], point_array[inextnext]);
            m = matrix.uviewpoint_d(point_array[i], point_array[inext], yup);
            DoubleBuffer mbuffer = BufferUtils.createDoubleBuffer(16);
            mbuffer.put(new double[] { m[0][0], m[0][1], m[0][2], m[0][3], m[1][0], m[1][1], m[1][2], m[1][3], m[2][0], m[2][1], m[2][2], m[2][3], m[3][0], m[3][1], m[3][2], m[3][3] });
            mbuffer.flip();
            GL11.glPushMatrix();
            GL11.glMultMatrix(mbuffer);
            bisector_0 = matrix.MAT_DOT_VEC_3X3(m, bi_0);
            bisector_2 = matrix.MAT_DOT_VEC_3X3(m, bi_2);
            neg_z[2] = -len_seg;
            for (j = 0; j < ncp; ++j) {
                if (cont_normal != null) {
                    if (xform_array != null) {
                        back_norm[j] = matrix.NORM_XFORM_2X2(xform_array[inext], cont_normal[j]);
                    }
                    if ((gleGetJoinStyle() & 0x400) == 0x400) {
                        if (xform_array == null) {
                            back_norm[j][0] = cont_normal[j][0];
                            back_norm[j][1] = cont_normal[j][1];
                        }
                        front_norm[j][2] = 0.0;
                        front_norm[j] = matrix.VEC_PERP(front_norm[j], bisector_0);
                        front_norm[j] = matrix.VEC_NORMALIZE(front_norm[j]);
                        back_norm[j][2] = 0.0;
                        back_norm[j] = matrix.VEC_PERP(back_norm[j], bisector_2);
                        back_norm[j] = matrix.VEC_NORMALIZE(back_norm[j]);
                    }
                }
                if (xform_array == null) {
                    end_point_0[0] = contour[j][0];
                    end_point_0[1] = contour[j][1];
                    end_point_2[0] = contour[j][0];
                    end_point_2[1] = contour[j][1];
                }
                else {
                    end_point_0 = matrix.MAT_DOT_VEC_2X3(xform_array[inext - 1], contour[j]);
                    end_point_2 = matrix.MAT_DOT_VEC_2X3(xform_array[inext - 1], contour[j]);
                }
                end_point_0[2] = 0.0;
                end_point_2[2] = -len_seg;
                front_loop[j] = intersect.INNERSECT(origin, bisector_0, end_point_0, end_point_2);
                if (xform_array != null) {
                    end_point_0 = matrix.MAT_DOT_VEC_2X3(xform_array[inext], contour[j]);
                    end_point_2 = matrix.MAT_DOT_VEC_2X3(xform_array[inext], contour[j]);
                }
                end_point_0[2] = 0.0;
                end_point_2[2] = -len_seg;
                back_loop[j] = intersect.INNERSECT(neg_z, bisector_2, end_point_0, end_point_2);
            }
            if ((gleGetJoinStyle() & 0x10) == 0x10) {
                if (first_time) {
                    if (color_array != null) {
                        GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
                    }
                    first_time = false;
                    draw_angle_style_front_cap(ncp, bisector_0, front_loop);
                }
                if (inext == npoints - 2) {
                    if (color_array != null) {
                        GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
                    }
                    draw_angle_style_back_cap(ncp, bisector_2, back_loop);
                }
            }
            if (xform_array == null && (gleGetJoinStyle() & 0x400) != 0x400) {
                if (color_array == null) {
                    if (cont_normal == null) {
                        draw_segment_plain(ncp, front_loop, back_loop, inext, len_seg);
                    }
                    else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                        draw_segment_facet_n(ncp, front_loop, back_loop, norm_loop, inext, len_seg);
                    }
                    else {
                        draw_segment_edge_n(ncp, front_loop, back_loop, norm_loop, inext, len_seg);
                    }
                }
                else if (cont_normal == null) {
                    draw_segment_color(ncp, front_loop, back_loop, color_array[inext - 1], color_array[inext], inext, len_seg);
                }
                else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                    draw_segment_c_and_facet_n(ncp, front_loop, back_loop, norm_loop, color_array[inext - 1], color_array[inext], inext, len_seg);
                }
                else {
                    draw_segment_c_and_edge_n(ncp, front_loop, back_loop, norm_loop, color_array[inext - 1], color_array[inext], inext, len_seg);
                }
            }
            else if (color_array == null) {
                if (cont_normal == null) {
                    draw_segment_plain(ncp, front_loop, back_loop, inext, len_seg);
                }
                else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                    draw_binorm_segment_facet_n(ncp, front_loop, back_loop, front_norm, back_norm, inext, len_seg);
                }
                else {
                    draw_binorm_segment_edge_n(ncp, front_loop, back_loop, front_norm, back_norm, inext, len_seg);
                }
            }
            else if (cont_normal == null) {
                draw_segment_color(ncp, front_loop, back_loop, color_array[inext - 1], color_array[inext], inext, len_seg);
            }
            else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                draw_binorm_segment_c_and_facet_n(ncp, front_loop, back_loop, front_norm, back_norm, color_array[inext - 1], color_array[inext], inext, len_seg);
            }
            else {
                draw_binorm_segment_c_and_edge_n(ncp, front_loop, back_loop, front_norm, back_norm, color_array[inext - 1], color_array[inext], inext, len_seg);
            }
            GL11.glPopMatrix();
            len_seg = len;
            i = inext;
            inext = inextnext;
            bi_0 = matrix.VEC_COPY(bi_2);
            double[][] tmp = front_norm;
            front_norm = back_norm;
            back_norm = tmp;
            yup = matrix.VEC_REFLECT(yup, bi_0);
        }
    }
    
    private void draw_angle_style_front_cap(int ncp, double[] bi, double[][] point_array) {
        GLUtessellator tobj = GLU.gluNewTess();
        tobj.gluTessProperty(100140, 100130.0);
        tobj.gluTessCallback(100101, tessCallback);
        tobj.gluTessCallback(100100, tessCallback);
        tobj.gluTessCallback(100102, tessCallback);
        tobj.gluTessCallback(100103, tessCallback);
        if (bi[2] < 0.0) {
            bi = matrix.VEC_SCALE(-1.0, bi);
        }
        GL11.glNormal3d(bi[0], bi[1], bi[2]);
        tobj.gluTessBeginPolygon(null);
        tobj.gluTessBeginContour();
        for (int j = 0; j < ncp; ++j) {
            tobj.gluTessVertex(point_array[j], 0, point_array[j]);
        }
        tobj.gluTessEndContour();
        tobj.gluTessEndPolygon();
        tobj.gluDeleteTess();
    }
    
    private void draw_angle_style_back_cap(int ncp, double[] bi, double[][] point_array) {
        GLUtessellator tobj = GLU.gluNewTess();
        tobj.gluTessProperty(100140, 100130.0);
        tobj.gluTessCallback(100101, tessCallback);
        tobj.gluTessCallback(100100, tessCallback);
        tobj.gluTessCallback(100102, tessCallback);
        tobj.gluTessCallback(100103, tessCallback);
        if (bi[2] > 0.0) {
            bi = matrix.VEC_SCALE(-1.0, bi);
        }
        GL11.glNormal3d(bi[0], bi[1], bi[2]);
        tobj.gluTessBeginPolygon(null);
        tobj.gluTessBeginContour();
        for (int j = ncp - 1; j >= 0; --j) {
            tobj.gluTessVertex(point_array[j], 0, point_array[j]);
        }
        tobj.gluTessEndContour();
        tobj.gluTessEndPolygon();
        tobj.gluDeleteTess();
    }
    
    private void draw_segment_plain(int ncp, double[][] front_contour, double[][] back_contour, int inext, double len) {
        System.out.println("draw_segment_plain");
        GL11.glBegin(5);
        for (int j = 0; j < ncp; ++j) {
            double tc = j / (double)ncp;
            GL11.glTexCoord2d(tc, SLICE_PROGRESS);
            GL11.glVertex3d(front_contour[j][0], front_contour[j][1], front_contour[j][2]);
            GL11.glTexCoord2d(tc, SLICE_PROGRESS + SLICE);
            GL11.glVertex3d(back_contour[j][0], back_contour[j][1], back_contour[j][2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS);
            GL11.glVertex3d(front_contour[0][0], front_contour[0][1], front_contour[0][2]);
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS + SLICE);
            GL11.glVertex3d(back_contour[0][0], back_contour[0][1], back_contour[0][2]);
        }
        GL11.glEnd();
        SLICE_PROGRESS += SLICE;
    }
    
    private void draw_segment_color(int ncp, double[][] front_contour, double[][] back_contour, float[] color_last, float[] color_next, int inext, double len) {
        GL11.glBegin(5);
        double tc = 0.0;
        for (int j = 0; j < ncp; ++j) {
            tc = j / (double)ncp;
            GL11.glTexCoord2d(tc, SLICE_PROGRESS);
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glVertex3d(front_contour[j][0], front_contour[j][1], front_contour[j][2]);
            GL11.glTexCoord2d(tc, SLICE_PROGRESS + SLICE);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glVertex3d(back_contour[j][0], back_contour[j][1], back_contour[j][2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS);
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glVertex3d(front_contour[0][0], front_contour[0][1], front_contour[0][2]);
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS + SLICE);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glVertex3d(back_contour[0][0], back_contour[0][1], back_contour[0][2]);
        }
        GL11.glEnd();
        SLICE_PROGRESS += SLICE;
    }
    
    private void draw_segment_edge_n(int ncp, double[][] front_contour, double[][] back_contour, double[][] norm_cont, int inext, double len) {
        System.out.println("draw_segment_edge_n");
        GL11.glBegin(5);
        for (int j = 0; j < ncp; ++j) {
            GL11.glNormal3d(norm_cont[j][0], norm_cont[j][1], norm_cont[j][2]);
            GL11.glVertex3d(front_contour[j][0], front_contour[j][1], front_contour[j][2]);
            GL11.glVertex3d(back_contour[j][0], back_contour[j][1], back_contour[j][2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            GL11.glNormal3d(norm_cont[0][0], norm_cont[0][1], norm_cont[0][2]);
            GL11.glVertex3d(front_contour[0][0], front_contour[0][1], front_contour[0][2]);
            GL11.glVertex3d(back_contour[0][0], back_contour[0][1], back_contour[0][2]);
        }
        GL11.glEnd();
    }
    
    private void draw_segment_c_and_edge_n(int ncp, double[][] front_contour, double[][] back_contour, double[][] norm_cont, float[] color_last, float[] color_next, int inext, double len) {
        GL11.glBegin(5);
        double tc = 0.0;
        for (int j = 0; j < ncp; ++j) {
            tc = j / (double)ncp;
            GL11.glTexCoord2d(tc, SLICE_PROGRESS);
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(norm_cont[j][0], norm_cont[j][1], norm_cont[j][2]);
            GL11.glVertex3d(front_contour[j][0], front_contour[j][1], front_contour[j][2]);
            GL11.glTexCoord2d(tc, SLICE_PROGRESS + SLICE);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(norm_cont[j][0], norm_cont[j][1], norm_cont[j][2]);
            GL11.glVertex3d(back_contour[j][0], back_contour[j][1], back_contour[j][2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS);
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(norm_cont[0][0], norm_cont[0][1], norm_cont[0][2]);
            GL11.glVertex3d(front_contour[0][0], front_contour[0][1], front_contour[0][2]);
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS + SLICE);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(norm_cont[0][0], norm_cont[0][1], norm_cont[0][2]);
            GL11.glVertex3d(back_contour[0][0], back_contour[0][1], back_contour[0][2]);
        }
        GL11.glEnd();
        SLICE_PROGRESS += SLICE;
    }
    
    private void draw_segment_facet_n(int ncp, double[][] front_contour, double[][] back_contour, double[][] norm_cont, int inext, double len) {
        GL11.glBegin(5);
        for (int j = 0; j < ncp - 1; ++j) {
            GL11.glNormal3d(norm_cont[j][0], norm_cont[j][1], norm_cont[j][2]);
            GL11.glVertex3d(front_contour[j][0], front_contour[j][1], front_contour[j][2]);
            GL11.glVertex3d(back_contour[j][0], back_contour[j][1], back_contour[j][2]);
            GL11.glVertex3d(front_contour[j + 1][0], front_contour[j + 1][1], front_contour[j + 1][2]);
            GL11.glVertex3d(back_contour[j + 1][0], back_contour[j + 1][1], back_contour[j + 1][2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            GL11.glNormal3d(norm_cont[ncp - 1][0], norm_cont[ncp - 1][1], norm_cont[ncp - 1][2]);
            GL11.glVertex3d(front_contour[ncp - 1][0], front_contour[ncp - 1][1], front_contour[ncp - 1][2]);
            GL11.glVertex3d(back_contour[ncp - 1][0], back_contour[ncp - 1][1], back_contour[ncp - 1][2]);
            GL11.glVertex3d(front_contour[0][0], front_contour[0][1], front_contour[0][2]);
            GL11.glVertex3d(back_contour[0][0], back_contour[0][1], back_contour[0][2]);
        }
        GL11.glEnd();
    }
    
    private void draw_segment_c_and_facet_n(int ncp, double[][] front_contour, double[][] back_contour, double[][] norm_cont, float[] color_last, float[] color_next, int inext, double len) {
        System.out.println("draw_segment_c_and_facet_n");
        GL11.glBegin(5);
        for (int j = 0; j < ncp - 1; ++j) {
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(norm_cont[j][0], norm_cont[j][1], norm_cont[j][2]);
            GL11.glVertex3d(front_contour[j][0], front_contour[j][1], front_contour[j][2]);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(norm_cont[j][0], norm_cont[j][1], norm_cont[j][2]);
            GL11.glVertex3d(back_contour[j][0], back_contour[j][1], back_contour[j][2]);
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(norm_cont[j][0], norm_cont[j][1], norm_cont[j][2]);
            GL11.glVertex3d(front_contour[j + 1][0], front_contour[j + 1][1], front_contour[j + 1][2]);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(norm_cont[j][0], norm_cont[j][1], norm_cont[j][2]);
            GL11.glVertex3d(back_contour[j + 1][0], back_contour[j + 1][1], back_contour[j + 1][2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(norm_cont[ncp - 1][0], norm_cont[ncp - 1][1], norm_cont[ncp - 1][2]);
            GL11.glVertex3d(front_contour[ncp - 1][0], front_contour[ncp - 1][1], front_contour[ncp - 1][2]);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(norm_cont[ncp - 1][0], norm_cont[ncp - 1][1], norm_cont[ncp - 1][2]);
            GL11.glVertex3d(back_contour[ncp - 1][0], back_contour[ncp - 1][1], back_contour[ncp - 1][2]);
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(norm_cont[ncp - 1][0], norm_cont[ncp - 1][1], norm_cont[ncp - 1][2]);
            GL11.glVertex3d(front_contour[0][0], front_contour[0][1], front_contour[0][2]);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(norm_cont[ncp - 1][0], norm_cont[ncp - 1][1], norm_cont[ncp - 1][2]);
            GL11.glVertex3d(back_contour[0][0], back_contour[0][1], back_contour[0][2]);
        }
        GL11.glEnd();
    }
    
    private void draw_binorm_segment_edge_n(int ncp, double[][] front_contour, double[][] back_contour, double[][] front_norm, double[][] back_norm, int inext, double len) {
        GL11.glBegin(5);
        double tc = 0.0;
        for (int j = 0; j < ncp; ++j) {
            tc = j / (double)ncp;
            GL11.glTexCoord2d(tc, SLICE_PROGRESS);
            GL11.glNormal3d(front_norm[j][0], front_norm[j][1], front_norm[j][2]);
            GL11.glVertex3d(front_contour[j][0], front_contour[j][1], front_contour[j][2]);
            GL11.glTexCoord2d(tc, SLICE_PROGRESS + SLICE);
            GL11.glNormal3d(back_norm[j][0], back_norm[j][1], back_norm[j][2]);
            GL11.glVertex3d(back_contour[j][0], back_contour[j][1], back_contour[j][2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS);
            GL11.glNormal3d(front_norm[0][0], front_norm[0][1], front_norm[0][2]);
            GL11.glVertex3d(front_contour[0][0], front_contour[0][1], front_contour[0][2]);
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS + SLICE);
            GL11.glNormal3d(back_norm[0][0], back_norm[0][1], back_norm[0][2]);
            GL11.glVertex3d(back_contour[0][0], back_contour[0][1], back_contour[0][2]);
        }
        GL11.glEnd();
        SLICE_PROGRESS += SLICE;
    }
    
    private void draw_binorm_segment_c_and_edge_n(int ncp, double[][] front_contour, double[][] back_contour, double[][] front_norm, double[][] back_norm, float[] color_last, float[] color_next, int inext, double len) {
        GL11.glBegin(5);
        double tc = 0.0;
        for (int j = 0; j < ncp; ++j) {
            tc = j / (double)ncp;
            GL11.glTexCoord2d(tc, SLICE_PROGRESS);
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(front_norm[j][0], front_norm[j][1], front_norm[j][2]);
            GL11.glVertex3d(front_contour[j][0], front_contour[j][1], front_contour[j][2]);
            GL11.glTexCoord2d(tc, SLICE_PROGRESS + SLICE);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(front_norm[j][0], front_norm[j][1], front_norm[j][2]);
            GL11.glVertex3d(back_contour[j][0], back_contour[j][1], back_contour[j][2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS);
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(front_norm[0][0], front_norm[0][1], front_norm[0][2]);
            GL11.glVertex3d(front_contour[0][0], front_contour[0][1], front_contour[0][2]);
            GL11.glTexCoord2d(1.0, SLICE_PROGRESS + SLICE);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(back_norm[0][0], back_norm[0][1], back_norm[0][2]);
            GL11.glVertex3d(back_contour[0][0], back_contour[0][1], back_contour[0][2]);
        }
        GL11.glEnd();
        SLICE_PROGRESS += SLICE;
    }
    
    private void draw_binorm_segment_facet_n(int ncp, double[][] front_contour, double[][] back_contour, double[][] front_norm, double[][] back_norm, int inext, double len) {
        System.out.println("draw_binorm_segment_facet_n");
        GL11.glBegin(5);
        for (int j = 0; j < ncp - 1; ++j) {
            GL11.glNormal3d(front_norm[j][0], front_norm[j][1], front_norm[j][2]);
            GL11.glVertex3d(front_contour[j][0], front_contour[j][1], front_contour[j][2]);
            GL11.glNormal3d(back_norm[j][0], back_norm[j][1], back_norm[j][2]);
            GL11.glVertex3d(back_contour[j][0], back_contour[j][1], back_contour[j][2]);
            GL11.glNormal3d(front_norm[j][0], front_norm[j][1], front_norm[j][2]);
            GL11.glVertex3d(front_contour[j + 1][0], front_contour[j + 1][1], front_contour[j + 1][2]);
            GL11.glNormal3d(back_norm[j][0], back_norm[j][1], back_norm[j][2]);
            GL11.glVertex3d(back_contour[j + 1][0], back_contour[j + 1][1], back_contour[j + 1][2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            GL11.glNormal3d(front_norm[ncp - 1][0], front_norm[ncp - 1][1], front_norm[ncp - 1][2]);
            GL11.glVertex3d(front_contour[ncp - 1][0], front_contour[ncp - 1][1], front_contour[ncp - 1][2]);
            GL11.glNormal3d(back_norm[ncp - 1][0], back_norm[ncp - 1][1], back_norm[ncp - 1][2]);
            GL11.glVertex3d(back_contour[ncp - 1][0], back_contour[ncp - 1][1], back_contour[ncp - 1][2]);
            GL11.glNormal3d(front_norm[ncp - 1][0], front_norm[ncp - 1][1], front_norm[ncp - 1][2]);
            GL11.glVertex3d(front_contour[0][0], front_contour[0][1], front_contour[0][2]);
            GL11.glNormal3d(back_norm[ncp - 1][0], back_norm[ncp - 1][1], back_norm[ncp - 1][2]);
            GL11.glVertex3d(back_contour[0][0], back_contour[0][1], back_contour[0][2]);
        }
        GL11.glEnd();
    }
    
    private void draw_binorm_segment_c_and_facet_n(int ncp, double[][] front_contour, double[][] back_contour, double[][] front_norm, double[][] back_norm, float[] color_last, float[] color_next, int inext, double len) {
        System.out.println("draw_binorm_segment_c_and_facet_n");
        GL11.glBegin(5);
        for (int j = 0; j < ncp - 1; ++j) {
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(front_norm[j][0], front_norm[j][1], front_norm[j][2]);
            GL11.glVertex3d(front_contour[j][0], front_contour[j][1], front_contour[j][2]);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(back_norm[j][0], back_norm[j][1], back_norm[j][2]);
            GL11.glVertex3d(back_contour[j][0], back_contour[j][1], back_contour[j][2]);
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(front_norm[j][0], front_norm[j][1], front_norm[j][2]);
            GL11.glVertex3d(front_contour[j + 1][0], front_contour[j + 1][1], front_contour[j + 1][2]);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(back_norm[j][0], back_norm[j][1], back_norm[j][2]);
            GL11.glVertex3d(back_contour[j + 1][0], back_contour[j + 1][1], back_contour[j + 1][2]);
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(front_norm[ncp - 1][0], front_norm[ncp - 1][1], front_norm[ncp - 1][2]);
            GL11.glVertex3d(front_contour[ncp - 1][0], front_contour[ncp - 1][1], front_contour[ncp - 1][2]);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(back_norm[ncp - 1][0], back_norm[ncp - 1][1], back_norm[ncp - 1][2]);
            GL11.glVertex3d(back_contour[ncp - 1][0], back_contour[ncp - 1][1], back_contour[ncp - 1][2]);
            GL11.glColor4f(color_last[0], color_last[1], color_last[2], color_last[3]);
            GL11.glNormal3d(front_norm[ncp - 1][0], front_norm[ncp - 1][1], front_norm[ncp - 1][2]);
            GL11.glVertex3d(front_contour[0][0], front_contour[0][1], front_contour[0][2]);
            GL11.glColor4f(color_next[0], color_next[1], color_next[2], color_next[3]);
            GL11.glNormal3d(back_norm[ncp - 1][0], back_norm[ncp - 1][1], back_norm[ncp - 1][2]);
            GL11.glVertex3d(back_contour[0][0], back_contour[0][1], back_contour[0][2]);
        }
        GL11.glEnd();
    }
    
    private void extrusion_round_or_cut_join(int ncp, double[][] contour, double[][] cont_normal, double[] up, int npoints, double[][] point_array, float[][] color_array, double[][][] xform_array) {
        int i = 0;
        int j = 0;
        int inext = 0;
        int inextnext = 0;
        double[][] m = new double[4][4];
        double tube_len = 0.0;
        double seg_len = 0.0;
        double[] diff = new double[3];
        double[] bi_0 = new double[3];
        double[] bi_2 = new double[3];
        double[] bisector_0 = new double[3];
        double[] bisector_2 = new double[3];
        double[] cut_0 = new double[3];
        double[] cut_2 = new double[3];
        double[] lcut_0 = new double[3];
        double[] lcut_2 = new double[3];
        boolean valid_cut_0 = false;
        boolean valid_cut_2 = false;
        double[] end_point_0 = new double[3];
        double[] end_point_2 = new double[3];
        double[] torsion_point_0 = new double[3];
        double[] torsion_point_2 = new double[3];
        double[] isect_point = new double[3];
        double[] origin = new double[3];
        double[] neg_z = new double[3];
        double[] yup = new double[3];
        double[][] front_cap = null;
        double[][] back_cap = null;
        double[][] front_loop = null;
        double[][] back_loop = null;
        double[][] front_norm = null;
        double[][] back_norm = null;
        double[][] norm_loop = null;
        double[][] tmp = null;
        boolean[] front_is_trimmed = null;
        boolean[] back_is_trimmed = null;
        float[] front_color = null;
        float[] back_color = null;
        boolean join_style_is_cut = false;
        double dot = 0.0;
        boolean first_time = true;
        double[] cut_vec = null;
        String cap_callback = null;
        String tmp_cap_callback = null;
        if ((gleGetJoinStyle() & 0x3) == 0x3) {
            join_style_is_cut = true;
            cap_callback = new String("cut");
        }
        else {
            join_style_is_cut = false;
            cap_callback = new String("round");
        }
        if (up == null) {
            yup[0] = 0.0;
            yup[1] = 1.0;
            yup[2] = 0.0;
        }
        else {
            yup = matrix.VEC_COPY(up);
        }
        yup = up_sanity_check(yup, npoints, point_array);
        origin[0] = 0.0;
        origin[2] = (origin[1] = 0.0);
        neg_z[1] = (neg_z[0] = 0.0);
        neg_z[2] = 1.0;
        front_norm = new double[ncp][3];
        back_norm = new double[ncp][3];
        front_loop = new double[ncp][3];
        back_loop = new double[ncp][3];
        front_cap = new double[ncp][3];
        back_cap = new double[ncp][3];
        front_is_trimmed = new boolean[ncp];
        back_is_trimmed = new boolean[ncp];
        i = (inext = 1);
        inext = intersect.FIND_NON_DEGENERATE_POINT(inext, npoints, seg_len, diff, point_array);
        seg_len = (tube_len = matrix.VEC_LENGTH(diff));
        if (cont_normal != null) {
            if (xform_array == null) {
                norm_loop = (back_norm = front_norm);
                for (j = 0; j < ncp; ++j) {
                    norm_loop[j][0] = cont_normal[j][0];
                    norm_loop[j][1] = cont_normal[j][1];
                    norm_loop[j][2] = 0.0;
                }
            }
            else {
                for (j = 0; j < ncp; ++j) {
                    (front_norm[j] = matrix.NORM_XFORM_2X2(xform_array[inext - 1], cont_normal[j]))[2] = 0.0;
                    back_norm[j][2] = 0.0;
                }
            }
        }
        else {
            back_norm = (front_norm = (norm_loop = null));
        }
        bi_0 = intersect.bisecting_plane(point_array[i - 1], point_array[i], point_array[inext]);
        valid_cut_0 = intersect.CUTTING_PLANE(cut_0, point_array[i - 1], point_array[i], point_array[inext]);
        for (yup = matrix.VEC_REFLECT(yup, bi_0); inext < npoints - 1; inext = inextnext, bi_0 = matrix.VEC_COPY(bi_2), cut_0 = matrix.VEC_COPY(cut_2), valid_cut_0 = valid_cut_2, yup = matrix.VEC_REFLECT(yup, bi_0)) {
            inextnext = inext;
            inextnext = intersect.FIND_NON_DEGENERATE_POINT(inextnext, npoints, seg_len, diff, point_array);
            seg_len = matrix.VEC_LENGTH(diff);
            bi_2 = intersect.bisecting_plane(point_array[i], point_array[inext], point_array[inextnext]);
            valid_cut_2 = intersect.CUTTING_PLANE(cut_2, point_array[i], point_array[inext], point_array[inextnext]);
            m = matrix.uviewpoint_d(point_array[i], point_array[inext], yup);
            DoubleBuffer mbuffer = BufferUtils.createDoubleBuffer(16);
            mbuffer.put(new double[] { m[0][0], m[0][1], m[0][2], m[0][3], m[1][0], m[1][1], m[1][2], m[1][3], m[2][0], m[2][1], m[2][2], m[2][3], m[3][0], m[3][1], m[3][2], m[3][3] });
            mbuffer.flip();
            GL11.glPushMatrix();
            GL11.glMultMatrix(mbuffer);
            lcut_0 = matrix.MAT_DOT_VEC_3X3(m, cut_0);
            lcut_2 = matrix.MAT_DOT_VEC_3X3(m, cut_2);
            bisector_0 = matrix.MAT_DOT_VEC_3X3(m, bi_0);
            bisector_2 = matrix.MAT_DOT_VEC_3X3(m, bi_2);
            neg_z[2] = -tube_len;
            for (j = 0; j < ncp; ++j) {
                if (xform_array == null) {
                    end_point_0 = matrix.VEC_COPY_2(contour[j]);
                    end_point_2 = matrix.VEC_COPY_2(contour[j]);
                    torsion_point_0 = matrix.VEC_COPY_2(contour[j]);
                    torsion_point_2 = matrix.VEC_COPY_2(contour[j]);
                }
                else {
                    end_point_0 = matrix.MAT_DOT_VEC_2X3(xform_array[inext - 1], contour[j]);
                    torsion_point_0 = matrix.MAT_DOT_VEC_2X3(xform_array[inext], contour[j]);
                    end_point_2 = matrix.MAT_DOT_VEC_2X3(xform_array[inext], contour[j]);
                    torsion_point_2 = matrix.MAT_DOT_VEC_2X3(xform_array[inext - 1], contour[j]);
                    if (cont_normal != null) {
                        back_norm[j] = matrix.NORM_XFORM_2X2(xform_array[inext], cont_normal[j]);
                    }
                }
                torsion_point_0[2] = (end_point_0[2] = 0.0);
                torsion_point_2[2] = (end_point_2[2] = -tube_len);
                if (valid_cut_0 && join_style_is_cut) {
                    isect_point = intersect.INNERSECT(origin, lcut_0, end_point_0, end_point_2);
                    if (lcut_0[2] < 0.0) {
                        lcut_0 = matrix.VEC_SCALE(-1.0, lcut_0);
                    }
                    dot = lcut_0[0] * end_point_0[0];
                    dot += lcut_0[1] * end_point_0[1];
                    front_loop[j] = matrix.VEC_COPY(isect_point);
                }
                else {
                    dot = 1.0;
                    front_loop[j] = matrix.VEC_COPY(end_point_0);
                }
                isect_point = intersect.INNERSECT(origin, bisector_0, end_point_0, torsion_point_2);
                if (dot <= 0.0 || isect_point[2] < front_loop[j][2]) {
                    front_cap[j] = matrix.VEC_COPY(front_loop[j]);
                    front_loop[j] = matrix.VEC_COPY(isect_point);
                    front_is_trimmed[j] = true;
                }
                else {
                    front_is_trimmed[j] = false;
                }
                if (front_loop[j][2] < -tube_len) {
                    front_loop[j] = matrix.VEC_COPY(end_point_2);
                }
                if (valid_cut_2 && join_style_is_cut) {
                    isect_point = intersect.INNERSECT(neg_z, lcut_2, end_point_2, end_point_0);
                    if (lcut_2[2] > 0.0) {
                        lcut_2 = matrix.VEC_SCALE(-1.0, lcut_2);
                    }
                    dot = lcut_2[0] * end_point_2[0];
                    dot += lcut_2[1] * end_point_2[1];
                    back_loop[j] = matrix.VEC_COPY(isect_point);
                }
                else {
                    dot = 1.0;
                    back_loop[j] = matrix.VEC_COPY(end_point_2);
                }
                isect_point = intersect.INNERSECT(neg_z, bisector_2, torsion_point_0, end_point_2);
                if (dot <= 0.0 || isect_point[2] > back_loop[j][2]) {
                    back_cap[j] = matrix.VEC_COPY(back_loop[j]);
                    back_loop[j] = matrix.VEC_COPY(isect_point);
                    back_is_trimmed[j] = true;
                }
                else {
                    back_is_trimmed[j] = false;
                }
                if (back_loop[j][2] > 0.0) {
                    back_loop[j] = matrix.VEC_COPY(end_point_0);
                }
            }
            if (xform_array == null) {
                if (color_array == null) {
                    if (cont_normal == null) {
                        draw_segment_plain(ncp, front_loop, back_loop, inext, seg_len);
                    }
                    else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                        draw_segment_facet_n(ncp, front_loop, back_loop, norm_loop, inext, seg_len);
                    }
                    else {
                        draw_segment_edge_n(ncp, front_loop, back_loop, norm_loop, inext, seg_len);
                    }
                }
                else if (cont_normal == null) {
                    draw_segment_color(ncp, front_loop, back_loop, color_array[inext - 1], color_array[inext], inext, seg_len);
                }
                else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                    draw_segment_c_and_facet_n(ncp, front_loop, back_loop, norm_loop, color_array[inext - 1], color_array[inext], inext, seg_len);
                }
                else {
                    draw_segment_c_and_edge_n(ncp, front_loop, back_loop, norm_loop, color_array[inext - 1], color_array[inext], inext, seg_len);
                }
            }
            else if (color_array == null) {
                if (cont_normal == null) {
                    draw_segment_plain(ncp, front_loop, back_loop, inext, seg_len);
                }
                else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                    draw_binorm_segment_facet_n(ncp, front_loop, back_loop, front_norm, back_norm, inext, seg_len);
                }
                else {
                    draw_binorm_segment_edge_n(ncp, front_loop, back_loop, front_norm, back_norm, inext, seg_len);
                }
            }
            else if (cont_normal == null) {
                draw_segment_color(ncp, front_loop, back_loop, color_array[inext - 1], color_array[inext], inext, seg_len);
            }
            else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                draw_binorm_segment_c_and_facet_n(ncp, front_loop, back_loop, front_norm, back_norm, color_array[inext - 1], color_array[inext], inext, seg_len);
            }
            else {
                draw_binorm_segment_c_and_edge_n(ncp, front_loop, back_loop, front_norm, back_norm, color_array[inext - 1], color_array[inext], inext, seg_len);
            }
            if (first_time) {
                first_time = false;
                tmp_cap_callback = cap_callback;
                cap_callback = new String("null");
                if ((gleGetJoinStyle() & 0x10) == 0x1) {
                    if (color_array != null) {
                        GL11.glColor4f(color_array[inext - 1][0], color_array[inext - 1][1], color_array[inext - 1][2], color_array[inext - 1][3]);
                    }
                    draw_angle_style_front_cap(ncp, bisector_0, front_loop);
                }
            }
            if (color_array != null) {
                front_color = color_array[inext - 1];
                back_color = color_array[inext];
            }
            else {
                front_color = null;
                back_color = null;
            }
            if (cont_normal == null) {
                if (valid_cut_0) {
                    cut_vec = lcut_0;
                }
                else {
                    cut_vec = null;
                }
                draw_fillets_and_join_plain(ncp, front_loop, front_cap, front_is_trimmed, origin, bisector_0, front_color, back_color, cut_vec, true, cap_callback);
                if (inext == npoints - 2) {
                    if ((gleGetJoinStyle() & 0x10) == 0x1) {
                        if (color_array != null) {
                            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
                        }
                        draw_angle_style_back_cap(ncp, bisector_2, back_loop);
                        cap_callback = new String("null");
                    }
                }
                else {
                    cap_callback = tmp_cap_callback;
                }
                if (valid_cut_2) {
                    cut_vec = lcut_2;
                }
                else {
                    cut_vec = null;
                }
                draw_fillets_and_join_plain(ncp, back_loop, back_cap, back_is_trimmed, neg_z, bisector_2, back_color, front_color, cut_vec, false, cap_callback);
            }
            else {
                if (valid_cut_0) {
                    cut_vec = lcut_0;
                }
                else {
                    cut_vec = null;
                }
                draw_fillets_and_join_n_norms(ncp, front_loop, front_cap, front_is_trimmed, origin, bisector_0, front_norm, front_color, back_color, cut_vec, true, cap_callback);
                if (inext == npoints - 2) {
                    if ((gleGetJoinStyle() & 0x10) == 0x1) {
                        if (color_array != null) {
                            GL11.glColor4f(color_array[inext][0], color_array[inext][1], color_array[inext][2], color_array[inext][3]);
                        }
                        draw_angle_style_back_cap(ncp, bisector_2, back_loop);
                        cap_callback = new String("null");
                    }
                }
                else {
                    cap_callback = tmp_cap_callback;
                }
                if (valid_cut_2) {
                    cut_vec = lcut_2;
                }
                else {
                    cut_vec = null;
                }
                draw_fillets_and_join_n_norms(ncp, back_loop, back_cap, back_is_trimmed, neg_z, bisector_2, back_norm, back_color, front_color, cut_vec, false, cap_callback);
            }
            GL11.glPopMatrix();
            tmp = front_norm;
            front_norm = back_norm;
            back_norm = tmp;
            tube_len = seg_len;
            i = inext;
        }
    }
    
    private void draw_fillets_and_join_plain(int ncp, double[][] trimmed_loop, double[][] untrimmed_loop, boolean[] is_trimmed, double[] bis_origin, double[] bis_vector, float[] front_color, float[] back_color, double[] cut_vector, boolean face, String cap_callback) {
        int istop = 0;
        int icnt = 0;
        int icnt_prev = 0;
        int iloop = 0;
        double[][] cap_loop = null;
        double[] sect = new double[3];
        double[] tmp_vec = new double[3];
        int save_style = 0;
        boolean was_trimmed = false;
        cap_loop = new double[ncp + 3][3];
        icnt = 0;
        iloop = 0;
        if (!is_trimmed[0]) {
            if ((gleGetJoinStyle() & 0x3) == 0x3 && (save_style & 0x1000) != 0x1000) {
                tmp_vec = matrix.VEC_SUM(trimmed_loop[0], bis_vector);
                sect = intersect.INNERSECT(bis_origin, bis_vector, trimmed_loop[0], tmp_vec);
                cap_loop[iloop] = matrix.VEC_COPY(sect);
                ++iloop;
            }
            cap_loop[iloop] = matrix.VEC_COPY(trimmed_loop[0]);
            ++iloop;
            icnt_prev = icnt;
            ++icnt;
        }
        else {
            was_trimmed = true;
            while (is_trimmed[icnt]) {
                icnt_prev = icnt;
                if (++icnt >= ncp) {
                    return;
                }
            }
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            istop = ncp;
        }
        else {
            istop = ncp - 1;
        }
        save_style = gleGetJoinStyle();
        gleSetJoinStyle(save_style & 0xFFFFEFFF);
        while (icnt_prev < istop) {
            if (!is_trimmed[icnt_prev] || is_trimmed[icnt]) {}
            if (is_trimmed[icnt_prev] && !is_trimmed[icnt]) {
                sect = intersect.INNERSECT(bis_origin, bis_vector, untrimmed_loop[icnt_prev], trimmed_loop[icnt]);
                draw_fillet_triangle_plain(trimmed_loop[icnt_prev], trimmed_loop[icnt], sect, face, front_color, back_color);
                cap_loop[iloop] = matrix.VEC_COPY(sect);
                ++iloop;
                cap_loop[iloop] = matrix.VEC_COPY(trimmed_loop[icnt]);
                ++iloop;
            }
            if (!is_trimmed[icnt_prev] && !is_trimmed[icnt]) {
                cap_loop[iloop] = matrix.VEC_COPY(trimmed_loop[icnt]);
                ++iloop;
            }
            if (!is_trimmed[icnt_prev] && is_trimmed[icnt]) {
                was_trimmed = true;
                sect = intersect.INNERSECT(bis_origin, bis_vector, trimmed_loop[icnt_prev], untrimmed_loop[icnt]);
                draw_fillet_triangle_plain(trimmed_loop[icnt_prev], trimmed_loop[icnt], sect, face, front_color, back_color);
                cap_loop[iloop] = matrix.VEC_COPY(sect);
                if (++iloop >= 3) {
                    if (cap_callback.equals("cut")) {
                        draw_cut_style_cap_callback(iloop, cap_loop, front_color, cut_vector, bis_vector, null, face);
                    }
                    else if (cap_callback.equals("round")) {
                        draw_round_style_cap_callback(iloop, cap_loop, front_color, cut_vector, bis_vector, null, face);
                    }
                }
                iloop = 0;
            }
            ++icnt_prev;
            icnt = ++icnt % ncp;
        }
        icnt = --icnt + ncp;
        icnt %= ncp;
        if (!is_trimmed[icnt] && iloop >= 2) {
            tmp_vec = matrix.VEC_SUM(trimmed_loop[icnt], bis_vector);
            sect = intersect.INNERSECT(bis_origin, bis_vector, trimmed_loop[icnt], tmp_vec);
            cap_loop[iloop] = matrix.VEC_COPY(sect);
            ++iloop;
            if (!was_trimmed) {
                gleSetJoinStyle(save_style);
            }
            if (cap_callback.equals("cut")) {
                draw_cut_style_cap_callback(iloop, cap_loop, front_color, cut_vector, bis_vector, null, face);
            }
            else if (cap_callback.equals("round")) {
                draw_round_style_cap_callback(iloop, cap_loop, front_color, cut_vector, bis_vector, null, face);
            }
        }
        gleSetJoinStyle(save_style);
    }
    
    private void draw_fillets_and_join_n_norms(int ncp, double[][] trimmed_loop, double[][] untrimmed_loop, boolean[] is_trimmed, double[] bis_origin, double[] bis_vector, double[][] normals, float[] front_color, float[] back_color, double[] cut_vector, boolean face, String cap_callback) {
        int istop = 0;
        int icnt = 0;
        int icnt_prev = 0;
        int iloop = 0;
        double[][] cap_loop = null;
        double[][] norm_loop = null;
        double[] sect = new double[3];
        double[] tmp_vec = new double[3];
        int save_style = 0;
        boolean was_trimmed = false;
        cap_loop = new double[ncp + 3][3];
        norm_loop = new double[ncp + 3][3];
        icnt = 0;
        iloop = 0;
        if (!is_trimmed[0]) {
            if ((gleGetJoinStyle() & 0x3) == 0x3 && (save_style & 0x1000) != 0x1000) {
                tmp_vec = matrix.VEC_SUM(trimmed_loop[0], bis_vector);
                sect = intersect.INNERSECT(bis_origin, bis_vector, trimmed_loop[0], tmp_vec);
                cap_loop[iloop] = matrix.VEC_COPY(sect);
                norm_loop[iloop] = matrix.VEC_COPY(normals[0]);
                ++iloop;
            }
            cap_loop[iloop] = matrix.VEC_COPY(trimmed_loop[0]);
            norm_loop[iloop] = matrix.VEC_COPY(normals[0]);
            ++iloop;
            icnt_prev = icnt;
            ++icnt;
        }
        else {
            was_trimmed = true;
            while (is_trimmed[icnt]) {
                icnt_prev = icnt;
                if (++icnt >= ncp) {
                    return;
                }
            }
        }
        if ((gleGetJoinStyle() & 0x1000) == 0x1000) {
            istop = ncp;
        }
        else {
            istop = ncp - 1;
        }
        save_style = gleGetJoinStyle();
        gleSetJoinStyle(save_style & 0xFFFFEFFF);
        while (icnt_prev < istop) {
            if (!is_trimmed[icnt_prev] || is_trimmed[icnt]) {}
            if (is_trimmed[icnt_prev] && !is_trimmed[icnt]) {
                sect = intersect.INNERSECT(bis_origin, bis_vector, untrimmed_loop[icnt_prev], trimmed_loop[icnt]);
                draw_fillet_triangle_n_norms(trimmed_loop[icnt_prev], trimmed_loop[icnt], sect, face, front_color, back_color, normals[icnt_prev], normals[icnt]);
                cap_loop[iloop] = matrix.VEC_COPY(sect);
                norm_loop[iloop] = matrix.VEC_COPY(normals[icnt_prev]);
                ++iloop;
                cap_loop[iloop] = matrix.VEC_COPY(trimmed_loop[icnt]);
                norm_loop[iloop] = matrix.VEC_COPY(normals[icnt]);
                ++iloop;
            }
            if (!is_trimmed[icnt_prev] && !is_trimmed[icnt]) {
                cap_loop[iloop] = matrix.VEC_COPY(trimmed_loop[icnt]);
                norm_loop[iloop] = matrix.VEC_COPY(normals[icnt]);
                ++iloop;
            }
            if (!is_trimmed[icnt_prev] && is_trimmed[icnt]) {
                was_trimmed = true;
                sect = intersect.INNERSECT(bis_origin, bis_vector, trimmed_loop[icnt_prev], untrimmed_loop[icnt]);
                draw_fillet_triangle_n_norms(trimmed_loop[icnt_prev], trimmed_loop[icnt], sect, face, front_color, back_color, normals[icnt_prev], normals[icnt]);
                cap_loop[iloop] = matrix.VEC_COPY(sect);
                if ((gleGetJoinStyle() & 0x100) == 0x100) {
                    norm_loop[iloop] = matrix.VEC_COPY(normals[icnt_prev]);
                }
                else {
                    norm_loop[iloop] = matrix.VEC_COPY(normals[icnt]);
                }
                if (++iloop >= 3) {
                    if (cap_callback.equals("cut")) {
                        draw_cut_style_cap_callback(iloop, cap_loop, front_color, cut_vector, bis_vector, norm_loop, face);
                    }
                    else if (cap_callback.equals("round")) {
                        draw_round_style_cap_callback(iloop, cap_loop, front_color, cut_vector, bis_vector, norm_loop, face);
                    }
                }
                iloop = 0;
            }
            ++icnt_prev;
            icnt = ++icnt % ncp;
        }
        icnt = --icnt + ncp;
        icnt %= ncp;
        if (!is_trimmed[icnt] && iloop >= 2) {
            if ((gleGetJoinStyle() & 0x3) == 0x3 && (save_style & 0x1000) != 0x1000) {
                tmp_vec = matrix.VEC_SUM(trimmed_loop[icnt], bis_vector);
                sect = intersect.INNERSECT(bis_origin, bis_vector, trimmed_loop[icnt], tmp_vec);
                cap_loop[iloop] = matrix.VEC_COPY(sect);
                norm_loop[iloop] = matrix.VEC_COPY(normals[icnt]);
                ++iloop;
            }
            if (!was_trimmed) {
                gleSetJoinStyle(save_style);
            }
            if (cap_callback.equals("cut")) {
                draw_cut_style_cap_callback(iloop, cap_loop, front_color, cut_vector, bis_vector, norm_loop, face);
            }
            else if (cap_callback.equals("round")) {
                draw_round_style_cap_callback(iloop, cap_loop, front_color, cut_vector, bis_vector, norm_loop, face);
            }
        }
        gleSetJoinStyle(save_style);
    }
    
    private static void draw_fillet_triangle_plain(double[] va, double[] vb, double[] vc, boolean face, float[] front_color, float[] back_color) {
        if (front_color != null) {
            GL11.glColor4f(front_color[0], front_color[1], front_color[2], front_color[3]);
        }
        GL11.glBegin(5);
        if (face) {
            GL11.glVertex3d(va[0], va[1], va[2]);
            GL11.glVertex3d(vb[0], vb[1], vb[2]);
        }
        else {
            GL11.glVertex3d(vb[0], vb[1], vb[2]);
            GL11.glVertex3d(va[0], va[1], va[2]);
        }
        GL11.glVertex3d(vc[0], vc[1], vc[2]);
        GL11.glEnd();
    }
    
    private void draw_fillet_triangle_n_norms(double[] va, double[] vb, double[] vc, boolean face, float[] front_color, float[] back_color, double[] na, double[] nb) {
        if (front_color != null) {
            GL11.glColor4f(front_color[0], front_color[1], front_color[2], front_color[3]);
        }
        GL11.glBegin(5);
        if ((gleGetJoinStyle() & 0x100) == 0x100) {
            GL11.glNormal3d(na[0], na[1], na[2]);
            if (face) {
                GL11.glVertex3d(va[0], va[1], va[2]);
                GL11.glVertex3d(vb[0], vb[1], vb[2]);
            }
            else {
                GL11.glVertex3d(vb[0], vb[1], vb[2]);
                GL11.glVertex3d(va[0], va[1], va[2]);
            }
            GL11.glNormal3d(vc[0], vc[1], vc[2]);
        }
        else {
            if (face) {
                GL11.glNormal3d(na[0], na[1], na[2]);
                GL11.glVertex3d(va[0], va[1], va[2]);
                GL11.glNormal3d(nb[0], nb[1], nb[2]);
                GL11.glVertex3d(vb[0], vb[1], vb[2]);
            }
            else {
                GL11.glNormal3d(nb[0], nb[1], nb[2]);
                GL11.glVertex3d(vb[0], vb[1], vb[2]);
                GL11.glNormal3d(na[0], na[1], na[2]);
                GL11.glVertex3d(va[0], va[1], va[2]);
                GL11.glNormal3d(nb[0], nb[1], nb[2]);
            }
            GL11.glVertex3d(vc[0], vc[1], vc[2]);
        }
        GL11.glEnd();
    }
    
    private void draw_cut_style_cap_callback(int iloop, double[][] cap, float[] face_color, double[] cut_vector, double[] bisect_vector, double[][] norms, boolean frontwards) {
        double[] previous_vertex = null;
        double[] first_vertex = null;
        boolean is_colinear = false;
        GLUtessellator tobj = GLU.gluNewTess();
        tobj.gluTessProperty(100140, 100130.0);
        tobj.gluTessCallback(100101, tessCallback);
        tobj.gluTessCallback(100100, tessCallback);
        tobj.gluTessCallback(100102, tessCallback);
        tobj.gluTessCallback(100103, tessCallback);
        if (face_color != null) {
            GL11.glColor4f(face_color[0], face_color[1], face_color[2], face_color[3]);
        }
        if (frontwards) {
            if (cut_vector != null) {
                if (cut_vector[2] < 0.0) {
                    cut_vector = matrix.VEC_SCALE(-1.0, cut_vector);
                }
                GL11.glNormal3d(cut_vector[0], cut_vector[1], cut_vector[2]);
            }
            tobj.gluTessBeginPolygon(null);
            tobj.gluTessBeginContour();
            first_vertex = null;
            previous_vertex = cap[iloop - 1];
            for (int i = 0; i < iloop - 1; ++i) {
                is_colinear = intersect.COLINEAR(previous_vertex, cap[i], cap[i + 1]);
                if (!is_colinear) {
                    tobj.gluTessVertex(cap[i], 0, cap[i]);
                    previous_vertex = cap[i];
                    if (first_vertex == null) {
                        first_vertex = previous_vertex;
                    }
                }
            }
            if (first_vertex == null) {
                first_vertex = cap[0];
            }
            is_colinear = intersect.COLINEAR(previous_vertex, cap[iloop - 1], first_vertex);
            if (!is_colinear) {
                tobj.gluTessVertex(cap[iloop - 1], 0, cap[iloop - 1]);
            }
            tobj.gluTessEndContour();
            tobj.gluTessEndPolygon();
        }
        else {
            if (cut_vector != null) {
                if (cut_vector[2] > 0.0) {
                    cut_vector = matrix.VEC_SCALE(-1.0, cut_vector);
                }
                GL11.glNormal3d(cut_vector[0], cut_vector[1], cut_vector[2]);
            }
            tobj.gluTessBeginPolygon(null);
            tobj.gluTessBeginContour();
            first_vertex = null;
            previous_vertex = cap[0];
            for (int i = iloop - 1; i > 0; --i) {
                is_colinear = intersect.COLINEAR(previous_vertex, cap[i], cap[i - 1]);
                if (!is_colinear) {
                    tobj.gluTessVertex(cap[i], 0, cap[i]);
                    previous_vertex = cap[i];
                    if (first_vertex == null) {
                        first_vertex = previous_vertex;
                    }
                }
            }
            if (first_vertex == null) {
                first_vertex = cap[iloop - 1];
            }
            is_colinear = intersect.COLINEAR(previous_vertex, cap[0], first_vertex);
            if (!is_colinear) {
                tobj.gluTessVertex(cap[0], 0, cap[0]);
            }
            tobj.gluTessEndContour();
            tobj.gluTessEndPolygon();
        }
        tobj.gluDeleteTess();
    }
    
    private void draw_round_style_cap_callback(int ncp, double[][] cap, float[] face_color, double[] cut, double[] bi, double[][] norms, boolean frontwards) {
        double[] axis = new double[3];
        double[] xycut = new double[3];
        double theta = 0.0;
        double[][] last_contour = null;
        double[][] next_contour = null;
        double[][] last_norm = null;
        double[][] next_norm = null;
        double[] cap_z = null;
        double[][] tmp = null;
        int i = 0;
        int j = 0;
        int k = 0;
        double[][] m = new double[4][4];
        if (face_color != null) {
            GL11.glColor4f(face_color[0], face_color[1], face_color[2], face_color[3]);
        }
        if (cut == null) {
            return;
        }
        if (cut[2] > 0.0) {
            cut = matrix.VEC_SCALE(-1.0, cut);
        }
        if (bi[2] < 0.0) {
            bi = matrix.VEC_SCALE(-1.0, bi);
        }
        axis = matrix.VEC_CROSS_PRODUCT(cut, bi);
        if (!frontwards) {
            cut = matrix.VEC_SCALE(-1.0, cut);
        }
        xycut[1] = (xycut[0] = 0.0);
        xycut[2] = 1.0;
        xycut = matrix.VEC_PERP(cut, xycut);
        xycut = matrix.VEC_NORMALIZE(xycut);
        theta = matrix.VEC_DOT_PRODUCT(xycut, cut);
        theta = Math.acos(theta);
        theta /= __ROUND_TESS_PIECES;
        m = matrix.urot_axis_d(theta, axis);
        last_contour = new double[ncp][3];
        next_contour = new double[ncp][3];
        cap_z = new double[ncp];
        last_norm = new double[ncp][3];
        next_norm = new double[ncp][3];
        if (frontwards) {
            for (j = 0; j < ncp; ++j) {
                last_contour[j][0] = cap[j][0];
                last_contour[j][1] = cap[j][1];
                last_contour[j][2] = (cap_z[j] = cap[j][2]);
            }
            if (norms != null) {
                for (j = 0; j < ncp; ++j) {
                    last_norm[j] = matrix.VEC_COPY(norms[j]);
                }
            }
        }
        else {
            for (j = 0; j < ncp; ++j) {
                k = ncp - j - 1;
                last_contour[k][0] = cap[j][0];
                last_contour[k][1] = cap[j][1];
                last_contour[k][2] = (cap_z[k] = cap[j][2]);
            }
            if (norms != null) {
                if ((gleGetJoinStyle() & 0x100) == 0x100) {
                    for (j = 0; j < ncp - 1; ++j) {
                        k = ncp - j - 2;
                        last_norm[k] = matrix.VEC_COPY(norms[j]);
                    }
                }
                else {
                    for (j = 0; j < ncp; ++j) {
                        k = ncp - j - 1;
                        last_norm[k] = matrix.VEC_COPY(norms[j]);
                    }
                }
            }
        }
        for (i = 0; i < __ROUND_TESS_PIECES; ++i) {
            for (j = 0; j < ncp; ++j) {
                double[] array = next_contour[j];
                int n = 2;
                array[n] -= cap_z[j];
                double[] array2 = last_contour[j];
                int n2 = 2;
                array2[n2] -= cap_z[j];
                next_contour[j] = matrix.MAT_DOT_VEC_3X3(m, last_contour[j]);
                double[] array3 = next_contour[j];
                int n3 = 2;
                array3[n3] += cap_z[j];
                double[] array4 = last_contour[j];
                int n4 = 2;
                array4[n4] += cap_z[j];
            }
            if (norms != null) {
                for (j = 0; j < ncp; ++j) {
                    next_norm[j] = matrix.MAT_DOT_VEC_3X3(m, last_norm[j]);
                }
            }
            if (norms == null) {
                draw_segment_plain(ncp, next_contour, last_contour, 0, 0.0);
            }
            else if ((gleGetJoinStyle() & 0x100) == 0x100) {
                draw_binorm_segment_facet_n(ncp, next_contour, last_contour, next_norm, last_norm, 0, 0.0);
            }
            else {
                draw_binorm_segment_edge_n(ncp, next_contour, last_contour, next_norm, last_norm, 0, 0.0);
            }
            tmp = next_contour;
            next_contour = last_contour;
            last_contour = tmp;
            tmp = next_norm;
            next_norm = last_norm;
            last_norm = tmp;
        }
    }
    
    static {
        VERSION = new String("$Id: CoreGLE.java,v 1.5 1998/05/20 00:19:43 descarte Exp descarte $");
        GLE_VERSION = new String("095");
        CoreGLE.glu_ = null;
    }
    
    class tessellCallBack implements GLUtessellatorCallback
    {
        public tessellCallBack(GLU glu) {
        }
        
        public void begin(int type) {
            GL11.glBegin(type);
        }
        
        public void end() {
            GL11.glEnd();
        }
        
        public void vertex(Object vertexData) {
            if (vertexData instanceof double[]) {
                double[] pointer = (double[])vertexData;
                if (pointer.length == 6) {
                    GL11.glColor3d(pointer[3], pointer[4], pointer[5]);
                }
                GL11.glVertex3d(pointer[0], pointer[1], pointer[2]);
            }
        }
        
        public void vertexData(Object vertexData, Object polygonData) {
        }
        
        public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
            double[] vertex = { coords[0], coords[1], coords[2], 0.0, 0.0, 0.0 };
            for (int i = 3; i < 6; ++i) {
                vertex[i] = weight[0] * ((double[])data[0])[i] + weight[1] * ((double[])data[1])[i] + weight[2] * ((double[])data[2])[i] + weight[3] * ((double[])data[3])[i];
            }
            outData[0] = vertex;
        }
        
        public void combineData(double[] coords, Object[] data, float[] weight, Object[] outData, Object polygonData) {
        }
        
        public void error(int errnum) {
            String estring = GLU.gluErrorString(errnum);
            System.err.println("Tessellation Error: " + estring);
        }
        
        public void beginData(int type, Object polygonData) {
        }
        
        public void endData(Object polygonData) {
        }
        
        public void edgeFlag(boolean boundaryEdge) {
        }
        
        public void edgeFlagData(boolean boundaryEdge, Object polygonData) {
        }
        
        public void errorData(int errnum, Object polygonData) {
        }
    }
}
