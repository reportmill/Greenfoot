package greenfoot;
import snap.view.ImageView;

/**
 * The Greenfoot SnapActor.
 */
public class ActorView extends ImageView {

    // The Greenfoot actor
    protected Actor _actor;

    /**
     * Creates a new SnapActor.
     */
    public ActorView(Actor anActor)
    {
        _actor = anActor;
    }

    /**
     * Returns the actor.
     */
    public Actor getActor()  { return _actor; }

    /**
     * Override to send to Greenfoot Actor.
     */
    public void act()
    {
        _actor.act();
    }
}
