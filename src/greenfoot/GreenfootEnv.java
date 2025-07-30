package greenfoot;
import snap.geom.Point;
import snap.gfx.Image;
import snap.gfx.SoundClip;
import snap.props.PropChange;
import snap.props.PropChangeListener;
import snap.props.PropObject;
import snap.util.Convert;
import snap.util.SnapEnv;
import snap.view.View;
import snap.view.ViewTimer;
import snap.viewx.DialogBox;
import snap.web.WebFile;
import snap.web.WebURL;
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

    // The greenfoot project
    private GreenfootProject _greenfootProject;

    // The animation timer
    private ViewTimer _timer;

    // The loaded images
    private Map<String, Image> _imageCache = new HashMap<>();

    // The loaded SoundClips
    private Map<String, SoundClip> _soundClipCache = new HashMap<>();

    // A prop change listener for greenfoot project prop changes
    private PropChangeListener _greenfootProjectLsnr = this::handleGreenfootProjectPropChange;

    // The world class from the current project
    protected static Class<? extends World> _worldClass;

    // A Random
    private static Random _random = new Random();

    // Constants for properties
    public static final String GreenfootProject_Prop = "GreenfootProject";

    // A shared image stand-in for when getImageForName() can't find image
    private static Image MISSING_IMAGE = Image.getImageForSize(100, 20, false);

    /**
     * Constructor.
     */
    public GreenfootEnv()
    {
        _timer = new ViewTimer(this::act, 40);
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
     * Sets the world for given class name.
     */
    public void setWorldForClass(Class<? extends World> worldClass)
    {
        World world;
        try { world = worldClass.getConstructor().newInstance(); }
        catch (Exception e) { e.printStackTrace(); return; }
        setWorld(world);
    }

    /**
     * Returns the world view.
     */
    private WorldView getWorldView()
    {
        World world = getWorld();
        return world != null ? world.getWorldView() : null;
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

        // Set timer delay for speed
        int timerPeriodMillis = GreenfootEnv.convertSpeedToDelayMillis(_speed);
        _timer.setPeriod(timerPeriodMillis);
    }

    /**
     * Returns whether game is running.
     */
    public boolean isPlaying()  { return _timer.isRunning(); }

    /**
     * Starts greenfoot playing.
     */
    public void start()  { _timer.start(); }

    /**
     * Stops Greenfoot from playing.
     */
    public void stop()  { _timer.stop(); }

    /**
     * Plays one frame of game.
     */
    protected void act()
    {
        WorldView worldView = getWorldView(); if (worldView == null) return;
        try { worldView.doAct(); }
        catch (Exception e) { handleException(e); }
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
        WorldView worldView = getWorldView();
        return worldView != null && worldView.isKeyDown(aName);
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
        WorldView worldView = getWorldView();
        if (worldView == null)
            return false;

        if (anObj == null)
            return worldView.isMouseClicked();
        if (anObj instanceof World)
            return anObj == world && worldView.isMouseClicked();
        System.out.println("Mouse Clicked not supported");
        return false;
    }

    /**
     * Returns whether mouse was pressed on given actor/world.
     */
    public boolean mousePressed(Object anObj)
    {
        World world = getWorld();
        WorldView worldView = getWorldView();

        if (anObj == null)
            return worldView != null && worldView.isMouseDown();
        if (anObj instanceof World)
            return anObj == world && worldView != null && worldView.isMouseDown();
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
        WorldView worldView = getWorldView();
        if (worldView == null)
            return false;
        Point pnt = view.parentToLocal(worldView._mx, worldView._my);
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
        _playerPane = new PlayerPane(this);
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
     * Returns a resource url for given name.
     */
    public WebURL getResourceForName(String aName)
    {
        Class<?> worldClass = getWorldClass();
        if (worldClass == null)
            return null;
        return WebURL.getResourceUrl(worldClass, aName);
    }

    /**
     * Returns the greenfoot project.
     */
    public GreenfootProject getGreenfootProject()
    {
        if (_greenfootProject != null) return _greenfootProject;

        // Get project file url for given world class
        WebURL projectUrl = getResourceForName("project.greenfoot");
        WebFile projectFile = projectUrl != null ? projectUrl.getFile() : null;
        if (projectFile == null) {
            System.err.println("GreenfootEnv: Couldn't find Greenfoot project file");
            return null;
        }

        // Return
        return _greenfootProject = new GreenfootProject(projectFile);
    }

    /**
     * Returns the greenfoot project.
     */
    public void setGreenfootProject(GreenfootProject greenfootProject)
    {
        if (greenfootProject == _greenfootProject) return;

        if (_greenfootProject != null)
            _greenfootProject.removePropChangeListener(_greenfootProjectLsnr);
        firePropChange(GreenfootProject_Prop, _greenfootProject, _greenfootProject = greenfootProject);
        if (_greenfootProject != null)
            _greenfootProject.addPropChangeListener(_greenfootProjectLsnr);
    }

    /**
     * Returns the greenfoot image for class, if configured in project.
     */
    public GreenfootImage getGreenfootImageForClass(Class<?> aClass)
    {
        GreenfootProject greenfootProject = getGreenfootProject(); if (greenfootProject == null) return null;
        String imageName =  greenfootProject.getImageNameForClass(aClass);
        return imageName != null ? new GreenfootImage(imageName) : null;
    }

    /**
     * Returns the image for given class.
     */
    public Image getImageForClass(Class<?> aClass)
    {
        GreenfootProject greenfootProject = getGreenfootProject(); if (greenfootProject == null) return null;
        String imageName =  greenfootProject.getImageNameForClass(aClass);
        return imageName != null ? getImageForName(imageName) : null;
    }

    /**
     * Returns image for given file name.
     */
    public Image getImageForName(String aName)
    {
        Image image = _imageCache.get(aName);
        if (image != null)
            return image;

        // Get image url for name
        WebURL imageUrl = getResourceForName("images/" + aName);
        if (imageUrl == null)
            imageUrl = getResourceForName(aName);
        if (imageUrl == null) {
            System.err.println("GreenfootEnv.getImageForName: Image not found: " + aName);
            return MISSING_IMAGE;
        }

        // Get image
        image = Image.getImageForSource(imageUrl);

        // Wait for image load, since GF apps regularly use image info (or do image transform) immediately after loading
        if (!image.isLoaded())
            image.waitForImageLoad();

        // If image has non-standard DPI, resize to pixel width/height
        if (image.getWidth() != image.getPixWidth()) {
            int imageW = image.getPixWidth();
            int imageH = image.getPixHeight();
            image = image.copyForSizeAndDpiScale(imageW, imageH, 1);
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

        // Get Sound URL and SoundClip
        WebURL soundUrl = getResourceForName("sounds/" + aName);
        if (soundUrl != null)
            soundClip = SoundClip.get(soundUrl);

        // Set and return
        _soundClipCache.put(aName, soundClip);
        return soundClip;
    }

    /**
     * Show world for class.
     */
    protected void showWorldForClass(Class<? extends World> worldClass)
    {
        // Set world for class
        setWorldForClass(worldClass);

        // Show PlayerPane window
        PlayerPane playerPane = getPlayerPane();
        playerPane.getWindow().setMaximized(SnapEnv.isWebVM);
        playerPane.setWindowVisible(true);
    }

    /**
     * Returns the current World class.
     */
    protected Class<? extends World> getWorldClass()
    {
        // If world current set, return that class
        World world = getWorld();
        if (world != null) {

            // Get latest version of class
            Class<? extends World> worldClass = world.getClass();
            GreenfootProject greenfootProject = getGreenfootProject();
            Class<? extends World> worldClass2 = (Class<? extends World>) greenfootProject.getClassForName(worldClass.getName());
            if (worldClass2 != null)
                return worldClass2;
            return worldClass;
        }

        // Return
        return _worldClass;
    }

    /**
     * Returns the default World class.
     */
    protected Class<? extends World> getDefaultWorldClass()
    {
        // If last instantiated available, use that
        GreenfootProject greenfootProject = getGreenfootProject();
        Class<? extends World> lastInstantiatedWorldClass = greenfootProject.getLastInstantiatedWorldClass();
        if (lastInstantiatedWorldClass != null)
            return lastInstantiatedWorldClass;

        // Return
        return _worldClass;
    }

    /**
     * Resets the world.
     */
    public void resetWorld()
    {
        stop();
        Class<? extends World> worldClass = getWorldClass();
        if (worldClass != null)
            setWorldForClass(worldClass);
        else resetWorldToDefault();
    }

    /**
     * Resets the world to default world class.
     */
    public void resetWorldToDefault()
    {
        stop();
        Class<? extends World> worldClass = getDefaultWorldClass();
        if (worldClass != null)
            setWorldForClass(worldClass);
    }

    /**
     * Called when GreenfootProject has prop change.
     */
    private void handleGreenfootProjectPropChange(PropChange propChange)
    {
        if (_playerPane != null)
            _playerPane.handleGreenfootProjectRootClassNodeChange(propChange);
    }

    /**
     * Called when Greenfoot gets exception.
     */
    public void handleException(Exception e)
    {
        stop();
        e.printStackTrace();
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
