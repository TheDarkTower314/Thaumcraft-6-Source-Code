// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.consumables;

import net.minecraft.block.state.IBlockState;
import thaumcraft.common.tiles.essentia.TileJarFillable;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.item.EntityItem;
import thaumcraft.common.tiles.essentia.TileAlembic;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import java.util.Iterator;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.ItemTCEssentiaContainer;

public class ItemPhial extends ItemTCEssentiaContainer
{
    public ItemPhial() {
        super("phial", 10, "empty", "filled");
    }
    
    public static ItemStack makePhial(final Aspect aspect, final int amt) {
        final ItemStack i = new ItemStack(ItemsTC.phial, 1, 1);
        ((IEssentiaContainerItem)i.getItem()).setAspects(i, new AspectList().add(aspect, amt));
        return i;
    }
    
    public static ItemStack makeFilledPhial(final Aspect aspect) {
        return makePhial(aspect, 10);
    }
    
    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            items.add(new ItemStack(this, 1, 0));
            for (final Aspect tag : Aspect.aspects.values()) {
                final ItemStack i = new ItemStack(this, 1, 1);
                setAspects(i, new AspectList().add(tag, base));
                items.add(i);
            }
        }
    }
    
    public String getItemStackDisplayName(final ItemStack stack) {
        return (getAspects(stack) != null && !getAspects(stack).aspects.isEmpty()) ? String.format(super.getItemStackDisplayName(stack), getAspects(stack).getAspects()[0].getName()) : super.getItemStackDisplayName(stack);
    }
    
    public String getUnlocalizedName(final ItemStack stack) {
        return super.getUnlocalizedName() + "." + getVariantNames()[stack.getItemDamage()];
    }
    
    @Override
    public void onUpdate(final ItemStack stack, final World world, final Entity entity, final int par4, final boolean par5) {
        if (!world.isRemote && !stack.hasTagCompound() && stack.getItemDamage() == 1) {
            stack.setItemDamage(0);
        }
    }
    
    @Override
    public void onCreated(final ItemStack stack, final World world, final EntityPlayer player) {
        if (!world.isRemote && !stack.hasTagCompound() && stack.getItemDamage() == 1) {
            stack.setItemDamage(0);
        }
    }
    
    public boolean doesSneakBypassUse(final ItemStack stack, final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return true;
    }
    
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float f1, final float f2, final float f3, final EnumHand hand) {
        final IBlockState bi = world.getBlockState(pos);
        if (player.getHeldItem(hand).getItemDamage() == 0 && bi.getBlock() == BlocksTC.alembic) {
            final TileAlembic tile = (TileAlembic)world.getTileEntity(pos);
            if (tile.amount >= base) {
                if (world.isRemote) {
                    player.swingArm(hand);
                    return EnumActionResult.PASS;
                }
                final ItemStack phial = new ItemStack(this, 1, 1);
                setAspects(phial, new AspectList().add(tile.aspect, base));
                if (tile.takeFromContainer(tile.aspect, base)) {
                    player.getHeldItem(hand).shrink(1);
                    if (!player.inventory.addItemStackToInventory(phial)) {
                        world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, phial));
                    }
                    player.playSound(SoundEvents.ITEM_BOTTLE_FILL, 0.25f, 1.0f);
                    player.inventoryContainer.detectAndSendChanges();
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        if (player.getHeldItem(hand).getItemDamage() == 0 && (bi.getBlock() == BlocksTC.jarNormal || bi.getBlock() == BlocksTC.jarVoid)) {
            final TileJarFillable tile2 = (TileJarFillable)world.getTileEntity(pos);
            if (tile2.amount >= base) {
                if (world.isRemote) {
                    player.swingArm(hand);
                    return EnumActionResult.PASS;
                }
                final Aspect asp = Aspect.getAspect(tile2.aspect.getTag());
                if (tile2.takeFromContainer(asp, base)) {
                    player.getHeldItem(hand).shrink(1);
                    final ItemStack phial2 = new ItemStack(this, 1, 1);
                    setAspects(phial2, new AspectList().add(asp, base));
                    if (!player.inventory.addItemStackToInventory(phial2)) {
                        world.spawnEntity(new EntityItem(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, phial2));
                    }
                    player.playSound(SoundEvents.ITEM_BOTTLE_FILL, 0.25f, 1.0f);
                    player.inventoryContainer.detectAndSendChanges();
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        final AspectList al = getAspects(player.getHeldItem(hand));
        if (al != null && al.size() == 1) {
            final Aspect aspect = al.getAspects()[0];
            if (player.getHeldItem(hand).getItemDamage() != 0 && (bi.getBlock() == BlocksTC.jarNormal || bi.getBlock() == BlocksTC.jarVoid)) {
                final TileJarFillable tile3 = (TileJarFillable)world.getTileEntity(pos);
                if (tile3.amount <= 250 - base && tile3.doesContainerAccept(aspect)) {
                    if (world.isRemote) {
                        player.swingArm(hand);
                        return EnumActionResult.PASS;
                    }
                    if (tile3.addToContainer(aspect, base) == 0) {
                        world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), bi, bi, 3);
                        tile3.markDirty();
                        player.getHeldItem(hand).shrink(1);
                        if (!player.inventory.addItemStackToInventory(new ItemStack(this, 1, 0))) {
                            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(this, 1, 0)));
                        }
                        player.playSound(SoundEvents.ITEM_BOTTLE_FILL, 0.25f, 1.0f);
                        player.inventoryContainer.detectAndSendChanges();
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
        }
        return EnumActionResult.PASS;
    }
}
