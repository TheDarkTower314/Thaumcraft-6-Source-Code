package thaumcraft.proxies;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import thaumcraft.client.renderers.tile.TileHoleRenderer;
import thaumcraft.common.tiles.misc.TileHole;
import thaumcraft.client.renderers.tile.TileBannerRenderer;
import thaumcraft.common.tiles.misc.TileBanner;
import thaumcraft.client.renderers.tile.TileVoidSiphonRenderer;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;
import thaumcraft.client.renderers.tile.TilePatternCrafterRenderer;
import thaumcraft.common.tiles.crafting.TilePatternCrafter;
import thaumcraft.client.renderers.tile.TileGolemBuilderRenderer;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.client.renderers.tile.TileMirrorRenderer;
import thaumcraft.client.renderers.tile.TileCentrifugeRenderer;
import thaumcraft.common.tiles.essentia.TileCentrifuge;
import thaumcraft.client.renderers.tile.TileThaumatoriumRenderer;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import thaumcraft.client.renderers.tile.TileResearchTableRenderer;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import thaumcraft.client.renderers.tile.TileInfusionMatrixRenderer;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.client.renderers.tile.TileAlembicRenderer;
import thaumcraft.common.tiles.essentia.TileAlembic;
import thaumcraft.client.renderers.tile.TileBellowsRenderer;
import thaumcraft.common.tiles.devices.TileBellows;
import thaumcraft.client.renderers.tile.TileJarRenderer;
import thaumcraft.common.tiles.essentia.TileJar;
import thaumcraft.client.renderers.tile.TileTubeBufferRenderer;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;
import thaumcraft.client.renderers.tile.TileTubeValveRenderer;
import thaumcraft.common.tiles.essentia.TileTubeValve;
import thaumcraft.client.renderers.tile.TileTubeOnewayRenderer;
import thaumcraft.common.tiles.essentia.TileTubeOneway;
import thaumcraft.client.renderers.tile.TileHungryChestRenderer;
import thaumcraft.common.tiles.devices.TileHungryChest;
import thaumcraft.client.renderers.tile.TileFocalManipulatorRenderer;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import thaumcraft.client.renderers.tile.TileRechargePedestalRenderer;
import thaumcraft.common.tiles.devices.TileRechargePedestal;
import thaumcraft.client.renderers.tile.TilePedestalRenderer;
import thaumcraft.common.tiles.crafting.TilePedestal;
import thaumcraft.client.renderers.tile.TileDioptraRenderer;
import thaumcraft.common.tiles.devices.TileDioptra;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import thaumcraft.client.renderers.tile.TileCrucibleRenderer;
import thaumcraft.common.tiles.crafting.TileCrucible;

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
