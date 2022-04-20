// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.misc;

import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.client.fx.FXDispatcher;
import java.util.Random;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.lib.events.ServerEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTC;

public class BlockEffect extends BlockTC
{
    public BlockEffect(final String name) {
        super(Material.AIR, name);
        this.setTickRandomly(true);
        this.blockResistance = 999.0f;
        this.setLightLevel(0.5f);
    }
    
    public int getLightValue(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        if (state.getBlock() != this) {
            return super.getLightValue(state, world, pos);
        }
        if (state.getBlock() == BlocksTC.effectGlimmer) {
            return 15;
        }
        return super.getLightValue(state, world, pos);
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public void onEntityCollidedWithBlock(final World world, final BlockPos pos, final IBlockState state, final Entity entity) {
        if (state.getBlock() == BlocksTC.effectShock) {
            if (entity instanceof EntityLivingBase) {
                ServerEvents.addRunnableServer(world, new Runnable() {
                    @Override
                    public void run() {
                        entity.attackEntityFrom(DamageSource.MAGIC, 1.0f);
                        final PotionEffect pe = new PotionEffect(MobEffects.SLOWNESS, 20, 0, true, true);
                        ((EntityLivingBase)entity).addPotionEffect(pe);
                    }
                }, 0);
            }
            if (!world.isRemote && world.rand.nextInt(100) == 0) {
                world.setBlockToAir(pos);
            }
        }
        else if (state.getBlock() == BlocksTC.effectSap && !(entity instanceof IEldritchMob) && entity instanceof EntityLivingBase && !((EntityLivingBase)entity).isPotionActive(MobEffects.WITHER)) {
            ServerEvents.addRunnableServer(world, new Runnable() {
                @Override
                public void run() {
                    final PotionEffect pe0 = new PotionEffect(MobEffects.WITHER, 40, 0, true, true);
                    ((EntityLivingBase)entity).addPotionEffect(pe0);
                    final PotionEffect pe2 = new PotionEffect(MobEffects.SLOWNESS, 40, 1, true, true);
                    ((EntityLivingBase)entity).addPotionEffect(pe2);
                    final PotionEffect pe3 = new PotionEffect(MobEffects.HUNGER, 40, 1, true, true);
                    ((EntityLivingBase)entity).addPotionEffect(pe3);
                }
            }, 0);
        }
    }
    
    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        if (!worldIn.isRemote && state.getBlock() != BlocksTC.effectGlimmer) {
            worldIn.setBlockToAir(pos);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World w, final BlockPos pos, final Random r) {
        if (state.getBlock() != BlocksTC.effectGlimmer) {
            final float h = r.nextFloat() * 0.33f;
            if (state.getBlock() == BlocksTC.effectShock) {
                FXDispatcher.INSTANCE.spark(pos.getX() + w.rand.nextFloat(), pos.getY() + 0.1515f + h / 2.0f, pos.getZ() + w.rand.nextFloat(), 3.0f + h * 6.0f, 0.65f + w.rand.nextFloat() * 0.1f, 1.0f, 1.0f, 0.8f);
            }
            else {
                FXDispatcher.INSTANCE.spark(pos.getX() + w.rand.nextFloat(), pos.getY() + 0.1515f + h / 2.0f, pos.getZ() + w.rand.nextFloat(), 3.0f + h * 6.0f, 0.3f - w.rand.nextFloat() * 0.1f, 0.0f, 0.5f + w.rand.nextFloat() * 0.2f, 1.0f);
            }
            if (r.nextInt(50) == 0) {
                w.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.jacobs, SoundCategory.AMBIENT, 0.25f, 1.0f + (r.nextFloat() - r.nextFloat()) * 0.2f, false);
            }
        }
    }
    
    public boolean isAir(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return true;
    }
    
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public boolean isReplaceable(final IBlockAccess worldIn, final BlockPos pos) {
        return true;
    }
    
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
        return ItemStack.EMPTY;
    }
    
    public boolean isSideSolid(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing o) {
        return false;
    }
    
    public boolean isPassable(final IBlockAccess worldIn, final BlockPos pos) {
        return true;
    }
    
    public AxisAlignedBB getCollisionBoundingBox(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return null;
    }
    
    public boolean canCollideCheck(final IBlockState state, final boolean hitIfLiquid) {
        return false;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemById(0);
    }
}
