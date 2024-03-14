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
    private World _gfw;

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

    /**
     * Constructor for given GreenFoot World.
     */
    public WorldView(World aWorld)
    {
        _gfw = aWorld;
        int csize = aWorld.getCellSize();
        int width = aWorld.getWidth() * csize;
        int height = aWorld.getHeight() * csize;
        setPrefSize(width, height);
        setClip(new Rect(0, 0, width, height));
        setFill(Color.WHITE);
        setBorder(Color.BLACK, 1);
        enableEvents(MouseEvents);
        enableEvents(KeyEvents);
        setFocusable(true);
        setFocusWhenPressed(true);
    }

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
    public void paintBack(Painter aPntr)
    {
        super.paintBack(aPntr);
        GreenfootImage gimg = _gfw.getBackground();
        Image img = gimg._image;
        int cs = _gfw.getCellSize();
        int w = _gfw.getWidth() * cs;
        int h = _gfw.getHeight() * cs;
        int iw = gimg.getWidth();
        int ih = gimg.getHeight();
        for (int x = 0; x < w; x += iw)
            for (int y = 0; y < h; y += ih)
                aPntr.drawImage(img, x, y, iw, ih);

    }

    /**
     * Override to paint background image.
     */
    public void paintAbove(Painter aPntr)
    {
        // Paint background text
        for (Map.Entry<String, String> entry : _gfw._text.entrySet()) {
            String key = entry.getKey();
            String str = entry.getValue();
            String[] keys = key.split("x");
            int x = StringUtils.intValue(keys[0]);
            int y = StringUtils.intValue(keys[1]);
            snap.gfx.Font font = snap.gfx.Font.Arial14.copyForSize(24).getBold();
            aPntr.setFont(font);
            Rect bnds = font.getStringBounds(str);
            x = x - (int) Math.round(bnds.width / 2);
            y = y - (int) Math.round(font.getDescent() - bnds.height / 2);
            aPntr.setColor(Color.BLACK);
            aPntr.drawString(str, x, y - 1);
            aPntr.drawString(str, x, y + 1);
            aPntr.drawString(str, x - 1, y);
            aPntr.drawString(str, x + 1, y);
            aPntr.setColor(Color.WHITE);
            aPntr.drawString(str, x, y);
        }
    }

    /**
     * Calls the act method and actors act methods.
     */
    void doAct()
    {
        try {
            _gfw.act();
            for (View child : getChildren()) ((ActorView) child).act();
            _mouseClicked = null;
            _keyClicks.clear();
        }

        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}