// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Iterator;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.golems.EnumGolemTrait;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.common.items.ItemTCBase;

public class ItemGolemPlacer extends ItemTCBase implements ISealDisplayer
{
    public ItemGolemPlacer() {
        super("golem", new String[0]);
    }
    
    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            final ItemStack is = new ItemStack(this, 1, 0);
            is.setTagInfo("props", new NBTTagLong(0L));
            items.add(is.copy());
            IGolemProperties props = new GolemProperties();
            props.setHead(GolemHead.getHeads()[1]);
            props.setArms(GolemArm.getArms()[1]);
            is.setTagInfo("props", new NBTTagLong(props.toLong()));
            items.add(is.copy());
            props = new GolemProperties();
            props.setMaterial(GolemMaterial.getMaterials()[1]);
            props.setHead(GolemHead.getHeads()[1]);
            props.setArms(GolemArm.getArms()[2]);
            is.setTagInfo("props", new NBTTagLong(props.toLong()));
            items.add(is.copy());
            props = new GolemProperties();
            props.setMaterial(GolemMaterial.getMaterials()[4]);
            props.setHead(GolemHead.getHeads()[1]);
            props.setArms(GolemArm.getArms()[3]);
            is.setTagInfo("props", new NBTTagLong(props.toLong()));
            items.add(is.copy());
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("props")) {
            final IGolemProperties props = GolemProperties.fromLong(stack.getTagCompound().getLong("props"));
            if (props.hasTrait(EnumGolemTrait.SMART)) {
                if (props.getRank() >= 10) {
                    tooltip.add("§6" + I18n.translateToLocal("golem.rank") + " " + props.getRank());
                }
                else {
                    final int rx = stack.getTagCompound().getInteger("xp");
                    final int xn = (props.getRank() + 1) * (props.getRank() + 1) * 1000;
                    tooltip.add("§6" + I18n.translateToLocal("golem.rank") + " " + props.getRank() + " §2(" + rx + "/" + xn + ")");
                }
            }
            tooltip.add("§a" + props.getMaterial().getLocalizedName());
            for (final EnumGolemTrait tag : props.getTraits()) {
                tooltip.add("§9-" + tag.getLocalizedName());
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
        IBlockState bs = world.getBlockState(pos);
        if (!bs.getMaterial().isSolid()) {
            return EnumActionResult.FAIL;
        }
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }
        pos = pos.offset(side);
        bs = world.getBlockState(pos);
        if (!player.canPlayerEdit(pos, side, player.getHeldItem(hand))) {
            return EnumActionResult.FAIL;
        }
        final EntityThaumcraftGolem golem = new EntityThaumcraftGolem(world);
        golem.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
        if (golem != null && world.spawnEntity(golem)) {
            golem.setOwned(true);
            golem.setValidSpawn();
            golem.setOwnerId(player.getUniqueID());
            if (player.getHeldItem(hand).hasTagCompound() && player.getHeldItem(hand).getTagCompound().hasKey("props")) {
                golem.setProperties(GolemProperties.fromLong(player.getHeldItem(hand).getTagCompound().getLong("props")));
            }
            if (player.getHeldItem(hand).hasTagCompound() && player.getHeldItem(hand).getTagCompound().hasKey("xp")) {
                golem.rankXp = player.getHeldItem(hand).getTagCompound().getInteger("xp");
            }
            golem.onInitialSpawn(world.getDifficultyForLocation(pos), null);
            if (!player.capabilities.isCreativeMode) {
                player.getHeldItem(hand).shrink(1);
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
