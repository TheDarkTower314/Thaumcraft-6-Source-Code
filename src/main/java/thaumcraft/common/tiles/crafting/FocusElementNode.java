package thaumcraft.common.tiles.crafting;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.IFocusElement;


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
        target = false;
        trajectory = false;
        parent = -1;
        children = new int[0];
        complexityMultiplier = 1.0f;
        node = null;
    }
    
    public float getPower(HashMap<Integer, FocusElementNode> data) {
        if (node == null) {
            return 1.0f;
        }
        float pow = node.getPowerMultiplier();
        FocusElementNode p = data.get(parent);
        if (p != null && p.node != null) {
            pow *= p.getPower(data);
        }
        return pow;
    }
    
    public void deserialize(NBTTagCompound nbt) {
        x = nbt.getInteger("x");
        y = nbt.getInteger("y");
        id = nbt.getInteger("id");
        target = nbt.getBoolean("target");
        trajectory = nbt.getBoolean("trajectory");
        parent = nbt.getInteger("parent");
        children = nbt.getIntArray("children");
        complexityMultiplier = nbt.getFloat("complexity");
        IFocusElement fe = FocusEngine.getElement(nbt.getString("key"));
        if (fe != null) {
            node = (FocusNode)fe;
            ((FocusNode)fe).initialize();
            if (((FocusNode)fe).getSettingList() != null) {
                for (String ns : ((FocusNode)fe).getSettingList()) {
                    ((FocusNode)fe).getSetting(ns).setValue(nbt.getInteger("setting." + ns));
                }
            }
        }
    }
    
    public NBTTagCompound serialize() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x", x);
        nbt.setInteger("y", y);
        nbt.setInteger("id", id);
        nbt.setBoolean("target", target);
        nbt.setBoolean("trajectory", trajectory);
        nbt.setInteger("parent", parent);
        nbt.setIntArray("children", children);
        nbt.setFloat("complexity", complexityMultiplier);
        if (node != null) {
            nbt.setString("key", node.getKey());
            if (node.getSettingList() != null) {
                for (String ns : node.getSettingList()) {
                    nbt.setInteger("setting." + ns, node.getSettingValue(ns));
                }
            }
        }
        return nbt;
    }
}
