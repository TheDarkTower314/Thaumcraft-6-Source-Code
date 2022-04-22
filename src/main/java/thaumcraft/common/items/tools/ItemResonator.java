// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import thaumcraft.api.aspects.Aspect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileCondenser;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.aspects.IAspectContainer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.api.aspects.IEssentiaTransport;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;

public class ItemResonator extends ItemTCBase
{
    public ItemResonator() {
        super("resonator");
        this.setMaxStackSize(1);
        this.setMaxStackSize(1);
        this.setCreativeTab(ConfigItems.TABTC);
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean hasEffect(final ItemStack stack1) {
        return stack1.hasTagCompound();
    }
    
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile == null || !(tile instanceof IEssentiaTransport)) {
            return EnumActionResult.FAIL;
        }
        if (world.isRemote) {
            player.swingArm(hand);
            return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
        }
        final IEssentiaTransport et = (IEssentiaTransport)tile;
        final RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit != null && hit.subHit >= 0 && hit.subHit < 6) {
            side = EnumFacing.VALUES[hit.subHit];
        }
        if (!(tile instanceof TileTubeBuffer) && et.getEssentiaType(side) != null) {
            player.sendMessage(new TextComponentTranslation("tc.resonator1", "" + et.getEssentiaAmount(side), et.getEssentiaType(side).getName()));
        }
        else if (tile instanceof TileTubeBuffer && ((IAspectContainer)tile).getAspects().size() > 0) {
            for (final Aspect aspect : ((IAspectContainer)tile).getAspects().getAspectsSortedByName()) {
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
            final TileCondenser tc = (TileCondenser)tile;
            player.sendMessage(new TextComponentTranslation("tc.condenser1", "" + tc.cost));
            final int s2 = tc.interval / 20;
            player.sendMessage(new TextComponentTranslation("tc.condenser2", "" + tc.interval, "" + s2));
        }
        return EnumActionResult.SUCCESS;
    }
}
