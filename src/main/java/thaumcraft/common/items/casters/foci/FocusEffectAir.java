// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import thaumcraft.common.lib.SoundsTC;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.world.World;
import thaumcraft.api.casters.NodeSetting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.casters.Trajectory;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;

public class FocusEffectAir extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSELEMENTAL";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.AIR";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.AIR;
    }
    
    @Override
    public int getComplexity() {
        return this.getSettingValue("power") * 2;
    }
    
    @Override
    public float getDamageForDisplay(final float finalPower) {
        return (1 + this.getSettingValue("power")) * finalPower;
    }
    
    @Override
    public boolean execute(final RayTraceResult target, final Trajectory trajectory, final float finalPower, final int num) {
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(target.hitVec.x, target.hitVec.y, target.hitVec.z, new String[] { this.getKey() }), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(), target.hitVec.x, target.hitVec.y, target.hitVec.z, 64.0));
        this.getPackage().world.playSound(null, target.hitVec.x, target.hitVec.y, target.hitVec.z, SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.PLAYERS, 0.5f, 0.66f);
        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit != null) {
            final float damage = this.getDamageForDisplay(finalPower);
            target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage((target.entityHit != null) ? target.entityHit : this.getPackage().getCaster(), this.getPackage().getCaster()), damage);
            if (target.entityHit instanceof EntityLivingBase) {
                if (trajectory != null) {
                    ((EntityLivingBase)target.entityHit).knockBack(this.getPackage().getCaster(), damage * 0.25f, -trajectory.direction.x, -trajectory.direction.z);
                }
                else {
                    ((EntityLivingBase)target.entityHit).knockBack(this.getPackage().getCaster(), damage * 0.25f, -MathHelper.sin(target.entityHit.rotationYaw * 0.017453292f), MathHelper.cos(target.entityHit.rotationYaw * 0.017453292f));
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)) };
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderParticleFX(final World world, final double posX, final double posY, final double posZ, final double motionX, final double motionY, final double motionZ) {
        final FXDispatcher.GenPart pp = new FXDispatcher.GenPart();
        pp.grav = -0.1f;
        pp.age = 20 + world.rand.nextInt(10);
        pp.alpha = new float[] { 0.5f, 0.0f };
        pp.grid = 32;
        pp.partStart = 337;
        pp.partInc = 1;
        pp.partNum = 5;
        pp.slowDown = 0.75;
        pp.rot = (float)world.rand.nextGaussian() / 2.0f;
        final float s = (float)(2.0 + world.rand.nextGaussian() * 0.5);
        pp.scale = new float[] { s, s * 2.0f };
        FXDispatcher.INSTANCE.drawGenericParticles(posX, posY, posZ, motionX, motionY, motionZ, pp);
    }
    
    @Override
    public void onCast(final Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundsTC.wind, SoundCategory.PLAYERS, 0.125f, 2.0f);
    }
}
