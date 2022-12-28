package thaumcraft.common.items.tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.tiles.devices.TileCondenser;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;


public class ItemResonator extends ItemTCBase
{
    public ItemResonator() {
        super("resonator");
        setMaxStackSize(1);
        setMaxStackSize(1);
        setCreativeTab(ConfigItems.TABTC);
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean hasEffect(ItemStack stack1) {
        return stack1.hasTagCompound();
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null || !(tile instanceof IEssentiaTransport)) {
            return EnumActionResult.FAIL;
        }
        if (world.isRemote) {
            player.swingArm(hand);
            return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
        }
        IEssentiaTransport et = (IEssentiaTransport)tile;
        RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit != null && hit.subHit >= 0 && hit.subHit < 6) {
            side = EnumFacing.VALUES[hit.subHit];
        }
        if (!(tile instanceof TileTubeBuffer) && et.getEssentiaType(side) != null) {
            player.sendMessage(new TextComponentTranslation("tc.resonator1", "" + et.getEssentiaAmount(side), et.getEssentiaType(side).getName()));
        }
        else if (tile instanceof TileTubeBuffer && ((IAspectContainer)tile).getAspects().size() > 0) {
            for (Aspect aspect : ((IAspectContainer)tile).getAspects().getAspectsSortedByName()) {
                player.sendMessage(new TextComponentTranslation("tc.resonator1", "" + ((IAspectContainer)tile).getAspects().getAmount(aspect), aspect.getName()));
            }
        }
        String s = I18n.translateToLocal("tc.resonator3");
        if (et.getSuctionType(side) != null) {
            s = et.getSuctionType(side).getName();
        }
        player.sendMessage(new TextComponentTranslation("tc.resonator2", "" + et.getSuctionAmount(side), s));
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.BLOCKS, 0.5f, 1.9f + world.rand.nextFloat() * 0.1f);
        if (tile != null && tile instanceof TileCondenser) {
            TileCondenser tc = (TileCondenser)tile;
            player.sendMessage(new TextComponentTranslation("tc.condenser1", "" + tc.cost));
            int s2 = tc.interval / 20;
            player.sendMessage(new TextComponentTranslation("tc.condenser2", "" + tc.interval, "" + s2));
        }
        return EnumActionResult.SUCCESS;
    }
}
