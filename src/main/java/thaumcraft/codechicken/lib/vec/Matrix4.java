// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.DoubleBuffer;
import thaumcraft.codechicken.lib.util.Copyable;

public class Matrix4 extends Transformation implements Copyable<Matrix4>
{
    private static DoubleBuffer glBuf;
    public double m00;
    public double m01;
    public double m02;
    public double m03;
    public double m10;
    public double m11;
    public double m12;
    public double m13;
    public double m20;
    public double m21;
    public double m22;
    public double m23;
    public double m30;
    public double m31;
    public double m32;
    public double m33;
    
    public Matrix4() {
        final double n = 1.0;
        this.m33 = n;
        this.m22 = n;
        this.m11 = n;
        this.m00 = n;
    }
    
    public Matrix4(final double d00, final double d01, final double d02, final double d03, final double d10, final double d11, final double d12, final double d13, final double d20, final double d21, final double d22, final double d23, final double d30, final double d31, final double d32, final double d33) {
        this.m00 = d00;
        this.m01 = d01;
        this.m02 = d02;
        this.m03 = d03;
        this.m10 = d10;
        this.m11 = d11;
        this.m12 = d12;
        this.m13 = d13;
        this.m20 = d20;
        this.m21 = d21;
        this.m22 = d22;
        this.m23 = d23;
        this.m30 = d30;
        this.m31 = d31;
        this.m32 = d32;
        this.m33 = d33;
    }
    
    public Matrix4(final Matrix4 mat) {
        this.set(mat);
    }
    
    public Matrix4 setIdentity() {
        final double n = 1.0;
        this.m33 = n;
        this.m22 = n;
        this.m11 = n;
        this.m00 = n;
        final double n2 = 0.0;
        this.m32 = n2;
        this.m31 = n2;
        this.m30 = n2;
        this.m23 = n2;
        this.m21 = n2;
        this.m20 = n2;
        this.m13 = n2;
        this.m12 = n2;
        this.m10 = n2;
        this.m03 = n2;
        this.m02 = n2;
        this.m01 = n2;
        return this;
    }
    
    public Matrix4 translate(final Vector3 vec) {
        this.m03 += this.m00 * vec.x + this.m01 * vec.y + this.m02 * vec.z;
        this.m13 += this.m10 * vec.x + this.m11 * vec.y + this.m12 * vec.z;
        this.m23 += this.m20 * vec.x + this.m21 * vec.y + this.m22 * vec.z;
        this.m33 += this.m30 * vec.x + this.m31 * vec.y + this.m32 * vec.z;
        return this;
    }
    
    public Matrix4 scale(final Vector3 vec) {
        this.m00 *= vec.x;
        this.m10 *= vec.x;
        this.m20 *= vec.x;
        this.m30 *= vec.x;
        this.m01 *= vec.y;
        this.m11 *= vec.y;
        this.m21 *= vec.y;
        this.m31 *= vec.y;
        this.m02 *= vec.z;
        this.m12 *= vec.z;
        this.m22 *= vec.z;
        this.m32 *= vec.z;
        return this;
    }
    
    public Matrix4 rotate(final double angle, final Vector3 axis) {
        final double c = Math.cos(angle);
        final double s = Math.sin(angle);
        final double mc = 1.0 - c;
        final double xy = axis.x * axis.y;
        final double yz = axis.y * axis.z;
        final double xz = axis.x * axis.z;
        final double xs = axis.x * s;
        final double ys = axis.y * s;
        final double zs = axis.z * s;
        final double f00 = axis.x * axis.x * mc + c;
        final double f2 = xy * mc + zs;
        final double f3 = xz * mc - ys;
        final double f4 = xy * mc - zs;
        final double f5 = axis.y * axis.y * mc + c;
        final double f6 = yz * mc + xs;
        final double f7 = xz * mc + ys;
        final double f8 = yz * mc - xs;
        final double f9 = axis.z * axis.z * mc + c;
        final double t00 = this.m00 * f00 + this.m01 * f2 + this.m02 * f3;
        final double t2 = this.m10 * f00 + this.m11 * f2 + this.m12 * f3;
        final double t3 = this.m20 * f00 + this.m21 * f2 + this.m22 * f3;
        final double t4 = this.m30 * f00 + this.m31 * f2 + this.m32 * f3;
        final double t5 = this.m00 * f4 + this.m01 * f5 + this.m02 * f6;
        final double t6 = this.m10 * f4 + this.m11 * f5 + this.m12 * f6;
        final double t7 = this.m20 * f4 + this.m21 * f5 + this.m22 * f6;
        final double t8 = this.m30 * f4 + this.m31 * f5 + this.m32 * f6;
        this.m02 = this.m00 * f7 + this.m01 * f8 + this.m02 * f9;
        this.m12 = this.m10 * f7 + this.m11 * f8 + this.m12 * f9;
        this.m22 = this.m20 * f7 + this.m21 * f8 + this.m22 * f9;
        this.m32 = this.m30 * f7 + this.m31 * f8 + this.m32 * f9;
        this.m00 = t00;
        this.m10 = t2;
        this.m20 = t3;
        this.m30 = t4;
        this.m01 = t5;
        this.m11 = t6;
        this.m21 = t7;
        this.m31 = t8;
        return this;
    }
    
    public Matrix4 rotate(final Rotation rotation) {
        rotation.apply(this);
        return this;
    }
    
    public Matrix4 leftMultiply(final Matrix4 mat) {
        final double n00 = this.m00 * mat.m00 + this.m10 * mat.m01 + this.m20 * mat.m02 + this.m30 * mat.m03;
        final double n2 = this.m01 * mat.m00 + this.m11 * mat.m01 + this.m21 * mat.m02 + this.m31 * mat.m03;
        final double n3 = this.m02 * mat.m00 + this.m12 * mat.m01 + this.m22 * mat.m02 + this.m32 * mat.m03;
        final double n4 = this.m03 * mat.m00 + this.m13 * mat.m01 + this.m23 * mat.m02 + this.m33 * mat.m03;
        final double n5 = this.m00 * mat.m10 + this.m10 * mat.m11 + this.m20 * mat.m12 + this.m30 * mat.m13;
        final double n6 = this.m01 * mat.m10 + this.m11 * mat.m11 + this.m21 * mat.m12 + this.m31 * mat.m13;
        final double n7 = this.m02 * mat.m10 + this.m12 * mat.m11 + this.m22 * mat.m12 + this.m32 * mat.m13;
        final double n8 = this.m03 * mat.m10 + this.m13 * mat.m11 + this.m23 * mat.m12 + this.m33 * mat.m13;
        final double n9 = this.m00 * mat.m20 + this.m10 * mat.m21 + this.m20 * mat.m22 + this.m30 * mat.m23;
        final double n10 = this.m01 * mat.m20 + this.m11 * mat.m21 + this.m21 * mat.m22 + this.m31 * mat.m23;
        final double n11 = this.m02 * mat.m20 + this.m12 * mat.m21 + this.m22 * mat.m22 + this.m32 * mat.m23;
        final double n12 = this.m03 * mat.m20 + this.m13 * mat.m21 + this.m23 * mat.m22 + this.m33 * mat.m23;
        final double n13 = this.m00 * mat.m30 + this.m10 * mat.m31 + this.m20 * mat.m32 + this.m30 * mat.m33;
        final double n14 = this.m01 * mat.m30 + this.m11 * mat.m31 + this.m21 * mat.m32 + this.m31 * mat.m33;
        final double n15 = this.m02 * mat.m30 + this.m12 * mat.m31 + this.m22 * mat.m32 + this.m32 * mat.m33;
        final double n16 = this.m03 * mat.m30 + this.m13 * mat.m31 + this.m23 * mat.m32 + this.m33 * mat.m33;
        this.m00 = n00;
        this.m01 = n2;
        this.m02 = n3;
        this.m03 = n4;
        this.m10 = n5;
        this.m11 = n6;
        this.m12 = n7;
        this.m13 = n8;
        this.m20 = n9;
        this.m21 = n10;
        this.m22 = n11;
        this.m23 = n12;
        this.m30 = n13;
        this.m31 = n14;
        this.m32 = n15;
        this.m33 = n16;
        return this;
    }
    
    public Matrix4 multiply(final Matrix4 mat) {
        final double n00 = this.m00 * mat.m00 + this.m01 * mat.m10 + this.m02 * mat.m20 + this.m03 * mat.m30;
        final double n2 = this.m00 * mat.m01 + this.m01 * mat.m11 + this.m02 * mat.m21 + this.m03 * mat.m31;
        final double n3 = this.m00 * mat.m02 + this.m01 * mat.m12 + this.m02 * mat.m22 + this.m03 * mat.m32;
        final double n4 = this.m00 * mat.m03 + this.m01 * mat.m13 + this.m02 * mat.m23 + this.m03 * mat.m33;
        final double n5 = this.m10 * mat.m00 + this.m11 * mat.m10 + this.m12 * mat.m20 + this.m13 * mat.m30;
        final double n6 = this.m10 * mat.m01 + this.m11 * mat.m11 + this.m12 * mat.m21 + this.m13 * mat.m31;
        final double n7 = this.m10 * mat.m02 + this.m11 * mat.m12 + this.m12 * mat.m22 + this.m13 * mat.m32;
        final double n8 = this.m10 * mat.m03 + this.m11 * mat.m13 + this.m12 * mat.m23 + this.m13 * mat.m33;
        final double n9 = this.m20 * mat.m00 + this.m21 * mat.m10 + this.m22 * mat.m20 + this.m23 * mat.m30;
        final double n10 = this.m20 * mat.m01 + this.m21 * mat.m11 + this.m22 * mat.m21 + this.m23 * mat.m31;
        final double n11 = this.m20 * mat.m02 + this.m21 * mat.m12 + this.m22 * mat.m22 + this.m23 * mat.m32;
        final double n12 = this.m20 * mat.m03 + this.m21 * mat.m13 + this.m22 * mat.m23 + this.m23 * mat.m33;
        final double n13 = this.m30 * mat.m00 + this.m31 * mat.m10 + this.m32 * mat.m20 + this.m33 * mat.m30;
        final double n14 = this.m30 * mat.m01 + this.m31 * mat.m11 + this.m32 * mat.m21 + this.m33 * mat.m31;
        final double n15 = this.m30 * mat.m02 + this.m31 * mat.m12 + this.m32 * mat.m22 + this.m33 * mat.m32;
        final double n16 = this.m30 * mat.m03 + this.m31 * mat.m13 + this.m32 * mat.m23 + this.m33 * mat.m33;
        this.m00 = n00;
        this.m01 = n2;
        this.m02 = n3;
        this.m03 = n4;
        this.m10 = n5;
        this.m11 = n6;
        this.m12 = n7;
        this.m13 = n8;
        this.m20 = n9;
        this.m21 = n10;
        this.m22 = n11;
        this.m23 = n12;
        this.m30 = n13;
        this.m31 = n14;
        this.m32 = n15;
        this.m33 = n16;
        return this;
    }
    
    public Matrix4 transpose() {
        final double n00 = this.m00;
        final double n2 = this.m01;
        final double n3 = this.m02;
        final double n4 = this.m03;
        final double n5 = this.m10;
        final double n6 = this.m11;
        final double n7 = this.m12;
        final double n8 = this.m13;
        final double n9 = this.m20;
        final double n10 = this.m21;
        final double n11 = this.m22;
        final double n12 = this.m23;
        final double n13 = this.m30;
        final double n14 = this.m31;
        final double n15 = this.m32;
        final double n16 = this.m33;
        this.m00 = n00;
        this.m01 = n5;
        this.m02 = n9;
        this.m03 = n13;
        this.m10 = n2;
        this.m11 = n6;
        this.m12 = n10;
        this.m13 = n14;
        this.m20 = n3;
        this.m21 = n7;
        this.m22 = n11;
        this.m23 = n15;
        this.m30 = n4;
        this.m31 = n8;
        this.m32 = n12;
        this.m33 = n16;
        return this;
    }
    
    @Override
    public Matrix4 copy() {
        return new Matrix4(this);
    }
    
    public Matrix4 set(final Matrix4 mat) {
        this.m00 = mat.m00;
        this.m01 = mat.m01;
        this.m02 = mat.m02;
        this.m03 = mat.m03;
        this.m10 = mat.m10;
        this.m11 = mat.m11;
        this.m12 = mat.m12;
        this.m13 = mat.m13;
        this.m20 = mat.m20;
        this.m21 = mat.m21;
        this.m22 = mat.m22;
        this.m23 = mat.m23;
        this.m30 = mat.m30;
        this.m31 = mat.m31;
        this.m32 = mat.m32;
        this.m33 = mat.m33;
        return this;
    }
    
    @Override
    public void apply(final Matrix4 mat) {
        mat.multiply(this);
    }
    
    private void mult3x3(final Vector3 vec) {
        final double x = this.m00 * vec.x + this.m01 * vec.y + this.m02 * vec.z;
        final double y = this.m10 * vec.x + this.m11 * vec.y + this.m12 * vec.z;
        final double z = this.m20 * vec.x + this.m21 * vec.y + this.m22 * vec.z;
        vec.x = x;
        vec.y = y;
        vec.z = z;
    }
    
    @Override
    public void apply(final Vector3 vec) {
        this.mult3x3(vec);
        vec.add(this.m03, this.m13, this.m23);
    }
    
    @Override
    public void applyN(final Vector3 vec) {
        this.mult3x3(vec);
        vec.normalize();
    }
    
    @Override
    public String toString() {
        final MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "[" + new BigDecimal(this.m00, cont) + "," + new BigDecimal(this.m01, cont) + "," + new BigDecimal(this.m02, cont) + "," + new BigDecimal(this.m03, cont) + "]\n[" + new BigDecimal(this.m10, cont) + "," + new BigDecimal(this.m11, cont) + "," + new BigDecimal(this.m12, cont) + "," + new BigDecimal(this.m13, cont) + "]\n[" + new BigDecimal(this.m20, cont) + "," + new BigDecimal(this.m21, cont) + "," + new BigDecimal(this.m22, cont) + "," + new BigDecimal(this.m23, cont) + "]\n[" + new BigDecimal(this.m30, cont) + "," + new BigDecimal(this.m31, cont) + "," + new BigDecimal(this.m32, cont) + "," + new BigDecimal(this.m33, cont) + "]";
    }
    
    public Matrix4 apply(final Transformation t) {
        t.apply(this);
        return this;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void glApply() {
        Matrix4.glBuf.put(this.m00).put(this.m10).put(this.m20).put(this.m30).put(this.m01).put(this.m11).put(this.m21).put(this.m31).put(this.m02).put(this.m12).put(this.m22).put(this.m32).put(this.m03).put(this.m13).put(this.m23).put(this.m33);
        Matrix4.glBuf.flip();
        GL11.glMultMatrix(Matrix4.glBuf);
    }
    
    @Override
    public Transformation inverse() {
        throw new IrreversibleTransformationException(this);
    }
    
    static {
        Matrix4.glBuf = ByteBuffer.allocateDirect(128).order(ByteOrder.nativeOrder()).asDoubleBuffer();
    }
}
