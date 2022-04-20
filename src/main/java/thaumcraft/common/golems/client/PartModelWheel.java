// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.client;

import net.minecraft.client.renderer.GlStateManager;
import thaumcraft.common.golems.parts.GolemLegWheels;
import thaumcraft.api.golems.IGolemAPI;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.parts.PartModel;

public class PartModelWheel extends PartModel
{
    public PartModelWheel(final ResourceLocation objModel, final ResourceLocation objTexture, final EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
    }
    
    @Override
    public void preRenderObjectPart(final String partName, final IGolemAPI golem, final float partialTicks, final EnumLimbSide side) {
        if (partName.equals("wheel")) {
            float lastRot = 0.0f;
            if (GolemLegWheels.ani.containsKey(golem.getGolemEntity().getEntityId())) {
                lastRot = GolemLegWheels.ani.get(golem.getGolemEntity().getEntityId());
            }
            GlStateManager.translate(0.0, -0.375, 0.0);
            GlStateManager.rotate(lastRot, -1.0f, 0.0f, 0.0f);
        }
    }
}
