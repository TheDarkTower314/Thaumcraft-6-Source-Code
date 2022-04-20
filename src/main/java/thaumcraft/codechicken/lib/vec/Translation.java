// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GlStateManager;

public class Translation extends Transformation
{
    public Vector3 vec;
    
    public Translation(final Vector3 vec) {
        this.vec = vec;
    }
    
    public Translation(final double x, final double y, final double z) {
        this(new Vector3(x, y, z));
    }
    
    @Override
    public void apply(final Vector3 vec) {
        vec.add(this.vec);
    }
    
    @Override
    public void applyN(final Vector3 normal) {
    }
    
    @Override
    public void apply(final Matrix4 mat) {
        mat.translate(this.vec);
    }
    
    @Override
    public Transformation at(final Vector3 point) {
        return this;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void glApply() {
        GlStateManager.translate(this.vec.x, this.vec.y, this.vec.z);
    }
    
    @Override
    public Transformation inverse() {
        return new Translation(-this.vec.x, -this.vec.y, -this.vec.z);
    }
    
    @Override
    public Transformation merge(final Transformation next) {
        if (next instanceof Translation) {
            return new Translation(this.vec.copy().add(((Translation)next).vec));
        }
        return null;
    }
    
    @Override
    public boolean isRedundant() {
        return this.vec.equalsT(Vector3.zero);
    }
    
    @Override
    public String toString() {
        final MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Translation(" + new BigDecimal(this.vec.x, cont) + ", " + new BigDecimal(this.vec.y, cont) + ", " + new BigDecimal(this.vec.z, cont) + ")";
    }
}
