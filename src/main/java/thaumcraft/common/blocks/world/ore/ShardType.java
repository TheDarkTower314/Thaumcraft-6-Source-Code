// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world.ore;

import net.minecraft.block.Block;
import thaumcraft.api.aspects.Aspect;

public enum ShardType
{
    AIR(0, "air", Aspect.AIR), 
    FIRE(1, "fire", Aspect.FIRE), 
    WATER(2, "water", Aspect.WATER), 
    EARTH(3, "earth", Aspect.EARTH), 
    ORDER(4, "order", Aspect.ORDER), 
    ENTROPY(5, "entropy", Aspect.ENTROPY), 
    FLUX(6, "flux", Aspect.FLUX);
    
    private static final ShardType[] METADATA_LOOKUP;
    private final int metadata;
    private final String name;
    private final Aspect aspect;
    private Block ore;
    
    private ShardType(final int metadata, final String unlocalizedName, final Aspect aspect) {
        this.metadata = metadata;
        this.name = unlocalizedName;
        this.aspect = aspect;
    }
    
    public int getMetadata() {
        return this.metadata;
    }
    
    public Aspect getAspect() {
        return this.aspect;
    }
    
    public Block getOre() {
        return this.ore;
    }
    
    public void setOre(final Block b) {
        this.ore = b;
    }
    
    public String getUnlocalizedName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return this.getUnlocalizedName();
    }
    
    public static int getMetaByAspect(final Aspect a) {
        final ShardType[] var0 = values();
        for (int var2 = var0.length, var3 = 0; var3 < var2; ++var3) {
            if (var0[var3].getAspect() == a) {
                return var3;
            }
        }
        return -1;
    }
    
    public static ShardType byMetadata(int metadata) {
        if (metadata < 0 || metadata >= ShardType.METADATA_LOOKUP.length) {
            metadata = 0;
        }
        return ShardType.METADATA_LOOKUP[metadata];
    }
    
    public String getName() {
        return this.name;
    }
    
    static {
        METADATA_LOOKUP = new ShardType[values().length];
        for (final ShardType var4 : values()) {
            ShardType.METADATA_LOOKUP[var4.getMetadata()] = var4;
        }
    }
}
