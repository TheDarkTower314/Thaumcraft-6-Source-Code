// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import java.util.Set;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.api.crafting.IInfusionStabiliser;
import net.minecraft.init.Blocks;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.container.InventoryFake;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.tiles.devices.TileStabilizer;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.potions.PotionVisExhaust;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.lib.events.EssentiaHandler;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXInfusionSource;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.InfusionRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import java.util.List;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.blocks.devices.BlockPedestal;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import java.text.DecimalFormat;
import net.minecraft.block.Block;
import java.util.HashMap;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import thaumcraft.api.items.IGogglesDisplayExtended;
import net.minecraft.util.ITickable;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileInfusionMatrix extends TileThaumcraft implements IInteractWithCaster, IAspectContainer, ITickable, IGogglesDisplayExtended
{
    private ArrayList<BlockPos> pedestals;
    private int dangerCount;
    public boolean active;
    public boolean crafting;
    public boolean checkSurroundings;
    public float costMult;
    private int cycleTime;
    public int stabilityCap;
    public float stability;
    public float stabilityReplenish;
    private AspectList recipeEssentia;
    private ArrayList<ItemStack> recipeIngredients;
    private Object recipeOutput;
    private String recipePlayer;
    private String recipeOutputLabel;
    private ItemStack recipeInput;
    private int recipeInstability;
    private int recipeXP;
    private int recipeType;
    public HashMap<String, SourceFX> sourceFX;
    public int count;
    public int craftCount;
    public float startUp;
    private int countDelay;
    ArrayList<ItemStack> ingredients;
    int itemCount;
    private ArrayList<BlockPos> problemBlocks;
    HashMap<Block, Integer> tempBlockCount;
    static DecimalFormat myFormatter;
    
    public TileInfusionMatrix() {
        this.pedestals = new ArrayList<BlockPos>();
        this.dangerCount = 0;
        this.active = false;
        this.crafting = false;
        this.checkSurroundings = true;
        this.costMult = 0.0f;
        this.cycleTime = 20;
        this.stabilityCap = 25;
        this.stability = 0.0f;
        this.stabilityReplenish = 0.0f;
        this.recipeEssentia = new AspectList();
        this.recipeIngredients = null;
        this.recipeOutput = null;
        this.recipePlayer = null;
        this.recipeOutputLabel = null;
        this.recipeInput = null;
        this.recipeInstability = 0;
        this.recipeXP = 0;
        this.recipeType = 0;
        this.sourceFX = new HashMap<String, SourceFX>();
        this.count = 0;
        this.craftCount = 0;
        this.countDelay = this.cycleTime / 2;
        this.ingredients = new ArrayList<ItemStack>();
        this.itemCount = 0;
        this.problemBlocks = new ArrayList<BlockPos>();
        this.tempBlockCount = new HashMap<Block, Integer>();
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos().getX() - 0.1, this.getPos().getY() - 0.1, this.getPos().getZ() - 0.1, this.getPos().getX() + 1.1, this.getPos().getY() + 1.1, this.getPos().getZ() + 1.1);
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbtCompound) {
        this.active = nbtCompound.getBoolean("active");
        this.crafting = nbtCompound.getBoolean("crafting");
        this.stability = nbtCompound.getFloat("stability");
        this.recipeInstability = nbtCompound.getInteger("recipeinst");
        this.recipeEssentia.readFromNBT(nbtCompound);
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbtCompound) {
        nbtCompound.setBoolean("active", this.active);
        nbtCompound.setBoolean("crafting", this.crafting);
        nbtCompound.setFloat("stability", this.stability);
        nbtCompound.setInteger("recipeinst", this.recipeInstability);
        this.recipeEssentia.writeToNBT(nbtCompound);
        return nbtCompound;
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        final NBTTagList nbttaglist = nbtCompound.getTagList("recipein", 10);
        this.recipeIngredients = new ArrayList<ItemStack>();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            this.recipeIngredients.add(new ItemStack(nbttagcompound1));
        }
        final String rot = nbtCompound.getString("rotype");
        if (rot != null && rot.equals("@")) {
            this.recipeOutput = new ItemStack(nbtCompound.getCompoundTag("recipeout"));
        }
        else if (rot != null) {
            this.recipeOutputLabel = rot;
            this.recipeOutput = nbtCompound.getTag("recipeout");
        }
        this.recipeInput = new ItemStack(nbtCompound.getCompoundTag("recipeinput"));
        this.recipeType = nbtCompound.getInteger("recipetype");
        this.recipeXP = nbtCompound.getInteger("recipexp");
        this.recipePlayer = nbtCompound.getString("recipeplayer");
        if (this.recipePlayer.isEmpty()) {
            this.recipePlayer = null;
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        if (this.recipeIngredients != null && this.recipeIngredients.size() > 0) {
            final NBTTagList nbttaglist = new NBTTagList();
            for (final ItemStack stack : this.recipeIngredients) {
                if (!stack.isEmpty()) {
                    final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("item", (byte)this.count);
                    stack.writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                    ++this.count;
                }
            }
            nbtCompound.setTag("recipein", nbttaglist);
        }
        if (this.recipeOutput != null && this.recipeOutput instanceof ItemStack) {
            nbtCompound.setString("rotype", "@");
        }
        if (this.recipeOutput != null && this.recipeOutput instanceof NBTBase) {
            nbtCompound.setString("rotype", this.recipeOutputLabel);
        }
        if (this.recipeOutput != null && this.recipeOutput instanceof ItemStack) {
            nbtCompound.setTag("recipeout", ((ItemStack)this.recipeOutput).writeToNBT(new NBTTagCompound()));
        }
        if (this.recipeOutput != null && this.recipeOutput instanceof NBTBase) {
            nbtCompound.setTag("recipeout", (NBTBase)this.recipeOutput);
        }
        if (this.recipeInput != null) {
            nbtCompound.setTag("recipeinput", this.recipeInput.writeToNBT(new NBTTagCompound()));
        }
        nbtCompound.setInteger("recipetype", this.recipeType);
        nbtCompound.setInteger("recipexp", this.recipeXP);
        if (this.recipePlayer == null) {
            nbtCompound.setString("recipeplayer", "");
        }
        else {
            nbtCompound.setString("recipeplayer", this.recipePlayer);
        }
        return nbtCompound;
    }
    
    private EnumStability getStability() {
        return (this.stability > this.stabilityCap / 2) ? EnumStability.VERY_STABLE : ((this.stability >= 0.0f) ? EnumStability.STABLE : ((this.stability > -25.0f) ? EnumStability.UNSTABLE : EnumStability.VERY_UNSTABLE));
    }
    
    private float getModFromCurrentStability() {
        switch (this.getStability()) {
            case VERY_STABLE: {
                return 5.0f;
            }
            case STABLE: {
                return 6.0f;
            }
            case UNSTABLE: {
                return 7.0f;
            }
            case VERY_UNSTABLE: {
                return 8.0f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    public void update() {
        ++this.count;
        if (this.checkSurroundings) {
            this.checkSurroundings = false;
            this.getSurroundings();
        }
        if (this.world.isRemote) {
            this.doEffects();
        }
        else {
            if (this.count % (this.crafting ? 20 : 100) == 0 && !this.validLocation()) {
                this.active = false;
                this.markDirty();
                this.syncTile(false);
                return;
            }
            if (this.active && !this.crafting && this.stability < this.stabilityCap && this.count % Math.max(5, this.countDelay) == 0) {
                this.stability += Math.max(0.1f, this.stabilityReplenish);
                if (this.stability > this.stabilityCap) {
                    this.stability = (float)this.stabilityCap;
                }
                this.markDirty();
                this.syncTile(false);
            }
            if (this.active && this.crafting && this.count % this.countDelay == 0) {
                this.craftCycle();
                this.markDirty();
            }
        }
    }
    
    public boolean validLocation() {
        return this.world.getBlockState(this.pos.add(0, -2, 0)).getBlock() instanceof BlockPedestal && this.world.getBlockState(this.pos.add(1, -2, 1)).getBlock() instanceof BlockPillar && this.world.getBlockState(this.pos.add(-1, -2, 1)).getBlock() instanceof BlockPillar && this.world.getBlockState(this.pos.add(1, -2, -1)).getBlock() instanceof BlockPillar && this.world.getBlockState(this.pos.add(-1, -2, -1)).getBlock() instanceof BlockPillar;
    }
    
    public void craftingStart(final EntityPlayer player) {
        if (!this.validLocation()) {
            this.active = false;
            this.markDirty();
            this.syncTile(false);
            return;
        }
        this.getSurroundings();
        TileEntity te = null;
        this.recipeInput = ItemStack.EMPTY;
        te = this.world.getTileEntity(this.pos.down(2));
        if (te != null && te instanceof TilePedestal) {
            final TilePedestal ped = (TilePedestal)te;
            if (!ped.getStackInSlot(0).isEmpty()) {
                this.recipeInput = ped.getStackInSlot(0).copy();
            }
        }
        if (this.recipeInput == null || this.recipeInput.isEmpty()) {
            return;
        }
        final ArrayList<ItemStack> components = new ArrayList<ItemStack>();
        for (final BlockPos cc : this.pedestals) {
            te = this.world.getTileEntity(cc);
            if (te != null && te instanceof TilePedestal) {
                final TilePedestal ped2 = (TilePedestal)te;
                if (ped2.getStackInSlot(0).isEmpty()) {
                    continue;
                }
                components.add(ped2.getStackInSlot(0).copy());
            }
        }
        if (components.size() == 0) {
            return;
        }
        final InfusionRecipe recipe = ThaumcraftCraftingManager.findMatchingInfusionRecipe(components, this.recipeInput, player);
        if (this.costMult < 0.5) {
            this.costMult = 0.5f;
        }
        if (recipe != null) {
            this.recipeType = 0;
            this.recipeIngredients = components;
            if (recipe.getRecipeOutput(player, this.recipeInput, components) instanceof Object[]) {
                final Object[] obj = (Object[])recipe.getRecipeOutput(player, this.recipeInput, components);
                this.recipeOutputLabel = (String)obj[0];
                this.recipeOutput = obj[1];
            }
            else {
                this.recipeOutput = recipe.getRecipeOutput(player, this.recipeInput, components);
            }
            this.recipeInstability = recipe.getInstability(player, this.recipeInput, components);
            final AspectList al = recipe.getAspects(player, this.recipeInput, components);
            final AspectList al2 = new AspectList();
            for (final Aspect as : al.getAspects()) {
                if ((int)(al.getAmount(as) * this.costMult) > 0) {
                    al2.add(as, (int)(al.getAmount(as) * this.costMult));
                }
            }
            this.recipeEssentia = al2;
            this.recipePlayer = player.getName();
            this.crafting = true;
            this.world.playSound(null, this.pos, SoundsTC.craftstart, SoundCategory.BLOCKS, 0.5f, 1.0f);
            this.syncTile(false);
            this.markDirty();
        }
    }
    
    private float getLossPerCycle() {
        return this.recipeInstability / this.getModFromCurrentStability();
    }
    
    public void craftCycle() {
        boolean valid = false;
        final float ff = this.world.rand.nextFloat() * this.getLossPerCycle();
        this.stability -= ff;
        this.stability += this.stabilityReplenish;
        if (this.stability < -100.0f) {
            this.stability = -100.0f;
        }
        if (this.stability > this.stabilityCap) {
            this.stability = (float)this.stabilityCap;
        }
        TileEntity te = this.world.getTileEntity(this.pos.down(2));
        if (te != null && te instanceof TilePedestal) {
            final TilePedestal ped = (TilePedestal)te;
            if (!ped.getStackInSlot(0).isEmpty()) {
                final ItemStack i2 = ped.getStackInSlot(0).copy();
                if (this.recipeInput.getItemDamage() == 32767) {
                    i2.setItemDamage(32767);
                }
                if (ThaumcraftInvHelper.areItemStacksEqualForCrafting(i2, this.recipeInput)) {
                    valid = true;
                }
            }
        }
        if (!valid || (this.stability < 0.0f && this.world.rand.nextInt(1500) <= Math.abs(this.stability))) {
            switch (this.world.rand.nextInt(24)) {
                case 0:
                case 1:
                case 2:
                case 3: {
                    this.inEvEjectItem(0);
                    break;
                }
                case 4:
                case 5:
                case 6: {
                    this.inEvWarp();
                    break;
                }
                case 7:
                case 8:
                case 9: {
                    this.inEvZap(false);
                    break;
                }
                case 10:
                case 11: {
                    this.inEvZap(true);
                    break;
                }
                case 12:
                case 13: {
                    this.inEvEjectItem(1);
                    break;
                }
                case 14:
                case 15: {
                    this.inEvEjectItem(2);
                    break;
                }
                case 16: {
                    this.inEvEjectItem(3);
                    break;
                }
                case 17: {
                    this.inEvEjectItem(4);
                    break;
                }
                case 18:
                case 19: {
                    this.inEvHarm(false);
                    break;
                }
                case 20:
                case 21: {
                    this.inEvEjectItem(5);
                    break;
                }
                case 22: {
                    this.inEvHarm(true);
                    break;
                }
                case 23: {
                    this.world.createExplosion(null, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, 1.5f + this.world.rand.nextFloat(), false);
                    break;
                }
            }
            this.stability += 5.0f + this.world.rand.nextFloat() * 5.0f;
            this.inResAdd();
            if (valid) {
                return;
            }
        }
        if (!valid) {
            this.crafting = false;
            this.recipeEssentia = new AspectList();
            this.recipeInstability = 0;
            this.syncTile(false);
            this.world.playSound(null, this.pos, SoundsTC.craftfail, SoundCategory.BLOCKS, 1.0f, 0.6f);
            this.markDirty();
            return;
        }
        if (this.recipeType == 1 && this.recipeXP > 0) {
            final List<EntityPlayer> targets = this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
            if (targets != null && targets.size() > 0) {
                for (final EntityPlayer target : targets) {
                    if (target.capabilities.isCreativeMode || target.experienceLevel > 0) {
                        if (!target.capabilities.isCreativeMode) {
                            target.addExperienceLevel(-1);
                        }
                        --this.recipeXP;
                        target.attackEntityFrom(DamageSource.MAGIC, (float)this.world.rand.nextInt(2));
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXInfusionSource(this.pos, this.pos, target.getEntityId()), new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 32.0));
                        target.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 1.0f, 2.0f + this.world.rand.nextFloat() * 0.4f);
                        this.countDelay = this.cycleTime;
                        return;
                    }
                }
                final Aspect[] ingEss = this.recipeEssentia.getAspects();
                if (ingEss != null && ingEss.length > 0 && this.world.rand.nextInt(3) == 0) {
                    final Aspect as = ingEss[this.world.rand.nextInt(ingEss.length)];
                    this.recipeEssentia.add(as, 1);
                    this.stability -= 0.25f;
                    this.syncTile(false);
                    this.markDirty();
                }
            }
            return;
        }
        if (this.recipeType == 1 && this.recipeXP == 0) {
            this.countDelay = this.cycleTime / 2;
        }
        if (this.countDelay < 1) {
            this.countDelay = 1;
        }
        if (this.recipeEssentia.visSize() > 0) {
            for (final Aspect aspect : this.recipeEssentia.getAspects()) {
                final int na = this.recipeEssentia.getAmount(aspect);
                if (na > 0) {
                    if (EssentiaHandler.drainEssentia(this, aspect, null, 12, (na > 1) ? this.countDelay : 0)) {
                        this.recipeEssentia.reduce(aspect, 1);
                        this.syncTile(false);
                        this.markDirty();
                        return;
                    }
                    this.stability -= 0.25f;
                    this.syncTile(false);
                    this.markDirty();
                }
            }
            this.checkSurroundings = true;
            return;
        }
        if (this.recipeIngredients.size() > 0) {
            for (int a = 0; a < this.recipeIngredients.size(); ++a) {
                for (final BlockPos cc : this.pedestals) {
                    te = this.world.getTileEntity(cc);
                    if (te != null && te instanceof TilePedestal && ((TilePedestal)te).getStackInSlot(0) != null && !((TilePedestal)te).getStackInSlot(0).isEmpty() && ThaumcraftInvHelper.areItemStacksEqualForCrafting(((TilePedestal)te).getStackInSlot(0), this.recipeIngredients.get(a))) {
                        if (this.itemCount == 0) {
                            this.itemCount = 5;
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXInfusionSource(this.pos, cc, 0), new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 32.0));
                        }
                        else if (this.itemCount-- <= 1) {
                            final ItemStack is = ((TilePedestal)te).getStackInSlot(0).getItem().getContainerItem(((TilePedestal)te).getStackInSlot(0));
                            ((TilePedestal)te).setInventorySlotContents(0, (is == null || is.isEmpty()) ? ItemStack.EMPTY : is.copy());
                            te.markDirty();
                            ((TilePedestal)te).syncTile(false);
                            this.recipeIngredients.remove(a);
                            this.markDirty();
                        }
                        return;
                    }
                }
                final Aspect[] ingEss = this.recipeEssentia.getAspects();
                if (ingEss != null && ingEss.length > 0 && this.world.rand.nextInt(1 + a) == 0) {
                    final Aspect as = ingEss[this.world.rand.nextInt(ingEss.length)];
                    this.recipeEssentia.add(as, 1);
                    this.stability -= 0.25f;
                    this.syncTile(false);
                    this.markDirty();
                }
            }
            return;
        }
        this.crafting = false;
        this.craftingFinish(this.recipeOutput, this.recipeOutputLabel);
        this.recipeOutput = null;
        this.syncTile(false);
        this.markDirty();
    }
    
    private void inEvZap(final boolean all) {
        final List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (final EntityLivingBase target : targets) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(this.pos, target, 0.3f - this.world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - this.world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 32.0));
                target.attackEntityFrom(DamageSource.MAGIC, (float)(4 + this.world.rand.nextInt(4)));
                if (!all) {
                    break;
                }
            }
        }
    }
    
    private void inEvHarm(final boolean all) {
        final List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (final EntityLivingBase target : targets) {
                if (this.world.rand.nextBoolean()) {
                    target.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 120, 0, false, true));
                }
                else {
                    final PotionEffect pe = new PotionEffect(PotionVisExhaust.instance, 2400, 0, true, true);
                    pe.getCurativeItems().clear();
                    target.addPotionEffect(pe);
                }
                if (!all) {
                    break;
                }
            }
        }
    }
    
    private void inResAdd() {
        final List<EntityPlayer> targets = this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1).grow(10.0));
        if (targets != null && targets.size() > 0) {
            for (final EntityPlayer player : targets) {
                final IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
                if (!knowledge.isResearchKnown("!INSTABILITY")) {
                    knowledge.addResearch("!INSTABILITY");
                    knowledge.sync((EntityPlayerMP)player);
                    player.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.instability")), true);
                }
            }
        }
    }
    
    private void inEvWarp() {
        final List<EntityPlayer> targets = this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1).grow(10.0));
        if (targets != null && targets.size() > 0) {
            final EntityPlayer target = targets.get(this.world.rand.nextInt(targets.size()));
            if (this.world.rand.nextFloat() < 0.25f) {
                ThaumcraftApi.internalMethods.addWarpToPlayer(target, 1, IPlayerWarp.EnumWarpType.NORMAL);
            }
            else {
                ThaumcraftApi.internalMethods.addWarpToPlayer(target, 2 + this.world.rand.nextInt(4), IPlayerWarp.EnumWarpType.TEMPORARY);
            }
        }
    }
    
    private void inEvEjectItem(final int type) {
        for (int retries = 0; retries < 25 && this.pedestals.size() > 0; ++retries) {
            final BlockPos cc = this.pedestals.get(this.world.rand.nextInt(this.pedestals.size()));
            final TileEntity te = this.world.getTileEntity(cc);
            if (te != null && te instanceof TilePedestal && ((TilePedestal)te).getStackInSlot(0) != null && !((TilePedestal)te).getStackInSlot(0).isEmpty()) {
                final BlockPos stabPos = ((TilePedestal)te).findInstabilityMitigator();
                if (stabPos != null) {
                    final TileEntity ste = this.world.getTileEntity(stabPos);
                    if (ste != null && ste instanceof TileStabilizer) {
                        final TileStabilizer tste = (TileStabilizer)ste;
                        if (tste.mitigate(MathHelper.getInt(this.world.rand, 5, 10))) {
                            this.world.addBlockEvent(cc, this.world.getBlockState(cc).getBlock(), 5, 0);
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(this.pos, cc.up(), 0.3f - this.world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - this.world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), cc.getX(), cc.getY(), cc.getZ(), 32.0));
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(cc.up(), stabPos, 0.3f - this.world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - this.world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), stabPos.getX(), stabPos.getY(), stabPos.getZ(), 32.0));
                            return;
                        }
                    }
                }
                if (type <= 3 || type == 5) {
                    InventoryUtils.dropItems(this.world, cc);
                }
                else {
                    ((TilePedestal)te).setInventorySlotContents(0, ItemStack.EMPTY);
                }
                te.markDirty();
                ((TilePedestal)te).syncTile(false);
                if (type == 1 || type == 3) {
                    this.world.setBlockState(cc.up(), BlocksTC.fluxGoo.getDefaultState());
                    this.world.playSound(null, cc, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.3f, 1.0f);
                }
                else if (type == 2 || type == 4) {
                    final int a = 5 + this.world.rand.nextInt(5);
                    AuraHelper.polluteAura(this.world, cc, (float)a, true);
                }
                else if (type == 5) {
                    this.world.createExplosion(null, cc.getX() + 0.5f, cc.getY() + 0.5f, cc.getZ() + 0.5f, 1.0f, false);
                }
                this.world.addBlockEvent(cc, this.world.getBlockState(cc).getBlock(), 11, 0);
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(this.pos, cc.up(), 0.3f - this.world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - this.world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), cc.getX(), cc.getY(), cc.getZ(), 32.0));
                return;
            }
        }
    }
    
    public void craftingFinish(final Object out, final String label) {
        final TileEntity te = this.world.getTileEntity(this.pos.down(2));
        if (te != null && te instanceof TilePedestal) {
            float dmg = 1.0f;
            if (out instanceof ItemStack) {
                final ItemStack qs = ((ItemStack)out).copy();
                if (((TilePedestal)te).getStackInSlot(0).isItemStackDamageable() && ((TilePedestal)te).getStackInSlot(0).isItemDamaged()) {
                    dmg = ((TilePedestal)te).getStackInSlot(0).getItemDamage() / (float)((TilePedestal)te).getStackInSlot(0).getMaxDamage();
                    if (qs.isItemStackDamageable() && !qs.isItemDamaged()) {
                        qs.setItemDamage((int)(qs.getMaxDamage() * dmg));
                    }
                }
                ((TilePedestal)te).setInventorySlotContentsFromInfusion(0, qs);
            }
            else if (out instanceof NBTBase) {
                final ItemStack temp = ((TilePedestal)te).getStackInSlot(0);
                final NBTBase tag = (NBTBase)out;
                temp.setTagInfo(label, tag);
                this.syncTile(false);
                te.markDirty();
            }
            else if (out instanceof Enchantment) {
                final ItemStack temp = ((TilePedestal)te).getStackInSlot(0);
                final Map enchantments = EnchantmentHelper.getEnchantments(temp);
                enchantments.put(out, EnchantmentHelper.getEnchantmentLevel((Enchantment)out, temp) + 1);
                EnchantmentHelper.setEnchantments(enchantments, temp);
                this.syncTile(false);
                te.markDirty();
            }
            if (this.recipePlayer != null) {
                final EntityPlayer p = this.world.getPlayerEntityByName(this.recipePlayer);
                if (p != null) {
                    FMLCommonHandler.instance().firePlayerCraftingEvent(p, ((TilePedestal)te).getStackInSlot(0), new InventoryFake(this.recipeIngredients));
                }
            }
            this.recipeEssentia = new AspectList();
            this.recipeInstability = 0;
            this.syncTile(false);
            this.markDirty();
            this.world.addBlockEvent(this.pos.down(2), this.world.getBlockState(this.pos.down(2)).getBlock(), 12, 0);
            this.world.playSound(null, this.pos, SoundsTC.wand, SoundCategory.BLOCKS, 0.5f, 1.0f);
        }
    }
    
    private void getSurroundings() {
        final Set<Long> stuff = new HashSet<Long>();
        this.pedestals.clear();
        this.tempBlockCount.clear();
        this.problemBlocks.clear();
        this.cycleTime = 10;
        this.stabilityReplenish = 0.0f;
        this.costMult = 1.0f;
        try {
            for (int xx = -8; xx <= 8; ++xx) {
                for (int zz = -8; zz <= 8; ++zz) {
                    final boolean skip = false;
                    for (int yy = -3; yy <= 7; ++yy) {
                        if (xx != 0 || zz != 0) {
                            final int x = this.pos.getX() + xx;
                            final int y = this.pos.getY() - yy;
                            final int z = this.pos.getZ() + zz;
                            final BlockPos bp = new BlockPos(x, y, z);
                            final Block bi = this.world.getBlockState(bp).getBlock();
                            if (bi instanceof BlockPedestal) {
                                this.pedestals.add(bp);
                            }
                            try {
                                if (bi == Blocks.SKULL || (bi instanceof IInfusionStabiliser && ((IInfusionStabiliser)bi).canStabaliseInfusion(this.getWorld(), bp))) {
                                    stuff.add(bp.toLong());
                                }
                            }
                            catch (final Exception ex) {}
                        }
                    }
                }
            }
            while (!stuff.isEmpty()) {
                final Long[] posArray = stuff.toArray(new Long[stuff.size()]);
                if (posArray == null) {
                    break;
                }
                if (posArray[0] == null) {
                    break;
                }
                final long lp = posArray[0];
                try {
                    final BlockPos c1 = BlockPos.fromLong(lp);
                    final int x2 = this.pos.getX() - c1.getX();
                    final int z2 = this.pos.getZ() - c1.getZ();
                    final int x3 = this.pos.getX() + x2;
                    final int z3 = this.pos.getZ() + z2;
                    final BlockPos c2 = new BlockPos(x3, c1.getY(), z3);
                    final Block sb1 = this.world.getBlockState(c1).getBlock();
                    final Block sb2 = this.world.getBlockState(c2).getBlock();
                    float amt1 = 0.1f;
                    float amt2 = 0.1f;
                    if (sb1 instanceof IInfusionStabiliserExt) {
                        amt1 = ((IInfusionStabiliserExt)sb1).getStabilizationAmount(this.getWorld(), c1);
                    }
                    if (sb2 instanceof IInfusionStabiliserExt) {
                        amt2 = ((IInfusionStabiliserExt)sb2).getStabilizationAmount(this.getWorld(), c2);
                    }
                    if (sb1 == sb2 && amt1 == amt2) {
                        if (sb1 instanceof IInfusionStabiliserExt && ((IInfusionStabiliserExt)sb1).hasSymmetryPenalty(this.getWorld(), c1, c2)) {
                            this.stabilityReplenish -= ((IInfusionStabiliserExt)sb1).getSymmetryPenalty(this.getWorld(), c1);
                            this.problemBlocks.add(c1);
                        }
                        else {
                            this.stabilityReplenish += this.calcDeminishingReturns(sb1, amt1);
                        }
                    }
                    else {
                        this.stabilityReplenish -= Math.max(amt1, amt2);
                        this.problemBlocks.add(c1);
                    }
                    stuff.remove(c2.toLong());
                }
                catch (final Exception ex2) {}
                stuff.remove(lp);
            }
            if (this.world.getBlockState(this.pos.add(-1, -2, -1)).getBlock() instanceof BlockPillar && this.world.getBlockState(this.pos.add(1, -2, -1)).getBlock() instanceof BlockPillar && this.world.getBlockState(this.pos.add(1, -2, 1)).getBlock() instanceof BlockPillar && this.world.getBlockState(this.pos.add(-1, -2, 1)).getBlock() instanceof BlockPillar) {
                if (this.world.getBlockState(this.pos.add(-1, -2, -1)).getBlock() == BlocksTC.pillarAncient && this.world.getBlockState(this.pos.add(1, -2, -1)).getBlock() == BlocksTC.pillarAncient && this.world.getBlockState(this.pos.add(1, -2, 1)).getBlock() == BlocksTC.pillarAncient && this.world.getBlockState(this.pos.add(-1, -2, 1)).getBlock() == BlocksTC.pillarAncient) {
                    --this.cycleTime;
                    this.costMult -= 0.1f;
                    this.stabilityReplenish -= 0.1f;
                }
                if (this.world.getBlockState(this.pos.add(-1, -2, -1)).getBlock() == BlocksTC.pillarEldritch && this.world.getBlockState(this.pos.add(1, -2, -1)).getBlock() == BlocksTC.pillarEldritch && this.world.getBlockState(this.pos.add(1, -2, 1)).getBlock() == BlocksTC.pillarEldritch && this.world.getBlockState(this.pos.add(-1, -2, 1)).getBlock() == BlocksTC.pillarEldritch) {
                    this.cycleTime -= 3;
                    this.costMult += 0.05f;
                    this.stabilityReplenish += 0.2f;
                }
            }
            final int[] xm = { -1, 1, 1, -1 };
            final int[] zm = { -1, -1, 1, 1 };
            for (int a = 0; a < 4; ++a) {
                final Block b = this.world.getBlockState(this.pos.add(xm[a], -3, zm[a])).getBlock();
                if (b == BlocksTC.matrixSpeed) {
                    --this.cycleTime;
                    this.costMult += 0.01f;
                }
                if (b == BlocksTC.matrixCost) {
                    ++this.cycleTime;
                    this.costMult -= 0.02f;
                }
            }
            this.countDelay = this.cycleTime / 2;
            final int apc = 0;
            for (final BlockPos cc : this.pedestals) {
                final boolean items = false;
                final int x4 = this.pos.getX() - cc.getX();
                final int z4 = this.pos.getZ() - cc.getZ();
                final Block bb = this.world.getBlockState(cc).getBlock();
                if (bb == BlocksTC.pedestalEldritch) {
                    this.costMult += 0.0025f;
                }
                if (bb == BlocksTC.pedestalAncient) {
                    this.costMult -= 0.01f;
                }
            }
        }
        catch (final Exception ex3) {}
    }
    
    private float calcDeminishingReturns(final Block b, final float base) {
        float bb = base;
        final int c = this.tempBlockCount.containsKey(b) ? this.tempBlockCount.get(b) : 0;
        if (c > 0) {
            bb *= (float)Math.pow(0.75, c);
        }
        this.tempBlockCount.put(b, c + 1);
        return bb;
    }
    
    @Override
    public boolean onCasterRightClick(final World world, final ItemStack wandstack, final EntityPlayer player, final BlockPos pos, final EnumFacing side, final EnumHand hand) {
        if (world.isRemote && this.active && !this.crafting) {
            this.checkSurroundings = true;
        }
        if (!world.isRemote && this.active && !this.crafting) {
            this.craftingStart(player);
            return false;
        }
        if (!world.isRemote && !this.active && this.validLocation()) {
            world.playSound(null, pos, SoundsTC.craftstart, SoundCategory.BLOCKS, 0.5f, 1.0f);
            this.active = true;
            this.syncTile(false);
            this.markDirty();
            return false;
        }
        return false;
    }
    
    private void doEffects() {
        if (this.crafting) {
            if (this.craftCount == 0) {
                this.world.playSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), SoundsTC.infuserstart, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
            }
            else if (this.craftCount == 0 || this.craftCount % 65 == 0) {
                this.world.playSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), SoundsTC.infuser, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
            }
            ++this.craftCount;
            FXDispatcher.INSTANCE.blockRunes(this.pos.getX(), this.pos.getY() - 2, this.pos.getZ(), 0.5f + this.world.rand.nextFloat() * 0.2f, 0.1f, 0.7f + this.world.rand.nextFloat() * 0.3f, 25, -0.03f);
        }
        else if (this.craftCount > 0) {
            this.craftCount -= 2;
            if (this.craftCount < 0) {
                this.craftCount = 0;
            }
            if (this.craftCount > 50) {
                this.craftCount = 50;
            }
        }
        if (this.active && this.startUp != 1.0f) {
            if (this.startUp < 1.0f) {
                this.startUp += Math.max(this.startUp / 10.0f, 0.001f);
            }
            if (this.startUp > 0.999) {
                this.startUp = 1.0f;
            }
        }
        if (!this.active && this.startUp > 0.0f) {
            if (this.startUp > 0.0f) {
                this.startUp -= this.startUp / 10.0f;
            }
            if (this.startUp < 0.001) {
                this.startUp = 0.0f;
            }
        }
        for (final String fxk : this.sourceFX.keySet().toArray(new String[0])) {
            final SourceFX fx = this.sourceFX.get(fxk);
            if (fx.ticks <= 0) {
                this.sourceFX.remove(fxk);
            }
            else {
                if (fx.loc.equals(this.pos)) {
                    final Entity player = this.world.getEntityByID(fx.color);
                    if (player != null) {
                        for (int a = 0; a < 4; ++a) {
                            FXDispatcher.INSTANCE.drawInfusionParticles4(player.posX + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * player.width, player.getEntityBoundingBox().minY + this.world.rand.nextFloat() * player.height, player.posZ + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * player.width, this.pos.getX(), this.pos.getY(), this.pos.getZ());
                        }
                    }
                }
                else {
                    final TileEntity tile = this.world.getTileEntity(fx.loc);
                    if (tile instanceof TilePedestal) {
                        final ItemStack is = ((TilePedestal)tile).getSyncedStackInSlot(0);
                        if (is != null && !is.isEmpty()) {
                            if (this.world.rand.nextInt(3) == 0) {
                                FXDispatcher.INSTANCE.drawInfusionParticles3(fx.loc.getX() + this.world.rand.nextFloat(), fx.loc.getY() + this.world.rand.nextFloat() + 1.0f, fx.loc.getZ() + this.world.rand.nextFloat(), this.pos.getX(), this.pos.getY(), this.pos.getZ());
                            }
                            else {
                                final Item bi = is.getItem();
                                if (bi instanceof ItemBlock) {
                                    for (int a2 = 0; a2 < 4; ++a2) {
                                        FXDispatcher.INSTANCE.drawInfusionParticles2(fx.loc.getX() + this.world.rand.nextFloat(), fx.loc.getY() + this.world.rand.nextFloat() + 1.0f, fx.loc.getZ() + this.world.rand.nextFloat(), this.pos, Block.getBlockFromItem(bi).getDefaultState(), is.getItemDamage());
                                    }
                                }
                                else {
                                    for (int a2 = 0; a2 < 4; ++a2) {
                                        FXDispatcher.INSTANCE.drawInfusionParticles1(fx.loc.getX() + 0.4f + this.world.rand.nextFloat() * 0.2f, fx.loc.getY() + 1.23f + this.world.rand.nextFloat() * 0.2f, fx.loc.getZ() + 0.4f + this.world.rand.nextFloat() * 0.2f, this.pos, is);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        fx.ticks = 0;
                    }
                }
                final SourceFX sourceFX = fx;
                --sourceFX.ticks;
                this.sourceFX.put(fxk, fx);
            }
        }
        if (this.crafting && this.stability < 0.0f && this.world.rand.nextInt(250) <= Math.abs(this.stability)) {
            FXDispatcher.INSTANCE.spark(this.getPos().getX() + this.world.rand.nextFloat(), this.getPos().getY() + this.world.rand.nextFloat(), this.getPos().getZ() + this.world.rand.nextFloat(), 3.0f + this.world.rand.nextFloat() * 2.0f, 0.7f + this.world.rand.nextFloat() * 0.1f, 0.1f, 0.65f + this.world.rand.nextFloat() * 0.1f, 0.8f);
        }
        if (this.active && !this.problemBlocks.isEmpty() && this.world.rand.nextInt(25) == 0) {
            final BlockPos p = this.problemBlocks.get(this.world.rand.nextInt(this.problemBlocks.size()));
            FXDispatcher.INSTANCE.spark(p.getX() + this.world.rand.nextFloat(), p.getY() + this.world.rand.nextFloat(), p.getZ() + this.world.rand.nextFloat(), 2.0f + this.world.rand.nextFloat(), 0.7f + this.world.rand.nextFloat() * 0.1f, 0.1f, 0.65f + this.world.rand.nextFloat() * 0.1f, 0.8f);
        }
    }
    
    @Override
    public AspectList getAspects() {
        return this.recipeEssentia;
    }
    
    @Override
    public void setAspects(final AspectList aspects) {
    }
    
    @Override
    public int addToContainer(final Aspect tag, final int amount) {
        return 0;
    }
    
    @Override
    public boolean takeFromContainer(final Aspect tag, final int amount) {
        return false;
    }
    
    @Override
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(final Aspect tag, final int amount) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }
    
    @Override
    public int containerContains(final Aspect tag) {
        return 0;
    }
    
    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return true;
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
    
    public String[] getIGogglesText() {
        final float lpc = this.getLossPerCycle();
        if (lpc != 0.0f) {
            return new String[] { TextFormatting.BOLD + I18n.translateToLocal("stability." + this.getStability().name()), TextFormatting.GOLD + "" + TextFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(this.stabilityReplenish) + " " + I18n.translateToLocal("stability.gain"), TextFormatting.RED + "" + I18n.translateToLocal("stability.range") + TextFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(lpc) + " " + I18n.translateToLocal("stability.loss") };
        }
        return new String[] { TextFormatting.BOLD + I18n.translateToLocal("stability." + this.getStability().name()), TextFormatting.GOLD + "" + TextFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(this.stabilityReplenish) + " " + I18n.translateToLocal("stability.gain") };
    }
    
    static {
        TileInfusionMatrix.myFormatter = new DecimalFormat("#######.##");
    }
    
    public class SourceFX
    {
        public BlockPos loc;
        public int ticks;
        public int color;
        public int entity;
        
        public SourceFX(final BlockPos loc, final int ticks, final int color) {
            this.loc = loc;
            this.ticks = ticks;
            this.color = color;
        }
    }
    
    private enum EnumStability
    {
        VERY_STABLE, 
        STABLE, 
        UNSTABLE, 
        VERY_UNSTABLE;
    }
}
