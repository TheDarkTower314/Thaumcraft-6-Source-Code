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
        rows = matrix.length;
        cols = matrix[0].length;
        this.matrix = new Part[rows][cols];
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                this.matrix[i][j] = matrix[i][j];
            }
        }
    }
    
    public void Rotate90DegRight(final int times) {
        for (int a = 0; a < times; ++a) {
            final Part[][] newMatrix = new Part[cols][rows];
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < cols; ++j) {
                    newMatrix[j][rows - i - 1] = matrix[i][j];
                }
            }
            matrix = newMatrix;
            final int tmp = rows;
            rows = cols;
            cols = tmp;
        }
    }
    
    public Part[][] getMatrix() {
        return matrix;
    }
}
