package greenfoot;
import java.util.*;
import snap.gfx.*;
import snap.view.*;
import greenfoot.Actor.GFSnapActor;

/**
 * A custom class.
 */
public class World {
    
    // The SnapScene
    SnapWorld          _scn;
    
    // The Width/Height/CellSize
    int                _width, _height, _cellSize = 1;
    
    // The background image
    GreenfootImage     _backImg;
    
/**
 * Creates a new world.
 */
public World(int aW, int aH, int aCellSize)  { this(aW, aH, aCellSize, false); }

/**
 * Creates a new world.
 */
public World(int aW, int aH, int aCellSize, boolean aValue)
{
    // Set sizing info
    _width = aW; _height = aH; _cellSize = aCellSize;
    
    // Set world
    _scn = new SnapWorld(this);
    
    // If first world, manually set it
    if(Greenfoot._world==null) Greenfoot.setWorld(this);
    
    // Set default FrameRate
    _scn.setFrameRate(Greenfoot.getFrameRate());
    _scn.setSize(_width*_cellSize, _height*_cellSize);
    
    // Set background image
    String iname = Greenfoot.getProperty("class." + getClass().getSimpleName() + ".image");
    if(iname!=null) setBackground(new GreenfootImage(iname));
    else setBackground(new GreenfootImage(aW*aCellSize, aH*aCellSize));
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
 * Returns the number of objects currently in world.
 */
public int numberOfObjects()
{
    int c = 0; for(View n : _scn.getChildren()) if(gfa(n)!=null) c++; return c;
}

/**
 * Adds an object.
 */
public void addObject(Actor anActor, int anX, int aY)
{
    _scn.addChild(anActor._sa); anActor._world = this;
    anActor.setLocation(anX, aY);
    anActor.addedToWorld(this);
}

/**
 * Removes an Actor.
 */
public void removeObject(Actor anActor)  { _scn.removeChild(anActor._sa); }

/**
 * Returns the actors of a given class.
 */
public List getObjects(Class aClass)
{
    List list = new ArrayList();
    for(View child : _scn.getChildren()) { Actor gfa = gfa(child); if(gfa==null) continue;
        if(aClass==null || aClass.isInstance(gfa))
            list.add(gfa); }
    return list;
}

/**
 * Returns the objects at given point.
 */
public List getObjectsAt(int aX, int aY, Class aClass)  { return getActorsAt(aX, aY, aClass); }

/**
 * Removes the objects of given class.
 */
public void removeObjects(Collection theActors)  { for(Object actor : theActors) removeObject((Actor)actor); }

/**
 * Returns the background image.
 */
public GreenfootImage getBackground()
{
    if(_backImg!=null) return _backImg;
    _backImg = new GreenfootImage(getWidth()*getCellSize(), getHeight()*getCellSize());
    return _backImg;
}

/**
 * Sets the greenfoot image.
 */
public void setBackground(GreenfootImage anImage)
{
    _backImg = anImage;
}

/**
 * Sets the image to named image.
 */
public void setBackground(String aName)  { setBackground(new GreenfootImage(aName)); }

/**
 * Returns the color at center of cell.
 */
public java.awt.Color getColor()  { System.err.println("World.getColor: Not Impl"); return null; }

/**
 * Repaint the world.
 */
public void repaint()  { _scn.repaint(); }

/**
 * Sets the act order.
 */
public void setActOrder(Class ... theClasses)  { System.err.println("World.setActOrder: Not Impl"); }

/**
 * Sets the paint order.
 */
public void setPaintOrder(Class ... theClasses)  { System.err.println("World.setPaintOrder: Not Impl"); }

/**
 * Show some text centered at given position in world.
 */
public void showText(String aStr, int x, int y)   { System.err.println("World.showText: Not Impl"); }

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
    for(View child : _scn.getChildren()) { Actor gfa = gfa(child); if(gfa==null) continue;
        if(aClass==null || aClass.isInstance(gfa)) { Point point = child.parentToLocal(aX, aY);
            if(child.contains(point.getX(), point.getY()))
                return gfa; } }
    return null;
}

/**
 * Returns the objects at given point.
 */
protected List getActorsAt(double aX, double aY, Class aClass)
{
    List hitList = new ArrayList();
    for(View child : _scn.getChildren()) { Actor gfa = gfa(child); if(gfa==null) continue;
        if(aClass==null || aClass.isInstance(gfa)) { Point point = child.parentToLocal(aX, aY);
            if(child.contains(point.getX(), point.getY()))
                hitList.add(gfa); } }
    return hitList;
}

/**
 * Returns on intersecting Actor.
 */
protected Actor getActorAt(Shape aShape, Class aClass)
{
    for(View child : _scn.getChildren()) { Actor gfa = gfa(child); if(gfa==null) continue;
        if(aClass==null || aClass.isInstance(gfa)) { Shape shp2 = child.parentToLocal(aShape);
            if(child.intersects(shp2))
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
    List hitList = new ArrayList();
    for(View child : _scn.getChildren()) { Actor gfa = gfa(child); if(gfa==null) continue;
        if(aClass==null || aClass.isInstance(gfa)) { Shape shp2 = child.parentToLocal(aShape);
            if(child.intersects(shp2))
                hitList.add(gfa);
        }
    }
    return hitList;
}

/** This method is called by snap.node.ClassPage to return actual Node. */
public View getView()  { return _scn; }

/** Returns a Snap ViewOwner for World. */
public ViewOwner getViewOwner()  { return _scn.getViewOwner(); }

// Convenience to return Greenfoot Actor for Node.
static Actor gfa(View aView)  { GFSnapActor gfsa = gfsa(aView); return gfsa!=null? gfsa._gfa : null; }
static GFSnapActor gfsa(View aView)  { return aView instanceof GFSnapActor? (GFSnapActor)aView : null; }

/**
 * Sets the window visible.
 */
public void setWindowVisible(boolean aValue)  { getViewOwner().setWindowVisible(true); }

}