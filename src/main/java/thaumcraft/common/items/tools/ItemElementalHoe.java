package thaumcraft.common.items.tools;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.common.lib.utils.Utils;


public class ItemElementalHoe extends ItemHoe implements IThaumcraftItems
{
    public ItemElementalHoe(Item.ToolMaterial enumtoolmaterial) {
        super(enumtoolmaterial);
        setCreativeTab(ConfigItems.TABTC);
        setRegistryName("elemental_hoe");
        setUnlocalizedName("elemental_hoe");
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public Item getItem() {
        return this;
    }
    
    public String[] getVariantNames() {
        return new String[] { "normal" };
    }
    
    public int[] getVariantMeta() {
        return new int[] { 0 };
    }
    
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public int getItemEnchantability() {
        return 5;
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 0)) || super.getIsRepairable(stack1, stack2);
    }
    
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }
        boolean did = false;
        for (int xx = -1; xx <= 1; ++xx) {
            for (int zz = -1; zz <= 1; ++zz) {
                if (super.onItemUse(player, world, pos.add(xx, 0, zz), hand, facing, hitX, hitY, hitZ) == EnumActionResult.SUCCESS) {
                    if (world.isRemote) {
                        BlockPos pp = pos.add(xx, 0, zz);
                        FXDispatcher.INSTANCE.drawBamf(pp.getX() + 0.5, pp.getY() + 1.01, pp.getZ() + 0.5, 0.3f, 0.12f, 0.1f, xx == 0 && zz == 0, false, EnumFacing.UP);
                    }
                    if (!did) {
                        did = true;
                    }
                }
            }
        }
        if (!did) {
            did = Utils.useBonemealAtLoc(world, player, pos);
            if (did) {
                player.getHeldItem(hand).damageItem(3, player);
                if (!world.isRemote) {
                    world.playBroadcastSound(2005, pos, 0);
                }
                else {
                    FXDispatcher.INSTANCE.drawBlockMistParticles(pos, 4259648);
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
