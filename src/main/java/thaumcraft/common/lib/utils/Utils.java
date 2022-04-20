// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.utils;

import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import net.minecraft.util.math.AxisAlignedBB;
import java.lang.reflect.Field;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumFacing;
import java.io.DataInput;
import net.minecraft.nbt.NBTSizeTracker;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import java.io.DataOutput;
import net.minecraft.nbt.CompressedStreamTools;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.item.Item;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.WeightedRandom;
import thaumcraft.api.internal.WeightedRandomLoot;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.misc.PacketBiomeChange;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.world.biome.Biome;
import java.util.Random;
import java.util.Arrays;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemDye;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.World;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import java.util.List;
import java.util.HashMap;

public class Utils
{
    public static HashMap<List<Object>, ItemStack> specialMiningResult;
    public static HashMap<List<Object>, Float> specialMiningChance;
    public static final String[] colorNames;
    public static final int[] colors;
    public static ArrayList<List> oreDictLogs;
    
    public static boolean isChunkLoaded(final World world, final int x, final int z) {
        final Chunk chunk = world.getChunkProvider().getLoadedChunk(x >> 4, z >> 4);
        return chunk != null && !chunk.isEmpty();
    }
    
    public static boolean useBonemealAtLoc(final World world, final EntityPlayer player, final BlockPos pos) {
        final ItemStack is = new ItemStack(Items.DYE, 1, 15);
        final ItemDye itemDye = (ItemDye)Items.DYE;
        return ItemDye.applyBonemeal(is, world, pos, player, EnumHand.MAIN_HAND);
    }
    
    public static boolean hasColor(final byte[] colors) {
        for (final byte col : colors) {
            if (col >= 0) {
                return true;
            }
        }
        return false;
    }
    
    public static void copyFile(final File sourceFile, final File destFile) throws IOException {
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
    
    public static void addSpecialMiningResult(final ItemStack in, final ItemStack out, final float chance) {
        Utils.specialMiningResult.put(Arrays.asList(in.getItem(), in.getItemDamage()), out);
        Utils.specialMiningChance.put(Arrays.asList(in.getItem(), in.getItemDamage()), chance);
    }
    
    public static ItemStack findSpecialMiningResult(final ItemStack is, final float chance, final Random rand) {
        ItemStack dropped = is.copy();
        final float r = rand.nextFloat();
        final List ik = Arrays.asList(is.getItem(), is.getItemDamage());
        if (Utils.specialMiningResult.containsKey(ik) && r <= chance * Utils.specialMiningChance.get(ik)) {
            dropped = Utils.specialMiningResult.get(ik).copy();
            dropped.setCount(dropped.getCount() * is.getCount());
        }
        return dropped;
    }
    
    public static float clamp_float(final float par0, final float par1, final float par2) {
        return (par0 < par1) ? par1 : ((par0 > par2) ? par2 : par0);
    }
    
    public static void setBiomeAt(final World world, final BlockPos pos, final Biome biome) {
        setBiomeAt(world, pos, biome, true);
    }
    
    public static void setBiomeAt(final World world, final BlockPos pos, final Biome biome, final boolean sync) {
        if (biome == null) {
            return;
        }
        final Chunk chunk = world.getChunkFromBlockCoords(pos);
        final byte[] array = chunk.getBiomeArray();
        array[(pos.getZ() & 0xF) << 4 | (pos.getX() & 0xF)] = (byte)(Biome.getIdForBiome(biome) & 0xFF);
        chunk.setBiomeArray(array);
        if (sync && !world.isRemote) {
            PacketHandler.INSTANCE.sendToAllAround(new PacketBiomeChange(pos.getX(), pos.getZ(), (short)Biome.getIdForBiome(biome)), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), world.getHeight(pos).getY(), pos.getZ(), 32.0));
        }
    }
    
    public static boolean resetBiomeAt(final World world, final BlockPos pos) {
        return resetBiomeAt(world, pos, true);
    }
    
    public static boolean resetBiomeAt(final World world, final BlockPos pos, final boolean sync) {
        Biome[] biomesForGeneration = null;
        biomesForGeneration = world.getBiomeProvider().getBiomesForGeneration(biomesForGeneration, pos.getX(), pos.getZ(), 1, 1);
        if (biomesForGeneration != null && biomesForGeneration[0] != null) {
            final Biome biome = biomesForGeneration[0];
            if (biome != world.getBiome(pos)) {
                setBiomeAt(world, pos, biome, sync);
                return true;
            }
        }
        return false;
    }
    
    public static boolean isWoodLog(final IBlockAccess world, final BlockPos pos) {
        final IBlockState bs = world.getBlockState(pos);
        final Block bi = bs.getBlock();
        return bi.isWood(world, pos) || bi.canSustainLeaves(bs, world, pos) || Utils.oreDictLogs.contains(Arrays.asList(bi, bi.getMetaFromState(bs)));
    }
    
    public static boolean isOreBlock(final World world, final BlockPos pos) {
        final IBlockState bi = world.getBlockState(pos);
        if (bi.getBlock() != Blocks.AIR && bi.getBlock() != Blocks.BEDROCK) {
            ItemStack is = BlockUtils.getSilkTouchDrop(bi);
            if (is == null || is.isEmpty()) {
                final int md = bi.getBlock().getMetaFromState(bi);
                is = new ItemStack(bi.getBlock(), 1, md);
            }
            if (is == null || is.isEmpty() || is.getItem() == null) {
                return false;
            }
            final int[] od = OreDictionary.getOreIDs(is);
            if (od != null && od.length > 0) {
                for (final int id : od) {
                    if (OreDictionary.getOreName(id) != null && OreDictionary.getOreName(id).toUpperCase().contains("ORE")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static int setNibble(final int data, final int nibble, final int nibbleIndex) {
        final int shift = nibbleIndex * 4;
        return (data & ~(15 << shift)) | nibble << shift;
    }
    
    public static int getNibble(final int data, final int nibbleIndex) {
        return data >> (nibbleIndex << 2) & 0xF;
    }
    
    public static boolean getBit(final int value, final int bit) {
        return (value & 1 << bit) != 0x0;
    }
    
    public static int setBit(final int value, final int bit) {
        return value | 1 << bit;
    }
    
    public static int clearBit(final int value, final int bit) {
        return value & ~(1 << bit);
    }
    
    public static int toggleBit(final int value, final int bit) {
        return value ^ 1 << bit;
    }
    
    public static byte pack(final boolean... vals) {
        byte result = 0;
        for (final boolean bit : vals) {
            result = (byte)(result << 1 | ((bit & true) ? 1 : 0));
        }
        return result;
    }
    
    public static boolean[] unpack(final byte val) {
        final boolean[] result = new boolean[8];
        for (int i = 0; i < 8; ++i) {
            result[i] = ((byte)(val >> 7 - i & 0x1) == 1);
        }
        return result;
    }
    
    public static final byte[] intToByteArray(final int value) {
        return new byte[] { (byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8), (byte)value };
    }
    
    public static int byteArraytoInt(final byte[] bytes) {
        return bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3];
    }
    
    public static final byte[] shortToByteArray(final short value) {
        return new byte[] { (byte)(value >>> 8), (byte)value };
    }
    
    public static short byteArraytoShort(final byte[] bytes) {
        return (short)(bytes[0] << 8 | bytes[1]);
    }
    
    public static boolean isLyingInCone(final double[] x, final double[] t, final double[] b, final float aperture) {
        final double halfAperture = aperture / 2.0f;
        final double[] apexToXVect = dif(t, x);
        final double[] axisVect = dif(t, b);
        final boolean isInInfiniteCone = dotProd(apexToXVect, axisVect) / magn(apexToXVect) / magn(axisVect) > Math.cos(halfAperture);
        if (!isInInfiniteCone) {
            return false;
        }
        final boolean isUnderRoundCap = dotProd(apexToXVect, axisVect) / magn(axisVect) < magn(axisVect);
        return isUnderRoundCap;
    }
    
    public static double dotProd(final double[] a, final double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }
    
    public static double[] dif(final double[] a, final double[] b) {
        return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
    }
    
    public static double magn(final double[] a) {
        return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    }
    
    public static Vec3d calculateVelocity(final Vec3d from, final Vec3d to, final double heightGain, final double gravity) {
        final double endGain = to.y - from.y;
        final double horizDist = Math.sqrt(distanceSquared2d(from, to));
        final double gain = heightGain;
        final double maxGain = (gain > endGain + gain) ? gain : (endGain + gain);
        final double a = -horizDist * horizDist / (4.0 * maxGain);
        final double b = horizDist;
        final double c = -endGain;
        final double slope = -b / (2.0 * a) - Math.sqrt(b * b - 4.0 * a * c) / (2.0 * a);
        final double vy = Math.sqrt(maxGain * gravity);
        final double vh = vy / slope;
        final double dx = to.x - from.x;
        final double dz = to.z - from.z;
        final double mag = Math.sqrt(dx * dx + dz * dz);
        final double dirx = dx / mag;
        final double dirz = dz / mag;
        final double vx = vh * dirx;
        final double vz = vh * dirz;
        return new Vec3d(vx, vy, vz);
    }
    
    public static double distanceSquared2d(final Vec3d from, final Vec3d to) {
        final double dx = to.x - from.x;
        final double dz = to.z - from.z;
        return dx * dx + dz * dz;
    }
    
    public static double distanceSquared3d(final Vec3d from, final Vec3d to) {
        final double dx = to.x - from.x;
        final double dy = to.y - from.y;
        final double dz = to.z - from.z;
        return dx * dx + dy * dy + dz * dz;
    }
    
    public static ItemStack generateLoot(final int rarity, final Random rand) {
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
    
    private static ItemStack genGear(final int rarity, final Random rand) {
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
        final Item item = getGearItemForSlot(rand.nextInt(5), quality);
        if (item != null) {
            is = new ItemStack(item, 1, rand.nextInt(1 + item.getMaxDamage() / 6));
            if (rand.nextInt(4) < rarity) {
                EnchantmentHelper.addRandomEnchantment(rand, is, (int)(5.0f + rarity * 0.75f * rand.nextInt(18)), false);
            }
            return is.copy();
        }
        return ItemStack.EMPTY;
    }
    
    private static Item getGearItemForSlot(final int slot, final int quality) {
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
    
    public static void writeItemStackToBuffer(final ByteBuf bb, final ItemStack stack) {
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
    
    public static ItemStack readItemStackFromBuffer(final ByteBuf bb) {
        ItemStack itemstack = ItemStack.EMPTY;
        final short short1 = bb.readShort();
        if (short1 >= 0) {
            final short b0 = bb.readShort();
            final short short2 = bb.readShort();
            itemstack = new ItemStack(Item.getItemById(short1), b0, short2);
            itemstack.setTagCompound(readNBTTagCompoundFromBuffer(bb));
        }
        return itemstack;
    }
    
    public static void writeNBTTagCompoundToBuffer(final ByteBuf bb, final NBTTagCompound nbt) {
        if (nbt == null) {
            bb.writeByte(0);
        }
        else {
            try {
                CompressedStreamTools.write(nbt, new ByteBufOutputStream(bb));
            }
            catch (final IOException ioexception) {
                throw new EncoderException(ioexception);
            }
        }
    }
    
    public static NBTTagCompound readNBTTagCompoundFromBuffer(final ByteBuf bb) {
        final int i = bb.readerIndex();
        final byte b0 = bb.readByte();
        if (b0 == 0) {
            return null;
        }
        bb.readerIndex(i);
        try {
            return CompressedStreamTools.read(new ByteBufInputStream(bb), new NBTSizeTracker(2097152L));
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public static Vec3d rotateAsBlock(final Vec3d vec, final EnumFacing side) {
        return rotate(vec.subtract(0.5, 0.5, 0.5), side).addVector(0.5, 0.5, 0.5);
    }
    
    public static Vec3d rotateAsBlockRev(final Vec3d vec, final EnumFacing side) {
        return revRotate(vec.subtract(0.5, 0.5, 0.5), side).addVector(0.5, 0.5, 0.5);
    }
    
    public static Vec3d rotate(final Vec3d vec, final EnumFacing side) {
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
    
    public static Vec3d revRotate(final Vec3d vec, final EnumFacing side) {
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
    
    public static Vec3d rotateAroundX(final Vec3d vec, final float angle) {
        final float var2 = MathHelper.cos(angle);
        final float var3 = MathHelper.sin(angle);
        final double var4 = vec.x;
        final double var5 = vec.y * var2 + vec.z * var3;
        final double var6 = vec.z * var2 - vec.y * var3;
        return new Vec3d(var4, var5, var6);
    }
    
    public static Vec3d rotateAroundY(final Vec3d vec, final float angle) {
        final float var2 = MathHelper.cos(angle);
        final float var3 = MathHelper.sin(angle);
        final double var4 = vec.x * var2 + vec.z * var3;
        final double var5 = vec.y;
        final double var6 = vec.z * var2 - vec.x * var3;
        return new Vec3d(var4, var5, var6);
    }
    
    public static Vec3d rotateAroundZ(final Vec3d vec, final float angle) {
        final float var2 = MathHelper.cos(angle);
        final float var3 = MathHelper.sin(angle);
        final double var4 = vec.x * var2 + vec.y * var3;
        final double var5 = vec.y * var2 - vec.x * var3;
        final double var6 = vec.z;
        return new Vec3d(var4, var5, var6);
    }
    
    public static RayTraceResult rayTrace(final World worldIn, final Entity entityIn, final boolean useLiquids) {
        double d3 = 5.0;
        if (entityIn instanceof EntityPlayerMP) {
            d3 = ((EntityPlayerMP)entityIn).interactionManager.getBlockReachDistance();
        }
        return rayTrace(worldIn, entityIn, useLiquids, d3);
    }
    
    public static RayTraceResult rayTrace(final World worldIn, final Entity entityIn, final boolean useLiquids, final double range) {
        final float f = entityIn.rotationPitch;
        final float f2 = entityIn.rotationYaw;
        final double d0 = entityIn.posX;
        final double d2 = entityIn.posY + entityIn.getEyeHeight();
        final double d3 = entityIn.posZ;
        final Vec3d vec3d = new Vec3d(d0, d2, d3);
        final float f3 = MathHelper.cos(-f2 * 0.017453292f - 3.1415927f);
        final float f4 = MathHelper.sin(-f2 * 0.017453292f - 3.1415927f);
        final float f5 = -MathHelper.cos(-f * 0.017453292f);
        final float f6 = MathHelper.sin(-f * 0.017453292f);
        final float f7 = f4 * f5;
        final float f8 = f3 * f5;
        final Vec3d vec3d2 = vec3d.addVector(f7 * range, f6 * range, f8 * range);
        return worldIn.rayTraceBlocks(vec3d, vec3d2, useLiquids, !useLiquids, false);
    }
    
    public static RayTraceResult rayTrace(final World worldIn, final Entity entityIn, final Vec3d lookvec, final boolean useLiquids, final double range) {
        final double d0 = entityIn.posX;
        final double d2 = entityIn.posY + entityIn.getEyeHeight();
        final double d3 = entityIn.posZ;
        final Vec3d vec3d = new Vec3d(d0, d2, d3);
        final Vec3d vec3d2 = vec3d.addVector(lookvec.x * range, lookvec.y * range, lookvec.z * range);
        return worldIn.rayTraceBlocks(vec3d, vec3d2, useLiquids, !useLiquids, false);
    }
    
    public static Field getField(final Class clazz, final String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        }
        catch (final NoSuchFieldException e) {
            final Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            }
            return getField(superClass, fieldName);
        }
    }
    
    public static AxisAlignedBB rotateBlockAABB(final AxisAlignedBB aabb, final EnumFacing facing) {
        final Cuboid6 c = new Cuboid6(aabb).add(new Vector3(-0.5, -0.5, -0.5)).apply(Rotation.sideRotations[facing.getIndex()]).add(new Vector3(0.5, 0.5, 0.5));
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
