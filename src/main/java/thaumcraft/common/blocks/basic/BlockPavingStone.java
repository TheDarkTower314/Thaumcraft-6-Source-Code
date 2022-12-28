package thaumcraft.common.blocks.basic;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.misc.TileBarrierStone;


public class BlockPavingStone extends BlockTC
{
    public BlockPavingStone(String name) {
        super(Material.ROCK, name);
        setHardness(2.5f);
        setSoundType(SoundType.STONE);
        setTickRandomly(true);
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
    }
    
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }
    
    public boolean hasTileEntity(IBlockState state) {
        return state.getBlock() == BlocksTC.pavingStoneBarrier;
    }
    
    public TileEntity createTileEntity(World world, IBlockState state) {
        return (state.getBlock() == BlocksTC.pavingStoneBarrier) ? new TileBarrierStone() : null;
    }
    
    public void onEntityWalk(World worldIn, BlockPos pos, Entity e) {
        IBlockState state = worldIn.getBlockState(pos);
        if (!worldIn.isRemote && state.getBlock() == BlocksTC.pavingStoneTravel && e instanceof EntityLivingBase) {
            ((EntityLivingBase)e).addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 1, false, false));
            ((EntityLivingBase)e).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 40, 0, false, false));
        }
        super.onEntityWalk(worldIn, pos, e);
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        if (state.getBlock() == BlocksTC.pavingStoneBarrier) {
            if (world.isBlockIndirectlyGettingPowered(pos) > 0) {
                for (int a = 0; a < 4; ++a) {
                    FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() + 0.7f, pos.getZ(), 0.2f + random.nextFloat() * 0.4f, random.nextFloat() * 0.3f, 0.8f + random.nextFloat() * 0.2f, 20, -0.02f);
                }
            }
            else if ((world.getBlockState(pos.up(1)) == BlocksTC.barrier.getDefaultState() && world.getBlockState(pos.up(1)).getBlock().isPassable(world, pos.up(1))) || (world.getBlockState(pos.up(2)) == BlocksTC.barrier.getDefaultState() && world.getBlockState(pos.up(2)).getBlock().isPassable(world, pos.up(2)))) {
                for (int a = 0; a < 6; ++a) {
                    FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() + 0.7f, pos.getZ(), 0.9f + random.nextFloat() * 0.1f, random.nextFloat() * 0.3f, random.nextFloat() * 0.3f, 24, -0.02f);
                }
            }
            else {
                List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(1.0, 1.0, 1.0));
                if (!list.isEmpty()) {
                    for (Entity entity : list) {
                        if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {
                            FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() + 0.6f + random.nextFloat() * Math.max(0.8f, entity.getEyeHeight()), pos.getZ(), 0.6f + random.nextFloat() * 0.4f, 0.0f, 0.3f + random.nextFloat() * 0.7f, 20, 0.0f);
                            break;
                        }
                    }
                }
            }
        }
    }
}
