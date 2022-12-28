package thaumcraft.client.renderers.entity.mob;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderInhabitedZombie extends RenderZombie
{
    private static ResourceLocation t1;
    private ModelBiped field_82434_o;
    private ModelZombieVillager field_82432_p;
    private int field_82431_q;
    
    public RenderInhabitedZombie(RenderManager p_i46127_1_) {
        super(p_i46127_1_);
        field_82431_q = 1;
    }
    
    protected ResourceLocation getEntityTexture(EntityZombie entity) {
        return RenderInhabitedZombie.t1;
    }
    
    static {
        t1 = new ResourceLocation("thaumcraft", "textures/entity/czombie.png");
    }
}
