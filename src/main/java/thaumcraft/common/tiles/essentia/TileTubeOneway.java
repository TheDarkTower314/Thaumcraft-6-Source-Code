package thaumcraft.common.tiles.essentia;
import thaumcraft.api.aspects.Aspect;


public class TileTubeOneway extends TileTube
{
    @Override
    void calculateSuction(Aspect filter, boolean restrict, boolean directional) {
        super.calculateSuction(filter, restrict, true);
    }
    
    @Override
    void equalizeWithNeighbours(boolean directional) {
        super.equalizeWithNeighbours(true);
    }
}
