package thaumcraft.common.items.tools;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.devices.TileMirror;


public class ItemHandMirror extends ItemTCBase
{
    public ItemHandMirror() {
        super("hand_mirror");
        setMaxStackSize(1);
    }
    
    public boolean getShareTag() {
        return true;
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean hasEffect(ItemStack stack1) {
        return stack1.hasTagCompound();
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float par8, float par9, float par10, EnumHand hand) {
        Block bi = world.getBlockState(pos).getBlock();
        if (bi != BlocksTC.mirror) {
            return EnumActionResult.FAIL;
        }
        if (world.isRemote) {
            player.swingArm(hand);
            return super.onItemUseFirst(player, world, pos, side, par8, par9, par10, hand);
        }
        TileEntity tm = world.getTileEntity(pos);
        if (tm != null && tm instanceof TileMirror) {
            player.getHeldItem(hand).setTagInfo("linkX", new NBTTagInt(pos.getX()));
            player.getHeldItem(hand).setTagInfo("linkY", new NBTTagInt(pos.getY()));
            player.getHeldItem(hand).setTagInfo("linkZ", new NBTTagInt(pos.getZ()));
            player.getHeldItem(hand).setTagInfo("linkDim", new NBTTagInt(world.provider.getDimension()));
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundsTC.jar, SoundCategory.BLOCKS, 1.0f, 2.0f);
            player.sendMessage(new TextComponentTranslation("tc.handmirrorlinked"));
            player.inventoryContainer.detectAndSendChanges();
        }
        return EnumActionResult.PASS;
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && player.getHeldItem(hand).hasTagCompound()) {
            int lx = player.getHeldItem(hand).getTagCompound().getInteger("linkX");
            int ly = player.getHeldItem(hand).getTagCompound().getInteger("linkY");
            int lz = player.getHeldItem(hand).getTagCompound().getInteger("linkZ");
            int ldim = player.getHeldItem(hand).getTagCompound().getInteger("linkDim");
            World targetWorld = DimensionManager.getWorld(ldim);
            if (targetWorld == null) {
                return super.onItemRightClick(world, player, hand);
            }
            TileEntity te = targetWorld.getTileEntity(new BlockPos(lx, ly, lz));
            if (te == null || !(te instanceof TileMirror)) {
                player.getHeldItem(hand).setTagCompound(null);
                player.playSound(SoundsTC.zap, 1.0f, 0.8f);
                player.sendMessage(new TextComponentTranslation("tc.handmirrorerror"));
                return super.onItemRightClick(world, player, hand);
            }
            player.openGui(Thaumcraft.instance, 4, world, MathHelper.floor(player.posX), MathHelper.floor(player.posY), MathHelper.floor(player.posZ));
        }
        return super.onItemRightClick(world, player, hand);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            int lx = stack.getTagCompound().getInteger("linkX");
            int ly = stack.getTagCompound().getInteger("linkY");
            int lz = stack.getTagCompound().getInteger("linkZ");
            int ldim = stack.getTagCompound().getInteger("linkDim");
            tooltip.add(I18n.translateToLocal("tc.handmirrorlinkedto") + " " + lx + "," + ly + "," + lz + " in " + ldim);
        }
    }
    
    public static boolean transport(ItemStack mirror, ItemStack items, EntityPlayer player, World worldObj) {
        if (!mirror.hasTagCompound()) {
            return false;
        }
        int lx = mirror.getTagCompound().getInteger("linkX");
        int ly = mirror.getTagCompound().getInteger("linkY");
        int lz = mirror.getTagCompound().getInteger("linkZ");
        int ldim = mirror.getTagCompound().getInteger("linkDim");
        World targetWorld = DimensionManager.getWorld(ldim);
        if (targetWorld == null) {
            return false;
        }
        TileEntity te = targetWorld.getTileEntity(new BlockPos(lx, ly, lz));
        if (te == null || !(te instanceof TileMirror)) {
            mirror.setTagCompound(null);
            player.playSound(SoundsTC.zap, 1.0f, 0.8f);
            player.sendMessage(new TextComponentTranslation("tc.handmirrorerror"));
            return false;
        }
        TileMirror tm = (TileMirror)te;
        if (tm.transportDirect(items)) {
            items = ItemStack.EMPTY;
            player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.1f, 1.0f);
        }
        return true;
    }
}
