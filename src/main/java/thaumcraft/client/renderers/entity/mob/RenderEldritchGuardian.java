// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import thaumcraft.common.config.ModConfig;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.EntityLiving;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.entity.ModelEldritchGuardian;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;

@SideOnly(Side.CLIENT)
public class RenderEldritchGuardian extends RenderLiving
{
    protected ModelEldritchGuardian modelMain;
    private static final ResourceLocation[] skin;
    
    public RenderEldritchGuardian(final RenderManager rm, final ModelEldritchGuardian par1ModelBiped, final float par2) {
        super(rm, par1ModelBiped, par2);
        this.modelMain = par1ModelBiped;
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return (entity instanceof EntityEldritchWarden) ? RenderEldritchGuardian.skin[1] : RenderEldritchGuardian.skin[0];
    }
    
    public void doRenderLiving(final EntityLiving guardian, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glEnable(3042);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glBlendFunc(770, 771);
        float base = 1.0f;
        double d3 = par4;
        if (guardian instanceof EntityEldritchWarden) {
            d3 -= guardian.height * (((EntityEldritchWarden)guardian).getSpawnTimer() / 150.0f);
        }
        else {
            final Entity e = Minecraft.getMinecraft().getRenderViewEntity();
            final float d4 = (e.world.getDifficulty() == EnumDifficulty.HARD) ? 576.0f : 1024.0f;
            final float d5 = 256.0f;
            if (guardian.world != null && guardian.world.provider.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId) {
                base = 1.0f;
            }
            else {
                final double d6 = guardian.getDistanceSq(e.posX, e.posY, e.posZ);
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
    
    public void doRender(final EntityLiving par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.doRenderLiving(par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        skin = new ResourceLocation[] { new ResourceLocation("thaumcraft", "textures/entity/eldritch_guardian.png"), new ResourceLocation("thaumcraft", "textures/entity/eldritch_warden.png") };
    }
}
