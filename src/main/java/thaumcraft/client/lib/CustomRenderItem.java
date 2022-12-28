package thaumcraft.client.lib;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;


public class CustomRenderItem extends RenderItem
{
    public CustomRenderItem() {
        super(null, Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager(), null);
    }
    
    public void registerItems() {
    }
    
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
        if (stack != null && !stack.isEmpty() && stack.getCount() <= 0) {
            text = TextFormatting.GOLD + "*";
        }
        Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text);
    }
    
    protected void registerItem(Item itm, int subType, String identifier) {
    }
    
    protected void registerBlock(Block blk, int subType, String identifier) {
    }
    
    public ItemModelMesher getItemModelMesher() {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
    }
    
    public void renderItem(ItemStack stack, IBakedModel model) {
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
    }
    
    public boolean shouldRenderItemIn3D(ItemStack stack) {
        return Minecraft.getMinecraft().getRenderItem().shouldRenderItemIn3D(stack);
    }
    
    public void renderItem(ItemStack p_181564_1_, ItemCameraTransforms.TransformType p_181564_2_) {
        Minecraft.getMinecraft().getRenderItem().renderItem(p_181564_1_, p_181564_2_);
    }
    
    public IBakedModel getItemModelWithOverrides(ItemStack p_184393_1_, World p_184393_2_, EntityLivingBase p_184393_3_) {
        return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(p_184393_1_, p_184393_2_, p_184393_3_);
    }
    
    public void renderItem(ItemStack p_184392_1_, EntityLivingBase p_184392_2_, ItemCameraTransforms.TransformType p_184392_3_, boolean p_184392_4_) {
        Minecraft.getMinecraft().getRenderItem().renderItem(p_184392_1_, p_184392_2_, p_184392_3_, p_184392_4_);
    }
    
    public void renderItemAndEffectIntoGUI(EntityLivingBase p_184391_1_, ItemStack p_184391_2_, int p_184391_3_, int p_184391_4_) {
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(p_184391_1_, p_184391_2_, p_184391_3_, p_184391_4_);
    }
    
    public void renderItemIntoGUI(ItemStack stack, int x, int y) {
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x, y);
    }
    
    public void renderItemAndEffectIntoGUI(ItemStack stack, int xPosition, int yPosition) {
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, xPosition, yPosition);
    }
    
    public void renderItemOverlays(FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(fr, stack, xPosition, yPosition);
    }
    
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Minecraft.getMinecraft().getRenderItem().onResourceManagerReload(resourceManager);
    }
}
