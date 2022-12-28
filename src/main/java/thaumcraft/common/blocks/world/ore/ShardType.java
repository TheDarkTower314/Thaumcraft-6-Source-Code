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
    
    private static ShardType[] METADATA_LOOKUP;
    private int metadata;
    private String name;
    private Aspect aspect;
    private Block ore;
    
    private ShardType(int metadata, String unlocalizedName, Aspect aspect) {
        this.metadata = metadata;
        name = unlocalizedName;
        this.aspect = aspect;
    }
    
    public int getMetadata() {
        return metadata;
    }
    
    public Aspect getAspect() {
        return aspect;
    }
    
    public Block getOre() {
        return ore;
    }
    
    public void setOre(Block b) {
        ore = b;
    }
    
    public String getUnlocalizedName() {
        return name;
    }
    
    @Override
    public String toString() {
        return getUnlocalizedName();
    }
    
    public static int getMetaByAspect(Aspect a) {
        ShardType[] var0 = values();
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
        return name;
    }
    
    static {
        METADATA_LOOKUP = new ShardType[values().length];
        for (ShardType var4 : values()) {
            ShardType.METADATA_LOOKUP[var4.getMetadata()] = var4;
        }
    }
}
