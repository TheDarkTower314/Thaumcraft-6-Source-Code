package thaumcraft.client.renderers.tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


@SideOnly(Side.CLIENT)
public class TileThaumatoriumRenderer extends TileEntitySpecialRenderer
{
    EntityItem entityitem;
    
    public TileThaumatoriumRenderer() {
        entityitem = null;
    }
    
    public void renderTileEntityAt(TileThaumatorium tile, double par2, double par4, double par6, float par8) {
        EnumFacing facing = BlockStateUtils.getFacing(tile.getBlockMetadata());
        if (tile != null && tile.getWorld() != null && tile.recipeHash != null && tile.recipeHash.size() > 0) {
            int stack = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted / 20 % tile.recipeHash.size();
            CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(tile.recipeHash.get(stack));
            if (recipe != null) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)par2 + 0.5f + facing.getFrontOffsetX() / 1.99f, (float)par4 + 1.125f, (float)par6 + 0.5f + facing.getFrontOffsetZ() / 1.99f);
                switch (facing.ordinal()) {
                    case 5: {
                        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                    case 4: {
                        GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                    case 2: {
                        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                }
                GL11.glScaled(0.75, 0.75, 0.75);
                ItemStack is = recipe.getRecipeOutput().copy();
                is.setCount(1);
                entityitem = new EntityItem(tile.getWorld(), 0.0, 0.0, 0.0, is);
                entityitem.hoverStart = 0.0f;
                RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
                rendermanager.renderEntity(entityitem, 0.0, 0.0, 0.0, 0.0f, 0.0f, false);
                GL11.glPopMatrix();
            }
        }
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderTileEntityAt((TileThaumatorium)te, x, y, z, partialTicks);
    }
}
