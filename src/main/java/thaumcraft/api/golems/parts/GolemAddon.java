// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.api.golems.parts;

import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.golems.EnumGolemTrait;
import net.minecraft.util.ResourceLocation;

public class GolemAddon
{
    protected static GolemAddon[] addons;
    public byte id;
    public String key;
    public String[] research;
    public ResourceLocation icon;
    public Object[] components;
    public EnumGolemTrait[] traits;
    public IAddonFunction function;
    public PartModel model;
    private static byte lastID;
    
    public GolemAddon(final String key, final String[] research, final ResourceLocation icon, final PartModel model, final Object[] comp, final EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.icon = icon;
        this.components = comp;
        this.traits = tags;
        this.model = model;
        this.function = null;
    }
    
    public GolemAddon(final String key, final String[] research, final ResourceLocation icon, final PartModel model, final Object[] comp, final IAddonFunction function, final EnumGolemTrait[] tags) {
        this(key, research, icon, model, comp, tags);
        this.function = function;
    }
    
    public static void register(final GolemAddon thing) {
        thing.id = GolemAddon.lastID;
        ++GolemAddon.lastID;
        if (thing.id >= GolemAddon.addons.length) {
            final GolemAddon[] temp = new GolemAddon[thing.id + 1];
            System.arraycopy(GolemAddon.addons, 0, temp, 0, GolemAddon.addons.length);
            GolemAddon.addons = temp;
        }
        GolemAddon.addons[thing.id] = thing;
    }
    
    public String getLocalizedName() {
        return I18n.translateToLocal("golem.addon." + this.key.toLowerCase());
    }
    
    public String getLocalizedDescription() {
        return I18n.translateToLocal("golem.addon.text." + this.key.toLowerCase());
    }
    
    public static GolemAddon[] getAddons() {
        return GolemAddon.addons;
    }
    
    static {
        GolemAddon.addons = new GolemAddon[1];
        GolemAddon.lastID = 0;
    }
    
    public interface IAddonFunction extends IGenericFunction
    {
    }
}
