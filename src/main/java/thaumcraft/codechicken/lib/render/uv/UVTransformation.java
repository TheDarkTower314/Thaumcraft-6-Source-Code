package thaumcraft.codechicken.lib.render.uv;
import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.codechicken.lib.vec.ITransformation;


public abstract class UVTransformation extends ITransformation<UV, UVTransformation> implements CCRenderState.IVertexOperation
{
    public static int operationIndex;
    
    @Override
    public UVTransformation at(UV point) {
        return new UVTransformationList(new UVTranslation(-point.u, -point.v), this, new UVTranslation(point.u, point.v));
    }
    
    @Override
    public UVTransformationList with(UVTransformation t) {
        return new UVTransformationList(this, t);
    }
    
    @Override
    public boolean load() {
        return !isRedundant();
    }
    
    @Override
    public void operate() {
        apply(CCRenderState.vert.uv);
    }
    
    @Override
    public int operationID() {
        return UVTransformation.operationIndex;
    }
    
    static {
        operationIndex = CCRenderState.registerOperation();
    }
}
