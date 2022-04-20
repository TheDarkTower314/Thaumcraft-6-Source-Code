// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world.plants;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import java.util.Random;
import net.minecraftforge.common.EnumPlantType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockBush;

public class BlockPlantVishroom extends BlockBush
{
    public BlockPlantVishroom() {
        super(Material.PLANTS);
        this.setUnlocalizedName("vishroom");
        this.setRegistryName("thaumcraft", "vishroom");
        this.setCreativeTab(ConfigItems.TABTC);
        this.setSoundType(SoundType.PLANT);
        this.setLightLevel(0.4f);
    }
    
    public boolean isFullCube(final IBlockState state) {
        return state.isFullBlock();
    }
    
    public void onEntityCollidedWithBlock(final World worldIn, final BlockPos pos, final IBlockState state, final Entity entityIn) {
        if (!worldIn.isRemote && entityIn instanceof EntityLivingBase && worldIn.rand.nextInt(5) == 0) {
            ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
        }
    }
    
    public EnumPlantType getPlantType(final IBlockAccess world, final BlockPos pos) {
        return EnumPlantType.Cave;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World world, final BlockPos pos, final Random rand) {
        if (rand.nextInt(3) == 0) {
            final float xr = pos.getX() + 0.5f + (rand.nextFloat() - rand.nextFloat()) * 0.4f;
            final float yr = pos.getY() + 0.3f;
            final float zr = pos.getZ() + 0.5f + (rand.nextFloat() - rand.nextFloat()) * 0.4f;
            FXDispatcher.INSTANCE.drawWispyMotes(xr, yr, zr, 0.0, 0.0, 0.0, 10, 0.5f, 0.3f, 0.8f, 0.001f);
        }
    }
}
