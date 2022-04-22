// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.SoundCategory;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.world.taint.BlockTaint;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.Entity;

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
    
    public EntityFallingTaint(final World par1World) {
        super(par1World);
        fallTime = 0;
        fallHurtMax = 40;
        fallHurtAmount = 2.0f;
    }
    
    public EntityFallingTaint(final World par1World, final double par2, final double par4, final double par6, final IBlockState par8, final BlockPos o) {
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
    
    public void writeSpawnData(final ByteBuf data) {
        data.writeInt(Block.getIdFromBlock(fallTile.getBlock()));
        data.writeByte(fallTile.getBlock().getMetaFromState(fallTile));
    }
    
    public void readSpawnData(final ByteBuf data) {
        try {
            fallTile = Block.getBlockById(data.readInt()).getStateFromMeta(data.readByte());
        }
        catch (final Exception ex) {}
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
            final BlockPos bp = new BlockPos(this);
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
    
    private boolean canPlace(final BlockPos pos) {
        return world.getBlockState(pos).getBlock() == BlocksTC.taintFibre || world.getBlockState(pos).getBlock() == BlocksTC.fluxGoo || world.mayPlace(fallTile.getBlock(), pos, true, EnumFacing.UP, null);
    }
    
    public void fall(final float distance, final float damageMultiplier) {
    }
    
    protected void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        final Block block = (fallTile != null) ? fallTile.getBlock() : Blocks.AIR;
        final ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(block);
        par1NBTTagCompound.setString("Block", (resourcelocation == null) ? "" : resourcelocation.toString());
        par1NBTTagCompound.setByte("Data", (byte)block.getMetaFromState(fallTile));
        par1NBTTagCompound.setByte("Time", (byte) fallTime);
        par1NBTTagCompound.setFloat("FallHurtAmount", fallHurtAmount);
        par1NBTTagCompound.setInteger("FallHurtMax", fallHurtMax);
        par1NBTTagCompound.setLong("Old", oldPos.toLong());
    }
    
    protected void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        final int i = par1NBTTagCompound.getByte("Data") & 0xFF;
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
    
    public void addEntityCrashInfo(final CrashReportCategory par1CrashReportCategory) {
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
