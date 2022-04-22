// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.lighting;

import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.render.CCModel;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.codechicken.lib.util.Copyable;

public class LC implements Copyable<LC>
{
    public int side;
    public float fa;
    public float fb;
    public float fc;
    public float fd;
    
    public LC() {
        this(0, 0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public LC(final int s, final float a, final float b, final float c, final float d) {
        side = s;
        fa = a;
        fb = b;
        fc = c;
        fd = d;
    }
    
    public LC set(final int s, final float a, final float b, final float c, final float d) {
        side = s;
        fa = a;
        fb = b;
        fc = c;
        fd = d;
        return this;
    }
    
    public LC set(final LC lc) {
        return set(lc.side, lc.fa, lc.fb, lc.fc, lc.fd);
    }
    
    public LC compute(final Vector3 vec, final Vector3 normal) {
        final int side = CCModel.findSide(normal);
        if (side < 0) {
            return set(12, 1.0f, 0.0f, 0.0f, 0.0f);
        }
        return compute(vec, side);
    }
    
    public LC compute(final Vector3 vec, int side) {
        boolean offset = false;
        switch (side) {
            case 0: {
                offset = (vec.y <= 0.0);
                break;
            }
            case 1: {
                offset = (vec.y >= 1.0);
                break;
            }
            case 2: {
                offset = (vec.z <= 0.0);
                break;
            }
            case 3: {
                offset = (vec.z >= 1.0);
                break;
            }
            case 4: {
                offset = (vec.x <= 0.0);
                break;
            }
            case 5: {
                offset = (vec.x >= 1.0);
                break;
            }
        }
        if (!offset) {
            side += 6;
        }
        return computeO(vec, side);
    }
    
    public LC computeO(final Vector3 vec, final int side) {
        final Vector3 v1 = Rotation.axes[((side & 0xE) + 3) % 6];
        final Vector3 v2 = Rotation.axes[((side & 0xE) + 5) % 6];
        final float d1 = (float)vec.scalarProject(v1);
        final float d2 = 1.0f - d1;
        final float d3 = (float)vec.scalarProject(v2);
        final float d4 = 1.0f - d3;
        return set(side, d2 * d4, d2 * d3, d1 * d4, d1 * d3);
    }
    
    @Override
    public LC copy() {
        return new LC(side, fa, fb, fc, fd);
    }
}
