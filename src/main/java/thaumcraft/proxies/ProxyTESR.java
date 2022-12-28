package thaumcraft.proxies;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import thaumcraft.client.renderers.tile.TileAlembicRenderer;
import thaumcraft.client.renderers.tile.TileBannerRenderer;
import thaumcraft.client.renderers.tile.TileBellowsRenderer;
import thaumcraft.client.renderers.tile.TileCentrifugeRenderer;
import thaumcraft.client.renderers.tile.TileCrucibleRenderer;
import thaumcraft.client.renderers.tile.TileDioptraRenderer;
import thaumcraft.client.renderers.tile.TileFocalManipulatorRenderer;
import thaumcraft.client.renderers.tile.TileGolemBuilderRenderer;
import thaumcraft.client.renderers.tile.TileHoleRenderer;
import thaumcraft.client.renderers.tile.TileHungryChestRenderer;
import thaumcraft.client.renderers.tile.TileInfusionMatrixRenderer;
import thaumcraft.client.renderers.tile.TileJarRenderer;
import thaumcraft.client.renderers.tile.TileMirrorRenderer;
import thaumcraft.client.renderers.tile.TilePatternCrafterRenderer;
import thaumcraft.client.renderers.tile.TilePedestalRenderer;
import thaumcraft.client.renderers.tile.TileRechargePedestalRenderer;
import thaumcraft.client.renderers.tile.TileResearchTableRenderer;
import thaumcraft.client.renderers.tile.TileThaumatoriumRenderer;
import thaumcraft.client.renderers.tile.TileTubeBufferRenderer;
import thaumcraft.client.renderers.tile.TileTubeOnewayRenderer;
import thaumcraft.client.renderers.tile.TileTubeValveRenderer;
import thaumcraft.client.renderers.tile.TileVoidSiphonRenderer;
import thaumcraft.common.tiles.crafting.TileCrucible;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.tiles.crafting.TilePatternCrafter;
import thaumcraft.common.tiles.crafting.TilePedestal;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;
import thaumcraft.common.tiles.devices.TileBellows;
import thaumcraft.common.tiles.devices.TileDioptra;
import thaumcraft.common.tiles.devices.TileHungryChest;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;
import thaumcraft.common.tiles.devices.TileRechargePedestal;
import thaumcraft.common.tiles.essentia.TileAlembic;
import thaumcraft.common.tiles.essentia.TileCentrifuge;
import thaumcraft.common.tiles.essentia.TileJar;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;
import thaumcraft.common.tiles.essentia.TileTubeOneway;
import thaumcraft.common.tiles.essentia.TileTubeValve;
import thaumcraft.common.tiles.misc.TileBanner;
import thaumcraft.common.tiles.misc.TileHole;


public class ProxyTESR
{
    public void setupTESR() {
        registerTESR(TileCrucible.class, new TileCrucibleRenderer());
        registerTESR(TileDioptra.class, new TileDioptraRenderer());
        registerTESR(TilePedestal.class, new TilePedestalRenderer());
        registerTESR(TileRechargePedestal.class, new TileRechargePedestalRenderer());
        registerTESR(TileFocalManipulator.class, new TileFocalManipulatorRenderer());
        registerTESR(TileHungryChest.class, new TileHungryChestRenderer());
        registerTESR(TileTubeOneway.class, new TileTubeOnewayRenderer());
        registerTESR(TileTubeValve.class, new TileTubeValveRenderer());
        registerTESR(TileTubeBuffer.class, new TileTubeBufferRenderer());
        registerTESR(TileJar.class, new TileJarRenderer());
        registerTESR(TileBellows.class, new TileBellowsRenderer());
        registerTESR(TileAlembic.class, new TileAlembicRenderer());
        registerTESR(TileInfusionMatrix.class, new TileInfusionMatrixRenderer());
        registerTESR(TileResearchTable.class, new TileResearchTableRenderer());
        registerTESR(TileThaumatorium.class, new TileThaumatoriumRenderer());
        registerTESR(TileCentrifuge.class, new TileCentrifugeRenderer());
        TileMirrorRenderer tmr = new TileMirrorRenderer();
        registerTESR(TileMirror.class, tmr);
        registerTESR(TileMirrorEssentia.class, tmr);
        registerTESR(TileGolemBuilder.class, new TileGolemBuilderRenderer());
        registerTESR(TilePatternCrafter.class, new TilePatternCrafterRenderer());
        registerTESR(TileVoidSiphon.class, new TileVoidSiphonRenderer());
        registerTESR(TileBanner.class, new TileBannerRenderer());
        registerTESR(TileHole.class, new TileHoleRenderer());
    }
    
    private void registerTESR(Class tile, TileEntitySpecialRenderer renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(tile, renderer);
    }
}
