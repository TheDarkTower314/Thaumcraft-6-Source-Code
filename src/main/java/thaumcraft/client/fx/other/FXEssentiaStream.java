package thaumcraft.client.fx.other;
import com.sasmaster.glelwjgl.java.CoreGLE;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.codechicken.lib.vec.Quat;
import thaumcraft.common.blocks.essentia.BlockEssentiaTransport;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class FXEssentiaStream extends Particle
{
    private double targetX;
    private double targetY;
    private double targetZ;
    private double startX;
    private double startY;
    private double startZ;
    private int count;
    public int length;
    private String key;
    private BlockPos startPos;
    private BlockPos endPos;
    static HashMap<String, FXEssentiaStream> pt;
    CoreGLE gle;
    private static ResourceLocation TEX0;
    int layer;
    double[][] points;
    float[][] colours;
    double[] radii;
    int growing;
    ArrayList<Quat> vecs;
    
    public FXEssentiaStream(World w, double par2, double par4, double par6, double tx, double ty, double tz, int count, int color, float scale, int extend, double my) {
        super(w, par2, par4, par6, 0.0, 0.0, 0.0);
        this.count = 0;
        length = 20;
        key = "";
        startPos = null;
        endPos = null;
        gle = new CoreGLE();
        layer = 1;
        growing = -1;
        vecs = new ArrayList<Quat>();
        particleScale = (float)(scale * (1.0 + rand.nextGaussian() * 0.15000000596046448));
        length = Math.max(20, extend);
        this.count = count;
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        BlockPos bp1 = new BlockPos(posX, posY, posZ);
        BlockPos bp2 = new BlockPos(targetX, targetY, targetZ);
        IBlockState bs = w.getBlockState(bp1);
        if (bs.getBlock() instanceof BlockEssentiaTransport) {
            EnumFacing f = BlockStateUtils.getFacing(bs);
            posX += f.getFrontOffsetX() * 0.05f;
            posY += f.getFrontOffsetY() * 0.05f;
            posZ += f.getFrontOffsetZ() * 0.05f;
        }
        double dx = tx - posX;
        double dy = ty - posY;
        double dz = tz - posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 21.0f);
        if (base < 1) {
            base = 1;
        }
        particleMaxAge = base;
        String k = bp1.toLong() + "" + bp2.toLong() + "" + color;
        if (FXEssentiaStream.pt.containsKey(k)) {
            FXEssentiaStream trail2 = FXEssentiaStream.pt.get(k);
            if (!trail2.isExpired && trail2.vecs.size() < trail2.length) {
                FXEssentiaStream fxEssentiaStream = trail2;
                fxEssentiaStream.length += Math.max(extend, 5);
                FXEssentiaStream fxEssentiaStream2 = trail2;
                fxEssentiaStream2.particleMaxAge += Math.max(extend, 5);
                particleMaxAge = 0;
            }
        }
        if (particleMaxAge > 0) {
            FXEssentiaStream.pt.put(k, this);
            key = k;
        }
        motionX = MathHelper.sin(count / 4.0f) * 0.015f;
        motionY = my + MathHelper.sin(count / 3.0f) * 0.015f;
        motionZ = MathHelper.sin(count / 2.0f) * 0.015f;
        Color c = new Color(color);
        particleRed = c.getRed() / 255.0f;
        particleGreen = c.getGreen() / 255.0f;
        particleBlue = c.getBlue() / 255.0f;
        particleGravity = 0.2f;
        vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        startX = posX;
        startY = posY;
        startZ = posZ;
        startPos = new BlockPos(startX, startY, startZ);
        endPos = bp2;
    }
    
    public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        double ePX = startX - FXEssentiaStream.interpPosX;
        double ePY = startY - FXEssentiaStream.interpPosY;
        double ePZ = startZ - FXEssentiaStream.interpPosZ;
        GL11.glTranslated(ePX, ePY, ePZ);
        if (points != null && points.length > 2) {
            Minecraft.getMinecraft().renderEngine.bindTexture(FXEssentiaStream.TEX0);
            gle.set_POLYCYL_TESS(8);
            gle.set__ROUND_TESS_PIECES(1);
            gle.gleSetJoinStyle(1042);
            gle.glePolyCone(points.length, points, colours, radii, 0.075f, (growing < 0) ? 0.0f : (0.075f * (particleAge - growing + f)));
        }
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleEngine.particleTexture);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
    
    public void setFXLayer(int l) {
        layer = l;
    }
    
    public int getFXLayer() {
        return layer;
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge || length < 1) {
            setExpired();
            if (FXEssentiaStream.pt.containsKey(key) && FXEssentiaStream.pt.get(key).isExpired) {
                FXEssentiaStream.pt.remove(key);
            }
            return;
        }
        motionY += 0.01 * particleGravity;
        move(motionX, motionY, motionZ);
        motionX *= 0.985;
        motionY *= 0.985;
        motionZ *= 0.985;
        motionX = MathHelper.clamp((float) motionX, -0.05f, 0.05f);
        motionY = MathHelper.clamp((float) motionY, -0.05f, 0.05f);
        motionZ = MathHelper.clamp((float) motionZ, -0.05f, 0.05f);
        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        double d13 = 0.01;
        double d14 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= d14;
        dy /= d14;
        dz /= d14;
        motionX += dx * (d13 / Math.min(1.0, d14));
        motionY += dy * (d13 / Math.min(1.0, d14));
        motionZ += dz * (d13 / Math.min(1.0, d14));
        float scale = particleScale * (0.75f + MathHelper.sin((count + particleAge) / 2.0f) * 0.25f);
        if (d14 < 1.0) {
            float f = MathHelper.sin((float)(d14 * 1.5707963267948966));
            scale *= f;
            particleScale *= f;
        }
        if (particleScale > 0.001) {
            vecs.add(new Quat(scale, posX - startX, posY - startY, posZ - startZ));
        }
        else {
            if (growing < 0) {
                growing = particleAge;
            }
            --length;
            FXDispatcher.INSTANCE.essentiaDropFx(targetX + rand.nextGaussian() * 0.07500000298023224, targetY + rand.nextGaussian() * 0.07500000298023224, targetZ + rand.nextGaussian() * 0.07500000298023224, particleRed, particleGreen, particleBlue, 0.5f);
        }
        if (vecs.size() > length) {
            vecs.remove(0);
        }
        points = new double[vecs.size()][3];
        colours = new float[vecs.size()][4];
        radii = new double[vecs.size()];
        int c = vecs.size();
        for (Quat v : vecs) {
            --c;
            float variance = 1.0f + MathHelper.sin((c + particleAge) / 3.0f) * 0.2f;
            float xx = MathHelper.sin((c + particleAge) / 6.0f) * 0.03f;
            float yy = MathHelper.sin((c + particleAge) / 7.0f) * 0.03f;
            float zz = MathHelper.sin((c + particleAge) / 8.0f) * 0.03f;
            points[c][0] = v.x + xx;
            points[c][1] = v.y + yy;
            points[c][2] = v.z + zz;
            radii[c] = v.s * variance;
            if (c > vecs.size() - 10) {
                double[] radii = this.radii;
                int n = c;
                radii[n] *= MathHelper.cos((float)((c - (vecs.size() - 12)) / 10.0f * 1.5707963267948966));
            }
            if (c == 0) {
                radii[c] = 0.0;
            }
            else if (c == 1) {
                radii[c] = 0.0;
            }
            else if (c == 2) {
                radii[c] = (particleScale * 0.5 + radii[c]) / 2.0;
            }
            else if (c == 3) {
                radii[c] = (particleScale + radii[c]) / 2.0;
            }
            else if (c == 4) {
                radii[c] = (particleScale + radii[c] * 2.0) / 3.0;
            }
            float v2 = 1.0f - MathHelper.sin((c + particleAge) / 2.0f) * 0.1f;
            colours[c][0] = particleRed * v2;
            colours[c][1] = particleGreen * v2;
            colours[c][2] = particleBlue * v2;
            colours[c][3] = 1.0f;
        }
        if (vecs.size() > 2 && rand.nextBoolean()) {
            int q = rand.nextInt(3);
            if (rand.nextBoolean()) {
                q = vecs.size() - 2;
            }
            FXDispatcher.INSTANCE.essentiaDropFx(vecs.get(q).x + startX, vecs.get(q).y + startY, vecs.get(q).z + startZ, particleRed, particleGreen, particleBlue, 0.5f);
        }
    }
    
    public void setGravity(float value) {
        particleGravity = value;
    }
    
    static {
        FXEssentiaStream.pt = new HashMap<String, FXEssentiaStream>();
        TEX0 = new ResourceLocation("thaumcraft", "textures/misc/essentia.png");
    }
}
