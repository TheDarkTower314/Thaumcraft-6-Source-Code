package thaumcraft.client.renderers.entity;
import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;


public class RenderSpecialItem extends RenderEntityItem
{
    public RenderSpecialItem(RenderManager p_i46167_1_, RenderItem p_i46167_2_) {
        super(p_i46167_1_, p_i46167_2_);
    }
    
    public void doRender(EntityItem e, double x, double y, double z, float p_177075_8_, float pt) {
        Random random = new Random(187L);
        float var11 = MathHelper.sin((e.getAge() + pt) / 10.0f + e.hoverStart) * 0.1f + 0.1f;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y + var11 + 0.25f, (float)z);
        int q = FMLClientHandler.instance().getClient().gameSettings.fancyGraphics ? 10 : 5;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = tessellator.getBuffer();
        RenderHelper.disableStandardItemLighting();
        float f1 = e.getAge() / 500.0f;
        float f2 = 0.9f;
        float f3 = 0.0f;
        GL11.glDisable(3553);
        GL11.glShadeModel(7425);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDisable(3008);
        GL11.glEnable(2884);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        for (int i = 0; i < q; ++i) {
            GL11.glRotatef(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f + f1 * 360.0f, 0.0f, 0.0f, 1.0f);
            wr.begin(6, DefaultVertexFormats.POSITION_COLOR);
            float fa = random.nextFloat() * 20.0f + 5.0f + f3 * 10.0f;
            float f4 = random.nextFloat() * 2.0f + 1.0f + f3 * 2.0f;
            fa /= 30.0f / (Math.min(e.getAge(), 10) / 10.0f);
            f4 /= 30.0f / (Math.min(e.getAge(), 10) / 10.0f);
            wr.pos(0.0, 0.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f - f3).endVertex();
            wr.pos(-0.866 * f4, fa, -0.5f * f4).color(1.0f, 0.0f, 1.0f, 0.0f).endVertex();
            wr.pos(0.866 * f4, fa, -0.5f * f4).color(1.0f, 0.0f, 1.0f, 0.0f).endVertex();
            wr.pos(0.0, fa, 1.0f * f4).color(1.0f, 0.0f, 1.0f, 0.0f).endVertex();
            wr.pos(-0.866 * f4, fa, -0.5f * f4).color(1.0f, 0.0f, 1.0f, 0.0f).endVertex();
            tessellator.draw();
        }
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDisable(2884);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3042);
        GL11.glShadeModel(7424);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        super.doRender(e, x, y, z, p_177075_8_, pt);
    }
}
