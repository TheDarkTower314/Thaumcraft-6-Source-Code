package thaumcraft.common.lib.events;
import baubles.api.BaublesApi;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityMindSpider;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;
import thaumcraft.common.lib.potions.PotionBlurredVision;
import thaumcraft.common.lib.potions.PotionDeathGaze;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;
import thaumcraft.common.lib.potions.PotionSunScorned;
import thaumcraft.common.lib.potions.PotionThaumarhia;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;
import thaumcraft.common.lib.utils.EntityUtils;


public class WarpEvents
{
    public static void checkWarpEvent(EntityPlayer player) {
        IPlayerWarp wc = ThaumcraftCapabilities.getWarp(player);
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, -1, IPlayerWarp.EnumWarpType.TEMPORARY);
        int tw = wc.get(IPlayerWarp.EnumWarpType.TEMPORARY);
        int nw = wc.get(IPlayerWarp.EnumWarpType.NORMAL);
        int pw = wc.get(IPlayerWarp.EnumWarpType.PERMANENT);
        int warp = tw + nw + pw;
        int actualwarp = pw + nw;
        int gearWarp = getWarpFromGear(player);
        warp += gearWarp;
        int warpCounter = wc.getCounter();
        int r = player.world.rand.nextInt(100);
        if (warpCounter > 0 && warp > 0 && r <= Math.sqrt(warpCounter)) {
            warp = Math.min(100, (warp + warp + warpCounter) / 3);
            warpCounter -= (int)Math.max(5.0, Math.sqrt(warpCounter) * 2.0 - gearWarp * 2);
            wc.setCounter(warpCounter);
            int eff = player.world.rand.nextInt(warp) + gearWarp;
            ItemStack helm = player.inventory.armorInventory.get(3);
            if (helm.getItem() instanceof ItemFortressArmor && helm.hasTagCompound() && helm.getTagCompound().hasKey("mask") && helm.getTagCompound().getInteger("mask") == 0) {
                eff -= 2 + player.world.rand.nextInt(4);
            }
            PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((byte)0), (EntityPlayerMP)player);
            if (eff > 0) {
                if (eff <= 4) {
                    if (!ModConfig.CONFIG_GRAPHICS.nostress) {
                        player.world.playSound(player, player.getPosition(), SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.AMBIENT, 1.0f, 0.5f);
                    }
                }
                else if (eff <= 8) {
                    if (!ModConfig.CONFIG_GRAPHICS.nostress) {
                        player.world.playSound(player, player.posX + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 10.0f, player.posY + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 10.0f, player.posZ + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 10.0f, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT, 4.0f, (1.0f + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2f) * 0.7f);
                    }
                }
                else if (eff <= 12) {
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.11")), true);
                }
                else if (eff <= 16) {
                    PotionEffect pe = new PotionEffect(PotionVisExhaust.instance, 5000, Math.min(3, warp / 15), true, true);
                    pe.getCurativeItems().clear();
                    try {
                        player.addPotionEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.1")), true);
                }
                else if (eff <= 20) {
                    PotionEffect pe = new PotionEffect(PotionThaumarhia.instance, Math.min(32000, 10 * warp), 0, true, true);
                    pe.getCurativeItems().clear();
                    try {
                        player.addPotionEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.15")), true);
                }
                else if (eff <= 24) {
                    PotionEffect pe = new PotionEffect(PotionUnnaturalHunger.instance, 5000, Math.min(3, warp / 15), true, true);
                    pe.getCurativeItems().clear();
                    pe.addCurativeItem(new ItemStack(Items.ROTTEN_FLESH));
                    pe.addCurativeItem(new ItemStack(ItemsTC.brain));
                    try {
                        player.addPotionEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.2")), true);
                }
                else if (eff <= 28) {
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.12")), true);
                }
                else if (eff <= 32) {
                    spawnMist(player, warp, 1);
                }
                else if (eff <= 36) {
                    try {
                        player.addPotionEffect(new PotionEffect(PotionBlurredVision.instance, Math.min(32000, 10 * warp), 0, true, true));
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                else if (eff <= 40) {
                    PotionEffect pe = new PotionEffect(PotionSunScorned.instance, 5000, Math.min(3, warp / 15), true, true);
                    pe.getCurativeItems().clear();
                    try {
                        player.addPotionEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.5")), true);
                }
                else if (eff <= 44) {
                    try {
                        player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 1200, Math.min(3, warp / 15), true, true));
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.9")), true);
                }
                else if (eff <= 48) {
                    PotionEffect pe = new PotionEffect(PotionInfectiousVisExhaust.instance, 6000, Math.min(3, warp / 15));
                    pe.getCurativeItems().clear();
                    try {
                        player.addPotionEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.1")), true);
                }
                else if (eff <= 52) {
                    player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, Math.min(40 * warp, 6000), 0, true, true));
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.10")), true);
                }
                else if (eff <= 56) {
                    PotionEffect pe = new PotionEffect(PotionDeathGaze.instance, 6000, Math.min(3, warp / 15), true, true);
                    pe.getCurativeItems().clear();
                    try {
                        player.addPotionEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.4")), true);
                }
                else if (eff <= 60) {
                    suddenlySpiders(player, warp, false);
                }
                else if (eff <= 64) {
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.13")), true);
                }
                else if (eff <= 68) {
                    spawnMist(player, warp, warp / 30);
                }
                else if (eff <= 72) {
                    try {
                        player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, Math.min(32000, 5 * warp), 0, true, true));
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                else if (eff == 76) {
                    if (nw > 0) {
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, -1, IPlayerWarp.EnumWarpType.NORMAL);
                    }
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.14")), true);
                }
                else if (eff <= 80) {
                    PotionEffect pe = new PotionEffect(PotionUnnaturalHunger.instance, 6000, Math.min(3, warp / 15), true, true);
                    pe.getCurativeItems().clear();
                    pe.addCurativeItem(new ItemStack(Items.ROTTEN_FLESH));
                    pe.addCurativeItem(new ItemStack(ItemsTC.brain));
                    try {
                        player.addPotionEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.2")), true);
                }
                else if (eff <= 88) {
                    spawnPortal(player);
                }
                else if (eff <= 92) {
                    suddenlySpiders(player, warp, true);
                }
                else {
                    spawnMist(player, warp, warp / 15);
                }
            }
            if (actualwarp > 10 && !ThaumcraftCapabilities.knowsResearch(player, "BATHSALTS") && !ThaumcraftCapabilities.knowsResearch(player, "!BATHSALTS")) {
                player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.8")), true);
                ThaumcraftApi.internalMethods.completeResearch(player, "!BATHSALTS");
            }
            if (actualwarp > 25 && !ThaumcraftCapabilities.knowsResearch(player, "ELDRITCHMINOR")) {
                ThaumcraftApi.internalMethods.completeResearch(player, "ELDRITCHMINOR");
            }
            if (actualwarp > 50 && !ThaumcraftCapabilities.knowsResearch(player, "ELDRITCHMAJOR")) {
                ThaumcraftApi.internalMethods.completeResearch(player, "ELDRITCHMAJOR");
            }
        }
    }
    
    private static void spawnMist(EntityPlayer player, int warp, int guardian) {
        PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((byte)1), (EntityPlayerMP)player);
        if (guardian > 0) {
            guardian = Math.min(8, guardian);
            for (int a = 0; a < guardian; ++a) {
                spawnGuardian(player);
            }
        }
        player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.6")), true);
    }
    
    private static void spawnPortal(EntityPlayer player) {
        EntityCultistPortalLesser eg = new EntityCultistPortalLesser(player.world);
        int i = MathHelper.floor(player.posX);
        int j = MathHelper.floor(player.posY);
        int k = MathHelper.floor(player.posZ);
        for (int l = 0; l < 50; ++l) {
            int i2 = i + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
            int j2 = j + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
            int k2 = k + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
            eg.setPosition(i2 + 0.5, j2 + 1.0, k2 + 0.5);
            if (player.world.getBlockState(new BlockPos(i2, j2 - 1, k2)).isOpaqueCube() && player.world.checkNoEntityCollision(eg.getEntityBoundingBox()) && player.world.getCollisionBoxes(eg, eg.getEntityBoundingBox()).isEmpty() && !player.world.containsAnyLiquid(eg.getEntityBoundingBox())) {
                eg.onInitialSpawn(player.world.getDifficultyForLocation(new BlockPos(eg)), null);
                player.world.spawnEntity(eg);
                player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.16")), true);
                break;
            }
        }
    }
    
    private static void spawnGuardian(EntityPlayer player) {
        EntityEldritchGuardian eg = new EntityEldritchGuardian(player.world);
        int i = MathHelper.floor(player.posX);
        int j = MathHelper.floor(player.posY);
        int k = MathHelper.floor(player.posZ);
        for (int l = 0; l < 50; ++l) {
            int i2 = i + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
            int j2 = j + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
            int k2 = k + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
            if (player.world.getBlockState(new BlockPos(i2, j2 - 1, k2)).isFullCube()) {
                eg.setPosition(i2, j2, k2);
                if (player.world.checkNoEntityCollision(eg.getEntityBoundingBox()) && player.world.getCollisionBoxes(eg, eg.getEntityBoundingBox()).isEmpty() && !player.world.containsAnyLiquid(eg.getEntityBoundingBox())) {
                    eg.setAttackTarget(player);
                    player.world.spawnEntity(eg);
                    break;
                }
            }
        }
    }
    
    private static void suddenlySpiders(EntityPlayer player, int warp, boolean real) {
        for (int spawns = Math.min(50, warp), a = 0; a < spawns; ++a) {
            EntityMindSpider spider = new EntityMindSpider(player.world);
            int i = MathHelper.floor(player.posX);
            int j = MathHelper.floor(player.posY);
            int k = MathHelper.floor(player.posZ);
            boolean success = false;
            for (int l = 0; l < 50; ++l) {
                int i2 = i + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
                int j2 = j + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
                int k2 = k + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
                if (player.world.getBlockState(new BlockPos(i2, j2 - 1, k2)).isFullCube()) {
                    spider.setPosition(i2, j2, k2);
                    if (player.world.checkNoEntityCollision(spider.getEntityBoundingBox()) && player.world.getCollisionBoxes(spider, spider.getEntityBoundingBox()).isEmpty() && !player.world.containsAnyLiquid(spider.getEntityBoundingBox())) {
                        success = true;
                        break;
                    }
                }
            }
            if (success) {
                spider.setAttackTarget(player);
                if (!real) {
                    spider.setViewer(player.getName());
                    spider.setHarmless(true);
                }
                player.world.spawnEntity(spider);
            }
        }
        player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.7")), true);
    }
    
    public static void checkDeathGaze(EntityPlayer player) {
        PotionEffect pe = player.getActivePotionEffect(PotionDeathGaze.instance);
        if (pe == null) {
            return;
        }
        int level = pe.getAmplifier();
        int range = Math.min(8 + level * 3, 24);
        List<Entity> list = player.world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(range, range, range));
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (entity.canBeCollidedWith() && entity instanceof EntityLivingBase) {
                if (entity.isEntityAlive()) {
                    if (EntityUtils.isVisibleTo(0.75f, player, entity, (float)range)) {
                        if (entity != null && player.canEntityBeSeen(entity) && (!(entity instanceof EntityPlayer) || FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()) && !((EntityLivingBase)entity).isPotionActive(MobEffects.WITHER)) {
                            ((EntityLivingBase)entity).setRevengeTarget(player);
                            ((EntityLivingBase)entity).setLastAttackedEntity(player);
                            if (entity instanceof EntityCreature) {
                                ((EntityCreature)entity).setAttackTarget(player);
                            }
                            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.WITHER, 80));
                        }
                    }
                }
            }
        }
    }
    
    private static int getWarpFromGear(EntityPlayer player) {
        int w = PlayerEvents.getFinalWarp(player.getHeldItemMainhand(), player);
        for (int a = 0; a < 4; ++a) {
            w += PlayerEvents.getFinalWarp(player.inventory.armorInventory.get(a), player);
        }
        IInventory baubles = BaublesApi.getBaubles(player);
        for (int a2 = 0; a2 < baubles.getSizeInventory(); ++a2) {
            w += PlayerEvents.getFinalWarp(baubles.getStackInSlot(a2), player);
        }
        return w;
    }
}
