package thaumcraft.api.golems.parts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.golems.EnumGolemTrait;


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
    
    public GolemAddon(String key, String[] research, ResourceLocation icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.icon = icon;
        components = comp;
        traits = tags;
        this.model = model;
        function = null;
    }
    
    public GolemAddon(String key, String[] research, ResourceLocation icon, PartModel model, Object[] comp, IAddonFunction function, EnumGolemTrait[] tags) {
        this(key, research, icon, model, comp, tags);
        this.function = function;
    }
    
    public static void register(GolemAddon thing) {
        thing.id = GolemAddon.lastID;
        ++GolemAddon.lastID;
        if (thing.id >= GolemAddon.addons.length) {
            GolemAddon[] temp = new GolemAddon[thing.id + 1];
            System.arraycopy(GolemAddon.addons, 0, temp, 0, GolemAddon.addons.length);
            GolemAddon.addons = temp;
        }
        GolemAddon.addons[thing.id] = thing;
    }
    
    public String getLocalizedName() {
        return I18n.translateToLocal("golem.addon." + key.toLowerCase());
    }
    
    public String getLocalizedDescription() {
        return I18n.translateToLocal("golem.addon.text." + key.toLowerCase());
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
