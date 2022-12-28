package thaumcraft.proxies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import thaumcraft.client.ColorHandler;
import thaumcraft.client.lib.ender.ShaderHelper;
import thaumcraft.common.lib.events.KeyHandler;


public class ClientProxy extends CommonProxy
{
    ProxyEntities proxyEntities;
    ProxyTESR proxyTESR;
    KeyHandler kh;
    
    public ClientProxy() {
        proxyEntities = new ProxyEntities();
        proxyTESR = new ProxyTESR();
        kh = new KeyHandler();
    }
    
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        OBJLoader.INSTANCE.addDomain("thaumcraft".toLowerCase());
        ShaderHelper.initShaders();
    }
    
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ColorHandler.registerColourHandlers();
        registerKeyBindings();
        proxyEntities.setupEntityRenderers();
        proxyTESR.setupTESR();
    }
    
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
    
    public void registerKeyBindings() {
        MinecraftForge.EVENT_BUS.register(kh);
    }
    
    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().world;
    }
    
    @Override
    public World getWorld(int dim) {
        return getClientWorld();
    }
    
    @Override
    public boolean getSingleplayer() {
        return Minecraft.getMinecraft().isSingleplayer();
    }
    
    @Override
    public boolean isShiftKeyDown() {
        return GuiScreen.isShiftKeyDown();
    }
    
    public void setOtherBlockRenderers() {
    }
    
    @Override
    public void registerModel(ItemBlock itemBlock) {
        ModelLoader.setCustomModelResourceLocation(itemBlock, 0, new ModelResourceLocation(itemBlock.getRegistryName(), "inventory"));
    }
}
