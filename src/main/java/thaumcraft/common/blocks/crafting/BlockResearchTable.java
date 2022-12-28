package thaumcraft.common.blocks.crafting;
import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.Thaumcraft;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.tiles.crafting.TileResearchTable;


public class BlockResearchTable extends BlockTCDevice implements IBlockFacingHorizontal
{
    public BlockResearchTable() {
        super(Material.WOOD, TileResearchTable.class, "research_table");
        setSoundType(SoundType.WOOD);
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        player.openGui(Thaumcraft.instance, 10, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)placer.getHorizontalFacing());
        return bs;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        TileEntity te = world.getTileEntity(pos);
        if (rand.nextInt(5) == 0 && te != null && ((TileResearchTable)te).data != null) {
            double xx = rand.nextGaussian() / 2.0;
            double zz = rand.nextGaussian() / 2.0;
            double yy = 1.5 + rand.nextFloat();
            int a = 40 + rand.nextInt(20);
            FXGeneric fb = new FXGeneric(world, pos.getX() + 0.5 + xx, pos.getY() + yy, pos.getZ() + 0.5 + zz, -xx / a, -(yy - 0.85) / a, -zz / a);
            fb.setMaxAge(a);
            fb.setRBGColorF(0.5f + rand.nextFloat() * 0.5f, 0.5f + rand.nextFloat() * 0.5f, 0.5f + rand.nextFloat() * 0.5f);
            fb.setAlphaF(0.0f, 0.25f, 0.5f, 0.75f, 0.0f);
            fb.setParticles(384 + rand.nextInt(16), 1, 1);
            fb.setScale(0.8f + rand.nextFloat() * 0.3f, 0.3f);
            fb.setLayer(0);
            ParticleEngine.addEffect(world, fb);
        }
    }
}
