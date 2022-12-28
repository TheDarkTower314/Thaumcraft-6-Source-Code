package thaumcraft.common.tiles.essentia;
import thaumcraft.api.aspects.Aspect;


public class TileTubeRestrict extends TileTube
{
    @Override
    void calculateSuction(Aspect filter, boolean restrict, boolean dir) {
        super.calculateSuction(filter, true, dir);
    }
}
