// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderZombie;

@SideOnly(Side.CLIENT)
public class RenderBrainyZombie extends RenderZombie
{
    private static final ResourceLocation ZOMBIE_TEXTURES;
    private static final ResourceLocation ZOMBIE_VILLAGER_TEXTURES;
    private ModelBiped field_82434_o;
    private ModelZombieVillager field_82432_p;
    private int field_82431_q;
    
    public RenderBrainyZombie(final RenderManager rm) {
        super(rm);
        this.field_82431_q = 1;
    }
    
    protected ResourceLocation getEntityTexture(final EntityZombie entity) {
        return RenderBrainyZombie.ZOMBIE_TEXTURES;
    }
    
    protected void preRenderScale(final EntityGiantBrainyZombie z, final float par2) {
        GL11.glScalef(1.0f + z.getAnger(), 1.0f + z.getAnger(), 1.0f + z.getAnger());
        final float q = Math.min(1.0f, z.getAnger()) / 2.0f;
        GL11.glColor3f(1.0f, 1.0f - q, 1.0f - q);
    }
    
    protected void preRenderCallback(final EntityZombie par1EntityLiving, final float par2) {
        if (par1EntityLiving instanceof EntityGiantBrainyZombie) {
            this.preRenderScale((EntityGiantBrainyZombie)par1EntityLiving, par2);
        }
    }
    
    static {
        ZOMBIE_TEXTURES = new ResourceLocation("thaumcraft", "textures/entity/bzombie.png");
        ZOMBIE_VILLAGER_TEXTURES = new ResourceLocation("thaumcraft", "textures/entity/bzombievil.png");
    }
}
