package thaumcraft.common.lib.utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketBiomeChange;


public class Utils
{
    public static HashMap<List<Object>, ItemStack> specialMiningResult;
    public static HashMap<List<Object>, Float> specialMiningChance;
    public static String[] colorNames;
    public static int[] colors;
    public static ArrayList<List> oreDictLogs;
    
    public static boolean isChunkLoaded(World world, int x, int z) {
        Chunk chunk = world.getChunkProvider().getLoadedChunk(x >> 4, z >> 4);
        return chunk != null && !chunk.isEmpty();
    }
    
    public static boolean useBonemealAtLoc(World world, EntityPlayer player, BlockPos pos) {
        ItemStack is = new ItemStack(Items.DYE, 1, 15);
        ItemDye itemDye = (ItemDye)Items.DYE;
        return ItemDye.applyBonemeal(is, world, pos, player, EnumHand.MAIN_HAND);
    }
    
    public static boolean hasColor(byte[] colors) {
        for (byte col : colors) {
            if (col >= 0) {
                return true;
            }
        }
        return false;
    }
    
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0L, source.size());
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
    
    public static void addSpecialMiningResult(ItemStack in, ItemStack out, float chance) {
        Utils.specialMiningResult.put(Arrays.asList(in.getItem(), in.getItemDamage()), out);
        Utils.specialMiningChance.put(Arrays.asList(in.getItem(), in.getItemDamage()), chance);
    }
    
    public static ItemStack findSpecialMiningResult(ItemStack is, float chance, Random rand) {
        ItemStack dropped = is.copy();
        float r = rand.nextFloat();
        List ik = Arrays.asList(is.getItem(), is.getItemDamage());
        if (Utils.specialMiningResult.containsKey(ik) && r <= chance * Utils.specialMiningChance.get(ik)) {
            dropped = Utils.specialMiningResult.get(ik).copy();
            dropped.setCount(dropped.getCount() * is.getCount());
        }
        return dropped;
    }
    
    public static float clamp_float(float par0, float par1, float par2) {
        return (par0 < par1) ? par1 : ((par0 > par2) ? par2 : par0);
    }
    
    public static void setBiomeAt(World world, BlockPos pos, Biome biome) {
        setBiomeAt(world, pos, biome, true);
    }
    
    public static void setBiomeAt(World world, BlockPos pos, Biome biome, boolean sync) {
        if (biome == null) {
            return;
        }
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        byte[] array = chunk.getBiomeArray();
        array[(pos.getZ() & 0xF) << 4 | (pos.getX() & 0xF)] = (byte)(Biome.getIdForBiome(biome) & 0xFF);
        chunk.setBiomeArray(array);
        if (sync && !world.isRemote) {
            PacketHandler.INSTANCE.sendToAllAround(new PacketBiomeChange(pos.getX(), pos.getZ(), (short)Biome.getIdForBiome(biome)), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), world.getHeight(pos).getY(), pos.getZ(), 32.0));
        }
    }
    
    public static boolean resetBiomeAt(World world, BlockPos pos) {
        return resetBiomeAt(world, pos, true);
    }
    
    public static boolean resetBiomeAt(World world, BlockPos pos, boolean sync) {
        Biome[] biomesForGeneration = null;
        biomesForGeneration = world.getBiomeProvider().getBiomesForGeneration(biomesForGeneration, pos.getX(), pos.getZ(), 1, 1);
        if (biomesForGeneration != null && biomesForGeneration[0] != null) {
            Biome biome = biomesForGeneration[0];
            if (biome != world.getBiome(pos)) {
                setBiomeAt(world, pos, biome, sync);
                return true;
            }
        }
        return false;
    }
    
    public static boolean isWoodLog(IBlockAccess world, BlockPos pos) {
        IBlockState bs = world.getBlockState(pos);
        Block bi = bs.getBlock();
        return bi.isWood(world, pos) || bi.canSustainLeaves(bs, world, pos) || Utils.oreDictLogs.contains(Arrays.asList(bi, bi.getMetaFromState(bs)));
    }
    
    public static boolean isOreBlock(World world, BlockPos pos) {
        IBlockState bi = world.getBlockState(pos);
        if (bi.getBlock() != Blocks.AIR && bi.getBlock() != Blocks.BEDROCK) {
            ItemStack is = BlockUtils.getSilkTouchDrop(bi);
            if (is == null || is.isEmpty()) {
                int md = bi.getBlock().getMetaFromState(bi);
                is = new ItemStack(bi.getBlock(), 1, md);
            }
            if (is == null || is.isEmpty() || is.getItem() == null) {
                return false;
            }
            int[] od = OreDictionary.getOreIDs(is);
            if (od != null && od.length > 0) {
                for (int id : od) {
                    if (OreDictionary.getOreName(id) != null && OreDictionary.getOreName(id).toUpperCase().contains("ORE")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static int setNibble(int data, int nibble, int nibbleIndex) {
        int shift = nibbleIndex * 4;
        return (data & ~(15 << shift)) | nibble << shift;
    }
    
    public static int getNibble(int data, int nibbleIndex) {
        return data >> (nibbleIndex << 2) & 0xF;
    }
    
    public static boolean getBit(int value, int bit) {
        return (value & 1 << bit) != 0x0;
    }
    
    public static int setBit(int value, int bit) {
        return value | 1 << bit;
    }
    
    public static int clearBit(int value, int bit) {
        return value & ~(1 << bit);
    }
    
    public static int toggleBit(int value, int bit) {
        return value ^ 1 << bit;
    }
    
    public static byte pack(boolean... vals) {
        byte result = 0;
        for (boolean bit : vals) {
            result = (byte)(result << 1 | ((bit & true) ? 1 : 0));
        }
        return result;
    }
    
    public static boolean[] unpack(byte val) {
        boolean[] result = new boolean[8];
        for (int i = 0; i < 8; ++i) {
            result[i] = ((byte)(val >> 7 - i & 0x1) == 1);
        }
        return result;
    }
    
    public static byte[] intToByteArray(int value) {
        return new byte[] { (byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8), (byte)value };
    }
    
    public static int byteArraytoInt(byte[] bytes) {
        return bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3];
    }
    
    public static byte[] shortToByteArray(short value) {
        return new byte[] { (byte)(value >>> 8), (byte)value };
    }
    
    public static short byteArraytoShort(byte[] bytes) {
        return (short)(bytes[0] << 8 | bytes[1]);
    }
    
    public static boolean isLyingInCone(double[] x, double[] t, double[] b, float aperture) {
        double halfAperture = aperture / 2.0f;
        double[] apexToXVect = dif(t, x);
        double[] axisVect = dif(t, b);
        boolean isInInfiniteCone = dotProd(apexToXVect, axisVect) / magn(apexToXVect) / magn(axisVect) > Math.cos(halfAperture);
        if (!isInInfiniteCone) {
            return false;
        }
        boolean isUnderRoundCap = dotProd(apexToXVect, axisVect) / magn(axisVect) < magn(axisVect);
        return isUnderRoundCap;
    }
    
    public static double dotProd(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }
    
    public static double[] dif(double[] a, double[] b) {
        return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
    }
    
    public static double magn(double[] a) {
        return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    }
    
    public static Vec3d calculateVelocity(Vec3d from, Vec3d to, double heightGain, double gravity) {
        double endGain = to.y - from.y;
        double horizDist = Math.sqrt(distanceSquared2d(from, to));
        double gain = heightGain;
        double maxGain = (gain > endGain + gain) ? gain : (endGain + gain);
        double a = -horizDist * horizDist / (4.0 * maxGain);
        double b = horizDist;
        double c = -endGain;
        double slope = -b / (2.0 * a) - Math.sqrt(b * b - 4.0 * a * c) / (2.0 * a);
        double vy = Math.sqrt(maxGain * gravity);
        double vh = vy / slope;
        double dx = to.x - from.x;
        double dz = to.z - from.z;
        double mag = Math.sqrt(dx * dx + dz * dz);
        double dirx = dx / mag;
        double dirz = dz / mag;
        double vx = vh * dirx;
        double vz = vh * dirz;
        return new Vec3d(vx, vy, vz);
    }
    
    public static double distanceSquared2d(Vec3d from, Vec3d to) {
        double dx = to.x - from.x;
        double dz = to.z - from.z;
        return dx * dx + dz * dz;
    }
    
    public static double distanceSquared3d(Vec3d from, Vec3d to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        return dx * dx + dy * dy + dz * dz;
    }
    
    public static ItemStack generateLoot(int rarity, Random rand) {
        ItemStack is = ItemStack.EMPTY;
        if (rarity > 0 && rand.nextFloat() < 0.025f * rarity) {
            is = genGear(rarity, rand);
            if (is.isEmpty()) {
                is = generateLoot(rarity, rand);
            }
        }
        else {
            switch (rarity) {
                default: {
                    is = ((WeightedRandomLoot)WeightedRandom.getRandomItem(rand, (List)WeightedRandomLoot.lootBagCommon)).item;
                    break;
                }
                case 1: {
                    is = ((WeightedRandomLoot)WeightedRandom.getRandomItem(rand, (List)WeightedRandomLoot.lootBagUncommon)).item;
                    break;
                }
                case 2: {
                    is = ((WeightedRandomLoot)WeightedRandom.getRandomItem(rand, (List)WeightedRandomLoot.lootBagRare)).item;
                    break;
                }
            }
        }
        if (is.getItem() == Items.BOOK) {
            EnchantmentHelper.addRandomEnchantment(rand, is, (int)(5.0f + rarity * 0.75f * rand.nextInt(18)), false);
        }
        return is.copy();
    }
    
    private static ItemStack genGear(int rarity, Random rand) {
        ItemStack is = ItemStack.EMPTY;
        int quality = rand.nextInt(2);
        if (rand.nextFloat() < 0.2f) {
            ++quality;
        }
        if (rand.nextFloat() < 0.15f) {
            ++quality;
        }
        if (rand.nextFloat() < 0.1f) {
            ++quality;
        }
        if (rand.nextFloat() < 0.095f) {
            ++quality;
        }
        if (rand.nextFloat() < 0.095f) {
            ++quality;
        }
        Item item = getGearItemForSlot(rand.nextInt(5), quality);
        if (item != null) {
            is = new ItemStack(item, 1, rand.nextInt(1 + item.getMaxDamage() / 6));
            if (rand.nextInt(4) < rarity) {
                EnchantmentHelper.addRandomEnchantment(rand, is, (int)(5.0f + rarity * 0.75f * rand.nextInt(18)), false);
            }
            return is.copy();
        }
        return ItemStack.EMPTY;
    }
    
    private static Item getGearItemForSlot(int slot, int quality) {
        switch (slot) {
            case 4: {
                if (quality == 0) {
                    return Items.LEATHER_HELMET;
                }
                if (quality == 1) {
                    return Items.GOLDEN_HELMET;
                }
                if (quality == 2) {
                    return Items.CHAINMAIL_HELMET;
                }
                if (quality == 3) {
                    return Items.IRON_HELMET;
                }
                if (quality == 4) {
                    return ItemsTC.thaumiumHelm;
                }
                if (quality == 5) {
                    return Items.DIAMOND_HELMET;
                }
                if (quality == 6) {
                    return ItemsTC.voidHelm;
                }
            }
            case 3: {
                if (quality == 0) {
                    return Items.LEATHER_CHESTPLATE;
                }
                if (quality == 1) {
                    return Items.GOLDEN_CHESTPLATE;
                }
                if (quality == 2) {
                    return Items.CHAINMAIL_CHESTPLATE;
                }
                if (quality == 3) {
                    return Items.IRON_CHESTPLATE;
                }
                if (quality == 4) {
                    return ItemsTC.thaumiumChest;
                }
                if (quality == 5) {
                    return Items.DIAMOND_CHESTPLATE;
                }
                if (quality == 6) {
                    return ItemsTC.voidChest;
                }
            }
            case 2: {
                if (quality == 0) {
                    return Items.LEATHER_LEGGINGS;
                }
                if (quality == 1) {
                    return Items.GOLDEN_LEGGINGS;
                }
                if (quality == 2) {
                    return Items.CHAINMAIL_LEGGINGS;
                }
                if (quality == 3) {
                    return Items.IRON_LEGGINGS;
                }
                if (quality == 4) {
                    return ItemsTC.thaumiumLegs;
                }
                if (quality == 5) {
                    return Items.DIAMOND_LEGGINGS;
                }
                if (quality == 6) {
                    return ItemsTC.voidLegs;
                }
            }
            case 1: {
                if (quality == 0) {
                    return Items.LEATHER_BOOTS;
                }
                if (quality == 1) {
                    return Items.GOLDEN_BOOTS;
                }
                if (quality == 2) {
                    return Items.CHAINMAIL_BOOTS;
                }
                if (quality == 3) {
                    return Items.IRON_BOOTS;
                }
                if (quality == 4) {
                    return ItemsTC.thaumiumBoots;
                }
                if (quality == 5) {
                    return Items.DIAMOND_BOOTS;
                }
                if (quality == 6) {
                    return ItemsTC.voidBoots;
                }
            }
            case 0: {
                if (quality == 0) {
                    return Items.IRON_AXE;
                }
                if (quality == 1) {
                    return Items.IRON_SWORD;
                }
                if (quality == 2) {
                    return Items.GOLDEN_AXE;
                }
                if (quality == 3) {
                    return Items.GOLDEN_SWORD;
                }
                if (quality == 4) {
                    return ItemsTC.thaumiumSword;
                }
                if (quality == 5) {
                    return Items.DIAMOND_SWORD;
                }
                if (quality == 6) {
                    return ItemsTC.voidSword;
                }
                break;
            }
        }
        return null;
    }
    
    public static void writeItemStackToBuffer(ByteBuf bb, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            bb.writeShort(-1);
        }
        else {
            bb.writeShort(Item.getIdFromItem(stack.getItem()));
            bb.writeShort(stack.getCount());
            bb.writeShort(stack.getMetadata());
            NBTTagCompound nbttagcompound = null;
            if (stack.getItem().isDamageable() || stack.getItem().getShareTag()) {
                nbttagcompound = stack.getTagCompound();
            }
            writeNBTTagCompoundToBuffer(bb, nbttagcompound);
        }
    }
    
    public static ItemStack readItemStackFromBuffer(ByteBuf bb) {
        ItemStack itemstack = ItemStack.EMPTY;
        short short1 = bb.readShort();
        if (short1 >= 0) {
            short b0 = bb.readShort();
            short short2 = bb.readShort();
            itemstack = new ItemStack(Item.getItemById(short1), b0, short2);
            itemstack.setTagCompound(readNBTTagCompoundFromBuffer(bb));
        }
        return itemstack;
    }
    
    public static void writeNBTTagCompoundToBuffer(ByteBuf bb, NBTTagCompound nbt) {
        if (nbt == null) {
            bb.writeByte(0);
        }
        else {
            try {
                CompressedStreamTools.write(nbt, new ByteBufOutputStream(bb));
            }
            catch (IOException ioexception) {
                throw new EncoderException(ioexception);
            }
        }
    }
    
    public static NBTTagCompound readNBTTagCompoundFromBuffer(ByteBuf bb) {
        int i = bb.readerIndex();
        byte b0 = bb.readByte();
        if (b0 == 0) {
            return null;
        }
        bb.readerIndex(i);
        try {
            return CompressedStreamTools.read(new ByteBufInputStream(bb), new NBTSizeTracker(2097152L));
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    public static Vec3d rotateAsBlock(Vec3d vec, EnumFacing side) {
        return rotate(vec.subtract(0.5, 0.5, 0.5), side).addVector(0.5, 0.5, 0.5);
    }
    
    public static Vec3d rotateAsBlockRev(Vec3d vec, EnumFacing side) {
        return revRotate(vec.subtract(0.5, 0.5, 0.5), side).addVector(0.5, 0.5, 0.5);
    }
    
    public static Vec3d rotate(Vec3d vec, EnumFacing side) {
        switch (side) {
            case DOWN: {
                return new Vec3d(vec.x, -vec.y, -vec.z);
            }
            case UP: {
                return new Vec3d(vec.x, vec.y, vec.z);
            }
            case NORTH: {
                return new Vec3d(vec.x, vec.z, -vec.y);
            }
            case SOUTH: {
                return new Vec3d(vec.x, -vec.z, vec.y);
            }
            case WEST: {
                return new Vec3d(-vec.y, vec.x, vec.z);
            }
            case EAST: {
                return new Vec3d(vec.y, -vec.x, vec.z);
            }
            default: {
                return null;
            }
        }
    }
    
    public static Vec3d revRotate(Vec3d vec, EnumFacing side) {
        switch (side) {
            case DOWN: {
                return new Vec3d(vec.x, -vec.y, -vec.z);
            }
            case UP: {
                return new Vec3d(vec.x, vec.y, vec.z);
            }
            case NORTH: {
                return new Vec3d(vec.x, -vec.z, vec.y);
            }
            case SOUTH: {
                return new Vec3d(vec.x, vec.z, -vec.y);
            }
            case WEST: {
                return new Vec3d(vec.y, -vec.x, vec.z);
            }
            case EAST: {
                return new Vec3d(-vec.y, vec.x, vec.z);
            }
            default: {
                return null;
            }
        }
    }
    
    public static Vec3d rotateAroundX(Vec3d vec, float angle) {
        float var2 = MathHelper.cos(angle);
        float var3 = MathHelper.sin(angle);
        double var4 = vec.x;
        double var5 = vec.y * var2 + vec.z * var3;
        double var6 = vec.z * var2 - vec.y * var3;
        return new Vec3d(var4, var5, var6);
    }
    
    public static Vec3d rotateAroundY(Vec3d vec, float angle) {
        float var2 = MathHelper.cos(angle);
        float var3 = MathHelper.sin(angle);
        double var4 = vec.x * var2 + vec.z * var3;
        double var5 = vec.y;
        double var6 = vec.z * var2 - vec.x * var3;
        return new Vec3d(var4, var5, var6);
    }
    
    public static Vec3d rotateAroundZ(Vec3d vec, float angle) {
        float var2 = MathHelper.cos(angle);
        float var3 = MathHelper.sin(angle);
        double var4 = vec.x * var2 + vec.y * var3;
        double var5 = vec.y * var2 - vec.x * var3;
        double var6 = vec.z;
        return new Vec3d(var4, var5, var6);
    }
    
    public static RayTraceResult rayTrace(World worldIn, Entity entityIn, boolean useLiquids) {
        double d3 = 5.0;
        if (entityIn instanceof EntityPlayerMP) {
            d3 = ((EntityPlayerMP)entityIn).interactionManager.getBlockReachDistance();
        }
        return rayTrace(worldIn, entityIn, useLiquids, d3);
    }
    
    public static RayTraceResult rayTrace(World worldIn, Entity entityIn, boolean useLiquids, double range) {
        float f = entityIn.rotationPitch;
        float f2 = entityIn.rotationYaw;
        double d0 = entityIn.posX;
        double d2 = entityIn.posY + entityIn.getEyeHeight();
        double d3 = entityIn.posZ;
        Vec3d vec3d = new Vec3d(d0, d2, d3);
        float f3 = MathHelper.cos(-f2 * 0.017453292f - 3.1415927f);
        float f4 = MathHelper.sin(-f2 * 0.017453292f - 3.1415927f);
        float f5 = -MathHelper.cos(-f * 0.017453292f);
        float f6 = MathHelper.sin(-f * 0.017453292f);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        Vec3d vec3d2 = vec3d.addVector(f7 * range, f6 * range, f8 * range);
        return worldIn.rayTraceBlocks(vec3d, vec3d2, useLiquids, !useLiquids, false);
    }
    
    public static RayTraceResult rayTrace(World worldIn, Entity entityIn, Vec3d lookvec, boolean useLiquids, double range) {
        double d0 = entityIn.posX;
        double d2 = entityIn.posY + entityIn.getEyeHeight();
        double d3 = entityIn.posZ;
        Vec3d vec3d = new Vec3d(d0, d2, d3);
        Vec3d vec3d2 = vec3d.addVector(lookvec.x * range, lookvec.y * range, lookvec.z * range);
        return worldIn.rayTraceBlocks(vec3d, vec3d2, useLiquids, !useLiquids, false);
    }
    
    public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            }
            return getField(superClass, fieldName);
        }
    }
    
    public static AxisAlignedBB rotateBlockAABB(AxisAlignedBB aabb, EnumFacing facing) {
        Cuboid6 c = new Cuboid6(aabb).add(new Vector3(-0.5, -0.5, -0.5)).apply(Rotation.sideRotations[facing.getIndex()]).add(new Vector3(0.5, 0.5, 0.5));
        return c.aabb();
    }
    
    static {
        Utils.specialMiningResult = new HashMap<List<Object>, ItemStack>();
        Utils.specialMiningChance = new HashMap<List<Object>, Float>();
        colorNames = new String[] { "White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };
        colors = new int[] { 15790320, 15435844, 12801229, 6719955, 14602026, 4312372, 14188952, 4408131, 10526880, 2651799, 8073150, 2437522, 5320730, 3887386, 11743532, 1973019 };
        Utils.oreDictLogs = new ArrayList<List>();
    }
}
