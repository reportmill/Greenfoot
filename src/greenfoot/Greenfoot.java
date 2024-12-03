package greenfoot;
import java.util.*;
import snap.geom.Point;
import snap.gfx.*;
import snap.util.Convert;
import snap.util.SnapUtils;
import snap.view.*;
import snap.viewx.DialogBox;

/**
 * Greenfoot class.
 * (<a href="https://www.greenfoot.org/files/javadoc/greenfoot/Greenfoot.html">Greenfoot</a>)
 */
public class Greenfoot {

    // The current speed
    private static int _speed = 50;

    // The last mouse x/y
    protected static double _mouseX, _mouseY;

    // The mouse info
    private static MouseInfo _mouseInfo = new MouseInfo();

    // A Random
    private static Random _random = new Random();

    // The greenfoot project
    private static GreenfootProject _greenfootProject;

    // The main player pane for the app (generally there is only one)
    private static PlayerPane _playerPane;

    // The world class from the current project
    protected static Class<?> _worldClass;

    /**
     * Returns a world.
     */
    public static World getWorld()
    {
        PlayerPane playerPane = getPlayerPane();
        return playerPane.getWorld();
    }

    /**
     * Sets a world.
     */
    public static void setWorld(World aWorld)
    {
        PlayerPane playerPane = getPlayerPane();
        playerPane.setWorld(aWorld);
    }

    /**
     * Returns the speed.
     */
    public static int getSpeed()  { return _speed; }

    /**
     * Sets the speed of greenfoot playback.
     */
    public static void setSpeed(int aValue)
    {
        _speed = aValue;

        // Set player pane timer delay for speed
        int timerPeriodMillis = Utils.convertSpeedToDelayMillis(_speed);
        PlayerPane playerPane = getPlayerPane();
        playerPane.setTimerPeriod(timerPeriodMillis);
    }

    /**
     * Starts greenfoot playing.
     */
    public static void start()
    {
        PlayerPane playerPane = getPlayerPane();
        playerPane.start();
    }

    /**
     * Stops Greenfoot from playing.
     */
    public static void stop()
    {
        PlayerPane playerPane = getPlayerPane();
        playerPane.stop();
    }

    /**
     * Delays the execution by given number of time steps.
     */
    public static void delay(int aValue)
    {
        System.out.println("Greenfoot.delay(): Not implemented yet");
    }

    /**
     * Plays a sound.
     */
    public static void playSound(String aName)
    {
        SoundClip soundClip = Utils.getSoundClipForName(aName);
        if (soundClip != null)
            soundClip.play();
    }

    /**
     * Get the most recently pressed key, since the last time this method was called.
     */
    public static String getKey()
    {
        System.err.println("Greenfoot.getKey: Not Impl");
        return "";
    }

    /**
     * Returns whether key is down.
     */
    public static boolean isKeyDown(String aName)
    {
        return getWorld().getWorldView().isKeyDown(aName);
    }

    /**
     * Returns the MouseInfo.
     */
    public static MouseInfo getMouseInfo()  { return _mouseInfo; }

    /**
     * Returns whether mouse was clicked on given actor/world.
     */
    public static boolean mouseClicked(Object anObj)
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
    public static boolean mousePressed(Object anObj)
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
    public static boolean mouseMoved(Object anObj)
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
    public static String ask(String aPrompt)
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
    public static int getRandomNumber(int aNum)  { return _random.nextInt(aNum); }

    /**
     * Returns the greenfoot image for class, if configured in project.
     */
    protected static GreenfootImage getGreenfootImageForClass(Class<?> aClass)
    {
        String imageName = getPropertyForKey("class." + aClass.getSimpleName() + ".image");
        return imageName != null ? new GreenfootImage(imageName) : null;
    }

    /**
     * Returns the greenfoot PlayerPane.
     */
    public static PlayerPane getPlayerPane()
    {
        if (_playerPane != null) return _playerPane;
        _playerPane = new PlayerPane();
        initGreenfoot();
        return _playerPane;
    }

    /**
     * Initialize Greenfoot.
     */
    private static void initGreenfoot()
    {
        // Initialize speed from project
        int speed = getIntPropertyForKey("simulation.speed");
        setSpeed(speed > 0 ? speed : _speed);
    }

    /**
     * Returns a property for a given key.
     */
    protected static String getPropertyForKey(String aKey)
    {
        GreenfootProject greenfootProject = getGreenfootProject(); if (greenfootProject == null) return null;
        return greenfootProject.getProperty(aKey);
    }

    /**
     * Returns an int property.
     */
    protected static int getIntPropertyForKey(String aKey)
    {
        String propString = getPropertyForKey(aKey);
        return Convert.intValue(propString);
    }

    /**
     * Returns the greenfoot project.
     */
    private static GreenfootProject getGreenfootProject()
    {
        if (_greenfootProject != null) return _greenfootProject;
        Class<?> worldClass = getWorldClass();
        return _greenfootProject = GreenfootProject.getGreenfootProjectForClass(worldClass);
    }

    /**
     * Show world for class.
     */
    public static void showWorldForClass(Class<? extends World> worldClass)
    {
        ViewUtils.runLater(() -> showWorldForClassImpl(worldClass));
    }

    /**
     * Show world for class.
     */
    private static void showWorldForClassImpl(Class<? extends World> worldClass)
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
    protected static Class<?> getWorldClass()
    {
        World world = getWorld();
        if (world != null)
            return world.getClass();
        return _worldClass;
    }
}