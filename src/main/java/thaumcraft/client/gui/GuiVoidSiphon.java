// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.ARBShaderObjects;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.lib.ender.ShaderCallback;
import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.container.ContainerVoidSiphon;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiVoidSiphon extends GuiContainer
{
    private TileVoidSiphon inventory;
    private ContainerVoidSiphon container;
    private EntityPlayer player;
    CoreGLE gle;
    private final ShaderCallback shaderCallback;
    private static final ResourceLocation starsTexture;
    ResourceLocation tex;
    
    public GuiVoidSiphon(final InventoryPlayer par1InventoryPlayer, final TileVoidSiphon tileVoidSiphon) {
        super(new ContainerVoidSiphon(par1InventoryPlayer, tileVoidSiphon));
        container = null;
        player = null;
        gle = new CoreGLE();
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_void_siphon.png");
        xSize = 176;
        ySize = 166;
        inventory = tileVoidSiphon;
        container = (ContainerVoidSiphon) inventorySlots;
        player = par1InventoryPlayer.player;
        shaderCallback = new ShaderCallback() {
            @Override
            public void call(final int shader) {
                final Minecraft mc = Minecraft.getMinecraft();
                final int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
                ARBShaderObjects.glUniform1fARB(x, (float)(mc.player.rotationYaw * 2.0f * 3.141592653589793 / 360.0));
                final int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
                ARBShaderObjects.glUniform1fARB(z, -(float)(mc.player.rotationPitch * 2.0f * 3.141592653589793 / 360.0));
            }
        };
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int mx, final int my) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex);
        final int k = (width - xSize) / 2;
        final int l = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
    }
    
    static {
        starsTexture = new ResourceLocation("textures/entity/end_portal.png");
    }
}
