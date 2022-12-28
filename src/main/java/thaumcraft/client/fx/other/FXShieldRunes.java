package thaumcraft.client.fx.other;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import thaumcraft.client.lib.obj.IModelCustom;
import thaumcraft.common.entities.monster.cult.EntityCultist;


public class FXShieldRunes extends Particle
{
    ResourceLocation[] tex1;
    ResourceLocation[] tex2;
    Entity target;
    float yaw;
    float pitch;
    private IModelCustom model;
    private static ResourceLocation MODEL;
    
    public FXShieldRunes(World world, double d, double d1, double d2, Entity target, int age, float yaw, float pitch) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        tex1 = new ResourceLocation[15];
        tex2 = new ResourceLocation[15];
        this.target = null;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
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
        this.yaw = yaw;
        this.pitch = pitch;
        if (target != null) {
            double posX = target.posX;
            this.posX = posX;
            prevPosX = posX;
            double n = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0;
            posY = n;
            prevPosY = n;
            double posZ = target.posZ;
            this.posZ = posZ;
            prevPosZ = posZ;
        }
        for (int a = 0; a < 15; ++a) {
            tex1[a] = new ResourceLocation("thaumcraft", "textures/models/ripple" + (a + 1) + ".png");
            tex2[a] = new ResourceLocation("thaumcraft", "textures/models/hemis" + (a + 1) + ".png");
        }
    }
    
    public void renderParticle(BufferBuilder wr, Entity p_180434_2_, float f, float f1, float f2, float f3, float f4, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        if (model == null) {
            model = AdvancedModelLoader.loadModel(FXShieldRunes.MODEL);
        }
        float fade = (particleAge + f) / particleMaxAge;
        float xx = (float)(prevPosX + (posX - prevPosX) * f - FXShieldRunes.interpPosX);
        float yy = (float)(prevPosY + (posY - prevPosY) * f - FXShieldRunes.interpPosY);
        float zz = (float)(prevPosZ + (posZ - prevPosZ) * f - FXShieldRunes.interpPosZ);
        GL11.glTranslated(xx, yy, zz);
        float b = 1.0f;
        int frame = Math.min(15, (int)(14.0f * fade) + 1);
        if (target != null && target instanceof EntityMob && !(target instanceof EntityCultist)) {
            Minecraft.getMinecraft().renderEngine.bindTexture(tex1[frame - 1]);
            b = 0.5f;
        }
        else {
            Minecraft.getMinecraft().renderEngine.bindTexture(tex2[frame - 1]);
        }
        int i = 220;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f);
        GL11.glRotatef(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-pitch, 1.0f, 0.0f, 0.0f);
        float th = (target == null) ? 2.0f : target.height;
        GL11.glScaled(0.2 * th, 0.2 * th, 0.2 * th);
        if (target == null) {
            GL11.glColor4f(0.65f, 0.1f, 0.5f, Math.min(1.0f, (1.0f - fade) * 3.0f));
        }
        else {
            GL11.glColor4f(b, b, b, Math.min(1.0f, (1.0f - fade) * 3.0f));
        }
        model.renderAll();
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glEnable(2884);
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
        if (target != null) {
            posX = target.posX;
            posY = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0;
            posZ = target.posZ;
        }
    }
    
    static {
        MODEL = new ResourceLocation("thaumcraft", "models/obj/hemis.obj");
    }
}
