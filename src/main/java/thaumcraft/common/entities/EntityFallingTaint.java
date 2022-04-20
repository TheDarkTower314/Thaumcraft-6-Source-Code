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
        return this.fallTile;
    }
    
    public EntityFallingTaint(final World par1World) {
        super(par1World);
        this.fallTime = 0;
        this.fallHurtMax = 40;
        this.fallHurtAmount = 2.0f;
    }
    
    public EntityFallingTaint(final World par1World, final double par2, final double par4, final double par6, final IBlockState par8, final BlockPos o) {
        super(par1World);
        this.fallTime = 0;
        this.fallHurtMax = 40;
        this.fallHurtAmount = 2.0f;
        this.fallTile = par8;
        this.preventEntitySpawning = true;
        this.setSize(0.98f, 0.98f);
        this.setPosition(par2, par4, par6);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevPosX = par2;
        this.prevPosY = par4;
        this.prevPosZ = par6;
        this.oldPos = o;
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    protected void entityInit() {
    }
    
    public void writeSpawnData(final ByteBuf data) {
        data.writeInt(Block.getIdFromBlock(this.fallTile.getBlock()));
        data.writeByte(this.fallTile.getBlock().getMetaFromState(this.fallTile));
    }
    
    public void readSpawnData(final ByteBuf data) {
        try {
            this.fallTile = Block.getBlockById(data.readInt()).getStateFromMeta(data.readByte());
        }
        catch (final Exception ex) {}
    }
    
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }
    
    public void onUpdate() {
        if (this.fallTile == null || this.fallTile == Blocks.AIR) {
            this.setDead();
        }
        else {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            ++this.fallTime;
            this.motionY -= 0.03999999910593033;
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9800000190734863;
            this.motionY *= 0.9800000190734863;
            this.motionZ *= 0.9800000190734863;
            final BlockPos bp = new BlockPos(this);
            if (!this.world.isRemote) {
                if (this.fallTime == 1) {
                    if (this.world.getBlockState(this.oldPos) != this.fallTile) {
                        this.setDead();
                        return;
                    }
                    this.world.setBlockToAir(this.oldPos);
                }
                if (this.onGround || this.world.getBlockState(bp.down()) == BlocksTC.fluxGoo) {
                    this.motionX *= 0.699999988079071;
                    this.motionZ *= 0.699999988079071;
                    this.motionY *= -0.5;
                    if (this.world.getBlockState(bp).getBlock() != Blocks.PISTON && this.world.getBlockState(bp).getBlock() != Blocks.PISTON_EXTENSION && this.world.getBlockState(bp).getBlock() != Blocks.PISTON_HEAD) {
                        this.playSound(SoundsTC.gore, 0.5f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) * 0.8f);
                        this.setDead();
                        if (this.canPlace(bp) && !BlockTaint.canFallBelow(this.world, bp.down()) && this.world.setBlockState(bp, this.fallTile)) {}
                    }
                }
                else if ((this.fallTime > 100 && !this.world.isRemote && (bp.getY() < 1 || bp.getY() > 256)) || this.fallTime > 600) {
                    this.setDead();
                }
            }
            else if (this.onGround || this.fallTime == 1) {
                for (int j = 0; j < 10; ++j) {
                    FXDispatcher.INSTANCE.taintLandFX(this);
                }
            }
        }
    }
    
    private boolean canPlace(final BlockPos pos) {
        return this.world.getBlockState(pos).getBlock() == BlocksTC.taintFibre || this.world.getBlockState(pos).getBlock() == BlocksTC.fluxGoo || this.world.mayPlace(this.fallTile.getBlock(), pos, true, EnumFacing.UP, null);
    }
    
    public void fall(final float distance, final float damageMultiplier) {
    }
    
    protected void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        final Block block = (this.fallTile != null) ? this.fallTile.getBlock() : Blocks.AIR;
        final ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(block);
        par1NBTTagCompound.setString("Block", (resourcelocation == null) ? "" : resourcelocation.toString());
        par1NBTTagCompound.setByte("Data", (byte)block.getMetaFromState(this.fallTile));
        par1NBTTagCompound.setByte("Time", (byte)this.fallTime);
        par1NBTTagCompound.setFloat("FallHurtAmount", this.fallHurtAmount);
        par1NBTTagCompound.setInteger("FallHurtMax", this.fallHurtMax);
        par1NBTTagCompound.setLong("Old", this.oldPos.toLong());
    }
    
    protected void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        final int i = par1NBTTagCompound.getByte("Data") & 0xFF;
        if (par1NBTTagCompound.hasKey("Block", 8)) {
            this.fallTile = Block.getBlockFromName(par1NBTTagCompound.getString("Block")).getStateFromMeta(i);
        }
        else if (par1NBTTagCompound.hasKey("TileID", 99)) {
            this.fallTile = Block.getBlockById(par1NBTTagCompound.getInteger("TileID")).getStateFromMeta(i);
        }
        else {
            this.fallTile = Block.getBlockById(par1NBTTagCompound.getByte("Tile") & 0xFF).getStateFromMeta(i);
        }
        this.fallTime = (par1NBTTagCompound.getByte("Time") & 0xFF);
        this.oldPos = BlockPos.fromLong(par1NBTTagCompound.getLong("Old"));
        if (par1NBTTagCompound.hasKey("HurtEntities")) {
            this.fallHurtAmount = par1NBTTagCompound.getFloat("FallHurtAmount");
            this.fallHurtMax = par1NBTTagCompound.getInteger("FallHurtMax");
        }
        if (this.fallTile == null) {
            this.fallTile = Blocks.SAND.getDefaultState();
        }
    }
    
    public void addEntityCrashInfo(final CrashReportCategory par1CrashReportCategory) {
        super.addEntityCrashInfo(par1CrashReportCategory);
        par1CrashReportCategory.addCrashSection("Immitating block ID", Block.getIdFromBlock(this.fallTile.getBlock()));
        par1CrashReportCategory.addCrashSection("Immitating block data", this.fallTile.getBlock().getMetaFromState(this.fallTile));
    }
    
    public SoundCategory getSoundCategory() {
        return SoundCategory.BLOCKS;
    }
    
    @SideOnly(Side.CLIENT)
    public World getWorld() {
        return this.world;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }
}
