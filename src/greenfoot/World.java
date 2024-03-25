package greenfoot;
import java.util.*;
import snap.geom.Point;
import snap.geom.Shape;
import snap.util.SnapUtils;
import snap.view.*;

/**
 * An implementation of the GreenFoot World class using SnapKit.
 * (<a href="https://www.greenfoot.org/files/javadoc/greenfoot/World.html">World</a>)
 */
public class World {

    // The Width/Height/CellSize
    private int _width, _height, _cellSize = 1;

    // Whether world is bounded
    private boolean _bounded;

    // The background image
    private GreenfootImage _backImg;

    // Text to be shown in world
    protected Map<String, String> _text = new HashMap<>();

    // The WorldView
    protected WorldView _worldView;

    /**
     * Constructor.
     */
    public World(int aW, int aH, int aCellSize)
    {
        this(aW, aH, aCellSize, true);
    }

    /**
     * Constructor.
     */
    public World(int aW, int aH, int aCellSize, boolean isBounded)
    {
        // Set any project class
        Greenfoot._worldClass = getClass();

        // Set sizing info
        _width = aW;
        _height = aH;
        _cellSize = aCellSize;
        _bounded = isBounded;

        // Set world
        _worldView = new WorldView(this);
        _worldView.setSize(_width * _cellSize, _height * _cellSize);

        // If first world, manually set it
        if (Greenfoot.getWorld() == null)
            Greenfoot.setWorld(this);

        // Set background image
        String imageName = Greenfoot.getProperty("class." + getClass().getSimpleName() + ".image");
        if (imageName != null)
            setBackground(new GreenfootImage(imageName));
        else setBackground(new GreenfootImage(aW * aCellSize, aH * aCellSize));
    }

    /**
     * Returns the world view.
     */
    public WorldView getWorldView()  { return _worldView; }

    /**
     * Act method for world.
     */
    public void act()  { }

    /**
     * Returns the width.
     */
    public int getWidth()  { return _width; }

    /**
     * Returns the height.
     */
    public int getHeight()  { return _height; }

    /**
     * Returns the cell size.
     */
    public int getCellSize()  { return _cellSize; }

    /**
     * Returns whether world is bounded.
     */
    public boolean isBounded()  { return _bounded; }

    /**
     * Returns the number of objects currently in world.
     */
    public int numberOfObjects()
    {
        int c = 0;
        for (View n : _worldView.getChildren())
            if (getActorForView(n) != null) c++;
        return c;
    }

    /**
     * Adds an object.
     */
    public void addObject(Actor anActor, int anX, int aY)
    {
        _worldView.addChild(anActor._actorView);
        anActor._world = this;
        anActor.setLocation(anX, aY);
        anActor.addedToWorld(this);
    }

    /**
     * Removes an Actor.
     */
    public void removeObject(Actor anActor)
    {
        _worldView.removeChild(anActor._actorView);
    }

    /**
     * Returns the actors of a given class.
     */
    public <T> List<T> getObjects(Class<T> aClass)
    {
        List<T> list = new ArrayList<>();
        for (View child : _worldView.getChildren()) {
            Actor actor = getActorForView(child);
            if (actor == null)
                continue;
            if (aClass == null || aClass.isInstance(actor))
                list.add((T) actor);
        }
        return list;
    }

    /**
     * Returns the objects at given point.
     */
    public <T> List<T> getObjectsAt(int aX, int aY, Class<T> aClass)
    {
        return getActorsAt(null, aX, aY, aClass);
    }

    /**
     * Removes the objects of given class.
     */
    public void removeObjects(Collection<? extends Actor> theActors)
    {
        theActors.forEach(this::removeObject);
    }

    /**
     * Returns the background image.
     */
    public GreenfootImage getBackground()
    {
        if (_backImg != null) return _backImg;
        _backImg = new GreenfootImage(getWidth() * getCellSize(), getHeight() * getCellSize());
        return _backImg;
    }

    /**
     * Sets the greenfoot image.
     */
    public void setBackground(GreenfootImage anImage)
    {
        if (_backImg != null) _backImg._world = null;
        _backImg = anImage;
        if (_backImg != null) _backImg._world = this;
    }

    /**
     * Sets the image to named image.
     */
    public void setBackground(String aName)
    {
        setBackground(new GreenfootImage(aName));
    }

    /**
     * Returns the color at center of cell.
     */
    public java.awt.Color getColor()
    {
        System.err.println("World.getColor: Not Impl");
        return null;
    }

    /**
     * Repaint the world.
     */
    public void repaint()
    {
        _worldView.repaint();
    }

    /**
     * Sets the act order.
     */
    public void setActOrder(Class<?>... theClasses)
    {
        System.err.println("World.setActOrder: Not Impl");
    }

    /**
     * Sets the paint order.
     */
    public void setPaintOrder(Class<?>... theClasses)
    {
        _worldView.setPaintOrder(theClasses);
    }

    /**
     * Show some text centered at given position in world.
     */
    public void showText(String aString, int textX, int textY)
    {
        String key = textX + "x" + textY;
        if (aString != null && aString.length() > 0)
            _text.put(key, aString);
        else _text.remove(key);
    }

    /**
     * This method is called by the greenfoot system.
     */
    public void started()  { }

    /**
     * This method is called when execution has stopped.
     */
    public void stopped()  { }

    /**
     * Returns the objects at given point.
     */
    protected <T> T getActorAt(Actor anActor, double aX, double aY, Class<T> aClass)
    {
        View[] actorViews = _worldView.getChildren();

        for (View actorView : actorViews) {
            Actor actor = getActorForView(actorView);
            if (actor == null || actor == anActor)
                continue;
            if (aClass == null || aClass.isInstance(actor)) {
                Point point = actorView.parentToLocal(aX, aY);
                if (actorView.contains(point.getX(), point.getY()))
                    return (T) actor;
            }
        }

        return null;
    }

    /**
     * Returns the objects at given point.
     */
    protected <T> List<T> getActorsAt(Actor anActor, double aX, double aY, Class<T> aClass)
    {
        View[] worldViews = _worldView.getChildren();
        List<T> hitList = new ArrayList<>();

        for (View child : worldViews) {

            Actor actor = getActorForView(child);
            if (actor == null || actor == anActor)
                continue;

            if (aClass == null || aClass.isInstance(actor)) {
                Point point = child.parentToLocal(aX, aY);
                if (child.contains(point.getX(), point.getY()))
                    hitList.add((T) actor);
            }
        }

        return hitList;
    }

    /**
     * Returns the first actor intersecting given Actor and matching given class (optional).
     */
    protected Actor getIntersectingActorForActorAndClass(Actor anActor, Class<?> aClass)
    {
        // Get actor bounds in world coords and all actor views
        Shape actorBoundsInWorld = anActor.getBoundsInWorld();
        View[] actorViews = _worldView.getChildren();

        // Iterate over all actor views and if any is intersecting and instance of given class, return actor
        for (View otherView : actorViews) {
            if (isIntersectingActorViewForActorShapeAndClass(otherView, anActor, actorBoundsInWorld, aClass))
                return getActorForView(otherView);
        }

        // Return not found
        return null;
    }

    /**
     * Returns all actors intersecting given Actor and matching given class (optional).
     */
    protected <T> List<T> getIntersectingActorsForActorShapeAndClass(Actor anActor, Shape aShape, Class<T> aClass)
    {
        // Get actor view, actor bounds in world coords and all actor views
        Shape actorBoundsInWorld = aShape != null ? aShape : anActor.getBoundsInWorld();
        View[] actorViews = _worldView.getChildren();
        List<T> intersectingActors = new ArrayList<>();

        // Iterate over all actor views and if any is intersecting and instance of given class, add actor to list
        for (View otherView : actorViews) {
            if (isIntersectingActorViewForActorShapeAndClass(otherView, anActor, actorBoundsInWorld, aClass))
                intersectingActors.add((T) getActorForView(otherView));
        }

        // Return
        return intersectingActors;
    }

    /**
     * Returns whether given actor view intersects given shape and is instance of given class (optional).
     */
    private boolean isIntersectingActorViewForActorShapeAndClass(View actorView, Actor anActor, Shape aShape, Class<?> aClass)
    {
        // If no actor or is given actor, return false
        Actor actor = getActorForView(actorView);
        if (actor == null || actor == anActor)
            return false;

        // If class is specified and actor isn't class, return false
        if (aClass != null && !aClass.isInstance(actor))
            return false;

        // Return whether actor bounds in world coords intersects shape
        Shape actorBoundsInWorld = actor.getBoundsInWorld();
        return actorBoundsInWorld.intersectsShape(aShape);
    }

    /**
     * Sets the window visible.
     */
    public void setWindowVisible(boolean aValue)
    {
        // If not on event thread, call again on event thread
        if (!ViewUtils.isEventThread()) {
            ViewUtils.runLater(() -> setWindowVisible(aValue)); return; }

        GreenfootOwner owner = GreenfootOwner.getShared();
        owner.setWorld(this);
        owner.getWindow().setMaximized(SnapUtils.isWebVM);
        owner.setWindowVisible(true);
    }

    /**
     * Return actor for given view.
     */
    private static Actor getActorForView(View aView)
    {
        ActorView gfsa = aView instanceof ActorView ? (ActorView) aView : null;
        return gfsa != null ? gfsa._actor : null;
    }
}