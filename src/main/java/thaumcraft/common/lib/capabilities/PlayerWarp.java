// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.capabilities;

import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import thaumcraft.api.capabilities.IPlayerWarp;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class PlayerWarp
{
    public static void preInit() {
        CapabilityManager.INSTANCE.register(IPlayerWarp.class, new Capability.IStorage<IPlayerWarp>() {
            public NBTTagCompound writeNBT(final Capability<IPlayerWarp> capability, final IPlayerWarp instance, final EnumFacing side) {
                return instance.serializeNBT();
            }
            
            public void readNBT(final Capability<IPlayerWarp> capability, final IPlayerWarp instance, final EnumFacing side, final NBTBase nbt) {
                if (nbt instanceof NBTTagCompound) {
                    instance.deserializeNBT((NBTTagCompound) nbt);
                }
            }
        }, DefaultImpl::new);
    }
    
    private static class DefaultImpl implements IPlayerWarp
    {
        private int[] warp;
        private int counter;
        
        private DefaultImpl() {
            this.warp = new int[EnumWarpType.values().length];
        }
        
        @Override
        public void clear() {
            this.warp = new int[EnumWarpType.values().length];
            this.counter = 0;
        }
        
        @Override
        public int get(@Nonnull final EnumWarpType type) {
            return this.warp[type.ordinal()];
        }
        
        @Override
        public void set(final EnumWarpType type, final int amount) {
            this.warp[type.ordinal()] = MathHelper.clamp(amount, 0, 500);
        }
        
        @Override
        public int add(@Nonnull final EnumWarpType type, final int amount) {
            return this.warp[type.ordinal()] = MathHelper.clamp(this.warp[type.ordinal()] + amount, 0, 500);
        }
        
        @Override
        public int reduce(@Nonnull final EnumWarpType type, final int amount) {
            return this.warp[type.ordinal()] = MathHelper.clamp(this.warp[type.ordinal()] - amount, 0, 500);
        }
        
        @Override
        public void sync(@Nonnull final EntityPlayerMP player) {
            PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player), player);
        }
        
        public NBTTagCompound serializeNBT() {
            final NBTTagCompound properties = new NBTTagCompound();
            properties.setIntArray("warp", this.warp);
            properties.setInteger("counter", this.getCounter());
            return properties;
        }
        
        public void deserializeNBT(final NBTTagCompound properties) {
            if (properties == null) {
                return;
            }
            this.clear();
            final int[] ba = properties.getIntArray("warp");
            if (ba != null) {
                int l = EnumWarpType.values().length;
                if (ba.length < l) {
                    l = ba.length;
                }
                for (int a = 0; a < l; ++a) {
                    this.warp[a] = ba[a];
                }
            }
            this.setCounter(properties.getInteger("counter"));
        }
        
        @Override
        public int getCounter() {
            return this.counter;
        }
        
        @Override
        public void setCounter(final int amount) {
            this.counter = amount;
        }
    }
    
    public static class Provider implements ICapabilitySerializable<NBTTagCompound>
    {
        public static final ResourceLocation NAME;
        private final DefaultImpl warp;
        
        public Provider() {
            this.warp = new DefaultImpl();
        }
        
        public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
            return capability == ThaumcraftCapabilities.WARP;
        }
        
        public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
            if (capability == ThaumcraftCapabilities.WARP) {
                return ThaumcraftCapabilities.WARP.cast(this.warp);
            }
            return null;
        }
        
        public NBTTagCompound serializeNBT() {
            return this.warp.serializeNBT();
        }
        
        public void deserializeNBT(final NBTTagCompound nbt) {
            this.warp.deserializeNBT(nbt);
        }
        
        static {
            NAME = new ResourceLocation("thaumcraft", "warp");
        }
    }
}
