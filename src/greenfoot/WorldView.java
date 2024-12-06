package greenfoot;
import java.util.*;
import snap.geom.Rect;
import snap.gfx.*;
import snap.gfx.Color;
import snap.util.StringUtils;
import snap.view.*;

/**
 * A Snap View for Greenfoot World.
 */
public class WorldView extends ChildView {

    // The Greenfoot World
    private World _world;

    // Whether mouse is down
    private ViewEvent _mouseDown;

    // Whether mouse was clicked on this frame
    private ViewEvent _mouseClicked;

    // The mouse location
    protected double _mx, _my;

    // The pressed key
    private Set<Integer> _keyDowns = new HashSet<>();

    // The key typed in current frame
    private Set<Integer> _keyClicks = new HashSet<>();

    // The children in paint order
    private Class<?>[] _paintOrderClasses;

    // The children in paint order
    private View[] _childrenInPaintOrder;

    /**
     * Constructor for given GreenFoot World.
     */
    public WorldView(World aWorld)
    {
        _world = aWorld;
        int csize = aWorld.getCellSize();
        int width = aWorld.getWidth() * csize;
        int height = aWorld.getHeight() * csize;
        setPrefSize(width, height);
        setOverflow(Overflow.Clip);
        setFill(Color.WHITE);
        enableEvents(MouseEvents);
        enableEvents(KeyEvents);
        setFocusable(true);
        setFocusWhenPressed(true);
    }

    /**
     * Returns the world.
     */
    public World getWorld()  { return _world; }

    /**
     * Returns whether the mouse was clicked on this frame.
     */
    public boolean isMouseClicked()
    {
        return _mouseClicked != null;
    }

    /**
     * Returns whether a given key is pressed.
     */
    public boolean isKeyDown(String aKey)
    {
        int kp = KeyCode.get(aKey.toUpperCase());
        return _keyDowns.contains(kp);
    }

    /**
     * Process event.
     */
    @Override
    protected void processEvent(ViewEvent anEvent)
    {
        // Handle MouseEvent
        if (anEvent.isMouseEvent()) {
            if (anEvent.isMousePress())
                _mouseDown = anEvent;
            else if (anEvent.isMouseRelease())
                _mouseDown = null;
            else if (anEvent.isMouseClick())
                _mouseClicked = anEvent;
            _mx = anEvent.getX();
            _my = anEvent.getY();
        }

        // Handle KeyEvent: Update KeyDowns and KeyClicks for event
        else if (anEvent.isKeyEvent()) {
            int kcode = anEvent.getKeyCode();
            if (anEvent.isKeyPress()) {
                _keyDowns.add(kcode);
                _keyClicks.add(kcode);
            }
            else if (anEvent.isKeyRelease()) _keyDowns.remove(kcode);
        }
    }

    /**
     * Override to paint background image.
     */
    @Override
    protected void paintBack(Painter aPntr)
    {
        super.paintBack(aPntr);

        GreenfootImage backgroundImageGF = _world.getBackground();
        Image backgroundImage = backgroundImageGF._image;

        // Get world and image sizes
        int cellSize = _world.getCellSize();
        int worldW = _world.getWidth() * cellSize;
        int worldH = _world.getHeight() * cellSize;
        int imageW = backgroundImageGF.getWidth();
        int imageH = backgroundImageGF.getHeight();

        // Tile image to fill world
        for (int x = 0; x < worldW; x += imageW)
            for (int y = 0; y < worldH; y += imageH)
                aPntr.drawImage(backgroundImage, x, y, imageW, imageH);
    }

    /**
     * Override to paint world strings.
     */
    @Override
    protected void paintAbove(Painter aPntr)
    {
        // Get strings to show
        Map<String,String> worldStrings = _world._text;

        // Paint background text
        for (Map.Entry<String, String> entry : worldStrings.entrySet()) {

            // Get string, key and text XY
            String str = entry.getValue();
            String key = entry.getKey();
            String[] textXYStrings = key.split("x");
            int textX = StringUtils.intValue(textXYStrings[0]);
            int textY = StringUtils.intValue(textXYStrings[1]);

            snap.gfx.Font font = snap.gfx.Font.Arial14.copyForSize(24).getBold();
            aPntr.setFont(font);
            Rect stringBounds = font.getStringBounds(str);
            textX = textX - (int) Math.round(stringBounds.width / 2);
            textY = textY - (int) Math.round(font.getDescent() - stringBounds.height / 2);

            // Draw string black (offset by one in every direction)
            aPntr.setColor(Color.BLACK);
            aPntr.drawString(str, textX, textY - 1);
            aPntr.drawString(str, textX, textY + 1);
            aPntr.drawString(str, textX - 1, textY);
            aPntr.drawString(str, textX + 1, textY);

            // Draw string white
            aPntr.setColor(Color.WHITE);
            aPntr.drawString(str, textX, textY);
        }
    }

    /**
     * Override to use paint order.
     */
    @Override
    protected void paintChildren(Painter aPntr)
    {
        super.paintChildren(aPntr);
    }

    /**
     * Sets the paint order.
     */
    public void setPaintOrder(Class<?>... theClasses)
    {
        _paintOrderClasses = theClasses != null && theClasses.length > 0 ? theClasses : null;
        _childrenInPaintOrder = null;
    }

    /**
     * Sets the children in paint order.
     */
    @Override
    protected View[] getChildrenInPaintOrder()
    {
        // If no paint order, do normal version
        if (_paintOrderClasses == null)
            return super.getChildrenInPaintOrder();

        // If childrenInPaintOrder not set, set
        if (_childrenInPaintOrder == null) {
            _childrenInPaintOrder = getChildren().clone();
            Arrays.sort(_childrenInPaintOrder, (o1,o2) -> Integer.compare(getPaintRanking(o1), getPaintRanking(o2)));
        }

        // Return
        return _childrenInPaintOrder;
    }

    /**
     * Returns a ranking of the given actorView, depending on where its actor falls in paintOrderClasses.
     */
    private int getPaintRanking(View actorView)
    {
        // Get actor for view, if not found, return no ranking
        Actor actor = actorView instanceof ActorView ? ((ActorView) actorView).getActor() : null;
        if (actor == null)
            return -1;

        // Iterate over paint order classes and if actor is instance of class, return opposite index
        // Iterate backwards, in case Actor.class is specified first? Dunno, probably still some problems
        for (int i = _paintOrderClasses.length - 1; i >= 0; i--) {
            Class<?> cls = _paintOrderClasses[i];
            if (cls.isInstance(actor)) // Return opposite index to make it ranking instead of index
                return _paintOrderClasses.length - i;
        }

        // Return no ranking
        return -1;
    }

    /**
     * Override to clear paint order.
     */
    @Override
    public void addChild(View aChild, int anIndex)
    {
        super.addChild(aChild, anIndex);
        _childrenInPaintOrder = null;
    }

    /**
     * Override to clear paint order.
     */
    @Override
    public View removeChild(int anIndex)
    {
        try { return super.removeChild(anIndex); }
        finally { _childrenInPaintOrder = null; }
    }

    /**
     * Calls the act method and actors act methods.
     */
    void doAct()
    {
        try {
            _world.act();
            for (View child : getChildren())
                ((ActorView) child)._actor.act();
            _mouseClicked = null;
            _keyClicks.clear();
        }

        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}