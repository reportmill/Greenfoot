package greenfoot;
import snap.gfx.*;
import snap.view.ImageView;
import java.util.List;

/**
 * An implementation of the GreenFoot Actor class using SnapKit.
 */
public class Actor {
    
    // The snap actor
    GFSnapActor          _sa = new GFSnapActor(this);
    
    // The actor location
    int                  _x, _y;
    
    // The greenfoot image
    GreenfootImage       _img;
    
    // The world
    World                _world;

/**
 * Creates a new Actor.
 */
public Actor()
{
    // If project configured image, set image
    String iname = Greenfoot.getProperty("class." + getClass().getSimpleName() + ".image");
    GreenfootImage img = iname!=null? new GreenfootImage(iname) : null;
    if(img!=null) setImage(img);
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
public int getWidth()  { return (int)_sa.getWidth(); }

/**
 * Returns the height.
 */
public int getHeight()  { return (int)_sa.getHeight(); }

/**
 * Set Location.
 */
public void setLocation(int aX, int aY)
{
    int cs = _world!=null? _world.getCellSize() : 1, x = aX*cs + cs/2, y = aY*cs + cs/2;
    if(_world!=null && _world.isBounded()) {
        if(x<0) x = 0; if(x>=_world.getWidth()*cs) x = _world.getWidth()*cs - cs;
        if(y<0) y = 0; if(y>=_world.getHeight()*cs) y = _world.getHeight()*cs - cs;
    }
        
    // Set View x/y
    _sa.setXY(x - getWidth()/2d, y - getHeight()/2d); _x = x; _y = y;
}

/**
 * Set Location.
 */
public void setLocation(double aX, double aY)
{
    setLocation((int)Math.round(aX), (int)Math.round(aY));
}

/**
 * Move.
 */
public void move(int aValue)
{
    double x = getX() + aValue*Math.cos(Math.toRadians(_sa.getRotate()));
    double y = getY() + aValue*Math.sin(Math.toRadians(_sa.getRotate()));
    setLocation(x, y);
}

/**
 * Turn.
 */
public void turn(int aDeg)  { _sa.setRotate(_sa.getRotate() + aDeg); }

/**
 * Rotates actor to face the given world location.
 */
public void turnTowards(int aX, int aY)
{
    int dx = aX - getX(), dy = aY - getY();
    double angle = dx!=0? Math.atan(dy/(double)dx) : dy<0? 90 : 270;
    setRotation((int)Math.round(angle));
}

/**
 * Returns the rotation.
 */
public int getRotation()  { int r = ((int)Math.round(_sa.getRotate()))%360; return r>=0? r : (r + 360); }

/**
 * Sets the rotation.
 */
public void setRotation(int aRotation)  { _sa.setRotate(aRotation); }

/**
 * Returns the greenfoot image.
 */
public GreenfootImage getImage()  { return _img; }

/**
 * Returns the greenfoot image.
 */
public void setImage(GreenfootImage anImage)
{
    // If image already set, just return
    if(_img==anImage) return;
    
    // Update image actor lists and set new image
    if(_img!=null) _img._actors.remove(this); _img = anImage;
    if(_img!=null) _img._actors.add(this);
    
    // Call image changed
    imageChanged();
}

/**
 * Sets an image by name.
 */
public void setImage(String aName)  { setImage(new GreenfootImage(aName)); }

/**
 * Called when the image changes.
 */
void imageChanged()
{
    // Set new image and new size and reset location to make sure new image is centered
    _sa.setImage(_img._image);
    _sa.setSize(_img._image.getWidth(), _img._image.getHeight());
    setLocation(getX(), getY());
}

/**
 * Returns the world.
 */
public World getWorld()  { return _world; }

/**
 * Returns on intersecting Actor.
 */
protected List getIntersectingObjects(Class aClass)
{
    Rect bnds = _sa.getBoundsLocal(); bnds.inset(.5);
    Shape shp = _sa.localToParent(bnds);
    return _world.getActorsAt(shp, aClass);
}

/**
 * Returns the neighbors to this object within a given distance.
 */
public List getNeighbors(int aDist, boolean doDiagonal, Class aClass)
{
    System.err.println("Actor.getNeighbors: Not impl"); return null;
}

/**
 * Returns peer actors at given offset from this actor's center.
 */
protected List getObjectsAtOffset(int aX, int aY, Class aClass)
{
    double cs = _world.getCellSize(), x = _sa.getWidth()/2 + aX*cs, y = _sa.getHeight()/2 + aY*cs;
    Point pnt = _sa.localToParent(x, y);
    return _world.getActorsAt(pnt.getX(), pnt.getY(), aClass);
}
/**
 * Returns actors in given range.
 */
protected List getObjectsInRange(int aR, Class aClass)
{
    double x = _sa.getWidth()/2, y = _sa.getHeight()/2, r = aR*_world.getCellSize(), hr = r/2;
    Shape rect = _sa.localToParent(new Rect(x-hr,y-hr,r,r));
    return _world.getActorsAt(rect, aClass);
}

/**
 * Returns on intersecting Actor.
 */
protected Actor getOneIntersectingObject(Class aClass)
{
    Rect bnds = _sa.getBoundsLocal(); bnds.inset(.5);
    Shape shp = _sa.localToParent(bnds);
    return _world.getActorAt(shp, aClass);
}

/**
 * Returns peer actor at given offset from this actor's center.
 */
protected Actor getOneObjectAtOffset(int aX, int aY, Class aClass)
{
    double cs = _world.getCellSize(), x = _sa.getWidth()/2 + aX*cs, y = _sa.getHeight()/2 + aY*cs;
    Point pnt = _sa.localToParent(x, y);
    return _world.getActorAt(pnt.getX(), pnt.getY(), aClass);
}

/**
 * Returns whether this actor is touching any other objects of the given class.
 */
public boolean isTouching(Class aClass)  { return getOneIntersectingObject(aClass)!=null; }

/**
 * Removes one object of the given class that this actor is currently touching (if any exist).
 */
protected void removeTouching(Class aClass)
{
    Actor obj = getOneIntersectingObject(aClass);
    if(obj!=null) _world.removeObject(obj);
}

public boolean isAtEdge()  { return false; }

/**
 * The Act method.
 */
public void act()  { }

/**
 * Notification for when actor is added to a world.
 */
protected void addedToWorld(World aWorld)  { }

/**
 * The Greenfoot SnapActor.
 */
public static class GFSnapActor extends ImageView {
    
    // The Greenfoot actor
    Actor        _gfa;
    
    /** Creates a new SnapActor. */
    public GFSnapActor(Actor anActor)  { _gfa = anActor; }
    
    /** Override to send to Greenfoot Actor. */
    public void act()  { _gfa.act(); }
}

}