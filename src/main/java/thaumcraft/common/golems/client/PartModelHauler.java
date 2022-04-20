// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.client;

import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import thaumcraft.api.golems.IGolemAPI;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.parts.PartModel;

public class PartModelHauler extends PartModel
{
    public PartModelHauler(final ResourceLocation objModel, final ResourceLocation objTexture, final EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
    }
    
    @Override
    public void postRenderObjectPart(final String partName, final IGolemAPI golem, final float partialTicks, final EnumLimbSide side) {
        if (golem.getCarrying().size() > 1 && golem.getCarrying().get(1) != null) {
            final ItemStack itemstack = golem.getCarrying().get(1);
            if (itemstack != null && !itemstack.isEmpty()) {
                GlStateManager.pushMatrix();
                final Item item = itemstack.getItem();
                final Minecraft minecraft = Minecraft.getMinecraft();
                GlStateManager.scale(0.375, 0.375, 0.375);
                GlStateManager.translate(0.0f, 0.33f, 0.825f);
                if (!(item instanceof ItemBlock)) {
                    GlStateManager.translate(0.0f, 0.0f, -0.25f);
                }
                minecraft.getItemRenderer().renderItem(golem.getGolemEntity(), itemstack, ItemCameraTransforms.TransformType.HEAD);
                GlStateManager.popMatrix();
            }
        }
    }
}
