package thaumcraft.common.lib.capabilities;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;


public class PlayerKnowledge
{
    public static void preInit() {
        CapabilityManager.INSTANCE.register(IPlayerKnowledge.class, new Capability.IStorage<IPlayerKnowledge>() {
            public NBTTagCompound writeNBT(Capability<IPlayerKnowledge> capability, IPlayerKnowledge instance, EnumFacing side) {
                return instance.serializeNBT();
            }
            
            public void readNBT(Capability<IPlayerKnowledge> capability, IPlayerKnowledge instance, EnumFacing side, NBTBase nbt) {
                if (nbt instanceof NBTTagCompound) {
                    instance.deserializeNBT((NBTTagCompound) nbt);
                }
            }
        }, DefaultImpl::new);
    }
    
    private static class DefaultImpl implements IPlayerKnowledge
    {
        private HashSet<String> research;
        private Map<String, Integer> stages;
        private Map<String, HashSet<EnumResearchFlag>> flags;
        private Map<String, Integer> knowledge;
        
        private DefaultImpl() {
            research = new HashSet<String>();
            stages = new HashMap<String, Integer>();
            flags = new HashMap<String, HashSet<EnumResearchFlag>>();
            knowledge = new HashMap<String, Integer>();
        }
        
        @Override
        public void clear() {
            research.clear();
            flags.clear();
            stages.clear();
            knowledge.clear();
        }
        
        @Override
        public EnumResearchStatus getResearchStatus(@Nonnull String res) {
            if (!isResearchKnown(res)) {
                return EnumResearchStatus.UNKNOWN;
            }
            ResearchEntry entry = ResearchCategories.getResearch(res);
            if (entry == null || entry.getStages() == null || getResearchStage(res) > entry.getStages().length) {
                return EnumResearchStatus.COMPLETE;
            }
            return EnumResearchStatus.IN_PROGRESS;
        }
        
        @Override
        public boolean isResearchKnown(String res) {
            if (res == null) {
                return false;
            }
            if (res.equals("")) {
                return true;
            }
            String[] ss = res.split("@");
            return (ss.length <= 1 || getResearchStage(ss[0]) >= MathHelper.getInt(ss[1], 0)) && research.contains(ss[0]);
        }
        
        @Override
        public boolean isResearchComplete(String res) {
            return getResearchStatus(res) == EnumResearchStatus.COMPLETE;
        }
        
        @Override
        public int getResearchStage(String res) {
            if (res == null || !research.contains(res)) {
                return -1;
            }
            Integer stage = stages.get(res);
            return (stage == null) ? 0 : stage;
        }
        
        @Override
        public boolean setResearchStage(String res, int stage) {
            if (res == null || !research.contains(res) || stage <= 0) {
                return false;
            }
            stages.put(res, stage);
            return true;
        }
        
        @Override
        public boolean addResearch(@Nonnull String res) {
            if (!isResearchKnown(res)) {
                research.add(res);
                return true;
            }
            return false;
        }
        
        @Override
        public boolean removeResearch(@Nonnull String res) {
            if (isResearchKnown(res)) {
                research.remove(res);
                return true;
            }
            return false;
        }
        
        @Nonnull
        @Override
        public Set<String> getResearchList() {
            return Collections.unmodifiableSet(research);
        }
        
        @Override
        public boolean setResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
            HashSet<EnumResearchFlag> list = flags.get(res);
            if (list == null) {
                list = new HashSet<EnumResearchFlag>();
                flags.put(res, list);
            }
            if (list.contains(flag)) {
                return false;
            }
            list.add(flag);
            return true;
        }
        
        @Override
        public boolean clearResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
            HashSet<EnumResearchFlag> list = flags.get(res);
            if (list != null) {
                boolean b = list.remove(flag);
                if (list.isEmpty()) {
                    flags.remove(research);
                }
                return b;
            }
            return false;
        }
        
        @Override
        public boolean hasResearchFlag(@Nonnull String res, @Nonnull EnumResearchFlag flag) {
            return flags.get(res) != null && flags.get(res).contains(flag);
        }
        
        private String getKey(EnumKnowledgeType type, ResearchCategory category) {
            return type.getAbbreviation() + "_" + ((category == null) ? "" : category.key);
        }
        
        @Override
        public boolean addKnowledge(EnumKnowledgeType type, ResearchCategory category, int amount) {
            String key = getKey(type, category);
            int c = getKnowledgeRaw(type, category);
            if (c + amount < 0) {
                return false;
            }
            c += amount;
            knowledge.put(key, c);
            return true;
        }
        
        @Override
        public int getKnowledge(EnumKnowledgeType type, ResearchCategory category) {
            String key = getKey(type, category);
            int c = knowledge.containsKey(key) ? knowledge.get(key) : 0;
            return (int)Math.floor(c / (double)type.getProgression());
        }
        
        @Override
        public int getKnowledgeRaw(EnumKnowledgeType type, ResearchCategory category) {
            String key = getKey(type, category);
            return knowledge.containsKey(key) ? knowledge.get(key) : 0;
        }
        
        @Override
        public void sync(@Nonnull EntityPlayerMP player) {
            PacketHandler.INSTANCE.sendTo(new PacketSyncKnowledge(player), player);
        }
        
        public NBTTagCompound serializeNBT() {
            NBTTagCompound rootTag = new NBTTagCompound();
            NBTTagList researchList = new NBTTagList();
            for (String resKey : research) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("key", resKey);
                if (stages.containsKey(resKey)) {
                    tag.setInteger("stage", stages.get(resKey));
                }
                if (flags.containsKey(resKey)) {
                    HashSet<EnumResearchFlag> list = flags.get(resKey);
                    if (list != null) {
                        String fs = "";
                        for (EnumResearchFlag flag : list) {
                            if (fs.length() > 0) {
                                fs += ",";
                            }
                            fs += flag.name();
                        }
                        tag.setString("flags", fs);
                    }
                }
                researchList.appendTag(tag);
            }
            rootTag.setTag("research", researchList);
            NBTTagList knowledgeList = new NBTTagList();
            for (String key : knowledge.keySet()) {
                Integer c = knowledge.get(key);
                if (c != null && c > 0 && key != null && !key.isEmpty()) {
                    NBTTagCompound tag2 = new NBTTagCompound();
                    tag2.setString("key", key);
                    tag2.setInteger("amount", c);
                    knowledgeList.appendTag(tag2);
                }
            }
            rootTag.setTag("knowledge", knowledgeList);
            return rootTag;
        }
        
        public void deserializeNBT(NBTTagCompound rootTag) {
            if (rootTag == null) {
                return;
            }
            clear();
            NBTTagList researchList = rootTag.getTagList("research", 10);
            for (int i = 0; i < researchList.tagCount(); ++i) {
                NBTTagCompound tag = researchList.getCompoundTagAt(i);
                String know = tag.getString("key");
                if (know != null && !isResearchKnown(know)) {
                    research.add(know);
                    int stage = tag.getInteger("stage");
                    if (stage > 0) {
                        stages.put(know, stage);
                    }
                    String fs = tag.getString("flags");
                    if (fs.length() > 0) {
                        String[] split;
                        String[] ss = split = fs.split(",");
                        for (String s : split) {
                            EnumResearchFlag flag = null;
                            try {
                                flag = EnumResearchFlag.valueOf(s);
                            }
                            catch (Exception ex) {}
                            if (flag != null) {
                                setResearchFlag(know, flag);
                            }
                        }
                    }
                }
            }
            NBTTagList knowledgeList = rootTag.getTagList("knowledge", 10);
            for (int j = 0; j < knowledgeList.tagCount(); ++j) {
                NBTTagCompound tag2 = knowledgeList.getCompoundTagAt(j);
                String key = tag2.getString("key");
                int amount = tag2.getInteger("amount");
                knowledge.put(key, amount);
            }
            addAutoUnlockResearch();
        }
        
        private void addAutoUnlockResearch() {
            for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
                for (ResearchEntry ri : cat.research.values()) {
                    if (ri.hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                        addResearch(ri.getKey());
                    }
                }
            }
        }
    }
    
    public static class Provider implements ICapabilitySerializable<NBTTagCompound>
    {
        public static ResourceLocation NAME;
        private DefaultImpl knowledge;
        
        public Provider() {
            knowledge = new DefaultImpl();
        }
        
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == ThaumcraftCapabilities.KNOWLEDGE;
        }
        
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == ThaumcraftCapabilities.KNOWLEDGE) {
                return ThaumcraftCapabilities.KNOWLEDGE.cast(knowledge);
            }
            return null;
        }
        
        public NBTTagCompound serializeNBT() {
            return knowledge.serializeNBT();
        }
        
        public void deserializeNBT(NBTTagCompound nbt) {
            knowledge.deserializeNBT(nbt);
        }
        
        static {
            NAME = new ResourceLocation("thaumcraft", "knowledge");
        }
    }
}
