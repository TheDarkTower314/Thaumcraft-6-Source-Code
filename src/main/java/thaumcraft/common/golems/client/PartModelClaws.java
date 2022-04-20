// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.client;

import net.minecraft.client.renderer.GlStateManager;
import thaumcraft.api.golems.IGolemAPI;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.parts.PartModel;

public class PartModelClaws extends PartModel
{
    float f;
    
    public PartModelClaws(final ResourceLocation objModel, final ResourceLocation objTexture, final EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
        this.f = 0.0f;
    }
    
    @Override
    public void preRenderObjectPart(final String partName, final IGolemAPI golem, final float partialTicks, final EnumLimbSide side) {
        if (partName.startsWith("claw")) {
            this.f = 0.0f;
            this.f = golem.getGolemEntity().getSwingProgress(partialTicks) * 4.1f;
            this.f *= this.f;
            GlStateManager.translate(0.0, -0.2, 0.0);
            GlStateManager.rotate(this.f, partName.endsWith("1") ? 1.0f : -1.0f, 0.0f, 0.0f);
        }
    }
}
