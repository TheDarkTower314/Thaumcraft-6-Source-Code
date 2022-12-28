package thaumcraft.common.items.resources;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;


public class ItemMagicDust extends ItemTCBase
{
    public ItemMagicDust() {
        super("salis_mundus");
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!player.canPlayerEdit(pos, side, player.getHeldItem(hand))) {
            return EnumActionResult.FAIL;
        }
        if (player.isSneaking()) {
            return EnumActionResult.PASS;
        }
        player.swingArm(hand);
        for (IDustTrigger trigger : IDustTrigger.triggers) {
            IDustTrigger.Placement place = trigger.getValidFace(world, player, pos, side);
            if (place != null) {
                if (!player.capabilities.isCreativeMode) {
                    player.getHeldItem(hand).shrink(1);
                }
                trigger.execute(world, player, pos, place, side);
                if (world.isRemote) {
                    doSparkles(player, world, pos, hitX, hitY, hitZ, hand, trigger, place);
                    break;
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
    
    private void doSparkles(EntityPlayer player, World world, BlockPos pos, float hitX, float hitY, float hitZ, EnumHand hand, IDustTrigger trigger, IDustTrigger.Placement place) {
        Vec3d v1 = EntityUtils.posToHand(player, hand);
        Vec3d v2 = new Vec3d(pos);
        v2 = v2.addVector(0.5, 0.5, 0.5);
        v2 = v2.subtract(v1);
        for (int cnt = 50, a = 0; a < cnt; ++a) {
            boolean floaty = a < cnt / 3;
            float r = MathHelper.getInt(world.rand, 255, 255) / 255.0f;
            float g = MathHelper.getInt(world.rand, 189, 255) / 255.0f;
            float b = MathHelper.getInt(world.rand, 64, 255) / 255.0f;
            FXDispatcher.INSTANCE.drawSimpleSparkle(world.rand, v1.x, v1.y, v1.z, v2.x / 6.0 + world.rand.nextGaussian() * 0.05, v2.y / 6.0 + world.rand.nextGaussian() * 0.05 + (floaty ? 0.05 : 0.15), v2.z / 6.0 + world.rand.nextGaussian() * 0.05, 0.5f, r, g, b, world.rand.nextInt(5), floaty ? (0.3f + world.rand.nextFloat() * 0.5f) : 0.85f, floaty ? 0.2f : 0.5f, 16);
        }
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.dust, SoundCategory.PLAYERS, 0.33f, 1.0f + (float)world.rand.nextGaussian() * 0.05f, false);
        List<BlockPos> sparkles = trigger.sparkle(world, player, pos, place);
        if (sparkles != null) {
            Vec3d v3 = new Vec3d(pos).addVector(hitX, hitY, hitZ);
            for (BlockPos p : sparkles) {
                FXDispatcher.INSTANCE.drawBlockSparkles(p, v3);
            }
        }
    }
}
