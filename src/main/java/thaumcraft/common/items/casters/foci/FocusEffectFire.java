package thaumcraft.common.items.casters.foci;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
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
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;


public class FocusEffectFire extends FocusEffect
{
    @Override
    public String getResearch() {
        return "BASEAUROMANCY";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.FIRE";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.FIRE;
    }
    
    @Override
    public int getComplexity() {
        return getSettingValue("duration") + getSettingValue("power") * 2;
    }
    
    @Override
    public float getDamageForDisplay(float finalPower) {
        return (3 + getSettingValue("power")) * finalPower;
    }
    
    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(target.hitVec.x, target.hitVec.y, target.hitVec.z, new String[] { getKey() }), new NetworkRegistry.TargetPoint(getPackage().world.provider.getDimension(), target.hitVec.x, target.hitVec.y, target.hitVec.z, 64.0));
        if (target.typeOfHit != RayTraceResult.Type.ENTITY || target.entityHit == null) {
            if (target.typeOfHit == RayTraceResult.Type.BLOCK && getSettingValue("duration") > 0) {
                BlockPos pos = target.getBlockPos();
                pos = pos.offset(target.sideHit);
                if (getPackage().world.isAirBlock(pos) && getPackage().world.rand.nextFloat() < finalPower) {
                    getPackage().world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, getPackage().world.rand.nextFloat() * 0.4f + 0.8f);
                    getPackage().world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
                    return true;
                }
            }
            return false;
        }
        if (target.entityHit.isImmuneToFire()) {
            return false;
        }
        float fire = (float)(1 + getSettingValue("duration") * getSettingValue("duration"));
        float damage = getDamageForDisplay(finalPower);
        fire *= finalPower;
        target.entityHit.attackEntityFrom(new EntityDamageSourceIndirect("fireball", (target.entityHit != null) ? target.entityHit : getPackage().getCaster(), getPackage().getCaster()).setFireDamage(), damage);
        if (fire > 0.0f) {
            target.entityHit.setFire(Math.round(fire));
        }
        return true;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)), new NodeSetting("duration", "focus.fire.burn", new NodeSetting.NodeSettingIntRange(0, 5)) };
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderParticleFX(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        FXDispatcher.GenPart pp = new FXDispatcher.GenPart();
        pp.grav = -0.2f;
        pp.age = 10;
        pp.alpha = new float[] { 0.7f };
        pp.partStart = 640;
        pp.partInc = 1;
        pp.partNum = 10;
        pp.slowDown = 0.75;
        pp.scale = new float[] { (float)(1.5 + world.rand.nextGaussian() * 0.20000000298023224) };
        FXDispatcher.INSTANCE.drawGenericParticles(posX, posY, posZ, motionX, motionY, motionZ, pp);
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f + (float)(caster.world.rand.nextGaussian() * 0.05000000074505806));
    }
}
