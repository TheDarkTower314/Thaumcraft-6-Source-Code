package thaumcraft.common.golems;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.api.golems.parts.PartModel;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.golems.client.PartModelBreakers;
import thaumcraft.common.golems.client.PartModelClaws;
import thaumcraft.common.golems.client.PartModelDarts;
import thaumcraft.common.golems.client.PartModelHauler;
import thaumcraft.common.golems.client.PartModelWheel;
import thaumcraft.common.golems.parts.GolemArmDart;
import thaumcraft.common.golems.parts.GolemLegLevitator;
import thaumcraft.common.golems.parts.GolemLegWheels;


public class GolemProperties implements IGolemProperties
{
    private long data;
    private Set<EnumGolemTrait> traitCache;
    
    public GolemProperties() {
        data = 0L;
        traitCache = null;
    }
    
    @Override
    public Set<EnumGolemTrait> getTraits() {
        if (traitCache == null) {
            traitCache = new HashSet<EnumGolemTrait>();
            for (EnumGolemTrait trait : getMaterial().traits) {
                addTraitSmart(trait);
            }
            for (EnumGolemTrait trait : getHead().traits) {
                addTraitSmart(trait);
            }
            for (EnumGolemTrait trait : getArms().traits) {
                addTraitSmart(trait);
            }
            for (EnumGolemTrait trait : getLegs().traits) {
                addTraitSmart(trait);
            }
            for (EnumGolemTrait trait : getAddon().traits) {
                addTraitSmart(trait);
            }
        }
        return traitCache;
    }
    
    private void addTraitSmart(EnumGolemTrait trait) {
        if (trait.opposite != null && traitCache.contains(trait.opposite)) {
            traitCache.remove(trait.opposite);
        }
        else {
            traitCache.add(trait);
        }
    }
    
    @Override
    public boolean hasTrait(EnumGolemTrait trait) {
        return getTraits().contains(trait);
    }
    
    @Override
    public void setMaterial(GolemMaterial mat) {
        data = ThaumcraftApiHelper.setByteInLong(data, mat.id, 0);
        traitCache = null;
    }
    
    @Override
    public GolemMaterial getMaterial() {
        return GolemMaterial.getMaterials()[ThaumcraftApiHelper.getByteInLong(data, 0)];
    }
    
    @Override
    public void setHead(GolemHead mat) {
        data = ThaumcraftApiHelper.setByteInLong(data, mat.id, 1);
        traitCache = null;
    }
    
    @Override
    public GolemHead getHead() {
        return GolemHead.getHeads()[ThaumcraftApiHelper.getByteInLong(data, 1)];
    }
    
    @Override
    public void setArms(GolemArm mat) {
        data = ThaumcraftApiHelper.setByteInLong(data, mat.id, 2);
        traitCache = null;
    }
    
    @Override
    public GolemArm getArms() {
        return GolemArm.getArms()[ThaumcraftApiHelper.getByteInLong(data, 2)];
    }
    
    @Override
    public void setLegs(GolemLeg mat) {
        data = ThaumcraftApiHelper.setByteInLong(data, mat.id, 3);
        traitCache = null;
    }
    
    @Override
    public GolemLeg getLegs() {
        return GolemLeg.getLegs()[ThaumcraftApiHelper.getByteInLong(data, 3)];
    }
    
    @Override
    public void setAddon(GolemAddon mat) {
        data = ThaumcraftApiHelper.setByteInLong(data, mat.id, 4);
        traitCache = null;
    }
    
    @Override
    public GolemAddon getAddon() {
        return GolemAddon.getAddons()[ThaumcraftApiHelper.getByteInLong(data, 4)];
    }
    
    @Override
    public void setRank(int rank) {
        data = ThaumcraftApiHelper.setByteInLong(data, (byte)rank, 5);
    }
    
    @Override
    public int getRank() {
        return ThaumcraftApiHelper.getByteInLong(data, 5);
    }
    
    public static IGolemProperties fromLong(long d) {
        GolemProperties out = new GolemProperties();
        out.data = d;
        return out;
    }
    
    @Override
    public long toLong() {
        return data;
    }
    
    @Override
    public ItemStack[] generateComponents() {
        ArrayList<ItemStack> comps = new ArrayList<ItemStack>();
        ItemStack base = getMaterial().componentBase;
        ItemStack mech = getMaterial().componentMechanism;
        addToList(comps, base, 2);
        addToList(comps, mech, 1);
        addToListFromComps(comps, getArms().components, getMaterial());
        addToListFromComps(comps, getLegs().components, getMaterial());
        addToListFromComps(comps, getHead().components, getMaterial());
        addToListFromComps(comps, getAddon().components, getMaterial());
        return comps.toArray(new ItemStack[0]);
    }
    
    private static void addToListFromComps(ArrayList<ItemStack> comps, Object[] objs, GolemMaterial mat) {
        for (Object o : objs) {
            if (o instanceof ItemStack) {
                addToList(comps, (ItemStack)o, 1);
            }
            else if (o instanceof String) {
                String s = (String)o;
                if (s.equalsIgnoreCase("base")) {
                    addToList(comps, mat.componentBase, 1);
                }
                else if (s.equalsIgnoreCase("mech")) {
                    addToList(comps, mat.componentMechanism, 1);
                }
            }
        }
    }
    
    private static void addToList(ArrayList<ItemStack> comps, ItemStack newItem, int mult) {
        for (ItemStack stack : comps) {
            if (stack.isItemEqual(newItem) && ItemStack.areItemStackTagsEqual(stack, newItem)) {
                stack.grow(newItem.getCount() * mult);
                return;
            }
        }
        ItemStack stack2 = newItem.copy();
        stack2.setCount(stack2.getCount() * mult);
        comps.add(stack2);
    }
    
    static {
        GolemMaterial.register(new GolemMaterial("WOOD", new String[] { "MATSTUDWOOD" }, new ResourceLocation("thaumcraft", "textures/entity/golems/mat_wood.png"), 5059370, 6, 2, 1, new ItemStack(BlocksTC.plankGreatwood, 1, 0), new ItemStack(ItemsTC.mechanismSimple), new EnumGolemTrait[] { EnumGolemTrait.LIGHT }));
        GolemMaterial.register(new GolemMaterial("IRON", new String[] { "MATSTUDIRON" }, new ResourceLocation("thaumcraft", "textures/entity/golems/mat_iron.png"), 16777215, 20, 8, 3, new ItemStack(ItemsTC.plate, 1, 1), new ItemStack(ItemsTC.mechanismSimple), new EnumGolemTrait[] { EnumGolemTrait.HEAVY, EnumGolemTrait.FIREPROOF, EnumGolemTrait.BLASTPROOF }));
        GolemMaterial.register(new GolemMaterial("CLAY", new String[] { "MATSTUDCLAY" }, new ResourceLocation("thaumcraft", "textures/entity/golems/mat_clay.png"), 13071447, 10, 4, 2, new ItemStack(Blocks.HARDENED_CLAY, 1, 0), new ItemStack(ItemsTC.mechanismSimple), new EnumGolemTrait[] { EnumGolemTrait.FIREPROOF }));
        GolemMaterial.register(new GolemMaterial("BRASS", new String[] { "MATSTUDBRASS" }, new ResourceLocation("thaumcraft", "textures/entity/golems/mat_brass.png"), 15638812, 16, 6, 3, new ItemStack(ItemsTC.plate, 1, 0), new ItemStack(ItemsTC.mechanismSimple), new EnumGolemTrait[] { EnumGolemTrait.LIGHT }));
        GolemMaterial.register(new GolemMaterial("THAUMIUM", new String[] { "MATSTUDTHAUMIUM" }, new ResourceLocation("thaumcraft", "textures/entity/golems/mat_thaumium.png"), 5257074, 24, 10, 4, new ItemStack(ItemsTC.plate, 1, 2), new ItemStack(ItemsTC.mechanismSimple), new EnumGolemTrait[] { EnumGolemTrait.HEAVY, EnumGolemTrait.FIREPROOF, EnumGolemTrait.BLASTPROOF }));
        GolemMaterial.register(new GolemMaterial("VOID", new String[] { "MATSTUDVOID" }, new ResourceLocation("thaumcraft", "textures/entity/golems/mat_void.png"), 1445161, 20, 6, 4, new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.mechanismSimple), new EnumGolemTrait[] { EnumGolemTrait.REPAIR }));
        GolemHead.register(new GolemHead("BASIC", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_basic.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_basic.obj"), null, PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 0) }, new EnumGolemTrait[0]));
        GolemHead.register(new GolemHead("SMART", new String[] { "MINDBIOTHAUMIC" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smart.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_smart.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1) }, new EnumGolemTrait[] { EnumGolemTrait.SMART, EnumGolemTrait.FRAGILE }));
        GolemHead.register(new GolemHead("SMART_ARMORED", new String[] { "MINDBIOTHAUMIC", "GOLEMCOMBATADV" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smartarmor.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_smart_armor.obj"), null, PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1), new ItemStack(ItemsTC.plate), "base", new ItemStack(Blocks.WOOL) }, new EnumGolemTrait[] { EnumGolemTrait.SMART }));
        GolemHead.register(new GolemHead("SCOUT", new String[] { "GOLEMVISION" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_scout.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_scout.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 0), new ItemStack(ItemsTC.modules) }, new EnumGolemTrait[] { EnumGolemTrait.SCOUT, EnumGolemTrait.FRAGILE }));
        GolemHead.register(new GolemHead("SMART_SCOUT", new String[] { "GOLEMVISION", "MINDBIOTHAUMIC" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smartscout.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_scout_smart.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1), new ItemStack(ItemsTC.modules) }, new EnumGolemTrait[] { EnumGolemTrait.SCOUT, EnumGolemTrait.SMART, EnumGolemTrait.FRAGILE }));
        GolemArm.register(new GolemArm("BASIC", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_basic.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_arms_basic.obj"), null, PartModel.EnumAttachPoint.ARMS), new Object[0], new EnumGolemTrait[0]));
        GolemArm.register(new GolemArm("FINE", new String[] { "MATSTUDBRASS" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_fine.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_arms_fine.obj"), null, PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(ItemsTC.mechanismSimple), "base" }, new EnumGolemTrait[] { EnumGolemTrait.DEFT, EnumGolemTrait.FRAGILE }));
        GolemArm.register(new GolemArm("CLAWS", new String[] { "GOLEMCOMBATADV" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_claws.png"), new PartModelClaws(new ResourceLocation("thaumcraft", "models/obj/golem_arms_claws.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_arms_claws.png"), PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(ItemsTC.modules, 1, 1), new ItemStack(Items.SHEARS, 2), "base" }, new EnumGolemTrait[] { EnumGolemTrait.FIGHTER, EnumGolemTrait.CLUMSY, EnumGolemTrait.BRUTAL }));
        GolemArm.register(new GolemArm("BREAKERS", new String[] { "GOLEMBREAKER" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_breakers.png"), new PartModelBreakers(new ResourceLocation("thaumcraft", "models/obj/golem_arms_breakers.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_arms_breakers.png"), PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(Items.DIAMOND, 2), "base", new ItemStack(Blocks.PISTON, 2) }, new EnumGolemTrait[] { EnumGolemTrait.BREAKER, EnumGolemTrait.CLUMSY, EnumGolemTrait.BRUTAL }));
        GolemArm.register(new GolemArm("DARTS", new String[] { "GOLEMCOMBATADV" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_darts.png"), new PartModelDarts(new ResourceLocation("thaumcraft", "models/obj/golem_arms_darter.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_arms_darter.png"), PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(ItemsTC.modules, 1, 1), new ItemStack(Blocks.DISPENSER, 2), new ItemStack(Items.ARROW, 32), "mech" }, new GolemArmDart(), new EnumGolemTrait[] { EnumGolemTrait.FIGHTER, EnumGolemTrait.CLUMSY, EnumGolemTrait.RANGED, EnumGolemTrait.FRAGILE }));
        GolemLeg.register(new GolemLeg("WALKER", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/legs_walker.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_legs_walker.obj"), null, PartModel.EnumAttachPoint.LEGS), new Object[] { "base", "mech" }, new EnumGolemTrait[0]));
        GolemLeg.register(new GolemLeg("ROLLER", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/legs_roller.png"), new PartModelWheel(new ResourceLocation("thaumcraft", "models/obj/golem_legs_wheel.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_legs_wheel.png"), PartModel.EnumAttachPoint.BODY), new Object[] { new ItemStack(Items.BOWL, 2), new ItemStack(Items.LEATHER), "mech" }, new GolemLegWheels(), new EnumGolemTrait[] { EnumGolemTrait.WHEELED }));
        GolemLeg.register(new GolemLeg("CLIMBER", new String[] { "GOLEMCLIMBER" }, new ResourceLocation("thaumcraft", "textures/misc/golem/legs_climber.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_legs_climber.obj"), new ResourceLocation("thaumcraft", "textures/blocks/base_metal.png"), PartModel.EnumAttachPoint.LEGS), new Object[] { new ItemStack(Items.FLINT, 4), "base", "mech", "mech" }, new EnumGolemTrait[] { EnumGolemTrait.CLIMBER }));
        GolemLeg.register(new GolemLeg("FLYER", new String[] { "GOLEMFLYER" }, new ResourceLocation("thaumcraft", "textures/misc/golem/legs_flyer.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_legs_floater.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_legs_floater.png"), PartModel.EnumAttachPoint.BODY), new Object[] { new ItemStack(BlocksTC.levitator), new ItemStack(ItemsTC.plate, 4), new ItemStack(Items.SLIME_BALL), "mech" }, new GolemLegLevitator(), new EnumGolemTrait[] { EnumGolemTrait.FLYER, EnumGolemTrait.FRAGILE }));
        GolemAddon.register(new GolemAddon("NONE", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/blocks/blank.png"), null, new Object[0], new EnumGolemTrait[0]));
        GolemAddon.register(new GolemAddon("ARMORED", new String[] { "GOLEMCOMBATADV" }, new ResourceLocation("thaumcraft", "textures/misc/golem/addon_armored.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_armor.obj"), null, PartModel.EnumAttachPoint.BODY), new Object[] { "base", "base", "base", "base" }, new EnumGolemTrait[] { EnumGolemTrait.ARMORED, EnumGolemTrait.HEAVY }));
        GolemAddon.register(new GolemAddon("FIGHTER", new String[] { "SEALGUARD" }, new ResourceLocation("thaumcraft", "textures/misc/golem/addon_fighter.png"), null, new Object[] { new ItemStack(ItemsTC.modules, 1, 1), "mech" }, new EnumGolemTrait[] { EnumGolemTrait.FIGHTER }));
        GolemAddon.register(new GolemAddon("HAULER", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/addon_hauler.png"), new PartModelHauler(new ResourceLocation("thaumcraft", "models/obj/golem_hauler.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_hauler.png"), PartModel.EnumAttachPoint.BODY), new Object[] { new ItemStack(Items.LEATHER), new ItemStack(Blocks.CHEST) }, new EnumGolemTrait[] { EnumGolemTrait.HAULER }));
    }
}
