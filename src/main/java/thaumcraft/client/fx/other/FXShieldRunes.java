// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.fx.other;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.Minecraft;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import net.minecraft.entity.monster.EntityMob;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import thaumcraft.client.lib.obj.IModelCustom;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.particle.Particle;

public class FXShieldRunes extends Particle
{
    ResourceLocation[] tex1;
    ResourceLocation[] tex2;
    Entity target;
    float yaw;
    float pitch;
    private IModelCustom model;
    private static final ResourceLocation MODEL;
    
    public FXShieldRunes(final World world, final double d, final double d1, final double d2, final Entity target, final int age, final float yaw, final float pitch) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        this.tex1 = new ResourceLocation[15];
        this.tex2 = new ResourceLocation[15];
        this.target = null;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
        this.particleRed = 1.0f;
        this.particleGreen = 1.0f;
        this.particleBlue = 1.0f;
        this.particleGravity = 0.0f;
        final double motionX = 0.0;
        this.motionZ = motionX;
        this.motionY = motionX;
        this.motionX = motionX;
        this.particleMaxAge = age + this.rand.nextInt(age / 2);
        this.setSize(0.01f, 0.01f);
        this.particleScale = 1.0f;
        this.target = target;
        this.yaw = yaw;
        this.pitch = pitch;
        if (target != null) {
            final double posX = target.posX;
            this.posX = posX;
            this.prevPosX = posX;
            final double n = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0;
            this.posY = n;
            this.prevPosY = n;
            final double posZ = target.posZ;
            this.posZ = posZ;
            this.prevPosZ = posZ;
        }
        for (int a = 0; a < 15; ++a) {
            this.tex1[a] = new ResourceLocation("thaumcraft", "textures/models/ripple" + (a + 1) + ".png");
            this.tex2[a] = new ResourceLocation("thaumcraft", "textures/models/hemis" + (a + 1) + ".png");
        }
    }
    
    public void renderParticle(final BufferBuilder wr, final Entity p_180434_2_, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        if (this.model == null) {
            this.model = AdvancedModelLoader.loadModel(FXShieldRunes.MODEL);
        }
        final float fade = (this.particleAge + f) / this.particleMaxAge;
        final float xx = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - FXShieldRunes.interpPosX);
        final float yy = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - FXShieldRunes.interpPosY);
        final float zz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXShieldRunes.interpPosZ);
        GL11.glTranslated(xx, yy, zz);
        float b = 1.0f;
        final int frame = Math.min(15, (int)(14.0f * fade) + 1);
        if (this.target != null && this.target instanceof EntityMob && !(this.target instanceof EntityCultist)) {
            Minecraft.getMinecraft().renderEngine.bindTexture(this.tex1[frame - 1]);
            b = 0.5f;
        }
        else {
            Minecraft.getMinecraft().renderEngine.bindTexture(this.tex2[frame - 1]);
        }
        final int i = 220;
        final int j = i % 65536;
        final int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f);
        GL11.glRotatef(180.0f - this.yaw, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-this.pitch, 1.0f, 0.0f, 0.0f);
        final float th = (this.target == null) ? 2.0f : this.target.height;
        GL11.glScaled(0.2 * th, 0.2 * th, 0.2 * th);
        if (this.target == null) {
            GL11.glColor4f(0.65f, 0.1f, 0.5f, Math.min(1.0f, (1.0f - fade) * 3.0f));
        }
        else {
            GL11.glColor4f(b, b, b, Math.min(1.0f, (1.0f - fade) * 3.0f));
        }
        this.model.renderAll();
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glEnable(2884);
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleManager.PARTICLE_TEXTURES);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
        if (this.target != null) {
            this.posX = this.target.posX;
            this.posY = (this.target.getEntityBoundingBox().minY + this.target.getEntityBoundingBox().maxY) / 2.0;
            this.posZ = this.target.posZ;
        }
    }
    
    static {
        MODEL = new ResourceLocation("thaumcraft", "models/obj/hemis.obj");
    }
}
