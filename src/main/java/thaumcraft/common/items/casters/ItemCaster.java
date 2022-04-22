// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters;

import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import thaumcraft.api.casters.FocusEngine;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import thaumcraft.codechicken.lib.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.casters.FocusPackage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.lib.utils.BlockUtils;
import net.minecraft.init.Blocks;
import thaumcraft.api.casters.IFocusBlockPicker;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.CasterTriggerRegistry;
import thaumcraft.api.casters.IInteractWithCaster;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.common.lib.network.misc.PacketAuraToClient;
import net.minecraft.world.chunk.Chunk;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.lib.network.PacketHandler;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.item.EnumRarity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.world.aura.AuraHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.text.DecimalFormat;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.items.IArchitect;
import thaumcraft.common.items.ItemTCBase;

public class ItemCaster extends ItemTCBase implements IArchitect, ICaster
{
    int area;
    DecimalFormat myFormatter;
    ArrayList<BlockPos> checked;
    
    public ItemCaster(final String name, final int area) {
        super(name, new String[0]);
        this.area = 0;
        this.myFormatter = new DecimalFormat("#######.#");
        this.checked = new ArrayList<BlockPos>();
        this.area = area;
        this.maxStackSize = 1;
        this.setMaxDamage(0);
        this.addPropertyOverride(new ResourceLocation("focus"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(final ItemStack stack, @Nullable final World worldIn, @Nullable final EntityLivingBase entityIn) {
                final ItemFocus f = ((ItemCaster)stack.getItem()).getFocus(stack);
                if (stack.getItem() instanceof ItemCaster && f != null) {
                    return 1.0f;
                }
                return 0.0f;
            }
        });
    }
    
    public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
        if (oldStack.getItem() != null && oldStack.getItem() == this && newStack.getItem() != null && newStack.getItem() == this) {
            final ItemFocus oldf = ((ItemCaster)oldStack.getItem()).getFocus(oldStack);
            final ItemFocus newf = ((ItemCaster)newStack.getItem()).getFocus(newStack);
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
    
    private float getAuraPool(final EntityPlayer player) {
        float tot = 0.0f;
        switch (this.area) {
            default: {
                tot = AuraHandler.getVis(player.world, player.getPosition());
                break;
            }
            case 1: {
                tot = AuraHandler.getVis(player.world, player.getPosition());
                for (final EnumFacing face : EnumFacing.HORIZONTALS) {
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
    public boolean consumeVis(final ItemStack is, final EntityPlayer player, float amount, final boolean crafting, final boolean sim) {
        amount *= this.getConsumptionModifier(is, player, crafting);
        final float tot = this.getAuraPool(player);
        if (tot < amount) {
            return false;
        }
        if (sim) {
            return true;
        }
        Label_0309: {
            switch (this.area) {
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
                        for (final EnumFacing face : EnumFacing.HORIZONTALS) {
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
    public float getConsumptionModifier(final ItemStack is, final EntityPlayer player, final boolean crafting) {
        float consumptionModifier = 1.0f;
        if (player != null) {
            consumptionModifier -= CasterManager.getTotalVisDiscount(player);
        }
        return Math.max(consumptionModifier, 0.1f);
    }
    
    @Override
    public ItemFocus getFocus(final ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("focus")) {
            final NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("focus");
            final ItemStack fs = new ItemStack(nbt);
            if (fs != null && !fs.isEmpty()) {
                return (ItemFocus)fs.getItem();
            }
        }
        return null;
    }
    
    @Override
    public ItemStack getFocusStack(final ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("focus")) {
            final NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("focus");
            return new ItemStack(nbt);
        }
        return null;
    }
    
    @Override
    public void setFocus(final ItemStack stack, final ItemStack focus) {
        if (focus == null || focus.isEmpty()) {
            stack.getTagCompound().removeTag("focus");
        }
        else {
            stack.setTagInfo("focus", focus.writeToNBT(new NBTTagCompound()));
        }
    }
    
    public EnumRarity getRarity(final ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            String text = "";
            final ItemStack focus = this.getFocusStack(stack);
            if (focus != null && !focus.isEmpty()) {
                final float amt = ((ItemFocus)focus.getItem()).getVisCost(focus);
                if (amt > 0.0f) {
                    text = "§r" + this.myFormatter.format(amt) + " " + I18n.translateToLocal("item.Focus.cost1");
                }
            }
            tooltip.add(TextFormatting.ITALIC + "" + TextFormatting.AQUA + I18n.translateToLocal("tc.vis.cost") + " " + text);
        }
        if (this.getFocus(stack) != null) {
            tooltip.add(TextFormatting.BOLD + "" + TextFormatting.ITALIC + "" + TextFormatting.GREEN + this.getFocus(stack).getItemStackDisplayName(this.getFocusStack(stack)));
            this.getFocus(stack).addFocusInformation(this.getFocusStack(stack), worldIn, tooltip, flagIn);
        }
    }
    
    public void onArmorTick(final World world, final EntityPlayer player, final ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
    }
    
    public void onUpdate(final ItemStack is, final World w, final Entity e, final int slot, final boolean currentItem) {
        if (!w.isRemote && e.ticksExisted % 10 == 0 && e instanceof EntityPlayerMP) {
            for (final ItemStack h : e.getHeldEquipment()) {
                if (h != null && !h.isEmpty() && h.getItem() instanceof ICaster) {
                    this.updateAura(is, w, (EntityPlayerMP)e);
                    break;
                }
            }
        }
    }
    
    private void updateAura(final ItemStack stack, final World world, final EntityPlayerMP player) {
        float cv = 0.0f;
        float cf = 0.0f;
        short bv = 0;
        switch (this.area) {
            default: {
                final AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), (int)player.posX >> 4, (int)player.posZ >> 4);
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
                for (final EnumFacing face : EnumFacing.HORIZONTALS) {
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
                        final AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), ((int)player.posX >> 4) + xx, ((int)player.posZ >> 4) + zz);
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
    
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
        final IBlockState bs = world.getBlockState(pos);
        if (bs.getBlock() instanceof IInteractWithCaster && ((IInteractWithCaster)bs.getBlock()).onCasterRightClick(world, player.getHeldItem(hand), player, pos, side, hand)) {
            return EnumActionResult.PASS;
        }
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof IInteractWithCaster && ((IInteractWithCaster)tile).onCasterRightClick(world, player.getHeldItem(hand), player, pos, side, hand)) {
            return EnumActionResult.PASS;
        }
        if (CasterTriggerRegistry.hasTrigger(bs)) {
            return CasterTriggerRegistry.performTrigger(world, player.getHeldItem(hand), player, pos, side, bs) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
        }
        final ItemStack fb = this.getFocusStack(player.getHeldItem(hand));
        if (fb != null && !fb.isEmpty()) {
            final FocusPackage core = ItemFocus.getPackage(fb);
            for (final IFocusElement fe : core.nodes) {
                if (fe instanceof IFocusBlockPicker && player.isSneaking() && world.getTileEntity(pos) == null) {
                    if (!world.isRemote) {
                        ItemStack isout = new ItemStack(bs.getBlock(), 1, bs.getBlock().getMetaFromState(bs));
                        try {
                            if (bs != Blocks.AIR) {
                                final ItemStack is = BlockUtils.getSilkTouchDrop(bs);
                                if (is != null && !is.isEmpty()) {
                                    isout = is.copy();
                                }
                            }
                        }
                        catch (final Exception ex) {}
                        this.storePickedBlock(player.getHeldItem(hand), isout);
                        return EnumActionResult.SUCCESS;
                    }
                    player.swingArm(hand);
                    return EnumActionResult.PASS;
                }
            }
        }
        return EnumActionResult.PASS;
    }
    
    private RayTraceResult generateSourceVector(final Entity e) {
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
        final double posX = -MathHelper.cos((e.rotationYaw - 0.5f) / 180.0f * 3.141593f) * 0.20000000298023224 * (mainhand ? 1 : -1);
        final double posZ = -MathHelper.sin((e.rotationYaw - 0.5f) / 180.0f * 3.141593f) * 0.30000001192092896 * (mainhand ? 1 : -1);
        final Vec3d vl = e.getLookVec();
        v = v.addVector(posX, e.getEyeHeight() - 0.4000000014901161, posZ);
        v = v.add(vl);
        return new RayTraceResult(e, v);
    }
    
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        final ItemStack focusStack = this.getFocusStack(player.getHeldItem(hand));
        final ItemFocus focus = this.getFocus(player.getHeldItem(hand));
        if (focus == null || CasterManager.isOnCooldown(player)) {
            return super.onItemRightClick(world, player, hand);
        }
        CasterManager.setCooldown(player, focus.getActivationTime(focusStack));
        final FocusPackage core = ItemFocus.getPackage(focusStack);
        if (player.isSneaking()) {
            for (final IFocusElement fe : core.nodes) {
                if (fe instanceof IFocusBlockPicker && player.isSneaking()) {
                    return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.PASS, player.getHeldItem(hand));
                }
            }
        }
        if (world.isRemote) {
            return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }
        if (this.consumeVis(player.getHeldItem(hand), player, focus.getVisCost(focusStack), false, false)) {
            FocusEngine.castFocusPackage(player, core);
            player.swingArm(hand);
            return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand));
    }
    
    public int getMaxItemUseDuration(final ItemStack itemstack) {
        return 72000;
    }
    
    public EnumAction getItemUseAction(final ItemStack stack1) {
        return EnumAction.BOW;
    }
    
    @Override
    public ArrayList<BlockPos> getArchitectBlocks(final ItemStack stack, final World world, final BlockPos pos, final EnumFacing side, final EntityPlayer player) {
        final ItemFocus focus = this.getFocus(stack);
        if (focus != null) {
            final FocusPackage fp = ItemFocus.getPackage(this.getFocusStack(stack));
            if (fp != null) {
                for (final IFocusElement fe : fp.nodes) {
                    if (fe instanceof IArchitect) {
                        return ((IArchitect)fe).getArchitectBlocks(stack, world, pos, side, player);
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean showAxis(final ItemStack stack, final World world, final EntityPlayer player, final EnumFacing side, final EnumAxis axis) {
        final ItemFocus focus = this.getFocus(stack);
        if (focus != null) {
            final FocusPackage fp = ItemFocus.getPackage(this.getFocusStack(stack));
            if (fp != null) {
                for (final IFocusElement fe : fp.nodes) {
                    if (fe instanceof IArchitect) {
                        return ((IArchitect)fe).showAxis(stack, world, player, side, axis);
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public RayTraceResult getArchitectMOP(final ItemStack stack, final World world, final EntityLivingBase player) {
        final ItemFocus focus = this.getFocus(stack);
        if (focus != null) {
            final FocusPackage fp = ItemFocus.getPackage(this.getFocusStack(stack));
            if (fp != null && FocusEngine.doesPackageContainElement(fp, "thaumcraft.PLAN")) {
                return ((IArchitect)FocusEngine.getElement("thaumcraft.PLAN")).getArchitectMOP(this.getFocusStack(stack), world, player);
            }
        }
        return null;
    }
    
    @Override
    public boolean useBlockHighlight(final ItemStack stack) {
        return false;
    }
    
    public void storePickedBlock(final ItemStack stack, final ItemStack stackout) {
        final NBTTagCompound item = new NBTTagCompound();
        stack.setTagInfo("picked", stackout.writeToNBT(item));
    }
    
    @Override
    public ItemStack getPickedBlock(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack out = null;
        final ItemFocus focus = this.getFocus(stack);
        if (focus != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("picked")) {
            final FocusPackage fp = ItemFocus.getPackage(this.getFocusStack(stack));
            if (fp != null) {
                for (final IFocusElement fe : fp.nodes) {
                    if (fe instanceof IFocusBlockPicker) {
                        out = new ItemStack(Blocks.AIR);
                        try {
                            out = new ItemStack(stack.getTagCompound().getCompoundTag("picked"));
                        }
                        catch (final Exception ex) {}
                        break;
                    }
                }
            }
        }
        return out;
    }
}
