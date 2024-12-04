package greenfoot;
import snap.geom.Point;
import snap.gfx.Image;
import snap.gfx.SoundClip;
import snap.props.PropObject;
import snap.util.Convert;
import snap.util.SnapUtils;
import snap.view.View;
import snap.viewx.DialogBox;
import java.util.*;

/**
 * The central class that manages a greenfoot app.
 */
public class GreenfootEnv extends PropObject {

    // The current speed
    private static int _speed = 50;

    // The mouse info
    private MouseInfo _mouseInfo = new MouseInfo();

    // The main player pane for the app (generally there is only one)
    private PlayerPane _playerPane;

    // A Random
    private static Random _random = new Random();

    // The greenfoot project
    private GreenfootProject _greenfootProject;

    // The loaded images
    private Map<String, Image> _imageCache = new HashMap<>();

    // The loaded SoundClips
    private Map<String, SoundClip> _soundClipCache = new HashMap<>();

    // The world class from the current project
    protected static Class<?> _worldClass;

    // Constants for properties
    public static final String GreenfootProject_Prop = "GreenfootProject";

    /**
     * Constructor.
     */
    public GreenfootEnv()
    {
    }

    /**
     * Returns a world.
     */
    public World getWorld()
    {
        PlayerPane playerPane = getPlayerPane();
        return playerPane.getWorld();
    }

    /**
     * Sets a world.
     */
    public void setWorld(World aWorld)
    {
        PlayerPane playerPane = getPlayerPane();
        playerPane.setWorld(aWorld);
    }

    /**
     * Returns the speed.
     */
    public int getSpeed()  { return _speed; }

    /**
     * Sets the speed of greenfoot playback.
     */
    public void setSpeed(int aValue)
    {
        _speed = aValue;

        // Set player pane timer delay for speed
        int timerPeriodMillis = GreenfootEnv.convertSpeedToDelayMillis(_speed);
        PlayerPane playerPane = getPlayerPane();
        playerPane.setTimerPeriod(timerPeriodMillis);
    }

    /**
     * Starts greenfoot playing.
     */
    public void start()
    {
        PlayerPane playerPane = getPlayerPane();
        playerPane.start();
    }

    /**
     * Stops Greenfoot from playing.
     */
    public void stop()
    {
        PlayerPane playerPane = getPlayerPane();
        playerPane.stop();
    }

    /**
     * Delays the execution by given number of time steps.
     */
    public void delay(int aValue)
    {
        System.out.println("Greenfoot.delay(): Not implemented yet");
    }

    /**
     * Plays a sound.
     */
    public void playSound(String aName)
    {
        SoundClip soundClip = getSoundClipForName(aName);
        if (soundClip != null)
            soundClip.play();
    }

    /**
     * Get the most recently pressed key, since the last time this method was called.
     */
    public String getKey()
    {
        System.err.println("Greenfoot.getKey: Not Impl");
        return "";
    }

    /**
     * Returns whether key is down.
     */
    public boolean isKeyDown(String aName)
    {
        WorldView worldView = getWorld().getWorldView();
        return worldView.isKeyDown(aName);
    }

    /**
     * Returns the MouseInfo.
     */
    public MouseInfo getMouseInfo()  { return _mouseInfo; }

    /**
     * Returns whether mouse was clicked on given actor/world.
     */
    public boolean mouseClicked(Object anObj)
    {
        World world = getWorld();
        if (anObj == null)
            return world != null && world.getWorldView().isMouseClicked();
        if (anObj instanceof World)
            return anObj == world && world.getWorldView().isMouseClicked();
        System.out.println("Mouse Clicked not supported");
        return false;
    }

    /**
     * Returns whether mouse was pressed on given actor/world.
     */
    public boolean mousePressed(Object anObj)
    {
        World world = getWorld();

        if (anObj == null)
            return world != null && world.getWorldView().isMouseDown();
        if (anObj instanceof World)
            return anObj == world && world.getWorldView().isMouseDown();
        if (anObj instanceof Actor)
            return ((Actor) anObj).getActorView().isMouseDown();
        return false;
    }

    /**
     * Returns whether mouse was clicked on given actor/world.
     */
    public boolean mouseMoved(Object anObj)
    {
        View view = anObj instanceof Actor ? ((Actor) anObj)._actorView : null;
        if (view == null)
            return false;
        Point pnt = view.parentToLocal(getWorld().getWorldView()._mx, getWorld().getWorldView()._my);
        return view.contains(pnt);
    }

    /**
     * Asks the user a question.
     */
    public String ask(String aPrompt)
    {
        stop();

        String title = "User Input";
        String output = DialogBox.showInputDialog(getPlayerPane().getUI(), title, aPrompt, "");
        start();
        return output;
    }

    /**
     * Returns a random number.
     */
    public int getRandomNumber(int aNum)  { return _random.nextInt(aNum); }

    /**
     * Returns the greenfoot PlayerPane.
     */
    public PlayerPane getPlayerPane()
    {
        if (_playerPane != null) return _playerPane;
        _playerPane = new PlayerPane();
        initGreenfoot();
        return _playerPane;
    }

    /**
     * Initialize Greenfoot.
     */
    private void initGreenfoot()
    {
        // Initialize speed from project
        int speed = getIntPropertyForKey("simulation.speed");
        setSpeed(speed > 0 ? speed : _speed);
    }

    /**
     * Returns a property for a given key.
     */
    protected String getPropertyForKey(String aKey)
    {
        GreenfootProject greenfootProject = getGreenfootProject(); if (greenfootProject == null) return null;
        return greenfootProject.getProperty(aKey);
    }

    /**
     * Returns an int property.
     */
    protected int getIntPropertyForKey(String aKey)
    {
        String propString = getPropertyForKey(aKey);
        return Convert.intValue(propString);
    }

    /**
     * Returns the greenfoot project.
     */
    public GreenfootProject getGreenfootProject()
    {
        if (_greenfootProject != null) return _greenfootProject;
        Class<?> worldClass = getWorldClass();
        return _greenfootProject = GreenfootProject.getGreenfootProjectForClass(worldClass);
    }

    /**
     * Returns the greenfoot project.
     */
    public void setGreenfootProject(GreenfootProject greenfootProject)
    {
        if (greenfootProject == _greenfootProject) return;
        firePropChange(GreenfootProject_Prop, _greenfootProject, _greenfootProject = greenfootProject);
    }

    /**
     * Returns the greenfoot image for class, if configured in project.
     */
    public GreenfootImage getGreenfootImageForClass(Class<?> aClass)
    {
        GreenfootProject greenfootProject = getGreenfootProject(); if (greenfootProject == null) return null;
        String imageKey = "class." + aClass.getSimpleName() + ".image";
        String imageName =  greenfootProject.getProperty(imageKey);
        return imageName != null ? new GreenfootImage(imageName) : null;
    }

    /**
     * Returns image for given file name.
     */
    public Image getImageForName(String aName)
    {
        Image image = _imageCache.get(aName);
        if (image != null)
            return image;

        // Get world class
        Class<?> worldClass = getWorldClass();

        // Get image for name
        image = Image.getImageForClassResource(worldClass, "images/" + aName);
        if (image == null)
            image = Image.getImageForClassResource(worldClass, aName);
        if (image == null) {
            System.err.println("Image not found: " + aName);
            image = Image.getImageForSize(100, 20, false);
        }

        // Wait for image load, since GF apps regularly use image info (or do image transform) immediately after loading
        if (!image.isLoaded())
            image.waitForImageLoad();

        // If image has non-standard DPI, resize to pixel width/height
        if (image.getWidth() != image.getPixWidth()) {
            int imageW = image.getPixWidth();
            int imageH = image.getPixHeight();
            image = image.cloneForSizeAndDpiScale(imageW, imageH, 1);
        }

        // Add to image cache
        _imageCache.put(aName, image);

        // Return
        return image;
    }

    /**
     * Returns a named sound.
     */
    public SoundClip getSoundClipForName(String aName)
    {
        SoundClip soundClip = _soundClipCache.get(aName);
        if (soundClip != null)
            return soundClip;

        Class<?> cls = getWorldClass();
        soundClip = SoundClip.get(cls, "sounds/" + aName);
        _soundClipCache.put(aName, soundClip);
        return soundClip;
    }

    /**
     * Show world for class.
     */
    protected void showWorldForClass(Class<? extends World> worldClass)
    {
        // Get world for class
        World world;
        try { world = worldClass.getConstructor().newInstance(); }
        catch (Exception e) { e.printStackTrace(); return; }
        setWorld(world);

        // Show PlayerPane window
        PlayerPane playerPane = getPlayerPane();
        playerPane.getWindow().setMaximized(SnapUtils.isWebVM);
        playerPane.setWindowVisible(true);
    }

    /**
     * Returns the current World class.
     */
    protected Class<?> getWorldClass()
    {
        World world = getWorld();
        if (world != null)
            return world.getClass();
        return _worldClass;
    }

    /**
     * Returns the delay in milliseconds for given greenfoot speed.
     */
    private static int convertSpeedToDelayMillis(int aSpeed)
    {
        // Make the speed into a delay
        long rawDelay = 100 - aSpeed;
        long min = 30 * 1000L; // Delay at MAX_SIMULATION_SPEED - 1
        long max = 10000 * 1000L * 1000L; // Delay at slowest speed
        double a = Math.pow(max / (double) min, 1D / (100 - 1));

        // Get timer period nanoseconds
        long timerPeriodNanos = 0;
        if (rawDelay > 0)
            timerPeriodNanos = (long) (Math.pow(a, rawDelay - 1) * min);

        // Convert nanoseconds to millis and return
        return (int) Math.round(timerPeriodNanos / 1000000d);
    }
}