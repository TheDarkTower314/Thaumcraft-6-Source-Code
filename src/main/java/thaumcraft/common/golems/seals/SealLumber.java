package thaumcraft.common.golems.seals;
import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;


public class SealLumber implements ISeal, ISealGui, ISealConfigArea
{
    int delay;
    HashMap<Integer, Long> cache;
    ResourceLocation icon;
    
    public SealLumber() {
        delay = new Random(System.nanoTime()).nextInt(33);
        cache = new HashMap<Integer, Long>();
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_lumber");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:lumber";
    }
    
    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (delay % 100 == 0) {
            Iterator<Integer> it = cache.keySet().iterator();
            while (it.hasNext()) {
                Task t = TaskHandler.getTask(world.provider.getDimension(), it.next());
                if (t == null) {
                    it.remove();
                }
            }
        }
        ++delay;
        BlockPos p = GolemHelper.getPosInArea(seal, delay);
        if (!cache.containsValue(p.toLong()) && Utils.isWoodLog(world, p)) {
            Task task = new Task(seal.getSealPos(), p);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.provider.getDimension(), task);
            cache.put(task.getId(), p.toLong());
        }
    }
    
    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        if (cache.containsKey(task.getId()) && Utils.isWoodLog(world, task.getPos())) {
            FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile(null, "FakeThaumcraftGolem"));
            fp.setPosition(golem.getGolemEntity().posX, golem.getGolemEntity().posY, golem.getGolemEntity().posZ);
            IBlockState bs = world.getBlockState(task.getPos());
            golem.swingArm();
            if (BlockUtils.breakFurthestBlock(world, task.getPos(), bs, fp)) {
                task.setLifespan((short)Math.max(task.getLifespan(), 10L));
                golem.addRankXp(1);
                return false;
            }
            cache.remove(task.getId());
        }
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        if (cache.containsKey(task.getId()) && Utils.isWoodLog(golem.getGolemWorld(), task.getPos())) {
            return true;
        }
        task.setSuspended(true);
        return false;
    }
    
    @Override
    public void onTaskSuspension(World world, Task task) {
        cache.remove(task.getId());
    }
    
    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
    }
    
    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
    }
    
    @Override
    public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
        return !world.isAirBlock(pos);
    }
    
    @Override
    public ResourceLocation getSealIcon() {
        return icon;
    }
    
    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing side) {
    }
    
    @Override
    public Object returnContainer(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
        return new SealBaseContainer(player.inventory, world, seal);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Object returnGui(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
        return new SealBaseGUI(player.inventory, world, seal);
    }
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 2, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.BREAKER, EnumGolemTrait.SMART };
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
    
    @Override
    public void onTaskStarted(World world, IGolemAPI golem, Task task) {
    }
}
