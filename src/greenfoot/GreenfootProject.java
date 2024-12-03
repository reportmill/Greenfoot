package greenfoot;
import snap.gfx.Image;
import snap.util.Convert;
import snap.web.WebFile;
import snap.web.WebSite;
import snap.web.WebURL;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages a 'project.greenfoot' file.
 */
public class GreenfootProject {

    // The greenfoot project file
    private WebFile _greenfootProjectFile;

    // The project properties
    private Map<String, String> _props;

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
    private WebFile getProjectFile()  { return _greenfootProjectFile; }

    /**
     * Returns the project properties map.
     */
    protected Map<String, String> getProperties()
    {
        if (_props != null) return _props;
        return _props = getPropertiesImpl();
    }

    /**
     * Returns the project properties map.
     */
    private Map<String, String> getPropertiesImpl()
    {
        // Create map
        Map<String,String> props = new HashMap<>();

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
     * Returns a property for a given key.
     */
    public String getProperty(String aKey)
    {
        Map<String,String> properties = getProperties();
        return properties.get(aKey);
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
     * Returns the image for given class.
     */
    public Image getImageForClass(Class<?> aClass)
    {
        String imageKey = "class." + aClass.getSimpleName() + ".image";
        String imageFilename = getProperty(imageKey);
        if (imageFilename == null)
            return null;

        String imagePath = "/images/" + imageFilename;
        return Image.getImageForClassResource(aClass, imagePath);
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

    /**
     * Returns the greenfoot project.
     */
    public static GreenfootProject getGreenfootProjectForClass(Class<?> aClass)
    {
        // Get URL for project class
        String classFilename = aClass.getSimpleName() + ".class";
        WebURL classUrl = WebURL.getURL(aClass, classFilename);
        if (classUrl == null) {
            System.err.println("Couldn't find Greenfoot project file for class: " + aClass.getName());
            return null;
        }

        // Get site root dir for project class
        WebSite classSite = classUrl.getSite();
        WebFile cassSiteDir = classSite.getRootDir();

        // Return
        return GreenfootProject.getGreenfootProjectForDir(cassSiteDir);
    }
}
