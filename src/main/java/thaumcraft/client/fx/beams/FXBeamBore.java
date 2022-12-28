package thaumcraft.client.fx.beams;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.UtilsFX;


public class FXBeamBore extends Particle
{
    public int particle;
    private double offset;
    private double tX;
    private double tY;
    private double tZ;
    private double ptX;
    private double ptY;
    private double ptZ;
    private float length;
    private float rotYaw;
    private float rotPitch;
    private float prevYaw;
    private float prevPitch;
    private Entity targetEntity;
    private int type;
    private float endMod;
    private boolean reverse;
    private boolean pulse;
    private int rotationspeed;
    private float prevSize;
    public int impact;
    ResourceLocation beam;
    ResourceLocation beam1;
    ResourceLocation beam2;
    ResourceLocation beam3;
    
    public FXBeamBore(World par1World, double px, double py, double pz, double tx, double ty, double tz, float red, float green, float blue, int age) {
        super(par1World, px, py, pz, 0.0, 0.0, 0.0);
        particle = 16;
        offset = 0.0;
        tX = 0.0;
        tY = 0.0;
        tZ = 0.0;
        ptX = 0.0;
        ptY = 0.0;
        ptZ = 0.0;
        length = 0.0f;
        rotYaw = 0.0f;
        rotPitch = 0.0f;
        prevYaw = 0.0f;
        prevPitch = 0.0f;
        targetEntity = null;
        type = 0;
        endMod = 1.0f;
        reverse = false;
        pulse = true;
        rotationspeed = 5;
        prevSize = 0.0f;
        beam = new ResourceLocation("thaumcraft", "textures/misc/beam.png");
        beam1 = new ResourceLocation("thaumcraft", "textures/misc/beam1.png");
        beam2 = new ResourceLocation("thaumcraft", "textures/misc/beam2.png");
        beam3 = new ResourceLocation("thaumcraft", "textures/misc/beam3.png");
        particleRed = red;
        particleGreen = green;
        particleBlue = blue;
        setSize(0.02f, 0.02f);
        motionX = 0.0;
        motionY = 0.0;
        motionZ = 0.0;
        tX = tx;
        tY = ty;
        tZ = tz;
        prevYaw = rotYaw;
        prevPitch = rotPitch;
        particleMaxAge = age;
        Entity renderentity = FMLClientHandler.instance().getClient().getRenderViewEntity();
        int visibleDistance = 64;
        if (!FMLClientHandler.instance().getClient().gameSettings.fancyGraphics) {
            visibleDistance = 32;
        }
        if (renderentity != null && renderentity.getDistance(posX, posY, posZ) > visibleDistance) {
            particleMaxAge = 0;
        }
    }
    
    public void updateBeam(double sx, double sy, double sz, double x, double y, double z) {
        posX = sx;
        posY = sy;
        posZ = sz;
        tX = x;
        tY = y;
        tZ = z;
        while (particleMaxAge - particleAge < 4) {
            ++particleMaxAge;
        }
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY + offset;
        prevPosZ = posZ;
        ptX = tX;
        ptY = tY;
        ptZ = tZ;
        prevYaw = rotYaw;
        prevPitch = rotPitch;
        float xd = (float)(posX - tX);
        float yd = (float)(posY - tY);
        float zd = (float)(posZ - tZ);
        length = MathHelper.sqrt(xd * xd + yd * yd + zd * zd);
        double var7 = MathHelper.sqrt(xd * xd + zd * zd);
        rotYaw = (float)(Math.atan2(xd, zd) * 180.0 / 3.141592653589793);
        rotPitch = (float)(Math.atan2(yd, var7) * 180.0 / 3.141592653589793);
        prevYaw = rotYaw;
        prevPitch = rotPitch;
        if (impact > 0) {
            --impact;
        }
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
    }
    
    public void setRGB(float r, float g, float b) {
        particleRed = r;
        particleGreen = g;
        particleBlue = b;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public void setEndMod(float endMod) {
        this.endMod = endMod;
    }
    
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
    
    public void setPulse(boolean pulse) {
        this.pulse = pulse;
    }
    
    public void setRotationspeed(int rotationspeed) {
        this.rotationspeed = rotationspeed;
    }
    
    public void renderParticle(BufferBuilder wr, Entity p_180434_2_, float f, float f1, float f2, float f3, float f4, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        float var9 = 1.0f;
        float slide = (float)Minecraft.getMinecraft().player.ticksExisted;
        float rot = world.provider.getWorldTime() % (360 / rotationspeed) * rotationspeed + rotationspeed * f;
        float size = 1.0f;
        if (pulse) {
            size = Math.min(particleAge / 4.0f, 1.0f);
            size = (float)(prevSize + (size - prevSize) * (double)f);
        }
        float op = 0.4f;
        if (pulse && particleMaxAge - particleAge <= 4) {
            op = 0.4f - (4 - (particleMaxAge - particleAge)) * 0.1f;
        }
        switch (type) {
            default: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam);
                break;
            }
            case 1: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam1);
                break;
            }
            case 2: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam2);
                break;
            }
            case 3: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam3);
                break;
            }
        }
        GL11.glTexParameterf(3553, 10242, 10497.0f);
        GL11.glTexParameterf(3553, 10243, 10497.0f);
        GL11.glDisable(2884);
        float var10 = slide + f;
        if (reverse) {
            var10 *= -1.0f;
        }
        float var11 = -var10 * 0.2f - MathHelper.floor(-var10 * 0.1f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDepthMask(false);
        float xx = (float)(prevPosX + (posX - prevPosX) * f - FXBeamBore.interpPosX);
        float yy = (float)(prevPosY + (posY - prevPosY) * f - FXBeamBore.interpPosY);
        float zz = (float)(prevPosZ + (posZ - prevPosZ) * f - FXBeamBore.interpPosZ);
        GL11.glTranslated(xx, yy, zz);
        float ry = (float)(prevYaw + (rotYaw - prevYaw) * (double)f);
        float rp = (float)(prevPitch + (rotPitch - prevPitch) * (double)f);
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(180.0f + ry, 0.0f, 0.0f, -1.0f);
        GL11.glRotatef(rp, 1.0f, 0.0f, 0.0f);
        double var12 = -0.15 * size;
        double var13 = 0.15 * size;
        double var44b = -0.15 * size * endMod;
        double var17b = 0.15 * size * endMod;
        int i = 200;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        GL11.glRotatef(rot, 0.0f, 1.0f, 0.0f);
        for (int t = 0; t < 3; ++t) {
            double var14 = length * size * var9;
            double var15 = 0.0;
            double var16 = 1.0;
            double var17 = -1.0f + var11 + t / 3.0f;
            double var18 = length * size * var9 + var17;
            GL11.glRotatef(60.0f, 0.0f, 1.0f, 0.0f);
            wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            wr.pos(var44b, var14, 0.0).tex(var16, var18).color(particleRed, particleGreen, particleBlue, op).lightmap(j, k).endVertex();
            wr.pos(var12, 0.0, 0.0).tex(var16, var17).color(particleRed, particleGreen, particleBlue, op).lightmap(j, k).endVertex();
            wr.pos(var13, 0.0, 0.0).tex(var15, var17).color(particleRed, particleGreen, particleBlue, op).lightmap(j, k).endVertex();
            wr.pos(var17b, var14, 0.0).tex(var15, var18).color(particleRed, particleGreen, particleBlue, op).lightmap(j, k).endVertex();
            Tessellator.getInstance().draw();
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDepthMask(true);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL11.glPopMatrix();
        if (impact > 0) {
            renderImpact(Tessellator.getInstance(), f, f1, f2, f3, f4, f5);
        }
        renderSource(Tessellator.getInstance(), f, f1, f2, f3, f4, f5);
        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleManager.PARTICLE_TEXTURES);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        prevSize = size;
    }
    
    public void renderSource(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5) {
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        Minecraft.getMinecraft().renderEngine.bindTexture(UtilsFX.nodeTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.66f);
        int part = particleAge % 32;
        float op = 0.8f;
        if (pulse && particleMaxAge - particleAge <= 4) {
            op = 0.8f - (4 - (particleMaxAge - particleAge)) * 0.2f;
        }
        float var8 = part / 32.0f;
        float var9 = var8 + 0.03125f;
        float var10 = 0.09375f;
        float var11 = var10 + 0.03125f;
        float var12 = 0.33f;
        float var13 = (float)(prevPosX + (posX - prevPosX) * f - FXBeamBore.interpPosX);
        float var14 = (float)(prevPosY + (posY - prevPosY) * f - FXBeamBore.interpPosY);
        float var15 = (float)(prevPosZ + (posZ - prevPosZ) * f - FXBeamBore.interpPosZ);
        int i = 200;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        tessellator.getBuffer().begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        tessellator.getBuffer().pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed, particleGreen, particleBlue, op).lightmap(j, k).endVertex();
        tessellator.getBuffer().pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed, particleGreen, particleBlue, op).lightmap(j, k).endVertex();
        tessellator.getBuffer().pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed, particleGreen, particleBlue, op).lightmap(j, k).endVertex();
        tessellator.getBuffer().pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed, particleGreen, particleBlue, op).lightmap(j, k).endVertex();
        tessellator.draw();
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
    
    public void renderImpact(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5) {
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleEngine.particleTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.66f);
        int part = particleAge % 16;
        float var8 = part / 16.0f;
        float var9 = var8 + 0.0624375f;
        float var10 = 0.3125f;
        float var11 = var10 + 0.0624375f;
        float var12 = endMod / 2.0f / (6 - impact);
        float var13 = (float)(ptX + (tX - ptX) * f - FXBeamBore.interpPosX);
        float var14 = (float)(ptY + (tY - ptY) * f - FXBeamBore.interpPosY);
        float var15 = (float)(ptZ + (tZ - ptZ) * f - FXBeamBore.interpPosZ);
        int i = 200;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        tessellator.getBuffer().begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        tessellator.getBuffer().pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed, particleGreen, particleBlue, 0.66f).lightmap(j, k).endVertex();
        tessellator.getBuffer().pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed, particleGreen, particleBlue, 0.66f).lightmap(j, k).endVertex();
        tessellator.getBuffer().pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed, particleGreen, particleBlue, 0.66f).lightmap(j, k).endVertex();
        tessellator.getBuffer().pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed, particleGreen, particleBlue, 0.66f).lightmap(j, k).endVertex();
        tessellator.draw();
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
}
