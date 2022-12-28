package thaumcraft.common.entities;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.world.taint.BlockTaint;
import thaumcraft.common.lib.SoundsTC;


public class EntityFallingTaint extends Entity implements IEntityAdditionalSpawnData
{
    public IBlockState fallTile;
    BlockPos oldPos;
    public int fallTime;
    private int fallHurtMax;
    private float fallHurtAmount;
    
    public IBlockState getBlock() {
        return fallTile;
    }
    
    public EntityFallingTaint(World par1World) {
        super(par1World);
        fallTime = 0;
        fallHurtMax = 40;
        fallHurtAmount = 2.0f;
    }
    
    public EntityFallingTaint(World par1World, double par2, double par4, double par6, IBlockState par8, BlockPos o) {
        super(par1World);
        fallTime = 0;
        fallHurtMax = 40;
        fallHurtAmount = 2.0f;
        fallTile = par8;
        preventEntitySpawning = true;
        setSize(0.98f, 0.98f);
        setPosition(par2, par4, par6);
        motionX = 0.0;
        motionY = 0.0;
        motionZ = 0.0;
        prevPosX = par2;
        prevPosY = par4;
        prevPosZ = par6;
        oldPos = o;
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    protected void entityInit() {
    }
    
    public void writeSpawnData(ByteBuf data) {
        data.writeInt(Block.getIdFromBlock(fallTile.getBlock()));
        data.writeByte(fallTile.getBlock().getMetaFromState(fallTile));
    }
    
    public void readSpawnData(ByteBuf data) {
        try {
            fallTile = Block.getBlockById(data.readInt()).getStateFromMeta(data.readByte());
        }
        catch (Exception ex) {}
    }
    
    public boolean canBeCollidedWith() {
        return !isDead;
    }
    
    public void onUpdate() {
        if (fallTile == null || fallTile == Blocks.AIR) {
            setDead();
        }
        else {
            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;
            ++fallTime;
            motionY -= 0.03999999910593033;
            move(MoverType.SELF, motionX, motionY, motionZ);
            motionX *= 0.9800000190734863;
            motionY *= 0.9800000190734863;
            motionZ *= 0.9800000190734863;
            BlockPos bp = new BlockPos(this);
            if (!world.isRemote) {
                if (fallTime == 1) {
                    if (world.getBlockState(oldPos) != fallTile) {
                        setDead();
                        return;
                    }
                    world.setBlockToAir(oldPos);
                }
                if (onGround || world.getBlockState(bp.down()) == BlocksTC.fluxGoo) {
                    motionX *= 0.699999988079071;
                    motionZ *= 0.699999988079071;
                    motionY *= -0.5;
                    if (world.getBlockState(bp).getBlock() != Blocks.PISTON && world.getBlockState(bp).getBlock() != Blocks.PISTON_EXTENSION && world.getBlockState(bp).getBlock() != Blocks.PISTON_HEAD) {
                        playSound(SoundsTC.gore, 0.5f, ((rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f) * 0.8f);
                        setDead();
                        if (canPlace(bp) && !BlockTaint.canFallBelow(world, bp.down()) && world.setBlockState(bp, fallTile)) {}
                    }
                }
                else if ((fallTime > 100 && !world.isRemote && (bp.getY() < 1 || bp.getY() > 256)) || fallTime > 600) {
                    setDead();
                }
            }
            else if (onGround || fallTime == 1) {
                for (int j = 0; j < 10; ++j) {
                    FXDispatcher.INSTANCE.taintLandFX(this);
                }
            }
        }
    }
    
    private boolean canPlace(BlockPos pos) {
        return world.getBlockState(pos).getBlock() == BlocksTC.taintFibre || world.getBlockState(pos).getBlock() == BlocksTC.fluxGoo || world.mayPlace(fallTile.getBlock(), pos, true, EnumFacing.UP, null);
    }
    
    public void fall(float distance, float damageMultiplier) {
    }
    
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        Block block = (fallTile != null) ? fallTile.getBlock() : Blocks.AIR;
        ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(block);
        par1NBTTagCompound.setString("Block", (resourcelocation == null) ? "" : resourcelocation.toString());
        par1NBTTagCompound.setByte("Data", (byte)block.getMetaFromState(fallTile));
        par1NBTTagCompound.setByte("Time", (byte) fallTime);
        par1NBTTagCompound.setFloat("FallHurtAmount", fallHurtAmount);
        par1NBTTagCompound.setInteger("FallHurtMax", fallHurtMax);
        par1NBTTagCompound.setLong("Old", oldPos.toLong());
    }
    
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        int i = par1NBTTagCompound.getByte("Data") & 0xFF;
        if (par1NBTTagCompound.hasKey("Block", 8)) {
            fallTile = Block.getBlockFromName(par1NBTTagCompound.getString("Block")).getStateFromMeta(i);
        }
        else if (par1NBTTagCompound.hasKey("TileID", 99)) {
            fallTile = Block.getBlockById(par1NBTTagCompound.getInteger("TileID")).getStateFromMeta(i);
        }
        else {
            fallTile = Block.getBlockById(par1NBTTagCompound.getByte("Tile") & 0xFF).getStateFromMeta(i);
        }
        fallTime = (par1NBTTagCompound.getByte("Time") & 0xFF);
        oldPos = BlockPos.fromLong(par1NBTTagCompound.getLong("Old"));
        if (par1NBTTagCompound.hasKey("HurtEntities")) {
            fallHurtAmount = par1NBTTagCompound.getFloat("FallHurtAmount");
            fallHurtMax = par1NBTTagCompound.getInteger("FallHurtMax");
        }
        if (fallTile == null) {
            fallTile = Blocks.SAND.getDefaultState();
        }
    }
    
    public void addEntityCrashInfo(CrashReportCategory par1CrashReportCategory) {
        super.addEntityCrashInfo(par1CrashReportCategory);
        par1CrashReportCategory.addCrashSection("Immitating block ID", Block.getIdFromBlock(fallTile.getBlock()));
        par1CrashReportCategory.addCrashSection("Immitating block data", fallTile.getBlock().getMetaFromState(fallTile));
    }
    
    public SoundCategory getSoundCategory() {
        return SoundCategory.BLOCKS;
    }
    
    @SideOnly(Side.CLIENT)
    public World getWorld() {
        return world;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }
}
