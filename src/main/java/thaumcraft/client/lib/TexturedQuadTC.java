// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib;

import net.minecraft.client.renderer.Tessellator;
import java.awt.Color;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.model.PositionTextureVertex;

public class TexturedQuadTC
{
    public PositionTextureVertex[] vertexPositions;
    public int nVertices;
    private boolean invertNormal;
    private boolean flipped;
    
    public TexturedQuadTC(final PositionTextureVertex[] vertices) {
        flipped = false;
        vertexPositions = vertices;
        nVertices = vertices.length;
    }
    
    public TexturedQuadTC(final PositionTextureVertex[] vertices, final int texcoordU1, final int texcoordV1, final int texcoordU2, final int texcoordV2, final float textureWidth, final float textureHeight) {
        this(vertices);
        final float f2 = 0.0f / textureWidth;
        final float f3 = 0.0f / textureHeight;
        vertices[0] = vertices[0].setTexturePosition(texcoordU2 / textureWidth - f2, texcoordV1 / textureHeight + f3);
        vertices[1] = vertices[1].setTexturePosition(texcoordU1 / textureWidth + f2, texcoordV1 / textureHeight + f3);
        vertices[2] = vertices[2].setTexturePosition(texcoordU1 / textureWidth + f2, texcoordV2 / textureHeight - f3);
        vertices[3] = vertices[3].setTexturePosition(texcoordU2 / textureWidth - f2, texcoordV2 / textureHeight - f3);
    }
    
    public void flipFace() {
        flipped = true;
        final PositionTextureVertex[] apositiontexturevertex = new PositionTextureVertex[vertexPositions.length];
        for (int i = 0; i < vertexPositions.length; ++i) {
            apositiontexturevertex[i] = vertexPositions[vertexPositions.length - i - 1];
        }
        vertexPositions = apositiontexturevertex;
    }
    
    public void draw(final BufferBuilder renderer, final float scale, final int bright, final int color, final float alpha) {
        if (bright != -99) {
            renderer.begin(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
        }
        else {
            renderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        }
        final Color c = new Color(color);
        final int aa = bright;
        final int j = aa >> 16 & 0xFFFF;
        final int k = aa & 0xFFFF;
        for (int i = 0; i < 4; ++i) {
            final PositionTextureVertex positiontexturevertex = vertexPositions[i];
            if (bright != -99) {
                renderer.pos(positiontexturevertex.vector3D.x * scale, positiontexturevertex.vector3D.y * scale, positiontexturevertex.vector3D.z * scale).tex(positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY).color(c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha * 255.0f)).lightmap(j, k).normal(0.0f, 0.0f, 1.0f).endVertex();
            }
            else {
                renderer.pos(positiontexturevertex.vector3D.x * scale, positiontexturevertex.vector3D.y * scale, positiontexturevertex.vector3D.z * scale).tex(positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY).color(c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha * 255.0f)).normal(0.0f, 0.0f, 1.0f).endVertex();
            }
        }
        Tessellator.getInstance().draw();
    }
    
    public void draw(final BufferBuilder renderer, final float scale, final int bright, final int[] color, final float[] alpha) {
        if (bright != -99) {
            renderer.begin(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
        }
        else {
            renderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        }
        final int aa = bright;
        final int j = aa >> 16 & 0xFFFF;
        final int k = aa & 0xFFFF;
        for (int i = 0; i < 4; ++i) {
            final int idx = flipped ? (3 - i) : i;
            final Color c = new Color(color[idx]);
            final PositionTextureVertex positiontexturevertex = vertexPositions[i];
            if (bright != -99) {
                renderer.pos(positiontexturevertex.vector3D.x * scale, positiontexturevertex.vector3D.y * scale, positiontexturevertex.vector3D.z * scale).tex(positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY).color(c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha[idx] * 255.0f)).lightmap(j, k).normal(0.0f, 0.0f, 1.0f).endVertex();
            }
            else {
                renderer.pos(positiontexturevertex.vector3D.x * scale, positiontexturevertex.vector3D.y * scale, positiontexturevertex.vector3D.z * scale).tex(positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY).color(c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha[idx] * 255.0f)).normal(0.0f, 0.0f, 1.0f).endVertex();
            }
        }
        Tessellator.getInstance().draw();
    }
}
