package greenfoot;
import snap.geom.Point;
import snap.geom.Rect;
import snap.geom.Shape;
import java.util.List;

/**
 * An implementation of the GreenFoot Actor class using SnapKit.
 */
public class Actor {

    // The actor view
    protected ActorView _actorView = new ActorView(this);

    // The actor location
    private int _x, _y;

    // The greenfoot image
    private GreenfootImage _image;

    // The world
    protected World _world;

    /**
     * Constructor.
     */
    public Actor()
    {
        // If project configured image, set image
        String imageName = Greenfoot.getProperty("class." + getClass().getSimpleName() + ".image");
        GreenfootImage image = imageName != null ? new GreenfootImage(imageName) : null;
        if (image == null)
            image = GreenfootImage.SHARED;
        setImage(image);
    }

    /**
     * Returns the x location.
     */
    public int getX()  { return _x; }

    /**
     * Returns the y location.
     */
    public int getY()  { return _y; }

    /**
     * Returns the width.
     */
    public int getWidth()  { return (int) _actorView.getWidth(); }

    /**
     * Returns the height.
     */
    public int getHeight()  { return (int) _actorView.getHeight(); }

    /**
     * Set Location.
     */
    public void setLocation(int aX, int aY)
    {
        int cs = _world != null ? _world.getCellSize() : 1, x = aX * cs + cs / 2, y = aY * cs + cs / 2;
        if (_world != null && _world.isBounded()) {
            if (x < 0) x = 0;
            if (x >= _world.getWidth() * cs) x = _world.getWidth() * cs - cs;
            if (y < 0) y = 0;
            if (y >= _world.getHeight() * cs) y = _world.getHeight() * cs - cs;
        }

        // Set View x/y
        _actorView.setXY(x - getWidth() / 2d, y - getHeight() / 2d);
        _x = x;
        _y = y;
    }

    /**
     * Set Location.
     */
    public void setLocation(double aX, double aY)
    {
        setLocation((int) Math.round(aX), (int) Math.round(aY));
    }

    /**
     * Move.
     */
    public void move(int aValue)
    {
        double x = getX() + aValue * Math.cos(Math.toRadians(_actorView.getRotate()));
        double y = getY() + aValue * Math.sin(Math.toRadians(_actorView.getRotate()));
        setLocation(x, y);
    }

    /**
     * Turn.
     */
    public void turn(int aDeg)
    {
        _actorView.setRotate(_actorView.getRotate() + aDeg);
    }

    /**
     * Rotates actor to face the given world location.
     */
    public void turnTowards(int aX, int aY)
    {
        int dx = aX - getX();
        int dy = aY - getY();
        double angle = dx != 0 ? Math.atan(dy / (double) dx) : dy < 0 ? 90 : 270;
        setRotation((int) Math.round(angle));
    }

    /**
     * Returns the rotation.
     */
    public int getRotation()
    {
        int r = ((int) Math.round(_actorView.getRotate())) % 360;
        return r >= 0 ? r : (r + 360);
    }

    /**
     * Sets the rotation.
     */
    public void setRotation(int aRotation)
    {
        _actorView.setRotate(aRotation);
    }

    /**
     * Returns the greenfoot image.
     */
    public GreenfootImage getImage()  { return _image; }

    /**
     * Returns the greenfoot image.
     */
    public void setImage(GreenfootImage anImage)
    {
        // If image already set, just return
        if (_image == anImage) return;

        // Update image actor lists and set new image
        if (_image != null) _image._actors.remove(this);
        _image = anImage;
        if (_image != null) _image._actors.add(this);

        // Call image changed
        imageChanged();
    }

    /**
     * Sets an image by name.
     */
    public void setImage(String aName)
    {
        setImage(new GreenfootImage(aName));
    }

    /**
     * Called when the image changes.
     */
    void imageChanged()
    {
        // If image not loaded, come back when loaded
        if (_image._image != null && !_image._image.isLoaded())
            _image._image.addLoadListener(this::imageChanged);

        // Set new image and new size and reset location to make sure new image is centered
        _actorView.setImage(_image._image);
        _actorView.setSize(_image._image.getWidth(), _image._image.getHeight());
        setLocation(getX(), getY());
    }

    /**
     * Returns the world.
     */
    public World getWorld()  { return _world; }

    /**
     * Returns the world as given class (null if wrong class).
     */
    public <T> T getWorldOfType(Class<T> worldClass)  { return worldClass.cast(_world); }

    /**
     * Returns on intersecting Actor.
     */
    protected <T extends Actor> List<T> getIntersectingObjects(Class<T> aClass)
    {
        Rect bnds = _actorView.getBoundsLocal();
        bnds.inset(.5);
        Shape shp = _actorView.localToParent(bnds);
        return _world.getActorsAt(shp, aClass);
    }

    /**
     * Returns the neighbors to this object within a given distance.
     */
    public <T> List<T> getNeighbors(int aDist, boolean doDiagonal, Class<T> aClass)
    {
        System.err.println("Actor.getNeighbors: Not impl");
        return null;
    }

    /**
     * Returns peer actors at given offset from this actor's center.
     */
    protected <T extends Actor> List<T> getObjectsAtOffset(int aX, int aY, Class<T> aClass)
    {
        double cs = _world.getCellSize();
        double x = _actorView.getWidth() / 2 + aX * cs;
        double y = _actorView.getHeight() / 2 + aY * cs;
        Point pnt = _actorView.localToParent(x, y);
        return _world.getActorsAt(pnt.x, pnt.y, aClass);
    }

    /**
     * Returns actors in given range.
     */
    protected <T extends Actor> List<T> getObjectsInRange(int aR, Class<T> aClass)
    {
        double x = _actorView.getWidth() / 2;
        double y = _actorView.getHeight() / 2;
        double r = aR * _world.getCellSize(), hr = r / 2;
        Shape rect = _actorView.localToParent(new Rect(x - hr, y - hr, r, r));
        return _world.getActorsAt(rect, aClass);
    }

    /**
     * Returns on intersecting Actor.
     */
    protected Actor getOneIntersectingObject(Class<?> aClass)
    {
        Rect bnds = _actorView.getBoundsLocal();
        bnds.inset(.5);
        Shape shp = _actorView.localToParent(bnds);
        return _world.getActorAt(shp, aClass);
    }

    /**
     * Returns peer actor at given offset from this actor's center.
     */
    protected Actor getOneObjectAtOffset(int aX, int aY, Class<?> aClass)
    {
        double cs = _world.getCellSize(), x = _actorView.getWidth() / 2 + aX * cs, y = _actorView.getHeight() / 2 + aY * cs;
        Point pnt = _actorView.localToParent(x, y);
        return (Actor) _world.getActorAt(pnt.getX(), pnt.getY(), aClass);
    }

    /**
     * Returns whether this actor is touching any other objects of the given class.
     */
    public boolean isTouching(Class<?> aClass)
    {
        return getOneIntersectingObject(aClass) != null;
    }

    /**
     * Removes one object of the given class that this actor is currently touching (if any exist).
     */
    protected void removeTouching(Class<?> aClass)
    {
        Actor obj = getOneIntersectingObject(aClass);
        if (obj != null) _world.removeObject(obj);
    }

    public boolean isAtEdge()  { return false; }

    /**
     * The Act method.
     */
    public void act()  { }

    /**
     * Check whether this object intersects with another given object.
     */
    protected boolean intersects(Actor other)
    {
        Shape thisShape = _actorView.localToParent(_actorView.getBoundsLocal());
        Shape otherShape = other._actorView.localToParent(other._actorView.getBoundsLocal());
        return thisShape.intersects(otherShape);
    }

    /**
     * Notification for when actor is added to a world.
     */
    protected void addedToWorld(World aWorld)  { }
}