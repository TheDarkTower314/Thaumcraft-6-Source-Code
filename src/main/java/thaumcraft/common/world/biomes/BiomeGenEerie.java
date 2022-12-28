package thaumcraft.common.world.biomes;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.entities.monster.EntityWisp;


public class BiomeGenEerie extends Biome
{
    public BiomeGenEerie(Biome.BiomeProperties par1) {
        super(par1);
        setRegistryName("thaumcraft", "eerie");
        spawnableCreatureList.clear();
        spawnableCreatureList.add(new Biome.SpawnListEntry(EntityBat.class, 3, 1, 1));
        spawnableMonsterList.add(new Biome.SpawnListEntry(EntityWitch.class, 8, 1, 1));
        spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEnderman.class, 4, 1, 1));
        if (ModConfig.CONFIG_WORLD.allowSpawnAngryZombie) {
            spawnableMonsterList.add(new Biome.SpawnListEntry(EntityBrainyZombie.class, 32, 1, 1));
            spawnableMonsterList.add(new Biome.SpawnListEntry(EntityGiantBrainyZombie.class, 8, 1, 1));
        }
        if (ModConfig.CONFIG_WORLD.allowSpawnWisp) {
            spawnableMonsterList.add(new Biome.SpawnListEntry(EntityWisp.class, 3, 1, 1));
        }
        if (ModConfig.CONFIG_WORLD.allowSpawnElder) {
            spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEldritchGuardian.class, 1, 1, 1));
        }
        decorator.treesPerChunk = 2;
        decorator.flowersPerChunk = 1;
        decorator.grassPerChunk = 2;
    }
    
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos p_180627_1_) {
        return 4212800;
    }
    
    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos p_180625_1_) {
        return 4212800;
    }
    
    public int getSkyColorByTemp(float par1) {
        return 2237081;
    }
    
    public int getWaterColorMultiplier() {
        return 3035999;
    }
}
