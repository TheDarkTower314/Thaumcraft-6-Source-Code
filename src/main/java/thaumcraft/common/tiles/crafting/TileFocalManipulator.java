// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.AspectList;
import java.util.HashMap;
import thaumcraft.common.tiles.TileThaumcraftInventory;

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
        this.vis = 0.0f;
        this.data = new HashMap<Integer, FocusElementNode>();
        this.focusName = "";
        this.ticks = 0;
        this.visCost = 0.0f;
        this.xpCost = 0;
        this.crystals = new AspectList();
        this.crystalsSync = new AspectList();
        this.doGuiReset = false;
        this.syncedSlots = new int[] { 0 };
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbt) {
        super.readSyncNBT(nbt);
        this.vis = nbt.getFloat("vis");
        this.focusName = nbt.getString("focusName");
        (this.crystalsSync = new AspectList()).readFromNBT(nbt, "crystals");
        final NBTTagList nodelist = nbt.getTagList("nodes", 10);
        this.data.clear();
        for (int x = 0; x < nodelist.tagCount(); ++x) {
            final NBTTagCompound nodenbt = nodelist.getCompoundTagAt(x);
            final FocusElementNode node = new FocusElementNode();
            node.deserialize(nodenbt);
            this.data.put(node.id, node);
        }
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbt) {
        super.writeSyncNBT(nbt);
        nbt.setFloat("vis", this.vis);
        nbt.setString("focusName", this.focusName);
        this.crystalsSync.writeToNBT(nbt, "crystals");
        final NBTTagList nodelist = new NBTTagList();
        for (final FocusElementNode node : this.data.values()) {
            nodelist.appendTag(node.serialize());
        }
        nbt.setTag("nodes", nodelist);
        return nbt;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1);
    }
    
    @Override
    public void setInventorySlotContents(final int par1, final ItemStack stack) {
        final ItemStack prev = this.getStackInSlot(par1);
        super.setInventorySlotContents(par1, stack);
        if (stack.isEmpty() || !ItemStack.areItemStacksEqual(stack, prev)) {
            if (this.world.isRemote) {
                this.data.clear();
                this.doGuiReset = true;
            }
            else {
                this.vis = 0.0f;
                this.crystalsSync = new AspectList();
                this.markDirty();
                this.syncSlots(null);
            }
        }
    }
    
    public float spendAura(final float vis) {
        if (this.world.getBlockState(this.getPos().up()).getBlock() == BlocksTC.arcaneWorkbenchCharger) {
            float q = vis;
            float z = vis / 9.0f;
        Label_0110:
            for (int xx = -1; xx <= 1; ++xx) {
                for (int zz = -1; zz <= 1; ++zz) {
                    if (z > q) {
                        z = q;
                    }
                    q -= AuraHandler.drainVis(this.getWorld(), this.getPos().add(xx * 16, 0, zz * 16), z, false);
                    if (q <= 0.0f) {
                        break Label_0110;
                    }
                }
            }
            return vis - q;
        }
        return AuraHandler.drainVis(this.getWorld(), this.getPos(), vis, false);
    }
    
    @Override
    public void update() {
        super.update();
        boolean complete = false;
        ++this.ticks;
        if (!this.world.isRemote) {
            if (this.ticks % 20 == 0) {
                if (this.vis > 0.0f && (this.getStackInSlot(0) == null || this.getStackInSlot(0).isEmpty() || !(this.getStackInSlot(0).getItem() instanceof ItemFocus))) {
                    complete = true;
                    this.vis = 0.0f;
                    this.world.playSound(null, this.pos, SoundsTC.wandfail, SoundCategory.BLOCKS, 0.33f, 1.0f);
                }
                if (!complete && this.vis > 0.0f) {
                    final float amt = this.spendAura(Math.min(20.0f, this.vis));
                    if (amt > 0.0f) {
                        this.world.addBlockEvent(this.pos, this.getBlockType(), 5, 1);
                        this.vis -= amt;
                        this.syncTile(false);
                        this.markDirty();
                    }
                    if (this.vis <= 0.0f && this.getStackInSlot(0) != null && !this.getStackInSlot(0).isEmpty() && this.getStackInSlot(0).getItem() instanceof ItemFocus) {
                        complete = true;
                        this.endCraft();
                    }
                }
            }
        }
        else if (this.vis > 0.0f) {
            FXDispatcher.INSTANCE.drawGenericParticles(this.pos.getX() + 0.5 + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3f, this.pos.getY() + 1.4 + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3f, this.pos.getZ() + 0.5 + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3f, 0.0, 0.0, 0.0, 0.5f + this.world.rand.nextFloat() * 0.4f, 1.0f - this.world.rand.nextFloat() * 0.4f, 1.0f - this.world.rand.nextFloat() * 0.4f, 0.8f, false, 448, 9, 1, 6 + this.world.rand.nextInt(5), 0, 0.3f + this.world.rand.nextFloat() * 0.3f, 0.0f, 0);
        }
        if (complete) {
            this.vis = 0.0f;
            this.syncTile(false);
            this.markDirty();
        }
    }
    
    private FocusPackage generateFocus() {
        if (this.data != null && !this.data.isEmpty()) {
            final FocusPackage core = new FocusPackage();
            int totalComplexity = 0;
            final HashMap<String, Integer> compCount = new HashMap<String, Integer>();
            for (final FocusElementNode node : this.data.values()) {
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
            final FocusElementNode root = this.data.get(0);
            this.traverseChildren(core, root);
            return core;
        }
        return null;
    }
    
    private void traverseChildren(final FocusPackage currentPackage, final FocusElementNode currentNode) {
        if (currentPackage == null || currentNode == null) {
            return;
        }
        currentPackage.addNode(currentNode.node);
        if (currentNode.children == null || currentNode.children.length == 0) {
            return;
        }
        if (currentNode.children.length == 1) {
            this.traverseChildren(currentPackage, this.data.get(currentNode.children[0]));
        }
        else {
            final FocusModSplit splitNode = (FocusModSplit)currentNode.node;
            splitNode.getSplitPackages().clear();
            for (final int c : currentNode.children) {
                final FocusPackage splitPackage = new FocusPackage();
                this.traverseChildren(splitPackage, this.data.get(c));
                splitNode.getSplitPackages().add(splitPackage);
            }
        }
    }
    
    public void endCraft() {
        this.vis = 0.0f;
        if (this.getStackInSlot(0) != null && !this.getStackInSlot(0).isEmpty() && this.getStackInSlot(0).getItem() instanceof ItemFocus) {
            final FocusPackage core = this.generateFocus();
            if (core != null) {
                this.world.playSound(null, this.pos, SoundsTC.wand, SoundCategory.BLOCKS, 1.0f, 1.0f);
                final ItemStack focus = this.getStackInSlot(0);
                if (focus.getTagCompound() != null) {
                    focus.getTagCompound().removeTag("color");
                }
                focus.setStackDisplayName(this.focusName);
                ItemFocus.setPackage(focus, core);
                this.setInventorySlotContents(0, focus);
                this.crystalsSync = new AspectList();
                this.data.clear();
                this.syncTile(false);
                this.markDirty();
            }
        }
    }
    
    public boolean startCraft(final int id, final EntityPlayer p) {
        if (this.data == null || this.data.isEmpty() || this.vis > 0.0f || this.getStackInSlot(0) == null || this.getStackInSlot(0).isEmpty() || !(this.getStackInSlot(0).getItem() instanceof ItemFocus)) {
            return false;
        }
        final int maxComplexity = ((ItemFocus)this.getStackInSlot(0).getItem()).getMaxComplexity();
        int totalComplexity = 0;
        this.crystals = new AspectList();
        final HashMap<String, Integer> compCount = new HashMap<String, Integer>();
        for (final FocusElementNode node : this.data.values()) {
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
            this.crystals.add(node.node.getAspect(), 1);
        }
        this.vis = (float)(totalComplexity * 10 + maxComplexity / 5);
        this.xpCost = (int)Math.max(1L, Math.round(Math.sqrt(totalComplexity)));
        if (!p.capabilities.isCreativeMode && p.experienceLevel < this.xpCost) {
            this.vis = 0.0f;
            return false;
        }
        if (!p.capabilities.isCreativeMode) {
            p.addExperienceLevel(-this.xpCost);
        }
        if (this.crystals.getAspects().length > 0) {
            final ItemStack[] components = new ItemStack[this.crystals.getAspects().length];
            int r = 0;
            for (final Aspect as : this.crystals.getAspects()) {
                components[r] = ThaumcraftApiHelper.makeCrystal(as, this.crystals.getAmount(as));
                ++r;
            }
            if (components.length >= 0) {
                for (int a = 0; a < components.length; ++a) {
                    if (!InventoryUtils.isPlayerCarryingAmount(p, components[a], false)) {
                        this.vis = 0.0f;
                        return false;
                    }
                }
                for (int a = 0; a < components.length; ++a) {
                    InventoryUtils.consumePlayerItem(p, components[a], true, false);
                }
                this.crystalsSync = this.crystals.copy();
            }
            this.markDirty();
            this.syncTile(false);
            this.world.playSound(null, this.pos, SoundsTC.craftstart, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        this.vis = 0.0f;
        return false;
    }
    
    @Override
    public boolean isItemValidForSlot(final int par1, final ItemStack stack) {
        return stack.getItem() instanceof ItemFocus;
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 1) {
            this.doGuiReset = true;
        }
        if (i == 5) {
            if (this.world.isRemote) {
                FXDispatcher.INSTANCE.visSparkle(this.pos.getX() + this.getWorld().rand.nextInt(3) - this.getWorld().rand.nextInt(3), this.pos.getY() + this.getWorld().rand.nextInt(3), this.pos.getZ() + this.getWorld().rand.nextInt(3) - this.getWorld().rand.nextInt(3), this.pos.getX(), this.pos.getY() + 1, this.pos.getZ(), j);
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
}
