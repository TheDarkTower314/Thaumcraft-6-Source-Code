package thaumcraft.common.lib;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumcraft.common.lib.research.ResearchManager;


public class CommandThaumcraft extends CommandBase
{
    private List aliases;
    
    public CommandThaumcraft() {
        (aliases = new ArrayList()).add("thaumcraft");
        aliases.add("thaum");
        aliases.add("tc");
    }
    
    public String getName() {
        return "thaumcraft";
    }
    
    public List<String> getAliases() {
        return aliases;
    }
    
    public String getUsage(ICommandSender icommandsender) {
        return "/thaumcraft <action> [<player> [<params>]]";
    }
    
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    public boolean isUsernameIndex(String[] astring, int i) {
        return i == 1;
    }
    
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentTranslation("§cInvalid arguments"));
            sender.sendMessage(new TextComponentTranslation("§cUse /thaumcraft help to get help"));
            return;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            for (ResearchCategory rc : ResearchCategories.researchCategories.values()) {
                rc.research.clear();
            }
            ResearchManager.parseAllResearch();
            sender.sendMessage(new TextComponentTranslation("§5Success!"));
        }
        else if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(new TextComponentTranslation("§3You can also use /thaum or /tc instead of /thaumcraft."));
            sender.sendMessage(new TextComponentTranslation("§3Use this to give research to a player."));
            sender.sendMessage(new TextComponentTranslation("  /thaumcraft research <list|player> <list|all|reset|<research>>"));
            sender.sendMessage(new TextComponentTranslation("§3Use this to remove research from a player."));
            sender.sendMessage(new TextComponentTranslation("  /thaumcraft research <player> revoke <research>"));
            sender.sendMessage(new TextComponentTranslation("§3Use this to give set a players warp level."));
            sender.sendMessage(new TextComponentTranslation("  /thaumcraft warp <player> <add|set> <amount> <PERM|TEMP>"));
            sender.sendMessage(new TextComponentTranslation("  not specifying perm or temp will just add normal warp"));
            sender.sendMessage(new TextComponentTranslation("§3Use this to reload json research data"));
            sender.sendMessage(new TextComponentTranslation("  /thaumcraft reload"));
        }
        else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("research") && args[1].equalsIgnoreCase("list")) {
                listResearch(sender);
            }
            else {
                EntityPlayerMP entityplayermp = getPlayer(server, sender, args[1]);
                if (args[0].equalsIgnoreCase("research")) {
                    if (args.length == 3) {
                        if (args[2].equalsIgnoreCase("list")) {
                            listAllResearch(sender, entityplayermp);
                        }
                        else if (args[2].equalsIgnoreCase("all")) {
                            giveAllResearch(sender, entityplayermp);
                        }
                        else if (args[2].equalsIgnoreCase("reset")) {
                            resetResearch(sender, entityplayermp);
                        }
                        else {
                            giveResearch(sender, entityplayermp, args[2]);
                        }
                    }
                    else if (args.length == 4) {
                        if (args[2].equalsIgnoreCase("revoke")) {
                            revokeResearch(sender, entityplayermp, args[3]);
                        }
                    }
                    else {
                        sender.sendMessage(new TextComponentTranslation("§cInvalid arguments"));
                        sender.sendMessage(new TextComponentTranslation("§cUse /thaumcraft research <list|player> <list|all|reset|<research>>"));
                    }
                }
                else if (args[0].equalsIgnoreCase("warp")) {
                    if (args.length >= 4 && args[2].equalsIgnoreCase("set")) {
                        int i = parseInt(args[3], 0);
                        setWarp(sender, entityplayermp, i, (args.length == 5) ? args[4] : "");
                    }
                    else if (args.length >= 4 && args[2].equalsIgnoreCase("add")) {
                        int i = parseInt(args[3], -100, 100);
                        addWarp(sender, entityplayermp, i, (args.length == 5) ? args[4] : "");
                    }
                    else {
                        sender.sendMessage(new TextComponentTranslation("§cInvalid arguments"));
                        sender.sendMessage(new TextComponentTranslation("§cUse /thaumcraft warp <player> <add|set> <amount> <PERM|TEMP>"));
                    }
                }
                else {
                    sender.sendMessage(new TextComponentTranslation("§cInvalid arguments"));
                    sender.sendMessage(new TextComponentTranslation("§cUse /thaumcraft help to get help"));
                }
            }
        }
        else {
            sender.sendMessage(new TextComponentTranslation("§cInvalid arguments"));
            sender.sendMessage(new TextComponentTranslation("§cUse /thaumcraft help to get help"));
        }
    }
    
    private void setWarp(ICommandSender icommandsender, EntityPlayerMP player, int i, String type) {
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
        player.sendMessage(new TextComponentTranslation("§5" + icommandsender.getName() + " set your warp to " + i));
        icommandsender.sendMessage(new TextComponentTranslation("§5Success!"));
    }
    
    private void addWarp(ICommandSender icommandsender, EntityPlayerMP player, int i, String type) {
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
        player.sendMessage(new TextComponentTranslation("§5" + icommandsender.getName() + " added " + i + " warp to your total."));
        icommandsender.sendMessage(new TextComponentTranslation("§5Success!"));
    }
    
    private void listResearch(ICommandSender icommandsender) {
        Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
        for (ResearchCategory cat : rc) {
            Collection<ResearchEntry> rl = cat.research.values();
            for (ResearchEntry ri : rl) {
                icommandsender.sendMessage(new TextComponentTranslation("§5" + ri.getKey()));
            }
        }
    }
    
    void giveResearch(ICommandSender icommandsender, EntityPlayerMP player, String research) {
        if (ResearchCategories.getResearch(research) != null) {
            giveRecursiveResearch(player, research);
            ThaumcraftCapabilities.getKnowledge(player).sync(player);
            player.sendMessage(new TextComponentTranslation("§5" + icommandsender.getName() + " gave you " + research + " research and its requisites."));
            icommandsender.sendMessage(new TextComponentTranslation("§5Success!"));
        }
        else {
            icommandsender.sendMessage(new TextComponentTranslation("§cResearch does not exist."));
        }
    }
    
    public static void giveRecursiveResearch(EntityPlayer player, String research) {
        if (research.contains("@")) {
            int i = research.indexOf("@");
            research = research.substring(0, i);
        }
        ResearchEntry res = ResearchCategories.getResearch(research);
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (!knowledge.isResearchComplete(research)) {
            if (res != null && res.getParents() != null) {
                for (String rsi : res.getParentsStripped()) {
                    giveRecursiveResearch(player, rsi);
                }
            }
            if (res != null && res.getStages() != null) {
                for (ResearchStage page : res.getStages()) {
                    if (page.getResearch() != null) {
                        for (String gr : page.getResearch()) {
                            ResearchManager.completeResearch(player, gr);
                        }
                    }
                }
            }
            ResearchManager.completeResearch(player, research);
            for (String rc : ResearchCategories.researchCategories.keySet()) {
                for (ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
                    if (ri.getStages() != null) {
                        for (ResearchStage stage : ri.getStages()) {
                            if (stage.getResearch() != null && Arrays.asList(stage.getResearch()).contains(research)) {
                                ThaumcraftCapabilities.getKnowledge(player).setResearchFlag(ri.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
                                break;
                            }
                        }
                    }
                }
            }
            if (res != null && res.getSiblings() != null) {
                for (String rsi : res.getSiblings()) {
                    giveRecursiveResearch(player, rsi);
                }
            }
        }
    }
    
    private void revokeResearch(ICommandSender icommandsender, EntityPlayerMP player, String research) {
        if (ResearchCategories.getResearch(research) != null) {
            revokeRecursiveResearch(player, research);
            ThaumcraftCapabilities.getKnowledge(player).sync(player);
            player.sendMessage(new TextComponentTranslation("§5" + icommandsender.getName() + " removed " + research + " research and its children."));
            icommandsender.sendMessage(new TextComponentTranslation("§5Success!"));
        }
        else {
            icommandsender.sendMessage(new TextComponentTranslation("§cResearch does not exist."));
        }
    }
    
    public static void revokeRecursiveResearch(EntityPlayer player, String research) {
        if (research.contains("@")) {
            int i = research.indexOf("@");
            research = research.substring(0, i);
        }
        ResearchEntry res = ResearchCategories.getResearch(research);
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (knowledge.isResearchComplete(research)) {
            for (String rc : ResearchCategories.researchCategories.keySet()) {
                for (ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
                    if (ri != null && ri.getParents() != null && knowledge.isResearchComplete(ri.getKey())) {
                        for (String rsi : ri.getParentsStripped()) {
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
    
    void listAllResearch(ICommandSender icommandsender, EntityPlayerMP player) {
        String ss = "";
        for (String key : ThaumcraftCapabilities.getKnowledge(player).getResearchList()) {
            if (ss.length() != 0) {
                ss += ", ";
            }
            ss += key;
        }
        icommandsender.sendMessage(new TextComponentTranslation("§5Research for " + player.getName()));
        icommandsender.sendMessage(new TextComponentTranslation("§5" + ss));
    }
    
    void giveAllResearch(ICommandSender icommandsender, EntityPlayerMP player) {
        Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
        for (ResearchCategory cat : rc) {
            Collection<ResearchEntry> rl = cat.research.values();
            for (ResearchEntry ri : rl) {
                giveRecursiveResearch(player, ri.getKey());
            }
        }
        player.sendMessage(new TextComponentTranslation("§5" + icommandsender.getName() + " has given you all research."));
        icommandsender.sendMessage(new TextComponentTranslation("§5Success!"));
    }
    
    void resetResearch(ICommandSender icommandsender, EntityPlayerMP player) {
        ThaumcraftCapabilities.getKnowledge(player).clear();
        Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
        for (ResearchCategory cat : rc) {
            Collection<ResearchEntry> res = cat.research.values();
            for (ResearchEntry ri : res) {
                if (ri.hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                    ResearchManager.completeResearch(player, ri.getKey(), false);
                }
            }
        }
        player.sendMessage(new TextComponentTranslation("§5" + icommandsender.getName() + " has reset all your research."));
        icommandsender.sendMessage(new TextComponentTranslation("§5Success!"));
        ThaumcraftCapabilities.getKnowledge(player).sync(player);
    }
}
