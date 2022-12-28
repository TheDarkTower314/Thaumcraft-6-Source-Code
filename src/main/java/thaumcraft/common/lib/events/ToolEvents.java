package thaumcraft.common.lib.events;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFollowingItem;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXScanSource;
import thaumcraft.common.lib.network.fx.PacketFXSlash;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.lib.utils.Utils;


@Mod.EventBusSubscriber
public class ToolEvents
{
    static HashMap<Integer, EnumFacing> lastFaceClicked;
    public static HashMap<Integer, ArrayList<BlockPos>> blockedBlocks;
    static boolean blockDestructiveRecursion;
    
    @SubscribeEvent
    public static void playerAttack(AttackEntityEvent event) {
        if (event.getEntityPlayer().getActiveHand() == null) {
            return;
        }
        ItemStack heldItem = event.getEntityPlayer().getHeldItem(event.getEntityPlayer().getActiveHand());
        if (heldItem != null && !heldItem.isEmpty()) {
            List<EnumInfusionEnchantment> list = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
            if (list.contains(EnumInfusionEnchantment.ARCING) && event.getTarget().isEntityAlive()) {
                int rank = EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.ARCING);
                List<Entity> targets = event.getEntityPlayer().world.getEntitiesWithinAABBExcludingEntity(event.getEntityPlayer(), event.getTarget().getEntityBoundingBox().grow(1.5 + rank, 1.0f + rank / 2.0f, 1.5 + rank));
                int count = 0;
                if (targets.size() > 1) {
                    for (int var9 = 0; var9 < targets.size(); ++var9) {
                        Entity var10 = targets.get(var9);
                        if (!var10.isDead) {
                            if (!EntityUtils.isFriendly(event.getEntity(), var10)) {
                                if (var10 instanceof EntityLiving && var10.getEntityId() != event.getTarget().getEntityId()) {
                                    if (!(var10 instanceof EntityPlayer) || var10.getName() != event.getEntityPlayer().getName()) {
                                        if (var10.isEntityAlive()) {
                                            float f = (float)event.getEntityPlayer().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                                            event.getEntityPlayer().attackEntityAsMob(var10);
                                            if (var10.attackEntityFrom(DamageSource.causePlayerDamage(event.getEntityPlayer()), f * 0.5f)) {
                                                try {
                                                    if (var10 instanceof EntityLivingBase) {
                                                        EnchantmentHelper.applyThornEnchantments((EntityLivingBase)var10, event.getEntityPlayer());
                                                    }
                                                }
                                                catch (Exception ex) {}
                                                var10.addVelocity(-MathHelper.sin(event.getEntityPlayer().rotationYaw * 3.1415927f / 180.0f) * 0.5f, 0.1, MathHelper.cos(event.getEntityPlayer().rotationYaw * 3.1415927f / 180.0f) * 0.5f);
                                                ++count;
                                                if (!event.getEntityPlayer().world.isRemote) {
                                                    PacketHandler.INSTANCE.sendToAllAround(new PacketFXSlash(event.getTarget().getEntityId(), var10.getEntityId()), new NetworkRegistry.TargetPoint(event.getEntityPlayer().world.provider.getDimension(), event.getTarget().posX, event.getTarget().posY, event.getTarget().posZ, 64.0));
                                                }
                                            }
                                        }
                                    }
                                }
                                if (count >= rank) {
                                    break;
                                }
                            }
                        }
                    }
                    if (count > 0 && !event.getEntityPlayer().world.isRemote) {
                        event.getEntityPlayer().playSound(SoundsTC.wind, 1.0f, 0.9f + event.getEntityPlayer().world.rand.nextFloat() * 0.2f);
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXSlash(event.getEntityPlayer().getEntityId(), event.getTarget().getEntityId()), new NetworkRegistry.TargetPoint(event.getEntityPlayer().world.provider.getDimension(), event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, 64.0));
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void playerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getWorld().isRemote && event.getEntityPlayer() != null) {
            ItemStack heldItem = event.getEntityPlayer().getHeldItem((event.getEntityPlayer().getActiveHand() == null) ? EnumHand.MAIN_HAND : event.getEntityPlayer().getActiveHand());
            if (heldItem != null && !heldItem.isEmpty()) {
                List<EnumInfusionEnchantment> list = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
                if (list.contains(EnumInfusionEnchantment.SOUNDING) && event.getEntityPlayer().isSneaking()) {
                    heldItem.damageItem(5, event.getEntityPlayer());
                    event.getWorld().playSound(null, event.getPos().getX() + 0.5, event.getPos().getY() + 0.5, event.getPos().getZ() + 0.5, SoundsTC.wandfail, SoundCategory.BLOCKS, 0.2f, 0.2f + event.getWorld().rand.nextFloat() * 0.2f);
                    PacketHandler.INSTANCE.sendTo(new PacketFXScanSource(event.getPos(), EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.SOUNDING)), (EntityPlayerMP)event.getEntityPlayer());
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntityPlayer() != null) {
            ToolEvents.lastFaceClicked.put(event.getEntityPlayer().getEntityId(), event.getFace());
        }
    }
    
    public static void addBlockedBlock(World world, BlockPos pos) {
        if (!ToolEvents.blockedBlocks.containsKey(world.provider.getDimension())) {
            ToolEvents.blockedBlocks.put(world.provider.getDimension(), new ArrayList<BlockPos>());
        }
        ArrayList<BlockPos> list = ToolEvents.blockedBlocks.get(world.provider.getDimension());
        if (!list.contains(pos)) {
            list.add(pos);
        }
    }
    
    public static void clearBlockedBlock(World world, BlockPos pos) {
        if (!ToolEvents.blockedBlocks.containsKey(world.provider.getDimension())) {
            ToolEvents.blockedBlocks.put(world.provider.getDimension(), new ArrayList<BlockPos>());
            return;
        }
        ArrayList<BlockPos> list = ToolEvents.blockedBlocks.get(world.provider.getDimension());
        list.remove(pos);
    }
    
    @SubscribeEvent
    public static void breakBlockEvent(BlockEvent.BreakEvent event) {
        if (ToolEvents.blockedBlocks.containsKey(event.getWorld().provider.getDimension())) {
            ArrayList<BlockPos> list = ToolEvents.blockedBlocks.get(event.getWorld().provider.getDimension());
            if (list == null) {
                list = new ArrayList<BlockPos>();
                ToolEvents.blockedBlocks.put(event.getWorld().provider.getDimension(), list);
            }
            if (list.contains(event.getPos())) {
                event.setCanceled(true);
            }
        }
        if (!event.getWorld().isRemote && event.getPlayer() != null) {
            ItemStack heldItem = event.getPlayer().getHeldItem(event.getPlayer().getActiveHand());
            if (heldItem != null && !heldItem.isEmpty()) {
                List<EnumInfusionEnchantment> list2 = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
                if (ForgeHooks.isToolEffective(event.getWorld(), event.getPos(), heldItem) && list2.contains(EnumInfusionEnchantment.BURROWING) && !event.getPlayer().isSneaking() && isValidBurrowBlock(event.getWorld(), event.getPos())) {
                    event.setCanceled(true);
                    if (!event.getPlayer().getName().equals("FakeThaumcraftBore")) {
                        heldItem.damageItem(1, event.getPlayer());
                    }
                    BlockUtils.breakFurthestBlock(event.getWorld(), event.getPos(), event.getState(), event.getPlayer());
                }
            }
        }
    }
    
    private static boolean isValidBurrowBlock(World world, BlockPos pos) {
        return Utils.isWoodLog(world, pos) || Utils.isOreBlock(world, pos);
    }
    
    @SubscribeEvent
    public static void harvestBlockEvent(BlockEvent.HarvestDropsEvent event) {
        if (!event.getWorld().isRemote && !event.isSilkTouching() && event.getState().getBlock() != null && ((event.getState().getBlock() == Blocks.DIAMOND_ORE && event.getWorld().rand.nextFloat() < 0.05) || (event.getState().getBlock() == Blocks.EMERALD_ORE && event.getWorld().rand.nextFloat() < 0.075) || (event.getState().getBlock() == Blocks.LAPIS_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.COAL_ORE && event.getWorld().rand.nextFloat() < 0.001) || (event.getState().getBlock() == Blocks.LIT_REDSTONE_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.REDSTONE_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.QUARTZ_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == BlocksTC.oreAmber && event.getWorld().rand.nextFloat() < 0.05) || (event.getState().getBlock() == BlocksTC.oreQuartz && event.getWorld().rand.nextFloat() < 0.05))) {
            event.getDrops().add(new ItemStack(ItemsTC.nuggets, 1, 10));
        }
        if (!event.getWorld().isRemote && event.getHarvester() != null) {
            ItemStack heldItem = event.getHarvester().getHeldItem(event.getHarvester().getActiveHand());
            if (heldItem != null && !heldItem.isEmpty()) {
                List<EnumInfusionEnchantment> list = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
                if (event.isSilkTouching() || ForgeHooks.isToolEffective(event.getWorld(), event.getPos(), heldItem) || (heldItem.getItem() instanceof ItemTool && heldItem.getItem().getDestroySpeed(heldItem, event.getState()) > 1.0f)) {
                    if (list.contains(EnumInfusionEnchantment.REFINING)) {
                        int fortune = 1 + EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.REFINING);
                        float chance = fortune * 0.125f;
                        boolean b = false;
                        for (int a = 0; a < event.getDrops().size(); ++a) {
                            ItemStack is = event.getDrops().get(a);
                            ItemStack smr = Utils.findSpecialMiningResult(is, chance, event.getWorld().rand);
                            if (!is.isItemEqual(smr)) {
                                event.getDrops().set(a, smr);
                                b = true;
                            }
                        }
                        if (b) {
                            event.getWorld().playSound(null, event.getPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.2f, 0.7f + event.getWorld().rand.nextFloat() * 0.2f);
                        }
                    }
                    if (!ToolEvents.blockDestructiveRecursion && list.contains(EnumInfusionEnchantment.DESTRUCTIVE) && !event.getHarvester().isSneaking()) {
                        ToolEvents.blockDestructiveRecursion = true;
                        EnumFacing face = ToolEvents.lastFaceClicked.get(event.getHarvester().getEntityId());
                        if (face == null) {
                            face = EnumFacing.getDirectionFromEntityLiving(event.getPos(), event.getHarvester());
                        }
                        for (int aa = -1; aa <= 1; ++aa) {
                            for (int bb = -1; bb <= 1; ++bb) {
                                if (aa != 0 || bb != 0) {
                                    int xx = 0;
                                    int yy = 0;
                                    int zz = 0;
                                    if (face.ordinal() <= 1) {
                                        xx = aa;
                                        zz = bb;
                                    }
                                    else if (face.ordinal() <= 3) {
                                        xx = aa;
                                        yy = bb;
                                    }
                                    else {
                                        zz = aa;
                                        yy = bb;
                                    }
                                    IBlockState bl = event.getWorld().getBlockState(event.getPos().add(xx, yy, zz));
                                    if (bl.getBlockHardness(event.getWorld(), event.getPos().add(xx, yy, zz)) >= 0.0f && (ForgeHooks.isToolEffective(event.getWorld(), event.getPos().add(xx, yy, zz), heldItem) || (heldItem.getItem() instanceof ItemTool && heldItem.getItem().getDestroySpeed(heldItem, bl) > 1.0f))) {
                                        if (event.getHarvester().getName().equals("FakeThaumcraftBore")) {
                                            EntityPlayer harvester = event.getHarvester();
                                            ++harvester.xpCooldown;
                                        }
                                        else {
                                            heldItem.damageItem(1, event.getHarvester());
                                        }
                                        BlockUtils.harvestBlock(event.getWorld(), event.getHarvester(), event.getPos().add(xx, yy, zz));
                                    }
                                }
                            }
                        }
                        ToolEvents.blockDestructiveRecursion = false;
                    }
                    if (list.contains(EnumInfusionEnchantment.COLLECTOR) && !event.getHarvester().isSneaking()) {
                        InventoryUtils.dropHarvestsAtPos(event.getWorld(), event.getPos(), event.getDrops(), true, 10, event.getHarvester());
                        event.getDrops().clear();
                    }
                    if (list.contains(EnumInfusionEnchantment.LAMPLIGHT) && !event.getHarvester().isSneaking() && event.getHarvester() instanceof EntityPlayerMP) {
                        IThreadListener mainThread = ((EntityPlayerMP)event.getHarvester()).getServerWorld();
                        mainThread.addScheduledTask(new Runnable() {
                            @Override
                            public void run() {
                                if (event.getWorld().isAirBlock(event.getPos()) && event.getWorld().getBlockState(event.getPos()) != BlocksTC.effectGlimmer.getDefaultState() && event.getWorld().getLight(event.getPos()) < 10) {
                                    event.getWorld().setBlockState(event.getPos(), BlocksTC.effectGlimmer.getDefaultState(), 3);
                                }
                            }
                        });
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void livingDrops(LivingDropsEvent event) {
        if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
            ItemStack heldItem = ((EntityPlayer)event.getSource().getTrueSource()).getHeldItem(((EntityPlayer)event.getSource().getTrueSource()).getActiveHand());
            if (heldItem != null && !heldItem.isEmpty()) {
                List<EnumInfusionEnchantment> list = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
                if (list.contains(EnumInfusionEnchantment.COLLECTOR)) {
                    for (int a = 0; a < event.getDrops().size(); ++a) {
                        EntityItem ei = event.getDrops().get(a);
                        ItemStack is = ei.getItem().copy();
                        EntityItem nei = new EntityFollowingItem(event.getEntity().world, ei.posX, ei.posY, ei.posZ, is, event.getSource().getTrueSource(), 10);
                        nei.motionX = ei.motionX;
                        nei.motionY = ei.motionY;
                        nei.motionZ = ei.motionZ;
                        nei.setDefaultPickupDelay();
                        ei.setDead();
                        event.getDrops().set(a, nei);
                    }
                }
                if (list.contains(EnumInfusionEnchantment.ESSENCE)) {
                    AspectList as = AspectHelper.getEntityAspects(event.getEntityLiving());
                    if (as != null && as.size() > 0) {
                        AspectList aspects = as.copy();
                        int q = EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.ESSENCE);
                        Aspect[] al = aspects.getAspects();
                        for (int b = (event.getEntity().world.rand.nextInt(5) < q) ? 0 : 99; b < q && al != null && al.length > 0; b += 1 + event.getEntity().world.rand.nextInt(2)) {
                            Aspect aspect = al[event.getEntity().world.rand.nextInt(al.length)];
                            if (aspects.getAmount(aspect) > 0) {
                                aspects.remove(aspect, 1);
                                ItemStack stack = ThaumcraftApiHelper.makeCrystal(aspect);
                                if (list.contains(EnumInfusionEnchantment.COLLECTOR)) {
                                    event.getDrops().add(new EntityFollowingItem(event.getEntity().world, event.getEntityLiving().posX, event.getEntityLiving().posY + event.getEntityLiving().getEyeHeight(), event.getEntityLiving().posZ, stack, event.getSource().getTrueSource(), 10));
                                }
                                else {
                                    event.getDrops().add(new EntityItem(event.getEntity().world, event.getEntityLiving().posX, event.getEntityLiving().posY + event.getEntityLiving().getEyeHeight(), event.getEntityLiving().posZ, stack));
                                }
                                ++b;
                            }
                            al = aspects.getAspects();
                            if (event.getEntity().world.rand.nextInt(q) == 0) {}
                        }
                    }
                }
            }
        }
    }
    
    static {
        ToolEvents.lastFaceClicked = new HashMap<Integer, EnumFacing>();
        ToolEvents.blockedBlocks = new HashMap<Integer, ArrayList<BlockPos>>();
        ToolEvents.blockDestructiveRecursion = false;
    }
}
