// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.crafting;

import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.init.Items;
import thaumcraft.api.items.IScribeTools;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import thaumcraft.api.research.theorycraft.TheorycraftManager;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import java.util.HashMap;
import thaumcraft.api.research.ResearchCategory;
import java.util.Iterator;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import java.util.stream.Collector;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileResearchTable extends TileThaumcraftInventory
{
    public ResearchTableData data;
    
    public TileResearchTable() {
        super(2);
        this.data = null;
        this.syncedSlots = new int[] { 0, 1 };
    }
    
    @Override
    public void readSyncNBT(final NBTTagCompound nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        if (nbttagcompound.hasKey("note")) {
            (this.data = new ResearchTableData(this)).deserialize(nbttagcompound.getCompoundTag("note"));
        }
        else {
            this.data = null;
        }
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(final NBTTagCompound nbttagcompound) {
        if (this.data != null) {
            nbttagcompound.setTag("note", this.data.serialize());
        }
        else {
            nbttagcompound.removeTag("note");
        }
        return super.writeSyncNBT(nbttagcompound);
    }
    
    protected void setWorldCreate(final World worldIn) {
        super.setWorldCreate(worldIn);
        if (!this.hasWorld()) {
            this.setWorld(worldIn);
        }
    }
    
    public void startNewTheory(final EntityPlayer player, final Set<String> mutators) {
        (this.data = new ResearchTableData(player, this)).initialize(player, mutators);
        this.syncTile(false);
        this.markDirty();
    }
    
    public void finishTheory(final EntityPlayer player) {
        final Comparator<Map.Entry<String, Integer>> valueComparator = (e1, e2) -> e2.getValue().compareTo(e1.getValue());
        final Map<String, Integer> sortedMap = this.data.categoryTotals.entrySet().stream().sorted(valueComparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        int i = 0;
        for (final String cat : sortedMap.keySet()) {
            int tot = Math.round(sortedMap.get(cat) / 100.0f * IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression());
            if (i > this.data.penaltyStart) {
                tot = (int)Math.max(1.0, tot * 0.666666667);
            }
            final ResearchCategory rc = ResearchCategories.getResearchCategory(cat);
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, rc, tot);
            ++i;
        }
        this.data = null;
    }
    
    public Set<String> checkSurroundingAids() {
        final HashMap<String, ITheorycraftAid> mutators = new HashMap<String, ITheorycraftAid>();
        for (int y = -1; y <= 1; ++y) {
            for (int x = -4; x <= 4; ++x) {
                for (int z = -4; z <= 4; ++z) {
                    for (final String muk : TheorycraftManager.aids.keySet()) {
                        final ITheorycraftAid mu = TheorycraftManager.aids.get(muk);
                        final IBlockState state = this.world.getBlockState(this.getPos().add(x, y, z));
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
                            final ItemStack is = state.getBlock().getItem(this.getWorld(), this.getPos().add(x, y, z), state);
                            if (is == null || is.isEmpty() || !is.isItemEqualIgnoreDurability((ItemStack)mu.getAidObject())) {
                                continue;
                            }
                            mutators.put(muk, mu);
                        }
                    }
                }
            }
        }
        final List<Entity> l = EntityUtils.getEntitiesInRange(this.getWorld(), this.getPos(), null, Entity.class, 5.0);
        if (l != null && !l.isEmpty()) {
            for (final Entity e : l) {
                for (final String muk : TheorycraftManager.aids.keySet()) {
                    final ITheorycraftAid mu = TheorycraftManager.aids.get(muk);
                    if (mu.getAidObject() instanceof Class && e.getClass().isAssignableFrom((Class<?>)mu.getAidObject())) {
                        mutators.put(muk, mu);
                    }
                }
            }
        }
        return mutators.keySet();
    }
    
    public boolean consumeInkFromTable() {
        if (this.getStackInSlot(0).getItem() instanceof IScribeTools && this.getStackInSlot(0).getItemDamage() < this.getStackInSlot(0).getMaxDamage()) {
            this.getStackInSlot(0).setItemDamage(this.getStackInSlot(0).getItemDamage() + 1);
            this.syncTile(false);
            this.markDirty();
            return true;
        }
        return false;
    }
    
    public boolean consumepaperFromTable() {
        if (this.getStackInSlot(1).getItem() == Items.PAPER && this.getStackInSlot(1).getCount() > 0) {
            this.decrStackSize(1, 1);
            this.syncTile(false);
            this.markDirty();
            return true;
        }
        return false;
    }
    
    @Override
    public String getName() {
        return "Research Table";
    }
    
    @Override
    public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
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
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (this.world != null && this.world.isRemote) {
            this.syncTile(false);
        }
    }
    
    public boolean receiveClientEvent(final int i, final int j) {
        if (i == 1) {
            if (this.world.isRemote) {
                this.world.playSound(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), SoundsTC.learn, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
}