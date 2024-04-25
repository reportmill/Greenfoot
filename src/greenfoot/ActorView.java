package greenfoot;
import snap.view.ImageView;

/**
 * The Greenfoot Actor.
 */
public class ActorView extends ImageView {

    // The Greenfoot actor
    protected Actor _actor;

    /**
     * Constructor.
     */
    public ActorView(Actor anActor)
    {
        _actor = anActor;
    }

    /**
     * Returns the actor.
     */
    public Actor getActor()  { return _actor; }
}
