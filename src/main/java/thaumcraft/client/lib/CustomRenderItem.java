// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;

public class CustomRenderItem extends RenderItem
{
    public CustomRenderItem() {
        super(null, Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager(), null);
    }
    
    public void registerItems() {
    }
    
    public void renderItemOverlayIntoGUI(final FontRenderer fr, final ItemStack stack, final int xPosition, final int yPosition, String text) {
        if (stack != null && !stack.isEmpty() && stack.getCount() <= 0) {
            text = TextFormatting.GOLD + "*";
        }
        Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text);
    }
    
    protected void registerItem(final Item itm, final int subType, final String identifier) {
    }
    
    protected void registerBlock(final Block blk, final int subType, final String identifier) {
    }
    
    public ItemModelMesher getItemModelMesher() {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
    }
    
    public void renderItem(final ItemStack stack, final IBakedModel model) {
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
    }
    
    public boolean shouldRenderItemIn3D(final ItemStack stack) {
        return Minecraft.getMinecraft().getRenderItem().shouldRenderItemIn3D(stack);
    }
    
    public void renderItem(final ItemStack p_181564_1_, final ItemCameraTransforms.TransformType p_181564_2_) {
        Minecraft.getMinecraft().getRenderItem().renderItem(p_181564_1_, p_181564_2_);
    }
    
    public IBakedModel getItemModelWithOverrides(final ItemStack p_184393_1_, final World p_184393_2_, final EntityLivingBase p_184393_3_) {
        return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(p_184393_1_, p_184393_2_, p_184393_3_);
    }
    
    public void renderItem(final ItemStack p_184392_1_, final EntityLivingBase p_184392_2_, final ItemCameraTransforms.TransformType p_184392_3_, final boolean p_184392_4_) {
        Minecraft.getMinecraft().getRenderItem().renderItem(p_184392_1_, p_184392_2_, p_184392_3_, p_184392_4_);
    }
    
    public void renderItemAndEffectIntoGUI(final EntityLivingBase p_184391_1_, final ItemStack p_184391_2_, final int p_184391_3_, final int p_184391_4_) {
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(p_184391_1_, p_184391_2_, p_184391_3_, p_184391_4_);
    }
    
    public void renderItemIntoGUI(final ItemStack stack, final int x, final int y) {
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x, y);
    }
    
    public void renderItemAndEffectIntoGUI(final ItemStack stack, final int xPosition, final int yPosition) {
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, xPosition, yPosition);
    }
    
    public void renderItemOverlays(final FontRenderer fr, final ItemStack stack, final int xPosition, final int yPosition) {
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(fr, stack, xPosition, yPosition);
    }
    
    public void onResourceManagerReload(final IResourceManager resourceManager) {
        Minecraft.getMinecraft().getRenderItem().onResourceManagerReload(resourceManager);
    }
}
