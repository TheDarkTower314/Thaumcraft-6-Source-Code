// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.Utils;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.EnumRarity;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.Block;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.api.items.IArchitect;
import net.minecraft.item.ItemSpade;

public class ItemElementalShovel extends ItemSpade implements IArchitect, IThaumcraftItems
{
    private static final Block[] isEffective;
    EnumFacing side;
    
    public ItemElementalShovel(final Item.ToolMaterial enumtoolmaterial) {
        super(enumtoolmaterial);
        this.side = EnumFacing.DOWN;
        this.setCreativeTab(ConfigItems.TABTC);
        this.setRegistryName("elemental_shovel");
        this.setUnlocalizedName("elemental_shovel");
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public Item getItem() {
        return this;
    }
    
    public String[] getVariantNames() {
        return new String[] { "normal" };
    }
    
    public int[] getVariantMeta() {
        return new int[] { 0 };
    }
    
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public Set<String> getToolClasses(final ItemStack stack) {
        return ImmutableSet.of("shovel");
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public boolean getIsRepairable(final ItemStack stack1, final ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 0)) || super.getIsRepairable(stack1, stack2);
    }
    
    public EnumActionResult onItemUse(final EntityPlayer player, final World world, final BlockPos pos, final EnumHand hand, final EnumFacing side, final float par8, final float par9, final float par10) {
        final IBlockState bs = world.getBlockState(pos);
        final TileEntity te = world.getTileEntity(pos);
        if (te == null) {
            for (int aa = -1; aa <= 1; ++aa) {
                for (int bb = -1; bb <= 1; ++bb) {
                    int xx = 0;
                    int yy = 0;
                    int zz = 0;
                    final byte o = getOrientation(player.getHeldItem(hand));
                    if (o == 1) {
                        yy = bb;
                        if (side.ordinal() <= 1) {
                            final int l = MathHelper.floor(player.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
                            if (l == 0 || l == 2) {
                                xx = aa;
                            }
                            else {
                                zz = aa;
                            }
                        }
                        else if (side.ordinal() <= 3) {
                            zz = aa;
                        }
                        else {
                            xx = aa;
                        }
                    }
                    else if (o == 2) {
                        if (side.ordinal() <= 1) {
                            final int l = MathHelper.floor(player.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
                            yy = bb;
                            if (l == 0 || l == 2) {
                                xx = aa;
                            }
                            else {
                                zz = aa;
                            }
                        }
                        else {
                            zz = bb;
                            xx = aa;
                        }
                    }
                    else if (side.ordinal() <= 1) {
                        xx = aa;
                        zz = bb;
                    }
                    else if (side.ordinal() <= 3) {
                        xx = aa;
                        yy = bb;
                    }
                    else {
                        zz = aa;
                        yy = bb;
                    }
                    final BlockPos p2 = pos.offset(side).add(xx, yy, zz);
                    final IBlockState b2 = world.getBlockState(p2);
                    if (bs.getBlock().canPlaceBlockAt(world, p2)) {
                        if (player.capabilities.isCreativeMode || InventoryUtils.consumePlayerItem(player, Item.getItemFromBlock(bs.getBlock()), bs.getBlock().getMetaFromState(bs))) {
                            world.playSound(p2.getX(), p2.getY(), p2.getZ(), bs.getBlock().getSoundType().getBreakSound(), SoundCategory.BLOCKS, 0.6f, 0.9f + world.rand.nextFloat() * 0.2f, false);
                            world.setBlockState(p2, bs);
                            player.getHeldItem(hand).damageItem(1, player);
                            if (world.isRemote) {
                                FXDispatcher.INSTANCE.drawBamf(p2, 8401408, false, false, side);
                            }
                            player.swingArm(hand);
                        }
                        else if (bs.getBlock() == Blocks.GRASS && (player.capabilities.isCreativeMode || InventoryUtils.consumePlayerItem(player, Item.getItemFromBlock(Blocks.DIRT), 0))) {
                            world.playSound(p2.getX(), p2.getY(), p2.getZ(), bs.getBlock().getSoundType().getBreakSound(), SoundCategory.BLOCKS, 0.6f, 0.9f + world.rand.nextFloat() * 0.2f, false);
                            world.setBlockState(p2, Blocks.DIRT.getDefaultState());
                            player.getHeldItem(hand).damageItem(1, player);
                            if (world.isRemote) {
                                FXDispatcher.INSTANCE.drawBamf(p2, 8401408, false, false, side);
                            }
                            player.swingArm(hand);
                            if (player.getHeldItem(hand).isEmpty()) {
                                break;
                            }
                            if (player.getHeldItem(hand).getCount() < 1) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return EnumActionResult.FAIL;
    }
    
    private boolean isEffectiveAgainst(final Block block) {
        for (int var3 = 0; var3 < ItemElementalShovel.isEffective.length; ++var3) {
            if (ItemElementalShovel.isEffective[var3] == block) {
                return true;
            }
        }
        return false;
    }
    
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            final ItemStack w1 = new ItemStack(this);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.DESTRUCTIVE, 1);
            items.add(w1);
        }
        else {
            super.getSubItems(tab, items);
        }
    }
    
    public ArrayList<BlockPos> getArchitectBlocks(final ItemStack focusstack, final World world, final BlockPos pos, final EnumFacing side, final EntityPlayer player) {
        final ArrayList<BlockPos> b = new ArrayList<BlockPos>();
        if (!player.isSneaking()) {
            return b;
        }
        final IBlockState bs = world.getBlockState(pos);
        for (int aa = -1; aa <= 1; ++aa) {
            for (int bb = -1; bb <= 1; ++bb) {
                int xx = 0;
                int yy = 0;
                int zz = 0;
                final byte o = getOrientation(focusstack);
                if (o == 1) {
                    yy = bb;
                    if (side.ordinal() <= 1) {
                        final int l = MathHelper.floor(player.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
                        if (l == 0 || l == 2) {
                            xx = aa;
                        }
                        else {
                            zz = aa;
                        }
                    }
                    else if (side.ordinal() <= 3) {
                        zz = aa;
                    }
                    else {
                        xx = aa;
                    }
                }
                else if (o == 2) {
                    if (side.ordinal() <= 1) {
                        final int l = MathHelper.floor(player.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
                        yy = bb;
                        if (l == 0 || l == 2) {
                            xx = aa;
                        }
                        else {
                            zz = aa;
                        }
                    }
                    else {
                        zz = bb;
                        xx = aa;
                    }
                }
                else if (side.ordinal() <= 1) {
                    xx = aa;
                    zz = bb;
                }
                else if (side.ordinal() <= 3) {
                    xx = aa;
                    yy = bb;
                }
                else {
                    zz = aa;
                    yy = bb;
                }
                final BlockPos p2 = pos.offset(side).add(xx, yy, zz);
                final IBlockState b2 = world.getBlockState(p2);
                if (bs.getBlock().canPlaceBlockAt(world, p2)) {
                    b.add(p2);
                }
            }
        }
        return b;
    }
    
    public boolean showAxis(final ItemStack stack, final World world, final EntityPlayer player, final EnumFacing side, final EnumAxis axis) {
        return false;
    }
    
    public static byte getOrientation(final ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("or")) {
            return stack.getTagCompound().getByte("or");
        }
        return 0;
    }
    
    public static void setOrientation(final ItemStack stack, final byte o) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setByte("or", (byte)(o % 3));
        }
    }
    
    public RayTraceResult getArchitectMOP(final ItemStack stack, final World world, final EntityLivingBase player) {
        return Utils.rayTrace(world, player, false);
    }
    
    public boolean useBlockHighlight(final ItemStack stack) {
        return true;
    }
    
    static {
        isEffective = new Block[] {Blocks.GRASS, Blocks.DIRT, Blocks.SAND, Blocks.GRAVEL, Blocks.SNOW_LAYER, Blocks.SNOW, Blocks.CLAY, Blocks.FARMLAND, Blocks.SOUL_SAND, Blocks.MYCELIUM};
    }
}
