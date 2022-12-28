package thaumcraft.common.items.casters.foci;
import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXZap;
import thaumcraft.common.lib.utils.EntityUtils;


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
    public boolean execute(Trajectory trajectory) {
        float range = 16.0f;
        Vec3d end = trajectory.direction.normalize();
        RayTraceResult ray = EntityUtils.getPointedEntityRay(getPackage().world, getPackage().getCaster(), trajectory.source, end, 0.25, range, 0.25f, false);
        if (ray == null) {
            end = end.scale(range);
            end = end.add(trajectory.source);
            ray = getPackage().world.rayTraceBlocks(trajectory.source, end);
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
        for (FocusEffect ef : getPackage().getFocusEffects()) {
            Color c = new Color(FocusEngine.getElementColor(ef.getKey()));
            r += c.getRed();
            g += c.getGreen();
            b += c.getBlue();
        }
        r /= getPackage().getFocusEffects().length;
        g /= getPackage().getFocusEffects().length;
        b /= getPackage().getFocusEffects().length;
        Color c2 = new Color(r, g, b);
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXZap(trajectory.source, end, c2.getRGB(), getPackage().getPower() * 0.66f), new NetworkRegistry.TargetPoint(getPackage().world.provider.getDimension(), trajectory.source.x, trajectory.source.y, trajectory.source.z, 64.0));
        return true;
    }
}
