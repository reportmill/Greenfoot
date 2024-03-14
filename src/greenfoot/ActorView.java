package greenfoot;
import snap.view.ImageView;

/**
 * The Greenfoot SnapActor.
 */
public class ActorView extends ImageView {

    // The Greenfoot actor
    Actor _actor;

    /**
     * Creates a new SnapActor.
     */
    public ActorView(Actor anActor)
    {
        _actor = anActor;
    }

    /**
     * Override to send to Greenfoot Actor.
     */
    public void act()
    {
        _actor.act();
    }
}
