package thaumcraft.common.items.casters.foci;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;


public class FocusEffectFlux extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSFLUX";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.FLUX";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.FLUX;
    }
    
    @Override
    public int getComplexity() {
        return getSettingValue("power") * 3;
    }
    
    @Override
    public float getDamageForDisplay(float finalPower) {
        return (3 + getSettingValue("power")) * finalPower;
    }
    
    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(target.hitVec.x, target.hitVec.y, target.hitVec.z, new String[] { getKey() }), new NetworkRegistry.TargetPoint(getPackage().world.provider.getDimension(), target.hitVec.x, target.hitVec.y, target.hitVec.z, 64.0));
        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit != null) {
            float damage = getDamageForDisplay(finalPower);
            target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage((target.entityHit != null) ? target.entityHit : getPackage().getCaster(), getPackage().getCaster()), damage);
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)) };
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.PLAYERS, 2.0f, 2.0f + (float)(caster.world.rand.nextGaussian() * 0.10000000149011612));
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        FXGeneric fb = new FXGeneric(world, x, y, z, vx + world.rand.nextGaussian() * 0.01, vy + world.rand.nextGaussian() * 0.01, vz + world.rand.nextGaussian() * 0.01);
        fb.setMaxAge((int)(15.0f + 10.0f * world.rand.nextFloat()));
        fb.setRBGColorF(0.25f + world.rand.nextFloat() * 0.25f, 0.0f, 0.25f + world.rand.nextFloat() * 0.25f);
        fb.setAlphaF(0.0f, 1.0f, 1.0f, 0.0f);
        fb.setGridSize(64);
        fb.setParticles(128, 14, 1);
        fb.setScale(2.0f + world.rand.nextFloat(), 0.25f + world.rand.nextFloat() * 0.25f);
        fb.setLoop(true);
        fb.setSlowDown(0.9);
        fb.setGravity((float)(world.rand.nextGaussian() * 0.10000000149011612));
        fb.setRandomMovementScale(0.0125f, 0.0125f, 0.0125f);
        fb.setRotationSpeed((float)world.rand.nextGaussian());
        ParticleEngine.addEffectWithDelay(world, fb, world.rand.nextInt(4));
    }
}
