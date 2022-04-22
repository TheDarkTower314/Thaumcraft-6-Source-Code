// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib;

import thaumcraft.common.golems.seals.ItemSealPlacer;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.api.golems.seals.ISeal;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXPollute;
import thaumcraft.common.world.aura.AuraHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.internal.IInternalMethodHandler;

public class InternalMethodHandler implements IInternalMethodHandler
{
    @Override
    public boolean addKnowledge(final EntityPlayer player, final IPlayerKnowledge.EnumKnowledgeType type, final ResearchCategory field, final int amount) {
        return amount != 0 && !player.world.isRemote && ResearchManager.addKnowledge(player, type, field, amount);
    }
    
    @Override
    public void addWarpToPlayer(final EntityPlayer player, int amount, final IPlayerWarp.EnumWarpType type) {
        if (amount == 0 || player.world.isRemote) {
            return;
        }
        final IPlayerWarp pw = ThaumcraftCapabilities.getWarp(player);
        final int cur = pw.get(type);
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
    public boolean progressResearch(final EntityPlayer player, final String researchkey) {
        return researchkey != null && !player.world.isRemote && ResearchManager.progressResearch(player, researchkey);
    }
    
    @Override
    public boolean completeResearch(final EntityPlayer player, final String researchkey) {
        return researchkey != null && !player.world.isRemote && ResearchManager.completeResearch(player, researchkey);
    }
    
    @Override
    public boolean doesPlayerHaveRequisites(final EntityPlayer player, final String researchkey) {
        return ResearchManager.doesPlayerHaveRequisites(player, researchkey);
    }
    
    @Override
    public AspectList getObjectAspects(final ItemStack is) {
        return ThaumcraftCraftingManager.getObjectTags(is);
    }
    
    @Override
    public AspectList generateTags(final ItemStack is) {
        return ThaumcraftCraftingManager.generateTags(is);
    }
    
    @Override
    public float drainFlux(final World world, final BlockPos pos, final float amount, final boolean simulate) {
        return AuraHandler.drainFlux(world, pos, amount, simulate);
    }
    
    @Override
    public float getFlux(final World world, final BlockPos pos) {
        return AuraHandler.getFlux(world, pos);
    }
    
    @Override
    public float drainVis(final World world, final BlockPos pos, final float amount, final boolean simulate) {
        return AuraHandler.drainVis(world, pos, amount, simulate);
    }
    
    @Override
    public void addVis(final World world, final BlockPos pos, final float amount) {
        AuraHandler.addVis(world, pos, amount);
    }
    
    @Override
    public float getTotalAura(final World world, final BlockPos pos) {
        return AuraHandler.getTotalAura(world, pos);
    }
    
    @Override
    public float getVis(final World world, final BlockPos pos) {
        return AuraHandler.getVis(world, pos);
    }
    
    @Override
    public int getAuraBase(final World world, final BlockPos pos) {
        return AuraHandler.getAuraBase(world, pos);
    }
    
    @Override
    public void addFlux(final World world, final BlockPos pos, final float amount, final boolean showEffect) {
        if (world.isRemote) {
            return;
        }
        AuraHandler.addFlux(world, pos, amount);
        if (showEffect && amount > 0.0f) {
            PacketHandler.INSTANCE.sendToAllAround(new PacketFXPollute(pos, amount), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
        }
    }
    
    @Override
    public void registerSeal(final ISeal seal) {
        SealHandler.registerSeal(seal);
    }
    
    @Override
    public ISeal getSeal(final String key) {
        return SealHandler.getSeal(key);
    }
    
    @Override
    public ISealEntity getSealEntity(final int dim, final SealPos pos) {
        return SealHandler.getSealEntity(dim, pos);
    }
    
    @Override
    public void addGolemTask(final int dim, final Task task) {
        TaskHandler.addTask(dim, task);
    }
    
    @Override
    public boolean shouldPreserveAura(final World world, final EntityPlayer player, final BlockPos pos) {
        return AuraHandler.shouldPreserveAura(world, player, pos);
    }
    
    @Override
    public ItemStack getSealStack(final String key) {
        return ItemSealPlacer.getSealStack(key);
    }
    
    @Override
    public int getActualWarp(final EntityPlayer player) {
        final IPlayerWarp wc = ThaumcraftCapabilities.getWarp(player);
        return wc.get(IPlayerWarp.EnumWarpType.NORMAL) + wc.get(IPlayerWarp.EnumWarpType.PERMANENT);
    }
}
