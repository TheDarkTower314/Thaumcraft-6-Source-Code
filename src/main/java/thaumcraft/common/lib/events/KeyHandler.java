package thaumcraft.common.lib.events;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;
import thaumcraft.common.lib.network.misc.PacketItemKeyToServer;


@Mod.EventBusSubscriber({ Side.CLIENT })
public class KeyHandler
{
    public static KeyBinding keyF;
    public static KeyBinding keyG;
    private static boolean keyPressedF;
    private boolean keyPressedH;
    private static boolean keyPressedG;
    public static boolean radialActive;
    public static boolean radialLock;
    public static long lastPressF;
    public static long lastPressH;
    public static long lastPressG;
    
    public KeyHandler() {
        keyPressedH = false;
        ClientRegistry.registerKeyBinding(KeyHandler.keyF);
        ClientRegistry.registerKeyBinding(KeyHandler.keyG);
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            if (KeyHandler.keyF.isKeyDown()) {
                if (FMLClientHandler.instance().getClient().inGameHasFocus) {
                    EntityPlayer player = event.player;
                    if (player != null) {
                        if (!KeyHandler.keyPressedF) {
                            KeyHandler.lastPressF = System.currentTimeMillis();
                            KeyHandler.radialLock = false;
                        }
                        if (!KeyHandler.radialLock && ((player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ICaster) || (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof ICaster))) {
                            if (player.isSneaking()) {
                                PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer("REMOVE"));
                            }
                            else {
                                KeyHandler.radialActive = true;
                            }
                        }
                        else if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ItemGolemBell && !KeyHandler.keyPressedF) {
                            PacketHandler.INSTANCE.sendToServer(new PacketItemKeyToServer(0));
                        }
                    }
                    KeyHandler.keyPressedF = true;
                }
            }
            else {
                KeyHandler.radialActive = false;
                if (KeyHandler.keyPressedF) {
                    KeyHandler.lastPressF = System.currentTimeMillis();
                }
                KeyHandler.keyPressedF = false;
            }
            if (KeyHandler.keyG.isKeyDown()) {
                if (FMLClientHandler.instance().getClient().inGameHasFocus) {
                    EntityPlayer player = event.player;
                    if (player != null && !KeyHandler.keyPressedG) {
                        KeyHandler.lastPressG = System.currentTimeMillis();
                        PacketHandler.INSTANCE.sendToServer(new PacketItemKeyToServer(1, Keyboard.isKeyDown(29) ? 1 : (Keyboard.isKeyDown(42) ? 2 : 0)));
                    }
                    KeyHandler.keyPressedG = true;
                }
            }
            else {
                if (KeyHandler.keyPressedG) {
                    KeyHandler.lastPressG = System.currentTimeMillis();
                }
                KeyHandler.keyPressedG = false;
            }
        }
    }
    
    static {
        KeyHandler.keyF = new KeyBinding("Change Caster Focus", 33, "key.categories.thaumcraft");
        KeyHandler.keyG = new KeyBinding("Misc Caster Toggle", 34, "key.categories.thaumcraft");
        KeyHandler.keyPressedF = false;
        KeyHandler.keyPressedG = false;
        KeyHandler.radialActive = false;
        KeyHandler.radialLock = false;
        KeyHandler.lastPressF = 0L;
        KeyHandler.lastPressH = 0L;
        KeyHandler.lastPressG = 0L;
    }
}
