package thaumcraft.client.renderers.tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.crafting.TilePedestal;


@SideOnly(Side.CLIENT)
public class TilePedestalRenderer extends TileEntitySpecialRenderer<TilePedestal>
{
    public void render(TilePedestal ped, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(ped, x, y, z, partialTicks, destroyStage, alpha);
        if (ped != null && !ped.getSyncedStackInSlot(0).isEmpty()) {
            EntityItem entityitem = null;
            float ticks = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + partialTicks;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x + 0.5f, (float)y + 0.75f, (float)z + 0.5f);
            GL11.glScaled(1.25, 1.25, 1.25);
            GL11.glRotatef(ticks % 360.0f, 0.0f, 1.0f, 0.0f);
            ItemStack is = ped.getSyncedStackInSlot(0).copy();
            is.setCount(1);
            entityitem = new EntityItem(Minecraft.getMinecraft().world, 0.0, 0.0, 0.0, is);
            entityitem.hoverStart = 0.0f;
            RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
            rendermanager.renderEntity(entityitem, 0.0, 0.0, 0.0, 0.0f, 0.0f, false);
            GL11.glPopMatrix();
        }
    }
}
