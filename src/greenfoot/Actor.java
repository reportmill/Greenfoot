package greenfoot;
import snap.geom.Point;
import snap.geom.Rect;
import snap.geom.Shape;
import snap.util.MathUtils;
import java.util.List;

/**
 * An implementation of the GreenFoot Actor class using SnapKit.
 * (<a href="https://www.greenfoot.org/files/javadoc/greenfoot/Actor.html">Actor</a>)
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

    // Shared image
    protected static GreenfootImage DEFAULT_ACTOR_IMAGE = new GreenfootImage(48, 48);

    /**
     * Constructor.
     */
    public Actor()
    {
        // If project configured image, set image
        GreenfootImage image = Greenfoot.env().getGreenfootImageForClass(getClass());
        if (image == null)
            image = DEFAULT_ACTOR_IMAGE;
        setImage(image);
    }

    /**
     * Returns the actor view.
     */
    public ActorView getActorView()  { return _actorView; }

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
     * Returns the actor bounds in world coords.
     */
    public Shape getBoundsInWorld()
    {
        // Get actor bounds in world coords
        Rect actorBounds = _actorView.getBoundsLocal();
        actorBounds.inset(.5);
        return _actorView.localToParent(actorBounds);
    }

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

        // Get whether location needs update
        boolean updateLocation = _image != null;

        // Update image actor lists and set new image
        if (_image != null) _image._actors.remove(this);
        _image = anImage;
        if (_image != null) _image._actors.add(this);

        // Update ActorView Image and Size
        if (_image != null) {
            _actorView.setImage(_image._image);
            _actorView.setSize(_image._image.getWidth(), _image._image.getHeight());
        }

        // If old image was set, update location to keep image centered if image size changed
        if (updateLocation)
            setLocation(getX(), getY());
    }

    /**
     * Sets an image by name.
     */
    public void setImage(String aName)
    {
        GreenfootImage greenfootImageForName = new GreenfootImage(aName);
        setImage(greenfootImageForName);
    }

    /**
     * Called when the image changes.
     */
    void imageChanged()
    {
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
        return _world.getIntersectingActorsForActorShapeAndClass(this, null, aClass);
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
    protected <T> List<T> getObjectsAtOffset(int aX, int aY, Class<T> aClass)
    {
        double cellSize = _world.getCellSize();
        double offsetX = _actorView.getWidth() / 2 + aX * cellSize;
        double offsetY = _actorView.getHeight() / 2 + aY * cellSize;
        Point offsetXYInWorld = _actorView.localToParent(offsetX, offsetY);
        return _world.getActorsAt(this, offsetXYInWorld.x, offsetXYInWorld.y, aClass);
    }

    /**
     * Returns actors in given range.
     */
    protected <T> List<T> getObjectsInRange(int aRadius, Class<T> aClass)
    {
        double actorX = _actorView.getWidth() / 2;
        double actorY = _actorView.getHeight() / 2;
        double radius = aRadius * _world.getCellSize();
        Rect rangeBounds = new Rect(actorX - radius / 2, actorY - radius / 2, radius, radius);
        Shape rangeBoundsInWorld = _actorView.localToParent(rangeBounds);
        return _world.getIntersectingActorsForActorShapeAndClass(this, rangeBoundsInWorld, aClass);
    }

    /**
     * Returns on intersecting Actor.
     */
    protected Actor getOneIntersectingObject(Class<?> aClass)
    {
        return _world.getIntersectingActorForActorAndClass(this, aClass);
    }

    /**
     * Returns peer actor at given offset from this actor's center.
     */
    protected Actor getOneObjectAtOffset(int aX, int aY, Class<?> aClass)
    {
        double cellSize = _world.getCellSize();
        double offsetX = _actorView.getWidth() / 2 + aX * cellSize;
        double offsetY = _actorView.getHeight() / 2 + aY * cellSize;
        Point offsetXYInWorld = _actorView.localToParent(offsetX, offsetY);
        return (Actor) _world.getActorAt(this, offsetXYInWorld.x, offsetXYInWorld.y, aClass);
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
        Shape thisShape = getBoundsInWorld();
        Shape otherShape = other.getBoundsInWorld();
        return thisShape.intersectsShape(otherShape);
    }

    /**
     * Notification for when actor is added to a world.
     */
    protected void addedToWorld(World aWorld)  { }
}