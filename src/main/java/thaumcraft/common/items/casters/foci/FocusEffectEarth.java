// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.world.World;
import thaumcraft.api.casters.NodeSetting;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.lib.events.ServerEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.casters.Trajectory;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;

public class FocusEffectEarth extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSELEMENTAL";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.EARTH";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.EARTH;
    }
    
    @Override
    public int getComplexity() {
        return this.getSettingValue("power") * 3;
    }
    
    @Override
    public float getDamageForDisplay(final float finalPower) {
        return 2 * this.getSettingValue("power") * finalPower;
    }
    
    @Override
    public boolean execute(final RayTraceResult target, final Trajectory trajectory, final float finalPower, final int num) {
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(target.hitVec.x, target.hitVec.y, target.hitVec.z, new String[] { this.getKey() }), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(), target.hitVec.x, target.hitVec.y, target.hitVec.z, 64.0));
        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit != null) {
            final float damage = this.getDamageForDisplay(finalPower);
            target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage((target.entityHit != null) ? target.entityHit : this.getPackage().getCaster(), this.getPackage().getCaster()), damage);
            return true;
        }
        if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            final BlockPos pos = target.getBlockPos();
            if (this.getPackage().getCaster() instanceof EntityPlayer && this.getPackage().world.getBlockState(pos).getBlockHardness(this.getPackage().world, pos) <= this.getDamageForDisplay(finalPower) / 25.0f) {
                ServerEvents.addBreaker(this.getPackage().world, pos, this.getPackage().world.getBlockState(pos), (EntityPlayer)this.getPackage().getCaster(), false, false, 0, 1.0f, 0.0f, 1.0f, num, 0.1f, null);
            }
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
        pp.grav = 0.4f;
        pp.layer = 1;
        pp.age = 20 + world.rand.nextInt(10);
        pp.alpha = new float[] { 1.0f, 0.0f };
        pp.partStart = 75 + world.rand.nextInt(4);
        pp.partInc = 1;
        pp.partNum = 1;
        pp.slowDown = 0.9;
        pp.rot = (float)world.rand.nextGaussian();
        final float s = (float)(1.0 + world.rand.nextGaussian() * 0.20000000298023224);
        pp.scale = new float[] { s, s / 2.0f };
        FXDispatcher.INSTANCE.drawGenericParticles(posX, posY, posZ, motionX, motionY, motionZ, pp);
    }
    
    @Override
    public void onCast(final Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundEvents.ENTITY_ENDERDRAGON_FIREBALL_EPLD, SoundCategory.PLAYERS, 0.25f, 1.0f + (float)(caster.world.rand.nextGaussian() * 0.05000000074505806));
    }
}
