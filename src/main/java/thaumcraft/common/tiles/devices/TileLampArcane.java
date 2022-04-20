// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.tiles.devices;

import thaumcraft.common.lib.utils.BlockUtils;
import net.minecraft.world.EnumSkyBlock;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ITickable;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileLampArcane extends TileThaumcraft implements ITickable
{
    public int rad;
    public int rad1;
    
    public TileLampArcane() {
        this.rad1 = 0;
    }
    
    public void update() {
        if (!this.world.isRemote && this.world.getTotalWorldTime() % 5L == 0L && !this.gettingPower()) {
            final int x = this.world.rand.nextInt(16) - this.world.rand.nextInt(16);
            final int y = this.world.rand.nextInt(16) - this.world.rand.nextInt(16);
            final int z = this.world.rand.nextInt(16) - this.world.rand.nextInt(16);
            BlockPos bp = this.pos.add(x, y, z);
            if (bp.getY() > this.world.getPrecipitationHeight(bp).getY() + 4) {
                bp = this.world.getPrecipitationHeight(bp).up(4);
            }
            if (bp.getY() < 5) {
                bp = new BlockPos(bp.getX(), 5, bp.getZ());
            }
            if (this.world.isAirBlock(bp) && this.world.getBlockState(bp) != BlocksTC.effectGlimmer.getDefaultState() && this.world.getLightFor(EnumSkyBlock.BLOCK, bp) < 11 && BlockUtils.hasLOS(this.getWorld(), this.getPos(), bp)) {
                this.world.setBlockState(bp, BlocksTC.effectGlimmer.getDefaultState(), 3);
            }
        }
    }
    
    public void removeLights() {
        for (int x = -15; x <= 15; ++x) {
            for (int y = -15; y <= 15; ++y) {
                for (int z = -15; z <= 15; ++z) {
                    final BlockPos bp = this.pos.add(x, y, z);
                    if (this.world.getBlockState(bp) == BlocksTC.effectGlimmer.getDefaultState()) {
                        this.world.setBlockToAir(bp);
                    }
                }
            }
        }
    }
}
