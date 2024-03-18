package greenfoot;
import java.util.*;
import snap.geom.Point;
import snap.geom.Shape;
import snap.view.*;

/**
 * An implementation of the GreenFoot World class using SnapKit.
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
        // Set sizing info
        _width = aW;
        _height = aH;
        _cellSize = aCellSize;
        _bounded = isBounded;

        // Set world
        _worldView = new WorldView(this);
        _worldView.setSize(_width * _cellSize, _height * _cellSize);

        // If first world, manually set it
        if (Greenfoot.getWorld() == null) Greenfoot.setWorld(this);

        // Set background image
        String iname = Greenfoot.getProperty("class." + getClass().getSimpleName() + ".image");
        if (iname != null)
            setBackground(new GreenfootImage(iname));
        else setBackground(new GreenfootImage(aW * aCellSize, aH * aCellSize));
    }

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
    public <T extends Actor> List<T> getObjectsAt(int aX, int aY, Class<T> aClass)
    {
        return getActorsAt(aX, aY, aClass);
    }

    /**
     * Removes the objects of given class.
     */
    public void removeObjects(Collection theActors)
    {
        for (Object actor : theActors) removeObject((Actor) actor);
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
        System.err.println("World.setPaintOrder: Not Impl");
    }

    /**
     * Show some text centered at given position in world.
     */
    public void showText(String aStr, int x, int y)
    {
        String key = x + "x" + y;
        if (aStr.length() > 0) _text.put(key, aStr);
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
    protected <T> T getActorAt(double aX, double aY, Class<T> aClass)
    {
        View[] worldViews = _worldView.getChildren();

        for (View actorView : worldViews) {
            Actor actor = getActorForView(actorView);
            if (actor == null)
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
    protected <T extends Actor> List<T> getActorsAt(double aX, double aY, Class<T> aClass)
    {
        View[] worldViews = _worldView.getChildren();
        List<T> hitList = new ArrayList<>();

        for (View child : worldViews) {

            Actor actor = getActorForView(child);
            if (actor == null)
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
     * Returns on intersecting Actor.
     */
    protected Actor getActorAt(Shape aShape, Class aClass)
    {
        View[] worldViews = _worldView.getChildren();

        for (View actorView : worldViews) {

            Actor actor = getActorForView(actorView);
            if (actor == null)
                continue;

            if (aClass == null || aClass.isInstance(actor)) {
                Shape shp2 = actorView.parentToLocal(aShape);
                if (actorView.intersects(shp2))
                    return actor;
            }
        }

        return null;
    }

    /**
     * Returns on intersecting Actor.
     */
    protected <T extends Actor> List<T> getActorsAt(Shape aShape, Class<T> aClass)
    {
        View[] worldViews = _worldView.getChildren();
        List<T> hitList = new ArrayList<>();

        for (View actorView : worldViews) {

            Actor actor = getActorForView(actorView);
            if (actor == null)
                continue;

            if (aClass == null || aClass.isInstance(actor)) {
                Shape shp2 = actorView.parentToLocal(aShape);
                if (actorView.intersects(shp2))
                    hitList.add((T) actor);
            }
        }

        return hitList;
    }

    /**
     * This method is called by snap.node.ClassPage to return actual Node.
     */
    public WorldView getView()  { return _worldView; }

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