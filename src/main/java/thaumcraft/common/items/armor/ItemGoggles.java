package thaumcraft.common.items.armor;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemGoggles extends ItemArmor implements IVisDiscountGear, IRevealer, IGoggles, IThaumcraftItems, IBauble, IRenderBauble
{
    ResourceLocation tex;
    
    public ItemGoggles() {
        super(ThaumcraftMaterials.ARMORMAT_SPECIAL, 4, EntityEquipmentSlot.HEAD);
        tex = new ResourceLocation("thaumcraft", "textures/items/goggles_bauble.png");
        setMaxDamage(350);
        setCreativeTab(ConfigItems.TABTC);
        setRegistryName("goggles");
        setUnlocalizedName("goggles");
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
    
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "thaumcraft:textures/entity/armor/goggles.png";
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 2)) || super.getIsRepairable(stack1, stack2);
    }
    
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        return 5;
    }
    
    public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
    
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
    
    public BaubleType getBaubleType(ItemStack arg0) {
        return BaubleType.HEAD;
    }
    
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, IRenderBauble.RenderType type, float ticks) {
        if (type == IRenderBauble.RenderType.HEAD) {
            boolean armor = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null;
            Minecraft.getMinecraft().renderEngine.bindTexture(tex);
            IRenderBauble.Helper.translateToHeadLevel(player);
            IRenderBauble.Helper.translateToFace();
            IRenderBauble.Helper.defaultTransforms();
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(-0.5, -0.5, armor ? 0.11999999731779099 : 0.0);
            UtilsFX.renderTextureIn3D(0.0f, 0.0f, 1.0f, 1.0f, 16, 26, 0.1f);
        }
    }
}
