// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.baubles;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.lib.UtilsFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import baubles.api.BaubleType;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import baubles.api.render.IRenderBauble;
import baubles.api.IBauble;
import thaumcraft.common.items.ItemTCBase;

public class ItemCuriosityBand extends ItemTCBase implements IBauble, IRenderBauble
{
    ResourceLocation tex;
    
    public ItemCuriosityBand() {
        super("curiosity_band", new String[0]);
        this.tex = new ResourceLocation("thaumcraft", "textures/items/curiosity_band_worn.png");
        this.maxStackSize = 1;
        this.canRepair = false;
        this.setMaxDamage(0);
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.RARE;
    }
    
    public BaubleType getBaubleType(final ItemStack itemstack) {
        return BaubleType.HEAD;
    }
    
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(final ItemStack stack, final EntityPlayer player, final IRenderBauble.RenderType type, final float ticks) {
        if (type == IRenderBauble.RenderType.HEAD) {
            final boolean armor = !player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty();
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
