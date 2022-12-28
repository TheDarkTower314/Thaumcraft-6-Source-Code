package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderTaintSwarm extends RenderLiving
{
    public RenderTaintSwarm(RenderManager rm) {
        super(rm, null, 0.0f);
    }
    
    public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
    }
    
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
