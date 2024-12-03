package greenfoot;
import snap.gfx.SoundClip;
import java.util.HashMap;
import java.util.Map;

/**
 * Some Greenfoot utility methods.
 */
class Utils {

    // The SoundClips
    private static Map<String, SoundClip> _clips = new HashMap<>();

    /**
     * Returns a named sound.
     */
    public static SoundClip getSoundClipForName(String aName)
    {
        SoundClip soundClip = _clips.get(aName);
        if (soundClip != null)
            return soundClip;

        Class<?> cls = Greenfoot.getWorldClass();
        soundClip = SoundClip.get(cls, "sounds/" + aName);
        _clips.put(aName, soundClip);
        return soundClip;
    }

    /**
     * Returns the delay in milliseconds for given greenfoot speed.
     */
    public static int convertSpeedToDelayMillis(int aSpeed)
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
