package thaumcraft.common.items.casters.foci;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.IFocusBlockPicker;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.lib.events.ServerEvents;


public class FocusEffectExchange extends FocusEffect implements IFocusBlockPicker
{
    @Override
    public String getResearch() {
        return "FOCUSEXCHANGE";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.EXCHANGE";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.EXCHANGE;
    }
    
    @Override
    public int getComplexity() {
        return (5 + getSettingValue("silk") * 4 + getSettingValue("fortune") == 0) ? 0 : ((getSettingValue("fortune") + 1) * 3);
    }
    
    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        if (target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return false;
        }
        ItemStack casterStack = ItemStack.EMPTY;
        if (getPackage().getCaster().getHeldItemMainhand() != null && getPackage().getCaster().getHeldItemMainhand().getItem() instanceof ItemCaster) {
            casterStack = getPackage().getCaster().getHeldItemMainhand();
        }
        else if (getPackage().getCaster().getHeldItemOffhand() != null && getPackage().getCaster().getHeldItemOffhand().getItem() instanceof ItemCaster) {
            casterStack = getPackage().getCaster().getHeldItemOffhand();
        }
        if (casterStack.isEmpty()) {
            return false;
        }
        boolean silk = getSettingValue("silk") > 0;
        int fortune = getSettingValue("fortune");
        if (getPackage().getCaster() instanceof EntityPlayer && ((ItemCaster)casterStack.getItem()).getPickedBlock(casterStack) != null && !((ItemCaster)casterStack.getItem()).getPickedBlock(casterStack).isEmpty()) {
            ServerEvents.addSwapper(getPackage().world, target.getBlockPos(), getPackage().world.getBlockState(target.getBlockPos()), ((ItemCaster)casterStack.getItem()).getPickedBlock(casterStack), true, 0, (EntityPlayer) getPackage().getCaster(), true, false, 8038177, true, silk, fortune, ServerEvents.DEFAULT_PREDICATE, 0.25f + (silk ? 0.25f : 0.0f) + fortune * 0.1f);
        }
        return true;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        int[] silk = { 0, 1 };
        String[] silkDesc = { "focus.common.no", "focus.common.yes" };
        int[] fortune = { 0, 1, 2, 3, 4 };
        String[] fortuneDesc = { "focus.common.no", "I", "II", "III", "IV" };
        return new NodeSetting[] { new NodeSetting("fortune", "focus.common.fortune", new NodeSetting.NodeSettingIntList(fortune, fortuneDesc)), new NodeSetting("silk", "focus.common.silk", new NodeSetting.NodeSettingIntList(silk, silkDesc)) };
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        FXGeneric fb = new FXGeneric(world, x, y, z, vx + world.rand.nextGaussian() * 0.01, vy + world.rand.nextGaussian() * 0.01, vz + world.rand.nextGaussian() * 0.01);
        fb.setMaxAge(9);
        fb.setRBGColorF(0.25f + world.rand.nextFloat() * 0.25f, 0.25f + world.rand.nextFloat() * 0.25f, 0.25f + world.rand.nextFloat() * 0.25f);
        fb.setAlphaF(0.0f, 0.6f, 0.6f, 0.0f);
        fb.setGridSize(64);
        fb.setParticles(448, 9, 1);
        fb.setScale(0.5f, 0.25f);
        fb.setGravity((float)(world.rand.nextGaussian() * 0.009999999776482582));
        fb.setRandomMovementScale(0.0025f, 0.0025f, 0.0025f);
        ParticleEngine.addEffect(world, fb);
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 0.2f, 2.0f + (float)(caster.world.rand.nextGaussian() * 0.05000000074505806));
    }
}
