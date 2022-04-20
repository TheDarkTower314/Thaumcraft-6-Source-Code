// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import java.util.Iterator;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.FocusEngine;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashMap;
import thaumcraft.api.casters.FocusNode;

public class FocusElementNode
{
    public int x;
    public int y;
    public int id;
    public boolean target;
    public boolean trajectory;
    public int parent;
    public int[] children;
    public float complexityMultiplier;
    public FocusNode node;
    
    public FocusElementNode() {
        this.target = false;
        this.trajectory = false;
        this.parent = -1;
        this.children = new int[0];
        this.complexityMultiplier = 1.0f;
        this.node = null;
    }
    
    public float getPower(final HashMap<Integer, FocusElementNode> data) {
        if (this.node == null) {
            return 1.0f;
        }
        float pow = this.node.getPowerMultiplier();
        final FocusElementNode p = data.get(this.parent);
        if (p != null && p.node != null) {
            pow *= p.getPower(data);
        }
        return pow;
    }
    
    public void deserialize(final NBTTagCompound nbt) {
        this.x = nbt.getInteger("x");
        this.y = nbt.getInteger("y");
        this.id = nbt.getInteger("id");
        this.target = nbt.getBoolean("target");
        this.trajectory = nbt.getBoolean("trajectory");
        this.parent = nbt.getInteger("parent");
        this.children = nbt.getIntArray("children");
        this.complexityMultiplier = nbt.getFloat("complexity");
        final IFocusElement fe = FocusEngine.getElement(nbt.getString("key"));
        if (fe != null) {
            this.node = (FocusNode)fe;
            ((FocusNode)fe).initialize();
            if (((FocusNode)fe).getSettingList() != null) {
                for (final String ns : ((FocusNode)fe).getSettingList()) {
                    ((FocusNode)fe).getSetting(ns).setValue(nbt.getInteger("setting." + ns));
                }
            }
        }
    }
    
    public NBTTagCompound serialize() {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x", this.x);
        nbt.setInteger("y", this.y);
        nbt.setInteger("id", this.id);
        nbt.setBoolean("target", this.target);
        nbt.setBoolean("trajectory", this.trajectory);
        nbt.setInteger("parent", this.parent);
        nbt.setIntArray("children", this.children);
        nbt.setFloat("complexity", this.complexityMultiplier);
        if (this.node != null) {
            nbt.setString("key", this.node.getKey());
            if (this.node.getSettingList() != null) {
                for (final String ns : this.node.getSettingList()) {
                    nbt.setInteger("setting." + ns, this.node.getSettingValue(ns));
                }
            }
        }
        return nbt;
    }
}
