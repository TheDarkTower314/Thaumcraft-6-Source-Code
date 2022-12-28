package thaumcraft.client.fx.other;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;


public class FXBlockWard extends Particle
{
    ResourceLocation[] tex1;
    EnumFacing side;
    int rotation;
    float sx;
    float sy;
    float sz;
    
    public FXBlockWard(World world, double d, double d1, double d2, EnumFacing side, float f, float f1, float f2) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        tex1 = new ResourceLocation[15];
        rotation = 0;
        sx = 0.0f;
        sy = 0.0f;
        sz = 0.0f;
        this.side = side;
        particleGravity = 0.0f;
        double motionX = 0.0;
        motionZ = motionX;
        motionY = motionX;
        this.motionX = motionX;
        particleMaxAge = 12 + rand.nextInt(5);
        setSize(0.01f, 0.01f);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        particleScale = (float)(1.4 + rand.nextGaussian() * 0.30000001192092896);
        rotation = rand.nextInt(360);
        sx = MathHelper.clamp(f - 0.6f + rand.nextFloat() * 0.2f, -0.4f, 0.4f);
        sy = MathHelper.clamp(f1 - 0.6f + rand.nextFloat() * 0.2f, -0.4f, 0.4f);
        sz = MathHelper.clamp(f2 - 0.6f + rand.nextFloat() * 0.2f, -0.4f, 0.4f);
        if (side.getFrontOffsetX() != 0) {
            sx = 0.0f;
        }
        if (side.getFrontOffsetY() != 0) {
            sy = 0.0f;
        }
        if (side.getFrontOffsetZ() != 0) {
            sz = 0.0f;
        }
        for (int a = 0; a < 15; ++a) {
            tex1[a] = new ResourceLocation("thaumcraft", "textures/models/hemis" + (a + 1) + ".png");
        }
    }
    
    public void renderParticle(BufferBuilder wr, Entity p_180434_2_, float f, float f1, float f2, float f3, float f4, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        float fade = (particleAge + f) / particleMaxAge;
        int frame = Math.min(15, (int)(15.0f * fade));
        Minecraft.getMinecraft().renderEngine.bindTexture(tex1[frame - 1]);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, particleAlpha / 2.0f);
        float var13 = (float)(prevPosX + (posX - prevPosX) * f - FXBlockWard.interpPosX);
        float var14 = (float)(prevPosY + (posY - prevPosY) * f - FXBlockWard.interpPosY);
        float var15 = (float)(prevPosZ + (posZ - prevPosZ) * f - FXBlockWard.interpPosZ);
        GL11.glTranslated(var13 + sx, var14 + sy, var15 + sz);
        GL11.glRotatef(90.0f, (float) side.getFrontOffsetY(), (float)(-side.getFrontOffsetX()), (float) side.getFrontOffsetZ());
        GL11.glRotatef((float) rotation, 0.0f, 0.0f, 1.0f);
        if (side.getFrontOffsetZ() > 0) {
            GL11.glTranslated(0.0, 0.0, 0.5049999952316284);
            GL11.glRotatef(180.0f, 0.0f, -1.0f, 0.0f);
        }
        else {
            GL11.glTranslated(0.0, 0.0, -0.5049999952316284);
        }
        float var16 = particleScale;
        float var17 = 1.0f;
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        int i = 240;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        wr.pos(-0.5 * var16, 0.5 * var16, 0.0).tex(0.0, 1.0).color(particleRed * var17, particleGreen * var17, particleBlue * var17, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var16, 0.5 * var16, 0.0).tex(1.0, 1.0).color(particleRed * var17, particleGreen * var17, particleBlue * var17, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(0.5 * var16, -0.5 * var16, 0.0).tex(1.0, 0.0).color(particleRed * var17, particleGreen * var17, particleBlue * var17, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        wr.pos(-0.5 * var16, -0.5 * var16, 0.0).tex(0.0, 0.0).color(particleRed * var17, particleGreen * var17, particleBlue * var17, particleAlpha / 2.0f).lightmap(j, k).endVertex();
        Tessellator.getInstance().draw();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleManager.PARTICLE_TEXTURES);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
    
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        float threshold = particleMaxAge / 5.0f;
        if (particleAge <= threshold) {
            particleAlpha = particleAge / threshold;
        }
        else {
            particleAlpha = (particleMaxAge - particleAge) / (float) particleMaxAge;
        }
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
        motionY -= 0.04 * particleGravity;
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
    }
    
    public void setGravity(float value) {
        particleGravity = value;
    }
}
