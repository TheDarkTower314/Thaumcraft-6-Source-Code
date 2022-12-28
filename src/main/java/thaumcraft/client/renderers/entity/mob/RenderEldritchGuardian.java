package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.entity.ModelEldritchGuardian;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;


@SideOnly(Side.CLIENT)
public class RenderEldritchGuardian extends RenderLiving
{
    protected ModelEldritchGuardian modelMain;
    private static ResourceLocation[] skin;
    
    public RenderEldritchGuardian(RenderManager rm, ModelEldritchGuardian par1ModelBiped, float par2) {
        super(rm, par1ModelBiped, par2);
        modelMain = par1ModelBiped;
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return (entity instanceof EntityEldritchWarden) ? RenderEldritchGuardian.skin[1] : RenderEldritchGuardian.skin[0];
    }
    
    public void doRenderLiving(EntityLiving guardian, double par2, double par4, double par6, float par8, float par9) {
        GL11.glEnable(3042);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glBlendFunc(770, 771);
        float base = 1.0f;
        double d3 = par4;
        if (guardian instanceof EntityEldritchWarden) {
            d3 -= guardian.height * (((EntityEldritchWarden)guardian).getSpawnTimer() / 150.0f);
        }
        else {
            Entity e = Minecraft.getMinecraft().getRenderViewEntity();
            float d4 = (e.world.getDifficulty() == EnumDifficulty.HARD) ? 576.0f : 1024.0f;
            float d5 = 256.0f;
            if (guardian.world != null && guardian.world.provider.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId) {
                base = 1.0f;
            }
            else {
                double d6 = guardian.getDistanceSq(e.posX, e.posY, e.posZ);
                if (d6 < 256.0) {
                    base = 0.6f;
                }
                else {
                    base = (float)(1.0 - Math.min(d4 - d5, d6 - d5) / (d4 - d5)) * 0.6f;
                }
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, base);
        super.doRender(guardian, par2, d3, par6, par8, par9);
        GL11.glDisable(3042);
        GL11.glAlphaFunc(516, 0.1f);
    }
    
    public void doRender(EntityLiving par1Entity, double par2, double par4, double par6, float par8, float par9) {
        doRenderLiving(par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        skin = new ResourceLocation[] { new ResourceLocation("thaumcraft", "textures/entity/eldritch_guardian.png"), new ResourceLocation("thaumcraft", "textures/entity/eldritch_warden.png") };
    }
}
