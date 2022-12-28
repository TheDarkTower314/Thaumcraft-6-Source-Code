package thaumcraft.common.lib.utils;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.entities.monster.mods.ChampionModifier;


public class EntityUtils
{
    public static AttributeModifier CHAMPION_HEALTH;
    public static AttributeModifier CHAMPION_DAMAGE;
    public static AttributeModifier BOLDBUFF;
    public static AttributeModifier MIGHTYBUFF;
    public static AttributeModifier[] HPBUFF;
    public static AttributeModifier[] DMGBUFF;
    
    public static boolean isFriendly(Entity source, Entity target) {
        if (source == null || target == null) {
            return false;
        }
        if (source.getEntityId() == target.getEntityId()) {
            return true;
        }
        if (source.isRidingOrBeingRiddenBy(target)) {
            return true;
        }
        if (source.isOnSameTeam(target)) {
            return true;
        }
        if (target instanceof IEntityOwnable && ((IEntityOwnable)target).getOwner() != null && ((IEntityOwnable)target).getOwner().equals(source)) {
            return true;
        }
        try {
            if (!target.getEntityWorld().isRemote && target instanceof EntityPlayer && !FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()) {
                return true;
            }
        }
        catch (Exception ex) {}
        return false;
    }
    
    public static Vec3d posToHand(Entity e, EnumHand hand) {
        double px = e.posX;
        double py = e.getEntityBoundingBox().minY + e.height / 2.0f + 0.25;
        double pz = e.posZ;
        float m = (hand == EnumHand.MAIN_HAND) ? 0.0f : 180.0f;
        px += -MathHelper.cos((e.rotationYaw + m) / 180.0f * 3.141593f) * 0.3f;
        pz += -MathHelper.sin((e.rotationYaw + m) / 180.0f * 3.141593f) * 0.3f;
        Vec3d vec3d = e.getLook(1.0f);
        px += vec3d.x * 0.3;
        py += vec3d.y * 0.3;
        pz += vec3d.z * 0.3;
        return new Vec3d(px, py, pz);
    }
    
    public static boolean hasGoggles(Entity e) {
        if (!(e instanceof EntityPlayer)) {
            return false;
        }
        EntityPlayer viewer = (EntityPlayer)e;
        if (viewer.getHeldItemMainhand().getItem() instanceof IGoggles && showPopups(viewer.getHeldItemMainhand(), viewer)) {
            return true;
        }
        for (int a = 0; a < 4; ++a) {
            if (viewer.inventory.armorInventory.get(a).getItem() instanceof IGoggles && showPopups(viewer.inventory.armorInventory.get(a), viewer)) {
                return true;
            }
        }
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(viewer);
        for (int a2 = 0; a2 < baubles.getSlots(); ++a2) {
            if (baubles.getStackInSlot(a2).getItem() instanceof IGoggles && showPopups(baubles.getStackInSlot(a2), viewer)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean showPopups(ItemStack stack, EntityPlayer player) {
        return ((IGoggles)stack.getItem()).showIngamePopups(stack, player);
    }
    
    public static boolean hasRevealer(Entity e) {
        if (!(e instanceof EntityPlayer)) {
            return false;
        }
        EntityPlayer viewer = (EntityPlayer)e;
        if (viewer.getHeldItemMainhand().getItem() instanceof IRevealer && reveals(viewer.getHeldItemMainhand(), viewer)) {
            return true;
        }
        if (viewer.getHeldItemOffhand().getItem() instanceof IRevealer && reveals(viewer.getHeldItemOffhand(), viewer)) {
            return true;
        }
        for (int a = 0; a < 4; ++a) {
            if (viewer.inventory.armorInventory.get(a).getItem() instanceof IRevealer && reveals(viewer.inventory.armorInventory.get(a), viewer)) {
                return true;
            }
        }
        IInventory baubles = BaublesApi.getBaubles(viewer);
        for (int a2 = 0; a2 < baubles.getSizeInventory(); ++a2) {
            if (baubles.getStackInSlot(a2).getItem() instanceof IRevealer && reveals(baubles.getStackInSlot(a2), viewer)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean reveals(ItemStack stack, EntityPlayer player) {
        return ((IRevealer)stack.getItem()).showNodes(stack, player);
    }
    
    public static Entity getPointedEntity(World world, Entity entity, double minrange, double range, float padding, boolean nonCollide) {
        return getPointedEntity(world, new RayTraceResult(entity, entity.getPositionVector().addVector(0.0, entity.getEyeHeight(), 0.0)), entity.getLookVec(), minrange, range, padding, nonCollide);
    }
    
    public static Entity getPointedEntity(World world, Entity entity, Vec3d lookVec, double minrange, double range, float padding) {
        return getPointedEntity(world, new RayTraceResult(entity, entity.getPositionVector().addVector(0.0, entity.getEyeHeight(), 0.0)), lookVec, minrange, range, padding, false);
    }
    
    public static Entity getPointedEntity(World world, RayTraceResult ray, Vec3d lookVec, double minrange, double range, float padding) {
        return getPointedEntity(world, ray, lookVec, minrange, range, padding, false);
    }
    
    public static Entity getPointedEntity(World world, RayTraceResult ray, Vec3d lookVec, double minrange, double range, float padding, boolean nonCollide) {
        Entity pointedEntity = null;
        double d = range;
        Vec3d entityVec = new Vec3d(ray.hitVec.x, ray.hitVec.y, ray.hitVec.z);
        Vec3d vec3d2 = entityVec.addVector(lookVec.x * d, lookVec.y * d, lookVec.z * d);
        float f1 = padding;
        AxisAlignedBB bb = (ray.entityHit != null) ? ray.entityHit.getEntityBoundingBox() : new AxisAlignedBB(ray.hitVec.x, ray.hitVec.y, ray.hitVec.z, ray.hitVec.x, ray.hitVec.y, ray.hitVec.z).grow(0.5);
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(ray.entityHit, bb.expand(lookVec.x * d, lookVec.y * d, lookVec.z * d).grow(f1, f1, f1));
        double d2 = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (ray.hitVec.distanceTo(entity.getPositionVector()) >= minrange) {
                if (entity.canBeCollidedWith() || nonCollide) {
                    if (world.rayTraceBlocks(ray.hitVec, new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), false, true, false) == null) {
                        float f2 = Math.max(0.8f, entity.getCollisionBorderSize());
                        AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(f2, f2, f2);
                        RayTraceResult RayTraceResult = axisalignedbb.calculateIntercept(entityVec, vec3d2);
                        if (axisalignedbb.contains(entityVec)) {
                            if (0.0 < d2 || d2 == 0.0) {
                                pointedEntity = entity;
                                d2 = 0.0;
                            }
                        }
                        else if (RayTraceResult != null) {
                            double d3 = entityVec.distanceTo(RayTraceResult.hitVec);
                            if (d3 < d2 || d2 == 0.0) {
                                pointedEntity = entity;
                                d2 = d3;
                            }
                        }
                    }
                }
            }
        }
        return pointedEntity;
    }
    
    public static RayTraceResult getPointedEntityRay(World world, Entity ignoreEntity, Vec3d startVec, Vec3d lookVec, double minrange, double range, float padding, boolean nonCollide) {
        RayTraceResult pointedEntityRay = null;
        double d = range;
        Vec3d vec3d2 = startVec.addVector(lookVec.x * d, lookVec.y * d, lookVec.z * d);
        float f1 = padding;
        AxisAlignedBB bb = (ignoreEntity != null) ? ignoreEntity.getEntityBoundingBox() : new AxisAlignedBB(startVec.x, startVec.y, startVec.z, startVec.x, startVec.y, startVec.z).grow(0.5);
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(ignoreEntity, bb.expand(lookVec.x * d, lookVec.y * d, lookVec.z * d).grow(f1, f1, f1));
        double d2 = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (startVec.distanceTo(entity.getPositionVector()) >= minrange) {
                if (entity.canBeCollidedWith() || nonCollide) {
                    if (world.rayTraceBlocks(startVec, new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), false, true, false) == null) {
                        float f2 = Math.max(0.8f, entity.getCollisionBorderSize());
                        AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(f2, f2, f2);
                        RayTraceResult rayTraceResult = axisalignedbb.calculateIntercept(startVec, vec3d2);
                        if (axisalignedbb.contains(startVec)) {
                            if (0.0 < d2 || d2 == 0.0) {
                                pointedEntityRay = new RayTraceResult(entity, rayTraceResult.hitVec);
                                d2 = 0.0;
                            }
                        }
                        else if (rayTraceResult != null) {
                            double d3 = startVec.distanceTo(rayTraceResult.hitVec);
                            if (d3 < d2 || d2 == 0.0) {
                                pointedEntityRay = new RayTraceResult(entity, rayTraceResult.hitVec);
                                d2 = d3;
                            }
                        }
                    }
                }
            }
        }
        return pointedEntityRay;
    }
    
    public static Entity getPointedEntity(World world, EntityLivingBase player, double range, Class<?> clazz) {
        Entity pointedEntity = null;
        double d = range;
        Vec3d vec3d = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d vec3d2 = player.getLookVec();
        Vec3d vec3d3 = vec3d.addVector(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float f1 = 1.1f;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().expand(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d).grow(f1, f1, f1));
        double d2 = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (entity.canBeCollidedWith() && world.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), false, true, false) == null) {
                if (!clazz.isInstance(entity)) {
                    float f2 = Math.max(0.8f, entity.getCollisionBorderSize());
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(f2, f2, f2);
                    RayTraceResult RayTraceResult = axisalignedbb.calculateIntercept(vec3d, vec3d3);
                    if (axisalignedbb.contains(vec3d)) {
                        if (0.0 < d2 || d2 == 0.0) {
                            pointedEntity = entity;
                            d2 = 0.0;
                        }
                    }
                    else if (RayTraceResult != null) {
                        double d3 = vec3d.distanceTo(RayTraceResult.hitVec);
                        if (d3 < d2 || d2 == 0.0) {
                            pointedEntity = entity;
                            d2 = d3;
                        }
                    }
                }
            }
        }
        return pointedEntity;
    }
    
    public static boolean canEntityBeSeen(Entity entity, TileEntity te) {
        return te.getWorld().rayTraceBlocks(new Vec3d(te.getPos().getX() + 0.5, te.getPos().getY() + 1.25, te.getPos().getZ() + 0.5), new Vec3d(entity.posX, entity.posY, entity.posZ), false, true, false) == null;
    }
    
    public static boolean canEntityBeSeen(Entity lookingEntity, double x, double y, double z) {
        return lookingEntity.world.rayTraceBlocks(new Vec3d(x, y, z), new Vec3d(lookingEntity.posX, lookingEntity.posY, lookingEntity.posZ), false, true, false) == null;
    }
    
    public static boolean canEntityBeSeen(Entity lookingEntity, Entity targetEntity) {
        return lookingEntity.world.rayTraceBlocks(new Vec3d(lookingEntity.posX, lookingEntity.posY + lookingEntity.height / 2.0f, lookingEntity.posZ), new Vec3d(targetEntity.posX, targetEntity.posY + targetEntity.height / 2.0f, targetEntity.posZ), false, true, false) == null;
    }
    
    public static void resetFloatCounter(EntityPlayerMP player) {
        player.connection.floatingTickCount = 0;
    }
    
    public static <T extends Entity> List<T> getEntitiesInRange(World world, BlockPos pos, Entity entity, Class<? extends T> classEntity, double range) {
        return getEntitiesInRange(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, entity, classEntity, range);
    }
    
    public static <T extends Entity> List<T> getEntitiesInRange(World world, double x, double y, double z, Entity entity, Class<? extends T> classEntity, double range) {
        ArrayList<T> out = new ArrayList<T>();
        List list = world.getEntitiesWithinAABB(classEntity, new AxisAlignedBB(x, y, z, x, y, z).grow(range, range, range));
        if (list.size() > 0) {
            for (Object e : list) {
                Entity ent = (Entity)e;
                if (entity != null && entity.getEntityId() == ent.getEntityId()) {
                    continue;
                }
                out.add((T)ent);
            }
        }
        return out;
    }
    
    public static <T extends Entity> List<T> getEntitiesInRangeSorted(World world, Entity entity, Class<? extends T> classEntity, double range) {
        List<T> list = getEntitiesInRange(world, entity.posX, entity.posY, entity.posZ, entity, classEntity, range);
        List<T> sl = list.stream().sorted(new EntityDistComparator(entity)).collect(Collectors.toList());
        return sl;
    }
    
    public static boolean isVisibleTo(float fov, Entity ent, Entity ent2, float range) {
        double[] x = { ent2.posX, ent2.getEntityBoundingBox().minY + ent2.height / 2.0f, ent2.posZ };
        double[] t = { ent.posX, ent.getEntityBoundingBox().minY + ent.getEyeHeight(), ent.posZ };
        Vec3d q = ent.getLookVec();
        q = new Vec3d(q.x * range, q.y * range, q.z * range);
        Vec3d l = q.addVector(ent.posX, ent.getEntityBoundingBox().minY + ent.getEyeHeight(), ent.posZ);
        double[] b = { l.x, l.y, l.z };
        return Utils.isLyingInCone(x, t, b, fov);
    }
    
    public static boolean isVisibleTo(float fov, Entity ent, double xx, double yy, double zz, float range) {
        double[] x = { xx, yy, zz };
        double[] t = { ent.posX, ent.getEntityBoundingBox().minY + ent.getEyeHeight(), ent.posZ };
        Vec3d q = ent.getLookVec();
        q = new Vec3d(q.x * range, q.y * range, q.z * range);
        Vec3d l = q.addVector(ent.posX, ent.getEntityBoundingBox().minY + ent.getEyeHeight(), ent.posZ);
        double[] b = { l.x, l.y, l.z };
        return Utils.isLyingInCone(x, t, b, fov);
    }
    
    public static EntityItem entityDropSpecialItem(Entity entity, ItemStack stack, float dropheight) {
        if (stack.getCount() != 0 && stack.getItem() != null) {
            EntitySpecialItem entityitem = new EntitySpecialItem(entity.world, entity.posX, entity.posY + dropheight, entity.posZ, stack);
            entityitem.setDefaultPickupDelay();
            entityitem.motionY = 0.10000000149011612;
            entityitem.motionX = 0.0;
            entityitem.motionZ = 0.0;
            if (entity.captureDrops) {
                entity.capturedDrops.add(entityitem);
            }
            else {
                entity.world.spawnEntity(entityitem);
            }
            return entityitem;
        }
        return null;
    }
    
    public static void makeChampion(EntityMob entity, boolean persist) {
        try {
            if (entity.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() > -2.0) {
                return;
            }
        }
        catch (Exception e) {
            return;
        }
        int type = entity.world.rand.nextInt(ChampionModifier.mods.length);
        if (entity instanceof EntityCreeper) {
            type = 0;
        }
        IAttributeInstance modai = entity.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD);
        modai.removeModifier(ChampionModifier.mods[type].attributeMod);
        modai.applyModifier(ChampionModifier.mods[type].attributeMod);
        if (!(entity instanceof EntityThaumcraftBoss)) {
            IAttributeInstance iattributeinstance = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            iattributeinstance.removeModifier(EntityUtils.CHAMPION_HEALTH);
            iattributeinstance.applyModifier(EntityUtils.CHAMPION_HEALTH);
            IAttributeInstance iattributeinstance2 = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            iattributeinstance2.removeModifier(EntityUtils.CHAMPION_DAMAGE);
            iattributeinstance2.applyModifier(EntityUtils.CHAMPION_DAMAGE);
            entity.heal(25.0f);
            entity.setCustomNameTag(ChampionModifier.mods[type].getModNameLocalized() + " " + entity.getName());
        }
        else {
            ((EntityThaumcraftBoss)entity).generateName();
        }
        if (persist) {
            entity.enablePersistence();
        }
        switch (type) {
            case 0: {
                IAttributeInstance sai = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                sai.removeModifier(EntityUtils.BOLDBUFF);
                sai.applyModifier(EntityUtils.BOLDBUFF);
                break;
            }
            case 3: {
                IAttributeInstance mai = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                mai.removeModifier(EntityUtils.MIGHTYBUFF);
                mai.applyModifier(EntityUtils.MIGHTYBUFF);
                break;
            }
            case 5: {
                int bh = (int)entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() / 2;
                entity.setAbsorptionAmount(entity.getAbsorptionAmount() + bh);
                break;
            }
        }
    }
    
    public static void makeTainted(EntityLivingBase target) {
        try {
            if (target.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD) != null && target.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() > -1.0) {
                return;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int type = 13;
        IAttributeInstance modai = target.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD);
        if (modai == null) {
            return;
        }
        if (target.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() == -1.0) {
            modai.applyModifier(ChampionModifier.ATTRIBUTE_MINUS_ONE);
        }
        modai.removeModifier(ChampionModifier.mods[type].attributeMod);
        modai.applyModifier(ChampionModifier.mods[type].attributeMod);
        if (!(target instanceof EntityThaumcraftBoss)) {
            IAttributeInstance iattributeinstance = target.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            iattributeinstance.removeModifier(EntityUtils.HPBUFF[5]);
            iattributeinstance.applyModifier(EntityUtils.HPBUFF[5]);
            IAttributeInstance iattributeinstance2 = target.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            if (iattributeinstance2 == null) {
                target.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                target.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Math.max(2.0f, (target.height + target.width) * 2.0f));
            }
            else {
                iattributeinstance2.removeModifier(EntityUtils.DMGBUFF[0]);
                iattributeinstance2.applyModifier(EntityUtils.DMGBUFF[0]);
            }
            target.heal(25.0f);
        }
        else {
            ((EntityThaumcraftBoss)target).generateName();
        }
    }
    
    static {
        CHAMPION_HEALTH = new AttributeModifier(UUID.fromString("a62bef38-48cc-42a6-ac5e-ef913841c4fd"), "Champion health buff", 100.0, 0);
        CHAMPION_DAMAGE = new AttributeModifier(UUID.fromString("a340d2db-d881-4c25-ac62-f0ad14cd63b0"), "Champion damage buff", 2.0, 2);
        BOLDBUFF = new AttributeModifier(UUID.fromString("4b1edd33-caa9-47ae-a702-d86c05701037"), "Bold speed boost", 0.3, 1);
        MIGHTYBUFF = new AttributeModifier(UUID.fromString("7163897f-07f5-49b3-9ce4-b74beb83d2d3"), "Mighty damage boost", 2.0, 2);
        HPBUFF = new AttributeModifier[] { new AttributeModifier(UUID.fromString("54d621c1-dd4d-4b43-8bd2-5531c8875797"), "HEALTH BUFF 1", 50.0, 0), new AttributeModifier(UUID.fromString("f51257dc-b7fa-4f7a-92d7-75d68e8592c4"), "HEALTH BUFF 2", 50.0, 0), new AttributeModifier(UUID.fromString("3d6b2e42-4141-4364-b76d-0e8664bbd0bb"), "HEALTH BUFF 3", 50.0, 0), new AttributeModifier(UUID.fromString("02c97a08-801c-4131-afa2-1427a6151934"), "HEALTH BUFF 4", 50.0, 0), new AttributeModifier(UUID.fromString("0f354f6a-33c5-40be-93be-81b1338567f1"), "HEALTH BUFF 5", 50.0, 0), new AttributeModifier(UUID.fromString("0f354f6a-33c5-40be-93be-81b1338567f1"), "HEALTH BUFF 6", 25.0, 0) };
        DMGBUFF = new AttributeModifier[] { new AttributeModifier(UUID.fromString("534f8c57-929a-48cf-bbd6-0fd851030748"), "DAMAGE BUFF 1", 0.5, 0), new AttributeModifier(UUID.fromString("d317a76e-0e7c-4c61-acfd-9fa286053b32"), "DAMAGE BUFF 2", 0.5, 0), new AttributeModifier(UUID.fromString("ff462d63-26a2-4363-830e-143ed97e2a4f"), "DAMAGE BUFF 3", 0.5, 0), new AttributeModifier(UUID.fromString("cf1eb39e-0c67-495f-887c-0d3080828d2f"), "DAMAGE BUFF 4", 0.5, 0), new AttributeModifier(UUID.fromString("3cfab9da-2701-43d8-ac07-885f16fa4117"), "DAMAGE BUFF 5", 0.5, 0) };
    }
    
    public static class EntityDistComparator implements Comparator<Entity>
    {
        private Entity source;
        
        public EntityDistComparator(Entity source) {
            this.source = source;
        }
        
        @Override
        public int compare(Entity a, Entity b) {
            if (a.equals(b)) {
                return 0;
            }
            double da = source.getPositionVector().squareDistanceTo(a.getPositionVector());
            double db = source.getPositionVector().squareDistanceTo(b.getPositionVector());
            return (da < db) ? -1 : ((da > db) ? 1 : 0);
        }
    }
}
