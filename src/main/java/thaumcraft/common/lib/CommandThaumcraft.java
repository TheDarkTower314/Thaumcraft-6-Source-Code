// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib;

import thaumcraft.api.research.ResearchStage;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import java.util.Arrays;
import java.util.Collection;
import thaumcraft.api.research.ResearchEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.capabilities.IPlayerWarp;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import java.util.Iterator;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchCategories;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;

public class CommandThaumcraft extends CommandBase
{
    private List aliases;
    
    public CommandThaumcraft() {
        (this.aliases = new ArrayList()).add("thaumcraft");
        this.aliases.add("thaum");
        this.aliases.add("tc");
    }
    
    public String getName() {
        return "thaumcraft";
    }
    
    public List<String> getAliases() {
        return this.aliases;
    }
    
    public String getUsage(final ICommandSender icommandsender) {
        return "/thaumcraft <action> [<player> [<params>]]";
    }
    
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    public boolean isUsernameIndex(final String[] astring, final int i) {
        return i == 1;
    }
    
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentTranslation("�cInvalid arguments", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("�cUse /thaumcraft help to get help", new Object[0]));
            return;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            for (final ResearchCategory rc : ResearchCategories.researchCategories.values()) {
                rc.research.clear();
            }
            ResearchManager.parseAllResearch();
            sender.sendMessage(new TextComponentTranslation("�5Success!", new Object[0]));
        }
        else if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(new TextComponentTranslation("�3You can also use /thaum or /tc instead of /thaumcraft.", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("�3Use this to give research to a player.", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("  /thaumcraft research <list|player> <list|all|reset|<research>>", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("�3Use this to remove research from a player.", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("  /thaumcraft research <player> revoke <research>", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("�3Use this to give set a players warp level.", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("  /thaumcraft warp <player> <add|set> <amount> <PERM|TEMP>", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("  not specifying perm or temp will just add normal warp", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("�3Use this to reload json research data", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("  /thaumcraft reload", new Object[0]));
        }
        else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("research") && args[1].equalsIgnoreCase("list")) {
                this.listResearch(sender);
            }
            else {
                final EntityPlayerMP entityplayermp = getPlayer(server, sender, args[1]);
                if (args[0].equalsIgnoreCase("research")) {
                    if (args.length == 3) {
                        if (args[2].equalsIgnoreCase("list")) {
                            this.listAllResearch(sender, entityplayermp);
                        }
                        else if (args[2].equalsIgnoreCase("all")) {
                            this.giveAllResearch(sender, entityplayermp);
                        }
                        else if (args[2].equalsIgnoreCase("reset")) {
                            this.resetResearch(sender, entityplayermp);
                        }
                        else {
                            this.giveResearch(sender, entityplayermp, args[2]);
                        }
                    }
                    else if (args.length == 4) {
                        if (args[2].equalsIgnoreCase("revoke")) {
                            this.revokeResearch(sender, entityplayermp, args[3]);
                        }
                    }
                    else {
                        sender.sendMessage(new TextComponentTranslation("�cInvalid arguments", new Object[0]));
                        sender.sendMessage(new TextComponentTranslation("�cUse /thaumcraft research <list|player> <list|all|reset|<research>>", new Object[0]));
                    }
                }
                else if (args[0].equalsIgnoreCase("warp")) {
                    if (args.length >= 4 && args[2].equalsIgnoreCase("set")) {
                        final int i = parseInt(args[3], 0);
                        this.setWarp(sender, entityplayermp, i, (args.length == 5) ? args[4] : "");
                    }
                    else if (args.length >= 4 && args[2].equalsIgnoreCase("add")) {
                        final int i = parseInt(args[3], -100, 100);
                        this.addWarp(sender, entityplayermp, i, (args.length == 5) ? args[4] : "");
                    }
                    else {
                        sender.sendMessage(new TextComponentTranslation("�cInvalid arguments", new Object[0]));
                        sender.sendMessage(new TextComponentTranslation("�cUse /thaumcraft warp <player> <add|set> <amount> <PERM|TEMP>", new Object[0]));
                    }
                }
                else {
                    sender.sendMessage(new TextComponentTranslation("�cInvalid arguments", new Object[0]));
                    sender.sendMessage(new TextComponentTranslation("�cUse /thaumcraft help to get help", new Object[0]));
                }
            }
        }
        else {
            sender.sendMessage(new TextComponentTranslation("�cInvalid arguments", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("�cUse /thaumcraft help to get help", new Object[0]));
        }
    }
    
    private void setWarp(final ICommandSender icommandsender, final EntityPlayerMP player, final int i, final String type) {
        if (type.equalsIgnoreCase("PERM")) {
            ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.PERMANENT, i);
        }
        else if (type.equalsIgnoreCase("TEMP")) {
            ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.TEMPORARY, i);
        }
        else {
            ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.NORMAL, i);
        }
        ThaumcraftCapabilities.getWarp(player).sync(player);
        player.sendMessage(new TextComponentTranslation("�5" + icommandsender.getName() + " set your warp to " + i, new Object[0]));
        icommandsender.sendMessage(new TextComponentTranslation("�5Success!", new Object[0]));
    }
    
    private void addWarp(final ICommandSender icommandsender, final EntityPlayerMP player, final int i, final String type) {
        if (type.equalsIgnoreCase("PERM")) {
            ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.PERMANENT, i);
        }
        else if (type.equalsIgnoreCase("TEMP")) {
            ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.TEMPORARY, i);
        }
        else {
            ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.NORMAL, i);
        }
        ThaumcraftCapabilities.getWarp(player).sync(player);
        PacketHandler.INSTANCE.sendTo(new PacketWarpMessage(player, (byte)0, i), player);
        player.sendMessage(new TextComponentTranslation("�5" + icommandsender.getName() + " added " + i + " warp to your total.", new Object[0]));
        icommandsender.sendMessage(new TextComponentTranslation("�5Success!", new Object[0]));
    }
    
    private void listResearch(final ICommandSender icommandsender) {
        final Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
        for (final ResearchCategory cat : rc) {
            final Collection<ResearchEntry> rl = cat.research.values();
            for (final ResearchEntry ri : rl) {
                icommandsender.sendMessage(new TextComponentTranslation("�5" + ri.getKey(), new Object[0]));
            }
        }
    }
    
    void giveResearch(final ICommandSender icommandsender, final EntityPlayerMP player, final String research) {
        if (ResearchCategories.getResearch(research) != null) {
            giveRecursiveResearch(player, research);
            ThaumcraftCapabilities.getKnowledge(player).sync(player);
            player.sendMessage(new TextComponentTranslation("�5" + icommandsender.getName() + " gave you " + research + " research and its requisites.", new Object[0]));
            icommandsender.sendMessage(new TextComponentTranslation("�5Success!", new Object[0]));
        }
        else {
            icommandsender.sendMessage(new TextComponentTranslation("�cResearch does not exist.", new Object[0]));
        }
    }
    
    public static void giveRecursiveResearch(final EntityPlayer player, String research) {
        if (research.contains("@")) {
            final int i = research.indexOf("@");
            research = research.substring(0, i);
        }
        final ResearchEntry res = ResearchCategories.getResearch(research);
        final IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (!knowledge.isResearchComplete(research)) {
            if (res != null && res.getParents() != null) {
                for (final String rsi : res.getParentsStripped()) {
                    giveRecursiveResearch(player, rsi);
                }
            }
            if (res != null && res.getStages() != null) {
                for (final ResearchStage page : res.getStages()) {
                    if (page.getResearch() != null) {
                        for (final String gr : page.getResearch()) {
                            ResearchManager.completeResearch(player, gr);
                        }
                    }
                }
            }
            ResearchManager.completeResearch(player, research);
            for (final String rc : ResearchCategories.researchCategories.keySet()) {
                for (final ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
                    if (ri.getStages() != null) {
                        for (final ResearchStage stage : ri.getStages()) {
                            if (stage.getResearch() != null && Arrays.asList(stage.getResearch()).contains(research)) {
                                ThaumcraftCapabilities.getKnowledge(player).setResearchFlag(ri.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
                                break;
                            }
                        }
                    }
                }
            }
            if (res != null && res.getSiblings() != null) {
                for (final String rsi : res.getSiblings()) {
                    giveRecursiveResearch(player, rsi);
                }
            }
        }
    }
    
    private void revokeResearch(final ICommandSender icommandsender, final EntityPlayerMP player, final String research) {
        if (ResearchCategories.getResearch(research) != null) {
            revokeRecursiveResearch(player, research);
            ThaumcraftCapabilities.getKnowledge(player).sync(player);
            player.sendMessage(new TextComponentTranslation("�5" + icommandsender.getName() + " removed " + research + " research and its children.", new Object[0]));
            icommandsender.sendMessage(new TextComponentTranslation("�5Success!", new Object[0]));
        }
        else {
            icommandsender.sendMessage(new TextComponentTranslation("�cResearch does not exist.", new Object[0]));
        }
    }
    
    public static void revokeRecursiveResearch(final EntityPlayer player, String research) {
        if (research.contains("@")) {
            final int i = research.indexOf("@");
            research = research.substring(0, i);
        }
        final ResearchEntry res = ResearchCategories.getResearch(research);
        final IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (knowledge.isResearchComplete(research)) {
            for (final String rc : ResearchCategories.researchCategories.keySet()) {
                for (final ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
                    if (ri != null && ri.getParents() != null && knowledge.isResearchComplete(ri.getKey())) {
                        for (final String rsi : ri.getParentsStripped()) {
                            if (rsi.equals(research)) {
                                revokeRecursiveResearch(player, ri.getKey());
                            }
                        }
                    }
                }
            }
            ThaumcraftCapabilities.getKnowledge(player).removeResearch(research);
        }
    }
    
    void listAllResearch(final ICommandSender icommandsender, final EntityPlayerMP player) {
        String ss = "";
        for (final String key : ThaumcraftCapabilities.getKnowledge(player).getResearchList()) {
            if (ss.length() != 0) {
                ss += ", ";
            }
            ss += key;
        }
        icommandsender.sendMessage(new TextComponentTranslation("�5Research for " + player.getName(), new Object[0]));
        icommandsender.sendMessage(new TextComponentTranslation("�5" + ss, new Object[0]));
    }
    
    void giveAllResearch(final ICommandSender icommandsender, final EntityPlayerMP player) {
        final Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
        for (final ResearchCategory cat : rc) {
            final Collection<ResearchEntry> rl = cat.research.values();
            for (final ResearchEntry ri : rl) {
                giveRecursiveResearch(player, ri.getKey());
            }
        }
        player.sendMessage(new TextComponentTranslation("�5" + icommandsender.getName() + " has given you all research.", new Object[0]));
        icommandsender.sendMessage(new TextComponentTranslation("�5Success!", new Object[0]));
    }
    
    void resetResearch(final ICommandSender icommandsender, final EntityPlayerMP player) {
        ThaumcraftCapabilities.getKnowledge(player).clear();
        final Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
        for (final ResearchCategory cat : rc) {
            final Collection<ResearchEntry> res = cat.research.values();
            for (final ResearchEntry ri : res) {
                if (ri.hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                    ResearchManager.completeResearch(player, ri.getKey(), false);
                }
            }
        }
        player.sendMessage(new TextComponentTranslation("�5" + icommandsender.getName() + " has reset all your research.", new Object[0]));
        icommandsender.sendMessage(new TextComponentTranslation("�5Success!", new Object[0]));
        ThaumcraftCapabilities.getKnowledge(player).sync(player);
    }
}
