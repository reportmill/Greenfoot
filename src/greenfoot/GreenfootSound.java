package greenfoot;
import snap.gfx.SoundClip;

/**
 * An implementation of the GreenFootSound class using SnapKit.
 * (<a href="https://www.greenfoot.org/files/javadoc/greenfoot/GreenfootSound.html">GreenfootSound</a>)
 */
public class GreenfootSound {

    // The SnapSound
    private SoundClip _sound;

    /**
     * Creates a new sound.
     */
    public GreenfootSound(String aName)
    {
        // Get world class
        Class<?> worldClass = Greenfoot.getWorldClass();

        // Get sound clip for name
        _sound = SoundClip.get(worldClass, "sounds/" + aName);
        if (_sound == null)
            System.err.println("GreenfootSound: Sound not found for name: " + aName);
    }

    /**
     * Returns whether greenfoot is playing.
     */
    public boolean isPlaying()  { return _sound != null && _sound.isPlaying(); }

    /**
     * Plays the sound.
     */
    public void play()
    {
        if (_sound != null)
            _sound.play();
    }

    /**
     * Plays the sound repetedly in a loop.
     */
    public void playLoop()
    {
        if (_sound != null)
            _sound.play(999);
    }

    /**
     * Tells Greenfoot to stop.
     */
    public void stop()
    {
        if (_sound != null)
            _sound.stop();
    }

    /**
     * Pauses a sound.
     */
    public void pause()
    {
        if (_sound != null)
            _sound.stop();
    }

    /**
     * Sets the volume of the sound.
     */
    public void setVolume(int aValue)  { }
}