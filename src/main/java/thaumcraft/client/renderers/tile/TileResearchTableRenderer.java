package thaumcraft.client.renderers.tile;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.block.ModelResearchTable;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.crafting.TileResearchTable;


@SideOnly(Side.CLIENT)
public class TileResearchTableRenderer extends TileEntitySpecialRenderer<TileResearchTable>
{
    private ModelResearchTable tableModel;
    private static ResourceLocation TEX;
    
    public TileResearchTableRenderer() {
        tableModel = new ModelResearchTable();
    }
    
    public void render(TileResearchTable table, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GL11.glPushMatrix();
        bindTexture(TileResearchTableRenderer.TEX);
        GL11.glTranslatef((float)x + 0.5f, (float)y + 1.0f, (float)z + 0.5f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        switch (BlockStateUtils.getFacing(table.getBlockMetadata())) {
            case EAST: {
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case WEST: {
                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case SOUTH: {
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
        }
        if (table.data != null) {
            tableModel.renderScroll(Aspect.ALCHEMY.getColor());
        }
        if (!table.getSyncedStackInSlot(0).isEmpty() && table.getSyncedStackInSlot(0).getItem() instanceof IScribeTools) {
            tableModel.renderInkwell();
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
            GL11.glTranslated(-0.5, 0.1, 0.125);
            GL11.glRotatef(60.0f, 0.0f, 1.0f, 0.0f);
            GL11.glScaled(0.5, 0.5, 0.5);
            UtilsFX.renderItemIn2D("thaumcraft:research/quill", 0.0625f);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glPopMatrix();
    }
    
    static {
        TEX = new ResourceLocation("thaumcraft", "textures/blocks/research_table_model.png");
    }
}
