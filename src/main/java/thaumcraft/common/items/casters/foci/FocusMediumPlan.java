// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.Vec3i;
import thaumcraft.common.items.casters.CasterManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import java.util.Iterator;
import thaumcraft.api.casters.Trajectory;
import net.minecraft.util.math.Vec3d;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.api.casters.ICaster;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import thaumcraft.api.items.IArchitect;
import thaumcraft.api.casters.FocusMedium;

public class FocusMediumPlan extends FocusMedium implements IArchitect
{
    ArrayList<BlockPos> checked;
    
    public FocusMediumPlan() {
        checked = new ArrayList<BlockPos>();
    }
    
    @Override
    public String getResearch() {
        return "FOCUSPLAN";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.PLAN";
    }
    
    @Override
    public int getComplexity() {
        return 4;
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.CRAFT;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        final int[] method = { 0, 1 };
        final String[] methodDesc = { "focus.plan.full", "focus.plan.surface" };
        return new NodeSetting[] { new NodeSetting("method", "focus.plan.method", new NodeSetting.NodeSettingIntList(method, methodDesc)) };
    }
    
    @Override
    public RayTraceResult[] supplyTargets() {
        if (getParent() == null || !(getPackage().getCaster() instanceof EntityPlayer)) {
            return new RayTraceResult[0];
        }
        final ArrayList<RayTraceResult> targets = new ArrayList<RayTraceResult>();
        ItemStack casterStack = ItemStack.EMPTY;
        if (getPackage().getCaster().getHeldItemMainhand() != null && getPackage().getCaster().getHeldItemMainhand().getItem() instanceof ICaster) {
            casterStack = getPackage().getCaster().getHeldItemMainhand();
        }
        else if (getPackage().getCaster().getHeldItemOffhand() != null && getPackage().getCaster().getHeldItemOffhand().getItem() instanceof ICaster) {
            casterStack = getPackage().getCaster().getHeldItemOffhand();
        }
        for (final Trajectory sT : getParent().supplyTrajectories()) {
            Vec3d end = sT.direction.normalize();
            end = end.scale(16.0);
            end = end.add(sT.source);
            final RayTraceResult target = getPackage().world.rayTraceBlocks(sT.source, end);
            if (target != null && target.typeOfHit == RayTraceResult.Type.BLOCK) {
                final ArrayList<BlockPos> usl = getArchitectBlocks(casterStack, getPackage().world, target.getBlockPos(), target.sideHit, (EntityPlayer) getPackage().getCaster());
                final ArrayList<BlockPos> sl = usl.stream().sorted(new BlockUtils.BlockPosComparator(target.getBlockPos())).collect(Collectors.toCollection(ArrayList::new));
                for (final BlockPos p : sl) {
                    targets.add(new RayTraceResult(new Vec3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5), target.sideHit, p));
                }
            }
        }
        return targets.toArray(new RayTraceResult[0]);
    }
    
    @Override
    public RayTraceResult getArchitectMOP(final ItemStack stack, final World world, final EntityLivingBase player) {
        Vec3d start = player.getPositionVector();
        start = start.addVector(0.0, player.getEyeHeight(), 0.0);
        Vec3d end = player.getLookVec();
        end = end.scale(16.0);
        end = end.add(start);
        return world.rayTraceBlocks(start, end);
    }
    
    @Override
    public boolean useBlockHighlight(final ItemStack stack) {
        return false;
    }
    
    @Override
    public boolean isExclusive() {
        return true;
    }
    
    @Override
    public boolean showAxis(final ItemStack stack, final World world, final EntityPlayer player, final EnumFacing side, final EnumAxis axis) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        final int dim = CasterManager.getAreaDim(stack);
        if (getSettingValue("method") == 0) {
            switch (axis) {
                case Y: {
                    if (dim == 0 || dim == 3) {
                        return true;
                    }
                    break;
                }
                case Z: {
                    if (dim == 0 || dim == 2) {
                        return true;
                    }
                    break;
                }
                case X: {
                    if (dim == 0 || dim == 1) {
                        return true;
                    }
                    break;
                }
            }
        }
        else {
            switch (side.getAxis()) {
                case Y: {
                    if ((axis == EnumAxis.X && (dim == 0 || dim == 1)) || (axis == EnumAxis.Z && (dim == 0 || dim == 2))) {
                        return true;
                    }
                    break;
                }
                case Z: {
                    if ((axis == EnumAxis.Y && (dim == 0 || dim == 1)) || (axis == EnumAxis.X && (dim == 0 || dim == 2))) {
                        return true;
                    }
                    break;
                }
                case X: {
                    if ((axis == EnumAxis.Y && (dim == 0 || dim == 1)) || (axis == EnumAxis.Z && (dim == 0 || dim == 2))) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }
    
    @Override
    public ArrayList<BlockPos> getArchitectBlocks(final ItemStack stack, final World world, final BlockPos pos, final EnumFacing side, final EntityPlayer player) {
        final ArrayList<BlockPos> out = new ArrayList<BlockPos>();
        if (stack == null || stack.isEmpty()) {
            return out;
        }
        if (getSettingValue("method") == 0) {
            checked.clear();
            checkNeighboursFull(world, pos, new BlockPos(pos), side, CasterManager.getAreaX(stack), CasterManager.getAreaY(stack), CasterManager.getAreaZ(stack), out, player);
        }
        else {
            final IBlockState bi = world.getBlockState(pos);
            checked.clear();
            if (side.getAxis() == EnumFacing.Axis.Z) {
                checkNeighboursSurface(world, pos, bi, new BlockPos(pos), side, CasterManager.getAreaZ(stack), CasterManager.getAreaY(stack), CasterManager.getAreaX(stack), out, player);
            }
            else {
                checkNeighboursSurface(world, pos, bi, new BlockPos(pos), side, CasterManager.getAreaX(stack), CasterManager.getAreaY(stack), CasterManager.getAreaZ(stack), out, player);
            }
        }
        return out;
    }
    
    public void checkNeighboursFull(final World world, final BlockPos pos1, final BlockPos pos2, final EnumFacing side, final int sizeX, final int sizeY, final int sizeZ, final ArrayList<BlockPos> list, final EntityPlayer player) {
        if (checked.contains(pos2)) {
            return;
        }
        checked.add(pos2);
        if (!world.isAirBlock(pos2)) {
            list.add(pos2);
        }
        int xs = -sizeX + pos1.getX();
        int xe = sizeX + pos1.getX();
        int ys = -sizeY + pos1.getY();
        int ye = sizeY + pos1.getY();
        int zs = -sizeZ + pos1.getZ();
        int ze = sizeZ + pos1.getZ();
        xs -= sizeX * side.getFrontOffsetX();
        xe -= sizeX * side.getFrontOffsetX();
        ys -= sizeY * side.getFrontOffsetY();
        ye -= sizeY * side.getFrontOffsetY();
        zs -= sizeZ * side.getFrontOffsetZ();
        ze -= sizeZ * side.getFrontOffsetZ();
        for (final EnumFacing dir : EnumFacing.values()) {
            final BlockPos q = pos2.offset(dir);
            if (q.getX() >= xs && q.getX() <= xe && q.getY() >= ys && q.getY() <= ye && q.getZ() >= zs) {
                if (q.getZ() <= ze) {
                    checkNeighboursFull(world, pos1, q, side, sizeX, sizeY, sizeZ, list, player);
                }
            }
        }
    }
    
    public void checkNeighboursSurface(final World world, final BlockPos pos1, final IBlockState bi, final BlockPos pos2, final EnumFacing side, final int sizeX, final int sizeY, final int sizeZ, final ArrayList<BlockPos> list, final EntityPlayer player) {
        if (checked.contains(pos2)) {
            return;
        }
        checked.add(pos2);
        switch (side.getAxis()) {
            case Y: {
                if (Math.abs(pos2.getX() - pos1.getX()) > sizeX) {
                    return;
                }
                if (Math.abs(pos2.getZ() - pos1.getZ()) > sizeZ) {
                    return;
                }
                break;
            }
            case Z: {
                if (Math.abs(pos2.getX() - pos1.getX()) > sizeX) {
                    return;
                }
                if (Math.abs(pos2.getY() - pos1.getY()) > sizeZ) {
                    return;
                }
                break;
            }
            case X: {
                if (Math.abs(pos2.getY() - pos1.getY()) > sizeX) {
                    return;
                }
                if (Math.abs(pos2.getZ() - pos1.getZ()) > sizeZ) {
                    return;
                }
                break;
            }
        }
        if (world.getBlockState(pos2) == bi && BlockUtils.isBlockExposed(world, pos2) && !world.isAirBlock(pos2)) {
            list.add(pos2);
            for (final EnumFacing dir : EnumFacing.values()) {
                if (dir != side) {
                    if (dir.getOpposite() != side) {
                        checkNeighboursSurface(world, pos1, bi, pos2.offset(dir), side, sizeX, sizeY, sizeZ, list, player);
                    }
                }
            }
        }
    }
}
