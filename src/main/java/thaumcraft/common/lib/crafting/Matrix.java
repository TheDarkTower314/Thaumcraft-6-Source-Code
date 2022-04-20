// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.crafting;

import thaumcraft.api.crafting.Part;

public class Matrix
{
    int rows;
    int cols;
    Part[][] matrix;
    
    public Matrix(final Part[][] matrix) {
        this.rows = matrix.length;
        this.cols = matrix[0].length;
        this.matrix = new Part[this.rows][this.cols];
        for (int i = 0; i < this.rows; ++i) {
            for (int j = 0; j < this.cols; ++j) {
                this.matrix[i][j] = matrix[i][j];
            }
        }
    }
    
    public void Rotate90DegRight(final int times) {
        for (int a = 0; a < times; ++a) {
            final Part[][] newMatrix = new Part[this.cols][this.rows];
            for (int i = 0; i < this.rows; ++i) {
                for (int j = 0; j < this.cols; ++j) {
                    newMatrix[j][this.rows - i - 1] = this.matrix[i][j];
                }
            }
            this.matrix = newMatrix;
            final int tmp = this.rows;
            this.rows = this.cols;
            this.cols = tmp;
        }
    }
    
    public Part[][] getMatrix() {
        return this.matrix;
    }
}
