package thaumcraft.common.blocks.essentia;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.tiles.essentia.TileAlembic;
import thaumcraft.common.tiles.essentia.TileJarFillable;


public class BlockJarItem extends ItemBlock implements IEssentiaContainerItem
{
    public BlockJarItem(Block block) {
        super(block);
        addPropertyOverride(new ResourceLocation("fill"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
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
    
    public boolean showDurabilityBar(ItemStack stack) {
        return getAspects(stack) != null;
    }
    
    public double getDurabilityForDisplay(ItemStack stack) {
        AspectList al = getAspects(stack);
        return (al == null) ? 1.0 : (1.0 - al.visSize() / 250.0);
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        Block bi = world.getBlockState(pos).getBlock();
        ItemStack itemstack = player.getHeldItem(hand);
        if (bi == BlocksTC.alembic && !world.isRemote) {
            TileAlembic tile = (TileAlembic)world.getTileEntity(pos);
            if (tile.amount > 0) {
                if (getFilter(itemstack) != null && getFilter(itemstack) != tile.aspect) {
                    return EnumActionResult.FAIL;
                }
                if (getAspects(itemstack) != null && getAspects(itemstack).getAspects()[0] != tile.aspect) {
                    return EnumActionResult.FAIL;
                }
                int amt = tile.amount;
                if (getAspects(itemstack) != null && getAspects(itemstack).visSize() + amt > 250) {
                    amt = Math.abs(getAspects(itemstack).visSize() - 250);
                }
                if (amt <= 0) {
                    return EnumActionResult.FAIL;
                }
                Aspect a = tile.aspect;
                if (tile.takeFromContainer(tile.aspect, amt)) {
                    int base = (getAspects(itemstack) == null) ? 0 : getAspects(itemstack).visSize();
                    if (itemstack.getCount() > 1) {
                        ItemStack stack = itemstack.copy();
                        setAspects(stack, new AspectList().add(a, base + amt));
                        itemstack.shrink(1);
                        stack.setCount(1);
                        if (!player.inventory.addItemStackToInventory(stack)) {
                            world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, stack));
                        }
                    }
                    else {
                        setAspects(itemstack, new AspectList().add(a, base + amt));
                    }
                    player.playSound(SoundEvents.ITEM_BOTTLE_FILL, 0.25f, 1.0f);
                    player.inventoryContainer.detectAndSendChanges();
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return EnumActionResult.PASS;
    }
    
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean b = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (b && !world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileJarFillable) {
                TileJarFillable jar = (TileJarFillable)te;
                jar.setAspects(getAspects(stack));
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
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AspectFilter")) {
            String tf = stack.getTagCompound().getString("AspectFilter");
            Aspect tag = Aspect.getAspect(tf);
            tooltip.add("§5" + tag.getName());
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    public AspectList getAspects(ItemStack itemstack) {
        if (itemstack.hasTagCompound()) {
            AspectList aspects = new AspectList();
            aspects.readFromNBT(itemstack.getTagCompound());
            return (aspects.size() > 0) ? aspects : null;
        }
        return null;
    }
    
    public Aspect getFilter(ItemStack itemstack) {
        if (itemstack.hasTagCompound()) {
            return Aspect.getAspect(itemstack.getTagCompound().getString("AspectFilter"));
        }
        return null;
    }
    
    public void setAspects(ItemStack itemstack, AspectList aspects) {
        if (!itemstack.hasTagCompound()) {
            itemstack.setTagCompound(new NBTTagCompound());
        }
        aspects.writeToNBT(itemstack.getTagCompound());
    }
    
    public boolean ignoreContainedAspects() {
        return false;
    }
}
