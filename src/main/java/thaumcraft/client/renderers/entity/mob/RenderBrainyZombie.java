package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;


@SideOnly(Side.CLIENT)
public class RenderBrainyZombie extends RenderZombie
{
    private static ResourceLocation ZOMBIE_TEXTURES;
    private static ResourceLocation ZOMBIE_VILLAGER_TEXTURES;
    private ModelBiped field_82434_o;
    private ModelZombieVillager field_82432_p;
    private int field_82431_q;
    
    public RenderBrainyZombie(RenderManager rm) {
        super(rm);
        field_82431_q = 1;
    }
    
    protected ResourceLocation getEntityTexture(EntityZombie entity) {
        return RenderBrainyZombie.ZOMBIE_TEXTURES;
    }
    
    protected void preRenderScale(EntityGiantBrainyZombie z, float par2) {
        GL11.glScalef(1.0f + z.getAnger(), 1.0f + z.getAnger(), 1.0f + z.getAnger());
        float q = Math.min(1.0f, z.getAnger()) / 2.0f;
        GL11.glColor3f(1.0f, 1.0f - q, 1.0f - q);
    }
    
    protected void preRenderCallback(EntityZombie par1EntityLiving, float par2) {
        if (par1EntityLiving instanceof EntityGiantBrainyZombie) {
            preRenderScale((EntityGiantBrainyZombie)par1EntityLiving, par2);
        }
    }
    
    static {
        ZOMBIE_TEXTURES = new ResourceLocation("thaumcraft", "textures/entity/bzombie.png");
        ZOMBIE_VILLAGER_TEXTURES = new ResourceLocation("thaumcraft", "textures/entity/bzombievil.png");
    }
}
