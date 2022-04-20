// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.render.uv;

import thaumcraft.codechicken.lib.vec.ITransformation;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

public class UVTransformationList extends UVTransformation
{
    private ArrayList<UVTransformation> transformations;
    
    public UVTransformationList(final UVTransformation... transforms) {
        this.transformations = new ArrayList<UVTransformation>();
        for (final UVTransformation t : transforms) {
            if (t instanceof UVTransformationList) {
                this.transformations.addAll(((UVTransformationList)t).transformations);
            }
            else {
                this.transformations.add(t);
            }
        }
        this.compact();
    }
    
    @Override
    public void apply(final UV uv) {
        for (int i = 0; i < this.transformations.size(); ++i) {
            transformations.get(i).apply(uv);
        }
    }
    
    @Override
    public UVTransformationList with(final UVTransformation t) {
        if (t.isRedundant()) {
            return this;
        }
        if (t instanceof UVTransformationList) {
            this.transformations.addAll(((UVTransformationList)t).transformations);
        }
        else {
            this.transformations.add(t);
        }
        this.compact();
        return this;
    }
    
    public UVTransformationList prepend(final UVTransformation t) {
        if (t.isRedundant()) {
            return this;
        }
        if (t instanceof UVTransformationList) {
            this.transformations.addAll(0, ((UVTransformationList)t).transformations);
        }
        else {
            this.transformations.add(0, t);
        }
        this.compact();
        return this;
    }
    
    private void compact() {
        final ArrayList<UVTransformation> newList = new ArrayList<UVTransformation>(this.transformations.size());
        final Iterator<UVTransformation> iterator = this.transformations.iterator();
        UVTransformation prev = null;
        while (iterator.hasNext()) {
            UVTransformation t = iterator.next();
            if (t.isRedundant()) {
                continue;
            }
            if (prev != null) {
                final UVTransformation m = prev.merge(t);
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
        }
    }
    
    @Override
    public boolean isRedundant() {
        return this.transformations.size() == 0;
    }
    
    @Override
    public UVTransformation inverse() {
        final UVTransformationList rev = new UVTransformationList(new UVTransformation[0]);
        for (int i = this.transformations.size() - 1; i >= 0; --i) {
            rev.with(transformations.get(i).inverse());
        }
        return rev;
    }
    
    @Override
    public String toString() {
        String s = "";
        for (final UVTransformation t : this.transformations) {
            s = s + "\n" + t.toString();
        }
        return s.trim();
    }
}
