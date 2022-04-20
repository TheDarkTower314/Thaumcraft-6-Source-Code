// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import thaumcraft.api.crafting.CrucibleRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import net.minecraft.client.Minecraft;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

@SideOnly(Side.CLIENT)
public class TileThaumatoriumRenderer extends TileEntitySpecialRenderer
{
    EntityItem entityitem;
    
    public TileThaumatoriumRenderer() {
        this.entityitem = null;
    }
    
    public void renderTileEntityAt(final TileThaumatorium tile, final double par2, final double par4, final double par6, final float par8) {
        final EnumFacing facing = BlockStateUtils.getFacing(tile.getBlockMetadata());
        if (tile != null && tile.getWorld() != null && tile.recipeHash != null && tile.recipeHash.size() > 0) {
            final int stack = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted / 20 % tile.recipeHash.size();
            final CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(tile.recipeHash.get(stack));
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
                final ItemStack is = recipe.getRecipeOutput().copy();
                is.setCount(1);
                this.entityitem = new EntityItem(tile.getWorld(), 0.0, 0.0, 0.0, is);
                this.entityitem.hoverStart = 0.0f;
                final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
                rendermanager.renderEntity(this.entityitem, 0.0, 0.0, 0.0, 0.0f, 0.0f, false);
                GL11.glPopMatrix();
            }
        }
    }
    
    public void render(final TileEntity te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        this.renderTileEntityAt((TileThaumatorium)te, x, y, z, partialTicks);
    }
}
