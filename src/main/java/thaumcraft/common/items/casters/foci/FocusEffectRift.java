package thaumcraft.common.items.casters.foci;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.tiles.misc.TileHole;


public class FocusEffectRift extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSRIFT";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.RIFT";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.ELDRITCH;
    }
    
    @Override
    public int getComplexity() {
        return 3 + getSettingValue("duration") / 2 + getSettingValue("depth") / 4;
    }
    
    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        if (target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return false;
        }
        if (getPackage().world.provider.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId) {
            getPackage().world.playSound(null, target.getBlockPos().getX() + 0.5, target.getBlockPos().getY() + 0.5, target.getBlockPos().getZ() + 0.5, SoundsTC.wandfail, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return false;
        }
        float maxdis = getSettingValue("depth") * finalPower;
        int dur = 20 * getSettingValue("duration");
        int distance = 0;
        BlockPos pos = new BlockPos(target.getBlockPos());
        for (distance = 0; distance < maxdis; ++distance) {
            IBlockState bi = getPackage().world.getBlockState(pos);
            if (BlockUtils.isPortableHoleBlackListed(bi) || bi.getBlock() == Blocks.BEDROCK || bi.getBlock() == BlocksTC.hole || bi.getBlock().isAir(bi, getPackage().world, pos)) {
                break;
            }
            if (bi.getBlockHardness(getPackage().world, pos) == -1.0f) {
                break;
            }
            pos = pos.offset(target.sideHit.getOpposite());
        }
        createHole(getPackage().world, target.getBlockPos(), target.sideHit, (byte)Math.round((float)(distance + 1)), dur);
        return true;
    }
    
    public static boolean createHole(World world, BlockPos pos, EnumFacing side, byte count, int max) {
        IBlockState bs = world.getBlockState(pos);
        if (!world.isRemote && world.getTileEntity(pos) == null && !BlockUtils.isPortableHoleBlackListed(bs) && bs.getBlock() != Blocks.BEDROCK && bs.getBlock() != BlocksTC.hole && (bs.getBlock().isAir(bs, world, pos) || !bs.getBlock().canPlaceBlockAt(world, pos)) && bs.getBlockHardness(world, pos) != -1.0f) {
            if (world.setBlockState(pos, BlocksTC.hole.getDefaultState())) {
                TileHole ts = (TileHole)world.getTileEntity(pos);
                ts.oldblock = bs;
                ts.countdownmax = (short)max;
                ts.count = count;
                ts.direction = side;
                ts.markDirty();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        int[] depth = { 8, 16, 24, 32 };
        String[] depthDesc = { "8", "16", "24", "32" };
        return new NodeSetting[] { new NodeSetting("depth", "focus.rift.depth", new NodeSetting.NodeSettingIntList(depth, depthDesc)), new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(2, 10)) };
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderParticleFX(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        FXGeneric fb = new FXGeneric(world, posX, posY, posZ, motionX, motionY, motionZ);
        fb.setMaxAge(16 + world.rand.nextInt(16));
        fb.setParticles(384 + world.rand.nextInt(16), 1, 1);
        fb.setSlowDown(0.75);
        fb.setAlphaF(1.0f, 0.0f);
        fb.setScale((float)(0.699999988079071 + world.rand.nextGaussian() * 0.30000001192092896));
        fb.setRBGColorF(0.25f, 0.25f, 1.0f);
        fb.setRandomMovementScale(0.01f, 0.01f, 0.01f);
        ParticleEngine.addEffectWithDelay(world, fb, 0);
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 0.2f, 0.7f);
    }
}
