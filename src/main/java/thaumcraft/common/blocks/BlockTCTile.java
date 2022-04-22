// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks;

import thaumcraft.api.aura.AuraHelper;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.block.state.IBlockState;
import thaumcraft.Thaumcraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.material.Material;
import net.minecraft.block.ITileEntityProvider;

public class BlockTCTile extends BlockTC implements ITileEntityProvider
{
    protected final Class<? extends TileEntity> tileClass;
    protected static boolean keepInventory;
    protected static boolean spillEssentia;
    
    public BlockTCTile(final Material mat, final Class<? extends TileEntity> tc, final String name) {
        super(mat, name);
        setHardness(2.0f);
        setResistance(20.0f);
        tileClass = tc;
    }
    
    public boolean canHarvestBlock(final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return true;
    }
    
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        if (tileClass == null) {
            return null;
        }
        try {
            return tileClass.newInstance();
        }
        catch (final InstantiationException e) {
            Thaumcraft.log.catching(e);
        }
        catch (final IllegalAccessException e2) {
            Thaumcraft.log.catching(e2);
        }
        return null;
    }
    
    public boolean hasTileEntity(final IBlockState state) {
        return true;
    }
    
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        InventoryUtils.dropItems(worldIn, pos);
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity != null && tileentity instanceof IEssentiaTransport && BlockTCTile.spillEssentia && !worldIn.isRemote) {
            final int ess = ((IEssentiaTransport)tileentity).getEssentiaAmount(EnumFacing.UP);
            if (ess > 0) {
                AuraHelper.polluteAura(worldIn, pos, (float)ess, true);
            }
        }
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }
    
    public boolean eventReceived(final IBlockState state, final World worldIn, final BlockPos pos, final int id, final int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }
    
    static {
        BlockTCTile.keepInventory = false;
        BlockTCTile.spillEssentia = true;
    }
}
