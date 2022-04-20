// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.gui;

import java.io.IOException;
import java.util.Iterator;
import net.minecraft.client.gui.GuiButton;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.api.golems.EnumGolemTrait;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.gui.GuiScreen;
import thaumcraft.client.gui.plugins.GuiHoverButton;
import thaumcraft.client.gui.plugins.GuiScrollButton;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.golems.GolemProperties;
import net.minecraft.inventory.Container;
import thaumcraft.common.container.ContainerGolemBuilder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.common.golems.client.gui.GuiGolemCraftButton;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.api.golems.parts.GolemHead;
import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class GuiGolemBuilder extends GuiContainer
{
    private TileGolemBuilder builder;
    private EntityPlayer player;
    ResourceLocation tex;
    ArrayList<GolemHead> valHeads;
    ArrayList<GolemMaterial> valMats;
    ArrayList<GolemArm> valArms;
    ArrayList<GolemLeg> valLegs;
    ArrayList<GolemAddon> valAddons;
    static int headIndex;
    static int matIndex;
    static int armIndex;
    static int legIndex;
    static int addonIndex;
    IGolemProperties props;
    float hearts;
    float armor;
    float damage;
    GuiGolemCraftButton craftButton;
    ResourceLocation matIcon;
    int cost;
    boolean allfound;
    ItemStack[] components;
    boolean[] owns;
    boolean disableAll;
    
    public GuiGolemBuilder(final InventoryPlayer par1InventoryPlayer, final TileGolemBuilder table) {
        super(new ContainerGolemBuilder(par1InventoryPlayer, table));
        this.tex = new ResourceLocation("thaumcraft", "textures/gui/gui_golembuilder.png");
        this.valHeads = new ArrayList<GolemHead>();
        this.valMats = new ArrayList<GolemMaterial>();
        this.valArms = new ArrayList<GolemArm>();
        this.valLegs = new ArrayList<GolemLeg>();
        this.valAddons = new ArrayList<GolemAddon>();
        this.props = GolemProperties.fromLong(0L);
        this.hearts = 0.0f;
        this.armor = 0.0f;
        this.damage = 0.0f;
        this.craftButton = null;
        this.matIcon = new ResourceLocation("thaumcraft", "textures/items/golem.png");
        this.cost = 0;
        this.allfound = false;
        this.components = null;
        this.owns = null;
        this.disableAll = false;
        this.player = par1InventoryPlayer.player;
        this.builder = table;
        this.xSize = 208;
        this.ySize = 224;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    public void initGui() {
        super.initGui();
        this.valHeads.clear();
        for (final GolemHead head : GolemHead.getHeads()) {
            if (ThaumcraftCapabilities.knowsResearchStrict(this.player, head.research)) {
                this.valHeads.add(head);
            }
        }
        this.valMats.clear();
        for (final GolemMaterial mat : GolemMaterial.getMaterials()) {
            if (ThaumcraftCapabilities.knowsResearchStrict(this.player, mat.research)) {
                this.valMats.add(mat);
            }
        }
        this.valArms.clear();
        for (final GolemArm arm : GolemArm.getArms()) {
            if (ThaumcraftCapabilities.knowsResearchStrict(this.player, arm.research)) {
                this.valArms.add(arm);
            }
        }
        this.valLegs.clear();
        for (final GolemLeg leg : GolemLeg.getLegs()) {
            if (ThaumcraftCapabilities.knowsResearchStrict(this.player, leg.research)) {
                this.valLegs.add(leg);
            }
        }
        this.valAddons.clear();
        for (final GolemAddon addon : GolemAddon.getAddons()) {
            if (ThaumcraftCapabilities.knowsResearchStrict(this.player, addon.research)) {
                this.valAddons.add(addon);
            }
        }
        if (GuiGolemBuilder.headIndex >= this.valHeads.size()) {
            GuiGolemBuilder.headIndex = 0;
        }
        if (GuiGolemBuilder.matIndex >= this.valMats.size()) {
            GuiGolemBuilder.matIndex = 0;
        }
        if (GuiGolemBuilder.armIndex >= this.valArms.size()) {
            GuiGolemBuilder.armIndex = 0;
        }
        if (GuiGolemBuilder.legIndex >= this.valLegs.size()) {
            GuiGolemBuilder.legIndex = 0;
        }
        if (GuiGolemBuilder.addonIndex >= this.valAddons.size()) {
            GuiGolemBuilder.addonIndex = 0;
        }
        this.gatherInfo();
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.tex);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if (this.components != null && this.components.length > 0) {
            int i = 1;
            int q = 0;
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
            for (int a = 0; a < this.components.length; ++a) {
                if (!this.owns[a]) {
                    this.drawTexturedModalRect(this.guiLeft + 144 + q * 16, this.guiTop + 16 + 16 * i, 240, 0, 16, 16);
                }
                if (++i > 3) {
                    i = 0;
                    ++q;
                }
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.builder.cost > 0) {
            this.drawTexturedModalRect(this.guiLeft + 145, this.guiTop + 89, 209, 89, (int)(46.0f * (1.0f - this.builder.cost / (float)this.builder.maxCost)), 6);
            if (!this.disableAll) {
                this.disableAll = true;
                this.redoComps();
            }
        }
        else if (this.disableAll) {
            this.disableAll = false;
            this.redoComps();
        }
        this.drawCenteredString(this.fontRenderer, "" + this.hearts, this.guiLeft + 48, this.guiTop + 108, 16777215);
        this.drawCenteredString(this.fontRenderer, "" + this.armor, this.guiLeft + 72, this.guiTop + 108, 16777215);
        this.drawCenteredString(this.fontRenderer, "" + this.damage, this.guiLeft + 97, this.guiTop + 108, 16777215);
    }
    
    private void gatherInfo() {
        this.buttonList.clear();
        this.craftButton = new GuiGolemCraftButton(99, this.guiLeft + 120, this.guiTop + 104);
        this.buttonList.add(this.craftButton);
        if (this.valHeads.size() > 1) {
            this.buttonList.add(new GuiScrollButton(0, this.guiLeft + 112 - 5 - 6, this.guiTop - 5 + 16 + 8, 10, 10, true));
            this.buttonList.add(new GuiScrollButton(1, this.guiLeft + 112 - 5 + 22, this.guiTop - 5 + 16 + 8, 10, 10, false));
        }
        if (this.valMats.size() > 1) {
            this.buttonList.add(new GuiScrollButton(2, this.guiLeft + 16 - 5 - 6, this.guiTop - 5 + 16 + 8, 10, 10, true));
            this.buttonList.add(new GuiScrollButton(3, this.guiLeft + 16 - 5 + 22, this.guiTop - 5 + 16 + 8, 10, 10, false));
        }
        if (this.valArms.size() > 1) {
            this.buttonList.add(new GuiScrollButton(4, this.guiLeft + 112 - 5 - 6, this.guiTop - 5 + 40 + 8, 10, 10, true));
            this.buttonList.add(new GuiScrollButton(5, this.guiLeft + 112 - 5 + 22, this.guiTop - 5 + 40 + 8, 10, 10, false));
        }
        if (this.valLegs.size() > 1) {
            this.buttonList.add(new GuiScrollButton(6, this.guiLeft + 112 - 5 - 6, this.guiTop - 5 + 64 + 8, 10, 10, true));
            this.buttonList.add(new GuiScrollButton(7, this.guiLeft + 112 - 5 + 22, this.guiTop - 5 + 64 + 8, 10, 10, false));
        }
        if (this.valAddons.size() > 1) {
            this.buttonList.add(new GuiScrollButton(8, this.guiLeft + 16 - 5 - 6, this.guiTop - 5 + 64 + 8, 10, 10, true));
            this.buttonList.add(new GuiScrollButton(9, this.guiLeft + 16 - 5 + 22, this.guiTop - 5 + 64 + 8, 10, 10, false));
        }
        if (this.valHeads.size() > 0) {
            this.buttonList.add(new GuiHoverButton(this, 100, this.guiLeft + 120, this.guiTop + 24, 16, 16, this.valHeads.get(GuiGolemBuilder.headIndex).getLocalizedName(), this.valHeads.get(GuiGolemBuilder.headIndex).getLocalizedDescription(), this.valHeads.get(GuiGolemBuilder.headIndex).icon));
        }
        if (this.valMats.size() > 0) {
            this.buttonList.add(new GuiHoverButton(this, 101, this.guiLeft + 24, this.guiTop + 24, 16, 16, this.valMats.get(GuiGolemBuilder.matIndex).getLocalizedName(), this.valMats.get(GuiGolemBuilder.matIndex).getLocalizedDescription(), this.matIcon, this.valMats.get(GuiGolemBuilder.matIndex).itemColor));
        }
        if (this.valArms.size() > 0) {
            this.buttonList.add(new GuiHoverButton(this, 102, this.guiLeft + 120, this.guiTop + 48, 16, 16, this.valArms.get(GuiGolemBuilder.armIndex).getLocalizedName(), this.valArms.get(GuiGolemBuilder.armIndex).getLocalizedDescription(), this.valArms.get(GuiGolemBuilder.armIndex).icon));
        }
        if (this.valLegs.size() > 0) {
            this.buttonList.add(new GuiHoverButton(this, 103, this.guiLeft + 120, this.guiTop + 72, 16, 16, this.valLegs.get(GuiGolemBuilder.legIndex).getLocalizedName(), this.valLegs.get(GuiGolemBuilder.legIndex).getLocalizedDescription(), this.valLegs.get(GuiGolemBuilder.legIndex).icon));
        }
        if (this.valAddons.size() > 0 && !this.valAddons.get(GuiGolemBuilder.addonIndex).key.equalsIgnoreCase("none")) {
            this.buttonList.add(new GuiHoverButton(this, 103, this.guiLeft + 24, this.guiTop + 72, 16, 16, this.valAddons.get(GuiGolemBuilder.addonIndex).getLocalizedName(), this.valAddons.get(GuiGolemBuilder.addonIndex).getLocalizedDescription(), this.valAddons.get(GuiGolemBuilder.addonIndex).icon));
        }
        (this.props = GolemProperties.fromLong(0L)).setHead(this.valHeads.get(GuiGolemBuilder.headIndex));
        this.props.setMaterial(this.valMats.get(GuiGolemBuilder.matIndex));
        this.props.setArms(this.valArms.get(GuiGolemBuilder.armIndex));
        this.props.setLegs(this.valLegs.get(GuiGolemBuilder.legIndex));
        this.props.setAddon(this.valAddons.get(GuiGolemBuilder.addonIndex));
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("check", true);
        nbt.setLong("golem", this.props.toLong());
        this.builder.sendMessageToServer(nbt);
        this.redoComps();
        final EnumGolemTrait[] tags = this.props.getTraits().toArray(new EnumGolemTrait[0]);
        if (tags != null && tags.length > 0) {
            final int yy = (tags.length <= 4) ? ((tags.length - 1) % 4 * 8) : 24;
            final int xx = (tags.length - 1) / 4 % 4 * 8;
            int i = 0;
            int q = 0;
            int z = 0;
            for (final EnumGolemTrait tag : tags) {
                this.buttonList.add(new GuiHoverButton(this, 30 + z, this.guiLeft + 72 + q * 16 - xx, this.guiTop + 48 + 16 * i - yy, 16, 16, tag.getLocalizedName(), tag.getLocalizedDescription(), tag.icon));
                if (++i > 3) {
                    i = 0;
                    ++q;
                }
                ++z;
            }
        }
        int hh = 10 + this.props.getMaterial().healthMod;
        if (this.props.hasTrait(EnumGolemTrait.FRAGILE)) {
            hh *= (int)0.75;
        }
        this.hearts = hh / 2.0f;
        int aa = this.props.getMaterial().armor;
        if (this.props.hasTrait(EnumGolemTrait.ARMORED)) {
            aa = (int)Math.max(aa * 1.5, aa + 1);
        }
        if (this.props.hasTrait(EnumGolemTrait.FRAGILE)) {
            aa *= (int)0.75;
        }
        this.armor = aa / 2.0f;
        double dd = this.props.hasTrait(EnumGolemTrait.FIGHTER) ? this.props.getMaterial().damage : 0.0;
        if (this.props.hasTrait(EnumGolemTrait.BRUTAL)) {
            dd = Math.max(dd * 1.5, dd + 1.0);
        }
        this.damage = (float)(dd / 2.0);
    }
    
    private void redoComps() {
        this.allfound = true;
        this.cost = this.props.getTraits().size() * 2;
        this.components = this.props.generateComponents();
        if (this.components.length >= 1) {
            this.owns = new boolean[this.components.length];
            for (int a = 0; a < this.components.length; ++a) {
                this.cost += this.components[a].getCount();
                this.owns[a] = false;
                if (this.builder.hasStuff != null && this.builder.hasStuff.length > a) {
                    this.owns[a] = this.builder.hasStuff[a];
                }
                if (!this.owns[a]) {
                    this.owns[a] = InventoryUtils.isPlayerCarryingAmount(this.player, this.components[a], true);
                }
                if (!this.owns[a]) {
                    this.allfound = false;
                }
            }
        }
        if (this.components != null && this.components.length > 0) {
            this.buttonList.add(new GuiHoverButton(this, 10, this.guiLeft + 152, this.guiTop + 24, 16, 16, Aspect.MECHANISM.getName(), Aspect.MECHANISM.getLocalizedDescription(), Aspect.MECHANISM));
            int i = 1;
            int q = 0;
            int z = 0;
            for (final ItemStack stack : this.components) {
                this.buttonList.add(new GuiHoverButton(this, 11 + z, this.guiLeft + 152 + q * 16, this.guiTop + 24 + 16 * i, 16, 16, stack.getDisplayName(), null, stack));
                if (++i > 3) {
                    i = 0;
                    ++q;
                }
                ++z;
            }
        }
        if (this.buttonList != null && this.buttonList.size() > 0) {
            for (final Object b : this.buttonList) {
                if (b instanceof GuiButton) {
                    ((GuiButton)b).enabled = !this.disableAll;
                    if (this.disableAll || b != this.craftButton) {
                        continue;
                    }
                    this.craftButton.enabled = this.allfound;
                }
            }
        }
    }
    
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        if (this.components != null && this.components.length > 0) {
            this.drawString(this.fontRenderer, "" + this.cost, 162 - this.fontRenderer.getStringWidth("" + this.cost), 24, 16777215);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.tex);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.drawTexturedModalRect(12, 12, 228, 124, 24, 24);
        this.drawTexturedModalRect(12, 60, 228, 124, 24, 24);
        this.drawTexturedModalRect(108, 12, 228, 124, 24, 24);
        this.drawTexturedModalRect(108, 36, 228, 124, 24, 24);
        this.drawTexturedModalRect(108, 60, 228, 124, 24, 24);
        for (final GuiButton guibutton : this.buttonList) {
            if (guibutton.isMouseOver()) {
                guibutton.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
                break;
            }
        }
        if (ContainerGolemBuilder.redo) {
            this.redoComps();
            ContainerGolemBuilder.redo = false;
        }
        GL11.glDisable(3042);
    }
    
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 0) {
            --GuiGolemBuilder.headIndex;
            if (GuiGolemBuilder.headIndex < 0) {
                GuiGolemBuilder.headIndex = this.valHeads.size() - 1;
            }
            this.gatherInfo();
        }
        else if (button.id == 1) {
            ++GuiGolemBuilder.headIndex;
            if (GuiGolemBuilder.headIndex >= this.valHeads.size()) {
                GuiGolemBuilder.headIndex = 0;
            }
            this.gatherInfo();
        }
        else if (button.id == 2) {
            --GuiGolemBuilder.matIndex;
            if (GuiGolemBuilder.matIndex < 0) {
                GuiGolemBuilder.matIndex = this.valMats.size() - 1;
            }
            this.gatherInfo();
        }
        else if (button.id == 3) {
            ++GuiGolemBuilder.matIndex;
            if (GuiGolemBuilder.matIndex >= this.valMats.size()) {
                GuiGolemBuilder.matIndex = 0;
            }
            this.gatherInfo();
        }
        else if (button.id == 4) {
            --GuiGolemBuilder.armIndex;
            if (GuiGolemBuilder.armIndex < 0) {
                GuiGolemBuilder.armIndex = this.valArms.size() - 1;
            }
            this.gatherInfo();
        }
        else if (button.id == 5) {
            ++GuiGolemBuilder.armIndex;
            if (GuiGolemBuilder.armIndex >= this.valArms.size()) {
                GuiGolemBuilder.armIndex = 0;
            }
            this.gatherInfo();
        }
        else if (button.id == 6) {
            --GuiGolemBuilder.legIndex;
            if (GuiGolemBuilder.legIndex < 0) {
                GuiGolemBuilder.legIndex = this.valLegs.size() - 1;
            }
            this.gatherInfo();
        }
        else if (button.id == 7) {
            ++GuiGolemBuilder.legIndex;
            if (GuiGolemBuilder.legIndex >= this.valLegs.size()) {
                GuiGolemBuilder.legIndex = 0;
            }
            this.gatherInfo();
        }
        else if (button.id == 8) {
            --GuiGolemBuilder.addonIndex;
            if (GuiGolemBuilder.addonIndex < 0) {
                GuiGolemBuilder.addonIndex = this.valAddons.size() - 1;
            }
            this.gatherInfo();
        }
        else if (button.id == 9) {
            ++GuiGolemBuilder.addonIndex;
            if (GuiGolemBuilder.addonIndex >= this.valAddons.size()) {
                GuiGolemBuilder.addonIndex = 0;
            }
            this.gatherInfo();
        }
        else if (button.id == 99 && this.allfound) {
            final NBTTagCompound nbt = new NBTTagCompound();
            nbt.setLong("golem", this.props.toLong());
            this.builder.sendMessageToServer(nbt);
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 99);
            this.disableAll = true;
        }
    }
    
    static {
        GuiGolemBuilder.headIndex = 0;
        GuiGolemBuilder.matIndex = 0;
        GuiGolemBuilder.armIndex = 0;
        GuiGolemBuilder.legIndex = 0;
        GuiGolemBuilder.addonIndex = 0;
    }
}
