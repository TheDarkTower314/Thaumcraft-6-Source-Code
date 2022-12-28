package thaumcraft.common.golems.client;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;


public class PartModelClaws extends PartModel
{
    float f;
    
    public PartModelClaws(ResourceLocation objModel, ResourceLocation objTexture, EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
        f = 0.0f;
    }
    
    @Override
    public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) {
        if (partName.startsWith("claw")) {
            f = 0.0f;
            f = golem.getGolemEntity().getSwingProgress(partialTicks) * 4.1f;
            f *= f;
            GlStateManager.translate(0.0, -0.2, 0.0);
            GlStateManager.rotate(f, partName.endsWith("1") ? 1.0f : -1.0f, 0.0f, 0.0f);
        }
    }
}
