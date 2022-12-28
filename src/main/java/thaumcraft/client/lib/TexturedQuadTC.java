package thaumcraft.client.lib;
import java.awt.Color;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;


public class TexturedQuadTC
{
    public PositionTextureVertex[] vertexPositions;
    public int nVertices;
    private boolean invertNormal;
    private boolean flipped;
    
    public TexturedQuadTC(PositionTextureVertex[] vertices) {
        flipped = false;
        vertexPositions = vertices;
        nVertices = vertices.length;
    }
    
    public TexturedQuadTC(PositionTextureVertex[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight) {
        this(vertices);
        float f2 = 0.0f / textureWidth;
        float f3 = 0.0f / textureHeight;
        vertices[0] = vertices[0].setTexturePosition(texcoordU2 / textureWidth - f2, texcoordV1 / textureHeight + f3);
        vertices[1] = vertices[1].setTexturePosition(texcoordU1 / textureWidth + f2, texcoordV1 / textureHeight + f3);
        vertices[2] = vertices[2].setTexturePosition(texcoordU1 / textureWidth + f2, texcoordV2 / textureHeight - f3);
        vertices[3] = vertices[3].setTexturePosition(texcoordU2 / textureWidth - f2, texcoordV2 / textureHeight - f3);
    }
    
    public void flipFace() {
        flipped = true;
        PositionTextureVertex[] apositiontexturevertex = new PositionTextureVertex[vertexPositions.length];
        for (int i = 0; i < vertexPositions.length; ++i) {
            apositiontexturevertex[i] = vertexPositions[vertexPositions.length - i - 1];
        }
        vertexPositions = apositiontexturevertex;
    }
    
    public void draw(BufferBuilder renderer, float scale, int bright, int color, float alpha) {
        if (bright != -99) {
            renderer.begin(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
        }
        else {
            renderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        }
        Color c = new Color(color);
        int aa = bright;
        int j = aa >> 16 & 0xFFFF;
        int k = aa & 0xFFFF;
        for (int i = 0; i < 4; ++i) {
            PositionTextureVertex positiontexturevertex = vertexPositions[i];
            if (bright != -99) {
                renderer.pos(positiontexturevertex.vector3D.x * scale, positiontexturevertex.vector3D.y * scale, positiontexturevertex.vector3D.z * scale).tex(positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY).color(c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha * 255.0f)).lightmap(j, k).normal(0.0f, 0.0f, 1.0f).endVertex();
            }
            else {
                renderer.pos(positiontexturevertex.vector3D.x * scale, positiontexturevertex.vector3D.y * scale, positiontexturevertex.vector3D.z * scale).tex(positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY).color(c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha * 255.0f)).normal(0.0f, 0.0f, 1.0f).endVertex();
            }
        }
        Tessellator.getInstance().draw();
    }
    
    public void draw(BufferBuilder renderer, float scale, int bright, int[] color, float[] alpha) {
        if (bright != -99) {
            renderer.begin(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
        }
        else {
            renderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        }
        int aa = bright;
        int j = aa >> 16 & 0xFFFF;
        int k = aa & 0xFFFF;
        for (int i = 0; i < 4; ++i) {
            int idx = flipped ? (3 - i) : i;
            Color c = new Color(color[idx]);
            PositionTextureVertex positiontexturevertex = vertexPositions[i];
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
