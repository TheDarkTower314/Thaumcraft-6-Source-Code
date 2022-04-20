// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.api.golems.parts;

import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.golems.EnumGolemTrait;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GolemMaterial
{
    protected static GolemMaterial[] materials;
    public byte id;
    public String key;
    public String[] research;
    public ResourceLocation texture;
    public int itemColor;
    public int healthMod;
    public int armor;
    public int damage;
    public ItemStack componentBase;
    public ItemStack componentMechanism;
    public EnumGolemTrait[] traits;
    private static byte lastID;
    
    public GolemMaterial(final String key, final String[] research, final ResourceLocation texture, final int itemColor, final int hp, final int armor, final int damage, final ItemStack compb, final ItemStack compm, final EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.texture = texture;
        this.itemColor = itemColor;
        this.componentBase = compb;
        this.componentMechanism = compm;
        this.healthMod = hp;
        this.armor = armor;
        this.traits = tags;
        this.damage = damage;
    }
    
    public static void register(final GolemMaterial thing) {
        thing.id = GolemMaterial.lastID;
        ++GolemMaterial.lastID;
        if (thing.id >= GolemMaterial.materials.length) {
            final GolemMaterial[] temp = new GolemMaterial[thing.id + 1];
            System.arraycopy(GolemMaterial.materials, 0, temp, 0, GolemMaterial.materials.length);
            GolemMaterial.materials = temp;
        }
        GolemMaterial.materials[thing.id] = thing;
    }
    
    public String getLocalizedName() {
        return I18n.translateToLocal("golem.material." + this.key.toLowerCase());
    }
    
    public String getLocalizedDescription() {
        return I18n.translateToLocal("golem.material.text." + this.key.toLowerCase());
    }
    
    public static GolemMaterial[] getMaterials() {
        return GolemMaterial.materials;
    }
    
    static {
        GolemMaterial.materials = new GolemMaterial[1];
        GolemMaterial.lastID = 0;
    }
}
