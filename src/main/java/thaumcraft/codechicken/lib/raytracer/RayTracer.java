// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.raytracer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import java.util.Iterator;
import net.minecraft.block.Block;
import java.util.List;
import net.minecraft.entity.Entity;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.math.MathHelper;
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
    
    private void traceSide(final int side, final Vector3 start, final Vector3 end, final Cuboid6 cuboid) {
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
        final double dist = vec2.set(hit).subtract(start).magSquared();
        if (dist < s_dist) {
            s_side = side;
            s_dist = dist;
            s_vec.set(vec);
        }
    }
    
    private boolean rayTraceCuboid(final Vector3 start, final Vector3 end, final Cuboid6 cuboid) {
        s_dist = Double.MAX_VALUE;
        s_side = -1;
        for (int i = 0; i < 6; ++i) {
            traceSide(i, start, end, cuboid);
        }
        return s_side >= 0;
    }
    
    public ExtendedMOP rayTraceCuboid(final Vector3 start, final Vector3 end, final Cuboid6 cuboid, final BlockCoord pos, final Object data) {
        return rayTraceCuboid(start, end, cuboid) ? new ExtendedMOP(s_vec, s_side, pos, data, s_dist) : null;
    }
    
    public ExtendedMOP rayTraceCuboid(final Vector3 start, final Vector3 end, final Cuboid6 cuboid, final Entity entity, final Object data) {
        return rayTraceCuboid(start, end, cuboid) ? new ExtendedMOP(entity, s_vec, data, s_dist) : null;
    }
    
    public void rayTraceCuboids(final Vector3 start, final Vector3 end, final List<IndexedCuboid6> cuboids, final BlockCoord pos, final Block block, final List<ExtendedMOP> hitList) {
        for (final IndexedCuboid6 cuboid : cuboids) {
            final ExtendedMOP mop = rayTraceCuboid(start, end, cuboid, pos, cuboid.data);
            if (mop != null) {
                hitList.add(mop);
            }
        }
    }
    
    public static RayTraceResult retraceBlock(final World world, final EntityPlayer player, final BlockPos pos) {
        final IBlockState b = world.getBlockState(pos);
        final Vec3d headVec = getCorrectedHeadVec(player);
        final Vec3d lookVec = player.getLook(1.0f);
        final double reach = getBlockReachDistance(player);
        final Vec3d endVec = headVec.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        return b.collisionRayTrace(world, pos, headVec, endVec);
    }
    
    private static double getBlockReachDistance_server(final EntityPlayerMP player) {
        return player.interactionManager.getBlockReachDistance();
    }
    
    @SideOnly(Side.CLIENT)
    private static double getBlockReachDistance_client() {
        return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }
    
    public static RayTraceResult retrace(final EntityPlayer player) {
        return retrace(player, getBlockReachDistance(player));
    }
    
    public static RayTraceResult retrace(final EntityPlayer player, final double reach) {
        final Vec3d headVec = getCorrectedHeadVec(player);
        final Vec3d lookVec = player.getLook(1.0f);
        final Vec3d endVec = headVec.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        return player.world.rayTraceBlocks(headVec, endVec, true, false, true);
    }
    
    public static Vec3d getCorrectedHeadVec(final EntityPlayer player) {
        final Vector3 v = Vector3.fromEntity(player);
        if (player.world.isRemote) {
            final Vector3 vector3 = v;
            vector3.y += player.getEyeHeight();
        }
        else {
            final Vector3 vector4 = v;
            vector4.y += player.getEyeHeight();
            if (player instanceof EntityPlayerMP && player.isSneaking()) {
                final Vector3 vector5 = v;
                vector5.y -= 0.08;
            }
        }
        return v.vec3();
    }
    
    public static Vec3d getStartVec(final EntityPlayer player) {
        return getCorrectedHeadVec(player);
    }
    
    public static double getBlockReachDistance(final EntityPlayer player) {
        return player.world.isRemote ? getBlockReachDistance_client() : ((player instanceof EntityPlayerMP) ? getBlockReachDistance_server((EntityPlayerMP)player) : 5.0);
    }
    
    public static Vec3d getEndVec(final EntityPlayer player) {
        final Vec3d headVec = getCorrectedHeadVec(player);
        final Vec3d lookVec = player.getLook(1.0f);
        final double reach = getBlockReachDistance(player);
        return headVec.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }
    
    static {
        RayTracer.t_inst = new ThreadLocal<RayTracer>();
    }
}
