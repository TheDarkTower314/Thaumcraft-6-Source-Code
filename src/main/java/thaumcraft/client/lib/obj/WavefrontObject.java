package thaumcraft.client.lib.obj;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class WavefrontObject implements IModelCustom
{
    private static Pattern vertexPattern;
    private static Pattern vertexNormalPattern;
    private static Pattern textureCoordinatePattern;
    private static Pattern face_V_VT_VN_Pattern;
    private static Pattern face_V_VT_Pattern;
    private static Pattern face_V_VN_Pattern;
    private static Pattern face_V_Pattern;
    private static Pattern groupObjectPattern;
    private static Matcher vertexMatcher;
    private static Matcher vertexNormalMatcher;
    private static Matcher textureCoordinateMatcher;
    private static Matcher face_V_VT_VN_Matcher;
    private static Matcher face_V_VT_Matcher;
    private static Matcher face_V_VN_Matcher;
    private static Matcher face_V_Matcher;
    private static Matcher groupObjectMatcher;
    public ArrayList<Vertex> vertices;
    public ArrayList<Vertex> vertexNormals;
    public ArrayList<TextureCoordinate> textureCoordinates;
    public ArrayList<GroupObject> groupObjects;
    private GroupObject currentGroupObject;
    private String fileName;
    
    public WavefrontObject(ResourceLocation resource) throws ModelFormatException {
        vertices = new ArrayList<Vertex>();
        vertexNormals = new ArrayList<Vertex>();
        textureCoordinates = new ArrayList<TextureCoordinate>();
        groupObjects = new ArrayList<GroupObject>();
        fileName = resource.toString();
        try {
            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
            loadObjModel(res.getInputStream());
        }
        catch (IOException e) {
            throw new ModelFormatException("IO Exception reading model format", e);
        }
    }
    
    public WavefrontObject(String filename, InputStream inputStream) throws ModelFormatException {
        vertices = new ArrayList<Vertex>();
        vertexNormals = new ArrayList<Vertex>();
        textureCoordinates = new ArrayList<TextureCoordinate>();
        groupObjects = new ArrayList<GroupObject>();
        fileName = filename;
        loadObjModel(inputStream);
    }
    
    private void loadObjModel(InputStream inputStream) throws ModelFormatException {
        BufferedReader reader = null;
        String currentLine = null;
        int lineCount = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((currentLine = reader.readLine()) != null) {
                ++lineCount;
                currentLine = currentLine.replaceAll("\\s+", " ").trim();
                if (!currentLine.startsWith("#")) {
                    if (currentLine.length() == 0) {
                        continue;
                    }
                    if (currentLine.startsWith("v ")) {
                        Vertex vertex = parseVertex(currentLine, lineCount);
                        if (vertex == null) {
                            continue;
                        }
                        vertices.add(vertex);
                    }
                    else if (currentLine.startsWith("vn ")) {
                        Vertex vertex = parseVertexNormal(currentLine, lineCount);
                        if (vertex == null) {
                            continue;
                        }
                        vertexNormals.add(vertex);
                    }
                    else if (currentLine.startsWith("vt ")) {
                        TextureCoordinate textureCoordinate = parseTextureCoordinate(currentLine, lineCount);
                        if (textureCoordinate == null) {
                            continue;
                        }
                        textureCoordinates.add(textureCoordinate);
                    }
                    else if (currentLine.startsWith("f ")) {
                        if (currentGroupObject == null) {
                            currentGroupObject = new GroupObject("Default");
                        }
                        Face face = parseFace(currentLine, lineCount);
                        if (face == null) {
                            continue;
                        }
                        currentGroupObject.faces.add(face);
                    }
                    else {
                        if (!(currentLine.startsWith("g ") | currentLine.startsWith("o "))) {
                            continue;
                        }
                        GroupObject group = parseGroupObject(currentLine, lineCount);
                        if (group != null && currentGroupObject != null) {
                            groupObjects.add(currentGroupObject);
                        }
                        currentGroupObject = group;
                    }
                }
            }
            groupObjects.add(currentGroupObject);
        }
        catch (IOException e) {
            throw new ModelFormatException("IO Exception reading model format", e);
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException ex) {}
            try {
                inputStream.close();
            }
            catch (IOException ex2) {}
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderAll() {
        Tessellator tessellator = Tessellator.getInstance();
        if (currentGroupObject != null) {
            tessellator.getBuffer().begin(currentGroupObject.glDrawingMode, DefaultVertexFormats.POSITION_TEX_NORMAL);
        }
        else {
            tessellator.getBuffer().begin(4, DefaultVertexFormats.POSITION_TEX_NORMAL);
        }
        tessellateAll(tessellator);
        tessellator.draw();
    }
    
    @SideOnly(Side.CLIENT)
    public void tessellateAll(Tessellator tessellator) {
        for (GroupObject groupObject : groupObjects) {
            groupObject.render(tessellator);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderOnly(String... groupNames) {
        for (GroupObject groupObject : groupObjects) {
            for (String groupName : groupNames) {
                if (groupName.equalsIgnoreCase(groupObject.name)) {
                    groupObject.render();
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void tessellateOnly(Tessellator tessellator, String... groupNames) {
        for (GroupObject groupObject : groupObjects) {
            for (String groupName : groupNames) {
                if (groupName.equalsIgnoreCase(groupObject.name)) {
                    groupObject.render(tessellator);
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public String[] getPartNames() {
        ArrayList<String> l = new ArrayList<String>();
        for (GroupObject groupObject : groupObjects) {
            l.add(groupObject.name);
        }
        return l.toArray(new String[0]);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderPart(String partName) {
        for (GroupObject groupObject : groupObjects) {
            if (partName.equalsIgnoreCase(groupObject.name)) {
                groupObject.render();
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void tessellatePart(Tessellator tessellator, String partName) {
        for (GroupObject groupObject : groupObjects) {
            if (partName.equalsIgnoreCase(groupObject.name)) {
                groupObject.render(tessellator);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderAllExcept(String... excludedGroupNames) {
        for (GroupObject groupObject : groupObjects) {
            boolean skipPart = false;
            for (String excludedGroupName : excludedGroupNames) {
                if (excludedGroupName.equalsIgnoreCase(groupObject.name)) {
                    skipPart = true;
                }
            }
            if (!skipPart) {
                groupObject.render();
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void tessellateAllExcept(Tessellator tessellator, String... excludedGroupNames) {
        for (GroupObject groupObject : groupObjects) {
            boolean exclude = false;
            for (String excludedGroupName : excludedGroupNames) {
                if (excludedGroupName.equalsIgnoreCase(groupObject.name)) {
                    exclude = true;
                }
            }
            if (!exclude) {
                groupObject.render(tessellator);
            }
        }
    }
    
    private Vertex parseVertex(String line, int lineCount) throws ModelFormatException {
        Vertex vertex = null;
        if (isValidVertexLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");
            try {
                if (tokens.length == 2) {
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]));
                }
                if (tokens.length == 3) {
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            }
            catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
            return vertex;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
    }
    
    private Vertex parseVertexNormal(String line, int lineCount) throws ModelFormatException {
        Vertex vertexNormal = null;
        if (isValidVertexNormalLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");
            try {
                if (tokens.length == 3) {
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            }
            catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
            return vertexNormal;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
    }
    
    private TextureCoordinate parseTextureCoordinate(String line, int lineCount) throws ModelFormatException {
        TextureCoordinate textureCoordinate = null;
        if (isValidTextureCoordinateLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");
            try {
                if (tokens.length == 2) {
                    return new TextureCoordinate(Float.parseFloat(tokens[0]), 1.0f - Float.parseFloat(tokens[1]));
                }
                if (tokens.length == 3) {
                    return new TextureCoordinate(Float.parseFloat(tokens[0]), 1.0f - Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            }
            catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
            return textureCoordinate;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
    }
    
    private Face parseFace(String line, int lineCount) throws ModelFormatException {
        Face face = null;
        if (isValidFaceLine(line)) {
            face = new Face();
            String trimmedLine = line.substring(line.indexOf(" ") + 1);
            String[] tokens = trimmedLine.split(" ");
            String[] subTokens = null;
            if (tokens.length == 3) {
                if (currentGroupObject.glDrawingMode == -1) {
                    currentGroupObject.glDrawingMode = 4;
                }
                else if (currentGroupObject.glDrawingMode != 4) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Invalid number of points for face (expected 4, found " + tokens.length + ")");
                }
            }
            else if (tokens.length == 4) {
                if (currentGroupObject.glDrawingMode == -1) {
                    currentGroupObject.glDrawingMode = 7;
                }
                else if (currentGroupObject.glDrawingMode != 7) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Invalid number of points for face (expected 3, found " + tokens.length + ")");
                }
            }
            if (isValidFace_V_VT_VN_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.textureCoordinates = new TextureCoordinate[tokens.length];
                face.vertexNormals = new Vertex[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("/");
                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                    face.vertexNormals[i] = vertexNormals.get(Integer.parseInt(subTokens[2]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            else if (isValidFace_V_VT_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.textureCoordinates = new TextureCoordinate[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("/");
                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            else if (isValidFace_V_VN_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.vertexNormals = new Vertex[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("//");
                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.vertexNormals[i] = vertexNormals.get(Integer.parseInt(subTokens[1]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            else {
                if (!isValidFace_V_Line(line)) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
                }
                face.vertices = new Vertex[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    face.vertices[i] = vertices.get(Integer.parseInt(tokens[i]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            return face;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
    }
    
    private GroupObject parseGroupObject(String line, int lineCount) throws ModelFormatException {
        GroupObject group = null;
        if (isValidGroupObjectLine(line)) {
            String trimmedLine = line.substring(line.indexOf(" ") + 1);
            if (trimmedLine.length() > 0) {
                group = new GroupObject(trimmedLine);
            }
            return group;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
    }
    
    private static boolean isValidVertexLine(String line) {
        if (WavefrontObject.vertexMatcher != null) {
            WavefrontObject.vertexMatcher.reset();
        }
        WavefrontObject.vertexMatcher = WavefrontObject.vertexPattern.matcher(line);
        return WavefrontObject.vertexMatcher.matches();
    }
    
    private static boolean isValidVertexNormalLine(String line) {
        if (WavefrontObject.vertexNormalMatcher != null) {
            WavefrontObject.vertexNormalMatcher.reset();
        }
        WavefrontObject.vertexNormalMatcher = WavefrontObject.vertexNormalPattern.matcher(line);
        return WavefrontObject.vertexNormalMatcher.matches();
    }
    
    private static boolean isValidTextureCoordinateLine(String line) {
        if (WavefrontObject.textureCoordinateMatcher != null) {
            WavefrontObject.textureCoordinateMatcher.reset();
        }
        WavefrontObject.textureCoordinateMatcher = WavefrontObject.textureCoordinatePattern.matcher(line);
        return WavefrontObject.textureCoordinateMatcher.matches();
    }
    
    private static boolean isValidFace_V_VT_VN_Line(String line) {
        if (WavefrontObject.face_V_VT_VN_Matcher != null) {
            WavefrontObject.face_V_VT_VN_Matcher.reset();
        }
        WavefrontObject.face_V_VT_VN_Matcher = WavefrontObject.face_V_VT_VN_Pattern.matcher(line);
        return WavefrontObject.face_V_VT_VN_Matcher.matches();
    }
    
    private static boolean isValidFace_V_VT_Line(String line) {
        if (WavefrontObject.face_V_VT_Matcher != null) {
            WavefrontObject.face_V_VT_Matcher.reset();
        }
        WavefrontObject.face_V_VT_Matcher = WavefrontObject.face_V_VT_Pattern.matcher(line);
        return WavefrontObject.face_V_VT_Matcher.matches();
    }
    
    private static boolean isValidFace_V_VN_Line(String line) {
        if (WavefrontObject.face_V_VN_Matcher != null) {
            WavefrontObject.face_V_VN_Matcher.reset();
        }
        WavefrontObject.face_V_VN_Matcher = WavefrontObject.face_V_VN_Pattern.matcher(line);
        return WavefrontObject.face_V_VN_Matcher.matches();
    }
    
    private static boolean isValidFace_V_Line(String line) {
        if (WavefrontObject.face_V_Matcher != null) {
            WavefrontObject.face_V_Matcher.reset();
        }
        WavefrontObject.face_V_Matcher = WavefrontObject.face_V_Pattern.matcher(line);
        return WavefrontObject.face_V_Matcher.matches();
    }
    
    private static boolean isValidFaceLine(String line) {
        return isValidFace_V_VT_VN_Line(line) || isValidFace_V_VT_Line(line) || isValidFace_V_VN_Line(line) || isValidFace_V_Line(line);
    }
    
    private static boolean isValidGroupObjectLine(String line) {
        if (WavefrontObject.groupObjectMatcher != null) {
            WavefrontObject.groupObjectMatcher.reset();
        }
        WavefrontObject.groupObjectMatcher = WavefrontObject.groupObjectPattern.matcher(line);
        return WavefrontObject.groupObjectMatcher.matches();
    }
    
    @Override
    public String getType() {
        return "obj";
    }
    
    static {
        WavefrontObject.vertexPattern = Pattern.compile("(v( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(v( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
        WavefrontObject.vertexNormalPattern = Pattern.compile("(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
        WavefrontObject.textureCoordinatePattern = Pattern.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *$)");
        WavefrontObject.face_V_VT_VN_Pattern = Pattern.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
        WavefrontObject.face_V_VT_Pattern = Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
        WavefrontObject.face_V_VN_Pattern = Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
        WavefrontObject.face_V_Pattern = Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
        WavefrontObject.groupObjectPattern = Pattern.compile("([go]( [\\w\\d\\.]+) *\\n)|([go]( [\\w\\d\\.]+) *$)");
    }
    
    public class TextureCoordinate
    {
        public float u;
        public float v;
        public float w;
        
        public TextureCoordinate(float u, float v) {
            this(u, v, 0.0f);
        }
        
        public TextureCoordinate(float u, float v, float w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }
    
    public class Face
    {
        public Vertex[] vertices;
        public Vertex[] vertexNormals;
        public Vertex faceNormal;
        public TextureCoordinate[] textureCoordinates;
        
        @SideOnly(Side.CLIENT)
        public void addFaceForRender(Tessellator tessellator) {
            addFaceForRender(tessellator, 5.0E-4f);
        }
        
        @SideOnly(Side.CLIENT)
        public void addFaceForRender(Tessellator tessellator, float textureOffset) {
            if (faceNormal == null) {
                faceNormal = calculateFaceNormal();
            }
            float averageU = 0.0f;
            float averageV = 0.0f;
            if (textureCoordinates != null && textureCoordinates.length > 0) {
                for (int i = 0; i < textureCoordinates.length; ++i) {
                    averageU += textureCoordinates[i].u;
                    averageV += textureCoordinates[i].v;
                }
                averageU /= textureCoordinates.length;
                averageV /= textureCoordinates.length;
            }
            for (int j = 0; j < vertices.length; ++j) {
                if (textureCoordinates != null && textureCoordinates.length > 0) {
                    float offsetU = textureOffset;
                    float offsetV = textureOffset;
                    if (textureCoordinates[j].u > averageU) {
                        offsetU = -offsetU;
                    }
                    if (textureCoordinates[j].v > averageV) {
                        offsetV = -offsetV;
                    }
                    tessellator.getBuffer().pos(vertices[j].x, vertices[j].y, vertices[j].z).tex(textureCoordinates[j].u + offsetU, textureCoordinates[j].v + offsetV).normal(faceNormal.x, faceNormal.y, faceNormal.z).endVertex();
                }
                else {
                    tessellator.getBuffer().pos(vertices[j].x, vertices[j].y, vertices[j].z).normal(faceNormal.x, faceNormal.y, faceNormal.z).endVertex();
                }
            }
        }
        
        public Vertex calculateFaceNormal() {
            Vec3d v1 = new Vec3d(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
            Vec3d v2 = new Vec3d(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);
            Vec3d normalVector = null;
            normalVector = v1.crossProduct(v2).normalize();
            return new Vertex((float)normalVector.x, (float)normalVector.y, (float)normalVector.z);
        }
    }
    
    public class GroupObject
    {
        public String name;
        public ArrayList<Face> faces;
        public int glDrawingMode;
        
        public GroupObject() {
            this("");
        }
        
        public GroupObject(String name) {
            this(name, -1);
        }
        
        public GroupObject(String name, int glDrawingMode) {
            faces = new ArrayList<Face>();
            this.name = name;
            this.glDrawingMode = glDrawingMode;
        }


        @SideOnly(Side.CLIENT)
        public void render() {
            if (faces.size() > 0) {
                Tessellator tessellator = Tessellator.getInstance();
                tessellator.getBuffer().begin(glDrawingMode, DefaultVertexFormats.POSITION_TEX_NORMAL);
                render(tessellator);
                tessellator.draw();
            }
        }
        
        @SideOnly(Side.CLIENT)
        public void render(Tessellator tessellator) {
            if (faces.size() > 0) {
                for (Face face : faces) {
                    face.addFaceForRender(tessellator);
                }
            }
        }
    }
    
    public class ModelFormatException extends RuntimeException
    {
        private static final long serialVersionUID = 2023547503969671835L;
        
        public ModelFormatException() {
        }
        
        public ModelFormatException(String message, Throwable cause) {
            super(message, cause);
        }
        
        public ModelFormatException(String message) {
            super(message);
        }
        
        public ModelFormatException(Throwable cause) {
            super(cause);
        }
    }
}
