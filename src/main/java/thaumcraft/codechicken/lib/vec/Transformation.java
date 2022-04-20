// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.codechicken.lib.render.CCRenderState;

public abstract class Transformation extends ITransformation<Vector3, Transformation> implements CCRenderState.IVertexOperation
{
    public static final int operationIndex;
    
    public abstract void applyN(final Vector3 p0);
    
    public abstract void apply(final Matrix4 p0);
    
    @Override
    public Transformation at(final Vector3 point) {
        return new TransformationList(new Transformation[] { new Translation(-point.x, -point.y, -point.z), this, point.translation() });
    }
    
    @Override
    public TransformationList with(final Transformation t) {
        return new TransformationList(new Transformation[] { this, t });
    }
    
    @SideOnly(Side.CLIENT)
    public abstract void glApply();
    
    @Override
    public boolean load() {
        CCRenderState.pipeline.addRequirement(CCRenderState.normalAttrib.operationID());
        return !this.isRedundant();
    }
    
    @Override
    public void operate() {
        this.apply(CCRenderState.vert.vec);
        if (CCRenderState.normalAttrib.active) {
            this.applyN(CCRenderState.normal);
        }
    }
    
    @Override
    public int operationID() {
        return Transformation.operationIndex;
    }
    
    static {
        operationIndex = CCRenderState.registerOperation();
    }
}
