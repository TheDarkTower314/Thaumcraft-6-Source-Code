package thaumcraft.common.golems.client.gui;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.Thaumcraft;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.container.slot.SlotGhost;


public class SealBaseContainer extends Container
{
    private World world;
    ISealEntity seal;
    EntityPlayer player;
    InventoryFake temp;
    int[] categories;
    int category;
    InventoryPlayer pinv;
    int t;
    private byte lastPriority;
    private byte lastColor;
    private int lastAreaX;
    private int lastAreaY;
    private int lastAreaZ;
    
    public SealBaseContainer(InventoryPlayer iinventory, World par2World, ISealEntity seal) {
        this.seal = null;
        player = null;
        category = -1;
        t = 0;
        world = par2World;
        player = iinventory.player;
        pinv = iinventory;
        this.seal = seal;
        if (seal.getSeal() instanceof ISealGui) {
            categories = ((ISealGui)seal.getSeal()).getGuiCategories();
        }
        else {
            categories = new int[] { 0 };
        }
        setupCategories();
    }
    
    void setupCategories() {
        inventoryItemStacks = NonNullList.create();
        inventorySlots = Lists.newArrayList();
        t = 0;
        if (category < 0) {
            category = categories[0];
        }
        switch (category) {
            case 1: {
                setupFilterInventory();
                break;
            }
        }
        bindPlayerInventory(pinv);
    }
    
    private void setupFilterInventory() {
        if (seal.getSeal() instanceof ISealConfigFilter) {
            int s = ((ISealConfigFilter) seal.getSeal()).getFilterSize();
            int sx = 16 + (s - 1) % 3 * 12;
            int sy = 16 + (s - 1) / 3 * 12;
            int middleX = 88;
            int middleY = 72;
            temp = new InventoryFake(((ISealConfigFilter) seal.getSeal()).getInv());
            for (int a = 0; a < s; ++a) {
                int x = a % 3;
                int y = a / 3;
                addSlotToContainer(new SlotGhost(temp, a, middleX + x * 24 - sx + 8, middleY + y * 24 - sy + 8));
                ++t;
            }
        }
    }
    
    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 150 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 208));
        }
    }
    
    public boolean canInteractWith(EntityPlayer var1) {
        return true;
    }
    
    public boolean enchantItem(EntityPlayer player, int par2) {
        if (par2 >= 0 && par2 < categories.length) {
            category = categories[par2];
            setupCategories();
            return true;
        }
        if (category == 3 && seal.getSeal() instanceof ISealConfigToggles && par2 >= 30 && par2 < 30 + ((ISealConfigToggles) seal.getSeal()).getToggles().length) {
            ISealConfigToggles cp = (ISealConfigToggles) seal.getSeal();
            cp.setToggle(par2 - 30, true);
            return true;
        }
        if (category == 3 && seal.getSeal() instanceof ISealConfigToggles && par2 >= 60 && par2 < 60 + ((ISealConfigToggles) seal.getSeal()).getToggles().length) {
            ISealConfigToggles cp = (ISealConfigToggles) seal.getSeal();
            cp.setToggle(par2 - 60, false);
            return true;
        }
        if (category == 0 && par2 >= 25 && par2 <= 26) {
            seal.setLocked(par2 == 25);
            return true;
        }
        if (par2 >= 27 && par2 <= 28) {
            seal.setRedstoneSensitive(par2 == 27);
            return true;
        }
        if (category == 1 && seal.getSeal() instanceof ISealConfigFilter && par2 >= 20 && par2 <= 21) {
            ISealConfigFilter cp2 = (ISealConfigFilter) seal.getSeal();
            cp2.setBlacklist(par2 == 20);
            return true;
        }
        if (par2 == 80 && seal.getPriority() > -5) {
            seal.setPriority((byte)(seal.getPriority() - 1));
            return true;
        }
        if (par2 == 81 && seal.getPriority() < 5) {
            seal.setPriority((byte)(seal.getPriority() + 1));
            return true;
        }
        if (par2 == 82 && seal.getColor() > 0) {
            seal.setColor((byte)(seal.getColor() - 1));
            return true;
        }
        if (par2 == 83 && seal.getColor() < 16) {
            seal.setColor((byte)(seal.getColor() + 1));
            return true;
        }
        if (seal.getSeal() instanceof ISealConfigArea) {
            if (par2 == 90 && seal.getArea().getY() > 1) {
                seal.setArea(seal.getArea().add(0, -1, 0));
                return true;
            }
            if (par2 == 91 && seal.getArea().getY() < 8) {
                seal.setArea(seal.getArea().add(0, 1, 0));
                return true;
            }
            if (par2 == 92 && seal.getArea().getX() > 1) {
                seal.setArea(seal.getArea().add(-1, 0, 0));
                return true;
            }
            if (par2 == 93 && seal.getArea().getX() < 8) {
                seal.setArea(seal.getArea().add(1, 0, 0));
                return true;
            }
            if (par2 == 94 && seal.getArea().getZ() > 1) {
                seal.setArea(seal.getArea().add(0, 0, -1));
                return true;
            }
            if (par2 == 95 && seal.getArea().getZ() < 8) {
                seal.setArea(seal.getArea().add(0, 0, 1));
                return true;
            }
        }
        return super.enchantItem(player, par2);
    }
    
    public void addListener(IContainerListener crafting) {
        super.addListener(crafting);
        crafting.sendWindowProperty(this, 0, seal.getPriority());
        crafting.sendWindowProperty(this, 4, seal.getColor());
    }
    
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < listeners.size(); ++i) {
            IContainerListener icrafting = listeners.get(i);
            if (lastPriority != seal.getPriority()) {
                icrafting.sendWindowProperty(this, 0, seal.getPriority());
            }
            if (lastAreaX != seal.getArea().getX()) {
                icrafting.sendWindowProperty(this, 1, seal.getArea().getX());
            }
            if (lastAreaY != seal.getArea().getY()) {
                icrafting.sendWindowProperty(this, 2, seal.getArea().getY());
            }
            if (lastAreaZ != seal.getArea().getZ()) {
                icrafting.sendWindowProperty(this, 3, seal.getArea().getZ());
            }
            if (lastColor != seal.getColor()) {
                icrafting.sendWindowProperty(this, 4, seal.getColor());
            }
        }
        lastPriority = seal.getPriority();
        lastColor = seal.getColor();
        lastAreaX = seal.getArea().getX();
        lastAreaY = seal.getArea().getY();
        lastAreaZ = seal.getArea().getZ();
        if (seal.getSeal() instanceof ISealConfigFilter && temp != null) {
            for (int a = 0; a < temp.getSizeInventory(); ++a) {
                ((ISealConfigFilter) seal.getSeal()).setFilterSlot(a, temp.getStackInSlot(a));
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            seal.setPriority((byte)par2);
        }
        if (par1 == 1) {
            seal.setArea(new BlockPos(par2, seal.getArea().getY(), seal.getArea().getZ()));
        }
        if (par1 == 2) {
            seal.setArea(new BlockPos(seal.getArea().getX(), par2, seal.getArea().getZ()));
        }
        if (par1 == 3) {
            seal.setArea(new BlockPos(seal.getArea().getX(), seal.getArea().getY(), par2));
        }
        if (par1 == 4) {
            seal.setColor((byte)par2);
        }
    }
    
    public ItemStack slotClick(int slotId, int clickedButton, ClickType mode, EntityPlayer playerIn) {
        if (slotId >= 0) {
            Slot slot = inventorySlots.get(slotId);
            InventoryPlayer inventoryplayer = playerIn.inventory;
            ItemStack ic = ItemStack.EMPTY;
            if (inventoryplayer.getItemStack() != null && !inventoryplayer.getItemStack().isEmpty()) {
                ic = inventoryplayer.getItemStack().copy();
            }
            if (slot != null && slot instanceof SlotGhost) {
                boolean filter = ((ISealConfigFilter) seal.getSeal()).hasStacksizeLimiters();
                if (playerIn.world.isRemote) {
                    Thaumcraft instance = Thaumcraft.instance;
                    if (Thaumcraft.proxy.getSingleplayer()) {
                        return ItemStack.EMPTY;
                    }
                }
                if (clickedButton == 1) {
                    if (!filter) {
                        slot.putStack(ItemStack.EMPTY);
                        ((ISealConfigFilter) seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                    }
                    else if (ic == ItemStack.EMPTY) {
                        if (slot.getHasStack()) {
                            ((ISealConfigFilter) seal.getSeal()).setFilterSlotSize(slot.slotNumber, ((ISealConfigFilter) seal.getSeal()).getFilterSlotSize(slot.slotNumber) - ((mode == ClickType.QUICK_MOVE) ? 10 : 1));
                            if (((ISealConfigFilter) seal.getSeal()).getFilterSlotSize(slot.slotNumber) < 0) {
                                slot.putStack(ItemStack.EMPTY);
                                ((ISealConfigFilter) seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                            }
                        }
                    }
                    else if (slot.getHasStack() && slot.getStack().getCount() == 0) {
                        slot.putStack(ItemStack.EMPTY);
                        ((ISealConfigFilter) seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                    }
                    else if (slot.getHasStack() && ItemStack.areItemsEqual(ic, slot.getStack()) && ItemStack.areItemStackTagsEqual(ic, slot.getStack())) {
                        ((ISealConfigFilter) seal.getSeal()).setFilterSlotSize(slot.slotNumber, ((ISealConfigFilter) seal.getSeal()).getFilterSlotSize(slot.slotNumber) - ic.getCount());
                        if (((ISealConfigFilter) seal.getSeal()).getFilterSlotSize(slot.slotNumber) < 0) {
                            slot.putStack(ItemStack.EMPTY);
                            ((ISealConfigFilter) seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                        }
                    }
                }
                else if (ic == ItemStack.EMPTY) {
                    if (filter && slot.getHasStack()) {
                        ((ISealConfigFilter) seal.getSeal()).setFilterSlotSize(slot.slotNumber, ((ISealConfigFilter) seal.getSeal()).getFilterSlotSize(slot.slotNumber) + ((mode == ClickType.QUICK_MOVE) ? 10 : 1));
                    }
                }
                else {
                    if (!filter) {
                        ic.setCount(1);
                        ((ISealConfigFilter) seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                    }
                    else {
                        int os = ic.getCount();
                        ic.setCount(1);
                        if (slot.getHasStack() && ItemStack.areItemsEqual(ic, slot.getStack()) && ItemStack.areItemStackTagsEqual(ic, slot.getStack())) {
                            ((ISealConfigFilter) seal.getSeal()).setFilterSlotSize(slot.slotNumber, ((ISealConfigFilter) seal.getSeal()).getFilterSlotSize(slot.slotNumber) + os);
                        }
                        else {
                            ((ISealConfigFilter) seal.getSeal()).setFilterSlotSize(slot.slotNumber, 0);
                        }
                    }
                    slot.putStack(ic);
                }
                if (slot.getHasStack() && slot.getStack().getCount() < 0) {
                    slot.getStack().setCount(0);
                }
                detectAndSendChanges();
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotId, clickedButton, mode, playerIn);
    }
    
    public ItemStack transferStackInSlot(EntityPlayer player, int par2) {
        ItemStack itemstack = null;
        Slot slot = inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (itemstack2.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }
            if (itemstack2.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemstack2);
        }
        return itemstack;
    }
}
