package thaumcraft.common.items.casters;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.casters.CasterTriggerRegistry;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.casters.IFocusBlockPicker;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.api.items.IArchitect;
import thaumcraft.codechicken.lib.math.MathHelper;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketAuraToClient;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;


public class ItemCaster extends ItemTCBase implements IArchitect, ICaster
{
    int area;
    DecimalFormat myFormatter;
    ArrayList<BlockPos> checked;
    
    public ItemCaster(String name, int area) {
        super(name);
        this.area = 0;
        myFormatter = new DecimalFormat("#######.#");
        checked = new ArrayList<BlockPos>();
        this.area = area;
        maxStackSize = 1;
        setMaxDamage(0);
        addPropertyOverride(new ResourceLocation("focus"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                ItemFocus f = ((ItemCaster)stack.getItem()).getFocus(stack);
                if (stack.getItem() instanceof ItemCaster && f != null) {
                    return 1.0f;
                }
                return 0.0f;
            }
        });
    }
    
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.getItem() != null && oldStack.getItem() == this && newStack.getItem() != null && newStack.getItem() == this) {
            ItemFocus oldf = ((ItemCaster)oldStack.getItem()).getFocus(oldStack);
            ItemFocus newf = ((ItemCaster)newStack.getItem()).getFocus(newStack);
            int s1 = 0;
            int s2 = 0;
            if (oldf != null && oldf.getSortingHelper(((ItemCaster)oldStack.getItem()).getFocusStack(oldStack)) != null) {
                s1 = oldf.getSortingHelper(((ItemCaster)oldStack.getItem()).getFocusStack(oldStack)).hashCode();
            }
            if (newf != null && newf.getSortingHelper(((ItemCaster)newStack.getItem()).getFocusStack(newStack)) != null) {
                s2 = newf.getSortingHelper(((ItemCaster)newStack.getItem()).getFocusStack(newStack)).hashCode();
            }
            return s1 != s2;
        }
        return newStack.getItem() != oldStack.getItem();
    }
    
    public boolean isDamageable() {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }
    
    private float getAuraPool(EntityPlayer player) {
        float tot = 0.0f;
        switch (area) {
            default: {
                tot = AuraHandler.getVis(player.world, player.getPosition());
                break;
            }
            case 1: {
                tot = AuraHandler.getVis(player.world, player.getPosition());
                for (EnumFacing face : EnumFacing.HORIZONTALS) {
                    tot += AuraHandler.getVis(player.world, player.getPosition().offset(face, 16));
                }
                break;
            }
            case 2: {
                tot = 0.0f;
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int zz = -1; zz <= 1; ++zz) {
                        tot += AuraHandler.getVis(player.world, player.getPosition().add(xx * 16, 0, zz * 16));
                    }
                }
                break;
            }
        }
        return tot;
    }
    
    @Override
    public boolean consumeVis(ItemStack is, EntityPlayer player, float amount, boolean crafting, boolean sim) {
        amount *= getConsumptionModifier(is, player, crafting);
        float tot = getAuraPool(player);
        if (tot < amount) {
            return false;
        }
        if (sim) {
            return true;
        }
        Label_0309: {
            switch (area) {
                default: {
                    amount -= AuraHandler.drainVis(player.world, player.getPosition(), amount, sim);
                    break;
                }
                case 1: {
                    float i = amount / 5.0f;
                    while (amount > 0.0f) {
                        if (i > amount) {
                            i = amount;
                        }
                        amount -= AuraHandler.drainVis(player.world, player.getPosition(), i, sim);
                        if (amount <= 0.0f) {
                            break;
                        }
                        if (i > amount) {
                            i = amount;
                        }
                        for (EnumFacing face : EnumFacing.HORIZONTALS) {
                            amount -= AuraHandler.drainVis(player.world, player.getPosition().offset(face, 16), i, sim);
                            if (amount <= 0.0f) {
                                break Label_0309;
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    float i = amount / 9.0f;
                    while (amount > 0.0f) {
                        if (i > amount) {
                            i = amount;
                        }
                        for (int xx = -1; xx <= 1; ++xx) {
                            for (int zz = -1; zz <= 1; ++zz) {
                                amount -= AuraHandler.drainVis(player.world, player.getPosition().add(xx * 16, 0, zz * 16), i, sim);
                                if (amount <= 0.0f) {
                                    break Label_0309;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return amount <= 0.0f;
    }
    
    @Override
    public float getConsumptionModifier(ItemStack is, EntityPlayer player, boolean crafting) {
        float consumptionModifier = 1.0f;
        if (player != null) {
            consumptionModifier -= CasterManager.getTotalVisDiscount(player);
        }
        return Math.max(consumptionModifier, 0.1f);
    }
    
    @Override
    public ItemFocus getFocus(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("focus")) {
            NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("focus");
            ItemStack fs = new ItemStack(nbt);
            if (fs != null && !fs.isEmpty()) {
                return (ItemFocus)fs.getItem();
            }
        }
        return null;
    }
    
    @Override
    public ItemStack getFocusStack(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("focus")) {
            NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("focus");
            return new ItemStack(nbt);
        }
        return null;
    }
    
    @Override
    public void setFocus(ItemStack stack, ItemStack focus) {
        if (focus == null || focus.isEmpty()) {
            stack.getTagCompound().removeTag("focus");
        }
        else {
            stack.setTagInfo("focus", focus.writeToNBT(new NBTTagCompound()));
        }
    }
    
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            String text = "";
            ItemStack focus = getFocusStack(stack);
            if (focus != null && !focus.isEmpty()) {
                float amt = ((ItemFocus)focus.getItem()).getVisCost(focus);
                if (amt > 0.0f) {
                    text = "§r" + myFormatter.format(amt) + " " + I18n.translateToLocal("item.Focus.cost1");
                }
            }
            tooltip.add(TextFormatting.ITALIC + "" + TextFormatting.AQUA + I18n.translateToLocal("tc.vis.cost") + " " + text);
        }
        if (getFocus(stack) != null) {
            tooltip.add(TextFormatting.BOLD + "" + TextFormatting.ITALIC + "" + TextFormatting.GREEN + getFocus(stack).getItemStackDisplayName(getFocusStack(stack)));
            getFocus(stack).addFocusInformation(getFocusStack(stack), worldIn, tooltip, flagIn);
        }
    }
    
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
    }
    
    public void onUpdate(ItemStack is, World w, Entity e, int slot, boolean currentItem) {
        if (!w.isRemote && e.ticksExisted % 10 == 0 && e instanceof EntityPlayerMP) {
            for (ItemStack h : e.getHeldEquipment()) {
                if (h != null && !h.isEmpty() && h.getItem() instanceof ICaster) {
                    updateAura(is, w, (EntityPlayerMP)e);
                    break;
                }
            }
        }
    }
    
    private void updateAura(ItemStack stack, World world, EntityPlayerMP player) {
        float cv = 0.0f;
        float cf = 0.0f;
        short bv = 0;
        switch (area) {
            default: {
                AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), (int)player.posX >> 4, (int)player.posZ >> 4);
                if (ac == null) {
                    break;
                }
                cv = ac.getVis();
                cf = ac.getFlux();
                bv = ac.getBase();
                break;
            }
            case 1: {
                AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), (int)player.posX >> 4, (int)player.posZ >> 4);
                if (ac == null) {
                    break;
                }
                cv = ac.getVis();
                cf = ac.getFlux();
                bv = ac.getBase();
                for (EnumFacing face : EnumFacing.HORIZONTALS) {
                    ac = AuraHandler.getAuraChunk(world.provider.getDimension(), ((int)player.posX >> 4) + face.getFrontOffsetX(), ((int)player.posZ >> 4) + face.getFrontOffsetZ());
                    if (ac != null) {
                        cv += ac.getVis();
                        cf += ac.getFlux();
                        bv += ac.getBase();
                    }
                }
                break;
            }
            case 2: {
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int zz = -1; zz <= 1; ++zz) {
                        AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), ((int)player.posX >> 4) + xx, ((int)player.posZ >> 4) + zz);
                        if (ac != null) {
                            cv += ac.getVis();
                            cf += ac.getFlux();
                            bv += ac.getBase();
                        }
                    }
                }
                break;
            }
        }
        PacketHandler.INSTANCE.sendTo(new PacketAuraToClient(new AuraChunk(null, bv, cv, cf)), player);
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState bs = world.getBlockState(pos);
        if (bs.getBlock() instanceof IInteractWithCaster && ((IInteractWithCaster)bs.getBlock()).onCasterRightClick(world, player.getHeldItem(hand), player, pos, side, hand)) {
            return EnumActionResult.PASS;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof IInteractWithCaster && ((IInteractWithCaster)tile).onCasterRightClick(world, player.getHeldItem(hand), player, pos, side, hand)) {
            return EnumActionResult.PASS;
        }
        if (CasterTriggerRegistry.hasTrigger(bs)) {
            return CasterTriggerRegistry.performTrigger(world, player.getHeldItem(hand), player, pos, side, bs) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
        }
        ItemStack fb = getFocusStack(player.getHeldItem(hand));
        if (fb != null && !fb.isEmpty()) {
            FocusPackage core = ItemFocus.getPackage(fb);
            for (IFocusElement fe : core.nodes) {
                if (fe instanceof IFocusBlockPicker && player.isSneaking() && world.getTileEntity(pos) == null) {
                    if (!world.isRemote) {
                        ItemStack isout = new ItemStack(bs.getBlock(), 1, bs.getBlock().getMetaFromState(bs));
                        try {
                            if (bs != Blocks.AIR) {
                                ItemStack is = BlockUtils.getSilkTouchDrop(bs);
                                if (is != null && !is.isEmpty()) {
                                    isout = is.copy();
                                }
                            }
                        }
                        catch (Exception ex) {}
                        storePickedBlock(player.getHeldItem(hand), isout);
                        return EnumActionResult.SUCCESS;
                    }
                    player.swingArm(hand);
                    return EnumActionResult.PASS;
                }
            }
        }
        return EnumActionResult.PASS;
    }
    
    private RayTraceResult generateSourceVector(Entity e) {
        Vec3d v = e.getPositionVector();
        boolean mainhand = true;
        if (e instanceof EntityPlayer) {
            if (((EntityPlayer)e).getHeldItemMainhand() != null && ((EntityPlayer)e).getHeldItemMainhand().getItem() instanceof ICaster) {
                mainhand = true;
            }
            else if (((EntityPlayer)e).getHeldItemOffhand() != null && ((EntityPlayer)e).getHeldItemOffhand().getItem() instanceof ICaster) {
                mainhand = false;
            }
        }
        double posX = -MathHelper.cos((e.rotationYaw - 0.5f) / 180.0f * 3.141593f) * 0.20000000298023224 * (mainhand ? 1 : -1);
        double posZ = -MathHelper.sin((e.rotationYaw - 0.5f) / 180.0f * 3.141593f) * 0.30000001192092896 * (mainhand ? 1 : -1);
        Vec3d vl = e.getLookVec();
        v = v.addVector(posX, e.getEyeHeight() - 0.4000000014901161, posZ);
        v = v.add(vl);
        return new RayTraceResult(e, v);
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack focusStack = getFocusStack(player.getHeldItem(hand));
        ItemFocus focus = getFocus(player.getHeldItem(hand));
        if (focus == null || CasterManager.isOnCooldown(player)) {
            return super.onItemRightClick(world, player, hand);
        }
        CasterManager.setCooldown(player, focus.getActivationTime(focusStack));
        FocusPackage core = ItemFocus.getPackage(focusStack);
        if (player.isSneaking()) {
            for (IFocusElement fe : core.nodes) {
                if (fe instanceof IFocusBlockPicker && player.isSneaking()) {
                    return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.PASS, player.getHeldItem(hand));
                }
            }
        }
        if (world.isRemote) {
            return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }
        if (consumeVis(player.getHeldItem(hand), player, focus.getVisCost(focusStack), false, false)) {
            FocusEngine.castFocusPackage(player, core);
            player.swingArm(hand);
            return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand));
    }
    
    public int getMaxItemUseDuration(ItemStack itemstack) {
        return 72000;
    }
    
    public EnumAction getItemUseAction(ItemStack stack1) {
        return EnumAction.BOW;
    }
    
    @Override
    public ArrayList<BlockPos> getArchitectBlocks(ItemStack stack, World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        ItemFocus focus = getFocus(stack);
        if (focus != null) {
            FocusPackage fp = ItemFocus.getPackage(getFocusStack(stack));
            if (fp != null) {
                for (IFocusElement fe : fp.nodes) {
                    if (fe instanceof IArchitect) {
                        return ((IArchitect)fe).getArchitectBlocks(stack, world, pos, side, player);
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean showAxis(ItemStack stack, World world, EntityPlayer player, EnumFacing side, EnumAxis axis) {
        ItemFocus focus = getFocus(stack);
        if (focus != null) {
            FocusPackage fp = ItemFocus.getPackage(getFocusStack(stack));
            if (fp != null) {
                for (IFocusElement fe : fp.nodes) {
                    if (fe instanceof IArchitect) {
                        return ((IArchitect)fe).showAxis(stack, world, player, side, axis);
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public RayTraceResult getArchitectMOP(ItemStack stack, World world, EntityLivingBase player) {
        ItemFocus focus = getFocus(stack);
        if (focus != null) {
            FocusPackage fp = ItemFocus.getPackage(getFocusStack(stack));
            if (fp != null && FocusEngine.doesPackageContainElement(fp, "thaumcraft.PLAN")) {
                return ((IArchitect)FocusEngine.getElement("thaumcraft.PLAN")).getArchitectMOP(getFocusStack(stack), world, player);
            }
        }
        return null;
    }
    
    @Override
    public boolean useBlockHighlight(ItemStack stack) {
        return false;
    }
    
    public void storePickedBlock(ItemStack stack, ItemStack stackout) {
        NBTTagCompound item = new NBTTagCompound();
        stack.setTagInfo("picked", stackout.writeToNBT(item));
    }
    
    @Override
    public ItemStack getPickedBlock(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack out = null;
        ItemFocus focus = getFocus(stack);
        if (focus != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("picked")) {
            FocusPackage fp = ItemFocus.getPackage(getFocusStack(stack));
            if (fp != null) {
                for (IFocusElement fe : fp.nodes) {
                    if (fe instanceof IFocusBlockPicker) {
                        out = new ItemStack(Blocks.AIR);
                        try {
                            out = new ItemStack(stack.getTagCompound().getCompoundTag("picked"));
                        }
                        catch (Exception ex) {}
                        break;
                    }
                }
            }
        }
        return out;
    }
}
