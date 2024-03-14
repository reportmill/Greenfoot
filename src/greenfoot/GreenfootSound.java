package greenfoot;
import snap.gfx.SoundClip;

/**
 * A custom class.
 */
public class GreenfootSound extends Object {

    // The SnapSound
    private SoundClip _sound;

    /**
     * Creates a new sound.
     */
    public GreenfootSound(String aName)
    {
        _sound = SoundClip.get(Greenfoot.getWorld().getClass(), "sounds/" + aName);
        if (_sound == null) System.err.println("Sound not found: " + aName);
    }

    /**
     * Returns whether greenfoot is playing.
     */
    public boolean isPlaying()  { return _sound.isPlaying(); }

    /**
     * Plays the sound.
     */
    public void play()  { _sound.play(); }

    /**
     * Plays the sound repetedly in a loop.
     */
    public void playLoop()  { _sound.play(999); }

    /**
     * Tells Greenfoot to stop.
     */
    public void stop()  { _sound.stop(); }

    /**
     * Pauses a sound.
     */
    public void pause()  { _sound.stop(); }

    /**
     * Sets the volume of the sound.
     */
    public void setVolume(int aValue)  { }
}