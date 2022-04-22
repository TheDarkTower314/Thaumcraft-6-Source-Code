// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.essentia;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileJarBrain;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class BlockJarBrainItem extends ItemBlock
{
    public BlockJarBrainItem(final Block block) {
        super(block);
    }
    
    public boolean placeBlockAt(final ItemStack stack, final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final IBlockState newState) {
        final boolean b = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (b && !world.isRemote) {
            final TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileJarBrain) {
                final TileJarBrain jar = (TileJarBrain)te;
                if (stack.hasTagCompound()) {
                    jar.xp = stack.getTagCompound().getInteger("xp");
                }
                te.markDirty();
                world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), newState, newState, 3);
            }
        }
        return b;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("xp")) {
            final int tf = stack.getTagCompound().getInteger("xp");
            tooltip.add("§a" + tf + " xp");
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
