// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.codechicken.lib.lighting;

import thaumcraft.codechicken.lib.colour.ColourRGBA;
import net.minecraft.client.Minecraft;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import net.minecraft.world.IBlockAccess;
import thaumcraft.codechicken.lib.render.CCRenderState;

public class LightMatrix implements CCRenderState.IVertexOperation
{
    public static final int operationIndex;
    public int computed;
    public float[][] ao;
    public int[][] brightness;
    public IBlockAccess access;
    public BlockCoord pos;
    private int sampled;
    private float[] aSamples;
    private int[] bSamples;
    public static final int[][] ssamplem;
    public static final int[][] qsamplem;
    public static final float[] sideao;
    
    public LightMatrix() {
        this.computed = 0;
        this.ao = new float[13][4];
        this.brightness = new int[13][4];
        this.pos = new BlockCoord();
        this.sampled = 0;
        this.aSamples = new float[27];
        this.bSamples = new int[27];
    }
    
    public void locate(final IBlockAccess a, final int x, final int y, final int z) {
        this.access = a;
        this.pos.set(x, y, z);
        this.computed = 0;
        this.sampled = 0;
    }
    
    public void sample(final int i) {
        if ((this.sampled & 1 << i) == 0x0) {
            final int x = this.pos.x + i % 3 - 1;
            final int y = this.pos.y + i / 9 - 1;
            final int z = this.pos.z + i / 3 % 3 - 1;
            final IBlockState b = this.access.getBlockState(new BlockPos(x, y, z));
            this.bSamples[i] = this.access.getCombinedLight(new BlockPos(x, y, z), b.getBlock().getLightValue(b, this.access, new BlockPos(x, y, z)));
            this.aSamples[i] = b.getAmbientOcclusionLightValue();
        }
    }
    
    public int[] brightness(final int side) {
        this.sideSample(side);
        return this.brightness[side];
    }
    
    public float[] ao(final int side) {
        this.sideSample(side);
        return this.ao[side];
    }
    
    public void sideSample(final int side) {
        if ((this.computed & 1 << side) == 0x0) {
            final int[] ssample = LightMatrix.ssamplem[side];
            for (int q = 0; q < 4; ++q) {
                final int[] qsample = LightMatrix.qsamplem[q];
                if (Minecraft.isAmbientOcclusionEnabled()) {
                    this.interp(side, q, ssample[qsample[0]], ssample[qsample[1]], ssample[qsample[2]], ssample[qsample[3]]);
                }
                else {
                    this.interp(side, q, ssample[4], ssample[4], ssample[4], ssample[4]);
                }
            }
            this.computed |= 1 << side;
        }
    }
    
    private void interp(final int s, final int q, final int a, final int b, final int c, final int d) {
        this.sample(a);
        this.sample(b);
        this.sample(c);
        this.sample(d);
        this.ao[s][q] = interpAO(this.aSamples[a], this.aSamples[b], this.aSamples[c], this.aSamples[d]) * LightMatrix.sideao[s];
        this.brightness[s][q] = interpBrightness(this.bSamples[a], this.bSamples[b], this.bSamples[c], this.bSamples[d]);
    }
    
    public static float interpAO(final float a, final float b, final float c, final float d) {
        return (a + b + c + d) / 4.0f;
    }
    
    public static int interpBrightness(int a, int b, int c, final int d) {
        if (a == 0) {
            a = d;
        }
        if (b == 0) {
            b = d;
        }
        if (c == 0) {
            c = d;
        }
        return a + b + c + d >> 2 & 0xFF00FF;
    }
    
    @Override
    public boolean load() {
        CCRenderState.pipeline.addDependency(CCRenderState.colourAttrib);
        CCRenderState.pipeline.addDependency(CCRenderState.lightCoordAttrib);
        return true;
    }
    
    @Override
    public void operate() {
        final LC lc = CCRenderState.lc;
        final float[] a = this.ao(lc.side);
        final float f = a[0] * lc.fa + a[1] * lc.fb + a[2] * lc.fc + a[3] * lc.fd;
        final int[] b = this.brightness(lc.side);
        CCRenderState.setColour(ColourRGBA.multiplyC(CCRenderState.colour, f));
        CCRenderState.setBrightness((int)(b[0] * lc.fa + b[1] * lc.fb + b[2] * lc.fc + b[3] * lc.fd) & 0xFF00FF);
    }
    
    @Override
    public int operationID() {
        return LightMatrix.operationIndex;
    }
    
    static {
        operationIndex = CCRenderState.registerOperation();
        ssamplem = new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, { 18, 19, 20, 21, 22, 23, 24, 25, 26 }, { 0, 9, 18, 1, 10, 19, 2, 11, 20 }, { 6, 15, 24, 7, 16, 25, 8, 17, 26 }, { 0, 3, 6, 9, 12, 15, 18, 21, 24 }, { 2, 5, 8, 11, 14, 17, 20, 23, 26 }, { 9, 10, 11, 12, 13, 14, 15, 16, 17 }, { 9, 10, 11, 12, 13, 14, 15, 16, 17 }, { 3, 12, 21, 4, 13, 22, 5, 14, 23 }, { 3, 12, 21, 4, 13, 22, 5, 14, 23 }, { 1, 4, 7, 10, 13, 16, 19, 22, 25 }, { 1, 4, 7, 10, 13, 16, 19, 22, 25 }, { 13, 13, 13, 13, 13, 13, 13, 13, 13 } };
        qsamplem = new int[][] { { 0, 1, 3, 4 }, { 5, 1, 2, 4 }, { 6, 7, 3, 4 }, { 5, 7, 8, 4 } };
        sideao = new float[] { 0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f, 0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f, 1.0f };
    }
}
