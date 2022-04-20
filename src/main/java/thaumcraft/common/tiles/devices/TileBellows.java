// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ITickable;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileBellows extends TileThaumcraft implements ITickable
{
    public float inflation;
    boolean direction;
    boolean firstrun;
    public int delay;
    
    public TileBellows() {
        this.inflation = 1.0f;
        this.direction = false;
        this.firstrun = true;
        this.delay = 0;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos().getX() - 0.3, this.getPos().getY() - 0.3, this.getPos().getZ() - 0.3, this.getPos().getX() + 1.3, this.getPos().getY() + 1.3, this.getPos().getZ() + 1.3);
    }
    
    public void update() {
        if (this.world.isRemote) {
            if (BlockStateUtils.isEnabled(this.getBlockMetadata())) {
                if (this.firstrun) {
                    this.inflation = 0.35f + this.world.rand.nextFloat() * 0.55f;
                }
                this.firstrun = false;
                if (this.inflation > 0.35f && !this.direction) {
                    this.inflation -= 0.075f;
                }
                if (this.inflation <= 0.35f && !this.direction) {
                    this.direction = true;
                }
                if (this.inflation < 1.0f && this.direction) {
                    this.inflation += 0.025f;
                }
                if (this.inflation >= 1.0f && this.direction) {
                    this.direction = false;
                    this.world.playSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 0.01f, 0.5f + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2f, false);
                }
            }
        }
        else if (BlockStateUtils.isEnabled(this.getBlockMetadata())) {
            ++this.delay;
            if (this.delay >= 2) {
                this.delay = 0;
                final TileEntity tile = this.world.getTileEntity(this.pos.offset(BlockStateUtils.getFacing(this.getBlockMetadata())));
                if (tile != null && tile instanceof TileEntityFurnace) {
                    final TileEntityFurnace tf = (TileEntityFurnace)tile;
                    final int ct = this.getCooktime(tf);
                    if (ct > 0 && ct < 199) {
                        this.setCooktime(tf, ct + 1);
                    }
                }
            }
        }
    }
    
    public void setCooktime(final TileEntityFurnace ent, final int hit) {
        ent.cookTime = hit;
    }
    
    public int getCooktime(final TileEntityFurnace ent) {
        return ent.cookTime;
    }
    
    public static int getBellows(final World world, final BlockPos pos, final EnumFacing[] directions) {
        int bellows = 0;
        for (final EnumFacing dir : directions) {
            final TileEntity tile = world.getTileEntity(pos.offset(dir));
            try {
                if (tile != null && tile instanceof TileBellows && BlockStateUtils.getFacing(tile.getBlockMetadata()) == dir.getOpposite() && BlockStateUtils.isEnabled(tile.getBlockMetadata())) {
                    ++bellows;
                }
            }
            catch (final Exception ex) {}
        }
        return bellows;
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
}
