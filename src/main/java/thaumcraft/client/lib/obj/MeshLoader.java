// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.obj;

import java.util.HashSet;
import net.minecraft.client.resources.IResource;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.common.base.Charsets;
import net.minecraft.client.Minecraft;
import java.io.IOException;
import net.minecraft.util.ResourceLocation;
import scala.NotImplementedError;
import java.util.Set;

public class MeshLoader
{
    static final Set<String> unknownCommands;
    private MeshModel currentModel;
    private MeshPart currentPart;
    private MaterialLibrary currentMatLib;
    int firstIndex;
    int lastIndex;
    private String filePath;
    private String lastObjectName;
    
    private void addTexCoord(final String line) {
        final String[] args = line.split(" ");
        final float x = Float.parseFloat(args[0]);
        final float y = Float.parseFloat(args[1]);
        currentModel.addTexCoords(x, y);
    }
    
    private void addNormal(final String line) {
        final String[] args = line.split(" ");
        final float x = Float.parseFloat(args[0]);
        final float y = Float.parseFloat(args[1]);
        final float z = args[2].equals("\\\\") ? ((float)Math.sqrt(1.0f - x * x - y * y)) : Float.parseFloat(args[2]);
        currentModel.addNormal(x, y, z);
    }
    
    private void addPosition(final String line) {
        final String[] args = line.split(" ");
        final float x = Float.parseFloat(args[0]);
        final float y = Float.parseFloat(args[1]);
        final float z = Float.parseFloat(args[2]);
        currentModel.addPosition(x, y, z);
    }
    
    private void addFace(final String line) {
        final String[] args = line.split(" ");
        if (args.length < 3 || args.length > 4) {
            throw new NotImplementedError();
        }
        final String[] p1 = args[0].split("/");
        final String[] p2 = args[1].split("/");
        final String[] p3 = args[2].split("/");
        final int[] v1 = parseIndices(p1);
        final int[] v2 = parseIndices(p2);
        final int[] v3 = parseIndices(p3);
        if (args.length == 3) {
            currentPart.addTriangleFace(v1, v2, v3);
        }
        else if (args.length == 4) {
            final String[] p4 = args[3].split("/");
            final int[] v4 = parseIndices(p4);
            currentPart.addQuadFace(v1, v2, v3, v4);
        }
    }
    
    private int[] parseIndices(final String[] p1) {
        final int[] indices = new int[p1.length];
        for (int i = 0; i < p1.length; ++i) {
            indices[i] = Integer.parseInt(p1[i]) - 1;
        }
        return indices;
    }
    
    private void useMaterial(final String matName) {
        final Material mat = currentMatLib.get(matName);
        currentPart = new MeshPart();
        currentPart.name = lastObjectName;
        currentPart.material = mat;
        currentModel.addPart(currentPart);
    }
    
    private void newObject(final String line) {
        lastObjectName = line;
    }
    
    private void newGroup(final String line) {
        lastObjectName = line;
    }
    
    private void loadMaterialLibrary(final ResourceLocation locOfParent, final String path) throws IOException {
        String prefix = locOfParent.getResourcePath();
        final int pp = prefix.lastIndexOf(47);
        prefix = ((pp >= 0) ? prefix.substring(0, pp + 1) : "");
        final ResourceLocation loc = new ResourceLocation(locOfParent.getResourceDomain(), prefix + path);
        currentMatLib.loadFromStream(loc);
    }
    
    public MeshModel loadFromResource(final ResourceLocation loc) throws IOException {
        final IResource res = Minecraft.getMinecraft().getResourceManager().getResource(loc);
        final InputStreamReader lineStream = new InputStreamReader(res.getInputStream(), Charsets.UTF_8);
        final BufferedReader lineReader = new BufferedReader(lineStream);
        currentModel = new MeshModel();
        currentMatLib = new MaterialLibrary();
        while (true) {
            String currentLine = lineReader.readLine();
            if (currentLine == null) {
                break;
            }
            if (currentLine.length() == 0) {
                continue;
            }
            if (currentLine.startsWith("#")) {
                continue;
            }
            if (currentLine.startsWith("v  ")) {
                currentLine = currentLine.replaceFirst("v  ", "v ");
            }
            final String[] fields = currentLine.split(" ", 2);
            final String keyword = fields[0];
            final String data = fields[1];
            if (keyword.equalsIgnoreCase("o")) {
                newObject(data);
            }
            else if (keyword.equalsIgnoreCase("g")) {
                newGroup(data);
            }
            else if (keyword.equalsIgnoreCase("mtllib")) {
                loadMaterialLibrary(loc, data);
            }
            else if (keyword.equalsIgnoreCase("usemtl")) {
                useMaterial(data);
            }
            else if (keyword.equalsIgnoreCase("v")) {
                addPosition(data);
            }
            else if (keyword.equalsIgnoreCase("vn")) {
                addNormal(data);
            }
            else if (keyword.equalsIgnoreCase("vt")) {
                addTexCoord(data);
            }
            else if (keyword.equalsIgnoreCase("f")) {
                addFace(data);
            }
            else {
                if (MeshLoader.unknownCommands.contains(keyword)) {
                    continue;
                }
                MeshLoader.unknownCommands.add(keyword);
            }
        }
        return currentModel;
    }
    
    static {
        unknownCommands = new HashSet<String>();
    }
}
