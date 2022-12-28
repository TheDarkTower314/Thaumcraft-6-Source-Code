package thaumcraft.common.blocks.basic;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.tiles.misc.TileBanner;


public class BlockBannerTC extends BlockTC implements ITileEntityProvider
{
    public EnumDyeColor dye;
    
    public BlockBannerTC(String name, EnumDyeColor dye) {
        super(Material.WOOD, name);
        setHardness(1.0f);
        setSoundType(SoundType.WOOD);
        this.dye = dye;
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return (dye == null) ? MapColor.RED : MapColor.getBlockColor(dye);
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity tile = source.getTileEntity(pos);
        if (tile != null && tile instanceof TileBanner) {
            if (!((TileBanner)tile).getWall()) {
                return new AxisAlignedBB(0.33000001311302185, 0.0, 0.33000001311302185, 0.6600000262260437, 2.0, 0.6600000262260437);
            }
            switch (((TileBanner)tile).getBannerFacing()) {
                case 0: {
                    return new AxisAlignedBB(0.0, -1.0, 0.0, 1.0, 1.0, 0.25);
                }
                case 8: {
                    return new AxisAlignedBB(0.0, -1.0, 0.75, 1.0, 1.0, 1.0);
                }
                case 12: {
                    return new AxisAlignedBB(0.0, -1.0, 0.0, 0.25, 1.0, 1.0);
                }
                case 4: {
                    return new AxisAlignedBB(0.75, -1.0, 0.0, 1.0, 1.0, 1.0);
                }
            }
        }
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }
    
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!w.isRemote && (p.isSneaking() || p.getHeldItem(hand).getItem() instanceof ItemPhial)) {
            TileBanner te = (TileBanner)w.getTileEntity(pos);
            if (te != null && dye != null) {
                if (p.isSneaking()) {
                    te.setAspect(null);
                }
                else if (((IEssentiaContainerItem)p.getHeldItem(hand).getItem()).getAspects(p.getHeldItem(hand)) != null) {
                    te.setAspect(((IEssentiaContainerItem)p.getHeldItem(hand).getItem()).getAspects(p.getHeldItem(hand)).getAspects()[0]);
                    p.getHeldItem(hand).shrink(1);
                }
                w.markAndNotifyBlock(pos, w.getChunkFromBlockCoords(pos), state, state, 3);
                te.markDirty();
                te.syncTile(false);
                w.playSound(null, pos, SoundEvents.BLOCK_CLOTH_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
        return true;
    }
    
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileBanner();
    }
    
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileBanner) {
            ItemStack drop = new ItemStack(this);
            if (dye != null || ((TileBanner)te).getAspect() != null) {
                drop.setTagCompound(new NBTTagCompound());
                if (((TileBanner)te).getAspect() != null) {
                    drop.getTagCompound().setString("aspect", ((TileBanner)te).getAspect().getTag());
                }
            }
            return drop;
        }
        return super.getPickBlock(state, target, world, pos, player);
    }
    
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileBanner) {
            ItemStack drop = new ItemStack(this);
            if (dye != null || ((TileBanner)te).getAspect() != null) {
                drop.setTagCompound(new NBTTagCompound());
                if (((TileBanner)te).getAspect() != null) {
                    drop.getTagCompound().setString("aspect", ((TileBanner)te).getAspect().getTag());
                }
            }
            spawnAsEntity(worldIn, pos, drop);
        }
        else {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }
    
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (te instanceof TileBanner) {
            ItemStack drop = new ItemStack(this);
            if (dye != null || ((TileBanner)te).getAspect() != null) {
                drop.setTagCompound(new NBTTagCompound());
                if (((TileBanner)te).getAspect() != null) {
                    drop.getTagCompound().setString("aspect", ((TileBanner)te).getAspect().getTag());
                }
            }
            spawnAsEntity(worldIn, pos, drop);
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }
}
