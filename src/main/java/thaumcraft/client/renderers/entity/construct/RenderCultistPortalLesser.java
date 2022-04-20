// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.construct;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import thaumcraft.client.lib.UtilsFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;

@SideOnly(Side.CLIENT)
public class RenderCultistPortalLesser extends Render
{
    public static final ResourceLocation portaltex;
    
    public RenderCultistPortalLesser(final RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0f;
        this.shadowOpaque = 0.0f;
    }
    
    public void renderPortal(final EntityCultistPortalLesser portal, final double px, double py, final double pz, final float par8, final float f) {
        if (portal.isActive()) {
            final long nt = System.nanoTime();
            final long time = nt / 50000000L;
            float scaley = 1.4f;
            int e = (int)Math.min(50.0f, portal.activeCounter + f);
            if (portal.hurtTime > 0) {
                final double d = Math.sin(portal.hurtTime * 72 * 3.141592653589793 / 180.0);
                scaley -= (float)(d / 4.0);
                e += (int)(6.0 * d);
            }
            if (portal.pulse > 0) {
                final double d = Math.sin(portal.pulse * 36 * 3.141592653589793 / 180.0);
                scaley += (float)(d / 4.0);
                e += (int)(12.0 * d);
            }
            float scale = e / 50.0f * 1.25f;
            py += portal.height / 2.0f;
            final float m = (1.0f - portal.getHealth() / portal.getMaxHealth()) / 3.0f;
            final float bob = MathHelper.sin(portal.activeCounter / (5.0f - 12.0f * m)) * m + m;
            final float bob2 = MathHelper.sin(portal.activeCounter / (6.0f - 15.0f * m)) * m + m;
            final float alpha = 1.0f - bob;
            scaley -= bob / 4.0f;
            scale -= bob2 / 3.0f;
            this.bindTexture(RenderCultistPortalLesser.portaltex);
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
            if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
                GL11.glDepthMask(false);
                final Tessellator tessellator = Tessellator.getInstance();
                final float arX = ActiveRenderInfo.getRotationX();
                final float arZ = ActiveRenderInfo.getRotationZ();
                final float arYZ = ActiveRenderInfo.getRotationYZ();
                final float arXY = ActiveRenderInfo.getRotationXY();
                final float arXZ = ActiveRenderInfo.getRotationXZ();
                tessellator.getBuffer().begin(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
                final Vec3d v1 = new Vec3d(-arX - arYZ, -arXZ, -arZ - arXY);
                final Vec3d v2 = new Vec3d(-arX + arYZ, arXZ, -arZ + arXY);
                final Vec3d v3 = new Vec3d(arX + arYZ, arXZ, arZ + arXY);
                final Vec3d v4 = new Vec3d(arX - arYZ, -arXZ, arZ - arXY);
                final int frame = 15 - (int)time % 16;
                final float f2 = frame / 16.0f;
                final float f3 = f2 + 0.0625f;
                final float f4 = 0.0f;
                final float f5 = 1.0f;
                final int i = 220;
                final int j = i >> 16 & 0xFFFF;
                final int k = i & 0xFFFF;
                tessellator.getBuffer().pos(px + v1.x * scale, py + v1.y * scaley, pz + v1.z * scale).tex(f3, f4).color(1.0f, 1.0f, 1.0f, alpha).lightmap(j, k).normal(0.0f, 0.0f, -1.0f).endVertex();
                tessellator.getBuffer().pos(px + v2.x * scale, py + v2.y * scaley, pz + v2.z * scale).tex(f3, f5).color(1.0f, 1.0f, 1.0f, alpha).lightmap(j, k).normal(0.0f, 0.0f, -1.0f).endVertex();
                tessellator.getBuffer().pos(px + v3.x * scale, py + v3.y * scaley, pz + v3.z * scale).tex(f2, f5).color(1.0f, 1.0f, 1.0f, alpha).lightmap(j, k).normal(0.0f, 0.0f, -1.0f).endVertex();
                tessellator.getBuffer().pos(px + v4.x * scale, py + v4.y * scaley, pz + v4.z * scale).tex(f2, f4).color(1.0f, 1.0f, 1.0f, alpha).lightmap(j, k).normal(0.0f, 0.0f, -1.0f).endVertex();
                tessellator.draw();
                GL11.glDepthMask(true);
            }
            GL11.glDisable(32826);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderPortal((EntityCultistPortalLesser)par1Entity, par2, par4, par6, par8, par9);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderCultistPortalLesser.portaltex;
    }
    
    static {
        portaltex = new ResourceLocation("thaumcraft", "textures/misc/cultist_portal.png");
    }
}
