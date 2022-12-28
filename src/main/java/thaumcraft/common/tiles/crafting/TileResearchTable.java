package thaumcraft.common.tiles.crafting;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftManager;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TileResearchTable extends TileThaumcraftInventory
{
    public ResearchTableData data;
    
    public TileResearchTable() {
        super(2);
        data = null;
        syncedSlots = new int[] { 0, 1 };
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        if (nbttagcompound.hasKey("note")) {
            (data = new ResearchTableData(this)).deserialize(nbttagcompound.getCompoundTag("note"));
        }
        else {
            data = null;
        }
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        if (data != null) {
            nbttagcompound.setTag("note", data.serialize());
        }
        else {
            nbttagcompound.removeTag("note");
        }
        return super.writeSyncNBT(nbttagcompound);
    }
    
    protected void setWorldCreate(World worldIn) {
        super.setWorldCreate(worldIn);
        if (!hasWorld()) {
            setWorld(worldIn);
        }
    }
    
    public void startNewTheory(EntityPlayer player, Set<String> mutators) {
        (data = new ResearchTableData(player, this)).initialize(player, mutators);
        syncTile(false);
        markDirty();
    }
    
    public void finishTheory(EntityPlayer player) {
        Comparator<Map.Entry<String, Integer>> valueComparator = (e1, e2) -> e2.getValue().compareTo(e1.getValue());
        Map<String, Integer> sortedMap = data.categoryTotals.entrySet().stream().sorted(valueComparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        int i = 0;
        for (String cat : sortedMap.keySet()) {
            int tot = Math.round(sortedMap.get(cat) / 100.0f * IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression());
            if (i > data.penaltyStart) {
                tot = (int)Math.max(1.0, tot * 0.666666667);
            }
            ResearchCategory rc = ResearchCategories.getResearchCategory(cat);
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, rc, tot);
            ++i;
        }
        data = null;
    }
    
    public Set<String> checkSurroundingAids() {
        HashMap<String, ITheorycraftAid> mutators = new HashMap<String, ITheorycraftAid>();
        for (int y = -1; y <= 1; ++y) {
            for (int x = -4; x <= 4; ++x) {
                for (int z = -4; z <= 4; ++z) {
                    for (String muk : TheorycraftManager.aids.keySet()) {
                        ITheorycraftAid mu = TheorycraftManager.aids.get(muk);
                        IBlockState state = world.getBlockState(getPos().add(x, y, z));
                        if (mu.getAidObject() instanceof Block) {
                            if (state.getBlock() != mu.getAidObject()) {
                                continue;
                            }
                            mutators.put(muk, mu);
                        }
                        else {
                            if (!(mu.getAidObject() instanceof ItemStack)) {
                                continue;
                            }
                            ItemStack is = state.getBlock().getItem(getWorld(), getPos().add(x, y, z), state);
                            if (is == null || is.isEmpty() || !is.isItemEqualIgnoreDurability((ItemStack)mu.getAidObject())) {
                                continue;
                            }
                            mutators.put(muk, mu);
                        }
                    }
                }
            }
        }
        List<Entity> l = EntityUtils.getEntitiesInRange(getWorld(), getPos(), null, Entity.class, 5.0);
        if (l != null && !l.isEmpty()) {
            for (Entity e : l) {
                for (String muk : TheorycraftManager.aids.keySet()) {
                    ITheorycraftAid mu = TheorycraftManager.aids.get(muk);
                    if (mu.getAidObject() instanceof Class && e.getClass().isAssignableFrom((Class<?>)mu.getAidObject())) {
                        mutators.put(muk, mu);
                    }
                }
            }
        }
        return mutators.keySet();
    }
    
    public boolean consumeInkFromTable() {
        if (getStackInSlot(0).getItem() instanceof IScribeTools && getStackInSlot(0).getItemDamage() < getStackInSlot(0).getMaxDamage()) {
            getStackInSlot(0).setItemDamage(getStackInSlot(0).getItemDamage() + 1);
            syncTile(false);
            markDirty();
            return true;
        }
        return false;
    }
    
    public boolean consumepaperFromTable() {
        if (getStackInSlot(1).getItem() == Items.PAPER && getStackInSlot(1).getCount() > 0) {
            decrStackSize(1, 1);
            syncTile(false);
            markDirty();
            return true;
        }
        return false;
    }
    
    @Override
    public String getName() {
        return "Research Table";
    }
    
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        switch (i) {
            case 0: {
                if (itemstack.getItem() instanceof IScribeTools) {
                    return true;
                }
                break;
            }
            case 1: {
                if (itemstack.getItem() == Items.PAPER && itemstack.getItemDamage() == 0) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (world != null && world.isRemote) {
            syncTile(false);
        }
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 1) {
            if (world.isRemote) {
                world.playSound(getPos().getX(), getPos().getY(), getPos().getZ(), SoundsTC.learn, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
}
