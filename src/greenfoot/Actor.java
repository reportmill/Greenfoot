package greenfoot;
import snap.geom.Point;
import snap.geom.Rect;
import snap.geom.Shape;
import snap.util.MathUtils;
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
        int cellSize = _world != null ? _world.getCellSize() : 1;
        int newX = aX * cellSize + cellSize / 2;
        int newY = aY * cellSize + cellSize / 2;

        // If bounded, clamp to bounds
        if (_world != null && _world.isBounded()) {
            newX = MathUtils.clamp(newX, 0, _world.getWidth() * cellSize - cellSize);
            newY = MathUtils.clamp(newY, 0, _world.getHeight() * cellSize - cellSize);
        }

        // Set View x/y
        _actorView.setXY(newX - getWidth() / 2d, newY - getHeight() / 2d);
        _x = newX;
        _y = newY;
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
        double angleDeg = Math.toDegrees(Math.atan2(dy, dx));
        int angleDegInt = (int) Math.round(angleDeg);
        setRotation(angleDegInt);
    }

    /**
     * Returns the rotation.
     */
    public int getRotation()
    {
        int rotation = ((int) Math.round(_actorView.getRotate())) % 360;
        return rotation >= 0 ? rotation : (rotation + 360);
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
        Rect actorBounds = _actorView.getBoundsLocal();
        actorBounds.inset(.5);
        Shape actorBoundsInWorld = _actorView.localToParent(actorBounds);
        return _world.getActorsAt(actorBoundsInWorld, aClass);
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
        double cellSize = _world.getCellSize();
        double x = _actorView.getWidth() / 2 + aX * cellSize;
        double y = _actorView.getHeight() / 2 + aY * cellSize;
        Point actorXYInWorld = _actorView.localToParent(x, y);
        return _world.getActorsAt(actorXYInWorld.x, actorXYInWorld.y, aClass);
    }

    /**
     * Returns actors in given range.
     */
    protected <T extends Actor> List<T> getObjectsInRange(int aRadius, Class<T> aClass)
    {
        double x = _actorView.getWidth() / 2;
        double y = _actorView.getHeight() / 2;
        double radius = aRadius * _world.getCellSize();
        double halfRadius = radius / 2;
        Shape rect = _actorView.localToParent(new Rect(x - halfRadius, y - halfRadius, radius, radius));
        return _world.getActorsAt(rect, aClass);
    }

    /**
     * Returns on intersecting Actor.
     */
    protected Actor getOneIntersectingObject(Class<?> aClass)
    {
        Rect actorBounds = _actorView.getBoundsLocal();
        actorBounds.inset(.5);
        Shape actorBoundsInWorld = _actorView.localToParent(actorBounds);
        return _world.getActorAt(actorBoundsInWorld, aClass);
    }

    /**
     * Returns peer actor at given offset from this actor's center.
     */
    protected Actor getOneObjectAtOffset(int aX, int aY, Class<?> aClass)
    {
        double cellSize = _world.getCellSize();
        double x = _actorView.getWidth() / 2 + aX * cellSize;
        double y = _actorView.getHeight() / 2 + aY * cellSize;
        Point actorXYInWorld = _actorView.localToParent(x, y);
        return (Actor) _world.getActorAt(actorXYInWorld.x, actorXYInWorld.y, aClass);
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
        if (obj != null)
            _world.removeObject(obj);
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