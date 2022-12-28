package thaumcraft.common.lib.utils;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.items.ItemsTC;



public class BlockUtils
{
    static BlockPos lastPos;
    static int lasty;
    static int lastz;
    static double lastdistance;
    public static ArrayList<String> portableHoleBlackList;
    
    private static boolean removeBlock(EntityPlayer player, BlockPos pos) {
        return removeBlock(player, pos, false);
    }
    
    private static boolean removeBlock(EntityPlayer player, BlockPos pos, boolean canHarvest) {
        IBlockState iblockstate = player.world.getBlockState(pos);
        boolean flag = iblockstate.getBlock().removedByPlayer(iblockstate, player.world, pos, player, canHarvest);
        if (flag) {
            try {
                iblockstate.getBlock().onBlockDestroyedByPlayer(player.world, pos, iblockstate);
            }
            catch (Exception ex) {}
        }
        return flag;
    }
    
    public static boolean harvestBlockSkipCheck(World world, EntityPlayer player, BlockPos pos) {
        return harvestBlock(world, player, pos, false, false, 0, true);
    }
    
    public static boolean harvestBlock(World world, EntityPlayer player, BlockPos pos) {
        return harvestBlock(world, player, pos, false, false, 0, false);
    }
    
    public static boolean harvestBlock(World world, EntityPlayer p, BlockPos pos, boolean alwaysDrop, boolean silkOverride, int fortuneOverride, boolean skipEvent) {
        if (world.isRemote || !(p instanceof EntityPlayerMP)) {
            return false;
        }
        EntityPlayerMP player = (EntityPlayerMP)p;
        int exp = skipEvent ? 0 : ForgeHooks.onBlockBreakEvent(world, player.interactionManager.getGameType(), player, pos);
        if (exp == -1) {
            return false;
        }
        IBlockState iblockstate = world.getBlockState(pos);
        TileEntity tileentity = world.getTileEntity(pos);
        Block block = iblockstate.getBlock();
        if ((block instanceof BlockCommandBlock || block instanceof BlockStructure) && !player.canUseCommandBlock()) {
            world.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
            return false;
        }
        world.playEvent(null, 2001, pos, Block.getStateId(iblockstate));
        boolean flag1 = false;
        if (player.interactionManager.isCreative()) {
            flag1 = removeBlock(player, pos);
            player.interactionManager.player.connection.sendPacket(new SPacketBlockChange(world, pos));
        }
        else {
            ItemStack itemstack1 = player.getHeldItemMainhand();
            boolean flag2 = alwaysDrop || iblockstate.getBlock().canHarvestBlock(world, pos, player);
            flag1 = removeBlock(player, pos, flag2);
            if (flag1 && flag2) {
                ItemStack fakeStack = itemstack1.copy();
                if (silkOverride || fortuneOverride > EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FORTUNE, player)) {
                    if (alwaysDrop || fakeStack.isEmpty()) {
                        fakeStack = new ItemStack(ItemsTC.enchantedPlaceholder);
                    }
                    Map<Enchantment, Integer> enchMap = EnchantmentHelper.getEnchantments(itemstack1);
                    if (silkOverride) {
                        enchMap.put(Enchantments.SILK_TOUCH, 1);
                    }
                    int fort = Math.max(fortuneOverride, (enchMap.get(Enchantments.FORTUNE) != null) ? enchMap.get(Enchantments.FORTUNE) : 0);
                    if (fort > 0) {
                        enchMap.put(Enchantments.FORTUNE, fort);
                    }
                    EnchantmentHelper.setEnchantments(enchMap, fakeStack);
                }
                iblockstate.getBlock().harvestBlock(world, player, pos, iblockstate, tileentity, fakeStack);
            }
        }
        if (!player.interactionManager.isCreative() && flag1 && exp > 0) {
            iblockstate.getBlock().dropXpOnBlockBreak(world, pos, exp);
        }
        return flag1;
    }
    
    public static void destroyBlockPartially(World world, int par1, BlockPos pos, int par5) {
        for (EntityPlayer player : world.playerEntities) {
            if (player != null && player.world == FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld() && player.getEntityId() != par1) {
                double d0 = pos.getX() - player.posX;
                double d2 = pos.getY() - player.posY;
                double d3 = pos.getZ() - player.posZ;
                if (d0 * d0 + d2 * d2 + d3 * d3 >= 1024.0) {
                    continue;
                }
                ((EntityPlayerMP)player).connection.sendPacket(new SPacketBlockBreakAnim(par1, pos, par5));
            }
        }
    }
    
    public static void findBlocks(World world, BlockPos pos, IBlockState block, int reach) {
        for (int xx = -reach; xx <= reach; ++xx) {
            for (int yy = reach; yy >= -reach; --yy) {
                for (int zz = -reach; zz <= reach; ++zz) {
                    if (Math.abs(BlockUtils.lastPos.getX() + xx - pos.getX()) > 24) {
                        return;
                    }
                    if (Math.abs(BlockUtils.lastPos.getY() + yy - pos.getY()) > 48) {
                        return;
                    }
                    if (Math.abs(BlockUtils.lastPos.getZ() + zz - pos.getZ()) > 24) {
                        return;
                    }
                    IBlockState bs = world.getBlockState(BlockUtils.lastPos.add(xx, yy, zz));
                    boolean same = bs.getBlock() == block.getBlock() && bs.getBlock().damageDropped(bs) == block.getBlock().damageDropped(block);
                    if (same && bs.getBlock().getBlockHardness(bs, world, BlockUtils.lastPos.add(xx, yy, zz)) >= 0.0f) {
                        double xd = BlockUtils.lastPos.getX() + xx - pos.getX();
                        double yd = BlockUtils.lastPos.getY() + yy - pos.getY();
                        double zd = BlockUtils.lastPos.getZ() + zz - pos.getZ();
                        double d = xd * xd + yd * yd + zd * zd;
                        if (d > BlockUtils.lastdistance) {
                            BlockUtils.lastdistance = d;
                            BlockUtils.lastPos = BlockUtils.lastPos.add(xx, yy, zz);
                            findBlocks(world, pos, block, reach);
                            return;
                        }
                    }
                }
            }
        }
    }
    
    public static boolean breakFurthestBlock(World world, BlockPos pos, IBlockState block, EntityPlayer player) {
        BlockUtils.lastPos = new BlockPos(pos);
        BlockUtils.lastdistance = 0.0;
        int reach = Utils.isWoodLog(world, pos) ? 2 : 1;
        findBlocks(world, pos, block, reach);
        boolean worked = harvestBlockSkipCheck(world, player, BlockUtils.lastPos);
        world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), block, block, 3);
        if (worked && Utils.isWoodLog(world, pos)) {
            world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), block, block, 3);
            for (int xx = -3; xx <= 3; ++xx) {
                for (int yy = -3; yy <= 3; ++yy) {
                    for (int zz = -3; zz <= 3; ++zz) {
                        world.scheduleUpdate(BlockUtils.lastPos.add(xx, yy, zz), world.getBlockState(BlockUtils.lastPos.add(xx, yy, zz)).getBlock(), 50 + world.rand.nextInt(75));
                    }
                }
            }
        }
        return worked;
    }
    
    public static RayTraceResult getTargetBlock(World world, Entity entity, boolean par3) {
        return getTargetBlock(world, entity, par3, par3, 10.0);
    }
    
    public static RayTraceResult getTargetBlock(World world, Entity entity, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, double range) {
        float var4 = 1.0f;
        float var5 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * var4;
        float var6 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * var4;
        double var7 = entity.prevPosX + (entity.posX - entity.prevPosX) * var4;
        double var8 = entity.prevPosY + (entity.posY - entity.prevPosY) * var4 + entity.getEyeHeight();
        double var9 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * var4;
        Vec3d var10 = new Vec3d(var7, var8, var9);
        float var11 = MathHelper.cos(-var6 * 0.017453292f - 3.1415927f);
        float var12 = MathHelper.sin(-var6 * 0.017453292f - 3.1415927f);
        float var13 = -MathHelper.cos(-var5 * 0.017453292f);
        float var14 = MathHelper.sin(-var5 * 0.017453292f);
        float var15 = var12 * var13;
        float var16 = var11 * var13;
        Vec3d var17 = var10.addVector(var15 * range, var14 * range, var16 * range);
        return world.rayTraceBlocks(var10, var17, stopOnLiquid, !ignoreBlockWithoutBoundingBox, false);
    }
    
    public static int countExposedSides(World world, BlockPos pos) {
        int count = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            if (world.isAirBlock(pos.offset(dir))) {
                ++count;
            }
        }
        return count;
    }
    
    public static boolean isBlockExposed(World world, BlockPos pos) {
        for (EnumFacing face : EnumFacing.values()) {
            if (!world.getBlockState(pos.offset(face)).isOpaqueCube()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAdjacentToSolidBlock(World world, BlockPos pos) {
        for (EnumFacing face : EnumFacing.values()) {
            if (world.isSideSolid(pos.offset(face), face.getOpposite())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isBlockTouching(IBlockAccess world, BlockPos pos, IBlockState bs) {
        for (EnumFacing face : EnumFacing.values()) {
            if (world.getBlockState(pos.offset(face)) == bs) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isBlockTouching(IBlockAccess world, BlockPos pos, Block bs) {
        for (EnumFacing face : EnumFacing.values()) {
            if (world.getBlockState(pos.offset(face)).getBlock() == bs) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isBlockTouching(IBlockAccess world, BlockPos pos, Material mat, boolean solid) {
        for (EnumFacing face : EnumFacing.values()) {
            if (world.getBlockState(pos.offset(face)).getMaterial() == mat && (!solid || world.getBlockState(pos.offset(face)).isSideSolid(world, pos.offset(face), face.getOpposite()))) {
                return true;
            }
        }
        return false;
    }
    
    public static EnumFacing getFaceBlockTouching(IBlockAccess world, BlockPos pos, Block bs) {
        for (EnumFacing face : EnumFacing.values()) {
            if (world.getBlockState(pos.offset(face)).getBlock() == bs) {
                return face;
            }
        }
        return null;
    }
    
    public static boolean isPortableHoleBlackListed(IBlockState blockstate) {
        return isBlockListed(blockstate, BlockUtils.portableHoleBlackList);
    }
    
    public static boolean isBlockListed(IBlockState blockstate, List<String> list) {
        String stateString = blockstate.toString();
        for (String key : list) {
            String[] splitString = key.split(";");
            if (splitString[0].contains(":")) {
                if (!Block.REGISTRY.getNameForObject(blockstate.getBlock()).toString().equals(splitString[0])) {
                    continue;
                }
                if (splitString.length <= 1) {
                    return true;
                }
                int matches = 0;
                for (int a = 1; a < splitString.length; ++a) {
                    if (stateString.contains(splitString[a])) {
                        ++matches;
                    }
                }
                if (matches == splitString.length - 1) {
                    return true;
                }
                continue;
            }
            else {
                ItemStack bs = new ItemStack(Item.getItemFromBlock(blockstate.getBlock()), 1, blockstate.getBlock().getMetaFromState(blockstate));
                for (ItemStack stack : OreDictionary.getOres(splitString[0], false)) {
                    if (OreDictionary.itemMatches(stack, bs, false)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static double distance(BlockPos b1, BlockPos b2) {
        double d3 = b1.getX() - b2.getX();
        double d4 = b1.getY() - b2.getY();
        double d5 = b1.getZ() - b2.getZ();
        return d3 * d3 + d4 * d4 + d5 * d5;
    }
    
    public static EnumFacing.Axis getBlockAxis(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        EnumFacing.Axis ax = EnumFacing.Axis.Y;
        for (IProperty prop : state.getProperties().keySet()) {
            if (prop.getName().equals("axis")) {
                if (state.getValue(prop) instanceof BlockLog.EnumAxis) {
                    ax = ((state.getValue(prop) == BlockLog.EnumAxis.X) ? EnumFacing.Axis.X : ((state.getValue(prop) == BlockLog.EnumAxis.Y) ? EnumFacing.Axis.Y : ((state.getValue(prop) == BlockLog.EnumAxis.Z) ? EnumFacing.Axis.Z : EnumFacing.Axis.Y)));
                    break;
                }
                if (state.getValue(prop) instanceof EnumFacing.Axis) {
                    ax = (EnumFacing.Axis)state.getValue(prop);
                    break;
                }
                continue;
            }
        }
        if (ax == null) {
            ax = EnumFacing.Axis.Y;
        }
        return ax;
    }
    
    public static boolean hasLOS(World world, BlockPos source, BlockPos target) {
        RayTraceResult mop = ThaumcraftApiHelper.rayTraceIgnoringSource(world, new Vec3d(source.getX() + 0.5, source.getY() + 0.5, source.getZ() + 0.5), new Vec3d(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5), false, true, false);
        return mop == null || (mop.typeOfHit == RayTraceResult.Type.BLOCK && mop.getBlockPos().getX() == target.getX() && mop.getBlockPos().getY() == target.getY() && mop.getBlockPos().getZ() == target.getZ());
    }
    
    public static ItemStack getSilkTouchDrop(IBlockState bs) {
        ItemStack dropped = ItemStack.EMPTY;
        try {
            Method m = ReflectionHelper.findMethod(Block.class, "getSilkTouchDrop", "func_180643_i", IBlockState.class);
            dropped = (ItemStack)m.invoke(bs.getBlock(), bs);
        }
        catch (Exception e) {
            Thaumcraft.log.warn("Could not invoke net.minecraft.block.Block method getSilkTouchDrop");
        }
        return dropped;
    }
    
    static {
        BlockUtils.lastPos = BlockPos.ORIGIN;
        BlockUtils.lasty = 0;
        BlockUtils.lastz = 0;
        BlockUtils.lastdistance = 0.0;
        BlockUtils.portableHoleBlackList = new ArrayList<String>();
    }
    
    public static class BlockPosComparator implements Comparator<BlockPos>
    {
        private BlockPos source;
        
        public BlockPosComparator(BlockPos source) {
            this.source = source;
        }
        
        @Override
        public int compare(BlockPos a, BlockPos b) {
            if (a.equals(b)) {
                return 0;
            }
            double da = source.distanceSq(a);
            double db = source.distanceSq(b);
            return (da < db) ? -1 : ((da > db) ? 1 : 0);
        }
    }
}
