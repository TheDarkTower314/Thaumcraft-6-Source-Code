package thaumcraft.client.gui;
import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.common.container.ContainerVoidSiphon;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;


@SideOnly(Side.CLIENT)
public class GuiVoidSiphon extends GuiContainer
{
    private TileVoidSiphon inventory;
    private ContainerVoidSiphon container;
    private EntityPlayer player;
    CoreGLE gle;
    private ShaderCallback shaderCallback;
    private static ResourceLocation starsTexture;
    ResourceLocation tex;
    
    public GuiVoidSiphon(InventoryPlayer par1InventoryPlayer, TileVoidSiphon tileVoidSiphon) {
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
            public void call(int shader) {
                Minecraft mc = Minecraft.getMinecraft();
                int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
                ARBShaderObjects.glUniform1fARB(x, (float)(mc.player.rotationYaw * 2.0f * 3.141592653589793 / 360.0));
                int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
                ARBShaderObjects.glUniform1fARB(z, -(float)(mc.player.rotationPitch * 2.0f * 3.141592653589793 / 360.0));
            }
        };
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int mx, int my) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
    }
    
    static {
        starsTexture = new ResourceLocation("textures/entity/end_portal.png");
    }
}
