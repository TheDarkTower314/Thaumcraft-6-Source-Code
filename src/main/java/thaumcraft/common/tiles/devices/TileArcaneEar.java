// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.material.Material;
import java.util.Iterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.blocks.devices.BlockArcaneEarToggle;
import net.minecraft.block.properties.IProperty;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import java.util.WeakHashMap;
import net.minecraft.util.ITickable;
import net.minecraft.tileentity.TileEntity;

public class TileArcaneEar extends TileEntity implements ITickable
{
    public byte note;
    public byte tone;
    public int redstoneSignal;
    public static WeakHashMap<Integer, ArrayList<Integer[]>> noteBlockEvents;
    
    public TileArcaneEar() {
        this.note = 0;
        this.tone = 0;
        this.redstoneSignal = 0;
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setByte("note", this.note);
        par1NBTTagCompound.setByte("tone", this.tone);
        return par1NBTTagCompound;
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.note = par1NBTTagCompound.getByte("note");
        this.tone = par1NBTTagCompound.getByte("tone");
        if (this.note < 0) {
            this.note = 0;
        }
        if (this.note > 24) {
            this.note = 24;
        }
    }
    
    public void update() {
        if (!this.world.isRemote) {
            if (this.redstoneSignal > 0) {
                --this.redstoneSignal;
                if (this.redstoneSignal == 0) {
                    final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata()).getOpposite();
                    final TileEntity tileentity = this.world.getTileEntity(this.pos);
                    this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)false), 3);
                    if (tileentity != null) {
                        tileentity.validate();
                        this.world.setTileEntity(this.pos, tileentity);
                    }
                    this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
                    this.world.notifyNeighborsOfStateChange(this.pos.offset(facing), this.getBlockType(), true);
                    final IBlockState state = this.world.getBlockState(this.pos);
                    this.world.markAndNotifyBlock(this.pos, this.world.getChunkFromBlockCoords(this.pos), state, state, 3);
                }
            }
            final ArrayList<Integer[]> nbe = TileArcaneEar.noteBlockEvents.get(this.world.provider.getDimension());
            if (nbe != null) {
                for (final Integer[] dat : nbe) {
                    if (dat[3] == this.tone && dat[4] == this.note && this.getDistanceSq(dat[0] + 0.5, dat[1] + 0.5, dat[2] + 0.5) <= 4096.0) {
                        final EnumFacing facing2 = BlockStateUtils.getFacing(this.getBlockMetadata()).getOpposite();
                        this.triggerNote(this.world, this.pos, true);
                        final TileEntity tileentity2 = this.world.getTileEntity(this.pos);
                        final IBlockState state2 = this.world.getBlockState(this.pos);
                        if (this.getBlockType() instanceof BlockArcaneEarToggle) {
                            this.world.setBlockState(this.pos, state2.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)!BlockStateUtils.isEnabled(state2)), 3);
                        }
                        else {
                            this.redstoneSignal = 10;
                            this.world.setBlockState(this.pos, state2.withProperty((IProperty)IBlockEnabled.ENABLED, (Comparable)true), 3);
                        }
                        if (tileentity2 != null) {
                            tileentity2.validate();
                            this.world.setTileEntity(this.pos, tileentity2);
                        }
                        this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
                        this.world.notifyNeighborsOfStateChange(this.pos.offset(facing2), this.getBlockType(), true);
                        final IBlockState state3 = this.world.getBlockState(this.pos);
                        this.world.markAndNotifyBlock(this.pos, this.world.getChunkFromBlockCoords(this.pos), state3, state3, 3);
                        break;
                    }
                }
            }
        }
    }
    
    public void updateTone() {
        try {
            final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata()).getOpposite();
            final IBlockState iblockstate = this.world.getBlockState(this.pos.offset(facing));
            final Material material = iblockstate.getMaterial();
            this.tone = 0;
            if (material == Material.ROCK) {
                this.tone = 1;
            }
            if (material == Material.SAND) {
                this.tone = 2;
            }
            if (material == Material.GLASS) {
                this.tone = 3;
            }
            if (material == Material.WOOD) {
                this.tone = 4;
            }
            final Block block = iblockstate.getBlock();
            if (block == Blocks.CLAY) {
                this.tone = 5;
            }
            if (block == Blocks.GOLD_BLOCK) {
                this.tone = 6;
            }
            if (block == Blocks.WOOL) {
                this.tone = 7;
            }
            if (block == Blocks.PACKED_ICE) {
                this.tone = 8;
            }
            if (block == Blocks.BONE_BLOCK) {
                this.tone = 9;
            }
            this.markDirty();
        }
        catch (final Exception ex) {}
    }
    
    public void changePitch() {
        this.note = (byte)((this.note + 1) % 25);
        this.markDirty();
    }
    
    public void triggerNote(final World world, final BlockPos pos, final boolean sound) {
        byte i = -1;
        if (sound) {
            final EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata()).getOpposite();
            final IBlockState iblockstate = world.getBlockState(pos.offset(facing));
            final Material material = iblockstate.getMaterial();
            i = 0;
            if (material == Material.ROCK) {
                i = 1;
            }
            if (material == Material.SAND) {
                i = 2;
            }
            if (material == Material.GLASS) {
                i = 3;
            }
            if (material == Material.WOOD) {
                i = 4;
            }
            final Block block = iblockstate.getBlock();
            if (block == Blocks.CLAY) {
                i = 5;
            }
            if (block == Blocks.GOLD_BLOCK) {
                i = 6;
            }
            if (block == Blocks.WOOL) {
                i = 7;
            }
            if (block == Blocks.PACKED_ICE) {
                i = 8;
            }
            if (block == Blocks.BONE_BLOCK) {
                i = 9;
            }
        }
        world.addBlockEvent(pos, this.getBlockType(), i, this.note);
    }
    
    static {
        TileArcaneEar.noteBlockEvents = new WeakHashMap<Integer, ArrayList<Integer[]>>();
    }
}
