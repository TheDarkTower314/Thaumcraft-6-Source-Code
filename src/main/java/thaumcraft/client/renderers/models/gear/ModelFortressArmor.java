// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.models.gear;

import org.lwjgl.opengl.GL11;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.armor.ItemFortressArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import java.util.HashMap;
import net.minecraft.client.model.ModelRenderer;

public class ModelFortressArmor extends ModelCustomArmor
{
    ModelRenderer OrnamentL;
    ModelRenderer OrnamentL2;
    ModelRenderer OrnamentR;
    ModelRenderer OrnamentR2;
    ModelRenderer Helmet;
    ModelRenderer HelmetR;
    ModelRenderer HelmetL;
    ModelRenderer HelmetB;
    ModelRenderer capsthingy;
    ModelRenderer flapR;
    ModelRenderer flapL;
    ModelRenderer Gemornament;
    ModelRenderer Gem;
    ModelRenderer[] Mask;
    ModelRenderer Goggles;
    ModelRenderer BeltR;
    ModelRenderer Mbelt;
    ModelRenderer MbeltL;
    ModelRenderer MbeltR;
    ModelRenderer BeltL;
    ModelRenderer Chestplate;
    ModelRenderer Scroll;
    ModelRenderer Backplate;
    ModelRenderer Book;
    ModelRenderer ShoulderR;
    ModelRenderer GauntletR;
    ModelRenderer GauntletstrapR1;
    ModelRenderer GauntletstrapR2;
    ModelRenderer ShoulderplateRtop;
    ModelRenderer ShoulderplateR1;
    ModelRenderer ShoulderplateR2;
    ModelRenderer ShoulderplateR3;
    ModelRenderer ShoulderL;
    ModelRenderer GauntletL;
    ModelRenderer Gauntletstrapl1;
    ModelRenderer GauntletstrapL2;
    ModelRenderer ShoulderplateLtop;
    ModelRenderer ShoulderplateL1;
    ModelRenderer ShoulderplateL2;
    ModelRenderer ShoulderplateL3;
    ModelRenderer LegpanelR1;
    ModelRenderer LegpanelR2;
    ModelRenderer LegpanelR3;
    ModelRenderer LegpanelR4;
    ModelRenderer LegpanelR5;
    ModelRenderer LegpanelR6;
    ModelRenderer SidepanelR1;
    ModelRenderer SidepanelR2;
    ModelRenderer SidepanelR3;
    ModelRenderer BackpanelR1;
    ModelRenderer BackpanelR2;
    ModelRenderer BackpanelR3;
    ModelRenderer BackpanelL3;
    ModelRenderer LegpanelL1;
    ModelRenderer LegpanelL2;
    ModelRenderer LegpanelL3;
    ModelRenderer LegpanelL4;
    ModelRenderer LegpanelL5;
    ModelRenderer LegpanelL6;
    ModelRenderer SidepanelL1;
    ModelRenderer SidepanelL2;
    ModelRenderer SidepanelL3;
    ModelRenderer BackpanelL1;
    ModelRenderer BackpanelL2;
    private static HashMap<Integer, Integer> hasSet;
    private static HashMap<Integer, Integer> hasMask;
    private static HashMap<Integer, Boolean> hasGoggles;
    
    public ModelFortressArmor(final float f) {
        super(f, 0, 128, 64);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.Mask = new ModelRenderer[3];
        for (int a = 0; a < 3; ++a) {
            (this.Mask[a] = new ModelRenderer(this, 52 + a * 24, 2)).addBox(-4.5f, -5.0f, -4.6f, 9, 5, 1);
            this.Mask[a].setRotationPoint(0.0f, 0.0f, 0.0f);
            this.Mask[a].setTextureSize(128, 64);
            this.setRotation(this.Mask[a], 0.0f, 0.0f, 0.0f);
        }
        (this.Goggles = new ModelRenderer(this, 100, 18)).addBox(-4.5f, -5.0f, -4.25f, 9, 5, 1);
        this.Goggles.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Goggles.setTextureSize(128, 64);
        this.setRotation(this.Goggles, 0.0f, 0.0f, 0.0f);
        this.OrnamentL = new ModelRenderer(this, 78, 8);
        this.OrnamentL.mirror = true;
        this.OrnamentL.addBox(1.5f, -9.0f, -6.5f, 2, 2, 1);
        this.OrnamentL.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.OrnamentL.setTextureSize(128, 64);
        this.setRotation(this.OrnamentL, -0.1396263f, 0.0f, 0.0f);
        this.OrnamentL2 = new ModelRenderer(this, 78, 8);
        this.OrnamentL2.mirror = true;
        this.OrnamentL2.addBox(3.5f, -10.0f, -6.5f, 1, 2, 1);
        this.OrnamentL2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.OrnamentL2.setTextureSize(128, 64);
        this.setRotation(this.OrnamentL2, -0.1396263f, 0.0f, 0.0f);
        (this.OrnamentR = new ModelRenderer(this, 78, 8)).addBox(-3.5f, -9.0f, -6.5f, 2, 2, 1);
        this.OrnamentR.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.OrnamentR.setTextureSize(128, 64);
        this.setRotation(this.OrnamentR, -0.1396263f, 0.0f, 0.0f);
        (this.OrnamentR2 = new ModelRenderer(this, 78, 8)).addBox(-4.5f, -10.0f, -6.5f, 1, 2, 1);
        this.OrnamentR2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.OrnamentR2.setTextureSize(128, 64);
        this.setRotation(this.OrnamentR2, -0.1396263f, 0.0f, 0.0f);
        (this.Helmet = new ModelRenderer(this, 41, 8)).addBox(-4.5f, -9.0f, -4.5f, 9, 4, 9);
        this.Helmet.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Helmet.setTextureSize(128, 64);
        this.setRotation(this.Helmet, 0.0f, 0.0f, 0.0f);
        (this.HelmetR = new ModelRenderer(this, 21, 13)).addBox(-6.5f, -3.0f, -4.5f, 1, 5, 9);
        this.HelmetR.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.HelmetR.setTextureSize(128, 64);
        this.setRotation(this.HelmetR, 0.0f, 0.0f, 0.5235988f);
        this.HelmetL = new ModelRenderer(this, 21, 13);
        this.HelmetL.mirror = true;
        this.HelmetL.addBox(5.5f, -3.0f, -4.5f, 1, 5, 9);
        this.HelmetL.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.HelmetL.setTextureSize(128, 64);
        this.setRotation(this.HelmetL, 0.0f, 0.0f, -0.5235988f);
        (this.HelmetB = new ModelRenderer(this, 41, 21)).addBox(-4.5f, -3.0f, 5.5f, 9, 5, 1);
        this.HelmetB.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.HelmetB.setTextureSize(128, 64);
        this.setRotation(this.HelmetB, 0.5235988f, 0.0f, 0.0f);
        (this.capsthingy = new ModelRenderer(this, 21, 0)).addBox(-4.5f, -6.0f, -6.5f, 9, 1, 2);
        this.capsthingy.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.capsthingy.setTextureSize(128, 64);
        this.setRotation(this.capsthingy, 0.0f, 0.0f, 0.0f);
        (this.flapR = new ModelRenderer(this, 59, 10)).addBox(-10.0f, -2.0f, -1.0f, 3, 3, 1);
        this.flapR.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.flapR.setTextureSize(128, 64);
        this.setRotation(this.flapR, 0.0f, -0.5235988f, 0.5235988f);
        this.flapL = new ModelRenderer(this, 59, 10);
        this.flapL.mirror = true;
        this.flapL.addBox(7.0f, -2.0f, -1.0f, 3, 3, 1);
        this.flapL.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.flapL.setTextureSize(128, 64);
        this.setRotation(this.flapL, 0.0f, 0.5235988f, -0.5235988f);
        (this.Gemornament = new ModelRenderer(this, 68, 11)).addBox(-1.5f, -9.0f, -7.0f, 3, 3, 2);
        this.Gemornament.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Gemornament.setTextureSize(128, 64);
        this.setRotation(this.Gemornament, -0.1396263f, 0.0f, 0.0f);
        (this.Gem = new ModelRenderer(this, 72, 8)).addBox(-1.0f, -8.5f, -7.5f, 2, 2, 1);
        this.Gem.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Gem.setTextureSize(128, 64);
        this.setRotation(this.Gem, -0.1396263f, 0.0f, 0.0f);
        (this.BeltR = new ModelRenderer(this, 76, 44)).addBox(-5.0f, 4.0f, -3.0f, 1, 3, 6);
        this.BeltR.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.BeltR.setTextureSize(128, 64);
        this.setRotation(this.BeltR, 0.0f, 0.0f, 0.0f);
        (this.Mbelt = new ModelRenderer(this, 56, 55)).addBox(-4.0f, 8.0f, -3.0f, 8, 4, 1);
        this.Mbelt.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Mbelt.setTextureSize(128, 64);
        this.setRotation(this.Mbelt, 0.0f, 0.0f, 0.0f);
        (this.MbeltL = new ModelRenderer(this, 76, 44)).addBox(4.0f, 8.0f, -3.0f, 1, 3, 6);
        this.MbeltL.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.MbeltL.setTextureSize(128, 64);
        this.setRotation(this.MbeltL, 0.0f, 0.0f, 0.0f);
        (this.MbeltR = new ModelRenderer(this, 76, 44)).addBox(-5.0f, 8.0f, -3.0f, 1, 3, 6);
        this.MbeltR.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.MbeltR.setTextureSize(128, 64);
        this.setRotation(this.MbeltR, 0.0f, 0.0f, 0.0f);
        (this.BeltL = new ModelRenderer(this, 76, 44)).addBox(4.0f, 4.0f, -3.0f, 1, 3, 6);
        this.BeltL.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.BeltL.setTextureSize(128, 64);
        this.setRotation(this.BeltL, 0.0f, 0.0f, 0.0f);
        (this.Chestplate = new ModelRenderer(this, 56, 45)).addBox(-4.0f, 1.0f, -4.0f, 8, 7, 2);
        this.Chestplate.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Chestplate.setTextureSize(128, 64);
        this.setRotation(this.Chestplate, 0.0f, 0.0f, 0.0f);
        (this.Scroll = new ModelRenderer(this, 34, 27)).addBox(-2.0f, 9.5f, 4.0f, 8, 3, 3);
        this.Scroll.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Scroll.setTextureSize(128, 64);
        this.setRotation(this.Scroll, 0.0f, 0.0f, 0.1919862f);
        (this.Backplate = new ModelRenderer(this, 36, 45)).addBox(-4.0f, 1.0f, 2.0f, 8, 11, 2);
        this.Backplate.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Backplate.setTextureSize(128, 64);
        this.setRotation(this.Backplate, 0.0f, 0.0f, 0.0f);
        (this.Book = new ModelRenderer(this, 100, 8)).addBox(1.0f, -0.3f, 4.0f, 5, 7, 2);
        this.Book.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Book.setTextureSize(128, 64);
        this.setRotation(this.Book, 0.0f, 0.0f, 0.7679449f);
        (this.ShoulderR = new ModelRenderer(this, 56, 35)).addBox(-3.5f, -2.5f, -2.5f, 5, 5, 5);
        this.ShoulderR.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ShoulderR.setTextureSize(128, 64);
        this.setRotation(this.ShoulderR, 0.0f, 0.0f, 0.0f);
        (this.GauntletR = new ModelRenderer(this, 100, 26)).addBox(-3.5f, 3.5f, -2.5f, 2, 6, 5);
        this.GauntletR.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.GauntletR.setTextureSize(128, 64);
        this.setRotation(this.GauntletR, 0.0f, 0.0f, 0.0f);
        (this.GauntletstrapR1 = new ModelRenderer(this, 84, 31)).addBox(-1.5f, 3.5f, -2.5f, 3, 1, 5);
        this.GauntletstrapR1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.GauntletstrapR1.setTextureSize(128, 64);
        this.setRotation(this.GauntletstrapR1, 0.0f, 0.0f, 0.0f);
        (this.GauntletstrapR2 = new ModelRenderer(this, 84, 31)).addBox(-1.5f, 6.5f, -2.5f, 3, 1, 5);
        this.GauntletstrapR2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.GauntletstrapR2.setTextureSize(128, 64);
        this.setRotation(this.GauntletstrapR2, 0.0f, 0.0f, 0.0f);
        (this.ShoulderplateRtop = new ModelRenderer(this, 110, 37)).addBox(-5.5f, -2.5f, -3.5f, 2, 1, 7);
        this.ShoulderplateRtop.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ShoulderplateRtop.setTextureSize(128, 64);
        this.setRotation(this.ShoulderplateRtop, 0.0f, 0.0f, 0.4363323f);
        (this.ShoulderplateR1 = new ModelRenderer(this, 110, 45)).addBox(-4.5f, -1.5f, -3.5f, 1, 4, 7);
        this.ShoulderplateR1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ShoulderplateR1.setTextureSize(128, 64);
        this.setRotation(this.ShoulderplateR1, 0.0f, 0.0f, 0.4363323f);
        (this.ShoulderplateR2 = new ModelRenderer(this, 94, 45)).addBox(-3.5f, 1.5f, -3.5f, 1, 3, 7);
        this.ShoulderplateR2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ShoulderplateR2.setTextureSize(128, 64);
        this.setRotation(this.ShoulderplateR2, 0.0f, 0.0f, 0.4363323f);
        (this.ShoulderplateR3 = new ModelRenderer(this, 94, 45)).addBox(-2.5f, 3.5f, -3.5f, 1, 3, 7);
        this.ShoulderplateR3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ShoulderplateR3.setTextureSize(128, 64);
        this.setRotation(this.ShoulderplateR3, 0.0f, 0.0f, 0.4363323f);
        this.ShoulderL = new ModelRenderer(this, 56, 35);
        this.ShoulderL.mirror = true;
        this.ShoulderL.addBox(-1.5f, -2.5f, -2.5f, 5, 5, 5);
        this.ShoulderL.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ShoulderL.setTextureSize(128, 64);
        this.setRotation(this.ShoulderL, 0.0f, 0.0f, 0.0f);
        (this.GauntletL = new ModelRenderer(this, 114, 26)).addBox(1.5f, 3.5f, -2.5f, 2, 6, 5);
        this.GauntletL.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.GauntletL.setTextureSize(128, 64);
        this.setRotation(this.GauntletL, 0.0f, 0.0f, 0.0f);
        this.Gauntletstrapl1 = new ModelRenderer(this, 84, 31);
        this.Gauntletstrapl1.mirror = true;
        this.Gauntletstrapl1.addBox(-1.5f, 3.5f, -2.5f, 3, 1, 5);
        this.Gauntletstrapl1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Gauntletstrapl1.setTextureSize(128, 64);
        this.setRotation(this.Gauntletstrapl1, 0.0f, 0.0f, 0.0f);
        this.GauntletstrapL2 = new ModelRenderer(this, 84, 31);
        this.GauntletstrapL2.mirror = true;
        this.GauntletstrapL2.addBox(-1.5f, 6.5f, -2.5f, 3, 1, 5);
        this.GauntletstrapL2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.GauntletstrapL2.setTextureSize(128, 64);
        this.setRotation(this.GauntletstrapL2, 0.0f, 0.0f, 0.0f);
        this.ShoulderplateLtop = new ModelRenderer(this, 110, 37);
        this.ShoulderplateLtop.mirror = true;
        this.ShoulderplateLtop.addBox(3.5f, -2.5f, -3.5f, 2, 1, 7);
        this.ShoulderplateLtop.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ShoulderplateLtop.setTextureSize(128, 64);
        this.setRotation(this.ShoulderplateLtop, 0.0f, 0.0f, -0.4363323f);
        this.ShoulderplateL1 = new ModelRenderer(this, 110, 45);
        this.ShoulderplateL1.mirror = true;
        this.ShoulderplateL1.addBox(3.5f, -1.5f, -3.5f, 1, 4, 7);
        this.ShoulderplateL1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ShoulderplateL1.setTextureSize(128, 64);
        this.setRotation(this.ShoulderplateL1, 0.0f, 0.0f, -0.4363323f);
        this.ShoulderplateL2 = new ModelRenderer(this, 94, 45);
        this.ShoulderplateL2.mirror = true;
        this.ShoulderplateL2.addBox(2.5f, 1.5f, -3.5f, 1, 3, 7);
        this.ShoulderplateL2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ShoulderplateL2.setTextureSize(128, 64);
        this.setRotation(this.ShoulderplateL2, 0.0f, 0.0f, -0.4363323f);
        this.ShoulderplateL3 = new ModelRenderer(this, 94, 45);
        this.ShoulderplateL3.mirror = true;
        this.ShoulderplateL3.addBox(1.5f, 3.5f, -3.5f, 1, 3, 7);
        this.ShoulderplateL3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.ShoulderplateL3.setTextureSize(128, 64);
        this.setRotation(this.ShoulderplateL3, 0.0f, 0.0f, -0.4363323f);
        (this.LegpanelR1 = new ModelRenderer(this, 0, 51)).addBox(-1.0f, 0.5f, -3.5f, 3, 4, 1);
        this.LegpanelR1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelR1.setTextureSize(128, 64);
        this.setRotation(this.LegpanelR1, -0.4363323f, 0.0f, 0.0f);
        (this.LegpanelR2 = new ModelRenderer(this, 8, 51)).addBox(-1.0f, 3.5f, -2.5f, 3, 4, 1);
        this.LegpanelR2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelR2.setTextureSize(128, 64);
        this.setRotation(this.LegpanelR2, -0.4363323f, 0.0f, 0.0f);
        (this.LegpanelR3 = new ModelRenderer(this, 0, 56)).addBox(-1.0f, 6.5f, -1.5f, 3, 3, 1);
        this.LegpanelR3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelR3.setTextureSize(128, 64);
        this.setRotation(this.LegpanelR3, -0.4363323f, 0.0f, 0.0f);
        (this.LegpanelR4 = new ModelRenderer(this, 0, 43)).addBox(-3.0f, 0.5f, -3.5f, 2, 3, 1);
        this.LegpanelR4.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelR4.setTextureSize(128, 64);
        this.setRotation(this.LegpanelR4, -0.4363323f, 0.0f, 0.0f);
        (this.LegpanelR5 = new ModelRenderer(this, 0, 47)).addBox(-3.0f, 2.5f, -2.5f, 2, 3, 1);
        this.LegpanelR5.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelR5.setTextureSize(128, 64);
        this.setRotation(this.LegpanelR5, -0.4363323f, 0.0f, 0.0f);
        (this.LegpanelR6 = new ModelRenderer(this, 6, 43)).addBox(-3.0f, 4.5f, -1.5f, 2, 3, 1);
        this.LegpanelR6.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelR6.setTextureSize(128, 64);
        this.setRotation(this.LegpanelR6, -0.4363323f, 0.0f, 0.0f);
        (this.SidepanelR1 = new ModelRenderer(this, 0, 22)).addBox(-2.5f, 0.5f, -2.5f, 1, 4, 5);
        this.SidepanelR1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.SidepanelR1.setTextureSize(128, 64);
        this.setRotation(this.SidepanelR1, 0.0f, 0.0f, 0.4363323f);
        (this.SidepanelR2 = new ModelRenderer(this, 0, 31)).addBox(-1.5f, 3.5f, -2.5f, 1, 3, 5);
        this.SidepanelR2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.SidepanelR2.setTextureSize(128, 64);
        this.setRotation(this.SidepanelR2, 0.0f, 0.0f, 0.4363323f);
        (this.SidepanelR3 = new ModelRenderer(this, 12, 31)).addBox(-0.5f, 5.5f, -2.5f, 1, 3, 5);
        this.SidepanelR3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.SidepanelR3.setTextureSize(128, 64);
        this.setRotation(this.SidepanelR3, 0.0f, 0.0f, 0.4363323f);
        (this.BackpanelR1 = new ModelRenderer(this, 0, 18)).addBox(-3.0f, 0.5f, 2.5f, 5, 3, 1);
        this.BackpanelR1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.BackpanelR1.setTextureSize(128, 64);
        this.setRotation(this.BackpanelR1, 0.4363323f, 0.0f, 0.0f);
        (this.BackpanelR2 = new ModelRenderer(this, 0, 18)).addBox(-3.0f, 2.5f, 1.5f, 5, 3, 1);
        this.BackpanelR2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.BackpanelR2.setTextureSize(128, 64);
        this.setRotation(this.BackpanelR2, 0.4363323f, 0.0f, 0.0f);
        (this.BackpanelR3 = new ModelRenderer(this, 0, 18)).addBox(-3.0f, 4.5f, 0.5f, 5, 3, 1);
        this.BackpanelR3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.BackpanelR3.setTextureSize(128, 64);
        this.setRotation(this.BackpanelR3, 0.4363323f, 0.0f, 0.0f);
        this.BackpanelL3 = new ModelRenderer(this, 0, 18);
        this.BackpanelL3.mirror = true;
        this.BackpanelL3.addBox(-2.0f, 4.5f, 0.5f, 5, 3, 1);
        this.BackpanelL3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.BackpanelL3.setTextureSize(128, 64);
        this.setRotation(this.BackpanelL3, 0.4363323f, 0.0f, 0.0f);
        this.LegpanelL1 = new ModelRenderer(this, 0, 51);
        this.LegpanelL1.mirror = true;
        this.LegpanelL1.addBox(-2.0f, 0.5f, -3.5f, 3, 4, 1);
        this.LegpanelL1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelL1.setTextureSize(128, 64);
        this.setRotation(this.LegpanelL1, -0.4363323f, 0.0f, 0.0f);
        this.LegpanelL2 = new ModelRenderer(this, 8, 51);
        this.LegpanelL2.mirror = true;
        this.LegpanelL2.addBox(-2.0f, 3.5f, -2.5f, 3, 4, 1);
        this.LegpanelL2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelL2.setTextureSize(128, 64);
        this.setRotation(this.LegpanelL2, -0.4363323f, 0.0f, 0.0f);
        this.LegpanelL3 = new ModelRenderer(this, 0, 56);
        this.LegpanelL3.mirror = true;
        this.LegpanelL3.addBox(-2.0f, 6.5f, -1.5f, 3, 3, 1);
        this.LegpanelL3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelL3.setTextureSize(128, 64);
        this.setRotation(this.LegpanelL3, -0.4363323f, 0.0f, 0.0f);
        this.LegpanelL4 = new ModelRenderer(this, 0, 43);
        this.LegpanelL4.mirror = true;
        this.LegpanelL4.addBox(1.0f, 0.5f, -3.5f, 2, 3, 1);
        this.LegpanelL4.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelL4.setTextureSize(128, 64);
        this.setRotation(this.LegpanelL4, -0.4363323f, 0.0f, 0.0f);
        this.LegpanelL5 = new ModelRenderer(this, 0, 47);
        this.LegpanelL5.mirror = true;
        this.LegpanelL5.addBox(1.0f, 2.5f, -2.5f, 2, 3, 1);
        this.LegpanelL5.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelL5.setTextureSize(128, 64);
        this.setRotation(this.LegpanelL5, -0.4363323f, 0.0f, 0.0f);
        this.LegpanelL6 = new ModelRenderer(this, 6, 43);
        this.LegpanelL6.mirror = true;
        this.LegpanelL6.addBox(1.0f, 4.5f, -1.5f, 2, 3, 1);
        this.LegpanelL6.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.LegpanelL6.setTextureSize(128, 64);
        this.setRotation(this.LegpanelL6, -0.4363323f, 0.0f, 0.0f);
        this.SidepanelL1 = new ModelRenderer(this, 0, 22);
        this.SidepanelL1.mirror = true;
        this.SidepanelL1.addBox(1.5f, 0.5f, -2.5f, 1, 4, 5);
        this.SidepanelL1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.SidepanelL1.setTextureSize(128, 64);
        this.setRotation(this.SidepanelL1, 0.0f, 0.0f, -0.4363323f);
        this.SidepanelL2 = new ModelRenderer(this, 0, 31);
        this.SidepanelL2.mirror = true;
        this.SidepanelL2.addBox(0.5f, 3.5f, -2.5f, 1, 3, 5);
        this.SidepanelL2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.SidepanelL2.setTextureSize(128, 64);
        this.setRotation(this.SidepanelL2, 0.0f, 0.0f, -0.4363323f);
        this.SidepanelL3 = new ModelRenderer(this, 12, 31);
        this.SidepanelL3.mirror = true;
        this.SidepanelL3.addBox(-0.5f, 5.5f, -2.5f, 1, 3, 5);
        this.SidepanelL3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.SidepanelL3.setTextureSize(128, 64);
        this.setRotation(this.SidepanelL3, 0.0f, 0.0f, -0.4363323f);
        this.BackpanelL1 = new ModelRenderer(this, 0, 18);
        this.BackpanelL1.mirror = true;
        this.BackpanelL1.addBox(-2.0f, 0.5f, 2.5f, 5, 3, 1);
        this.BackpanelL1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.BackpanelL1.setTextureSize(128, 64);
        this.setRotation(this.BackpanelL1, 0.4363323f, 0.0f, 0.0f);
        this.BackpanelL2 = new ModelRenderer(this, 0, 18);
        this.BackpanelL2.mirror = true;
        this.BackpanelL2.addBox(-2.0f, 2.5f, 1.5f, 5, 3, 1);
        this.BackpanelL2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.BackpanelL2.setTextureSize(128, 64);
        this.setRotation(this.BackpanelL2, 0.4363323f, 0.0f, 0.0f);
        this.bipedHeadwear.cubeList.clear();
        this.bipedHead.cubeList.clear();
        this.bipedHead.addChild(this.OrnamentL);
        this.bipedHead.addChild(this.OrnamentL2);
        this.bipedHead.addChild(this.OrnamentR);
        this.bipedHead.addChild(this.OrnamentR2);
        this.bipedHead.addChild(this.Helmet);
        this.bipedHead.addChild(this.HelmetR);
        this.bipedHead.addChild(this.HelmetL);
        this.bipedHead.addChild(this.HelmetB);
        this.bipedHead.addChild(this.capsthingy);
        this.bipedHead.addChild(this.flapR);
        this.bipedHead.addChild(this.flapL);
        this.bipedHead.addChild(this.Gemornament);
        this.bipedHead.addChild(this.Gem);
        this.bipedHead.addChild(this.Goggles);
        this.bipedHead.addChild(this.Mask[0]);
        this.bipedHead.addChild(this.Mask[1]);
        this.bipedHead.addChild(this.Mask[2]);
        this.bipedBody.cubeList.clear();
        if (f < 1.0f) {
            this.bipedBody.addChild(this.Mbelt);
            this.bipedBody.addChild(this.MbeltL);
            this.bipedBody.addChild(this.MbeltR);
        }
        else {
            this.bipedBody.addChild(this.BeltR);
            this.bipedBody.addChild(this.BeltL);
            this.bipedBody.addChild(this.Chestplate);
            this.bipedBody.addChild(this.Scroll);
            this.bipedBody.addChild(this.Backplate);
            this.bipedBody.addChild(this.Book);
        }
        this.bipedRightArm.cubeList.clear();
        this.bipedRightArm.addChild(this.ShoulderR);
        this.bipedRightArm.addChild(this.GauntletR);
        this.bipedRightArm.addChild(this.GauntletstrapR1);
        this.bipedRightArm.addChild(this.GauntletstrapR2);
        this.bipedRightArm.addChild(this.ShoulderplateRtop);
        this.bipedRightArm.addChild(this.ShoulderplateR1);
        this.bipedRightArm.addChild(this.ShoulderplateR2);
        this.bipedRightArm.addChild(this.ShoulderplateR3);
        this.bipedLeftArm.cubeList.clear();
        this.bipedLeftArm.addChild(this.ShoulderL);
        this.bipedLeftArm.addChild(this.GauntletL);
        this.bipedLeftArm.addChild(this.Gauntletstrapl1);
        this.bipedLeftArm.addChild(this.GauntletstrapL2);
        this.bipedLeftArm.addChild(this.ShoulderplateLtop);
        this.bipedLeftArm.addChild(this.ShoulderplateL1);
        this.bipedLeftArm.addChild(this.ShoulderplateL2);
        this.bipedLeftArm.addChild(this.ShoulderplateL3);
        this.bipedRightLeg.cubeList.clear();
        this.bipedRightLeg.addChild(this.LegpanelR1);
        this.bipedRightLeg.addChild(this.LegpanelR2);
        this.bipedRightLeg.addChild(this.LegpanelR3);
        this.bipedRightLeg.addChild(this.LegpanelR4);
        this.bipedRightLeg.addChild(this.LegpanelR5);
        this.bipedRightLeg.addChild(this.LegpanelR6);
        this.bipedRightLeg.addChild(this.SidepanelR1);
        this.bipedRightLeg.addChild(this.SidepanelR2);
        this.bipedRightLeg.addChild(this.SidepanelR3);
        this.bipedRightLeg.addChild(this.BackpanelR1);
        this.bipedRightLeg.addChild(this.BackpanelR2);
        this.bipedRightLeg.addChild(this.BackpanelR3);
        this.bipedLeftLeg.cubeList.clear();
        this.bipedLeftLeg.addChild(this.BackpanelL3);
        this.bipedLeftLeg.addChild(this.LegpanelL1);
        this.bipedLeftLeg.addChild(this.LegpanelL2);
        this.bipedLeftLeg.addChild(this.LegpanelL3);
        this.bipedLeftLeg.addChild(this.LegpanelL4);
        this.bipedLeftLeg.addChild(this.LegpanelL5);
        this.bipedLeftLeg.addChild(this.LegpanelL6);
        this.bipedLeftLeg.addChild(this.SidepanelL1);
        this.bipedLeftLeg.addChild(this.SidepanelL2);
        this.bipedLeftLeg.addChild(this.SidepanelL3);
        this.bipedLeftLeg.addChild(this.BackpanelL1);
        this.bipedLeftLeg.addChild(this.BackpanelL2);
    }
    
    private void checkSet(final Entity entity) {
        if (entity instanceof EntityLivingBase && entity.ticksExisted % 20 == 0) {
            int set = 0;
            for (int a = 2; a < 5; ++a) {
                final ItemStack piece = ((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.values()[a + 1]);
                if (piece != null && piece.getItem() instanceof ItemFortressArmor) {
                    ++set;
                    if (a == 4) {
                        if (piece.hasTagCompound() && piece.getTagCompound().hasKey("mask")) {
                            ModelFortressArmor.hasMask.put(entity.getEntityId(), piece.getTagCompound().getInteger("mask"));
                        }
                        else {
                            ModelFortressArmor.hasMask.remove(entity.getEntityId());
                        }
                        if (piece.hasTagCompound() && piece.getTagCompound().hasKey("goggles")) {
                            ModelFortressArmor.hasGoggles.put(entity.getEntityId(), true);
                        }
                        else {
                            ModelFortressArmor.hasGoggles.remove(entity.getEntityId());
                        }
                    }
                }
            }
            if (set > 0) {
                ModelFortressArmor.hasSet.put(entity.getEntityId(), set);
            }
            else {
                ModelFortressArmor.hasSet.remove(entity.getEntityId());
            }
        }
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        this.checkSet(entity);
        final int set = ModelFortressArmor.hasSet.containsKey(entity.getEntityId()) ? ModelFortressArmor.hasSet.get(entity.getEntityId()) : -1;
        final int mask = ModelFortressArmor.hasMask.containsKey(entity.getEntityId()) ? ModelFortressArmor.hasMask.get(entity.getEntityId()) : -1;
        this.Goggles.isHidden = !ModelFortressArmor.hasGoggles.containsKey(entity.getEntityId());
        for (int a = 0; a < 3; ++a) {
            if (mask == a) {
                this.Mask[a].isHidden = false;
            }
            else {
                this.Mask[a].isHidden = true;
            }
        }
        this.Scroll.isHidden = (set < 3);
        this.Book.isHidden = (set < 2);
        this.OrnamentL.isHidden = (set < 3);
        this.OrnamentL2.isHidden = (set < 3);
        this.OrnamentR.isHidden = (set < 3);
        this.OrnamentR2.isHidden = (set < 3);
        this.Gemornament.isHidden = (set < 3);
        this.Gem.isHidden = (set < 3);
        this.flapL.isHidden = (set < 2);
        this.flapR.isHidden = (set < 2);
        this.ShoulderplateLtop.isHidden = (set < 2);
        this.ShoulderplateL1.isHidden = (set < 2);
        this.ShoulderplateL2.isHidden = (set < 3);
        this.ShoulderplateL3.isHidden = (set < 3);
        this.ShoulderplateRtop.isHidden = (set < 2);
        this.ShoulderplateR1.isHidden = (set < 2);
        this.ShoulderplateR2.isHidden = (set < 3);
        this.ShoulderplateR3.isHidden = (set < 3);
        this.SidepanelR2.isHidden = (set < 2);
        this.SidepanelL2.isHidden = (set < 2);
        this.SidepanelR3.isHidden = (set < 3);
        this.SidepanelL3.isHidden = (set < 3);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        if (this.isChild) {
            final float f6 = 2.0f;
            GL11.glPushMatrix();
            GL11.glScalef(1.5f / f6, 1.5f / f6, 1.5f / f6);
            GL11.glTranslatef(0.0f, 16.0f * f5, 0.0f);
            this.bipedHead.render(f5);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0f / f6, 1.0f / f6, 1.0f / f6);
            GL11.glTranslatef(0.0f, 24.0f * f5, 0.0f);
            this.bipedBody.render(f5);
            this.bipedRightArm.render(f5);
            this.bipedLeftArm.render(f5);
            this.bipedRightLeg.render(f5);
            this.bipedLeftLeg.render(f5);
            this.bipedHeadwear.render(f5);
            GL11.glPopMatrix();
        }
        else {
            GL11.glPushMatrix();
            GL11.glScalef(1.01f, 1.01f, 1.01f);
            this.bipedHead.render(f5);
            GL11.glPopMatrix();
            this.bipedBody.render(f5);
            this.bipedRightArm.render(f5);
            this.bipedLeftArm.render(f5);
            this.bipedRightLeg.render(f5);
            this.bipedLeftLeg.render(f5);
            this.bipedHeadwear.render(f5);
        }
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    static {
        ModelFortressArmor.hasSet = new HashMap<Integer, Integer>();
        ModelFortressArmor.hasMask = new HashMap<Integer, Integer>();
        ModelFortressArmor.hasGoggles = new HashMap<Integer, Boolean>();
    }
}
