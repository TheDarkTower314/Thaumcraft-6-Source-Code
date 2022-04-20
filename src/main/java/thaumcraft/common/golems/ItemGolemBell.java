// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thaumcraft.api.golems.seals.SealPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.golems.seals.ISealEntity;
import net.minecraft.init.SoundEvents;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.util.EnumActionResult;
import thaumcraft.Thaumcraft;
import net.minecraft.util.SoundCategory;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.golems.seals.SealHandler;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.common.items.ItemTCBase;

public class ItemGolemBell extends ItemTCBase implements ISealDisplayer
{
    public ItemGolemBell() {
        super("golem_bell", new String[0]);
        this.setHasSubtypes(false);
        this.setMaxStackSize(1);
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World worldIn, final EntityPlayer playerIn, final EnumHand hand) {
        playerIn.swingArm(hand);
        if (!worldIn.isRemote) {
            final RayTraceResult mop = RayTracer.retrace(playerIn);
            if (mop != null && (mop.typeOfHit == RayTraceResult.Type.BLOCK || mop.typeOfHit == RayTraceResult.Type.ENTITY)) {
                final ISealEntity se = getSeal(playerIn);
                if (se != null) {
                    if (playerIn.isSneaking()) {
                        SealHandler.removeSealEntity(playerIn.world, se.getSealPos(), false);
                        worldIn.playSound(null, se.getSealPos().pos, SoundsTC.zap, SoundCategory.BLOCKS, 0.5f, 1.0f);
                    }
                    else {
                        playerIn.openGui(Thaumcraft.instance, 18, playerIn.world, se.getSealPos().pos.getX(), se.getSealPos().pos.getY(), se.getSealPos().pos.getZ());
                    }
                }
                return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.FAIL, playerIn.getHeldItem(hand));
            }
            if (playerIn.isSneaking() && ThaumcraftCapabilities.knowsResearch(playerIn, "GOLEMLOGISTICS")) {
                playerIn.openGui(Thaumcraft.instance, 20, playerIn.world, (int)playerIn.posX, (int)playerIn.posY, (int)playerIn.posZ);
                return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.FAIL, playerIn.getHeldItem(hand));
            }
        }
        else {
            playerIn.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 1.0f + worldIn.rand.nextFloat() * 0.1f);
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }
    
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
        player.swingArm(hand);
        if (!world.isRemote) {
            final ISealEntity se = SealHandler.getSealEntity(world.provider.getDimension(), new SealPos(pos, side));
            if (se != null) {
                if (player.isSneaking()) {
                    SealHandler.removeSealEntity(world, se.getSealPos(), false);
                    world.playSound(null, pos, SoundsTC.zap, SoundCategory.BLOCKS, 0.5f, 1.0f);
                }
                else {
                    player.openGui(Thaumcraft.instance, 18, world, pos.getX(), pos.getY(), pos.getZ());
                }
                return EnumActionResult.SUCCESS;
            }
            if (player.isSneaking() && ThaumcraftCapabilities.knowsResearch(player, "GOLEMLOGISTICS")) {
                player.openGui(Thaumcraft.instance, 20, world, pos.getX(), pos.getY(), pos.getZ());
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }
    
    public static ISealEntity getSeal(final EntityPlayer playerIn) {
        final float f = playerIn.rotationPitch;
        final float f2 = playerIn.rotationYaw;
        final double d0 = playerIn.posX;
        final double d2 = playerIn.posY + playerIn.getEyeHeight();
        final double d3 = playerIn.posZ;
        final Vec3d vec0 = new Vec3d(d0, d2, d3);
        final float f3 = MathHelper.cos(-f2 * 0.017453292f - 3.1415927f);
        final float f4 = MathHelper.sin(-f2 * 0.017453292f - 3.1415927f);
        final float f5 = -MathHelper.cos(-f * 0.017453292f);
        final float f6 = MathHelper.sin(-f * 0.017453292f);
        final float f7 = f4 * f5;
        final float f8 = f3 * f5;
        final double d4 = 5.0;
        final Vec3d vec2 = vec0.addVector(f7 * d4, f6 * d4, f8 * d4);
        final Vec3d vec3 = new Vec3d(f7 * d4, f6 * d4, f8 * d4);
        Vec3d vec4 = vec0.addVector(vec3.x / 10.0, vec3.y / 10.0, vec3.z / 10.0);
        for (int a = 0; a < vec3.lengthVector() * 10.0; ++a) {
            final BlockPos pos = new BlockPos(vec4);
            final RayTraceResult mop = collisionRayTrace(playerIn.world, pos, vec0, vec2);
            if (mop != null) {
                final ISealEntity se = SealHandler.getSealEntity(playerIn.world.provider.getDimension(), new SealPos(pos, mop.sideHit));
                if (se != null) {
                    return se;
                }
            }
            vec4 = vec4.addVector(vec3.x / 10.0, vec3.y / 10.0, vec3.z / 10.0);
        }
        return null;
    }
    
    private static boolean isVecInsideYZBounds(final Vec3d point, final BlockPos pos) {
        return point != null && (point.y >= pos.getY() && point.y <= pos.getY() + 1 && point.z >= pos.getZ() && point.z <= pos.getZ() + 1);
    }
    
    private static boolean isVecInsideXZBounds(final Vec3d point, final BlockPos pos) {
        return point != null && (point.x >= pos.getX() && point.x <= pos.getX() + 1 && point.z >= pos.getZ() && point.z <= pos.getZ() + 1);
    }
    
    private static boolean isVecInsideXYBounds(final Vec3d point, final BlockPos pos) {
        return point != null && (point.x >= pos.getX() && point.x <= pos.getX() + 1 && point.y >= pos.getY() && point.y <= pos.getY() + 1);
    }
    
    private static RayTraceResult collisionRayTrace(final World worldIn, final BlockPos pos, final Vec3d start, final Vec3d end) {
        Vec3d vec3 = start.getIntermediateWithXValue(end, pos.getX());
        Vec3d vec4 = start.getIntermediateWithXValue(end, pos.getX() + 1);
        Vec3d vec5 = start.getIntermediateWithYValue(end, pos.getY());
        Vec3d vec6 = start.getIntermediateWithYValue(end, pos.getY() + 1);
        Vec3d vec7 = start.getIntermediateWithZValue(end, pos.getZ());
        Vec3d vec8 = start.getIntermediateWithZValue(end, pos.getZ() + 1);
        if (!isVecInsideYZBounds(vec3, pos)) {
            vec3 = null;
        }
        if (!isVecInsideYZBounds(vec4, pos)) {
            vec4 = null;
        }
        if (!isVecInsideXZBounds(vec5, pos)) {
            vec5 = null;
        }
        if (!isVecInsideXZBounds(vec6, pos)) {
            vec6 = null;
        }
        if (!isVecInsideXYBounds(vec7, pos)) {
            vec7 = null;
        }
        if (!isVecInsideXYBounds(vec8, pos)) {
            vec8 = null;
        }
        Vec3d vec9 = null;
        if (vec3 != null && (vec9 == null || start.squareDistanceTo(vec3) < start.squareDistanceTo(vec9))) {
            vec9 = vec3;
        }
        if (vec4 != null && (vec9 == null || start.squareDistanceTo(vec4) < start.squareDistanceTo(vec9))) {
            vec9 = vec4;
        }
        if (vec5 != null && (vec9 == null || start.squareDistanceTo(vec5) < start.squareDistanceTo(vec9))) {
            vec9 = vec5;
        }
        if (vec6 != null && (vec9 == null || start.squareDistanceTo(vec6) < start.squareDistanceTo(vec9))) {
            vec9 = vec6;
        }
        if (vec7 != null && (vec9 == null || start.squareDistanceTo(vec7) < start.squareDistanceTo(vec9))) {
            vec9 = vec7;
        }
        if (vec8 != null && (vec9 == null || start.squareDistanceTo(vec8) < start.squareDistanceTo(vec9))) {
            vec9 = vec8;
        }
        if (vec9 == null) {
            return null;
        }
        EnumFacing enumfacing = null;
        if (vec9 == vec3) {
            enumfacing = EnumFacing.WEST;
        }
        if (vec9 == vec4) {
            enumfacing = EnumFacing.EAST;
        }
        if (vec9 == vec5) {
            enumfacing = EnumFacing.DOWN;
        }
        if (vec9 == vec6) {
            enumfacing = EnumFacing.UP;
        }
        if (vec9 == vec7) {
            enumfacing = EnumFacing.NORTH;
        }
        if (vec9 == vec8) {
            enumfacing = EnumFacing.SOUTH;
        }
        return new RayTraceResult(vec9.addVector(pos.getX(), pos.getY(), pos.getZ()), enumfacing, pos);
    }
}
