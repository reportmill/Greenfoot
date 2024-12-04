package greenfoot;
import snap.view.*;

/**
 * Greenfoot class.
 * (<a href="https://www.greenfoot.org/files/javadoc/greenfoot/Greenfoot.html">Greenfoot</a>)
 */
public class Greenfoot {

    // The greenfoot environment
    protected static GreenfootEnv _env = new GreenfootEnv();

    /**
     * Returns a world.
     */
    public static World getWorld()  { return _env.getWorld(); }

    /**
     * Sets a world.
     */
    public static void setWorld(World aWorld)  { _env.setWorld(aWorld); }

    /**
     * Returns the speed.
     */
    public static int getSpeed()  { return _env.getSpeed(); }

    /**
     * Sets the speed of greenfoot playback.
     */
    public static void setSpeed(int aValue)  { _env.setSpeed(aValue); }

    /**
     * Starts greenfoot playing.
     */
    public static void start()  { _env.start(); }

    /**
     * Stops Greenfoot from playing.
     */
    public static void stop()  { _env.stop(); }

    /**
     * Delays the execution by given number of time steps.
     */
    public static void delay(int aValue)  { _env.delay(aValue); }

    /**
     * Plays a sound.
     */
    public static void playSound(String aName)  { _env.playSound(aName); }

    /**
     * Get the most recently pressed key, since the last time this method was called.
     */
    public static String getKey()  { return _env.getKey(); }

    /**
     * Returns whether key is down.
     */
    public static boolean isKeyDown(String aName)  { return _env.isKeyDown(aName); }

    /**
     * Returns the MouseInfo.
     */
    public static MouseInfo getMouseInfo()  { return _env.getMouseInfo(); }

    /**
     * Returns whether mouse was clicked on given actor/world.
     */
    public static boolean mouseClicked(Object anObj)  { return _env.mouseClicked(anObj); }

    /**
     * Returns whether mouse was pressed on given actor/world.
     */
    public static boolean mousePressed(Object anObj)  { return _env.mousePressed(anObj); }

    /**
     * Returns whether mouse was clicked on given actor/world.
     */
    public static boolean mouseMoved(Object anObj)  { return _env.mouseMoved(anObj); }

    /**
     * Asks the user a question.
     */
    public static String ask(String aPrompt)  { return _env.ask(aPrompt); }

    /**
     * Returns a random number.
     */
    public static int getRandomNumber(int aNum)  { return _env.getRandomNumber(aNum); }

    /**
     * Returns the current Greenfoot environment.
     */
    public static GreenfootEnv env()  { return _env; }

    /**
     * Show world for class.
     */
    public static void showWorldForClass(Class<? extends World> worldClass)
    {
        ViewUtils.runLater(() -> _env.showWorldForClass(worldClass));
    }
}