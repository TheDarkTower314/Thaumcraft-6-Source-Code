// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.essentia;

import thaumcraft.client.fx.FXDispatcher;
import java.util.Random;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aura.AuraHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileJarBrain;
import thaumcraft.common.tiles.essentia.TileJarFillable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.block.material.Material;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.common.blocks.BlockTCTile;

public class BlockJar extends BlockTCTile implements ILabelable
{
    public BlockJar(final Class t, final String name) {
        super(Material.GLASS, t, name);
        setHardness(0.3f);
        setSoundType(SoundsTC.JAR);
    }
    
    public SoundType getSoundType() {
        return SoundsTC.JAR;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.75, 0.8125);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return getStateFromMeta(meta);
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        BlockJar.spillEssentia = false;
        super.breakBlock(worldIn, pos, state);
        BlockJar.spillEssentia = true;
    }
    
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        final TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileJarFillable) {
            spawnFilledJar(worldIn, pos, state, (TileJarFillable)te);
        }
        else if (te instanceof TileJarBrain) {
            spawnBrainJar(worldIn, pos, state, (TileJarBrain)te);
        }
        else {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }
    
    public void harvestBlock(final World worldIn, final EntityPlayer player, final BlockPos pos, final IBlockState state, final TileEntity te, final ItemStack stack) {
        if (te instanceof TileJarFillable) {
            spawnFilledJar(worldIn, pos, state, (TileJarFillable)te);
        }
        else if (te instanceof TileJarBrain) {
            spawnBrainJar(worldIn, pos, state, (TileJarBrain)te);
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }
    
    private void spawnFilledJar(final World world, final BlockPos pos, final IBlockState state, final TileJarFillable te) {
        final ItemStack drop = new ItemStack(this, 1, getMetaFromState(state));
        if (te.amount > 0) {
            ((BlockJarItem)drop.getItem()).setAspects(drop, new AspectList().add(te.aspect, te.amount));
        }
        if (te.aspectFilter != null) {
            if (!drop.hasTagCompound()) {
                drop.setTagCompound(new NBTTagCompound());
            }
            drop.getTagCompound().setString("AspectFilter", te.aspectFilter.getTag());
        }
        if (te.blocked) {
            spawnAsEntity(world, pos, new ItemStack(ItemsTC.jarBrace));
        }
        spawnAsEntity(world, pos, drop);
    }
    
    private void spawnBrainJar(final World world, final BlockPos pos, final IBlockState state, final TileJarBrain te) {
        final ItemStack drop = new ItemStack(this, 1, getMetaFromState(state));
        if (te.xp > 0) {
            drop.setTagInfo("xp", new NBTTagInt(te.xp));
        }
        spawnAsEntity(world, pos, drop);
    }
    
    public void onBlockPlacedBy(final World world, final BlockPos pos, final IBlockState state, final EntityLivingBase ent, final ItemStack stack) {
        final int l = MathHelper.floor(ent.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        final TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileJarFillable) {
            if (l == 0) {
                ((TileJarFillable)tile).facing = 2;
            }
            if (l == 1) {
                ((TileJarFillable)tile).facing = 5;
            }
            if (l == 2) {
                ((TileJarFillable)tile).facing = 3;
            }
            if (l == 3) {
                ((TileJarFillable)tile).facing = 4;
            }
        }
    }
    
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        final TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileJarBrain) {
            ((TileJarBrain)te).eatDelay = 40;
            if (!world.isRemote) {
                final int var6 = world.rand.nextInt(Math.min(((TileJarBrain)te).xp + 1, 64));
                if (var6 > 0) {
                    final TileJarBrain tileJarBrain = (TileJarBrain)te;
                    tileJarBrain.xp -= var6;
                    int xp = var6;
                    while (xp > 0) {
                        final int var7 = EntityXPOrb.getXPSplit(xp);
                        xp -= var7;
                        world.spawnEntity(new EntityXPOrb(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, var7));
                    }
                    world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 3);
                    te.markDirty();
                }
            }
            else {
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.jar, SoundCategory.BLOCKS, 0.2f, 1.0f, false);
            }
        }
        if (te != null && te instanceof TileJarFillable && !((TileJarFillable)te).blocked && player.getHeldItem(hand).getItem() == ItemsTC.jarBrace) {
            ((TileJarFillable)te).blocked = true;
            player.getHeldItem(hand).shrink(1);
            if (world.isRemote) {
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.key, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            }
            else {
                te.markDirty();
            }
        }
        else if (te != null && te instanceof TileJarFillable && player.isSneaking() && ((TileJarFillable)te).aspectFilter != null && side.ordinal() == ((TileJarFillable)te).facing) {
            ((TileJarFillable)te).aspectFilter = null;
            if (world.isRemote) {
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.page, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            }
            else {
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5f + side.getFrontOffsetX() / 3.0f, pos.getY() + 0.5f, pos.getZ() + 0.5f + side.getFrontOffsetZ() / 3.0f, new ItemStack(ItemsTC.label)));
            }
        }
        else if (te != null && te instanceof TileJarFillable && player.isSneaking() && player.getHeldItem(hand).isEmpty()) {
            if (((TileJarFillable)te).aspectFilter == null) {
                ((TileJarFillable)te).aspect = null;
            }
            if (world.isRemote) {
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.jar, SoundCategory.BLOCKS, 0.4f, 1.0f, false);
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.5f, 1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3f, false);
            }
            else {
                AuraHelper.polluteAura(world, pos, (float)((TileJarFillable)te).amount, true);
            }
            ((TileJarFillable)te).amount = 0;
            te.markDirty();
        }
        return true;
    }
    
    @Override
    public boolean applyLabel(final EntityPlayer player, final BlockPos pos, final EnumFacing side, final ItemStack labelstack) {
        final TileEntity te = player.world.getTileEntity(pos);
        if (te == null || !(te instanceof TileJarFillable) || ((TileJarFillable)te).aspectFilter != null) {
            return false;
        }
        if (((TileJarFillable)te).amount == 0 && ((IEssentiaContainerItem)labelstack.getItem()).getAspects(labelstack) == null) {
            return false;
        }
        if (((TileJarFillable)te).amount == 0 && ((IEssentiaContainerItem)labelstack.getItem()).getAspects(labelstack) != null) {
            ((TileJarFillable)te).aspect = ((IEssentiaContainerItem)labelstack.getItem()).getAspects(labelstack).getAspects()[0];
        }
        onBlockPlacedBy(player.world, pos, player.world.getBlockState(pos), player, null);
        ((TileJarFillable)te).aspectFilter = ((TileJarFillable)te).aspect;
        player.world.markAndNotifyBlock(pos, player.world.getChunkFromBlockCoords(pos), player.world.getBlockState(pos), player.world.getBlockState(pos), 3);
        te.markDirty();
        player.world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.jar, SoundCategory.BLOCKS, 0.4f, 1.0f);
        return true;
    }
    
    public float getEnchantPowerBonus(final World world, final BlockPos pos) {
        final TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileJarBrain) {
            return 5.0f;
        }
        return super.getEnchantPowerBonus(world, pos);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World world, final BlockPos pos, final Random rand) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileJarBrain && ((TileJarBrain)tile).xp >= ((TileJarBrain)tile).xpMax) {
            FXDispatcher.INSTANCE.spark(pos.getX() + 0.5f, pos.getY() + 0.8f, pos.getZ() + 0.5f, 3.0f, 0.2f + rand.nextFloat() * 0.2f, 1.0f, 0.3f + rand.nextFloat() * 0.2f, 0.5f);
        }
    }
    
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return true;
    }
    
    public int getComparatorInputOverride(final IBlockState state, final World world, final BlockPos pos) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileJarBrain) {
            final float r = ((TileJarBrain)tile).xp / (float)((TileJarBrain)tile).xpMax;
            return MathHelper.floor(r * 14.0f) + ((((TileJarBrain)tile).xp > 0) ? 1 : 0);
        }
        if (tile != null && tile instanceof TileJarFillable) {
            final float n = (float)((TileJarFillable)tile).amount;
            final TileJarFillable tileJarFillable = (TileJarFillable)tile;
            final float r = n / 250.0f;
            return MathHelper.floor(r * 14.0f) + ((((TileJarFillable)tile).amount > 0) ? 1 : 0);
        }
        return super.getComparatorInputOverride(state, world, pos);
    }
}
