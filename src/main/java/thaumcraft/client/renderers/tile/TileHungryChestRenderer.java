package thaumcraft.client.renderers.tile;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.devices.TileHungryChest;


@SideOnly(Side.CLIENT)
public class TileHungryChestRenderer extends TileEntitySpecialRenderer
{
    private ModelChest chestModel;
    private static ResourceLocation textureNormal;
    
    public TileHungryChestRenderer() {
        chestModel = new ModelChest();
    }
    
    public void renderTileEntityChestAt(TileHungryChest chest, double par2, double par4, double par6, float par8, int bp) {
        int var9 = 0;
        if (!chest.hasWorld()) {
            var9 = 0;
        }
        else {
            var9 = chest.getBlockMetadata();
        }
        ModelChest var10 = chestModel;
        if (bp >= 0) {
            bindTexture(TileHungryChestRenderer.DESTROY_STAGES[bp]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0f, 4.0f, 1.0f);
            GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            bindTexture(TileHungryChestRenderer.textureNormal);
        }
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        if (bp < 0) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }
        GL11.glTranslatef((float)par2, (float)par4 + 1.0f, (float)par6 + 1.0f);
        GL11.glScalef(1.0f, -1.0f, -1.0f);
        GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        short var11 = 0;
        if (var9 == 2) {
            var11 = 180;
        }
        if (var9 == 3) {
            var11 = 0;
        }
        if (var9 == 4) {
            var11 = 90;
        }
        if (var9 == 5) {
            var11 = -90;
        }
        GL11.glRotatef(var11, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        float var12 = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * par8;
        var12 = 1.0f - var12;
        var12 = 1.0f - var12 * var12 * var12;
        var10.chestLid.rotateAngleX = -(var12 * 3.1415927f / 2.0f);
        var10.renderAll();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (bp >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        renderTileEntityChestAt((TileHungryChest)te, x, y, z, partialTicks, destroyStage);
    }
    
    static {
        textureNormal = new ResourceLocation("thaumcraft", "textures/models/chesthungry.png");
    }
}
