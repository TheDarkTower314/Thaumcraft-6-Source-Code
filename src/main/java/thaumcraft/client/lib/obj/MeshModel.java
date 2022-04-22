// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.obj;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelManager;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Vector3;
import java.util.Iterator;
import java.util.ArrayList;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.util.List;

public class MeshModel
{
    public List<Vector3f> positions;
    public List<Vector3f> normals;
    public List<Vector2f> texCoords;
    public List<MeshPart> parts;
    
    public MeshModel() {
        parts = new ArrayList<MeshPart>();
    }
    
    public MeshModel clone() {
        final MeshModel mm = new MeshModel();
        mm.parts = new ArrayList<MeshPart>();
        for (final MeshPart mp : parts) {
            mm.parts.add(mp);
        }
        if (positions != null) {
            mm.positions = new ArrayList<Vector3f>();
            for (final Vector3f mp2 : positions) {
                mm.positions.add((Vector3f)mp2.clone());
            }
        }
        if (normals != null) {
            mm.normals = new ArrayList<Vector3f>();
            for (final Vector3f mp2 : normals) {
                mm.normals.add((Vector3f)mp2.clone());
            }
        }
        if (texCoords != null) {
            mm.texCoords = new ArrayList<Vector2f>();
            for (final Vector2f mp3 : texCoords) {
                mm.texCoords.add((Vector2f)mp3.clone());
            }
        }
        return mm;
    }
    
    public void rotate(final double d, final Vector3 axis, final Vector3 offset) {
        final Rotation r = new Rotation(d, axis);
        final List<Vector3f> p = new ArrayList<Vector3f>();
        for (final Vector3f v : positions) {
            Vector3 vec = new Vector3(v.x, v.y, v.z);
            r.apply(vec);
            vec = vec.add(offset);
            p.add(new Vector3f((float)vec.x, (float)vec.y, (float)vec.z));
        }
        positions = p;
    }
    
    public void addPosition(final float x, final float y, final float z) {
        if (positions == null) {
            positions = new ArrayList<Vector3f>();
        }
        positions.add(new Vector3f(x, y, z));
    }
    
    public void addNormal(final float x, final float y, final float z) {
        if (normals == null) {
            normals = new ArrayList<Vector3f>();
        }
        normals.add(new Vector3f(x, y, z));
    }
    
    public void addTexCoords(final float x, final float y) {
        if (texCoords == null) {
            texCoords = new ArrayList<Vector2f>();
        }
        texCoords.add(new Vector2f(x, y));
    }
    
    public void addPart(final MeshPart part) {
        parts.add(part);
    }
    
    public void addPart(final MeshPart part, final int ti) {
        parts.add(new MeshPart(part, ti));
    }
    
    private int getColorValue(final Vector3f color) {
        final int r = (int)color.x;
        final int g = (int)color.y;
        final int b = (int)color.z;
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
    
    public List<BakedQuad> bakeModel(final ModelManager manager) {
        final List<BakedQuad> bakeList = new ArrayList<BakedQuad>();
        for (int j = 0; j < parts.size(); ++j) {
            final MeshPart part = parts.get(j);
            TextureAtlasSprite sprite = null;
            int color = -1;
            if (part.material != null) {
                if (part.material.DiffuseTextureMap != null) {
                    sprite = manager.getTextureMap().getAtlasSprite(part.material.DiffuseTextureMap);
                }
                else if (part.material.AmbientTextureMap != null) {
                    sprite = manager.getTextureMap().getAtlasSprite(part.material.AmbientTextureMap);
                }
                if (part.material.DiffuseColor != null) {
                    color = getColorValue(part.material.DiffuseColor);
                }
            }
            for (int i = 0; i < part.indices.size(); i += 4) {
                final BakedQuad quad = bakeQuad(part, i, sprite, color);
                bakeList.add(quad);
            }
        }
        return bakeList;
    }
    
    public List<BakedQuad> bakeModel(final TextureAtlasSprite sprite) {
        final List<BakedQuad> bakeList = new ArrayList<BakedQuad>();
        for (int j = 0; j < parts.size(); ++j) {
            final MeshPart part = parts.get(j);
            final int color = -1;
            for (int i = 0; i < part.indices.size(); i += 4) {
                final BakedQuad quad = bakeQuad(part, i, sprite, color);
                bakeList.add(quad);
            }
        }
        return bakeList;
    }
    
    private BakedQuad bakeQuad(final MeshPart part, final int startIndex, final TextureAtlasSprite sprite, final int color) {
        final int[] faceData = new int[28];
        for (int i = 0; i < 4; ++i) {
            Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
            Vector2f texCoord = new Vector2f(0.0f, 0.0f);
            int p = 0;
            final int[] indices = part.indices.get(startIndex + i);
            if (positions != null) {
                position = positions.get(indices[p++]);
            }
            if (normals != null) {
                ++p;
            }
            if (texCoords != null) {
                texCoord = texCoords.get(indices[p++]);
            }
            storeVertexData(faceData, i, position, texCoord, sprite, color);
        }
        return new BakedQuad(faceData, part.name.contains("focus") ? 1 : part.tintIndex, FaceBakery.getFacingFromVertexData(faceData), sprite, false, DefaultVertexFormats.BLOCK);
    }
    
    private static void storeVertexData(final int[] faceData, final int storeIndex, final Vector3f position, final Vector2f faceUV, final TextureAtlasSprite sprite, final int shadeColor) {
        final int l = storeIndex * 7;
        faceData[l + 0] = Float.floatToRawIntBits(position.x);
        faceData[l + 1] = Float.floatToRawIntBits(position.y);
        faceData[l + 2] = Float.floatToRawIntBits(position.z);
        faceData[l + 3] = shadeColor;
        if (sprite != null) {
            faceData[l + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU(faceUV.x * 16.0f));
            faceData[l + 5] = Float.floatToRawIntBits(sprite.getInterpolatedV(faceUV.y * 16.0f));
        }
        else {
            faceData[l + 4] = Float.floatToRawIntBits(faceUV.x);
            faceData[l + 5] = Float.floatToRawIntBits(faceUV.y);
        }
        faceData[l + 6] = 0;
    }
}
