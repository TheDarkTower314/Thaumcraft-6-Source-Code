// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import thaumcraft.codechicken.lib.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class Rotation extends Transformation
{
    public static Transformation[] quarterRotations;
    public static Transformation[] sideRotations;
    public static Vector3[] axes;
    public static int[] sideRotMap;
    public static int[] rotSideMap;
    public static int[] sideRotOffsets;
    public double angle;
    public Vector3 axis;
    private Quat quat;
    
    public static int rotateSide(final int s, final int r) {
        return Rotation.sideRotMap[s << 2 | r];
    }
    
    public static int rotationTo(final int s1, final int s2) {
        if ((s1 & 0x6) == (s2 & 0x6)) {
            throw new IllegalArgumentException("Faces " + s1 + " and " + s2 + " are opposites");
        }
        return Rotation.rotSideMap[s1 * 6 + s2];
    }
    
    public static int getSidedRotation(final EntityPlayer player, final int side) {
        final Vector3 look = new Vector3(player.getLook(1.0f));
        double max = 0.0;
        int maxr = 0;
        for (int r = 0; r < 4; ++r) {
            final Vector3 axis = Rotation.axes[rotateSide(side ^ 0x1, r)];
            final double d = look.scalarProject(axis);
            if (d > max) {
                max = d;
                maxr = r;
            }
        }
        return maxr;
    }
    
    public static Transformation sideOrientation(final int s, final int r) {
        return Rotation.quarterRotations[(r + Rotation.sideRotOffsets[s]) % 4].with(Rotation.sideRotations[s]);
    }
    
    public static int getSideFromLookAngle(final EntityLivingBase entity) {
        final Vector3 look = new Vector3(entity.getLook(1.0f));
        double max = 0.0;
        int maxs = 0;
        for (int s = 0; s < 6; ++s) {
            final double d = look.scalarProject(Rotation.axes[s]);
            if (d > max) {
                max = d;
                maxs = s;
            }
        }
        return maxs;
    }
    
    public Rotation(final double angle, final Vector3 axis) {
        this.angle = angle;
        this.axis = axis;
    }
    
    public Rotation(final double angle, final double x, final double y, final double z) {
        this(angle, new Vector3(x, y, z));
    }
    
    public Rotation(final Quat quat) {
        this.quat = quat;
        this.angle = Math.acos(quat.s) * 2.0;
        if (this.angle == 0.0) {
            this.axis = new Vector3(0.0, 1.0, 0.0);
        }
        else {
            final double sa = Math.sin(this.angle * 0.5);
            this.axis = new Vector3(quat.x / sa, quat.y / sa, quat.z / sa);
        }
    }
    
    @Override
    public void apply(final Vector3 vec) {
        if (this.quat == null) {
            this.quat = Quat.aroundAxis(this.axis, this.angle);
        }
        vec.rotate(this.quat);
    }
    
    @Override
    public void applyN(final Vector3 normal) {
        this.apply(normal);
    }
    
    @Override
    public void apply(final Matrix4 mat) {
        mat.rotate(this.angle, this.axis);
    }
    
    public Quat toQuat() {
        if (this.quat == null) {
            this.quat = Quat.aroundAxis(this.axis, this.angle);
        }
        return this.quat;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void glApply() {
        GlStateManager.rotate((float)(this.angle * 57.29577951308232), (float)this.axis.x, (float)this.axis.y, (float)this.axis.z);
    }
    
    @Override
    public Transformation inverse() {
        return new Rotation(-this.angle, this.axis);
    }
    
    @Override
    public Transformation merge(final Transformation next) {
        if (!(next instanceof Rotation)) {
            return null;
        }
        final Rotation r = (Rotation)next;
        if (r.axis.equalsT(this.axis)) {
            return new Rotation(this.angle + r.angle, this.axis);
        }
        return new Rotation(this.toQuat().copy().multiply(r.toQuat()));
    }
    
    @Override
    public boolean isRedundant() {
        return MathHelper.between(-1.0E-5, this.angle, 1.0E-5);
    }
    
    @Override
    public String toString() {
        final MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Rotation(" + new BigDecimal(this.angle, cont) + ", " + new BigDecimal(this.axis.x, cont) + ", " + new BigDecimal(this.axis.y, cont) + ", " + new BigDecimal(this.axis.z, cont) + ")";
    }
    
    static {
        Rotation.quarterRotations = new Transformation[] { new RedundantTransformation(), new VariableTransformation(new Matrix4(0.0, 0.0, -1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(final Vector3 vec) {
                    final double d1 = vec.x;
                    final double d2 = vec.z;
                    vec.x = -d2;
                    vec.z = d1;
                }
                
                @Override
                public Transformation inverse() {
                    return Rotation.quarterRotations[3];
                }
            }, new VariableTransformation(new Matrix4(-1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(final Vector3 vec) {
                    vec.x = -vec.x;
                    vec.z = -vec.z;
                }
                
                @Override
                public Transformation inverse() {
                    return this;
                }
            }, new VariableTransformation(new Matrix4(0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(final Vector3 vec) {
                    final double d1 = vec.x;
                    final double d2 = vec.z;
                    vec.x = d2;
                    vec.z = -d1;
                }
                
                @Override
                public Transformation inverse() {
                    return Rotation.quarterRotations[1];
                }
            } };
        Rotation.sideRotations = new Transformation[] { new RedundantTransformation(), new VariableTransformation(new Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(final Vector3 vec) {
                    vec.y = -vec.y;
                    vec.z = -vec.z;
                }
                
                @Override
                public Transformation inverse() {
                    return this;
                }
            }, new VariableTransformation(new Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(final Vector3 vec) {
                    final double d1 = vec.y;
                    final double d2 = vec.z;
                    vec.y = -d2;
                    vec.z = d1;
                }
                
                @Override
                public Transformation inverse() {
                    return Rotation.sideRotations[3];
                }
            }, new VariableTransformation(new Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(final Vector3 vec) {
                    final double d1 = vec.y;
                    final double d2 = vec.z;
                    vec.y = d2;
                    vec.z = -d1;
                }
                
                @Override
                public Transformation inverse() {
                    return Rotation.sideRotations[2];
                }
            }, new VariableTransformation(new Matrix4(0.0, 1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(final Vector3 vec) {
                    final double d0 = vec.x;
                    final double d2 = vec.y;
                    vec.x = d2;
                    vec.y = -d0;
                }
                
                @Override
                public Transformation inverse() {
                    return Rotation.sideRotations[5];
                }
            }, new VariableTransformation(new Matrix4(0.0, -1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(final Vector3 vec) {
                    final double d0 = vec.x;
                    final double d2 = vec.y;
                    vec.x = -d2;
                    vec.y = d0;
                }
                
                @Override
                public Transformation inverse() {
                    return Rotation.sideRotations[4];
                }
            } };
        Rotation.axes = new Vector3[] { new Vector3(0.0, -1.0, 0.0), new Vector3(0.0, 1.0, 0.0), new Vector3(0.0, 0.0, -1.0), new Vector3(0.0, 0.0, 1.0), new Vector3(-1.0, 0.0, 0.0), new Vector3(1.0, 0.0, 0.0) };
        Rotation.sideRotMap = new int[] { 3, 4, 2, 5, 3, 5, 2, 4, 1, 5, 0, 4, 1, 4, 0, 5, 1, 2, 0, 3, 1, 3, 0, 2 };
        Rotation.rotSideMap = new int[] { -1, -1, 2, 0, 1, 3, -1, -1, 2, 0, 3, 1, 2, 0, -1, -1, 3, 1, 2, 0, -1, -1, 1, 3, 2, 0, 1, 3, -1, -1, 2, 0, 3, 1, -1, -1 };
        Rotation.sideRotOffsets = new int[] { 0, 2, 2, 0, 1, 3 };
    }
}
