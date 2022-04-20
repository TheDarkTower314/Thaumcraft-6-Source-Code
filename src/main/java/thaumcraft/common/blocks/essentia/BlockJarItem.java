// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.essentia;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.essentia.TileJarFillable;
import net.minecraft.block.state.IBlockState;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import thaumcraft.common.tiles.essentia.TileAlembic;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.aspects.AspectList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.Block;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import net.minecraft.item.ItemBlock;

public class BlockJarItem extends ItemBlock implements IEssentiaContainerItem
{
    public BlockJarItem(final Block block) {
        super(block);
        this.addPropertyOverride(new ResourceLocation("fill"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(final ItemStack stack, @Nullable final World worldIn, @Nullable final EntityLivingBase entityIn) {
                if (stack.getItem().getDurabilityForDisplay(stack) == 1.0) {
                    return 0.0f;
                }
                if (stack.getItem().getDurabilityForDisplay(stack) >= 0.75) {
                    return 1.0f;
                }
                if (stack.getItem().getDurabilityForDisplay(stack) >= 0.5) {
                    return 2.0f;
                }
                if (stack.getItem().getDurabilityForDisplay(stack) >= 0.25) {
                    return 3.0f;
                }
                return 4.0f;
            }
        });
    }
    
    public boolean showDurabilityBar(final ItemStack stack) {
        return this.getAspects(stack) != null;
    }
    
    public double getDurabilityForDisplay(final ItemStack stack) {
        final AspectList al = this.getAspects(stack);
        return (al == null) ? 1.0 : (1.0 - al.visSize() / 250.0);
    }
    
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
        final Block bi = world.getBlockState(pos).getBlock();
        final ItemStack itemstack = player.getHeldItem(hand);
        if (bi == BlocksTC.alembic && !world.isRemote) {
            final TileAlembic tile = (TileAlembic)world.getTileEntity(pos);
            if (tile.amount > 0) {
                if (this.getFilter(itemstack) != null && this.getFilter(itemstack) != tile.aspect) {
                    return EnumActionResult.FAIL;
                }
                if (this.getAspects(itemstack) != null && this.getAspects(itemstack).getAspects()[0] != tile.aspect) {
                    return EnumActionResult.FAIL;
                }
                int amt = tile.amount;
                if (this.getAspects(itemstack) != null && this.getAspects(itemstack).visSize() + amt > 250) {
                    amt = Math.abs(this.getAspects(itemstack).visSize() - 250);
                }
                if (amt <= 0) {
                    return EnumActionResult.FAIL;
                }
                final Aspect a = tile.aspect;
                if (tile.takeFromContainer(tile.aspect, amt)) {
                    final int base = (this.getAspects(itemstack) == null) ? 0 : this.getAspects(itemstack).visSize();
                    if (itemstack.getCount() > 1) {
                        final ItemStack stack = itemstack.copy();
                        this.setAspects(stack, new AspectList().add(a, base + amt));
                        itemstack.shrink(1);
                        stack.setCount(1);
                        if (!player.inventory.addItemStackToInventory(stack)) {
                            world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, stack));
                        }
                    }
                    else {
                        this.setAspects(itemstack, new AspectList().add(a, base + amt));
                    }
                    player.playSound(SoundEvents.ITEM_BOTTLE_FILL, 0.25f, 1.0f);
                    player.inventoryContainer.detectAndSendChanges();
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return EnumActionResult.PASS;
    }
    
    public boolean placeBlockAt(final ItemStack stack, final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final IBlockState newState) {
        final boolean b = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (b && !world.isRemote) {
            final TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileJarFillable) {
                final TileJarFillable jar = (TileJarFillable)te;
                jar.setAspects(this.getAspects(stack));
                if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AspectFilter")) {
                    jar.aspectFilter = Aspect.getAspect(stack.getTagCompound().getString("AspectFilter"));
                }
                te.markDirty();
                world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), newState, newState, 3);
            }
        }
        return b;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AspectFilter")) {
            final String tf = stack.getTagCompound().getString("AspectFilter");
            final Aspect tag = Aspect.getAspect(tf);
            tooltip.add("�5" + tag.getName());
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    public AspectList getAspects(final ItemStack itemstack) {
        if (itemstack.hasTagCompound()) {
            final AspectList aspects = new AspectList();
            aspects.readFromNBT(itemstack.getTagCompound());
            return (aspects.size() > 0) ? aspects : null;
        }
        return null;
    }
    
    public Aspect getFilter(final ItemStack itemstack) {
        if (itemstack.hasTagCompound()) {
            return Aspect.getAspect(itemstack.getTagCompound().getString("AspectFilter"));
        }
        return null;
    }
    
    public void setAspects(final ItemStack itemstack, final AspectList aspects) {
        if (!itemstack.hasTagCompound()) {
            itemstack.setTagCompound(new NBTTagCompound());
        }
        aspects.writeToNBT(itemstack.getTagCompound());
    }
    
    public boolean ignoreContainedAspects() {
        return false;
    }
}
