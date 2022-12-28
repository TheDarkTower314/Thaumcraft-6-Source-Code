package thaumcraft.client.renderers.block;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thaumcraft.client.lib.obj.MeshLoader;
import thaumcraft.client.lib.obj.MeshModel;
import thaumcraft.client.lib.obj.MeshPart;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.blocks.world.ore.BlockCrystal;


public class CrystalModel implements IBakedModel
{
    ResourceLocation model;
    MeshModel sourceMesh;
    TextureAtlasSprite tex;
    
    public CrystalModel(TextureAtlasSprite tex2) {
        model = new ResourceLocation("thaumcraft", "models/obj/crystal.obj");
        tex = tex2;
        try {
            sourceMesh = new MeshLoader().loadFromResource(model);
            for (MeshPart mp : sourceMesh.parts) {
                mp.tintIndex = 0;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side == null && state instanceof IExtendedBlockState) {
            List<BakedQuad> ret = new ArrayList<BakedQuad>();
            IExtendedBlockState es = (IExtendedBlockState)state;
            int m = ((BlockCrystal)state.getBlock()).getGrowth(state) + 1;
            MeshModel mm = sourceMesh.clone();
            try {
                if (es != null) {
                    if (!(boolean)es.getValue(BlockCrystal.UP) || !(boolean)es.getValue(BlockCrystal.DOWN) || !(boolean)es.getValue(BlockCrystal.EAST) || !(boolean)es.getValue(BlockCrystal.WEST) || !(boolean)es.getValue(BlockCrystal.NORTH) || !(boolean)es.getValue(BlockCrystal.SOUTH)) {
                        if (es.getValue(BlockCrystal.UP)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            MeshModel mod = mm.clone();
                            mod.rotate(Math.toRadians(180.0), new Vector3(1.0, 0.0, 0.0), new Vector3(0.0, 1.0, 1.0));
                            for (BakedQuad quad : mod.bakeModel(getParticleTexture())) {
                                ret.add(quad);
                            }
                        }
                        if (es.getValue(BlockCrystal.DOWN)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand + 5L));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            for (BakedQuad quad2 : mm.bakeModel(getParticleTexture())) {
                                ret.add(quad2);
                            }
                        }
                        if (es.getValue(BlockCrystal.EAST)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand + 10L));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            MeshModel mod = mm.clone();
                            mod.rotate(Math.toRadians(90.0), new Vector3(1.0, 0.0, 0.0), new Vector3(0.0, 0.0, 0.0));
                            mod.rotate(Math.toRadians(270.0), new Vector3(0.0, 1.0, 0.0), new Vector3(1.0, 1.0, 0.0));
                            for (BakedQuad quad : mod.bakeModel(getParticleTexture())) {
                                ret.add(quad);
                            }
                        }
                        if (es.getValue(BlockCrystal.WEST)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand + 15L));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            MeshModel mod = mm.clone();
                            mod.rotate(Math.toRadians(90.0), new Vector3(1.0, 0.0, 0.0), new Vector3(0.0, 0.0, 0.0));
                            mod.rotate(Math.toRadians(90.0), new Vector3(0.0, 1.0, 0.0), new Vector3(0.0, 1.0, 1.0));
                            for (BakedQuad quad : mod.bakeModel(getParticleTexture())) {
                                ret.add(quad);
                            }
                        }
                        if (es.getValue(BlockCrystal.NORTH)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand + 20L));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            MeshModel mod = mm.clone();
                            mod.rotate(Math.toRadians(90.0), new Vector3(1.0, 0.0, 0.0), new Vector3(0.0, 1.0, 0.0));
                            for (BakedQuad quad : mod.bakeModel(getParticleTexture())) {
                                ret.add(quad);
                            }
                        }
                        if (es.getValue(BlockCrystal.SOUTH)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand + 25L));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            MeshModel mod = mm.clone();
                            mod.rotate(Math.toRadians(90.0), new Vector3(1.0, 0.0, 0.0), new Vector3(0.0, 0.0, 0.0));
                            mod.rotate(Math.toRadians(180.0), new Vector3(0.0, 1.0, 0.0), new Vector3(1.0, 1.0, 1.0));
                            for (BakedQuad quad : mod.bakeModel(getParticleTexture())) {
                                ret.add(quad);
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {}
            return ret;
        }
        return ImmutableList.of();
    }
    
    public boolean isAmbientOcclusion() {
        return true;
    }
    
    public boolean isGui3d() {
        return false;
    }
    
    public boolean isBuiltInRenderer() {
        return false;
    }
    
    public TextureAtlasSprite getParticleTexture() {
        return tex;
    }
    
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
    
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
