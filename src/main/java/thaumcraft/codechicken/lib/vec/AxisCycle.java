package thaumcraft.codechicken.lib.vec;


public class AxisCycle
{
    public static Transformation[] cycles;
    
    static {
        AxisCycle.cycles = new Transformation[] { new RedundantTransformation(), new VariableTransformation(new Matrix4(0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(Vector3 vec) {
                    double d0 = vec.x;
                    double d2 = vec.y;
                    double d3 = vec.z;
                    vec.x = d3;
                    vec.y = d0;
                    vec.z = d2;
                }
                
                @Override
                public Transformation inverse() {
                    return AxisCycle.cycles[2];
                }
            }, new VariableTransformation(new Matrix4(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)) {
                @Override
                public void apply(Vector3 vec) {
                    double d0 = vec.x;
                    double d2 = vec.y;
                    double d3 = vec.z;
                    vec.x = d2;
                    vec.y = d3;
                    vec.z = d0;
                }
                
                @Override
                public Transformation inverse() {
                    return AxisCycle.cycles[1];
                }
            } };
    }
}
