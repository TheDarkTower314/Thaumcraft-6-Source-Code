package thaumcraft.client.fx.other;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import thaumcraft.client.lib.obj.IModelCustom;


public class FXSonic extends Particle
{
    Entity target;
    float yaw;
    float pitch;
    private IModelCustom model;
    private static ResourceLocation MODEL;
    
    public FXSonic(World world, double d, double d1, double d2, Entity target, int age) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        this.target = null;
        yaw = 0.0f;
        pitch = 0.0f;
        particleRed = 1.0f;
        particleGreen = 1.0f;
        particleBlue = 1.0f;
        particleGravity = 0.0f;
        double motionX = 0.0;
        motionZ = motionX;
        motionY = motionX;
        this.motionX = motionX;
        particleMaxAge = age + rand.nextInt(age / 2);
        setSize(0.01f, 0.01f);
        particleScale = 1.0f;
        this.target = target;
        yaw = target.getRotationYawHead();
        pitch = target.rotationPitch;
        double posX = target.posX;
        this.posX = posX;
        prevPosX = posX;
        double n = target.posY + target.getEyeHeight();
        posY = n;
        prevPosY = n;
        double posZ = target.posZ;
        this.posZ = posZ;
        prevPosZ = posZ;
    }
    
    public void renderParticle(BufferBuilder wr, Entity p_180434_2_, float f, float f1, float f2, float f3, float f4, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        if (model == null) {
            model = AdvancedModelLoader.loadModel(FXSonic.MODEL);
        }
        float fade = (particleAge + f) / particleMaxAge;
        float xx = (float)(prevPosX + (posX - prevPosX) * f - FXSonic.interpPosX);
        float yy = (float)(prevPosY + (posY - prevPosY) * f - FXSonic.interpPosY);
        float zz = (float)(prevPosZ + (posZ - prevPosZ) * f - FXSonic.interpPosZ);
        GL11.glTranslated(xx, yy, zz);
        float b = 1.0f;
        int frame = Math.min(15, (int)(14.0f * fade) + 1);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("thaumcraft", "textures/models/ripple" + frame + ".png"));
        b = 0.5f;
        int i = 220;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f);
        GL11.glRotatef(-yaw, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        GL11.glTranslated(0.0, 0.0, 2.0f * target.height + target.width / 2.0f);
        GL11.glScaled(0.25 * target.height, 0.25 * target.height, -1.0f * target.height);
        GL11.glColor4f(b, b, b, 1.0f);
        model.renderAll();
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
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
        posX = target.posX;
        posY = target.posY + target.getEyeHeight();
        posZ = target.posZ;
    }
    
    static {
        MODEL = new ResourceLocation("thaumcraft", "models/obj/hemis.obj");
    }
}
