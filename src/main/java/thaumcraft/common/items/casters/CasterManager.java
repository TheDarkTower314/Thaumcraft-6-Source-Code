package thaumcraft.common.items.casters;
import baubles.api.BaublesApi;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;


public class CasterManager
{
    static HashMap<Integer, Long> cooldownServer;
    static HashMap<Integer, Long> cooldownClient;
    
    public static float getTotalVisDiscount(EntityPlayer player) {
        int total = 0;
        if (player == null) {
            return 0.0f;
        }
        IInventory baubles = BaublesApi.getBaubles(player);
        for (int a = 0; a < baubles.getSizeInventory(); ++a) {
            if (baubles.getStackInSlot(a) != null && baubles.getStackInSlot(a).getItem() instanceof IVisDiscountGear) {
                total += ((IVisDiscountGear)baubles.getStackInSlot(a).getItem()).getVisDiscount(baubles.getStackInSlot(a), player);
            }
        }
        for (int a = 0; a < 4; ++a) {
            if (player.inventory.armorInventory.get(a).getItem() instanceof IVisDiscountGear) {
                total += ((IVisDiscountGear) player.inventory.armorInventory.get(a).getItem()).getVisDiscount(player.inventory.armorInventory.get(a), player);
            }
        }
        if (player.isPotionActive(PotionVisExhaust.instance) || player.isPotionActive(PotionInfectiousVisExhaust.instance)) {
            int level1 = 0;
            int level2 = 0;
            if (player.isPotionActive(PotionVisExhaust.instance)) {
                level1 = player.getActivePotionEffect(PotionVisExhaust.instance).getAmplifier();
            }
            if (player.isPotionActive(PotionInfectiousVisExhaust.instance)) {
                level2 = player.getActivePotionEffect(PotionInfectiousVisExhaust.instance).getAmplifier();
            }
            total -= (Math.max(level1, level2) + 1) * 10;
        }
        return total / 100.0f;
    }
    
    public static boolean consumeVisFromInventory(EntityPlayer player, float cost) {
        for (int a = player.inventory.mainInventory.size() - 1; a >= 0; --a) {
            ItemStack item = player.inventory.mainInventory.get(a);
            if (item.getItem() instanceof ICaster) {
                boolean done = ((ICaster)item.getItem()).consumeVis(item, player, cost, true, false);
                if (done) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void changeFocus(ItemStack is, World w, EntityPlayer player, String focus) {
        ICaster wand = (ICaster)is.getItem();
        TreeMap<String, Integer> foci = new TreeMap<String, Integer>();
        HashMap<Integer, Integer> pouches = new HashMap<Integer, Integer>();
        int pouchcount = 0;
        ItemStack item = ItemStack.EMPTY;
        IInventory baubles = BaublesApi.getBaubles(player);
        for (int a = 0; a < baubles.getSizeInventory(); ++a) {
            if (baubles.getStackInSlot(a).getItem() instanceof ItemFocusPouch) {
                ++pouchcount;
                item = baubles.getStackInSlot(a);
                pouches.put(pouchcount, a - 4);
                NonNullList<ItemStack> inv = ((ItemFocusPouch)item.getItem()).getInventory(item);
                for (int q = 0; q < inv.size(); ++q) {
                    item = inv.get(q);
                    if (item.getItem() instanceof ItemFocus) {
                        String sh = ((ItemFocus)item.getItem()).getSortingHelper(item);
                        if (sh != null) {
                            foci.put(sh, q + pouchcount * 1000);
                        }
                    }
                }
            }
        }
        for (int a = 0; a < 36; ++a) {
            item = player.inventory.mainInventory.get(a);
            if (item.getItem() instanceof ItemFocus) {
                String sh2 = ((ItemFocus)item.getItem()).getSortingHelper(item);
                if (sh2 == null) {
                    continue;
                }
                foci.put(sh2, a);
            }
            if (item.getItem() instanceof ItemFocusPouch) {
                ++pouchcount;
                pouches.put(pouchcount, a);
                NonNullList<ItemStack> inv = ((ItemFocusPouch)item.getItem()).getInventory(item);
                for (int q = 0; q < inv.size(); ++q) {
                    item = inv.get(q);
                    if (item.getItem() instanceof ItemFocus) {
                        String sh = ((ItemFocus)item.getItem()).getSortingHelper(item);
                        if (sh != null) {
                            foci.put(sh, q + pouchcount * 1000);
                        }
                    }
                }
            }
        }
        if (focus.equals("REMOVE") || foci.size() == 0) {
            if (wand.getFocus(is) != null && (addFocusToPouch(player, wand.getFocusStack(is).copy(), pouches) || player.inventory.addItemStackToInventory(wand.getFocusStack(is).copy()))) {
                wand.setFocus(is, null);
                player.playSound(SoundsTC.ticks, 0.3f, 0.9f);
            }
            return;
        }
        if (foci != null && foci.size() > 0 && focus != null) {
            String newkey = focus;
            if (foci.get(newkey) == null) {
                newkey = foci.higherKey(newkey);
            }
            if (newkey == null || foci.get(newkey) == null) {
                newkey = foci.firstKey();
            }
            if (foci.get(newkey) < 1000 && foci.get(newkey) >= 0) {
                item = player.inventory.mainInventory.get(foci.get(newkey)).copy();
            }
            else {
                int pid = foci.get(newkey) / 1000;
                if (pouches.containsKey(pid)) {
                    int pouchslot = pouches.get(pid);
                    int focusslot = foci.get(newkey) - pid * 1000;
                    ItemStack tmp = ItemStack.EMPTY;
                    if (pouchslot >= 0) {
                        tmp = player.inventory.mainInventory.get(pouchslot).copy();
                    }
                    else {
                        tmp = baubles.getStackInSlot(pouchslot + 4).copy();
                    }
                    item = fetchFocusFromPouch(player, focusslot, tmp, pouchslot);
                }
            }
            if (item == null || item.isEmpty()) {
                return;
            }
            if (foci.get(newkey) < 1000 && foci.get(newkey) >= 0) {
                player.inventory.setInventorySlotContents(foci.get(newkey), ItemStack.EMPTY);
            }
            player.playSound(SoundsTC.ticks, 0.3f, 1.0f);
            if (wand.getFocus(is) != null && (addFocusToPouch(player, wand.getFocusStack(is).copy(), pouches) || player.inventory.addItemStackToInventory(wand.getFocusStack(is).copy()))) {
                wand.setFocus(is, ItemStack.EMPTY);
            }
            if (wand.getFocus(is) == null) {
                wand.setFocus(is, item);
            }
            else if (!addFocusToPouch(player, item, pouches)) {
                player.inventory.addItemStackToInventory(item);
            }
        }
    }
    
    private static ItemStack fetchFocusFromPouch(EntityPlayer player, int focusid, ItemStack pouch, int pouchslot) {
        ItemStack focus = ItemStack.EMPTY;
        NonNullList<ItemStack> inv = ((ItemFocusPouch)pouch.getItem()).getInventory(pouch);
        ItemStack contents = inv.get(focusid);
        if (contents.getItem() instanceof ItemFocus) {
            focus = contents.copy();
            inv.set(focusid, ItemStack.EMPTY);
            ((ItemFocusPouch)pouch.getItem()).setInventory(pouch, inv);
            if (pouchslot >= 0) {
                player.inventory.setInventorySlotContents(pouchslot, pouch);
                player.inventory.markDirty();
            }
            else {
                IInventory baubles = BaublesApi.getBaubles(player);
                baubles.setInventorySlotContents(pouchslot + 4, pouch);
                BaublesApi.getBaublesHandler(player).setChanged(pouchslot + 4, true);
                baubles.markDirty();
            }
        }
        return focus;
    }
    
    private static boolean addFocusToPouch(EntityPlayer player, ItemStack focus, HashMap<Integer, Integer> pouches) {
        IInventory baubles = BaublesApi.getBaubles(player);
        for (Integer pouchslot : pouches.values()) {
            ItemStack pouch;
            if (pouchslot >= 0) {
                pouch = player.inventory.mainInventory.get(pouchslot);
            }
            else {
                pouch = baubles.getStackInSlot(pouchslot + 4);
            }
            NonNullList<ItemStack> inv = ((ItemFocusPouch)pouch.getItem()).getInventory(pouch);
            for (int q = 0; q < inv.size(); ++q) {
                ItemStack contents = inv.get(q);
                if (contents.isEmpty()) {
                    inv.set(q, focus.copy());
                    ((ItemFocusPouch)pouch.getItem()).setInventory(pouch, inv);
                    if (pouchslot >= 0) {
                        player.inventory.setInventorySlotContents(pouchslot, pouch);
                        player.inventory.markDirty();
                    }
                    else {
                        baubles.setInventorySlotContents(pouchslot + 4, pouch);
                        BaublesApi.getBaublesHandler(player).setChanged(pouchslot + 4, true);
                        baubles.markDirty();
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void toggleMisc(ItemStack itemstack, World world, EntityPlayer player, int mod) {
        if (!(itemstack.getItem() instanceof ICaster)) {
            return;
        }
        ICaster caster = (ICaster)itemstack.getItem();
        ItemFocus focus = (ItemFocus)caster.getFocus(itemstack);
        FocusPackage fp = ItemFocus.getPackage(caster.getFocusStack(itemstack));
        if (fp != null && FocusEngine.doesPackageContainElement(fp, "thaumcraft.PLAN")) {
            int dim = getAreaDim(itemstack);
            if (mod == 0) {
                int areax = getAreaX(itemstack);
                int areay = getAreaY(itemstack);
                int areaz = getAreaZ(itemstack);
                int max = getAreaSize(itemstack);
                if (dim == 0) {
                    ++areax;
                    ++areaz;
                    ++areay;
                }
                else if (dim == 1) {
                    ++areax;
                }
                else if (dim == 2) {
                    ++areaz;
                }
                else if (dim == 3) {
                    ++areay;
                }
                if (areax > max) {
                    areax = 0;
                }
                if (areaz > max) {
                    areaz = 0;
                }
                if (areay > max) {
                    areay = 0;
                }
                setAreaX(itemstack, areax);
                setAreaY(itemstack, areay);
                setAreaZ(itemstack, areaz);
            }
            if (mod == 1) {
                if (++dim > 3) {
                    dim = 0;
                }
                setAreaDim(itemstack, dim);
            }
        }
    }
    
    private static int getAreaSize(ItemStack itemstack) {
        boolean pot = false;
        if (!(itemstack.getItem() instanceof ICaster)) {
            return 0;
        }
        ICaster caster = (ICaster)itemstack.getItem();
        ItemFocus focus = (ItemFocus)caster.getFocus(itemstack);
        return pot ? 6 : 3;
    }
    
    public static int getAreaDim(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("aread")) {
            return stack.getTagCompound().getInteger("aread");
        }
        return 0;
    }
    
    public static int getAreaX(ItemStack stack) {
        ICaster wand = (ICaster)stack.getItem();
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("areax")) {
            int a = stack.getTagCompound().getInteger("areax");
            if (a > getAreaSize(stack)) {
                a = getAreaSize(stack);
            }
            return a;
        }
        return getAreaSize(stack);
    }
    
    public static int getAreaY(ItemStack stack) {
        ICaster wand = (ICaster)stack.getItem();
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("areay")) {
            int a = stack.getTagCompound().getInteger("areay");
            if (a > getAreaSize(stack)) {
                a = getAreaSize(stack);
            }
            return a;
        }
        return getAreaSize(stack);
    }
    
    public static int getAreaZ(ItemStack stack) {
        ICaster wand = (ICaster)stack.getItem();
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("areaz")) {
            int a = stack.getTagCompound().getInteger("areaz");
            if (a > getAreaSize(stack)) {
                a = getAreaSize(stack);
            }
            return a;
        }
        return getAreaSize(stack);
    }
    
    public static void setAreaX(ItemStack stack, int area) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setInteger("areax", area);
        }
    }
    
    public static void setAreaY(ItemStack stack, int area) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setInteger("areay", area);
        }
    }
    
    public static void setAreaZ(ItemStack stack, int area) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setInteger("areaz", area);
        }
    }
    
    public static void setAreaDim(ItemStack stack, int dim) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setInteger("aread", dim);
        }
    }
    
    static boolean isOnCooldown(EntityLivingBase entityLiving) {
        if (entityLiving.world.isRemote && CasterManager.cooldownClient.containsKey(entityLiving.getEntityId())) {
            return CasterManager.cooldownClient.get(entityLiving.getEntityId()) > System.currentTimeMillis();
        }
        return !entityLiving.world.isRemote && CasterManager.cooldownServer.containsKey(entityLiving.getEntityId()) && CasterManager.cooldownServer.get(entityLiving.getEntityId()) > System.currentTimeMillis();
    }
    
    public static float getCooldown(EntityLivingBase entityLiving) {
        if (entityLiving.world.isRemote && CasterManager.cooldownClient.containsKey(entityLiving.getEntityId())) {
            return (CasterManager.cooldownClient.get(entityLiving.getEntityId()) - System.currentTimeMillis()) / 1000.0f;
        }
        return 0.0f;
    }
    
    public static void setCooldown(EntityLivingBase entityLiving, int cd) {
        if (cd == 0) {
            CasterManager.cooldownClient.remove(entityLiving.getEntityId());
            CasterManager.cooldownServer.remove(entityLiving.getEntityId());
        }
        else if (entityLiving.world.isRemote) {
            CasterManager.cooldownClient.put(entityLiving.getEntityId(), System.currentTimeMillis() + cd * 50);
        }
        else {
            CasterManager.cooldownServer.put(entityLiving.getEntityId(), System.currentTimeMillis() + cd * 50);
        }
    }
    
    static {
        CasterManager.cooldownServer = new HashMap<Integer, Long>();
        CasterManager.cooldownClient = new HashMap<Integer, Long>();
    }
}
