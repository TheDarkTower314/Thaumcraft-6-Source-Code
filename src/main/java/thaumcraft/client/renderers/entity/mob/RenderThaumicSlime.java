package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderThaumicSlime extends RenderSlime
{
    private static ResourceLocation slimeTexture;
    
    public RenderThaumicSlime(RenderManager p_i46141_1_, ModelBase p_i46141_2_, float p_i46141_3_) {
        super(p_i46141_1_);
    }
    
    protected ResourceLocation getEntityTexture(EntitySlime entity) {
        return RenderThaumicSlime.slimeTexture;
    }
    
    static {
        slimeTexture = new ResourceLocation("thaumcraft", "textures/entity/tslime.png");
    }
}
