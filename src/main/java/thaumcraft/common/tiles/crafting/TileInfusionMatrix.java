package thaumcraft.common.tiles.crafting;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.blocks.devices.BlockPedestal;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.network.fx.PacketFXInfusionSource;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.tiles.devices.TileStabilizer;


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
        pedestals = new ArrayList<BlockPos>();
        dangerCount = 0;
        active = false;
        crafting = false;
        checkSurroundings = true;
        costMult = 0.0f;
        cycleTime = 20;
        stabilityCap = 25;
        stability = 0.0f;
        stabilityReplenish = 0.0f;
        recipeEssentia = new AspectList();
        recipeIngredients = null;
        recipeOutput = null;
        recipePlayer = null;
        recipeOutputLabel = null;
        recipeInput = null;
        recipeInstability = 0;
        recipeXP = 0;
        recipeType = 0;
        sourceFX = new HashMap<String, SourceFX>();
        count = 0;
        craftCount = 0;
        countDelay = cycleTime / 2;
        ingredients = new ArrayList<ItemStack>();
        itemCount = 0;
        problemBlocks = new ArrayList<BlockPos>();
        tempBlockCount = new HashMap<Block, Integer>();
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX() - 0.1, getPos().getY() - 0.1, getPos().getZ() - 0.1, getPos().getX() + 1.1, getPos().getY() + 1.1, getPos().getZ() + 1.1);
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbtCompound) {
        active = nbtCompound.getBoolean("active");
        crafting = nbtCompound.getBoolean("crafting");
        stability = nbtCompound.getFloat("stability");
        recipeInstability = nbtCompound.getInteger("recipeinst");
        recipeEssentia.readFromNBT(nbtCompound);
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbtCompound) {
        nbtCompound.setBoolean("active", active);
        nbtCompound.setBoolean("crafting", crafting);
        nbtCompound.setFloat("stability", stability);
        nbtCompound.setInteger("recipeinst", recipeInstability);
        recipeEssentia.writeToNBT(nbtCompound);
        return nbtCompound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        NBTTagList nbttaglist = nbtCompound.getTagList("recipein", 10);
        recipeIngredients = new ArrayList<ItemStack>();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            recipeIngredients.add(new ItemStack(nbttagcompound1));
        }
        String rot = nbtCompound.getString("rotype");
        if (rot != null && rot.equals("@")) {
            recipeOutput = new ItemStack(nbtCompound.getCompoundTag("recipeout"));
        }
        else if (rot != null) {
            recipeOutputLabel = rot;
            recipeOutput = nbtCompound.getTag("recipeout");
        }
        recipeInput = new ItemStack(nbtCompound.getCompoundTag("recipeinput"));
        recipeType = nbtCompound.getInteger("recipetype");
        recipeXP = nbtCompound.getInteger("recipexp");
        recipePlayer = nbtCompound.getString("recipeplayer");
        if (recipePlayer.isEmpty()) {
            recipePlayer = null;
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        if (recipeIngredients != null && recipeIngredients.size() > 0) {
            NBTTagList nbttaglist = new NBTTagList();
            for (ItemStack stack : recipeIngredients) {
                if (!stack.isEmpty()) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("item", (byte) count);
                    stack.writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                    ++count;
                }
            }
            nbtCompound.setTag("recipein", nbttaglist);
        }
        if (recipeOutput != null && recipeOutput instanceof ItemStack) {
            nbtCompound.setString("rotype", "@");
        }
        if (recipeOutput != null && recipeOutput instanceof NBTBase) {
            nbtCompound.setString("rotype", recipeOutputLabel);
        }
        if (recipeOutput != null && recipeOutput instanceof ItemStack) {
            nbtCompound.setTag("recipeout", ((ItemStack) recipeOutput).writeToNBT(new NBTTagCompound()));
        }
        if (recipeOutput != null && recipeOutput instanceof NBTBase) {
            nbtCompound.setTag("recipeout", (NBTBase) recipeOutput);
        }
        if (recipeInput != null) {
            nbtCompound.setTag("recipeinput", recipeInput.writeToNBT(new NBTTagCompound()));
        }
        nbtCompound.setInteger("recipetype", recipeType);
        nbtCompound.setInteger("recipexp", recipeXP);
        if (recipePlayer == null) {
            nbtCompound.setString("recipeplayer", "");
        }
        else {
            nbtCompound.setString("recipeplayer", recipePlayer);
        }
        return nbtCompound;
    }
    
    private EnumStability getStability() {
        return (stability > stabilityCap / 2) ? EnumStability.VERY_STABLE : ((stability >= 0.0f) ? EnumStability.STABLE : ((stability > -25.0f) ? EnumStability.UNSTABLE : EnumStability.VERY_UNSTABLE));
    }
    
    private float getModFromCurrentStability() {
        switch (getStability()) {
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
        ++count;
        if (checkSurroundings) {
            checkSurroundings = false;
            getSurroundings();
        }
        if (world.isRemote) {
            doEffects();
        }
        else {
            if (count % (crafting ? 20 : 100) == 0 && !validLocation()) {
                active = false;
                markDirty();
                syncTile(false);
                return;
            }
            if (active && !crafting && stability < stabilityCap && count % Math.max(5, countDelay) == 0) {
                stability += Math.max(0.1f, stabilityReplenish);
                if (stability > stabilityCap) {
                    stability = (float) stabilityCap;
                }
                markDirty();
                syncTile(false);
            }
            if (active && crafting && count % countDelay == 0) {
                craftCycle();
                markDirty();
            }
        }
    }
    
    public boolean validLocation() {
        return world.getBlockState(pos.add(0, -2, 0)).getBlock() instanceof BlockPedestal && world.getBlockState(pos.add(1, -2, 1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(-1, -2, 1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(1, -2, -1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(-1, -2, -1)).getBlock() instanceof BlockPillar;
    }
    
    public void craftingStart(EntityPlayer player) {
        if (!validLocation()) {
            active = false;
            markDirty();
            syncTile(false);
            return;
        }
        getSurroundings();
        TileEntity te = null;
        recipeInput = ItemStack.EMPTY;
        te = world.getTileEntity(pos.down(2));
        if (te != null && te instanceof TilePedestal) {
            TilePedestal ped = (TilePedestal)te;
            if (!ped.getStackInSlot(0).isEmpty()) {
                recipeInput = ped.getStackInSlot(0).copy();
            }
        }
        if (recipeInput == null || recipeInput.isEmpty()) {
            return;
        }
        ArrayList<ItemStack> components = new ArrayList<ItemStack>();
        for (BlockPos cc : pedestals) {
            te = world.getTileEntity(cc);
            if (te != null && te instanceof TilePedestal) {
                TilePedestal ped2 = (TilePedestal)te;
                if (ped2.getStackInSlot(0).isEmpty()) {
                    continue;
                }
                components.add(ped2.getStackInSlot(0).copy());
            }
        }
        if (components.size() == 0) {
            return;
        }
        InfusionRecipe recipe = ThaumcraftCraftingManager.findMatchingInfusionRecipe(components, recipeInput, player);
        if (costMult < 0.5) {
            costMult = 0.5f;
        }
        if (recipe != null) {
            recipeType = 0;
            recipeIngredients = components;
            if (recipe.getRecipeOutput(player, recipeInput, components) instanceof Object[]) {
                Object[] obj = (Object[])recipe.getRecipeOutput(player, recipeInput, components);
                recipeOutputLabel = (String)obj[0];
                recipeOutput = obj[1];
            }
            else {
                recipeOutput = recipe.getRecipeOutput(player, recipeInput, components);
            }
            recipeInstability = recipe.getInstability(player, recipeInput, components);
            AspectList al = recipe.getAspects(player, recipeInput, components);
            AspectList al2 = new AspectList();
            for (Aspect as : al.getAspects()) {
                if ((int)(al.getAmount(as) * costMult) > 0) {
                    al2.add(as, (int)(al.getAmount(as) * costMult));
                }
            }
            recipeEssentia = al2;
            recipePlayer = player.getName();
            crafting = true;
            world.playSound(null, pos, SoundsTC.craftstart, SoundCategory.BLOCKS, 0.5f, 1.0f);
            syncTile(false);
            markDirty();
        }
    }
    
    private float getLossPerCycle() {
        return recipeInstability / getModFromCurrentStability();
    }
    
    public void craftCycle() {
        boolean valid = false;
        float ff = world.rand.nextFloat() * getLossPerCycle();
        stability -= ff;
        stability += stabilityReplenish;
        if (stability < -100.0f) {
            stability = -100.0f;
        }
        if (stability > stabilityCap) {
            stability = (float) stabilityCap;
        }
        TileEntity te = world.getTileEntity(pos.down(2));
        if (te != null && te instanceof TilePedestal) {
            TilePedestal ped = (TilePedestal)te;
            if (!ped.getStackInSlot(0).isEmpty()) {
                ItemStack i2 = ped.getStackInSlot(0).copy();
                if (recipeInput.getItemDamage() == 32767) {
                    i2.setItemDamage(32767);
                }
                if (ThaumcraftInvHelper.areItemStacksEqualForCrafting(i2, recipeInput)) {
                    valid = true;
                }
            }
        }
        if (!valid || (stability < 0.0f && world.rand.nextInt(1500) <= Math.abs(stability))) {
            switch (world.rand.nextInt(24)) {
                case 0:
                case 1:
                case 2:
                case 3: {
                    inEvEjectItem(0);
                    break;
                }
                case 4:
                case 5:
                case 6: {
                    inEvWarp();
                    break;
                }
                case 7:
                case 8:
                case 9: {
                    inEvZap(false);
                    break;
                }
                case 10:
                case 11: {
                    inEvZap(true);
                    break;
                }
                case 12:
                case 13: {
                    inEvEjectItem(1);
                    break;
                }
                case 14:
                case 15: {
                    inEvEjectItem(2);
                    break;
                }
                case 16: {
                    inEvEjectItem(3);
                    break;
                }
                case 17: {
                    inEvEjectItem(4);
                    break;
                }
                case 18:
                case 19: {
                    inEvHarm(false);
                    break;
                }
                case 20:
                case 21: {
                    inEvEjectItem(5);
                    break;
                }
                case 22: {
                    inEvHarm(true);
                    break;
                }
                case 23: {
                    world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.5f + world.rand.nextFloat(), false);
                    break;
                }
            }
            stability += 5.0f + world.rand.nextFloat() * 5.0f;
            inResAdd();
            if (valid) {
                return;
            }
        }
        if (!valid) {
            crafting = false;
            recipeEssentia = new AspectList();
            recipeInstability = 0;
            syncTile(false);
            world.playSound(null, pos, SoundsTC.craftfail, SoundCategory.BLOCKS, 1.0f, 0.6f);
            markDirty();
            return;
        }
        if (recipeType == 1 && recipeXP > 0) {
            List<EntityPlayer> targets = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
            if (targets != null && targets.size() > 0) {
                for (EntityPlayer target : targets) {
                    if (target.capabilities.isCreativeMode || target.experienceLevel > 0) {
                        if (!target.capabilities.isCreativeMode) {
                            target.addExperienceLevel(-1);
                        }
                        --recipeXP;
                        target.attackEntityFrom(DamageSource.MAGIC, (float) world.rand.nextInt(2));
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXInfusionSource(pos, pos, target.getEntityId()), new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                        target.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 1.0f, 2.0f + world.rand.nextFloat() * 0.4f);
                        countDelay = cycleTime;
                        return;
                    }
                }
                Aspect[] ingEss = recipeEssentia.getAspects();
                if (ingEss != null && ingEss.length > 0 && world.rand.nextInt(3) == 0) {
                    Aspect as = ingEss[world.rand.nextInt(ingEss.length)];
                    recipeEssentia.add(as, 1);
                    stability -= 0.25f;
                    syncTile(false);
                    markDirty();
                }
            }
            return;
        }
        if (recipeType == 1 && recipeXP == 0) {
            countDelay = cycleTime / 2;
        }
        if (countDelay < 1) {
            countDelay = 1;
        }
        if (recipeEssentia.visSize() > 0) {
            for (Aspect aspect : recipeEssentia.getAspects()) {
                int na = recipeEssentia.getAmount(aspect);
                if (na > 0) {
                    if (EssentiaHandler.drainEssentia(this, aspect, null, 12, (na > 1) ? countDelay : 0)) {
                        recipeEssentia.reduce(aspect, 1);
                        syncTile(false);
                        markDirty();
                        return;
                    }
                    stability -= 0.25f;
                    syncTile(false);
                    markDirty();
                }
            }
            checkSurroundings = true;
            return;
        }
        if (recipeIngredients.size() > 0) {
            for (int a = 0; a < recipeIngredients.size(); ++a) {
                for (BlockPos cc : pedestals) {
                    te = world.getTileEntity(cc);
                    if (te != null && te instanceof TilePedestal && ((TilePedestal)te).getStackInSlot(0) != null && !((TilePedestal)te).getStackInSlot(0).isEmpty() && ThaumcraftInvHelper.areItemStacksEqualForCrafting(((TilePedestal)te).getStackInSlot(0), recipeIngredients.get(a))) {
                        if (itemCount == 0) {
                            itemCount = 5;
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXInfusionSource(pos, cc, 0), new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                        }
                        else if (itemCount-- <= 1) {
                            ItemStack is = ((TilePedestal)te).getStackInSlot(0).getItem().getContainerItem(((TilePedestal)te).getStackInSlot(0));
                            ((TilePedestal)te).setInventorySlotContents(0, (is == null || is.isEmpty()) ? ItemStack.EMPTY : is.copy());
                            te.markDirty();
                            ((TilePedestal)te).syncTile(false);
                            recipeIngredients.remove(a);
                            markDirty();
                        }
                        return;
                    }
                }
                Aspect[] ingEss = recipeEssentia.getAspects();
                if (ingEss != null && ingEss.length > 0 && world.rand.nextInt(1 + a) == 0) {
                    Aspect as = ingEss[world.rand.nextInt(ingEss.length)];
                    recipeEssentia.add(as, 1);
                    stability -= 0.25f;
                    syncTile(false);
                    markDirty();
                }
            }
            return;
        }
        crafting = false;
        craftingFinish(recipeOutput, recipeOutputLabel);
        recipeOutput = null;
        syncTile(false);
        markDirty();
    }
    
    private void inEvZap(boolean all) {
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (EntityLivingBase target : targets) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(pos, target, 0.3f - world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                target.attackEntityFrom(DamageSource.MAGIC, (float)(4 + world.rand.nextInt(4)));
                if (!all) {
                    break;
                }
            }
        }
    }
    
    private void inEvHarm(boolean all) {
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (EntityLivingBase target : targets) {
                if (world.rand.nextBoolean()) {
                    target.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 120, 0, false, true));
                }
                else {
                    PotionEffect pe = new PotionEffect(PotionVisExhaust.instance, 2400, 0, true, true);
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
        List<EntityPlayer> targets = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0));
        if (targets != null && targets.size() > 0) {
            for (EntityPlayer player : targets) {
                IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
                if (!knowledge.isResearchKnown("!INSTABILITY")) {
                    knowledge.addResearch("!INSTABILITY");
                    knowledge.sync((EntityPlayerMP)player);
                    player.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.instability")), true);
                }
            }
        }
    }
    
    private void inEvWarp() {
        List<EntityPlayer> targets = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0));
        if (targets != null && targets.size() > 0) {
            EntityPlayer target = targets.get(world.rand.nextInt(targets.size()));
            if (world.rand.nextFloat() < 0.25f) {
                ThaumcraftApi.internalMethods.addWarpToPlayer(target, 1, IPlayerWarp.EnumWarpType.NORMAL);
            }
            else {
                ThaumcraftApi.internalMethods.addWarpToPlayer(target, 2 + world.rand.nextInt(4), IPlayerWarp.EnumWarpType.TEMPORARY);
            }
        }
    }
    
    private void inEvEjectItem(int type) {
        for (int retries = 0; retries < 25 && pedestals.size() > 0; ++retries) {
            BlockPos cc = pedestals.get(world.rand.nextInt(pedestals.size()));
            TileEntity te = world.getTileEntity(cc);
            if (te != null && te instanceof TilePedestal && ((TilePedestal)te).getStackInSlot(0) != null && !((TilePedestal)te).getStackInSlot(0).isEmpty()) {
                BlockPos stabPos = ((TilePedestal)te).findInstabilityMitigator();
                if (stabPos != null) {
                    TileEntity ste = world.getTileEntity(stabPos);
                    if (ste != null && ste instanceof TileStabilizer) {
                        TileStabilizer tste = (TileStabilizer)ste;
                        if (tste.mitigate(MathHelper.getInt(world.rand, 5, 10))) {
                            world.addBlockEvent(cc, world.getBlockState(cc).getBlock(), 5, 0);
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(pos, cc.up(), 0.3f - world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), cc.getX(), cc.getY(), cc.getZ(), 32.0));
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(cc.up(), stabPos, 0.3f - world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), stabPos.getX(), stabPos.getY(), stabPos.getZ(), 32.0));
                            return;
                        }
                    }
                }
                if (type <= 3 || type == 5) {
                    InventoryUtils.dropItems(world, cc);
                }
                else {
                    ((TilePedestal)te).setInventorySlotContents(0, ItemStack.EMPTY);
                }
                te.markDirty();
                ((TilePedestal)te).syncTile(false);
                if (type == 1 || type == 3) {
                    world.setBlockState(cc.up(), BlocksTC.fluxGoo.getDefaultState());
                    world.playSound(null, cc, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.3f, 1.0f);
                }
                else if (type == 2 || type == 4) {
                    int a = 5 + world.rand.nextInt(5);
                    AuraHelper.polluteAura(world, cc, (float)a, true);
                }
                else if (type == 5) {
                    world.createExplosion(null, cc.getX() + 0.5f, cc.getY() + 0.5f, cc.getZ() + 0.5f, 1.0f, false);
                }
                world.addBlockEvent(cc, world.getBlockState(cc).getBlock(), 11, 0);
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(pos, cc.up(), 0.3f - world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), cc.getX(), cc.getY(), cc.getZ(), 32.0));
                return;
            }
        }
    }
    
    public void craftingFinish(Object out, String label) {
        TileEntity te = world.getTileEntity(pos.down(2));
        if (te != null && te instanceof TilePedestal) {
            float dmg = 1.0f;
            if (out instanceof ItemStack) {
                ItemStack qs = ((ItemStack)out).copy();
                if (((TilePedestal)te).getStackInSlot(0).isItemStackDamageable() && ((TilePedestal)te).getStackInSlot(0).isItemDamaged()) {
                    dmg = ((TilePedestal)te).getStackInSlot(0).getItemDamage() / (float)((TilePedestal)te).getStackInSlot(0).getMaxDamage();
                    if (qs.isItemStackDamageable() && !qs.isItemDamaged()) {
                        qs.setItemDamage((int)(qs.getMaxDamage() * dmg));
                    }
                }
                ((TilePedestal)te).setInventorySlotContentsFromInfusion(0, qs);
            }
            else if (out instanceof NBTBase) {
                ItemStack temp = ((TilePedestal)te).getStackInSlot(0);
                NBTBase tag = (NBTBase)out;
                temp.setTagInfo(label, tag);
                syncTile(false);
                te.markDirty();
            }
            else if (out instanceof Enchantment) {
                ItemStack temp = ((TilePedestal)te).getStackInSlot(0);
                Map enchantments = EnchantmentHelper.getEnchantments(temp);
                enchantments.put(out, EnchantmentHelper.getEnchantmentLevel((Enchantment)out, temp) + 1);
                EnchantmentHelper.setEnchantments(enchantments, temp);
                syncTile(false);
                te.markDirty();
            }
            if (recipePlayer != null) {
                EntityPlayer p = world.getPlayerEntityByName(recipePlayer);
                if (p != null) {
                    FMLCommonHandler.instance().firePlayerCraftingEvent(p, ((TilePedestal)te).getStackInSlot(0), new InventoryFake(recipeIngredients));
                }
            }
            recipeEssentia = new AspectList();
            recipeInstability = 0;
            syncTile(false);
            markDirty();
            world.addBlockEvent(pos.down(2), world.getBlockState(pos.down(2)).getBlock(), 12, 0);
            world.playSound(null, pos, SoundsTC.wand, SoundCategory.BLOCKS, 0.5f, 1.0f);
        }
    }
    
    private void getSurroundings() {
        Set<Long> stuff = new HashSet<Long>();
        pedestals.clear();
        tempBlockCount.clear();
        problemBlocks.clear();
        cycleTime = 10;
        stabilityReplenish = 0.0f;
        costMult = 1.0f;
        try {
            for (int xx = -8; xx <= 8; ++xx) {
                for (int zz = -8; zz <= 8; ++zz) {
                    boolean skip = false;
                    for (int yy = -3; yy <= 7; ++yy) {
                        if (xx != 0 || zz != 0) {
                            int x = pos.getX() + xx;
                            int y = pos.getY() - yy;
                            int z = pos.getZ() + zz;
                            BlockPos bp = new BlockPos(x, y, z);
                            Block bi = world.getBlockState(bp).getBlock();
                            if (bi instanceof BlockPedestal) {
                                pedestals.add(bp);
                            }
                            try {
                                if (bi == Blocks.SKULL || (bi instanceof IInfusionStabiliser && ((IInfusionStabiliser)bi).canStabaliseInfusion(getWorld(), bp))) {
                                    stuff.add(bp.toLong());
                                }
                            }
                            catch (Exception ex) {}
                        }
                    }
                }
            }
            while (!stuff.isEmpty()) {
                Long[] posArray = stuff.toArray(new Long[stuff.size()]);
                if (posArray == null) {
                    break;
                }
                if (posArray[0] == null) {
                    break;
                }
                long lp = posArray[0];
                try {
                    BlockPos c1 = BlockPos.fromLong(lp);
                    int x2 = pos.getX() - c1.getX();
                    int z2 = pos.getZ() - c1.getZ();
                    int x3 = pos.getX() + x2;
                    int z3 = pos.getZ() + z2;
                    BlockPos c2 = new BlockPos(x3, c1.getY(), z3);
                    Block sb1 = world.getBlockState(c1).getBlock();
                    Block sb2 = world.getBlockState(c2).getBlock();
                    float amt1 = 0.1f;
                    float amt2 = 0.1f;
                    if (sb1 instanceof IInfusionStabiliserExt) {
                        amt1 = ((IInfusionStabiliserExt)sb1).getStabilizationAmount(getWorld(), c1);
                    }
                    if (sb2 instanceof IInfusionStabiliserExt) {
                        amt2 = ((IInfusionStabiliserExt)sb2).getStabilizationAmount(getWorld(), c2);
                    }
                    if (sb1 == sb2 && amt1 == amt2) {
                        if (sb1 instanceof IInfusionStabiliserExt && ((IInfusionStabiliserExt)sb1).hasSymmetryPenalty(getWorld(), c1, c2)) {
                            stabilityReplenish -= ((IInfusionStabiliserExt)sb1).getSymmetryPenalty(getWorld(), c1);
                            problemBlocks.add(c1);
                        }
                        else {
                            stabilityReplenish += calcDeminishingReturns(sb1, amt1);
                        }
                    }
                    else {
                        stabilityReplenish -= Math.max(amt1, amt2);
                        problemBlocks.add(c1);
                    }
                    stuff.remove(c2.toLong());
                }
                catch (Exception ex2) {}
                stuff.remove(lp);
            }
            if (world.getBlockState(pos.add(-1, -2, -1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(1, -2, -1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(1, -2, 1)).getBlock() instanceof BlockPillar && world.getBlockState(pos.add(-1, -2, 1)).getBlock() instanceof BlockPillar) {
                if (world.getBlockState(pos.add(-1, -2, -1)).getBlock() == BlocksTC.pillarAncient && world.getBlockState(pos.add(1, -2, -1)).getBlock() == BlocksTC.pillarAncient && world.getBlockState(pos.add(1, -2, 1)).getBlock() == BlocksTC.pillarAncient && world.getBlockState(pos.add(-1, -2, 1)).getBlock() == BlocksTC.pillarAncient) {
                    --cycleTime;
                    costMult -= 0.1f;
                    stabilityReplenish -= 0.1f;
                }
                if (world.getBlockState(pos.add(-1, -2, -1)).getBlock() == BlocksTC.pillarEldritch && world.getBlockState(pos.add(1, -2, -1)).getBlock() == BlocksTC.pillarEldritch && world.getBlockState(pos.add(1, -2, 1)).getBlock() == BlocksTC.pillarEldritch && world.getBlockState(pos.add(-1, -2, 1)).getBlock() == BlocksTC.pillarEldritch) {
                    cycleTime -= 3;
                    costMult += 0.05f;
                    stabilityReplenish += 0.2f;
                }
            }
            int[] xm = { -1, 1, 1, -1 };
            int[] zm = { -1, -1, 1, 1 };
            for (int a = 0; a < 4; ++a) {
                Block b = world.getBlockState(pos.add(xm[a], -3, zm[a])).getBlock();
                if (b == BlocksTC.matrixSpeed) {
                    --cycleTime;
                    costMult += 0.01f;
                }
                if (b == BlocksTC.matrixCost) {
                    ++cycleTime;
                    costMult -= 0.02f;
                }
            }
            countDelay = cycleTime / 2;
            int apc = 0;
            for (BlockPos cc : pedestals) {
                boolean items = false;
                int x4 = pos.getX() - cc.getX();
                int z4 = pos.getZ() - cc.getZ();
                Block bb = world.getBlockState(cc).getBlock();
                if (bb == BlocksTC.pedestalEldritch) {
                    costMult += 0.0025f;
                }
                if (bb == BlocksTC.pedestalAncient) {
                    costMult -= 0.01f;
                }
            }
        }
        catch (Exception ex3) {}
    }
    
    private float calcDeminishingReturns(Block b, float base) {
        float bb = base;
        int c = tempBlockCount.containsKey(b) ? tempBlockCount.get(b) : 0;
        if (c > 0) {
            bb *= (float)Math.pow(0.75, c);
        }
        tempBlockCount.put(b, c + 1);
        return bb;
    }
    
    @Override
    public boolean onCasterRightClick(World world, ItemStack wandstack, EntityPlayer player, BlockPos pos, EnumFacing side, EnumHand hand) {
        if (world.isRemote && active && !crafting) {
            checkSurroundings = true;
        }
        if (!world.isRemote && active && !crafting) {
            craftingStart(player);
            return false;
        }
        if (!world.isRemote && !active && validLocation()) {
            world.playSound(null, pos, SoundsTC.craftstart, SoundCategory.BLOCKS, 0.5f, 1.0f);
            active = true;
            syncTile(false);
            markDirty();
            return false;
        }
        return false;
    }
    
    private void doEffects() {
        if (crafting) {
            if (craftCount == 0) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.infuserstart, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
            }
            else if (craftCount == 0 || craftCount % 65 == 0) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.infuser, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
            }
            ++craftCount;
            FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() - 2, pos.getZ(), 0.5f + world.rand.nextFloat() * 0.2f, 0.1f, 0.7f + world.rand.nextFloat() * 0.3f, 25, -0.03f);
        }
        else if (craftCount > 0) {
            craftCount -= 2;
            if (craftCount < 0) {
                craftCount = 0;
            }
            if (craftCount > 50) {
                craftCount = 50;
            }
        }
        if (active && startUp != 1.0f) {
            if (startUp < 1.0f) {
                startUp += Math.max(startUp / 10.0f, 0.001f);
            }
            if (startUp > 0.999) {
                startUp = 1.0f;
            }
        }
        if (!active && startUp > 0.0f) {
            if (startUp > 0.0f) {
                startUp -= startUp / 10.0f;
            }
            if (startUp < 0.001) {
                startUp = 0.0f;
            }
        }
        for (String fxk : sourceFX.keySet().toArray(new String[0])) {
            SourceFX fx = sourceFX.get(fxk);
            if (fx.ticks <= 0) {
                sourceFX.remove(fxk);
            }
            else {
                if (fx.loc.equals(pos)) {
                    Entity player = world.getEntityByID(fx.color);
                    if (player != null) {
                        for (int a = 0; a < 4; ++a) {
                            FXDispatcher.INSTANCE.drawInfusionParticles4(player.posX + (world.rand.nextFloat() - world.rand.nextFloat()) * player.width, player.getEntityBoundingBox().minY + world.rand.nextFloat() * player.height, player.posZ + (world.rand.nextFloat() - world.rand.nextFloat()) * player.width, pos.getX(), pos.getY(), pos.getZ());
                        }
                    }
                }
                else {
                    TileEntity tile = world.getTileEntity(fx.loc);
                    if (tile instanceof TilePedestal) {
                        ItemStack is = ((TilePedestal)tile).getSyncedStackInSlot(0);
                        if (is != null && !is.isEmpty()) {
                            if (world.rand.nextInt(3) == 0) {
                                FXDispatcher.INSTANCE.drawInfusionParticles3(fx.loc.getX() + world.rand.nextFloat(), fx.loc.getY() + world.rand.nextFloat() + 1.0f, fx.loc.getZ() + world.rand.nextFloat(), pos.getX(), pos.getY(), pos.getZ());
                            }
                            else {
                                Item bi = is.getItem();
                                if (bi instanceof ItemBlock) {
                                    for (int a2 = 0; a2 < 4; ++a2) {
                                        FXDispatcher.INSTANCE.drawInfusionParticles2(fx.loc.getX() + world.rand.nextFloat(), fx.loc.getY() + world.rand.nextFloat() + 1.0f, fx.loc.getZ() + world.rand.nextFloat(), pos, Block.getBlockFromItem(bi).getDefaultState(), is.getItemDamage());
                                    }
                                }
                                else {
                                    for (int a2 = 0; a2 < 4; ++a2) {
                                        FXDispatcher.INSTANCE.drawInfusionParticles1(fx.loc.getX() + 0.4f + world.rand.nextFloat() * 0.2f, fx.loc.getY() + 1.23f + world.rand.nextFloat() * 0.2f, fx.loc.getZ() + 0.4f + world.rand.nextFloat() * 0.2f, pos, is);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        fx.ticks = 0;
                    }
                }
                SourceFX sourceFX = fx;
                --sourceFX.ticks;
                this.sourceFX.put(fxk, fx);
            }
        }
        if (crafting && stability < 0.0f && world.rand.nextInt(250) <= Math.abs(stability)) {
            FXDispatcher.INSTANCE.spark(getPos().getX() + world.rand.nextFloat(), getPos().getY() + world.rand.nextFloat(), getPos().getZ() + world.rand.nextFloat(), 3.0f + world.rand.nextFloat() * 2.0f, 0.7f + world.rand.nextFloat() * 0.1f, 0.1f, 0.65f + world.rand.nextFloat() * 0.1f, 0.8f);
        }
        if (active && !problemBlocks.isEmpty() && world.rand.nextInt(25) == 0) {
            BlockPos p = problemBlocks.get(world.rand.nextInt(problemBlocks.size()));
            FXDispatcher.INSTANCE.spark(p.getX() + world.rand.nextFloat(), p.getY() + world.rand.nextFloat(), p.getZ() + world.rand.nextFloat(), 2.0f + world.rand.nextFloat(), 0.7f + world.rand.nextFloat() * 0.1f, 0.1f, 0.65f + world.rand.nextFloat() * 0.1f, 0.8f);
        }
    }
    
    @Override
    public AspectList getAspects() {
        return recipeEssentia;
    }
    
    @Override
    public void setAspects(AspectList aspects) {
    }
    
    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }
    
    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }
    
    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
    
    public String[] getIGogglesText() {
        float lpc = getLossPerCycle();
        if (lpc != 0.0f) {
            return new String[] { TextFormatting.BOLD + I18n.translateToLocal("stability." + getStability().name()), TextFormatting.GOLD + "" + TextFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(stabilityReplenish) + " " + I18n.translateToLocal("stability.gain"), TextFormatting.RED + "" + I18n.translateToLocal("stability.range") + TextFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(lpc) + " " + I18n.translateToLocal("stability.loss") };
        }
        return new String[] { TextFormatting.BOLD + I18n.translateToLocal("stability." + getStability().name()), TextFormatting.GOLD + "" + TextFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(stabilityReplenish) + " " + I18n.translateToLocal("stability.gain") };
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
        
        public SourceFX(BlockPos loc, int ticks, int color) {
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
