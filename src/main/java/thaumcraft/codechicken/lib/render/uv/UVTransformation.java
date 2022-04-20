// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.render.uv;

import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.codechicken.lib.vec.ITransformation;

public abstract class UVTransformation extends ITransformation<UV, UVTransformation> implements CCRenderState.IVertexOperation
{
    public static final int operationIndex;
    
    @Override
    public UVTransformation at(final UV point) {
        return new UVTransformationList(new UVTransformation[] { new UVTranslation(-point.u, -point.v), this, new UVTranslation(point.u, point.v) });
    }
    
    @Override
    public UVTransformationList with(final UVTransformation t) {
        return new UVTransformationList(new UVTransformation[] { this, t });
    }
    
    @Override
    public boolean load() {
        return !this.isRedundant();
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
