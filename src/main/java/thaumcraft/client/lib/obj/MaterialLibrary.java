// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.obj;

import java.util.HashSet;
import javax.vecmath.Vector3f;
import java.io.IOException;
import net.minecraft.client.resources.IResource;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.common.base.Charsets;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.Dictionary;

class MaterialLibrary extends Dictionary<String, Material>
{
    static final Set<String> unknownCommands;
    private final Dictionary<String, Material> materialLibrary;
    private Material currentMaterial;
    
    public MaterialLibrary() {
        this.materialLibrary = new Hashtable<String, Material>();
    }
    
    @Override
    public int size() {
        return this.materialLibrary.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.materialLibrary.isEmpty();
    }
    
    @Override
    public Enumeration<String> keys() {
        return this.materialLibrary.keys();
    }
    
    @Override
    public Enumeration<Material> elements() {
        return this.materialLibrary.elements();
    }
    
    @Override
    public Material get(final Object key) {
        return this.materialLibrary.get(key);
    }
    
    @Override
    public Material put(final String key, final Material value) {
        return this.materialLibrary.put(key, value);
    }
    
    @Override
    public Material remove(final Object key) {
        return this.materialLibrary.remove(key);
    }
    
    public void loadFromStream(final ResourceLocation loc) throws IOException {
        final IResource res = Minecraft.getMinecraft().getResourceManager().getResource(loc);
        final InputStreamReader lineStream = new InputStreamReader(res.getInputStream(), Charsets.UTF_8);
        final BufferedReader lineReader = new BufferedReader(lineStream);
        while (true) {
            final String currentLine = lineReader.readLine();
            if (currentLine == null) {
                break;
            }
            if (currentLine.length() == 0) {
                continue;
            }
            if (currentLine.startsWith("#")) {
                continue;
            }
            final String[] fields = currentLine.split(" ", 2);
            final String keyword = fields[0];
            final String data = fields[1];
            if (keyword.equalsIgnoreCase("newmtl")) {
                this.pushMaterial(data);
            }
            else if (keyword.equalsIgnoreCase("Ka")) {
                this.currentMaterial.AmbientColor = this.parseVector3f(data);
            }
            else if (keyword.equalsIgnoreCase("Kd")) {
                this.currentMaterial.DiffuseColor = this.parseVector3f(data);
            }
            else if (keyword.equalsIgnoreCase("Ks")) {
                this.currentMaterial.SpecularColor = this.parseVector3f(data);
            }
            else if (keyword.equalsIgnoreCase("Ns")) {
                this.currentMaterial.SpecularCoefficient = this.parseFloat(data);
            }
            else if (keyword.equalsIgnoreCase("Tr")) {
                this.currentMaterial.Transparency = this.parseFloat(data);
            }
            else if (keyword.equalsIgnoreCase("illum")) {
                this.currentMaterial.IlluminationModel = this.parseInt(data);
            }
            else if (keyword.equalsIgnoreCase("map_Ka")) {
                this.currentMaterial.AmbientTextureMap = data;
                final ResourceLocation resourceLocation = new ResourceLocation(data);
            }
            else if (keyword.equalsIgnoreCase("map_Kd")) {
                this.currentMaterial.DiffuseTextureMap = data;
                final ResourceLocation resourceLocation2 = new ResourceLocation(data);
            }
            else if (keyword.equalsIgnoreCase("map_Ks")) {
                this.currentMaterial.SpecularTextureMap = data;
            }
            else if (keyword.equalsIgnoreCase("map_Ns")) {
                this.currentMaterial.SpecularHighlightTextureMap = data;
            }
            else if (keyword.equalsIgnoreCase("map_d")) {
                this.currentMaterial.AlphaTextureMap = data;
            }
            else if (keyword.equalsIgnoreCase("map_bump")) {
                this.currentMaterial.BumpMap = data;
            }
            else if (keyword.equalsIgnoreCase("bump")) {
                this.currentMaterial.BumpMap = data;
            }
            else if (keyword.equalsIgnoreCase("disp")) {
                this.currentMaterial.DisplacementMap = data;
            }
            else if (keyword.equalsIgnoreCase("decal")) {
                this.currentMaterial.StencilDecalMap = data;
            }
            else {
                if (MaterialLibrary.unknownCommands.contains(keyword)) {
                    continue;
                }
                MaterialLibrary.unknownCommands.add(keyword);
            }
        }
    }
    
    private float parseFloat(final String data) {
        return Float.parseFloat(data);
    }
    
    private int parseInt(final String data) {
        return Integer.parseInt(data);
    }
    
    private Vector3f parseVector3f(final String data) {
        final String[] parts = data.split(" ");
        return new Vector3f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
    }
    
    private void pushMaterial(final String materialName) {
        this.currentMaterial = new Material(materialName);
        this.materialLibrary.put(this.currentMaterial.Name, this.currentMaterial);
    }
    
    static {
        unknownCommands = new HashSet<String>();
    }
}
