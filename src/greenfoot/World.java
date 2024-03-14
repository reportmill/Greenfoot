package greenfoot;
import java.util.*;
import snap.geom.Point;
import snap.geom.Shape;
import snap.view.*;
import greenfoot.Actor.GFSnapActor;

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
    protected WorldView _wv;

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
        _wv = new WorldView(this);
        _wv.setSize(_width * _cellSize, _height * _cellSize);

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
        for (View n : _wv.getChildren()) if (gfa(n) != null) c++;
        return c;
    }

    /**
     * Adds an object.
     */
    public void addObject(Actor anActor, int anX, int aY)
    {
        _wv.addChild(anActor._sa);
        anActor._world = this;
        anActor.setLocation(anX, aY);
        anActor.addedToWorld(this);
    }

    /**
     * Removes an Actor.
     */
    public void removeObject(Actor anActor)
    {
        _wv.removeChild(anActor._sa);
    }

    /**
     * Returns the actors of a given class.
     */
    public List getObjects(Class aClass)
    {
        List list = new ArrayList();
        for (View child : _wv.getChildren()) {
            Actor gfa = gfa(child);
            if (gfa == null) continue;
            if (aClass == null || aClass.isInstance(gfa))
                list.add(gfa);
        }
        return list;
    }

    /**
     * Returns the objects at given point.
     */
    public List getObjectsAt(int aX, int aY, Class aClass)
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
        _wv.repaint();
    }

    /**
     * Sets the act order.
     */
    public void setActOrder(Class... theClasses)
    {
        System.err.println("World.setActOrder: Not Impl");
    }

    /**
     * Sets the paint order.
     */
    public void setPaintOrder(Class... theClasses)
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
    protected Actor getActorAt(double aX, double aY, Class aClass)
    {
        for (View child : _wv.getChildren()) {
            Actor gfa = gfa(child);
            if (gfa == null) continue;
            if (aClass == null || aClass.isInstance(gfa)) {
                Point point = child.parentToLocal(aX, aY);
                if (child.contains(point.getX(), point.getY()))
                    return gfa;
            }
        }
        return null;
    }

    /**
     * Returns the objects at given point.
     */
    protected List getActorsAt(double aX, double aY, Class aClass)
    {
        List hitList = new ArrayList();
        for (View child : _wv.getChildren()) {
            Actor gfa = gfa(child);
            if (gfa == null) continue;
            if (aClass == null || aClass.isInstance(gfa)) {
                Point point = child.parentToLocal(aX, aY);
                if (child.contains(point.getX(), point.getY()))
                    hitList.add(gfa);
            }
        }

        return hitList;
    }

    /**
     * Returns on intersecting Actor.
     */
    protected Actor getActorAt(Shape aShape, Class aClass)
    {
        for (View child : _wv.getChildren()) {
            Actor gfa = gfa(child);
            if (gfa == null) continue;
            if (aClass == null || aClass.isInstance(gfa)) {
                Shape shp2 = child.parentToLocal(aShape);
                if (child.intersects(shp2))
                    return gfa;
            }
        }

        return null;
    }

    /**
     * Returns on intersecting Actor.
     */
    protected List getActorsAt(Shape aShape, Class aClass)
    {
        List<Actor> hitList = new ArrayList<>();
        for (View child : _wv.getChildren()) {
            Actor gfa = gfa(child);
            if (gfa == null)
                continue;
            if (aClass == null || aClass.isInstance(gfa)) {
                Shape shp2 = child.parentToLocal(aShape);
                if (child.intersects(shp2))
                    hitList.add(gfa);
            }
        }

        return hitList;
    }

    /**
     * This method is called by snap.node.ClassPage to return actual Node.
     */
    public WorldView getView()  { return _wv; }

    // Convenience to return Greenfoot Actor for Node.
    static Actor gfa(View aView)
    {
        GFSnapActor gfsa = gfsa(aView);
        return gfsa != null ? gfsa._gfa : null;
    }

    static GFSnapActor gfsa(View aView)
    {
        return aView instanceof GFSnapActor ? (GFSnapActor) aView : null;
    }

    /**
     * Sets the window visible.
     */
    public void setWindowVisible(boolean aValue)
    {
        GreenfootOwner owner = GreenfootOwner.getShared();
        owner.setWorld(this);
        owner.setWindowVisible(true);
    }
}