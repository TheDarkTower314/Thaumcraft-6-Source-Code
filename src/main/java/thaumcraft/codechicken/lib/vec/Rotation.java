package thaumcraft.codechicken.lib.vec;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.codechicken.lib.math.MathHelper;


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
    
    public static int rotateSide(int s, int r) {
        return Rotation.sideRotMap[s << 2 | r];
    }
    
    public static int rotationTo(int s1, int s2) {
        if ((s1 & 0x6) == (s2 & 0x6)) {
            throw new IllegalArgumentException("Faces " + s1 + " and " + s2 + " are opposites");
        }
        return Rotation.rotSideMap[s1 * 6 + s2];
    }
    
    public static int getSidedRotation(EntityPlayer player, int side) {
        Vector3 look = new Vector3(player.getLook(1.0f));
        double max = 0.0;
        int maxr = 0;
        for (int r = 0; r < 4; ++r) {
            Vector3 axis = Rotation.axes[rotateSide(side ^ 0x1, r)];
            double d = look.scalarProject(axis);
            if (d > max) {
                max = d;
                maxr = r;
            }
        }
        return maxr;
    }
    
    public static Transformation sideOrientation(int s, int r) {
        return Rotation.quarterRotations[(r + Rotation.sideRotOffsets[s]) % 4].with(Rotation.sideRotations[s]);
    }
    
    public static int getSideFromLookAngle(EntityLivingBase entity) {
        Vector3 look = new Vector3(entity.getLook(1.0f));
        double max = 0.0;
        int maxs = 0;
        for (int s = 0; s < 6; ++s) {
            double d = look.scalarProject(Rotation.axes[s]);
            if (d > max) {
                max = d;
                maxs = s;
            }
        }
        return maxs;
    }
    
    public Rotation(double angle, Vector3 axis) {
        this.angle = angle;
        this.axis = axis;
    }
    
    public Rotation(double angle, double x, double y, double z) {
        this(angle, new Vector3(x, y, z));
    }
    
    public Rotation(Quat quat) {
        this.quat = quat;
        angle = Math.acos(quat.s) * 2.0;
        if (angle == 0.0) {
            axis = new Vector3(0.0, 1.0, 0.0);
        }
        else {
            double sa = Math.sin(angle * 0.5);
            axis = new Vector3(quat.x / sa, quat.y / sa, quat.z / sa);
        }
    }
    
    @Override
    public void apply(Vector3 vec) {
        if (quat == null) {
            quat = Quat.aroundAxis(axis, angle);
        }
        vec.rotate(quat);
    }
    
    @Override
    public void applyN(Vector3 normal) {
        apply(normal);
    }
    
    @Override
    public void apply(Matrix4 mat) {
        mat.rotate(angle, axis);
    }
    
    public Quat toQuat() {
        if (quat == null) {
            quat = Quat.aroundAxis(axis, angle);
        }
        return quat;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void glApply() {
        GlStateManager.rotate((float)(angle * 57.29577951308232), (float) axis.x, (float) axis.y, (float) axis.z);
    }
    
    @Override
    public Transformation inverse() {
        return new Rotation(-angle, axis);
    }
    
    @Override
    public Transformation merge(Transformation next) {
        if (!(next instanceof Rotation)) {
            return null;
        }
        Rotation r = (Rotation)next;
        if (r.axis.equalsT(axis)) {
            return new Rotation(angle + r.angle, axis);
        }
        return new Rotation(toQuat().copy().multiply(r.toQuat()));
    }
    
    @Override
    public boolean isRedundant() {
        return MathHelper.between(-1.0E-5, angle, 1.0E-5);
    }
    
    @Override
    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Rotation(" + new BigDecimal(angle, cont) + ", " + new BigDecimal(axis.x, cont) + ", " + new BigDecimal(axis.y, cont) + ", " + new BigDecimal(axis.z, cont) + ")";
    }
    
    static {
        Rotation.quarterRotations = new Transformation[] { new RedundantTransformation(), new VariableTransformation(new Matrix4(0.0, 0.0, -1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(Vector3 vec) {
                    double d1 = vec.x;
                    double d2 = vec.z;
                    vec.x = -d2;
                    vec.z = d1;
                }
                
                @Override
                public Transformation inverse() {
                    return Rotation.quarterRotations[3];
                }
            }, new VariableTransformation(new Matrix4(-1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(Vector3 vec) {
                    vec.x = -vec.x;
                    vec.z = -vec.z;
                }
                
                @Override
                public Transformation inverse() {
                    return this;
                }
            }, new VariableTransformation(new Matrix4(0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(Vector3 vec) {
                    double d1 = vec.x;
                    double d2 = vec.z;
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
                public void apply(Vector3 vec) {
                    vec.y = -vec.y;
                    vec.z = -vec.z;
                }
                
                @Override
                public Transformation inverse() {
                    return this;
                }
            }, new VariableTransformation(new Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(Vector3 vec) {
                    double d1 = vec.y;
                    double d2 = vec.z;
                    vec.y = -d2;
                    vec.z = d1;
                }
                
                @Override
                public Transformation inverse() {
                    return Rotation.sideRotations[3];
                }
            }, new VariableTransformation(new Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(Vector3 vec) {
                    double d1 = vec.y;
                    double d2 = vec.z;
                    vec.y = d2;
                    vec.z = -d1;
                }
                
                @Override
                public Transformation inverse() {
                    return Rotation.sideRotations[2];
                }
            }, new VariableTransformation(new Matrix4(0.0, 1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(Vector3 vec) {
                    double d0 = vec.x;
                    double d2 = vec.y;
                    vec.x = d2;
                    vec.y = -d0;
                }
                
                @Override
                public Transformation inverse() {
                    return Rotation.sideRotations[5];
                }
            }, new VariableTransformation(new Matrix4(0.0, -1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(Vector3 vec) {
                    double d0 = vec.x;
                    double d2 = vec.y;
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
