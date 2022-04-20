// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import thaumcraft.api.casters.FocusEffect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXFocusEffect;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import net.minecraft.entity.player.EntityPlayer;
import java.util.ArrayList;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusMedium;

public class FocusMediumTouch extends FocusMedium
{
    @Override
    public String getResearch() {
        return "BASEAUROMANCY";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.TOUCH";
    }
    
    @Override
    public int getComplexity() {
        return 2;
    }
    
    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TRAJECTORY, EnumSupplyType.TARGET };
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.AVERSION;
    }
    
    @Override
    public Trajectory[] supplyTrajectories() {
        if (this.getParent() == null) {
            return new Trajectory[0];
        }
        final ArrayList<Trajectory> trajectories = new ArrayList<Trajectory>();
        final double range = (this instanceof FocusMediumBolt) ? 16.0 : RayTracer.getBlockReachDistance((EntityPlayer)this.getPackage().getCaster());
        for (final Trajectory sT : this.getParent().supplyTrajectories()) {
            Vec3d end = sT.direction.normalize();
            RayTraceResult ray = EntityUtils.getPointedEntityRay(this.getPackage().world, this.getPackage().getCaster(), sT.source, end, 0.25, range, 0.25f, false);
            if (ray == null) {
                end = end.scale(range);
                end = end.add(sT.source);
                ray = this.getPackage().world.rayTraceBlocks(sT.source, end);
                if (ray != null) {
                    end = ray.hitVec;
                }
            }
            else if (ray.entityHit != null) {
                end = end.scale(sT.source.distanceTo(ray.entityHit.getPositionVector()));
                end = end.add(sT.source);
            }
            trajectories.add(new Trajectory(end, sT.direction.normalize()));
        }
        return trajectories.toArray(new Trajectory[0]);
    }
    
    @Override
    public RayTraceResult[] supplyTargets() {
        if (this.getParent() == null || !(this.getPackage().getCaster() instanceof EntityPlayer)) {
            return new RayTraceResult[0];
        }
        final ArrayList<RayTraceResult> targets = new ArrayList<RayTraceResult>();
        final double range = (this instanceof FocusMediumBolt) ? 16.0 : RayTracer.getBlockReachDistance((EntityPlayer)this.getPackage().getCaster());
        for (final Trajectory sT : this.getParent().supplyTrajectories()) {
            Vec3d end = sT.direction.normalize();
            RayTraceResult ray = EntityUtils.getPointedEntityRay(this.getPackage().world, this.getPackage().getCaster(), sT.source, end, 0.25, range, 0.25f, false);
            if (ray == null) {
                end = end.scale(range);
                end = end.add(sT.source);
                ray = this.getPackage().world.rayTraceBlocks(sT.source, end);
            }
            if (ray != null) {
                targets.add(ray);
            }
        }
        return targets.toArray(new RayTraceResult[0]);
    }
    
    @Override
    public boolean execute(final Trajectory trajectory) {
        final FocusEffect[] fe = this.getPackage().getFocusEffects();
        if (fe != null && fe.length > 0) {
            final String[] effects = new String[fe.length];
            for (int a = 0; a < fe.length; ++a) {
                effects[a] = fe[a].getKey();
            }
            PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusEffect((float)trajectory.source.x, (float)trajectory.source.y, (float)trajectory.source.z, (float)trajectory.direction.x / 2.0f, (float)trajectory.direction.y / 2.0f, (float)trajectory.direction.z / 2.0f, effects), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(), (float)trajectory.source.x, (float)trajectory.source.y, (float)trajectory.source.z, 64.0));
        }
        return true;
    }
}
