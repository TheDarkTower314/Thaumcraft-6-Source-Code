// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.vec;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

public class TransformationList extends Transformation
{
    private ArrayList<Transformation> transformations;
    private Matrix4 mat;
    
    public TransformationList(final Transformation... transforms) {
        this.transformations = new ArrayList<Transformation>();
        for (final Transformation t : transforms) {
            if (t instanceof TransformationList) {
                this.transformations.addAll(((TransformationList)t).transformations);
            }
            else {
                this.transformations.add(t);
            }
        }
        this.compact();
    }
    
    public Matrix4 compile() {
        if (this.mat == null) {
            this.mat = new Matrix4();
            for (int i = this.transformations.size() - 1; i >= 0; --i) {
                this.transformations.get(i).apply(this.mat);
            }
        }
        return this.mat;
    }
    
    public Matrix4 reverseCompile() {
        final Matrix4 mat = new Matrix4();
        for (final Transformation t : this.transformations) {
            t.apply(mat);
        }
        return mat;
    }
    
    @Override
    public void apply(final Vector3 vec) {
        if (this.mat != null) {
            this.mat.apply(vec);
        }
        else {
            for (int i = 0; i < this.transformations.size(); ++i) {
                this.transformations.get(i).apply(vec);
            }
        }
    }
    
    @Override
    public void applyN(final Vector3 normal) {
        if (this.mat != null) {
            this.mat.applyN(normal);
        }
        else {
            for (int i = 0; i < this.transformations.size(); ++i) {
                this.transformations.get(i).applyN(normal);
            }
        }
    }
    
    @Override
    public void apply(final Matrix4 mat) {
        mat.multiply(this.compile());
    }
    
    @Override
    public TransformationList with(final Transformation t) {
        if (t.isRedundant()) {
            return this;
        }
        this.mat = null;
        if (t instanceof TransformationList) {
            this.transformations.addAll(((TransformationList)t).transformations);
        }
        else {
            this.transformations.add(t);
        }
        this.compact();
        return this;
    }
    
    public TransformationList prepend(final Transformation t) {
        if (t.isRedundant()) {
            return this;
        }
        this.mat = null;
        if (t instanceof TransformationList) {
            this.transformations.addAll(0, ((TransformationList)t).transformations);
        }
        else {
            this.transformations.add(0, t);
        }
        this.compact();
        return this;
    }
    
    private void compact() {
        final ArrayList<Transformation> newList = new ArrayList<Transformation>(this.transformations.size());
        final Iterator<Transformation> iterator = this.transformations.iterator();
        Transformation prev = null;
        while (iterator.hasNext()) {
            Transformation t = iterator.next();
            if (t.isRedundant()) {
                continue;
            }
            if (prev != null) {
                final Transformation m = prev.merge(t);
                if (m == null) {
                    newList.add(prev);
                }
                else if (m.isRedundant()) {
                    t = null;
                }
                else {
                    t = m;
                }
            }
            prev = t;
        }
        if (prev != null) {
            newList.add(prev);
        }
        if (newList.size() < this.transformations.size()) {
            this.transformations = newList;
            this.mat = null;
        }
        if (this.transformations.size() > 3 && this.mat == null) {
            this.compile();
        }
    }
    
    @Override
    public boolean isRedundant() {
        return this.transformations.size() == 0;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void glApply() {
        for (int i = this.transformations.size() - 1; i >= 0; --i) {
            this.transformations.get(i).glApply();
        }
    }
    
    @Override
    public Transformation inverse() {
        final TransformationList rev = new TransformationList(new Transformation[0]);
        for (int i = this.transformations.size() - 1; i >= 0; --i) {
            rev.with(transformations.get(i).inverse());
        }
        return rev;
    }
    
    @Override
    public String toString() {
        String s = "";
        for (final Transformation t : this.transformations) {
            s = s + "\n" + t.toString();
        }
        return s.trim();
    }
}
