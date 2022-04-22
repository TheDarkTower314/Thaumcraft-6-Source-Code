// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import org.lwjgl.opengl.GL11;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;

@SideOnly(Side.CLIENT)
public class RenderTaintCrawler extends RenderLiving
{
    private static final ResourceLocation textures;
    
    public RenderTaintCrawler(final RenderManager p_i46144_1_) {
        super(p_i46144_1_, new ModelSilverfish(), 0.2f);
    }
    
    protected float func_180584_a(final EntityTaintCrawler p_180584_1_) {
        return 180.0f;
    }
    
    protected ResourceLocation getEntityTexture(final EntityTaintCrawler entity) {
        return RenderTaintCrawler.textures;
    }
    
    protected float getDeathMaxRotation(final EntityLivingBase p_77037_1_) {
        return func_180584_a((EntityTaintCrawler)p_77037_1_);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return getEntityTexture((EntityTaintCrawler)entity);
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLiving, final float par2) {
        GL11.glScalef(0.7f, 0.7f, 0.7f);
    }
    
    static {
        textures = new ResourceLocation("thaumcraft", "textures/entity/crawler.png");
    }
}
