// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.armor;

import thaumcraft.client.lib.UtilsFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import baubles.api.BaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.EnumRarity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.inventory.EntityEquipmentSlot;
import thaumcraft.api.ThaumcraftMaterials;
import net.minecraft.util.ResourceLocation;
import baubles.api.render.IRenderBauble;
import baubles.api.IBauble;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import net.minecraft.item.ItemArmor;

public class ItemGoggles extends ItemArmor implements IVisDiscountGear, IRevealer, IGoggles, IThaumcraftItems, IBauble, IRenderBauble
{
    ResourceLocation tex;
    
    public ItemGoggles() {
        super(ThaumcraftMaterials.ARMORMAT_SPECIAL, 4, EntityEquipmentSlot.HEAD);
        this.tex = new ResourceLocation("thaumcraft", "textures/items/goggles_bauble.png");
        this.setMaxDamage(350);
        this.setCreativeTab(ConfigItems.TABTC);
        this.setRegistryName("goggles");
        this.setUnlocalizedName("goggles");
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
    
    @SideOnly(Side.CLIENT)
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public String getArmorTexture(final ItemStack stack, final Entity entity, final EntityEquipmentSlot slot, final String type) {
        return "thaumcraft:textures/entity/armor/goggles.png";
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public boolean getIsRepairable(final ItemStack stack1, final ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 2)) || super.getIsRepairable(stack1, stack2);
    }
    
    public int getVisDiscount(final ItemStack stack, final EntityPlayer player) {
        return 5;
    }
    
    public boolean showNodes(final ItemStack itemstack, final EntityLivingBase player) {
        return true;
    }
    
    public boolean showIngamePopups(final ItemStack itemstack, final EntityLivingBase player) {
        return true;
    }
    
    public BaubleType getBaubleType(final ItemStack arg0) {
        return BaubleType.HEAD;
    }
    
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(final ItemStack stack, final EntityPlayer player, final IRenderBauble.RenderType type, final float ticks) {
        if (type == IRenderBauble.RenderType.HEAD) {
            final boolean armor = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null;
            Minecraft.getMinecraft().renderEngine.bindTexture(this.tex);
            IRenderBauble.Helper.translateToHeadLevel(player);
            IRenderBauble.Helper.translateToFace();
            IRenderBauble.Helper.defaultTransforms();
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(-0.5, -0.5, armor ? 0.11999999731779099 : 0.0);
            UtilsFX.renderTextureIn3D(0.0f, 0.0f, 1.0f, 1.0f, 16, 26, 0.1f);
        }
    }
}
