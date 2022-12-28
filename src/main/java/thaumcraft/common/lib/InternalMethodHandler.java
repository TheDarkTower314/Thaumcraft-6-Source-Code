package thaumcraft.common.lib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.golems.seals.ItemSealPlacer;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXPollute;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.world.aura.AuraHandler;


public class InternalMethodHandler implements IInternalMethodHandler
{
    @Override
    public boolean addKnowledge(EntityPlayer player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory field, int amount) {
        return amount != 0 && !player.world.isRemote && ResearchManager.addKnowledge(player, type, field, amount);
    }
    
    @Override
    public void addWarpToPlayer(EntityPlayer player, int amount, IPlayerWarp.EnumWarpType type) {
        if (amount == 0 || player.world.isRemote) {
            return;
        }
        IPlayerWarp pw = ThaumcraftCapabilities.getWarp(player);
        int cur = pw.get(type);
        if (amount < 0 && cur + amount < 0) {
            amount = cur;
        }
        pw.add(type, amount);
        if (type == IPlayerWarp.EnumWarpType.PERMANENT) {
            PacketHandler.INSTANCE.sendTo(new PacketWarpMessage(player, (byte)0, amount), (EntityPlayerMP)player);
        }
        if (type == IPlayerWarp.EnumWarpType.NORMAL) {
            PacketHandler.INSTANCE.sendTo(new PacketWarpMessage(player, (byte)1, amount), (EntityPlayerMP)player);
        }
        if (type == IPlayerWarp.EnumWarpType.TEMPORARY) {
            PacketHandler.INSTANCE.sendTo(new PacketWarpMessage(player, (byte)2, amount), (EntityPlayerMP)player);
        }
        if (amount > 0) {
            pw.setCounter(pw.get(IPlayerWarp.EnumWarpType.TEMPORARY) + pw.get(IPlayerWarp.EnumWarpType.PERMANENT) + pw.get(IPlayerWarp.EnumWarpType.NORMAL));
        }
        if (type != IPlayerWarp.EnumWarpType.TEMPORARY && ThaumcraftCapabilities.knowsResearchStrict(player, "FIRSTSTEPS") && !ThaumcraftCapabilities.knowsResearchStrict(player, "WARP")) {
            completeResearch(player, "WARP");
            player.sendStatusMessage(new TextComponentTranslation("research.WARP.warn"), true);
        }
        pw.sync((EntityPlayerMP)player);
    }
    
    @Override
    public boolean progressResearch(EntityPlayer player, String researchkey) {
        return researchkey != null && !player.world.isRemote && ResearchManager.progressResearch(player, researchkey);
    }
    
    @Override
    public boolean completeResearch(EntityPlayer player, String researchkey) {
        return researchkey != null && !player.world.isRemote && ResearchManager.completeResearch(player, researchkey);
    }
    
    @Override
    public boolean doesPlayerHaveRequisites(EntityPlayer player, String researchkey) {
        return ResearchManager.doesPlayerHaveRequisites(player, researchkey);
    }
    
    @Override
    public AspectList getObjectAspects(ItemStack is) {
        return ThaumcraftCraftingManager.getObjectTags(is);
    }
    
    @Override
    public AspectList generateTags(ItemStack is) {
        return ThaumcraftCraftingManager.generateTags(is);
    }
    
    @Override
    public float drainFlux(World world, BlockPos pos, float amount, boolean simulate) {
        return AuraHandler.drainFlux(world, pos, amount, simulate);
    }
    
    @Override
    public float getFlux(World world, BlockPos pos) {
        return AuraHandler.getFlux(world, pos);
    }
    
    @Override
    public float drainVis(World world, BlockPos pos, float amount, boolean simulate) {
        return AuraHandler.drainVis(world, pos, amount, simulate);
    }
    
    @Override
    public void addVis(World world, BlockPos pos, float amount) {
        AuraHandler.addVis(world, pos, amount);
    }
    
    @Override
    public float getTotalAura(World world, BlockPos pos) {
        return AuraHandler.getTotalAura(world, pos);
    }
    
    @Override
    public float getVis(World world, BlockPos pos) {
        return AuraHandler.getVis(world, pos);
    }
    
    @Override
    public int getAuraBase(World world, BlockPos pos) {
        return AuraHandler.getAuraBase(world, pos);
    }
    
    @Override
    public void addFlux(World world, BlockPos pos, float amount, boolean showEffect) {
        if (world.isRemote) {
            return;
        }
        AuraHandler.addFlux(world, pos, amount);
        if (showEffect && amount > 0.0f) {
            PacketHandler.INSTANCE.sendToAllAround(new PacketFXPollute(pos, amount), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
        }
    }
    
    @Override
    public void registerSeal(ISeal seal) {
        SealHandler.registerSeal(seal);
    }
    
    @Override
    public ISeal getSeal(String key) {
        return SealHandler.getSeal(key);
    }
    
    @Override
    public ISealEntity getSealEntity(int dim, SealPos pos) {
        return SealHandler.getSealEntity(dim, pos);
    }
    
    @Override
    public void addGolemTask(int dim, Task task) {
        TaskHandler.addTask(dim, task);
    }
    
    @Override
    public boolean shouldPreserveAura(World world, EntityPlayer player, BlockPos pos) {
        return AuraHandler.shouldPreserveAura(world, player, pos);
    }
    
    @Override
    public ItemStack getSealStack(String key) {
        return ItemSealPlacer.getSealStack(key);
    }
    
    @Override
    public int getActualWarp(EntityPlayer player) {
        IPlayerWarp wc = ThaumcraftCapabilities.getWarp(player);
        return wc.get(IPlayerWarp.EnumWarpType.NORMAL) + wc.get(IPlayerWarp.EnumWarpType.PERMANENT);
    }
}
