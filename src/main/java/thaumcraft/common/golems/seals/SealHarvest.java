// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.seals;

import thaumcraft.api.golems.EnumGolemTrait;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraft.block.Block;
import net.minecraft.util.EnumActionResult;
import net.minecraft.init.Items;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockDirectional;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.golems.GolemInteractionHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.EnumPacketDirection;
import net.minecraftforge.common.util.FakePlayerFactory;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.world.WorldServer;
import thaumcraft.api.golems.IGolemAPI;
import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.lib.utils.CropUtils;
import thaumcraft.common.golems.tasks.TaskHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.world.World;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import java.util.HashMap;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.seals.ISeal;

public class SealHarvest implements ISeal, ISealGui, ISealConfigArea, ISealConfigToggles
{
    int delay;
    int count;
    HashMap<Long, ReplantInfo> replantTasks;
    ResourceLocation icon;
    protected SealToggle[] props;
    
    public SealHarvest() {
        this.delay = new Random(System.nanoTime()).nextInt(33);
        this.count = 0;
        this.replantTasks = new HashMap<Long, ReplantInfo>();
        this.icon = new ResourceLocation("thaumcraft", "items/seals/seal_harvest");
        this.props = new SealToggle[] { new SealToggle(true, "prep", "golem.prop.replant"), new SealToggle(false, "ppro", "golem.prop.provision") };
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:harvest";
    }
    
    @Override
    public void tickSeal(final World world, final ISealEntity seal) {
        if (this.delay % 100 == 0) {
            final AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
            final Iterator<Long> rt = this.replantTasks.keySet().iterator();
            while (rt.hasNext()) {
                final BlockPos pp = BlockPos.fromLong(rt.next());
                if (!area.contains(new Vec3d(pp.getX() + 0.5, pp.getY() + 0.5, pp.getZ() + 0.5))) {
                    if (this.replantTasks.get(rt) != null) {
                        final Task tt = TaskHandler.getTask(world.provider.getDimension(), this.replantTasks.get(rt).taskid);
                        if (tt != null) {
                            tt.setSuspended(true);
                        }
                    }
                    rt.remove();
                }
            }
        }
        if (this.delay++ % 5 != 0) {
            return;
        }
        final BlockPos p = GolemHelper.getPosInArea(seal, this.count++);
        if (CropUtils.isGrownCrop(world, p)) {
            final Task task = new Task(seal.getSealPos(), p);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.provider.getDimension(), task);
        }
        else if (this.getToggles()[0].value && this.replantTasks.containsKey(p.toLong()) && world.isAirBlock(p)) {
            final Task t = TaskHandler.getTask(world.provider.getDimension(), this.replantTasks.get(p.toLong()).taskid);
            if (t == null) {
                final Task tt2 = new Task(seal.getSealPos(), this.replantTasks.get(p.toLong()).pos);
                tt2.setPriority(seal.getPriority());
                TaskHandler.addTask(world.provider.getDimension(), tt2);
                this.replantTasks.get(p.toLong()).taskid = tt2.getId();
            }
        }
    }
    
    @Override
    public boolean onTaskCompletion(final World world, final IGolemAPI golem, final Task task) {
        if (CropUtils.isGrownCrop(world, task.getPos())) {
            final FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile(null, "FakeThaumcraftGolem"));
            fp.connection = new FakeNetHandlerPlayServer(fp.mcServer, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
            fp.setPosition(golem.getGolemEntity().posX, golem.getGolemEntity().posY, golem.getGolemEntity().posZ);
            final EnumFacing face = EnumFacing.getDirectionFromEntityLiving(task.getPos(), golem.getGolemEntity());
            final IBlockState bs = world.getBlockState(task.getPos());
            if (CropUtils.clickableCrops.contains(bs.getBlock().getUnlocalizedName() + bs.getBlock().getMetaFromState(bs))) {
                bs.getBlock().onBlockActivated(world, task.getPos(), bs, fp, EnumHand.MAIN_HAND, face, 0.0f, 0.0f, 0.0f);
                golem.addRankXp(1);
                golem.swingArm();
            }
            else {
                GolemInteractionHelper.golemClick(world, golem, task.getPos(), task.getSealPos().face, ItemStack.EMPTY, false, true);
                if (CropUtils.isGrownCrop(world, task.getPos())) {
                    BlockUtils.harvestBlock(world, fp, task.getPos(), true, false, 0, true);
                    golem.addRankXp(1);
                    golem.swingArm();
                    if (this.getToggles()[0].value) {
                        final ItemStack seed = ThaumcraftApi.getSeed(bs.getBlock());
                        if (seed != null && !seed.isEmpty()) {
                            final IBlockState bb = world.getBlockState(task.getPos().down());
                            EnumFacing rf = null;
                            if (seed.getItem() instanceof IPlantable && bb.getBlock().canSustainPlant(bb, world, task.getPos().down(), EnumFacing.UP, (IPlantable)seed.getItem())) {
                                rf = EnumFacing.DOWN;
                            }
                            else if (!(seed.getItem() instanceof IPlantable) && bs.getBlock() instanceof BlockDirectional) {
                                rf = (EnumFacing)bs.getValue((IProperty)BlockDirectional.FACING);
                            }
                            if (rf != null) {
                                final Task tt = new Task(task.getSealPos(), task.getPos());
                                tt.setPriority(task.getPriority());
                                tt.setLifespan((short)300);
                                this.replantTasks.put(tt.getPos().toLong(), new ReplantInfo(tt.getPos(), rf, tt.getId(), seed.copy(), bb.getBlock() instanceof BlockFarmland));
                                TaskHandler.addTask(world.provider.getDimension(), tt);
                            }
                        }
                    }
                }
            }
        }
        else if (this.replantTasks.containsKey(task.getPos().toLong()) && this.replantTasks.get(task.getPos().toLong()).taskid == task.getId() && world.isAirBlock(task.getPos()) && golem.isCarrying(this.replantTasks.get(task.getPos().toLong()).stack)) {
            final FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile(null, "FakeThaumcraftGolem"));
            fp.setPosition(golem.getGolemEntity().posX, golem.getGolemEntity().posY, golem.getGolemEntity().posZ);
            final IBlockState bb2 = world.getBlockState(task.getPos().down());
            final ReplantInfo ri = this.replantTasks.get(task.getPos().toLong());
            if ((bb2.getBlock() instanceof BlockDirt || bb2.getBlock() instanceof BlockGrass) && ri.farmland) {
                Items.DIAMOND_HOE.onItemUse(fp, world, task.getPos().down(), EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
            }
            final ItemStack seed = ri.stack.copy();
            seed.setCount(1);
            if (seed.getItem().onItemUse(fp, world, task.getPos().offset(ri.face), EnumHand.MAIN_HAND, ri.face.getOpposite(), 0.5f, 0.5f, 0.5f) == EnumActionResult.SUCCESS) {
                world.playBroadcastSound(2001, task.getPos(), Block.getStateId(world.getBlockState(task.getPos())));
                golem.dropItem(seed);
                golem.addRankXp(1);
                golem.swingArm();
            }
        }
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(final IGolemAPI golem, final Task task) {
        if (this.replantTasks.containsKey(task.getPos().toLong()) && this.replantTasks.get(task.getPos().toLong()).taskid == task.getId()) {
            final boolean carry = golem.isCarrying(this.replantTasks.get(task.getPos().toLong()).stack);
            if (!carry && this.getToggles()[1].value) {
                final ISealEntity se = SealHandler.getSealEntity(golem.getGolemWorld().provider.getDimension(), task.getSealPos());
                if (se != null) {
                    GolemHelper.requestProvisioning(golem.getGolemWorld(), se, this.replantTasks.get(task.getPos().toLong()).stack);
                }
            }
            return carry;
        }
        return true;
    }
    
    @Override
    public void onTaskSuspension(final World world, final Task task) {
    }
    
    @Override
    public void readCustomNBT(final NBTTagCompound nbt) {
        final NBTTagList nbttaglist = nbt.getTagList("replant", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            final long loc = nbttagcompound1.getLong("taskloc");
            final byte face = nbttagcompound1.getByte("taskface");
            final boolean farmland = nbttagcompound1.getBoolean("farmland");
            final ItemStack stack = new ItemStack(nbttagcompound1);
            this.replantTasks.put(loc, new ReplantInfo(BlockPos.fromLong(loc), EnumFacing.VALUES[face], 0, stack, farmland));
        }
    }
    
    @Override
    public void writeCustomNBT(final NBTTagCompound nbt) {
        if (this.getToggles()[0].value) {
            final NBTTagList nbttaglist = new NBTTagList();
            for (final Long key : this.replantTasks.keySet()) {
                final ReplantInfo info = this.replantTasks.get(key);
                final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setLong("taskloc", info.pos.toLong());
                nbttagcompound1.setByte("taskface", (byte)info.face.ordinal());
                nbttagcompound1.setBoolean("farmland", info.farmland);
                info.stack.writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
            nbt.setTag("replant", nbttaglist);
        }
    }
    
    @Override
    public boolean canPlaceAt(final World world, final BlockPos pos, final EnumFacing side) {
        return !world.isAirBlock(pos);
    }
    
    @Override
    public ResourceLocation getSealIcon() {
        return this.icon;
    }
    
    @Override
    public void onRemoval(final World world, final BlockPos pos, final EnumFacing side) {
    }
    
    @Override
    public Object returnContainer(final World world, final EntityPlayer player, final BlockPos pos, final EnumFacing side, final ISealEntity seal) {
        return new SealBaseContainer(player.inventory, world, seal);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Object returnGui(final World world, final EntityPlayer player, final BlockPos pos, final EnumFacing side, final ISealEntity seal) {
        return new SealBaseGUI(player.inventory, world, seal);
    }
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 2, 3, 0, 4 };
    }
    
    @Override
    public SealToggle[] getToggles() {
        return this.props;
    }
    
    @Override
    public void setToggle(final int indx, final boolean value) {
        this.props[indx].setValue(value);
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.DEFT, EnumGolemTrait.SMART };
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
    
    @Override
    public void onTaskStarted(final World world, final IGolemAPI golem, final Task task) {
    }
    
    private class ReplantInfo
    {
        EnumFacing face;
        BlockPos pos;
        int taskid;
        ItemStack stack;
        boolean farmland;
        
        public ReplantInfo(final BlockPos pos, final EnumFacing face, final int taskid, final ItemStack stack, final boolean farmland) {
            this.pos = pos;
            this.face = face;
            this.taskid = taskid;
            this.stack = stack;
            this.farmland = farmland;
        }
    }
}
