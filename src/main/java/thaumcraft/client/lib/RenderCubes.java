package thaumcraft.client.lib;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderCubes
{
    public IBlockAccess blockAccess;
    public boolean flipTexture;
    public boolean field_152631_f;
    public boolean renderAllFaces;
    public boolean useInventoryTint;
    public boolean renderFromInside;
    public double renderMinX;
    public double renderMaxX;
    public double renderMinY;
    public double renderMaxY;
    public double renderMinZ;
    public double renderMaxZ;
    public boolean lockBlockBounds;
    public boolean partialRenderBounds;
    public Minecraft minecraftRB;
    public int uvRotateEast;
    public int uvRotateWest;
    public int uvRotateSouth;
    public int uvRotateNorth;
    public int uvRotateTop;
    public int uvRotateBottom;
    public float aoLightValueScratchXYZNNN;
    public float aoLightValueScratchXYNN;
    public float aoLightValueScratchXYZNNP;
    public float aoLightValueScratchYZNN;
    public float aoLightValueScratchYZNP;
    public float aoLightValueScratchXYZPNN;
    public float aoLightValueScratchXYPN;
    public float aoLightValueScratchXYZPNP;
    public float aoLightValueScratchXYZNPN;
    public float aoLightValueScratchXYNP;
    public float aoLightValueScratchXYZNPP;
    public float aoLightValueScratchYZPN;
    public float aoLightValueScratchXYZPPN;
    public float aoLightValueScratchXYPP;
    public float aoLightValueScratchYZPP;
    public float aoLightValueScratchXYZPPP;
    public float aoLightValueScratchXZNN;
    public float aoLightValueScratchXZPN;
    public float aoLightValueScratchXZNP;
    public float aoLightValueScratchXZPP;
    public int aoBrightnessXYZNNN;
    public int aoBrightnessXYNN;
    public int aoBrightnessXYZNNP;
    public int aoBrightnessYZNN;
    public int aoBrightnessYZNP;
    public int aoBrightnessXYZPNN;
    public int aoBrightnessXYPN;
    public int aoBrightnessXYZPNP;
    public int aoBrightnessXYZNPN;
    public int aoBrightnessXYNP;
    public int aoBrightnessXYZNPP;
    public int aoBrightnessYZPN;
    public int aoBrightnessXYZPPN;
    public int aoBrightnessXYPP;
    public int aoBrightnessYZPP;
    public int aoBrightnessXYZPPP;
    public int aoBrightnessXZNN;
    public int aoBrightnessXZPN;
    public int aoBrightnessXZNP;
    public int aoBrightnessXZPP;
    public int brightnessTopLeft;
    public int brightnessBottomLeft;
    public int brightnessBottomRight;
    public int brightnessTopRight;
    public float colorRedTopLeft;
    public float colorRedBottomLeft;
    public float colorRedBottomRight;
    public float colorRedTopRight;
    public float colorGreenTopLeft;
    public float colorGreenBottomLeft;
    public float colorGreenBottomRight;
    public float colorGreenTopRight;
    public float colorBlueTopLeft;
    public float colorBlueBottomLeft;
    public float colorBlueBottomRight;
    public float colorBlueTopRight;
    private static String __OBFID = "CL_00000940";
    private static RenderCubes instance;
    
    public RenderCubes(IBlockAccess p_i1251_1_) {
        useInventoryTint = true;
        renderFromInside = false;
        blockAccess = p_i1251_1_;
        field_152631_f = false;
        flipTexture = false;
        minecraftRB = Minecraft.getMinecraft();
    }
    
    public RenderCubes() {
        useInventoryTint = true;
        renderFromInside = false;
        minecraftRB = Minecraft.getMinecraft();
    }
    
    public void setRenderBounds(double p_147782_1_, double p_147782_3_, double p_147782_5_, double p_147782_7_, double p_147782_9_, double p_147782_11_) {
        if (!lockBlockBounds) {
            renderMinX = p_147782_1_;
            renderMaxX = p_147782_7_;
            renderMinY = p_147782_3_;
            renderMaxY = p_147782_9_;
            renderMinZ = p_147782_5_;
            renderMaxZ = p_147782_11_;
            partialRenderBounds = (minecraftRB.gameSettings.ambientOcclusion >= 2 && (renderMinX > 0.0 || renderMaxX < 1.0 || renderMinY > 0.0 || renderMaxY < 1.0 || renderMinZ > 0.0 || renderMaxZ < 1.0));
        }
    }
    
    public void overrideBlockBounds(double p_147770_1_, double p_147770_3_, double p_147770_5_, double p_147770_7_, double p_147770_9_, double p_147770_11_) {
        renderMinX = p_147770_1_;
        renderMaxX = p_147770_7_;
        renderMinY = p_147770_3_;
        renderMaxY = p_147770_9_;
        renderMinZ = p_147770_5_;
        renderMaxZ = p_147770_11_;
        lockBlockBounds = true;
        partialRenderBounds = (minecraftRB.gameSettings.ambientOcclusion >= 2 && (renderMinX > 0.0 || renderMaxX < 1.0 || renderMinY > 0.0 || renderMaxY < 1.0 || renderMinZ > 0.0 || renderMaxZ < 1.0));
    }
    
    public void renderFaceYNeg(Block p_147768_1_, double p_147768_2_, double p_147768_4_, double p_147768_6_, TextureAtlasSprite p_147768_8_, float red, float green, float blue, int bright) {
        Tessellator tessellator = Tessellator.getInstance();
        double d3 = p_147768_8_.getInterpolatedU(renderMinX * 16.0);
        double d4 = p_147768_8_.getInterpolatedU(renderMaxX * 16.0);
        double d5 = p_147768_8_.getInterpolatedV(renderMinZ * 16.0);
        double d6 = p_147768_8_.getInterpolatedV(renderMaxZ * 16.0);
        if (renderMinX < 0.0 || renderMaxX > 1.0) {
            d3 = p_147768_8_.getMinU();
            d4 = p_147768_8_.getMaxU();
        }
        if (renderMinZ < 0.0 || renderMaxZ > 1.0) {
            d5 = p_147768_8_.getMinV();
            d6 = p_147768_8_.getMaxV();
        }
        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;
        if (uvRotateBottom == 2) {
            d3 = p_147768_8_.getInterpolatedU(renderMinZ * 16.0);
            d5 = p_147768_8_.getInterpolatedV(16.0 - renderMaxX * 16.0);
            d4 = p_147768_8_.getInterpolatedU(renderMaxZ * 16.0);
            d6 = p_147768_8_.getInterpolatedV(16.0 - renderMinX * 16.0);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (uvRotateBottom == 1) {
            d3 = p_147768_8_.getInterpolatedU(16.0 - renderMaxZ * 16.0);
            d5 = p_147768_8_.getInterpolatedV(renderMinX * 16.0);
            d4 = p_147768_8_.getInterpolatedU(16.0 - renderMinZ * 16.0);
            d6 = p_147768_8_.getInterpolatedV(renderMaxX * 16.0);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (uvRotateBottom == 3) {
            d3 = p_147768_8_.getInterpolatedU(16.0 - renderMinX * 16.0);
            d4 = p_147768_8_.getInterpolatedU(16.0 - renderMaxX * 16.0);
            d5 = p_147768_8_.getInterpolatedV(16.0 - renderMinZ * 16.0);
            d6 = p_147768_8_.getInterpolatedV(16.0 - renderMaxZ * 16.0);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }
        double d11 = p_147768_2_ + renderMinX;
        double d12 = p_147768_2_ + renderMaxX;
        double d13 = p_147768_4_ + renderMinY;
        double d14 = p_147768_6_ + renderMinZ;
        double d15 = p_147768_6_ + renderMaxZ;
        if (renderFromInside) {
            d11 = p_147768_2_ + renderMaxX;
            d12 = p_147768_2_ + renderMinX;
        }
        int i = bright;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        tessellator.getBuffer().pos(d11, d13, d15).tex(d8, d10).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d11, d13, d14).tex(d3, d5).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d12, d13, d14).tex(d7, d9).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d12, d13, d15).tex(d4, d6).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
    }
    
    public void renderFaceYPos(Block p_147806_1_, double p_147806_2_, double p_147806_4_, double p_147806_6_, TextureAtlasSprite p_147806_8_, float red, float green, float blue, int bright) {
        Tessellator tessellator = Tessellator.getInstance();
        double d3 = p_147806_8_.getInterpolatedU(renderMinX * 16.0);
        double d4 = p_147806_8_.getInterpolatedU(renderMaxX * 16.0);
        double d5 = p_147806_8_.getInterpolatedV(renderMinZ * 16.0);
        double d6 = p_147806_8_.getInterpolatedV(renderMaxZ * 16.0);
        if (renderMinX < 0.0 || renderMaxX > 1.0) {
            d3 = p_147806_8_.getMinU();
            d4 = p_147806_8_.getMaxU();
        }
        if (renderMinZ < 0.0 || renderMaxZ > 1.0) {
            d5 = p_147806_8_.getMinV();
            d6 = p_147806_8_.getMaxV();
        }
        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;
        if (uvRotateTop == 1) {
            d3 = p_147806_8_.getInterpolatedU(renderMinZ * 16.0);
            d5 = p_147806_8_.getInterpolatedV(16.0 - renderMaxX * 16.0);
            d4 = p_147806_8_.getInterpolatedU(renderMaxZ * 16.0);
            d6 = p_147806_8_.getInterpolatedV(16.0 - renderMinX * 16.0);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (uvRotateTop == 2) {
            d3 = p_147806_8_.getInterpolatedU(16.0 - renderMaxZ * 16.0);
            d5 = p_147806_8_.getInterpolatedV(renderMinX * 16.0);
            d4 = p_147806_8_.getInterpolatedU(16.0 - renderMinZ * 16.0);
            d6 = p_147806_8_.getInterpolatedV(renderMaxX * 16.0);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (uvRotateTop == 3) {
            d3 = p_147806_8_.getInterpolatedU(16.0 - renderMinX * 16.0);
            d4 = p_147806_8_.getInterpolatedU(16.0 - renderMaxX * 16.0);
            d5 = p_147806_8_.getInterpolatedV(16.0 - renderMinZ * 16.0);
            d6 = p_147806_8_.getInterpolatedV(16.0 - renderMaxZ * 16.0);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }
        double d11 = p_147806_2_ + renderMinX;
        double d12 = p_147806_2_ + renderMaxX;
        double d13 = p_147806_4_ + renderMaxY;
        double d14 = p_147806_6_ + renderMinZ;
        double d15 = p_147806_6_ + renderMaxZ;
        if (renderFromInside) {
            d11 = p_147806_2_ + renderMaxX;
            d12 = p_147806_2_ + renderMinX;
        }
        int i = bright;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        tessellator.getBuffer().pos(d12, d13, d15).tex(d4, d6).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d12, d13, d14).tex(d7, d9).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d11, d13, d14).tex(d3, d5).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d11, d13, d15).tex(d8, d10).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
    }
    
    public void renderFaceZNeg(Block p_147761_1_, double p_147761_2_, double p_147761_4_, double p_147761_6_, TextureAtlasSprite p_147761_8_, float red, float green, float blue, int bright) {
        Tessellator tessellator = Tessellator.getInstance();
        double d3 = p_147761_8_.getInterpolatedU(renderMinX * 16.0);
        double d4 = p_147761_8_.getInterpolatedU(renderMaxX * 16.0);
        if (field_152631_f) {
            d4 = p_147761_8_.getInterpolatedU((1.0 - renderMinX) * 16.0);
            d3 = p_147761_8_.getInterpolatedU((1.0 - renderMaxX) * 16.0);
        }
        double d5 = p_147761_8_.getInterpolatedV(16.0 - renderMaxY * 16.0);
        double d6 = p_147761_8_.getInterpolatedV(16.0 - renderMinY * 16.0);
        if (flipTexture) {
            double d7 = d3;
            d3 = d4;
            d4 = d7;
        }
        if (renderMinX < 0.0 || renderMaxX > 1.0) {
            d3 = p_147761_8_.getMinU();
            d4 = p_147761_8_.getMaxU();
        }
        if (renderMinY < 0.0 || renderMaxY > 1.0) {
            d5 = p_147761_8_.getMinV();
            d6 = p_147761_8_.getMaxV();
        }
        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;
        if (uvRotateEast == 2) {
            d3 = p_147761_8_.getInterpolatedU(renderMinY * 16.0);
            d4 = p_147761_8_.getInterpolatedU(renderMaxY * 16.0);
            d5 = p_147761_8_.getInterpolatedV(16.0 - renderMinX * 16.0);
            d6 = p_147761_8_.getInterpolatedV(16.0 - renderMaxX * 16.0);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (uvRotateEast == 1) {
            d3 = p_147761_8_.getInterpolatedU(16.0 - renderMaxY * 16.0);
            d4 = p_147761_8_.getInterpolatedU(16.0 - renderMinY * 16.0);
            d5 = p_147761_8_.getInterpolatedV(renderMaxX * 16.0);
            d6 = p_147761_8_.getInterpolatedV(renderMinX * 16.0);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (uvRotateEast == 3) {
            d3 = p_147761_8_.getInterpolatedU(16.0 - renderMinX * 16.0);
            d4 = p_147761_8_.getInterpolatedU(16.0 - renderMaxX * 16.0);
            d5 = p_147761_8_.getInterpolatedV(renderMaxY * 16.0);
            d6 = p_147761_8_.getInterpolatedV(renderMinY * 16.0);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }
        double d11 = p_147761_2_ + renderMinX;
        double d12 = p_147761_2_ + renderMaxX;
        double d13 = p_147761_4_ + renderMinY;
        double d14 = p_147761_4_ + renderMaxY;
        double d15 = p_147761_6_ + renderMinZ;
        if (renderFromInside) {
            d11 = p_147761_2_ + renderMaxX;
            d12 = p_147761_2_ + renderMinX;
        }
        int i = bright;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        tessellator.getBuffer().pos(d11, d14, d15).tex(d7, d9).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d12, d14, d15).tex(d3, d5).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d12, d13, d15).tex(d8, d10).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d11, d13, d15).tex(d4, d6).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
    }
    
    public void renderFaceZPos(Block p_147734_1_, double p_147734_2_, double p_147734_4_, double p_147734_6_, TextureAtlasSprite p_147734_8_, float red, float green, float blue, int bright) {
        Tessellator tessellator = Tessellator.getInstance();
        double d3 = p_147734_8_.getInterpolatedU(renderMinX * 16.0);
        double d4 = p_147734_8_.getInterpolatedU(renderMaxX * 16.0);
        double d5 = p_147734_8_.getInterpolatedV(16.0 - renderMaxY * 16.0);
        double d6 = p_147734_8_.getInterpolatedV(16.0 - renderMinY * 16.0);
        if (flipTexture) {
            double d7 = d3;
            d3 = d4;
            d4 = d7;
        }
        if (renderMinX < 0.0 || renderMaxX > 1.0) {
            d3 = p_147734_8_.getMinU();
            d4 = p_147734_8_.getMaxU();
        }
        if (renderMinY < 0.0 || renderMaxY > 1.0) {
            d5 = p_147734_8_.getMinV();
            d6 = p_147734_8_.getMaxV();
        }
        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;
        if (uvRotateWest == 1) {
            d3 = p_147734_8_.getInterpolatedU(renderMinY * 16.0);
            d6 = p_147734_8_.getInterpolatedV(16.0 - renderMinX * 16.0);
            d4 = p_147734_8_.getInterpolatedU(renderMaxY * 16.0);
            d5 = (d9 = p_147734_8_.getInterpolatedV(16.0 - renderMaxX * 16.0));
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (uvRotateWest == 2) {
            d3 = p_147734_8_.getInterpolatedU(16.0 - renderMaxY * 16.0);
            d5 = p_147734_8_.getInterpolatedV(renderMinX * 16.0);
            d4 = p_147734_8_.getInterpolatedU(16.0 - renderMinY * 16.0);
            d6 = p_147734_8_.getInterpolatedV(renderMaxX * 16.0);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (uvRotateWest == 3) {
            d3 = p_147734_8_.getInterpolatedU(16.0 - renderMinX * 16.0);
            d4 = p_147734_8_.getInterpolatedU(16.0 - renderMaxX * 16.0);
            d5 = p_147734_8_.getInterpolatedV(renderMaxY * 16.0);
            d6 = p_147734_8_.getInterpolatedV(renderMinY * 16.0);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }
        double d11 = p_147734_2_ + renderMinX;
        double d12 = p_147734_2_ + renderMaxX;
        double d13 = p_147734_4_ + renderMinY;
        double d14 = p_147734_4_ + renderMaxY;
        double d15 = p_147734_6_ + renderMaxZ;
        if (renderFromInside) {
            d11 = p_147734_2_ + renderMaxX;
            d12 = p_147734_2_ + renderMinX;
        }
        int i = bright;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        tessellator.getBuffer().pos(d11, d14, d15).tex(d3, d5).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d11, d13, d15).tex(d8, d10).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d12, d13, d15).tex(d4, d6).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d12, d14, d15).tex(d7, d9).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
    }
    
    public void renderFaceXNeg(Block p_147798_1_, double p_147798_2_, double p_147798_4_, double p_147798_6_, TextureAtlasSprite p_147798_8_, float red, float green, float blue, int bright) {
        Tessellator tessellator = Tessellator.getInstance();
        double d3 = p_147798_8_.getInterpolatedU(renderMinZ * 16.0);
        double d4 = p_147798_8_.getInterpolatedU(renderMaxZ * 16.0);
        double d5 = p_147798_8_.getInterpolatedV(16.0 - renderMaxY * 16.0);
        double d6 = p_147798_8_.getInterpolatedV(16.0 - renderMinY * 16.0);
        if (flipTexture) {
            double d7 = d3;
            d3 = d4;
            d4 = d7;
        }
        if (renderMinZ < 0.0 || renderMaxZ > 1.0) {
            d3 = p_147798_8_.getMinU();
            d4 = p_147798_8_.getMaxU();
        }
        if (renderMinY < 0.0 || renderMaxY > 1.0) {
            d5 = p_147798_8_.getMinV();
            d6 = p_147798_8_.getMaxV();
        }
        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;
        if (uvRotateNorth == 1) {
            d3 = p_147798_8_.getInterpolatedU(renderMinY * 16.0);
            d5 = p_147798_8_.getInterpolatedV(16.0 - renderMaxZ * 16.0);
            d4 = p_147798_8_.getInterpolatedU(renderMaxY * 16.0);
            d6 = p_147798_8_.getInterpolatedV(16.0 - renderMinZ * 16.0);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (uvRotateNorth == 2) {
            d3 = p_147798_8_.getInterpolatedU(16.0 - renderMaxY * 16.0);
            d5 = p_147798_8_.getInterpolatedV(renderMinZ * 16.0);
            d4 = p_147798_8_.getInterpolatedU(16.0 - renderMinY * 16.0);
            d6 = p_147798_8_.getInterpolatedV(renderMaxZ * 16.0);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (uvRotateNorth == 3) {
            d3 = p_147798_8_.getInterpolatedU(16.0 - renderMinZ * 16.0);
            d4 = p_147798_8_.getInterpolatedU(16.0 - renderMaxZ * 16.0);
            d5 = p_147798_8_.getInterpolatedV(renderMaxY * 16.0);
            d6 = p_147798_8_.getInterpolatedV(renderMinY * 16.0);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }
        double d11 = p_147798_2_ + renderMinX;
        double d12 = p_147798_4_ + renderMinY;
        double d13 = p_147798_4_ + renderMaxY;
        double d14 = p_147798_6_ + renderMinZ;
        double d15 = p_147798_6_ + renderMaxZ;
        if (renderFromInside) {
            d14 = p_147798_6_ + renderMaxZ;
            d15 = p_147798_6_ + renderMinZ;
        }
        int i = bright;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        tessellator.getBuffer().pos(d11, d13, d15).tex(d7, d9).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d11, d13, d14).tex(d3, d5).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d11, d12, d14).tex(d8, d10).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d11, d12, d15).tex(d4, d6).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
    }
    
    public void renderFaceXPos(Block p_147764_1_, double p_147764_2_, double p_147764_4_, double p_147764_6_, TextureAtlasSprite p_147764_8_, float red, float green, float blue, int bright) {
        Tessellator tessellator = Tessellator.getInstance();
        double d3 = p_147764_8_.getInterpolatedU(renderMinZ * 16.0);
        double d4 = p_147764_8_.getInterpolatedU(renderMaxZ * 16.0);
        if (field_152631_f) {
            d4 = p_147764_8_.getInterpolatedU((1.0 - renderMinZ) * 16.0);
            d3 = p_147764_8_.getInterpolatedU((1.0 - renderMaxZ) * 16.0);
        }
        double d5 = p_147764_8_.getInterpolatedV(16.0 - renderMaxY * 16.0);
        double d6 = p_147764_8_.getInterpolatedV(16.0 - renderMinY * 16.0);
        if (flipTexture) {
            double d7 = d3;
            d3 = d4;
            d4 = d7;
        }
        if (renderMinZ < 0.0 || renderMaxZ > 1.0) {
            d3 = p_147764_8_.getMinU();
            d4 = p_147764_8_.getMaxU();
        }
        if (renderMinY < 0.0 || renderMaxY > 1.0) {
            d5 = p_147764_8_.getMinV();
            d6 = p_147764_8_.getMaxV();
        }
        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;
        if (uvRotateSouth == 2) {
            d3 = p_147764_8_.getInterpolatedU(renderMinY * 16.0);
            d5 = p_147764_8_.getInterpolatedV(16.0 - renderMinZ * 16.0);
            d4 = p_147764_8_.getInterpolatedU(renderMaxY * 16.0);
            d6 = p_147764_8_.getInterpolatedV(16.0 - renderMaxZ * 16.0);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (uvRotateSouth == 1) {
            d3 = p_147764_8_.getInterpolatedU(16.0 - renderMaxY * 16.0);
            d5 = p_147764_8_.getInterpolatedV(renderMaxZ * 16.0);
            d4 = p_147764_8_.getInterpolatedU(16.0 - renderMinY * 16.0);
            d6 = p_147764_8_.getInterpolatedV(renderMinZ * 16.0);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (uvRotateSouth == 3) {
            d3 = p_147764_8_.getInterpolatedU(16.0 - renderMinZ * 16.0);
            d4 = p_147764_8_.getInterpolatedU(16.0 - renderMaxZ * 16.0);
            d5 = p_147764_8_.getInterpolatedV(renderMaxY * 16.0);
            d6 = p_147764_8_.getInterpolatedV(renderMinY * 16.0);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }
        double d11 = p_147764_2_ + renderMaxX;
        double d12 = p_147764_4_ + renderMinY;
        double d13 = p_147764_4_ + renderMaxY;
        double d14 = p_147764_6_ + renderMinZ;
        double d15 = p_147764_6_ + renderMaxZ;
        if (renderFromInside) {
            d14 = p_147764_6_ + renderMaxZ;
            d15 = p_147764_6_ + renderMinZ;
        }
        int i = bright;
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        tessellator.getBuffer().pos(d11, d12, d15).tex(d8, d10).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d11, d12, d14).tex(d4, d6).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d11, d13, d14).tex(d7, d9).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
        tessellator.getBuffer().pos(d11, d13, d15).tex(d3, d5).lightmap(j, k).color(red, green, blue, 1.0f).endVertex();
    }
    
    public static RenderCubes getInstance() {
        if (RenderCubes.instance == null) {
            RenderCubes.instance = new RenderCubes();
        }
        return RenderCubes.instance;
    }
}
