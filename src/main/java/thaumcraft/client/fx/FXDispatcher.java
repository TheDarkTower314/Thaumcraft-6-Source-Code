package thaumcraft.client.fx;
import java.awt.Color;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleLava;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.beams.FXArc;
import thaumcraft.client.fx.beams.FXBeamBore;
import thaumcraft.client.fx.beams.FXBeamWand;
import thaumcraft.client.fx.beams.FXBolt;
import thaumcraft.client.fx.other.FXBlockWard;
import thaumcraft.client.fx.other.FXBoreStream;
import thaumcraft.client.fx.other.FXEssentiaStream;
import thaumcraft.client.fx.other.FXShieldRunes;
import thaumcraft.client.fx.other.FXVoidStream;
import thaumcraft.client.fx.particles.FXBlockRunes;
import thaumcraft.client.fx.particles.FXBoreParticles;
import thaumcraft.client.fx.particles.FXBoreSparkle;
import thaumcraft.client.fx.particles.FXBreakingFade;
import thaumcraft.client.fx.particles.FXFireMote;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.client.fx.particles.FXGenericGui;
import thaumcraft.client.fx.particles.FXGenericP2E;
import thaumcraft.client.fx.particles.FXPlane;
import thaumcraft.client.fx.particles.FXSmokeSpiral;
import thaumcraft.client.fx.particles.FXSwarm;
import thaumcraft.client.fx.particles.FXVent2;
import thaumcraft.client.fx.particles.FXVent;
import thaumcraft.client.fx.particles.FXVisSparkle;
import thaumcraft.client.fx.particles.FXWispEG;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.crafting.TileCrucible;


public class FXDispatcher
{
    public static FXDispatcher INSTANCE;
    static int q;
    
    public World getWorld() {
        return FMLClientHandler.instance().getClient().world;
    }
    
    public void drawFireMote(float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float alpha, float scale) {
        boolean bb = getWorld().rand.nextBoolean();
        FXFireMote glow = new FXFireMote(getWorld(), x, y, z, vx, vy, vz, r, g, b, bb ? (scale / 3.0f) : scale, bb ? 1 : 0);
        glow.setAlphaF(alpha);
        ParticleEngine.addEffect(getWorld(), glow);
    }
    
    public void drawAlumentum(float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float alpha, float scale) {
        FXFireMote glow = new FXFireMote(getWorld(), x, y, z, vx, vy, vz, r, g, b, scale, 1);
        glow.setAlphaF(alpha);
        ParticleEngine.addEffect(getWorld(), glow);
    }
    
    public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, vx, vy, vz);
        fb.setMaxAge(80 + getWorld().rand.nextInt(20));
        fb.setRBGColorF(0.4f + getWorld().rand.nextFloat() * 0.2f, 0.1f + getWorld().rand.nextFloat() * 0.3f, 0.5f + getWorld().rand.nextFloat() * 0.2f);
        fb.setAlphaF(0.75f, 0.0f);
        fb.setGridSize(16);
        fb.setParticles(57 + getWorld().rand.nextInt(3), 1, 1);
        fb.setScale(scale, scale / 4.0f);
        fb.setLayer(1);
        fb.setSlowDown(0.9750000238418579);
        fb.setGravity(0.2f);
        fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawLightningFlash(double x, double y, double z, float r, float g, float b, float alpha, float scale) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);
        fb.setMaxAge(5 + getWorld().rand.nextInt(5));
        fb.setGridSize(16);
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(alpha, 0.0f);
        fb.setParticles(108 + getWorld().rand.nextInt(4), 1, 1);
        fb.setScale(scale);
        fb.setLayer(0);
        fb.setRotationSpeed(getWorld().rand.nextFloat(), 0.0f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, mx, my, mz);
        fb.setMaxAge(part.age);
        fb.setRBGColorF(part.redStart, part.greenStart, part.blueStart, part.redEnd, part.greenEnd, part.blueEnd);
        fb.setAlphaF(part.alpha);
        fb.setLoop(part.loop);
        fb.setParticles(part.partStart, part.partNum, part.partInc);
        fb.setScale(part.scale);
        fb.setLayer(part.layer);
        fb.setRotationSpeed(part.rotstart, part.rot);
        fb.setSlowDown(part.slowDown);
        fb.setGravity(part.grav);
        fb.setGridSize(part.grid);
        ParticleEngine.addEffectWithDelay(getWorld(), fb, part.delay);
    }
    
    public void drawGenericParticles(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
        fb.setMaxAge(age);
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(alpha);
        fb.setLoop(loop);
        fb.setParticles(start, num, inc);
        fb.setScale(scale);
        fb.setLayer(layer);
        fb.setRotationSpeed(rot);
        ParticleEngine.addEffectWithDelay(getWorld(), fb, delay);
    }
    
    public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
        fb.setGridSize(16);
        fb.setMaxAge(age);
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(alpha);
        fb.setLoop(loop);
        fb.setParticles(start, num, inc);
        fb.setScale(scale);
        fb.setLayer(layer);
        fb.setRotationSpeed(rot);
        ParticleEngine.addEffectWithDelay(getWorld(), fb, delay);
    }
    
    public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
        fb.setMaxAge(200 + getWorld().rand.nextInt(100));
        fb.setRBGColorF(0.5f, 0.5f, 0.2f);
        fb.setAlphaF(0.3f, 0.0f);
        fb.setGridSize(16);
        fb.setParticles(56, 1, 1);
        fb.setScale(2.0f, 5.0f);
        fb.setLayer(0);
        fb.setSlowDown(1.0);
        fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
        fb.setMaxAge(life + getWorld().rand.nextInt(life));
        fb.setRBGColorF(0.5f, 0.2f, 0.5f);
        fb.setAlphaF(0.3f, 0.0f);
        fb.setGridSize(16);
        fb.setParticles(72 + getWorld().rand.nextInt(4), 1, 1);
        fb.setScale(1.0f, 10.0f);
        fb.setLayer(0);
        fb.setSlowDown(1.01);
        fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) {
        try {
            FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
            fb.setMaxAge(20 + getWorld().rand.nextInt(5));
            fb.setAlphaF(0.3f, 0.0f);
            fb.setGridSize(16);
            fb.setParticles(56, 1, 1);
            fb.setScale(1.5f, 3.0f, 8.0f);
            fb.setLayer(0);
            fb.setSlowDown(1.0);
            fb.setWind(0.001);
            fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);
            ParticleEngine.addEffect(getWorld(), fb);
        }
        catch (Exception ex) {}
    }
    
    public void drawPollutionParticles(BlockPos p) {
        float x = p.getX() + 0.2f + getWorld().rand.nextFloat() * 0.6f;
        float y = p.getY() + 0.2f + getWorld().rand.nextFloat() * 0.6f;
        float z = p.getZ() + 0.2f + getWorld().rand.nextFloat() * 0.6f;
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005, 0.02, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005);
        fb.setMaxAge(100 + getWorld().rand.nextInt(60));
        fb.setRBGColorF(1.0f, 0.3f, 0.9f);
        fb.setAlphaF(0.5f, 0.0f);
        fb.setGridSize(16);
        fb.setParticles(56, 1, 1);
        fb.setScale(2.0f, 5.0f);
        fb.setLayer(1);
        fb.setSlowDown(1.0);
        fb.setWind(0.001);
        fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawBlockSparkles(BlockPos p, Vec3d start) {
        AxisAlignedBB bs = getWorld().getBlockState(p).getBoundingBox(getWorld(), p);
        bs.grow(0.1, 0.1, 0.1);
        int num = (int)(bs.getAverageEdgeLength() * 20.0);
        for (EnumFacing face : EnumFacing.values()) {
            IBlockState state = getWorld().getBlockState(p.offset(face));
            if (!state.isOpaqueCube()) {
                if (!state.isSideSolid(getWorld(), p.offset(face), face.getOpposite())) {
                    boolean rx = face.getFrontOffsetX() == 0;
                    boolean ry = face.getFrontOffsetY() == 0;
                    boolean rz = face.getFrontOffsetZ() == 0;
                    double mx = 0.5 + face.getFrontOffsetX() * 0.51;
                    double my = 0.5 + face.getFrontOffsetY() * 0.51;
                    double mz = 0.5 + face.getFrontOffsetZ() * 0.51;
                    for (int a = 0; a < num * 2; ++a) {
                        double x = mx;
                        double y = my;
                        double z = mz;
                        if (rx) {
                            x += getWorld().rand.nextGaussian() * 0.6;
                        }
                        if (ry) {
                            y += getWorld().rand.nextGaussian() * 0.6;
                        }
                        if (rz) {
                            z += getWorld().rand.nextGaussian() * 0.6;
                        }
                        x = MathHelper.clamp(x, bs.minX, bs.maxX);
                        y = MathHelper.clamp(y, bs.minY, bs.maxY);
                        z = MathHelper.clamp(z, bs.minZ, bs.maxZ);
                        float r = MathHelper.getInt(getWorld().rand, 255, 255) / 255.0f;
                        float g = MathHelper.getInt(getWorld().rand, 189, 255) / 255.0f;
                        float b = MathHelper.getInt(getWorld().rand, 64, 255) / 255.0f;
                        Vec3d v1 = new Vec3d(p.getX() + x, p.getY() + y, p.getZ() + z);
                        double delay = getWorld().rand.nextInt(5) + v1.distanceTo(start) * 16.0;
                        drawSimpleSparkle(getWorld().rand, p.getX() + x, p.getY() + y, p.getZ() + z, 0.0, 0.0025, 0.0, 0.4f + (float) getWorld().rand.nextGaussian() * 0.1f, r, g, b, (int)delay, 1.0f, 0.01f, 16);
                    }
                }
            }
        }
    }
    
    public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) {
        boolean sp = rand.nextFloat() < 0.2;
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
        int age = baseAge * 4 + getWorld().rand.nextInt(baseAge);
        fb.setMaxAge(age);
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.0f, 1.0f, 0.0f);
        fb.setParticles(sp ? 320 : 512, 16, 1);
        fb.setLoop(true);
        fb.setGravity(grav);
        fb.setScale(scale, scale * 2.0f, scale);
        fb.setLayer(0);
        fb.setSlowDown(decay);
        fb.setRandomMovementScale(5.0E-5f, 0.0f, 5.0E-5f);
        ParticleEngine.addEffectWithDelay(getWorld(), fb, delay);
    }
    
    public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) {
        boolean sp = rand.nextFloat() < 0.2;
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
        int age = baseAge * 4 + getWorld().rand.nextInt(baseAge);
        fb.setMaxAge(age);
        fb.setRBGColorF(r, g, b);
        float[] alphas = new float[6 + rand.nextInt(age / 3)];
        for (int a = 1; a < alphas.length - 1; ++a) {
            alphas[a] = rand.nextFloat();
        }
        fb.setAlphaF(alphas);
        fb.setParticles(sp ? 320 : 512, 16, 1);
        fb.setLoop(true);
        fb.setGravity(grav);
        fb.setScale(scale, scale * 2.0f);
        fb.setLayer(0);
        fb.setSlowDown(decay);
        fb.setRandomMovementScale(5.0E-4f, 0.001f, 5.0E-4f);
        fb.setWind(5.0E-4);
        ParticleEngine.addEffectWithDelay(getWorld(), fb, delay);
    }
    
    public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) {
        boolean sp = rand.nextFloat() < 0.2;
        FXGenericGui fb = new FXGenericGui(getWorld(), x, y, 0.0, x2, y2, 0.0);
        fb.setMaxAge(32 + getWorld().rand.nextInt(8));
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f);
        fb.setParticles(sp ? 320 : 512, 16, 1);
        fb.setLoop(true);
        fb.setGravity(grav);
        fb.setScale(scale, scale * 2.0f);
        fb.setNoClip(false);
        fb.setLayer(4);
        fb.setSlowDown(decay);
        fb.setRandomMovementScale(0.025f, 0.025f, 0.0f);
        ParticleEngine.addEffectWithDelay(getWorld(), fb, delay);
    }
    
    public void drawBlockMistParticles(BlockPos p, int c) {
        AxisAlignedBB bs = getWorld().getBlockState(p).getBoundingBox(getWorld(), p);
        Color color = new Color(c);
        for (int a = 0; a < 8; ++a) {
            double x = p.getX() + bs.minX + getWorld().rand.nextFloat() * (bs.maxX - bs.minX);
            double y = p.getY() + bs.minY + getWorld().rand.nextFloat() * (bs.maxY - bs.minY);
            double z = p.getZ() + bs.minZ + getWorld().rand.nextFloat() * (bs.maxZ - bs.minZ);
            FXGeneric fb = new FXGeneric(getWorld(), x, y, z, getWorld().rand.nextGaussian() * 0.01, getWorld().rand.nextFloat() * 0.075, getWorld().rand.nextGaussian() * 0.01);
            fb.setMaxAge(50 + getWorld().rand.nextInt(25));
            fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
            fb.setAlphaF(0.0f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.0f);
            fb.setGridSize(16);
            fb.setParticles(56, 1, 1);
            fb.setScale(5.0f, 1.0f);
            fb.setLayer(0);
            fb.setSlowDown(1.0);
            fb.setGravity(0.1f);
            fb.setWind(0.001);
            fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);
            ParticleEngine.addEffect(getWorld(), fb);
        }
    }
    
    public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) {
        Color color = new Color(c);
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, mx, my, mz);
        fb.setMaxAge(20 + getWorld().rand.nextInt(10));
        fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        fb.setAlphaF(0.0f, 0.66f, 0.0f);
        fb.setGridSize(16);
        fb.setParticles(56 + getWorld().rand.nextInt(4), 1, 1);
        fb.setScale(5.0f + getWorld().rand.nextFloat(), 10.0f + getWorld().rand.nextFloat());
        fb.setLayer(0);
        fb.setSlowDown(0.99);
        fb.setWind(0.001);
        fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -0.25f : 0.25f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawWispyMotesOnBlock(BlockPos pp, int age, float grav) {
        drawWispyMotes(pp.getX() + getWorld().rand.nextFloat(), pp.getY(), pp.getZ() + getWorld().rand.nextFloat(), 0.0, 0.0, 0.0, age, 0.4f + getWorld().rand.nextFloat() * 0.6f, 0.6f + getWorld().rand.nextFloat() * 0.4f, 0.6f + getWorld().rand.nextFloat() * 0.4f, grav);
    }
    
    public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) {
        drawWispyMotes(d, e, f, vx, vy, vz, age, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, grav);
    }
    
    public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) {
        FXGeneric fb = new FXGeneric(getWorld(), d, e, f, vx, vy, vz);
        fb.setMaxAge((int)(age + age / 2 * getWorld().rand.nextFloat()));
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.0f, 0.6f, 0.6f, 0.0f);
        fb.setGridSize(64);
        fb.setParticles(512, 16, 1);
        fb.setScale(1.0f, 0.5f);
        fb.setLoop(true);
        fb.setWind(0.001);
        fb.setGravity(grav);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawBlockMistParticlesFlat(BlockPos p, int c) {
        Block bs = getWorld().getBlockState(p).getBlock();
        Color color = new Color(c);
        for (int a = 0; a < 6; ++a) {
            double x = p.getX() + getWorld().rand.nextFloat();
            double y = p.getY() + getWorld().rand.nextFloat() * 0.125f;
            double z = p.getZ() + getWorld().rand.nextFloat();
            FXGeneric fb = new FXGeneric(getWorld(), x, y, z, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005, 0.005, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005);
            fb.setMaxAge(400 + getWorld().rand.nextInt(100));
            fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
            fb.setAlphaF(1.0f, 0.0f);
            fb.setGridSize(8);
            fb.setParticles(24, 1, 1);
            fb.setScale(2.0f, 5.0f);
            fb.setLayer(0);
            fb.setSlowDown(1.0);
            fb.setWind(0.001);
            fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);
            ParticleEngine.addEffect(getWorld(), fb);
        }
    }
    
    public void crucibleBubble(float x, float y, float z, float cr, float cg, float cb) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);
        fb.setMaxAge(15 + getWorld().rand.nextInt(10));
        fb.setScale(getWorld().rand.nextFloat() * 0.3f + 0.3f);
        fb.setRBGColorF(cr, cg, cb);
        fb.setRandomMovementScale(0.002f, 0.002f, 0.002f);
        fb.setGravity(-0.001f);
        fb.setParticle(64);
        fb.setFinalFrames(65, 66, 66);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void crucibleBoil(BlockPos pos, TileCrucible tile, int j) {
        for (int a = 0; a < 2; ++a) {
            FXGeneric fb = new FXGeneric(getWorld(), pos.getX() + 0.2f + getWorld().rand.nextFloat() * 0.6f, pos.getY() + 0.1f + tile.getFluidHeight(), pos.getZ() + 0.2f + getWorld().rand.nextFloat() * 0.6f, 0.0, 0.002, 0.0);
            fb.setMaxAge((int)(7.0 + 8.0 / (Math.random() * 0.8 + 0.2)));
            fb.setScale(getWorld().rand.nextFloat() * 0.3f + 0.2f);
            if (tile.aspects.size() == 0) {
                fb.setRBGColorF(1.0f, 1.0f, 1.0f);
            }
            else {
                Color color = new Color(tile.aspects.getAspects()[getWorld().rand.nextInt(tile.aspects.getAspects().length)].getColor());
                fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
            }
            fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
            fb.setGravity(-0.025f * j);
            fb.setParticle(64);
            fb.setFinalFrames(65, 66);
            ParticleEngine.addEffect(getWorld(), fb);
        }
    }
    
    public void crucibleFroth(float x, float y, float z) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);
        fb.setMaxAge(4 + getWorld().rand.nextInt(3));
        fb.setScale(getWorld().rand.nextFloat() * 0.2f + 0.2f);
        fb.setRBGColorF(0.5f, 0.5f, 0.7f);
        fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
        fb.setGravity(0.1f);
        fb.setParticle(64);
        fb.setFinalFrames(65, 66);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void crucibleFrothDown(float x, float y, float z) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);
        fb.setMaxAge(12 + getWorld().rand.nextInt(12));
        fb.setScale(getWorld().rand.nextFloat() * 0.2f + 0.4f);
        fb.setRBGColorF(0.25f, 0.0f, 0.75f);
        fb.setAlphaF(0.8f);
        fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
        fb.setGravity(0.05f);
        fb.setNoClip(false);
        fb.setParticle(73);
        fb.setFinalFrames(65, 66);
        fb.setLayer(1);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawBamf(BlockPos p, boolean sound, boolean flair, EnumFacing side) {
        drawBamf(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, sound, flair, side);
    }
    
    public void drawPedestalShield(BlockPos pos) {
        FXShieldRunes fb = new FXShieldRunes(getWorld(), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, null, 8, 0.0f, 90.0f);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb);
    }
    
    public void drawBamf(BlockPos p, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) {
        drawBamf(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, r, g, b, sound, flair, side);
    }
    
    public void drawBamf(BlockPos p, int color, boolean sound, boolean flair, EnumFacing side) {
        drawBamf(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, color, sound, flair, side);
    }
    
    public void drawBamf(double x, double y, double z, int color, boolean sound, boolean flair, EnumFacing side) {
        Color c = new Color(color);
        float r = c.getRed() / 255.0f;
        float g = c.getGreen() / 255.0f;
        float b = c.getBlue() / 255.0f;
        drawBamf(x, y, z, r, g, b, sound, flair, side);
    }
    
    public void drawBamf(double x, double y, double z, boolean sound, boolean flair, EnumFacing side) {
        drawBamf(x, y, z, 0.5f, 0.1f, 0.6f, sound, flair, side);
    }
    
    public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) {
        if (sound) {
            getWorld().playSound(x, y, z, SoundsTC.poof, SoundCategory.BLOCKS, 0.4f, 1.0f + (float) getWorld().rand.nextGaussian() * 0.05f, false);
        }
        for (int a = 0; a < 6 + getWorld().rand.nextInt(3) + 2; ++a) {
            double vx = (0.05f + getWorld().rand.nextFloat() * 0.05f) * (getWorld().rand.nextBoolean() ? -1 : 1);
            double vy = (0.05f + getWorld().rand.nextFloat() * 0.05f) * (getWorld().rand.nextBoolean() ? -1 : 1);
            double vz = (0.05f + getWorld().rand.nextFloat() * 0.05f) * (getWorld().rand.nextBoolean() ? -1 : 1);
            if (side != null) {
                vx += side.getFrontOffsetX() * 0.1f;
                vy += side.getFrontOffsetY() * 0.1f;
                vz += side.getFrontOffsetZ() * 0.1f;
            }
            FXGeneric fb2 = new FXGeneric(getWorld(), x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx / 2.0, vy / 2.0, vz / 2.0);
            fb2.setMaxAge(20 + getWorld().rand.nextInt(15));
            fb2.setRBGColorF(MathHelper.clamp(r * (1.0f + (float) getWorld().rand.nextGaussian() * 0.1f), 0.0f, 1.0f), MathHelper.clamp(g * (1.0f + (float) getWorld().rand.nextGaussian() * 0.1f), 0.0f, 1.0f), MathHelper.clamp(b * (1.0f + (float) getWorld().rand.nextGaussian() * 0.1f), 0.0f, 1.0f));
            fb2.setAlphaF(1.0f, 0.1f);
            fb2.setGridSize(16);
            fb2.setParticles(123, 5, 1);
            fb2.setScale(3.0f, 4.0f + getWorld().rand.nextFloat() * 3.0f);
            fb2.setLayer(1);
            fb2.setSlowDown(0.7);
            fb2.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);
            ParticleEngine.addEffect(getWorld(), fb2);
        }
        if (flair) {
            for (int a = 0; a < 2 + getWorld().rand.nextInt(3); ++a) {
                double vx = (0.025f + getWorld().rand.nextFloat() * 0.025f) * (getWorld().rand.nextBoolean() ? -1 : 1);
                double vy = (0.025f + getWorld().rand.nextFloat() * 0.025f) * (getWorld().rand.nextBoolean() ? -1 : 1);
                double vz = (0.025f + getWorld().rand.nextFloat() * 0.025f) * (getWorld().rand.nextBoolean() ? -1 : 1);
                drawWispyMotes(x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx, vy, vz, 15 + getWorld().rand.nextInt(10), -0.01f);
            }
            FXGeneric fb3 = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);
            fb3.setMaxAge(10 + getWorld().rand.nextInt(5));
            fb3.setRBGColorF(1.0f, 0.9f, 1.0f);
            fb3.setAlphaF(1.0f, 0.0f);
            fb3.setGridSize(16);
            fb3.setParticles(77, 1, 1);
            fb3.setScale(10.0f + getWorld().rand.nextFloat() * 2.0f, 0.0f);
            fb3.setLayer(0);
            fb3.setRotationSpeed(getWorld().rand.nextFloat(), (float) getWorld().rand.nextGaussian());
            ParticleEngine.addEffect(getWorld(), fb3);
        }
        for (int a = 0; a < (flair ? 2 : 0) + getWorld().rand.nextInt(3); ++a) {
            drawCurlyWisp(x, y, z, 0.0, 0.0, 0.0, 1.0f, (0.9f + getWorld().rand.nextFloat() * 0.1f + r) / 2.0f, (0.1f + g) / 2.0f, (0.5f + getWorld().rand.nextFloat() * 0.1f + b) / 2.0f, 0.75f, side, a, 0, 0);
        }
    }
    
    public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, EnumFacing side, int seed, int layer, int delay) {
        if (getWorld() == null) {
            return;
        }
        vx += (0.0025f + getWorld().rand.nextFloat() * 0.005f) * (getWorld().rand.nextBoolean() ? -1 : 1);
        vy += (0.0025f + getWorld().rand.nextFloat() * 0.005f) * (getWorld().rand.nextBoolean() ? -1 : 1);
        vz += (0.0025f + getWorld().rand.nextFloat() * 0.005f) * (getWorld().rand.nextBoolean() ? -1 : 1);
        if (side != null) {
            vx += side.getFrontOffsetX() * 0.025f;
            vy += side.getFrontOffsetY() * 0.025f;
            vz += side.getFrontOffsetZ() * 0.025f;
        }
        FXGeneric fb2 = new FXGeneric(getWorld(), x + vx * 5.0, y + vy * 5.0, z + vz * 5.0, vx, vy, vz);
        if (seed > 0 && getWorld().rand.nextBoolean()) {
            fb2.setAngles(90.0f * (float) getWorld().rand.nextGaussian(), 90.0f * (float) getWorld().rand.nextGaussian());
        }
        fb2.setMaxAge(25 + getWorld().rand.nextInt(20 + 20 * seed));
        fb2.setRBGColorF(r, g, b, 0.1f, 0.0f, 0.1f);
        fb2.setAlphaF(a, 0.0f);
        fb2.setGridSize(16);
        fb2.setParticles(60 + getWorld().rand.nextInt(4), 1, 1);
        fb2.setScale(5.0f * scale, (10.0f + getWorld().rand.nextFloat() * 4.0f) * scale);
        fb2.setLayer(layer);
        fb2.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? (-2.0f - getWorld().rand.nextFloat() * 2.0f) : (2.0f + getWorld().rand.nextFloat() * 2.0f));
        ParticleEngine.addEffectWithDelay(getWorld(), fb2, delay);
    }
    
    public void pechsCurseTick(double posX, double posY, double posZ) {
        FXGeneric fb2 = new FXGeneric(getWorld(), posX, posY, posZ, 0.0, 0.0, 0.0);
        fb2.setAngles(90.0f * (float) getWorld().rand.nextGaussian(), 90.0f * (float) getWorld().rand.nextGaussian());
        fb2.setMaxAge(50 + getWorld().rand.nextInt(50));
        fb2.setRBGColorF(0.9f, 0.1f, 0.5f, 0.1f + getWorld().rand.nextFloat() * 0.1f, 0.0f, 0.5f + getWorld().rand.nextFloat() * 0.1f);
        fb2.setAlphaF(0.75f, 0.0f);
        fb2.setGridSize(8);
        fb2.setParticles(28 + getWorld().rand.nextInt(4), 1, 1);
        fb2.setScale(3.0f, 5.0f + getWorld().rand.nextFloat() * 2.0f);
        fb2.setLayer(0);
        fb2.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? (-3.0f - getWorld().rand.nextFloat() * 3.0f) : (3.0f + getWorld().rand.nextFloat() * 3.0f));
        ParticleEngine.addEffect(getWorld(), fb2);
        drawWispyMotes(posX, posY, posZ, 0.0, 0.0, 0.0, 10 + getWorld().rand.nextInt(10), -0.01f);
    }
    
    public void scanHighlight(BlockPos p) {
        AxisAlignedBB bb = getWorld().getBlockState(p).getBoundingBox(getWorld(), p);
        bb = bb.offset(p);
        scanHighlight(bb);
    }
    
    public void scanHighlight(Entity e) {
        AxisAlignedBB bb = e.getEntityBoundingBox();
        scanHighlight(bb);
    }
    
    public void scanHighlight(AxisAlignedBB bb) {
        int num = MathHelper.ceil(bb.getAverageEdgeLength() * 2.0);
        double ax = (bb.minX + bb.maxX) / 2.0;
        double ay = (bb.minY + bb.maxY) / 2.0;
        double az = (bb.minZ + bb.maxZ) / 2.0;
        for (EnumFacing face : EnumFacing.values()) {
            double mx = 0.5 + face.getFrontOffsetX() * 0.51;
            double my = 0.5 + face.getFrontOffsetY() * 0.51;
            double mz = 0.5 + face.getFrontOffsetZ() * 0.51;
            for (int a = 0; a < num * 2; ++a) {
                double x = mx;
                double y = my;
                double z = mz;
                x += getWorld().rand.nextGaussian() * (bb.maxX - bb.minX);
                y += getWorld().rand.nextGaussian() * (bb.maxY - bb.minY);
                z += getWorld().rand.nextGaussian() * (bb.maxZ - bb.minZ);
                x = MathHelper.clamp(x, bb.minX - ax, bb.maxX - ax);
                y = MathHelper.clamp(y, bb.minY - ay, bb.maxY - ay);
                z = MathHelper.clamp(z, bb.minZ - az, bb.maxZ - az);
                float r = MathHelper.getInt(getWorld().rand, 16, 32) / 255.0f;
                float g = MathHelper.getInt(getWorld().rand, 132, 165) / 255.0f;
                float b = MathHelper.getInt(getWorld().rand, 223, 239) / 255.0f;
                drawSimpleSparkle(getWorld().rand, ax + x, ay + y, az + z, 0.0, 0.0, 0.0, 0.4f + (float) getWorld().rand.nextGaussian() * 0.1f, r, g, b, getWorld().rand.nextInt(10), 1.0f, 0.0f, 4);
            }
        }
    }
    
    public void sparkle(float x, float y, float z, float r, float g, float b) {
        if (getWorld().rand.nextInt(6) < 4) {
            drawGenericParticles(x, y, z, 0.0, 0.0, 0.0, r, g, b, 0.9f, true, 320, 16, 1, 6 + getWorld().rand.nextInt(4), 0, 0.6f + getWorld().rand.nextFloat() * 0.2f, 0.0f, 0);
        }
    }
    
    public void visSparkle(int x, int y, int z, int x2, int y2, int z2, int color) {
        FXVisSparkle fb = new FXVisSparkle(getWorld(), x + getWorld().rand.nextFloat(), y + getWorld().rand.nextFloat(), z + getWorld().rand.nextFloat(), x2 + 0.4 + getWorld().rand.nextFloat() * 0.2f, y2 + 0.4 + getWorld().rand.nextFloat() * 0.2f, z2 + 0.4 + getWorld().rand.nextFloat() * 0.2f);
        Color c = new Color(color);
        fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void voidStreak(double x, double y, double z, double x2, double y2, double z2, int seed, float scale) {
        FXVoidStream fb = new FXVoidStream(getWorld(), x, y, z, x2, y2, z2, seed, scale);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void splooshFX(Entity e) {
        float f = getWorld().rand.nextFloat() * 3.1415927f * 2.0f;
        float f2 = getWorld().rand.nextFloat() * 0.5f + 0.5f;
        float f3 = MathHelper.sin(f) * 2.0f * 0.5f * f2;
        float f4 = MathHelper.cos(f) * 2.0f * 0.5f * f2;
        FXBreakingFade fx = new FXBreakingFade(getWorld(), e.posX + f3, e.posY + getWorld().rand.nextFloat() * e.height, e.posZ + f4, Items.SLIME_BALL, 0);
        if (getWorld().rand.nextBoolean()) {
            fx.setRBGColorF(0.6f, 0.0f, 0.3f);
            fx.setAlphaF(0.4f);
        }
        else {
            fx.setRBGColorF(0.3f, 0.0f, 0.3f);
            fx.setAlphaF(0.6f);
        }
        fx.setParticleMaxAge((int)(66.0f / (getWorld().rand.nextFloat() * 0.9f + 0.1f)));
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
    }
    
    public void taintsplosionFX(Entity e) {
        FXBreakingFade fx = new FXBreakingFade(getWorld(), e.posX, e.posY + getWorld().rand.nextFloat() * e.height, e.posZ, Items.SLIME_BALL);
        if (getWorld().rand.nextBoolean()) {
            fx.setRBGColorF(0.6f, 0.0f, 0.3f);
            fx.setAlphaF(0.4f);
        }
        else {
            fx.setRBGColorF(0.3f, 0.0f, 0.3f);
            fx.setAlphaF(0.6f);
        }
        fx.setSpeed(Math.random() * 2.0 - 1.0, Math.random() * 2.0 - 1.0, Math.random() * 2.0 - 1.0);
        fx.boom();
        fx.setParticleMaxAge((int)(66.0f / (getWorld().rand.nextFloat() * 0.9f + 0.1f)));
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
    }
    
    public void tentacleAriseFX(Entity e) {
        for (int j = 0; j < 2.0f * e.height; ++j) {
            float f = getWorld().rand.nextFloat() * 3.1415927f * e.height;
            float f2 = getWorld().rand.nextFloat() * 0.5f + 0.5f;
            float f3 = MathHelper.sin(f) * e.height * 0.25f * f2;
            float f4 = MathHelper.cos(f) * e.height * 0.25f * f2;
            FXBreakingFade fx = new FXBreakingFade(getWorld(), e.posX + f3, e.posY, e.posZ + f4, Items.SLIME_BALL);
            fx.setRBGColorF(0.4f, 0.0f, 0.4f);
            fx.setAlphaF(0.5f);
            fx.setParticleMaxAge((int)(66.0f / (getWorld().rand.nextFloat() * 0.9f + 0.1f)));
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
            if (!getWorld().isAirBlock(e.getPosition().down())) {
                f = getWorld().rand.nextFloat() * 3.1415927f * e.height;
                f2 = getWorld().rand.nextFloat() * 0.5f + 0.5f;
                f3 = MathHelper.sin(f) * e.height * 0.25f * f2;
                f4 = MathHelper.cos(f) * e.height * 0.25f * f2;
                getWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK, e.posX + f3, e.posY, e.posZ + f4, 0.0, 0.0, 0.0, Block.getStateId(getWorld().getBlockState(e.getPosition().down())));
            }
        }
    }
    
    public void slimeJumpFX(Entity e, int i) {
        float f = getWorld().rand.nextFloat() * 3.1415927f * 2.0f;
        float f2 = getWorld().rand.nextFloat() * 0.5f + 0.5f;
        float f3 = MathHelper.sin(f) * i * 0.5f * f2;
        float f4 = MathHelper.cos(f) * i * 0.5f * f2;
        FXBreakingFade fx = new FXBreakingFade(getWorld(), e.posX + f3, (e.getEntityBoundingBox().minY + e.getEntityBoundingBox().maxY) / 2.0, e.posZ + f4, Items.SLIME_BALL, 0);
        fx.setRBGColorF(0.7f, 0.0f, 1.0f);
        fx.setAlphaF(0.4f);
        fx.setParticleMaxAge((int)(66.0f / (getWorld().rand.nextFloat() * 0.9f + 0.1f)));
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
    }
    
    public void taintLandFX(Entity e) {
        float f = getWorld().rand.nextFloat() * 3.1415927f * 2.0f;
        float f2 = getWorld().rand.nextFloat() * 0.5f + 0.5f;
        float f3 = MathHelper.sin(f) * 2.0f * 0.5f * f2;
        float f4 = MathHelper.cos(f) * 2.0f * 0.5f * f2;
        if (getWorld().isRemote) {
            FXBreakingFade fx = new FXBreakingFade(getWorld(), e.posX + f3, (e.getEntityBoundingBox().minY + e.getEntityBoundingBox().maxY) / 2.0, e.posZ + f4, Items.SLIME_BALL);
            fx.setRBGColorF(0.1f, 0.0f, 0.1f);
            fx.setAlphaF(0.4f);
            fx.setParticleMaxAge((int)(66.0f / (getWorld().rand.nextFloat() * 0.9f + 0.1f)));
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
        }
    }
    
    public void drawInfusionParticles1(double x, double y, double z, BlockPos pos, ItemStack stack) {
        FXBoreParticles fb = new FXBoreParticles(getWorld(), x, y, z, pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, (float) getWorld().rand.nextGaussian() * 0.03f, (float) getWorld().rand.nextGaussian() * 0.03f, (float) getWorld().rand.nextGaussian() * 0.03f, stack).getObjectColor(pos);
        fb.setAlphaF(0.3f);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb);
    }
    
    public void drawInfusionParticles2(double x, double y, double z, BlockPos pos, IBlockState id, int md) {
        FXBoreParticles fb = new FXBoreParticles(getWorld(), x, y, z, pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, id, md).getObjectColor(pos);
        fb.setAlphaF(0.3f);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb);
    }
    
    public void drawInfusionParticles3(double x, double y, double z, int x2, int y2, int z2) {
        FXBoreSparkle fb = new FXBoreSparkle(getWorld(), x, y, z, x2 + 0.5, y2 - 0.5, z2 + 0.5);
        fb.setRBGColorF(0.4f + getWorld().rand.nextFloat() * 0.2f, 0.2f, 0.6f + getWorld().rand.nextFloat() * 0.3f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawInfusionParticles4(double x, double y, double z, int x2, int y2, int z2) {
        FXBoreSparkle fb = new FXBoreSparkle(getWorld(), x, y, z, x2 + 0.5, y2 - 0.5, z2 + 0.5);
        fb.setRBGColorF(0.2f, 0.6f + getWorld().rand.nextFloat() * 0.3f, 0.3f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawVentParticles(double x, double y, double z, double x2, double y2, double z2, int color) {
        FXVent fb = new FXVent(getWorld(), x, y, z, x2, y2, z2, color);
        fb.setAlphaF(0.4f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawVentParticles(double x, double y, double z, double x2, double y2, double z2, int color, float scale) {
        FXVent fb = new FXVent(getWorld(), x, y, z, x2, y2, z2, color);
        fb.setAlphaF(0.4f);
        fb.setScale(scale);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawVentParticles2(double x, double y, double z, double x2, double y2, double z2, int color, float scale) {
        FXVent2 fb = new FXVent2(getWorld(), x, y, z, x2, y2, z2, color);
        fb.setAlphaF(0.4f);
        fb.setScale(scale);
        ParticleEngine.addEffect(getWorld(), fb);
        if (getWorld().rand.nextInt(6) < 2) {
            drawGenericParticles(x, y, z, x2 / 2.0, y2 / 2.0, z2 / 2.0, 1.0f, 0.7f, 0.2f, 0.9f, true, 320, 16, 1, 10 + getWorld().rand.nextInt(4), 0, 0.25f + getWorld().rand.nextFloat() * 0.1f, 0.0f, 0);
        }
    }
    
    public void spark(double d, double e, double f, float size, float r, float g, float b, float a) {
        FXGeneric fb = new FXGeneric(getWorld(), d, e, f, 0.0, 0.0, 0.0);
        fb.setMaxAge(5 + getWorld().rand.nextInt(5));
        fb.setAlphaF(a);
        fb.setRBGColorF(r, g, b);
        fb.setGridSize(16);
        fb.setParticles(8 + getWorld().rand.nextInt(3) * 16, 8, 1);
        fb.setScale(size);
        fb.setFlipped(getWorld().rand.nextBoolean());
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void smokeSpiral(double x, double y, double z, float rad, int start, int miny, int color) {
        FXSmokeSpiral fx = new FXSmokeSpiral(getWorld(), x, y, z, rad, start, miny);
        Color c = new Color(color);
        fx.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        ParticleEngine.addEffect(getWorld(), fx);
    }
    
    public void wispFXEG(double posX, double posY, double posZ, Entity target) {
        for (int a = 0; a < 2; ++a) {
            FXWispEG ef = new FXWispEG(getWorld(), posX, posY, posZ, target);
            ParticleEngine.addEffect(getWorld(), ef);
        }
    }
    
    public void burst(double sx, double sy, double sz, float size) {
        FXGeneric fb = new FXGeneric(getWorld(), sx, sy, sz, 0.0, 0.0, 0.0);
        fb.setMaxAge(31);
        fb.setGridSize(16);
        fb.setParticles(208, 31, 1);
        fb.setScale(size);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void excavateFX(BlockPos pos, EntityLivingBase p, int progress) {
        RenderGlobal rg = Minecraft.getMinecraft().renderGlobal;
        rg.sendBlockBreakProgress(p.getEntityId(), pos, progress);
    }
    
    public Object beamCont(EntityLivingBase p, double tx, double ty, double tz, int type, int color, boolean reverse, float endmod, Object input, int impact) {
        FXBeamWand beamcon = null;
        Color c = new Color(color);
        if (input instanceof FXBeamWand) {
            beamcon = (FXBeamWand)input;
        }
        if (beamcon == null || !beamcon.isAlive()) {
            beamcon = new FXBeamWand(getWorld(), p, tx, ty, tz, c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 8);
            beamcon.setType(type);
            beamcon.setEndMod(endmod);
            beamcon.setReverse(reverse);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(beamcon);
        }
        else {
            beamcon.updateBeam(tx, ty, tz);
            beamcon.setEndMod(endmod);
            beamcon.impact = impact;
        }
        return beamcon;
    }
    
    public Object beamBore(double px, double py, double pz, double tx, double ty, double tz, int type, int color, boolean reverse, float endmod, Object input, int impact) {
        FXBeamBore beamcon = null;
        Color c = new Color(color);
        if (input instanceof FXBeamBore) {
            beamcon = (FXBeamBore)input;
        }
        if (beamcon == null || !beamcon.isAlive()) {
            beamcon = new FXBeamBore(getWorld(), px, py, pz, tx, ty, tz, c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 8);
            beamcon.setType(type);
            beamcon.setEndMod(endmod);
            beamcon.setReverse(reverse);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(beamcon);
        }
        else {
            beamcon.updateBeam(px, py, pz, tx, ty, tz);
            beamcon.setEndMod(endmod);
            beamcon.impact = impact;
        }
        return beamcon;
    }
    
    public void boreDigFx(int x, int y, int z, Entity e, IBlockState bi, int md, int delay) {
        float p = 50.0f;
        for (int a = 0; a < p / delay; ++a) {
            if (getWorld().rand.nextInt(4) == 0) {
                FXBoreSparkle fb = new FXBoreSparkle(getWorld(), x + getWorld().rand.nextFloat(), y + getWorld().rand.nextFloat(), z + getWorld().rand.nextFloat(), e);
                ParticleEngine.addEffect(getWorld(), fb);
            }
            else {
                FXBoreParticles fb2 = new FXBoreParticles(getWorld(), x + getWorld().rand.nextFloat(), y + getWorld().rand.nextFloat(), z + getWorld().rand.nextFloat(), e.posX, e.posY, e.posZ, bi, md);
                fb2.setTarget(e);
                FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb2);
            }
        }
    }
    
    public void essentiaTrailFx(BlockPos p1, BlockPos p2, int count, int color, float scale, int ext) {
        FXEssentiaStream fb = new FXEssentiaStream(getWorld(), p1.getX() + 0.5, p1.getY() + 0.5, p1.getZ() + 0.5, p2.getX() + 0.5, p2.getY() + 0.5, p2.getZ() + 0.5, count, color, scale, ext, 0.0);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void boreTrailFx(BlockPos p1, Entity e, int count, int color, float scale, int ext) {
        FXBoreStream fb = new FXBoreStream(getWorld(), p1.getX() + 0.5, p1.getY() + 0.5, p1.getZ() + 0.5, e, count, color, scale, ext, 0.0);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void essentiaDropFx(double x, double y, double z, float r, float g, float b, float alpha) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, getWorld().rand.nextGaussian() * 0.004999999888241291, getWorld().rand.nextGaussian() * 0.004999999888241291, getWorld().rand.nextGaussian() * 0.004999999888241291);
        fb.setMaxAge(20 + getWorld().rand.nextInt(10));
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(alpha);
        fb.setLoop(false);
        fb.setParticles(25, 1, 1);
        fb.setScale(0.4f + getWorld().rand.nextFloat() * 0.2f, 0.2f);
        fb.setLayer(1);
        fb.setGravity(0.01f);
        fb.setRotationSpeed(0.0f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void jarSplashFx(double x, double y, double z) {
        FXGeneric fb = new FXGeneric(getWorld(), x + getWorld().rand.nextGaussian() * 0.07500000298023224, y, z + getWorld().rand.nextGaussian() * 0.07500000298023224, getWorld().rand.nextGaussian() * 0.014999999664723873, 0.075f + getWorld().rand.nextFloat() * 0.05f, getWorld().rand.nextGaussian() * 0.014999999664723873);
        fb.setMaxAge(20 + getWorld().rand.nextInt(10));
        Color c = new Color(2650102);
        fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        fb.setAlphaF(0.5f);
        fb.setLoop(false);
        fb.setParticles(73, 1, 1);
        fb.setScale(0.4f + getWorld().rand.nextFloat() * 0.3f, 0.0f);
        fb.setLayer(1);
        fb.setGravity(0.3f);
        fb.setRotationSpeed(0.0f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void waterTrailFx(BlockPos p1, BlockPos p2, int count, int color, float scale) {
        FXEssentiaStream fb = new FXEssentiaStream(getWorld(), p1.getX() + 0.5, p1.getY() + 0.66, p1.getZ() + 0.5, p2.getX() + 0.5, p2.getY() + 0.5, p2.getZ() + 0.5, count, color, scale, 0, 0.2);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void furnaceLavaFx(int x, int y, int z, int facingX, int facingZ) {
        float qx = (facingX == 0) ? ((getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.5f) : (facingX * getWorld().rand.nextFloat());
        float qz = (facingZ == 0) ? ((getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.5f) : (facingZ * getWorld().rand.nextFloat());
        Particle fb = new ParticleLava.Factory().createParticle(0, getWorld(), x + 0.5f + (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.3f + facingX * 1.0f, y + 0.3f, z + 0.5f + (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.3f + facingZ * 1.0f, 0.15f * qx, 0.2f * getWorld().rand.nextFloat(), 0.15f * qz);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb);
    }
    
    public void blockRunes(double x, double y, double z, float r, float g, float b, int dur, float grav) {
        FXBlockRunes fb = new FXBlockRunes(getWorld(), x + 0.5, y + 0.5, z + 0.5, r, g, b, dur);
        fb.setGravity(grav);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void blockRunes2(double x, double y, double z, float r, float g, float b, int dur, float grav) {
        FXBlockRunes fb = new FXBlockRunes(getWorld(), x + 0.5, y + 0.5, z + 0.5, r, g, b, dur);
        fb.setGravity(grav);
        fb.setScale((float)(0.5 + getWorld().rand.nextGaussian() * 0.10000000149011612));
        fb.setOffsetX(0.0);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawSlash(double x, double y, double z, double x2, double y2, double z2, int dur) {
        FXPlane fb = new FXPlane(getWorld(), x, y, z, x2, y2, z2, dur);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void blockWard(double x, double y, double z, EnumFacing side, float f, float f1, float f2) {
        FXBlockWard fb = new FXBlockWard(getWorld(), x + 0.5, y + 0.5, z + 0.5, side, f, f1, f2);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb);
    }
    
    public FXSwarm swarmParticleFX(Entity targetedEntity, float f1, float f2, float pg) {
        FXSwarm fx = new FXSwarm(getWorld(), targetedEntity.posX + (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 2.0f, targetedEntity.posY + (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 2.0f, targetedEntity.posZ + (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 2.0f, targetedEntity, 0.8f + getWorld().rand.nextFloat() * 0.2f, getWorld().rand.nextFloat() * 0.4f, 1.0f - getWorld().rand.nextFloat() * 0.2f, f1, f2, pg);
        ParticleEngine.addEffect(getWorld(), fx);
        return fx;
    }
    
    public void bottleTaintBreak(double x, double y, double z) {
        for (int k1 = 0; k1 < 8; ++k1) {
            getWorld().spawnParticle(EnumParticleTypes.ITEM_CRACK, x, y, z, getWorld().rand.nextGaussian() * 0.15, getWorld().rand.nextDouble() * 0.2, getWorld().rand.nextGaussian() * 0.15, Item.getIdFromItem(ItemsTC.bottleTaint));
        }
        getWorld().playSound(x, y, z, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0f, getWorld().rand.nextFloat() * 0.1f + 0.9f, false);
    }
    
    public void arcLightning(double x, double y, double z, double tx, double ty, double tz, float r, float g, float b, float h) {
        if (h <= 0.0f) {
            h = 0.1f;
        }
        FXArc efa = new FXArc(getWorld(), x, y, z, tx, ty, tz, r, g, b, h);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(efa);
    }
    
    public void arcBolt(double x, double y, double z, double tx, double ty, double tz, float r, float g, float b, float width) {
        FXBolt efa = new FXBolt(getWorld(), x, y, z, tx, ty, tz, r, g, b, width);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(efa);
    }
    
    public void cultistSpawn(double x, double y, double z, double a, double b, double c) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, a, b, c);
        fb.setMaxAge(10 + getWorld().rand.nextInt(10));
        fb.setRBGColorF(1.0f, 1.0f, 1.0f, 0.6f, 0.0f, 0.0f);
        fb.setAlphaF(0.8f);
        fb.setGridSize(16);
        fb.setParticles(160, 6, 1);
        fb.setScale(3.0f + getWorld().rand.nextFloat() * 2.0f);
        fb.setLayer(1);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawWispyMotesEntity(double x, double y, double z, Entity e, float r, float g, float b) {
        FXGenericP2E fb = new FXGenericP2E(getWorld(), x, y, z, e);
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.6f);
        fb.setParticles(512, 16, 1);
        fb.setLoop(true);
        fb.setWind(0.001);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawWispParticles(double x, double y, double z, double x2, double y2, double z2, int color, int a) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
        fb.setMaxAge(10 + getWorld().rand.nextInt(5));
        Color c = new Color(color);
        fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        fb.setAlphaF(0.5f);
        fb.setLoop(true);
        fb.setGridSize(64);
        fb.setParticles(264, 8, 1);
        fb.setScale(1.0f + getWorld().rand.nextFloat() * 0.25f, 0.05f);
        fb.setWind(2.5E-4);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        ParticleEngine.addEffectWithDelay(getWorld(), fb, a);
    }
    
    public void drawNitorCore(double x, double y, double z, double x2, double y2, double z2) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
        fb.setMaxAge(10);
        fb.setRBGColorF(1.0f, 1.0f, 1.0f);
        fb.setAlphaF(1.0f);
        fb.setParticles(457, 1, 1);
        fb.setScale(1.0f, 1.0f + (float) getWorld().rand.nextGaussian() * 0.1f, 1.0f);
        fb.setLayer(1);
        fb.setRandomMovementScale(2.0E-4f, 2.0E-4f, 2.0E-4f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
    
    public void drawNitorFlames(double x, double y, double z, double x2, double y2, double z2, int color, int a) {
        FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);
        fb.setMaxAge(10 + getWorld().rand.nextInt(5));
        Color c = new Color(color);
        fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        fb.setAlphaF(0.66f);
        fb.setLoop(true);
        fb.setGridSize(64);
        fb.setParticles(264, 8, 1);
        fb.setScale(3.0f + getWorld().rand.nextFloat(), 0.05f);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        ParticleEngine.addEffectWithDelay(getWorld(), fb, a);
    }
    
    static {
        FXDispatcher.INSTANCE = new FXDispatcher();
        FXDispatcher.q = 0;
    }
    
    public static class GenPart
    {
        public int grid;
        public int age;
        public float redStart;
        public float greenStart;
        public float blueStart;
        public float redEnd;
        public float greenEnd;
        public float blueEnd;
        public float[] alpha;
        public float[] scale;
        public float rot;
        public float rotstart;
        public boolean loop;
        public int partStart;
        public int partNum;
        public int partInc;
        public int layer;
        public double slowDown;
        public float grav;
        public int delay;
        
        public GenPart() {
            grid = 64;
            age = 0;
            redStart = 1.0f;
            greenStart = 1.0f;
            blueStart = 1.0f;
            redEnd = 1.0f;
            greenEnd = 1.0f;
            blueEnd = 1.0f;
            alpha = new float[] { 1.0f };
            scale = new float[] { 1.0f };
            rotstart = 0.0f;
            loop = false;
            partStart = 0;
            partNum = 1;
            partInc = 1;
            layer = 0;
            slowDown = 0.9800000190734863;
            grav = 0.0f;
            delay = 0;
        }
    }
}
