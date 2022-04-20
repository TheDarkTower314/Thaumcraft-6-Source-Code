// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import thaumcraft.Thaumcraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.Block;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;

public class ItemHandMirror extends ItemTCBase
{
    public ItemHandMirror() {
        super("hand_mirror", new String[0]);
        this.setMaxStackSize(1);
    }
    
    public boolean getShareTag() {
        return true;
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean hasEffect(final ItemStack stack1) {
        return stack1.hasTagCompound();
    }
    
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float par8, final float par9, final float par10, final EnumHand hand) {
        final Block bi = world.getBlockState(pos).getBlock();
        if (bi != BlocksTC.mirror) {
            return EnumActionResult.FAIL;
        }
        if (world.isRemote) {
            player.swingArm(hand);
            return super.onItemUseFirst(player, world, pos, side, par8, par9, par10, hand);
        }
        final TileEntity tm = world.getTileEntity(pos);
        if (tm != null && tm instanceof TileMirror) {
            player.getHeldItem(hand).setTagInfo("linkX", new NBTTagInt(pos.getX()));
            player.getHeldItem(hand).setTagInfo("linkY", new NBTTagInt(pos.getY()));
            player.getHeldItem(hand).setTagInfo("linkZ", new NBTTagInt(pos.getZ()));
            player.getHeldItem(hand).setTagInfo("linkDim", new NBTTagInt(world.provider.getDimension()));
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundsTC.jar, SoundCategory.BLOCKS, 1.0f, 2.0f);
            player.sendMessage(new TextComponentTranslation("tc.handmirrorlinked", new Object[0]));
            player.inventoryContainer.detectAndSendChanges();
        }
        return EnumActionResult.PASS;
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        if (!world.isRemote && player.getHeldItem(hand).hasTagCompound()) {
            final int lx = player.getHeldItem(hand).getTagCompound().getInteger("linkX");
            final int ly = player.getHeldItem(hand).getTagCompound().getInteger("linkY");
            final int lz = player.getHeldItem(hand).getTagCompound().getInteger("linkZ");
            final int ldim = player.getHeldItem(hand).getTagCompound().getInteger("linkDim");
            final World targetWorld = DimensionManager.getWorld(ldim);
            if (targetWorld == null) {
                return super.onItemRightClick(world, player, hand);
            }
            final TileEntity te = targetWorld.getTileEntity(new BlockPos(lx, ly, lz));
            if (te == null || !(te instanceof TileMirror)) {
                player.getHeldItem(hand).setTagCompound(null);
                player.playSound(SoundsTC.zap, 1.0f, 0.8f);
                player.sendMessage(new TextComponentTranslation("tc.handmirrorerror", new Object[0]));
                return super.onItemRightClick(world, player, hand);
            }
            player.openGui(Thaumcraft.instance, 4, world, MathHelper.floor(player.posX), MathHelper.floor(player.posY), MathHelper.floor(player.posZ));
        }
        return super.onItemRightClick(world, player, hand);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            final int lx = stack.getTagCompound().getInteger("linkX");
            final int ly = stack.getTagCompound().getInteger("linkY");
            final int lz = stack.getTagCompound().getInteger("linkZ");
            final int ldim = stack.getTagCompound().getInteger("linkDim");
            tooltip.add(I18n.translateToLocal("tc.handmirrorlinkedto") + " " + lx + "," + ly + "," + lz + " in " + ldim);
        }
    }
    
    public static boolean transport(final ItemStack mirror, ItemStack items, final EntityPlayer player, final World worldObj) {
        if (!mirror.hasTagCompound()) {
            return false;
        }
        final int lx = mirror.getTagCompound().getInteger("linkX");
        final int ly = mirror.getTagCompound().getInteger("linkY");
        final int lz = mirror.getTagCompound().getInteger("linkZ");
        final int ldim = mirror.getTagCompound().getInteger("linkDim");
        final World targetWorld = DimensionManager.getWorld(ldim);
        if (targetWorld == null) {
            return false;
        }
        final TileEntity te = targetWorld.getTileEntity(new BlockPos(lx, ly, lz));
        if (te == null || !(te instanceof TileMirror)) {
            mirror.setTagCompound(null);
            player.playSound(SoundsTC.zap, 1.0f, 0.8f);
            player.sendMessage(new TextComponentTranslation("tc.handmirrorerror", new Object[0]));
            return false;
        }
        final TileMirror tm = (TileMirror)te;
        if (tm.transportDirect(items)) {
            items = ItemStack.EMPTY;
            player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.1f, 1.0f);
        }
        return true;
    }
}
