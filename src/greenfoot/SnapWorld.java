package greenfoot;
import java.util.*;
import snap.gfx.*;
import snap.view.*;

/**
 * The SnapView for Greenfoot World.
 */
public class SnapWorld extends ChildView {
    
    // The Greenfoot World
    World              _gfw;
    
    // The frame rate
    double             _frameRate = 24;
    
    // Whether mouse is down
    ViewEvent          _mouseDown;
    
    // Whether mouse was clicked on this frame
    ViewEvent          _mouseClicked;
    
    // The mouse location
    double             _mx, _my;
    
    // The pressed key
    Set <Integer>      _keyDowns = new HashSet();
    
    // The key typed in current frame
    Set <Integer>      _keyClicks = new HashSet();
    
    // The ViewOwner for world
    ViewOwner          _vowner = new ViewOwner(this);
    
    // The animation timer    
    ViewTimer          _timer = new ViewTimer(getFrameDelay(), t -> doAct());

/**
 * Creates a new SnapWorld for given GreenFoot World.
 */
public SnapWorld(World aWorld)
{
    _gfw = aWorld;
     setPrefSize(720, 405);
     setFill(Color.WHITE); setBorder(Color.BLACK, 1);
     enableEvents(MouseEvents); enableEvents(KeyEvents);
     setFocusable(true); setFocusWhenPressed(true);
}
    
/**
 * Returns the frame rate.
 */
public double getFrameRate()  { return _frameRate; }

/**
 * Sets the frame rate.
 */
public void setFrameRate(double aValue)
{
    _frameRate = aValue;
    _timer.setPeriod(getFrameDelay());
}

/**
 * Returns the frame delay in milliseconds.
 */
public int getFrameDelay()  { return (int)Math.round(1000/_frameRate); }

/**
 * Starts the animation.
 */
public void start()  { _timer.start(); }

/**
 * Stops the animation.
 */
public void stop()  { _timer.stop(); }

/**
 * Whether scene is playing.
 */
public boolean isPlaying()  { return _timer.isRunning(); }

/**
 * Returns whether the mouse was clicked on this frame.
 */
public boolean isMouseClicked()  { return _mouseClicked!=null; }

/**
 * Returns whether a given key is pressed.
 */
public boolean isKeyDown(String aKey)
{
    int kp = KeyCode.get(aKey.toUpperCase());
    return _keyDowns.contains(kp);
}

/**
 * Override to start greenfoot.
 */
protected void setShowing(boolean aValue)
{
    if(aValue==isShowing()) return; super.setShowing(aValue);
    if(aValue) {
        Greenfoot.start();
        requestFocus();
    }
}

/**
 * Process event.
 */
protected void processEvent(ViewEvent anEvent)
{
    // Handle MouseEvent
    if(anEvent.isMouseEvent()) {
        if(anEvent.isMousePressed()) _mouseDown = anEvent;
        else if(anEvent.isMouseReleased()) _mouseDown = null;
        else if(anEvent.isMouseClicked()) _mouseClicked = anEvent;
        _mx = anEvent.getX(); _my = anEvent.getY();
    }
    
    // Handle KeyEvent: Update KeyDowns and KeyClicks for event
    else if(anEvent.isKeyEvent()) {
        int kcode = anEvent.getKeyCode();
        if(anEvent.isKeyPressed()) { _keyDowns.add(kcode); _keyClicks.add(kcode); }
        else if(anEvent.isKeyReleased()) _keyDowns.remove(kcode);
    }
}

/**
 * Override to paint background image.
 */
public void paintBack(Painter aPntr)
{
    super.paintBack(aPntr);
    GreenfootImage gimg = _gfw.getBackground(); Image img = gimg._image;
    int cs = _gfw.getCellSize(), w = _gfw.getWidth()*cs, h = _gfw.getHeight()*cs;
    for(int x=0;x<w;x+=gimg.getWidth())
        for(int y=0;y<h;y+=gimg.getHeight())
            aPntr.drawImage(img, x, y);
}

/**
 * Calls the act method and actors act methods.
 */
void doAct()
{
    try {
        _gfw.act();
        for(View child : getChildren()) ((Actor.GFSnapActor)child).act();
        _mouseClicked = null; _keyClicks.clear();
    }
    catch(Exception e) { throw new RuntimeException(e); }
}

/**
 * Returns a ViewOwner for this SnapWorld.
 */
public ViewOwner getViewOwner()  { return _vowner; }

}