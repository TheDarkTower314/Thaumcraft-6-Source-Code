package thaumcraft.common.golems.client;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;
import thaumcraft.common.golems.parts.GolemLegWheels;


public class PartModelWheel extends PartModel
{
    public PartModelWheel(ResourceLocation objModel, ResourceLocation objTexture, EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
    }
    
    @Override
    public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) {
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
