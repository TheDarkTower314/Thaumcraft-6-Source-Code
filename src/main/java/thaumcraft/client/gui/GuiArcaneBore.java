package thaumcraft.client.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.container.ContainerArcaneBore;
import thaumcraft.common.entities.construct.EntityArcaneBore;


@SideOnly(Side.CLIENT)
public class GuiArcaneBore extends GuiContainer
{
    EntityArcaneBore turret;
    ResourceLocation tex;
    
    public GuiArcaneBore(InventoryPlayer par1InventoryPlayer, World world, EntityArcaneBore t) {
        super(new ContainerArcaneBore(par1InventoryPlayer, world, t));
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_arcanebore.png");
        xSize = 175;
        ySize = 232;
        turret = t;
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        int h = (int)(39.0f * (turret.getHealth() / turret.getMaxHealth()));
        drawTexturedModalRect(k + 68, l + 59, 192, 48, h, 6);
        if (turret.getHeldItemMainhand() != null && !turret.getHeldItemMainhand().isEmpty() && turret.getHeldItemMainhand().getItemDamage() + 1 >= turret.getHeldItemMainhand().getMaxDamage()) {
            drawTexturedModalRect(k + 80, l + 29, 240, 0, 16, 16);
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(k + 124), (float)(l + 18), 505.0f);
        GL11.glScalef(0.5f, 0.5f, 0.0f);
        String text = "Width: " + (1 + turret.getDigRadius() * 2);
        fontRenderer.drawStringWithShadow(text, 0.0f, 0.0f, 16777215);
        text = "Depth: " + turret.getDigDepth();
        fontRenderer.drawStringWithShadow(text, 64.0f, 0.0f, 16777215);
        text = "Speed: +" + turret.getDigSpeed(Blocks.STONE.getDefaultState());
        fontRenderer.drawStringWithShadow(text, 0.0f, 10.0f, 16777215);
        int base = 0;
        int refining = turret.getRefining();
        int fortune = turret.getFortune();
        if (turret.hasSilkTouch() || refining > 0 || fortune > 0) {
            text = "Other properties:";
            fontRenderer.drawStringWithShadow(text, 0.0f, 24.0f, 16777215);
        }
        if (refining > 0) {
            text = "Refining " + refining;
            fontRenderer.drawStringWithShadow(text, 4.0f, (float)(34 + base), 12632256);
            base += 9;
        }
        if (fortune > 0) {
            text = "Fortune " + fortune;
            fontRenderer.drawStringWithShadow(text, 4.0f, (float)(34 + base), 15648330);
            base += 9;
        }
        if (turret.hasSilkTouch()) {
            text = "Silk Touch";
            fontRenderer.drawStringWithShadow(text, 4.0f, (float)(34 + base), 8421631);
            base += 9;
        }
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
}
