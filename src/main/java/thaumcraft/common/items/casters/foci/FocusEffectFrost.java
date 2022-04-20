// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.particle.Particle;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import net.minecraft.world.World;
import thaumcraft.api.casters.NodeSetting;
import net.minecraft.block.state.IBlockState;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.init.Blocks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.EntityLivingBase;
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

public class FocusEffectFrost extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSELEMENTAL";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.FROST";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.COLD;
    }
    
    @Override
    public int getComplexity() {
        return this.getSettingValue("duration") + this.getSettingValue("power") * 2;
    }
    
    @Override
    public float getDamageForDisplay(final float finalPower) {
        return (3 + this.getSettingValue("power")) * finalPower;
    }
    
    @Override
    public boolean execute(final RayTraceResult target, final Trajectory trajectory, final float finalPower, final int num) {
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(target.hitVec.x, target.hitVec.y, target.hitVec.z, new String[] { this.getKey() }), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(), target.hitVec.x, target.hitVec.y, target.hitVec.z, 64.0));
        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit != null) {
            final float damage = this.getDamageForDisplay(finalPower);
            final int duration = 20 * this.getSettingValue("duration");
            final int potency = (int)(1.0f + this.getSettingValue("power") * finalPower / 3.0f);
            target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage((target.entityHit != null) ? target.entityHit : this.getPackage().getCaster(), this.getPackage().getCaster()), damage);
            if (target.entityHit instanceof EntityLivingBase) {
                ((EntityLivingBase)target.entityHit).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, duration, potency));
            }
        }
        else if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            final float f = Math.min(16.0f, 2 * this.getSettingValue("power") * finalPower);
            for (final BlockPos.MutableBlockPos blockpos$mutableblockpos1 : BlockPos.getAllInBoxMutable(target.getBlockPos().add(-f, -f, -f), target.getBlockPos().add(f, f, f))) {
                if (blockpos$mutableblockpos1.distanceSqToCenter(target.hitVec.x, target.hitVec.y, target.hitVec.z) <= f * f) {
                    final IBlockState iblockstate1 = this.getPackage().world.getBlockState(blockpos$mutableblockpos1);
                    if (iblockstate1.getMaterial() != Material.WATER || (int)iblockstate1.getValue((IProperty)BlockLiquid.LEVEL) != 0 || !this.getPackage().world.mayPlace(Blocks.FROSTED_ICE, blockpos$mutableblockpos1, false, EnumFacing.DOWN, null)) {
                        continue;
                    }
                    this.getPackage().world.setBlockState(blockpos$mutableblockpos1, Blocks.FROSTED_ICE.getDefaultState());
                    this.getPackage().world.scheduleUpdate(blockpos$mutableblockpos1.toImmutable(), Blocks.FROSTED_ICE, MathHelper.getInt(this.getPackage().world.rand, 60, 120));
                }
            }
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)), new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(2, 10)) };
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderParticleFX(final World world, final double posX, final double posY, final double posZ, final double motionX, final double motionY, final double motionZ) {
        final FXGeneric fb = new FXGeneric(world, posX, posY, posZ, motionX, motionY, motionZ);
        fb.setMaxAge(40 + world.rand.nextInt(40));
        fb.setAlphaF(1.0f, 0.0f);
        fb.setParticles(8, 1, 1);
        fb.setGravity(0.033f);
        fb.setSlowDown(0.8);
        fb.setRandomMovementScale(0.0025f, 1.0E-4f, 0.0025f);
        fb.setScale((float)(0.699999988079071 + world.rand.nextGaussian() * 0.30000001192092896));
        fb.setRotationSpeed(world.rand.nextFloat() * 3.0f, (float)world.rand.nextGaussian() / 4.0f);
        ParticleEngine.addEffectWithDelay(world, fb, 0);
    }
    
    @Override
    public void onCast(final Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 0.2f, 1.0f + (float)(caster.world.rand.nextGaussian() * 0.05000000074505806));
    }
}