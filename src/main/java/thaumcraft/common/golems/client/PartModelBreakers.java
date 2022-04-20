// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.client;

import net.minecraft.client.renderer.GlStateManager;
import thaumcraft.api.golems.IGolemAPI;
import net.minecraft.util.ResourceLocation;
import java.util.HashMap;
import thaumcraft.api.golems.parts.PartModel;

public class PartModelBreakers extends PartModel
{
    private HashMap<Integer, Float[]> ani;
    
    public PartModelBreakers(final ResourceLocation objModel, final ResourceLocation objTexture, final EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
        this.ani = new HashMap<Integer, Float[]>();
    }
    
    @Override
    public void preRenderObjectPart(final String partName, final IGolemAPI golem, final float partialTicks, final EnumLimbSide side) {
        if (partName.equals("grinder")) {
            float lastSpeed = 0.0f;
            float lastRot = 0.0f;
            if (this.ani.containsKey(golem.getGolemEntity().getEntityId())) {
                lastSpeed = this.ani.get(golem.getGolemEntity().getEntityId())[0];
                lastRot = this.ani.get(golem.getGolemEntity().getEntityId())[1];
            }
            final float f = Math.max(lastSpeed, golem.getGolemEntity().getSwingProgress(partialTicks) * 20.0f);
            final float rot = lastRot + f;
            lastSpeed = f * 0.99f;
            this.ani.put(golem.getGolemEntity().getEntityId(), new Float[] { lastSpeed, rot });
            GlStateManager.translate(0.0, -0.34, 0.0);
            GlStateManager.rotate((golem.getGolemEntity().ticksExisted + partialTicks) / 2.0f + rot + ((side == EnumLimbSide.LEFT) ? 22 : 0), (side == EnumLimbSide.LEFT) ? -1.0f : 1.0f, 0.0f, 0.0f);
        }
    }
}
