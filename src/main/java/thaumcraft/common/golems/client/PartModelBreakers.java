package thaumcraft.common.golems.client;
import java.util.HashMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;


public class PartModelBreakers extends PartModel
{
    private HashMap<Integer, Float[]> ani;
    
    public PartModelBreakers(ResourceLocation objModel, ResourceLocation objTexture, EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
        ani = new HashMap<Integer, Float[]>();
    }
    
    @Override
    public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) {
        if (partName.equals("grinder")) {
            float lastSpeed = 0.0f;
            float lastRot = 0.0f;
            if (ani.containsKey(golem.getGolemEntity().getEntityId())) {
                lastSpeed = ani.get(golem.getGolemEntity().getEntityId())[0];
                lastRot = ani.get(golem.getGolemEntity().getEntityId())[1];
            }
            float f = Math.max(lastSpeed, golem.getGolemEntity().getSwingProgress(partialTicks) * 20.0f);
            float rot = lastRot + f;
            lastSpeed = f * 0.99f;
            ani.put(golem.getGolemEntity().getEntityId(), new Float[] { lastSpeed, rot });
            GlStateManager.translate(0.0, -0.34, 0.0);
            GlStateManager.rotate((golem.getGolemEntity().ticksExisted + partialTicks) / 2.0f + rot + ((side == EnumLimbSide.LEFT) ? 22 : 0), (side == EnumLimbSide.LEFT) ? -1.0f : 1.0f, 0.0f, 0.0f);
        }
    }
}
