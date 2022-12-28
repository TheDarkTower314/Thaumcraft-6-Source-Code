package thaumcraft.common.world.biomes;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;


public class BiomeGenEldritch extends Biome
{
    public BiomeGenEldritch(Biome.BiomeProperties p_i1990_1_) {
        super(p_i1990_1_);
        setRegistryName("thaumcraft", "eldritch");
        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
        spawnableMonsterList.add(new Biome.SpawnListEntry(EntityInhabitedZombie.class, 1, 1, 1));
        spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEldritchGuardian.class, 1, 1, 1));
        topBlock = Blocks.DIRT.getDefaultState();
        fillerBlock = Blocks.DIRT.getDefaultState();
    }
    
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float p_76731_1_) {
        return 0;
    }
    
    public void decorate(World world, Random random, BlockPos pos) {
    }
}
