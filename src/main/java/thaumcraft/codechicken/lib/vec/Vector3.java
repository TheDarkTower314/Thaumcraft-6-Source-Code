// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

import thaumcraft.codechicken.lib.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thaumcraft.codechicken.lib.util.Copyable;

public class Vector3 implements Copyable<Vector3>
{
    public static Vector3 zero;
    public static Vector3 one;
    public static Vector3 center;
    public double x;
    public double y;
    public double z;
    
    public Vector3() {
    }
    
    public Vector3(final double d, final double d1, final double d2) {
        this.x = d;
        this.y = d1;
        this.z = d2;
    }
    
    public Vector3(final Vector3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }
    
    public Vector3(final double[] da) {
        this(da[0], da[1], da[2]);
    }
    
    public Vector3(final Vec3d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }
    
    public Vector3(final BlockCoord coord) {
        this.x = coord.x;
        this.y = coord.y;
        this.z = coord.z;
    }
    
    public Vector3(final BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }
    
    @Override
    public Vector3 copy() {
        return new Vector3(this);
    }
    
    public static Vector3 fromEntity(final Entity e) {
        return new Vector3(e.posX, e.posY, e.posZ);
    }
    
    public static Vector3 fromEntityCenter(final Entity e) {
        return new Vector3(e.posX, e.posY - e.getYOffset() + e.height / 2.0f, e.posZ);
    }
    
    public static Vector3 fromTile(final TileEntity tile) {
        return new Vector3(tile.getPos());
    }
    
    public static Vector3 fromTileCenter(final TileEntity tile) {
        return fromTile(tile).add(0.5);
    }
    
    public static Vector3 fromAxes(final double[] da) {
        return new Vector3(da[2], da[0], da[1]);
    }
    
    public Vector3 set(final double d, final double d1, final double d2) {
        this.x = d;
        this.y = d1;
        this.z = d2;
        return this;
    }
    
    public Vector3 set(final Vector3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        return this;
    }
    
    public double getSide(final int side) {
        switch (side) {
            case 0:
            case 1: {
                return this.y;
            }
            case 2:
            case 3: {
                return this.z;
            }
            case 4:
            case 5: {
                return this.x;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
        }
    }
    
    public Vector3 setSide(final int s, final double v) {
        switch (s) {
            case 0:
            case 1: {
                this.y = v;
                break;
            }
            case 2:
            case 3: {
                this.z = v;
                break;
            }
            case 4:
            case 5: {
                this.x = v;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
        }
        return this;
    }
    
    public double dotProduct(final Vector3 vec) {
        double d = vec.x * this.x + vec.y * this.y + vec.z * this.z;
        if (d > 1.0 && d < 1.00001) {
            d = 1.0;
        }
        else if (d < -1.0 && d > -1.00001) {
            d = -1.0;
        }
        return d;
    }
    
    public double dotProduct(final double d, final double d1, final double d2) {
        return d * this.x + d1 * this.y + d2 * this.z;
    }
    
    public Vector3 crossProduct(final Vector3 vec) {
        final double d = this.y * vec.z - this.z * vec.y;
        final double d2 = this.z * vec.x - this.x * vec.z;
        final double d3 = this.x * vec.y - this.y * vec.x;
        this.x = d;
        this.y = d2;
        this.z = d3;
        return this;
    }
    
    public Vector3 add(final double d, final double d1, final double d2) {
        this.x += d;
        this.y += d1;
        this.z += d2;
        return this;
    }
    
    public Vector3 add(final Vector3 vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }
    
    public Vector3 add(final double d) {
        return this.add(d, d, d);
    }
    
    public Vector3 sub(final Vector3 vec) {
        return this.subtract(vec);
    }
    
    public Vector3 subtract(final Vector3 vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }
    
    public Vector3 negate(final Vector3 vec) {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }
    
    public Vector3 multiply(final double d) {
        this.x *= d;
        this.y *= d;
        this.z *= d;
        return this;
    }
    
    public Vector3 multiply(final Vector3 f) {
        this.x *= f.x;
        this.y *= f.y;
        this.z *= f.z;
        return this;
    }
    
    public Vector3 multiply(final double fx, final double fy, final double fz) {
        this.x *= fx;
        this.y *= fy;
        this.z *= fz;
        return this;
    }
    
    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public double magSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public Vector3 normalize() {
        final double d = this.mag();
        if (d != 0.0) {
            this.multiply(1.0 / d);
        }
        return this;
    }
    
    @Override
    public String toString() {
        final MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Vector3(" + new BigDecimal(this.x, cont) + ", " + new BigDecimal(this.y, cont) + ", " + new BigDecimal(this.z, cont) + ")";
    }
    
    public Vector3 perpendicular() {
        if (this.z == 0.0) {
            return this.zCrossProduct();
        }
        return this.xCrossProduct();
    }
    
    public Vector3 xCrossProduct() {
        final double d = this.z;
        final double d2 = -this.y;
        this.x = 0.0;
        this.y = d;
        this.z = d2;
        return this;
    }
    
    public Vector3 zCrossProduct() {
        final double d = this.y;
        final double d2 = -this.x;
        this.x = d;
        this.y = d2;
        this.z = 0.0;
        return this;
    }
    
    public Vector3 yCrossProduct() {
        final double d = -this.z;
        final double d2 = this.x;
        this.x = d;
        this.y = 0.0;
        this.z = d2;
        return this;
    }
    
    public Vector3 rotate(final double angle, final Vector3 axis) {
        Quat.aroundAxis(axis.copy().normalize(), angle).rotate(this);
        return this;
    }
    
    public Vector3 rotate(final Quat rotator) {
        rotator.rotate(this);
        return this;
    }
    
    public Vec3d vec3() {
        return new Vec3d(this.x, this.y, this.z);
    }
    
    public double angle(final Vector3 vec) {
        return Math.acos(this.copy().normalize().dotProduct(vec.copy().normalize()));
    }
    
    public boolean isZero() {
        return this.x == 0.0 && this.y == 0.0 && this.z == 0.0;
    }
    
    public boolean isAxial() {
        return (this.x == 0.0) ? (this.y == 0.0 || this.z == 0.0) : (this.y == 0.0 && this.z == 0.0);
    }
    
    @SideOnly(Side.CLIENT)
    public Vector3f vector3f() {
        return new Vector3f((float)this.x, (float)this.y, (float)this.z);
    }
    
    @SideOnly(Side.CLIENT)
    public Vector4f vector4f() {
        return new Vector4f((float)this.x, (float)this.y, (float)this.z, 1.0f);
    }
    
    @SideOnly(Side.CLIENT)
    public void glVertex() {
        GL11.glVertex3d(this.x, this.y, this.z);
    }
    
    public Vector3 YZintercept(final Vector3 end, final double px) {
        final double dx = end.x - this.x;
        final double dy = end.y - this.y;
        final double dz = end.z - this.z;
        if (dx == 0.0) {
            return null;
        }
        final double d = (px - this.x) / dx;
        if (MathHelper.between(-1.0E-5, d, 1.0E-5)) {
            return this;
        }
        if (!MathHelper.between(0.0, d, 1.0)) {
            return null;
        }
        this.x = px;
        this.y += d * dy;
        this.z += d * dz;
        return this;
    }
    
    public Vector3 XZintercept(final Vector3 end, final double py) {
        final double dx = end.x - this.x;
        final double dy = end.y - this.y;
        final double dz = end.z - this.z;
        if (dy == 0.0) {
            return null;
        }
        final double d = (py - this.y) / dy;
        if (MathHelper.between(-1.0E-5, d, 1.0E-5)) {
            return this;
        }
        if (!MathHelper.between(0.0, d, 1.0)) {
            return null;
        }
        this.x += d * dx;
        this.y = py;
        this.z += d * dz;
        return this;
    }
    
    public Vector3 XYintercept(final Vector3 end, final double pz) {
        final double dx = end.x - this.x;
        final double dy = end.y - this.y;
        final double dz = end.z - this.z;
        if (dz == 0.0) {
            return null;
        }
        final double d = (pz - this.z) / dz;
        if (MathHelper.between(-1.0E-5, d, 1.0E-5)) {
            return this;
        }
        if (!MathHelper.between(0.0, d, 1.0)) {
            return null;
        }
        this.x += d * dx;
        this.y += d * dy;
        this.z = pz;
        return this;
    }
    
    public Vector3 negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }
    
    public Translation translation() {
        return new Translation(this);
    }
    
    public double scalarProject(final Vector3 b) {
        final double l = b.mag();
        return (l == 0.0) ? 0.0 : (this.dotProduct(b) / l);
    }
    
    public Vector3 project(final Vector3 b) {
        final double l = b.magSquared();
        if (l == 0.0) {
            this.set(0.0, 0.0, 0.0);
            return this;
        }
        final double m = this.dotProduct(b) / l;
        this.set(b).multiply(m);
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Vector3)) {
            return false;
        }
        final Vector3 v = (Vector3)o;
        return this.x == v.x && this.y == v.y && this.z == v.z;
    }
    
    public boolean equalsT(final Vector3 v) {
        return MathHelper.between(this.x - 1.0E-5, v.x, this.x + 1.0E-5) && MathHelper.between(this.y - 1.0E-5, v.y, this.y + 1.0E-5) && MathHelper.between(this.z - 1.0E-5, v.z, this.z + 1.0E-5);
    }
    
    public Vector3 apply(final Transformation t) {
        t.apply(this);
        return this;
    }
    
    public Vector3 $tilde() {
        return this.normalize();
    }
    
    public Vector3 unary_$tilde() {
        return this.normalize();
    }
    
    public Vector3 $plus(final Vector3 v) {
        return this.add(v);
    }
    
    public Vector3 $minus(final Vector3 v) {
        return this.subtract(v);
    }
    
    public Vector3 $times(final double d) {
        return this.multiply(d);
    }
    
    public Vector3 $div(final double d) {
        return this.multiply(1.0 / d);
    }
    
    public Vector3 $times(final Vector3 v) {
        return this.crossProduct(v);
    }
    
    public double $dot$times(final Vector3 v) {
        return this.dotProduct(v);
    }
    
    static {
        Vector3.zero = new Vector3();
        Vector3.one = new Vector3(1.0, 1.0, 1.0);
        Vector3.center = new Vector3(0.5, 0.5, 0.5);
    }
}
