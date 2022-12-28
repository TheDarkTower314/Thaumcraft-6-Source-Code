package thaumcraft.client.lib.events;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.common.lib.potions.PotionBlurredVision;
import thaumcraft.common.lib.potions.PotionDeathGaze;
import thaumcraft.common.lib.potions.PotionSunScorned;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;


public class ShaderHandler
{
    public static int warpVignette;
    public static int SHADER_DESAT = 0;
    public static int SHADER_BLUR = 1;
    public static int SHADER_HUNGER = 2;
    public static int SHADER_SUNSCORNED = 3;
    public static ResourceLocation[] shader_resources;
    
    protected void checkShaders(TickEvent.PlayerTickEvent event, Minecraft mc) {
        if (event.player.isPotionActive(PotionDeathGaze.instance)) {
            ShaderHandler.warpVignette = 10;
            if (!RenderEventHandler.shaderGroups.containsKey(0)) {
                try {
                    setShader(new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), ShaderHandler.shader_resources[0]), 0);
                }
                catch (JsonSyntaxException ex) {}
                catch (IOException ex2) {}
            }
        }
        else if (RenderEventHandler.shaderGroups.containsKey(0)) {
            deactivateShader(0);
        }
        if (event.player.isPotionActive(PotionBlurredVision.instance)) {
            if (!RenderEventHandler.shaderGroups.containsKey(1)) {
                try {
                    setShader(new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), ShaderHandler.shader_resources[1]), 1);
                }
                catch (JsonSyntaxException ex3) {}
                catch (IOException ex4) {}
            }
        }
        else if (RenderEventHandler.shaderGroups.containsKey(1)) {
            deactivateShader(1);
        }
        if (event.player.isPotionActive(PotionUnnaturalHunger.instance)) {
            if (!RenderEventHandler.shaderGroups.containsKey(2)) {
                try {
                    setShader(new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), ShaderHandler.shader_resources[2]), 2);
                }
                catch (JsonSyntaxException ex5) {}
                catch (IOException ex6) {}
            }
        }
        else if (RenderEventHandler.shaderGroups.containsKey(2)) {
            deactivateShader(2);
        }
        if (event.player.isPotionActive(PotionSunScorned.instance)) {
            if (!RenderEventHandler.shaderGroups.containsKey(3)) {
                try {
                    setShader(new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), ShaderHandler.shader_resources[3]), 3);
                }
                catch (JsonSyntaxException ex7) {}
                catch (IOException ex8) {}
            }
        }
        else if (RenderEventHandler.shaderGroups.containsKey(3)) {
            deactivateShader(3);
        }
    }
    
    void setShader(ShaderGroup target, int shaderId) {
        if (OpenGlHelper.shadersSupported) {
            if (RenderEventHandler.shaderGroups.containsKey(shaderId)) {
                RenderEventHandler.shaderGroups.get(shaderId).deleteShaderGroup();
                RenderEventHandler.shaderGroups.remove(shaderId);
            }
            try {
                if (target == null) {
                    deactivateShader(shaderId);
                }
                else {
                    RenderEventHandler.resetShaders = true;
                    RenderEventHandler.shaderGroups.put(shaderId, target);
                }
            }
            catch (Exception ioexception) {
                RenderEventHandler.shaderGroups.remove(shaderId);
            }
        }
    }
    
    public void deactivateShader(int shaderId) {
        if (RenderEventHandler.shaderGroups.containsKey(shaderId)) {
            RenderEventHandler.shaderGroups.get(shaderId).deleteShaderGroup();
        }
        RenderEventHandler.shaderGroups.remove(shaderId);
    }
    
    static {
        ShaderHandler.warpVignette = 0;
        ShaderHandler.shader_resources = new ResourceLocation[] { new ResourceLocation("shaders/post/desaturatetc.json"), new ResourceLocation("shaders/post/blurtc.json"), new ResourceLocation("shaders/post/hunger.json"), new ResourceLocation("shaders/post/sunscorned.json") };
    }
}
