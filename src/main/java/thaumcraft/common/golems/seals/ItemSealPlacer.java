package thaumcraft.common.golems.seals;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;


public class ItemSealPlacer extends ItemTCBase implements ISealDisplayer
{
    public ItemSealPlacer() {
        super("seal", "blank");
        setMaxStackSize(64);
        setHasSubtypes(true);
        setMaxDamage(0);
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            String[] vn = getVariantNames();
            for (int a = 0; a < vn.length; ++a) {
                items.add(new ItemStack(this, 1, a));
            }
        }
    }
    
    @Override
    public String[] getVariantNames() {
        if (SealHandler.types.size() + 1 != VARIANTS.length) {
            String[] rs = SealHandler.getRegisteredSeals();
            String[] out = new String[rs.length + 1];
            out[0] = "blank";
            int indx = 1;
            for (String s : rs) {
                String[] sp = s.split(":");
                out[indx] = ((sp.length > 1) ? sp[1] : sp[0]);
                ++indx;
            }
            VARIANTS = out;
            VARIANTS_META = new int[VARIANTS.length];
            for (int m = 0; m < VARIANTS.length; ++m) {
                VARIANTS_META[m] = m;
            }
        }
        return VARIANTS;
    }
    
    public static ItemStack getSealStack(String sealKey) {
        String[] rs = SealHandler.getRegisteredSeals();
        int indx = 1;
        for (String s : rs) {
            if (s.equals(sealKey)) {
                return new ItemStack(ItemsTC.seals, 1, indx);
            }
            ++indx;
        }
        return null;
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote || player.getHeldItem(hand).getItemDamage() == 0 || player.isSneaking()) {
            return EnumActionResult.PASS;
        }
        if (!player.canPlayerEdit(pos, side, player.getHeldItem(hand))) {
            return EnumActionResult.FAIL;
        }
        String[] rs = SealHandler.getRegisteredSeals();
        ISeal seal = null;
        try {
            seal = SealHandler.getSeal(rs[player.getHeldItem(hand).getItemDamage() - 1]).getClass().newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (seal == null || !seal.canPlaceAt(world, pos, side)) {
            return EnumActionResult.FAIL;
        }
        if (SealHandler.addSealEntity(world, pos, side, seal, player) && !player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }
    
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }
}
