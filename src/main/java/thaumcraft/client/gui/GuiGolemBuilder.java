package thaumcraft.client.gui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.client.gui.plugins.GuiHoverButton;
import thaumcraft.client.gui.plugins.GuiScrollButton;
import thaumcraft.common.container.ContainerGolemBuilder;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.golems.client.gui.GuiGolemCraftButton;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;


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
    
    public GuiGolemBuilder(InventoryPlayer par1InventoryPlayer, TileGolemBuilder table) {
        super(new ContainerGolemBuilder(par1InventoryPlayer, table));
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_golembuilder.png");
        valHeads = new ArrayList<GolemHead>();
        valMats = new ArrayList<GolemMaterial>();
        valArms = new ArrayList<GolemArm>();
        valLegs = new ArrayList<GolemLeg>();
        valAddons = new ArrayList<GolemAddon>();
        props = GolemProperties.fromLong(0L);
        hearts = 0.0f;
        armor = 0.0f;
        damage = 0.0f;
        craftButton = null;
        matIcon = new ResourceLocation("thaumcraft", "textures/items/golem.png");
        cost = 0;
        allfound = false;
        components = null;
        owns = null;
        disableAll = false;
        player = par1InventoryPlayer.player;
        builder = table;
        xSize = 208;
        ySize = 224;
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    public void initGui() {
        super.initGui();
        valHeads.clear();
        for (GolemHead head : GolemHead.getHeads()) {
            if (ThaumcraftCapabilities.knowsResearchStrict(player, head.research)) {
                valHeads.add(head);
            }
        }
        valMats.clear();
        for (GolemMaterial mat : GolemMaterial.getMaterials()) {
            if (ThaumcraftCapabilities.knowsResearchStrict(player, mat.research)) {
                valMats.add(mat);
            }
        }
        valArms.clear();
        for (GolemArm arm : GolemArm.getArms()) {
            if (ThaumcraftCapabilities.knowsResearchStrict(player, arm.research)) {
                valArms.add(arm);
            }
        }
        valLegs.clear();
        for (GolemLeg leg : GolemLeg.getLegs()) {
            if (ThaumcraftCapabilities.knowsResearchStrict(player, leg.research)) {
                valLegs.add(leg);
            }
        }
        valAddons.clear();
        for (GolemAddon addon : GolemAddon.getAddons()) {
            if (ThaumcraftCapabilities.knowsResearchStrict(player, addon.research)) {
                valAddons.add(addon);
            }
        }
        if (GuiGolemBuilder.headIndex >= valHeads.size()) {
            GuiGolemBuilder.headIndex = 0;
        }
        if (GuiGolemBuilder.matIndex >= valMats.size()) {
            GuiGolemBuilder.matIndex = 0;
        }
        if (GuiGolemBuilder.armIndex >= valArms.size()) {
            GuiGolemBuilder.armIndex = 0;
        }
        if (GuiGolemBuilder.legIndex >= valLegs.size()) {
            GuiGolemBuilder.legIndex = 0;
        }
        if (GuiGolemBuilder.addonIndex >= valAddons.size()) {
            GuiGolemBuilder.addonIndex = 0;
        }
        gatherInfo();
    }
    
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        if (components != null && components.length > 0) {
            int i = 1;
            int q = 0;
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
            for (int a = 0; a < components.length; ++a) {
                if (!owns[a]) {
                    drawTexturedModalRect(guiLeft + 144 + q * 16, guiTop + 16 + 16 * i, 240, 0, 16, 16);
                }
                if (++i > 3) {
                    i = 0;
                    ++q;
                }
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (builder.cost > 0) {
            drawTexturedModalRect(guiLeft + 145, guiTop + 89, 209, 89, (int)(46.0f * (1.0f - builder.cost / (float) builder.maxCost)), 6);
            if (!disableAll) {
                disableAll = true;
                redoComps();
            }
        }
        else if (disableAll) {
            disableAll = false;
            redoComps();
        }
        drawCenteredString(fontRenderer, "" + hearts, guiLeft + 48, guiTop + 108, 16777215);
        drawCenteredString(fontRenderer, "" + armor, guiLeft + 72, guiTop + 108, 16777215);
        drawCenteredString(fontRenderer, "" + damage, guiLeft + 97, guiTop + 108, 16777215);
    }
    
    private void gatherInfo() {
        buttonList.clear();
        craftButton = new GuiGolemCraftButton(99, guiLeft + 120, guiTop + 104);
        buttonList.add(craftButton);
        if (valHeads.size() > 1) {
            buttonList.add(new GuiScrollButton(0, guiLeft + 112 - 5 - 6, guiTop - 5 + 16 + 8, 10, 10, true));
            buttonList.add(new GuiScrollButton(1, guiLeft + 112 - 5 + 22, guiTop - 5 + 16 + 8, 10, 10, false));
        }
        if (valMats.size() > 1) {
            buttonList.add(new GuiScrollButton(2, guiLeft + 16 - 5 - 6, guiTop - 5 + 16 + 8, 10, 10, true));
            buttonList.add(new GuiScrollButton(3, guiLeft + 16 - 5 + 22, guiTop - 5 + 16 + 8, 10, 10, false));
        }
        if (valArms.size() > 1) {
            buttonList.add(new GuiScrollButton(4, guiLeft + 112 - 5 - 6, guiTop - 5 + 40 + 8, 10, 10, true));
            buttonList.add(new GuiScrollButton(5, guiLeft + 112 - 5 + 22, guiTop - 5 + 40 + 8, 10, 10, false));
        }
        if (valLegs.size() > 1) {
            buttonList.add(new GuiScrollButton(6, guiLeft + 112 - 5 - 6, guiTop - 5 + 64 + 8, 10, 10, true));
            buttonList.add(new GuiScrollButton(7, guiLeft + 112 - 5 + 22, guiTop - 5 + 64 + 8, 10, 10, false));
        }
        if (valAddons.size() > 1) {
            buttonList.add(new GuiScrollButton(8, guiLeft + 16 - 5 - 6, guiTop - 5 + 64 + 8, 10, 10, true));
            buttonList.add(new GuiScrollButton(9, guiLeft + 16 - 5 + 22, guiTop - 5 + 64 + 8, 10, 10, false));
        }
        if (valHeads.size() > 0) {
            buttonList.add(new GuiHoverButton(this, 100, guiLeft + 120, guiTop + 24, 16, 16, valHeads.get(GuiGolemBuilder.headIndex).getLocalizedName(), valHeads.get(GuiGolemBuilder.headIndex).getLocalizedDescription(), valHeads.get(GuiGolemBuilder.headIndex).icon));
        }
        if (valMats.size() > 0) {
            buttonList.add(new GuiHoverButton(this, 101, guiLeft + 24, guiTop + 24, 16, 16, valMats.get(GuiGolemBuilder.matIndex).getLocalizedName(), valMats.get(GuiGolemBuilder.matIndex).getLocalizedDescription(), matIcon, valMats.get(GuiGolemBuilder.matIndex).itemColor));
        }
        if (valArms.size() > 0) {
            buttonList.add(new GuiHoverButton(this, 102, guiLeft + 120, guiTop + 48, 16, 16, valArms.get(GuiGolemBuilder.armIndex).getLocalizedName(), valArms.get(GuiGolemBuilder.armIndex).getLocalizedDescription(), valArms.get(GuiGolemBuilder.armIndex).icon));
        }
        if (valLegs.size() > 0) {
            buttonList.add(new GuiHoverButton(this, 103, guiLeft + 120, guiTop + 72, 16, 16, valLegs.get(GuiGolemBuilder.legIndex).getLocalizedName(), valLegs.get(GuiGolemBuilder.legIndex).getLocalizedDescription(), valLegs.get(GuiGolemBuilder.legIndex).icon));
        }
        if (valAddons.size() > 0 && !valAddons.get(GuiGolemBuilder.addonIndex).key.equalsIgnoreCase("none")) {
            buttonList.add(new GuiHoverButton(this, 103, guiLeft + 24, guiTop + 72, 16, 16, valAddons.get(GuiGolemBuilder.addonIndex).getLocalizedName(), valAddons.get(GuiGolemBuilder.addonIndex).getLocalizedDescription(), valAddons.get(GuiGolemBuilder.addonIndex).icon));
        }
        (props = GolemProperties.fromLong(0L)).setHead(valHeads.get(GuiGolemBuilder.headIndex));
        props.setMaterial(valMats.get(GuiGolemBuilder.matIndex));
        props.setArms(valArms.get(GuiGolemBuilder.armIndex));
        props.setLegs(valLegs.get(GuiGolemBuilder.legIndex));
        props.setAddon(valAddons.get(GuiGolemBuilder.addonIndex));
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("check", true);
        nbt.setLong("golem", props.toLong());
        builder.sendMessageToServer(nbt);
        redoComps();
        EnumGolemTrait[] tags = props.getTraits().toArray(new EnumGolemTrait[0]);
        if (tags != null && tags.length > 0) {
            int yy = (tags.length <= 4) ? ((tags.length - 1) % 4 * 8) : 24;
            int xx = (tags.length - 1) / 4 % 4 * 8;
            int i = 0;
            int q = 0;
            int z = 0;
            for (EnumGolemTrait tag : tags) {
                buttonList.add(new GuiHoverButton(this, 30 + z, guiLeft + 72 + q * 16 - xx, guiTop + 48 + 16 * i - yy, 16, 16, tag.getLocalizedName(), tag.getLocalizedDescription(), tag.icon));
                if (++i > 3) {
                    i = 0;
                    ++q;
                }
                ++z;
            }
        }
        int hh = 10 + props.getMaterial().healthMod;
        if (props.hasTrait(EnumGolemTrait.FRAGILE)) {
            hh *= (int)0.75;
        }
        hearts = hh / 2.0f;
        int aa = props.getMaterial().armor;
        if (props.hasTrait(EnumGolemTrait.ARMORED)) {
            aa = (int)Math.max(aa * 1.5, aa + 1);
        }
        if (props.hasTrait(EnumGolemTrait.FRAGILE)) {
            aa *= (int)0.75;
        }
        armor = aa / 2.0f;
        double dd = props.hasTrait(EnumGolemTrait.FIGHTER) ? props.getMaterial().damage : 0.0;
        if (props.hasTrait(EnumGolemTrait.BRUTAL)) {
            dd = Math.max(dd * 1.5, dd + 1.0);
        }
        damage = (float)(dd / 2.0);
    }
    
    private void redoComps() {
        allfound = true;
        cost = props.getTraits().size() * 2;
        components = props.generateComponents();
        if (components.length >= 1) {
            owns = new boolean[components.length];
            for (int a = 0; a < components.length; ++a) {
                cost += components[a].getCount();
                owns[a] = false;
                if (builder.hasStuff != null && builder.hasStuff.length > a) {
                    owns[a] = builder.hasStuff[a];
                }
                if (!owns[a]) {
                    owns[a] = InventoryUtils.isPlayerCarryingAmount(player, components[a], true);
                }
                if (!owns[a]) {
                    allfound = false;
                }
            }
        }
        if (components != null && components.length > 0) {
            buttonList.add(new GuiHoverButton(this, 10, guiLeft + 152, guiTop + 24, 16, 16, Aspect.MECHANISM.getName(), Aspect.MECHANISM.getLocalizedDescription(), Aspect.MECHANISM));
            int i = 1;
            int q = 0;
            int z = 0;
            for (ItemStack stack : components) {
                buttonList.add(new GuiHoverButton(this, 11 + z, guiLeft + 152 + q * 16, guiTop + 24 + 16 * i, 16, 16, stack.getDisplayName(), null, stack));
                if (++i > 3) {
                    i = 0;
                    ++q;
                }
                ++z;
            }
        }
        if (buttonList != null && buttonList.size() > 0) {
            for (Object b : buttonList) {
                if (b instanceof GuiButton) {
                    ((GuiButton)b).enabled = !disableAll;
                    if (disableAll || b != craftButton) {
                        continue;
                    }
                    craftButton.enabled = allfound;
                }
            }
        }
    }
    
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (components != null && components.length > 0) {
            drawString(fontRenderer, "" + cost, 162 - fontRenderer.getStringWidth("" + cost), 24, 16777215);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        drawTexturedModalRect(12, 12, 228, 124, 24, 24);
        drawTexturedModalRect(12, 60, 228, 124, 24, 24);
        drawTexturedModalRect(108, 12, 228, 124, 24, 24);
        drawTexturedModalRect(108, 36, 228, 124, 24, 24);
        drawTexturedModalRect(108, 60, 228, 124, 24, 24);
        for (GuiButton guibutton : buttonList) {
            if (guibutton.isMouseOver()) {
                guibutton.drawButtonForegroundLayer(mouseX - guiLeft, mouseY - guiTop);
                break;
            }
        }
        if (ContainerGolemBuilder.redo) {
            redoComps();
            ContainerGolemBuilder.redo = false;
        }
        GL11.glDisable(3042);
    }
    
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            --GuiGolemBuilder.headIndex;
            if (GuiGolemBuilder.headIndex < 0) {
                GuiGolemBuilder.headIndex = valHeads.size() - 1;
            }
            gatherInfo();
        }
        else if (button.id == 1) {
            ++GuiGolemBuilder.headIndex;
            if (GuiGolemBuilder.headIndex >= valHeads.size()) {
                GuiGolemBuilder.headIndex = 0;
            }
            gatherInfo();
        }
        else if (button.id == 2) {
            --GuiGolemBuilder.matIndex;
            if (GuiGolemBuilder.matIndex < 0) {
                GuiGolemBuilder.matIndex = valMats.size() - 1;
            }
            gatherInfo();
        }
        else if (button.id == 3) {
            ++GuiGolemBuilder.matIndex;
            if (GuiGolemBuilder.matIndex >= valMats.size()) {
                GuiGolemBuilder.matIndex = 0;
            }
            gatherInfo();
        }
        else if (button.id == 4) {
            --GuiGolemBuilder.armIndex;
            if (GuiGolemBuilder.armIndex < 0) {
                GuiGolemBuilder.armIndex = valArms.size() - 1;
            }
            gatherInfo();
        }
        else if (button.id == 5) {
            ++GuiGolemBuilder.armIndex;
            if (GuiGolemBuilder.armIndex >= valArms.size()) {
                GuiGolemBuilder.armIndex = 0;
            }
            gatherInfo();
        }
        else if (button.id == 6) {
            --GuiGolemBuilder.legIndex;
            if (GuiGolemBuilder.legIndex < 0) {
                GuiGolemBuilder.legIndex = valLegs.size() - 1;
            }
            gatherInfo();
        }
        else if (button.id == 7) {
            ++GuiGolemBuilder.legIndex;
            if (GuiGolemBuilder.legIndex >= valLegs.size()) {
                GuiGolemBuilder.legIndex = 0;
            }
            gatherInfo();
        }
        else if (button.id == 8) {
            --GuiGolemBuilder.addonIndex;
            if (GuiGolemBuilder.addonIndex < 0) {
                GuiGolemBuilder.addonIndex = valAddons.size() - 1;
            }
            gatherInfo();
        }
        else if (button.id == 9) {
            ++GuiGolemBuilder.addonIndex;
            if (GuiGolemBuilder.addonIndex >= valAddons.size()) {
                GuiGolemBuilder.addonIndex = 0;
            }
            gatherInfo();
        }
        else if (button.id == 99 && allfound) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setLong("golem", props.toLong());
            builder.sendMessageToServer(nbt);
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 99);
            disableAll = true;
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
