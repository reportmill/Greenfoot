package greenfoot;
import snap.gfx.Image;
import snap.gfx.SoundClip;
import java.util.HashMap;
import java.util.Map;

/**
 * Some Greenfoot utility methods.
 */
class Utils {

    // The loaded images
    private static Map<String, Image> _imageCache = new HashMap<>();

    // The loaded SoundClips
    private static Map<String, SoundClip> _soundClipCache = new HashMap<>();

    /**
     * Returns the greenfoot image for class, if configured in project.
     */
    public static GreenfootImage getGreenfootImageForClass(Class<?> aClass)
    {
        GreenfootProject greenfootProject = Greenfoot.getGreenfootProject(); if (greenfootProject == null) return null;
        String imageKey = "class." + aClass.getSimpleName() + ".image";
        String imageName =  greenfootProject.getProperty(imageKey);
        return imageName != null ? new GreenfootImage(imageName) : null;
    }

    /**
     * Returns image for given file name.
     */
    public static Image getImageForName(String aName)
    {
        Image image = _imageCache.get(aName);
        if (image != null)
            return image;

        // Get world class
        Class<?> worldClass = Greenfoot.getWorldClass();

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
    public static SoundClip getSoundClipForName(String aName)
    {
        SoundClip soundClip = _soundClipCache.get(aName);
        if (soundClip != null)
            return soundClip;

        Class<?> cls = Greenfoot.getWorldClass();
        soundClip = SoundClip.get(cls, "sounds/" + aName);
        _soundClipCache.put(aName, soundClip);
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
