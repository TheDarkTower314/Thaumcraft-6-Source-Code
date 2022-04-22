// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.client;

import org.apache.logging.log4j.LogManager;
import thaumcraft.api.golems.IGolemProperties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemBlock;
import java.util.Iterator;
import thaumcraft.api.golems.IGolemAPI;
import java.awt.Color;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thaumcraft.api.golems.EnumGolemTrait;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.Entity;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.api.golems.ISealDisplayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import java.util.ArrayList;
import thaumcraft.api.golems.parts.PartModel;
import thaumcraft.client.lib.obj.IModelCustom;
import java.util.HashMap;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderBiped;

@SideOnly(Side.CLIENT)
public class RenderThaumcraftGolem extends RenderBiped
{
    private static final Logger logger;
    private HashMap<String, IModelCustom> models;
    private HashMap<Integer, HashMap<PartModel.EnumAttachPoint, ArrayList<PartModel>>> partsCache;
    private IModelCustom baseModel;
    float swingProgress;
    
    public RenderThaumcraftGolem(final RenderManager p_i46127_1_) {
        super(p_i46127_1_, new ModelBiped(), 0.3f);
        models = new HashMap<String, IModelCustom>();
        partsCache = new HashMap<Integer, HashMap<PartModel.EnumAttachPoint, ArrayList<PartModel>>>();
        swingProgress = 0.0f;
        layerRenderers.clear();
        baseModel = AdvancedModelLoader.loadModel(new ResourceLocation("thaumcraft", "models/obj/golem_base.obj"));
    }
    
    private void renderModel(final EntityThaumcraftGolem entity, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float partialTicks) {
        final boolean flag = !entity.isInvisible();
        final boolean flag2 = !flag && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);
        if (flag || flag2) {
            if (flag2) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 0.15f);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.alphaFunc(516, 0.003921569f);
            }
            renderParts(entity, p1, p2, p3, p4, p5, p6, partialTicks);
            if (flag2) {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1f);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }
        }
        final EntityPlayer player = Minecraft.getMinecraft().player;
        if (player.isSneaking() && ((player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ISealDisplayer) || (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof ISealDisplayer)) && !EntityUtils.canEntityBeSeen(player, entity)) {
            GlStateManager.pushMatrix();
            GlStateManager.color(0.25f, 0.25f, 0.25f, 0.25f);
            GlStateManager.depthMask(false);
            GL11.glDisable(2929);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569f);
            renderParts(entity, p1, p2, p3, p4, p5, p6, partialTicks);
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.popMatrix();
            GL11.glEnable(2929);
            GlStateManager.depthMask(true);
        }
    }
    
    private void renderParts(final EntityThaumcraftGolem entity, final float limbSwing, final float prevLimbSwing, final float rotFloat, final float headPitch, final float headYaw, final float p_78087_6_, final float partialTicks) {
        final ResourceLocation matTexture = entity.getProperties().getMaterial().texture;
        final boolean holding = !entity.getHeldItemMainhand().isEmpty();
        final boolean aflag = entity.getProperties().hasTrait(EnumGolemTrait.WHEELED) || entity.getProperties().hasTrait(EnumGolemTrait.FLYER);
        final Vec3d v1 = new Vec3d(entity.posX, 0.0, entity.posZ);
        final Vec3d v2 = new Vec3d(entity.prevPosX, 0.0, entity.prevPosZ);
        final double speed = v1.squareDistanceTo(v2);
        if (entity.redrawParts || !partsCache.containsKey(entity.getEntityId())) {
            entity.redrawParts = false;
            createPartsCache(entity);
        }
        float f1 = 0.0f;
        float bry = 0.0f;
        final float rx = (float)Math.toDegrees(MathHelper.sin(rotFloat * 0.067f) * 0.03f);
        final float rz = (float)Math.toDegrees(MathHelper.cos(rotFloat * 0.09f) * 0.05f + 0.05f);
        float rrx = 0.0f;
        float rry = 0.0f;
        float rrz = 0.0f;
        float rlx = 0.0f;
        float rly = 0.0f;
        float rlz = 0.0f;
        if (holding) {
            rrx = 90.0f - rz / 2.0f;
            rrz = -2.0f;
            rlx = 90.0f - rz / 2.0f;
            rlz = 2.0f;
        }
        else {
            if (aflag) {
                rrx = rx * 2.0f;
                rlx = -rx * 2.0f;
            }
            else {
                f1 = MathHelper.cos(limbSwing * 0.6662f + 3.1415927f) * 2.0f * prevLimbSwing * 0.5f;
                rrx = (float)(Math.toDegrees(f1) + rx);
                f1 = MathHelper.cos(limbSwing * 0.6662f) * 2.0f * prevLimbSwing * 0.5f;
                rlx = (float)(Math.toDegrees(f1) - rx);
            }
            rrz += rz + 2.0f;
            rlz -= rz + 2.0f;
        }
        if (swingProgress > 0.0f) {
            final float wiggle = -MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927f * 2.0f) * 0.2f;
            bry = (float)Math.toDegrees(wiggle);
            rrz = -(float)Math.toDegrees(MathHelper.sin(wiggle) * 3.0f);
            rrx = (float)Math.toDegrees(-MathHelper.cos(wiggle) * 5.0f);
            rry += bry;
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GlStateManager.rotate(bry, 0.0f, 1.0f, 0.0f);
        float lean = 25.0f;
        if (aflag) {
            lean = 75.0f;
        }
        GlStateManager.rotate((float)(speed * lean), -1.0f, 0.0f, 0.0f);
        GlStateManager.rotate((float)(speed * lean * 0.06 * (entity.rotationYaw - entity.prevRotationYaw)), 0.0f, 0.0f, -1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, 0.5, 0.0);
        bindTexture(matTexture);
        baseModel.renderPart("chest");
        baseModel.renderPart("waist");
        if (entity.getGolemColor() > 0) {
            final Color c = new Color(EnumDyeColor.byMetadata(entity.getGolemColor() - 1).getColorValue());
            GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
            baseModel.renderPart("flag");
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        for (final PartModel part : partsCache.get(entity.getEntityId()).get(PartModel.EnumAttachPoint.BODY)) {
            renderPart(entity, part.getObjModel().toString(), part, matTexture, partialTicks, PartModel.EnumLimbSide.MIDDLE);
        }
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, 0.75, -0.03125);
        GlStateManager.rotate(headPitch, 0.0f, -1.0f, 0.0f);
        GlStateManager.rotate(headYaw, -1.0f, 0.0f, 0.0f);
        for (final PartModel part : partsCache.get(entity.getEntityId()).get(PartModel.EnumAttachPoint.HEAD)) {
            renderPart(entity, part.getObjModel().toString(), part, matTexture, partialTicks, PartModel.EnumLimbSide.MIDDLE);
        }
        bindTexture(matTexture);
        baseModel.renderPart("head");
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.20625, 0.6875, 0.0);
        final Iterator iterator3 = ((ArrayList) partsCache.get(entity.getEntityId()).get(PartModel.EnumAttachPoint.ARMS)).iterator();
        if (iterator3.hasNext()) {
            final PartModel part = (PartModel)iterator3.next();
            rrx = part.preRenderArmRotationX(entity, partialTicks, PartModel.EnumLimbSide.RIGHT, rrx);
            rry = part.preRenderArmRotationY(entity, partialTicks, PartModel.EnumLimbSide.RIGHT, rry);
            rrz = part.preRenderArmRotationZ(entity, partialTicks, PartModel.EnumLimbSide.RIGHT, rrz);
        }
        GlStateManager.rotate(rrx, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(rry, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(rrz, 0.0f, 0.0f, 1.0f);
        bindTexture(matTexture);
        baseModel.renderPart("arm");
        final Iterator iterator4 = ((ArrayList) partsCache.get(entity.getEntityId()).get(PartModel.EnumAttachPoint.ARMS)).iterator();
        while (iterator4.hasNext()) {
            final PartModel part = (PartModel)iterator4.next();
            renderPart(entity, part.getObjModel().toString(), part, matTexture, partialTicks, PartModel.EnumLimbSide.RIGHT);
        }
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.20625, 0.6875, 0.0);
        final Iterator iterator5 = ((ArrayList) partsCache.get(entity.getEntityId()).get(PartModel.EnumAttachPoint.ARMS)).iterator();
        if (iterator5.hasNext()) {
            final PartModel part = (PartModel)iterator5.next();
            rlx = part.preRenderArmRotationX(entity, partialTicks, PartModel.EnumLimbSide.LEFT, rlx);
            rly = part.preRenderArmRotationY(entity, partialTicks, PartModel.EnumLimbSide.LEFT, rly);
            rlz = part.preRenderArmRotationZ(entity, partialTicks, PartModel.EnumLimbSide.LEFT, rlz);
        }
        GlStateManager.rotate(rlx, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(rly + 180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(rlz, 0.0f, 0.0f, -1.0f);
        bindTexture(matTexture);
        baseModel.renderPart("arm");
        final Iterator iterator6 = ((ArrayList) partsCache.get(entity.getEntityId()).get(PartModel.EnumAttachPoint.ARMS)).iterator();
        while (iterator6.hasNext()) {
            final PartModel part = (PartModel)iterator6.next();
            renderPart(entity, part.getObjModel().toString(), part, matTexture, partialTicks, PartModel.EnumLimbSide.LEFT);
        }
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.09375, 0.375, 0.0);
        f1 = MathHelper.cos(limbSwing * 0.6662f) * prevLimbSwing;
        GlStateManager.rotate((float)Math.toDegrees(f1), 1.0f, 0.0f, 0.0f);
        final Iterator iterator7 = ((ArrayList) partsCache.get(entity.getEntityId()).get(PartModel.EnumAttachPoint.LEGS)).iterator();
        while (iterator7.hasNext()) {
            final PartModel part = (PartModel)iterator7.next();
            renderPart(entity, part.getObjModel().toString(), part, matTexture, partialTicks, PartModel.EnumLimbSide.RIGHT);
        }
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.09375, 0.375, 0.0);
        f1 = MathHelper.cos(limbSwing * 0.6662f + 3.1415927f) * prevLimbSwing;
        GlStateManager.rotate((float)Math.toDegrees(f1), 1.0f, 0.0f, 0.0f);
        final Iterator iterator8 = ((ArrayList) partsCache.get(entity.getEntityId()).get(PartModel.EnumAttachPoint.LEGS)).iterator();
        while (iterator8.hasNext()) {
            final PartModel part = (PartModel)iterator8.next();
            renderPart(entity, part.getObjModel().toString(), part, matTexture, partialTicks, PartModel.EnumLimbSide.LEFT);
        }
        GlStateManager.popMatrix();
        GL11.glDisable(3042);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, 0.625, 0.0);
        GlStateManager.rotate(90.0f - rz * 0.5f, 1.0f, 0.0f, 0.0f);
        drawHeldItem(entity);
        GlStateManager.popMatrix();
    }
    
    private void drawHeldItem(final EntityThaumcraftGolem entity) {
        final ItemStack itemstack = entity.getHeldItemMainhand();
        if (itemstack != null && !itemstack.isEmpty()) {
            GlStateManager.pushMatrix();
            final Item item = itemstack.getItem();
            final Minecraft minecraft = Minecraft.getMinecraft();
            GlStateManager.rotate(90.0f, -1.0f, 0.0f, 0.0f);
            GlStateManager.scale(0.375, 0.375, 0.375);
            GlStateManager.translate(0.0f, 0.25f, -1.5f);
            if (!(item instanceof ItemBlock)) {
                GlStateManager.translate(0.0f, -0.6f, 0.0f);
            }
            minecraft.getItemRenderer().renderItem(entity, itemstack, ItemCameraTransforms.TransformType.HEAD);
            GlStateManager.popMatrix();
        }
    }
    
    private void renderPart(final EntityThaumcraftGolem golem, final String partName, final PartModel part, final ResourceLocation matTexture, final float partialTicks, final PartModel.EnumLimbSide side) {
        IModelCustom model = models.get(partName);
        if (model == null) {
            model = AdvancedModelLoader.loadModel(part.getObjModel());
            if (model == null) {
                return;
            }
            models.put(partName, model);
        }
        for (final String op : model.getPartNames()) {
            GL11.glPushMatrix();
            if (part.useMaterialTextureForObjectPart(op)) {
                bindTexture(matTexture);
            }
            else {
                bindTexture(part.getTexture());
            }
            part.preRenderObjectPart(op, golem, partialTicks, side);
            model.renderPart(op);
            part.postRenderObjectPart(op, golem, partialTicks, side);
            GL11.glPopMatrix();
        }
    }
    
    private void doRender(final EntityThaumcraftGolem entity, final double x, final double y, final double z, final float p_76986_8_, final float partialTicks) {
        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(entity, this, x, y, z))) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        swingProgress = getSwingProgress(entity, partialTicks);
        try {
            float f2 = interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            final float f3 = interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f4 = f3 - f2;
            if (entity.isRiding() && entity.getRidingEntity() instanceof EntityLivingBase) {
                final EntityLivingBase entitylivingbase1 = (EntityLivingBase)entity.getRidingEntity();
                f2 = interpolateRotation(entitylivingbase1.prevRenderYawOffset, entitylivingbase1.renderYawOffset, partialTicks);
                f4 = f3 - f2;
                float f5 = MathHelper.wrapDegrees(f4);
                if (f5 < -85.0f) {
                    f5 = -85.0f;
                }
                if (f5 >= 85.0f) {
                    f5 = 85.0f;
                }
                f2 = f3 - f5;
                if (f5 * f5 > 2500.0f) {
                    f2 += f5 * 0.2f;
                }
            }
            final float f6 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            renderLivingAt(entity, x, y, z);
            float f5 = handleRotationFloat(entity, partialTicks);
            applyRotations(entity, f5, f2, partialTicks);
            GlStateManager.enableRescaleNormal();
            preRenderCallback(entity, partialTicks);
            float f7 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            final float f8 = entity.limbSwing - entity.limbSwingAmount * (1.0f - partialTicks);
            if (f7 > 1.0f) {
                f7 = 1.0f;
            }
            GlStateManager.enableAlpha();
            if (renderOutlines) {
                final boolean flag = setScoreTeamColor(entity);
                renderModel(entity, f8, f7, f5, f4, f6, 0.0625f, partialTicks);
                if (flag) {
                    unsetScoreTeamColor();
                }
            }
            else {
                final boolean flag = setDoRenderBrightness(entity, partialTicks);
                renderModel(entity, f8, f7, f5, f4, f6, 0.0625f, partialTicks);
                if (flag) {
                    unsetBrightness();
                }
                GlStateManager.depthMask(true);
                renderLayers(entity, f8, f7, partialTicks, f5, f4, f6, 0.0625f);
            }
            GlStateManager.disableRescaleNormal();
        }
        catch (final Exception exception) {
            RenderThaumcraftGolem.logger.error("Couldn't render entity", exception);
        }
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        if (!renderOutlines) {
            renderName(entity, x, y, z);
        }
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(entity, this, x, y, z));
        renderLeash(entity, x, y, z, p_76986_8_, partialTicks);
    }
    
    private void createPartsCache(final EntityThaumcraftGolem golem) {
        final HashMap<PartModel.EnumAttachPoint, ArrayList<PartModel>> pl = new HashMap<PartModel.EnumAttachPoint, ArrayList<PartModel>>();
        pl.put(PartModel.EnumAttachPoint.BODY, new ArrayList<PartModel>());
        pl.put(PartModel.EnumAttachPoint.HEAD, new ArrayList<PartModel>());
        pl.put(PartModel.EnumAttachPoint.ARMS, new ArrayList<PartModel>());
        pl.put(PartModel.EnumAttachPoint.LEGS, new ArrayList<PartModel>());
        final IGolemProperties props = golem.getProperties();
        if (props.getHead().model != null) {
            pl.get(props.getHead().model.getAttachPoint()).add(props.getHead().model);
        }
        if (props.getArms().model != null) {
            pl.get(props.getArms().model.getAttachPoint()).add(props.getArms().model);
        }
        if (props.getLegs().model != null) {
            pl.get(props.getLegs().model.getAttachPoint()).add(props.getLegs().model);
        }
        if (props.getAddon().model != null) {
            pl.get(props.getAddon().model.getAttachPoint()).add(props.getAddon().model);
        }
        partsCache.put(golem.getEntityId(), pl);
    }
    
    public void doRender(final EntityLiving entity, final double p_76986_2_, final double p_76986_4_, final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        doRender((EntityThaumcraftGolem)entity, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
    
    protected ResourceLocation getEntityTexture(final EntityLiving p_110775_1_) {
        return null;
    }
    
    static {
        logger = LogManager.getLogger();
    }
}
