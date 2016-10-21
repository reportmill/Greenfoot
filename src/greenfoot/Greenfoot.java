package greenfoot;
import java.util.*;
import snap.gfx.SoundClip;
import snap.view.ParentView;
import snap.util.SnapUtils;
import snap.web.*;

/**
 * A custom class.
 */
public class Greenfoot extends ParentView {
    
    // The current world
    static World      _world;
    
    // The current speed
    static int        _speed = 50;
    
    // The last mouse x/y
    static double     _mouseX, _mouseY;
    
    // The mouse info
    static MouseInfo  _mouseInfo = new MouseInfo();

    // The project properties
    static Map <String,String>  _props;
    
    // The SoundClips
    static Map <String,SoundClip> _clips = new HashMap();
    
    // A Random
    static Random      _random = new Random();

/**
 * Get the most recently pressed key, since the last time this method was called.
 */
public static String getKey()  { System.err.println("Greenfoot.getKey: Not Impl"); return ""; }

/**
 * Returns a random number.
 */
public static int getRandomNumber(int aNum)  { return _random.nextInt(aNum); }

/**
 * Returns whether key is down.
 */
public static boolean isKeyDown(String aName)  { return getWorld().getView().isKeyDown(aName); }

/**
 * Plays a sound.
 */
public static void playSound(String aName)
{
    SoundClip snd = getSound(aName);
    if(snd!=null) snd.play();
}

/**
 * Returns a named sound.
 */
public static SoundClip getSound(String aName)
{
    SoundClip snd = _clips.get(aName); if(snd!=null) return snd;
    Class cls = getWorld().getClass();
    snd = SoundClip.get(cls, "sounds/" + aName);
    _clips.put(aName, snd);
    return snd;
}

/**
 * Initialize Greenfoot.
 */
static void initGreenfoot()
{
    int speed = getIntProperty("simulation.speed");
    setSpeed(speed>0? speed : _speed);
}

/**
 * Returns a world.
 */
public static World getWorld()  { return _world; }

/**
 * Sets a world.
 */
public static void setWorld(World aWorld)
{
    // Get old world - if there, stop it
    World oworld = _world; if(oworld!=null) oworld.getView().stop();
    
    // Set world
    _world = aWorld;
    _world.getView().setTimerPeriod(getTimerPeriod());
    
    // If no props, init greenfoot
    if(_props==null) initGreenfoot();
}

public static void start()  { if(_world!=null) getWorld().getView().start(); }

/**
 * Stops Greenfoot from playing.
 */
public static void stop()  { getWorld().getView().stop(); }

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
    getWorld()._wv.setTimerPeriod(period);
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
    long delayNS = 0; if (rawDelay > 0) delayNS = (long) (Math.pow(a, rawDelay - 1) * min);
    int delayMillis = (int)Math.round(delayNS/1000000d);
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
   if(anObj==null) return getWorld()!=null? getWorld().getView().isMouseClicked() : false;
   System.out.println("Mouse Clicked not supported"); return false;
}
    
/**
 * Returns the project properties map.
 */
static Map <String,String> getProps2()  { return _props!=null? _props : (_props=createProps()); }

/**
 * Returns the project properties map.
 */
static Map <String,String> createProps()
{
    // Create map
    Map props = new HashMap();
    
    // Get project file
    //WebSite site = _world._scn.getSite();
    WebURL url = WebURL.getURL(_world.getClass(), "/project.greenfoot");
    WebFile file = url.getFile();//site.getFile("/project.greenfoot"); if(file==null) return props;
        
    // Get file text
    String text = file.getText();
    if(text!=null && text.length()>0) {
        String lines[] = text.split("\\n");
        for(String line : lines) {
            String parts[] = line.split("=");
            if(parts.length>1)
                props.put(parts[0], parts[1]);
        }
    }
    
    // Return properties
    return props;
}

/**
 * Returns a property for a given key.
 */
static String getProperty(String aKey)  { return getProps2().get(aKey); }

/**
 * Returns an int property.
 */
static int getIntProperty(String aKey)  { return SnapUtils.intValue(getProperty(aKey)); }

}