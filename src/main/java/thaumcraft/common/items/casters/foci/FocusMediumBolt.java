// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import thaumcraft.api.casters.FocusEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXZap;
import thaumcraft.common.lib.network.PacketHandler;
import java.awt.Color;
import thaumcraft.api.casters.FocusEngine;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.aspects.Aspect;

public class FocusMediumBolt extends FocusMediumTouch
{
    @Override
    public String getResearch() {
        return "FOCUSBOLT";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.BOLT";
    }
    
    @Override
    public int getComplexity() {
        return 5;
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.ENERGY;
    }
    
    @Override
    public boolean execute(final Trajectory trajectory) {
        final float range = 16.0f;
        Vec3d end = trajectory.direction.normalize();
        RayTraceResult ray = EntityUtils.getPointedEntityRay(this.getPackage().world, this.getPackage().getCaster(), trajectory.source, end, 0.25, range, 0.25f, false);
        if (ray == null) {
            end = end.scale(range);
            end = end.add(trajectory.source);
            ray = this.getPackage().world.rayTraceBlocks(trajectory.source, end);
            if (ray != null) {
                end = ray.hitVec;
            }
        }
        else if (ray.entityHit != null) {
            end = end.scale(trajectory.source.distanceTo(ray.entityHit.getPositionVector()));
            end = end.add(trajectory.source);
        }
        int r = 0;
        int g = 0;
        int b = 0;
        for (final FocusEffect ef : this.getPackage().getFocusEffects()) {
            final Color c = new Color(FocusEngine.getElementColor(ef.getKey()));
            r += c.getRed();
            g += c.getGreen();
            b += c.getBlue();
        }
        r /= this.getPackage().getFocusEffects().length;
        g /= this.getPackage().getFocusEffects().length;
        b /= this.getPackage().getFocusEffects().length;
        final Color c2 = new Color(r, g, b);
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXZap(trajectory.source, end, c2.getRGB(), this.getPackage().getPower() * 0.66f), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(), trajectory.source.x, trajectory.source.y, trajectory.source.z, 64.0));
        return true;
    }
}
