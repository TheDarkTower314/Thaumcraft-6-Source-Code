package thaumcraft.codechicken.lib.raytracer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.codechicken.lib.math.MathHelper;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;


public class RayTracer
{
    private Vector3 vec;
    private Vector3 vec2;
    private Vector3 s_vec;
    private double s_dist;
    private int s_side;
    private IndexedCuboid6 c_cuboid;
    private static ThreadLocal<RayTracer> t_inst;
    
    public RayTracer() {
        vec = new Vector3();
        vec2 = new Vector3();
        s_vec = new Vector3();
    }
    
    public static RayTracer instance() {
        RayTracer inst = RayTracer.t_inst.get();
        if (inst == null) {
            RayTracer.t_inst.set(inst = new RayTracer());
        }
        return inst;
    }
    
    private void traceSide(int side, Vector3 start, Vector3 end, Cuboid6 cuboid) {
        vec.set(start);
        Vector3 hit = null;
        switch (side) {
            case 0: {
                hit = vec.XZintercept(end, cuboid.min.y);
                break;
            }
            case 1: {
                hit = vec.XZintercept(end, cuboid.max.y);
                break;
            }
            case 2: {
                hit = vec.XYintercept(end, cuboid.min.z);
                break;
            }
            case 3: {
                hit = vec.XYintercept(end, cuboid.max.z);
                break;
            }
            case 4: {
                hit = vec.YZintercept(end, cuboid.min.x);
                break;
            }
            case 5: {
                hit = vec.YZintercept(end, cuboid.max.x);
                break;
            }
        }
        if (hit == null) {
            return;
        }
        switch (side) {
            case 0:
            case 1: {
                if (!MathHelper.between(cuboid.min.x, hit.x, cuboid.max.x) || !MathHelper.between(cuboid.min.z, hit.z, cuboid.max.z)) {
                    return;
                }
                break;
            }
            case 2:
            case 3: {
                if (!MathHelper.between(cuboid.min.x, hit.x, cuboid.max.x) || !MathHelper.between(cuboid.min.y, hit.y, cuboid.max.y)) {
                    return;
                }
                break;
            }
            case 4:
            case 5: {
                if (!MathHelper.between(cuboid.min.y, hit.y, cuboid.max.y) || !MathHelper.between(cuboid.min.z, hit.z, cuboid.max.z)) {
                    return;
                }
                break;
            }
        }
        double dist = vec2.set(hit).subtract(start).magSquared();
        if (dist < s_dist) {
            s_side = side;
            s_dist = dist;
            s_vec.set(vec);
        }
    }
    
    private boolean rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid) {
        s_dist = Double.MAX_VALUE;
        s_side = -1;
        for (int i = 0; i < 6; ++i) {
            traceSide(i, start, end, cuboid);
        }
        return s_side >= 0;
    }
    
    public ExtendedMOP rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid, BlockCoord pos, Object data) {
        return rayTraceCuboid(start, end, cuboid) ? new ExtendedMOP(s_vec, s_side, pos, data, s_dist) : null;
    }
    
    public ExtendedMOP rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid, Entity entity, Object data) {
        return rayTraceCuboid(start, end, cuboid) ? new ExtendedMOP(entity, s_vec, data, s_dist) : null;
    }
    
    public void rayTraceCuboids(Vector3 start, Vector3 end, List<IndexedCuboid6> cuboids, BlockCoord pos, Block block, List<ExtendedMOP> hitList) {
        for (IndexedCuboid6 cuboid : cuboids) {
            ExtendedMOP mop = rayTraceCuboid(start, end, cuboid, pos, cuboid.data);
            if (mop != null) {
                hitList.add(mop);
            }
        }
    }
    
    public static RayTraceResult retraceBlock(World world, EntityPlayer player, BlockPos pos) {
        IBlockState b = world.getBlockState(pos);
        Vec3d headVec = getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0f);
        double reach = getBlockReachDistance(player);
        Vec3d endVec = headVec.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        return b.collisionRayTrace(world, pos, headVec, endVec);
    }
    
    private static double getBlockReachDistance_server(EntityPlayerMP player) {
        return player.interactionManager.getBlockReachDistance();
    }
    
    @SideOnly(Side.CLIENT)
    private static double getBlockReachDistance_client() {
        return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }
    
    public static RayTraceResult retrace(EntityPlayer player) {
        return retrace(player, getBlockReachDistance(player));
    }
    
    public static RayTraceResult retrace(EntityPlayer player, double reach) {
        Vec3d headVec = getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0f);
        Vec3d endVec = headVec.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        return player.world.rayTraceBlocks(headVec, endVec, true, false, true);
    }
    
    public static Vec3d getCorrectedHeadVec(EntityPlayer player) {
        Vector3 v = Vector3.fromEntity(player);
        if (player.world.isRemote) {
            Vector3 vector3 = v;
            vector3.y += player.getEyeHeight();
        }
        else {
            Vector3 vector4 = v;
            vector4.y += player.getEyeHeight();
            if (player instanceof EntityPlayerMP && player.isSneaking()) {
                Vector3 vector5 = v;
                vector5.y -= 0.08;
            }
        }
        return v.vec3();
    }
    
    public static Vec3d getStartVec(EntityPlayer player) {
        return getCorrectedHeadVec(player);
    }
    
    public static double getBlockReachDistance(EntityPlayer player) {
        return player.world.isRemote ? getBlockReachDistance_client() : ((player instanceof EntityPlayerMP) ? getBlockReachDistance_server((EntityPlayerMP)player) : 5.0);
    }
    
    public static Vec3d getEndVec(EntityPlayer player) {
        Vec3d headVec = getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0f);
        double reach = getBlockReachDistance(player);
        return headVec.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }
    
    static {
        RayTracer.t_inst = new ThreadLocal<RayTracer>();
    }
}
