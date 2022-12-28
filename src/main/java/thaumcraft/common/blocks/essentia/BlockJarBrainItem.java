package thaumcraft.common.blocks.essentia;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.tiles.devices.TileJarBrain;


public class BlockJarBrainItem extends ItemBlock
{
    public BlockJarBrainItem(Block block) {
        super(block);
    }
    
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean b = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (b && !world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileJarBrain) {
                TileJarBrain jar = (TileJarBrain)te;
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
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("xp")) {
            int tf = stack.getTagCompound().getInteger("xp");
            tooltip.add("§a" + tf + " xp");
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
