package greenfoot;
import snap.props.PropObject;
import snap.util.Convert;
import snap.util.ListUtils;
import snap.view.ViewUtils;
import snap.web.WebFile;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class manages a 'project.greenfoot' file.
 */
public class GreenfootProject extends PropObject {

    // The greenfoot project file
    private WebFile _greenfootProjectFile;

    // The project properties
    private Map<String, String> _props;

    // The root class node
    private ClassNode _rootClassNode;

    // A runnable to save file
    private Runnable _saveFileRun;

    // Constants for properties
    public static final String RootClassNode_Prop = "RootClassNode";

    /**
     * Constructor.
     */
    public GreenfootProject(WebFile greenfootProjectFile)
    {
        _greenfootProjectFile = greenfootProjectFile;
    }

    /**
     * Returns the project file.
     */
    public WebFile getProjectFile()  { return _greenfootProjectFile; }

    /**
     * Returns the project properties map.
     */
    protected Map<String, String> getProperties()
    {
        if (_props != null) return _props;
        return _props = readFile();
    }

    /**
     * Returns a property for a given key.
     */
    public String getProperty(String aKey)
    {
        Map<String,String> properties = getProperties();
        return properties.get(aKey);
    }

    /**
     * Sets a property.
     */
    private void setProperty(String aKey, String aValue)
    {
        getProperties().put(aKey, aValue);
        saveFileLater();
    }

    /**
     * Returns an int property.
     */
    public int getIntProperty(String aKey)
    {
        String propString = getProperty(aKey);
        return Convert.intValue(propString);
    }

    /**
     * Returns the root node for project class tree.
     */
    public ClassNode getRootClassNode()  { return _rootClassNode; }

    /**
     * Sets the root node for project class tree.
     */
    public void setRootClassNode(ClassNode rootClassNode)
    {
        if (rootClassNode == getRootClassNode()) return;
        ClassNode.orderGreenfootRootNode(rootClassNode);
        firePropChange(RootClassNode_Prop, _rootClassNode, _rootClassNode = rootClassNode);
    }

    /**
     * Returns class for name.
     */
    public Class<?> getClassForName(String className)
    {
        ClassNode rootClassNode = getRootClassNode();
        if (rootClassNode != null)
            return rootClassNode.getClassForName(className);
        return null;
    }

    /**
     * Returns the last instantiated world class.
     */
    public Class<? extends World> getLastInstantiatedWorldClass()
    {
        String worldClassName = getProperty("world.lastInstantiated");
        return worldClassName != null ? (Class<? extends World>) getClassForName(worldClassName) : null;
    }

    /**
     * Returns the image name for class.
     */
    public String getImageNameForClass(Class<?> aClass)
    {
        String imageKey = "class." + aClass.getSimpleName() + ".image";
        return getProperty(imageKey);
    }

    /**
     * Sets the image name for class name.
     */
    public void setImageNameForClassName(String className, String imageName)
    {
        String imageKey = "class." + className + ".image";
        setProperty(imageKey, imageName);
    }

    /**
     * Saves the file.
     */
    public void saveFileLater()
    {
        if (_saveFileRun == null)
            ViewUtils.runLater(_saveFileRun = this::writeFile);
    }

    /**
     * Reads the file.
     */
    private Map<String, String> readFile()
    {
        // Create map
        Map<String,String> props = new LinkedHashMap<>();

        // Get project file
        WebFile projectFile = getProjectFile();
        if (projectFile == null)
            return props;

        // Get project file lines
        String text = projectFile.getText();
        String[] lines = text != null ? text.split("\\n") : null;
        if (lines == null)
            return props;

        // Iterate over lines and get key/value for each
        for (String line : lines) {
            String[] parts = line.split("=");
            if (parts.length > 1)
                props.put(parts[0].trim(), parts[1].trim());
        }

        // Return
        return props;
    }

    /**
     * Writes the file.
     */
    private void writeFile()
    {
        // Get properties string
        String propertiesText = ListUtils.mapToStringsAndJoin(getProperties().entrySet(),
                entry -> entry.getKey() + "=" + entry.getValue(), "\n");

        // Get project file
        WebFile projectFile = getProjectFile();
        if (projectFile == null)
            return;

        // Set text and save
        projectFile.setText(propertiesText);
        projectFile.save();
        _saveFileRun = null;
    }

    /**
     * Returns the greenfoot project for given project dir.
     */
    public static GreenfootProject getGreenfootProjectForDir(WebFile projDir)
    {
        // If project already set in dir, just return
        GreenfootProject greenfootProject = (GreenfootProject) projDir.getProp(GreenfootProject.class.getName());
        if (greenfootProject != null)
            return greenfootProject;

        // Try to create greenfoot project for dir
        greenfootProject = createGreenfootProjectForDir(projDir);
        if (greenfootProject != null)
            projDir.setProp(GreenfootProject.class.getName(), greenfootProject);

        // Return
        return greenfootProject;
    }

    /**
     * Creates the greenfoot project for given project dir.
     */
    private static GreenfootProject createGreenfootProjectForDir(WebFile projectDir)
    {
        // Get project file - just return if not found
        WebFile greenfootProjectFile = projectDir.getFileForName("/src/project.greenfoot");
        if (greenfootProjectFile == null)
            return null;

        // Create and return
        return new GreenfootProject(greenfootProjectFile);
    }
}
