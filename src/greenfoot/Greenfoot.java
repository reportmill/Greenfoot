package greenfoot;
import java.util.*;
import snap.geom.Point;
import snap.gfx.*;
import snap.util.Convert;
import snap.view.*;
import snap.viewx.DialogBox;
import snap.web.*;

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

    // The SoundClips
    private static Map<String, SoundClip> _clips = new HashMap<>();

    // A Random
    private static Random _random = new Random();

    // The world class from the current project
    protected static Class<?> _worldClass;

    /**
     * Get the most recently pressed key, since the last time this method was called.
     */
    public static String getKey()
    {
        System.err.println("Greenfoot.getKey: Not Impl");
        return "";
    }

    /**
     * Returns a random number.
     */
    public static int getRandomNumber(int aNum)
    {
        return _random.nextInt(aNum);
    }

    /**
     * Returns whether key is down.
     */
    public static boolean isKeyDown(String aName)
    {
        return getWorld().getWorldView().isKeyDown(aName);
    }

    /**
     * Plays a sound.
     */
    public static void playSound(String aName)
    {
        SoundClip soundClip = getSound(aName);
        if (soundClip != null)
            soundClip.play();
    }

    /**
     * Returns a named sound.
     */
    public static SoundClip getSound(String aName)
    {
        SoundClip soundClip = _clips.get(aName);
        if (soundClip != null)
            return soundClip;

        Class<?> cls = getWorld().getClass();
        soundClip = SoundClip.get(cls, "sounds/" + aName);
        _clips.put(aName, soundClip);
        return soundClip;
    }

    /**
     * Initialize Greenfoot.
     */
    static void initGreenfoot()
    {
        int speed = getIntProperty("simulation.speed");
        setSpeed(speed > 0 ? speed : _speed);
    }

    /**
     * Returns the GreenfootOwner.
     */
    public static GreenfootOwner getWorldOwner()  { return GreenfootOwner.getShared(); }

    /**
     * Returns a world.
     */
    public static World getWorld()  { return getWorldOwner().getWorld(); }

    /**
     * Sets a world.
     */
    public static void setWorld(World aWorld)
    {
        World oldWorld = getWorld();
        getWorldOwner().setWorld(aWorld);
        if (oldWorld == null)
            initGreenfoot();
    }

    public static void start()  { getWorldOwner().start(); }

    /**
     * Stops Greenfoot from playing.
     */
    public static void stop()  { getWorldOwner().stop(); }

    /**
     * Delays the execution by given number of time steps.
     */
    public static void delay(int aValue)  { }

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
        int period = getTimerPeriod();
        getWorldOwner().setTimerPeriod(period);
    }

    /**
     * Returns the delay as a function of the speed.
     */
    static int getTimerPeriod()
    {
        // Make the speed into a delay
        long rawDelay = 100 - _speed;
        long min = 30 * 1000L; // Delay at MAX_SIMULATION_SPEED - 1
        long max = 10000 * 1000L * 1000L; // Delay at slowest speed
        double a = Math.pow(max / (double) min, 1D / (100 - 1));
        long delayNS = 0;
        if (rawDelay > 0)
            delayNS = (long) (Math.pow(a, rawDelay - 1) * min);
        int delayMillis = (int) Math.round(delayNS / 1000000d);
        return delayMillis;
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
        String output = DialogBox.showInputDialog(getWorldOwner().getUI(), title, aPrompt, "");
        start();
        return output;
    }

    /**
     * Returns the greenfoot image for class, if configured in project.
     */
    protected static GreenfootImage getGreenfootImageForClass(Class<?> aClass)
    {
        String imageName = Greenfoot.getProperty("class." + aClass.getSimpleName() + ".image");
        return imageName != null ? new GreenfootImage(imageName) : null;
    }

    /**
     * Returns a property for a given key.
     */
    protected static String getProperty(String aKey)
    {
        GreenfootProject greenfootProject = getGreenfootProject();
        return greenfootProject != null ? greenfootProject.getProperty(aKey) : null;
    }

    /**
     * Returns an int property.
     */
    protected static int getIntProperty(String aKey)
    {
        String propString = getProperty(aKey);
        return Convert.intValue(propString);
    }

    /**
     * Returns the greenfoot project.
     */
    private static GreenfootProject getGreenfootProject()
    {
        // Get URL for World class
        World world = getWorld();
        Class<?> worldClass = world.getClass();
        String worldClassFilename = worldClass.getSimpleName() + ".class";
        WebURL worldClassUrl = WebURL.getURL(worldClass, worldClassFilename);
        if (worldClassUrl == null) {
            System.err.println("Couldn't find Greenfoot project file");
            return null;
        }

        // Get site root dir for world class
        WebSite worldClassSite = worldClassUrl.getSite();
        WebFile worldClassSiteDir = worldClassSite.getRootDir();

        // Return
        return GreenfootProject.getGreenfootProjectForDir(worldClassSiteDir);
    }

    /**
     * Show world for class.
     */
    public static void showWorldForClass(Class<? extends World> worldClass)
    {
        ViewUtils.runLater(() -> {
            try {
                World world = worldClass.getConstructor().newInstance();
                world.setWindowVisible(true);
            }
            catch (Exception e) { e.printStackTrace(); }
        });
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