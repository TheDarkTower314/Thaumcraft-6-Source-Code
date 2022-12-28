package thaumcraft.codechicken.lib.vec;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
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
        double n = 1.0;
        m33 = n;
        m22 = n;
        m11 = n;
        m00 = n;
    }
    
    public Matrix4(double d00, double d01, double d02, double d03, double d10, double d11, double d12, double d13, double d20, double d21, double d22, double d23, double d30, double d31, double d32, double d33) {
        m00 = d00;
        m01 = d01;
        m02 = d02;
        m03 = d03;
        m10 = d10;
        m11 = d11;
        m12 = d12;
        m13 = d13;
        m20 = d20;
        m21 = d21;
        m22 = d22;
        m23 = d23;
        m30 = d30;
        m31 = d31;
        m32 = d32;
        m33 = d33;
    }
    
    public Matrix4(Matrix4 mat) {
        set(mat);
    }
    
    public Matrix4 setIdentity() {
        double n = 1.0;
        m33 = n;
        m22 = n;
        m11 = n;
        m00 = n;
        double n2 = 0.0;
        m32 = n2;
        m31 = n2;
        m30 = n2;
        m23 = n2;
        m21 = n2;
        m20 = n2;
        m13 = n2;
        m12 = n2;
        m10 = n2;
        m03 = n2;
        m02 = n2;
        m01 = n2;
        return this;
    }
    
    public Matrix4 translate(Vector3 vec) {
        m03 += m00 * vec.x + m01 * vec.y + m02 * vec.z;
        m13 += m10 * vec.x + m11 * vec.y + m12 * vec.z;
        m23 += m20 * vec.x + m21 * vec.y + m22 * vec.z;
        m33 += m30 * vec.x + m31 * vec.y + m32 * vec.z;
        return this;
    }
    
    public Matrix4 scale(Vector3 vec) {
        m00 *= vec.x;
        m10 *= vec.x;
        m20 *= vec.x;
        m30 *= vec.x;
        m01 *= vec.y;
        m11 *= vec.y;
        m21 *= vec.y;
        m31 *= vec.y;
        m02 *= vec.z;
        m12 *= vec.z;
        m22 *= vec.z;
        m32 *= vec.z;
        return this;
    }
    
    public Matrix4 rotate(double angle, Vector3 axis) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double mc = 1.0 - c;
        double xy = axis.x * axis.y;
        double yz = axis.y * axis.z;
        double xz = axis.x * axis.z;
        double xs = axis.x * s;
        double ys = axis.y * s;
        double zs = axis.z * s;
        double f00 = axis.x * axis.x * mc + c;
        double f2 = xy * mc + zs;
        double f3 = xz * mc - ys;
        double f4 = xy * mc - zs;
        double f5 = axis.y * axis.y * mc + c;
        double f6 = yz * mc + xs;
        double f7 = xz * mc + ys;
        double f8 = yz * mc - xs;
        double f9 = axis.z * axis.z * mc + c;
        double t00 = m00 * f00 + m01 * f2 + m02 * f3;
        double t2 = m10 * f00 + m11 * f2 + m12 * f3;
        double t3 = m20 * f00 + m21 * f2 + m22 * f3;
        double t4 = m30 * f00 + m31 * f2 + m32 * f3;
        double t5 = m00 * f4 + m01 * f5 + m02 * f6;
        double t6 = m10 * f4 + m11 * f5 + m12 * f6;
        double t7 = m20 * f4 + m21 * f5 + m22 * f6;
        double t8 = m30 * f4 + m31 * f5 + m32 * f6;
        m02 = m00 * f7 + m01 * f8 + m02 * f9;
        m12 = m10 * f7 + m11 * f8 + m12 * f9;
        m22 = m20 * f7 + m21 * f8 + m22 * f9;
        m32 = m30 * f7 + m31 * f8 + m32 * f9;
        m00 = t00;
        m10 = t2;
        m20 = t3;
        m30 = t4;
        m01 = t5;
        m11 = t6;
        m21 = t7;
        m31 = t8;
        return this;
    }
    
    public Matrix4 rotate(Rotation rotation) {
        rotation.apply(this);
        return this;
    }
    
    public Matrix4 leftMultiply(Matrix4 mat) {
        double n00 = m00 * mat.m00 + m10 * mat.m01 + m20 * mat.m02 + m30 * mat.m03;
        double n2 = m01 * mat.m00 + m11 * mat.m01 + m21 * mat.m02 + m31 * mat.m03;
        double n3 = m02 * mat.m00 + m12 * mat.m01 + m22 * mat.m02 + m32 * mat.m03;
        double n4 = m03 * mat.m00 + m13 * mat.m01 + m23 * mat.m02 + m33 * mat.m03;
        double n5 = m00 * mat.m10 + m10 * mat.m11 + m20 * mat.m12 + m30 * mat.m13;
        double n6 = m01 * mat.m10 + m11 * mat.m11 + m21 * mat.m12 + m31 * mat.m13;
        double n7 = m02 * mat.m10 + m12 * mat.m11 + m22 * mat.m12 + m32 * mat.m13;
        double n8 = m03 * mat.m10 + m13 * mat.m11 + m23 * mat.m12 + m33 * mat.m13;
        double n9 = m00 * mat.m20 + m10 * mat.m21 + m20 * mat.m22 + m30 * mat.m23;
        double n10 = m01 * mat.m20 + m11 * mat.m21 + m21 * mat.m22 + m31 * mat.m23;
        double n11 = m02 * mat.m20 + m12 * mat.m21 + m22 * mat.m22 + m32 * mat.m23;
        double n12 = m03 * mat.m20 + m13 * mat.m21 + m23 * mat.m22 + m33 * mat.m23;
        double n13 = m00 * mat.m30 + m10 * mat.m31 + m20 * mat.m32 + m30 * mat.m33;
        double n14 = m01 * mat.m30 + m11 * mat.m31 + m21 * mat.m32 + m31 * mat.m33;
        double n15 = m02 * mat.m30 + m12 * mat.m31 + m22 * mat.m32 + m32 * mat.m33;
        double n16 = m03 * mat.m30 + m13 * mat.m31 + m23 * mat.m32 + m33 * mat.m33;
        m00 = n00;
        m01 = n2;
        m02 = n3;
        m03 = n4;
        m10 = n5;
        m11 = n6;
        m12 = n7;
        m13 = n8;
        m20 = n9;
        m21 = n10;
        m22 = n11;
        m23 = n12;
        m30 = n13;
        m31 = n14;
        m32 = n15;
        m33 = n16;
        return this;
    }
    
    public Matrix4 multiply(Matrix4 mat) {
        double n00 = m00 * mat.m00 + m01 * mat.m10 + m02 * mat.m20 + m03 * mat.m30;
        double n2 = m00 * mat.m01 + m01 * mat.m11 + m02 * mat.m21 + m03 * mat.m31;
        double n3 = m00 * mat.m02 + m01 * mat.m12 + m02 * mat.m22 + m03 * mat.m32;
        double n4 = m00 * mat.m03 + m01 * mat.m13 + m02 * mat.m23 + m03 * mat.m33;
        double n5 = m10 * mat.m00 + m11 * mat.m10 + m12 * mat.m20 + m13 * mat.m30;
        double n6 = m10 * mat.m01 + m11 * mat.m11 + m12 * mat.m21 + m13 * mat.m31;
        double n7 = m10 * mat.m02 + m11 * mat.m12 + m12 * mat.m22 + m13 * mat.m32;
        double n8 = m10 * mat.m03 + m11 * mat.m13 + m12 * mat.m23 + m13 * mat.m33;
        double n9 = m20 * mat.m00 + m21 * mat.m10 + m22 * mat.m20 + m23 * mat.m30;
        double n10 = m20 * mat.m01 + m21 * mat.m11 + m22 * mat.m21 + m23 * mat.m31;
        double n11 = m20 * mat.m02 + m21 * mat.m12 + m22 * mat.m22 + m23 * mat.m32;
        double n12 = m20 * mat.m03 + m21 * mat.m13 + m22 * mat.m23 + m23 * mat.m33;
        double n13 = m30 * mat.m00 + m31 * mat.m10 + m32 * mat.m20 + m33 * mat.m30;
        double n14 = m30 * mat.m01 + m31 * mat.m11 + m32 * mat.m21 + m33 * mat.m31;
        double n15 = m30 * mat.m02 + m31 * mat.m12 + m32 * mat.m22 + m33 * mat.m32;
        double n16 = m30 * mat.m03 + m31 * mat.m13 + m32 * mat.m23 + m33 * mat.m33;
        m00 = n00;
        m01 = n2;
        m02 = n3;
        m03 = n4;
        m10 = n5;
        m11 = n6;
        m12 = n7;
        m13 = n8;
        m20 = n9;
        m21 = n10;
        m22 = n11;
        m23 = n12;
        m30 = n13;
        m31 = n14;
        m32 = n15;
        m33 = n16;
        return this;
    }
    
    public Matrix4 transpose() {
        double n00 = m00;
        double n2 = m01;
        double n3 = m02;
        double n4 = m03;
        double n5 = m10;
        double n6 = m11;
        double n7 = m12;
        double n8 = m13;
        double n9 = m20;
        double n10 = m21;
        double n11 = m22;
        double n12 = m23;
        double n13 = m30;
        double n14 = m31;
        double n15 = m32;
        double n16 = m33;
        m00 = n00;
        m01 = n5;
        m02 = n9;
        m03 = n13;
        m10 = n2;
        m11 = n6;
        m12 = n10;
        m13 = n14;
        m20 = n3;
        m21 = n7;
        m22 = n11;
        m23 = n15;
        m30 = n4;
        m31 = n8;
        m32 = n12;
        m33 = n16;
        return this;
    }
    
    @Override
    public Matrix4 copy() {
        return new Matrix4(this);
    }
    
    public Matrix4 set(Matrix4 mat) {
        m00 = mat.m00;
        m01 = mat.m01;
        m02 = mat.m02;
        m03 = mat.m03;
        m10 = mat.m10;
        m11 = mat.m11;
        m12 = mat.m12;
        m13 = mat.m13;
        m20 = mat.m20;
        m21 = mat.m21;
        m22 = mat.m22;
        m23 = mat.m23;
        m30 = mat.m30;
        m31 = mat.m31;
        m32 = mat.m32;
        m33 = mat.m33;
        return this;
    }
    
    @Override
    public void apply(Matrix4 mat) {
        mat.multiply(this);
    }
    
    private void mult3x3(Vector3 vec) {
        double x = m00 * vec.x + m01 * vec.y + m02 * vec.z;
        double y = m10 * vec.x + m11 * vec.y + m12 * vec.z;
        double z = m20 * vec.x + m21 * vec.y + m22 * vec.z;
        vec.x = x;
        vec.y = y;
        vec.z = z;
    }
    
    @Override
    public void apply(Vector3 vec) {
        mult3x3(vec);
        vec.add(m03, m13, m23);
    }
    
    @Override
    public void applyN(Vector3 vec) {
        mult3x3(vec);
        vec.normalize();
    }
    
    @Override
    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "[" + new BigDecimal(m00, cont) + "," + new BigDecimal(m01, cont) + "," + new BigDecimal(m02, cont) + "," + new BigDecimal(m03, cont) + "]\n[" + new BigDecimal(m10, cont) + "," + new BigDecimal(m11, cont) + "," + new BigDecimal(m12, cont) + "," + new BigDecimal(m13, cont) + "]\n[" + new BigDecimal(m20, cont) + "," + new BigDecimal(m21, cont) + "," + new BigDecimal(m22, cont) + "," + new BigDecimal(m23, cont) + "]\n[" + new BigDecimal(m30, cont) + "," + new BigDecimal(m31, cont) + "," + new BigDecimal(m32, cont) + "," + new BigDecimal(m33, cont) + "]";
    }
    
    public Matrix4 apply(Transformation t) {
        t.apply(this);
        return this;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void glApply() {
        Matrix4.glBuf.put(m00).put(m10).put(m20).put(m30).put(m01).put(m11).put(m21).put(m31).put(m02).put(m12).put(m22).put(m32).put(m03).put(m13).put(m23).put(m33);
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
