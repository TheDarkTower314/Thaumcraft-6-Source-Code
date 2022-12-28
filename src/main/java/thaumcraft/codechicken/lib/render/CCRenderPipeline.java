package thaumcraft.codechicken.lib.render;
import java.util.ArrayList;


public class CCRenderPipeline
{
    private ArrayList<CCRenderState.VertexAttribute> attribs;
    private ArrayList<CCRenderState.IVertexOperation> ops;
    private ArrayList<PipelineNode> nodes;
    private ArrayList<CCRenderState.IVertexOperation> sorted;
    private PipelineNode loading;
    
    public CCRenderPipeline() {
        attribs = new ArrayList<CCRenderState.VertexAttribute>();
        ops = new ArrayList<CCRenderState.IVertexOperation>();
        nodes = new ArrayList<PipelineNode>();
        sorted = new ArrayList<CCRenderState.IVertexOperation>();
    }
    
    public void setPipeline(CCRenderState.IVertexOperation... ops) {
        this.ops.clear();
        for (int i = 0; i < ops.length; ++i) {
            this.ops.add(ops[i]);
        }
        rebuild();
    }
    
    public void reset() {
        ops.clear();
        unbuild();
    }
    
    private void unbuild() {
        for (int i = 0; i < attribs.size(); ++i) {
            attribs.get(i).active = false;
        }
        attribs.clear();
        sorted.clear();
    }
    
    public void rebuild() {
        if (ops.isEmpty() || CCRenderState.model == null) {
            return;
        }
        while (nodes.size() < CCRenderState.operationCount()) {
            nodes.add(new PipelineNode());
        }
        unbuild();
        if (CCRenderState.useNormals) {
            addAttribute(CCRenderState.normalAttrib);
        }
        if (CCRenderState.baseColour != -1 || CCRenderState.alphaOverride >= 0) {
            addAttribute(CCRenderState.colourAttrib);
        }
        else if (CCRenderState.hasColour) {
            CCRenderState.setColour(-1);
        }
        for (int i = 0; i < ops.size(); ++i) {
            CCRenderState.IVertexOperation op = ops.get(i);
            loading = nodes.get(op.operationID());
            boolean loaded = op.load();
            if (loaded) {
                loading.op = op;
            }
            if (op instanceof CCRenderState.VertexAttribute) {
                if (loaded) {
                    attribs.add((CCRenderState.VertexAttribute)op);
                }
                else {
                    ((CCRenderState.VertexAttribute)op).active = false;
                }
            }
        }
        for (int i = 0; i < nodes.size(); ++i) {
            nodes.get(i).add();
        }
    }
    
    public void addRequirement(int opRef) {
        loading.deps.add(nodes.get(opRef));
    }
    
    public void addDependency(CCRenderState.VertexAttribute attrib) {
        loading.deps.add(nodes.get(attrib.operationID()));
        addAttribute(attrib);
    }
    
    public void addAttribute(CCRenderState.VertexAttribute attrib) {
        if (!attrib.active) {
            ops.add(attrib);
            attrib.active = true;
        }
    }
    
    public void operate() {
        for (int i = 0; i < sorted.size(); ++i) {
            sorted.get(i).operate();
        }
    }
    
    private class PipelineNode
    {
        public ArrayList<PipelineNode> deps;
        public CCRenderState.IVertexOperation op;
        
        private PipelineNode() {
            deps = new ArrayList<PipelineNode>();
        }
        
        public void add() {
            if (op == null) {
                return;
            }
            for (int i = 0; i < deps.size(); ++i) {
                deps.get(i).add();
            }
            deps.clear();
            sorted.add(op);
            op = null;
        }
    }
}
