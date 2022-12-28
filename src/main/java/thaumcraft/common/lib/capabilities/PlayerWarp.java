package thaumcraft.common.lib.capabilities;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;


public class PlayerWarp
{
    public static void preInit() {
        CapabilityManager.INSTANCE.register(IPlayerWarp.class, new Capability.IStorage<IPlayerWarp>() {
            public NBTTagCompound writeNBT(Capability<IPlayerWarp> capability, IPlayerWarp instance, EnumFacing side) {
                return instance.serializeNBT();
            }
            
            public void readNBT(Capability<IPlayerWarp> capability, IPlayerWarp instance, EnumFacing side, NBTBase nbt) {
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
            warp = new int[EnumWarpType.values().length];
        }
        
        @Override
        public void clear() {
            warp = new int[EnumWarpType.values().length];
            counter = 0;
        }
        
        @Override
        public int get(@Nonnull EnumWarpType type) {
            return warp[type.ordinal()];
        }
        
        @Override
        public void set(EnumWarpType type, int amount) {
            warp[type.ordinal()] = MathHelper.clamp(amount, 0, 500);
        }
        
        @Override
        public int add(@Nonnull EnumWarpType type, int amount) {
            return warp[type.ordinal()] = MathHelper.clamp(warp[type.ordinal()] + amount, 0, 500);
        }
        
        @Override
        public int reduce(@Nonnull EnumWarpType type, int amount) {
            return warp[type.ordinal()] = MathHelper.clamp(warp[type.ordinal()] - amount, 0, 500);
        }
        
        @Override
        public void sync(@Nonnull EntityPlayerMP player) {
            PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player), player);
        }
        
        public NBTTagCompound serializeNBT() {
            NBTTagCompound properties = new NBTTagCompound();
            properties.setIntArray("warp", warp);
            properties.setInteger("counter", getCounter());
            return properties;
        }
        
        public void deserializeNBT(NBTTagCompound properties) {
            if (properties == null) {
                return;
            }
            clear();
            int[] ba = properties.getIntArray("warp");
            if (ba != null) {
                int l = EnumWarpType.values().length;
                if (ba.length < l) {
                    l = ba.length;
                }
                for (int a = 0; a < l; ++a) {
                    warp[a] = ba[a];
                }
            }
            setCounter(properties.getInteger("counter"));
        }
        
        @Override
        public int getCounter() {
            return counter;
        }
        
        @Override
        public void setCounter(int amount) {
            counter = amount;
        }
    }
    
    public static class Provider implements ICapabilitySerializable<NBTTagCompound>
    {
        public static ResourceLocation NAME;
        private DefaultImpl warp;
        
        public Provider() {
            warp = new DefaultImpl();
        }
        
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == ThaumcraftCapabilities.WARP;
        }
        
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == ThaumcraftCapabilities.WARP) {
                return ThaumcraftCapabilities.WARP.cast(warp);
            }
            return null;
        }
        
        public NBTTagCompound serializeNBT() {
            return warp.serializeNBT();
        }
        
        public void deserializeNBT(NBTTagCompound nbt) {
            warp.deserializeNBT(nbt);
        }
        
        static {
            NAME = new ResourceLocation("thaumcraft", "warp");
        }
    }
}
