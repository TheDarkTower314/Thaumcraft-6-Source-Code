package thaumcraft.common.items.armor;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class CustomArmorHelper
{
    protected static ModelBiped getCustomArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped model, ModelBiped model1, ModelBiped model2) {
        if (model == null) {
            EntityEquipmentSlot type = ((ItemArmor)itemStack.getItem()).armorType;
            if (type == EntityEquipmentSlot.CHEST || type == EntityEquipmentSlot.FEET) {
                model = model1;
            }
            else {
                model = model2;
            }
        }
        if (model != null) {
            model.bipedHead.showModel = (armorSlot == EntityEquipmentSlot.HEAD);
            model.bipedHeadwear.showModel = (armorSlot == EntityEquipmentSlot.HEAD);
            model.bipedBody.showModel = (armorSlot == EntityEquipmentSlot.CHEST || armorSlot == EntityEquipmentSlot.LEGS);
            model.bipedRightArm.showModel = (armorSlot == EntityEquipmentSlot.CHEST);
            model.bipedLeftArm.showModel = (armorSlot == EntityEquipmentSlot.CHEST);
            model.bipedRightLeg.showModel = (armorSlot == EntityEquipmentSlot.LEGS);
            model.bipedLeftLeg.showModel = (armorSlot == EntityEquipmentSlot.LEGS);
            model.isSneak = entityLiving.isSneaking();
            model.isRiding = entityLiving.isRiding();
            model.isChild = entityLiving.isChild();
            ItemStack itemstack = entityLiving.getHeldItemMainhand();
            ItemStack itemstack2 = entityLiving.getHeldItemOffhand();
            ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
            ModelBiped.ArmPose modelbiped$armpose2 = ModelBiped.ArmPose.EMPTY;
            if (itemstack != null && !itemstack.isEmpty()) {
                modelbiped$armpose = ModelBiped.ArmPose.ITEM;
                if (entityLiving.getItemInUseCount() > 0) {
                    EnumAction enumaction = itemstack.getItemUseAction();
                    if (enumaction == EnumAction.BLOCK) {
                        modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
                    }
                    else if (enumaction == EnumAction.BOW) {
                        modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }
            if (itemstack2 != null && !itemstack2.isEmpty()) {
                modelbiped$armpose2 = ModelBiped.ArmPose.ITEM;
                if (entityLiving.getItemInUseCount() > 0) {
                    EnumAction enumaction2 = itemstack2.getItemUseAction();
                    if (enumaction2 == EnumAction.BLOCK) {
                        modelbiped$armpose2 = ModelBiped.ArmPose.BLOCK;
                    }
                }
            }
            if (entityLiving.getPrimaryHand() == EnumHandSide.RIGHT) {
                model.rightArmPose = modelbiped$armpose;
                model.leftArmPose = modelbiped$armpose2;
            }
            else {
                model.rightArmPose = modelbiped$armpose2;
                model.leftArmPose = modelbiped$armpose;
            }
        }
        return model;
    }
}
