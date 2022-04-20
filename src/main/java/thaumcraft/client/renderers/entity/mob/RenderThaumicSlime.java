// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderSlime;

@SideOnly(Side.CLIENT)
public class RenderThaumicSlime extends RenderSlime
{
    private static final ResourceLocation slimeTexture;
    
    public RenderThaumicSlime(final RenderManager p_i46141_1_, final ModelBase p_i46141_2_, final float p_i46141_3_) {
        super(p_i46141_1_);
    }
    
    protected ResourceLocation getEntityTexture(final EntitySlime entity) {
        return RenderThaumicSlime.slimeTexture;
    }
    
    static {
        slimeTexture = new ResourceLocation("thaumcraft", "textures/entity/tslime.png");
    }
}
