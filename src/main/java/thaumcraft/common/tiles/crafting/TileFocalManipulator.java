package thaumcraft.common.tiles.crafting;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.world.aura.AuraHandler;


public class TileFocalManipulator extends TileThaumcraftInventory
{
    public float vis;
    public HashMap<Integer, FocusElementNode> data;
    public String focusName;
    int ticks;
    public boolean doGather;
    public float visCost;
    public int xpCost;
    private AspectList crystals;
    public AspectList crystalsSync;
    public boolean doGuiReset;
    
    public TileFocalManipulator() {
        super(1);
        vis = 0.0f;
        data = new HashMap<Integer, FocusElementNode>();
        focusName = "";
        ticks = 0;
        visCost = 0.0f;
        xpCost = 0;
        crystals = new AspectList();
        crystalsSync = new AspectList();
        doGuiReset = false;
        syncedSlots = new int[] { 0 };
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbt) {
        super.readSyncNBT(nbt);
        vis = nbt.getFloat("vis");
        focusName = nbt.getString("focusName");
        (crystalsSync = new AspectList()).readFromNBT(nbt, "crystals");
        NBTTagList nodelist = nbt.getTagList("nodes", 10);
        data.clear();
        for (int x = 0; x < nodelist.tagCount(); ++x) {
            NBTTagCompound nodenbt = nodelist.getCompoundTagAt(x);
            FocusElementNode node = new FocusElementNode();
            node.deserialize(nodenbt);
            data.put(node.id, node);
        }
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
        super.writeSyncNBT(nbt);
        nbt.setFloat("vis", vis);
        nbt.setString("focusName", focusName);
        crystalsSync.writeToNBT(nbt, "crystals");
        NBTTagList nodelist = new NBTTagList();
        for (FocusElementNode node : data.values()) {
            nodelist.appendTag(node.serialize());
        }
        nbt.setTag("nodes", nodelist);
        return nbt;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }
    
    @Override
    public void setInventorySlotContents(int par1, ItemStack stack) {
        ItemStack prev = getStackInSlot(par1);
        super.setInventorySlotContents(par1, stack);
        if (stack.isEmpty() || !ItemStack.areItemStacksEqual(stack, prev)) {
            if (world.isRemote) {
                data.clear();
                doGuiReset = true;
            }
            else {
                vis = 0.0f;
                crystalsSync = new AspectList();
                markDirty();
                syncSlots(null);
            }
        }
    }
    
    public float spendAura(float vis) {
        if (world.getBlockState(getPos().up()).getBlock() == BlocksTC.arcaneWorkbenchCharger) {
            float q = vis;
            float z = vis / 9.0f;
        Label_0110:
            for (int xx = -1; xx <= 1; ++xx) {
                for (int zz = -1; zz <= 1; ++zz) {
                    if (z > q) {
                        z = q;
                    }
                    q -= AuraHandler.drainVis(getWorld(), getPos().add(xx * 16, 0, zz * 16), z, false);
                    if (q <= 0.0f) {
                        break Label_0110;
                    }
                }
            }
            return vis - q;
        }
        return AuraHandler.drainVis(getWorld(), getPos(), vis, false);
    }
    
    @Override
    public void update() {
        super.update();
        boolean complete = false;
        ++ticks;
        if (!world.isRemote) {
            if (ticks % 20 == 0) {
                if (vis > 0.0f && (getStackInSlot(0) == null || getStackInSlot(0).isEmpty() || !(getStackInSlot(0).getItem() instanceof ItemFocus))) {
                    complete = true;
                    vis = 0.0f;
                    world.playSound(null, pos, SoundsTC.wandfail, SoundCategory.BLOCKS, 0.33f, 1.0f);
                }
                if (!complete && vis > 0.0f) {
                    float amt = spendAura(Math.min(20.0f, vis));
                    if (amt > 0.0f) {
                        world.addBlockEvent(pos, getBlockType(), 5, 1);
                        vis -= amt;
                        syncTile(false);
                        markDirty();
                    }
                    if (vis <= 0.0f && getStackInSlot(0) != null && !getStackInSlot(0).isEmpty() && getStackInSlot(0).getItem() instanceof ItemFocus) {
                        complete = true;
                        endCraft();
                    }
                }
            }
        }
        else if (vis > 0.0f) {
            FXDispatcher.INSTANCE.drawGenericParticles(pos.getX() + 0.5 + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3f, pos.getY() + 1.4 + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3f, pos.getZ() + 0.5 + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3f, 0.0, 0.0, 0.0, 0.5f + world.rand.nextFloat() * 0.4f, 1.0f - world.rand.nextFloat() * 0.4f, 1.0f - world.rand.nextFloat() * 0.4f, 0.8f, false, 448, 9, 1, 6 + world.rand.nextInt(5), 0, 0.3f + world.rand.nextFloat() * 0.3f, 0.0f, 0);
        }
        if (complete) {
            vis = 0.0f;
            syncTile(false);
            markDirty();
        }
    }
    
    private FocusPackage generateFocus() {
        if (data != null && !data.isEmpty()) {
            FocusPackage core = new FocusPackage();
            int totalComplexity = 0;
            HashMap<String, Integer> compCount = new HashMap<String, Integer>();
            for (FocusElementNode node : data.values()) {
                if (node.node != null) {
                    int a = 0;
                    if (compCount.containsKey(node.node.getKey())) {
                        a = compCount.get(node.node.getKey());
                    }
                    ++a;
                    node.complexityMultiplier = 0.5f * (a + 1);
                    compCount.put(node.node.getKey(), a);
                    totalComplexity += (int)(node.node.getComplexity() * node.complexityMultiplier);
                }
            }
            core.setComplexity(totalComplexity);
            FocusElementNode root = data.get(0);
            traverseChildren(core, root);
            return core;
        }
        return null;
    }
    
    private void traverseChildren(FocusPackage currentPackage, FocusElementNode currentNode) {
        if (currentPackage == null || currentNode == null) {
            return;
        }
        currentPackage.addNode(currentNode.node);
        if (currentNode.children == null || currentNode.children.length == 0) {
            return;
        }
        if (currentNode.children.length == 1) {
            traverseChildren(currentPackage, data.get(currentNode.children[0]));
        }
        else {
            FocusModSplit splitNode = (FocusModSplit)currentNode.node;
            splitNode.getSplitPackages().clear();
            for (int c : currentNode.children) {
                FocusPackage splitPackage = new FocusPackage();
                traverseChildren(splitPackage, data.get(c));
                splitNode.getSplitPackages().add(splitPackage);
            }
        }
    }
    
    public void endCraft() {
        vis = 0.0f;
        if (getStackInSlot(0) != null && !getStackInSlot(0).isEmpty() && getStackInSlot(0).getItem() instanceof ItemFocus) {
            FocusPackage core = generateFocus();
            if (core != null) {
                world.playSound(null, pos, SoundsTC.wand, SoundCategory.BLOCKS, 1.0f, 1.0f);
                ItemStack focus = getStackInSlot(0);
                if (focus.getTagCompound() != null) {
                    focus.getTagCompound().removeTag("color");
                }
                focus.setStackDisplayName(focusName);
                ItemFocus.setPackage(focus, core);
                setInventorySlotContents(0, focus);
                crystalsSync = new AspectList();
                data.clear();
                syncTile(false);
                markDirty();
            }
        }
    }
    
    public boolean startCraft(int id, EntityPlayer p) {
        if (data == null || data.isEmpty() || vis > 0.0f || getStackInSlot(0) == null || getStackInSlot(0).isEmpty() || !(getStackInSlot(0).getItem() instanceof ItemFocus)) {
            return false;
        }
        int maxComplexity = ((ItemFocus) getStackInSlot(0).getItem()).getMaxComplexity();
        int totalComplexity = 0;
        crystals = new AspectList();
        HashMap<String, Integer> compCount = new HashMap<String, Integer>();
        for (FocusElementNode node : data.values()) {
            if (node.node == null) {
                return false;
            }
            if (!ThaumcraftCapabilities.knowsResearchStrict(p, node.node.getResearch())) {
                return false;
            }
            int a = 0;
            if (compCount.containsKey(node.node.getKey())) {
                a = compCount.get(node.node.getKey());
            }
            ++a;
            node.complexityMultiplier = 0.5f * (a + 1);
            compCount.put(node.node.getKey(), a);
            totalComplexity += (int)(node.node.getComplexity() * node.complexityMultiplier);
            if (node.node.getAspect() == null) {
                continue;
            }
            crystals.add(node.node.getAspect(), 1);
        }
        vis = (float)(totalComplexity * 10 + maxComplexity / 5);
        xpCost = (int)Math.max(1L, Math.round(Math.sqrt(totalComplexity)));
        if (!p.capabilities.isCreativeMode && p.experienceLevel < xpCost) {
            vis = 0.0f;
            return false;
        }
        if (!p.capabilities.isCreativeMode) {
            p.addExperienceLevel(-xpCost);
        }
        if (crystals.getAspects().length > 0) {
            ItemStack[] components = new ItemStack[crystals.getAspects().length];
            int r = 0;
            for (Aspect as : crystals.getAspects()) {
                components[r] = ThaumcraftApiHelper.makeCrystal(as, crystals.getAmount(as));
                ++r;
            }
            if (components.length >= 0) {
                for (int a = 0; a < components.length; ++a) {
                    if (!InventoryUtils.isPlayerCarryingAmount(p, components[a], false)) {
                        vis = 0.0f;
                        return false;
                    }
                }
                for (int a = 0; a < components.length; ++a) {
                    InventoryUtils.consumePlayerItem(p, components[a], true, false);
                }
                crystalsSync = crystals.copy();
            }
            markDirty();
            syncTile(false);
            world.playSound(null, pos, SoundsTC.craftstart, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        vis = 0.0f;
        return false;
    }
    
    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack) {
        return stack.getItem() instanceof ItemFocus;
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 1) {
            doGuiReset = true;
        }
        if (i == 5) {
            if (world.isRemote) {
                FXDispatcher.INSTANCE.visSparkle(pos.getX() + getWorld().rand.nextInt(3) - getWorld().rand.nextInt(3), pos.getY() + getWorld().rand.nextInt(3), pos.getZ() + getWorld().rand.nextInt(3) - getWorld().rand.nextInt(3), pos.getX(), pos.getY() + 1, pos.getZ(), j);
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
}
